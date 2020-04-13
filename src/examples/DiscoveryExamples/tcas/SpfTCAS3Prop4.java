package DiscoveryExamples.tcas;


/**
 * This class was used to bring down the prop4 to see why the pc is unsat when running spf or JR with incremental solver and optimizechoices turned false.
 * Also if optimizechoices is turned true and with the incremental solver, paths are not explored, indicating a bug.
 */
public class SpfTCAS3Prop4 {

    //free input
    public static int Cur_Vertical_Sep;
    public static int Own_Tracked_Alt;
    public static int Own_Tracked_Alt_Rate;
    public static int Other_Tracked_Alt;
    public static int Alt_Layer_Value;
    public static int Up_Separation;
    public static int Down_Separation;
    public static int Other_RAC;
    public static int Other_Capability;
    public static int Climb_Inhibit;


    //all state input
    public static int OLEV = 600;
    public static int MAXALTDIFF = 300;
    public static int MINSEP = 600;
    public static int NOZCROSS = 100;
    public static boolean High_Confidence;
    public static boolean Two_of_Three_Reports_Valid;
    static int Positive_RA_Alt_Thresh_0 = 400;
    static int Positive_RA_Alt_Thresh_1 = 500;
    static int Positive_RA_Alt_Thresh_2 = 640;
    static int Positive_RA_Alt_Thresh_3 = 740;
    public static int NO_INTENT = 0;
    public static int DO_NOT_CLIMB = 1;
    public static int DO_NOT_DESCEND = 2;
    public static int TCAS_TA = 1;
    public static int OTHER = 2;
    public static int UNRESOLVED = 0;
    public static int UPWARD_RA = 1;
    public static int DOWNWARD_RA = 2;


    //created field for output
    private static int result_alt_sep_test = 0;
    private static int alim_res = 0;


    public static boolean Non_Crossing_Biased_Descend() {
        int upward_preferred = 0;
        boolean result = false;

        int inhibit_biased_climb = Up_Separation + NOZCROSS;
        if (inhibit_biased_climb > Down_Separation) {
            upward_preferred = 1;
        } else {
            int alim = 400;//ALIM();
            if (!(Up_Separation >= alim)) {
                result = false;
            } else
                result = true;
        }
        return result;
    }


    public static int alt_assign() {
        int alt_sep = UNRESOLVED;

        boolean need_downward_RA = false;
        boolean non_crossing_biased_descend = Non_Crossing_Biased_Descend();
        if (non_crossing_biased_descend) {
            boolean own_above_threat;
            need_downward_RA = true;
        }
        if (need_downward_RA) {
            alt_sep = DOWNWARD_RA;
        } else {
            alt_sep = UNRESOLVED;
        }

        return alt_sep;
    }

    public static int alt_sep_test() {
        return alt_assign();

    }

    public static void mainProcess(int Cur_Vertical_Sep, int High_Confidence_flag, int Two_of_Three_Reports_Valid_flag,
                                   int Own_Tracked_Alt, int Own_Tracked_Alt_Rate, int Other_Tracked_Alt,
                                   int Alt_Layer_Value, int Up_Separation, int Down_Separation, int Other_RAC, int Other_Capability, int Climb_Inhibit) {
        SpfTCAS3Prop4.Cur_Vertical_Sep = Cur_Vertical_Sep;
        if (High_Confidence_flag == 0) {
            SpfTCAS3Prop4.High_Confidence = false;
        } else {
            SpfTCAS3Prop4.High_Confidence = true;
        }
        if (Two_of_Three_Reports_Valid_flag == 0) {
            SpfTCAS3Prop4.Two_of_Three_Reports_Valid = false;
        } else {
            SpfTCAS3Prop4.Two_of_Three_Reports_Valid = true;
        }

        SpfTCAS3Prop4.Own_Tracked_Alt = Own_Tracked_Alt;
        SpfTCAS3Prop4.Own_Tracked_Alt_Rate = Own_Tracked_Alt_Rate;
        SpfTCAS3Prop4.Other_Tracked_Alt = Other_Tracked_Alt;
        SpfTCAS3Prop4.Alt_Layer_Value = Alt_Layer_Value;
        SpfTCAS3Prop4.Up_Separation = Up_Separation;
        SpfTCAS3Prop4.Down_Separation = Down_Separation;
        SpfTCAS3Prop4.Other_RAC = Other_RAC;
        SpfTCAS3Prop4.Other_Capability = Other_Capability;
        SpfTCAS3Prop4.Climb_Inhibit = Climb_Inhibit;

        SpfTCAS3Prop4.result_alt_sep_test = alt_sep_test();
        SpfTCAS3Prop4.alim_res = 400;


        //Prop4:
        /*if ((Up_Separation >= 400 && Down_Separation < 400))
            if (SpfTCAS3.result_alt_sep_test == DOWNWARD_RA) {
                Debug.printPC("pc at violation is");
                assert false;
            }*/

        assert ((Up_Separation >= alim_res &&
                Down_Separation < alim_res) ?
                result_alt_sep_test != DOWNWARD_RA : true);

    }

    public static void main(String[] argv) {
        mainProcess(601, -1, 0, -1, 0, 0, 0, 301, 400, 0, 0, 1);
    }
}
