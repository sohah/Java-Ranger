package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.ExecutionMode;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.OperationMode;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.dynamicRepairDefinition.GenericRepairNode;
import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;
import jkind.lustre.*;
import jkind.lustre.parsing.LustreParseUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.*;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.writeToFile;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.createSpecMutants;

public class ProcessMutants {

    //a hashmap that contains a mapping from a type of a mutation, and its location to all possible repairs.
    public static HashMap<String, Queue<MutationResult>> mutationSpecPool = new HashMap<>();

    public static Pair<List<String>, Integer[]> runMultipleMutations(int numOfMutations, String folderName, String initialSpec, OperationMode operationMode, String mutationDir) throws IOException {
        assert numOfMutations >= 1 : "number of multiple mutations must be set.";
        List<String> mutatedSpecs = new ArrayList<>();
        List<Integer> repairDepths = new ArrayList<>();
        Queue<String> queueOfSpecs = new LinkedList<>(); // holds the specs we want to mutate, the mutation of this spec must be >=0 && < numOfMutations.
        queueOfSpecs.offer(initialSpec);

        int numOfFinishedMutations = 0;
        while (!queueOfSpecs.isEmpty()) {
            numOfFinishedMutations++;
            int mutationLevelSize = queueOfSpecs.size(); // this holds the size of specs we need to mutate, where every level indicates the number of mutations that has been done so far.

            for (int i = 0; i < mutationLevelSize; i++) {
                String currFaultySpec = queueOfSpecs.poll();
                String tFileName = folderName + currFaultySpec;
                Program origSpec = LustreParseUtil.program(new String(Files.readAllBytes(Paths.get(tFileName)), "UTF-8"));
                ArrayList<MutationResult> mutationResults = createSpecMutants(origSpec, mutationDir, numOfFinishedMutations);
                Pair<List<String>, List<Integer>> triple = processMutants(numOfFinishedMutations, numOfMutations, mutationResults, origSpec, currFaultySpec, operationMode);
//                new File(tFileName).delete(); // delete the file after processing it and generating the appropriate mutants.
                if (numOfFinishedMutations < numOfMutations) { // if we have not finished all mutations yet, then put it back for further processing
                    queueOfSpecs.addAll(triple.getFirst());
                    mutationSpecPool = new HashMap<>();
                } else {
                    mutatedSpecs.addAll(triple.getFirst());
                    repairDepths.addAll(triple.getSecond());
                }
            }
        }
        assert (mutatedSpecs.size() == repairDepths.size()) : "unexpected non match for sizes for mutations. Failing";
        System.out.println("total number of mutants = " + mutatedSpecs.size());
        return new Pair<List<String>, Integer[]>(mutatedSpecs, repairDepths.toArray(new Integer[0]));
    }


    public static Pair<List<String>, List<Integer>> processMutants(int numOfFinishedMutations, int numOfMutations, ArrayList<MutationResult> mutationResults, Program inputExtendedPgm, String currFaultySpec, OperationMode operationMode) {
        List<String> mutatedSpecs = new ArrayList<>();

        //a hashmap that contains a mapping from a type of a mutation, and its location to all possible repairs.

        List<Integer> repairDepths = new ArrayList<>();
        HashSet<Integer> generatedMutantsHash = new HashSet();
        assert mutationResults.size() > 0 || Config.numOfMutations > 1; //there must be mutants to be processed to call this method.
        /*String[] mutatedSpecs = new String[mutationResults.size()];
        int[] repairDepths = new int[mutationResults.size()];
        boolean[] perfectMutantFlags = new boolean[mutationResults.size()];
*/

        int mutantIndex = 0;
        while (mutantIndex < mutationResults.size()) {
            MutationResult mutationResult = mutationResults.get(mutantIndex);
            Program newProgram = updateMainPropertyExpr(inputExtendedPgm, mutationResult);
            int newPgmHash = newProgram.toString().hashCode();
            /* we are only writing mutations that are unique, and also has gone through the number of mutations that we
            desire except in the none-evaluation phase, that is if we are in the evaluation phase we want to be processing oly
                    the mutants who has the same number of mutations configured for the run.*/
            if (!generatedMutantsHash.contains(newPgmHash)) {
               /* if (!Config.evaluationMode ||
                        mutationResult.mutationsOccured == Config.numOfMutations) { // a new unique mutant*/
                assert (!((operationMode == OperationMode.PERFECT_ONLY && !mutationResult.isPerfect) || (operationMode == OperationMode.SMALLEST_ONLY && !mutationResult.isSmallestWrapper))) : "wrong setup for configuration";
                generatedMutantsHash.add(newPgmHash);

                String specFileName = currFaultySpec + mutationResult.mutationIdentifier;
                if (!Files.exists(Paths.get(specFileName)))
                    writeToFile(specFileName, newProgram.toString(), false, true);
                mutationResult.fileName = specFileName;
                mutatedSpecs.add(specFileName);
                repairDepths.add(mutationResult.repairDepth);

                if (numOfFinishedMutations >= numOfMutations)
                    if (executionMode == ExecutionMode.MULTI_THREAD_MODE)
                        putInMutationSpecHashMap(specFileName, newProgram, mutationResult, currFaultySpec, numOfMutations);
                if (IsPerfectRepairVisitor.execute(Config.origProp, mutationResult.mutatedExpr))
                    Config.perfectMutants.add(specFileName);
                else Config.nonPerfectMutants.add(specFileName);

            }
            ++mutantIndex;
        }

//        }
//        System.out.println("number of mutants generated after checksum are: " + mutatedSpecs.size());
        //assert perfectMutant != null; //TODO:enable that once we have the perfectMutant plugged in.
        return new Pair<List<String>, List<Integer>>(mutatedSpecs, repairDepths);
    }

