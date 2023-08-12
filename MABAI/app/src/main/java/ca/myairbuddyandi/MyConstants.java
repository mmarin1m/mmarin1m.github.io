package ca.myairbuddyandi;

/**
 * Created by Michel on 2017-07-21.
 * Holds all of this project constants
 */

public final class MyConstants {

    // Static
    private static final String LOG_TAG = "MyConstants";

    // Double
    static final Double ATA_TO_BAR = 1.01325;
    static final Double ATA_TO_MBAR = 980.665;
    static final Double ATA_TO_PSI = 14.6959;
    static final Double ATMOSPHERIC_PRESSURE = 5.255876;
    static final Double BAR_TO_ATA = 0.98692;
    static final Double BAR_TO_PSI = 14.503774;
    static final Double C_IMPERIAL = 0.0000068756;
    static final Double C_METRIC = 0.000022558;
    static final Double CUFT_TO_LITER = 28.3168;
    static final Double DEFAULT_BEST_MIX_HE = 0.0;
    static final Double DEFAULT_BEST_MIX_N2 = 68.0;
    static final Double DEFAULT_BEST_MIX_O2 = 32.0;
    static final Double DEFAULT_PARTIAL_PRESSURE = 1.4;
    static final Double FT_TO_M = 0.3048;
    static final Double HYDROSTATIC_PRESSURE_FRESH_WATER = 10.3; // 10.3 bar / 10 mfw - The Theory of Recreational Scuba Diving: Prepare for Your Dive Professional
    static final Double HYDROSTATIC_PRESSURE_SEA_WATER = 10.0; // 10.0 bar / 10 msw - The Theory of Recreational Scuba Diving: Prepare for Your Dive Professional
    static final Double KELVIN = 273.0;
    static final Double KG_TO_LB = 2.20462;
    static final Double KNOT_IMPERIAL = 1.687810;
    static final Double KNOT_METRIC = 0.51444;
    static final Double KNOT_TO_KPH = 1.8524;
    static final Double KNOT_TO_MPH = 1.150779;
    static final Double LB_TO_KG = 0.453592;
    static final Double LITER_TO_CUFT = 0.0353147;
    static final Double M_TO_FT = 3.28084;
    static final Double MAX_BOTTOM_TIME = 300.0;
    static final Double MIN_RMV = 0.0;
    static final Double MOLECULAR_WEIGHT_AIR = 28.84; // gram
    static final Double MOLECULAR_WEIGHT_HE = 4.0; // gram
    static final Double MOLECULAR_WEIGHT_N2 = 28.0; // gram
    static final Double MOLECULAR_WEIGHT_O2 = 32.0; // gram
    static final Double ONE_D = 1.0;
    static final Double PSI_TO_BAR = 0.068948;
    // 123 ft / 34 ft + 1 ata = 4.6176471 ata
    // 4.7272727 ata * 14.6959 psi/ata = 67.8604800 psi
    // 67.8604800 psi / (123 ft + 34 ft) = 0.4322324 psi/ft
    static final Double PSI_TO_FFW = 0.43223234;
    // 123 ft / 33 ft + 1 ata = 4.7272727 ata
    // 4.7272727 ata * 14.6959 psi/ata = 69.4715269 psi
    // 69.4715269 psi / (123 ft + 33 ft) = 0.4453303 ft/psi
    static final Double PSI_TO_FSW = 0.4453303;
    static final Double RANKINE = 460.0;
    static final Double WEIGHT_FRESH_WATER_I = 62.421; // lb/ft3
    static final Double WEIGHT_FRESH_WATER_M = 1.0; // kg/l
    static final Double WEIGHT_SEA_WATER_I = 63.92623; // lb/ft3
    static final Double WEIGHT_SEA_WATER_M = 1.0252959; // kg/l
    static final Double ZERO_D = 0.0;

    // Request Codes
    static final int REQ_CODE_EXTERNAL_STORAGE_PERMISSION = 10;
    static final int REQ_CODE_GOOGLE_ACCOUNT = 20;
    static final int REQ_CODE_PERMISSION_UTIL = 30;
    static final int REQ_CODE_RMV = 40;

    // Float
    static final Float ZERO_F = 0.0F;

    // int
    static final int MINUS_ONE_I = -1;
    static final int ONE_I = 1;
    static final int ZERO_I = 0;

    // Integer
    static final Integer AIR_O2 = 21;

    static final Integer AIR_N = 79;
    static final Integer AVERAGE_AGE = 25;
    static final Integer DC_TRANSPORT_BLE = 32;
    static final Integer DC_TRANSPORT_BLUETOOTH = 16;
    static final Integer DC_TRANSPORT_DUAL = 49;
    static final Integer DELAY_MILLI_SECONDS = 750; // Delay in Milliseconds
    static final Integer HUNDRED_PERCENT = 100;
    static final Integer MAX_LOG_BOOK_NO = 9999;
    static final Integer MAX_MINUTE = 300;
    static final Integer MAX_ORDER_NO = 12000;
    static final Integer MIN_MINUTE = 1;
    static final Integer NINETY_PERCENT = 90;
    static final Integer ONE_INT = 1;
    static final Integer REFRESH_RATE = 2000;
    static final Integer TEN_PERCENT = 10;
    static final Integer ZERO_INT = 0;

    // Long
    static final Long MINUS_ONE_L = -1L;
    static final Long ONE_L = 1L;
    static final Long ZERO_L = 0L;

