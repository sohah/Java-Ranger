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
    public static boolean z3Solver;
    public static int repairNodeDepth = 1; //defines the depth of the repair node. A depth 0 means a single boolean
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
    public static boolean useInitialSpecValues = true;
    public static String genericRepairNodeName = "repairNode";
    public static RepairScopeType repairScope = RepairScopeType.ENCLOSED_TERMS; //the default configuration.

    //this boolean toggles between equation based repair and whole spec repair.
    public static boolean specLevelRepair;// = false;

    public static String spec;// = "even";

    public static String currFaultySpec;
    public static String[] faultySpecs;

    public static int faultySpecIndex = 0;

    public static boolean defaultBoolValue = false;
    public static int initialIntValue = 0;

    public static String methodReturnName = "result";

    public static Program auxilaryRepairProgram;

    public static String repairLustreFileName = "RepairLibrary";

    public static int costLimit = 10; // value entered by hand for now

    public static boolean printMutantDir = false;


    public static int faultyEquationNumber = 1;

    public static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.RepairMode repairMode;
    public static boolean repairInitialValues;

    //this contains specific equations we would like to repair, instead of repairing the whole thing. This is now used for testing only.
    public static Integer[] equationNumToRepair = {1};
    public static boolean allEqRepair = true;

    static String mutationDir = "../src/DiscoveryExamples/mutants";

    public static boolean canSetup() throws IOException {

        DiscoverContract.contract = new Contract();
        tFileName = folderName + currFaultySpec;

        Program origSpec = LustreParseUtil.program(new String(Files.readAllBytes(Paths.get(tFileName)), "UTF-8"));

        ArrayList<MutationResult> mutationResults = createSpecMutants(origSpec, mutationDir, DiscoverContract.contract.tInOutManager);
        faultySpecs = processMutants(mutationResults, origSpec, currFaultySpec);


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
