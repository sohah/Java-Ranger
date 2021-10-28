package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class AllMutationStatistics {
    List<String> uniqueTighestPropsFound = new ArrayList<>();

    String uniquePropFileName = Config.folderName + Config.spec + "_unique_prop" + Config.prop + ".txt";
    String allPropFileName = Config.folderName + Config.spec + "_all_prop" + Config.prop + ".txt";
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
            System.out.println("problem writing to all statistics file = " + file.toAbsolutePath());
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

        if (tightestProp != null) {
            uniqueTighestPropsFound.add(tightestProp);
            Path file = Paths.get(allPropFileName);
            try {
                if (Files.exists(file)){
                    tightestProp += "\n";
                    Files.write(file, tightestProp.getBytes(), StandardOpenOption.APPEND);}
                else
                    Files.write(file, tightestProp.getBytes());
            } catch (IOException e) {
                System.out.println("problem writing to all tight props file");
                assert false;
            }
        }

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

        Path file = Paths.get(uniquePropFileName);
        try {
            Files.write(file, uniqueTighestPropsFound, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("problem writing to unique tight props file");
            assert false;
        }
    }
}
