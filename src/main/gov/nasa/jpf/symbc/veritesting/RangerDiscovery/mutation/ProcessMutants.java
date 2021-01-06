package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.OperationMode;
import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;
import jkind.lustre.*;
import jkind.lustre.parsing.LustreParseUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.writeToFile;
import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation.MutationUtils.createSpecMutants;

public class ProcessMutants {


    public static Pair<Pair<String[], Integer[]>, Boolean[]> runMultipleMutations(int numOfMutations, String folderName, String initialSpec, OperationMode operationMode, String mutationDir) throws IOException {
        assert numOfMutations >= 1 : "number of multiple mutations must be set.";
        List<String> mutatedSpecs = new ArrayList<>();
        List<Integer> repairDepths = new ArrayList<>();
        List<Boolean> perfectMutantFlags = new ArrayList<>();
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
                ArrayList<MutationResult> mutationResults = createSpecMutants(origSpec, mutationDir, DiscoverContract.contract.tInOutManager);
                Pair<Pair<List<String>, List<Integer>>, List<Boolean>> triple = processMutants(mutationResults, origSpec, currFaultySpec, operationMode);

                if (numOfFinishedMutations < numOfMutations) // if we have not finished all mutations yet, then put it back for further processing
                    queueOfSpecs.addAll(triple.getFirst().getFirst());
                else {
                    mutatedSpecs.addAll(triple.getFirst().getFirst());
                    repairDepths.addAll(triple.getFirst().getSecond());
                    perfectMutantFlags.addAll(triple.getSecond());
                }
            }
        }
        assert (mutatedSpecs.size() == repairDepths.size() && repairDepths.size() == perfectMutantFlags.size()) : "unexpected non match for sizes for mutations. Failing";
        return new Pair<Pair<String[], Integer[]>, Boolean[]>(new Pair<String[], Integer[]>(mutatedSpecs.toArray(new String[0]), repairDepths.toArray(new Integer[0])), perfectMutantFlags.toArray(new Boolean[0]));
    }


    public static Pair<Pair<List<String>, List<Integer>>, List<Boolean>> processMutants(ArrayList<MutationResult> mutationResults, Program inputExtendedPgm, String currFaultySpec, OperationMode operationMode) {
        List<String> mutatedSpecs = new ArrayList<>();
        List<Integer> repairDepths = new ArrayList<>();
        List<Boolean> perfectMutantFlags = new ArrayList<>();
        HashSet<Integer> generatedMutantsHash = new HashSet();
        assert mutationResults.size() > 0; //there must be mutants to be processed to call this method.
        /*String[] mutatedSpecs = new String[mutationResults.size()];
        int[] repairDepths = new int[mutationResults.size()];
        boolean[] perfectMutantFlags = new boolean[mutationResults.size()];
*/

        int mutationIndex = 0;
        while (mutationIndex < mutationResults.size()) {
            MutationResult mutationResult = mutationResults.get(mutationIndex);
            Program newProgram = updateMainPropertyExpr(inputExtendedPgm, mutationResult);
            int newPgmHash = newProgram.toString().hashCode();
            if (!generatedMutantsHash.contains(newPgmHash)) { // a new unique mutant
                assert (!((operationMode == OperationMode.PERFECT_ONLY && !mutationResult.isPerfect) || (operationMode == OperationMode.SMALLEST_ONLY && !mutationResult.isSmallestWrapper))) : "wrong setup for configuration";
                generatedMutantsHash.add(newPgmHash);
                String specFileName = currFaultySpec + mutationResult.mutationIdentifier;
                writeToFile(specFileName, newProgram.toString(), false, true);
                mutatedSpecs.add(specFileName);
                repairDepths.add(mutationResult.repairDepth);
                perfectMutantFlags.add(mutationResult.isPerfect);
            } else {
                System.out.println("find a repetative hashcode for:" + currFaultySpec + mutationResult.mutationIdentifier);
            }
            ++mutationIndex;
        }

        System.out.println("number of mutants generated after checksum are: " + mutatedSpecs.size());
        //assert perfectMutant != null; //TODO:enable that once we have the perfectMutant plugged in.
        return new Pair<Pair<List<String>, List<Integer>>, List<Boolean>>(new Pair<List<String>, List<Integer>>(mutatedSpecs, repairDepths), perfectMutantFlags);
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