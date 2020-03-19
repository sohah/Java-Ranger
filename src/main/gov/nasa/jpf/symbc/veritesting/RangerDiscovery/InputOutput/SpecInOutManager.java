package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import jkind.lustre.NamedType;

import java.util.List;


/**
 * this class manages the input and output of TNODE, RNODE uses another class that inherets from this one and adds a few extra functions.
 */
public class SpecInOutManager {

    SpecInputOutput freeInput = new SpecInputOutput();
    SpecInputOutput stateVars = new SpecInputOutput();

    // holds the inputs in the contract which are the outputs, i.e., which constrain the output.
    SpecInputOutput inOutputVars = new SpecInputOutput();

    public void discoverVars() {
        if (Config.spec.equals("pad")) {
            discoverFreeInputPad();
            discoverStateVarsPad();
            discoverOutputVarPad();
        } else if (Config.spec.equals("even")) {
            discoverFreeInputEven();
            discoverStateVarsEven();
            discoverOutputVarEven();
        } else if (Config.spec.equals("wbs")) {
            discoverFreeInputWbs();
            discoverOutputVarWbs();
        } else if (Config.spec.equals("tcas")) {
            discoverFreeInputTcas();
            discoverOutputVarTcas();
        } else if (Config.spec.equals("vote")) {
            discoverFreeInputVote();
            discoverOutputVarVote();
        } else if (Config.spec.equals("vote2")) {
            discoverFreeInputVote2();
            discoverOutputVarVote2();
        }
        if (Config.spec.equals("gpca")) {
            discoverFreeInputGpca();
            discoverOutputVarGpca();
        }
        if (Config.spec.equals("infusion")) {
            discoverFreeInputInfusion();
            discoverOutputVarInfusion();
        } else {
            System.out.println("unexpected spec to run.!");
        }
    }
    //=========================== Pad ===========================

    private void discoverFreeInputPad() {
        freeInput.add("sig", NamedType.INT);
    }

    private void discoverStateVarsPad() {
        stateVars.add("start_bt", NamedType.BOOL);
        stateVars.add("launch_bt", NamedType.BOOL);
        stateVars.add("reset_bt", NamedType.BOOL);
    }

    private void discoverOutputVarPad() {
        inOutputVars.add("ignition", NamedType.BOOL);
    }


    //=========================== Even ===========================

    private void discoverOutputVarEven() {
        inOutputVars.add("out", NamedType.INT);
    }

    private void discoverFreeInputEven() {
        freeInput.add("signal", NamedType.BOOL);
    }

    private void discoverStateVarsEven() {
        stateVars.add("step", NamedType.INT);
    }


    //=========================== WBS ===========================

    private void discoverFreeInputWbs() {
        freeInput.add("pedal_r", NamedType.INT);
        freeInput.add("autoBreak_r", NamedType.BOOL);
        freeInput.add("skid_r", NamedType.BOOL);
    }


    private void discoverOutputVarWbs() {
        inOutputVars.add("NormalPressure_r", NamedType.INT);
        inOutputVars.add("AltPressure_r", NamedType.INT);
        inOutputVars.add("Sys_Mode", NamedType.INT);
    }

    //=========================== GPCA ===========================

