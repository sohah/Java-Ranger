//package Launch;
package DiscoveryExamples.AnRocketLaunch;

class RocketPad
    //true: rocket1; false: rocket2
{
    private boolean[] relay = new boolean[2];
    //--------------------------------------------------------------------

    public RocketPad() //Constructor
    {
        // activateButton = new Button();
        relay[0] = false;//open
        relay[1] = false;//open    }
    }
    //Open: False; Close: True
    //Enum State;//Active, inactive
    /*public void activateButtonPressed(PadUnit pad) throws InvalidInputException
    {
        //Send signal to PadUnit
        activateButton.activate();
        // Main.controller.activateControlButton(pad,rocketName);//throw InvalidInputException here
    }
*/
    public void getRelayState()
    {
        // System.out.println("Rocket: " + rocketName);
        System.out.println("relay1: "+relay[0]);
        System.out.println("relay2: " + relay[1]);
    }
    public void closeRelay(int relayNumber)
    {
        relay[relayNumber] = true;
    }

    public void resetRelays()
    {
        relay[0] = false;
        relay[1] = false;
        // activateButton.reset();
    }

}
