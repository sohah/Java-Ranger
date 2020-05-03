package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics.ExprSizeVisitor;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.mutate;

public class MutateExpr implements ExprVisitor<Expr> {

    private final MutationType mutationType;
    private final ShouldApplyMutation shouldApplyMutation;

    MutateExpr(MutationType mutationType,
               ShouldApplyMutation shouldApplyMutation) {
        this.mutationType = mutationType;

        this.shouldApplyMutation = shouldApplyMutation;

    }

    BinaryOp applyBinaryOpMutation(BinaryOp origOp, BinaryOp[] mutatedOpArr) {
        for (BinaryOp mutatedOp: mutatedOpArr) {
            if (shouldApplyMutation.shouldApplyMutation())
                return mutatedOp;
        }
        return origOp;
    }


    @Override
    public Expr visit(ArrayAccessExpr e) {
        shouldApplyMutation.justDidMutation = false;
        return new ArrayAccessExpr(e.array.accept(this), e.index.accept(this));
    }

    @Override
    public Expr visit(ArrayExpr e) {
        shouldApplyMutation.justDidMutation = false;
        List<Expr> newElems = new ArrayList<>();
        for(Expr expr: e.elements) {
            newElems.add(expr.accept(this));
        }
        return new ArrayExpr(newElems);
    }

    @Override
    public Expr visit(ArrayUpdateExpr e) {
        shouldApplyMutation.justDidMutation = false;
        return new ArrayUpdateExpr(e.array.accept(this), e.index.accept(this), e.value.accept(this));
    }

    @Override
    public Expr visit(BinaryExpr e) {
        if (e.op == BinaryOp.ARROW) {
            return new BinaryExpr(e.location,
                    e.left.accept(this), e.op, e.right.accept(this));
        }
        Expr repairExpr = shouldApplyMutation.wrapRepairExpr(e);
        if (repairExpr instanceof RepairExpr) {
            boolean prevDidMutation = shouldApplyMutation.didMutation();
            Expr newRepairOrigExpr = ((RepairExpr) repairExpr).origExpr.accept(this);
            boolean nowDidMutation = shouldApplyMutation.didMutation();
            shouldApplyMutation.isPerfect = !prevDidMutation && nowDidMutation;
            shouldApplyMutation.isSmallestWrapper = shouldApplyMutation.isPerfect && shouldApplyMutation.justDidMutation;
            return new RepairExpr(newRepairOrigExpr, ((RepairExpr) repairExpr).repairNode);
        }
        Expr applyMCO = mutateMCO((BinaryExpr) repairExpr);
        if (!shouldApplyMutation.didMutation()) {
            Expr leftExpr = e.left.accept(this);
            BinaryOp newOp = mutate(mutationType, e.op, this);
            boolean mutatedOp = newOp != e.op;
            Expr rightExpr = e.right.accept(this);
            shouldApplyMutation.justDidMutation = mutatedOp;
            return new BinaryExpr(e.location,
                    leftExpr, newOp, rightExpr);
        } else {
            return applyMCO;
        }
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



    @Override
    public Expr visit(BoolExpr e) {
        shouldApplyMutation.justDidMutation = false; return e;
    }

    @Override
    public Expr visit(CastExpr e) {
        Expr newExpr = new CastExpr(e.location, e.type, e.expr.accept(this));
        shouldApplyMutation.justDidMutation = false;
        return newExpr;
    }

    @Override
    public Expr visit(CondactExpr e) {
        ArrayList<Expr> args = new ArrayList<>();
        e.args.forEach(expr -> args.add(expr.accept(this)));
        ArrayList<Expr> nodeCallArgs = new ArrayList<>();
        e.call.args.forEach(expr -> nodeCallArgs.add(expr.accept(this)));
        NodeCallExpr newCallExpr = new NodeCallExpr(e.call.node, nodeCallArgs);
        shouldApplyMutation.justDidMutation = false;
        return new CondactExpr(e.clock.accept(this), newCallExpr, args);
    }

    @Override
    public Expr visit(FunctionCallExpr e) {
        ArrayList<Expr> args = new ArrayList<>();
        e.args.forEach(expr -> args.add(expr.accept(this)));
        shouldApplyMutation.justDidMutation = false;
        return new FunctionCallExpr(e.function, args);
    }

    @Override
    public Expr visit(IdExpr e) {
        shouldApplyMutation.justDidMutation = false; return e;
    }

    @Override
    public Expr visit(IfThenElseExpr e) {
        Expr newExpr = new IfThenElseExpr(e.cond.accept(this), e.thenExpr.accept(this), e.elseExpr.accept(this));
        shouldApplyMutation.justDidMutation = false;
        return newExpr;
    }

    @Override
    public Expr visit(IntExpr e) {
        shouldApplyMutation.justDidMutation = false; return e;
    }

    @Override
    public Expr visit(NodeCallExpr e) {
        ArrayList<Expr> args = new ArrayList<>();
        e.args.forEach(expr -> args.add(expr.accept(this)));
        shouldApplyMutation.justDidMutation = false;
        return new NodeCallExpr(e.node, args);
    }

    @Override
    public Expr visit(RepairExpr e) {
        shouldApplyMutation.justDidMutation = false; return e;
    }

    @Override
    public Expr visit(RealExpr e) {
        shouldApplyMutation.justDidMutation = false; return e;
    }

    @Override
    public Expr visit(RecordAccessExpr e) {
        Expr newExpr = new RecordAccessExpr(e.record.accept(this), e.field);
        shouldApplyMutation.justDidMutation = false;
        return newExpr;
    }

    @Override
    public Expr visit(RecordExpr e) {
        HashMap<String, Expr> fields = new HashMap<>();
        e.fields.forEach((key, value) -> fields.put(key, value.accept(this)));
        shouldApplyMutation.justDidMutation = false;
        return new RecordExpr(e.id, fields);
    }

    @Override
    public Expr visit(RecordUpdateExpr e) {
        RecordUpdateExpr newExpr = new RecordUpdateExpr(e.record.accept(this), e.field, e.value.accept(this));
        shouldApplyMutation.justDidMutation = false;
        return newExpr;
    }

    @Override
    public Expr visit(TupleExpr e) {
        ArrayList<Expr> elems = new ArrayList<>();
        e.elements.forEach(elem -> elems.add(elem.accept(this)));
        shouldApplyMutation.justDidMutation = false;
        return new TupleExpr(elems);
    }

    @Override
    public Expr visit(UnaryExpr e) {
        Expr repairExpr = shouldApplyMutation.wrapRepairExpr(e);
        if (repairExpr instanceof RepairExpr) {
            boolean prevDidMutation = shouldApplyMutation.didMutation();
            Expr newRepairOrigExpr = ((RepairExpr) repairExpr).origExpr.accept(this);
            boolean nowDidMutation = shouldApplyMutation.didMutation();
            shouldApplyMutation.isPerfect = !prevDidMutation && nowDidMutation;
            shouldApplyMutation.isSmallestWrapper = shouldApplyMutation.isPerfect && shouldApplyMutation.justDidMutation;
            return new RepairExpr(newRepairOrigExpr, ((RepairExpr) repairExpr).repairNode);
        }
        if (!shouldApplyMutation.didMutation()) {
            Expr expr = e.expr.accept(this);
            shouldApplyMutation.justDidMutation = false;
            return new UnaryExpr(e.op, expr);
        } else {
            return e;
        }
    }
}
