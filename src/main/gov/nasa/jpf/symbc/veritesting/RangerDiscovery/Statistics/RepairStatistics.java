package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Queries.MinimalRepair.MinimalRepairDriver;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationType;
import jkind.lustre.Equation;
import jkind.lustre.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.milliSecondSimplification;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract.executionTime;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract.loopCount;


/**
 * This class collects statistics among different repairs and different loops. There is a specific sequence of method
 * calls expected to occur, in particular a statistic object is created after which with every loop, a
 * printCandStatistics should be called then with the advancement of the loop, advanceCandLoop should be called which
 * will result in printing accumulative statics values. Finally after finishing up the repair of the spec a call
 * should be done to printSpecStatistics which should print out accumulative statistics over all the loops and
 * candidates.
 */
public class RepairStatistics {

    public static FileWriter fw;
    public static BufferedWriter bw;
    public static PrintWriter out;
    public TerminationResult terminationResult;
    CandidateStatistics candidateStatistics;
    int candidatesInAllLoops = 0; //accumulative number of all candidates attempted in both the inner and the outer loop.
    String repairPropFileName;


    public RepairStatistics(String fileName, MutationType mutationType) {
        LocalDateTime time = LocalDateTime.now();
        String statisticFileName = Config.folderName + Config.currFaultySpec + "-stat" + time + ".txt";
        repairPropFileName = Config.folderName + Config.currFaultySpec + "-stat_prop" + time + ".txt";

        try {
            fw = new FileWriter(statisticFileName, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.print(fileName + "     ");
            out.print("mutationType:" + mutationType + "     "); //number of iterations in the outer loop
            out.println("randZ3Seed:" + Config.randZ3Seed);
            out.println();
            candidateStatistics = new CandidateStatistics();
        } catch (IOException e) {
            System.out.println("problem writing to statistics file");
            assert false;
        }
    }

    public void printCandStatistics(String loopCount, boolean minimal, int candidateNum, QueryType queryType, long queryTime) {
        candidateStatistics.printCandStatistics(loopCount, minimal, candidateNum, queryType, queryTime);
        candidatesInAllLoops += candidateNum;
    }

    public void advanceTighterLoop(boolean repairFound) {
        candidateStatistics.advanceTightLoop(repairFound);
    }

    public void printSpecStatistics() throws IOException {
        executionTime = (System.currentTimeMillis() - executionTime);

        out.println("---------------------------SPEC STATS-------------------");
        out.print("Spec,     ");
        out.print("Perfect,     ");
        out.print("libraryDepth,     ");
        out.print("terminationResult,     ");
        out.print("allCandidatesAttempted,     ");
        out.print("repairsFoundNum,     ");
        out.print("executionTime,     ");
        out.print("total Queries Time,     ");
        out.print("totalExistsTime,     ");
        out.print("totalForallTime,     ");
        out.print("avgExistsTime,     ");
        out.print("avgForallTime,     ");
        out.println();
        out.print(Config.currFaultySpec + ",     ");
        out.print(Config.isCurrMutantPerfect() + ",     ");
        out.print(Config.repairNodeDepth + ",     ");
        out.print(terminationResult.name() + ",     ");
        out.print(candidatesInAllLoops + ",     ");
        out.print(candidateStatistics.repairsFoundNum + ",     ");
        out.print(DiscoveryUtil.convertTimeToSecond(executionTime) + ",     ");
        out.print(DiscoveryUtil.convertTimeToSecond(candidateStatistics.totalTime) + ",     ");
        out.print(DiscoveryUtil.convertTimeToSecond(candidateStatistics.totalExistsTime) + ",     ");
        out.print(DiscoveryUtil.convertTimeToSecond(candidateStatistics.totalForallTime) + ",     ");

        long exitsAvg = 0;
        long forallAvg = 0;
        if (candidateStatistics.totalExistsNum != 0) {
            exitsAvg = (candidateStatistics.totalExistsTime / candidateStatistics.totalExistsNum);
            out.print(DiscoveryUtil.convertTimeToSecond(exitsAvg) + ",     ");
        } else out.print("N/A,     ");
        if (candidateStatistics.totalForallNum != 0) {
            forallAvg = (candidateStatistics.totalForallTime / candidateStatistics.totalForallNum);
            out.print(DiscoveryUtil.convertTimeToSecond(forallAvg) + ",     ");
        } else out.print("N/A,     ");

        out.println();
        out.close();

        printRepairProp();

        String tightestProp;
        Node tighterNode;
        if (MinimalRepairDriver.repairs.size() > 0) {
            tighterNode = MinimalRepairDriver.repairs.get(MinimalRepairDriver.repairs.size() - 1);
            tightestProp = tighterNode.equations.get(tighterNode.equations.size() - 1).toString();
        } else tightestProp = null;

        Config.allMutationStatistics.writeFinalResult(Config.currFaultySpec, Config.isCurrMutantPerfect(), Config.repairNodeDepth, terminationResult.name(), candidatesInAllLoops, candidateStatistics.repairsFoundNum, executionTime, candidateStatistics.totalTime, candidateStatistics.totalExistsTime, candidateStatistics.totalForallTime, candidateStatistics.totalExistsNum, candidateStatistics.totalForallNum, exitsAvg, forallAvg, tightestProp);
    }

    private void printRepairProp() throws IOException {
        fw = new FileWriter(repairPropFileName, true);
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
        for (Node node : MinimalRepairDriver.repairs) {
            out.println(node.equations);
        }
        out.println();
        out.close();
    }
}
