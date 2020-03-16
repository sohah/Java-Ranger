//package Launch;

package DiscoveryExamples.AnRocketLaunch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;

class Main {
    public static String readInputStreamWithTimeout(InputStream is, byte[] b, int timeoutMillis, PadUnit[] padArray, String padInfo, String rocketInfo)
            throws IOException {
        int PadNumber = Integer.parseInt(padInfo);
        int rocketName = 0;
        if (rocketInfo.equals("2")) {
            rocketName = 1;
        }
        int bufferOffset = 0;
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        String action = "";
        boolean notReceiveInput = true;
        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < b.length && notReceiveInput) {
            int readLength = java.lang.Math.min(is.available(), b.length - bufferOffset);
            // can alternatively use bufferedReader, guarded by isReady():
            int readResult = is.read(b, bufferOffset, readLength);
            bufferOffset += readResult;
            // System.out.println("Run here");
            if (bufferOffset != 0) {
                notReceiveInput = false;
            }
        }
        action = new String(b);
        if (notReceiveInput == true) {
            padArray[PadNumber].takeAction(rocketName, 4, false);
            return ("Time out case");
        }
        String result = "";
        int actionLength = action.length();
        for (int k = 0; k < actionLength; k++) {

            if (Character.isDigit(action.charAt(k))) {
                result += action.charAt(k);
            }
        }
        return result;
    }

    private static boolean ExceptionInputCase(int result) {
        if (result == 5) {
            System.out.println("Launch button is inactive unavaiable now");
            return false;
        }
        if (result == 6) {
            System.out.println("armed button is unavaiable now");
            return false;
        }
        if (result == 17) {
            System.out.println("Control Buttons are activated already");
            return false;
        }
        return true;
    }

    private static int makeStep(int rocketAction, int padNumber, int rocketNumber,
                                boolean actionIsTimeout, boolean x) // throws InvalidInputException
    {
        if (x)
            return padArray[padNumber].takeAction(rocketNumber, rocketAction, actionIsTimeout);
        else
            return padArray[padNumber].takeAction(rocketNumber, rocketAction, actionIsTimeout);
    }

    public static boolean checkPadNumber(String inputNumOfPad, String padNumber) {
        int NumberOfPad = Integer.parseInt(inputNumOfPad);
        for (int k = 0; k <= NumberOfPad; k++) {
            boolean rightPadNum = padNumber.equals(Integer.toString(k));
            if (rightPadNum) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkInputRocketNumber(String rocketNum) {
        boolean rocketNumEqual1 = (!rocketNum.equals("1"));
        boolean rocketNumEqual2 = (!rocketNum.equals("2"));
        if (rocketNumEqual1 && rocketNumEqual2) {
            return false;
        }
        return true;
    }

    /**
     * This method gets the current state of the pad and displays possible actions on the pad.
     *
     * @param rocketName
     * @param padArray
     * @param inputPadName
     * @param inputNumOfPad
     */
    public static boolean CheckPadNameAndRocketNumberThenInputRocketAction(String rocketName, PadUnit[] padArray, String inputPadName, String inputNumOfPad) // throws InvalidInputException
    //Check input padName and rocketInfo
    {
        if (checkPadNumber(inputNumOfPad, inputPadName) == false) {
            System.out.println("This Pad is not registered");
            return false;
        }
        if (checkInputRocketNumber(rocketName) == false) {
            System.out.println("Rocket number can only be 1 or 2");
            return false;
        }
        int rocketNumber = 0;
        if (rocketName.equals("2")) {
            rocketNumber = 1;
        }
        Integer.parseInt(rocketName);
        int padNumber = Integer.parseInt(inputPadName);
        ControlButtonState currentState;
        currentState = padArray[padNumber].getRocketState(rocketNumber);
        if (currentState == ControlButtonState.armedLaunchAvailable) {
            System.out.println("Input2: Enter action (only 2('armed') and 4('reset') are available)");
        } else if (currentState == ControlButtonState.inactive) {
            System.out.println("Input2: Enter action (only 1('activate') and 4('reset') are available)");
        } else if (currentState == ControlButtonState.launchAvailable) {
            System.out.println("Input2: Enter action (only 2('armed'), 3('launch') and 4('reset') are available)");
        }
        return true;
    }

    public static void terminateInstruction() {
        System.out.println("TERMINATE THE PROCESS ANYTIME BY ENTERING '0'");
        System.out.println("TERMINATE THE PROCESS ANYTIME BY ENTERING '0'");
        System.out.println("TERMINATE THE PROCESS ANYTIME BY ENTERING '0'");
    }

    public static void println() {
        System.out.println("------------------------------------------------");
    }

    public static boolean actionValidity(String action) {
        boolean actionIs0 = action.equals("0");
        boolean actionIs1 = action.equals("1");
        boolean actionIs2 = action.equals("2");
        boolean actionIs3 = action.equals("3");
        boolean actionIs4 = action.equals("4");
        if ((!actionIs0) && (!actionIs1) && (!actionIs2)
                && (!actionIs3) && (!actionIs4)) {
            return false;
        }
        return true;
    }

    public static boolean initialize_pads(int NumberOfPads, String stringNumberOfPad) {
        if (numberOfPadValidity(stringNumberOfPad) == false) {
            System.out.println("Invalid number of pads");
            System.out.println("-------------------------------");
            return false;
        }
        NumberOfPads = Integer.parseInt(stringNumberOfPad);
        for (int i = 1; i < (NumberOfPads + 1); i++) {
            //Create instance of PadUnit
            //Only show Pad from 1
            padArray[i] = new PadUnit(i);
            System.out.println("Pad" + i + ": on");
        }
        System.out.println(NumberOfPads + " pads are registered successfully");
        return true;
    }

    public static boolean numberOfPadValidity(String numOfPad) // throws InvalidInputException
    {
        for (int k = 0; k <= 8; k++) {//may be null pointer is here. k can start from 0 to fix the problem
            String intToStringk = Integer.toString(k);
            if (numOfPad.equals(intToStringk)) {
                return true;
            }
        }
        return false;
    }

    public static void showState(int NumberOfPad, PadUnit[] padArray) {
        System.out.println("Rocket Pad Control Button system table");
        for (int j = 1; j <= NumberOfPad; j++) {
            println();
            System.out.println("Pad " + j + "Rocket 1: " + "Control Button State: " + padArray[j].getRocketState(0));
            System.out.println("Pad " + j + "Rocket 2: " + "Control Button State: " + padArray[j].getRocketState(1));
        }
    }

    public static PadUnit[] padArray = new PadUnit[9];

    public static void main(String[] args) throws IOException {
        String rocketInfo = "";
        int MaxPad = 8;
        String action;
        terminateInstruction();
        println();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean notHaltProgram1 = true;
        boolean notHaltProgram2 = true;
        int intNumOfPad = 0;
        int padNumber = 0;
        int rocketNumber = 0;
        int rocketAction = 0;
        String inputPadName = "";
        boolean actionIsActivate = false;
        int result = 2;
        // boolean actionValid = false;
        int padResult = 0;
        while (notHaltProgram1) {
            System.out.println("Enter number of pad (Maximum 8)");
            String inputNumOfPad = reader.readLine();
            boolean inputIs0 = inputNumOfPad.equals("0");
            if (inputIs0) {
                notHaltProgram1 = false;
            }
            boolean initializePadsIfContinueProgram = true;
            if (notHaltProgram1 && initializePadsIfContinueProgram) {
                if (initialize_pads(intNumOfPad, inputNumOfPad) == true) {
                    intNumOfPad = Integer.parseInt(inputNumOfPad);
                    while (notHaltProgram2) {
                        if (actionIsActivate == true) {
                            println();
                            System.out.println("Enter action for rocket " + rocketInfo +
                                    "(only 2('armed') and 4('reset') are available) (The system of this rocket will be reset in 10 seconds if no more action is executed.");
                            byte[] inputData = new byte[20];
                            //function readInputStreamWithTimeout and input0 will have problem if parameter for inputdata
                            //have more than 20 characters
                            String nextAction = readInputStreamWithTimeout(System.in, inputData, 10000, padArray, inputPadName, rocketInfo);
                            // System.out.println("next action: " + nextAction);
                            boolean nextActionIsTimeout = nextAction.equals("Time out case");
                            boolean actionValid = actionValidity(nextAction);
                            if (nextActionIsTimeout == false) {
                                if (actionValid == true) {
                                    int intNextAction = Integer.parseInt(nextAction);
                                    if (intNextAction == 0) {
                                        notHaltProgram2 = false;
                                        notHaltProgram1 = false;
                                    } else if (intNextAction == 1) {
                                        System.out.println("Control Buttons are activated already");
                                    } else {
                                        padResult = padArray[padNumber].takeAction(rocketNumber, intNextAction, nextActionIsTimeout);
                                        ExceptionInputCase(padResult);
                                        println();
                                    }
                                } else {
                                    System.out.println("Action can only be 1('activate'), 2('armed'), 3('launch'), or 4('reset')");
                                }
                            }
                            actionIsActivate = false;
                            if (!inputIs0 && !actionIsActivate) {
                                showState(intNumOfPad, padArray);
                            }
                        } else {
                            println();
                            System.out.println("Input0: Enter pad name");
                            inputPadName = reader.readLine();
                            inputIs0 = inputPadName.equals("0");
                            if (inputIs0) {
                                notHaltProgram2 = false;
                                notHaltProgram1 = false;
                            } else {
                                System.out.println("Input1: Enter rocket number (1 or 2)");
                                rocketInfo = reader.readLine();
                                inputIs0 = rocketInfo.equals("0");
                                if (inputIs0) {
                                    notHaltProgram2 = false;
                                    notHaltProgram1 = false;
                                }
                                // System.out.println(inputNumOfPad);
                                // System.out.println(pa[inputPadName]);
                                // System.out.println(rocketInfo);
                                if (CheckPadNameAndRocketNumberThenInputRocketAction(rocketInfo, padArray, inputPadName, inputNumOfPad) == true) {
                                    if (rocketInfo.equals("1")) {
                                        rocketNumber = 0;
                                    } else {
                                        rocketNumber = 1;
                                    }
                                    padNumber = Integer.parseInt(inputPadName);
                                    action = reader.readLine();
                                    //actionResult = ;
                                    if (actionValidity(action)) {
                                        rocketAction = Integer.parseInt(action);
                                        boolean ActionIsTimeout = action.equals("Time out case");
                                        inputIs0 = (rocketAction == 0);
                                        if (inputIs0) {
                                            notHaltProgram2 = false;
                                            notHaltProgram1 = false;
                                        }
                                        if (notHaltProgram2) {
                                            result = makeStep(rocketAction, padNumber, rocketNumber,
                                                    ActionIsTimeout, true);
                                            System.out.println("result: " + result);
                                            if (result == 11) {
                                                actionIsActivate = true;
                                            }
                                            if (result == 1) {
                                                actionIsActivate = false;
                                            }
                                            ExceptionInputCase(result);
                                        }
                                    } else {
                                        System.out.println("Action can only be 1('activate'), 2('armed'), 3('launch'), or 4('reset')");
                                    }
                                    if (!inputIs0 && !actionIsActivate) {
                                        showState(intNumOfPad, padArray);
                                    }
                                }

                            }
                        }

                        // catch (InvalidInputException e) {
                        // System.out.println(e);

                    }
                    initializePadsIfContinueProgram = false;
                }
            }
        }
        // catch (InvalidInputException e) {
        // System.out.println(e);
    }
}
