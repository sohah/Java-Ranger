package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

// this is now pending
public class ExprSize implements ExprVisitor<Integer> {


    @Override
    public Integer visit(ArrayAccessExpr e) {
        assert false; // I am not expecting to see properties that contains that.
        return null; // size of array access is assumed one, since we can change here only the index of the array.
    }

    @Override
    public Integer visit(ArrayExpr e) {
        //size of an arrayExpr definition is assumed to be the size of its elements.
        assert false; // I am not expecting to see properties that contains that.
        return null;
    }

    @Override
    public Integer visit(ArrayUpdateExpr e) {
        System.out.println("currently unsupported property expression");
        assert false;
        return null;
    }

    @Override
    public Integer visit(BinaryExpr e) {
        return e.left.accept(this) + e.right.accept(this) + 1;
    }

    @Override
    public Integer visit(BoolExpr e) {
        return 1;
    }

    @Override
    public Integer visit(CastExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(CondactExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(FunctionCallExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(IdExpr e) {
        return 1;
    }

    @Override
    public Integer visit(IfThenElseExpr e) {
        return e.cond.accept(this) + e.thenExpr.accept(this) + e.elseExpr.accept(this);
    }

    @Override
    public Integer visit(IntExpr e) {
        return 1;
    }

    @Override
    public Integer visit(NodeCallExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RepairExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RealExpr e) {
        return 1;
    }

    @Override
    public Integer visit(RecordAccessExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RecordExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RecordUpdateExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(TupleExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(UnaryExpr e) {
        return e.expr.accept(this) + 1;
    }
}
