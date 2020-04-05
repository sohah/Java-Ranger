package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.RepairMode;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

import java.util.ArrayList;
import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.mutate;

public class MutateExpr implements ExprVisitor<Expr> {
    private final int prevMutationIndex, prevRepairMutationIndex;
    private final MutationType mutationType;
    private final SpecInOutManager tInOutManager;
    private final ShouldApplyMutation shouldApplyMutation;
    public List<GenericRepairNode> repairNodes = new ArrayList<>();

    private int mutationIndex;
    private int repairMutationIndex;

    MutateExpr(MutationType mutationType, int prevMutationIndex, int prevRepairMutationIndex, SpecInOutManager tInOutManager) {
        this.mutationType = mutationType;
        this.prevMutationIndex = prevMutationIndex;
        this.prevRepairMutationIndex = prevRepairMutationIndex;
        this.mutationIndex = -1;
        this.repairMutationIndex = -1;
        this.tInOutManager = tInOutManager;
        this.shouldApplyMutation = new ShouldApplyMutation();
    }

    boolean didMutation() {
        return mutationIndex > prevMutationIndex;
    }

    private int incAndGetMutationIndex() {
        return ++mutationIndex;
    }

    private int incAndGetRepairMutationIndex() {
        return ++repairMutationIndex;
    }

    public boolean addedRepairWrapper() {
        return repairMutationIndex > prevRepairMutationIndex;
    }

    public class ShouldApplyMutation {
        public boolean shouldApplyMutation() {
            return incAndGetMutationIndex() == prevMutationIndex + 1;
        }
        public boolean shouldApplyRepairMutation() {
            return incAndGetRepairMutationIndex() == prevRepairMutationIndex + 1;
        }
    };

    BinaryOp applyBinaryOpMutation(BinaryOp origOp, BinaryOp[] mutatedOpArr) {
        for (BinaryOp mutatedOp: mutatedOpArr) {
            if (shouldApplyMutation.shouldApplyMutation())
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
        if (e.op == BinaryOp.ARROW) {
            return new BinaryExpr(e.location,
                    e.left.accept(this), e.op, e.right.accept(this));
        }
        Expr repairExpr = wrapRepairExpr(e);
        if (repairExpr instanceof RepairExpr) {
            return new RepairExpr(((RepairExpr) repairExpr).origExpr.accept(this), ((RepairExpr) repairExpr).repairNode);
        }
        Expr applyMCO = mutateMCO((BinaryExpr) repairExpr);
        if (!didMutation()) {
            Expr applyORO = mutateORO(e);
            if (!didMutation()) {
                return new BinaryExpr(e.location,
                        e.left.accept(this), mutate(mutationType, e.op, this), e.right.accept(this));
            } else return applyORO;
        } else return applyMCO;
    }

    private Expr mutateORO(BinaryExpr e) {
        if (mutationType == MutationType.OPERAND_REPLACEMENT_MUT) {
            IdExprVisitor idExprVisitor = new IdExprVisitor(e, tInOutManager);
            e.accept(idExprVisitor);
            ArrayList<IdExpr> idExprs = idExprVisitor.getIdExprs();
            ConstExprVisitor constExprVisitor = new ConstExprVisitor();
            e.accept(constExprVisitor);
            ArrayList<Expr> constExprs = constExprVisitor.getConstExprs();
            OROMutationVisitor oroVisitor = new OROMutationVisitor(shouldApplyMutation, idExprs, constExprs);
            return e.accept(oroVisitor);
        }
        return e;
    }

    private Expr mutateMCO(BinaryExpr e) {
        if (mutationType == MutationType.MISSING_COND_MUT) {
            if (e.op == BinaryOp.IMPLIES || e.op == BinaryOp.AND || e.op == BinaryOp.OR) {
                boolean replaceLHSWithTrue = shouldApplyMutation.shouldApplyMutation();
                boolean replaceLHSWithFalse = shouldApplyMutation.shouldApplyMutation();
                boolean replaceRHSWithTrue = shouldApplyMutation.shouldApplyMutation();
                boolean replaceRHSWithFalse = shouldApplyMutation.shouldApplyMutation();
                BinaryExpr trueExpr = new BinaryExpr(new IntExpr(0), BinaryOp.EQUAL, new IntExpr(0));
                BinaryExpr falseExpr = new BinaryExpr(new IntExpr(0), BinaryOp.EQUAL, new IntExpr(1));
                if (replaceLHSWithTrue) { return new BinaryExpr(trueExpr, e.op, e.right); }
                if (replaceLHSWithFalse) { return new BinaryExpr(falseExpr, e.op, e.right); }
                if (replaceRHSWithTrue) { return new BinaryExpr(e.left, e.op, trueExpr); }
                if (replaceRHSWithFalse) { return new BinaryExpr(e.left, e.op, falseExpr); }
            }
        }
        return e;
    }

    private Expr wrapRepairExpr(BinaryExpr e) {
        if (shouldApplyMutation.shouldApplyRepairMutation()) {
            IdExprVisitor idExprVisitor = new IdExprVisitor(e, tInOutManager);
            e.accept(idExprVisitor);
            List<VarDecl> varDecls = idExprVisitor.getVarDeclList();
            GenericRepairNode genericRepairNode = new GenericRepairNode(varDecls);
            repairNodes.add(genericRepairNode);
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
