package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;
import jkind.lustre.ArrayAccessExpr;
import jkind.lustre.ArrayExpr;
import jkind.lustre.ArrayUpdateExpr;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.CastExpr;
import jkind.lustre.CondactExpr;
import jkind.lustre.Expr;
import jkind.lustre.FunctionCallExpr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NamedType;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.mutate;

public class OROMutationVisitor implements ExprVisitor<Expr> {
    private final ArrayList<IdExpr> idExprs;
    private final ArrayList<Expr> constExprs;
    private final HashMap<String, NamedType> types;
    private final ShouldApplyMutation shouldApplyMutation;

    private static boolean inRepairContext = false;

    public OROMutationVisitor(ShouldApplyMutation shouldApplyMutation, ArrayList<IdExpr> idExprs,
                              ArrayList<Expr> constExprs, HashMap<String, NamedType> types) {
        this.idExprs = idExprs;
        this.constExprs = constExprs;
        this.shouldApplyMutation = shouldApplyMutation;
        this.types = types;
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
        Expr leftExpr = e.left.accept(this);
        Expr rightExpr = e.right.accept(this);
        BinaryExpr newExpr = new BinaryExpr(e.location, leftExpr, e.op, rightExpr);
        shouldApplyMutation.justDidMutation = checkImmediateOROMutation(e, newExpr);
        return newExpr;
    }

    @Override
    public Expr visit(BoolExpr e) {
        for (Expr newConstExpr: constExprs) {
            if (newConstExpr instanceof BoolExpr && ((BoolExpr) newConstExpr).value != e.value) {
                if (shouldApplyMutation.shouldApplyMutation()) {
                    shouldApplyMutation.justDidMutation = true;
                    return new BoolExpr(((BoolExpr) newConstExpr).value);
                }
            }
        }
        return e;
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
        for (IdExpr newIdExpr: idExprs) {
            if (!newIdExpr.id.equals(e.id)) {
                if (getType(newIdExpr).equals(getType(e)) && shouldApplyMutation.shouldApplyMutation()) {
                    shouldApplyMutation.justDidMutation = true;
                    return new IdExpr(newIdExpr.id);
                }
            }
        }
        return e;
    }

    private NamedType getType(IdExpr idExpr) {
        return types.get(idExpr.id);
    }

    @Override
    public Expr visit(IfThenElseExpr e) {
        Expr newExpr = new IfThenElseExpr(e.cond.accept(this), e.thenExpr.accept(this), e.elseExpr.accept(this));
        shouldApplyMutation.justDidMutation = false;
        return newExpr;
    }

    @Override
    public Expr visit(IntExpr e) {
        for (Expr newConstExpr: constExprs) {
            if (newConstExpr instanceof IntExpr && !((IntExpr) newConstExpr).value.equals(e.value)) {
                if (shouldApplyMutation.shouldApplyMutation()) {
                    shouldApplyMutation.justDidMutation = true;
                    return new IntExpr(((IntExpr) newConstExpr).value);
                }
            }
        }
        return e;
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
        boolean oldRepairContext = inRepairContext;
        inRepairContext = true;
        shouldApplyMutation.justDidMutation = false;
        Expr innerMutation = e.origExpr.accept(this);
        inRepairContext = oldRepairContext;

        //this is to handle the repair wrapping that resulted from the wrapping in ShouldApplyMutation
        NestedRepairsVisitor nestedRepairsVisitor = new NestedRepairsVisitor();
        Expr unwrapRepair = innerMutation.accept(nestedRepairsVisitor);

        if(inRepairContext)
            return innerMutation;
        else if(nestedRepairsVisitor.hasNestedRepairs)
            return new RepairExpr(unwrapRepair, e.repairNode);
        else return  new RepairExpr(innerMutation, e.repairNode);
    }

    @Override
    public Expr visit(RealExpr e) {
        for (Expr newConstExpr: constExprs) {
            if (newConstExpr instanceof RealExpr && !((RealExpr) newConstExpr).value.equals(e.value)) {
                if (shouldApplyMutation.shouldApplyMutation()) {
                    shouldApplyMutation.justDidMutation = true;
                    return new RealExpr(((RealExpr) newConstExpr).value);
                }
            }
        }
        return e;
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

    public boolean checkImmediateOROMutation(Expr oldExpr, Expr newExpr) {
        if (oldExpr instanceof BinaryExpr && newExpr instanceof BinaryExpr) {
            Expr oldLeftExpr = ((BinaryExpr) oldExpr).left;
            Expr oldRightExpr = ((BinaryExpr) oldExpr).right;
            Expr newLeftExpr = ((BinaryExpr) newExpr).left;
            Expr newRightExpr = ((BinaryExpr) newExpr).right;
            if (oldLeftExpr instanceof IdExpr && newLeftExpr instanceof IdExpr && !((IdExpr) oldLeftExpr).id.equals(((IdExpr) newLeftExpr).id))
                return true;
            if (oldRightExpr instanceof IdExpr && newRightExpr instanceof IdExpr && !((IdExpr) oldRightExpr).id.equals(((IdExpr) newRightExpr).id))
                return true;
            if (oldLeftExpr instanceof IntExpr && newLeftExpr instanceof IntExpr && !((IntExpr) oldLeftExpr).value.equals(((IntExpr) newLeftExpr).value))
                return true;
            if (oldRightExpr instanceof IntExpr && newRightExpr instanceof IntExpr && !((IntExpr) oldRightExpr).value.equals(((IntExpr) newRightExpr).value))
                return true;
            if (oldLeftExpr instanceof BoolExpr && newLeftExpr instanceof BoolExpr && !((BoolExpr) oldLeftExpr).value == ((BoolExpr) newLeftExpr).value)
                return true;
            if (oldRightExpr instanceof BoolExpr && newRightExpr instanceof BoolExpr && !((BoolExpr) oldRightExpr).value == ((BoolExpr) newRightExpr).value)
                return true;
            if (oldLeftExpr instanceof RealExpr && newLeftExpr instanceof RealExpr && !((RealExpr) oldLeftExpr).value.equals(((RealExpr) newLeftExpr).value))
                return true;
            if (oldRightExpr instanceof RealExpr && newRightExpr instanceof RealExpr && !((RealExpr) oldRightExpr).value.equals(((RealExpr) newRightExpr).value))
                return true;
            return false;
        } else if (oldExpr instanceof UnaryExpr && newExpr instanceof UnaryExpr) {
            Expr oldUnaryExpr = ((UnaryExpr) oldExpr).expr;
            Expr newUnaryExpr = ((UnaryExpr) newExpr).expr;
            if (oldUnaryExpr instanceof IdExpr && newUnaryExpr instanceof IdExpr && !((IdExpr) oldUnaryExpr).id.equals(((IdExpr) newUnaryExpr).id))
                return true;
            if (oldUnaryExpr instanceof IntExpr && newUnaryExpr instanceof IntExpr && !((IntExpr) oldUnaryExpr).value.equals(((IntExpr) newUnaryExpr).value))
                return true;
            if (oldUnaryExpr instanceof BoolExpr && newUnaryExpr instanceof BoolExpr && !((BoolExpr) oldUnaryExpr).value == ((BoolExpr) newUnaryExpr).value)
                return true;
            if (oldUnaryExpr instanceof RealExpr && newUnaryExpr instanceof RealExpr && !((RealExpr) oldUnaryExpr).value.equals(((RealExpr) newUnaryExpr).value))
                return true;
            return false;
        }
        return false;
    }

}
