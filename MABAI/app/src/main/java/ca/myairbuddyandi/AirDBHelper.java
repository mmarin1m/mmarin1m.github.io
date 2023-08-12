package ca.myairbuddyandi;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Michel on 2016-11-27.
 * Holds all of the logic for the AirDBHelper Class
 */

public class AirDBHelper extends SQLiteOpenHelper {

    // Static
    private static final String LOG_TAG = "AirDBHelper";
    private static final String DB_NAME = "air.db";
    private static final int DB_VERSION = 7;
    public static final int FK_CONSTRAINT_DELETE = 1811;
    public static final int FK_CONSTRAINT_UPDATE = -1;

    // Table definitions
    //Code tables
    public static final String TABLE_CYLINDER_TYPE = "CYLINDER_TYPE";
    private static final String TABLE_CYLINDER_TYPE_I1 = "CYLINDER_TYPE_I1";
    public static final String TABLE_CYLINDER_TYPE_CYLINDER_TYPE = "CYLINDER_TYPE"; // PK e.g. HPAL80
    public static final String TABLE_CYLINDER_TYPE_DESCRIPTION = "DESCRIPTION"; // High Pressure Aluminum 80
    public static final String TABLE_CYLINDER_TYPE_VOLUME = "VOLUME"; // in ft3 e.g 119 ft3
    public static final String TABLE_CYLINDER_TYPE_RATED_PRESSURE = "RATED_PRESSURE"; // In psi or bar e.g. 2250 psi

    public static final String TABLE_DIVE_TYPE = "DIVE_TYPE";
    private static final String TABLE_DIVE_TYPE_I1 = "DIVE_TYPE_I1";
    public static final String TABLE_DIVE_TYPE_DIVE_TYPE= "DIVE_TYPE"; // PK e.g. L
    public static final String TABLE_DIVE_TYPE_DESCRIPTION = "DESCRIPTION"; // Last
    public static final String TABLE_DIVE_TYPE_SORT_ORDER = "SORT_ORDER"; // e.g. 10, 20 30
    public static final String TABLE_DIVE_TYPE_IN_PICKER = "IN_PICKER"; // Can the user pick this dive type when adding a new dive, Yes or No

    // DB Change 2020/04/01 New table DB_VERSION = 2
    public static final String TABLE_DYNAMIC_SPINNER = "DYNAMIC_SPINNER";
    public static final String TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED = "SYSTEM_DEFINED"; // Yes (protected), No (Not protected), Text
    public static final String TABLE_DYNAMIC_SPINNER_SPINNER_TYPE= "SPINNER_TYPE"; // PK but no data integrity, LO Location, DI for DS for Dive Site, DB for Dive Boat, Text
    public static final String TABLE_DYNAMIC_SPINNER_SPINNER_TEXT = "SPINNER_TEXT"; // Free text, Text

    public static final String TABLE_GROUP_TYPE = "GROUP_TYPE";
    private static final String TABLE_GROUP_TYPE_I1 = "GROUP_TYPE_I1";
    public static final String TABLE_GROUP_TYPE_GROUP_TYPE = "GROUP_TYPE"; // PK e.g. S, D, SBS
    public static final String TABLE_GROUP_TYPE_DESCRIPTION = "DESCRIPTION"; // Single, Double, Side by Side
    public static final String TABLE_GROUP_TYPE_SYSTEM_DEFINED = "SYSTEM_DEFINED"; // Yes (protected), No (Not protected)

    public static final String TABLE_SEGMENT_TYPE = "SEGMENT_TYPE";
    private static final String TABLE_SEGMENT_TYPE_I1 = "SEGMENT_TYPE_I1";
    public static final String TABLE_SEGMENT_TYPE_SEGMENT_TYPE = "SEGMENT_TYPE"; // PK e.g. D
    public static final String TABLE_SEGMENT_TYPE_DESCRIPTION = "DESCRIPTION"; // Descent
    public static final String TABLE_SEGMENT_TYPE_ORDER_NO = "ORDER_NO"; // Order of the segment in the calculation results
    public static final String TABLE_SEGMENT_TYPE_DIRECTION = "DIRECTION"; // Down, Bottom, Up
    public static final String TABLE_SEGMENT_TYPE_SHOW_RESULT = "SHOW_RESULT"; // Yes or No
    public static final String TABLE_SEGMENT_TYPE_SYSTEM_DEFINED = "SYSTEM_DEFINED"; // Yes (protected), No (Not protected)
    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
    public static final String TABLE_SEGMENT_TYPE_STATUS = "STATUS"; // Active, Inactive, Text

    public static final String TABLE_USAGE_TYPE = "USAGE_TYPE";
    private static final String TABLE_USAGE_TYPE_I1 = "USAGE_TYPE_I1";
    public static final String TABLE_USAGE_TYPE_USAGE_TYPE = "USAGE_TYPE"; // PK e.g. BG, TG, DG, BA
    public static final String TABLE_USAGE_TYPE_DESCRIPTION = "DESCRIPTION"; // Bottom Gas, Travel Gas, Decompression (Stage) Gas, Emergency Gas (Pony)
    public static final String TABLE_USAGE_TYPE_SYSTEM_DEFINED = "SYSTEM_DEFINED"; // Yes (protected), No (Not protected)

    public static final String TABLE_SETTING = "SETTING"; // Fake table to export/import application settings

    // Base tables
    // DB Change 2023/04/25 New table DB_VERSION = 6
    public static final String TABLE_COMPUTER = "COMPUTER";
    public static final String TABLE_COMPUTER_COMPUTER_NO = "COMPUTER_NO"; // PK INTEGER
    public static final String TABLE_COMPUTER_DESCRIPTION = "DESCRIPTION"; // TEXT
    public static final String TABLE_COMPUTER_VENDOR = "VENDOR"; // TEXT
    public static final String TABLE_COMPUTER_PRODUCT = "PRODUCT"; // TEXT
    public static final String TABLE_COMPUTER_TYPE = "TYPE"; // TEXT
    public static final String TABLE_COMPUTER_MODEL = "MODEL"; // INTEGER
    public static final String TABLE_COMPUTER_TRANSPORT = "TRANSPORT"; // INTEGER
    public static final String TABLE_COMPUTER_SERIAL_NO = "SERIAL_NO"; // TEXT
    public static final String TABLE_COMPUTER_FW = "FW"; // TEXT
    public static final String TABLE_COMPUTER_FWID = "FWID"; // TEXT
    public static final String TABLE_COMPUTER_LANGUAGE = "LANGUAGE"; // TEXT
    public static final String TABLE_COMPUTER_UNIT = "UNIT"; // TEXT
    public static final String TABLE_COMPUTER_CONNECTION_TYPE = "CONNECTION_TYPE"; // TEXT
    public static final String TABLE_COMPUTER_MAC_ADDRESS = "MAC_ADDRESS"; // TEXT
    public static final String TABLE_COMPUTER_DEVICE_NAME = "DEVICE_NAME"; // TEXT
    public static final String TABLE_COMPUTER_SERVICE = "SERVICE"; // TEXT
    public static final String TABLE_COMPUTER_CHARACTERISTIC_RX = "CHARACTERISTIC_RX"; // TEXT
    public static final String TABLE_COMPUTER_CHARACTERISTIC_RX_CREDITS = "CHARACTERISTIC_RX_CREDITS"; // TEXT
    public static final String TABLE_COMPUTER_CHARACTERISTIC_TX = "CHARACTERISTIC_TX"; // TEXT
    public static final String TABLE_COMPUTER_CHARACTERISTIC_TX_CREDITS = "CHARACTERISTIC_TX_CREDITS"; // TEXT

    // DB Change 2023/04/24 New table DB_VERSION = 6
    public static final String TABLE_COMPUTER_DIVE = "COMPUTER_DIVE";
    private static final String TABLE_COMPUTER_DIVE_COMPUTER_NO = "COMPUTER_NO"; // PK FK INTEGER
    private static final String TABLE_COMPUTER_DIVE_COMPUTER_DIVE_NO = "COMPUTER_DIVE_NO"; // PK INTEGER
    private static final String TABLE_COMPUTER_DIVE_AVERAGE_DEPTH = "AVERAGE_DEPTH"; // REAL
    private static final String TABLE_COMPUTER_DIVE_MAXIMUM_DEPTH = "MAXIMUM_DEPTH"; // REAL
    private static final String TABLE_COMPUTER_DIVE_DATE = "DIVE_DATE"; // INTEGER
    private static final String TABLE_COMPUTER_DIVE_START_TIME = "START_TIME"; // INTEGER
    private static final String TABLE_COMPUTER_DIVE_BOTTOM_TIME = "BOTTOM_TIME"; // Time spent at the bottom. In minutes and seconds, REAL
    private static final String TABLE_COMPUTER_DIVE_SURFACE_INTERVAL = "SURFACE_INTERVAL"; // In days, hours, minutes, REAL
    private static final String TABLE_COMPUTER_DIVE_AIR_TEMP = "AIR_TEMP"; // 78, REAL
    private static final String TABLE_COMPUTER_DIVE_WATER_TEMP_SURFACE = "WATER_TEMP_SURFACE"; // 60, REAL
    private static final String TABLE_COMPUTER_DIVE_WATER_TEMP_BOTTOM = "WATER_TEMP_BOTTOM"; // 50, REAL
    private static final String TABLE_COMPUTER_DIVE_WATER_TEMP_AVERAGE = "WATER_TEMP_AVERAGE"; // 55, REAL

