package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.verification;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.RepairScopeType;
import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;
import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class CheckAllInOutIdVisitor implements ExprVisitor<Expr> {

    public boolean isAllInOut = true;


    @Override
    public Expr visit(ArrayAccessExpr e) {
        if (!isAllInOut)
            return e;
        e.array.accept(this);
        e.index.accept(this);
        return e;
    }

    @Override
    public Expr visit(ArrayExpr e) {
        if (!isAllInOut)
            return e;

        e.elements.forEach(ele -> ele.accept(this));
        return e;
    }

    @Override
    public Expr visit(ArrayUpdateExpr e) {
        if (!isAllInOut)
            return e;

        e.array.accept(this);
        e.index.accept(this);
        return e;
    }

    @Override
    public Expr visit(BinaryExpr e) {
        if (!isAllInOut)
            return e;

        e.left.accept(this);
        e.right.accept(this);
        return e;
    }

    @Override
    public Expr visit(BoolExpr e) {
        return e;
    }

    @Override
    public Expr visit(CastExpr e) {
        if(!isAllInOut)
            return e;

        e.expr.accept(this);
        return e;
    }

    @Override
    public Expr visit(CondactExpr e) {
        if(!isAllInOut)
            return e;

        e.args.forEach(expr -> expr.accept(this));
        e.call.accept(this);
        e.clock.accept(this);
        return e;
    }

    @Override
    public Expr visit(FunctionCallExpr e) {
        if(!isAllInOut)
            return e;

        e.args.forEach(expr -> expr.accept(this));
        return e;
    }

    @Override
    public Expr visit(IdExpr e) {
        if(!isAllInOut)
            return e;

        isAllInOut = checkIsInOut(e);
        return e;
    }

    private boolean checkIsInOut(IdExpr e) {
        SpecInOutManager specInOutManager = DiscoverContract.contract.tInOutManager;
        return specInOutManager.isInputOrOutputByName(e);
    }

    @Override
    public Expr visit(IfThenElseExpr e) {
        if(!isAllInOut)
            return e;

        e.cond.accept(this);
        e.thenExpr.accept(this);
        e.elseExpr.accept(this);
        return e;
    }

    @Override
    public Expr visit(IntExpr e) {
        return e;
    }

    @Override
    public Expr visit(NodeCallExpr e) {
        if(!isAllInOut)
            return e;

        e.args.forEach(expr -> expr.accept(this));
        return e;
    }

    @Override
    public Expr visit(RepairExpr e) {
        if(!isAllInOut)
            return e;

        return e;
    }

    @Override
    public Expr visit(RealExpr e) {
        if(!isAllInOut)
            return e;

        return e;
    }

    @Override
    public Expr visit(RecordAccessExpr e) {
        if(!isAllInOut)
            return e;

        e.record.accept(this);
        return e;
    }

    @Override
    public Expr visit(RecordExpr e) {
        if(!isAllInOut)
            return e;

        e.fields.forEach((key, value) -> value.accept(this));
        return e;
    }

    @Override
    public Expr visit(RecordUpdateExpr e) {
        if(!isAllInOut)
            return e;

        e.record.accept(this);
        e.value.accept(this);
        return e;
    }

    @Override
    public Expr visit(TupleExpr e) {
        if(!isAllInOut)
            return e;

        e.elements.forEach(elem -> elem.accept(this));
        return e;
    }

    @Override
    public Expr visit(UnaryExpr e) {
        if(!isAllInOut)
            return e;

        e.expr.accept(this);
        return e;
    }

}