    private static void putInMutationSpecHashMap(String specFileName, Program newProgram, MutationResult mutationResult, String currFaultySpec, int numOfMutations) {

        assert numOfMutations <= 2 && (currFaultySpec.contains("LOR") || currFaultySpec.contains("ROR") || !currFaultySpec.contains("-")) : "population of the hashmap is configured to handle only two mutations ROR and LOR for at most 2 mutations. Violation detected. Failing.";

        String filteredCurrSpecName = currFaultySpec.replaceAll("LOR-.", "LOR");
        filteredCurrSpecName = filteredCurrSpecName.replaceAll("ROR-.", "ROR");

        String mutantName = mutationResult.getUniqueMutationName(filteredCurrSpecName);
        Queue<MutationResult> mutationResQueue = mutationSpecPool.get(mutantName);

        //hack
        String oldFolderName = folderName;
        folderName += "/" + mutantName;

        if (mutationResQueue == null) {
            if (!evaluationMode)
                new File(folderName).mkdirs();
            PriorityQueue<MutationResult> queue = new PriorityQueue<>(new Comparator<MutationResult>() {
                @Override
                public int compare(MutationResult t1, MutationResult t2) {

                    int repairDepthSum1 = 0;
                    for (GenericRepairNode repairNode : t1.repairNodes)
                        repairDepthSum1 += repairNode.repairDepth;

                    int repairDepthSum2 = 0;
                    for (GenericRepairNode repairNode : t2.repairNodes)
                        repairDepthSum2 += repairNode.repairDepth;
                    return Integer.compare(repairDepthSum1, repairDepthSum2);
                }
            });
            queue.add(mutationResult);
            mutationSpecPool.put(mutationResult.getUniqueMutationName(filteredCurrSpecName), queue);
        } else mutationResQueue.add(mutationResult);

        //hack
        if (!evaluationMode) // can be printed in a folder formate in non-evaluation mode, for debugging purposes.
            writeToFile(specFileName, newProgram.toString(), false, true);
        folderName = oldFolderName;
    }


    /**
     * updates the Property of main, usually due to mutation.
     *
     * @param pgm
     * @return
     */
    public static Program updateMainPropertyExpr(Program pgm, MutationResult mutationResult) {
        List<Node> newNodes = new ArrayList<>();
        String mainNodeStr = pgm.main;
        for (Node node : pgm.nodes) {
            if (node.id.equals(mainNodeStr)) newNodes.add(updateEqExpr(node, mutationResult.mutatedExpr));
            else newNodes.add(node);
        }

        List<RepairNode> repairNodes = new ArrayList<>();
//        assert pgm.repairNodes.size() == 0; //no repair nodes should exist at that point
        assert mutationResult.repairNodes != null;// && mutationResult.repairNodes.size() == 1; // repair nodes definitions cannot be null and must exist
        repairNodes.add(mutationResult.repairNodes.get(0).nodeDefinition);
        repairNodes.addAll(pgm.repairNodes);
        return new Program(pgm.location, pgm.types, pgm.constants, pgm.functions, newNodes, repairNodes, pgm.main);
    }

    /**
     * updates the expression of the property equation to a new definition. Usually due to mutation.
     *
     * @return
     */
    public static Node updateEqExpr(Node node, Expr newExpr) {
        List<Equation> equations = node.equations;
        assert (equations.size() == 1); //assumes only a single equation exists that we want to update.
        Equation newEquation = new Equation(equations.get(0).lhs, newExpr);
        List<Equation> newEquations = new ArrayList<>();
        newEquations.add(newEquation);
        return new Node(node.id, node.inputs, node.outputs, node.locals, newEquations, node.properties, node.assertions, node.realizabilityInputs, node.contract, node.ivc);
    }


}