package DiscoveryExamples.AnRocketLaunch;

//package Launch;
class PadUnit
{
    private RocketPad[] rocketPad = new RocketPad[2];
    private int name;
    private ControlButton controlButton = new ControlButton();
    public PadUnit(int name) //Constructor
    {
        this.name = name;
        rocketPad[0] = new RocketPad();
        rocketPad[1] = new RocketPad();
    }
    public void closeRelay(int rocketNumber, int relayNumber)
    {
        rocketPad[rocketNumber].closeRelay(relayNumber);
    }
    public void resetRelays(int rocketNumber)
    {
            rocketPad[rocketNumber].resetRelays();
    }
    public ControlButtonState getRocketState(int rocketNum)
    {
            return controlButton.getState(rocketNum);
    }

    public int takeAction(int rocketName, int action, boolean actionIsTimeout) {
        if (!actionIsTimeout) {
            {
                if (action == 1)
                {
                   if (activateControlButton(rocketName) == true)
                   {
                        return 11;
                   }
                   else
                   {
                        return 7;
                   }
                }
                else if (action == 2) {
                    System.out.println("rocketName: " + rocketName);
                    if (armedLaunchButtonPressed(rocketName) == true)
                    {
                        return 1;
                    }
                    else
                    {
                        return 6;
                    }
                } else if (action == 3) {

                     if (launchButtonPressed(rocketName) == true)
                     {
                        return 1;
                    }
                    else
                    {
                        return 5;
                    }
                } else if (action == 4) {
                        reset(rocketName);
                        return 1;
                }
            }
        }
        return 1;
    }

    private void reset(int name) {
        controlButton.reset(this, name);
        System.out.println("reset done");
    }

    public boolean activateControlButton(int rocketName)
    {
        ControlButtonState state = controlButton.getState(rocketName);
        if (state == ControlButtonState.inactive) {
            controlButton.activateControlButton(rocketName);
            System.out.println("Control Buttons were activated");
            state = controlButton.getState(rocketName);
            assert state == ControlButtonState.armedLaunchAvailable :
            "State has to be armedLaunchAvailable after activtion!!!";
            // assert value >= 20 : " Underweight";
            return true;
        }
        else
        {
            System.out.println("Control Buttons are activated already");
            return false;
        }
    }

    private boolean armedLaunchButtonPressed(int rocketName)
    {
        ControlButtonState state = controlButton.getState(rocketName);
        if (state == ControlButtonState.inactive) {
            System.out.println("armed button is unavaiable now");//code exception: 6
            return false;
        } else if (state == ControlButtonState.armedLaunchAvailable) {
            controlButton.armedLaunchButtonPressed(this, rocketName);
            state = controlButton.getState(rocketName);
            System.out.println("armed pressed");
            assert state == ControlButtonState.launchAvailable :
            "State has to be launchAvailable after armedLaunchButtonPressed!!!";
            return true;
        } else if (state == ControlButtonState.launchAvailable) {
            reset(rocketName);
            state = controlButton.getState(rocketName);
            assert state == ControlButtonState.inactive:
            "State has to be inactive after reset!!!";
            return true;
        }
        return true;
    }

    private boolean launchButtonPressed(int rocketName)
    {
        String launchSuccess = ("Rocket " + rocketName + ": Launch!!!!!");
            ControlButtonState state = controlButton.getState(rocketName);
            //check state
            if (state == ControlButtonState.inactive) {
                System.out.println("Launch button is inactive unavaiable now"); // code exception: 5
                return false;
            } else if (state == ControlButtonState.armedLaunchAvailable) {
                System.out.println("Launch button is inactive unavaiable now");
                return false;
            } else if (state == ControlButtonState.launchAvailable) {
                controlButton.launchButtonPressed(this, rocketName);
                state = controlButton.getState(rocketName);
                System.out.println("Launch!!!!!");
                assert state == ControlButtonState.launched:
                "State has to be launched after launchButtonPressed!!!";
                reset(rocketName);
                state = controlButton.getState(rocketName);
                assert state == ControlButtonState.inactive:
                "State has to be inactive after reset!!!";
                return true;
            }
        return true; // Never reach to this statement. But compiler gives error if not
    }
}
