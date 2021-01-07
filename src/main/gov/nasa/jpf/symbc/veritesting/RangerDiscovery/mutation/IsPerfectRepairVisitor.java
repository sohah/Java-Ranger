package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

public class IsPerfectRepairVisitor implements ExprVisitor<Boolean> {

    private Expr origProp;
    private boolean result;

    public IsPerfectRepairVisitor(Expr origProp) {
        this.origProp = origProp;
    }


    @Override
    public Boolean visit(ArrayAccessExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(ArrayExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(ArrayUpdateExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(BinaryExpr e) {
        assert origProp instanceof BinaryExpr : "the two expressions must be structurally equivalent. Something went wrong. Failing.";

        if(e.op != ((BinaryExpr) origProp).op)
            return false;

        BinaryExpr oldOrigProp = (BinaryExpr) origProp;

        origProp = ((BinaryExpr) origProp).left;
        if(!e.left.accept(this))
            return false;

        origProp = oldOrigProp.right;

        if(!e.right.accept(this))
            return false;

        origProp = oldOrigProp;
        return true;
    }

    @Override
    public Boolean visit(BoolExpr e) {
        return e.toString().equals(origProp.toString());
    }

    @Override
    public Boolean visit(CastExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(CondactExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(FunctionCallExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(IdExpr e) {
        return e.toString().equals(origProp.toString());
    }

    @Override
    public Boolean visit(IfThenElseExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(IntExpr e) {
        return e.toString().equals(origProp.toString());
    }

    @Override
    public Boolean visit(NodeCallExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(RepairExpr e) {
        return true; // regardless what is inside, we can consider repair wrapped around an expression as perfect repair, as long as later, there exists no mutation that is unwrapped with repair expression
    }

    @Override
    public Boolean visit(RealExpr e) {
        return e.toString().equals(origProp.toString());
    }

    @Override
    public Boolean visit(RecordAccessExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(RecordExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(RecordUpdateExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(TupleExpr e) {
        assert false : "unsupported expression. Failing.";
        return null;
    }

    @Override
    public Boolean visit(UnaryExpr e) {
        assert origProp instanceof UnaryExpr : "the two expressions must be structurally equivalent. Something went wrong. Failing.";

        if(e.op != ((UnaryExpr) origProp).op)
            return false;

        UnaryExpr oldOrigProp = (UnaryExpr) origProp;

        origProp = ((UnaryExpr) origProp).expr;
        if(!e.expr.accept(this))
            return false;

        origProp = oldOrigProp;

        return true;
    }


    public static boolean execute(Expr origProp, Expr mutatedProp){
        IsPerfectRepairVisitor isPerfectRepairVisitor = new IsPerfectRepairVisitor(origProp);
        return mutatedProp.accept(isPerfectRepairVisitor);
    }

}