    // Activities
    static final String COMPUTER = "COMPUTER";
    static final String COMPUTER_DIVES_PICK = "COMPUTER_DIVES_PICK";
    static final String CURRENT_SPEED = "CURRENT_SPEED";
    static final String CYLINDER = "CYLINDER";
    static final String CYLINDER_TYPE = "CYLINDER_TYPE";
    static final String DIVE = "DIVE";
    static final String DIVE_PICK = "DIVE_PICK";
    static final String DIVE_PLAN = "DIVE_PLAN";
    static final String DIVE_TYPE = "DIVE_TYPE";
    static final String DIVES_SELECTED = "DIVES_SELECTED";
    static final String DIVER = "DIVER";
    static final String DIVER_DIVE_GROUP_CYLINDER = "DIVER_DIVE_GROUP_CYLINDER";
    static final String GROUPP = "GROUPP";
    static final String GROUPP_TYPE = "GROUPP_TYPE";
    static final String LIST_STATE = "LIST_STATE";
    static final String PICK_A_CYLINDER = "PICK_A_CYLINDER";
    static final String PICK_A_DIVE = "PICK_A_DIVE";
    static final String PICK_A_DIVER = "PICK_A_DIVER";
    static final String PICK_A_DIVER_EXTRA = "PICK_A_DIVER_EXTRA";
    static final String PICK_A_GROUPP = "PICK_A_GROUPP";
    static final String PICK_A_LIBDIVECOMPUTER = "PICK_A_LIBDIVECOMPUTER";
    static final String SEGMENT_TYPE = "SEGMENT_TYPE";
    static final String SWIMMING_SPEED = "SWIMMING_SPEED";
    static final String USAGE_TYPE = "USAGE_TYPE";
    static final String STATE = "STATE";

    // String
    static final String AIRDA = "airDa";
    static final String AVERAGE = "A";
    static final String BLUE = "#1A21E1";
    static final String BOTTOM_GAS = "BG";
    static final String DB_VERSION = "DB_VERSION";
    static final String DECO_GAS = "DG";
    static final String EMERGENCY_GAS = "EG";
    static final String ENDN2 = "ENDN2";
    static final String ENDN2O2 = "ENDN2O2";
    static final String ENDN2O2HE = "ENDN2O2HE";
    static final String GREEN = "#1B5E20";
    static final String IMPERIAL = "imperial";
    static final String LAST = "L";
    static final String LAST_10 = "L10";
    static final String METRIC = "metric";
    static final String MINUS = "Minus";
    static final String MY_DATE_PATTERN = "M/d/yy";
    static final String MY_EMAIL = "mmarin1m@gmail.com";
    static final String NO = "NO";
    static final String NONE = "None";
    static final String PLAN = "Plan";
    static final String PLUS = "Plus";
    static final String REAL = "Real";
    static final String RED = "#F44336";
    static final String REEL = "Réel";
    static final String TIME_PATTERN_12 = "h:mm a";
    static final String TIME_PATTERN_24 = "HH:mm";
    static final String TRAVEL_GAS = "TG";
    static final String TYPICAL = "TY";
    static final String VERSION_NO = "VERSION_NO";
    static final String YES = "YES";
    static final String WHITE = "#FFFFFF";

    // Settings
    // Rates
    static final String DESCENT_RATE = "descentRate";
    static final String ASCENT_RATE_TO_DS = "ascentRateToDs";
    static final String ASCENT_RATE_TO_SS = "ascentRateToSs";
    static final String ASCENT_RATE_TO_SU = "ascentRateToSu";
    // Depths
    static final String BUBBLE_CHECK_DEPTH = "bubbleCheckDepth";
    static final String SAFETY_STOP_DIVE = "safetyStopDive";
    static final String SAFETY_STOP_DEPTH = "safetyStopDepth";
    static final String DEEP_STOP_PERCENT = "deepStopPercent";
    static final String DEEP_STOP_DIVE = "deepStopDive";
    static final String END = "end";
    // Times
    static final String BUBBLE_CHECK_TIME = "bubbleCheckTime";
    static final String TURNAROUND_TIME = "turnaroundTime";
    static final String SUBTRACT_DEEP_STOP_TIME = "subtractDeepStopTime";
    static final String DEEP_STOP_TIME = "deepStopTime";
    static final String SAFETY_STOP_TIME = "safetyStopTime";
    static final String OOA_TURNAROUND_TIME = "ooaTurnaroundTime";
    // Pressures
    static final String MY_MIN_PRESSURE = "myMinPressure";
    static final String ROCK_BOTTOM_MIN_PRESSURE = "rockBottomMinPressure";
    // SACs and RMVs
    static final String ROCK_BOTTOM_SAC = "rockBottomSac";
    static final String ROCK_BOTTOM_RMV = "rockBottomRmv";
    // Blending
    static final String HELIUM_MIX = "heliumMix";
    static final String OXYGEN_MIX = "oxygenMix";
    static final String TOP_OFF_MIX = "topOffMix";
    static final String LENGTH_HOSE = "lengthHose";
    static final String DIAMETER_HOSE = "diameterHose";
    // Bluetooth
    static final String CONNECTING_TIMEOUT = "connectingTimeout";
    static final String SCANNING_TIMEOUT = "scanningTimeout";

    // Public

    // Protected

    // Private

    // End of variables

    // Public constructor
    public MyConstants() {
    }
}
