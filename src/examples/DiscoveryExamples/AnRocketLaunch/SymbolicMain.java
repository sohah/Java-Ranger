package DiscoveryExamples.AnRocketLaunch;

import static DiscoveryExamples.AnRocketLaunch.Main.padArray;

public class SymbolicMain {

    public static void main(String[] args) {

        initPad();
        makeStep(1, 1, 1, false, true);
        makeStep(2, 1, 1, false, true);
        makeStep(3, 1, 1, false, true);
        makeStep(1, 1, 2, false, true);
        makeStep(2, 1, 2, false, true);
        makeStep(3, 1, 2, false, true);
        makeStep(1, 2, 1, false, true);
        makeStep(2, 2, 1, false, true);
        makeStep(3, 2, 1, false, true);
        makeStep(1, 2, 2, false, true);
        makeStep(2, 2, 2, false, true);
        makeStep(3, 2, 2, false, true);
        makeStep(1, 2, 2, false, true);
        makeStep(2, 2, 2, false, true);
        makeStep(2, 2, 2, false, true); // reset here
        makeStep(1, 2, 2, false, true);
        makeStep(4, 2, 2, false, true); // reset here
    }


    public static void initPad() {
        PadUnit unit1 = new PadUnit(1);
        PadUnit unit2 = new PadUnit(2);
        PadUnit unit3 = new PadUnit(3);
    }

    private static int makeStep(int rocketAction, int padNumber, int rocketNumber, boolean actionIsTimeout, boolean x) // throws InvalidInputException
    {
        if (x && (padNumber >= 0) && (padNumber < 3))
            return padArray[padNumber].takeAction(rocketNumber, rocketAction, actionIsTimeout);
        else
            return padArray[padNumber].takeAction(rocketNumber, rocketAction, actionIsTimeout);
    }
}
