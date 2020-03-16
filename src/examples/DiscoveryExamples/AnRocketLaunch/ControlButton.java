//package Launch;
package DiscoveryExamples.AnRocketLaunch;

enum ControlButtonState {
    invalid, armedLaunchAvailable, launchAvailable, launched, inactive;
}

class ControlButton {
    private Button[] ArmedButton = new Button[2];
    private Button[] LaunchButton = new Button[2];
    private Button[] ResetButton = new Button[2];

    public ControlButton() {
        this.ArmedButton[0] = new Button();
        this.LaunchButton[0] = new Button();
        this.ResetButton[0] = new Button();
        this.ArmedButton[1] = new Button();
        this.LaunchButton[1] = new Button();
        this.ResetButton[1] = new Button();
    }

    public void activateControlButton(int rocketName) {
            ArmedButton[rocketName].activate();
            ResetButton[rocketName].activate();
    }

    public void armedLaunchButtonPressed(PadUnit pad, int rocketName)
    {
        LaunchButton[rocketName].activate();
        ArmedButton[rocketName].pressed();
        if (rocketName == 1)
        {
            pad.closeRelay(0,1);
        } else
        {
            pad.closeRelay(1,1);
        }
    }

    public void launchButtonPressed(PadUnit pad, int rocketName)
    {
        LaunchButton[rocketName].pressed();
        if (rocketName == 1) {
            pad.closeRelay(0,1);
        } else {
            pad.closeRelay(1,1);
        }
    }

    public void reset(PadUnit pad, int rocketName) {
        ArmedButton[rocketName].reset();
        LaunchButton[rocketName].reset();
        if (rocketName == 1) {
            pad.resetRelays(0);
        } else {
            pad.resetRelays(1);
        }
    }

    public ControlButtonState getState(int rocketNumber) {
        if ((ArmedButton[rocketNumber].state() == ButtonState.inactive) &&
                (LaunchButton[rocketNumber].state() == ButtonState.inactive)) {
            return ControlButtonState.inactive;
        } else if ((ArmedButton[rocketNumber].state() == ButtonState.notPressed) &&
                (LaunchButton[rocketNumber].state() == ButtonState.inactive)) {
            return ControlButtonState.armedLaunchAvailable;
        } else if ((ArmedButton[rocketNumber].state() == ButtonState.pressed) &&
                (LaunchButton[rocketNumber].state() == ButtonState.notPressed)) {
            return ControlButtonState.launchAvailable;
        } else if ((ArmedButton[rocketNumber].state() == ButtonState.pressed) &&
                (LaunchButton[rocketNumber].state() == ButtonState.pressed)) {
            return ControlButtonState.launched;
        }
        return ControlButtonState.invalid;
    }
}
