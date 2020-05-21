package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.Expression;
import za.ac.sun.cs.green.expr.IntConstant;
import za.ac.sun.cs.green.expr.IntVariable;
import za.ac.sun.cs.green.expr.Operation;

import java.util.ArrayList;
import java.util.List;

public class RangeCondExprVisitor extends ExprMapVisitor implements ExprVisitor<Expression> {

    public static List<Integer> rangeValues;
    private static String interestingInputName;

    public RangeCondExprVisitor(String varName) {
        super();
        rangeValues = new ArrayList<>(); // all possible values encountered for the variable we are looking for.
        interestingInputName = varName;
    }

    @Override
    public Expression visit(Operation expr) {

        if(expr.getOperator() == Operation.Operator.NOT){
            eva.accept(expr.getOperand(0));
            return expr;
        }
        if ((expr.getOperator() == Operation.Operator.EQ) || (expr.getOperator() == Operation.Operator.NE)){
            if ((expr.getOperand(0).toString().equals(interestingInputName)) &&
                    (expr.getOperand(1) instanceof IntConstant)) {
                rangeValues.add(((IntConstant) expr.getOperand(1)).getValue());
            }
        }
        return expr;
    }

    @Override
    public Expression visit(IntConstant expr) {
        assert false;
        return expr;
    }

    @Override
    public Expression visit(IntVariable expr) {
        assert false;
        return expr;
    }

    @Override
    public Expression visit(GammaVarExpr expr) {
        eva.accept(expr.condition);
        if (expr.thenExpr instanceof GammaVarExpr)
            eva.accept(expr.thenExpr);
        if (expr.elseExpr instanceof GammaVarExpr)
            eva.accept(expr.elseExpr);
        return expr;
    }

    @Override
    public Expression visit(IfThenElseExpr expr) {
        assert false;
        return expr;
    }

    @Override
    public Expression visit(WalaVarExpr expr) {
        assert false;
        return expr;
    }

    @Override
    public Expression visit(AstVarExpr expr) {
        assert false;
        return expr;
    }

    @Override
    public Expression visit(FieldRefVarExpr expr) {
        assert false;
        return expr;
    }

    @Override
    public Expression visit(ArrayRefVarExpr expr) {
        assert false;
        return expr;
    }


}
