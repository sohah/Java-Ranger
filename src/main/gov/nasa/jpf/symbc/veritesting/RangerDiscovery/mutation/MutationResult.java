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


    public MutationResult(Expr mutatedExpr, int mutationIndex, MutationType mutationType, List<GenericRepairNode> repairNodes) {
        this.mutatedExpr = mutatedExpr;
        this.mutationIndex = mutationIndex;
        this.mutationType = mutationType;
        this.mutationIdentifier = "-" + MutationUtils.mutationTypeToString(mutationType) + "-" + mutationIndex;
        this.repairNodes = repairNodes;
        if (mutationType == MutationType.REPAIR_EXPR_MUT)
            assert repairNodes.size() == 1; // currently we are not expecting multiple repair nodes with a single mutant.
        else
            assert repairNodes == null || repairNodes.size() == 0;
    }
}
