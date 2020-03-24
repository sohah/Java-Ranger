package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.ast.def.GammaVarExpr;
import gov.nasa.jpf.symbc.veritesting.ast.def.IfThenElseExpr;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprMapVisitor;

import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.*;

import java.util.ArrayList;
import java.util.List;

public class VariableRangeExprVisitor extends ExprMapVisitor implements ExprVisitor<Expression> {

    List<Integer> rangeValues = new ArrayList<>();

    @Override
    public Expression visit(IntConstant expr) {
        rangeValues.add(expr.getValue());
        return expr;
    }


    @Override
    public Expression visit(GammaVarExpr expr) {
        return new GammaVarExpr(expr.condition,
                eva.accept(expr.thenExpr),
                eva.accept(expr.elseExpr));
    }

    @Override
    public Expression visit(IfThenElseExpr expr) {
        return new IfThenElseExpr(expr.condition,
                eva.accept(expr.thenExpr),
                eva.accept(expr.elseExpr));
    }


}
