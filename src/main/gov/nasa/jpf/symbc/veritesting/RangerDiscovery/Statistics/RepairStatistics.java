package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationType;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;


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

    CandidateStatistics candidateStatistics;


    public RepairStatistics(String fileName, String depth, MutationType mutationType) {
        String statisticFileName = Config.folderName + "statistics" + LocalDateTime.now() + ".txt";
        try {
            fw = new FileWriter(statisticFileName, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
            out.print(fileName + "     ");
            out.print("depth:" + depth + "     ");
            out.print("mutationType:" + mutationType + "     "); //number of iterations in the outer loop
            out.println("z3Enabled:" + Config.z3Solver);
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

    public void printSpecStatistics() {
        out.println("---------------------------SPEC STATS-------------------");
        out.print("totalExistsTime     ");
        out.print("totalForallTime     ");
        out.print("repairsFoundNum     ");
        out.print("totalTime     ");
        out.print("avgExistsTime     ");
        out.print("avgForallTime     ");
        out.println();
        out.print(candidateStatistics.totalExistsTime + "     ");
        out.print(candidateStatistics.totalForallTime + "     ");
        out.print(candidateStatistics.repairsFoundNum + "     ");
        out.print(candidateStatistics.totalTime + "     ");
        out.print(candidateStatistics.totalExistsTime / candidateStatistics.totalExistsNum + "     ");
        if (candidateStatistics.totalForallNum != 0)
            out.print(candidateStatistics.totalForallTime / candidateStatistics.totalForallNum + "     ");
        else
            out.print("N/A     ");
        out.println();
        out.close();
    }


}
