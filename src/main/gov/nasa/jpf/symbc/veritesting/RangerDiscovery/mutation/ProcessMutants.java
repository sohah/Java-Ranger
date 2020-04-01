package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.*;

import java.util.ArrayList;
import java.util.List;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Util.DiscoveryUtil.writeToFile;

public class ProcessMutants {

    public static String[] processMutants(ArrayList<MutationResult> mutationResults, Program inputExtendedPgm, String currFaultySpec) {
        assert mutationResults.size() > 0; //there must be mutants to be processed to call this method.
        String[] mutatedSpecs = new String[mutationResults.size()];

        for (int i = 0; i < mutationResults.size(); i++) {
            MutationResult mutationResult = mutationResults.get(i);
            Program newProgram = updateMainPropertyExpr(inputExtendedPgm, mutationResult);
            String specFileName = currFaultySpec + mutationResult.mutationIdentifier;
            writeToFile(specFileName, newProgram.toString(), false, true);
            mutatedSpecs[i] = specFileName;
        }
        return mutatedSpecs;
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
            if (node.id.equals(mainNodeStr))
                newNodes.add(updateEqExpr(node, mutationResult.mutatedExpr));
            else
                newNodes.add(node);
        }

        List<RepairNode> repairNodes = new ArrayList<>();
        assert pgm.repairNodes.size() == 0; //no repair nodes should exist at that point
        assert mutationResult.repairNodes != null && mutationResult.repairNodes.size() == 1; // repair nodes definitions cannot be null and must exist
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
