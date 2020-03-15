package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.mutate;

public class MutateExpr implements ExprVisitor<Expr> {
    private final int previousMutationIndex;
    private final MutationType mutationType;

    private int mutationIndex;

    MutateExpr(MutationType mutationType, int previousMutationIndex) {
        this.mutationType = mutationType;
        this.previousMutationIndex = previousMutationIndex;
        this.mutationIndex = -1;
    }

    boolean didMutation() {
        return mutationIndex > previousMutationIndex;
    }

    private int incAndGetMutationIndex() {
        return ++mutationIndex;
    }

    private boolean shouldApplyMutation() {
        return incAndGetMutationIndex() == previousMutationIndex + 1;
    }

    BinaryOp applyBinaryOpMutation(BinaryOp origOp, BinaryOp[] mutatedOpArr) {
        for (BinaryOp mutatedOp: mutatedOpArr) {
            if (shouldApplyMutation())
                return mutatedOp;
        }
        return origOp;
    }


    @Override
    public Expr visit(ArrayAccessExpr e) {
        return e;
    }

    @Override
    public Expr visit(ArrayExpr e) {
        return e;
    }

    @Override
    public Expr visit(ArrayUpdateExpr e) {
        return e;
    }

    @Override
    public Expr visit(BinaryExpr e) {
        return new BinaryExpr(e.location,
                e.left.accept(this), mutate(mutationType, e.op, this), e.right.accept(this));
    }

    @Override
    public Expr visit(BoolExpr e) {
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
