package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.mutation;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput.SpecInOutManager;
import jkind.lustre.BinaryOp;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config.repairMutantsOnly;
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
            case IMPLIES:
                BinaryOp newOp = mutateExpr.applyBinaryOpMutation(op, getMutationOps(op, allOps));
                return newOp;
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
            case NOTEQUAL:
                BinaryOp newOp = mutateExpr.applyBinaryOpMutation(op, getMutationOps(op, allOps));
                return newOp;
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
                                                              final String mutationDirectory,
                                                              SpecInOutManager tInOutManager) {
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

        MutationType[] mutationTypes = !repairMutantsOnly ? new MutationType[]{MutationType.OPERAND_REPLACEMENT_MUT,
                MutationType.LOGICAL_OP_REPLACEMENT, MutationType.RELATIONAL_OP_REPLACEMENT,
                MutationType.REPAIR_EXPR_MUT, //MutationType.MISSING_COND_MUT, //Soha turning this one off for now
                } :
                new MutationType[]{MutationType.REPAIR_EXPR_MUT};
        ArrayList<MutationResult> mutationResults = new ArrayList<>();
        for(MutationType mutationType: mutationTypes) {
            mutationResults.addAll(applyMutation(originalProgram, mutationType, mutationDirectory, tInOutManager));
        }
        System.out.println("wrote " + mutationResults.size() + " mutants into the " + mutationDirectory + " folder");
        return mutationResults;
    }

    private static Expr mutateORO(ShouldApplyMutation shouldApplyMutation, Expr e) {
        IdExprVisitor idExprVisitor = new IdExprVisitor(e, shouldApplyMutation.tInOutManager,
                shouldApplyMutation.inputs, shouldApplyMutation.outputs);
        e.accept(idExprVisitor);
        ArrayList<IdExpr> idExprs = idExprVisitor.getIdExprs();
        ConstExprVisitor constExprVisitor = new ConstExprVisitor();
        e.accept(constExprVisitor);
        ArrayList<Expr> constExprs = constExprVisitor.getConstExprs();
        OROMutationVisitor oroVisitor = new OROMutationVisitor(shouldApplyMutation, idExprs, constExprs, idExprVisitor.getTypes());
        Expr newExpr = e.accept(oroVisitor);
        shouldApplyMutation.justDidMutation = oroVisitor.checkImmediateOROMutation( e, newExpr);
        return newExpr;
        /*if (newExpr instanceof BinaryExpr)
            return new BinaryExpr(((BinaryExpr) newExpr).left.accept(this),
                    ((BinaryExpr) newExpr).op, ((BinaryExpr) newExpr).right.accept(this));
        else if (newExpr instanceof UnaryExpr)
            return new UnaryExpr(((UnaryExpr) newExpr).op, ((UnaryExpr) newExpr).expr.accept(this));*/
//        return e;
    }

    private static ArrayList<MutationResult> applyMutation(final Program originalProgram,
                                                           final MutationType mutationType,
                                                           String mutationDirectory,
                                                           SpecInOutManager tInOutManager) {
        int mutationIndex = -1, repairMutationIndex = -1;
        ArrayList<MutationResult> ret = new ArrayList<>();
        while (true) {
            Node mainNode = originalProgram.nodes.get(0);
            Expr mutatedExpr;
            ShouldApplyMutation shouldApplyMutation = new ShouldApplyMutation(mutationIndex, repairMutationIndex, tInOutManager, mainNode.inputs, mainNode.outputs);
            if (mutationType != MutationType.OPERAND_REPLACEMENT_MUT) {
                MutateExpr mutateExpr = new MutateExpr(mutationType, shouldApplyMutation);
                mutatedExpr = mainNode.equations.get(0).expr.accept(mutateExpr);
            } else {
                mutatedExpr = mutateORO(shouldApplyMutation, mainNode.equations.get(0).expr);
            }
            if (!shouldApplyMutation.didMutation() && !repairMutantsOnly) {
                break;
            } else {
                if (!shouldApplyMutation.addedRepairWrapper()) {
                    if (!repairMutantsOnly) {
                        mutationIndex++;
                        repairMutationIndex = -1;
                    } else {
                        break;
                    }
                } else {
                    repairMutationIndex++;
                    if (Config.printMutantDir)
                        writeUsingFileWriter(mutatedExpr.toString(), mutationDirectory
                                + "/mutatedSpec-" + mutationTypeToString(mutationType) + "-"
                                + repairMutationIndex + "-" + mutationIndex);
                    ret.add(new MutationResult(mutatedExpr, repairMutationIndex, mutationIndex, mutationType,
                            shouldApplyMutation.repairNodes, shouldApplyMutation.repairDepth,
                            shouldApplyMutation.isPerfect, shouldApplyMutation.isSmallestWrapper));
                }
            }
        }
        return ret;
    }

    static String mutationTypeToString(MutationType mutationType) {
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
            case REPAIR_EXPR_MUT:
                return "REXPR";
            case MISSING_COND_MUT:
                return "MCO";
            case OPERAND_REPLACEMENT_MUT:
                return "ORO";
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
