package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import jkind.lustre.Expr;

import java.util.List;

public class MutationResult {
    final Expr mutatedExpr;
    final int mutationIndex;
    final String mutationIdentifier;
    final MutationType mutationType;
    final List<GenericRepairNode> repairNodes;
    final int repairDepth;
    boolean isPerfect;
    boolean isSmallestWrapper;
    // is the number of mutations that that mutant had gone through.
    int mutationsOccured;
    public String fileName;


    public MutationResult(Expr mutatedExpr, int repairMutationIndex, int mutationIndex, MutationType mutationType, List<GenericRepairNode> repairNodes, int repairDepth, boolean isPerfect, boolean isSmallestWrapper, int mutationOccured) {
        this.mutatedExpr = mutatedExpr;
        this.mutationIndex = mutationIndex;
        this.mutationType = mutationType;
        this.mutationIdentifier = ("-" + MutationUtils.mutationTypeToString(mutationType) + "-" + repairMutationIndex +
                "-" + mutationIndex);
        this.repairNodes = repairNodes;
        assert repairNodes.size() == 1; // currently we are not expecting multiple repair nodes with a single mutant.
        this.repairDepth = repairDepth;
        this.isPerfect = isPerfect;
        this.isSmallestWrapper = isSmallestWrapper;
        this.mutationsOccured = mutationOccured;
    }

    public String getUniqueMutationName(String currFaultySpec) {
        return currFaultySpec + ("-" + MutationUtils.mutationTypeToString(mutationType) + "-" + mutationIndex);
    }
}
