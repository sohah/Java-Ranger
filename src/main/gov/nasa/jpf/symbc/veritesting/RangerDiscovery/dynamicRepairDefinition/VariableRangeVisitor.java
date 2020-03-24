package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.AstMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import za.ac.sun.cs.green.expr.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to identify the range of values that could be assigned to variables. This is expected to run after linearization, thus is not implementing all types of statements.
 */
public class VariableRangeVisitor extends AstMapVisitor {

    public VariableRangeVisitor(ExprVisitor<Expression> exprVisitor) {
        super(exprVisitor);
    }

    @Override
    public Stmt visit(AssignmentStmt a) {
        return new AssignmentStmt(eva.accept(a.lhs), eva.accept(a.rhs));
    }

    @Override
    public Stmt visit(CompositionStmt a) {
        return new CompositionStmt(a.s1.accept(this), a.s2.accept(this));

    }

    @Override
    public Stmt visit(IfThenElseStmt a) {
        return new IfThenElseStmt(a.original, eva.accept(a.condition), a.thenStmt.accept(this),
                a.elseStmt.accept(this));
    }

    @Override
    public Stmt visit(SkipStmt a) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(SPFCaseStmt c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ArrayLoadInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ArrayStoreInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(SwitchInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ReturnInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(GetInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(PutInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(NewInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(InvokeInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ArrayLengthInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(ThrowInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(CheckCastInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(InstanceOfInstruction c) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(PhiInstruction c) {
        assert false;
        return null;
    }
}