    public static final String TABLE_DIVE = "DIVE";
    private static final String TABLE_DIVE_I1 = "DIVE_I1"; // Index
    public static final String TABLE_DIVE_DIVE_NO = "DIVE_NO"; // PK INTEGER
    public static final String TABLE_DIVE_LOG_BOOK_NO = "LOG_BOOK_NO"; // 215, INTEGER
    // DB Change 2023/04/24 New column(s) DB_VERSION = 6
    public static final String TABLE_DIVE_COMPUTER_DIVE_NO = "COMPUTER_DIVE_NO"; // 215, INTEGER
    public static final String TABLE_DIVE_DATE = "DATE"; // Date of the dive INTEGER
    public static final String TABLE_DIVE_STATUS = "STATUS"; // Real, Plan, TEXT
    public static final String TABLE_DIVE_DIVE_TYPE = "DIVE_TYPE"; // FK e.g. DE for Deep TEXT
    static final String TABLE_DIVE_SALINITY = "SALINITY"; // 0 = Salt, 1 = Fresh INTEGER
    public static final String TABLE_DIVE_BOTTOM_TIME = "BOTTOM_TIME"; // Time spent at the bottom. In minutes and seconds, REAL
    public static final String TABLE_DIVE_AVERAGE_DEPTH = "AVERAGE_DEPTH"; // Average depth according to your computer. In ft or m. e.g. 77.7, REAL
    // Between 0 ft (sea level) and 20,938 ft (Ojos del Salado). 12,507 ft (Lake Titicaca)
    public static final String TABLE_DIVE_ALTITUDE = "ALTITUDE"; // 1000 feet, INTEGER
    // DB Change 2020/03/22 New column(s) DB_VERSION = 2
    public static final String TABLE_DIVE_LOCATION = "LOCATION"; // Dutch Springs, TEXT
    public static final String TABLE_DIVE_DIVE_SITE = "DIVE_SITE"; // Peninsula, TEXT
    public static final String TABLE_DIVE_DIVE_BOAT = "DIVE_BOAT"; // Independence II, TEXT
    public static final String TABLE_DIVE_PURPOSE = "PURPOSE"; // Deco Procedures, TEXT
    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
    public static final String TABLE_DIVE_VISIBILITY = "VISIBILITY"; // 10 feet, murky, TEXT
    public static final String TABLE_DIVE_MAXIMUM_DEPTH = "MAXIMUM_DEPTH"; // Maximum depth according to your computer. In ft or m. e.g. 77.7, REAL
    public static final String TABLE_DIVE_SUIT = "SUIT"; // Dry, Wet, TEXT
    public static final String TABLE_DIVE_WEIGHT = "WEIGHT"; // 3.3 kg, REAL
    public static final String TABLE_DIVE_AIR_TEMP = "AIR_TEMP"; // 78, REAL
    public static final String TABLE_DIVE_WATER_TEMP_SURFACE = "WATER_TEMP_SURFACE"; // 60, REAL
    public static final String TABLE_DIVE_WATER_TEMP_BOTTOM = "WATER_TEMP_BOTTOM"; // 50, REAL
    public static final String TABLE_DIVE_WATER_TEMP_AVERAGE = "WATER_TEMP_AVERAGE"; // 55, REAL
    public static final String TABLE_DIVE_NOTE = "NOTE"; // Free text, TEXT
    // 2023/08/2 DB_VERSION = 7 Renamed from NOTE to NOTE_SUMMARY
    public static final String TABLE_DIVE_NOTE_SUMMARY = "NOTE_SUMMARY"; // Free text, TEXT
    public static final String TABLE_DIVE_ENVIRONMENT = "ENVIRONMENT"; // Free text, TEXT
    public static final String TABLE_DIVE_PLATFORM = "PLATFORM"; // Free text, TEXT
    public static final String TABLE_DIVE_WEATHER = "WEATHER"; // Free text, TEXT
    public static final String TABLE_DIVE_CONDITION = "CONDITION"; // Free text, TEXT
    // 2023/08/2 DB_VERSION = 7 New columns
    // 1- Planning
    //    None
    // 2- Summary
    //    None
    // 3- Environment
    public static final String TABLE_DIVE_NOTE_ENVIRONMENT = "NOTE_ENVIRONMENT"; // Free text, TEXT
    // 4- Gas
    public static final String TABLE_DIVE_NOTE_GAS = "NOTE_GAS"; // Free text, TEXT
    // 5- Gear
    public static final String TABLE_DIVE_NOTE_GEAR = "NOTE_GEAR"; // Free text, TEXT
    // 6- Problem
    public static final String TABLE_DIVE_THERMAL_COMFORT = "THERMAL_COMFORT"; // Very Hot, Hot TEXT
    public static final String TABLE_DIVE_WORK_LOAD = "WORK_LOAD"; // REsting, Light, TEXT
    public static final String TABLE_DIVE_PROBLEM = "PROBLEM"; // No Problem, Equalization, OOO, TEXT
    public static final String TABLE_DIVE_MALFUNCTION = "MALFUNCTION"; // No Problem, FAce Mask, Fins, TEXT
    public static final String TABLE_DIVE_ANY_SYMPTOM = "ANY_SYMPTOM"; // No, Yes, TEXT
    public static final String TABLE_DIVE_EXPOSURE_ALTITUDE = "EXPOSURE_ALTITUDE"; // No Problem. Commercial aircraft, TEXT
    public static final String TABLE_DIVE_NOTE_PROBLEM = "NOTE_PROBLEM"; // Free text, TEXT
    // 7- Dive Computer
    // 8- Graph

    public static final String TABLE_DIVER = "DIVER"; // All the divers I know
    private static final String TABLE_DIVER_I1 = "DIVER_I1";
    public static final String TABLE_DIVER_DIVER_NO = "DIVER_NO"; // PK INTEGER Me is always 1
    public static final String TABLE_DIVER_FIRST_NAME = "FIRST_NAME";
    public static final String TABLE_DIVER_MIDDLE_NAME = "MIDDLE_NAME";
    public static final String TABLE_DIVER_LAST_NAME = "LAST_NAME";
    public static final String TABLE_DIVER_GENDER = "GENDER"; // 0 = Female, 1 = Male
    public static final String TABLE_DIVER_BIRTH_DATE = "BIRTH_DATE"; // Either MM/ddYYYY or MM/YYYY
    public static final String TABLE_DIVER_PHONE = "PHONE";
    public static final String TABLE_DIVER_EMAIL = "EMAIL";
    public static final String TABLE_DIVER_CERTIFICATION_BODY = "CERTIFICATION_BODY"; // PADI
    public static final String TABLE_DIVER_CERTIFICATION_LEVEL = "CERTIFICATION_LEVEL"; // Advanced
    public static final String TABLE_DIVER_MAX_DEPTH_ALLOWED = "MAX_DEPTH_ALLOWED"; // 130 ft or 40 m

    public static final String TABLE_GROUPP = "GROUPP"; // GROUP is a reserved word in SQLite
    private static final String TABLE_GROUPP_I1 = "GROUPP_I1";
    public static final String TABLE_GROUP_GROUP_NO = "GROUP_NO"; // PK INTEGER
    public static final String TABLE_GROUP_DIVER_NO = "DIVER_NO"; // PK FK INTEGER
    public static final String TABLE_GROUP_GROUP_TYPE = "GROUP_TYPE"; // FK e.g. S
    public static final String TABLE_GROUP_DESCRIPTION = "DESCRIPTION"; // My double, My 200' kit, My 300' kit

    public static final String TABLE_STATE = "STATE"; // Last values used by the user
    public static final String TABLE_STATE_STATE_NO = "STATE_NO"; // PK INTEGER Always 1
    public static final String TABLE_STATE_DIVE_TYPE = "DIVE_TYPE"; // FK e.g. L
    public static final String TABLE_STATE_MY_BUDDY_DIVER_NO = "MY_BUDDY_DIVER_NO"; // FK e.g. 2 for Yvan
    public static final String TABLE_STATE_MY_SAC = "MY_SAC"; // My Selected Surface Air Consumption
    public static final String TABLE_STATE_MY_RMV = "MY_RMV"; // My Selected Respirator Minute Volume
    public static final String TABLE_STATE_MY_GROUP = "MY_GROUP"; // My Selected Equipment Group
    public static final String TABLE_STATE_MY_BUDDY_SAC = "MY_BUDDY_SAC"; // My Buddy Selected Surface Air Consumption
    public static final String TABLE_STATE_MY_BUDDY_RMV = "MY_BUDDY_RMV"; // My Buddy Selected Respirator Minute Volume
    public static final String TABLE_STATE_MY_BUDDY_GROUP = "MY_BUDDY_GROUP"; // My Selected Equipment Group

    // Child Tables
    public static final String TABLE_DIVE_PLAN = "DIVE_PLAN";
    private static final String TABLE_DIVE_PLAN_I1 = "DIVE_PLAN_I1";
    public static final String TABLE_DIVE_PLAN_DIVE_PLAN_NO = "DIVE_PLAN_NO"; // PK INTEGER
    public static final String TABLE_DIVE_PLAN_DIVE_NO = "DIVE_NO"; // FK
    public static final String TABLE_DIVE_PLAN_ORDER_NO = "ORDER_NO"; // // Order of the plan in the sequence in this dive
    public static final String TABLE_DIVE_PLAN_DEPTH = "DEPTH"; // 130 ft or 40 m
    public static final String TABLE_DIVE_PLAN_MINUTE = "MINUTE"; // 25 min

    // Junction tables
    public static final String TABLE_CYLINDER = "CYLINDER";
    private static final String TABLE_CYLINDER_I1 = "CYLINDER_I1";
    public static final String TABLE_CYLINDER_CYLINDER_NO = "CYLINDER_NO"; // PK INTEGER Starts at 1
    public static final String TABLE_CYLINDER_DIVER_NO = "DIVER_NO"; // PK The dive who owns that cylinder
    public static final String TABLE_CYLINDER_CYLINDER_TYPE = "CYLINDER_TYPE"; // FK e.g. HPAL80
    public static final String TABLE_CYLINDER_VOLUME = "VOLUME"; // in ft3 e.g 119 ft3
    public static final String TABLE_CYLINDER_RATED_PRESSURE = "RATED_PRESSURE"; // In psi or bar e.g. 2250 psi
    public static final String TABLE_CYLINDER_BRAND = "BRAND"; // Free text
    public static final String TABLE_CYLINDER_MODEL = "MODEL"; // Free text
    public static final String TABLE_CYLINDER_SERIAL_NO = "SERIAL_NO"; // Free text. Alphanumeric with spaces
    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
    public static final String TABLE_CYLINDER_LAST_VIP = "LAST_VIP"; // Integer. Date of the last Visual Inspection
    public static final String TABLE_CYLINDER_LAST_HYDRO = "LAST_HYDRO"; // Integer. Date of the last Hydro
    public static final String TABLE_CYLINDER_COLOR = "COLOR"; // Free text. Color of the tank e..g yellow, green
    public static final String TABLE_CYLINDER_WEIGHT_FULL = "WEIGHT_FULL"; // Real. Weight of the cylinder when full at rated pressure
    public static final String TABLE_CYLINDER_WEIGHT_EMPTY = "WEIGHT_EMPTY"; // Real. Weight of the cylinder when empty at 500 psi
    public static final String TABLE_CYLINDER_BUOYANCY_FULL = "BUOYANCY_FULL"; // Real. Buoyancy of the cylinder when full at rated pressure in salt water
    public static final String TABLE_CYLINDER_BUOYANCY_EMPTY = "BUOYANCY_EMPTY"; // Real. Buoyancy of the cylinder when empty at 500 psi in salt water

