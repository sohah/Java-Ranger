package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;
import jkind.lustre.*;
import jkind.lustre.visitors.ExprVisitor;

import java.util.List;

/**
 * This counts the expression size in two mode. The first mode if we have booleanNodesOnly being true, in this case
 * this class counts the number of boolean nodes in the expression. It therefore requires varDecls of the Ids in the expression.
 * If however it is counting all expressions regardless of their type, then the booleanNodesOnly should be false and varDecls should be null.
 * Caution: this expects either no -> or pre in the expression. Should either count any boolean expr or a property of the form true -> expr,
 * in the later case expr is the one that is actually counted
 */

public class ExprSizeVisitor implements ExprVisitor<Integer> {

    private final boolean booleanNodesOnly;
    private final List<VarDecl> varDecls;

    public ExprSizeVisitor(List<VarDecl> varDecls, boolean booleanNodesOnly) {
        this.booleanNodesOnly = booleanNodesOnly;
        if (!booleanNodesOnly)
            this.varDecls = null;
        else
            this.varDecls = varDecls;
    }

    @Override
    public Integer visit(ArrayAccessExpr e) {
        assert false; // I am not expecting to see properties that contains that.
        return null; // size of array access is assumed one, since we can change here only the index of the array.
    }

    @Override
    public Integer visit(ArrayExpr e) {
        //size of an arrayExpr definition is assumed to be the size of its elements.
        assert false; // I am not expecting to see properties that contains that.
        return null;
    }

    @Override
    public Integer visit(ArrayUpdateExpr e) {
        System.out.println("currently unsupported property expression");
        assert false;
        return null;
    }

    @Override
    public Integer visit(BinaryExpr e) {
        if (booleanNodesOnly) {
            switch (e.op) {
                case PLUS:
                case MINUS:
                case MULTIPLY:
                case DIVIDE:
                case INT_DIVIDE:
                case MODULUS:
                    return 0;
                case EQUAL:
                case NOTEQUAL:
                case GREATER:
                case LESS:
                case GREATEREQUAL:
                case LESSEQUAL:
                case OR:
                case AND:
                case XOR:
                case IMPLIES:
                    return e.left.accept(this) + e.right.accept(this) + 1;
                case ARROW:
                    return e.right.accept(this);
                default:
                    assert false;
                    return 0;
            }
        } else
            return e.left.accept(this) + e.right.accept(this) + 1;
    }

    @Override
    public Integer visit(BoolExpr e) {
        return 1;
    }

    @Override
    public Integer visit(CastExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(CondactExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(FunctionCallExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(IdExpr e) {
        if (booleanNodesOnly) {// count only the e if it is actually a boolean in case of booleanNodesOnly is on.
            VarDecl varDecl = DiscoveryUtil.findInList(varDecls, e);
            if ((varDecl != null) && (varDecl.type == NamedType.BOOL))
                return 1;
            else
                return 0;
        } else
            return 1;
    }

    @Override
    public Integer visit(IfThenElseExpr e) {
        assert false; //we do not count if then else expressions yet
        return e.cond.accept(this) + e.thenExpr.accept(this) + e.elseExpr.accept(this);
    }

    @Override
    public Integer visit(IntExpr e) {
        if (booleanNodesOnly)
            return 0;
        else
            return 1;
    }

    @Override
    public Integer visit(NodeCallExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RepairExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RealExpr e) {
        if (booleanNodesOnly)
            return 0;
        else
            return 1;
    }

    @Override
    public Integer visit(RecordAccessExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RecordExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(RecordUpdateExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(TupleExpr e) {
        assert false;
        return null;
    }

    @Override
    public Integer visit(UnaryExpr e) {
        if (booleanNodesOnly) {
            switch (e.op) {
                case NEGATIVE:
                    return 0;
                case NOT:
                    return 1;
                case PRE:
                    assert false; //undefined for now. Since we do not expect our repair to include PRE expressions.
                    break;
            }
        }
        return e.expr.accept(this);
    }
}
