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

    public GenericRepairNode(List<VarDecl> actualParamVarDecls) {
        this.actualParamVarDecls = actualParamVarDecls;
        callExpr = generateCallExpr();
        nodeDefinition = null; //generateRepairDef();
    }


    private NodeCallExpr generateCallExpr() {
        return new NodeCallExpr(name, (List<Expr>) (List<?>) varDeclToIdExpr(actualParamVarDecls));
    }

    private RepairNode generateRepairDef() {
        return new DynamicRepairNode(name).create(actualParamVarDecls);
    }
}
