package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.Expr;

public class MutationResult {
    final Expr mutatedExpr;
    final int mutationIndex;

    public MutationResult(Expr mutatedExpr, int mutationIndex) {
        this.mutatedExpr = mutatedExpr;
        this.mutationIndex = mutationIndex;
    }
}
