package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput;

import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.*;

import java.util.HashSet;

public class LastSSAExpVisitor implements ExprVisitor {

    private final HashSet<String> defVarsSet;

    public LastSSAExpVisitor(HashSet<String> defVarsSet) {
        this.defVarsSet = defVarsSet;
    }

    @Override
    public Object visit(IntConstant expr) {
        return null;
    }

    @Override
    public Object visit(IntVariable expr) {
        defVarsSet.add(expr.toString());
        return null;
    }

    @Override
    public Object visit(Operation expr) {
        return null;
    }

    @Override
    public Object visit(RealConstant expr) {
        return null;
    }

    @Override
    public Object visit(RealVariable expr) {
        return null;
    }

    @Override
    public Object visit(StringConstantGreen expr) {
        return null;
    }

    @Override
    public Object visit(StringVariable expr) {
        return null;
    }

    @Override
    public Object visit(IfThenElseExpr expr) {
        return null;
    }

    @Override
    public Object visit(ArrayRefVarExpr expr) {
        return null;
    }

    @Override
    public Object visit(WalaVarExpr expr) {
        defVarsSet.add(expr.toString());
        return null;
    }

    @Override
    public Object visit(FieldRefVarExpr expr) {
        defVarsSet.add(expr.toString());
        return null;
    }

    @Override
    public Object visit(GammaVarExpr expr) {
        return null;
    }

    @Override
    public Object visit(AstVarExpr expr) {
        defVarsSet.add(expr.toString());
        return null;
    }
}
