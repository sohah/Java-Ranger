package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.*;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.visitors.ExprVisitor;

/*
This class is used to unwrap an expression from any internal repair wrapping
 */
public class NestedRepairsVisitor implements ExprVisitor<Expr>{

    public boolean hasNestedRepairs = false;

    @Override
    public Expr visit(ArrayAccessExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(ArrayExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(ArrayUpdateExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(BinaryExpr e) {
        Expr left = e.left.accept(this);
        Expr right = e.right.accept(this);
        return new BinaryExpr(left, e.op, right);
    }

    @Override
    public Expr visit(BoolExpr e) {
        return e;
    }

    @Override
    public Expr visit(CastExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(CondactExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(FunctionCallExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(IdExpr e) {
        return e;
    }

    @Override
    public Expr visit(IfThenElseExpr e) {
        Expr cond = e.cond.accept(this);
        Expr thenExpr = e.thenExpr.accept(this);
        Expr elseExpr = e.elseExpr.accept(this);
        return new IfThenElseExpr(cond, thenExpr, elseExpr);
    }

    @Override
    public Expr visit(IntExpr e) {
        return e;
    }

    @Override
    public Expr visit(NodeCallExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(RepairExpr e) {
        hasNestedRepairs = true;
        return e.origExpr.accept(this);
    }

    @Override
    public Expr visit(RealExpr e) {
        return e;
    }

    @Override
    public Expr visit(RecordAccessExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(RecordExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(RecordUpdateExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(TupleExpr e) {
        assert false;
        return null;
    }

    @Override
    public Expr visit(UnaryExpr e) {
        return new UnaryExpr(e.op, e.expr.accept(this));
    }
}