    public static final String TABLE_DIVE_SEGMENT = "DIVE_SEGMENT";
    private static final String TABLE_DIVE_SEGMENT_I1 = "DIVE_SEGMENT_I1";
    public static final String TABLE_DIVE_SEGMENT_DIVER_NO = "DIVER_NO"; // PK FK INTEGER
    public static final String TABLE_DIVE_SEGMENT_DIVE_NO = "DIVE_NO"; // PK FK INTEGER
    public static final String TABLE_DIVE_SEGMENT_ORDER_NO = "ORDER_NO"; // // PK INTEGER Order of the segment in the sequence in this dive
    public static final String TABLE_DIVE_SEGMENT_SEGMENT_TYPE = "SEGMENT_TYPE"; // FK e.g. D
    public static final String TABLE_DIVE_SEGMENT_DEPTH = "DEPTH"; // 130 ft or 40 m
    public static final String TABLE_DIVE_SEGMENT_MINUTE = "MINUTE"; // 25.5 min
    public static final String TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE = "AIR_CONSUMPTION_PRESSURE"; // 2000 psi
    public static final String TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME = "AIR_CONSUMPTION_VOLUME"; // 100 ft3
    public static final String TABLE_DIVE_SEGMENT_CALC_ATA = "CALC_ATA"; // 2 ATA
    public static final String TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH = "CALC_AVERAGE_DEPTH"; // 100 ft
    public static final String TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA = "CALC_AVERAGE_ATA"; // 1.45 ATA
    public static final String TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE = "CALC_DESCENT_RATE"; // 30 ft/min
    public static final String TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE = "CALC_ASCENT_RATE"; // 30 ft/min
    public static final String TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE = "CALC_DECREASING_PRESSURE"; // Starts at maximum pressure e.g. 3442 and keeps decreasing
    public static final String TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME = "CALC_DECREASING_VOLUME"; // Starts at maximum volume e.g. 117 and keeps decreasing

    public static final String TABLE_DIVER_DIVE = "DIVER_DIVE";
    private static final String TABLE_DIVER_DIVE_I1 = "DIVER_DIVE_I1";
    public static final String TABLE_DIVER_DIVE_DIVER_NO = "DIVER_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_DIVE_NO = "DIVE_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_RMV = "RMV"; // Respirator Minute Volume. In ft3 / min. e.g. 0.801
    // 2020/02/22 New column(s) DB_VERSION = 2
    public static final String TABLE_DIVER_DIVE_IS_PRIMARY = "IS_PRIMARY"; // M, Me, Y, Primary buddy on the dive N, Extra divers i.e Students, Text

    public static final String TABLE_DIVER_DIVE_GROUP = "DIVER_DIVE_GROUP";
    private static final String TABLE_DIVER_DIVE_GROUP_I1 = "DIVER_DIVE_GROUP_I1";
    public static final String TABLE_DIVER_DIVE_GROUP_DIVER_NO = "DIVER_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_DIVE_NO = "DIVE_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_GROUP_NO = "GROUP_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_SAC = "SAC"; // Surface air Consumption. In psi / min. e.g. 25 psi/min

    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER = "DIVER_DIVE_GROUP_CYLINDER";
    private static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_I1 = "DIVER_DIVE_GROUP_CYLINDER_I1";
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO = "DIVER_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO = "DIVE_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO = "GROUP_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO = "CYLINDER_NO"; // PK FK INTEGER
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE = "BEGINNING_PRESSURE"; // Pressure at the beginning of the dive. In psi or bar. e.g. 3400
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE = "ENDING_PRESSURE"; // Pressure at the end of the dive. In psi or bar. e.g. 750
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_O2 = "O2"; // Oxygen, between 00% and 100%
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_N = "N"; // Oxygen, between 00% and 100%
    public static final String TABLE_DIVER_DIVE_GROUP_CYLINDER_HE = "HE"; // Helium, between 00% and 100%

    public static final String TABLE_GROUP_CYLINDER = "GROUP_CYLINDER";
    private static final String TABLE_GROUP_CYLINDER_I1 = "GROUP_CYLINDER_I1";
    public static final String TABLE_GROUP_CYLINDER_GROUP_NO = "GROUP_NO"; // PK FK INTEGER
    public static final String TABLE_GROUP_CYLINDER_CYLINDER_NO = "CYLINDER_NO"; // PK FK INTEGER
    public static final String TABLE_GROUP_CYLINDER_USAGE_TYPE = "USAGE_TYPE"; // BG = Bottom Gas, TG = Travel Gas, DG = Decompression Gas (Stage), EG = Emergency Gas (Pony)

    //Activity Select
    //Main Activity
    public static final String AS_MAIN_ACTIVITY_DIVE_TYPE = "DIVE_TYPE";
    public static final String AS_MAIN_ACTIVITY_MY_SAC = "MY_SAC";
    public static final String AS_MAIN_ACTIVITY_MY_RMV = "MY_RMV";
    public static final String AS_MAIN_ACTIVITY_MY_GROUP = "MY_GROUP";
    public static final String AS_MAIN_ACTIVITY_MY_TOTAL_DIVE = "MY_TOTAL_DIVE";
    public static final String AS_MAIN_ACTIVITY_MY_LAST_DIVE = "MY_LAST_DIVE";
    public static final String AS_MAIN_ACTIVITY_MY_BOTTOM_TIME = "MY_BOTTOM_TIME";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_NAME = "MY_BUDDY_NAME";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_SAC = "MY_BUDDY_SAC";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_RMV = "MY_BUDDY_RMV";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_GROUP = "MY_BUDDY_GROUP";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_TOTAL_DIVE = "MY_BUDDY_TOTAL_DIVE";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_LAST_DIVE = "MY_BUDDY_LAST_DIVE";
    public static final String AS_MAIN_ACTIVITY_MY_BUDDY_BOTTOM_TIME = "MY_BUDDY_BOTTOM_TIME";

    //SacRmvActivity
    public static final String AS_SAC_RMV_ACTIVITY_DIVE_TYPE_SELECTED = "DIVE_TYPE_SELECTED";
    public static final String AS_SAC_RMV_ACTIVITY_MY_RMV = "MY_RMV";
    public static final String AS_SAC_RMV_ACTIVITY_MY_COUNT = "MY_COUNT";
    public static final String AS_SAC_RMV_ACTIVITY_DIVE_TYPE = "DIVE_TYPE";
    public static final String AS_SAC_RMV_ACTIVITY_DIVE_TYPE_DESC = "DIVE_TYPE_DESC";
    public static final String AS_SAC_RMV_ACTIVITY_MY_BUDDY_RMV = "MY_BUDDY_RMV";
    public static final String AS_SAC_RMV_ACTIVITY_MY_BUDDY_COUNT = "MY_BUDDY_COUNT";

    // Table creations
    // Code tables
    public static final String TABLE_CREATE_CYLINDER_TYPE =
            "CREATE TABLE " + TABLE_CYLINDER_TYPE + " (" +
                    TABLE_CYLINDER_TYPE_CYLINDER_TYPE + " TEXT PRIMARY KEY, " +
                    TABLE_CYLINDER_TYPE_DESCRIPTION + " TEXT, " +
                    TABLE_CYLINDER_TYPE_VOLUME + " REAL, " +
                    TABLE_CYLINDER_TYPE_RATED_PRESSURE + " REAL" +
                    ")";

    public static final String TABLE_CREATE_CYLINDER_TYPE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_CYLINDER_TYPE_I1 + " ON " + TABLE_CYLINDER_TYPE + "(" + TABLE_CYLINDER_TYPE_CYLINDER_TYPE + ")";

    // DB Change 2020/04/01 New table DB_VERSION = 2
    public static final String TABLE_CREATE_CYLINDER_TYPE_TRIGGER =
            "CREATE TRIGGER aft_update AFTER UPDATE ON " + TABLE_CYLINDER_TYPE + " "
          + "BEGIN "
          + "UPDATE " + TABLE_CYLINDER + " "
          + "SET rated_pressure = NEW.rated_pressure "
          + "WHERE cylinder_type = OLD.cylinder_type; "
          + "END";

    public static final String TABLE_CREATE_DIVE_TYPE =
            "CREATE TABLE " + TABLE_DIVE_TYPE + " (" +
                    TABLE_DIVE_TYPE_DIVE_TYPE + " TEXT NOT NULL PRIMARY KEY UNIQUE, " +
                    TABLE_DIVE_TYPE_DESCRIPTION + " TEXT, " +
                    TABLE_DIVE_TYPE_SORT_ORDER + " INTEGER, " +
                    TABLE_DIVE_TYPE_IN_PICKER + " TEXT" +
                    ")";

    public static final String TABLE_CREATE_DIVE_TYPE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVE_TYPE_I1 + " ON " + TABLE_DIVE_TYPE + "(" + TABLE_DIVE_TYPE_DIVE_TYPE + ")";

    // DB Change 2020/04/01 New table DB_VERSION = 2
    public static final String TABLE_CREATE_DYNAMIC_SPINNER =
            "CREATE TABLE " + TABLE_DYNAMIC_SPINNER + " (" +
                    TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED + " TEXT, " +
                    TABLE_DYNAMIC_SPINNER_SPINNER_TYPE + " TEXT, " +
                    TABLE_DYNAMIC_SPINNER_SPINNER_TEXT  + " TEXT" +
                    ")";

    public static final String TABLE_CREATE_GROUP_TYPE =
            "CREATE TABLE " + TABLE_GROUP_TYPE + " (" +
                    TABLE_GROUP_TYPE_GROUP_TYPE + " TEXT NOT NULL PRIMARY KEY UNIQUE, " +
                    TABLE_GROUP_TYPE_DESCRIPTION + " TEXT, " +
                    TABLE_GROUP_TYPE_SYSTEM_DEFINED + " TEXT" +
                    ")";

    public static final String TABLE_CREATE_GROUP_TYPE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_GROUP_TYPE_I1 + " ON " + TABLE_GROUP_TYPE + "(" + TABLE_GROUP_TYPE_GROUP_TYPE + ")";

    public static final String TABLE_CREATE_SEGMENT_TYPE =
            "CREATE TABLE " + TABLE_SEGMENT_TYPE + " (" +
                    TABLE_SEGMENT_TYPE_SEGMENT_TYPE + " TEXT NOT NULL PRIMARY KEY UNIQUE, " +
                    TABLE_SEGMENT_TYPE_DESCRIPTION + " TEXT, " +
                    TABLE_SEGMENT_TYPE_ORDER_NO + " INTEGER, " +
                    TABLE_SEGMENT_TYPE_DIRECTION + " TEXT, " +
                    TABLE_SEGMENT_TYPE_SHOW_RESULT + " TEXT, " +
                    TABLE_SEGMENT_TYPE_SYSTEM_DEFINED + " TEXT, " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    TABLE_SEGMENT_TYPE_STATUS + " TEXT" +
                    ")";

