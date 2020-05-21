package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract;
import gov.nasa.jpf.symbc.veritesting.ast.def.*;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.AstMapVisitor;
import gov.nasa.jpf.symbc.veritesting.ast.visitors.ExprVisitor;
import jkind.lustre.NamedType;
import za.ac.sun.cs.green.expr.Expression;

import java.util.ArrayList;
import java.util.List;

/**
 * should only be invoked for inputs
 */
public class RangeCondVisitor extends AstMapVisitor {

    public static String interestedVarName;

    //this collects possible concrete "equal" values checked in equality in condition.
    public static List<Integer> concreteEqualValues = new ArrayList<>();

    public RangeCondVisitor(ExprVisitor<Expression> exprVisitor, String varName) {
        super(exprVisitor);
        //it must be an input variable that we want to see if its values are being checked, it must always be of type int
        assert DiscoverContract.contract.rInOutManager.isFreeInVar(varName, NamedType.INT);
        interestedVarName = varName;
    }

    @Override
    public Stmt visit(AssignmentStmt a) {
        if (a.rhs instanceof GammaVarExpr)
            eva.accept(a.rhs);

        return a;
    }

    @Override
    public Stmt visit(CompositionStmt a) {
        a.s1.accept(this);
        a.s2.accept(this);
        return a;

    }

    @Override
    public Stmt visit(IfThenElseStmt a) {
        assert false;
        return null;
    }

    @Override
    public Stmt visit(SkipStmt a) {
        return a;
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
