package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.ArrayAccessExpr;
import jkind.lustre.ArrayExpr;
import jkind.lustre.ArrayUpdateExpr;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BoolExpr;
import jkind.lustre.CastExpr;
import jkind.lustre.CondactExpr;
import jkind.lustre.Expr;
import jkind.lustre.FunctionCallExpr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RealExpr;
import jkind.lustre.RecordAccessExpr;
import jkind.lustre.RecordExpr;
import jkind.lustre.RecordUpdateExpr;
import jkind.lustre.RepairExpr;
import jkind.lustre.TupleExpr;
import jkind.lustre.UnaryExpr;
import jkind.lustre.visitors.ExprVisitor;

import java.util.ArrayList;

public class ConstExprVisitor implements ExprVisitor<Expr> {
    public ArrayList<Expr> getConstExprs() {
        return constExprs;
    }

    private final ArrayList<Expr> constExprs;

    public ConstExprVisitor() {
        this.constExprs = new ArrayList<>();
    }


    @Override
    public Expr visit(ArrayAccessExpr e) {
        e.array.accept(this);
        e.index.accept(this);
        return e;
    }

    @Override
    public Expr visit(ArrayExpr e) {
        e.elements.forEach(ele -> ele.accept(this));
        return e;
    }

    @Override
    public Expr visit(ArrayUpdateExpr e) {
        e.array.accept(this);
        e.index.accept(this);
        return e;
    }

    @Override
    public Expr visit(BinaryExpr e) {
        e.left.accept(this);
        e.right.accept(this);
        return e;
    }

    @Override
    public Expr visit(BoolExpr e) {
        if (!constExprs.contains(e)) constExprs.add(e);
        return e;
    }

    @Override
    public Expr visit(CastExpr e) {
        e.expr.accept(this);
        return e;
    }

    @Override
    public Expr visit(CondactExpr e) {
        e.args.forEach(expr -> expr.accept(this));
        e.call.accept(this);
        e.clock.accept(this);
        return e;
    }

    @Override
    public Expr visit(FunctionCallExpr e) {
        e.args.forEach(expr -> expr.accept(this));
        return e;
    }

    @Override
    public Expr visit(IdExpr e) {
        return e;
    }

    @Override
    public Expr visit(IfThenElseExpr e) {
        e.cond.accept(this);
        e.thenExpr.accept(this);
        e.elseExpr.accept(this);
        return e;
    }

    @Override
    public Expr visit(IntExpr e) {
        if (!constExprs.contains(e)) constExprs.add(e);
        return e;
    }

    @Override
    public Expr visit(NodeCallExpr e) {
        e.args.forEach(expr -> expr.accept(this));
        return e;
    }

    @Override
    public Expr visit(RepairExpr e) {
        return e;
    }

    @Override
    public Expr visit(RealExpr e) {
        if (!constExprs.contains(e)) constExprs.add(e);
        return e;
    }

    @Override
    public Expr visit(RecordAccessExpr e) {
        e.record.accept(this);
        return e;
    }

    @Override
    public Expr visit(RecordExpr e) {
        e.fields.forEach((key, value) -> value.accept(this));
        return e;
    }

    @Override
    public Expr visit(RecordUpdateExpr e) {
        e.record.accept(this);
        e.value.accept(this);
        return e;
    }

    @Override
    public Expr visit(TupleExpr e) {
        e.elements.forEach(elem -> elem.accept(this));
        return e;
    }

    @Override
    public Expr visit(UnaryExpr e) {
        e.expr.accept(this);
        return e;
    }
}
