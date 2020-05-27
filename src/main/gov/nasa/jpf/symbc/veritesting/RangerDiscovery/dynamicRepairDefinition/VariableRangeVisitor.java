package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract;
import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.AstMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import jkind.lustre.*;
import za.ac.sun.cs.green.expr.Expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is used to identify the range of values that could be assigned to variables. This is expected to run after linearization, thus is not implementing all types of statements.
 * It works with VariableRangeExprVisitor to visit only interesting assignments.
 */
public class VariableRangeVisitor extends AstMapVisitor {
    public static List<String> interestedVarName; //variable names that we realize are ssa instances that we need to traverse them

    public static List<String> relatedSymInput;

    public VariableRangeVisitor(ExprVisitor<Expression> exprVisitor, String varName) {
        super(exprVisitor);
        interestedVarName = new ArrayList<>();
        interestedVarName.add(varName);
        relatedSymInput = new ArrayList<>();
    }

    @Override
    public Stmt visit(AssignmentStmt a) {
        if (interestedVarName.contains(a.lhs.toString())) {
            VariableRangeExprVisitor.inVarOfInterestScope = true;
            interestedVarName.remove(a.lhs.toString()); //removing what we have visited in its definition.
            eva.accept(a.rhs);
        }
        return a;
    }

    @Override
    public Stmt visit(CompositionStmt a) { //traversing backward.
        a.s2.accept(this);
        a.s1.accept(this);
        return a;
    }

    @Override
    public Stmt visit(IfThenElseStmt a) {
        a.thenStmt.accept(this);
        a.elseStmt.accept(this);
        return a;
    }

    @Override
    public Stmt visit(SkipStmt a) {
        return a;
    }

    @Override
    public Stmt visit(SPFCaseStmt c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ArrayLoadInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ArrayStoreInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(SwitchInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ReturnInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(GetInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(PutInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(NewInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(InvokeInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ArrayLengthInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ThrowInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(CheckCastInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(InstanceOfInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(PhiInstruction c) {
        assert false;
        return null;
    }

    public static BinaryExpr getRangeExpr(IdExpr expr, IdExpr holeExpr) {
        int specOutIndex = DiscoverContract.contract.tInOutManager.indexOfOutputVar(expr.toString());
        if (specOutIndex == -1) { //if we can't find it in the output, then we can't do a range analysis for that variable, since that would mean that it is either a free input or a state variable, in both cases we can't analyze its range of values.
            return null;
        }
        String varName = DiscoverContract.contract.rInOutManager.varOutNameByIndex(specOutIndex);

        VariableRangeExprVisitor varRangeExprVisitor = new VariableRangeExprVisitor();
        VariableRangeVisitor varRangeVisitor = new VariableRangeVisitor(varRangeExprVisitor, varName);
        DiscoverContract.dynRegion.dynStmt.accept(varRangeVisitor);

        List<Integer> rangeValues = VariableRangeExprVisitor.rangeValues;
        if (rangeValues.size() != 0) {//some range of value was found for the variable
            assert interestedVarName.size() == 0; // we should have visited all definitions by now.
            return new BinaryExpr(new BinaryExpr(holeExpr, BinaryOp.LESSEQUAL, new IntExpr(Collections.max(rangeValues))), BinaryOp.AND,
                    new BinaryExpr(holeExpr, BinaryOp.GREATEREQUAL, new IntExpr(Collections.min(rangeValues))));
        }
        return null;
    }

    public static BinaryExpr getInputCondRangeExpr(String varName, IdExpr holeExpr) {

        //attempting range condition variable range for inputs
        RangeCondExprVisitor rangeCondExprVisitor = new RangeCondExprVisitor(varName);
        RangeCondVisitor rangeCondVisitor = new RangeCondVisitor(rangeCondExprVisitor, varName);
        DiscoverContract.dynRegion.dynStmt.accept(rangeCondVisitor);

        List<Integer> condRangeValues = RangeCondExprVisitor.rangeValues;
        if (condRangeValues.size() != 0) {//some range of value was found for the variable
            condRangeValues.add(new Integer(Collections.min(condRangeValues) - 1));
            condRangeValues.add(new Integer(Collections.max(condRangeValues) + 1));
            return createRangeCondExpr(holeExpr, condRangeValues, condRangeValues);
        }
        return null;
    }

    private static BinaryExpr createRangeCondExpr(IdExpr holeExpr, List<Integer> modifiedCondRangeValues, List<Integer> origCondRangeValues) {
        if (modifiedCondRangeValues.size() == 1) { //base case
//            return createRangeCondElseExpr(holeExpr, origCondRangeValues);
            return new BinaryExpr(holeExpr, BinaryOp.EQUAL, new IntExpr(modifiedCondRangeValues.get(0)));
        }
        return new BinaryExpr(new BinaryExpr(holeExpr, BinaryOp.EQUAL, new IntExpr(modifiedCondRangeValues.get(0))), BinaryOp.OR, createRangeCondExpr(holeExpr, modifiedCondRangeValues.subList(1, modifiedCondRangeValues.size()), origCondRangeValues));
    }

    private static BinaryExpr createRangeCondElseExpr(IdExpr holeExpr, List<Integer> condRangeValues) { //choosing another variable for the else range

        BinaryExpr elseVarValue = new BinaryExpr(holeExpr, BinaryOp.EQUAL, new IntExpr(Collections.max(condRangeValues) + 1));
        return elseVarValue;
        /*if (condRangeValues.size() == 1) { //base case
            return new BinaryExpr(holeExpr, BinaryOp.NOTEQUAL, new IntExpr(condRangeValues.get(0)));
        }
        return new BinaryExpr(new BinaryExpr(holeExpr, BinaryOp.NOTEQUAL, new IntExpr(condRangeValues.get(0))), BinaryOp.AND, createRangeCondElseExpr(holeExpr, condRangeValues.subList(1, condRangeValues.size())));*/
    }


}
