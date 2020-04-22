package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;
import jkind.lustre.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * The idea of this class is to create a dynamic repair library based on the passed inputs. Construction is done recursively with side effects collected on some class internal data-structures.
 */
public class DynamicRepairNode {

    private final String id;
    private final List<VarDecl> boolInputs = new ArrayList<>();
    private final List<VarDecl> intInputs = new ArrayList<>();
    private int holesCounter;
    private String holePrefixStr = "inner_hole_";
    private String constholePrefixStr = "const_hole_";

    //collecting side effects here starts here
    private final List<VarDecl> holeInputs = new ArrayList<>();
    private final List<VarDecl> outputs = new ArrayList<>();
    private final List<VarDecl> locals = new ArrayList<>();
    private final List<Equation> equations = new ArrayList<>();

    public DynamicRepairNode(String id) {
        this.id = id;
    }

    public RepairNode create(List<VarDecl> actualParamVarDecls, int exprSize) { //exprSize here indecates how many boolean nodes are in the expression, not how many possible terms.
        populateBoolIntInputs(actualParamVarDecls);
        List<Character> pathLabel = new ArrayList<>();
        pathLabel.add('R'); //for root node
        if (Config.depthFixed) outputs.add(defineTreeLevel(Config.repairNodeDepth, pathLabel));
        else {
            int balancedTreeDepth;
            double logDepth = (Math.log(exprSize) / Math.log(2));
            if (logDepth == 0) {
                assert exprSize == 1;
                balancedTreeDepth = exprSize;
            } else balancedTreeDepth = (logDepth % 1) == 0 ? (int) logDepth : (int) logDepth + 1;
            outputs.add(defineTreeLevel(balancedTreeDepth - 1, pathLabel));
        }
        return new RepairNode(id, actualParamVarDecls, holeInputs, outputs, locals, equations, null, null);
    }

    private VarDecl defineTreeLevel(int balancedTreeDepth, List<Character> pathLabel) {
        if (balancedTreeDepth == 0) { //base case
            return createLeaf(pathLabel);
        } else {
            ArrayList<Character> leftPathLabel = new ArrayList<>(pathLabel);
            leftPathLabel.add('l');
            VarDecl leftVarDecl = defineTreeLevel(balancedTreeDepth - 1, leftPathLabel);
            IdExpr leftOperand = DiscoveryUtil.varDeclToIdExpr(leftVarDecl);

            ArrayList<Character> rightPathLabel = new ArrayList<>(pathLabel);
            rightPathLabel.add('r');
            VarDecl rightVarDecl = defineTreeLevel(balancedTreeDepth - 1, rightPathLabel);
            IdExpr rightOperand = DiscoveryUtil.varDeclToIdExpr(rightVarDecl);


            VarDecl myNodeNameVarDecl = constructPathLabelName(pathLabel);

            // variables constituting an inner node must be of type bool. Integers should only appear in the leaf.
            assert (leftVarDecl.type == NamedType.BOOL) && (rightVarDecl.type == NamedType.BOOL);

            Equation myEquation = new Equation(DiscoveryUtil.varDeclToIdExpr(myNodeNameVarDecl), constructInnerBoolNode(pathLabel, leftOperand, rightOperand));

            //populate my stuff
            if (!getPathLabelStr(pathLabel).equals("R")) //add only local variables other than R, since R should be a return
                locals.add(myNodeNameVarDecl);

            equations.add(myEquation);

            return myNodeNameVarDecl;
        }
    }

