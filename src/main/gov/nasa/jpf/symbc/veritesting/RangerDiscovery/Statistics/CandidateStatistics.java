package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Statistics;


import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.DiscoverContract;

/**
 * collects statistics of all candidates tried within a tight loop or in the main outer loop.
 */
public class CandidateStatistics {

    long totalLoopTimeUntilRepair; //all the time for all queries in a loop until a repair is found
    long thereExistTimeTillRepair; //time for all thereExists query until a repair is found
    long forallTimeTillRepair; // time for all forall queries until a repair is found
    int thereExistsQueryNumTillRepair; // number of thereExists queries, these are also the number of candidates
    int forallQueryNumTillRepair;

    public long totalExistsTime;
    public long totalExistsNum;
    public long totalForallTime;
    public long totalForallNum;
    public int repairsFoundNum;
    public long totalTime;

    public CandidateStatistics() {
        RepairStatistics.out.print("Minimal     ");
        RepairStatistics.out.print("LoopCount     "); //number of iterations in the outer loop
        RepairStatistics.out.print("CandidateNum     ");
        RepairStatistics.out.print("QueryTime     ");
        RepairStatistics.out.print("QueryType     ");
        RepairStatistics.out.println();
    }


    public void printCandStatistics(String loopCount, boolean minimal, int candidateNum, QueryType queryType,
                                    long queryTime) {
        RepairStatistics.out.print(minimal + "     ");
        RepairStatistics.out.print(loopCount + "     "); //iteration number
        RepairStatistics.out.print(candidateNum + "     ");
        RepairStatistics.out.print(queryTime + "     ");
        RepairStatistics.out.print(queryType + "     ");
        RepairStatistics.out.println();

        totalLoopTimeUntilRepair += queryTime;

        if (queryType == QueryType.FORALL) {
            forallTimeTillRepair += queryTime;
            ++forallQueryNumTillRepair;
        }

        if (queryType == QueryType.THERE_EXISTS) {
            thereExistTimeTillRepair += queryTime;
            ++thereExistsQueryNumTillRepair;
        }


    }

    //Advancing prints
    public void advanceTightLoop(boolean repairFound) {
        repairsFoundNum = repairFound ? ++repairsFoundNum : repairsFoundNum;

        totalExistsTime += thereExistTimeTillRepair;
        totalForallTime += forallTimeTillRepair;
        totalTime += totalLoopTimeUntilRepair;
        totalExistsNum += thereExistsQueryNumTillRepair;
        totalForallNum += forallQueryNumTillRepair;

        RepairStatistics.out.println("---------------------------LOOP STATS------------------------------");
        RepairStatistics.out.println();

        RepairStatistics.out.print("forallTime     ");
        RepairStatistics.out.print("existTime     "); //number of iterations in the outer loop
        RepairStatistics.out.print("forallNum     ");
        RepairStatistics.out.print("existsNum     ");
        RepairStatistics.out.print("totalTime     ");
        RepairStatistics.out.print("avgForallTime     ");
        RepairStatistics.out.print("avgExistsTime     ");
        RepairStatistics.out.print("avgTotalTime     ");
        RepairStatistics.out.println();


        RepairStatistics.out.print(forallTimeTillRepair + "     ");
        RepairStatistics.out.print(thereExistTimeTillRepair + "     "); //number of iterations in the outer loop
        RepairStatistics.out.print(forallQueryNumTillRepair + "     ");
        RepairStatistics.out.print(thereExistsQueryNumTillRepair + "     ");
        RepairStatistics.out.print(totalLoopTimeUntilRepair + "     ");
        if (forallQueryNumTillRepair == 0) { // this condition is expected to hold only in the minimal query when the
            // thereExists fails to find even a single synthesis for which we can do a forall query. Therefore one
            // way to check that is that there can't be a repair found at this point.
            assert (DiscoverContract.loopCount == 0);
            RepairStatistics.out.print("N/A     ");
        } else RepairStatistics.out.print(forallTimeTillRepair / forallQueryNumTillRepair + "     ");

        if (thereExistsQueryNumTillRepair == 0) { // this condition is not expected to occur since at least one
            // thereExists query must happen in either the outer or the inner loop.
            assert false;
        } else RepairStatistics.out.print(thereExistTimeTillRepair / thereExistsQueryNumTillRepair + "     ");
        RepairStatistics.out.print(totalTime / (thereExistsQueryNumTillRepair + forallQueryNumTillRepair) + "     ");
        RepairStatistics.out.println();
        RepairStatistics.out.println();

        RepairStatistics.out.print("Minimal     ");
        RepairStatistics.out.print("LoopCount     "); //number of iterations in the outer loop
        RepairStatistics.out.print("CandidateNum     ");
        RepairStatistics.out.print("QueryTime     ");
        RepairStatistics.out.print("QueryType     ");
        RepairStatistics.out.println();

        //reset state
        totalLoopTimeUntilRepair = 0;
        forallTimeTillRepair = 0;
        forallQueryNumTillRepair = 0;
        thereExistTimeTillRepair = 0;
        thereExistsQueryNumTillRepair = 0;
    }


}