    public static final String TABLE_CREATE_SEGMENT_TYPE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_SEGMENT_TYPE_I1 + " ON " + TABLE_SEGMENT_TYPE + "(" + TABLE_SEGMENT_TYPE_SEGMENT_TYPE + ")";

    public static final String TABLE_CREATE_USAGE_TYPE =
            "CREATE TABLE " + TABLE_USAGE_TYPE + " (" +
                    TABLE_USAGE_TYPE_USAGE_TYPE + " TEXT NOT NULL PRIMARY KEY UNIQUE, " +
                    TABLE_USAGE_TYPE_DESCRIPTION + " TEXT, " +
                    TABLE_USAGE_TYPE_SYSTEM_DEFINED + " TEXT" +
                    ")";

    private static final String TABLE_CREATE_USAGE_TYPE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_USAGE_TYPE_I1 + " ON " + TABLE_USAGE_TYPE + "(" + TABLE_USAGE_TYPE_USAGE_TYPE + ")";

    // Base tables

    // DB Change 2023/04/24 New table DB_VERSION = 6
    public static final String TABLE_CREATE_COMPUTER =
            "CREATE TABLE " + TABLE_COMPUTER + " (" +
                    TABLE_COMPUTER_COMPUTER_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_COMPUTER_DESCRIPTION + " TEXT, " +
                    TABLE_COMPUTER_VENDOR + " TEXT, " +
                    TABLE_COMPUTER_PRODUCT + " TEXT, " +
                    TABLE_COMPUTER_TYPE + " TEXT, " +
                    TABLE_COMPUTER_MODEL + " INTEGER, " +
                    TABLE_COMPUTER_TRANSPORT + " INTEGER, " +
                    TABLE_COMPUTER_SERIAL_NO + " TEXT, " +
                    TABLE_COMPUTER_FW + " TEXT, " +
                    TABLE_COMPUTER_FWID + " TEXT, " +
                    TABLE_COMPUTER_LANGUAGE + " TEXT, " +
                    TABLE_COMPUTER_UNIT + " TEXT, " +
                    TABLE_COMPUTER_CONNECTION_TYPE + " TEXT, " +
                    TABLE_COMPUTER_MAC_ADDRESS + " TEXT," +
                    TABLE_COMPUTER_DEVICE_NAME + " TEXT," +
                    TABLE_COMPUTER_SERVICE + " TEXT," +
                    TABLE_COMPUTER_CHARACTERISTIC_RX + " TEXT," +
                    TABLE_COMPUTER_CHARACTERISTIC_RX_CREDITS + " TEXT," +
                    TABLE_COMPUTER_CHARACTERISTIC_TX + " TEXT," +
                    TABLE_COMPUTER_CHARACTERISTIC_TX_CREDITS + " TEXT" +
                    ")";

    // DB Change 2023/04/24 New table DB_VERSION = 6
    public static final String TABLE_CREATE_COMPUTER_DIVE =
            "CREATE TABLE " + TABLE_COMPUTER_DIVE + " (" +
                    TABLE_COMPUTER_DIVE_COMPUTER_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_COMPUTER_DIVE_COMPUTER_DIVE_NO + " INTEGER, " +
                    TABLE_COMPUTER_DIVE_AVERAGE_DEPTH + " REAL, " +
                    TABLE_COMPUTER_DIVE_MAXIMUM_DEPTH + " REAL, " +
                    TABLE_COMPUTER_DIVE_DATE + " INTEGER, " +
                    TABLE_COMPUTER_DIVE_START_TIME + " INTEGER, " +
                    TABLE_COMPUTER_DIVE_BOTTOM_TIME + " REAL, " +
                    TABLE_COMPUTER_DIVE_SURFACE_INTERVAL + " REAL, " +
                    TABLE_COMPUTER_DIVE_AIR_TEMP + " REAL, " +
                    TABLE_COMPUTER_DIVE_WATER_TEMP_SURFACE + " REAL, " +
                    TABLE_COMPUTER_DIVE_WATER_TEMP_BOTTOM + " REAL, " +
                    TABLE_COMPUTER_DIVE_WATER_TEMP_AVERAGE + " REAL, " +
                    "FOREIGN KEY(" + TABLE_COMPUTER_DIVE_COMPUTER_DIVE_NO + ") REFERENCES " + TABLE_COMPUTER + "(" + TABLE_COMPUTER_COMPUTER_NO + ") ON UPDATE RESTRICT ON DELETE RESTRICT" +
                    ")";

    public static final String TABLE_CREATE_DIVE =
            "CREATE TABLE " + TABLE_DIVE + " (" +
                    TABLE_DIVE_DIVE_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_DIVE_LOG_BOOK_NO + " INTEGER, " +
                    // DB Change 2023/04/24 New column(s) DB_VERSION = 6
                    // No RI needed
                    // The COMPUTER_DIVE_NO only exists for real in the Dive Computer itself
                    // Used to look back in the Dive Computer or other log book
                    TABLE_DIVE_COMPUTER_DIVE_NO + " INTEGER, " +
                    TABLE_DIVE_DATE + " INTEGER, " +
                    TABLE_DIVE_STATUS + " TEXT, " +
                    TABLE_DIVE_DIVE_TYPE + " TEXT, " +
                    TABLE_DIVE_SALINITY + " INTEGER, " +
                    // DB Change 2020/03/22  Change from INTEGER to REAL hold seconds DB_VERSION = 2
                    TABLE_DIVE_BOTTOM_TIME + " REAL, " +
                    TABLE_DIVE_AVERAGE_DEPTH + " REAL, " +
                    TABLE_DIVE_ALTITUDE + " INTEGER, " +
                    // DB Change 2020/03/22 New column(s) DB_VERSION = 2
                    TABLE_DIVE_LOCATION + " TEXT, " +
                    TABLE_DIVE_DIVE_SITE + " TEXT, " +
                    TABLE_DIVE_DIVE_BOAT + " TEXT, " +
                    TABLE_DIVE_PURPOSE + " TEXT, " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    TABLE_DIVE_VISIBILITY + " TEXT, " +
                    TABLE_DIVE_MAXIMUM_DEPTH + " REAL, " +
                    TABLE_DIVE_SUIT + " TEXT, " +
                    TABLE_DIVE_WEIGHT + " REAL, " +
                    TABLE_DIVE_AIR_TEMP + " REAL, " +
                    TABLE_DIVE_WATER_TEMP_SURFACE + " REAL, " +
                    TABLE_DIVE_WATER_TEMP_BOTTOM + " REAL, " +
                    TABLE_DIVE_WATER_TEMP_AVERAGE + " REAL, " +
                    // 2023/08/2 DB_VERSION = 7 Renamed from note to note_summary
                    TABLE_DIVE_NOTE_SUMMARY + " TEXT, " +
                    TABLE_DIVE_ENVIRONMENT + " TEXT, " +
                    TABLE_DIVE_PLATFORM + " TEXT, " +
                    TABLE_DIVE_WEATHER + " TEXT, " +
                    TABLE_DIVE_CONDITION + " TEXT, " +
                    // 2023/08/2 DB_VERSION = 7 New columns
                    TABLE_DIVE_NOTE_ENVIRONMENT + " TEXT, " +
                    TABLE_DIVE_NOTE_GAS + " TEXT, " +
                    TABLE_DIVE_NOTE_GEAR + " TEXT, " +
                    TABLE_DIVE_THERMAL_COMFORT + " TEXT, " +
                    TABLE_DIVE_WORK_LOAD + " TEXT, " +
                    TABLE_DIVE_PROBLEM + " TEXT, " +
                    TABLE_DIVE_MALFUNCTION + " TEXT, " +
                    TABLE_DIVE_ANY_SYMPTOM + " TEXT, " +
                    TABLE_DIVE_EXPOSURE_ALTITUDE + " TEXT, " +
                    TABLE_DIVE_NOTE_PROBLEM + " TEXT, " +
                    "FOREIGN KEY(" + TABLE_DIVE_DIVE_TYPE + ") REFERENCES " + TABLE_DIVE_TYPE + "(" + TABLE_DIVE_TYPE_DIVE_TYPE + ") ON UPDATE RESTRICT ON DELETE CASCADE" +
                    ")";

    public static final String TABLE_CREATE_DIVE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVE_I1 + " ON " + TABLE_DIVE + "(" + TABLE_DIVE_DIVE_NO + ")";

    // No RI on the Diver table since we are deleting a diver with DELETE CASCADE
    // All entries for a Diver will be deleted
    public static final String TABLE_CREATE_DIVER =
            "CREATE TABLE " + TABLE_DIVER + " (" +
                    TABLE_DIVER_DIVER_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_DIVER_FIRST_NAME + " TEXT, " +
                    TABLE_DIVER_MIDDLE_NAME + " TEXT, " +
                    TABLE_DIVER_LAST_NAME + " TEXT, " +
                    TABLE_DIVER_GENDER + " INTEGER, " +
                    TABLE_DIVER_BIRTH_DATE + " INTEGER, " +
                    TABLE_DIVER_PHONE + " TEXT, " +
                    TABLE_DIVER_EMAIL + " TEXT, " +
                    TABLE_DIVER_CERTIFICATION_BODY + " TEXT, " +
                    TABLE_DIVER_CERTIFICATION_LEVEL + " TEXT, " +
                    TABLE_DIVER_MAX_DEPTH_ALLOWED + " REAL" +
                    ")";

    public static final String TABLE_CREATE_DIVER_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVER_I1 + " ON " + TABLE_DIVER + "(" + TABLE_DIVER_DIVER_NO + ")";

    public static final String TABLE_CREATE_GROUPP =
            "CREATE TABLE " + TABLE_GROUPP + " (" +
                    TABLE_GROUP_GROUP_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_GROUP_DIVER_NO + " INTEGER, " +
                    TABLE_GROUP_GROUP_TYPE + " TEXT, " +
                    TABLE_GROUP_DESCRIPTION + " TEXT, " +
                    "FOREIGN KEY(" + TABLE_GROUP_DIVER_NO + ") REFERENCES " + TABLE_DIVER + "(" + TABLE_DIVER_DIVER_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_GROUP_GROUP_TYPE + ") REFERENCES " + TABLE_GROUP_TYPE + "(" + TABLE_GROUP_TYPE_GROUP_TYPE + ") ON UPDATE RESTRICT ON DELETE RESTRICT" +
                    ")";

    public static final String TABLE_CREATE_GROUPP_I1 =
            "CREATE UNIQUE INDEX " + TABLE_GROUPP_I1 + " ON " + TABLE_GROUPP + "(" + TABLE_GROUP_GROUP_NO + ")";

