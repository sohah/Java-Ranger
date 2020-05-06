package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationType;
import jkind.lustre.Equation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class AllMutationStatistics {
    Set<String> tighestPropsFound = new HashSet<String>();

    String propFileName = Config.folderName + Config.spec + "_prop" + Config.prop + ".txt";
    String statFileName = Config.folderName + Config.spec + "_prop" + Config.prop + "_stats.txt";


    public AllMutationStatistics() {
        String out = ("bench,     ") + ("prop,     ") + ("currFaultySpec,     ") + ("perfect,     ") + ("repairNodeDepth,     ") + ("terminationResult,     ") +
//                ("allCandidatesAttempted,     ") +
                ("repairsFoundNum,     ") + ("executionTime,     ") + ("totalQueriesTime,     ") +
                ("totalExistsTime,     ") + ("totalForallTime,     ") + ("totalExistsNum,     ") +
                ("totalForallNum,     ") + ("avgExistsTime,     ") + ("avgForallTime,     ") + ("tightestProp,     ");

//        List<String> lines = Arrays.asList("randZ3Seed:" + Config.randZ3Seed, "", out);

        List<String> lines = new ArrayList<>();
        Path file = Paths.get(statFileName);
        try {
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("problem writing to all statistics file");
            assert false;
        }


    }

    public void writeFinalResult(String spec, int prop, String currFaultySpec, boolean isPerfectMutant, int repairNodeDepth, String terminationResult, int candidatesInAllLoops, int repairsFoundNum,
                                 long executionTime, long totalQueryTime, long totalExistsTime, long totalForallTime, long totalExistsNum,
                                 long totalForallNum, long exitsAvg, long forallAvg, String tightestProp) {

        String out = (spec + ",     ") +
                ("p" + prop + ",     ") +
                (currFaultySpec + ",     ") +
                (isPerfectMutant + ",    ") +
                (repairNodeDepth + ",    ")
                + (terminationResult + ",     ")
//                + (candidatesInAllLoops + ",     ")
                + (repairsFoundNum + ",     ")
                + (DiscoveryUtil.convertTimeToSecond(executionTime) + ",     ")
                + (DiscoveryUtil.convertTimeToSecond(totalQueryTime) + ",     ")
                + (DiscoveryUtil.convertTimeToSecond(totalExistsTime) + ",     ")
                + (DiscoveryUtil.convertTimeToSecond(totalForallTime) + ",     ")
                + (totalExistsNum + ",     ")
                + (totalForallNum + ",     ")
                + (DiscoveryUtil.convertTimeToSecond(exitsAvg) + ",     ")
                + (DiscoveryUtil.convertTimeToSecond(forallAvg) + ",     ")
                + (tightestProp + ",     ");

        if (tightestProp != null)
            tighestPropsFound.add(tightestProp);

        List<String> lines = Arrays.asList(out);

        Path file = Paths.get(statFileName);
        try {
            Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("problem writing to all statistics file");
            assert false;
        }

    }

    public void doneAllMutants() {

        Path file = Paths.get(propFileName);
        try {
            Files.write(file, tighestPropsFound, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("problem writing to all tight props file");
            assert false;
        }
    }
}
