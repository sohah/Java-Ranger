package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.LustreExtension;

import jkind.lustre.Expr;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.RepairNode;
import jkind.lustre.VarDecl;

import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.varDeclToIdExpr;

public class GenericRepairNode {

    final String name;
    final List<VarDecl> actualParamVarDecls;
    public RepairNode nodeDefinition;
    public final NodeCallExpr callExpr;

    public GenericRepairNode(String name, List<VarDecl> actualParamVarDecls) {
        this.name = name;
        this.actualParamVarDecls = actualParamVarDecls;
        callExpr = generateCallExpr();
    }


    private NodeCallExpr generateCallExpr() {
        return new NodeCallExpr(name, (List<Expr>) (List<?>) varDeclToIdExpr(actualParamVarDecls));
    }

    private void generateRepairDef() {
        System.out.println("generation of repair node definition is currently undefined");
        assert false;
    }
}
