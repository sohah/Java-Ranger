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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class OROMutationVisitor implements ExprVisitor<Expr> {
    private final ArrayList<IdExpr> idExprs;
    private final ArrayList<Expr> constExprs;
    private final MutateExpr.ShouldApplyMutation shouldApplyMutation;
    public OROMutationVisitor(MutateExpr.ShouldApplyMutation shouldApplyMutation, ArrayList<IdExpr> idExprs, ArrayList<Expr> constExprs) {
        this.idExprs = idExprs;
        this.constExprs = constExprs;
        this.shouldApplyMutation = shouldApplyMutation;
    }

    @Override
    public Expr visit(ArrayAccessExpr e) {
        return new ArrayAccessExpr(e.array.accept(this), e.index.accept(this));
    }

    @Override
    public Expr visit(ArrayExpr e) {
        List<Expr> newElems = new ArrayList<>();
        for(Expr expr: e.elements) {
            newElems.add(expr.accept(this));
        }
        return new ArrayExpr(newElems);
    }

    @Override
    public Expr visit(ArrayUpdateExpr e) {
        return new ArrayUpdateExpr(e.array.accept(this), e.index.accept(this), e.value.accept(this));
    }

    @Override
    public Expr visit(BinaryExpr e) {
        return new BinaryExpr(e.left.accept(this), e.op, e.right.accept(this));
    }

    @Override
    public Expr visit(BoolExpr e) {
        for (Expr newConstExpr: constExprs) {
            if (newConstExpr instanceof BoolExpr && ((BoolExpr) newConstExpr).value != e.value) {
                if (shouldApplyMutation.shouldApplyMutation())
                    return new BoolExpr(((BoolExpr) newConstExpr).value);
            }
        }
        return e;
    }

    @Override
    public Expr visit(CastExpr e) {
        return new CastExpr(e.type, e.expr.accept(this));
    }

    @Override
    public Expr visit(CondactExpr e) {
        List<Expr> newArgs = new ArrayList<>();
        for(Expr expr: e.args) {
            newArgs.add(expr.accept(this));
        }
        return new CondactExpr(e.clock.accept(this), (NodeCallExpr) e.call.accept(this), newArgs);
    }

    @Override
    public Expr visit(FunctionCallExpr e) {
        List<Expr> newArgs = new ArrayList<>();
        for(Expr expr: e.args) {
            newArgs.add(expr.accept(this));
        }
        return new FunctionCallExpr(e.function, newArgs);
    }

    @Override
    public Expr visit(IdExpr e) {
        for (IdExpr newIdExpr: idExprs) {
            if (!newIdExpr.id.equals(e.id)) {
                if (shouldApplyMutation.shouldApplyMutation())
                    return new IdExpr(newIdExpr.id);
            }
        }
        return e;
    }

    @Override
    public Expr visit(IfThenElseExpr e) {
        return new IfThenElseExpr(e.cond.accept(this), e.thenExpr.accept(this), e.elseExpr.accept(this));
    }

    @Override
    public Expr visit(IntExpr e) {
        for (Expr newConstExpr: constExprs) {
            if (newConstExpr instanceof IntExpr && !((IntExpr) newConstExpr).value.equals(e.value)) {
                if (shouldApplyMutation.shouldApplyMutation())
                    return new IntExpr(((IntExpr) newConstExpr).value);
            }
        }
        return e;
    }

    @Override
    public Expr visit(NodeCallExpr e) {
        List<Expr> newArgs = new ArrayList<>();
        for(Expr arg: e.args) {
            newArgs.add(arg.accept(this));
        }
        return new NodeCallExpr(e.node, newArgs);
    }

    @Override
    public Expr visit(RepairExpr e) {
        return e;
    }

    @Override
    public Expr visit(RealExpr e) {
        for (Expr newConstExpr: constExprs) {
            if (newConstExpr instanceof RealExpr && !((RealExpr) newConstExpr).value.equals(e.value)) {
                if (shouldApplyMutation.shouldApplyMutation())
                    return new RealExpr(((RealExpr) newConstExpr).value);
            }
        }
        return e;
    }

    @Override
    public Expr visit(RecordAccessExpr e) {
        return e.record.accept(this);
    }

    @Override
    public Expr visit(RecordExpr e) {
        Iterator itr = e.fields.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<String, Expr> field = (Map.Entry<String, Expr>) itr.next();
            Expr newExpr = field.getValue().accept(this);
            e.fields.put(field.getKey(), newExpr);
        }
        return e;
    }

    @Override
    public Expr visit(RecordUpdateExpr e) {
        return new RecordUpdateExpr(e.record.accept(this), e.field, e.value.accept(this));
    }

    @Override
    public Expr visit(TupleExpr e) {
        List<Expr> newElems = new ArrayList<>();
        for(Expr expr: e.elements) {
            newElems.add(expr.accept(this));
        }
        return new TupleExpr(newElems);
    }

    @Override
    public Expr visit(UnaryExpr e) {
        return new UnaryExpr(e.op, e.expr.accept(this));
    }
}
