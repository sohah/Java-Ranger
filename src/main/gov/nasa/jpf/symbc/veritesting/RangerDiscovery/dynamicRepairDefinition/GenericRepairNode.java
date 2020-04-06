package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import jkind.lustre.Expr;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RepairNode;
import jkind.lustre.VarDecl;

import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.varDeclToIdExpr;

public class GenericRepairNode {

    final String name = Config.genericRepairNodeName;
    final List<VarDecl> actualParamVarDecls;
    public final RepairNode nodeDefinition;
    public final NodeCallExpr callExpr;

    /**
     * Takes the number of declared variables as well as the original expression size.
     * The depth of the dynamic node created is relevant/extracted from the size of the
     * original expression that needs repair.
     * @param actualParamVarDecls
     * @param exprSize
     */
    public GenericRepairNode(List<VarDecl> actualParamVarDecls, int exprSize) {
        this.actualParamVarDecls = actualParamVarDecls;
        callExpr = generateCallExpr();
        nodeDefinition = generateRepairDef(exprSize);
    }


    private NodeCallExpr generateCallExpr() {
        return new NodeCallExpr(name, (List<Expr>) (List<?>) varDeclToIdExpr(actualParamVarDecls));
    }

    private RepairNode generateRepairDef(int exprSize) {
        return new DynamicRepairNode(name).create(actualParamVarDecls, exprSize);
    }
}