    public static final String TABLE_CREATE_STATE =
            "CREATE TABLE " + TABLE_STATE + " (" +
                    TABLE_STATE_STATE_NO + " INTEGER, " +
                    TABLE_STATE_DIVE_TYPE + " TEXT DEFAULT 'A', " +
                    TABLE_STATE_MY_BUDDY_DIVER_NO + " INTEGER, " +
                    TABLE_STATE_MY_SAC + " REAL, " +
                    TABLE_STATE_MY_RMV + " REAL, " +
                    TABLE_STATE_MY_GROUP + " INTEGER, " +
                    TABLE_STATE_MY_BUDDY_SAC + " REAL, " +
                    TABLE_STATE_MY_BUDDY_RMV + " REAL, " +
                    TABLE_STATE_MY_BUDDY_GROUP + " INTEGER, " +
                    "FOREIGN KEY(" + TABLE_DIVE_DIVE_TYPE + ") REFERENCES " + TABLE_DIVE_TYPE + "(" + TABLE_DIVE_TYPE_DIVE_TYPE + ") ON UPDATE SET DEFAULT ON DELETE SET DEFAULT" +
                    ")";

    // Child tables
    public static final String TABLE_CREATE_DIVE_PLAN =
            "CREATE TABLE " + TABLE_DIVE_PLAN + " (" +
                    TABLE_DIVE_PLAN_DIVE_PLAN_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_DIVE_PLAN_DIVE_NO + " INTEGER, " +
                    TABLE_DIVE_PLAN_ORDER_NO + " INTEGER, " +
                    TABLE_DIVE_PLAN_DEPTH + " REAL, " +
                    TABLE_DIVE_PLAN_MINUTE + " INTEGER, " +
                    "FOREIGN KEY(" + TABLE_DIVE_PLAN_DIVE_NO + ") REFERENCES " + TABLE_DIVE + "(" + TABLE_DIVE_DIVE_NO + ") ON DELETE CASCADE" +
                    ")";

    public static final String TABLE_CREATE_DIVE_PLAN_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVE_PLAN_I1 + " ON " + TABLE_DIVE_PLAN + "(" + TABLE_DIVE_PLAN_DIVE_PLAN_NO + ")";

    // Junction tables
    public static final String TABLE_CREATE_CYLINDER =
            "CREATE TABLE " + TABLE_CYLINDER + " (" +
                    TABLE_CYLINDER_CYLINDER_NO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_CYLINDER_DIVER_NO + " INTEGER, " +
                    TABLE_CYLINDER_CYLINDER_TYPE + " TEXT, " +
                    TABLE_CYLINDER_VOLUME + " REAL, " +
                    TABLE_CYLINDER_RATED_PRESSURE + " REAL, " +
                    TABLE_CYLINDER_BRAND + " TEXT, " +
                    TABLE_CYLINDER_MODEL + " TEXT, " +
                    TABLE_CYLINDER_SERIAL_NO + " TEXT, " +
                    TABLE_CYLINDER_COLOR + " TEXT, " +
                    TABLE_CYLINDER_LAST_VIP + " INTEGER, " +
                    TABLE_CYLINDER_LAST_HYDRO + " INTEGER, " +
                    TABLE_CYLINDER_WEIGHT_FULL + " REAL, " +
                    TABLE_CYLINDER_WEIGHT_EMPTY + " REAL, " +
                    TABLE_CYLINDER_BUOYANCY_FULL + " REAL, " +
                    TABLE_CYLINDER_BUOYANCY_EMPTY + " REAL, " +
                    "FOREIGN KEY(" + TABLE_CYLINDER_DIVER_NO + ") REFERENCES " + TABLE_DIVER + "(" + TABLE_DIVER_DIVER_NO + ") ON DELETE CASCADE, " +
                    // Cannot enforce that FK Constraint because the App does not create the GROUP_CYLINDER row before creating the CYLINDER row
                    //"FOREIGN KEY(" + TABLE_CYLINDER_CYLINDER_NO + ") REFERENCES " + TABLE_GROUP_CYLINDER + "(" + TABLE_GROUP_CYLINDER_CYLINDER_NO + ") ON UPDATE RESTRICT ON DELETE RESTRICT, " +
                    "FOREIGN KEY(" + TABLE_CYLINDER_CYLINDER_TYPE + ") REFERENCES " + TABLE_CYLINDER_TYPE + "(" + TABLE_CYLINDER_TYPE_CYLINDER_TYPE + ") ON UPDATE RESTRICT ON DELETE RESTRICT" +
                    ")";

    public static final String TABLE_CREATE_CYLINDER_I1 =
            "CREATE UNIQUE INDEX " + TABLE_CYLINDER_I1 + " ON " + TABLE_CYLINDER + "(" + TABLE_CYLINDER_CYLINDER_NO + ")";

    public static final String TABLE_CREATE_DIVE_SEGMENT =
            "CREATE TABLE " + TABLE_DIVE_SEGMENT + " (" +
                    TABLE_DIVE_SEGMENT_DIVER_NO + " INTEGER, " +
                    TABLE_DIVE_SEGMENT_DIVE_NO + " INTEGER, " +
                    TABLE_DIVE_SEGMENT_ORDER_NO + " INTEGER, " +
                    TABLE_DIVE_SEGMENT_SEGMENT_TYPE + " TEXT, " +
                    TABLE_DIVE_SEGMENT_DEPTH + " REAL, " +
                    TABLE_DIVE_SEGMENT_MINUTE + " REAL, " +
                    TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE + " REAL, " +
                    TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME + " REAL, " +
                    TABLE_DIVE_SEGMENT_CALC_ATA + " REAL, " +
                    TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH + " REAL, " +
                    TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA + " REAL, " +
                    TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE + " INTEGER, " +
                    TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE + " INTEGER, " +
                    TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE + " REAL, " +
                    TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME + " REAL, " +
                    "PRIMARY KEY(" + TABLE_DIVE_SEGMENT_DIVER_NO +", " + TABLE_DIVE_SEGMENT_DIVE_NO +", " + TABLE_DIVE_SEGMENT_ORDER_NO + "), " +
                    "FOREIGN KEY(" + TABLE_DIVE_SEGMENT_DIVER_NO + ") REFERENCES " + TABLE_DIVER + "(" + TABLE_DIVER_DIVER_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_DIVE_SEGMENT_DIVE_NO + ") REFERENCES " + TABLE_DIVE + "(" + TABLE_DIVE_DIVE_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_DIVE_SEGMENT_SEGMENT_TYPE + ") REFERENCES " + TABLE_SEGMENT_TYPE + "(" + TABLE_SEGMENT_TYPE_SEGMENT_TYPE + ") ON UPDATE RESTRICT ON DELETE RESTRICT" +
                    ")";

    public static final String TABLE_CREATE_DIVE_SEGMENT_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVE_SEGMENT_I1 + " ON " + TABLE_DIVE_SEGMENT + "(" + TABLE_DIVE_SEGMENT_DIVER_NO + ", " + TABLE_DIVE_SEGMENT_DIVE_NO + ", " + TABLE_DIVE_SEGMENT_ORDER_NO + ")";

    public static final String TABLE_CREATE_DIVER_DIVE =
            "CREATE TABLE " + TABLE_DIVER_DIVE + " (" +
                    TABLE_DIVER_DIVE_DIVER_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_DIVE_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_RMV + " REAL, " +
                    // 2020/02/22 New column(s) DB_VERSION = 2
                    TABLE_DIVER_DIVE_IS_PRIMARY + " TEXT, " +
                    "PRIMARY KEY(" + TABLE_DIVER_DIVE_DIVER_NO +", " + TABLE_DIVER_DIVE_DIVE_NO + "), " +
                    "FOREIGN KEY(" + TABLE_DIVER_DIVE_DIVER_NO + ") REFERENCES " + TABLE_DIVER + "(" + TABLE_DIVER_DIVER_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_DIVER_DIVE_DIVE_NO + ") REFERENCES " + TABLE_DIVE + "(" + TABLE_DIVE_DIVE_NO + ") ON DELETE CASCADE" +
                    ")";

    public static final String TABLE_CREATE_DIVER_DIVE_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVER_DIVE_I1 + " ON " + TABLE_DIVER_DIVE + "(" + TABLE_DIVER_DIVE_DIVER_NO + ", " + TABLE_DIVER_DIVE_DIVE_NO + ")";

    public static final String TABLE_CREATE_DIVER_DIVE_GROUP =
            "CREATE TABLE " + TABLE_DIVER_DIVE_GROUP + " (" +
                    TABLE_DIVER_DIVE_GROUP_DIVER_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_DIVE_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_GROUP_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_SAC + " REAL, " +
                    "PRIMARY KEY(" + TABLE_DIVER_DIVE_GROUP_DIVER_NO + ", " + TABLE_DIVER_DIVE_GROUP_DIVE_NO + ", " + TABLE_DIVER_DIVE_GROUP_GROUP_NO + "), " +
                    "FOREIGN KEY(" + TABLE_DIVER_DIVE_GROUP_DIVER_NO + ", " + TABLE_DIVER_DIVE_GROUP_DIVE_NO + ") REFERENCES " + TABLE_DIVER_DIVE + "(" + TABLE_DIVER_DIVE_DIVER_NO + ", " + TABLE_DIVER_DIVE_DIVE_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_DIVER_DIVE_GROUP_GROUP_NO + ") REFERENCES " + TABLE_GROUPP + "(" + TABLE_GROUP_GROUP_NO + ") ON UPDATE RESTRICT ON DELETE RESTRICT" +
                    ")";

    public static final String TABLE_CREATE_DIVER_DIVE_GROUP_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVER_DIVE_GROUP_I1 + " ON " + TABLE_DIVER_DIVE_GROUP + "(" + TABLE_DIVER_DIVE_GROUP_DIVER_NO + ", " + TABLE_DIVER_DIVE_GROUP_DIVE_NO + ", " + TABLE_DIVER_DIVE_GROUP_GROUP_NO + ")";

    public static final String TABLE_CREATE_DIVER_DIVE_GROUP_CYLINDER =
            "CREATE TABLE " + TABLE_DIVER_DIVE_GROUP_CYLINDER + " (" +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE + " REAL, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE + " REAL, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_O2 + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_N + " INTEGER, " +
                    TABLE_DIVER_DIVE_GROUP_CYLINDER_HE + " INTEGER, " +
                    "PRIMARY KEY(" + TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO + "), " +
                    "FOREIGN KEY(" + TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO+ ") REFERENCES " + TABLE_DIVER_DIVE_GROUP + "(" + TABLE_DIVER_DIVE_DIVER_NO + ", " + TABLE_DIVER_DIVE_DIVE_NO + ", " + TABLE_DIVER_DIVE_GROUP_GROUP_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO + ") REFERENCES " + TABLE_GROUP_CYLINDER + "(" + TABLE_GROUP_CYLINDER_GROUP_NO + ", " + TABLE_GROUP_CYLINDER_CYLINDER_NO + ") ON DELETE CASCADE " +
                    ")";

