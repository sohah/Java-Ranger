package gov.nasa.jpf.symbc.veritesting.RangerDiscovery;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationResult;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FireThreads {
    public static void findRepairs(int mutationNum, Queue<MutationResult> repairPossibilities) {

        ArrayList<MutationResult> repairPossibilitiesArr = new ArrayList<>(repairPossibilities);
        int coreCount = Runtime.getRuntime().availableProcessors();
        ExecutorService service = Executors.newFixedThreadPool(coreCount-1);

        ArrayList<Future> futures = new ArrayList<>();

        //mutation number is the
        for (int i = 0; i < repairPossibilitiesArr.size(); i++) {  //repairPossibilitiesArr.size()
            String threadJpfFile = Config.folderName + "threadJpf_" + mutationNum + "_" + i + ".jpf";
            futures.add(service.submit(new RepairTask(repairPossibilitiesArr.get(i), threadJpfFile, mutationNum, i)));
        }

        try {
            getAllFutures(futures); //block main thread until all has threads has finished.
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