    /**
     * This has the same logic as the inner nodes, except it might require one extra hole to constraint int vars.
     *
     * @param pathLabel
     * @return
     */
    private VarDecl createLeaf(List<Character> pathLabel) {
        VarDecl selectionHoleVarDecl = createNewHole(true);
        IdExpr selectionHoleExpr = DiscoveryUtil.varDeclToIdExpr(selectionHoleVarDecl);

        List<Expr> leafSelectionExprs = new ArrayList<>();
        leafSelectionExprs.addAll(allPossibleBoolExp(boolInputs));


        if (intInputs.size() > 0) {
            VarDecl constantHoleVarDecl = createNewHole(false); // creating a constantHoleVar
            for (VarDecl intVar : intInputs)
                leafSelectionExprs.addAll(constructLeafIntSelection(DiscoveryUtil.varDeclToIdExpr(intVar), DiscoveryUtil.varDeclToIdExpr(constantHoleVarDecl), intInputs));
        }
        for (int i = 0; i < intInputs.size(); i++) {
            for (int j = 1; j < intInputs.size(); i++) {
                leafSelectionExprs.add(new BinaryExpr(DiscoveryUtil.varDeclToIdExpr(intInputs.get(i)), BinaryOp.EQUAL, DiscoveryUtil.varDeclToIdExpr(intInputs.get(j))));
            }
        }

        VarDecl myNodeNameVarDecl = constructPathLabelName(pathLabel);

        Equation myEquation = new Equation(DiscoveryUtil.varDeclToIdExpr(myNodeNameVarDecl), createLeafSelectionExpr(selectionHoleExpr, leafSelectionExprs));

        if (!getPathLabelStr(pathLabel).equals("R")) //add only local variables other than R, since R should be a return
            locals.add(myNodeNameVarDecl);

        equations.add(myEquation);

        return myNodeNameVarDecl;
    }

    //returns bool expr, i.e., a, along with "not a"
    private Collection<? extends Expr> allPossibleBoolExp(List<VarDecl> boolInputs) {
        List<Expr> origBoolExprs = (List<Expr>) ((List<?>) DiscoveryUtil.varDeclToIdExpr(boolInputs));
        List<Expr> newBoolExprs = new ArrayList<>(origBoolExprs);
        for (Expr expr : origBoolExprs) {
            assert expr instanceof IdExpr;
            newBoolExprs.add(new UnaryExpr(UnaryOp.NOT, expr));
        }
        return newBoolExprs;
    }

    private Expr createLeafSelectionExpr(IdExpr selectionHoleExpr, List<Expr> leafSelectionExprs) {
        if (leafSelectionExprs.size() == 1) return leafSelectionExprs.get(0);

        return new IfThenElseExpr(new BinaryExpr(selectionHoleExpr, BinaryOp.EQUAL, new IntExpr(leafSelectionExprs.size())), leafSelectionExprs.get(0), createLeafSelectionExpr(selectionHoleExpr, leafSelectionExprs.subList(1, leafSelectionExprs.size())));
    }

    //makes a selection for every IntExpr in the input
    private List<BinaryExpr> constructLeafIntSelection(IdExpr leftIntExpr, IdExpr holeExpr, List<VarDecl> intInputs) {

        List<BinaryExpr> selectionBinaryExpr = new ArrayList<>();

        if (Config.rangeValueAnalysis) {
            BinaryExpr holeRangeExpr = VariableRangeVisitor.getRangeExpr(leftIntExpr, holeExpr);
            if (holeRangeExpr != null) {
                selectionBinaryExpr.add(new BinaryExpr(new BinaryExpr(leftIntExpr, BinaryOp.EQUAL, holeExpr), BinaryOp.AND, holeRangeExpr));
                selectionBinaryExpr.add(new BinaryExpr(new BinaryExpr(leftIntExpr, BinaryOp.LESS, holeExpr), BinaryOp.AND, holeRangeExpr));
                selectionBinaryExpr.add(new BinaryExpr(new BinaryExpr(leftIntExpr, BinaryOp.LESSEQUAL, holeExpr), BinaryOp.AND, holeRangeExpr));
                selectionBinaryExpr.add(new BinaryExpr(new BinaryExpr(leftIntExpr, BinaryOp.GREATER, holeExpr), BinaryOp.AND, holeRangeExpr));
                selectionBinaryExpr.add(new BinaryExpr(new BinaryExpr(leftIntExpr, BinaryOp.GREATEREQUAL, holeExpr), BinaryOp.AND, holeRangeExpr));
            } else {
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.EQUAL, holeExpr));
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.LESS, holeExpr));
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.LESSEQUAL, holeExpr));
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.GREATER, holeExpr));
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.GREATEREQUAL, holeExpr));
            }
        } else {
            selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.EQUAL, holeExpr));
            selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.LESS, holeExpr));
            selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.LESSEQUAL, holeExpr));
            selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.GREATER, holeExpr));
            selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.GREATEREQUAL, holeExpr));
        }

        for (int i = 0; i < intInputs.size(); i++) {
            VarDecl rhs = intInputs.get(i);
            if (!leftIntExpr.id.equals(rhs.id)) {
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.LESSEQUAL, DiscoveryUtil.varDeclToIdExpr(rhs)));
                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.LESS, DiscoveryUtil.varDeclToIdExpr(rhs)));