    private void discoverFreeInputGpca() {
        freeInput.add("Commanded_Flow_Rate", NamedType.INT);
        freeInput.add("Current_System_Mode", NamedType.INT);
        freeInput.add("System_On", NamedType.BOOL);
        freeInput.add("System_Monitor_Failed", NamedType.BOOL);
        freeInput.add("Logging_Failed", NamedType.BOOL);
        freeInput.add("Infusion_Initiate", NamedType.BOOL);
        freeInput.add("Disable_Audio", NamedType.INT);
        freeInput.add("Notification_Cancel", NamedType.BOOL);
        freeInput.add("VTBI_High", NamedType.INT);
        freeInput.add("Flow_Rate_High", NamedType.INT);
        freeInput.add("Flow_Rate_Low", NamedType.INT);
        freeInput.add("Flow_Rate", NamedType.INT);
        freeInput.add("Flow_Rate_Not_Stable", NamedType.BOOL);
        freeInput.add("Air_In_Line", NamedType.BOOL);
        freeInput.add("Occlusion", NamedType.BOOL);
        freeInput.add("Door_Open", NamedType.BOOL);
        freeInput.add("Temp", NamedType.BOOL);
        freeInput.add("Air_Pressure", NamedType.BOOL);
        freeInput.add("Humidity", NamedType.BOOL);
        freeInput.add("Battery_Depleted", NamedType.BOOL);
        freeInput.add("Battery_Low", NamedType.BOOL);
        freeInput.add("Battery_Unable_To_Charge", NamedType.BOOL);
        freeInput.add("Supply_Voltage", NamedType.BOOL);
        freeInput.add("CPU_In_Error", NamedType.BOOL);
        freeInput.add("RTC_In_Error", NamedType.BOOL);
        freeInput.add("Watchdog_Interrupted", NamedType.BOOL);
        freeInput.add("Memory_Corrupted", NamedType.BOOL);
        freeInput.add("Pump_Too_Hot", NamedType.BOOL);
        freeInput.add("Pump_Overheated", NamedType.BOOL);
        freeInput.add("Audio_Enable_Duration", NamedType.INT);
        freeInput.add("Audio_Level", NamedType.INT);
        freeInput.add("Config_Warning_Duration", NamedType.INT);
        freeInput.add("Low_Reservoir", NamedType.INT);
        freeInput.add("Max_Duration_Over_Infusion", NamedType.INT);
        freeInput.add("Max_Duration_Under_Infusion", NamedType.INT);
        freeInput.add("Max_Paused_Duration", NamedType.INT);
        freeInput.add("Max_Idle_Duration", NamedType.INT);
        freeInput.add("Tolerance_Max", NamedType.INT);
        freeInput.add("Tolerance_Min", NamedType.INT);
        freeInput.add("Reservoir_Empty", NamedType.BOOL);
        freeInput.add("Reservoir_Volume", NamedType.INT);
        freeInput.add("Volume_Infused", NamedType.INT);
        freeInput.add("In_Therapy", NamedType.BOOL);
        freeInput.add("Config_Timer", NamedType.INT);

    }


    private void discoverOutputVarGpca() {
        inOutputVars.add("out_Is_Audio_Disabled", NamedType.INT);
        inOutputVars.add("out_Notification_Message", NamedType.INT);
        inOutputVars.add("out_Audio_Notification_Command", NamedType.INT);
        inOutputVars.add("out_Highest_Level_Alarm", NamedType.INT);
        inOutputVars.add("out_Log_Message_ID5", NamedType.INT);
    }


    //=========================== INFUSION ===========================

    private void discoverFreeInputInfusion() {
        freeInput.add("System_On_1_SYMINT", NamedType.BOOL);
        freeInput.add("Infusion_Initiate_6_SYMINT", NamedType.BOOL);
        freeInput.add("Infusion_Inhibit_7_SYMINT", NamedType.INT);
        freeInput.add("Infusion_Cancel_8_SYMINT", NamedType.BOOL);
        freeInput.add("Patient_Bolus_Request_18_SYMINT", NamedType.BOOL);
        freeInput.add("Infusion_Total_Duration_22_SYMINT", NamedType.INT);
        freeInput.add("VTBI_Total_23_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_Basal_24_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_Intermittent_Bolus_25_SYMINT", NamedType.INT);
        freeInput.add("Duration_Intermittent_Bolus_26_SYMINT", NamedType.INT);
        freeInput.add("Interval_Intermittent_Bolus_27_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_Patient_Bolus_28_SYMINT", NamedType.INT);
        freeInput.add("Duration_Patient_Bolus_29_SYMINT", NamedType.INT);
        freeInput.add("Lockout_Period_Patient_Bolus_30_SYMINT", NamedType.INT);
        freeInput.add("Max_Number_of_Patient_Bolus_Period_Patient_Bolus_31_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_KVO_32_SYMINT", NamedType.INT);
        freeInput.add("Entered_Reservoir_Volume_33_SYMINT", NamedType.INT);
        freeInput.add("Configured_35_SYMINT", NamedType.INT);
        freeInput.add("Highest_Level_Alarm_47_SYMINT", NamedType.INT);
        freeInput.add("Reservoir_Empty_49_SYMINT", NamedType.BOOL);
        freeInput.add("Volume_Infused_51_SYMINT", NamedType.INT);

    }


