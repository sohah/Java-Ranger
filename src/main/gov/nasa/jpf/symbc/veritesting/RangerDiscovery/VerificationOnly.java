package gov.nasa.jpf.symbc.veritesting.RangerDiscovery;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Queries.ARepair.CounterExampleQuery;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics.QueryType;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics.RepairStatistics;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.verification.CheckAllInOutIdVisitor;
import jkind.JKindException;
import jkind.api.results.JKindResult;
import jkind.api.results.Status;
import jkind.lustre.Equation;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.parsing.LustreParseUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.currFaultySpec;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.tnodeSpecPropertyName;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.appendToFile;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.callJkind;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.writeToFile;

public class VerificationOnly {

    public static void computeVerification(ArrayList<String> invariants,
                                           RepairStatistics repairStatistics, CounterExampleQuery counterExampleQuery,
                                           Program originalProgram) throws IOException {
        int ignoredInvariants = 0;
        int validINv = 0;
        for (int i = 0; i < invariants.size(); i++) {
            String inv = invariants.get(i);

//            Program invCounterExample = replaceProp(counterExampleQuery.getCounterExamplePgm(), inv);
            String invCounterExampleStr = counterExampleQuery.toString().replaceAll("(?m)^  p1 =.*", "  p1 = " + inv + ";");
            if (onlyDefinedInputOutput(LustreParseUtil.program(invCounterExampleStr))) {
                String fileName = currFaultySpec + "_daikon_" + i + ".lus";
                writeToFile(fileName, invCounterExampleStr, false, false);
                long singleQueryTime = System.currentTimeMillis();

                try {
                    JKindResult counterExResult = callJkind(fileName, true, -1, false, false);
                    singleQueryTime = (System.currentTimeMillis() - singleQueryTime);
                    System.out.println("TIME = " + DiscoveryUtil.convertTimeToSecond(singleQueryTime));

                    repairStatistics.printCandStatistics("verification", false, -1, QueryType.FORALL, singleQueryTime);

                    if (counterExResult.getPropertyResult(tnodeSpecPropertyName).getStatus() == Status.VALID) {
                        assert originalProgram.nodes != null && originalProgram.nodes.size() == 1 && originalProgram.nodes.get(0).equations.size() == 1 : "unexpected form for the verification property. Failing.";
                        appendToFile("../daikonverified/verifiedProps_" + Config.spec, inv);
                        ++validINv;
                    }
                } catch (JKindException e) {
                    System.out.println("invairant: " + inv + " has raised JKIND exception. ignoring invariant.");
                    ++ignoredInvariants;
                }
            } else
                assert Config.spec.equals("infusion") : "only infusion has the form where not all inputs and outputs are summarized, but the running spec is not for infusion. Assumption violated. Failing.";
        }

        String invariantsStats = "total Invariants,ignored invariants,considered invariants,valid invariants";
        invariantsStats += "\n" + invariants.size() + "," + ignoredInvariants + "," + (invariants.size() - ignoredInvariants) + "," + validINv;

        appendToFile("../daikonverified/verifiedProps_" + Config.spec, (invariantsStats));
    }

    /**
     * this is useful to filter out input and outputs that are not part of the summarization and thus we have removed their occurances from the SpecInputOutputManager
     *
     * @param program
     * @return
     */
    private static boolean onlyDefinedInputOutput(Program program) {
        assert program.nodes.size() == 4 && program.nodes.get(0).equations.size() == 1 : "the invariant program needs to have 4 nodes where the first is the TNode which should contain a single equation that defines the property. Assumption violated. Failing.";

        Equation e = program.nodes.get(0).equations.get(0);
        CheckAllInOutIdVisitor idExprVisitor = new CheckAllInOutIdVisitor();
        e.expr.accept(idExprVisitor);
        return idExprVisitor.isAllInOut;
    }

    private static Program replaceProp(Program cexPgm, String inv) {


        Node tnode = getNewTnode(cexPgm.nodes, inv);
        ArrayList<Node> newNodes = new ArrayList<Node>(Arrays.asList(tnode));

        for (Node n : cexPgm.nodes) {
            if (!n.id.equals("T_node"))
                newNodes.add(n);
        }

        return new Program(cexPgm.location, cexPgm.types, cexPgm.constants, cexPgm.functions,
                newNodes, cexPgm.repairNodes, cexPgm.main);

    }

    private static Node getNewTnode(List<Node> nodes, String inv) {

        Node tNode = DiscoveryUtil.findNode(nodes, "T_node");
        return null;


    }
}
