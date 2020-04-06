package gov.nasa.jpf.symbc.veritesting.RangerDiscovery;

import gov.nasa.jpf.symbc.VeritestingListener;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationResult;
import jkind.lustre.Ast;
import jkind.lustre.BoolExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.Program;
import jkind.lustre.parsing.LustreParseUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.createSpecMutants;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.ProcessMutants.processMutants;

public class Config {
    public static String counterExPropertyName = "fail";
    public static String folderName = "../src/DiscoveryExamples/";
    public static String symVarName;
    public static String origFaultySpec;
    // atom synthesized
    static String tFileName;
    static String holeRepairFileName = folderName + "holeRepair";
    public static String TNODE = "T_node"; // also refers to the R_prime in the refinement loop.
    public static String RNODE = "Ranger_node";
    public static String WRAPPERNODE = "Ranger_wrapper";
    public static String CHECKSPECNODE = "Check_spec";
    public static String H_discovery = "H_discovery";
    public static String FIXED_T = "Fixed_T";
    public static String CAND_T_PRIME = "Candidate_T_Prime";
    public static String specPropertyName = "ok";
    public static String wrapperOutpuName = "out";
    public static boolean limitedSteps = true; //this controls the number of steps we allow for there exists query
    // of finding a different R.

    public static String tnodeSpecPropertyName;
    public static String candidateSpecPropertyName = "discovery_out";

    public static Ast defaultHoleValBool = new BoolExpr(false);
    public static Ast defaultHoleValInt = new IntExpr(1);
    public static boolean defaultBoolValue = false;
    public static int initialIntValue = 0;

    public static boolean useInitialSpecValues = true;
    public static String genericRepairNodeName = "repairNode";
    //this boolean toggles between equation based repair and whole spec repair.
    public static boolean specLevelRepair;// = false;
    //this contains specific equations we would like to repair, instead of repairing the whole thing. This is now used for testing only.
    public static Integer[] equationNumToRepair = {1};
    public static boolean allEqRepair = true;


    /***** configurations needs consideration for each run in .jpf file *********/
    public static RepairScopeType repairScope = RepairScopeType.ENCLOSED_TERMS; //the default configuration.
    public static boolean randZ3Seed = false;
    public static String spec;
    public static String currFaultySpec;
    public static boolean printMutantDir = false;
    public static boolean mutationEnabled = true;
    public static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.RepairMode repairMode;
    public static String[] faultySpecs;
    public static boolean z3Solver = true;
    public static int repairNodeDepth = 1; //defines the depth of the repair node. A depth 0 means a single boolean
    public static boolean depthFixed = false;
    public static boolean rangeValueAnalysis = true;


    public static int faultySpecIndex = 0;

    public static boolean repairInitialValues;

    private static boolean firstTime = true;

    static String mutationDir = "../src/DiscoveryExamples/mutants";

    public static boolean canSetup() throws IOException {

        DiscoverContract.contract = new Contract();
        tFileName = folderName + currFaultySpec;

        if (firstTime) {
            firstTime = false;
            Program origSpec = LustreParseUtil.program(new String(Files.readAllBytes(Paths.get(tFileName)), "UTF-8"));
            if (mutationEnabled) {
                ArrayList<MutationResult> mutationResults = createSpecMutants(origSpec, mutationDir, DiscoverContract.contract.tInOutManager);
                faultySpecs = processMutants(mutationResults, origSpec, currFaultySpec);
            } else {
                if (origSpec.repairNodes.size() == 0) {
                    System.out.println("repair nodes can not be zero if we are not using mutation. The user needs to specify a repair node and repair expr");
                    assert false;
                }
                faultySpecs = new String[]{currFaultySpec};
            }
        }
        if ((faultySpecIndex) >= faultySpecs.length)
            return false;

        currFaultySpec = faultySpecs[faultySpecIndex];
        ++faultySpecIndex;

        tFileName = folderName + currFaultySpec;
        tnodeSpecPropertyName = "T_node~0.p1";

        //make a new directory for the output of that spec
        new File(folderName + "/output/" + Config.currFaultySpec).mkdirs();

        VeritestingListener.simplify = false; //forcing simplification to be false for now
        return true;

    }
}