    private void discoverOutputVarInfusion() {
        inOutputVars.add("Commanded_Flow_Rate", NamedType.INT);
        inOutputVars.add("Current_System_Mode", NamedType.INT);
        inOutputVars.add("New_Infusion", NamedType.BOOL);
        inOutputVars.add("Log_Message_ID", NamedType.INT);
        inOutputVars.add("Actual_Infusion_Duration", NamedType.INT);
    }

//=========================== TCAS ===========================

    private void discoverFreeInputTcas() {
        freeInput.add("Cur_Vertical_Sep_s", NamedType.INT);
        freeInput.add("High_Confidence_flag_s", NamedType.INT);
        freeInput.add("Two_of_Three_Reports_Valid_flag_s", NamedType.INT);
        freeInput.add("Own_Tracked_Alt_s", NamedType.INT);
        freeInput.add("Own_Tracked_Alt_Rate_s", NamedType.INT);
        freeInput.add("Other_Tracked_Alt_s", NamedType.INT);
        freeInput.add("Alt_Layer_Value_s", NamedType.INT);
        freeInput.add("Up_Separation_s", NamedType.INT);
        freeInput.add("Down_Separation_s", NamedType.INT);
        freeInput.add("Other_RAC_s", NamedType.INT);
        freeInput.add("Other_Capability_s", NamedType.INT);
        freeInput.add("Climb_Inhibit_s", NamedType.INT);
    }


    private void discoverOutputVarTcas() {
        inOutputVars.add("result_alt_sep_test_s", NamedType.INT);
        inOutputVars.add("alim_res_s", NamedType.INT);
    }


    //=========================== Vote ===========================
    private void discoverFreeInputVote() {
        freeInput.add("a", NamedType.BOOL);
        freeInput.add("b", NamedType.BOOL);
        freeInput.add("c", NamedType.BOOL);
        freeInput.add("threshold", NamedType.INT);
    }

    private void discoverOutputVarVote() {
        inOutputVars.add("out", NamedType.BOOL);
    }

    //=========================== Vote2 ===========================
    private void discoverFreeInputVote2() {
        freeInput.add("a", NamedType.INT);
        freeInput.add("b", NamedType.INT);
        freeInput.add("c", NamedType.INT);
        freeInput.add("threshold", NamedType.INT);
    }

    private void discoverOutputVarVote2() {
        inOutputVars.add("out", NamedType.BOOL);
    }


    /**
     * searches in all in input and output arrays to check if it is one in them
     *
     * @param s
     * @return
     */
    public boolean isInOutVar(String s, NamedType type) {
        return isFreeInVar(s, type) || isStateInVar(s, type) || isStateOutVar(s, type);
    }


    public boolean isFreeInVar(String varName, NamedType type) {
        return freeInput.contains(varName, type);
    }

    public boolean isStateInVar(String varName, NamedType type) {
        return stateVars.contains(varName, type);
    }

    public boolean isStateOutVar(String varName, NamedType type) {
        return inOutputVars.contains(varName, type);
    }


    public SpecInputOutput getFreeInputs() {
        return freeInput;
    }

    public List<String> getFreeInputNames() {
        return freeInput.getInputNames();
    }

    public SpecInputOutput getInOutput() {
        return inOutputVars;
    }
}
