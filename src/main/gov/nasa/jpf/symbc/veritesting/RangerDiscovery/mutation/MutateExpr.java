package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

import java.util.ArrayList;
import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.mutate;

public class MutateExpr implements ExprVisitor<Expr> {
    private final int previousMutationIndex;
    private final MutationType mutationType;
    private final SpecInOutManager tInOutManager;

    private int mutationIndex;

    MutateExpr(MutationType mutationType, int previousMutationIndex, SpecInOutManager tInOutManager) {
        this.mutationType = mutationType;
        this.previousMutationIndex = previousMutationIndex;
        this.mutationIndex = -1;
        this.tInOutManager = tInOutManager;
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
        Expr repairExpr = wrapRepairExpr(e);
        if (!didMutation())
            return new BinaryExpr(e.location,
                    e.left.accept(this), mutate(mutationType, e.op, this), e.right.accept(this));
        else return repairExpr;
    }

    private Expr wrapRepairExpr(BinaryExpr e) {
        if (shouldApplyMutation() && mutationType == MutationType.REPAIR_EXPR_MUT) {
            IdExprVisitor idExprVisitor = new IdExprVisitor(e, tInOutManager);
            e.accept(idExprVisitor);
            List<VarDecl> varDecls = idExprVisitor.getVarDeclList();
            GenericRepairNode genericRepairNode = new GenericRepairNode(varDecls);
            NodeCallExpr callExpr = genericRepairNode.callExpr;
            RepairExpr repairExpr = new RepairExpr(e, callExpr);
            return repairExpr;
        } else return e;
    }

    private ArrayList<IdExpr> getAllIdExpr(Expr e) {
        ArrayList<IdExpr> ids = new ArrayList<>();
        if (e instanceof IdExpr) {
            ids.add((IdExpr) e);
        } else if (e instanceof BinaryExpr) {
            ids.addAll(getAllIdExpr(((BinaryExpr) e).left));
            ids.addAll(getAllIdExpr(((BinaryExpr) e).right));
        } else if (e instanceof UnaryExpr) {
            ids.addAll(getAllIdExpr(((UnaryExpr) e).expr));
        }
        return ids;
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
