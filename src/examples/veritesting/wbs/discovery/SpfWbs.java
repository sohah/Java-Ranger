package veritesting.wbs.discovery;/*
 * Copyright (C) 2014, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 * All rights reserved.
 *
 * Symbolic Pathfinder (jpf-symbc) is licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class SpfWbs {

    //Internal state
    private int WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;
    private int WBS_Node_WBS_BSCU_rlt_PRE1;
    private int WBS_Node_WBS_rlt_PRE2;
//	boolean WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6; //10

    //Outputs
    private int Nor_Pressure;
    private int Alt_Pressure;
    private int Sys_Mode;

    @Override
    public boolean equals(Object obj) {
        if (SpfWbs.class.isInstance(obj)) {
            SpfWbs o = (SpfWbs) obj;
            return (Nor_Pressure == o.Nor_Pressure) && (Alt_Pressure == o.Alt_Pressure) && (Sys_Mode == o.Sys_Mode) &&
                    (WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE == o.WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE) &&
                    (WBS_Node_WBS_BSCU_rlt_PRE1 == o.WBS_Node_WBS_BSCU_rlt_PRE1) &&
                    (WBS_Node_WBS_rlt_PRE2 == o.WBS_Node_WBS_rlt_PRE2);// &&
//					(WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6 == o.WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6);
        }
        return false;
    }

    public SpfWbs() {
        WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = 0;
        WBS_Node_WBS_BSCU_rlt_PRE1 = 0;
        WBS_Node_WBS_rlt_PRE2 = 100;
        Nor_Pressure = 0;
        Alt_Pressure = 0;
        Sys_Mode = 0;
    }

    public void update(int PedalPos, boolean AutoBrake,
                       boolean Skid) {
        int WBS_Node_WBS_AS_MeterValve_Switch; //4
        int WBS_Node_WBS_AccumulatorValve_Switch; //5
        int WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch; //6
        boolean WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator; //7
        int WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1; //8
        int WBS_Node_WBS_BSCU_Command_Switch; //9
        boolean WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6; //10
        int WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay; //11
        int WBS_Node_WBS_BSCU_Switch2; //12
        int WBS_Node_WBS_BSCU_Switch3; //13
        int WBS_Node_WBS_BSCU_Unit_Delay1; //14
        int WBS_Node_WBS_Green_Pump_IsolationValve_Switch; //15
        int WBS_Node_WBS_SelectorValve_Switch; //16
        int WBS_Node_WBS_SelectorValve_Switch1; //17
        int WBS_Node_WBS_Unit_Delay2; //18

        WBS_Node_WBS_Unit_Delay2 = WBS_Node_WBS_rlt_PRE2;
        WBS_Node_WBS_BSCU_Unit_Delay1 = WBS_Node_WBS_BSCU_rlt_PRE1;
        WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay = WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE;

        WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator = (WBS_Node_WBS_BSCU_SystemModeSelCmd_Unit_Delay == 0);

        if ((PedalPos == 0)) {
            WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 0;
        } else {
            if ((PedalPos == 1)) {
                WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 1;
            } else {
                if ((PedalPos == 2)) {
                    WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 2;
                } else {
                    if ((PedalPos == 3)) {
                        WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 3;
                    } else {
                        if ((PedalPos == 4)) {
                            WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 4;
                        } else {
                            WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 = 0;
                        }
                    }
                }
            }
        }
        //Debug.printPC("PC @fter(PadalPos == 0) [Region 1] = ");


        if ((AutoBrake &&
                WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator)) {
            WBS_Node_WBS_BSCU_Command_Switch = 1;
        } else {
            WBS_Node_WBS_BSCU_Command_Switch = 0;
        }
        //Debug.printPC("PC @fter(AutoBrake && WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator) [Region 2] = ");


        WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6 = ((((!(WBS_Node_WBS_BSCU_Unit_Delay1 == 0)) &&
                (WBS_Node_WBS_Unit_Delay2 <= 0)) &&
                WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator) ||
                (!WBS_Node_WBS_BSCU_Command_Is_Normal_Relational_Operator));

        //	Debug.printPC("PC @fter(WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6 = ((((!(WBS_Node_WBS_BSCU_Unit_Delay1 == 0)) &&) = ");

        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            if (Skid)
                WBS_Node_WBS_BSCU_Switch3 = 0;
            else
                WBS_Node_WBS_BSCU_Switch3 = 4;
        } else {
            WBS_Node_WBS_BSCU_Switch3 = 4;
        }

        //	Debug.printPC("PC @fter((WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6)) = ");


        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            WBS_Node_WBS_Green_Pump_IsolationValve_Switch = 0;
        } else {
            WBS_Node_WBS_Green_Pump_IsolationValve_Switch = 5;
        }

        //	Debug.printPC("PC @fter(WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) = ");

        if ((WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1)) {
            WBS_Node_WBS_SelectorValve_Switch1 = 0;
        } else {
            WBS_Node_WBS_SelectorValve_Switch1 = 5;
        }

        //	Debug.printPC("PC @fter(WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1) = ");

        if ((!WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6)) {
            WBS_Node_WBS_AccumulatorValve_Switch = 0;
        } else {
            if ((WBS_Node_WBS_SelectorValve_Switch1 >= 1)) {
                WBS_Node_WBS_AccumulatorValve_Switch = WBS_Node_WBS_SelectorValve_Switch1;
            } else {
                WBS_Node_WBS_AccumulatorValve_Switch = 5;
            }
        }

        //	Debug.printPC("PC @fter(!WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) = ");


        if ((WBS_Node_WBS_BSCU_Switch3 == 0)) {
            WBS_Node_WBS_AS_MeterValve_Switch = 0;
        } else {
            if ((WBS_Node_WBS_BSCU_Switch3 == 1)) {
                WBS_Node_WBS_AS_MeterValve_Switch = (WBS_Node_WBS_AccumulatorValve_Switch / 4);
            } else {
                if ((WBS_Node_WBS_BSCU_Switch3 == 2)) {
                    WBS_Node_WBS_AS_MeterValve_Switch = (WBS_Node_WBS_AccumulatorValve_Switch / 2);
                } else {
                    if ((WBS_Node_WBS_BSCU_Switch3 == 3)) {
                        WBS_Node_WBS_AS_MeterValve_Switch = ((WBS_Node_WBS_AccumulatorValve_Switch / 4) * 3);
                    } else {
                        if ((WBS_Node_WBS_BSCU_Switch3 == 4)) {
                            WBS_Node_WBS_AS_MeterValve_Switch = WBS_Node_WBS_AccumulatorValve_Switch;
                        } else {
                            WBS_Node_WBS_AS_MeterValve_Switch = 0;
                        }
                    }
                }
            }
        }
        //	Debug.printPC("PC @fter(WBS_Node_WBS_BSCU_Switch3 == 0) = ");


        if (Skid) {
            WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch = 0;
        } else {
            WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch = (WBS_Node_WBS_BSCU_Command_Switch + WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1);
        }
        //Debug.printPC("PC @fter(Skid) [Region 3] = ");


        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            Sys_Mode = 1;
        } else {
            Sys_Mode = 0;
        }
        //	Debug.printPC("PC @fter(WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) = ");


        if (WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) {
            WBS_Node_WBS_BSCU_Switch2 = 0;
        } else {
            if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 0) &&
                    (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 1))) {
                WBS_Node_WBS_BSCU_Switch2 = 0;
            } else {
                if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 1) &&
                        (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 2))) {
                    WBS_Node_WBS_BSCU_Switch2 = 1;
                } else {
                    if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 2) &&
                            (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 3))) {
                        WBS_Node_WBS_BSCU_Switch2 = 2;
                    } else {
                        if (((WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch >= 3) &&
                                (WBS_Node_WBS_BSCU_Command_AntiSkidCommand_Normal_Switch < 4))) {
                            WBS_Node_WBS_BSCU_Switch2 = 3;
                        } else {
                            WBS_Node_WBS_BSCU_Switch2 = 4;
                        }
                    }
                }
            }
        }
        //Debug.printPC("PC @fter(WBS_Node_WBS_BSCU_SystemModeSelCmd_Logical_Operator6) = ");


        if ((WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1)) {
            WBS_Node_WBS_SelectorValve_Switch = WBS_Node_WBS_Green_Pump_IsolationValve_Switch;
        } else {
            WBS_Node_WBS_SelectorValve_Switch = 0;
        }
        //	Debug.printPC("PC @fter(WBS_Node_WBS_Green_Pump_IsolationValve_Switch >= 1) = ");


        if ((WBS_Node_WBS_BSCU_Switch2 == 0)) {
            Nor_Pressure = 0;
        } else {
            if ((WBS_Node_WBS_BSCU_Switch2 == 1)) {
                Nor_Pressure = (WBS_Node_WBS_SelectorValve_Switch / 4);
            } else {
                if ((WBS_Node_WBS_BSCU_Switch2 == 2)) {
                    Nor_Pressure = (WBS_Node_WBS_SelectorValve_Switch / 2);
                } else {
                    if ((WBS_Node_WBS_BSCU_Switch2 == 3)) {
                        Nor_Pressure = ((WBS_Node_WBS_SelectorValve_Switch / 4) * 3);
                    } else {
                        if ((WBS_Node_WBS_BSCU_Switch2 == 4)) {
                            Nor_Pressure = WBS_Node_WBS_SelectorValve_Switch;
                        } else {
                            Nor_Pressure = 0;
                        }
                    }
                }
            }
        }

        //	Debug.printPC("PC before(WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 0) [region 4] = ");

        if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 0)) {
            Alt_Pressure = 0;
        } else {
            if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 1)) {
                Alt_Pressure = (WBS_Node_WBS_AS_MeterValve_Switch / 4);
            } else {
                if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 2)) {
                    Alt_Pressure = (WBS_Node_WBS_AS_MeterValve_Switch / 2);
                } else {
                    if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 3)) {
                        Alt_Pressure = ((WBS_Node_WBS_AS_MeterValve_Switch / 4) * 3);
                    } else {
                        if ((WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 4)) {
                            Alt_Pressure = WBS_Node_WBS_AS_MeterValve_Switch;
                        } else {
                            Alt_Pressure = 0;
                        }
                    }
                }
            }
        }
        //	Debug.printPC("PC @fter(WBS_Node_WBS_BSCU_Command_PedalCommand_Switch1 == 0) [Region 4] = ");


        WBS_Node_WBS_rlt_PRE2 = Nor_Pressure;

        WBS_Node_WBS_BSCU_rlt_PRE1 = WBS_Node_WBS_BSCU_Switch2;

        WBS_Node_WBS_BSCU_SystemModeSelCmd_rlt_PRE = Sys_Mode;


        //assert ((Alt_Pressure == 0)); //SPF passing
        //assert (Sys_Mode == 0); //SPF passing
        //assert (Nor_Pressure == 0); //SPF failing

        /************ SH edits starts here **********/
        //assertion (1) -- passing assertion
        //assert((PedalPos > 0 && PedalPos <= 4 && !Skid) ? (Alt_Pressure > 0 || Nor_Pressure > 0) : true);

        //assertion(2) -- failing assertion
        //assert((PedalPos > 0 && PedalPos <= 4 && !Skid) ? (Alt_Pressure > 0) : true);

        //assertion(3) -- passing assertion.
        //System.out.println("Nor_Pressure = " + Debug.isSymbolicInteger(Nor_Pressure));
        //assert ((PedalPos > 0 && PedalPos <= 4 && !Skid) ? (Nor_Pressure > 0) : true);

        //assertion(4) -- failing assertion.
        //assert((PedalPos > 0 && PedalPos <= 4) ? (Alt_Pressure > 0 || Nor_Pressure > 0) : true);


        //assertion(5) -- failing assertion
        //assert((PedalPos > 0 && !Skid) ? (Alt_Pressure > 0 || Nor_Pressure > 0) : true);


        /************ SH edits ends here **********/


        /************ SH edits starts here **********/

        //assertion (1) -- passing assertion -- repairing its negation.
        //assert (!((Nor_Pressure <= 14) && (Sys_Mode == 13))); //initial repair
        //assert (! ((Nor_Pressure == -1) || (Sys_Mode == 13))); //tight(1)
        //assert (!((Nor_Pressure == -1) || (Sys_Mode > 12))); //tight(2)
        //assert (! ((Nor_Pressure <= -1) || (Sys_Mode > 8))); //tight(3)
        //assert (! ((Nor_Pressure <= -1) || (Sys_Mode > 3)));//tight(4)
        //assert ((Sys_Mode == 0) && ((Nor_Pressure <= -1) ^ (Sys_Mode <= 14)));//tight(5)

        //assertion(2)
        //assert (((Nor_Pressure == 5) || (Sys_Mode == 0)) || ((Nor_Pressure == -9) && Skid)); //tight(1)
        //assert ((Nor_Pressure <= 5) && (Sys_Mode == 0)); //tight(2)
        //assert (((Nor_Pressure <= 5) && (Sys_Mode == 0)) && ((PedalPos > 9) || (Nor_Pressure > -3))); //tight(3)
        //assert (((Nor_Pressure <= 5) ^ (Sys_Mode > 11)) && ((Nor_Pressure > -1) && (Sys_Mode == 0))); //tight(4)

        //assertion(3)
        //assert (!((PedalPos == 0) && (Sys_Mode > 13)) || ((PedalPos == 3) && (Nor_Pressure <= -1))); //initial
        // repaired prop
        //assert ((!(Nor_Pressure <= -2) || (Sys_Mode <= 0)) && (Sys_Mode == 0)); //tight(1)
        //assert (((Nor_Pressure == 0) && (Sys_Mode == 0)) || ((Nor_Pressure > 0) && (Sys_Mode == 0))); //tight(2)
        //assert (((Nor_Pressure == 15) ^ (Sys_Mode <= 0)) && ((Nor_Pressure > -1) && (Sys_Mode == 0))); //tight(3)
        //assert (((Nor_Pressure <= -1) ^ (Sys_Mode > -3)) && ((Nor_Pressure <= 5) && (Sys_Mode == 0))); //tight(4)

        //assertion(4)
        //assert (!((Nor_Pressure == -1) && (Sys_Mode == -4))); //initial repaired prop
        //assert (((Nor_Pressure > -2) && (Sys_Mode > -5)) && (Sys_Mode == 0)); //tight(1)
        //assert ((Nor_Pressure > -1) && (Sys_Mode == 0)); //tight(2)
        //assert ((!Skid || (Nor_Pressure == 0)) && ((Nor_Pressure > -1) && (Sys_Mode == 0))); //tight(3)

        //assertion(5)
        //assert  !(Nor_Pressure == -1); //initial repair prop
        //assert (!(Nor_Pressure <= -1)); //tight(1)
        //assert (((Nor_Pressure <= 5) ^ (Sys_Mode == -2)) && (Nor_Pressure > -1)); //tight(2)
        //assert (((Nor_Pressure > 5) || (Sys_Mode <= -2)) ^ ((Nor_Pressure > -1) || (Sys_Mode <= -2))); //tight(2)
        //assert ((Nor_Pressure > -1) && ((Nor_Pressure <= 5) && (Sys_Mode > -1))); //tight(2)
        //assert (((Nor_Pressure <= 5) || (Sys_Mode == -2)) && ((Nor_Pressure > -1) && (Sys_Mode == 0))); //tight(2)

        /************ SH edits ends here **********/


        /************ SH Mutation Results starts here **********/

        //mutation (1) fix
        //assert (!((AutoBrake || (Sys_Mode <= -1)) && (! Skid)) || ((Alt_Pressure > 0) || (Nor_Pressure > 0)));

        //mutation (2) fix
        //assert (!((!(Sys_Mode == 0) || (Alt_Pressure <= -1)) && (! Skid)) || ((Alt_Pressure > 0) || (Nor_Pressure >
        // 0)));

        //mutation(3)
        //assert (!((PedalPos > 3) && (!(Sys_Mode == 0) || (Nor_Pressure <= -1))) || ((Alt_Pressure > 0) ||
        // (Nor_Pressure > 0)));

        //mutation(5)
        //assert ((!(PedalPos > 3)) || ((Sys_Mode == 0) && (Alt_Pressure == 0)));
        /************ SH Mutation Results ends here **********/

    }


    public static void launch(int pedal1, boolean auto1, boolean skid1, int pedal2, boolean auto2, boolean skid2, int pedal3, boolean auto3, boolean skid3) {
        SpfWbs spfWbs = new SpfWbs();
        spfWbs.update(pedal1, auto1, skid1);
        //wbs.update(pedal2, auto2, skid2);
        //wbs.update(pedal3, auto3, skid3);
    }

    public void sym1(int a1, boolean a2, boolean a3) {
        update(a1, a2, a3);
    }

    public void sym2(int b1, boolean b2, boolean b3) {
        update(b1, b2, b3);
    }

    public void sym3(int c1, boolean c2, boolean c3) {
        update(c1, c2, c3);
    }

    public void sym4(int d1, boolean d2, boolean d3) {
        update(d1, d2, d3);
    }

    public void sym5(int e1, boolean e2, boolean e3) {
        update(e1, e2, e3);
    }

    public void sym6(int f1, boolean f2, boolean f3) {
        update(f1, f2, f3);
    }

    public void sym7(int g1, boolean g2, boolean g3) {
        update(g1, g2, g3);
    }

    public void sym8(int h1, boolean h2, boolean h3) {
        update(h1, h2, h3);
    }

    public void sym9(int i1, boolean i2, boolean i3) {
        update(i1, i2, i3);
    }

    public void sym10(int j1, boolean j2, boolean j3) {
        update(j1, j2, j3);
    }

    public static void main(String[] args) {
        launch(0, false, false, 0, false, false, 0, false, false);
    }
}
