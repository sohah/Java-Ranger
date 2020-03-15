package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import jkind.lustre.BinaryOp;
import jkind.lustre.Expr;
import jkind.lustre.Node;
import jkind.lustre.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static jkind.lustre.BinaryOp.*;

public class MutationUtils {

    private static BinaryOp[] getMutationOps(final BinaryOp origOp, final BinaryOp[] allOps) {
        BinaryOp[] retOps = new BinaryOp[allOps.length - 1];
        for(int i = 0, j = 0; i < allOps.length; i++) {
            if (allOps[i] != origOp) {
                retOps[j++] = allOps[i];
            }
        }
        return retOps;
    }

    private static BinaryOp mutateLOR(final BinaryOp op, final MutateExpr mutateExpr) {
        BinaryOp[] allOps = new BinaryOp[]{AND, OR, IMPLIES, XOR};
        switch (op) {
            case AND:
            case OR:
            case XOR:
            case IMPLIES: return mutateExpr.applyBinaryOpMutation(op, getMutationOps(op, allOps));
            default:
                return op;
        }
    }

    private static BinaryOp mutateROR(final BinaryOp op, final MutateExpr mutateExpr) {
        BinaryOp[] allOps = new BinaryOp[]{LESS, LESSEQUAL, GREATER, GREATEREQUAL, EQUAL, NOTEQUAL};
        switch (op) {
            case LESS:
            case LESSEQUAL:
            case GREATER:
            case GREATEREQUAL:
            case EQUAL:
            case NOTEQUAL: return mutateExpr.applyBinaryOpMutation(op, getMutationOps(op, allOps));
            default:
                return op;
        }
    }

    static BinaryOp mutate(final MutationType mutationType, final BinaryOp op, final MutateExpr mutateExpr) {
        if (mutationType == MutationType.RELATIONAL_OP_REPLACEMENT) {
            return mutateROR(op, mutateExpr);
        } else if (mutationType == MutationType.LOGICAL_OP_REPLACEMENT) {
            return mutateLOR(op, mutateExpr);
        } else {
            return op;
        }
    }

    public static ArrayList<MutationResult> createSpecMutants(final Program originalProgram,
                                                               final String mutationDirectory) {
        Node mainNode = null;
        for (Node n: originalProgram.nodes) {
            if (n.id.equals("main")) {
                mainNode = n;
            }
        }
        if (mainNode == null || mainNode.equations.size() != 1) {
            System.out.println("Failed to find the main node or main node has more than one equation");
            return null;
        }
        int mutationIndex = -1;
        File directory = new File(mutationDirectory);
        if (! directory.exists()){
            if (!directory.mkdir()) {
                throw new UnsupportedOperationException("Failed to create the mutants directory");
            }
            // If you require it to make the entire directory path including parents,
            // use directory.mkdirs(); here instead.
        }
        writeUsingFileWriter(mainNode.equations.get(0).expr.toString(),
                mutationDirectory + "/origSpec");

        MutationType[] mutationTypes = new MutationType[]{
                MutationType.LOGICAL_OP_REPLACEMENT, MutationType.RELATIONAL_OP_REPLACEMENT};
        ArrayList<MutationResult> mutationResults = new ArrayList<>();
        for(MutationType mutationType: mutationTypes) {
            mutationResults.addAll(applyMutation(originalProgram, mutationType, mutationIndex, mutationDirectory));
        }
        return mutationResults;
    }

    private static ArrayList<MutationResult> applyMutation(final Program originalProgram, final MutationType mutationType,
                                                int mutationIndex, String mutationDirectory) {
        ArrayList<MutationResult> ret = new ArrayList<>();
        while (true) {
            MutateExpr mutateExpr = new MutateExpr(mutationType, mutationIndex);
            Expr mutatedExpr = originalProgram.nodes.get(0).equations.get(0).expr.accept(mutateExpr);
            if (!mutateExpr.didMutation()) {
                break;
            } else {
                mutationIndex++;
                writeUsingFileWriter(mutatedExpr.toString(), mutationDirectory + "/mutatedSpec-"
                        + mutationTypeToString(mutationType)
                        + "-" + mutationIndex);
                ret.add(new MutationResult(mutatedExpr, mutationIndex));
            }
        }
        return ret;
    }

    private static String mutationTypeToString(MutationType mutationType) {
        switch (mutationType) {
            case OP_MUT:
            case LITERAL_MUT:
            case ADD_TERM_MUT:
            case REMOVE_TERM_MUT:
            case UNKNOWN:
            default:
                throw new UnsupportedOperationException("this mutation type is unsupported");
            case LOGICAL_OP_REPLACEMENT:
                return "LOR";
            case RELATIONAL_OP_REPLACEMENT:
                return "ROR";
        }
    }

    /**
     * Use FileWriter when number of write operations are less
     * @param data
     */
    private static void writeUsingFileWriter(final String data, final String fileName) {
        File file = new File(fileName);
        FileWriter fr = null;
        try {
            fr = new FileWriter(file);
            fr.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            //close resources
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