//                selectionBinaryExpr.add(new BinaryExpr(leftIntExpr, BinaryOp.EQUAL, DiscoveryUtil.varDeclToIdExpr(rhs)));
            }
        }
        return selectionBinaryExpr;
    }


    private Expr constructInnerBoolNode(List<Character> myPathLabel, IdExpr leftOperand, IdExpr rightOperand) {
        VarDecl selectionHoleVarDecl = createNewHole(true);
        IdExpr sectionHoleExpr = DiscoveryUtil.varDeclToIdExpr(selectionHoleVarDecl);
        IfThenElseExpr expr;
        if (!getPathLabelStr(myPathLabel).equals("R")) {
            expr = new IfThenElseExpr(new BinaryExpr(sectionHoleExpr, BinaryOp.EQUAL, new IntExpr(1)), new BinaryExpr(leftOperand, BinaryOp.AND, rightOperand), new IfThenElseExpr(new BinaryExpr(sectionHoleExpr, BinaryOp.EQUAL, new IntExpr(2)), new BinaryExpr(leftOperand, BinaryOp.OR, rightOperand), new IfThenElseExpr(new BinaryExpr(sectionHoleExpr, BinaryOp.EQUAL, new IntExpr(3)), new BinaryExpr(leftOperand, BinaryOp.IMPLIES, rightOperand), new BinaryExpr(leftOperand, BinaryOp.XOR, rightOperand))));
        } else expr = new IfThenElseExpr(new BinaryExpr(sectionHoleExpr, BinaryOp.EQUAL, new IntExpr(1)), new BinaryExpr(leftOperand, BinaryOp.AND, rightOperand), new IfThenElseExpr(new BinaryExpr(sectionHoleExpr, BinaryOp.EQUAL, new IntExpr(2)), new BinaryExpr(leftOperand, BinaryOp.OR, rightOperand), new IfThenElseExpr(new BinaryExpr(sectionHoleExpr, BinaryOp.EQUAL, new IntExpr(3)), new BinaryExpr(leftOperand, BinaryOp.IMPLIES, rightOperand), new BinaryExpr(leftOperand, BinaryOp.XOR, rightOperand))));
        return expr;
    }


    private VarDecl createNewHole(boolean innerHole) {
        VarDecl newHole;
        if (innerHole) newHole = new VarDecl(holePrefixStr + holesCounter, NamedType.INTHOLE);
        else newHole = new VarDecl(constholePrefixStr + holesCounter, NamedType.INTHOLE);
        holeInputs.add(newHole);
        ++holesCounter;
        return newHole;
    }

    private VarDecl constructPathLabelName(List<Character> pathLabel) {
        return new VarDecl(getPathLabelStr(pathLabel), NamedType.BOOL);
    }

    private String getPathLabelStr(List<Character> pathLabel) {
        String pathStr = "";
        for (int i = 0; i < pathLabel.size(); i++)
            pathStr += i == 0 ? pathLabel.get(i) : "_" + pathLabel.get(i);
        return pathStr;
    }

    private void populateBoolIntInputs(List<VarDecl> actualParamVarDecls) {
        for (VarDecl var : actualParamVarDecls) {
            if (var.type == NamedType.BOOL) boolInputs.add(var);
            else if (var.type == NamedType.INT) intInputs.add(var);
            else {
                System.out.println("unsupported type for dynamic repair library");
                assert false;
            }
        }
    }
}