    public static final String TABLE_CREATE_DIVER_DIVE_GROUP_CYLINDER_I1 =
            "CREATE UNIQUE INDEX " + TABLE_DIVER_DIVE_GROUP_CYLINDER_I1 + " ON " + TABLE_DIVER_DIVE_GROUP_CYLINDER + "(" + TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO + ", " + TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO+ ")";

    public static final String TABLE_CREATE_GROUP_CYLINDER =
            "CREATE TABLE " + TABLE_GROUP_CYLINDER + " (" +
                    TABLE_GROUP_CYLINDER_GROUP_NO + " INTEGER, " +
                    TABLE_GROUP_CYLINDER_CYLINDER_NO + " INTEGER, " +
                    TABLE_GROUP_CYLINDER_USAGE_TYPE + " TEXT, " +
                    "PRIMARY KEY(" + TABLE_GROUP_CYLINDER_GROUP_NO +", " + TABLE_GROUP_CYLINDER_CYLINDER_NO + "), " +
                    "FOREIGN KEY(" + TABLE_GROUP_CYLINDER_GROUP_NO + ") REFERENCES " + TABLE_GROUPP + "(" + TABLE_GROUP_GROUP_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_GROUP_CYLINDER_CYLINDER_NO + ") REFERENCES " + TABLE_CYLINDER + "(" + TABLE_CYLINDER_CYLINDER_NO + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + TABLE_GROUP_CYLINDER_USAGE_TYPE + ") REFERENCES " + TABLE_USAGE_TYPE + "(" + TABLE_USAGE_TYPE_USAGE_TYPE + ") ON UPDATE RESTRICT ON DELETE RESTRICT" +
                    ")";

    public static final String TABLE_CREATE_GROUP_CYLINDER_I1 =
            "CREATE UNIQUE INDEX " + TABLE_GROUP_CYLINDER_I1 + " ON " + TABLE_GROUP_CYLINDER + "(" + TABLE_GROUP_CYLINDER_GROUP_NO + ", " + TABLE_GROUP_CYLINDER_CYLINDER_NO + ")";

    private static AirDBHelper sInstance;

    // Public

    // Protected

    // Private

    // End of variables

    // Constructor should be private to prevent direct instantiation.
    // Make call to static method "getInstance()" instead.

