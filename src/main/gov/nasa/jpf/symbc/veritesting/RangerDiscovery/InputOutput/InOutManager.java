package gov.nasa.jpf.symbc.veritesting.RangerDiscovery.InputOutput;

import gov.nasa.jpf.symbc.veritesting.RangerDiscovery.Config;
import gov.nasa.jpf.symbc.veritesting.VeritestingUtil.Pair;
import jkind.lustre.*;

import java.util.ArrayList;


/**
 * this class manages the input and output of RNode, it assumes that the input and the output of the "step" function is
 * provided, it is divided into 4 types, freeInput, stateInput, stateOutput and contractOutput. The type of those
 * should match the signature of the step function. Type conversion is needed sometimes, if so then the variable
 * names in the arraylist will change to the new var being created, in this case there will be as well a side effect
 * for the equations needed for conversion between the original var and the new var being created for conversion.
 * <p>
 * An important thing to note here is that the signature of the different input, output, or state are reflecting those
 * in the implementation type.
 */

/**
 * State output refers to any state variable that the class keeps track of in each iteration, it really depends on
 * the implementation.
 * Contract output on the other hand refers to outputs that the specification are going to use to check specific
 * constraints. With this definition, contract output has nothing to do with the actual output of the contract in the
 * implementation, for the specification can be checking really multiple things.
 * <p>
 * There is an overlap between state output and contract output, when plugging in things we need to be careful about
 * what each term means. i.e., for those state variables that are going to be checked with the specification, even
 * though they are part of the state and therefore might be considered as state output, they are however checked by
 * the sepecification and with this regard they are defined as contract output instead. They should not be included as
 * a state output, only as a contract output.
 */
public class InOutManager {

    //for now we are adding the reference object by hand, it changes from lunix to mac, so I am adding this here to avoid having to repeatedly change the code
    //private String referenceObjectName = "r351"; //for lunix

    private String referenceObjectName = "r347"; //for mac

    //specific reference names for GPCA
    private String referenceObjectName_gpca_Alarm_Outputs = "r390";
    private String referenceObjectName_gpca_localB = "r394";
    private String referenceObjectName_gpca_localDW = "r398";

    //this number is very important it should be the same between the passed inputs into the spec that we think is an
    // output of the model and it must also be the same size as the list in contractOutput
    public static int wrapperOutputNum;

    Input freeInput = new Input();
    Input stateInput = new Input();

    //This is the state output of the class in the implementation.
    ContractOutput stateOutput = new ContractOutput();

    //This describes the output that is going to be validated with the specification, they are usually part of the
    // state but should NOT be mistaken as a stateOutput, a stateOutput are only those needed internally for R node
    // and are not validated by the spec, for those that needs to be  validated by the sepc we call the
    // contractOutput and must be populated there.
    ContractOutput contractOutput = new ContractOutput();

    boolean isOutputConverted = false;

    //carries any type conversion equation which can be triggered both in case of the input and the output
    ArrayList<Equation> typeConversionEq = new ArrayList<>();

    ArrayList<VarDecl> conversionLocalList = new ArrayList<>();

    public ArrayList<Equation> getTypeConversionEq() {
        return typeConversionEq;
    }

    public ArrayList<VarDecl> getConversionLocalList() {
        return conversionLocalList;
    }

    public boolean isOutputConverted() {
        return isOutputConverted;
    }

    // this is now here to determine the name of the symVar, since in the new SPF they have adapted a new naming for
    // symbolic variables for which we need a dynamic mechanism to get the right name. This can be handled in a
    // later stage.
    public static String setSymVarName() {
        if (Config.spec.equals("wbs"))
            Config.symVarName = "symVar_10_SYMINT";
        else if (Config.spec.equals("tcas"))
            Config.symVarName = "symVar_15_SYMINT";
        else if (Config.spec.equals("vote"))
            Config.symVarName = "symVar_6_SYMINT";
        else if (Config.spec.equals("gpca"))
            Config.symVarName = "symVar_217_SYMINT";
        else
            assert false;
        return Config.symVarName;
    }

    //* IMPORTANT!!! the order of variables of state input should match those  of variables of the state output!!*//
    public void discoverVars() {
        setSymVarName();

        if (Config.spec.equals("pad")) {
            discoverFreeInputPad();
            doFreeTypeConversion();

            discoverStateInputPad();
            doStateInputTypeConversion();

            discoverStateOutputPad();
            doStateOutputTypeConversion();

            discoverContractOutputPad();
            doContractOutputTypeConversion();

        } else if (Config.spec.equals("even")) {
            discoverFreeInputEven();
            doFreeTypeConversion();

            discoverStateInputEven();
            doStateInputTypeConversion();

            discoverStateOutputEven();
            doStateOutputTypeConversion();

            discoverContractOutputEven();
            doContractOutputTypeConversion();

        } else if (Config.spec.equals("wbs")) {
            discoverFreeInputWBS();
            doFreeTypeConversion();

            discoverStateInputWBS();
            doStateInputTypeConversion();

            discoverStateOutputWBS();
            doStateOutputTypeConversion();

            discoverContractOutputWBS();
            doContractOutputTypeConversion();
        } else if (Config.spec.equals("tcas")) {
            discoverFreeInputTCAS();
            doFreeTypeConversion();

            discoverStateInputTCAS();
            doStateInputTypeConversion();

            discoverStateOutputTCAS();
            doStateOutputTypeConversion();

            discoverContractOutputTCAS();
            doContractOutputTypeConversion();

        } else if (Config.spec.equals("gpca")) {
            discoverFreeInputGPCA();
            doFreeTypeConversion();

            discoverStateInputGPCA();
            doStateInputTypeConversion();

            discoverStateOutputGPCA();
            doStateOutputTypeConversion();

            discoverContractOutputGPCA();
            doContractOutputTypeConversion();

        } else if (Config.spec.equals("vote")) {
            discoverFreeInputVote();
            doFreeTypeConversion();

            discoverStateInputVote();
            doStateInputTypeConversion();

            discoverStateOutputVote();
            doStateOutputTypeConversion();

            discoverContractOutputVote();
            doContractOutputTypeConversion();

        } else if (Config.spec.equals("vote2")) {
            discoverFreeInputVote2();
            doFreeTypeConversion();

            discoverStateInputVote2();
            doStateInputTypeConversion();

            discoverStateOutputVote2();
            doStateOutputTypeConversion();

            discoverContractOutputVote2();
            doContractOutputTypeConversion();

        } else {
            System.out.println("unexpected spec to run.!");
            assert false;
        }
        wrapperOutputNum = contractOutput.size;

        checkAsserts();
    }

