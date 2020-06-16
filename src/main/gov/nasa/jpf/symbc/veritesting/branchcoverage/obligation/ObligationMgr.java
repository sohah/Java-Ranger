package gov.nasa.jpf.symbc.veritesting.branchcoverage.obligation;

import com.ibm.wala.ssa.SSAInstruction;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ObligationMgr {

    public static final HashMap<Obligation, Integer> obligationsMap = new HashMap<>();

    //since reachability of the then and the else are the same, we only store the reachable obligation for one of them. It always the "then"
    public static final HashMap<Obligation, HashSet<Obligation>> reachabilityMap = new HashMap<>();

    private static int indexSerial = 0;

    private static boolean[] coveredArray;

    public static void finishedCollection() {
        assert (obligationsMap.size() > 0) : "obligation Map cannot be empty";
        coveredArray = new boolean[obligationsMap.size()];
    }

    public static void addOblgMap(String walaPackageName, String className, String methodSig, int instLine, SSAInstruction inst, HashSet<Obligation> reachableObl) {
        Obligation oblgThen = new Obligation(walaPackageName, className, methodSig, instLine, inst, ObligationSide.THEN);
        Obligation oblgElse = new Obligation(walaPackageName, className, methodSig, instLine, inst, ObligationSide.ELSE);

        if (oblgExists(oblgElse)) return;

        obligationsMap.put(oblgThen, indexSerial++);
        obligationsMap.put(oblgElse, indexSerial++);

        reachabilityMap.put(oblgThen, reachableObl);
    }


    public static boolean oblgExists(Obligation oblg) {
        Integer oblIndex = obligationsMap.get(oblg);
        return oblIndex != null;
    }


    // when encountering an instruction, tries to see its side was already covered or not. depending on that either ignore the choice or continue
    //returns a ignore flag, true if the obligation is already covered, false otherwise.
    public static boolean coverNgetIgnore(Obligation oblg) {

        Integer oblgIndex = obligationsMap.get(oblg);
/*
        Set<Obligation> oblgKeySet = obligationsMap.keySet();
        for (Obligation myOblg : oblgKeySet) {
            System.out.println(myOblg);
        }*/

//        assert (!(oblgIndex == null)) : "obligation not found in the obligation HashMap. Something is wrong. Failing.";

        if ((oblgIndex == null)) {
            System.out.println("obligation not found in the obligation HashMap. Assumed none application/user branch. Coverage Ignored for instruction.");
            return false; //returning ignore flag to false, since it is none user branch that we do not care about and we'd like to resume execution to potientially find something down the line.
        }
        if (isOblgCovered(oblg)) return true; //returning ignore flag to true, since the oblgation is already covered.
        else {
            coveredArray[oblgIndex] = true;
            return false; //returning ignore flag to false, since the oblgation is NOT covered.
        }
    }

    // SPF methods to manipulate covering at runtime. Must always be called with already existing obligation in the map.
    public static boolean isOblgCovered(Obligation oblg) {
        Integer oblgIndex = obligationsMap.get(oblg);
        Set<Obligation> oblgKeySet = obligationsMap.keySet();
        /*for (Obligation myOblg : oblgKeySet) {
            System.out.println(myOblg);
        }*/
        assert (!(oblgIndex == null)) : ("obligation not found in the obligation HashMap. Something is wrong. Failing.");

        assert oblgIndex < coveredArray.length;

        return coveredArray[oblgIndex];
    }

    public static int getOblgIndex(Obligation oblg) {
        return obligationsMap.get(oblg);
    }

    //returns an array of unreachableObligations, empty array if all are already covered and null if the mainOblg is not found in the map indicating it is not an obligation we are tracking for coverage.
    public static Obligation[] isReachableOblgsCovered(Obligation mainOblg) {
        ArrayList<Obligation> uncoveredOblgList = new ArrayList<>();
        assert mainOblg.oblgSide == ObligationSide.THEN : "reachability map is storing the then side only, since the reachability of a node is the same among its then and else sides.";

        if (!reachabilityMap.containsKey(mainOblg)) {  //if it can't be found in t he reachability map then it must be the that it doesn't exist in the obligationMap as well indicating that it is an obligation that we do not care about tracking its cover, for example, it is not an application users code.
            assert !obligationsMap.containsKey(mainOblg);
            return null;
        }
        HashSet<Obligation> reachableOblgs = reachabilityMap.get(mainOblg);

        //add myself if I am not yet covered.
        if (!isOblgCovered(mainOblg)) uncoveredOblgList.add(mainOblg);

        for (Obligation reachableOblg : reachableOblgs) {
            if (!isOblgCovered(reachableOblg)) uncoveredOblgList.add(reachableOblg);
        }
        if (uncoveredOblgList.size() > 0) return uncoveredOblgList.toArray(new Obligation[uncoveredOblgList.size()]);
        else return new Obligation[]{};
    }

    public static void printCoverage(PrintWriter pw) {
        pw.println("Obligation -----> Coverage:");

        Set<Obligation> olgKeySet = obligationsMap.keySet();
        for (Obligation oblg : olgKeySet) {
            pw.println(oblg + " -----> " + coveredArray[obligationsMap.get(oblg)]);
        }
    }

    public static String printCoverage() {
        String coverageStr = ("Obligation -----> Coverage:\n");

        Set<Obligation> olgKeySet = obligationsMap.keySet();
        for (Obligation oblg : olgKeySet) {
            coverageStr = coverageStr.concat(oblg + " -----> " + coveredArray[obligationsMap.get(oblg)] + " \n");
        }
        return coverageStr;
    }
}
