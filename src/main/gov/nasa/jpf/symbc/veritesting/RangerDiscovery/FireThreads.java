package gov.nasa.jpf.symbc.veritesting.RangerDiscovery;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationResult;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FireThreads {
    public static FileWriter fw;
    public static BufferedWriter bw;
    public static PrintWriter out;
    public static boolean firstTime = true;

    public static void findRepairs(int mutationNum, Queue<MutationResult> repairPossibilities) {
        String repairFileName = Config.folderName + "MultiThread_stats_" + Config.spec;
        try {
            if (firstTime) {
                fw = new FileWriter(repairFileName, false);
                firstTime = false;
            } else
                fw = new FileWriter(repairFileName, true);
        } catch (IOException e) {
            System.out.println("problem creating MultiThread_stats for " + Config.spec);
            assert false;
        }

        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);

        ArrayList<MutationResult> repairPossibilitiesArr = new ArrayList<>(repairPossibilities);
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(coreCount - 1);

        ArrayList<Future> futures = new ArrayList<>();

        long mutantRepairTime = System.currentTimeMillis();

        //mutation number is the
        for (int i = 0; i < 1; i++) {  //repairPossibilitiesArr.size()
            String threadJpfFile = Config.folderName + "threadJpf_" + mutationNum + "_" + i + ".jpf";
            futures.add(service.submit(new RepairTask(repairPossibilitiesArr.get(i), threadJpfFile, mutationNum, i)));
        }

        try {
            getAllFutures(futures); //block main thread until all has threads has finished.
            mutantRepairTime = System.currentTimeMillis() - mutantRepairTime;
            out.print(Config.spec + ",  ");
            out.print("prop" + Config.prop + ",  ");
            out.print((DiscoveryUtil.convertTimeToSecond(mutantRepairTime)));
            out.println();
            out.close();
        } catch (InterruptedException e) {
            assert false : "interruptedException was raised, something went wrong. Failing.";
        } catch (ExecutionException e) {
            assert false : "interruptedException was raised, something went wrong. Failing.";
        }
        System.out.println("future done status = " + futures.get(0).isDone());
        service.shutdown();

        System.out.println("computing statistics for all ");
    }

    private static void getAllFutures(ArrayList<Future> futures) throws ExecutionException, InterruptedException {
        for (Future f : futures)
            f.get();
    }
}

