package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Queries.MinimalRepair.MinimalRepairDriver;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationType;
import jkind.lustre.Node;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.milliSecondSimplification;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract.executionTime;


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
    String repairPropFileName;


    public RepairStatistics(String fileName, String depth, MutationType mutationType) {
        LocalDateTime time = LocalDateTime.now();
        String statisticFileName = Config.folderName + Config.currFaultySpec + "-stat" + time + ".txt";
        repairPropFileName = Config.folderName + Config.currFaultySpec + "-stat_prop" + time + ".txt";

        try {
            fw = new FileWriter(statisticFileName, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.print(fileName + "     ");
            out.print("depth:" + depth + "     ");
            out.print("mutationType:" + mutationType + "     "); //number of iterations in the outer loop
            out.println("randZ3Seed:" + Config.randZ3Seed);
            out.println();
            candidateStatistics = new CandidateStatistics();
        } catch (IOException e) {
            System.out.println("problem writing to statistics file");
            assert false;
        }
    }

    public void printCandStatistics(String loopCount, boolean minimal, int candidateNum, QueryType queryType,
                                    long queryTime) {
        candidateStatistics.printCandStatistics(loopCount, minimal, candidateNum, queryType, queryTime);
    }

    public void advanceTighterLoop(boolean repairFound) {
        candidateStatistics.advanceTightLoop(repairFound);
    }

    public void printSpecStatistics() throws IOException {
        executionTime = (System.currentTimeMillis() - executionTime) / milliSecondSimplification;

        out.println("---------------------------SPEC STATS-------------------");
        out.print("libraryDepth,     ");
        out.print("terminationResult,     ");
        out.print("repairsFoundNum,     ");
        out.print("executionTime,     ");
        out.print("total Queries Time,     ");
        out.print("totalExistsTime,     ");
        out.print("totalForallTime,     ");
        out.print("avgExistsTime,     ");
        out.print("avgForallTime,     ");
        out.println();
        out.print(Config.repairNodeDepth + ",     ");
        out.print(terminationResult.name() + ",     ");
        out.print(candidateStatistics.repairsFoundNum + ",     ");
        out.print(executionTime + ",     ");
        out.print(candidateStatistics.totalTime + ",     ");
        out.print(candidateStatistics.totalExistsTime + ",     ");
        out.print(candidateStatistics.totalForallTime + ",     ");

        if (candidateStatistics.totalExistsNum != 0)
            out.print(candidateStatistics.totalExistsTime / candidateStatistics.totalExistsNum + ",     ");
        else
            out.print("N/A,     ");
        if (candidateStatistics.totalForallNum != 0)
            out.print(candidateStatistics.totalForallTime / candidateStatistics.totalForallNum + ",     ");
        else
            out.print("N/A,     ");
        out.println();
        out.close();

        printRepairProp();
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
