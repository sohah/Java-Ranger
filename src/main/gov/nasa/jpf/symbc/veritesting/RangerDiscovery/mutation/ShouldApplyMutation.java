package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics.ExprSizeVisitor;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import jkind.lustre.*;

import java.util.ArrayList;
import java.util.List;

public class ShouldApplyMutation {
    public ShouldApplyMutation(int prevMutationIndex, int prevRepairMutationIndex, SpecInOutManager tInOutManager, List<VarDecl> inputs, List<VarDecl> outputs) {
        this.prevMutationIndex = prevMutationIndex;
        this.prevRepairMutationIndex = prevRepairMutationIndex;
        this.mutationIndex = -1;
        this.repairMutationIndex = -1;
        this.tInOutManager = tInOutManager;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public boolean shouldApplyMutation() {
        return incAndGetMutationIndex() == prevMutationIndex + 1;
    }

    public boolean shouldApplyRepairMutation() {
        return incAndGetRepairMutationIndex() == prevRepairMutationIndex + 1;
    }

    public List<GenericRepairNode> repairNodes = new ArrayList<>();
    public int repairDepth;
    boolean justDidMutation = false;
    public boolean isPerfect = false;
    public boolean isSmallestWrapper = false;
    private int mutationIndex;
    private int repairMutationIndex;
    private final int prevMutationIndex, prevRepairMutationIndex;
    final SpecInOutManager tInOutManager;
    final List<VarDecl> inputs;
    final List<VarDecl> outputs;

    /**
     * This method is used to wrap the repair expression, then we later do a mutation of its internal/original expression.
     * However, to support multiple mutations, at that point there is a case when we want to wrap an expression that already have
     * a repair expression somewhere in the AST. In this case we want to keep the upper wrapping that we are about to create now.
     * Therefore, we use the NestRepairsVisitor to get rid of any repair sub-expressions and propagate the mutation to inner parts
     * of the expression.
     * @param e
     * @return
     */
    Expr wrapRepairExpr(Expr e) {
        NestedRepairsVisitor nestedRepairsVisitor = new NestedRepairsVisitor();
        Expr exprToWrap = e.accept(nestedRepairsVisitor);

        if (this.shouldApplyRepairMutation()) {
            IdExprVisitor idExprVisitor = new IdExprVisitor(exprToWrap, tInOutManager, inputs, outputs);
            exprToWrap.accept(idExprVisitor);
            List<VarDecl> varDecls = idExprVisitor.getVarDeclList();
            int exprSize = exprToWrap.accept(new ExprSizeVisitor(varDecls, true));
            GenericRepairNode genericRepairNode = new GenericRepairNode(varDecls, exprSize);
            repairNodes.add(genericRepairNode);
            repairDepth = genericRepairNode.repairDepth;
            NodeCallExpr callExpr = genericRepairNode.callExpr;
            RepairExpr repairExpr = new RepairExpr(exprToWrap, callExpr);
            return repairExpr;
        } else return exprToWrap;
    }

    boolean didMutation() {
        return mutationIndex > prevMutationIndex;
    }

    private int incAndGetRepairMutationIndex() {
        return ++repairMutationIndex;
    }

    public boolean addedRepairWrapper() {
        return repairMutationIndex > prevRepairMutationIndex;
    }

    private int incAndGetMutationIndex() {
        return ++mutationIndex;
    }
};