    // Private constructor
    private AirDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

    }

    public static synchronized AirDBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new AirDBHelper(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Code tables
        db.execSQL(TABLE_CREATE_CYLINDER_TYPE);
        db.execSQL(TABLE_CREATE_CYLINDER_TYPE_I1);
        db.execSQL(TABLE_CREATE_CYLINDER_TYPE_TRIGGER);
        db.execSQL(TABLE_CREATE_DIVE_TYPE);
        db.execSQL(TABLE_CREATE_DIVE_TYPE_I1);
        db.execSQL(TABLE_CREATE_GROUP_TYPE);
        db.execSQL(TABLE_CREATE_GROUP_TYPE_I1);
        db.execSQL(TABLE_CREATE_SEGMENT_TYPE);
        db.execSQL(TABLE_CREATE_SEGMENT_TYPE_I1);
        db.execSQL(TABLE_CREATE_USAGE_TYPE);
        db.execSQL(TABLE_CREATE_USAGE_TYPE_I1);
        // DB Change 2020/04/01 New table DB_VERSION = 2
        db.execSQL(TABLE_CREATE_DYNAMIC_SPINNER);

        // Base tables
        db.execSQL(TABLE_CREATE_DIVE);
        db.execSQL(TABLE_CREATE_DIVE_I1);
        db.execSQL(TABLE_CREATE_DIVER);
        db.execSQL(TABLE_CREATE_DIVER_I1);
        db.execSQL(TABLE_CREATE_GROUPP);
        db.execSQL(TABLE_CREATE_GROUPP_I1);
        db.execSQL(TABLE_CREATE_STATE);
        // No index for table STATE
        // DB Change 2023/04/25 New table DB_VERSION = 6
        db.execSQL(TABLE_CREATE_COMPUTER);
        // No index for table COMPUTER
        // Child tables
        db.execSQL(TABLE_CREATE_DIVE_PLAN);
        db.execSQL(TABLE_CREATE_DIVE_PLAN_I1);
        // Junction tables
        db.execSQL(TABLE_CREATE_CYLINDER);
        db.execSQL(TABLE_CREATE_CYLINDER_I1);
        db.execSQL(TABLE_CREATE_DIVE_SEGMENT);
        db.execSQL(TABLE_CREATE_DIVE_SEGMENT_I1);
        db.execSQL(TABLE_CREATE_DIVER_DIVE);
        db.execSQL(TABLE_CREATE_DIVER_DIVE_I1);
        db.execSQL(TABLE_CREATE_DIVER_DIVE_GROUP);
        db.execSQL(TABLE_CREATE_DIVER_DIVE_GROUP_I1);
        db.execSQL(TABLE_CREATE_DIVER_DIVE_GROUP_CYLINDER);
        db.execSQL(TABLE_CREATE_DIVER_DIVE_GROUP_CYLINDER_I1);
        db.execSQL(TABLE_CREATE_GROUP_CYLINDER);
        db.execSQL(TABLE_CREATE_GROUP_CYLINDER_I1);

        Log.i(LOG_TAG, "All Tables have been created");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion <  2) {
            upgradeVersion2(db);
        } else if (oldVersion == 1 && newVersion == 3) {
            upgradeVersion2(db);
            upgradeVersion3(db);
        } else if (oldVersion == 1 && newVersion == 4) {
            upgradeVersion2(db);
            upgradeVersion3(db);
            upgradeVersion4(db);
        } else if (oldVersion == 1 && newVersion == 5) {
            upgradeVersion2(db);
            upgradeVersion3(db);
            upgradeVersion4(db);
            upgradeVersion5(db);
        } else if (oldVersion == 1 && newVersion == 6) {
            upgradeVersion2(db);
            upgradeVersion3(db);
            upgradeVersion4(db);
            upgradeVersion5(db);
            upgradeVersion6(db);
        } else if (oldVersion == 2 && newVersion == 3) {
            upgradeVersion3(db);
        } else if (oldVersion == 2 && newVersion == 4) {
            upgradeVersion3(db);
            upgradeVersion4(db);
        } else if (oldVersion == 2 && newVersion == 5) {
            upgradeVersion3(db);
            upgradeVersion4(db);
            upgradeVersion5(db);
        } else if (oldVersion == 2 && newVersion == 6) {
            upgradeVersion3(db);
            upgradeVersion4(db);
            upgradeVersion5(db);
            upgradeVersion6(db);
        } else if (oldVersion == 3 && newVersion == 4) {
            upgradeVersion4(db);
        } else if (oldVersion == 3 && newVersion == 5) {
            upgradeVersion4(db);
            upgradeVersion5(db);
        } else if (oldVersion == 3 && newVersion == 6) {
            upgradeVersion4(db);
            upgradeVersion5(db);
            upgradeVersion6(db);
        } else if (oldVersion == 4 && newVersion == 5) {
            upgradeVersion5(db);
        } else if (oldVersion == 4 && newVersion == 6) {
            upgradeVersion5(db);
            upgradeVersion6(db);
        } else if (oldVersion == 5 && newVersion == 6) {
            upgradeVersion6(db);
        } else if (oldVersion == 6 && newVersion == 7) {
            upgradeVersion7(db);
        }

        Log.i(LOG_TAG, "All Tables have been dropped");
    }

    private void upgradeVersion2(SQLiteDatabase db) {
        // App version 1.01.00
        try {
            Log.d(LOG_TAG, "upgradeVersion2");

            // DIVE table
            db.execSQL("ALTER TABLE " + TABLE_DIVE + " RENAME TO " + TABLE_DIVE + "_BACK;");

            db.execSQL(TABLE_CREATE_DIVE);

            String insertTable = "INSERT INTO " + TABLE_DIVE + "(" +
                    TABLE_DIVE_DIVE_NO + ", " +
                    TABLE_DIVE_LOG_BOOK_NO + ", " +
                    TABLE_DIVE_DATE + ", " +
                    TABLE_DIVE_STATUS + ", " +
                    TABLE_DIVE_DIVE_TYPE + ", " +
                    TABLE_DIVE_SALINITY + ", " +
                    // DB Change 2020/03/22 Change from INTEGER to REAL hold seconds
                    TABLE_DIVE_BOTTOM_TIME + ", " +
                    TABLE_DIVE_AVERAGE_DEPTH + ", " +
                    TABLE_DIVE_ALTITUDE + ", " +
                    // DB Change 2020/03/22 New column(s) DB_VERSION = 2
                    TABLE_DIVE_LOCATION + ", " +
                    TABLE_DIVE_DIVE_SITE + ", " +
                    TABLE_DIVE_DIVE_BOAT + ", " +
                    TABLE_DIVE_PURPOSE + ", " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    TABLE_DIVE_VISIBILITY + ", " +
                    TABLE_DIVE_MAXIMUM_DEPTH + ", " +
                    TABLE_DIVE_SUIT + ", " +
                    TABLE_DIVE_WEIGHT + ", " +
                    TABLE_DIVE_AIR_TEMP + ", " +
                    TABLE_DIVE_WATER_TEMP_SURFACE + ", " +
                    TABLE_DIVE_WATER_TEMP_BOTTOM + ", " +
                    TABLE_DIVE_WATER_TEMP_AVERAGE + ", " +
                    TABLE_DIVE_NOTE + ", " +
                    TABLE_DIVE_ENVIRONMENT + ", " +
                    TABLE_DIVE_PLATFORM + ", " +
                    TABLE_DIVE_WEATHER + ", " +
                    TABLE_DIVE_CONDITION + ") " +
                    "SELECT " +
                    TABLE_DIVE_DIVE_NO + ", " +
                    TABLE_DIVE_LOG_BOOK_NO + ", " +
                    TABLE_DIVE_DATE + ", " +
                    TABLE_DIVE_STATUS + ", " +
                    TABLE_DIVE_DIVE_TYPE + ", " +
                    TABLE_DIVE_SALINITY + ", " +
                    // DB Change 2002/03/22 Change from INTEGER to REAL hold seconds
                    TABLE_DIVE_BOTTOM_TIME + ", " +
                    TABLE_DIVE_AVERAGE_DEPTH + ", " +
                    TABLE_DIVE_ALTITUDE + ", " +
                    // DB Change 2020/03/22 New column(s) DB_VERSION = 2
                    "' ' AS LOCATION, " +
                    "' ' AS DIVE_SITE, " +
                    "' ' AS DIVE_BOAT, " +
                    "' ' AS PURPOSE,  " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    "' ' AS VISIBILITY, " +
                    "'0.0' AS MAXIMUM_DEPTH, " +
                    "' ' AS SUIT, " +
                    "'0.0' AS WEIGHT,  " +
                    "'0' AS AIR_TEMP, " +
                    "'0' AS WATER_TEMP_SURFACE, " +
                    "'0' AS WATER_TEMP_BOTTOM, " +
                    "'0' AS WATER_TEMP_AVERAGE, " +
                    "' ' AS NOTE, " +
                    "' ' AS ENVIRONMENT, " +
                    "' ' AS PLATFORM, " +
                    "' ' AS WEATHER, " +
                    "' ' AS CONDITION " +
                    "FROM " + TABLE_DIVE + "_BACK;";

            db.execSQL(insertTable);

            db.execSQL("DROP TABLE " + TABLE_DIVE + "_BACK;");

            // DIVER_DIVE table
            db.execSQL("ALTER TABLE " + TABLE_DIVER_DIVE + " RENAME TO " + TABLE_DIVER_DIVE + "_BACK;");

            db.execSQL(TABLE_CREATE_DIVER_DIVE);

            insertTable = "INSERT INTO " + TABLE_DIVER_DIVE + "(" +
                    TABLE_DIVER_DIVE_DIVER_NO + ", " +
                    TABLE_DIVER_DIVE_DIVE_NO + ", " +
                    TABLE_DIVER_DIVE_RMV + ", " +
                    // DB Change 2020/03/22 New column(s) DB_VERSION = 2
                    TABLE_DIVER_DIVE_IS_PRIMARY + ") " +
                    "SELECT " +
                    TABLE_DIVER_DIVE_DIVER_NO + ", " +
                    TABLE_DIVER_DIVE_DIVE_NO + ", " +
                    TABLE_DIVER_DIVE_RMV + ", " +
                    // DB Change 2020/03/22 New column(s) DB_VERSION = 2
                    "CASE WHEN diver_no = 0 THEN 'M' " +
                    "ELSE 'Y' " +
                    "END AS IS_PRIMARY " +
                    "FROM " + TABLE_DIVER_DIVE + "_BACK;";

            db.execSQL(insertTable);

            db.execSQL("DROP TABLE " + TABLE_DIVER_DIVE + "_BACK;");

            // CYLINDER table
            db.execSQL("ALTER TABLE " + TABLE_CYLINDER + " RENAME TO " + TABLE_CYLINDER + "_BACK;");

            db.execSQL(TABLE_CREATE_CYLINDER);

            insertTable = "INSERT INTO " + TABLE_CYLINDER + "(" +
                    TABLE_CYLINDER_CYLINDER_NO + ", " +
                    TABLE_CYLINDER_DIVER_NO + ", " +
                    TABLE_CYLINDER_CYLINDER_TYPE + ", " +
                    TABLE_CYLINDER_VOLUME + ", " +
                    TABLE_CYLINDER_RATED_PRESSURE + ", " +
                    TABLE_CYLINDER_BRAND + ", " +
                    TABLE_CYLINDER_MODEL + ", " +
                    TABLE_CYLINDER_SERIAL_NO + ", " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    TABLE_CYLINDER_LAST_VIP + ", " +
                    TABLE_CYLINDER_LAST_HYDRO + ", " +
                    TABLE_CYLINDER_COLOR + ", " +
                    TABLE_CYLINDER_WEIGHT_FULL + ", " +
                    TABLE_CYLINDER_WEIGHT_EMPTY + ", " +
                    TABLE_CYLINDER_BUOYANCY_FULL + ", " +
                    TABLE_CYLINDER_BUOYANCY_EMPTY + ") " +
                    "SELECT " +
                    TABLE_CYLINDER_CYLINDER_NO + ", " +
                    TABLE_CYLINDER_DIVER_NO + ", " +
                    TABLE_CYLINDER_CYLINDER_TYPE + ", " +
                    TABLE_CYLINDER_VOLUME + ", " +
                    TABLE_CYLINDER_RATED_PRESSURE + ", " +
                    "IFNULL(" + TABLE_CYLINDER_BRAND + ", ' '), " +
                    "IFNULL(" + TABLE_CYLINDER_MODEL + ", ' '), " +
                    "IFNULL(" + TABLE_CYLINDER_SERIAL_NO + ", ' '), " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    "0 AS LAST_VIP, " +
                    "0 AS LAST_HYDRO, " +
                    "' ' AS COLOR, " +
                    "0.0 AS WEIGHT_FULL, " +
                    "0.0 AS WEIGHT_EMPTY, " +
                    "0.0 AS BUOYANCY_FULL, " +
                    "0.0 AS BUOYANCY_EMPTY " +
                    "FROM " + TABLE_CYLINDER + "_BACK;";

            db.execSQL(insertTable);

            db.execSQL("DROP TABLE " + TABLE_CYLINDER + "_BACK;");

            // SEGMENT_TYPE table
            db.execSQL("ALTER TABLE " + TABLE_SEGMENT_TYPE + " RENAME TO " + TABLE_SEGMENT_TYPE + "_BACK;");

            db.execSQL(TABLE_CREATE_SEGMENT_TYPE);

            insertTable = "INSERT INTO " + TABLE_SEGMENT_TYPE + "(" +
                    TABLE_SEGMENT_TYPE_SEGMENT_TYPE + ", " +
                    TABLE_SEGMENT_TYPE_DESCRIPTION + ", " +
                    TABLE_SEGMENT_TYPE_ORDER_NO + ", " +
                    TABLE_SEGMENT_TYPE_DIRECTION + ", " +
                    TABLE_SEGMENT_TYPE_SHOW_RESULT + ", " +
                    TABLE_SEGMENT_TYPE_SYSTEM_DEFINED + ", " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    TABLE_SEGMENT_TYPE_STATUS + ") " +
                    "SELECT " +
                    TABLE_SEGMENT_TYPE_SEGMENT_TYPE + ", " +
                    TABLE_SEGMENT_TYPE_DESCRIPTION + ", " +
                    TABLE_SEGMENT_TYPE_ORDER_NO + ", " +
                    TABLE_SEGMENT_TYPE_DIRECTION + ", " +
                    TABLE_SEGMENT_TYPE_SHOW_RESULT + ", " +
                    TABLE_SEGMENT_TYPE_SYSTEM_DEFINED + ", " +
                    // DB Change 2020/03/30 New column(s) DB_VERSION = 2
                    "'A' AS STATUS  " +
                    "FROM " + TABLE_SEGMENT_TYPE + "_BACK;";

            db.execSQL(insertTable);

            db.execSQL("DROP TABLE " + TABLE_SEGMENT_TYPE + "_BACK;");

            // DYNAMIC_SPINNER table
            db.execSQL(TABLE_CREATE_DYNAMIC_SPINNER);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void upgradeVersion3(SQLiteDatabase db) {
        // App version 1.01.02
        try {
            Log.d(LOG_TAG, "upgradeVersion3");

            // SEGMENT table
            String insertTable = "INSERT INTO " + TABLE_SEGMENT_TYPE + "(" +
                    TABLE_SEGMENT_TYPE_SEGMENT_TYPE + ", " +
                    TABLE_SEGMENT_TYPE_DESCRIPTION + ", " +
                    TABLE_SEGMENT_TYPE_ORDER_NO + ", " +
                    TABLE_SEGMENT_TYPE_DIRECTION + ", " +
                    TABLE_SEGMENT_TYPE_SHOW_RESULT + ", " +
                    TABLE_SEGMENT_TYPE_SYSTEM_DEFINED + ", " +
                    TABLE_SEGMENT_TYPE_STATUS + ") " +
                    "SELECT " +
                    "'BC' AS SEGMENT_TYPE, " +
                    "'Bubble Check' AS DESCRIPTION, " +
                    "5 AS ORDER_NO, " +
                    "'B' AS DIRECTION, " +
                    "'Y' AS SHOW_RESULT, " +
                    "'Y' AS SYSTEM_DEFINED, " +
                    "'A' AS STATUS;";

            db.execSQL(insertTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void upgradeVersion4(SQLiteDatabase db) {
        // App version 1.02.00
        try {
            Log.d(LOG_TAG, "upgradeVersion4");

            // DB Change 2020/04/23 New table DB_VERSION = 4
            // The only DB change is the re-sorting of table DIVE_PLAN

            // Take a backup
            db.execSQL("ALTER TABLE " + TABLE_DIVE_PLAN + " RENAME TO " + TABLE_DIVE_PLAN + "_BACK;");

            // Recreate the DIVE_PLAN table
            db.execSQL(TABLE_CREATE_DIVE_PLAN);

            // Insert rows with new DEPTH DESC
            String insertTable = "INSERT INTO " + TABLE_DIVE_PLAN + "(" +
                    TABLE_DIVE_PLAN_DIVE_PLAN_NO + ", " +
                    TABLE_DIVE_PLAN_DIVE_NO + ", " +
                    TABLE_DIVE_PLAN_ORDER_NO + ", " +
                    TABLE_DIVE_PLAN_DEPTH + ", " +
                    TABLE_DIVE_PLAN_MINUTE + ") " +
                    "WITH old_rows AS " +
                    "(SELECT dive_plan_no " +
                    ",dive_no " +
                    ",12000 - order_no AS order_no " +
                    ",depth " +
                    ",minute " +
                    "FROM dive_plan_back dp " +
                    "ORDER BY 12000 - order_no) " +
                    "SELECT dive_plan_no " +
                    ",dive_no " +
                    ",((SELECT COUNT(*) " +
                    "FROM old_rows dp2 " +
                    "WHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) AS order_no " +
                    ",depth " +
                    ",minute " +
                    "FROM old_rows dp;";

            db.execSQL(insertTable);

            // Drop backup table
            db.execSQL("DROP TABLE " + TABLE_DIVE_PLAN + "_BACK;");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void upgradeVersion5(SQLiteDatabase db) {
        // App version 1.05.00
        try {
            Log.d(LOG_TAG, "upgradeVersion5");

            db.execSQL(TABLE_CREATE_CYLINDER_TYPE_TRIGGER);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void upgradeVersion6(SQLiteDatabase db) {
        // App version 1.01.00
        try {
            Log.d(LOG_TAG, "upgradeVersion6");

            // DIVE table
            db.execSQL("ALTER TABLE " + TABLE_DIVE + " RENAME TO " + TABLE_DIVE + "_BACK;");

            db.execSQL(TABLE_CREATE_DIVE);

            String insertTable = "INSERT INTO " + TABLE_DIVE + "(" +
                    TABLE_DIVE_DIVE_NO + ", " +
                    TABLE_DIVE_LOG_BOOK_NO + ", " +
                    // DB Change 2023/04/24 New column(s) DB_VERSION = 6
                    TABLE_DIVE_COMPUTER_DIVE_NO + ", " +
                    TABLE_DIVE_DATE + ", " +
                    TABLE_DIVE_STATUS + ", " +
                    TABLE_DIVE_DIVE_TYPE + ", " +
                    TABLE_DIVE_SALINITY + ", " +
                    TABLE_DIVE_BOTTOM_TIME + ", " +
                    TABLE_DIVE_AVERAGE_DEPTH + ", " +
                    TABLE_DIVE_ALTITUDE + ", " +
                    TABLE_DIVE_LOCATION + ", " +
                    TABLE_DIVE_DIVE_SITE + ", " +
                    TABLE_DIVE_DIVE_BOAT + ", " +
                    TABLE_DIVE_PURPOSE + ", " +
                    TABLE_DIVE_VISIBILITY + ", " +
                    TABLE_DIVE_MAXIMUM_DEPTH + ", " +
                    TABLE_DIVE_SUIT + ", " +
                    TABLE_DIVE_WEIGHT + ", " +
                    TABLE_DIVE_AIR_TEMP + ", " +
                    TABLE_DIVE_WATER_TEMP_SURFACE + ", " +
                    TABLE_DIVE_WATER_TEMP_BOTTOM + ", " +
                    TABLE_DIVE_WATER_TEMP_AVERAGE + ", " +
                    TABLE_DIVE_NOTE + ", " +
                    TABLE_DIVE_ENVIRONMENT + ", " +
                    TABLE_DIVE_PLATFORM + ", " +
                    TABLE_DIVE_WEATHER + ", " +
                    TABLE_DIVE_CONDITION + ") " +
                    "SELECT " +
                    TABLE_DIVE_DIVE_NO + ", " +
                    TABLE_DIVE_LOG_BOOK_NO + ", " +
                    // DB Change 2023/04/24 New column(s) DB_VERSION = 6 Set to zero
                    "'0' AS COMPUTER_DIVE_NO, " +
                    TABLE_DIVE_DATE + ", " +
                    TABLE_DIVE_STATUS + ", " +
                    TABLE_DIVE_DIVE_TYPE + ", " +
                    TABLE_DIVE_SALINITY + ", " +
                    TABLE_DIVE_BOTTOM_TIME + ", " +
                    TABLE_DIVE_AVERAGE_DEPTH + ", " +
                    TABLE_DIVE_ALTITUDE + ", " +
                    TABLE_DIVE_LOCATION + ", " +
                    TABLE_DIVE_DIVE_SITE + ", " +
                    TABLE_DIVE_DIVE_BOAT + ", " +
                    TABLE_DIVE_PURPOSE + ", " +
                    TABLE_DIVE_VISIBILITY + ", " +
                    TABLE_DIVE_MAXIMUM_DEPTH + ", " +
                    TABLE_DIVE_SUIT + ", " +
                    TABLE_DIVE_WEIGHT + ", " +
                    TABLE_DIVE_AIR_TEMP + ", " +
                    TABLE_DIVE_WATER_TEMP_SURFACE + ", " +
                    TABLE_DIVE_WATER_TEMP_BOTTOM + ", " +
                    TABLE_DIVE_WATER_TEMP_AVERAGE + ", " +
                    // 2023/08/2 DB_VERSION = 7 Renamed from NOTE to NOTE_SUMMARY
                    TABLE_DIVE_NOTE_SUMMARY + ", " +
                    TABLE_DIVE_ENVIRONMENT + ", " +
                    TABLE_DIVE_PLATFORM + ", " +
                    TABLE_DIVE_WEATHER + ", " +
                    TABLE_DIVE_CONDITION + " " +
                    // 2023/08/2 DB_VERSION = 7 New columns
                    TABLE_DIVE_NOTE_ENVIRONMENT + ", " +
                    TABLE_DIVE_NOTE_GAS + ", " +
                    TABLE_DIVE_NOTE_GEAR + ", " +
                    TABLE_DIVE_THERMAL_COMFORT + ", " +
                    TABLE_DIVE_WORK_LOAD + ", " +
                    TABLE_DIVE_PROBLEM + ", " +
                    TABLE_DIVE_MALFUNCTION + ", " +
                    TABLE_DIVE_ANY_SYMPTOM + ", " +
                    TABLE_DIVE_EXPOSURE_ALTITUDE + ", " +
                    TABLE_DIVE_NOTE_PROBLEM + " " +
                    "FROM " + TABLE_DIVE + "_BACK;";

            db.execSQL(insertTable);

            db.execSQL("DROP TABLE " + TABLE_DIVE + "_BACK;");

            // DIVE table
            db.execSQL(TABLE_CREATE_DIVE);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void upgradeVersion7(SQLiteDatabase db) {
        // App version 1.07.00
        try {
            Log.d(LOG_TAG, "upgradeVersion7");

            // Take a backup
            db.execSQL("ALTER TABLE " + TABLE_DIVE + " RENAME TO " + TABLE_DIVE + "_BACK;");

            // Recreate the DIVE table
            db.execSQL(TABLE_CREATE_DIVE);

            // Insert rows with the new renamed and new columns
            String insertTable = "INSERT INTO " + TABLE_DIVE + "(" +
                    TABLE_DIVE_DIVE_NO + ", " +
                    TABLE_DIVE_LOG_BOOK_NO + ", " +
                    // DB Change 2023/04/24 New column(s) DB_VERSION = 6
                    TABLE_DIVE_COMPUTER_DIVE_NO + ", " +
                    TABLE_DIVE_DATE + ", " +
                    TABLE_DIVE_STATUS + ", " +
                    TABLE_DIVE_DIVE_TYPE + ", " +
                    TABLE_DIVE_SALINITY + ", " +
                    TABLE_DIVE_BOTTOM_TIME + ", " +
                    TABLE_DIVE_AVERAGE_DEPTH + ", " +
                    TABLE_DIVE_ALTITUDE + ", " +
                    TABLE_DIVE_LOCATION + ", " +
                    TABLE_DIVE_DIVE_SITE + ", " +
                    TABLE_DIVE_DIVE_BOAT + ", " +
                    TABLE_DIVE_PURPOSE + ", " +
                    TABLE_DIVE_VISIBILITY + ", " +
                    TABLE_DIVE_MAXIMUM_DEPTH + ", " +
                    TABLE_DIVE_SUIT + ", " +
                    TABLE_DIVE_WEIGHT + ", " +
                    TABLE_DIVE_AIR_TEMP + ", " +
                    TABLE_DIVE_WATER_TEMP_SURFACE + ", " +
                    TABLE_DIVE_WATER_TEMP_BOTTOM + ", " +
                    TABLE_DIVE_WATER_TEMP_AVERAGE + ", " +
                    // 2023/08/2 DB_VERSION = 7 Renamed from NOTE to NOTE_SUMMARY
                    TABLE_DIVE_NOTE_SUMMARY + ", " +
                    TABLE_DIVE_ENVIRONMENT + ", " +
                    TABLE_DIVE_PLATFORM + ", " +
                    TABLE_DIVE_WEATHER + ", " +
                    TABLE_DIVE_CONDITION + ", " +
                    // 2023/08/2 DB_VERSION = 7 New columns
                    TABLE_DIVE_NOTE_ENVIRONMENT + ", " +
                    TABLE_DIVE_NOTE_GAS + ", " +
                    TABLE_DIVE_NOTE_GEAR + ", " +
                    TABLE_DIVE_THERMAL_COMFORT + ", " +
                    TABLE_DIVE_WORK_LOAD + ", " +
                    TABLE_DIVE_PROBLEM + ", " +
                    TABLE_DIVE_MALFUNCTION + ", " +
                    TABLE_DIVE_ANY_SYMPTOM + ", " +
                    TABLE_DIVE_EXPOSURE_ALTITUDE + ", " +
                    TABLE_DIVE_NOTE_PROBLEM + ") " +
                    "SELECT " +
                    TABLE_DIVE_DIVE_NO + ", " +
                    TABLE_DIVE_LOG_BOOK_NO + ", " +
                    TABLE_DIVE_COMPUTER_DIVE_NO + ", " +
                    TABLE_DIVE_DATE + ", " +
                    TABLE_DIVE_STATUS + ", " +
                    TABLE_DIVE_DIVE_TYPE + ", " +
                    TABLE_DIVE_SALINITY + ", " +
                    TABLE_DIVE_BOTTOM_TIME + ", " +
                    TABLE_DIVE_AVERAGE_DEPTH + ", " +
                    TABLE_DIVE_ALTITUDE + ", " +
                    TABLE_DIVE_LOCATION + ", " +
                    TABLE_DIVE_DIVE_SITE + ", " +
                    TABLE_DIVE_DIVE_BOAT + ", " +
                    TABLE_DIVE_PURPOSE + ", " +
                    TABLE_DIVE_VISIBILITY + ", " +
                    TABLE_DIVE_MAXIMUM_DEPTH + ", " +
                    TABLE_DIVE_SUIT + ", " +
                    TABLE_DIVE_WEIGHT + ", " +
                    TABLE_DIVE_AIR_TEMP + ", " +
                    TABLE_DIVE_WATER_TEMP_SURFACE + ", " +
                    TABLE_DIVE_WATER_TEMP_BOTTOM + ", " +
                    TABLE_DIVE_WATER_TEMP_AVERAGE + ", " +
                    // 2023/08/2 DB_VERSION = 7 Renamed from NOTE to NOTE_SUMMARY
                    TABLE_DIVE_NOTE + ", " +
                    TABLE_DIVE_ENVIRONMENT + ", " +
                    TABLE_DIVE_PLATFORM + ", " +
                    TABLE_DIVE_WEATHER + ", " +
                    TABLE_DIVE_CONDITION + ", " +
                    // 2023/08/2 DB_VERSION = 7 New columns
                    "' ' AS NOTE_ENVIRONMENT, " +
                    "' ' AS NOTE_GAS, " +
                    "' ' AS NOTE_GEAR, " +
                    "' ' AS THERMAL_COMFORT, " +
                    "' ' AS WORK_LOAD, " +
                    "' ' AS PROBLEM, " +
                    "' ' AS MALFUNCTION, " +
                    "' ' AS ANY_SYMPTOM, " +
                    "' ' AS EXPOSURE_ALTITUDE, " +
                    "' ' AS NOTE_PROBLEM " +
                    "FROM " + TABLE_DIVE + "_BACK;";

            db.execSQL(insertTable);

            // Drop backup table
            db.execSQL("DROP TABLE " + TABLE_DIVE + "_BACK;");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}