    private void checkAsserts() {
        assert contractOutput.varInitValuePair.size() == contractOutput.varList.size();
        assert stateOutput.varInitValuePair.size() == stateOutput.varList.size();
        assert freeInput.size > 0;
        assert wrapperOutputNum == contractOutput.size;

    }

    //================================= Type Conversion ========================

    private void doContractOutputTypeConversion() {
        if (contractOutput.containsBool()) { // isn't that replicated with the state output.
            ArrayList<Equation> conversionResult = contractOutput.convertOutput();
            assert conversionResult.size() == 1;
            typeConversionEq.addAll(conversionResult);
            isOutputConverted = true;
        }
    }

    private void doFreeTypeConversion() {
        if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }
    }

    private void doStateInputTypeConversion() {
        if (stateInput.containsBool()) { //type conversion to spf int type is needed
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = stateInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }
    }

    private void doStateOutputTypeConversion() {
        if (stateOutput.containsBool()) {
            ArrayList<Equation> conversionResult = stateOutput.convertOutput();
            typeConversionEq.addAll(conversionResult);
            //conversionLocalList.addAll(conversionResult.getFirst()); // no need to add this, since these are already as
            // def in the dynStmt
            isOutputConverted = true;
        }
    }

    //================================= end Type Conversion ========================


    //================================= Pad ========================
    //entered by hand for now -- this is a singleton, I need to enforce this everywhere.
    private void discoverContractOutputPad() {
        contractOutput.add(referenceObjectName + ".ignition_r.1.7.4", NamedType.BOOL);
        contractOutput.addInit(referenceObjectName + ".ignition_r.1.7.4", new BoolExpr(false));
    }


    //entered by hand for now
    private void discoverFreeInputPad() {
        freeInput.add("signal", NamedType.INT);
    }


    //entered by hand for now
    private void discoverStateInputPad() {
        stateInput.add("start_btn", NamedType.BOOL);
        stateInput.add("launch_btn", NamedType.BOOL);
        stateInput.add("reset_btn", NamedType.BOOL);
        stateInput.add("ignition", NamedType.BOOL);

    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputPad() {
        stateOutput.add(referenceObjectName + ".start_btn.1.15.4", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName + ".start_btn.1.15.4", new BoolExpr(false));

        stateOutput.add(referenceObjectName + ".launch_btn.1.17.4", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName + ".start_btn.1.15.4", new BoolExpr(false));

        stateOutput.add(referenceObjectName + ".reset_btn.1.9.4", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName + ".start_btn.1.15.4", new BoolExpr(false));

    }


    //====================== WBS ====================================

    //entered by hand for now - this defines the output that we expect to validate with the T_node,i.e, this is the
    // output of the wrapper that gets plugged in the T_node to  validate it. Therefore it is not directly reflecting
    // the method output of the implementation, instead it is the output of the to-be-created r_wrapper node.

    private void discoverContractOutputWBS() {

        contractOutput.add(referenceObjectName + ".Nor_Pressure.1.13.2", NamedType.INT);
        contractOutput.addInit(referenceObjectName + ".Nor_Pressure.1.13.2", new IntExpr(0));

        contractOutput.add(referenceObjectName + ".Alt_Pressure.1.13.2", NamedType.INT);
        contractOutput.addInit(referenceObjectName + ".Alt_Pressure.1.13.2", new IntExpr(0));

        contractOutput.add(referenceObjectName + ".Sys_Mode.1.5.2", NamedType.INT);
        contractOutput.addInit(referenceObjectName + ".Sys_Mode.1.5.2", new IntExpr(0));

    }

    //entered by hand for now
    private void discoverFreeInputWBS() {
        freeInput.add("pedal_1_SYMINT", NamedType.INT);
        freeInput.add("autoBrake_2_SYMINT", NamedType.BOOL);
        freeInput.add("skid_3_SYMINT", NamedType.BOOL);

        /*if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }
// IMP: order here is important. Firs the put the input variables for the state, then the state variables that account
// for the outputs of the sepc.
    //entered by hand for now
    private void discoverStateInputWBS() {
        stateInput.add("WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE_4_SYMINT", NamedType.INT);
        stateInput.add("WBS_Node_WBS_BSCU_rlt_PRE1_5_SYMINT", NamedType.INT);
        stateInput.add("WBS_Node_WBS_rlt_PRE2_6_SYMINT", NamedType.INT);

        stateInput.add("Nor_Pressure_7_SYMINT", NamedType.INT);
        stateInput.add("Alt_Pressure_8_SYMINT", NamedType.INT);
        stateInput.add("Sys_Mode_9_SYMINT", NamedType.INT);

    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputWBS() {

        stateOutput.add(referenceObjectName + ".WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE.1.3.2", NamedType.INT);
        stateOutput.addInit(referenceObjectName + ".WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE.1.3.2", new IntExpr(0));


        stateOutput.add(referenceObjectName + ".WBS_Node_WBS_BSCU_rlt_PRE1.1.3.2", NamedType.INT);
        stateOutput.addInit(referenceObjectName + ".WBS_Node_WBS_BSCU_rlt_PRE1.1.3.2", new IntExpr(0));


        stateOutput.add(referenceObjectName + ".WBS_Node_WBS_rlt_PRE2.1.3.2", NamedType.INT);
        stateOutput.addInit(referenceObjectName + ".WBS_Node_WBS_rlt_PRE2.1.3.2", new IntExpr(0));

    }


    //====================== TCAS ====================================

    //entered by hand for now - this defines the output that we expect to validate with the T_node,i.e, this is the
    // output of the wrapper that gets plugged in the T_node to  validate it. Therefore it is not directly reflecting
    // the method output of the implementation, instead it is the output of the to-be-created r_wrapper node.

    private void discoverContractOutputTCAS() {

        contractOutput.add("r-1.result_alt_sep_test.1.4.33", NamedType.INT);
        contractOutput.addInit("r-1.result_alt_sep_test.1.4.33", new IntExpr(0));

        contractOutput.add("r-1.alim_res.1.4.33", NamedType.INT);
        contractOutput.addInit("r-1.alim_res.1.4.33", new IntExpr(0));
    }


    //entered by hand for now
    private void discoverFreeInputTCAS() {
        freeInput.add("Cur_Vertical_Sep_1_SYMINT", NamedType.INT);
        freeInput.add("High_Confidence_flag_2_SYMINT", NamedType.INT);
        freeInput.add("Two_of_Three_Reports_Valid_flag_3_SYMINT", NamedType.INT);
        freeInput.add("Own_Tracked_Alt_4_SYMINT", NamedType.INT);
        freeInput.add("Own_Tracked_Alt_Rate_5_SYMINT", NamedType.INT);
        freeInput.add("Other_Tracked_Alt_6_SYMINT", NamedType.INT);
        freeInput.add("Alt_Layer_Value_7_SYMINT", NamedType.INT);
        freeInput.add("Up_Separation_8_SYMINT", NamedType.INT);
        freeInput.add("Down_Separation_9_SYMINT", NamedType.INT);
        freeInput.add("Other_RAC_10_SYMINT", NamedType.INT);
        freeInput.add("Other_Capability_11_SYMINT", NamedType.INT);
        freeInput.add("Climb_Inhibit_12_SYMINT", NamedType.INT);

        /*if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now
    private void discoverStateInputTCAS() {
/*
// constants -- not an input.
        stateInput.add("OLEV", NamedType.INT);
        stateInput.add("MAXALTDIFF", NamedType.INT);
        stateInput.add("MINSEP", NamedType.INT);
        stateInput.add("NOZCROSS", NamedType.INT);
        stateInput.add("NO_INTENT", NamedType.INT);
        stateInput.add("DO_NOT_CLIMB", NamedType.INT);
        stateInput.add("DO_NOT_DESCEND", NamedType.INT);
        stateInput.add("TCAS_TA", NamedType.INT);
        stateInput.add("OTHER", NamedType.INT);
*/

        stateInput.add("High_Confidence", NamedType.INT);
        stateInput.add("Two_of_Three_Reports_Valid", NamedType.INT);
        stateInput.add("Positive_RA_Alt_Thresh_0", NamedType.INT);
        stateInput.add("Positive_RA_Alt_Thresh_1", NamedType.INT);
        stateInput.add("Positive_RA_Alt_Thresh_2", NamedType.INT);
        stateInput.add("Positive_RA_Alt_Thresh_3", NamedType.INT);

        stateInput.add("result_alt_sep_test_13_SYMINT", NamedType.INT);
        stateInput.add("alim_res_14_SYMINT", NamedType.INT);
    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputTCAS() {

        //commenting these out even though they capture a side-effect and thus can be thought of as state output,
        // they are in fact the input in TCAS, thus we need not capture them.

        /*contractOutput.add("r-1.Cur_Vertical_Sep.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Cur_Vertical_Sep.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Own_Tracked_Alt.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Own_Tracked_Alt.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Own_Tracked_Alt_Rate.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Own_Tracked_Alt_Rate.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Other_Tracked_Alt.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Other_Tracked_Alt.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Alt_Layer_Value.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Alt_Layer_Value.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Up_Separation.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Up_Separation.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Down_Separation.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Down_Separation.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Other_RAC.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Other_RAC.1.3.32", new IntExpr(0));

        contractOutput.add("r-1.Other_Capability.1.3.32", NamedType.INT);
        contractOutput.addInit("r-1.Other_Capability.1.3.32", new IntExpr(0));
*/
        stateOutput.add("r-1.High_Confidence.1.5.33", NamedType.INT);
        stateOutput.addInit("r-1.High_Confidence.1.5.33", new IntExpr(0));

        stateOutput.add("r-1.Two_of_Three_Reports_Valid.1.5.33", NamedType.INT);
        stateOutput.addInit("r-1.Two_of_Three_Reports_Valid.1.5.33", new IntExpr(0));

        stateOutput.add("r-1.Positive_RA_Alt_Thresh_0.1.3.33", NamedType.INT);
        stateOutput.addInit("r-1.Positive_RA_Alt_Thresh_0.1.3.33", new IntExpr(0));

        stateOutput.add("r-1.Positive_RA_Alt_Thresh_1.1.3.33", NamedType.INT);
        stateOutput.addInit("r-1.Positive_RA_Alt_Thresh_1.1.3.33", new IntExpr(0));

        stateOutput.add("r-1.Positive_RA_Alt_Thresh_2.1.3.33", NamedType.INT);
        stateOutput.addInit("r-1.Positive_RA_Alt_Thresh_2.1.3.33", new IntExpr(0));

        stateOutput.add("r-1.Positive_RA_Alt_Thresh_3.1.3.33", NamedType.INT);
        stateOutput.addInit("r-1.Positive_RA_Alt_Thresh_3.1.3.33", new IntExpr(0));
    }

//====================== GPCA ====================================

    private void discoverContractOutputGPCA() {

        contractOutput.add(referenceObjectName_gpca_Alarm_Outputs + ".Is_Audio_Disabled.1.3.52", NamedType.INT);
        contractOutput.addInit(referenceObjectName_gpca_Alarm_Outputs + ".Is_Audio_Disabled.1.3.52", new IntExpr(0));

        contractOutput.add(referenceObjectName_gpca_Alarm_Outputs + ".Notification_Message.1.3.52", NamedType.INT);
        contractOutput.addInit(referenceObjectName_gpca_Alarm_Outputs + ".Notification_Message.1.3.52", new IntExpr(0));

        contractOutput.add(referenceObjectName_gpca_Alarm_Outputs + ".Audio_Notification_Command.1.1.52", NamedType.INT);
        contractOutput.addInit(referenceObjectName_gpca_Alarm_Outputs + ".Audio_Notification_Command.1.1.52", new IntExpr(0));

        contractOutput.add(referenceObjectName_gpca_Alarm_Outputs + ".Highest_Level_Alarm.1.3.52", NamedType.INT);
        contractOutput.addInit(referenceObjectName_gpca_Alarm_Outputs + ".Highest_Level_Alarm.1.3.52", new IntExpr(0));

        contractOutput.add(referenceObjectName_gpca_Alarm_Outputs + ".Log_Message_ID.1.3.52", NamedType.INT);
        contractOutput.addInit(referenceObjectName_gpca_Alarm_Outputs + ".Log_Message_ID.1.3.52", new IntExpr(0));

    }

    //entered by hand for now -- this is not completely matching the signature of Alarm_FunctionalSymWrapper because
    // some of the inputs are not used and therefore they do not show up in the linearized form.
    private void discoverFreeInputGPCA() {
        freeInput.add("Commanded_Flow_Rate_1_SYMINT", NamedType.INT);
        freeInput.add("Current_System_Mode_2_SYMINT", NamedType.INT);
        freeInput.add("System_On_6_SYMINT", NamedType.BOOL);
        freeInput.add("System_Monitor_Failed_9_SYMINT", NamedType.BOOL);
        freeInput.add("Logging_Failed_11_SYMINT", NamedType.BOOL);
        freeInput.add("Infusion_Initiate_14_SYMINT", NamedType.BOOL);
        freeInput.add("Disable_Audio_22_SYMINT", NamedType.INT);
        freeInput.add("Notification_Cancel_23_SYMINT", NamedType.BOOL);
        freeInput.add("VTBI_High_30_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_High_35_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_Low_36_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_37_SYMINT", NamedType.INT);
        freeInput.add("Flow_Rate_Not_Stable_38_SYMINT", NamedType.BOOL);
        freeInput.add("Air_In_Line_39_SYMINT", NamedType.BOOL);
        freeInput.add("Occlusion_40_SYMINT", NamedType.BOOL);
        freeInput.add("Door_Open_41_SYMINT", NamedType.BOOL);
        freeInput.add("Temp_42_SYMINT", NamedType.BOOL);
        freeInput.add("Air_Pressure_43_SYMINT", NamedType.BOOL);
        freeInput.add("Humidity_44_SYMINT", NamedType.BOOL);
        freeInput.add("Battery_Depleted_45_SYMINT", NamedType.BOOL);
        freeInput.add("Battery_Low_46_SYMINT", NamedType.BOOL);
        freeInput.add("Battery_Unable_To_Charge_47_SYMINT", NamedType.BOOL);
        freeInput.add("Supply_Voltage_48_SYMINT", NamedType.BOOL);
        freeInput.add("CPU_In_Error_49_SYMINT", NamedType.BOOL);
        freeInput.add("RTC_In_Error_50_SYMINT", NamedType.BOOL);
        freeInput.add("Watchdog_Interrupted_51_SYMINT", NamedType.BOOL);
        freeInput.add("Memory_Corrupted_52_SYMINT", NamedType.BOOL);
        freeInput.add("Pump_Too_Hot_53_SYMINT", NamedType.BOOL);
        freeInput.add("Pump_Overheated_54_SYMINT", NamedType.BOOL);
        freeInput.add("Audio_Enable_Duration_57_SYMINT", NamedType.INT);
        freeInput.add("Audio_Level_58_SYMINT", NamedType.INT);
        freeInput.add("Config_Warning_Duration_59_SYMINT", NamedType.INT);
        freeInput.add("Low_Reservoir_61_SYMINT", NamedType.INT);
        freeInput.add("Max_Duration_Over_Infusion_63_SYMINT", NamedType.INT);
        freeInput.add("Max_Duration_Under_Infusion_64_SYMINT", NamedType.INT);
        freeInput.add("Max_Paused_Duration_65_SYMINT", NamedType.INT);
        freeInput.add("Max_Idle_Duration_66_SYMINT", NamedType.INT);
        freeInput.add("Tolerance_Max_67_SYMINT", NamedType.INT);
        freeInput.add("Tolerance_Min_68_SYMINT", NamedType.INT);
        freeInput.add("Reservoir_Empty_73_SYMINT", NamedType.BOOL);
        freeInput.add("Reservoir_Volume1_74_SYMINT", NamedType.INT);
        freeInput.add("Volume_Infused_75_SYMINT", NamedType.INT);
        freeInput.add("In_Therapy_77_SYMINT", NamedType.BOOL);
        freeInput.add("Config_Timer_101_SYMINT", NamedType.INT);

        /*if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now
    private void discoverStateInputGPCA() {

        //localB
        stateInput.add("localB_Commanded_Flow_Rate_108_SYMINT", NamedType.INT);
        stateInput.add("localB_Current_System_Mode_109_SYMINT", NamedType.INT);
        stateInput.add("localB_Disable_Audio_110_SYMINT", NamedType.INT);
        stateInput.add("localB_VTBI_High_111_SYMINT", NamedType.INT);
        stateInput.add("localB_Flow_Rate_High_112_SYMINT", NamedType.INT);
        stateInput.add("localB_Flow_Rate_Low_113_SYMINT", NamedType.INT);
        stateInput.add("localB_Flow_Rate_114_SYMINT", NamedType.INT);
        stateInput.add("localB_Flow_Rate_Not_Stable_138_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Audio_Enable_Duration_115_SYMINT", NamedType.INT);
        stateInput.add("localB_Config_Warning_Duration_117_SYMINT", NamedType.INT);
        stateInput.add("localB_Low_Reservoir_118_SYMINT", NamedType.INT);
        stateInput.add("localB_Max_Duration_Over_Infusion_119_SYMINT", NamedType.INT);
        stateInput.add("localB_Max_Duration_Under_Infusion_120_SYMINT", NamedType.INT);
        stateInput.add("localB_Max_Paused_Duration_121_SYMINT", NamedType.INT);
        stateInput.add("localB_Max_Idle_Duration_122_SYMINT", NamedType.INT);
        stateInput.add("localB_Tolerance_Max_123_SYMINT", NamedType.INT);
        stateInput.add("localB_Tolerance_Min_124_SYMINT", NamedType.INT);
        stateInput.add("localB_Reservoir_Volume_125_SYMINT", NamedType.INT);
        stateInput.add("localB_Volume_Infused_126_SYMINT", NamedType.INT);
        stateInput.add("localB_Config_Timer_127_SYMINT", NamedType.INT);
        stateInput.add("localB_ALARM_OUT_Display_Audio_Disabled_Indicator_128_SYMINT", NamedType.INT);
        stateInput.add("localB_ALARM_OUT_Display_Notification_Command_129_SYMINT", NamedType.INT);
        stateInput.add("localB_ALARM_OUT_Audio_Notification_Command_130_SYMINT", NamedType.INT);
        stateInput.add("localB_ALARM_OUT_Highest_Level_Alarm_131_SYMINT", NamedType.INT);
        stateInput.add("localB_ALARM_OUT_Log_Message_ID_132_SYMINT", NamedType.INT);
        stateInput.add("localB_System_On_133_SYMINT", NamedType.BOOL);
        stateInput.add("localB_System_Monitor_Failed_134_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Logging_Failed_135_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Infusion_Initiate_136_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Notification_Cancel_137_SYMINT", NamedType.BOOL);

        stateInput.add("localB_Air_In_Line_139_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Occlusion_140_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Door_Open_141_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Temp_142_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Air_Pressure_143_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Humidity_144_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Battery_Depleted_145_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Battery_Low_146_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Battery_Unable_To_Charge_147_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Supply_Voltage_148_SYMINT", NamedType.BOOL);
        stateInput.add("localB_CPU_In_Error_149_SYMINT", NamedType.BOOL);
        stateInput.add("localB_RTC_In_Error_150_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Watchdog_Interrupted_151_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Memory_Corrupted_152_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Pump_Too_Hot_153_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Pump_Overheated_154_SYMINT", NamedType.BOOL);
        stateInput.add("localB_Reservoir_Empty_155_SYMINT", NamedType.BOOL);
        stateInput.add("localB_In_Therapy_156_SYMINT", NamedType.BOOL);

//localDW
        stateInput.add("is_active_c2_ALARM_Functional_157_SYMINT", NamedType.INT);
        stateInput.add("is_c2_ALARM_Functional_158_SYMINT", NamedType.INT);
        stateInput.add("is_active_Notification_159_SYMINT", NamedType.INT);
        stateInput.add("is_Visual_160_SYMINT", NamedType.INT);
        stateInput.add("is_active_Visual_161_SYMINT", NamedType.INT);
        stateInput.add("is_Audio_162_SYMINT", NamedType.INT);
        stateInput.add("is_active_Audio_163_SYMINT", NamedType.INT);
        stateInput.add("is_active_CheckAlarm_164_SYMINT", NamedType.INT);

        //here
        stateInput.add("is_CancelAlarm_165_SYMINT", NamedType.INT);
        stateInput.add("is_active_CancelAlarm_166_SYMINT", NamedType.INT);
        stateInput.add("is_active_SetAlarmStatus_167_SYMINT", NamedType.INT);
        stateInput.add("is_active_Level4_168_SYMINT", NamedType.INT);
        stateInput.add("is_IsEmptyReservoir_169_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsEmptyReservoir_170_SYMINT", NamedType.INT);
        stateInput.add("is_IsSystemMonitorFailed_171_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsSystemMonitorFailed_172_SYMINT", NamedType.INT);
        stateInput.add("is_IsEnviromentalError_173_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsEnviromentalError_174_SYMINT", NamedType.INT);
        stateInput.add("is_active_Level3_175_SYMINT", NamedType.INT);
        stateInput.add("is_IsOverInfusionFlowRate_176_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsOverInfusionFlowRate_177_SYMINT", NamedType.INT);
        stateInput.add("is_InfusionNotStartedWarning_178_SYMINT", NamedType.INT);
        stateInput.add("is_active_InfusionNotStartedWarning_179_SYMINT", NamedType.INT);
        stateInput.add("is_IsOverInfusionVTBI_180_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsOverInfusionVTBI_181_SYMINT", NamedType.INT);
        stateInput.add("is_IsAirInLine_182_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsAirInLine_183_SYMINT", NamedType.INT);
        stateInput.add("is_IsOcclusion_184_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsOcclusion_185_SYMINT", NamedType.INT);
        stateInput.add("is_IsDoorOpen_186_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsDoorOpen_187_SYMINT", NamedType.INT);
        stateInput.add("is_active_Level2_188_SYMINT", NamedType.INT);
        stateInput.add("is_IsLowReservoir_189_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsLowReservoir_190_SYMINT", NamedType.INT);
        stateInput.add("is_active_Level1_191_SYMINT", NamedType.INT);
        stateInput.add("is_IsUnderInfusion_192_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsUnderInfusion_193_SYMINT", NamedType.INT);
        stateInput.add("is_IsFlowRateNotStable_194_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsFlowRateNotStable_195_SYMINT", NamedType.INT);
        stateInput.add("is_IsIdleTimeExceeded_196_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsIdleTimeExceeded_197_SYMINT", NamedType.INT);
        stateInput.add("is_IsPausedTimeExceeded_198_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsPausedTimeExceeded_199_SYMINT", NamedType.INT);
        stateInput.add("is_IsConfigTimeWarning_200_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsConfigTimeWarning_201_SYMINT", NamedType.INT);
        stateInput.add("is_IsBatteryError_202_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsBatteryError_203_SYMINT", NamedType.INT);
        stateInput.add("is_IsPumpHot_204_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsPumpHot_205_SYMINT", NamedType.INT);
        stateInput.add("is_IsLoggingFailed_206_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsLoggingFailed_207_SYMINT", NamedType.INT);
        stateInput.add("is_IsHardwareError_208_SYMINT", NamedType.INT);
        stateInput.add("is_active_IsHardwareError_209_SYMINT", NamedType.INT);
        stateInput.add("overInfusionTimer_210_SYMINT", NamedType.INT);
        stateInput.add("underInfusionTimer_211_SYMINT", NamedType.INT);
        stateInput.add("currentAlarm_212_SYMINT", NamedType.INT);
        stateInput.add("audioTimer_213_SYMINT", NamedType.INT);
        stateInput.add("cancelAlarm_214_SYMINT", NamedType.INT);
        stateInput.add("idletimer_215_SYMINT", NamedType.INT);
        stateInput.add("pausedtimer_216_SYMINT", NamedType.INT);

        stateInput.add("Is_Audio_Disabled_103_SYMINT", NamedType.INT);
        stateInput.add("Notification_Message_104_SYMINT", NamedType.INT);
        stateInput.add("Audio_Notification_Command_105_SYMINT", NamedType.INT);
        stateInput.add("Highest_Level_Alarm_106_SYMINT", NamedType.INT);
        stateInput.add("Log_Message_ID5_107_SYMINT", NamedType.INT);

    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputGPCA() {

        //localB
        stateOutput.add(referenceObjectName_gpca_localB + ".Commanded_Flow_Rate.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Commanded_Flow_Rate.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Current_System_Mode.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Current_System_Mode.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Disable_Audio.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Disable_Audio.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".VTBI_High.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".VTBI_High.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Flow_Rate_High.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Flow_Rate_High.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Flow_Rate_Low.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Flow_Rate_Low.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Flow_Rate_Not_Stable.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Flow_Rate_Not_Stable.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Audio_Enable_Duration.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Audio_Enable_Duration.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Config_Warning_Duration.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Config_Warning_Duration.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Low_Reservoir.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Low_Reservoir.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Max_Duration_Over_Infusion.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Max_Duration_Over_Infusion.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Max_Duration_Under_Infusion.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Max_Duration_Under_Infusion.1.3.52",
                new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Max_Paused_Duration.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Max_Paused_Duration.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Max_Idle_Duration.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Max_Idle_Duration.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Tolerance_Max.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Tolerance_Max.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Tolerance_Min.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Tolerance_Min.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Reservoir_Volume.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Reservoir_Volume.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Volume_Infused.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Volume_Infused.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".Config_Timer.1.3.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Config_Timer.1.3.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".ALARM_OUT_Display_Audio_Disabled_Indicator.1.12.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".ALARM_OUT_Display_Audio_Disabled_Indicator.1" +
                ".12.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".ALARM_OUT_Display_Notification_Command.1.24.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".ALARM_OUT_Display_Notification_Command.1.24" +
                ".52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".ALARM_OUT_Audio_Notification_Command.1.61.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".ALARM_OUT_Audio_Notification_Command.1.61.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".ALARM_OUT_Highest_Level_Alarm.1.12.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".ALARM_OUT_Highest_Level_Alarm.1.12.52",
                new IntExpr(0));


        stateOutput.add(referenceObjectName_gpca_localB + ".ALARM_OUT_Log_Message_ID.1.10.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".ALARM_OUT_Log_Message_ID.1.10.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localB + ".System_On.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".System_On.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".System_Monitor_Failed.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".System_Monitor_Failed.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Logging_Failed.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Logging_Failed.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Infusion_Initiate.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Infusion_Initiate.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Notification_Cancel.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Notification_Cancel.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Flow_Rate_Not_Stable.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Flow_Rate_Not_Stable.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Air_In_Line.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Air_In_Line.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Occlusion.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Occlusion.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Door_Open.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Door_Open.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Temp.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Temp.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Air_Pressure.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Air_Pressure.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Humidity.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Humidity.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Battery_Depleted.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Battery_Depleted.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Battery_Low.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Battery_Low.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Battery_Unable_To_Charge.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Battery_Unable_To_Charge.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Supply_Voltage.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Supply_Voltage.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".CPU_In_Error.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".CPU_In_Error.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".RTC_In_Error.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".RTC_In_Error.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Watchdog_Interrupted.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Watchdog_Interrupted.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Memory_Corrupted.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Memory_Corrupted.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Pump_Too_Hot.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Pump_Too_Hot.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Pump_Overheated.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Pump_Overheated.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".Reservoir_Empty.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".Reservoir_Empty.1.3.52", new BoolExpr(false));

        stateOutput.add(referenceObjectName_gpca_localB + ".In_Therapy.1.3.52", NamedType.BOOL);
        stateOutput.addInit(referenceObjectName_gpca_localB + ".In_Therapy.1.3.52", new BoolExpr(false));


//localDW
        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_c2_ALARM_Functional.1.5.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_c2_ALARM_Functional.1.5.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_c2_ALARM_Functional.1.12.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_c2_ALARM_Functional.1.12.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Notification.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Notification.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_Visual.1.24.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_Visual.1.24.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Visual.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Visual.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_Audio.1.59.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_Audio.1.59.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Audio.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Audio.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_CheckAlarm.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_CheckAlarm.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_CancelAlarm.1.16.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_CancelAlarm.1.16.52", new IntExpr(0));


        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_CancelAlarm.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_CancelAlarm.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_SetAlarmStatus.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_SetAlarmStatus.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Level4.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Level4.1.11.522", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsEmptyReservoir.1.24.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsEmptyReservoir.1.24.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsEmptyReservoir.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsEmptyReservoir.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsSystemMonitorFailed.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsSystemMonitorFailed.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsSystemMonitorFailed.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsSystemMonitorFailed.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsEnviromentalError_173_SYMINT", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsEnviromentalError_173_SYMINT", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsEnviromentalError.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsEnviromentalError.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Level3.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Level3.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsOverInfusionFlowRate.1.39.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsOverInfusionFlowRate.1.39.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsOverInfusionFlowRate.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsOverInfusionFlowRate.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_InfusionNotStartedWarning.1.22.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_InfusionNotStartedWarning.1.22.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_InfusionNotStartedWarning.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".ALARM_OUT_Log_Message_ID.1.10.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsOverInfusionVTBI.1.23.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsOverInfusionVTBI.1.23.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsOverInfusionVTBI.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsOverInfusionVTBI.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsAirInLine.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsAirInLine.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsAirInLine.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsAirInLine.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsOcclusion.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsOcclusion.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsOcclusion.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsOcclusion.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsDoorOpen.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsDoorOpen.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsDoorOpen.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsDoorOpen.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Level2.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Level2.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsLowReservoir.1.22.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsLowReservoir.1.22.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsLowReservoir.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsLowReservoir.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_Level1.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_Level1.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsUnderInfusion.1.33.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsUnderInfusion.1.33.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsUnderInfusion.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsUnderInfusion.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsFlowRateNotStable.1.22.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsFlowRateNotStable.1.22.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsFlowRateNotStable.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsFlowRateNotStable.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsIdleTimeExceeded.1.33.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsIdleTimeExceeded.1.33.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsIdleTimeExceeded.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsIdleTimeExceeded.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsPausedTimeExceeded.1.33.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsPausedTimeExceeded.1.33.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsPausedTimeExceeded.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsPausedTimeExceeded.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsConfigTimeWarning.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsConfigTimeWarning.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsConfigTimeWarning.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsConfigTimeWarning.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsBatteryError.1.23.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsBatteryError.1.23.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsBatteryError.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsBatteryError.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsPumpHot.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsPumpHot.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsPumpHot.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsPumpHot.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsLoggingFailed.1.21.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsLoggingFailed.1.21.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsLoggingFailed.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_active_IsLoggingFailed.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_IsHardwareError.1.26.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".is_IsHardwareError.1.26.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".is_active_IsHardwareError.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + "is_active_IsHardwareError.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".overInfusionTimer.1.17.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".overInfusionTimer.1.17.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".underInfusionTimer.1.17.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".underInfusionTimer.1.17.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".currentAlarm.1.12.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".currentAlarm.1.12.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".audioTimer.1.34.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".audioTimer.1.34.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".cancelAlarm.1.11.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".cancelAlarm.1.11.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".idletimer.1.35.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".idletimer.1.35.52", new IntExpr(0));

        stateOutput.add(referenceObjectName_gpca_localDW + ".pausedtimer.1.35.52", NamedType.INT);
        stateOutput.addInit(referenceObjectName_gpca_localDW + ".pausedtimer.1.35.52", new IntExpr(0));

    }


    //=========================== Even ============================
    //entered by hand for now

    private void discoverContractOutputEven() {
        contractOutput.add(referenceObjectName + ".output.1.5.2", NamedType.INT);
        contractOutput.addInit(referenceObjectName + ".output.1.5.2", new IntExpr(8));
    }

    //entered by hand for now
    private void discoverFreeInputEven() {
        freeInput.add("signal", NamedType.BOOL);
        /*if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now
    private void discoverStateInputEven() {
        stateInput.add("countState", NamedType.INT);
        stateInput.add("output", NamedType.INT);
    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputEven() {
        stateOutput.add(referenceObjectName + ".countState.1.5.2", NamedType.INT);
        stateOutput.addInit(referenceObjectName + ".countState.1.5.2", new IntExpr(0));

    }


    //=========================== Vote ===========================

    private void discoverContractOutputVote() {

        contractOutput.add(referenceObjectName + ".out.1.3.2", NamedType.BOOL);
        contractOutput.addInit(referenceObjectName + ".out.1.3.2", new BoolExpr(false));
        /*if (contractOutput.containsBool()) { // isn't that replicated with the state output.
            ArrayList<Equation> conversionResult = contractOutput.convertOutput();
            assert conversionResult.size() == 1;
            typeConversionEq.addAll(conversionResult);
            isOutputConverted = true;
        }*/
    }

    //entered by hand for now
    private void discoverFreeInputVote() {
        freeInput.add("a_1_SYMINT", NamedType.BOOL);
        freeInput.add("b_2_SYMINT", NamedType.BOOL);
        freeInput.add("c_3_SYMINT", NamedType.BOOL);
        freeInput.add("threshold_4_SYMINT", NamedType.INT);

        /*if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now
    private void discoverStateInputVote() {
        stateInput.add("out_5_SYMINT", NamedType.BOOL);
        /*if (stateInput.containsBool()) { //type conversion to spf int type is needed
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = stateInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputVote() {
    }


    //=========================== Vote2 ===========================

    private void discoverContractOutputVote2() {

        contractOutput.add(referenceObjectName + "r347.out.1.1.2", NamedType.BOOL);
        contractOutput.addInit(referenceObjectName + "r347.out.1.1.2", new BoolExpr(false));
        /*if (contractOutput.containsBool()) { // isn't that replicated with the state output.
            ArrayList<Equation> conversionResult = contractOutput.convertOutput();
            assert conversionResult.size() == 1;
            typeConversionEq.addAll(conversionResult);
            isOutputConverted = true;
        }*/
    }

    //entered by hand for now
    private void discoverFreeInputVote2() {
        freeInput.add("a", NamedType.INT);
        freeInput.add("b", NamedType.INT);
        freeInput.add("c", NamedType.INT);
        freeInput.add("threshold", NamedType.INT);

        /*if (freeInput.containsBool()) {
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = freeInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now
    private void discoverStateInputVote2() {
        stateInput.add("out", NamedType.BOOL);
        /*if (stateInput.containsBool()) { //type conversion to spf int type is needed
            Pair<ArrayList<VarDecl>, ArrayList<Equation>> conversionResult = stateInput.convertInput();
            typeConversionEq.addAll(conversionResult.getSecond());
            conversionLocalList.addAll(conversionResult.getFirst());
        }*/
    }

    //entered by hand for now - order is important, needs to match in order of the input
    private void discoverStateOutputVote2() {
    }


    public ArrayList<VarDecl> generateInputDecl() {
        ArrayList<VarDecl> inputDeclList = generateFreeInputDecl();
        inputDeclList.addAll(generateStateInputDecl());
        return inputDeclList;
    }

    public ArrayList<VarDecl> generateFreeInputDecl() {
        return generateLustreDecl(freeInput);
    }

    public ArrayList<VarDecl> generateStateInputDecl() {
        return generateLustreDecl(stateInput);
    }

    private ArrayList<VarDecl> generateLustreDecl(SpecInputOutput inputOutput) {
        return inputOutput.generateVarDecl();
    }

    public ArrayList<VarDecl> generaterContractOutDeclList() {
        return contractOutput.generateVarDecl();
    }

    public ArrayList<VarDecl> generateOutputDecl() {
        return stateOutput.generateVarDecl();
    }

    /**
     * searches in all in input and output arrays to check if it is one in them
     *
     * @param s
     * @return
     */
    public boolean isInOutVar(String s, NamedType type) {
        return isFreeInVar(s, type) || isStateInVar(s, type) || isStateOutVar(s, type) || isContractOutputVar(s, type);
    }


    public boolean isFreeInVar(String varName, NamedType type) {
        return freeInput.contains(varName, type);
    }

    public boolean isStateInVar(String varName, NamedType type) {
        return stateInput.contains(varName, type);
    }

    public boolean isStateOutVar(String varName, NamedType type) {
        return stateOutput.contains(varName, type);
    }

    public boolean isContractOutputVar(String varName, NamedType type) {
        return contractOutput.contains(varName, type);
    }

    public boolean isContractOutputStr(String name) {
        return contractOutput.hasName(name);
    }

    public boolean isStateOutVar(String name) {
        return stateOutput.hasName(name);
    }

    public Pair<VarDecl, Equation> replicateContractOutput(String outVarName) {
        return contractOutput.replicateMe(outVarName);
    }

    public NamedType getContractOutType() {
        if (contractOutput.varList.size() == 0) {
            System.out.println("Contract has no output, this is unexpected signature for contract R! Aborting!");
            assert false;
        }
        return contractOutput.varList.get(0).getSecond();
    }

    //gets the initial value of a wrapper output.
    public Expr getContractOutputInit(String name) {
        return contractOutput.getReturnInitVal(name);
    }

    public Expr getStateOutInit(String name) {
        return stateOutput.getReturnInitVal(name);
    }

    public int getContractOutputCount() {
        return contractOutput.size;
    }
}
