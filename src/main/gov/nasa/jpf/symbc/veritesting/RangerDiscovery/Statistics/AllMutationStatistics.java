package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.ExecutionMode;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;

import java.io.*;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class AllMutationStatistics {
    List<String> uniqueTighestPropsFound = new ArrayList<>();

    String uniquePropFileName = Config.folderName + Config.spec + "_unique_prop" + Config.prop + ".txt";
    String allPropFileName = Config.folderName + Config.spec + "_all_prop" + Config.prop + ".txt";
    public String statFileName = Config.folderName + Config.spec + "_prop" + Config.prop + "_stats.txt";

    static public String threadStatFileName = Config.folderName + Config.spec + "_prop" + Config.prop + "_stats.txt";

    public AllMutationStatistics() {
        String out = ("bench,     ") + ("prop,     ") + ("currFaultySpec,     ") + ("perfect,     ") + ("repairNodeDepth,     ") + ("terminationResult,     ") +
//                ("allCandidatesAttempted,     ") +
                ("repairsFoundNum,     ") + ("executionTime,     ") + ("totalQueriesTime,     ") +
                ("totalExistsTime,     ") + ("totalForallTime,     ") + ("totalExistsNum,     ") +
                ("totalForallNum,     ") + ("avgExistsTime,     ") + ("avgForallTime,     ") + ("tightestProp,     ");
        System.out.println("working directory" + FileSystems.getDefault().getPath(".").toAbsolutePath());
        System.out.println("Config.folderName=" + Config.folderName);
        File file = new File(statFileName);
        try {
            if (!file.exists()) {
                if (Config.executionMode == ExecutionMode.MULTI_THREAD_MODE)
                    file.createNewFile();
                else assert false : "file must have been created by the parent thread. Something went wrong. Failing.";
            } else {
                if (Config.executionMode == ExecutionMode.MULTI_THREAD_MODE) { // clear the file
                    Path pFile = Paths.get(statFileName);
                    Files.write(pFile, Arrays.asList(""));
                }
            }
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
                + (tightestProp + "\n");
/*
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
        }*/

       /* List<String> lines = Arrays.asList(out);

        Path file = Paths.get(statFileName);
        try {
            Files.write(file, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("problem writing to all statistics file");
            assert false;
        }*/
        try {
            File file = new File(statFileName);
            assert file.exists() : "file must have been created. Assumption Violated. Failing.";

            FileOutputStream fileStream = new FileOutputStream(file, true);
            write(fileStream, out.getBytes()); //writing nothing onto the file
        } catch (IOException e) {
            System.out.println("problem writing to all statistics file");
            assert false;
        }
    }

    public void doneAllMutants() {

     /*   Path file = Paths.get(uniquePropFileName);
        try {
            Files.write(file, uniqueTighestPropsFound, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("problem writing to unique tight props file");
            assert false;
        }*/
    }


    void write(FileOutputStream file, byte[] bytes) {
        try {
            boolean written = false;
            do {
                try {
                    // Lock it!
                    FileLock lock = file.getChannel().lock();
                    try {
                        // Write the bytes.
                        file.write(bytes);
                        written = true;
                    } finally {
                        // Release the lock.
                        lock.release();
                    }
                } catch (OverlappingFileLockException ofle) {
                    try {
                        // Wait a bit
                        Thread.sleep(0);
                    } catch (InterruptedException ex) {
                        throw new InterruptedIOException("Interrupted waiting for a file lock.");
                    }
                }
            } while (!written);
        } catch (IOException ex) {
            System.out.println("Failed to lock " + ex);
            assert false;
        }
    }
}
