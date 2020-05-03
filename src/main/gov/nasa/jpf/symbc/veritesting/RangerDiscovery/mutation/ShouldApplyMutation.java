package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics.ExprSizeVisitor;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import jkind.lustre.Expr;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RepairExpr;
import jkind.lustre.VarDecl;

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
    Expr wrapRepairExpr(Expr e) {
        if (this.shouldApplyRepairMutation()) {
            IdExprVisitor idExprVisitor = new IdExprVisitor(e, tInOutManager, inputs, outputs);
            e.accept(idExprVisitor);
            List<VarDecl> varDecls = idExprVisitor.getVarDeclList();
            int exprSize = e.accept(new ExprSizeVisitor(varDecls, true));
            GenericRepairNode genericRepairNode = new GenericRepairNode(varDecls, exprSize);
            repairNodes.add(genericRepairNode);
            repairDepth = genericRepairNode.repairDepth;
            NodeCallExpr callExpr = genericRepairNode.callExpr;
            RepairExpr repairExpr = new RepairExpr(e, callExpr);
            return repairExpr;
        } else return e;
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