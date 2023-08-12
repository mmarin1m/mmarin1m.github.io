package ca.myairbuddyandi;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Michel on 2016-11-28.

 * Holds all of the logic for the AirDA Class
 */

public class AirDA {

    // Static
    private static final String LOG_TAG = "AirDA";
    private static final String FK_CONSTRAINT_1811 = "1811";

    private static final String[] COLUMNS_COMPUTER = {
            AirDBHelper.TABLE_COMPUTER_COMPUTER_NO,
            AirDBHelper.TABLE_COMPUTER_DESCRIPTION,
            AirDBHelper.TABLE_COMPUTER_VENDOR,
            AirDBHelper.TABLE_COMPUTER_PRODUCT,
            AirDBHelper.TABLE_COMPUTER_TYPE,
            AirDBHelper.TABLE_COMPUTER_MODEL,
            AirDBHelper.TABLE_COMPUTER_TRANSPORT,
            AirDBHelper.TABLE_COMPUTER_SERIAL_NO,
            AirDBHelper.TABLE_COMPUTER_FW,
            AirDBHelper.TABLE_COMPUTER_FWID,
            AirDBHelper.TABLE_COMPUTER_LANGUAGE,
            AirDBHelper.TABLE_COMPUTER_UNIT,
            AirDBHelper.TABLE_COMPUTER_CONNECTION_TYPE,
            AirDBHelper.TABLE_COMPUTER_MAC_ADDRESS,
            AirDBHelper.TABLE_COMPUTER_DEVICE_NAME,
            AirDBHelper.TABLE_COMPUTER_SERVICE,
            AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX,
            AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX_CREDITS,
            AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX,
            AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX_CREDITS
    };

    private static final String[] COLUMNS_CYLINDER = {
            AirDBHelper.TABLE_CYLINDER_CYLINDER_NO,
            AirDBHelper.TABLE_CYLINDER_DIVER_NO,
            AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE,
            AirDBHelper.TABLE_CYLINDER_VOLUME,
            AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE,
            AirDBHelper.TABLE_CYLINDER_BRAND,
            AirDBHelper.TABLE_CYLINDER_MODEL,
            AirDBHelper.TABLE_CYLINDER_SERIAL_NO,
            AirDBHelper.TABLE_CYLINDER_LAST_VIP,
            AirDBHelper.TABLE_CYLINDER_LAST_HYDRO,
            AirDBHelper.TABLE_CYLINDER_COLOR,
            AirDBHelper.TABLE_CYLINDER_WEIGHT_FULL,
            AirDBHelper.TABLE_CYLINDER_WEIGHT_EMPTY,
            AirDBHelper.TABLE_CYLINDER_BUOYANCY_FULL,
            AirDBHelper.TABLE_CYLINDER_BUOYANCY_EMPTY
    };

    private static final String[] COLUMNS_CYLINDER_TYPE = {
            AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE,
            AirDBHelper.TABLE_CYLINDER_TYPE_DESCRIPTION,
            AirDBHelper.TABLE_CYLINDER_TYPE_VOLUME,
            AirDBHelper.TABLE_CYLINDER_TYPE_RATED_PRESSURE
    };

    private static final String[] COLUMNS_DIVE_PLAN = {
            AirDBHelper.TABLE_DIVE_PLAN_DIVE_PLAN_NO,
            AirDBHelper.TABLE_DIVE_PLAN_DIVE_NO,
            AirDBHelper.TABLE_DIVE_PLAN_ORDER_NO,
            AirDBHelper.TABLE_DIVE_PLAN_DEPTH,
            AirDBHelper.TABLE_DIVE_PLAN_MINUTE
    };

    private static final String[] COLUMNS_DIVE_SEGMENT = {
            AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO,
            AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO,
            AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO,
            AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE,
            AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH,
            AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE,
            AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE,
            AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE,
            AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME
    };

    private static final String[] COLUMNS_DIVE_TYPE = {
            AirDBHelper.TABLE_DIVE_TYPE_DIVE_TYPE,
            AirDBHelper.TABLE_DIVE_TYPE_DESCRIPTION,
            AirDBHelper.TABLE_DIVE_TYPE_SORT_ORDER,
            AirDBHelper.TABLE_DIVE_TYPE_IN_PICKER
    };

    private static final String[] COLUMNS_DIVER = {
            AirDBHelper.TABLE_DIVER_DIVER_NO,
            AirDBHelper.TABLE_DIVER_FIRST_NAME,
            AirDBHelper.TABLE_DIVER_MIDDLE_NAME,
            AirDBHelper.TABLE_DIVER_LAST_NAME,
            AirDBHelper.TABLE_DIVER_GENDER,
            AirDBHelper.TABLE_DIVER_BIRTH_DATE,
            AirDBHelper.TABLE_DIVER_PHONE,
            AirDBHelper.TABLE_DIVER_EMAIL,
            AirDBHelper.TABLE_DIVER_CERTIFICATION_BODY,
            AirDBHelper.TABLE_DIVER_CERTIFICATION_LEVEL,
            AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED
    };

    private static final String[] COLUMNS_DYNAMIC_SPINNER = {
            AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE,
            AirDBHelper.TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED,
            AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TEXT
    };

    private static final String[] COLUMNS_DYNAMIC_SPINNER_COUNT = {
            "COUNT(*) AS SPINNER_TEXT_COUNT"
    };

    private static final String[] COLUMNS_GROUP_TYPE = {
            AirDBHelper.TABLE_GROUP_TYPE_GROUP_TYPE,
            AirDBHelper.TABLE_GROUP_TYPE_DESCRIPTION,
            AirDBHelper.TABLE_GROUP_TYPE_SYSTEM_DEFINED
    };

    private static final String[] COLUMNS_GROUPP = {
            AirDBHelper.TABLE_GROUP_GROUP_NO,
            AirDBHelper.TABLE_GROUP_DIVER_NO,
            AirDBHelper.TABLE_GROUP_GROUP_TYPE,
            AirDBHelper.TABLE_GROUP_DESCRIPTION
    };

    private static final String[] COLUMNS_GROUPP_CYLINDER = {
            AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO,
            AirDBHelper.TABLE_GROUP_CYLINDER_CYLINDER_NO,
            AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE
    };

    private static final String[] COLUMNS_SEGMENT_TYPE = {
            AirDBHelper.TABLE_SEGMENT_TYPE_SEGMENT_TYPE,
            AirDBHelper.TABLE_SEGMENT_TYPE_DESCRIPTION,
            AirDBHelper.TABLE_SEGMENT_TYPE_ORDER_NO,
            AirDBHelper.TABLE_SEGMENT_TYPE_DIRECTION,
            AirDBHelper.TABLE_SEGMENT_TYPE_SHOW_RESULT,
            AirDBHelper.TABLE_SEGMENT_TYPE_SYSTEM_DEFINED,
            AirDBHelper.TABLE_SEGMENT_TYPE_STATUS
    };

    private static final String[] COLUMNS_STATE = {
            AirDBHelper.TABLE_STATE_STATE_NO,
            AirDBHelper.TABLE_STATE_DIVE_TYPE,
            AirDBHelper.TABLE_STATE_MY_BUDDY_DIVER_NO,
            AirDBHelper.TABLE_STATE_MY_SAC,
            AirDBHelper.TABLE_STATE_MY_RMV,
            AirDBHelper.TABLE_STATE_MY_GROUP,
            AirDBHelper.TABLE_STATE_MY_BUDDY_SAC,
            AirDBHelper.TABLE_STATE_MY_BUDDY_RMV,
            AirDBHelper.TABLE_STATE_MY_BUDDY_GROUP
    };

    private static final String[] COLUMNS_USAGE_TYPE = {
            AirDBHelper.TABLE_USAGE_TYPE_USAGE_TYPE,
            AirDBHelper.TABLE_USAGE_TYPE_DESCRIPTION,
            AirDBHelper.TABLE_USAGE_TYPE_SYSTEM_DEFINED
    };

    // Public

    // Protected

    // Private
    private Boolean mSuccess = true;
    private final Context mContext;
    private SQLiteDatabase mDb;
    private final SQLiteOpenHelper mAirDbHelper;

    // End of variables

    // Public constructor
    public AirDA(Context context) {
        mContext = context;
        mAirDbHelper = AirDBHelper.getInstance(context);
    }

    // DATABASE functions
    public boolean isOpen() {
        return mDb.isOpen();
    }

    public boolean isLock() {
        return mDb.isDbLockedByCurrentThread();
    }

    public void open() {
        try {
            mDb = mAirDbHelper.getWritableDatabase();
            Log.d(LOG_TAG, "airDbHelper open");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void openWithFKConstraintsEnabled() {
        try {
            mDb = mAirDbHelper.getWritableDatabase();
            mDb.setForeignKeyConstraintsEnabled(true);
            Log.d(LOG_TAG, "airDbHelper openWithFKConstraintsEnabled");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void beginTransaction() {
        // Starts a transaction in EXCLUSIVE mode
        try {
            mDb.beginTransaction();
            Log.d(LOG_TAG, "airDbHelper beginTransaction");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void beginTransactionNonExclusive() {
        // Starts a transaction in IMMEDIATE mode
        try {
            mDb.beginTransactionNonExclusive();
            Log.d(LOG_TAG, "airDbHelper beginTransactionNonExclusive");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void setTransactionSuccessful() {
        try {
            mDb.setTransactionSuccessful();
            Log.d(LOG_TAG, "airDbHelper setTransactionSuccessful");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

     void endTransaction() {
        try {
            mDb.endTransaction();
            Log.d(LOG_TAG, "airDbHelper endTransaction");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            if (mAirDbHelper != null) {
                mAirDbHelper.close();
                Log.d(LOG_TAG, "airDbHelper close");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSQLiteVersion() {
        Cursor cursor = SQLiteDatabase.openOrCreateDatabase(":memory:", null).rawQuery("select sqlite_version() AS sqlite_version", null);
        String sqliteVersion = "";
        while(cursor.moveToNext()){
            sqliteVersion += cursor.getString(0);
        }
        return sqliteVersion;
    }

    public SQLiteDatabase getDb() {
        return mDb;
    }

    // DATABASE ACCESS

    public Boolean getSuccess() {
        return mSuccess;
    }

    public void setSuccess(Boolean success) {
        mSuccess = success;
    }

    public int getCountFromTable(String tableName) {
        Cursor cursor = mDb.rawQuery("SELECT COUNT(*) AS TABLE_COUNT FROM " + tableName,null);
        int tableCount;

        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                tableCount = cursor.getInt(cursor.getColumnIndex("TABLE_COUNT"));
            } else {
                tableCount = 0;
            }
            Log.d(LOG_TAG, "Total getCountFromTable rows = " + cursor.getCount());
            return tableCount;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // COMPUTER Data Access
    void getComputer(Long computerNo, Computer computer) {

        // TODO: Add missing columns
        // TODO: Remove obsolete columns
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_COMPUTER, COLUMNS_COMPUTER, AirDBHelper.TABLE_COMPUTER_COMPUTER_NO + " = " + computerNo, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                computer.setComputerNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_COMPUTER_NO)));
                computer.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_DESCRIPTION)));
                computer.setVendor(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_VENDOR)));
                computer.setProduct(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_PRODUCT)));
                computer.setTransport(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_TRANSPORT)));
                computer.setSerialNumber(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_SERIAL_NO)));
                computer.setFw(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_FW)));
                computer.setFwId(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_FWID)));
                computer.setLanguage(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_LANGUAGE)));
                computer.setUnit(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_UNIT)));
                computer.setConnectionType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_CONNECTION_TYPE)));
                computer.setMacAddress(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_MAC_ADDRESS)));
                computer.setDeviceName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_DEVICE_NAME)));
                computer.setService(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_SERVICE)));
                computer.setCharacteristicRx(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX)));
                computer.setCharacteristicRxCredits(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX_CREDITS)));
                computer.setCharacteristicTx(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX)));
                computer.setCharacteristicTxCredits(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX_CREDITS)));
            } else {
                computer.setComputerNo(MyConstants.ZERO_L);
                computer.setDescription(null);
                computer.setVendor(null);
                computer.setProduct(null);
                computer.setTransport(MyConstants.ZERO_I);
                computer.setSerialNumber(null);
                computer.setFw(null);
                computer.setFwId(null);
                computer.setLanguage(null);
                computer.setUnit(null);
                computer.setConnectionType(null);
                computer.setMacAddress(null);
                computer.setDeviceName(null);
                computer.setService(null);
                computer.setCharacteristicRx(null);
                computer.setCharacteristicRxCredits(null);
                computer.setCharacteristicTx(null);
                computer.setCharacteristicTxCredits(null);
            }
            Log.d(LOG_TAG, "Total COMPUTER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<ComputerPick> getAllComputers() {
        ArrayList<ComputerPick> computers = new ArrayList<>();

        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_COMPUTER, COLUMNS_COMPUTER, null, null, null, null, null)) {

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    ComputerPick computerPickList = new ComputerPick();
                    computerPickList.setComputerNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_COMPUTER_NO)));
                    computerPickList.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_DESCRIPTION)));
                    computerPickList.setVendor(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_VENDOR)));
                    computerPickList.setProduct(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_COMPUTER_PRODUCT)));
                    computers.add(computerPickList);
                }
            }
            Log.d(LOG_TAG, "Total COMPUTER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return computers;
    }

    // TODO: Add missing columns
    // TODO: Remove obsolete columns
    void createComputer(Computer computer, boolean restore) {
        try {
            ContentValues values = new ContentValues();
            // PK is autoincrement but COMPUTER_NO is populated for a Database Restore
            if (restore) {
                values.put(AirDBHelper.TABLE_COMPUTER_COMPUTER_NO, computer.getComputerNo());
            }
            values.put(AirDBHelper.TABLE_COMPUTER_DESCRIPTION, computer.getDescription());
            values.put(AirDBHelper.TABLE_COMPUTER_VENDOR, computer.getVendor());
            values.put(AirDBHelper.TABLE_COMPUTER_PRODUCT, computer.getProduct());
            values.put(AirDBHelper.TABLE_COMPUTER_TRANSPORT, computer.getTransport());
            values.put(AirDBHelper.TABLE_COMPUTER_SERIAL_NO, computer.getSerialNumber());
            values.put(AirDBHelper.TABLE_COMPUTER_FW, computer.getFw());
            values.put(AirDBHelper.TABLE_COMPUTER_FWID, computer.getFwId());
            values.put(AirDBHelper.TABLE_COMPUTER_LANGUAGE, computer.getLanguage());
            values.put(AirDBHelper.TABLE_COMPUTER_UNIT, computer.getUnit());
            values.put(AirDBHelper.TABLE_COMPUTER_CONNECTION_TYPE, computer.getConnectionType());
            values.put(AirDBHelper.TABLE_COMPUTER_MAC_ADDRESS, computer.getMacAddress());
            values.put(AirDBHelper.TABLE_COMPUTER_DEVICE_NAME, computer.getDeviceName());
            values.put(AirDBHelper.TABLE_COMPUTER_SERVICE, computer.getService());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX, computer.getCharacteristicRx());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX_CREDITS, computer.getCharacteristicRxCredits());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX, computer.getCharacteristicTx());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX_CREDITS, computer.getCharacteristicTxCredits());

            long id = mDb.insert(AirDBHelper.TABLE_COMPUTER, null, values);
            if (!restore) {
                computer.setComputerNo(id);
            }
            Log.d(LOG_TAG, "Inserted COMPUTER_NO is " + String.valueOf(computer.getComputerNo()));
        } catch (SQLException e){
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    // TODO: Add missing columns
    // TODO: Remove obsolete columns
    void updateComputer(Computer computer) {
        try {
            String whereClause = AirDBHelper.TABLE_COMPUTER_COMPUTER_NO + "=" + computer.getComputerNo();
            Log.d(LOG_TAG, "Updated COMPUTER_NO is " + String.valueOf(computer.getComputerNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_COMPUTER_DESCRIPTION, computer.getDescription());
            values.put(AirDBHelper.TABLE_COMPUTER_VENDOR, computer.getVendor());
            values.put(AirDBHelper.TABLE_COMPUTER_PRODUCT, computer.getProduct());
            values.put(AirDBHelper.TABLE_COMPUTER_TRANSPORT, computer.getTransport());
            values.put(AirDBHelper.TABLE_COMPUTER_SERIAL_NO, computer.getSerialNumber());
            values.put(AirDBHelper.TABLE_COMPUTER_FW, computer.getFw());
            values.put(AirDBHelper.TABLE_COMPUTER_FWID, computer.getFwId());
            values.put(AirDBHelper.TABLE_COMPUTER_LANGUAGE, computer.getLanguage());
            values.put(AirDBHelper.TABLE_COMPUTER_UNIT, computer.getUnit());
            values.put(AirDBHelper.TABLE_COMPUTER_CONNECTION_TYPE, computer.getConnectionType());
            values.put(AirDBHelper.TABLE_COMPUTER_MAC_ADDRESS, computer.getMacAddress());
            values.put(AirDBHelper.TABLE_COMPUTER_DEVICE_NAME, computer.getDeviceName());
            values.put(AirDBHelper.TABLE_COMPUTER_SERVICE, computer.getService());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX, computer.getCharacteristicRx());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_RX_CREDITS, computer.getCharacteristicRxCredits());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX, computer.getCharacteristicTx());
            values.put(AirDBHelper.TABLE_COMPUTER_CHARACTERISTIC_TX_CREDITS, computer.getCharacteristicTxCredits());
            mDb.update(AirDBHelper.TABLE_COMPUTER, values, whereClause, null);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    Integer deleteComputer(long computerNo) {
        try {
            // No need to check for RI
            // COMPUTER can be deleted anytime
            String whereClause = AirDBHelper.TABLE_COMPUTER_COMPUTER_NO + "=" + computerNo;
            Log.d(LOG_TAG, "Deleted COMPUTER_NO is " + String.valueOf(computerNo));
            mDb.delete(AirDBHelper.TABLE_COMPUTER, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    // CYLINDER Data Access
    void getCylinder(Long cylinderNo, Cylinder cylinder) {
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_CYLINDER, COLUMNS_CYLINDER, AirDBHelper.TABLE_CYLINDER_CYLINDER_NO + " = " + cylinderNo, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    cylinder.setCylinderNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_NO)));
                    cylinder.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_DIVER_NO)));
                    cylinder.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                    cylinder.setCylinderTypeOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                    cylinder.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_VOLUME)));
                    cylinder.setVolumeOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_VOLUME)));
                    cylinder.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                    cylinder.setRatedPressureOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                    cylinder.setBrand(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_BRAND)));
                    cylinder.setModel(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_MODEL)));
                    cylinder.setSerialNo(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_SERIAL_NO)));
                    cylinder.setTankColor(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_COLOR)));
                    // Transform the Last vip Date from Integer/Long to a Date
                    Long lastVip = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_LAST_VIP));
                    if (!lastVip.equals(MyConstants.ZERO_L)) {
                        cylinder.setLastVip(MyFunctions.convertDateFromLongToDate(lastVip));
                    }
                    // Transform the Last Hydro Date from Integer/Long to a Date
                    Long lastHydro = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_LAST_HYDRO));
                    if (!lastHydro.equals(MyConstants.ZERO_L)) {
                        cylinder.setLastHydro(MyFunctions.convertDateFromLongToDate(lastHydro));
                    }
                    cylinder.setWeightFull(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_WEIGHT_FULL)));
                    cylinder.setWeightEmpty(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_WEIGHT_EMPTY)));
                    cylinder.setBuoyancyFull(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_BUOYANCY_FULL)));
                    cylinder.setBuoyancyEmpty(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_BUOYANCY_EMPTY)));
                }
            }
            Log.d(LOG_TAG, "Total CYLINDER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<Cylinder> getAllCylinders() {
        ArrayList<Cylinder> cylinders = new ArrayList<>();

        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_CYLINDER, COLUMNS_CYLINDER, null, null, null, null, null)) {

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Cylinder cylinder = new Cylinder();
                    cylinder.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_DIVER_NO)));
                    cylinder.setCylinderTypeCommon(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                    cylinder.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_VOLUME)));
                    cylinder.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                    cylinder.setBrand(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_BRAND)));
                    cylinder.setModel(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_MODEL)));
                    cylinder.setSerialNo(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_SERIAL_NO)));
                    // Transform the Last vip Date from Integer/Long to a Date
                    Long lastVip = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_LAST_VIP));
                    if (!lastVip.equals(MyConstants.ZERO_L)) {
                        cylinder.setLastVip(MyFunctions.convertDateFromLongToDate(lastVip));
                    }
                    // Transform the Last Hydro Date from Integer/Long to a Date
                    Long lastHydro = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_LAST_HYDRO));
                    if (!lastHydro.equals(MyConstants.ZERO_L)) {
                        cylinder.setLastHydro(MyFunctions.convertDateFromLongToDate(lastHydro));
                    }
                    cylinder.setTankColor(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_COLOR)));
                    cylinder.setWeightFull(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_WEIGHT_FULL)));
                    cylinder.setWeightEmpty(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_WEIGHT_EMPTY)));
                    cylinder.setBuoyancyFull(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_BUOYANCY_FULL)));
                    cylinder.setBuoyancyFull(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_BUOYANCY_EMPTY)));
                    cylinders.add(cylinder);
                }
            }
            Log.d(LOG_TAG, "Total CYLINDER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return cylinders;
    }

    ArrayList<CylinderPick> getAllCylindersByDiver(Long diverNo) {
        ArrayList<CylinderPick> cylinderPicks = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "WITH all_row AS "
                + "\n(SELECT c.diver_no AS DIVER_NO "
                + "\n,c.cylinder_no AS CYLINDER_NO "
                + "\n,c.cylinder_type AS CYLINDER_TYPE "
                + "\n,c.volume AS VOLUME "
                + "\n,c.rated_pressure AS RATED_PRESSURE "
                + "\n,g.group_no AS GROUP_NO "
                + "\n,g.description AS DESCRIPTION "
                + "\nFROM groupp g "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = g.group_no) "
                + "\nINNER JOIN cylinder c "
                + "\nON (c.cylinder_no = gc.cylinder_no) "
                + "\nWHERE g.diver_no = ? /*diverNo*/ "
                + "\nUNION "
                + "\nSELECT c.diver_no AS DIVER_NO "
                + "\n,c.cylinder_no AS CYLINDER_NO "
                + "\n,c.cylinder_type AS CYLINDER_TYPE "
                + "\n,c.volume AS VOLUME "
                + "\n,c.rated_pressure "
                + "\n,IFNULL(gc.group_no, 0) AS GROUP_NO "
                + "\n,'Orphan' AS DESCRIPTION "
                + "\nFROM cylinder c "
                + "\nLEFT OUTER JOIN group_cylinder gc "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE c.diver_no = ? /*diverNo*/ "
                + "\nAND gc.group_no IS NULL) "
                + "\nSELECT ar.diver_no AS DIVER_NO "
                + "\n,ar.cylinder_no AS CYLINDER_NO "
                + "\n,ar.cylinder_type AS CYLINDER_TYPE "
                + "\n,ar.volume AS VOLUME "
                + "\n,ar.rated_pressure AS RATED_PRESSURE "
                + "\n,ar.group_no AS GROUP_NO "
                + "\n,ar.description AS DESCRIPTION "
                + "\nFROM all_row ar "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diverNo), String.valueOf(diverNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CylinderPick cylinderPick = new CylinderPick();
                    cylinderPick.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_DIVER_NO)));
                    cylinderPick.setCylinderNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_NO)));
                    cylinderPick.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_NO)));
                    cylinderPick.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                    cylinderPick.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_VOLUME)));
                    cylinderPick.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                    cylinderPick.setGroupDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DESCRIPTION)));
                    cylinderPicks.add(cylinderPick);
                }
            }
            Log.d(LOG_TAG, "Total CYLINDER rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cylinderPicks;
    }

    void createCylinder(Cylinder cylinder, boolean restore) {
        try {
            ContentValues values = new ContentValues();
            // PK is autoincrement but CYLINDER_NO is populated for a Database Restore
            if (restore) {
                values.put(AirDBHelper.TABLE_CYLINDER_CYLINDER_NO, cylinder.getCylinderNo());
            }
            values.put(AirDBHelper.TABLE_CYLINDER_DIVER_NO, cylinder.getDiverNo());
            values.put(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE, cylinder.getCylinderType());
            values.put(AirDBHelper.TABLE_CYLINDER_VOLUME, cylinder.getVolume());
            values.put(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE, cylinder.getRatedPressure());
            values.put(AirDBHelper.TABLE_CYLINDER_BRAND, cylinder.getBrand());
            values.put(AirDBHelper.TABLE_CYLINDER_MODEL, cylinder.getModel());
            values.put(AirDBHelper.TABLE_CYLINDER_SERIAL_NO, cylinder.getSerialNo());
            // Transform the Last VIP Date and Time In from String to Integer/Long
            Long nullDate = MyConstants.ZERO_L;
            if (cylinder.getLastVip() != null) {
            values.put(AirDBHelper.TABLE_CYLINDER_LAST_VIP, MyFunctions.convertDateTimeToLong(cylinder.getLastVip(),cylinder.getLastVipHour(),cylinder.getLastVipMinute()));
            } else {
                values.put(AirDBHelper.TABLE_CYLINDER_LAST_VIP,nullDate);
            }
            // Transform the Last Hydro Date and Time In from String to Integer/Long
            if (cylinder.getLastHydro() != null) {
                values.put(AirDBHelper.TABLE_CYLINDER_LAST_HYDRO, MyFunctions.convertDateTimeToLong(cylinder.getLastHydro(),cylinder.getLastHydroHour(),cylinder.getLastHydroMinute()));
            } else {
                values.put(AirDBHelper.TABLE_CYLINDER_LAST_HYDRO,nullDate);
            }
            values.put(AirDBHelper.TABLE_CYLINDER_COLOR, cylinder.getTankColor());
            values.put(AirDBHelper.TABLE_CYLINDER_WEIGHT_FULL, cylinder.getWeightFull());
            values.put(AirDBHelper.TABLE_CYLINDER_WEIGHT_EMPTY, cylinder.getWeightEmpty());
            values.put(AirDBHelper.TABLE_CYLINDER_BUOYANCY_FULL, cylinder.getBuoyancyFull());
            values.put(AirDBHelper.TABLE_CYLINDER_BUOYANCY_EMPTY, cylinder.getBuoyancyEmpty());
            long id = mDb.insert(AirDBHelper.TABLE_CYLINDER, null, values);
            if (!restore) {
                cylinder.setCylinderNo(id);
            }
            Log.d(LOG_TAG, "Inserted CYLINDER_NO is " + String.valueOf(cylinder.getCylinderNo()));
        } catch (SQLException e){
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateCylinder(Cylinder cylinder) {
        try {
            String whereClause = AirDBHelper.TABLE_CYLINDER_CYLINDER_NO + "=" + cylinder.getCylinderNo();
            Log.d(LOG_TAG, "Updated CYLINDER_NO is " + String.valueOf(cylinder.getCylinderNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_CYLINDER_DIVER_NO, cylinder.getDiverNo());
            values.put(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE, cylinder.getCylinderType());
            values.put(AirDBHelper.TABLE_CYLINDER_VOLUME, cylinder.getVolume());
            values.put(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE, cylinder.getRatedPressure());
            values.put(AirDBHelper.TABLE_CYLINDER_BRAND, cylinder.getBrand());
            values.put(AirDBHelper.TABLE_CYLINDER_MODEL, cylinder.getModel());
            values.put(AirDBHelper.TABLE_CYLINDER_SERIAL_NO, cylinder.getSerialNo());
            // Transform the Last VIP Date and Time In from String to Integer/Long
            values.put(AirDBHelper.TABLE_CYLINDER_LAST_VIP, MyFunctions.convertDateTimeToLong(cylinder.getLastVip(),cylinder.getLastVipHour(),cylinder.getLastVipMinute()));
            // Transform the Last Hydro Date and Time In from String to Integer/Long
            values.put(AirDBHelper.TABLE_CYLINDER_LAST_HYDRO, MyFunctions.convertDateTimeToLong(cylinder.getLastHydro(),cylinder.getLastHydroHour(),cylinder.getLastHydroMinute()));
            values.put(AirDBHelper.TABLE_CYLINDER_COLOR, cylinder.getTankColor());
            values.put(AirDBHelper.TABLE_CYLINDER_WEIGHT_FULL, cylinder.getWeightFull());
            values.put(AirDBHelper.TABLE_CYLINDER_WEIGHT_EMPTY, cylinder.getWeightEmpty());
            values.put(AirDBHelper.TABLE_CYLINDER_BUOYANCY_FULL, cylinder.getBuoyancyFull());
            values.put(AirDBHelper.TABLE_CYLINDER_BUOYANCY_EMPTY, cylinder.getBuoyancyEmpty());
            mDb.update(AirDBHelper.TABLE_CYLINDER, values, whereClause, null);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    Integer deleteCylinder(long cylinderNo) {
        try {
            // Enforcing FK Constraint manually
            if (cylinderUsed(cylinderNo) == MyConstants.ZERO_I) {
                String whereClause = AirDBHelper.TABLE_CYLINDER_CYLINDER_NO + "=" + cylinderNo;
                Log.d(LOG_TAG, "Deleted CYLINDER_NO is " + String.valueOf(cylinderNo));
                mDb.delete(AirDBHelper.TABLE_CYLINDER, whereClause, null);
                return 0;
            } else {
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            }
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    private int cylinderUsed(long cylinderNo) {
        Cursor cursor = null;
        String sqlSt;
        int cylinderCount;

        sqlSt = "SELECT COUNT(*) AS CYLINDER_COUNT FROM group_cylinder WHERE cylinder_no = ?";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(cylinderNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cylinderCount = cursor.getInt(cursor.getColumnIndex("CYLINDER_COUNT"));
            } else {
                cylinderCount = 0;
            }
            Log.d(LOG_TAG, "Total GROUP_CYLINDER rows = " + cursor.getCount());
            return cylinderCount;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void dropCylinder() {
        try {
            Log.d(LOG_TAG, "Drop CYLINDER");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_CYLINDER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_CYLINDER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_CYLINDER_I1);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // CYLINDER_TYPE Data Access
    public void getCylinderType(CylinderType cylinderType) {
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_CYLINDER_TYPE, COLUMNS_CYLINDER_TYPE, AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE + " = '" + cylinderType.getCylinderType() + "'", null, null, null, null)) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    cylinderType.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE)));
                    cylinderType.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_DESCRIPTION)));
                    cylinderType.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_VOLUME)));
                    cylinderType.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_RATED_PRESSURE)));
                }
            }
            Log.d(LOG_TAG, "Total CYLINDER_TYPE rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<CylinderType> getAllCylinderTypes() {
        ArrayList<CylinderType> cylinderTypes = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        // Returning the count for all cylinder types for all divers, including the Buddies
        sqlSt = "SELECT ct.cylinder_type "
                + "\n,ct.description "
                + "\n,ct.volume "
                + "\n,ct.rated_pressure "
                + "\n,COUNT(ddgc.dive_no) AS DIVES "
                + "\nFROM cylinder_type ct "
                + "\nLEFT JOIN cylinder c "
                + "\nON (c.cylinder_type = ct.cylinder_type) "
                + "\nLEFT JOIN diver_dive_group_cylinder ddgc "
                + "\nON (ddgc.cylinder_no = c.cylinder_no) "
                + "\nGROUP BY ct.cylinder_type "
                + "\n,ct.description "
                + "\n,ct.volume "
                + "\n,ct.rated_pressure "
                + "\nORDER BY ct.volume ASC, ct.rated_pressure ASC";

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CylinderType cylindertype = new CylinderType();
                    cylindertype.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE)));
                    cylindertype.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_DESCRIPTION)));
                    cylindertype.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_VOLUME)));
                    cylindertype.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_RATED_PRESSURE)));
                    cylindertype.setDives(cursor.getInt(cursor.getColumnIndex("DIVES")));
                    cylinderTypes.add(cylindertype);
                }
            }
            Log.d(LOG_TAG, "Total CYLINDER_TYPE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cylinderTypes;
    }

    ArrayList<CylinderType> getAllCylinderTypesSpinner() {
        ArrayList<CylinderType> cylinderTypes = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        // Returning the count for all cylinder types for all divers, including the Buddies
        sqlSt = "SELECT ct.cylinder_type "
                + "\n,ct.cylinder_type || ' ' || ct.description AS DESCRIPTION "
                + "\n,ct.volume "
                + "\n,ct.rated_pressure "
                + "\n,COUNT(ddgc.dive_no) AS DIVES "
                + "\nFROM cylinder_type ct "
                + "\nLEFT JOIN cylinder c "
                + "\nON (c.cylinder_type = ct.cylinder_type) "
                + "\nLEFT JOIN diver_dive_group_cylinder ddgc "
                + "\nON (ddgc.cylinder_no = c.cylinder_no) "
                + "\nGROUP BY ct.cylinder_type "
                + "\n,ct.description "
                + "\n,ct.volume "
                + "\n,ct.rated_pressure "
                + "\nORDER BY ct.volume ASC, ct.rated_pressure ASC";

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    CylinderType cylindertype = new CylinderType();
                    cylindertype.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE)));
                    cylindertype.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_DESCRIPTION)));
                    cylindertype.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_VOLUME)));
                    cylindertype.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_TYPE_RATED_PRESSURE)));
                    cylindertype.setDives(cursor.getInt(cursor.getColumnIndex("DIVES")));
                    cylinderTypes.add(cylindertype);
                }
            }
            Log.d(LOG_TAG, "Total CYLINDER_TYPE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cylinderTypes;
    }

    Integer createCylinderType(CylinderType cylinderType) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE, cylinderType.getCylinderType());
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_DESCRIPTION, cylinderType.getDescription());
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_VOLUME, cylinderType.getVolume());
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_RATED_PRESSURE, cylinderType.getRatedPressure());
            long id = mDb.insert(AirDBHelper.TABLE_CYLINDER_TYPE, null, values);
            Log.d(LOG_TAG, "Inserted CYLINDER_TYPE is " + String.valueOf(cylinderType.getCylinderType()));
            if (id ==  AirDBHelper.FK_CONSTRAINT_UPDATE) {
                return AirDBHelper.FK_CONSTRAINT_UPDATE;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    Integer updateCylinderType(CylinderType cylinderType) {
        try {
            String whereClause = AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE + "= '" + cylinderType.getCylinderType() + "'";
            Log.d(LOG_TAG, "Updated CYLINDER_TYPE is " + String.valueOf(cylinderType.getCylinderType()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_DESCRIPTION, cylinderType.getDescription());
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_VOLUME, cylinderType.getVolume());
            values.put(AirDBHelper.TABLE_CYLINDER_TYPE_RATED_PRESSURE, cylinderType.getRatedPressure());
            mDb.update(AirDBHelper.TABLE_CYLINDER_TYPE, values, whereClause, null);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteCylinderType(String cylinderTypePk) {
        try {
            String whereClause = AirDBHelper.TABLE_CYLINDER_TYPE_CYLINDER_TYPE + "= '" + cylinderTypePk + "'";
            Log.d(LOG_TAG, "Deleted CYLINDER_TYPE is " + String.valueOf(cylinderTypePk));
            mDb.delete(AirDBHelper.TABLE_CYLINDER_TYPE, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void dropCylinderType() {
        try {
            Log.d(LOG_TAG, "Drop CYLINDER_TYPE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_CYLINDER_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_CYLINDER_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_CYLINDER_TYPE_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DIVE Data Access
    void getDive(Long diveNo, Dive dive) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        sqlSt = "WITH rated_group AS "
                + "\n(SELECT gc.group_no AS GROUP_NO "
                + "\n,CAST(SUM(c.volume) AS REAL) AS RATED_VOLUME "
                + "\n,CAST(c.rated_pressure AS REAL) AS RATED_PRESSURE "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY gc.group_no "
                + "\n,c.volume "
                + "\n,c.rated_pressure) "
                + "\n, all_dive_plans AS "
                + "\n(SELECT dive_no "
                + "\n,COUNT(dive_plan_no) AS DIVE_PLAN_COUNT "
                + "\nFROM dive_plan "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,planning AS "
                + "\n(SELECT dp.dive_no "
                + "\n,dp.depth "
                + "\n,dp.minute "
                + "\n,MIN(dp.order_no) "
                + "\nFROM dive_plan dp "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nLIMIT 1) "
                + "\n,consumption AS "
                + "\n(SELECT ddgc.diver_no AS DIVER_NO "
                + "\n,ddgc.dive_no AS DIVE_NO "
                + "\n,ddgc.group_no AS GROUP_NO "
                + "\n,CAST(ddgc.beginning_pressure AS REAL) AS BEGINNING_PRESSURE "
                + "\n,CAST(ddgc.ending_pressure AS REAL) AS ENDING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no) "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nAND ddgc.dive_no = ? /*diveNo*/ "
                + "\nGROUP BY ddgc.diver_no, ddgc.dive_no, ddgc.group_no) "
                + "\n,all_extra_divers AS "
                + "\n(SELECT dive_no "
                + "\n,COUNT(diver_no) AS EXTRA_DIVER_COUNT "
                + "\nFROM diver_dive dd "
                + "\nWHERE dd.dive_no = ? /*diveNo*/ "
                + "\nAND dd.is_primary NOT IN ('M','Y') " // M=Me, Y=My Buddy
                + "\nAND dd.diver_no != 1) "
                + "\nSELECT DISTINCT "
                // Common
                + "\nd.dive_no "
                // 1- Planning
                + "\n,d.status "
                + "\n,IFNULL(adp.dive_plan_count,0) AS DIVE_PLAN_COUNT "
                + "\n,CASE WHEN p.dive_no IS NULL THEN '" + mContext.getResources().getString(R.string.lbl_planning) + "' "
                + "\nELSE p.minute || ' min @ ' || p.depth || ' " + myCalc.getDepthUnit() + "' "
                + "\nEND AS PLANNING "
                // Planning Me
                + "\n,IFNULL(ddme.diver_no,0) AS MY_DIVER_NO "
                + "\n,CASE WHEN ddme.diver_no IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_me) + "' "
                + "\nELSE '" + mContext.getResources().getString(R.string.sql_me) + "' "
                + "\nEND AS ME_LABEL "
                + "\n,IFNULL(gme.description,'" + mContext.getResources().getString(R.string.sql_pick_groupp).replaceAll("\'","''") + "') AS MY_GROUP "
                + "\n,gme.group_no AS MY_GROUP_NO "
                + "\n,CASE WHEN ddgme.sac IS NULL OR ddgme.sac = 0.0 THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddgme.sac "
                + "\nEND AS MY_SAC "
                + "\n,CASE WHEN ddme.RMV IS NULL OR ddme.rmv = 0.0 THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddme.rmv "
                + "\nEND AS MY_RMV "
                + "\n,IFNULL(rgme.rated_volume,0.0) AS MY_RATED_VOLUME "
                + "\n,IFNULL(rgme.rated_pressure,0.0) AS MY_RATED_PRESSURE "
                + "\n,IFNULL(cme.ending_pressure,0.0) AS MY_ENDING_PRESSURE "
                + "\n,IFNULL(cme.beginning_pressure, 0.0) - IFNULL(cme.ending_pressure, 0.0)  AS MY_PRESSURE "
                + "\n,ROUND(IFNULL((IFNULL(cme.beginning_pressure, 0.0) - IFNULL(cme.ending_pressure, 0.0)) * (IFNULL(rgme.rated_volume, 0.0) / IFNULL(rgme.rated_pressure, 0.0)),0.0),2)  AS MY_VOLUME "
                // Planning My Buddy
                + "\n,IFNULL(drmb.diver_no,0) AS MY_BUDDY_DIVER_NO "
                + "\n,CASE WHEN drmb.last_name IS NULL AND drmb.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                + "\nELSE drmb.last_name" + " || ', ' || " + "drmb.first_name "
                + "\nEND AS MY_BUDDY_FULL_NAME "
                + "\n,IFNULL(gmb.description,'" + mContext.getResources().getString(R.string.sql_pick_groupp).replaceAll("\'","''") + "') AS MY_BUDDY_GROUP "
                + "\n,gmb.group_no AS MY_BUDDY_GROUP_NO "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddgmb.sac IS NULL OR ddgmb.sac = 0.0)) THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddgmb.sac, 0.0) "
                + "\nEND AS MY_BUDDY_SAC "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddmb.RMV IS NULL OR ddmb.rmv = 0.0)) THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddmb.rmv,0.0) "
                + "\nEND AS MY_BUDDY_RMV "
                + "\n,IFNULL(rgmb.rated_volume,0.0) AS MY_BUDDY_RATED_VOLUME "
                + "\n,IFNULL(rgmb.rated_pressure,0.0) AS MY_BUDDY_RATED_PRESSURE "
                + "\n,IFNULL(cmb.ending_pressure,0.0) AS MY_BUDDY_ENDING_PRESSURE "
                + "\n,IFNULL(cmb.beginning_pressure, 0.0) - IFNULL(cmb.ending_pressure, 0.0)  AS MY_BUDDY_PRESSURE "
                + "\n,ROUND(IFNULL((IFNULL(cmb.beginning_pressure, 0.0) - IFNULL(cmb.ending_pressure, 0.0)) * (IFNULL(rgmb.rated_volume, 0.0) / IFNULL(rgmb.rated_pressure, 0.0)),0.0),2)  AS MY_BUDDY_VOLUME "
                // 2- Summary
                + "\n,d.log_book_no "
                + "\n,d.dive_type "
                + "\n,d.salinity "
                + "\n,d.date "
                + "\n,d.bottom_time "
                + "\n,d.average_depth "
                + "\n,CAST(d.maximum_depth AS REAL) AS MAXIMUM_DEPTH "
                + "\n,IFNULL(aed.extra_diver_count,0) AS EXTRA_DIVER_COUNT "
                + "\n,d.location "
                + "\n,d.dive_site "
                + "\n,d.purpose "
                // 2023/08/2 DB_VERSION = 7 Renamed from note to note_summary
                + "\n,d.note_summary "
                // 3- Environment
                + "\n,d.altitude "
                + "\n,d.dive_boat "
                + "\n,d.visibility "
                + "\n,CAST(d.air_temp AS REAL) AS AIR_TEMP "
                + "\n,CAST(d.water_temp_surface AS REAL) AS WATER_TEMP_SURFACE "
                + "\n,CAST(d.water_temp_bottom AS REAL) AS WATER_TEMP_BOTTOM "
                + "\n,CAST(d.water_temp_average AS REAL) AS WATER_TEMP_AVERAGE "
                + "\n,d.environment "
                + "\n,d.platform "
                + "\n,d.weather "
                + "\n,d.condition "
                + "\n,d.note_environment "
                // 4- Gas/Consumption (Real, not Planning)
                + "\n,d.note_gas "
                // 5- Gear
                + "\n,d.suit "
                + "\n,CAST(d.weight AS REAL) AS WEIGHT "
                + "\n,d.note_gear "
                // 6- Problem
                + "\n,d.thermal_comfort "
                + "\n,d.work_load "
                + "\n,d.problem "
                + "\n,d.malfunction "
                + "\n,d.any_symptom "
                + "\n,d.exposure_altitude "
                + "\n,d.note_problem "
                // 7- Computer
                //    None for now
                // 8- Graph
                //    None for now
                // Dive
                + "\nFROM dive d "
                + "\nLEFT JOIN all_dive_plans adp "
                + "\nON (adp.dive_no = d.dive_no) "
                // Me
                + "\nLEFT JOIN diver_dive ddme "
                + "\nON (ddme.dive_no = d.dive_no "
                + "\nAND ddme.diver_no = 1) "
                + "\nLEFT JOIN diver_dive_group ddgme "
                + "\nON (ddgme.diver_no = ddme.diver_no "
                + "\nAND ddgme.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN groupp gme "
                + "\nON (gme.group_no = ddgme.group_no) "
                + "\nLEFT JOIN rated_group rgme "
                + "\nON (rgme.group_no = gme.group_no) "
                + "\nLEFT JOIN consumption cme "
                + "\nON (cme.dive_no = d.dive_no "
                + "\nAND cme.diver_no = 1) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb "
                + "\nON (ddmb.dive_no = d.dive_no "
                + "\nAND ddmb.diver_no <> 1 "
                + "\nAND ddmb.is_primary = 'Y') " // My Buddy
                + "\nLEFT JOIN diver drmb "
                + "\nON (drmb.diver_no = ddmb.diver_no) "
                + "\nLEFT JOIN diver_dive_group ddgmb "
                + "\nON (ddgmb.diver_no = ddmb.diver_no "
                + "\nAND ddgmb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN groupp gmb "
                + "\nON (gmb.group_no = ddgmb.group_no) "
                + "\nLEFT JOIN rated_group rgmb "
                + "\nON (rgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN consumption cmb "
                + "\nON (cmb.dive_no = d.dive_no "
                + "\nAND cmb.diver_no = ddmb.diver_no) "
                // Plan
                + "\nLEFT JOIN planning p "
                + "\nON (p.dive_no = d.dive_no) "
                // Extra Diver(s)
                + "\nLEFT JOIN all_extra_divers aed "
                + "\nON (aed.dive_no = d.dive_no) "
                // Main where clause
                + "\nWHERE d.dive_no = ? /*diveNo*/";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diveNo)
                    , String.valueOf(diveNo)
                    , String.valueOf(diveNo)
                    , String.valueOf(diveNo)
                    , String.valueOf(diveNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Common
                    dive.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    // 1- Planning
                    dive.setStatus(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_STATUS)));
                    dive.setDivePlanCount(cursor.getInt(cursor.getColumnIndex("DIVE_PLAN_COUNT")));
                    dive.setPlanning(cursor.getString(cursor.getColumnIndex("PLANNING")));
                    // Planning Me
                    dive.setMyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO")));
                    dive.setMeLabel(cursor.getString(cursor.getColumnIndex("ME_LABEL")));
                    dive.setMyGroup(cursor.getString(cursor.getColumnIndex("MY_GROUP")));
                    dive.setMyGroupNo(cursor.getLong(cursor.getColumnIndex("MY_GROUP_NO")));
                    dive.setMySac(MyFunctions.roundUp(cursor.getDouble(cursor.getColumnIndex("MY_SAC")),2));
                    dive.setMyRmv(MyFunctions.roundUp(cursor.getDouble(cursor.getColumnIndex("MY_RMV")),2));
                    dive.setMyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME")));
                    dive.setMyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE")));
                    dive.setMyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE")));
                    dive.setMyVolume(cursor.getDouble(cursor.getColumnIndex("MY_VOLUME")));
                    dive.setMyPressure(cursor.getDouble(cursor.getColumnIndex("MY_PRESSURE")));
                    // Planning My Buddy
                    dive.setMyBuddyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO")));
                    dive.setMyBuddyFullName(cursor.getString(cursor.getColumnIndex("MY_BUDDY_FULL_NAME")));
                    dive.setMyBuddyGroup(cursor.getString(cursor.getColumnIndex("MY_BUDDY_GROUP")));
                    dive.setMyBuddyGroupNo(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_GROUP_NO")));
                    dive.setMyBuddySac(MyFunctions.roundUp(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_SAC")),2));
                    dive.setMyBuddyRmv(MyFunctions.roundUp(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RMV")),2));
                    dive.setMyBuddyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_VOLUME")));
                    dive.setMyBuddyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_PRESSURE")));
                    dive.setMyBuddyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_ENDING_PRESSURE")));
                    dive.setMyBuddyVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_VOLUME")));
                    dive.setMyBuddyPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_PRESSURE")));
                    // 2- Summary
                    dive.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    dive.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_TYPE)));
                    // 1 = TRUE = Salt; 0 = FALSE = Fresh
                    dive.setSalinity((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SALINITY)) == MyConstants.ONE_INT));
                    // Transform the Dive Date from Integer/Long to a Date
                    Long diveDate = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DATE));
                    if (!diveDate.equals(MyConstants.ZERO_L)) {
                        dive.setDate(MyFunctions.convertDateFromLongToDate(diveDate));
                    }
                    dive.setBottomTime(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_BOTTOM_TIME)));
                    // 2020/03/25 To support mm:ss
                    dive.setBottomTimeString(dive.getBottomTimeString());
                    dive.setAverageDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_AVERAGE_DEPTH)));
                    dive.setMaximumDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_MAXIMUM_DEPTH)));
                    dive.setExtraDiversCount(cursor.getInt(cursor.getColumnIndex("EXTRA_DIVER_COUNT")));
                    dive.setLocation(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOCATION)));
                    dive.setLocationOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOCATION)));
                    dive.setDiveSite(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_SITE)));
                    dive.setDiveSiteOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_SITE)));
                    dive.setPurpose(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PURPOSE)));
                    dive.setNoteSummary(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_NOTE_SUMMARY)));
                    // 3- Environment
                    dive.setAltitude(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_ALTITUDE)));
                    dive.setDiveBoat(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_BOAT)));
                    dive.setDiveBoatOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_BOAT)));
                    dive.setVisibility(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_VISIBILITY)));
                    dive.setAirTemp(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_AIR_TEMP)));
                    dive.setWaterTempSurface(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WATER_TEMP_SURFACE)));
                    dive.setWaterTempBottom(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WATER_TEMP_BOTTOM)));
                    dive.setWaterTempAverage(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WATER_TEMP_AVERAGE)));
                    dive.setEnvironment(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_ENVIRONMENT)));
                    dive.setPlatform(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLATFORM)));
                    dive.setWeather(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WEATHER)));
                    dive.setCondition(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_CONDITION)));
                    dive.setNoteEnvironment(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_NOTE_ENVIRONMENT)));
                    // 4- Gas/Consumption (Real, not Planning)
                    dive.setNoteGas(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_NOTE_GAS)));
                    // 5- Gear
                    dive.setSuit(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SUIT)));
                    dive.setWeight(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WEIGHT)));
                    dive.setNoteGear(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_NOTE_GEAR)));
                    // 6- Problem
                    dive.setThermalComfort(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_THERMAL_COMFORT)));
                    dive.setWorkLoad(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WORK_LOAD)));
                    dive.setProblem(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PROBLEM)));
                    dive.setMalfunction(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_MALFUNCTION)));
                    dive.setAnySymptom(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_ANY_SYMPTOM)));
                    dive.setExposureAltitude(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_EXPOSURE_ALTITUDE)));
                    dive.setNoteProblem(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_NOTE_PROBLEM)));
                    // 7- Dive Computer
                    //    None for now
                    // 8- Graph
                    //    None for now
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void getDiveForGraphic(Long diveNo, DiveForGraphic diveForGraphic) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        sqlSt = "WITH rated_group AS "
                + "\n(SELECT gc.group_no AS GROUP_NO "
                + "\n,CAST(SUM(c.volume) AS REAL) AS RATED_VOLUME "
                + "\n,CAST(c.rated_pressure AS REAL) AS RATED_PRESSURE "
                + "\n,CAST(SUM(c.volume) / c.rated_pressure AS REAL) AS CONVERSION_FACTOR "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY gc.group_no "
                + "\n,c.volume "
                + "\n,c.rated_pressure) "
                + "\n,beginning_group AS "
                + "\n(SELECT ddgc.diver_no AS DIVER_NO "
                + "\n,ddgc.dive_no AS DIVE_NO "
                + "\n,ddgc.group_no AS GROUP_NO "
                + "\n,CAST(ddgc.beginning_pressure AS REAL) AS BEGINNING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY ddgc.diver_no, ddgc.dive_no, ddgc.group_no) "
                + "\n,turnaround_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS TURNAROUND_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'TA') "
                + "\n,ending_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS ENDING_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'STO') "
                + "\n,first_ascent_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS FIRST_ASCENT_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'AS' "
                + "\nORDER BY ds.order_no "
                + "\nLIMIT 1) "
                + "\nSELECT DISTINCT "
                // Dive
                + "\nd.dive_no "
                + "\n,d.dive_type "
                + "\n,d.salinity "
                + "\n,d.status "
                + "\n,IFNULL(d.log_book_no,0) AS LOG_BOOK_NO "
                + "\n,IFNULL(d.air_temp,0.0) AS AIR_TEMP "
                + "\n,IFNULL(d.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM "
                // Me
                + "\n,IFNULL(ddme.diver_no,0) AS MY_DIVER_NO "
                + "\n,CASE WHEN ddme.diver_no IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_me) + "' "
                + "\nELSE '" + mContext.getResources().getString(R.string.sql_me) + "' "
                + "\nEND AS ME_LABEL "
                + "\n,CASE WHEN ddgme.sac IS NULL OR ddgme.sac = 0.0 THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddgme.sac "
                + "\nEND AS MY_SAC "
                + "\n,CASE WHEN ddme.RMV IS NULL OR ddme.rmv = 0.0 THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddme.rmv "
                + "\nEND AS MY_RMV "
                + "\n,IFNULL(rgme.rated_volume,0.0) AS MY_RATED_VOLUME "
                + "\n,IFNULL(rgme.rated_pressure,0.0) AS MY_RATED_PRESSURE "
                + "\n,IFNULL(tame.turnaround_pressure,0) AS MY_TURNAROUND_PRESSURE "
                + "\n,IFNULL(bgme.beginning_pressure,0) AS MY_BEGINNING_PRESSURE "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme.beginning_pressure * rgme.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme.beginning_pressure * rgme.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME "
                + "\n,IFNULL(epme.ending_pressure,0) AS MY_ENDING_PRESSURE "
                + "\n,IFNULL(fapme.first_ascent_pressure,0) AS MY_FIRST_ASCENT_PRESSURE "
                // My Buddy
                + "\n,IFNULL(drmb.diver_no,0) AS MY_BUDDY_DIVER_NO "
                + "\n,CASE WHEN drmb.last_name IS NULL AND drmb.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                + "\nELSE drmb.last_name" + " || ', ' || " + "drmb.first_name "
                + "\nEND AS MY_BUDDY_FULL_NAME "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddgmb.sac IS NULL OR ddgmb.sac = 0.0)) THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddgmb.sac, 0.0) "
                + "\nEND AS MY_BUDDY_SAC "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddmb.RMV IS NULL OR ddmb.rmv = 0.0)) THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddmb.rmv,0.0) "
                + "\nEND AS MY_BUDDY_RMV "
                + "\n,IFNULL(rgmb.rated_volume,0.0) AS MY_BUDDY_RATED_VOLUME "
                + "\n,IFNULL(rgmb.rated_pressure,0.0) AS MY_BUDDY_RATED_PRESSURE "
                + "\n,IFNULL(tamb.turnaround_pressure,0) AS MY_BUDDY_TURNAROUND_PRESSURE "
                + "\n,IFNULL(bgmb.beginning_pressure,0) AS MY_BUDDY_BEGINNING_PRESSURE "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgmb.beginning_pressure * rgmb.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgmb.beginning_pressure * rgmb.rated_volume, 0.0),2) "
                + "\nEND AS MY_BUDDY_BEGINNING_VOLUME "
                + "\n,IFNULL(epmb.ending_pressure,0) AS MY_BUDDY_ENDING_PRESSURE "
                + "\n,IFNULL(fapmb.first_ascent_Pressure,0) AS MY_BUDDY_FIRST_ASCENT_PRESSURE "
                // Dive
                + "\nFROM dive d "
                // Me
                + "\nLEFT JOIN diver_dive ddme "
                + "\nON (ddme.dive_no = d.dive_no "
                + "\nAND ddme.diver_no = 1) "
                + "\nLEFT JOIN diver_dive_group ddgme "
                + "\nON (ddgme.diver_no = ddme.diver_no "
                + "\nAND ddgme.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN groupp gme "
                + "\nON (gme.group_no = ddgme.group_no) "
                + "\nLEFT JOIN rated_group rgme "
                + "\nON (rgme.group_no = gme.group_no) "
                + "\nLEFT JOIN beginning_group bgme "
                + "\nON (bgme.diver_no = ddme.diver_no "
                + "\nAND bgme.dive_no = ddme.dive_no "
                + "\nAND bgme.group_no = gme.group_no) "
                + "\nLEFT JOIN turnaround_pressure tame "
                + "\nON (tame.diver_no = ddme.diver_no "
                + "\nAND tame.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN ending_pressure epme "
                + "\nON (epme.diver_no = ddme.diver_no "
                + "\nAND epme.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN first_ascent_pressure fapme "
                + "\nON (fapme.diver_no = ddme.diver_no "
                + "\nAND fapme.dive_no = ddme.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb "
                + "\nON (ddmb.dive_no = d.dive_no "
                + "\nAND ddmb.diver_no <> 1) "
                + "\nLEFT JOIN diver drmb "
                + "\nON (drmb.diver_no = ddmb.diver_no) "
                + "\nLEFT JOIN diver_dive_group ddgmb "
                + "\nON (ddgmb.diver_no = ddmb.diver_no "
                + "\nAND ddgmb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN groupp gmb "
                + "\nON (gmb.group_no = ddgmb.group_no) "
                + "\nLEFT JOIN rated_group rgmb "
                + "\nON (rgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN beginning_group bgmb "
                + "\nON (bgmb.diver_no = ddmb.diver_no "
                + "\nAND bgmb.dive_no = ddmb.dive_no "
                + "\nAND bgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN turnaround_pressure tamb "
                + "\nON (tamb.diver_no = ddmb.diver_no "
                + "\nAND tamb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN ending_pressure epmb "
                + "\nON (epmb.diver_no = ddmb.diver_no "
                + "\nAND epmb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN first_ascent_pressure fapmb "
                + "\nON (fapmb.diver_no = ddmb.diver_no "
                + "\nAND fapmb.dive_no = ddmb.dive_no) "
                + "\nWHERE d.dive_no = ? /*diveNo*/ "
                ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                      String.valueOf(diveNo)
                    , String.valueOf(diveNo)
                    , String.valueOf(diveNo)
                    , String.valueOf(diveNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Dive
                    diveForGraphic.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    diveForGraphic.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    // 1 = TRUE = Salt; 0 = FALSE = Fresh
                    diveForGraphic.setSalinity((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SALINITY)) == MyConstants.ONE_INT));
                    diveForGraphic.setStatus(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_STATUS)));
                    diveForGraphic.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_TYPE)));
                    diveForGraphic.setAirTemp(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_AIR_TEMP)));
                    diveForGraphic.setWaterTempBottom(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WATER_TEMP_BOTTOM)));
                    // Me
                    diveForGraphic.setMyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO")));
                    diveForGraphic.setMeLabel(cursor.getString(cursor.getColumnIndex("ME_LABEL")));
                    diveForGraphic.setMySac(cursor.getDouble(cursor.getColumnIndex("MY_SAC")));
                    diveForGraphic.setMyRmv(cursor.getDouble(cursor.getColumnIndex("MY_RMV")));
                    diveForGraphic.setMyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME")));
                    diveForGraphic.setMyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE")));
                    diveForGraphic.setMyTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("MY_TURNAROUND_PRESSURE")));
                    diveForGraphic.setMyBeginningPressure(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE")));
                    diveForGraphic.setMyBeginningVolume(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME")));
                    diveForGraphic.setMyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE")));
                    diveForGraphic.setMyFirstsAscentPressure(cursor.getDouble(cursor.getColumnIndex("MY_FIRST_ASCENT_PRESSURE")));
                    // My Buddy
                    diveForGraphic.setMyBuddyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO")));
                    diveForGraphic.setMyBuddyFullName(cursor.getString(cursor.getColumnIndex("MY_BUDDY_FULL_NAME")));
                    diveForGraphic.setMyBuddySac(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_SAC")));
                    diveForGraphic.setMyBuddyRmv(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RMV")));
                    diveForGraphic.setMyBuddyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_VOLUME")));
                    diveForGraphic.setMyBuddyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_PRESSURE")));
                    diveForGraphic.setMyBuddyTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_TURNAROUND_PRESSURE")));
                    diveForGraphic.setMyBuddyBeginningPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_BEGINNING_PRESSURE")));
                    diveForGraphic.setMyBuddyBeginningVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_BEGINNING_VOLUME")));
                    diveForGraphic.setMyBuddyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_ENDING_PRESSURE")));
                    diveForGraphic.setMyBuddyFirstsAscentPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_FIRST_ASCENT_PRESSURE")));
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void getDivesForCompare(DivesForCompare divesForCompare) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        sqlSt = "WITH rated_group AS "
                + "\n(SELECT gc.group_no AS GROUP_NO "
                + "\n,CAST(SUM(c.volume) AS REAL) AS RATED_VOLUME "
                + "\n,CAST(c.rated_pressure AS REAL) AS RATED_PRESSURE "
                + "\n,CAST(SUM(c.volume) / c.rated_pressure AS REAL) AS CONVERSION_FACTOR "
                + "\n,c.cylinder_type "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY gc.group_no "
                + "\n,c.volume "
                + "\n,c.rated_pressure "
                + "\n,c.cylinder_type) "
                + "\n,beginning_group AS "
                + "\n(SELECT ddgc.diver_no AS DIVER_NO "
                + "\n,ddgc.dive_no AS DIVE_NO "
                + "\n,ddgc.group_no AS GROUP_NO "
                + "\n,CAST(ddgc.beginning_pressure AS REAL) AS BEGINNING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY ddgc.diver_no, ddgc.dive_no, ddgc.group_no) "
                + "\n,ending_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS ENDING_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.segment_type = 'STO') "
                + "\n,ending_volume AS "
                + "\n(SELECT ds.diver_no "
                + "\n,ds.dive_no "
                + "\n,ds.calc_decreasing_volume AS ENDING_VOLUME "
                + "\nFROM dive_segment ds "
                + "\nWHERE order_no = (SELECT MAX(ds2.order_no) FROM dive_segment ds2 WHERE ds2.diver_no = ds.diver_no AND ds2.dive_no = ds.dive_no)) "
                + "\n,runtime AS "
                + "\n(SELECT ds.diver_no "
                + "\n,ds.dive_no "
                + "\n,ROUND(SUM(minute),1) AS runtime "
                + "\nFROM dive_segment ds "
                + "\nGROUP BY diver_no "
                + "\n,dive_no) "
                + "\nSELECT DISTINCT "
                // Dive 1
                + "\nd1.dive_no AS DIVE_NO1 "
                + "\n,d1.dive_type AS DIVE_TYPE1 "
                + "\n,d1.salinity AS SALINITY1 "
                + "\n,d1.status AS STATUS1 "
                + "\n,d1.average_depth AS AVERAGE_DEPTH1 "
                + "\n,IFNULL(d1.log_book_no,0) AS LOG_BOOK_NO1 "
                + "\n,IFNULL(d1.air_temp,0.0) AS AIR_TEMP1 "
                + "\n,IFNULL(d1.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM1 "
                // Diver 1: Me or My Buddy
                + "\n,IFNULL(ddme1.diver_no,0) AS MY_DIVER_NO1 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_me) + "' AS ME_LABEL1 "
                + "\n,CASE WHEN ddgme1.sac IS NULL THEN 0.0 "
                + "\nELSE ddgme1.sac "
                + "\nEND AS MY_SAC1 "
                + "\n,CASE WHEN ddme1.RMV IS NULL THEN 0.0 "
                + "\nELSE ddme1.rmv "
                + "\nEND AS MY_RMV1 "
                + "\n,IFNULL(rgme1.rated_volume,0.0) AS MY_RATED_VOLUME1 "
                + "\n,IFNULL(rgme1.rated_pressure,0.0) AS MY_RATED_PRESSURE1 "
                + "\n,IFNULL(rgme1.cylinder_type, ' ') AS MY_CYLINDER_TYPE1 "
                + "\n,IFNULL(bgme1.beginning_pressure,0) AS MY_BEGINNING_PRESSURE1 "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme1.beginning_pressure * rgme1.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme1.beginning_pressure * rgme1.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME1 "
                + "\n,IFNULL(epme1.ending_pressure,0) AS MY_ENDING_PRESSURE1 "
                + "\n,IFNULL(evme1.ending_volume,0) AS MY_ENDING_VOLUME1 "
                + "\n,IFNULL(rtme1.runtime,0) AS MY_RUNTIME1 "
                // My Buddy
                + ",IFNULL(ddmb1.diver_no,0) AS MY_BUDDY_DIVER_NO1 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_my_buddy) + "' AS MY_BUDDY1 "
                // Dive 2
                + "\n,d2.dive_no AS DIVE_NO2 "
                + "\n,d2.dive_type AS DIVE_TYPE2 "
                + "\n,d2.salinity AS SALINITY2 "
                + "\n,d2.status AS STATUS2 "
                + "\n,d2.average_depth AS AVERAGE_DEPTH2 "
                + "\n,IFNULL(d2.log_book_no,0) AS LOG_BOOK_NO2 "
                + "\n,IFNULL(d2.air_temp,0.0) AS AIR_TEMP2 "
                + "\n,IFNULL(d2.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM2 "
                // Diver 2: Me or My Buddy
                + "\n,IFNULL(ddme2.diver_no,0) AS MY_DIVER_NO2 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_me) + "' AS ME_LABEL2 "
                + "\n,CASE WHEN ddgme2.sac IS NULL THEN 0.0 "
                + "\nELSE ddgme2.sac "
                + "\nEND AS MY_SAC2 "
                + "\n,CASE WHEN ddme2.RMV IS NULL THEN 0.0 "
                + "\nELSE ddme2.rmv "
                + "\nEND AS MY_RMV2 "
                + "\n,IFNULL(rgme2.rated_volume,0.0) AS MY_RATED_VOLUME2 "
                + "\n,IFNULL(rgme2.rated_pressure,0.0) AS MY_RATED_PRESSURE2 "
                + "\n,IFNULL(rgme2.cylinder_type, ' ') AS MY_CYLINDER_TYPE2 "
                + "\n,IFNULL(bgme2.beginning_pressure,0) AS MY_BEGINNING_PRESSURE2 "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme2.beginning_pressure * rgme2.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme2.beginning_pressure * rgme2.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME2 "
                + "\n,IFNULL(epme2.ending_pressure,0) AS MY_ENDING_PRESSURE2 "
                + "\n,IFNULL(evme2.ending_volume,0) AS MY_ENDING_VOLUME2 "
                + "\n,IFNULL(rtme2.runtime,0) AS MY_RUNTIME2 "
                // My Buddy
                + "\n,IFNULL(ddmb2.diver_no,0) AS MY_BUDDY_DIVER_NO2 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_my_buddy) + "' AS MY_BUDDY2 "
                // Dive 3
                + "\n,d3.dive_no AS DIVE_NO3 "
                + "\n,d3.dive_type AS DIVE_TYPE3 "
                + "\n,d3.salinity AS SALINITY3 "
                + "\n,d3.status AS STATUS3 "
                + "\n,d3.average_depth AS AVERAGE_DEPTH3 "
                + "\n,IFNULL(d3.log_book_no,0) AS LOG_BOOK_NO3 "
                + "\n,IFNULL(d3.air_temp,0.0) AS AIR_TEMP3 "
                + "\n,IFNULL(d3.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM3 "
                // Diver 3: Me or My Buddy
                + "\n,IFNULL(ddme3.diver_no,0) AS MY_DIVER_NO3 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_me) + "' AS ME_LABEL3 "
                + "\n,CASE WHEN ddgme3.sac IS NULL THEN 0.0 "
                + "\nELSE ddgme3.sac "
                + "\nEND AS MY_SAC3 "
                + "\n,CASE WHEN ddme3.RMV IS NULL THEN 0.0 "
                + "\nELSE ddme3.rmv "
                + "\nEND AS MY_RMV3 "
                + "\n,IFNULL(rgme3.rated_volume,0.0) AS MY_RATED_VOLUME3 "
                + "\n,IFNULL(rgme3.rated_pressure,0.0) AS MY_RATED_PRESSURE3 "
                + "\n,IFNULL(rgme3.cylinder_type, ' ') AS MY_CYLINDER_TYPE3 "
                + "\n,IFNULL(bgme3.beginning_pressure,0) AS MY_BEGINNING_PRESSURE3 "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme3.beginning_pressure * rgme3.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme3.beginning_pressure * rgme3.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME3 "
                + "\n,IFNULL(epme3.ending_pressure,0) AS MY_ENDING_PRESSURE3 "
                + "\n,IFNULL(evme3.ending_volume,0) AS MY_ENDING_VOLUME3 "
                + "\n,IFNULL(rtme3.runtime,0) AS MY_RUNTIME3 "
                // My Buddy
                + ",IFNULL(ddmb3.diver_no,0) AS MY_BUDDY_DIVER_NO3 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_my_buddy) + "' AS MY_BUDDY3 "
                // Dive
                + "\nFROM dive d1 "
                // Diver 1: Me or My Buddy
                + "\nLEFT JOIN diver_dive ddme1 "
                + "\nON (ddme1.dive_no = d1.dive_no "
                + "\nAND ((%1$s = 1 AND ddme1.diver_no = 1) OR (%2$s = 2 AND ddme1.diver_no <> 1))) "
                + "\nLEFT JOIN diver_dive_group ddgme1 "
                + "\nON (ddgme1.diver_no = ddme1.diver_no "
                + "\nAND ddgme1.dive_no = ddme1.dive_no) "
                + "\nLEFT JOIN groupp gme1 "
                + "\nON (gme1.group_no = ddgme1.group_no) "
                + "\nLEFT JOIN rated_group rgme1 "
                + "\nON (rgme1.group_no = gme1.group_no) "
                + "\nLEFT JOIN beginning_group bgme1 "
                + "\nON (bgme1.diver_no = ddme1.diver_no "
                + "\nAND bgme1.dive_no = ddme1.dive_no "
                + "\nAND bgme1.group_no = gme1.group_no) "
                + "\nLEFT JOIN ending_pressure epme1 "
                + "\nON (epme1.diver_no = ddme1.diver_no "
                + "\nAND epme1.dive_no = ddme1.dive_no) "
                + "\nLEFT JOIN ending_volume evme1 "
                + "\nON (evme1.diver_no = ddme1.diver_no "
                + "\nAND evme1.dive_no = ddme1.dive_no) "
                + "\nLEFT JOIN runtime rtme1 "
                + "\nON (rtme1.diver_no = ddme1.diver_no"
                + "\nAND rtme1.dive_no = ddme1.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb1 "
                + "\nON (ddmb1.dive_no = d1.dive_no "
                + "\nAND ddmb1.diver_no <> 1) "
                // Diver 2: Me or My Buddy
                + "\nLEFT JOIN dive d2 "
                + "\nON d2.dive_no = %3$s "
                + "\nLEFT JOIN diver_dive ddme2 "
                + "\nON (ddme2.dive_no = d2.dive_no "
                + "\nAND ((%4$s = 1 AND ddme2.diver_no = 1) OR (%5$s = 2 AND ddme2.diver_no <> 1))) "
                + "\nLEFT JOIN diver_dive_group ddgme2 "
                + "\nON (ddgme2.diver_no = ddme2.diver_no "
                + "\nAND ddgme2.dive_no = ddme2.dive_no) "
                + "\nLEFT JOIN groupp gme2 "
                + "\nON (gme2.group_no = ddgme2.group_no) "
                + "\nLEFT JOIN rated_group rgme2 "
                + "\nON (rgme2.group_no = gme2.group_no) "
                + "\nLEFT JOIN beginning_group bgme2 "
                + "\nON (bgme2.diver_no = ddme2.diver_no "
                + "\nAND bgme2.dive_no = ddme2.dive_no "
                + "\nAND bgme2.group_no = gme2.group_no) "
                + "\nLEFT JOIN ending_pressure epme2 "
                + "\nON (epme2.diver_no = ddme2.diver_no "
                + "\nAND epme2.dive_no = ddme2.dive_no) "
                + "\nLEFT JOIN ending_volume evme2 "
                + "\nON (evme2.diver_no = ddme2.diver_no "
                + "\nAND evme2.dive_no = ddme2.dive_no) "
                + "\nLEFT JOIN runtime rtme2 "
                + "\nON (rtme2.diver_no = ddme2.diver_no"
                + "\nAND rtme2.dive_no = ddme2.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb2 "
                + "\nON (ddmb2.dive_no = d2.dive_no "
                + "\nAND ddmb2.diver_no <> 1) "
                // Diver 3: Me or My Buddy
                + "\nLEFT JOIN dive d3 "
                + "\nON d3.dive_no = %6$s "
                + "\nLEFT JOIN diver_dive ddme3 "
                + "\nON (ddme3.dive_no = d3.dive_no "
                + "\nAND ((%7$s = 1 AND ddme3.diver_no = 1) OR (%8$s = 2 AND ddme3.diver_no <> 1))) "
                + "\nLEFT JOIN diver_dive_group ddgme3 "
                + "\nON (ddgme3.diver_no = ddme3.diver_no "
                + "\nAND ddgme3.dive_no = ddme3.dive_no) "
                + "\nLEFT JOIN groupp gme3 "
                + "\nON (gme3.group_no = ddgme3.group_no) "
                + "\nLEFT JOIN rated_group rgme3 "
                + "\nON (rgme3.group_no = gme3.group_no) "
                + "\nLEFT JOIN beginning_group bgme3 "
                + "\nON (bgme3.diver_no = ddme3.diver_no "
                + "\nAND bgme3.dive_no = ddme3.dive_no "
                + "\nAND bgme3.group_no = gme3.group_no) "
                + "\nLEFT JOIN ending_pressure epme3 "
                + "\nON (epme3.diver_no = ddme3.diver_no "
                + "\nAND epme3.dive_no = ddme3.dive_no) "
                + "\nLEFT JOIN ending_volume evme3 "
                + "\nON (evme3.diver_no = ddme3.diver_no "
                + "\nAND evme3.dive_no = ddme3.dive_no) "
                + "\nLEFT JOIN runtime rtme3 "
                + "\nON (rtme3.diver_no = ddme3.diver_no"
                + "\nAND rtme3.dive_no = ddme3.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb3 "
                + "\nON (ddmb3.dive_no = d3.dive_no "
                + "\nAND ddmb3.diver_no <> 1) "
                + "\nWHERE d1.dive_no = %9$s /*diveNo*/ "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(divesForCompare.getMeMyBuddy1())
                ,String.valueOf(divesForCompare.getMeMyBuddy1())
                ,String.valueOf(divesForCompare.getDiveNo2())
                ,String.valueOf(divesForCompare.getMeMyBuddy2())
                ,String.valueOf(divesForCompare.getMeMyBuddy2())
                ,String.valueOf(divesForCompare.getDiveNo3())
                ,String.valueOf(divesForCompare.getMeMyBuddy3())
                ,String.valueOf(divesForCompare.getMeMyBuddy3())
                ,String.valueOf(divesForCompare.getDiveNo1())
        );

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Dive
                    // Dive 1, if any
                    divesForCompare.setMyDiverNo1(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO1")));
                    divesForCompare.setLogBookNo1(cursor.getInt(cursor.getColumnIndex("LOG_BOOK_NO1")));
                    divesForCompare.setStatus1(cursor.getString(cursor.getColumnIndex("STATUS1")));
                    divesForCompare.setCylType1(cursor.getString(cursor.getColumnIndex("MY_CYLINDER_TYPE1")));
                    divesForCompare.setSac1(cursor.getDouble(cursor.getColumnIndex("MY_SAC1")));
                    divesForCompare.setRmv1(cursor.getDouble(cursor.getColumnIndex("MY_RMV1")));
                    divesForCompare.setMyBeginningPressure1(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE1")));
                    divesForCompare.setMyEndingPressure1(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE1")));
                    divesForCompare.setMyEndingVolume1(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_VOLUME1")));
                    divesForCompare.setMyBeginningVolume1(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME1")));
                    divesForCompare.setMyRatedPressure1(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE1")));
                    divesForCompare.setMyRatedVolume1(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME1")));
                    if (divesForCompare.getStatus1() != null && (divesForCompare.getStatus1().equals(MyConstants.REAL) || divesForCompare.getStatus1().equals(MyConstants.REEL))) {
                        divesForCompare.setCalcAverageDepth1(cursor.getDouble(cursor.getColumnIndex("AVERAGE_DEPTH1")));
                    } else if (divesForCompare.getDiveNo1() > 0) {
                        divesForCompare.setCalcAverageDepth1(myCalc.getCalcAverageDepth(divesForCompare.getMyDiverNo1(), divesForCompare.getDiveNo1()));
                    } else {
                        divesForCompare.setCalcAverageDepth1(0.0);
                    }
                    divesForCompare.setRt1(MyFunctions.convertToMmSs(cursor.getDouble(cursor.getColumnIndex("MY_RUNTIME1"))));
                    divesForCompare.setRtPsi1(MyFunctions.roundUp(divesForCompare.getMyBeginningPressure1() - divesForCompare.getMyEndingPressure1(),1));
                    divesForCompare.setRtVol1(MyFunctions.roundUp(divesForCompare.getMyBeginningVolume1() - divesForCompare.getMyEndingVolume1(), 1));
                    divesForCompare.setPsiLeft1(divesForCompare.getMyEndingPressure1());
                    divesForCompare.setVolLeft1(divesForCompare.getMyEndingVolume1());
                    // 1 = TRUE = Salt; 0 = FALSE = Fresh
                    divesForCompare.setSalinity((cursor.getInt(cursor.getColumnIndex("SALINITY1")) == MyConstants.ONE_INT));
                    divesForCompare.setDiveType(cursor.getString(cursor.getColumnIndex("DIVE_TYPE1")));
                    divesForCompare.setAirTemp(cursor.getDouble(cursor.getColumnIndex("AIR_TEMP1")));
                    divesForCompare.setWaterTempBottom(cursor.getDouble(cursor.getColumnIndex("WATER_TEMP_BOTTOM1")));
                    // Me
                    divesForCompare.setMeLabel(cursor.getString(cursor.getColumnIndex("ME_LABEL1")));
                    // My Buddy
                    divesForCompare.setMyBuddy(cursor.getString(cursor.getColumnIndex("MY_BUDDY1")));
                    divesForCompare.setMyBuddyDiverNo1(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO1")));

                    // Dive 2, if any
                    divesForCompare.setMyDiverNo2(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO2")));
                    divesForCompare.setLogBookNo2(cursor.getInt(cursor.getColumnIndex("LOG_BOOK_NO2")));
                    divesForCompare.setStatus2(cursor.getString(cursor.getColumnIndex("STATUS2")));
                    divesForCompare.setCylType2(cursor.getString(cursor.getColumnIndex("MY_CYLINDER_TYPE2")));
                    divesForCompare.setSac2(cursor.getDouble(cursor.getColumnIndex("MY_SAC2")));
                    divesForCompare.setRmv2(cursor.getDouble(cursor.getColumnIndex("MY_RMV2")));
                    divesForCompare.setMyBeginningPressure2(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE2")));
                    divesForCompare.setMyEndingPressure2(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE2")));
                    divesForCompare.setMyEndingVolume2(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_VOLUME2")));
                    divesForCompare.setMyBeginningVolume2(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME2")));
                    divesForCompare.setMyRatedPressure2(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE2")));
                    divesForCompare.setMyRatedVolume2(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME2")));
                    if (divesForCompare.getStatus2() != null && (divesForCompare.getStatus2().equals(MyConstants.REAL) || divesForCompare.getStatus2().equals(MyConstants.REEL))) {
                        divesForCompare.setCalcAverageDepth2(cursor.getDouble(cursor.getColumnIndex("AVERAGE_DEPTH2")));
                    } else if (divesForCompare.getDiveNo2() > 0) {
                        divesForCompare.setCalcAverageDepth2(myCalc.getCalcAverageDepth(divesForCompare.getMyDiverNo2(), divesForCompare.getDiveNo2()));
                    } else {
                        divesForCompare.setCalcAverageDepth2(0.0);
                    }
                    divesForCompare.setRt2(MyFunctions.convertToMmSs(cursor.getDouble(cursor.getColumnIndex("MY_RUNTIME2"))));
                    divesForCompare.setRtPsi2(MyFunctions.roundUp(divesForCompare.getMyBeginningPressure2() - divesForCompare.getMyEndingPressure2(),1));
                    divesForCompare.setRtVol2(MyFunctions.roundUp(divesForCompare.getMyBeginningVolume2() - divesForCompare.getMyEndingVolume2(),1));
                    divesForCompare.setPsiLeft2(divesForCompare.getMyEndingPressure2());
                    divesForCompare.setVolLeft2(divesForCompare.getMyEndingVolume2());
                    // My Buddy
                    divesForCompare.setMyBuddyDiverNo2(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO2")));

                    // Dive 3, if any
                    divesForCompare.setMyDiverNo3(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO3")));
                    divesForCompare.setLogBookNo3(cursor.getInt(cursor.getColumnIndex("LOG_BOOK_NO3")));
                    divesForCompare.setStatus3(cursor.getString(cursor.getColumnIndex("STATUS3")));
                    divesForCompare.setCylType3(cursor.getString(cursor.getColumnIndex("MY_CYLINDER_TYPE3")));
                    divesForCompare.setSac3(cursor.getDouble(cursor.getColumnIndex("MY_SAC3")));
                    divesForCompare.setRmv3(cursor.getDouble(cursor.getColumnIndex("MY_RMV3")));
                    divesForCompare.setMyBeginningPressure3(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE3")));
                    divesForCompare.setMyEndingPressure3(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE3")));
                    divesForCompare.setMyEndingVolume3(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_VOLUME3")));
                    divesForCompare.setMyBeginningVolume3(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME3")));
                    divesForCompare.setMyRatedPressure3(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE3")));
                    divesForCompare.setMyRatedVolume3(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME3")));
                    if (divesForCompare.getStatus3() != null && (divesForCompare.getStatus3().equals(MyConstants.REAL) || divesForCompare.getStatus3().equals(MyConstants.REEL))) {
                        divesForCompare.setCalcAverageDepth3(cursor.getDouble(cursor.getColumnIndex("AVERAGE_DEPTH3")));
                    } else if (divesForCompare.getDiveNo3() > 0) {
                        divesForCompare.setCalcAverageDepth3(myCalc.getCalcAverageDepth(divesForCompare.getMyDiverNo3(), divesForCompare.getDiveNo3()));
                    } else {
                        divesForCompare.setCalcAverageDepth3(0.0);
                    }
                    divesForCompare.setRt3(MyFunctions.convertToMmSs(cursor.getDouble(cursor.getColumnIndex("MY_RUNTIME3"))));
                    divesForCompare.setRtPsi3(MyFunctions.roundUp(divesForCompare.getMyBeginningPressure3() - divesForCompare.getMyEndingPressure3(),1));
                    divesForCompare.setRtVol3(MyFunctions.roundUp(divesForCompare.getMyBeginningVolume3() - divesForCompare.getMyEndingVolume3(),1));
                    divesForCompare.setPsiLeft3(divesForCompare.getMyEndingPressure3());
                    divesForCompare.setVolLeft3(divesForCompare.getMyEndingVolume3());
                    // My buddy
                    divesForCompare.setMyBuddyDiverNo3(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO3")));
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void getDivesForCompareEmergency(DivesForCompare divesForCompare) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        sqlSt = "WITH rated_group AS "
                + "\n(SELECT gc.group_no AS GROUP_NO "
                + "\n,CAST(SUM(c.volume) AS REAL) AS RATED_VOLUME "
                + "\n,CAST(c.rated_pressure AS REAL) AS RATED_PRESSURE "
                + "\n,CAST(SUM(c.volume) / c.rated_pressure AS REAL) AS CONVERSION_FACTOR "
                + "\n,c.cylinder_type "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.EMERGENCY_GAS + "' "
                + "\nGROUP BY gc.group_no "
                + "\n,c.volume "
                + "\n,c.rated_pressure "
                + "\n,c.cylinder_type) "
                + "\n,beginning_group AS "
                + "\n(SELECT ddgc.diver_no AS DIVER_NO "
                + "\n,ddgc.dive_no AS DIVE_NO "
                + "\n,ddgc.group_no AS GROUP_NO "
                + "\n,CAST(ddgc.beginning_pressure AS REAL) AS BEGINNING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.EMERGENCY_GAS + "' "
                + "\nGROUP BY ddgc.diver_no, ddgc.dive_no, ddgc.group_no) "
                + "\n,ending_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS ENDING_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.segment_type = 'STO') "
                + "\n,ending_volume AS "
                + "\n(SELECT ds.diver_no "
                + "\n,ds.dive_no "
                + "\n,ds.calc_decreasing_volume AS ENDING_VOLUME "
                + "\nFROM dive_segment ds "
                + "\nWHERE order_no = (SELECT MAX(ds2.order_no) FROM dive_segment ds2 WHERE ds2.diver_no = ds.diver_no AND ds2.dive_no = ds.dive_no)) "
                + "\n,runtime AS "
                + "\n(SELECT ds.diver_no "
                + "\n,ds.dive_no "
                + "\n,ROUND(SUM(minute),1) AS runtime "
                + "\nFROM dive_segment ds "
                + "\nGROUP BY diver_no "
                + "\n,dive_no) "
                + "\nSELECT DISTINCT "
                // Dive 1
                + "\nd1.dive_no AS DIVE_NO1 "
                + "\n,d1.dive_type AS DIVE_TYPE1 "
                + "\n,d1.salinity AS SALINITY1 "
                + "\n,d1.status AS STATUS1 "
                + "\n,d1.average_depth AS AVERAGE_DEPTH1 "
                + "\n,IFNULL(d1.log_book_no,0) AS LOG_BOOK_NO1 "
                + "\n,IFNULL(d1.air_temp,0.0) AS AIR_TEMP1 "
                + "\n,IFNULL(d1.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM1 "
                // Diver 1: Me or My Buddy
                + "\n,IFNULL(ddme1.diver_no,0) AS MY_DIVER_NO1 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_me) + "' AS ME_LABEL1 "
                + "\n,CASE WHEN ddgme1.sac IS NULL THEN 0.0 "
                + "\nELSE ddgme1.sac "
                + "\nEND AS MY_SAC1 "
                + "\n,CASE WHEN ddme1.RMV IS NULL THEN 0.0 "
                + "\nELSE ddme1.rmv "
                + "\nEND AS MY_RMV1 "
                + "\n,IFNULL(rgme1.rated_volume,0.0) AS MY_RATED_VOLUME1 "
                + "\n,IFNULL(rgme1.rated_pressure,0.0) AS MY_RATED_PRESSURE1 "
                + "\n,IFNULL(rgme1.cylinder_type, ' ') AS MY_CYLINDER_TYPE1 "
                + "\n,IFNULL(bgme1.beginning_pressure,0) AS MY_BEGINNING_PRESSURE1 "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme1.beginning_pressure * rgme1.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme1.beginning_pressure * rgme1.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME1 "
                + "\n,IFNULL(epme1.ending_pressure,0) AS MY_ENDING_PRESSURE1 "
                + "\n,IFNULL(evme1.ending_volume,0) AS MY_ENDING_VOLUME1 "
                + "\n,IFNULL(rtme1.runtime,0) AS MY_RUNTIME1 "
                // My Buddy
                + ",IFNULL(ddmb1.diver_no,0) AS MY_BUDDY_DIVER_NO1 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_my_buddy) + "' AS MY_BUDDY1 "
                // Dive 2
                + "\n,d2.dive_no AS DIVE_NO2 "
                + "\n,d2.dive_type AS DIVE_TYPE2 "
                + "\n,d2.salinity AS SALINITY2 "
                + "\n,d2.status AS STATUS2 "
                + "\n,d2.average_depth AS AVERAGE_DEPTH2 "
                + "\n,IFNULL(d2.log_book_no,0) AS LOG_BOOK_NO2 "
                + "\n,IFNULL(d2.air_temp,0.0) AS AIR_TEMP2 "
                + "\n,IFNULL(d2.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM2 "
                // Diver 2: Me or My Buddy
                + "\n,IFNULL(ddme2.diver_no,0) AS MY_DIVER_NO2 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_me) + "' AS ME_LABEL2 "
                + "\n,CASE WHEN ddgme2.sac IS NULL THEN 0.0 "
                + "\nELSE ddgme2.sac "
                + "\nEND AS MY_SAC2 "
                + "\n,CASE WHEN ddme2.RMV IS NULL THEN 0.0 "
                + "\nELSE ddme2.rmv "
                + "\nEND AS MY_RMV2 "
                + "\n,IFNULL(rgme2.rated_volume,0.0) AS MY_RATED_VOLUME2 "
                + "\n,IFNULL(rgme2.rated_pressure,0.0) AS MY_RATED_PRESSURE2 "
                + "\n,IFNULL(rgme2.cylinder_type, ' ') AS MY_CYLINDER_TYPE2 "
                + "\n,IFNULL(bgme2.beginning_pressure,0) AS MY_BEGINNING_PRESSURE2 "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme2.beginning_pressure * rgme2.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme2.beginning_pressure * rgme2.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME2 "
                + "\n,IFNULL(epme2.ending_pressure,0) AS MY_ENDING_PRESSURE2 "
                + "\n,IFNULL(evme2.ending_volume,0) AS MY_ENDING_VOLUME2 "
                + "\n,IFNULL(rtme2.runtime,0) AS MY_RUNTIME2 "
                // My Buddy
                + "\n,IFNULL(ddmb2.diver_no,0) AS MY_BUDDY_DIVER_NO2 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_my_buddy) + "' AS MY_BUDDY2 "
                // Dive 3
                + "\n,d3.dive_no AS DIVE_NO3 "
                + "\n,d3.dive_type AS DIVE_TYPE3 "
                + "\n,d3.salinity AS SALINITY3 "
                + "\n,d3.status AS STATUS3 "
                + "\n,d3.average_depth AS AVERAGE_DEPTH3 "
                + "\n,IFNULL(d3.log_book_no,0) AS LOG_BOOK_NO3 "
                + "\n,IFNULL(d3.air_temp,0.0) AS AIR_TEMP3 "
                + "\n,IFNULL(d3.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM3 "
                // Diver 3: Me or My Buddy
                + "\n,IFNULL(ddme3.diver_no,0) AS MY_DIVER_NO3 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_me) + "' AS ME_LABEL3 "
                + "\n,CASE WHEN ddgme3.sac IS NULL THEN 0.0 "
                + "\nELSE ddgme3.sac "
                + "\nEND AS MY_SAC3 "
                + "\n,CASE WHEN ddme3.RMV IS NULL THEN 0.0 "
                + "\nELSE ddme3.rmv "
                + "\nEND AS MY_RMV3 "
                + "\n,IFNULL(rgme3.rated_volume,0.0) AS MY_RATED_VOLUME3 "
                + "\n,IFNULL(rgme3.rated_pressure,0.0) AS MY_RATED_PRESSURE3 "
                + "\n,IFNULL(rgme3.cylinder_type, ' ') AS MY_CYLINDER_TYPE3 "
                + "\n,IFNULL(bgme3.beginning_pressure,0) AS MY_BEGINNING_PRESSURE3 "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND(IFNULL(bgme3.beginning_pressure * rgme3.conversion_factor, 0.0),2) "
                + "\nELSE ROUND(IFNULL(bgme3.beginning_pressure * rgme3.rated_volume, 0.0),2) "
                + "\nEND AS MY_BEGINNING_VOLUME3 "
                + "\n,IFNULL(epme3.ending_pressure,0) AS MY_ENDING_PRESSURE3 "
                + "\n,IFNULL(evme3.ending_volume,0) AS MY_ENDING_VOLUME3 "
                + "\n,IFNULL(rtme3.runtime,0) AS MY_RUNTIME3 "
                // My Buddy
                + ",IFNULL(ddmb3.diver_no,0) AS MY_BUDDY_DIVER_NO3 "
                + "\n,'" + mContext.getResources().getString(R.string.sql_my_buddy) + "' AS MY_BUDDY3 "
                // Dive
                + "\nFROM dive d1 "
                // Diver 1: Me or My Buddy
                + "\nLEFT JOIN diver_dive ddme1 "
                + "\nON (ddme1.dive_no = d1.dive_no "
                + "\nAND ((%1$s = 1 AND ddme1.diver_no = 1) OR (%2$s = 2 AND ddme1.diver_no <> 1))) "
                + "\nLEFT JOIN diver_dive_group ddgme1 "
                + "\nON (ddgme1.diver_no = ddme1.diver_no "
                + "\nAND ddgme1.dive_no = ddme1.dive_no) "
                + "\nLEFT JOIN groupp gme1 "
                + "\nON (gme1.group_no = ddgme1.group_no) "
                + "\nLEFT JOIN rated_group rgme1 "
                + "\nON (rgme1.group_no = gme1.group_no) "
                + "\nLEFT JOIN beginning_group bgme1 "
                + "\nON (bgme1.diver_no = ddme1.diver_no "
                + "\nAND bgme1.dive_no = ddme1.dive_no "
                + "\nAND bgme1.group_no = gme1.group_no) "
                + "\nLEFT JOIN ending_pressure epme1 "
                + "\nON (epme1.diver_no = ddme1.diver_no "
                + "\nAND epme1.dive_no = ddme1.dive_no) "
                + "\nLEFT JOIN ending_volume evme1 "
                + "\nON (evme1.diver_no = ddme1.diver_no "
                + "\nAND evme1.dive_no = ddme1.dive_no) "
                + "\nLEFT JOIN runtime rtme1 "
                + "\nON (rtme1.diver_no = ddme1.diver_no"
                + "\nAND rtme1.dive_no = ddme1.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb1 "
                + "\nON (ddmb1.dive_no = d1.dive_no "
                + "\nAND ddmb1.diver_no <> 1) "
                // Diver 2: Me or My Buddy
                + "\nLEFT JOIN dive d2 "
                + "\nON d2.dive_no = %3$s "
                + "\nLEFT JOIN diver_dive ddme2 "
                + "\nON (ddme2.dive_no = d2.dive_no "
                + "\nAND ((%4$s = 1 AND ddme2.diver_no = 1) OR (%5$s = 2 AND ddme2.diver_no <> 1))) "
                + "\nLEFT JOIN diver_dive_group ddgme2 "
                + "\nON (ddgme2.diver_no = ddme2.diver_no "
                + "\nAND ddgme2.dive_no = ddme2.dive_no) "
                + "\nLEFT JOIN groupp gme2 "
                + "\nON (gme2.group_no = ddgme2.group_no) "
                + "\nLEFT JOIN rated_group rgme2 "
                + "\nON (rgme2.group_no = gme2.group_no) "
                + "\nLEFT JOIN beginning_group bgme2 "
                + "\nON (bgme2.diver_no = ddme2.diver_no "
                + "\nAND bgme2.dive_no = ddme2.dive_no "
                + "\nAND bgme2.group_no = gme2.group_no) "
                + "\nLEFT JOIN ending_pressure epme2 "
                + "\nON (epme2.diver_no = ddme2.diver_no "
                + "\nAND epme2.dive_no = ddme2.dive_no) "
                + "\nLEFT JOIN ending_volume evme2 "
                + "\nON (evme2.diver_no = ddme2.diver_no "
                + "\nAND evme2.dive_no = ddme2.dive_no) "
                + "\nLEFT JOIN runtime rtme2 "
                + "\nON (rtme2.diver_no = ddme2.diver_no"
                + "\nAND rtme2.dive_no = ddme2.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb2 "
                + "\nON (ddmb2.dive_no = d2.dive_no "
                + "\nAND ddmb2.diver_no <> 1) "
                // Diver 3: Me or My Buddy
                + "\nLEFT JOIN dive d3 "
                + "\nON d3.dive_no = %6$s "
                + "\nLEFT JOIN diver_dive ddme3 "
                + "\nON (ddme3.dive_no = d3.dive_no "
                + "\nAND ((%7$s = 1 AND ddme3.diver_no = 1) OR (%8$s = 2 AND ddme3.diver_no <> 1))) "
                + "\nLEFT JOIN diver_dive_group ddgme3 "
                + "\nON (ddgme3.diver_no = ddme3.diver_no "
                + "\nAND ddgme3.dive_no = ddme3.dive_no) "
                + "\nLEFT JOIN groupp gme3 "
                + "\nON (gme3.group_no = ddgme3.group_no) "
                + "\nLEFT JOIN rated_group rgme3 "
                + "\nON (rgme3.group_no = gme3.group_no) "
                + "\nLEFT JOIN beginning_group bgme3 "
                + "\nON (bgme3.diver_no = ddme3.diver_no "
                + "\nAND bgme3.dive_no = ddme3.dive_no "
                + "\nAND bgme3.group_no = gme3.group_no) "
                + "\nLEFT JOIN ending_pressure epme3 "
                + "\nON (epme3.diver_no = ddme3.diver_no "
                + "\nAND epme3.dive_no = ddme3.dive_no) "
                + "\nLEFT JOIN ending_volume evme3 "
                + "\nON (evme3.diver_no = ddme3.diver_no "
                + "\nAND evme3.dive_no = ddme3.dive_no) "
                + "\nLEFT JOIN runtime rtme3 "
                + "\nON (rtme3.diver_no = ddme3.diver_no"
                + "\nAND rtme3.dive_no = ddme3.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb3 "
                + "\nON (ddmb3.dive_no = d3.dive_no "
                + "\nAND ddmb3.diver_no <> 1) "
                + "\nWHERE d1.dive_no = %9$s /*diveNo*/ "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(divesForCompare.getMeMyBuddy1())
                ,String.valueOf(divesForCompare.getMeMyBuddy1())
                ,String.valueOf(divesForCompare.getDiveNo2())
                ,String.valueOf(divesForCompare.getMeMyBuddy2())
                ,String.valueOf(divesForCompare.getMeMyBuddy2())
                , String.valueOf(divesForCompare.getDiveNo3())
                ,String.valueOf(divesForCompare.getMeMyBuddy3())
                ,String.valueOf(divesForCompare.getMeMyBuddy3())
                , String.valueOf(divesForCompare.getDiveNo1())
        );

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Dive
                    // Dive 1, if any
                    divesForCompare.setMyDiverNo1(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO1")));
                    divesForCompare.setLogBookNo1(cursor.getInt(cursor.getColumnIndex("LOG_BOOK_NO1")));
                    divesForCompare.setStatus1(cursor.getString(cursor.getColumnIndex("STATUS1")));
                    divesForCompare.setCylType1(cursor.getString(cursor.getColumnIndex("MY_CYLINDER_TYPE1")));
                    divesForCompare.setSac1(cursor.getDouble(cursor.getColumnIndex("MY_SAC1")));
                    divesForCompare.setRmv1(cursor.getDouble(cursor.getColumnIndex("MY_RMV1")));
                    divesForCompare.setMyBeginningPressure1(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE1")));
                    divesForCompare.setMyEndingPressure1(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE1")));
                    divesForCompare.setMyEndingVolume1(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_VOLUME1")));
                    divesForCompare.setMyBeginningVolume1(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME1")));
                    divesForCompare.setMyRatedPressure1(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE1")));
                    divesForCompare.setMyRatedVolume1(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME1")));
                    if (divesForCompare.getStatus1() != null && (divesForCompare.getStatus1().equals(MyConstants.REAL) || divesForCompare.getStatus1().equals(MyConstants.REEL))) {
                        divesForCompare.setCalcAverageDepth1(cursor.getDouble(cursor.getColumnIndex("AVERAGE_DEPTH1")));
                    } else if (divesForCompare.getDiveNo1() > 0) {
                        divesForCompare.setCalcAverageDepth1(myCalc.getCalcAverageDepth(divesForCompare.getMyDiverNo1(), divesForCompare.getDiveNo1()));
                    } else {
                        divesForCompare.setCalcAverageDepth1(0.0);
                    }
                    divesForCompare.setRt1(MyFunctions.convertToMmSs(cursor.getDouble(cursor.getColumnIndex("MY_RUNTIME1"))));
                    divesForCompare.setRtPsi1(MyFunctions.roundUp(divesForCompare.getMyBeginningPressure1() - divesForCompare.getMyEndingPressure1(), 1));
                    divesForCompare.setRtVol1(MyFunctions.roundUp(divesForCompare.getMyBeginningVolume1() - divesForCompare.getMyEndingVolume1(), 1));
                    divesForCompare.setPsiLeft1(divesForCompare.getMyEndingPressure1());
                    divesForCompare.setVolLeft1(divesForCompare.getMyEndingVolume1());
                    // 1 = TRUE = Salt; 0 = FALSE = Fresh
                    divesForCompare.setSalinity((cursor.getInt(cursor.getColumnIndex("SALINITY1")) == MyConstants.ONE_INT));
                    divesForCompare.setDiveType(cursor.getString(cursor.getColumnIndex("DIVE_TYPE1")));
                    divesForCompare.setAirTemp(cursor.getDouble(cursor.getColumnIndex("AIR_TEMP1")));
                    divesForCompare.setWaterTempBottom(cursor.getDouble(cursor.getColumnIndex("WATER_TEMP_BOTTOM1")));
                    // Me
                    divesForCompare.setMeLabel(cursor.getString(cursor.getColumnIndex("ME_LABEL1")));
                    // My Buddy
                    divesForCompare.setMyBuddy(cursor.getString(cursor.getColumnIndex("MY_BUDDY1")));
                    divesForCompare.setMyBuddyDiverNo1(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO1")));

                    // Dive 2, if any
                    divesForCompare.setMyDiverNo2(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO2")));
                    divesForCompare.setLogBookNo2(cursor.getInt(cursor.getColumnIndex("LOG_BOOK_NO2")));
                    divesForCompare.setStatus2(cursor.getString(cursor.getColumnIndex("STATUS2")));
                    divesForCompare.setCylType2(cursor.getString(cursor.getColumnIndex("MY_CYLINDER_TYPE2")));
                    divesForCompare.setSac2(cursor.getDouble(cursor.getColumnIndex("MY_SAC2")));
                    divesForCompare.setRmv2(cursor.getDouble(cursor.getColumnIndex("MY_RMV2")));
                    divesForCompare.setMyBeginningPressure2(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE2")));
                    divesForCompare.setMyEndingPressure2(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE2")));
                    divesForCompare.setMyEndingVolume2(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_VOLUME2")));
                    divesForCompare.setMyBeginningVolume2(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME2")));
                    divesForCompare.setMyRatedPressure2(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE2")));
                    divesForCompare.setMyRatedVolume2(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME2")));
                    if (divesForCompare.getStatus2() != null && (divesForCompare.getStatus2().equals(MyConstants.REAL) || divesForCompare.getStatus2().equals(MyConstants.REEL))) {
                        divesForCompare.setCalcAverageDepth2(cursor.getDouble(cursor.getColumnIndex("AVERAGE_DEPTH2")));
                    } else if (divesForCompare.getDiveNo2() > 0) {
                        divesForCompare.setCalcAverageDepth2(myCalc.getCalcAverageDepth(divesForCompare.getMyDiverNo2(), divesForCompare.getDiveNo2()));
                    } else {
                        divesForCompare.setCalcAverageDepth2(0.0);
                    }
                    divesForCompare.setRt2(MyFunctions.convertToMmSs(cursor.getDouble(cursor.getColumnIndex("MY_RUNTIME2"))));
                    divesForCompare.setRtPsi2(MyFunctions.roundUp(divesForCompare.getMyBeginningPressure2() - divesForCompare.getMyEndingPressure2(),1));
                    divesForCompare.setRtVol2(MyFunctions.roundUp(divesForCompare.getMyBeginningVolume2() - divesForCompare.getMyEndingVolume2(),1));
                    divesForCompare.setPsiLeft2(divesForCompare.getMyEndingPressure2());
                    divesForCompare.setVolLeft2(divesForCompare.getMyEndingVolume2());
                    // My Buddy
                    divesForCompare.setMyBuddyDiverNo2(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO2")));

                    // Dive 3, if any
                    divesForCompare.setMyDiverNo3(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO3")));
                    divesForCompare.setLogBookNo3(cursor.getInt(cursor.getColumnIndex("LOG_BOOK_NO3")));
                    divesForCompare.setStatus3(cursor.getString(cursor.getColumnIndex("STATUS3")));
                    divesForCompare.setCylType3(cursor.getString(cursor.getColumnIndex("MY_CYLINDER_TYPE3")));
                    divesForCompare.setSac3(cursor.getDouble(cursor.getColumnIndex("MY_SAC3")));
                    divesForCompare.setRmv3(cursor.getDouble(cursor.getColumnIndex("MY_RMV3")));
                    divesForCompare.setMyBeginningPressure3(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE3")));
                    divesForCompare.setMyEndingPressure3(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE3")));
                    divesForCompare.setMyEndingVolume3(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_VOLUME3")));
                    divesForCompare.setMyBeginningVolume3(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME3")));
                    divesForCompare.setMyRatedPressure3(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE3")));
                    divesForCompare.setMyRatedVolume3(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME3")));
                    if (divesForCompare.getStatus3() != null && (divesForCompare.getStatus3().equals(MyConstants.REAL) || divesForCompare.getStatus3().equals(MyConstants.REEL))) {
                        divesForCompare.setCalcAverageDepth3(cursor.getDouble(cursor.getColumnIndex("AVERAGE_DEPTH3")));
                    } else if (divesForCompare.getDiveNo3() > 0) {
                        divesForCompare.setCalcAverageDepth3(myCalc.getCalcAverageDepth(divesForCompare.getMyDiverNo3(), divesForCompare.getDiveNo3()));
                    } else {
                        divesForCompare.setCalcAverageDepth3(0.0);
                    }
                    divesForCompare.setRt3(MyFunctions.convertToMmSs(cursor.getDouble(cursor.getColumnIndex("MY_RUNTIME3"))));
                    divesForCompare.setRtPsi3(MyFunctions.roundUp(divesForCompare.getMyBeginningPressure3() - divesForCompare.getMyEndingPressure3(),1));
                    divesForCompare.setRtVol3(MyFunctions.roundUp(divesForCompare.getMyBeginningVolume3() - divesForCompare.getMyEndingVolume3(),1));
                    divesForCompare.setPsiLeft3(divesForCompare.getMyEndingPressure3());
                    divesForCompare.setVolLeft3(divesForCompare.getMyEndingVolume3());
                    // My buddy
                    divesForCompare.setMyBuddyDiverNo3(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO3")));
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void  getDiveForGraphicRockbottom(Long diveNo, DiveForGraphic diveForGraphic) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        sqlSt = "WITH rated_group AS "
                + "\n(SELECT gc.group_no AS GROUP_NO "
                + "\n,CAST(SUM(c.volume) AS REAL) AS RATED_VOLUME "
                + "\n,CAST(c.rated_pressure AS REAL) AS RATED_PRESSURE "
                + "\n,CAST(SUM(c.volume) / c.rated_pressure AS REAL) AS CONVERSION_FACTOR "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY gc.group_no "
                + "\n,c.volume "
                + "\n,c.rated_pressure) "
                + "\n,beginning_group AS "
                + "\n(SELECT ddgc.diver_no AS DIVER_NO "
                + "\n,ddgc.dive_no AS DIVE_NO "
                + "\n,ddgc.group_no AS GROUP_NO "
                + "\n,CAST(ddgc.beginning_pressure AS REAL) AS BEGINNING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nGROUP BY ddgc.diver_no, ddgc.dive_no, ddgc.group_no) "
                + "\n,turnaround_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS TURNAROUND_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type IN ('ADS','ASS','AS') "
                + "\nGROUP BY ds.diver_no "
                + "\n,ds.dive_no "
                + "\n,ds.calc_decreasing_pressure "
                + "\nORDER BY CASE WHEN ds.segment_type = 'ADS' THEN 10 "
                + "\nWHEN ds.segment_type = 'ASS' THEN 20 "
                + "\nWHEN ds.segment_type = 'AS' THEN 30 "
                + "\nEND "
                + "\nLIMIT 2) "
                + "\n,ending_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS ENDING_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'STO') "
                + "\nSELECT DISTINCT "
                // Dive
                + "\nd.dive_no "
                + "\n,d.dive_type "
                + "\n,d.salinity "
                + "\n,d.status "
                + "\n,IFNULL(d.log_book_no,0) AS LOG_BOOK_NO "
                + "\n,IFNULL(d.air_temp,0.0) AS AIR_TEMP "
                + "\n,IFNULL(d.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM "
                // Me
                + "\n,IFNULL(ddme.diver_no,0) AS MY_DIVER_NO "
                + "\n,CASE WHEN ddme.diver_no IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_me) + "' "
                + "\nELSE '" + mContext.getResources().getString(R.string.sql_me) + "' "
                + "\nEND AS ME_LABEL "
                + "\n,CASE WHEN ddgme.sac IS NULL OR ddgme.sac = 0.0 THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddgme.sac "
                + "\nEND AS MY_SAC "
                + "\n,CASE WHEN ddme.RMV IS NULL OR ddme.rmv = 0.0 THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddme.rmv "
                + "\nEND AS MY_RMV "
                + "\n,IFNULL(rgme.rated_volume,0.0) AS MY_RATED_VOLUME "
                + "\n,IFNULL(rgme.rated_pressure,0.0) AS MY_RATED_PRESSURE "
                + "\n,IFNULL(tame.turnaround_pressure,0) AS MY_TURNAROUND_PRESSURE "
                + "\n,IFNULL(bgme.beginning_pressure,0) AS MY_BEGINNING_PRESSURE "
                + "\n,ROUND(IFNULL(bgme.beginning_pressure * rgme.conversion_factor, 0.0),2) AS MY_BEGINNING_VOLUME "
                + "\n,IFNULL(epme.ending_pressure,0) AS MY_ENDING_PRESSURE "
                // My Buddy
                + "\n,IFNULL(drmb.diver_no,0) AS MY_BUDDY_DIVER_NO "
                + "\n,CASE WHEN drmb.last_name IS NULL AND drmb.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                + "\nELSE drmb.last_name" + " || ', ' || " + "drmb.first_name "
                + "\nEND AS MY_BUDDY_FULL_NAME "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddgmb.sac IS NULL OR ddgmb.sac = 0.0)) THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddgmb.sac, 0.0) "
                + "\nEND AS MY_BUDDY_SAC "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddmb.RMV IS NULL OR ddmb.rmv = 0.0)) THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddmb.rmv,0.0) "
                + "\nEND AS MY_BUDDY_RMV "
                + "\n,IFNULL(rgmb.rated_volume,0.0) AS MY_BUDDY_RATED_VOLUME "
                + "\n,IFNULL(rgmb.rated_pressure,0.0) AS MY_BUDDY_RATED_PRESSURE "
                + "\n,IFNULL(tamb.turnaround_pressure,0) AS MY_BUDDY_TURNAROUND_PRESSURE "
                + "\n,IFNULL(bgmb.beginning_pressure,0) AS MY_BUDDY_BEGINNING_PRESSURE "
                + "\n,ROUND(IFNULL(bgmb.beginning_pressure * rgmb.conversion_factor, 0.0),2) AS MY_BUDDY_BEGINNING_VOLUME "
                + "\n,IFNULL(epmb.ending_pressure,0) AS MY_BUDDY_ENDING_PRESSURE "
                + "\nFROM dive d "
                // Me
                + "\nLEFT JOIN diver_dive ddme "
                + "\nON (ddme.dive_no = d.dive_no "
                + "\nAND ddme.diver_no = 1) "
                + "\nLEFT JOIN diver_dive_group ddgme "
                + "\nON (ddgme.diver_no = ddme.diver_no "
                + "\nAND ddgme.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN groupp gme "
                + "\nON (gme.group_no = ddgme.group_no) "
                + "\nLEFT JOIN rated_group rgme "
                + "\nON (rgme.group_no = gme.group_no) "
                + "\nLEFT JOIN beginning_group bgme "
                + "\nON (bgme.diver_no = ddme.diver_no "
                + "\nAND bgme.dive_no = ddme.dive_no "
                + "\nAND bgme.group_no = gme.group_no) "
                + "\nLEFT JOIN turnaround_pressure tame "
                + "\nON (tame.diver_no = ddme.diver_no "
                + "\nAND tame.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN ending_pressure epme "
                + "\nON (epme.diver_no = ddme.diver_no "
                + "\nAND epme.dive_no = ddme.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb "
                + "\nON (ddmb.dive_no = d.dive_no "
                + "\nAND ddmb.diver_no <> 1) "
                + "\nLEFT JOIN diver drmb "
                + "\nON (drmb.diver_no = ddmb.diver_no) "
                + "\nLEFT JOIN diver_dive_group ddgmb "
                + "\nON (ddgmb.diver_no = ddmb.diver_no "
                + "\nAND ddgmb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN groupp gmb "
                + "\nON (gmb.group_no = ddgmb.group_no) "
                + "\nLEFT JOIN rated_group rgmb "
                + "\nON (rgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN beginning_group bgmb "
                + "\nON (bgmb.diver_no = ddmb.diver_no "
                + "\nAND bgmb.dive_no = ddmb.dive_no "
                + "\nAND bgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN turnaround_pressure tamb "
                + "\nON (tamb.diver_no = ddmb.diver_no "
                + "\nAND tamb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN ending_pressure epmb "
                + "\nON (epmb.diver_no = ddmb.diver_no "
                + "\nAND epmb.dive_no = ddmb.dive_no) "
                + "\nWHERE d.dive_no = ? /*diveNo*/"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diveNo), String.valueOf(diveNo), String.valueOf(diveNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Dive
                    diveForGraphic.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    diveForGraphic.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    // 1 = TRUE = Salt; 0 = FALSE = Fresh
                    diveForGraphic.setSalinity((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SALINITY)) == MyConstants.ONE_INT));
                    diveForGraphic.setStatus(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_STATUS)));
                    diveForGraphic.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_TYPE)));
                    diveForGraphic.setAirTemp(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_AIR_TEMP)));
                    diveForGraphic.setWaterTempBottom(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WATER_TEMP_BOTTOM)));
                    // Me
                    diveForGraphic.setMyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO")));
                    diveForGraphic.setMeLabel(cursor.getString(cursor.getColumnIndex("ME_LABEL")));
                    diveForGraphic.setMySac(cursor.getDouble(cursor.getColumnIndex("MY_SAC")));
                    diveForGraphic.setMyRmv(cursor.getDouble(cursor.getColumnIndex("MY_RMV")));
                    diveForGraphic.setMyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME")));
                    diveForGraphic.setMyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE")));
                    diveForGraphic.setMyTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("MY_TURNAROUND_PRESSURE")));
                    diveForGraphic.setMyBeginningPressure(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE")));
                    diveForGraphic.setMyBeginningVolume(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME")));
                    diveForGraphic.setMyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE")));
                    // My Buddy
                    diveForGraphic.setMyBuddyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO")));
                    diveForGraphic.setMyBuddyFullName(cursor.getString(cursor.getColumnIndex("MY_BUDDY_FULL_NAME")));
                    diveForGraphic.setMyBuddySac(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_SAC")));
                    diveForGraphic.setMyBuddyRmv(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RMV")));
                    diveForGraphic.setMyBuddyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_VOLUME")));
                    diveForGraphic.setMyBuddyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_PRESSURE")));
                    diveForGraphic.setMyBuddyTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_TURNAROUND_PRESSURE")));
                    diveForGraphic.setMyBuddyBeginningPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_BEGINNING_PRESSURE")));
                    diveForGraphic.setMyBuddyBeginningVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_BEGINNING_VOLUME")));
                    diveForGraphic.setMyBuddyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_ENDING_PRESSURE")));
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void getDiveForGraphicEmergency(Long diveNo, DiveForGraphic diveForGraphic) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        sqlSt = "WITH rated_group AS "
                + "\n(SELECT gc.group_no AS GROUP_NO "
                + "\n,CAST(SUM(c.volume) AS REAL) AS RATED_VOLUME "
                + "\n,CAST(c.rated_pressure AS REAL) AS RATED_PRESSURE "
                + "\n,CAST(SUM(c.volume) / c.rated_pressure AS REAL) AS CONVERSION_FACTOR "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (gc.cylinder_no = c.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.EMERGENCY_GAS + "' "
                + "\nGROUP BY gc.group_no "
                + "\n,c.volume "
                + "\n,c.rated_pressure) "
                + "\n,beginning_group AS "
                + "\n(SELECT ddgc.diver_no AS DIVER_NO "
                + "\n,ddgc.dive_no AS DIVE_NO "
                + "\n,ddgc.group_no AS GROUP_NO "
                + "\n,CAST(ddgc.beginning_pressure AS REAL) AS BEGINNING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                + "\nWHERE usage_type = '" + MyConstants.EMERGENCY_GAS + "' "
                + "\nGROUP BY ddgc.diver_no, ddgc.dive_no, ddgc.group_no) "
                + "\n,ending_pressure AS "
                + "\n(SELECT ds.diver_no AS DIVER_NO "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ds.calc_decreasing_pressure AS ENDING_PRESSURE "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'STO') "
                + "\nSELECT DISTINCT "
                // Dive
                + "\nd.dive_no "
                + "\n,d.dive_type "
                + "\n,d.salinity "
                + "\n,d.status "
                + "\n,IFNULL(d.log_book_no,0) AS LOG_BOOK_NO "
                + "\n,IFNULL(d.air_temp,0.0) AS AIR_TEMP "
                + "\n,IFNULL(d.water_temp_bottom,0.0) AS WATER_TEMP_BOTTOM "
                // Me
                + "\n,IFNULL(ddme.diver_no,0) AS MY_DIVER_NO "
                + "\n,CASE WHEN ddme.diver_no IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_me) + "' "
                + "\nELSE '" + mContext.getResources().getString(R.string.sql_me) + "' "
                + "\nEND AS ME_LABEL "
                + "\n,CASE WHEN ddgme.sac IS NULL OR ddgme.sac = 0.0 THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddgme.sac "
                + "\nEND AS MY_SAC "
                + "\n,CASE WHEN ddme.RMV IS NULL OR ddme.rmv = 0.0 THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE ddme.rmv "
                + "\nEND AS MY_RMV "
                + "\n,IFNULL(rgme.rated_volume,0.0) AS MY_RATED_VOLUME "
                + "\n,IFNULL(rgme.rated_pressure,0.0) AS MY_RATED_PRESSURE "
                + "\n,IFNULL(rgme.rated_pressure,0.0) AS MY_TURNAROUND_PRESSURE "
                + "\n,IFNULL(bgme.beginning_pressure,0) AS MY_BEGINNING_PRESSURE "
                + "\n,ROUND(IFNULL(bgme.beginning_pressure * rgme.conversion_factor, 0.0),2) AS MY_BEGINNING_VOLUME "
                + "\n,IFNULL(epme.ending_pressure,0) AS MY_ENDING_PRESSURE "
                // My Buddy
                + "\n,IFNULL(drmb.diver_no,0) AS MY_BUDDY_DIVER_NO "
                + "\n,CASE WHEN drmb.last_name IS NULL AND drmb.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                + "\nELSE drmb.last_name" + " || ', ' || " + "drmb.first_name "
                + "\nEND AS MY_BUDDY_FULL_NAME "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddgmb.sac IS NULL OR ddgmb.sac = 0.0)) THEN '" + String.valueOf(myCalc.getSacDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddgmb.sac, 0.0) "
                + "\nEND AS MY_BUDDY_SAC "
                + "\n,CASE WHEN (drmb.diver_no IS NOT NULL AND (ddmb.RMV IS NULL OR ddmb.rmv = 0.0)) THEN '" + String.valueOf(myCalc.getRmvDefault()) + "' " // Conservative number if no history exist
                + "\nELSE IFNULL(ddmb.rmv,0.0) "
                + "\nEND AS MY_BUDDY_RMV "
                + "\n,IFNULL(rgmb.rated_volume,0.0) AS MY_BUDDY_RATED_VOLUME "
                + "\n,IFNULL(rgmb.rated_pressure,0.0) AS MY_BUDDY_RATED_PRESSURE "
                + "\n,IFNULL(rgmb.rated_pressure,0.0) AS MY_BUDDY_TURNAROUND_PRESSURE "
                + "\n,IFNULL(bgmb.beginning_pressure,0) AS MY_BUDDY_BEGINNING_PRESSURE "
                + "\n,ROUND(IFNULL(bgmb.beginning_pressure * rgmb.conversion_factor, 0.0),2) AS MY_BUDDY_BEGINNING_VOLUME "
                + "\n,IFNULL(epmb.ending_pressure,0) AS MY_BUDDY_ENDING_PRESSURE "
                + "\nFROM dive d "
                // Me
                + "\nLEFT JOIN diver_dive ddme "
                + "\nON (ddme.dive_no = d.dive_no "
                + "\nAND ddme.diver_no = 1) "
                + "\nLEFT JOIN diver_dive_group ddgme "
                + "\nON (ddgme.diver_no = ddme.diver_no "
                + "\nAND ddgme.dive_no = ddme.dive_no) "
                + "\nLEFT JOIN groupp gme "
                + "\nON (gme.group_no = ddgme.group_no) "
                + "\nLEFT JOIN rated_group rgme "
                + "\nON (rgme.group_no = gme.group_no) "
                + "\nLEFT JOIN beginning_group bgme "
                + "\nON (bgme.diver_no = ddme.diver_no "
                + "\nAND bgme.dive_no = ddme.dive_no "
                + "\nAND bgme.group_no = gme.group_no) "
                + "\nLEFT JOIN ending_pressure epme "
                + "\nON (epme.diver_no = ddme.diver_no "
                + "\nAND epme.dive_no = ddme.dive_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb "
                + "\nON (ddmb.dive_no = d.dive_no "
                + "\nAND ddmb.diver_no <> 1) "
                + "\nLEFT JOIN diver drmb "
                + "\nON (drmb.diver_no = ddmb.diver_no) "
                + "\nLEFT JOIN diver_dive_group ddgmb "
                + "\nON (ddgmb.diver_no = ddmb.diver_no "
                + "\nAND ddgmb.dive_no = ddmb.dive_no) "
                + "\nLEFT JOIN groupp gmb "
                + "\nON (gmb.group_no = ddgmb.group_no) "
                + "\nLEFT JOIN rated_group rgmb "
                + "\nON (rgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN beginning_group bgmb "
                + "\nON (bgmb.diver_no = ddmb.diver_no "
                + "\nAND bgmb.dive_no = ddmb.dive_no "
                + "\nAND bgmb.group_no = gmb.group_no) "
                + "\nLEFT JOIN ending_pressure epmb "
                + "\nON (epmb.diver_no = ddmb.diver_no "
                + "\nAND epmb.dive_no = ddmb.dive_no) "
                + "\nWHERE d.dive_no = ? /*diveNo*/"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diveNo),String.valueOf(diveNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    // Dive
                    diveForGraphic.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    diveForGraphic.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    // 1 = TRUE = Salt; 0 = FALSE = Fresh
                    diveForGraphic.setSalinity((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SALINITY)) == MyConstants.ONE_INT));
                    diveForGraphic.setStatus(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_STATUS)));
                    diveForGraphic.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_TYPE)));
                    diveForGraphic.setAirTemp(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_AIR_TEMP)));
                    diveForGraphic.setWaterTempBottom(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_WATER_TEMP_BOTTOM)));
                    // Me
                    diveForGraphic.setMyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO")));
                    diveForGraphic.setMeLabel(cursor.getString(cursor.getColumnIndex("ME_LABEL")));
                    diveForGraphic.setMySac(cursor.getDouble(cursor.getColumnIndex("MY_SAC")));
                    diveForGraphic.setMyRmv(cursor.getDouble(cursor.getColumnIndex("MY_RMV")));
                    diveForGraphic.setMyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_RATED_VOLUME")));
                    diveForGraphic.setMyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_RATED_PRESSURE")));
                    diveForGraphic.setMyTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("MY_TURNAROUND_PRESSURE")));
                    diveForGraphic.setMyBeginningPressure(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_PRESSURE")));
                    diveForGraphic.setMyBeginningVolume(cursor.getDouble(cursor.getColumnIndex("MY_BEGINNING_VOLUME")));
                    diveForGraphic.setMyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_ENDING_PRESSURE")));
                    // My Buddy
                    diveForGraphic.setMyBuddyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO")));
                    diveForGraphic.setMyBuddyFullName(cursor.getString(cursor.getColumnIndex("MY_BUDDY_FULL_NAME")));
                    diveForGraphic.setMyBuddySac(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_SAC")));
                    diveForGraphic.setMyBuddyRmv(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RMV")));
                    diveForGraphic.setMyBuddyRatedVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_VOLUME")));
                    diveForGraphic.setMyBuddyRatedPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_RATED_PRESSURE")));
                    diveForGraphic.setMyBuddyTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_TURNAROUND_PRESSURE")));
                    diveForGraphic.setMyBuddyBeginningPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_BEGINNING_PRESSURE")));
                    diveForGraphic.setMyBuddyBeginningVolume(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_BEGINNING_VOLUME")));
                    diveForGraphic.setMyBuddyEndingPressure(cursor.getDouble(cursor.getColumnIndex("MY_BUDDY_ENDING_PRESSURE")));

                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    int getPlanCount(long diveNo) {
        Cursor cursor = null;
        String sqlSt;
        int planCount;

        sqlSt = "SELECT COUNT(*) AS PLAN_COUNT FROM dive_plan WHERE dive_no = ?";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                planCount = cursor.getInt(cursor.getColumnIndex("PLAN_COUNT"));
            } else {
                planCount = 0;
            }
            Log.d(LOG_TAG, "Total PLAN_COUNT rows = " + cursor.getCount());
            return planCount;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    int getLastLogBookNo() {
        Cursor cursor = null;
        String sqlSt;
        int lastLogBookNo;

        sqlSt = "SELECT MAX(log_book_no) AS LAST_LOG_BOOK_NO FROM dive";

        try {
            cursor = mDb.rawQuery(sqlSt,null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastLogBookNo = cursor.getInt(cursor.getColumnIndex("LAST_LOG_BOOK_NO"));
            } else {
                lastLogBookNo = 0;
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
            return lastLogBookNo;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void getDiveLast(DiveLast diveLast) {
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "SELECT IFNULL(date,0) AS LAST_DIVE_DATE " +
                "\n,IFNULL(bottom_time,0.0) AS BOTTOM_TIME " +
                "\nFROM dive " +
                "\nWHERE status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') " + // Real in Spanish is Real
                "\nORDER BY date  DESC " +
                "\nLIMIT 1";

        try {
            cursor = mDb.rawQuery(sqlSt,null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                diveLast.setLastDate(cursor.getLong(cursor.getColumnIndex("LAST_DIVE_DATE")));
                diveLast.setBottomTime(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_BOTTOM_TIME)));
            } else {
                diveLast.setLastDate(MyConstants.ZERO_L);
                diveLast.setBottomTime(MyConstants.ZERO_D);
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    Double getSumBeginningPressure(Long diverNo, Long diveNo) {
        Cursor cursor = null;
        String sqlSt;
        Double sumBeginningPressure;

        sqlSt =   "SELECT ddgc.beginning_pressure AS SUM_BEGINNING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                // NOTE: TG for Travel Gas is reserved for future use
//                + "WHERE gc.usage_type IN ('BG','TG') "
                + "\nWHERE gc.usage_type = '" + MyConstants.BOTTOM_GAS + "' "
                + "\nAND ddgc.diver_no = ? /*diverNo*/ "
                + "\nAND ddgc.dive_no = ? /*diveNo*/ "
                + "\nLIMIT 1";

        try {
            cursor = mDb.rawQuery(sqlSt,new String [] {String.valueOf(diverNo), String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                sumBeginningPressure = cursor.getDouble(cursor.getColumnIndex("SUM_BEGINNING_PRESSURE"));
            } else {
                sumBeginningPressure = MyConstants.ZERO_D;
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
            return sumBeginningPressure;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    Double getSumEndingPressure(Long diverNo, Long diveNo) {
        Cursor cursor = null;
        String sqlSt;
        Double sumEndingPressure;

        sqlSt = "SELECT ending_pressure AS SUM_ENDING_PRESSURE "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN diver_dive_group ddg "
                + "\nON (ddg.diver_no = ddgc.diver_no "
                + "\nAND ddg.dive_no = ddgc.dive_no "
                + "\nAND ddg.group_no = ddgc.group_no) "
                + "\nINNER JOIN groupp g "
                + "\nON (g.group_no = ddg.group_no "
                + "\nAND g.diver_no = ddg.diver_no) "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = g.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no "
                + "\nAND gc.usage_type = '" + MyConstants.BOTTOM_GAS + "') "
                + "\nWHERE ddgc.diver_no = ? /*diverNo*/ "
                + "\nAND ddgc.dive_no = ? /*diveNo*/ "
                + "\nGROUP BY ddgc.diver_no, ddgc.diver_no ";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diverNo), String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                sumEndingPressure = cursor.getDouble(cursor.getColumnIndex("SUM_ENDING_PRESSURE"));
            } else {
                sumEndingPressure = MyConstants.ZERO_D;
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
            return sumEndingPressure;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    ArrayList<DivePick> getAllDivesWBuddy() {
        ArrayList<DivePick> divesDiver = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "SELECT d.dive_no "
                + "\n,d.log_book_no "
                + "\n,d.date "
                + "\n,d.status "
                + "\n,d.dive_type "
                + "\n,CASE WHEN TRIM(d.location) IS NULL OR d.location IN ('',' ') THEN '" + mContext.getResources().getString(R.string.sql_location_unknown) + "' "
                + "\nELSE d.location "
                + "\nEND AS LOCATION "
                + "\n,CASE WHEN TRIM(d.dive_site) IS NULL OR d.dive_site IN ('',' ') THEN '" + mContext.getResources().getString(R.string.sql_dive_site_unknown) + "' "
                + "\nELSE d.dive_site "
                + "\nEND AS DIVE_SITE "
                + "\n,dt.description AS DIVE_TYPE_DESC "
                + "\n,CASE WHEN drmb.last_name IS NULL AND drmb.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                + "\nELSE drmb.last_name" + " || ', ' || " + "drmb.first_name "
                + "\nEND AS FULL_NAME "
                + "\n,IFNULL(ddme.diver_no,0) AS MY_DIVER_NO "
                + "\n,IFNULL(ddmB.diver_no,0) AS MY_BUDDY_DIVER_NO "
                + "\n,gme.description "
                + "\nFROM dive d "
                + "\nINNER JOIN dive_type dt "
                + "\nON (dt.dive_type = d.dive_type) "
                // Me
                + "\nLEFT JOIN diver_dive ddme "
                + "\nON (ddme.dive_no = d.dive_no "
                + "\nAND ddme.diver_no = 1) "
                + "\nINNER JOIN diver_dive_group ddgme "
                + "\nON (ddgme.diver_no = ddme.diver_no "
                + "\nAND ddgme.dive_no = ddme.dive_no) "
                + "\nINNER JOIN groupp gme "
                + "\nON (gme.group_no = ddgme.group_no) "
                // My Buddy
                + "\nLEFT JOIN diver_dive ddmb "
                + "\nON (ddmb.dive_no = d.dive_no "
                + "\nAND ddmb.diver_no <> 1) "
                + "\nLEFT JOIN diver drmb "
                + "\nON (drmb.diver_no = ddmb.diver_no) "
                + "\nORDER BY d.dive_no DESC";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DivePick divePick = new DivePick();
                    divePick.setContext(mContext);
                    divePick.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    divePick.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_TYPE)));
                    divePick.setLocation(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOCATION)));
                    divePick.setDiveSite(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_SITE)));
                    divePick.setDiveTypeDesc(cursor.getString(cursor.getColumnIndex("DIVE_TYPE_DESC")));
                    // Transform the Dive Date from Integer/Long to a Date String
                    Long diveDate = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DATE));
                    divePick.setDate(MyFunctions.convertDateFromLongToDate(diveDate));
                    divePick.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    divePick.setStatus(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_STATUS)));
                    divePick.setGroupDesc(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DESCRIPTION)));
                    divePick.setMyBuddyFullName(cursor.getString(cursor.getColumnIndex("FULL_NAME")));
                    divePick.setMyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_DIVER_NO")));
                    divePick.setMyBuddyDiverNo(cursor.getLong(cursor.getColumnIndex("MY_BUDDY_DIVER_NO")));
                    divesDiver.add(divePick);
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return divesDiver;
    }

    void createDive(Dive dive, boolean restore) {
        try {
            ContentValues values = new ContentValues();
            // Common
            // PK is autoincrement but DIVE_NO is populated for a Database Restore
            if (restore) {
                values.put(AirDBHelper.TABLE_DIVE_DIVE_NO, dive.getDiveNo());
            }
            // 1- Planning
            values.put(AirDBHelper.TABLE_DIVE_STATUS, dive.getStatus());
            // Planning Me
            // None
            // Planning My Buddy
            // None
            // 2- Summary
            values.put(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO, dive.getLogBookNo());
            values.put(AirDBHelper.TABLE_DIVE_TYPE, dive.getDiveType());
            // 1 = TRUE = Salt; 0 = FALSE = Fresh
            values.put(AirDBHelper.TABLE_DIVE_SALINITY, (dive.getSalinity()) ? 1 : 0);
            // Transform the Dive Date and Time In from String to Integer/Long
            values.put(AirDBHelper.TABLE_DIVE_DATE, MyFunctions.convertDateTimeToLong(dive.getDate(),dive.getHour(),dive.getMinute()));
            values.put(AirDBHelper.TABLE_DIVE_BOTTOM_TIME, dive.getBottomTime());
            values.put(AirDBHelper.TABLE_DIVE_AVERAGE_DEPTH, dive.getAverageDepth());
            values.put(AirDBHelper.TABLE_DIVE_MAXIMUM_DEPTH, dive.getMaximumDepth());
            values.put(AirDBHelper.TABLE_DIVE_LOCATION, dive.getLocation());
            values.put(AirDBHelper.TABLE_DIVE_DIVE_SITE, dive.getDiveSite());
            values.put(AirDBHelper.TABLE_DIVE_PURPOSE, dive.getPurpose());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_SUMMARY, dive.getNoteSummary());
            // 3- Environment
            values.put(AirDBHelper.TABLE_DIVE_ALTITUDE, dive.getAltitude());
            values.put(AirDBHelper.TABLE_DIVE_DIVE_BOAT, dive.getDiveBoat());
            values.put(AirDBHelper.TABLE_DIVE_VISIBILITY, dive.getVisibility());
            values.put(AirDBHelper.TABLE_DIVE_AIR_TEMP, dive.getAirTemp());
            values.put(AirDBHelper.TABLE_DIVE_WATER_TEMP_SURFACE, dive.getWaterTempSurface());
            values.put(AirDBHelper.TABLE_DIVE_WATER_TEMP_BOTTOM, dive.getWaterTempBottom());
            values.put(AirDBHelper.TABLE_DIVE_WATER_TEMP_AVERAGE, dive.getWaterTempAverage());
            values.put(AirDBHelper.TABLE_DIVE_ENVIRONMENT, dive.getEnvironment());
            values.put(AirDBHelper.TABLE_DIVE_PLATFORM, dive.getPlatform());
            values.put(AirDBHelper.TABLE_DIVE_WEATHER, dive.getWeather());
            values.put(AirDBHelper.TABLE_DIVE_CONDITION, dive.getCondition());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_ENVIRONMENT, dive.getNoteEnvironment());
            // 4- Gas/Consumption (Real, not Planning)
            values.put(AirDBHelper.TABLE_DIVE_NOTE_GAS, dive.getNoteGas());
            // 5- Gear
            values.put(AirDBHelper.TABLE_DIVE_SUIT, dive.getSuit());
            values.put(AirDBHelper.TABLE_DIVE_WEIGHT, dive.getWeight());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_GEAR, dive.getNoteGas());
            // 6- Problem
            values.put(AirDBHelper.TABLE_DIVE_THERMAL_COMFORT, dive.getThermalComfort());
            values.put(AirDBHelper.TABLE_DIVE_WORK_LOAD, dive.getWorkLoad());
            values.put(AirDBHelper.TABLE_DIVE_PROBLEM, dive.getProblem());
            values.put(AirDBHelper.TABLE_DIVE_MALFUNCTION, dive.getMalfunction());
            values.put(AirDBHelper.TABLE_DIVE_ANY_SYMPTOM, dive.getAnySymptom());
            values.put(AirDBHelper.TABLE_DIVE_EXPOSURE_ALTITUDE, dive.getExposureAltitude());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_PROBLEM, dive.getNoteProblem());
            // 7- Dive Computer
            //    None for now
            // 8- Graph
            //    None for now
            long id = mDb.insert(AirDBHelper.TABLE_DIVE, null, values);
            if (!restore) {
                dive.setDiveNo(id);
            }
            Log.d(LOG_TAG, "Inserted DIVE_NO is " + String.valueOf(dive.getDiveNo()));
        } catch (SQLException e){
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateDive(Dive dive) {
        try {
            // Common
            String whereClause = AirDBHelper.TABLE_DIVE_DIVE_NO + "=" + dive.getDiveNo();
            Log.d(LOG_TAG, "Updated DIVE_NO is " + String.valueOf(dive.getDiveNo()));
            ContentValues values = new ContentValues();
            // 1- Planning
            values.put(AirDBHelper.TABLE_DIVE_STATUS, dive.getStatus());
            // Planning Me
            // None
            // Planning My Buddy
            // None
            // 2- Summary
            values.put(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO, dive.getLogBookNo());
            values.put(AirDBHelper.TABLE_DIVE_DIVE_TYPE, dive.getDiveType());
            // 1 = TRUE = Salt; 0 = FALSE = Fresh
            values.put(AirDBHelper.TABLE_DIVE_SALINITY, (dive.getSalinity()) ? 1 : 0);
            // Transform the Dive Date and Time In from String to Integer/Long
            values.put(AirDBHelper.TABLE_DIVE_DATE, MyFunctions.convertDateTimeToLong(dive.getDate(),dive.getHour(),dive.getMinute()));
            values.put(AirDBHelper.TABLE_DIVE_BOTTOM_TIME, dive.getBottomTime());
            values.put(AirDBHelper.TABLE_DIVE_AVERAGE_DEPTH, dive.getAverageDepth());
            values.put(AirDBHelper.TABLE_DIVE_MAXIMUM_DEPTH, dive.getMaximumDepth());
            values.put(AirDBHelper.TABLE_DIVE_LOCATION, dive.getLocation());
            values.put(AirDBHelper.TABLE_DIVE_DIVE_SITE, dive.getDiveSite());
            values.put(AirDBHelper.TABLE_DIVE_PURPOSE, dive.getPurpose());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_SUMMARY, dive.getNoteSummary());
            // 3- Environment
            values.put(AirDBHelper.TABLE_DIVE_ALTITUDE, dive.getAltitude());
            values.put(AirDBHelper.TABLE_DIVE_DIVE_BOAT, dive.getDiveBoat());
            values.put(AirDBHelper.TABLE_DIVE_VISIBILITY, dive.getVisibility());
            values.put(AirDBHelper.TABLE_DIVE_AIR_TEMP, dive.getAirTemp());
            values.put(AirDBHelper.TABLE_DIVE_WATER_TEMP_SURFACE, dive.getWaterTempSurface());
            values.put(AirDBHelper.TABLE_DIVE_WATER_TEMP_BOTTOM, dive.getWaterTempBottom());
            values.put(AirDBHelper.TABLE_DIVE_WATER_TEMP_AVERAGE, dive.getWaterTempAverage());
            values.put(AirDBHelper.TABLE_DIVE_ENVIRONMENT, dive.getEnvironment());
            values.put(AirDBHelper.TABLE_DIVE_PLATFORM, dive.getPlatform());
            values.put(AirDBHelper.TABLE_DIVE_WEATHER, dive.getWeather());
            values.put(AirDBHelper.TABLE_DIVE_CONDITION, dive.getCondition());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_ENVIRONMENT, dive.getNoteEnvironment());
            // 4- Gas/Consumption (Real, not Planning)
            values.put(AirDBHelper.TABLE_DIVE_NOTE_GAS, dive.getNoteGas());
            // 5- Gear
            values.put(AirDBHelper.TABLE_DIVE_SUIT, dive.getSuit());
            values.put(AirDBHelper.TABLE_DIVE_WEIGHT, dive.getWeight());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_GEAR, dive.getNoteGas());
            // 6- Problem
            values.put(AirDBHelper.TABLE_DIVE_THERMAL_COMFORT, dive.getThermalComfort());
            values.put(AirDBHelper.TABLE_DIVE_WORK_LOAD, dive.getWorkLoad());
            values.put(AirDBHelper.TABLE_DIVE_PROBLEM, dive.getProblem());
            values.put(AirDBHelper.TABLE_DIVE_MALFUNCTION, dive.getMalfunction());
            values.put(AirDBHelper.TABLE_DIVE_ANY_SYMPTOM, dive.getAnySymptom());
            values.put(AirDBHelper.TABLE_DIVE_EXPOSURE_ALTITUDE, dive.getExposureAltitude());
            values.put(AirDBHelper.TABLE_DIVE_NOTE_PROBLEM, dive.getNoteProblem());
            // 7- Dive Computer
            //    None for now
            // 8- Graph
            //    None for now
            mDb.update(AirDBHelper.TABLE_DIVE, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteDive(long diveNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_DIVE_NO + "=" + diveNo;
            Log.d(LOG_TAG, "Deleted DIVE_NO is " + String.valueOf(diveNo));
            mDb.delete(AirDBHelper.TABLE_DIVE, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDive() {
        try {
            Log.d(LOG_TAG, "Drop DIVE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVE_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<DivePlan> getAllDivePlanByDiveNo(Long diveNo) {
        ArrayList<DivePlan> divePlans = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "SELECT dp.dive_plan_no "
                + "\n,dp.dive_no "
                + "\n,dp.order_no "
                + "\n,dp.depth "
                + "\n,dp.minute "
                + "\n,d.log_book_no "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN dive d "
                + "\nON (d.dive_no = dp.dive_no) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY dp.dive_no, dp.order_no";

        try {
            cursor = mDb.rawQuery(sqlSt, new String[]{String.valueOf(diveNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DivePlan divePlan = new DivePlan();
                    divePlan.setDivePlanNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_DIVE_PLAN_NO)));
                    divePlan.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_DIVE_NO)));
                    divePlan.setOrderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_ORDER_NO)));
                    divePlan.setDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_DEPTH)));
                    divePlan.setMinute(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_MINUTE)));
                    divePlan.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    divePlans.add(divePlan);
                }
            }
            Log.d(LOG_TAG, "Total DIVE_PLAN rows = " + cursor.getCount());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return divePlans;
    }

    void getDivePlan(DivePlan divePlan) {
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DIVE_PLAN, COLUMNS_DIVE_PLAN, AirDBHelper.TABLE_DIVE_PLAN_DIVE_PLAN_NO + " = " + divePlan.getDivePlanNo(), null, null, null, null)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                divePlan.setDivePlanNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_DIVE_PLAN_NO)));
                divePlan.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_DIVE_NO)));
                divePlan.setOrderNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_ORDER_NO)));
                divePlan.setDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_DEPTH)));
                divePlan.setMinute(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_PLAN_MINUTE)));
            }
            Log.d(LOG_TAG, "Total DIVE-PLAN rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Boolean isDivePlanSorted(Long diveNo, Long orderNo, Double depth) {
        Cursor cursor = null;
        boolean isSorted;

        // NOTE: Do not replace %1$s by ?
        String sqlSt = "WITH prev_dp AS " +
                "\n(SELECT d.dive_no " +
                "\n,CAST(IFNULL(dp.order_no,000) AS INTEGER) AS ORDER_NO " +
                "\n,CAST(IFNULL(dp.depth,999.9) AS REAL) AS DEPTH " +
                "\nFROM dive d " +
                "\nLEFT JOIN dive_plan dp " +
                "\nON (dp.dive_no = d.dive_no " +
                "\nAND dp.order_no <= %1$s /*order_no*/) " +
                "\nWHERE d.dive_no = %2$s /*diveNo*/ " +
                "\nORDER BY dp.dive_no ASC " +
                "\n,dp.order_no DESC " +
                // NOTE: Leave as is
                "\nLIMIT 1) " +
                "\nSELECT CAST(COUNT(*) AS INTEGER) AS SORTED " +
                "\nFROM prev_dp pdp " +
                "\nWHERE pdp.dive_no = %3$s /*diveNo*/ " +
                "\nAND ((pdp.order_no + 1) <= %4$s /*order_no*/ ) " +
                "\nAND ((pdp.depth - 0.1) > %5$s ) /*depth*/ ";

        sqlSt =  String.format(sqlSt
                ,String.valueOf(orderNo)
                ,String.valueOf(diveNo)
                ,String.valueOf(diveNo)
                ,String.valueOf(orderNo)
                ,String.valueOf(depth)
        );

        try {
            cursor = mDb.rawQuery(sqlSt, null);
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                isSorted = cursor.getInt(cursor.getColumnIndex("SORTED")) == MyConstants.ONE_INT;
            } else {
                isSorted = false;
            }
            Log.d(LOG_TAG, "Total DIVE_PLAN rows = " + cursor.getCount());
            return isSorted;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    Long getDivePlanLastOrderNo(Long diveNo) {
        Cursor cursor = null;
        String sqlSt;
        Long lastOrderNo;

        sqlSt = "SELECT MAX(order_no) AS LAST_ORDER_NO "
                + "\nFROM dive_plan "
                + "\nWHERE dive_no = ? /*diveNo*/";

        try {
            cursor = mDb.rawQuery(sqlSt,new String [] {String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                lastOrderNo = cursor.getLong(cursor.getColumnIndex("LAST_ORDER_NO"));
            } else {
                lastOrderNo = MyConstants.ZERO_L;
            }
            Log.d(LOG_TAG, "Total DIVE_PLAN rows = " + cursor.getCount());
            return lastOrderNo;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    Double getDivePlanMaxDepth(Long diveNo) {
        Cursor cursor = null;
        String sqlSt;
        Double maxDepth;

        sqlSt = "SELECT MAX(depth) AS MAX_DEPTH "
                + "\nFROM dive_plan "
                + "\nWHERE dive_no = ? /*diveNo*/";

        try {
            cursor = mDb.rawQuery(sqlSt,new String [] {String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                maxDepth = cursor.getDouble(cursor.getColumnIndex("MAX_DEPTH"));
            } else {
                maxDepth = MyConstants.ZERO_D;
            }
            Log.d(LOG_TAG, "Total DIVE_PLAN rows = " + cursor.getCount());
            return maxDepth;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void createDivePlan(DivePlan divePlan, boolean restore) {
        try {
            ContentValues values = new ContentValues();
            // PK is autoincrement but DIVE_PLAN_NO is populated for a Database Restore
            if (restore) {
                values.put(AirDBHelper.TABLE_DIVE_PLAN_DIVE_PLAN_NO, divePlan.getDivePlanNo());
            }
            values.put(AirDBHelper.TABLE_DIVE_PLAN_DIVE_NO, divePlan.getDiveNo());
            values.put(AirDBHelper.TABLE_DIVE_PLAN_ORDER_NO, divePlan.getOrderNo());
            values.put(AirDBHelper.TABLE_DIVE_PLAN_DEPTH, divePlan.getDepth());
            values.put(AirDBHelper.TABLE_DIVE_PLAN_MINUTE, divePlan.getMinute());
            long id = mDb.insert(AirDBHelper.TABLE_DIVE_PLAN, null, values);
            if (!restore) {
                divePlan.setDivePlanNo(id);
            }
            Log.d(LOG_TAG, "Inserted DIVE_PLAN_NO is " + String.valueOf(divePlan.getDivePlanNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateDivePlan(DivePlan divePlan) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_PLAN_DIVE_PLAN_NO + "=" + divePlan.getDivePlanNo();
            Log.d(LOG_TAG, "Updated DIVE_PLAN_NO is " + String.valueOf(divePlan.getDivePlanNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVE_PLAN_ORDER_NO, divePlan.getOrderNo());
            values.put(AirDBHelper.TABLE_DIVE_PLAN_DEPTH, divePlan.getDepth());
            values.put(AirDBHelper.TABLE_DIVE_PLAN_MINUTE, divePlan.getMinute());
            mDb.update(AirDBHelper.TABLE_DIVE_PLAN, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteDivePlanByDiveNoOrderNo(long diveNo, long orderNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_PLAN_DIVE_NO + "=" + diveNo + " AND " + AirDBHelper.TABLE_DIVE_PLAN_ORDER_NO + "=" + orderNo;
            Log.d(LOG_TAG, "Deleted DIVE_NO is " + String.valueOf(diveNo) + " and ORDER_NO is" + String.valueOf(orderNo));
            mDb.delete(AirDBHelper.TABLE_DIVE_PLAN, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDivePlan() {
        try {
            Log.d(LOG_TAG, "Drop DIVE_PLAN");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVE_PLAN);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVE_PLAN);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVE_PLAN_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DIVER Data Access
    void getDiver(Long diverNo, Diver diver) {

        // Get the Preferences

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DIVER, COLUMNS_DIVER, AirDBHelper.TABLE_DIVER_DIVER_NO + " = " + diverNo, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                diver.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVER_NO)));
                diver.setFirstName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_FIRST_NAME)));
                diver.setFirstNameOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_FIRST_NAME)));
                diver.setMiddleName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MIDDLE_NAME)));
                diver.setLastName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_LAST_NAME)));
                diver.setLastNameOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_LAST_NAME)));
                // 1 = TRUE = Man; 0 = FALSE = Woman
                diver.setGender((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_GENDER)) == MyConstants.ONE_INT));
                // Transform the Birth Date from Integer/Long to Date
                Long birthDate = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_BIRTH_DATE));
                if (!birthDate.equals(MyConstants.ZERO_L)) {
                    diver.setBirthDate(MyFunctions.convertDateFromLongToDate(birthDate));
                }
                diver.setPhone(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_PHONE)));
                diver.setEmail(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_EMAIL)));
                diver.setCertificationBody(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_CERTIFICATION_BODY)));
                diver.setCertificationLevel(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_CERTIFICATION_LEVEL)));
                diver.setMaxDepthAllowed(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED)));
                diver.setMaxDepthAllowedOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED)));
            } else {
                diver.setDiverNo(MyConstants.ZERO_L);
                diver.setFirstName(null);
                diver.setFirstNameOld(" ");
                diver.setMiddleName(null);
                diver.setLastName(mContext.getResources().getString(R.string.sql_no_buddy));
                diver.setLastNameOld(" ");
                // Default to Man
                diver.setGender(true);
                diver.setBirthDate(null);
                diver.setPhone(null);
                diver.setEmail(null);
                diver.setCertificationBody(null);
                diver.setCertificationLevel(null);

                diver.setMaxDepthAllowed(myCalc.getMaxDepthAllowed());
                diver.setMaxDepthAllowedOld(myCalc.getMaxDepthAllowed());
            }
            Log.d(LOG_TAG, "Total DIVER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Double getDiverMaxDepthAllowed(Long diverNo) {
        Diver diver = new Diver();
        diver.setContext(mContext);
        getDiver(diverNo,diver);
        return diver.getMaxDepthAllowed();
    }

    ArrayList<Diver> getAllDivers(Long diverNo, Long diveNo, int logBookNo) {
        ArrayList<Diver> divers = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        if (diverNo == MyConstants.MINUS_ONE_L && diveNo == MyConstants.MINUS_ONE_L && logBookNo == MyConstants.ZERO_INT) {
            // Looking for Divers from Main
            // Looking for Divers from Load History
            // Returning the count for all cylinder types for all divers, including the Buddies
            // Returning all Divers except Me (diver_no = 1)
            sqlSt = "SELECT dr.diver_no "
                    + "\n,dr.first_name "
                    + "\n,dr.middle_name "
                    + "\n,dr.last_name "
                    + "\n,dr.gender "
                    + "\n,dr.birth_date "
                    + "\n,dr.phone "
                    + "\n,dr.email "
                    + "\n,dr.certification_body "
                    + "\n,dr.certification_level "
                    + "\n,dr.max_depth_allowed "
                    + "\n,? AS LOG_BOOK_NO /*logBookNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,COUNT(dd.dive_no) AS DIVES "
                    + "\nFROM diver dr "
                    + "\nLEFT JOIN diver_dive dd "
                    + "\nON (dd.diver_no = dr.diver_no) "
                    + "\nWHERE dr.diver_no <> 1 "
                    + "\nGROUP BY dr.diver_no "
                    + "\n,dr.first_name "
                    + "\n,dr.middle_name "
                    + "\n,dr.last_name "
                    + "\n,dr.gender "
                    + "\n,dr.birth_date "
                    + "\n,dr.phone "
                    + "\n,dr.email "
                    + "\n,dr.certification_body "
                    + "\n,dr.certification_level "
                    + "\n,dr.max_depth_allowed "
                    + "\nORDER BY dr.last_name ASC, dr.first_name ASC";

        } else if (diverNo > MyConstants.MINUS_ONE_L && diveNo == MyConstants.MINUS_ONE_L && logBookNo > MyConstants.ZERO_INT) {
            // Looking for Divers for My Buddy
            // Returning the count for all cylinder types for all divers, including the Buddies
            // Returning all Divers except Me (diver_no = 1 OR is_primary = 'M') and Extra Divers (is_primary = 'N')
            // The buddy already assigned can still be retrieved
            sqlSt = "SELECT dr.diver_no "
                    + "\n,dr.first_name "
                    + "\n,dr.middle_name "
                    + "\n,dr.last_name "
                    + "\n,dr.gender "
                    + "\n,dr.birth_date "
                    + "\n,dr.phone "
                    + "\n,dr.email "
                    + "\n,dr.certification_body "
                    + "\n,dr.certification_level "
                    + "\n,dr.max_depth_allowed "
                    + "\n,? AS LOG_BOOK_NO /*logBookNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,COUNT(dd.dive_no) AS DIVES "
                    + "\nFROM diver dr "
                    + "\nLEFT JOIN diver_dive dd "
                    + "\nON (dd.diver_no = dr.diver_no) "
                    + "\nWHERE dr.diver_no <> 1 "
                    + "\nAND NOT EXISTS (SELECT 1 "
                    + "\nFROM diver_dive dd "
                    + "\nWHERE dd.diver_no = dr.diver_no "
                    + "\nAND dd.is_primary = 'N') " // Extra diver
                    + "\nGROUP BY dr.diver_no "
                    + "\n,dr.first_name "
                    + "\n,dr.middle_name "
                    + "\n,dr.last_name "
                    + "\n,dr.gender "
                    + "\n,dr.birth_date "
                    + "\n,dr.phone "
                    + "\n,dr.email "
                    + "\n,dr.certification_body "
                    + "\n,dr.certification_level "
                    + "\n,dr.max_depth_allowed "
                    + "\nORDER BY dr.last_name ASC, dr.first_name ASC";

        } else  {
            // Looking for Divers from Extra Divers
            // Returning the count for all cylinder types for all divers, including the Buddies
            // Returning all available Divers
            // Except Me (diver_no = 1 OR is_primary = 'M'), Extra Divers (is_primary = 'N') and Primary Divers (My Buddy) (is_primary = 'Y')
            sqlSt = "SELECT dr.diver_no "
                    + "\n,dr.first_name "
                    + "\n,dr.middle_name "
                    + "\n,dr.last_name "
                    + "\n,dr.gender "
                    + "\n,dr.birth_date "
                    + "\n,dr.phone "
                    + "\n,dr.email "
                    + "\n,dr.certification_body "
                    + "\n,dr.certification_level "
                    + "\n,dr.max_depth_allowed "
                    + "\n,? AS LOG_BOOK_NO /*logBookNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,COUNT(dd.dive_no) AS DIVES "
                    + "\nFROM diver dr "
                    + "\nLEFT JOIN diver_dive dd "
                    + "\nON (dd.diver_no = dr.diver_no) "
                    + "\nWHERE dr.diver_no <> 1 "
                    + "\nAND NOT EXISTS (SELECT 1 "
                    + "\nFROM diver_dive dd "
                    + "\nWHERE dd.diver_no = dr.diver_no "
                    + "\nAND dd.is_primary IN ('Y', 'N')) " // Y=My Buddy, N=Extra Diver
                    + "\nGROUP BY dr.diver_no "
                    + "\n,dr.first_name "
                    + "\n,dr.middle_name "
                    + "\n,dr.last_name "
                    + "\n,dr.gender "
                    + "\n,dr.birth_date "
                    + "\n,dr.phone "
                    + "\n,dr.email "
                    + "\n,dr.certification_body "
                    + "\n,dr.certification_level "
                    + "\n,dr.max_depth_allowed "
                    + "\nORDER BY dr.last_name ASC, dr.first_name ASC";
        }

        try {
            cursor = mDb.rawQuery(sqlSt,new String [] {String.valueOf(logBookNo), String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Diver diver = new Diver();
                    diver.setContext(mContext);
                    diver.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVER_NO)));
                    diver.setFirstName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_FIRST_NAME)));
                    diver.setMiddleName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MIDDLE_NAME)));
                    diver.setLastName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_LAST_NAME)));
                    diver.setDives(cursor.getInt(cursor.getColumnIndex("DIVES")));

                    // 1 = TRUE = Man; 0 = FALSE = Woman
                    diver.setGender((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_GENDER)) == MyConstants.ONE_INT));
                    // Transform the Birth Date from Integer/Long to String
                    Long birthDate = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_BIRTH_DATE));
                    if (!birthDate.equals(MyConstants.ZERO_L)) {
                        diver.setBirthDate(MyFunctions.convertDateFromLongToDate(birthDate));
                    }
                    diver.setPhone(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_PHONE)));
                    diver.setEmail(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_EMAIL)));
                    diver.setCertificationBody(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_CERTIFICATION_BODY)));
                    diver.setCertificationLevel(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_CERTIFICATION_LEVEL)));
                    diver.setMaxDepthAllowed(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED)));
                    diver.setMaxDepthAllowedOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED)));
                    diver.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    diver.setDiveNo(cursor.getLong(cursor.getColumnIndex("DIVE_NO")));
                    divers.add(diver);
                }
            }
            Log.d(LOG_TAG, "Total DIVER rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return divers;
    }

    ArrayList<Diver> getAllDiversExtra(int logBookNo, Long diveNo) {
        ArrayList<Diver> divers = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        // Returning the count for all cylinder types for all divers, excluding the Buddies
        sqlSt = "SELECT dr.diver_no "
                + "\n,dr.first_name "
                + "\n,dr.middle_name "
                + "\n,dr.last_name "
                + "\n,dr.gender "
                + "\n,dr.birth_date "
                + "\n,dr.phone "
                + "\n,dr.email "
                + "\n,dr.certification_body "
                + "\n,dr.certification_level "
                + "\n,dr.max_depth_allowed "
                + "\n,? AS LOG_BOOK_NO /*logBookNo*/ "
                + "\n,COUNT(dd.dive_no) AS DIVES "
                + "\nFROM diver dr "
                + "\nLEFT JOIN diver_dive dd "
                + "\nON (dd.diver_no = dr.diver_no) "
                + "\nWHERE dr.diver_no <> 1 "
                + "\nAND dd.dive_no = ? /*diveNo*/ "
                + "\nAND dd.is_primary NOT IN ('M','Y') " // M=Me, Y=My Buddy
                + "\nGROUP BY dr.diver_no "
                + "\n,dr.first_name "
                + "\n,dr.middle_name "
                + "\n,dr.last_name "
                + "\n,dr.gender "
                + "\n,dr.birth_date "
                + "\n,dr.phone "
                + "\n,dr.email "
                + "\n,dr.certification_body "
                + "\n,dr.certification_level "
                + "\n,dr.max_depth_allowed "
                + "\nORDER BY dr.last_name ASC, dr.first_name ASC";

        try {
            cursor = mDb.rawQuery(sqlSt,new String [] {String.valueOf(logBookNo),String.valueOf(diveNo)});
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Diver diver = new Diver();
                    diver.setContext(mContext);
                    diver.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVER_NO)));
                    diver.setFirstName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_FIRST_NAME)));
                    diver.setMiddleName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MIDDLE_NAME)));
                    diver.setLastName(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_LAST_NAME)));
                    diver.setDives(cursor.getInt(cursor.getColumnIndex("DIVES")));

                    // 1 = TRUE = Man; 0 = FALSE = Woman
                    diver.setGender((cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_GENDER)) == MyConstants.ONE_INT));
                    // Transform the Birth Date from Integer/Long to String
                    Long birthDate = cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_BIRTH_DATE));
                    if (!birthDate.equals(MyConstants.ZERO_L)) {
                        diver.setBirthDate(MyFunctions.convertDateFromLongToDate(birthDate));
                    }
                    diver.setPhone(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_PHONE)));
                    diver.setEmail(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_EMAIL)));
                    diver.setCertificationBody(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_CERTIFICATION_BODY)));
                    diver.setCertificationLevel(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_CERTIFICATION_LEVEL)));
                    diver.setMaxDepthAllowed(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED)));
                    diver.setMaxDepthAllowedOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED)));
                    diver.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    diver.setDiveNo(diveNo);
                    divers.add(diver);
                }
            }
            Log.d(LOG_TAG, "Total DIVER rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return divers;
    }

    void createDiver(Diver diver, boolean restore) {
        try {
            ContentValues values = new ContentValues();
            // PK is autoincrement but DIVER_NO is populated for a Database Restore
            if (restore) {
                values.put(AirDBHelper.TABLE_DIVER_DIVER_NO, diver.getDiverNo());
            }
            values.put(AirDBHelper.TABLE_DIVER_FIRST_NAME, diver.getFirstName());
            values.put(AirDBHelper.TABLE_DIVER_MIDDLE_NAME, diver.getMiddleName());
            values.put(AirDBHelper.TABLE_DIVER_LAST_NAME, diver.getLastName());
            // 1 = TRUE = Man; 0 = FALSE = Woman
            values.put(AirDBHelper.TABLE_DIVER_GENDER, (diver.getGender()) ? 1 : 0);
            // Transform the Birth Date from String to Integer/Long
            values.put(AirDBHelper.TABLE_DIVER_BIRTH_DATE, MyFunctions.convertDateFromDateToLong(diver.getBirthDate()));
            values.put(AirDBHelper.TABLE_DIVER_PHONE, diver.getPhone());
            values.put(AirDBHelper.TABLE_DIVER_EMAIL, diver.getEmail());
            values.put(AirDBHelper.TABLE_DIVER_CERTIFICATION_BODY, diver.getCertificationBody());
            values.put(AirDBHelper.TABLE_DIVER_CERTIFICATION_LEVEL, diver.getCertificationLevel());
            values.put(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED, diver.getMaxDepthAllowed());
            long id = mDb.insert(AirDBHelper.TABLE_DIVER, null, values);
            if (!restore) {
                diver.setDiverNo(id);
            }
            Log.d(LOG_TAG, "Inserted DIVER_NO is " + String.valueOf(diver.getDiverNo()));
        } catch (SQLException e){
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateDiver(Diver diver) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVER_NO + "=" + diver.getDiverNo();
            Log.d(LOG_TAG, "Updated DIVER_NO is " + String.valueOf(diver.getDiverNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVER_FIRST_NAME, diver.getFirstName());
            values.put(AirDBHelper.TABLE_DIVER_MIDDLE_NAME, diver.getMiddleName());
            values.put(AirDBHelper.TABLE_DIVER_LAST_NAME, diver.getLastName());
            // 1 = TRUE = Man; 0 = FALSE = Woman
            values.put(AirDBHelper.TABLE_DIVER_GENDER, (diver.getGender()) ? 1 : 0);
            // Transform the Birth Date from String to Integer/Long
            values.put(AirDBHelper.TABLE_DIVER_BIRTH_DATE, MyFunctions.convertDateFromDateToLong(diver.getBirthDate()));
            values.put(AirDBHelper.TABLE_DIVER_PHONE, diver.getPhone());
            values.put(AirDBHelper.TABLE_DIVER_EMAIL, diver.getEmail());
            values.put(AirDBHelper.TABLE_DIVER_CERTIFICATION_BODY, diver.getCertificationBody());
            values.put(AirDBHelper.TABLE_DIVER_CERTIFICATION_LEVEL, diver.getCertificationLevel());
            values.put(AirDBHelper.TABLE_DIVER_MAX_DEPTH_ALLOWED, diver.getMaxDepthAllowed());
            mDb.update(AirDBHelper.TABLE_DIVER, values, whereClause, null);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    Integer deleteDiver(long diverNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVER_NO + "=" + diverNo;
            Log.d(LOG_TAG, "Deleted DIVER_NO is " + String.valueOf(diverNo));
            // No RI on the Diver table since we are deleting a diver with DELETE CASCADE
            // All entries for a Diver will be deleted
            mDb.delete(AirDBHelper.TABLE_DIVER, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void dropDiver() {
        try {
            Log.d(LOG_TAG, "Drop DIVER ");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_I1);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    // DIVER_DIVE Data Access
    void createDiverDive(DiverDive diverDive) {
        try {
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVER_DIVE_DIVER_NO, diverDive.getDiverNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_DIVE_NO, diverDive.getDiveNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_RMV, diverDive.getRmv());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_IS_PRIMARY, diverDive.getIsPrimary());
            long id = mDb.insert(AirDBHelper.TABLE_DIVER_DIVE, null, values);
            diverDive.setDiverNo(id);
            Log.d(LOG_TAG, "Inserted DIVER_NO is " + String.valueOf(diverDive.getDiverNo()) + " and DIVE_NO is " + String.valueOf(diverDive.getDiveNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateDiverDive(DiverDive diverDive) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_DIVER_NO + " = " + diverDive.getDiverNo() + " AND " +  AirDBHelper.TABLE_DIVER_DIVE_DIVE_NO + " = " + diverDive.getDiveNo();
            Log.d(LOG_TAG, "Updated DIVER_NO is " + String.valueOf(diverDive.getDiverNo()) + " and DIVE_NO is " + String.valueOf(diverDive.getDiveNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVER_DIVE_RMV, String.valueOf(diverDive.getRmv()));
            values.put(AirDBHelper.TABLE_DIVER_DIVE_IS_PRIMARY, String.valueOf(diverDive.getIsPrimary()));

            mDb.update(AirDBHelper.TABLE_DIVER_DIVE, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteDiverDive(long diverNo, long diveNo) {// Make sure the call has the same order for the arguments
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_DIVER_NO + "=" + diverNo + " AND " + AirDBHelper.TABLE_DIVER_DIVE_DIVE_NO + "=" + diveNo;
            Log.d(LOG_TAG, "Deleted DIVER_NO is " + String.valueOf(diverNo) + " and DIVE_NO is " + String.valueOf(diveNo));
            mDb.delete(AirDBHelper.TABLE_DIVER_DIVE, whereClause, null);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDiverDive() {
        try {
            Log.d(LOG_TAG, "Drop DIVER_DIVE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVER_DIVE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_DIVE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_DIVE_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DIVER_DIVE_GROUP Data Access
    void createDiverDiveGroup(DiverDiveGroup diverDiveGroup) {
        try {
            ContentValues values = new ContentValues();
            if (diverDiveGroup.getDiverNo() == MyConstants.ZERO_L) {
                return;
            }
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_DIVER_NO, diverDiveGroup.getDiverNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_DIVE_NO, diverDiveGroup.getDiveNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_GROUP_NO, diverDiveGroup.getGroupNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_SAC, diverDiveGroup.getSac());
            long id = mDb.insert(AirDBHelper.TABLE_DIVER_DIVE_GROUP, null, values);
            // TODO: Do we need this if no autoincrement all
            diverDiveGroup.setDiverNo(id);
            Log.d(LOG_TAG, "Inserted DIVER_NO is " + String.valueOf(diverDiveGroup.getDiverNo())
                    + " and DIVE_NO is" + String.valueOf(diverDiveGroup.getDiveNo())
                    + " and GROUP_NO is " + String.valueOf(diverDiveGroup.getGroupNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateDiverDiveGroup(DiverDiveGroup diverDiveGroup) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_GROUP_DIVER_NO + "=" + diverDiveGroup.getDiverNo()
                    + " AND " +  AirDBHelper.TABLE_DIVER_DIVE_GROUP_DIVE_NO + "=" + diverDiveGroup.getDiveNo()
                    + " AND " +  AirDBHelper.TABLE_DIVER_DIVE_GROUP_GROUP_NO + "=" + diverDiveGroup.getGroupNo();
            Log.d(LOG_TAG, "Updated DIVER_NO is " + String.valueOf(diverDiveGroup.getDiverNo())
                    + " and DIVE_NO is " + String.valueOf(diverDiveGroup.getDiveNo())
                    + " and GROUP_NO is " + String.valueOf(diverDiveGroup.getGroupNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_SAC, diverDiveGroup.getSac());
            mDb.update(AirDBHelper.TABLE_DIVER_DIVE_GROUP, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteDiverDiveGroupByGroupNo(long groupNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_GROUP_GROUP_NO + "=" + groupNo;
            Log.d(LOG_TAG, "Deleted DIVER_DIVE_GROUP with GROUP_NO is " + String.valueOf(groupNo));
            mDb.delete(AirDBHelper.TABLE_DIVER_DIVE_GROUP, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteDiverDiveGroupByDiverNoDiveNo(long diverNo, long diveNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_GROUP_DIVER_NO + "=" + diverNo + " AND " + AirDBHelper.TABLE_DIVER_DIVE_GROUP_DIVE_NO + "=" + diveNo;
            Log.d(LOG_TAG, "Deleted DIVER_DIVE_GROUP with DIVER_NO " + String.valueOf(diverNo) + " and DIVE_NO is " + String.valueOf(diveNo));
            mDb.delete(AirDBHelper.TABLE_DIVER_DIVE_GROUP, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDiverDiveGroup() {
        try {
            Log.d(LOG_TAG, "Drop DIVER_DIVE_GROUP");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVER_DIVE_GROUP);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_DIVE_GROUP);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_DIVE_GROUP_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DIVER_DIVE_GROUP_CYLINDER Data Access
    ArrayList<DiverDiveGroupCyl> getAllDiverDiveGroupCylinder(DiverDiveGroupCyl diverDiveGroupCylArg) {

        String sqlSt;
        ArrayList<DiverDiveGroupCyl> diverDiveGroupCyls = new ArrayList<>();

        sqlSt = "SELECT DISTINCT ddgc.diver_no "
                + "\n,ddgc.dive_no "
                + "\n,ddgc.group_no "
                + "\n,c.cylinder_type "
                + "\n,ddgc.beginning_pressure "
                + "\n,ddgc.ending_pressure "
                + "\n,gc.usage_type "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN cylinder c "
                + "\nON (c.cylinder_no = ddgc.cylinder_no) "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no) "
                + "\nWHERE ddgc.diver_no = ? /*diverNo*/ "
                + "\nAND ddgc.dive_no = ? /*diveNo*/ "
                + "\nAND ddgc.group_no = ? /*group_no*/ "
                + "\nORDER BY gc.usage_type "
                ;
        try {
            try (Cursor cursor = mDb.rawQuery(sqlSt, new String[]{String.valueOf(diverDiveGroupCylArg.getDiverNo())
                                                       , String.valueOf(diverDiveGroupCylArg.getDiveNo())
                                                       , String.valueOf(diverDiveGroupCylArg.getGroupNo())})) {

                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
                        diverDiveGroupCyl.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO)));
                        diverDiveGroupCyl.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO)));
                        diverDiveGroupCyl.setLogBookNo(diverDiveGroupCylArg.getLogBookNo());
                        diverDiveGroupCyl.setGroupNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO)));
                        diverDiveGroupCyl.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                        diverDiveGroupCyl.setBeginningPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE)));
                        diverDiveGroupCyl.setEndingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE)));
                        diverDiveGroupCyl.setUsageType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                        diverDiveGroupCyls.add(diverDiveGroupCyl);
                    }
                }
                Log.d(LOG_TAG, "Total DIVER_DIVE_GROUP_CYLINDER rows = " + cursor.getCount());
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception raised with a value of " + e);
        }
        return diverDiveGroupCyls;
    }

    DiverDiveGroupCyl getDiverDiveGroupCylinderByGroup(DiverDiveGroupCyl diverDiveGroupCylArg) {

        /**
         * Even if the group contains more than one cylinders
         * It always returns only one definition
         * because both cylinders are in a twin set
         * and contains same rated, beginning and ending pressure
         * See LIMIT 1 clause in the select
         */
        String sqlSt;
        DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
        sqlSt = "SELECT ddgc.diver_no "
                + "\n,ddgc.dive_no "
                + "\n,ddgc.group_no "
                + "\n,ddgc.cylinder_no "
                + "\n,c.cylinder_type "
                + "\n,ddgc.beginning_pressure "
                + "\n,ddgc.ending_pressure "
                + "\n,ddgc.o2 "
                + "\n,ddgc.n "
                + "\n,ddgc.he "
                + "\n,gc.usage_type "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN cylinder c "
                + "\nON (c.cylinder_no = ddgc.cylinder_no) "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no "
                // NOTE: Might be reserved for future use
                //+ "\nAND gc.usage_type = '" + diverDiveGroupCylArg.getUsageType() + "') "
                + "\n) "
                + "\nWHERE ddgc.diver_no = ? /*diverNo*/ "
                + "\nAND ddgc.dive_no = ? /*diveNo*/ "
                + "\nAND ddgc.group_no = ? /*group_no*/ "
                + "\nORDER BY ddgc.cylinder_no "
                + "\nLIMIT 1";
        try {
            try (Cursor cursor = mDb.rawQuery(sqlSt, new String[]{String.valueOf(diverDiveGroupCylArg.getDiverNo())
                                                       , String.valueOf(diverDiveGroupCylArg.getDiveNo())
                                                       , String.valueOf(diverDiveGroupCylArg.getGroupNo())})) {

                if (cursor.getCount() > 0) {
                    // May have more than one cylinder for doubles
                    // All cylinders need to have the same Beginning and Ending pressures
                    while (cursor.moveToNext()) {
                        diverDiveGroupCyl.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO)));
                        diverDiveGroupCyl.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO)));
                        diverDiveGroupCyl.setGroupNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO)));
                        diverDiveGroupCyl.setCylinderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO)));
                        diverDiveGroupCyl.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                        diverDiveGroupCyl.setBeginningPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE)));
                        diverDiveGroupCyl.setEndingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE)));
                        diverDiveGroupCyl.setO2(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_O2)));
                        diverDiveGroupCyl.setN(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_N)));
                        diverDiveGroupCyl.setHe(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_HE)));
                        diverDiveGroupCyl.setUsageType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                    }
                }
                Log.d(LOG_TAG, "Total DIVER_DIVE_GROUP_CYLINDER rows = " + cursor.getCount());
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "Exception raised with a value of " + e);
        }
        return diverDiveGroupCyl;
    }

    void createDiverDiveGroupCylinder(DiverDiveGroupCyl diverDiveGroupCyl) {
        try {
            ContentValues values = new ContentValues();
            if (diverDiveGroupCyl.getDiverNo() == MyConstants.ZERO_L) {
                Toast.makeText(mContext, "Diver No is at 0", Toast.LENGTH_LONG).show();
            }
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO, diverDiveGroupCyl.getDiverNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO, diverDiveGroupCyl.getDiveNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO, diverDiveGroupCyl.getGroupNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO, diverDiveGroupCyl.getCylinderNo());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE, diverDiveGroupCyl.getBeginningPressure());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE, diverDiveGroupCyl.getEndingPressure());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_O2, diverDiveGroupCyl.getO2());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_N, diverDiveGroupCyl.getN());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_HE, diverDiveGroupCyl.getHe());
            long id = mDb.insert(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER, null, values);
            diverDiveGroupCyl.setDiverNo(id);
            Log.d(LOG_TAG, "Inserted DIVER_NO is " + String.valueOf(diverDiveGroupCyl.getDiverNo())
                    + " and DIVE_NO is" + String.valueOf(diverDiveGroupCyl.getDiveNo())
                    + " and GROUP_NO is " + String.valueOf(diverDiveGroupCyl.getGroupNo())
                    + " and CYLINDER_NO is " + String.valueOf(diverDiveGroupCyl.getCylinderNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateDiverDiveGroupCylinder(DiverDiveGroupCyl diverDiveGroupCyl) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO + "=" + diverDiveGroupCyl.getDiverNo()
                    + " AND " +  AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO + "=" + diverDiveGroupCyl.getDiveNo()
                    + " AND " +  AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO + "=" + diverDiveGroupCyl.getGroupNo()
                    + " AND " +  AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO + "=" + diverDiveGroupCyl.getCylinderNo();
            Log.d(LOG_TAG, "Updated DIVER_NO is " + String.valueOf(diverDiveGroupCyl.getDiverNo())
                    + " and DIVE_NO is " + String.valueOf(diverDiveGroupCyl.getDiveNo())
                    + " and GROUP_NO is " + String.valueOf(diverDiveGroupCyl.getGroupNo())
                    + " and CYLINDER_NO is " + String.valueOf(diverDiveGroupCyl.getCylinderNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE, diverDiveGroupCyl.getBeginningPressure());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE, diverDiveGroupCyl.getEndingPressure());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_O2, diverDiveGroupCyl.getO2());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_N, diverDiveGroupCyl.getN());
            values.put(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_HE, diverDiveGroupCyl.getHe());
            mDb.update(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<DiverDiveGroupCyl> getAllDiverDiveGroupCylinderByGroup(DiverDiveGroupCyl diverDiveGroupCylArg) {

        /**
         * Returns all the cylinders within a group
         * Need to update all cylinders within a twin set
         * with the same rated, beginning and ending pressure
         */
        ArrayList<DiverDiveGroupCyl> diverDiveGroupCyls = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "SELECT ddgc.diver_no "
                + "\n,ddgc.dive_no "
                + "\n,ddgc.group_no "
                + "\n,ddgc.cylinder_no "
                + "\n,c.cylinder_type "
                + "\n,ddgc.beginning_pressure "
                + "\n,ddgc.ending_pressure "
                + "\n,ddgc.o2 "
                + "\n,ddgc.n "
                + "\n,ddgc.he "
                + "\n,gc.usage_type "
                + "\nFROM diver_dive_group_cylinder ddgc "
                + "\nINNER JOIN cylinder c "
                + "\nON (c.cylinder_no = ddgc.cylinder_no) "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = ddgc.group_no "
                + "\nAND gc.cylinder_no = ddgc.cylinder_no "
                + "\nAND gc.usage_type = '" + diverDiveGroupCylArg.getUsageType() + "') "
                + "\nWHERE ddgc.diver_no = ? /*diverNo*/ "
                + "\nAND ddgc.dive_no = ? /*diveNo*/ "
                + "\nAND ddgc.group_no = ? /*group_no*/ "
                + "\nORDER BY ddgc.cylinder_no ";

        try {
            cursor = mDb.rawQuery(sqlSt, new String[]{String.valueOf(diverDiveGroupCylArg.getDiverNo()), String.valueOf(diverDiveGroupCylArg.getDiveNo()), String.valueOf(diverDiveGroupCylArg.getGroupNo())});
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DiverDiveGroupCyl diverDiveGroupCyl = new DiverDiveGroupCyl();
                    diverDiveGroupCyl.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO)));
                    diverDiveGroupCyl.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO)));
                    diverDiveGroupCyl.setGroupNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_GROUP_NO)));
                    diverDiveGroupCyl.setCylinderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_CYLINDER_NO)));
                    diverDiveGroupCyl.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                    diverDiveGroupCyl.setBeginningPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE)));
                    diverDiveGroupCyl.setEndingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE)));
                    diverDiveGroupCyl.setO2(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_O2)));
                    diverDiveGroupCyl.setN(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_N)));
                    diverDiveGroupCyl.setHe(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_HE)));
                    diverDiveGroupCyl.setUsageType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                    diverDiveGroupCyls.add(diverDiveGroupCyl);
                }
            }
            Log.d(LOG_TAG, "Total DIVER_DIVE_GROUP_CYLINDER rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return diverDiveGroupCyls;
    }

    void deleteDiverDiveGroupCylinderByDiverNoDiveNo(long diverNo, long diveNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVER_NO + "=" + diverNo + " AND " + AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_DIVE_NO + "=" + diveNo;
            Log.d(LOG_TAG, "Deleted DIVER_DIVE_GROUP_CYLINDER with DIVER_NO " + String.valueOf(diverNo) + " and DIVE_NO is " + String.valueOf(diveNo));
            mDb.delete(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteDiverDiveGroupCylinderByDiverNoDiveNoGroupNoUsageType(long diverNo, long diveNo, long groupNo, String usageType) {
        try {
            Log.d(LOG_TAG, "Deleted DIVER_DIVE_GROUP_CYLINDER with DIVER_NO " + String.valueOf(diverNo) + " and DIVE_NO is " + String.valueOf(diveNo) + " and GROUP_NO is " + String.valueOf(groupNo));

            String deleteTable = "DELETE FROM diver_dive_group_cylinder " +
                    "\nWHERE diver_no = " + String.valueOf(diverNo) + " " +
                    "\nAND dive_no = " + String.valueOf(diveNo) + " " +
                    "\nAND group_no = " + String.valueOf(groupNo) + " " +
                    "\nAND cylinder_no IN (SELECT cylinder_no " +
                    "\nFROM group_cylinder gc " +
                    "\nWHERE gc.group_no = diver_dive_group_cylinder.group_no " +
                    "\nAND gc.usage_type = '" + usageType + "')";

            mDb.execSQL(deleteTable);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDiverDiveGroupCylinder() {
        try {
            Log.d(LOG_TAG, "Drop DIVER_DIVE_GROUP_CYLINDER");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_DIVE_GROUP_CYLINDER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVER_DIVE_GROUP_CYLINDER_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DIVE_SEGMENT Data Access
    void createDiveSegment(DiveSegment diveSegment) {
        try {
            ContentValues values = new ContentValues();
            // PK is autoincrement
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO, diveSegment.getDiverNo());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO, diveSegment.getDiveNo());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO, diveSegment.getOrderNo());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE, diveSegment.getSegmentType());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH, diveSegment.getDepth());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE, diveSegment.getMinute());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE, diveSegment.getAirConsumptionPressure());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME, diveSegment.getAirConsumptionVolume());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA, diveSegment.getCalcAta());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH, diveSegment.getCalcAverageDepth());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA, diveSegment.getCalcAverageAta());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE, diveSegment.getCalcDescentRate());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE, diveSegment.getCalcAscentRate());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE, diveSegment.getCalcDecreasingPressure());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME, diveSegment.getCalcDecreasingVolume());
            long id = mDb.insert(AirDBHelper.TABLE_DIVE_SEGMENT, null, values);
            // TODO: Do we need if no autoincrement
            diveSegment.setDiveNo(id);
            Log.d(LOG_TAG, "DiveSegment inserted with DIVER_NO is " + String.valueOf(diveSegment.getDiverNo())
                    + " and DIVE_NO is" + String.valueOf(diveSegment.getDiveNo())
                    + " and ORDER_NO is " + String.valueOf(diveSegment.getOrderNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    ArrayList<DiveSegment> getAllDiveSegments(Long diverNo, Long diveNo) {
        ArrayList<DiveSegment> diveSegments = new ArrayList<>();
        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DIVE_SEGMENT, COLUMNS_DIVE_SEGMENT, "DIVER_NO = " + diverNo + " AND DIVE_NO = " + diveNo, null, null, null, null)) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        DiveSegment diveSegment = new DiveSegment();
                        diveSegment.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO)));
                        diveSegment.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO)));
                        diveSegment.setOrderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO)));
                        diveSegment.setSegmentType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE)));
                        diveSegment.setDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH)));
                        diveSegment.setMinute(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE)));
                        diveSegment.setAirConsumptionPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE)));
                        diveSegment.setAirConsumptionVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME)));
                        diveSegment.setCalcAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA)));
                        diveSegment.setCalcAverageDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH)));
                        diveSegment.setCalcAverageAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA)));
                        diveSegment.setCalcDescentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE)));
                        diveSegment.setCalcAscentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE)));
                        diveSegment.setCalcDecreasingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE)));
                        diveSegment.setCalcDecreasingVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME)));
                        diveSegments.add(diveSegment);
                    }
                }
                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return diveSegments;
    }

    ArrayList<DiveSegment> getDiveSegmentsDescent(Long diverNo, Long diveNo) {
        ArrayList<DiveSegment> diveSegments = new ArrayList<>();
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DIVE_SEGMENT, COLUMNS_DIVE_SEGMENT, "DIVER_NO = " + diverNo + " AND DIVE_NO = " + diveNo + " AND SEGMENT_TYPE IN ('BC') ", null, null, null, " order_no ASC")) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DiveSegment diveSegment = new DiveSegment();
                    diveSegment.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO)));
                    diveSegment.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO)));
                    diveSegment.setOrderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO)));
                    diveSegment.setSegmentType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE)));
                    diveSegment.setDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH)));
                    diveSegment.setMinute(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE)));
                    diveSegment.setAirConsumptionPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE)));
                    diveSegment.setAirConsumptionVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME)));
                    diveSegment.setCalcAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA)));
                    diveSegment.setCalcAverageDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH)));
                    diveSegment.setCalcAverageAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA)));
                    diveSegment.setCalcDescentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE)));
                    diveSegment.setCalcAscentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE)));
                    diveSegment.setCalcDecreasingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE)));
                    diveSegment.setCalcDecreasingVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME)));
                    diveSegments.add(diveSegment);
                }
            }
            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return diveSegments;
    }

    ArrayList<DiveSegment> getDiveSegmentsAscent(Long diverNo, Long diveNo) {
        ArrayList<DiveSegment> diveSegments = new ArrayList<>();
        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DIVE_SEGMENT, COLUMNS_DIVE_SEGMENT, "DIVER_NO = " + diverNo + " AND DIVE_NO = " + diveNo + " AND SEGMENT_TYPE IN ('BT','TA','OOA','ADS','DS','ASS','SS','AS') ", null, null, null, " order_no ASC")) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        DiveSegment diveSegment = new DiveSegment();
                        diveSegment.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO)));
                        diveSegment.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO)));
                        diveSegment.setOrderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO)));
                        diveSegment.setSegmentType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE)));
                        diveSegment.setDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH)));
                        diveSegment.setMinute(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE)));
                        diveSegment.setAirConsumptionPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE)));
                        diveSegment.setAirConsumptionVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME)));
                        diveSegment.setCalcAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA)));
                        diveSegment.setCalcAverageDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH)));
                        diveSegment.setCalcAverageAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA)));
                        diveSegment.setCalcDescentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE)));
                        diveSegment.setCalcAscentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE)));
                        diveSegment.setCalcDecreasingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE)));
                        diveSegment.setCalcDecreasingVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME)));
                        diveSegments.add(diveSegment);
                    }
                }
                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return diveSegments;
    }

    ArrayList<DiveSegment> getDiveSegmentsAscentEmergency(Long diverNo, Long diveNo, String shorten) {
        ArrayList<DiveSegment> diveSegments = new ArrayList<>();
        Cursor cursor = null;
        try {
            if (shorten.equals(MyConstants.YES)) {
                cursor = mDb.query(AirDBHelper.TABLE_DIVE_SEGMENT, COLUMNS_DIVE_SEGMENT, "DIVER_NO = " + diverNo + " AND DIVE_NO = " + diveNo + " AND SEGMENT_TYPE IN ('OOA','AS') ", null, null, null, " ORDER_NO DESC");
            } else {
                cursor = mDb.query(AirDBHelper.TABLE_DIVE_SEGMENT, COLUMNS_DIVE_SEGMENT, "DIVER_NO = " + diverNo + " AND DIVE_NO = " + diveNo + " AND SEGMENT_TYPE IN ('OOA','ADS','DS','ASS','SS','AS') ", null, null, null, " ORDER_NO DESC");
            }

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DiveSegment diveSegment = new DiveSegment();
                    diveSegment.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO)));
                    diveSegment.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO)));
                    diveSegment.setOrderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO)));
                    diveSegment.setSegmentType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE)));
                    diveSegment.setDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH)));
                    diveSegment.setMinute(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE)));
                    diveSegment.setAirConsumptionPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE)));
                    diveSegment.setAirConsumptionVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME)));
                    diveSegment.setCalcAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA)));
                    diveSegment.setCalcAverageDepth(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH)));
                    diveSegment.setCalcAverageAta(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA)));
                    diveSegment.setCalcDescentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE)));
                    diveSegment.setCalcAscentRate(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE)));
                    diveSegment.setCalcDecreasingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE)));
                    diveSegment.setCalcDecreasingVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME)));
                    diveSegments.add(diveSegment);
                }
            }
            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return diveSegments;
    }

    void getDiveSegmentDetail(Long diverNo, Long diveNo, DiveSegmentDetail diveSegmentDetail) {
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "WITH max_depth AS "
                + "\n(SELECT dive_no "
                + "\n,MAX(dp.depth) AS MAX_DEPTH "
                + "\nFROM dive_plan dp "
                + "\nWHERE dp.dive_no = ?) /*diveNo*/ "
                + "\n,starting_pressure AS "
                + "\n(SELECT ds.dive_no "
                + "\n,ds.calc_decreasing_pressure "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'STA') "
                + "\n,turnaround_pressure AS "
                + "\n(SELECT ds.dive_no "
                + "\n,ds.calc_decreasing_pressure "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'TA') "
                + "\n,ending_pressure AS "
                + "\n(SELECT ds.dive_no "
                + "\n,ds.calc_decreasing_pressure "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'STO') "
                + "\n,running_total AS "
                + "\n(SELECT ds.dive_no "
                + "\n,SUM(ds.minute) AS running_total_time "
                + "\n,SUM(ds.air_consumption_volume) AS running_total_volume "
                + "\n,SUM(ds.air_consumption_pressure) AS running_total_pressure "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nGROUP BY ds.dive_no) "
                + "\n,running_descent AS "
                + "\n(SELECT ds.dive_no "
                + "\n,SUM(ds.minute) AS running_descent_time "
                + "\n,SUM(ds.air_consumption_volume) AS running_descent_volume "
                + "\n,SUM(ds.air_consumption_pressure) AS running_descent_pressure "
                + "\nFROM dive_segment ds "
                + "\nINNER JOIN segment_type st "
                + "\nON (st.segment_type = ds.segment_type) "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND st.direction = 'D' "
                + "\nGROUP BY ds.dive_no) "
                + "\n,running_ascent AS "
                + "\n(SELECT ds.dive_no "
                + "\n,SUM(ds.minute)  AS running_ascent_time "
                + "\n,SUM(ds.air_consumption_volume)  AS running_ascent_volume "
                + "\n,SUM(ds.air_consumption_pressure)  AS running_ascent_pressure "
                + "\nFROM dive_segment ds "
                + "\nINNER JOIN segment_type st "
                + "\nON (st.segment_type = ds.segment_type) "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND st.direction = 'A' "
                + "\nGROUP BY ds.dive_no) "
                + "\nSELECT md.max_depth AS MAX_DEPTH "
                + "\n,IFNULL(sp.calc_decreasing_pressure, 0.0) AS BEGINNING_PRESSURE "
                + "\n,IFNULL(tap.calc_decreasing_pressure, 0.0) AS TURNAROUND_PRESSURE "
                + "\n,IFNULL(ep.calc_decreasing_pressure, 0.0) AS ENDING_PRESSURE "
                + "\n,IFNULL(rt.running_total_time, 0.0) AS RUNNING_TOTAL_TIME "
                + "\n,IFNULL(rt.running_total_volume, 0.0) AS RUNNING_TOTAL_VOLUME "
                + "\n,IFNULL(rt.running_total_pressure, 0.0) AS RUNNING_TOTAL_PRESSURE "
                + "\n,IFNULL(rd.running_descent_time, 0.0) AS RUNNING_DESCENT_TIME "
                + "\n,IFNULL(rd.running_descent_volume, 0.0) AS RUNNING_DESCENT_VOLUME "
                + "\n,IFNULL(rd.running_descent_pressure, 0.0) AS RUNNING_DESCENT_PRESSURE "
                + "\n,IFNULL(ra.running_ascent_time, 0.0) AS RUNNING_ASCENT_TIME "
                + "\n,IFNULL(ra.running_ascent_volume, 0.0) AS RUNNING_ASCENT_VOLUME "
                + "\n,IFNULL(ra.running_ascent_pressure, 0.0) AS RUNNING_ASCENT_PRESSURE "
                + "\nFROM dive d "
                + "\nINNER JOIN diver_dive dd "
                + "\nON (dd.dive_no = d.dive_no "
                + "\nAND dd.diver_no = ? /*diverNo*/) "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.dive_no = d.dive_no) "
                + "\nLEFT JOIN starting_pressure sp "
                + "\nON (sp.dive_no = d.dive_no) "
                + "\nLEFT JOIN turnaround_pressure tap "
                + "\nON (tap.dive_no = d.dive_no) "
                + "\nLEFT JOIN ending_pressure ep "
                + "\nON (ep.dive_no = d.dive_no) "
                + "\nLEFT JOIN running_total rt "
                + "\nON (rt.dive_no = d.dive_no) "
                + "\nLEFT JOIN running_descent rd "
                + "\nON (rd.dive_no = d.dive_no) "
                + "\nLEFT JOIN running_ascent ra "
                + "\nON (ra.dive_no = d.dive_no) "
                + "\nWHERE d.dive_no = ? /*diveNo*/";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] { String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
                                                        ,String.valueOf(diverNo)
                                                        ,String.valueOf(diveNo)
            });

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    diveSegmentDetail.setMaxDepth(cursor.getDouble(cursor.getColumnIndex("MAX_DEPTH")));
                    diveSegmentDetail.setBeginningPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_BEGINNING_PRESSURE)));
                    diveSegmentDetail.setTurnaroundPressure(cursor.getDouble(cursor.getColumnIndex("TURNAROUND_PRESSURE")));
                    diveSegmentDetail.setEndingPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_GROUP_CYLINDER_ENDING_PRESSURE)));
                    diveSegmentDetail.setRunningTotalTime(cursor.getDouble(cursor.getColumnIndex("RUNNING_TOTAL_TIME")));
                    diveSegmentDetail.setRunningTotalVolume(cursor.getDouble(cursor.getColumnIndex("RUNNING_TOTAL_VOLUME")));
                    diveSegmentDetail.setRunningTotalPressure(cursor.getDouble(cursor.getColumnIndex("RUNNING_TOTAL_PRESSURE")));
                    diveSegmentDetail.setRunningDescentTime(cursor.getDouble(cursor.getColumnIndex("RUNNING_DESCENT_TIME")));
                    diveSegmentDetail.setRunningDescentVolume(cursor.getDouble(cursor.getColumnIndex("RUNNING_DESCENT_VOLUME")));
                    diveSegmentDetail.setRunningDescentPressure(cursor.getDouble(cursor.getColumnIndex("RUNNING_DESCENT_PRESSURE")));
                    diveSegmentDetail.setRunningAscentTime(cursor.getDouble(cursor.getColumnIndex("RUNNING_ASCENT_TIME")));
                    diveSegmentDetail.setRunningAscentVolume(cursor.getDouble(cursor.getColumnIndex("RUNNING_ASCENT_VOLUME")));
                    diveSegmentDetail.setRunningAscentPressure(cursor.getDouble(cursor.getColumnIndex("RUNNING_ASCENT_PRESSURE")));
                }
            }
            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // AKA Pony
    void insertDiveSegmentEmergencyPressure(
              Long diverNo
            , Long diveNo
            , Double salinity
            , int ascentRateToDs
            , int ascentRateToSs
            , int ascentRateToSu
            , int deepStopDive
            , int deepStopPercent
            , int deepStopTime
            , int safetyStopDive
            , Double safetyStopDepth
            , int safetyStopTime
            , int turnaroundTime
            , int ooaTurnaroundTime
            , String subtractDeepStopTime
            , String needDeepDeco
            , String needDeco
            , String shorten) {
        Cursor cursor = null;
        String sqlSt;

        // Get the Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        // Inserts all of the segments except: BT - Bottom Time
        //                                     DE - Descent
        sqlSt = "/*01- Inserts all of the segments EXCEPT: BT & DE*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH dps AS "
                + "\n(SELECT dp.dive_no AS DIVE_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\nFROM dive_plan dp "
                + "\nWHERE dp.dive_no = %1$s /*diveNo*/ "
                + "\nORDER BY order_no ASC ) "
                + "\n,max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %2$s) /*diveNo*/ "
                // Generate the segments except Bottom Time (BT) and Descent (DE)
                + "\nSELECT %3$s /*diverNo*/ AS DIVER_NO "
                + "\n,%4$s /*diveNo*/ AS DIVE_NO "
                + "\n,st.order_no AS ORDER_NO "
                + "\n,st.segment_type AS SEGMENT_TYPE "
                + "\n,CASE WHEN st.segment_type IN ('TA','OOA') THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'ADS' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'DS' THEN maxd.max_depth * %5$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND maxd.max_depth >= %6$s /*deepStopDive*/ THEN maxd.max_depth * %7$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND maxd.max_depth < %8$s /*deepStopDive*/ THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'SS' THEN %9$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %10$s /*safetyStopDive*/ AND 'NO' = '" + shorten + "' THEN %11$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %12$s /*safetyStopDive*/ AND 'YES' = '" + shorten + "' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %13$s /*safetyStopDive*/ THEN maxd.max_depth "
                + "\nELSE IFNULL(dps.depth,0) "
                + "\nEND AS DEPTH "
                + "\n,CASE WHEN st.segment_type = 'DS' THEN %14$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %15$s /*safetyStopDive*/ AND 'YES' = '" + subtractDeepStopTime + "' THEN %16$s /*safetyStopTime*/ - %17$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %18$s /*safetyStopDive*/ AND 'NO' = '" + subtractDeepStopTime + "' THEN %19$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %20$s /*safetyStopDive*/THEN %21$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'OOA' THEN %22$s /*ooaTurnaroundTime*/ "
                + "\nWHEN st.segment_type = 'TA' THEN %23$s /*turnaroundTime*/ "
                + "\nELSE IFNULL(dps.minute,0) "
                + "\nEND AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                // Imperial
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type IN ('TA','OOA') THEN ROUND((maxd.max_depth / %24$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %25$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND((maxd.max_depth / 2 / %26$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND maxd.max_depth >= %27$s /*deepStopDive*/ AND %28$s /*deepStopDive*/ > 0 THEN ROUND((maxd.max_depth * %29$s /*deepStopPercent*/ / 100 / %30$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %31$s /*deepStopDive*/ = 0 THEN ROUND(((maxd.max_depth - %32$s /*safetyStopDepth+*/ ) / %33$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%34$s /*safetyStopDepth*/ / %35$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %36$s /*safetyStopDive*/ THEN ROUND((%37$s /*safetyStopDepth*/ / %38$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %39$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %40$s /*salinity*/ ) + 1,3) "
                + "\nELSE ROUND((dps.depth / %41$s /*salinity*/ ) + 1,3) "
                + "\nEND "
                // Metric
                + "\nELSE CASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type IN ('TA','OOA') THEN ROUND((maxd.max_depth / %42$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %43$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND(((maxd.max_depth / 2) / %44$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND maxd.max_depth >= %45$s /*deepStopDive*/ AND %46$s /*deepStopDive*/ > 0 THEN ROUND(((maxd.max_depth * %47$s /*deepStopPercent*/ / 100) / %48$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %49$s /*deepStopDive*/ = 0 THEN ROUND(((maxd.max_depth - %50$s /*safetyStopDepth+*/ ) / %51$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%52$s /*safetyStopDepth*/ / %53$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %54$s /*safetyStopDive*/ THEN ROUND((%55$s /*safetyStopDepth*/ / %56$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %57$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %58$s /*salinity*/) + 1,3) "
                + "\nELSE ROUND((dps.depth / %59$s /*salinity*/) + 1,3) "
                + "\nEND "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,CASE WHEN st.segment_type in ('ASS','DS') THEN %60$s /*ascentRateToSs*/ "
                + "\nWHEN st.segment_type = 'ADS' THEN %61$s /*ascentRateToDs*/ "
                + "\nWHEN st.segment_type = 'SS' THEN %62$s /*ascentRateToSu*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth > %63$s /*safetyStopDepth*/ THEN %64$s /*ascentRateToSs*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth <= %65$s /*safetyStopDepth*/ THEN %66$s /*ascentRateToSu*/ "
                + "\nELSE 0 "
                + "\nEND AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM segment_type st "
                + "\nLEFT JOIN dps "
                + "\nON (dps.segment_type = st.segment_type "
                + "\nAND dps.dive_no = %67$s /*diveNo*/) "
                + "\nINNER JOIN max_depth maxd "
                + "\nON (maxd.rowid = 1) "
                + "\nWHERE (('NO' = '" + shorten + "' " // shorten
                + "\nAND ((st.segment_type IN ('TA','OOA','AS','STO') AND maxd.max_depth < %68$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','OOA','AS','STO') AND maxd.max_depth >= %69$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','OOA','AS','STO') AND maxd.max_depth < %70$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','OOA','ASS','SS','AS','STO') AND maxd.max_depth >= %71$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('ADS','DS') AND maxd.max_depth >= %72$s /*deepStopDive*/ AND %73$s /*deepStopDive*/ > 0) "
                // TODO: Do we need ADS and DS during DECO and DEEP DECO?
                + "\nOR (st.segment_type IN ('TA','OOA','ASS','SS','AS','STO','ADD','DD','AD') AND 'YES' = '" + needDeepDeco + "') "
                + "\nOR (st.segment_type IN ('TA','OOA','ASS','SS','AS','STO','AD') AND 'YES' = '" + needDeco + "'))) "
                // In the case of a shorten dive, no Ascend to Deep Stop (ADS), no Deep Stop (DS), no Ascend to Safety Stop (ASS) and no Safety Stop are present
                // But we keep them all for a dive requiring Deep Deco
                + "\nOR ('YES' = '" + shorten + "' " // shorten
                + "\nAND ((st.segment_type IN ('TA','OOA','AS','STO') AND maxd.max_depth < %74$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','OOA','AS','STO') AND maxd.max_depth >= %75$s /*deepStopDive*/) "
                // TODO: Do we need ADS and DS during DECO and DEEP DECO?
                + "\nOR (st.segment_type IN ('TA','OOA','ASS','SS','AS','STO','ADD','DD','AD') AND 'YES' = '" + needDeepDeco + "') "
                + "\nOR (st.segment_type IN ('TA','OOA','ASS','SS','AS','STO','AD') AND 'YES' = '" + needDeco + "')))) "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(diveNo) // 1
                ,String.valueOf(diveNo)
                ,String.valueOf(diverNo)
                ,String.valueOf(diveNo)
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(safetyStopDive) // 10
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopTime)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(deepStopTime)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(safetyStopDive) // 20
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(ooaTurnaroundTime) // 22
                ,String.valueOf(turnaroundTime) // 23
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(salinity) // 30
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(salinity) // 40
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth) // 50
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(ascentRateToSs) // 60
                ,String.valueOf(ascentRateToDs)
                ,String.valueOf(ascentRateToSu)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(ascentRateToSs)
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(ascentRateToSu)
                ,String.valueOf(diveNo)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDive) // 70
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 75
        );

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        long affectedRowCount;
        try {
            try (Cursor cursor2 = mDb.rawQuery("SELECT changes() AS affected_row_count", null)) {
                if (cursor2 != null && cursor2.getCount() > 0 && cursor2.moveToFirst()) {
                    affectedRowCount = cursor2.getLong(cursor2.getColumnIndex("affected_row_count"));
                    Log.d("LOG", "affectedRowCount = " + affectedRowCount);
                } else {
                    affectedRowCount = -1;
                    Log.d("LOG", "affectedRowCount = " + affectedRowCount);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // For a shorten dive, if the maximum depth is > then the safetyStopDive (20 ft)
        // inserts an Ascend to Surface (AS) segment from the safetyStopDepth (15 ft) at the ascentRateToSu (10 ft/min)
        sqlSt = "/*02- Inserts the Ascent (AS)*/"
                + "INSERT INTO dive_segment "
                + "\nWITH dps AS "
                + "\n(SELECT dp.dive_no AS DIVE_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY order_no ASC )"
                + "\n,max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                // Generate the segments except Bottom Time (BT) and Descent (DE)
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,? AS DIVE_NO /*diveNo*/ "
                + "\n,305 AS ORDER_NO "
                + "\n,st.segment_type AS SEGMENT_TYPE "
                + "\n,? AS DEPTH /*safetyStopDepth*/ "
                + "\n,0 AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((? / ?) + 1,3)  /*safetyStopDepth salinity*/ "
                + "\nELSE ROUND((? / ?) + 1,3) /*safetyStopDepth salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,? CALC_ASCENT_RATE /*ascentRateToSu*/ "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM segment_type st "
                + "\nLEFT JOIN dps "
                + "\nON (dps.segment_type = st.segment_type "
                + "\nAND dps.dive_no = ? /*diveNo*/) "
                + "\nINNER JOIN max_depth maxd "
                + "\nON (maxd.rowid = 1) "
                + "\nWHERE 'YES' = '" + shorten + "' " // shorten
                + "\nAND st.segment_type = 'AS' "
                + "\nAND maxd.max_depth > ? /*safetyStopDive*/"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                     String.valueOf(diveNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(safetyStopDepth)
                    ,String.valueOf(safetyStopDepth), String.valueOf(salinity)
                    ,String.valueOf(safetyStopDepth), String.valueOf(salinity)
                    ,String.valueOf(ascentRateToSu)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(safetyStopDive)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void insertDiveSegmentDrift(
            Long diverNo
            , Long diveNo
            , Double salinity
            , Double bubbleCheckDepth
            , int bubbleCheckTime
            , int descentRate
            , int ascentRateToDs
            , int ascentRateToSs
            , int ascentRateToSu
            , int deepStopDive
            , int deepStopPercent
            , int deepStopTime
            , int safetyStopDive
            , Double safetyStopDepth
            , int safetyStopTime
            , String subtractDeepStopTime
            , String needDeepDeco
            , String needDeco) {
        Cursor cursor = null;
        String sqlSt;

        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        // Inserts all of the segments EXCEPT: BT - Bottom Time
        //                                     BC - Bubble Check
        //                                     DE - Descent
        sqlSt = "/*01- Inserts all of the segments EXCEPT: BT, BC & DE*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the minimum depth for that Dive Plan
                // The shallowest segment
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %1$s /*diveNo*/) "
                + ",\nmax_depth AS "
                // Find the maximum depth for that Dive Plan
                // The deepest segment
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dive_no = %2$s /*diveNo*/) "
                + "\n,dive_plan_count AS "
                + "\n(SELECT CAST(COUNT(*) AS INTEGER) AS SEGMENT_COUNT "
                + "\n,1 AS ROWID "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dive_no = %3$s /*diveNo*/) "
                // Generate the segments EXCEPT Bottom Time (BT), Descent (DE) and Bubble Check (BC)
                + "\nSELECT %4$s /*diverNo*/ AS DIVER_NO "
                + "\n,%5$s /*diveNo*/ AS DIVE_NO "
                + "\n,st.order_no AS ORDER_NO "
                + "\n,st.segment_type AS SEGMENT_TYPE "
                + "\n,CASE WHEN st.segment_type = 'ADS' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'DS' THEN maxd.max_depth * %6$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %7$s /*deepStopDive*/ AND %8$s /*deepStopDive*/ > 0 THEN mind.min_depth * %9$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %10$s /*deepStopDive*/ AND %11$s /*deepStopDive*/ > 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'ASS' AND %12$s /*deepStopDive*/ = 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'SS' THEN %13$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %14$s /*safetyStopDive*/ THEN %15$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %16$s /*safetyStopDive*/ THEN maxd.max_depth "
                + "\nELSE 0 "
                + "\nEND AS DEPTH "
                + "\n,CASE WHEN st.segment_type = 'DS' THEN %17$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %18$s /*safetyStopDive*/ AND 'YES' = '" + subtractDeepStopTime + "' THEN %19$s /*safetyStopTime*/ - %20$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %21$s /*safetyStopDive*/ AND 'NO' = '" + subtractDeepStopTime + "' THEN %22$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %23$s /*safetyStopDive*/ THEN %24$s /*safetyStopTime*/ "
                + "\nELSE 0 "
                + "\nEND AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                // Imperial
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %25$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND((maxd.max_depth / 2 / %26$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %27$s /*deepStopDive*/ AND %28$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth * %29$s /*deepStopPercent*/ / 100 / %30$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %31$s /*deepStopDive*/ AND %32$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %33$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %34$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %35$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%36$s /*safetyStopDepth*/ / %37$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %38$s /*safetyStopDive*/ THEN ROUND((%39$s /*safetyStopDepth*/ / %40$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %41$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %42$s /*salinity*/ ) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nELSE "
                // Metric
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %43$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND(((maxd.max_depth / 2) / %44$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %45$s /*deepStopDive*/ AND %46$s /*deepStopDive*/ > 0 THEN ROUND(((mind.min_depth * %47$s /*deepStopPercent*/ / 100) / %48$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %49$s /*deepStopDive*/ AND %50$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %51$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %52$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %53$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%54$s /*safetyStopDepth*/ / %55$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %56$s /*safetyStopDive*/ THEN ROUND((%57$s /*safetyStopDepth*/ / %58$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %59$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %60$s /*salinity*/) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,CASE WHEN st.segment_type in ('ASS') THEN %61$s /*ascentRateToSs*/ "
                + "\nWHEN st.segment_type = 'ADS' THEN %62$s /*ascentRateToDs*/ "
                + "\nWHEN st.segment_type IN ('AS') THEN %63$s /*ascentRateToSu*/ "
                + "\nELSE 0 "
                + "\nEND AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM segment_type st "
                + "\nINNER JOIN min_depth mind "
                + "\nON (mind.rowid = 1) "
                + "\nINNER JOIN max_depth maxd "
                + "\nON (maxd.rowid = 1) "
                + "\nINNER JOIN dive_plan_count dpc "
                + "\nON (dpc.rowid = 1) "
                + "\nWHERE ((st.segment_type IN ('STA','AS','STO') AND maxd.max_depth < %64$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('STA','AS','STO') AND maxd.max_depth >= %65$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('AS','STO') AND maxd.max_depth < %66$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('ASS','SS','AS','STO') AND maxd.max_depth >= %67$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('ADS','DS') AND maxd.max_depth >= %68$s /*deepStopDive*/ AND 0 <> %69$s /*deepStopDive*/ AND dpc.segment_count = 1) "
                // TODO: Do we need ADS and DS during DECO and DEEP DECO?
                + "\nOR (st.segment_type IN ('STA','ASS','SS','AS','STO','ADD','DD','AD') AND 'YES' = '" + needDeepDeco + "') "
                + "\nOR (st.segment_type IN ('STA','ASS','SS','AS','STO','AD') AND 'YES' = '" + needDeco + "')) "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(diveNo) // 1
                ,String.valueOf(diveNo)
                ,String.valueOf(diveNo) // 3
                ,String.valueOf(diverNo)
                ,String.valueOf(diveNo) // 5
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(deepStopDive) // 7
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 9
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 11
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth) // 13
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 15
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopTime) // 17
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopTime) // 19
                ,String.valueOf(deepStopTime)
                ,String.valueOf(safetyStopDive) // 21
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(safetyStopDive) // 23
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(salinity) // 25
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 27
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 29
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 31
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 33
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 35
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 37
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 39
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 41
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 43
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 45
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 47
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 49
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 51
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 53
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 55
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 57
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 59
                ,String.valueOf(salinity)
                ,String.valueOf(ascentRateToSs) // 61
                ,String.valueOf(ascentRateToDs)
                ,String.valueOf(ascentRateToSu) // 63
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 65
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDive) // 67
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 69
        );

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts all the Descents (DE) for that Dive Plan
        // Inserts one Descent (DE) to the maximum depth
        sqlSt = "/*02- Inserts all the Descents (DE) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) - 1 AS ORDER_NO "
                + "\n,'DE' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,0 AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE  dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(descentRate)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts a Descent (DE) for the Bubble Check
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*03- Inserts a Descent (DE) for the Bubble Check*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,1 AS ORDER_NO "
                    + "\n,'DE' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,0 AS MINUTE "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                        String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(descentRate)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts all of the Bottom Time (BT) for that Dive Plan
        // Only insert the deepest Bottom Time (BT)
        sqlSt = "/*04- Inserts all of the Bottom Time (BT) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,(SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 DESC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts the Bubble Check (BC) first a position 1
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*05- Inserts the Bubble Check (BC) first a position 1*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diverNo*/ "
                    + "\n,2 AS ORDER_NO "
                    + "\n,'BC' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,? AS MINUTE /*bubbleCheckTime*/ "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,0 AS CALC_DESCENT_RATE "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                        String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckTime)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts the Ascent (AS)
        // All the segments shallower than the deepest segment
        sqlSt = "/*06- Inserts the last Bottom Time (BT) after the Turnaround (TA)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no AS ORDER_NO "
                + "\n,'AS' AS SEGMENT_TYPE "
                // Depth has to be the same depth as the previous Bottom Time (BT)
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,? AS CALC_ASCENT_RATE /*ascentRateToSs*/ "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN min_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth > md.min_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(ascentRateToSs)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Bottom Time (BT) going up
        // All the segments shallower than the deepest segment
        sqlSt = "/*07- Inserts the Ascent (AS)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no - 5 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth < md.max_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Bottom Time (BT) going up
        // All the segments shallower than the deepest segment
        sqlSt = "/*08- Inserts the Bottom Time (BT) going up*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no - 5 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth < md.max_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void insertDiveSegmentRockbottom(
              Long diverNo
            , Long diveNo
            , Double salinity
            , Double bubbleCheckDepth
            , int bubbleCheckTime
            , int descentRate
            , int turnaroundTime
            , int ascentRateToDs
            , int ascentRateToSs
            , int ascentRateToSu
            , int deepStopDive
            , int deepStopPercent
            , int deepStopTime
            , int safetyStopDive
            , Double safetyStopDepth
            , int safetyStopTime
            , int ooaTurnaroundTime
            , String subtractDeepStopTime
            , String needDeepDeco
            , String needDeco) {
        Cursor cursor = null;
        String sqlSt;

        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        // Inserts all of the segments EXCEPT: BT - Bottom Time
        //                                     BC - Bubble Check
        //                                     DE - Descent
        sqlSt = "/*01- Inserts all of the segments EXCEPT: BT, BC & DE*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the minimum depth for that Dive Plan
                // The shallowest segment
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %1$s /*diveNo*/) "
                + "\n,max_depth AS "
                // Find the maximum depth for that Dive Plan
                // The deepest segment
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE  dive_no = %2$s /*diveNo*/) "
                + "\n,dive_plan_count AS "
                + "\n(SELECT CAST(COUNT(*) AS INTEGER) AS SEGMENT_COUNT "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %3$s /*diveNo*/) "
                // Generate the segments EXCEPT Bottom Time (BT), Descent (DE) and Bubble Check (BC)
                + "\nSELECT %4$s /*diverNo*/ AS DIVER_NO "
                + "\n,%5$s /*diveNo*/ AS DIVE_NO "
                + "\n,st.order_no AS ORDER_NO "
                + "\n,st.segment_type AS SEGMENT_TYPE "
                + "\n,CASE WHEN st.segment_type IN ('TA','OOA') THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'DS' THEN maxd.max_depth * %6$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %7$s /*deepStopDive*/ AND %8$s /*deepStopDive*/ > 0 THEN mind.min_depth * %9$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %10$s /*deepStopDive*/ AND %11$s /*deepStopDive*/ > 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'ASS' AND %12$s /*deepStopDive*/ = 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'SS' THEN %13$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %14$s /*safetyStopDive*/ THEN %15$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %16$s /*safetyStopDive*/ THEN maxd.max_depth "
                + "\nELSE 0 "
                + "\nEND AS DEPTH "
                + "\n,CASE WHEN st.segment_type = 'DS' THEN %17$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %18$s /*safetyStopDive*/ AND 'YES' = '" + subtractDeepStopTime + "' THEN %19$s /*safetyStopTime*/ - %20$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %21$s /*safetyStopDive*/ AND 'NO' = '" + subtractDeepStopTime + "' THEN %22$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %23$s /*safetyStopDive*/ THEN %24$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'OOA' THEN %25$s /*ooaTurnaroundTime*/ "
                + "\nWHEN st.segment_type = 'TA' THEN %26$s /*turnaroundTime*/ "
                + "\nELSE 0 "
                + "\nEND AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                // Imperial
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type IN ('TA','OOA') THEN ROUND((maxd.max_depth / %27$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %28$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND((maxd.max_depth / 2 / %29$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %30$s /*deepStopDive*/ AND %31$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth * %32$s /*deepStopPercent*/ / 100 / %33$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %34$s /*deepStopDive*/ AND %35$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %36$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %37$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %38$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%39$s /*safetyStopDepth*/ / %40$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %41$s /*safetyStopDive*/ THEN ROUND((%42$s /*safetyStopDepth*/ / %43$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %44$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %45$s /*salinity*/ ) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nELSE "
                // Metric
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type IN ('TA','OOA') THEN ROUND((maxd.max_depth / %46$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %47$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND(((maxd.max_depth / 2) / %48$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %49$s /*deepStopDive*/ AND %50$s /*deepStopDive*/ > 0 THEN ROUND(((mind.min_depth * %51$s /*deepStopPercent*/ / 100) / %52$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %52$s /*deepStopDive*/ AND %54$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %55$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %56$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %57$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%58$s /*safetyStopDepth*/ / %59$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %60$s /*safetyStopDive*/ THEN ROUND((%61$s /*safetyStopDepth*/ / %62$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %63$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %64$s /*salinity*/) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,CASE WHEN st.segment_type in ('ASS') THEN %65$s /*ascentRateToSs*/ "
                + "\nWHEN st.segment_type = 'ADS' THEN %66$s /*ascentRateToDs*/ "
                + "\nWHEN st.segment_type IN ('AS') THEN %67$s /*ascentRateToSu*/ "
                + "\nELSE 0 "
                + "\nEND AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM segment_type st "
                + "\nINNER JOIN min_depth mind "
                + "\nON (mind.rowid = 1) "
                + "\nINNER JOIN max_depth maxd "
                + "\nON (maxd.rowid = 1) "
                + "\nINNER JOIN dive_plan_count dpc "
                + "\nON (dpc.rowid = 1) "
                + "\nWHERE ((st.segment_type IN ('STA','TA','OOA','AS','STO') AND maxd.max_depth < %68$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('STA','TA','OOA','AS','STO') AND maxd.max_depth >= %69$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','OOA','AS','STO') AND maxd.max_depth < %70$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','OOA','ASS','SS','AS','STO') AND maxd.max_depth >= %71$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('ADS','DS') AND maxd.max_depth >= %72$s /*deepStopDive*/ AND 0 <> %73$s /*deepStopDive*/ AND dpc.segment_count = 1) "
                // TODO: Do we need ADS and DS during DECO and DEEP DECO?
                + "\nOR (st.segment_type IN ('STA','TA','OOA','ASS','SS','AS','STO','ADD','DD','AD') AND 'YES' = '" + needDeepDeco + "') "
                + "\nOR (st.segment_type IN ('STA','TA','OOA','ASS','SS','AS','STO','AD') AND 'YES' = '" + needDeco + "')) "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(diveNo) // 1
                ,String.valueOf(diveNo)
                ,String.valueOf(diveNo) // 3
                ,String.valueOf(diverNo)
                ,String.valueOf(diveNo) // 5
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(deepStopDive) // 7
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 9
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 11
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth) // 13
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 15
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopTime) // 17
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopTime) // 19
                ,String.valueOf(deepStopTime)
                ,String.valueOf(safetyStopDive) // 21
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(safetyStopDive) // 23
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(ooaTurnaroundTime) // 25
                ,String.valueOf(turnaroundTime)
                ,String.valueOf(salinity) // 27
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 29
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 31
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(salinity) // 33
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 35
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 37
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDepth) // 39
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 41
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 43
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(salinity) // 45
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 47
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 49
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 51
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 53
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 55
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 57
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 59
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 61
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 63
                ,String.valueOf(salinity)
                ,String.valueOf(ascentRateToSs) // 65
                ,String.valueOf(ascentRateToDs)
                ,String.valueOf(ascentRateToSu) // 67
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 69
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDive) // 71
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 73
        );

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts all the Descents (DE) for that Dive Plan
        // Inserts one Descent (DE) to the maximum depth
        sqlSt = "/*02- Inserts all the Descents (DE) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) - 1 AS ORDER_NO "
                + "\n,'DE' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,0 AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE  dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(descentRate)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts a Descent (DE) for the Bubble Check
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*03- Inserts a Descent (DE) for the Bubble Check*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,1 AS ORDER_NO "
                    + "\n,'DE' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,0 AS MINUTE "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                        String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(descentRate)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts all of the Bottom Time (BT) for that Dive Plan
        // Only insert the deepest Bottom Time (BT)
        sqlSt = "/*04- Inserts all of the Bottom Time (BT) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,(SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 DESC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts the Bubble Check (BC) first a position 1
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*05- Inserts the Bubble Check (BC) first a position 1*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diverNo*/ "
                    + "\n,2 AS ORDER_NO "
                    + "\n,'BC' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,? AS MINUTE /*bubbleCheckTime*/ "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,0 AS CALC_DESCENT_RATE "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                        String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckTime)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts the last Bottom Time (BT) after the Turnaround (TA)
        sqlSt = "/*06- Inserts the last Bottom Time (BT) after the Turnaround (TA)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH ta AS "
                + "\n(SELECT ds.dive_no AS DIVE_NO "
                + "\n,ds.order_no + 1 AS NEW_ORDER_NO "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.segment_type = 'TA' "
                + "\nAND ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ?) /*diveNo*/ "
                + "\n,max_bt AS "
                // Find the maximum BT for that Dive Plan
                + "\n(SELECT CAST(MAX(ds.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\n,ds.dive_no "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT' ) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ta.new_order_no AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,ds.depth AS DEPTH "
                + "\n,ds.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((ds.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((ds.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_segment ds "
                + "\nINNER JOIN max_bt mbt "
                + "\nON (mbt.dive_no = ds.dive_no "
                + "\nAND mbt.max_depth = ds.depth "
                + "\nAND ds.segment_type = 'BT') "
                + "\nINNER JOIN ta "
                + "\nON (ta.dive_no = ds.dive_no) "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Ascent (AS)
        // All the segments shallower than the deepest segment
        sqlSt = "/*07- Inserts the Ascent (AS)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no AS ORDER_NO "
                + "\n,'AS' AS SEGMENT_TYPE "
                // Depth has to be the same depth as the previous Bottom Time (BT)
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,? AS CALC_ASCENT_RATE /*ascentRateToSs*/ "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN min_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth > md.min_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(ascentRateToSs)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Bottom Time (BT) going up
        // All the segments shallower than the deepest segment
        sqlSt = "/*08- Inserts the Bottom Time (BT) going up*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no - 5 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth < md.max_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void insertDiveSegmentRuleOfThird(
              Long diverNo
            , Long diveNo
            , Double salinity
            , Double bubbleCheckDepth
            , int bubbleCheckTime
            , int descentRate
            , int turnaroundTime
            , int ascentRateToDs
            , int ascentRateToSs
            , int ascentRateToSu
            , int deepStopDive
            , int deepStopPercent
            , int deepStopTime
            , int safetyStopDive
            , Double safetyStopDepth
            , int safetyStopTime
            , String subtractDeepStopTime
            , String needDeepDeco
            , String needDeco) {
        Cursor cursor = null;
        String sqlSt;

        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        // Inserts all of the segments EXCEPT: BT - Bottom Time
        //                                     BC - Bubble Check
        //                                     DE - Descent
        sqlSt = "/*01- Inserts all of the segments EXCEPT: BT, BC & DE*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the minimum depth for that Dive Plan
                // The shallowest segment
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %1$s /*diveNo*/) "
                + "\n,max_depth AS "
                // Find the maximum depth for that Dive Plan
                // The deepest segment
                + "\n(SELECT CAST(MAX(dp.depth) AS INTEGER) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dive_no = %2$s /*diveNo*/) "
                + "\n,dive_plan_count AS "
                + "\n(SELECT CAST(COUNT(*) AS INTEGER) AS SEGMENT_COUNT "
                + "\n,1 AS ROWID "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dive_no = %3$s /*diveNo*/) "
                // Generate the segments EXCEPT Bottom Time (BT), Descent (DE) and Bubble Check (BC)
                + "\nSELECT %4$s /*diverNo*/ AS DIVER_NO "
                + "\n,%5$s /*diveNo*/ AS DIVE_NO "
                + "\n,st.order_no AS ORDER_NO "
                + "\n,st.segment_type AS SEGMENT_TYPE "
                + "\n,CASE WHEN st.segment_type = 'TA' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'ADS' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'DS' THEN maxd.max_depth * %6$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %7$s /*deepStopDive*/ AND %8$s /*deepStopDive*/ > 0 THEN mind.min_depth * %9$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %10$s /*deepStopDive*/ AND %11$s /*deepStopDive*/ > 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'ASS' AND %12$s /*deepStopDive*/ = 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'SS' THEN %13$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %14$s /*safetyStopDive*/ THEN %15$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %16$s /*safetyStopDive*/ THEN maxd.max_depth "
                + "\nELSE 0 "
                + "\nEND AS DEPTH "
                + "\n,CASE WHEN st.segment_type = 'DS' THEN %17$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %18$s /*safetyStopDive*/ AND 'YES' = '" + subtractDeepStopTime + "' THEN %19$s /*safetyStopTime*/ - %20$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %21$s /*safetyStopDive*/ AND 'NO' = '" + subtractDeepStopTime + "' THEN %22$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %23$s /*safetyStopDive*/ THEN %24$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'TA' THEN %25$s /*turnaroundTime*/ "
                + "\nELSE 0 "
                + "\nEND AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                // Imperial
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type = 'TA' THEN ROUND((maxd.max_depth / %26$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %27$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND((maxd.max_depth / 2 / %28$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %29$s /*deepStopDive*/ AND %30$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth * %31$s /*deepStopPercent*/ / 100 / %32$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %33$s /*deepStopDive*/ AND %34$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %35$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %36$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %37$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%38$s /*safetyStopDepth*/ / %39$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %40$s /*safetyStopDive*/ THEN ROUND((%41$s /*safetyStopDepth*/ / %42$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %43$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %44$s /*salinity*/ ) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nELSE "
                // Metric
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type = 'TA' THEN ROUND((maxd.max_depth / %45$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %46$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND(((maxd.max_depth / 2) / %47$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %48$s /*deepStopDive*/ AND %49$s /*deepStopDive*/ > 0 THEN ROUND(((mind.min_depth * %50$s /*deepStopPercent*/ / 100) / %51$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %52$s /*deepStopDive*/ AND %53$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %54$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %55$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %56$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%57$s /*safetyStopDepth*/ / %58$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %59$s /*safetyStopDive*/ THEN ROUND((%60$s /*safetyStopDepth*/ / %61$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %62$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %63$s /*salinity*/) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,CASE WHEN st.segment_type in ('ASS') THEN %64$s /*ascentRateToSs*/ "
                + "\nWHEN st.segment_type = 'ADS' THEN %65$s /*ascentRateToDs*/ "
                + "\nWHEN st.segment_type IN ('AS') THEN %66$s /*ascentRateToSu*/ "
                + "\nELSE 0 "
                + "\nEND AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM segment_type st "
                + "\nINNER JOIN min_depth mind "
                + "\nON (mind.rowid = 1) "
                + "\nINNER JOIN max_depth maxd "
                + "\nON (maxd.rowid = 1) "
                + "\nINNER JOIN dive_plan_count dpc "
                + "\nON (dpc.rowid = 1) "
                + "\nWHERE ((st.segment_type IN ('STA','TA','AS','STO') AND maxd.max_depth < %67$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('STA','TA','AS','STO') AND maxd.max_depth >= %68$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','AS','STO') AND maxd.max_depth < %69$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','ASS','SS','AS','STO') AND maxd.max_depth >= %70$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('ADS','DS') AND maxd.max_depth >= %71$s /*deepStopDive*/ AND 0 <> %72$s /*deepStopDive*/ AND dpc.segment_count = 1) "
                // TODO: Do we need ADS and DS during DECO and DEEP DECO?
                + "\nOR (st.segment_type IN ('STA','TA','ASS','SS','AS','STO','ADD','DD','AD') AND 'YES' = '" + needDeepDeco + "') "
                + "\nOR (st.segment_type IN ('STA','TA','ASS','SS','AS','STO','AD') AND 'YES' = '" + needDeco + "')) "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(diveNo) // 1
                ,String.valueOf(diveNo)
                ,String.valueOf(diveNo) // 3
                ,String.valueOf(diverNo)
                ,String.valueOf(diveNo) // 5
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(deepStopDive) // 7
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 9
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 11
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth) // 13
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 15
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopTime) // 17
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopTime) // 19
                ,String.valueOf(deepStopTime)
                ,String.valueOf(safetyStopDive) // 21
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(safetyStopDive) // 23
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(turnaroundTime) // 25
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 27
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 29
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 31
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 33
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 35
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 37
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 39
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 41
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 43
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 45
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 47
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 49
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(salinity) // 51
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 53
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 55
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDepth) // 57
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 59
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 61
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(salinity) // 63
                ,String.valueOf(ascentRateToSs)
                ,String.valueOf(ascentRateToDs) // 65
                ,String.valueOf(ascentRateToSu)
                ,String.valueOf(deepStopDive) // 67
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDive) // 69
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopDive) // 71
                ,String.valueOf(deepStopDive));

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts all the Descents (DE) for that Dive Plan
        // Inserts one Descent (DE) to the maximum depth
        sqlSt = "/*02- Inserts all the Descents (DE) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) - 1 AS ORDER_NO "
                + "\n,'DE' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,0 AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE  dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(descentRate)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts a Descent (DE) for the Bubble Check
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*03- Inserts a Descent (DE) for the Bubble Check*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,1 AS ORDER_NO "
                    + "\n,'DE' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,0 AS MINUTE "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                        String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(descentRate)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts all of the Bottom Time (BT) for that Dive Plan
        // Only insert the deepest Bottom Time (BT)
        sqlSt = "/*04- Inserts all of the Bottom Time (BT) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + ",1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,(SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 DESC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts the Bubble Check (BC) first a position 1
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*05- Inserts the Bubble Check (BC) first a position 1*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diverNo*/ "
                    + "\n,2 AS ORDER_NO "
                    + "\n,'BC' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,? AS MINUTE /*bubbleCheckTime*/ "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,0 AS CALC_DESCENT_RATE "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                        String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckTime)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts the last Bottom Time (BT) after the Turnaround (TA)
        sqlSt = "/*06- Inserts the last Bottom Time (BT) after the Turnaround (TA)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH ta AS "
                + "\n(SELECT ds.dive_no AS DIVE_NO "
                + "\n,ds.order_no + 1 AS NEW_ORDER_NO "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.segment_type = 'TA' "
                + "\nAND ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ?) /*diveNo*/ "
                + "\n,max_bt AS "
                // Find the maximum BT for that Dive Plan
                + "\n(SELECT CAST(MAX(ds.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\n,ds.dive_no "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT' ) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ta.new_order_no AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,ds.depth AS DEPTH "
                + "\n,ds.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((ds.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((ds.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_segment ds "
                + "\nINNER JOIN max_bt mbt "
                + "\nON (mbt.dive_no = ds.dive_no "
                + "\nAND mbt.max_depth = ds.depth "
                + "\nAND ds.segment_type = 'BT') "
                + "\nINNER JOIN ta "
                + "\nON (ta.dive_no = ds.dive_no) "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Ascent (AS)
        // All the segments shallower than the deepest segment
        sqlSt = "/*07- Inserts the Ascent (AS)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no AS ORDER_NO "
                + "\n,'AS' AS SEGMENT_TYPE "
                // Depth has to be the same depth as the previous Bottom Time (BT)
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,? AS CALC_ASCENT_RATE /*ascentRateToSs*/ "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN min_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth > md.min_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(ascentRateToSs)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Bottom Time (BT) going up
        // All the segments shallower than the deepest segment
        sqlSt = "/*08- Inserts the Bottom Time (BT) going up*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no - 5 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth < md.max_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void insertDiveSegmentTurnaround(
              Long diverNo
            , Long diveNo
            , Double salinity
            , Double bubbleCheckDepth
            , int bubbleCheckTime
            , int descentRate
            , int turnaroundTime
            , int ascentRateToDs
            , int ascentRateToSs
            , int ascentRateToSu
            , int deepStopDive
            , int deepStopPercent
            , int deepStopTime
            , int safetyStopDive
            , Double safetyStopDepth
            , int safetyStopTime
            , String subtractDeepStopTime
            , String needDeepDeco
            , String needDeco) {
        Cursor cursor = null;
        String sqlSt;

        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }

        // Inserts all of the segments EXCEPT: BT - Bottom Time
        //                                     BC - Bubble Check
        //                                     DE - Descent
        sqlSt =   "/*01- Inserts all of the segments EXCEPT: BT, BC & DE*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the minimum depth for that Dive Plan
                // The shallowest segment
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %1$s /*diveNo*/) "
                + "\n,max_depth AS "
                // Find the maximum depth for that Dive Plan
                // The deepest segment
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dive_no = %2$s /*diveNo*/) "
                + "\n,dive_plan_count AS "
                + "\n(SELECT CAST(COUNT(*) AS INTEGER) AS SEGMENT_COUNT "
                + "\n,1 AS ROWID "
                + "\nFROM   dive_plan dp "
                + "\nWHERE  dive_no = %3$s /*diveNo*/) "
                // Generate the segments EXCEPT Bottom Time (BT), Descent (DE) and Bubble Check (BC)
                + "\nSELECT %4$s /*diverNo*/ AS DIVER_NO "
                + "\n,%5$s /*diveNo*/ AS DIVE_NO "
                + "\n,st.order_no AS ORDER_NO "
                + "\n,st.segment_type AS SEGMENT_TYPE "
                + "\n,CASE WHEN st.segment_type = 'TA' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'ADS' THEN maxd.max_depth "
                + "\nWHEN st.segment_type = 'DS' THEN maxd.max_depth * %6$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %7$s /*deepStopDive*/ AND %8$s /*deepStopDive*/ > 0 THEN mind.min_depth * %9$s /*deepStopPercent*/ / 100 "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %10$s /*deepStopDive*/ AND %11$s /*deepStopDive*/ > 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'ASS' AND %12$s /*deepStopDive*/ = 0 THEN mind.min_depth "
                + "\nWHEN st.segment_type = 'SS' THEN %13$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %14$s /*safetyStopDive*/ THEN %15$s /*safetyStopDepth*/ "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %16$s /*safetyStopDive*/ THEN maxd.max_depth "
                + "\nELSE 0 "
                + "\nEND AS DEPTH "
                + "\n,CASE WHEN st.segment_type = 'DS' THEN %17$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %18$s /*safetyStopDive*/ AND 'YES' = '" + subtractDeepStopTime + "' THEN %19$s /*safetyStopTime*/ - %20$s /*deepStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %21$s /*safetyStopDive*/ AND 'NO' = '" + subtractDeepStopTime + "' THEN %22$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'SS' AND maxd.max_depth >= %23$s /*safetyStopDive*/ THEN %24$s /*safetyStopTime*/ "
                + "\nWHEN st.segment_type = 'TA' THEN %25$s /*turnaroundTime*/ "
                + "\nELSE 0 "
                + "\nEND AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                // Imperial
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type = 'TA' THEN ROUND((maxd.max_depth / %26$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %27$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND((maxd.max_depth / 2 / %28$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %29$s /*deepStopDive*/ AND %30$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth * %31$s /*deepStopPercent*/ / 100 / %32$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %33$s /*deepStopDive*/ AND %34$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %35$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %36$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %37$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%38$s /*safetyStopDepth*/ / %39$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %40$s /*safetyStopDive*/ THEN ROUND((%41$s /*safetyStopDepth*/ / %42$s /*salinity*/ ) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %43$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %44$s /*salinity*/ ) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nELSE "
                // Metric
                + "\nCASE WHEN  st.segment_type IN ('STA','STO') THEN 1 "
                + "\nWHEN st.segment_type = 'TA' THEN ROUND((maxd.max_depth / %45$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ADS' THEN ROUND((maxd.max_depth / %46$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'DS' THEN ROUND(((maxd.max_depth / 2) / %47$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth >= %48$s /*deepStopDive*/ AND %49$s /*deepStopDive*/ > 0 THEN ROUND(((mind.min_depth * %50$s /*deepStopPercent*/ / 100) / %51$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND mind.min_depth < %52$s /*deepStopDive*/ AND %53$s /*deepStopDive*/ > 0 THEN ROUND((mind.min_depth / %54$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'ASS' AND %55$s /*deepStopDive*/ = 0 THEN ROUND((mind.min_depth / %56$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'SS' THEN ROUND((%57$s /*safetyStopDepth*/ / %58$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth >= %59$s /*safetyStopDive*/ THEN ROUND((%60$s /*safetyStopDepth*/ / %61$s /*salinity*/) + 1,3) "
                + "\nWHEN st.segment_type = 'AS' AND maxd.max_depth < %62$s /*safetyStopDive*/ THEN ROUND((maxd.max_depth / %63$s /*salinity*/) + 1,3) "
                + "\nELSE 0.0 "
                + "\nEND "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,CASE WHEN st.segment_type in ('ASS') THEN %64$s /*ascentRateToSs*/ "
                + "\nWHEN st.segment_type = 'ADS' THEN %65$s /*ascentRateToDs*/ "
                + "\nWHEN st.segment_type IN ('AS') THEN %66$s /*ascentRateToSu*/ "
                + "\nELSE 0 "
                + "\nEND AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM segment_type st "
                + "\nINNER JOIN min_depth mind "
                + "\nON (mind.rowid = 1) "
                + "\nINNER JOIN max_depth maxd "
                + "\nON (maxd.rowid = 1) "
                + "\nINNER JOIN dive_plan_count dpc "
                + "\nON (dpc.rowid = 1) "
                + "\nWHERE ((st.segment_type IN ('STA','TA','AS','STO') AND maxd.max_depth < %67$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('STA','TA','AS','STO') AND maxd.max_depth >= %68$s /*deepStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','AS','STO') AND maxd.max_depth < %69$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('TA','ASS','SS','AS','STO') AND maxd.max_depth >= %70$s /*safetyStopDive*/ ) "
                + "\nOR (st.segment_type IN ('ADS','DS') AND maxd.max_depth >= %71$s /*deepStopDive*/ AND 0 <> %72$s /*deepStopDive*/ AND dpc.segment_count = 1) "
                // TODO: Do we need ADS and DS during DECO and DEEP DECO?
                + "\nOR (st.segment_type IN ('STA','TA','ASS','SS','AS','STO','ADD','DD','AD') AND 'YES' = '" + needDeepDeco + "') "
                + "\nOR (st.segment_type IN ('STA','TA','ASS','SS','AS','STO','AD') AND 'YES' = '" + needDeco + "')) "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(diveNo) // 1
                ,String.valueOf(diveNo)
                ,String.valueOf(diveNo) // 3
                ,String.valueOf(diverNo)
                ,String.valueOf(diveNo) // 5
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(deepStopDive) // 7
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 9
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 11
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDepth) // 13
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 15
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopTime) // 17
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopTime) // 19
                ,String.valueOf(deepStopTime)
                ,String.valueOf(safetyStopDive) // 21
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(safetyStopDive) // 23
                ,String.valueOf(safetyStopTime)
                ,String.valueOf(turnaroundTime) // 25
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 27
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 29
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopPercent) // 31
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 33
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 35
                ,String.valueOf(deepStopDive)
                ,String.valueOf(salinity) // 37
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 39
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(safetyStopDepth) // 41
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 43
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 45
                ,String.valueOf(salinity)
                ,String.valueOf(salinity) // 47
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 49
                ,String.valueOf(deepStopPercent)
                ,String.valueOf(salinity) // 51
                ,String.valueOf(deepStopDive)
                ,String.valueOf(deepStopDive) // 53
                ,String.valueOf(salinity)
                ,String.valueOf(deepStopDive) // 55
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDepth) // 57
                ,String.valueOf(salinity)
                ,String.valueOf(safetyStopDive) // 59
                ,String.valueOf(safetyStopDepth)
                ,String.valueOf(salinity) // 61
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(salinity) // 63
                ,String.valueOf(ascentRateToSs)
                ,String.valueOf(ascentRateToDs) // 65
                ,String.valueOf(ascentRateToSu)
                ,String.valueOf(deepStopDive) // 67
                ,String.valueOf(deepStopDive)
                ,String.valueOf(safetyStopDive) // 69
                ,String.valueOf(safetyStopDive)
                ,String.valueOf(deepStopDive) // 71
                ,String.valueOf(deepStopDive));

        try {
            cursor = mDb.rawQuery(sqlSt,null);

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts all the Descents (DE) for that Dive Plan
        // Inserts one Descent (DE) to the maximum depth
        sqlSt = "/*02- Inserts all the Descents (DE) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = %1$s /*diveNo*/) "
                + "\nSELECT %2$s AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) - 1 AS ORDER_NO "
                + "\n,'DE' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,0 AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / %3$s) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / %4$s) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,%5$s AS CALC_DESCENT_RATE /*descentRate*/ "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE  dp.dive_no = %6$s /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        sqlSt = String.format(sqlSt
                ,String.valueOf(diveNo)
                ,String.valueOf(diverNo)
                ,String.valueOf(salinity)
                ,String.valueOf(salinity)
                ,String.valueOf(descentRate)
                ,String.valueOf(diveNo));

        try {
            cursor = mDb.rawQuery(sqlSt, null);

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts a Descent (DE) for the Bubble Check
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*03- Inserts a Descent (DE) for the Bubble Check*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diveNo*/ "
                    + "\n,1 AS ORDER_NO "
                    + "\n,'DE' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,0 AS MINUTE "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,? AS CALC_DESCENT_RATE /*descentRate*/ "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                          String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(descentRate)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts all of the Bottom Time (BT) for that Dive Plan
        // Only insert the deepest Bottom Time (BT)
        sqlSt = "/*04- Inserts all of the Bottom Time (BT) for that Dive Plan*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ? /*diveNo*/) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,(SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND md.max_depth = dp.depth) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 DESC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                     String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        if (bubbleCheckDepth > MyConstants.ZERO_D && bubbleCheckTime > MyConstants.ZERO_D) {
            // Inserts the Bubble Check (BC) first a position 1
            // If the Bubble Check Depth is greater then the maximum dept
            sqlSt = "/*05- Inserts the Bubble Check (BC) first a position 1*/"
                    + "\nINSERT INTO dive_segment "
                    + "\nWITH max_depth AS "
                    // Find the maximum depth for that Dive Plan
                    + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                    + "\n,1 AS ROWID "
                    + "\nFROM dive_plan dp "
                    + "\nWHERE dive_no = ? /*diveNo*/) "
                    + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                    + "\n,? AS DIVE_NO /*diverNo*/ "
                    + "\n,2 AS ORDER_NO "
                    + "\n,'BC' AS SEGMENT_TYPE "
                    + "\n,? AS DEPTH /*bubbleCheckDepth*/ "
                    + "\n,? AS MINUTE /*bubbleCheckTime*/ "
                    + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                    + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                    + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                    + "\nROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nELSE ROUND((? / ?) + 1,3) /*bubbleCheckDepth salinity*/ "
                    + "\nEND AS CALC_ATA "
                    + "\n,0 AS CALC_AVERAGE_DEPTH "
                    + "\n,0 AS CALC_AVERAGE_ATA "
                    + "\n,0 AS CALC_DESCENT_RATE "
                    + "\n,0 AS CALC_ASCENT_RATE "
                    + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                    + "\n,0.0 AS CALC_DECREASING_VOLUME "
                    + "\nFROM max_depth md "
                    + "\nWHERE md.rowid = 1 "
                    + "\nAND md.max_depth > " + String.valueOf(bubbleCheckDepth)
            ;

            try {
                cursor = mDb.rawQuery(sqlSt, new String[]{
                          String.valueOf(diveNo)
                        , String.valueOf(diverNo)
                        , String.valueOf(diveNo)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(bubbleCheckTime)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                        , String.valueOf(bubbleCheckDepth)
                        , String.valueOf(salinity)
                });

                Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        // Inserts the last Bottom Time (BT) after the Turnaround (TA)
        sqlSt = "/*06- Inserts the last Bottom Time (BT) after the Turnaround (TA)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH ta AS "
                + "\n(SELECT ds.dive_no AS DIVE_NO "
                + "\n,ds.order_no + 1 AS NEW_ORDER_NO "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.segment_type = 'TA' "
                + "\nAND ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ?) /*diveNo*/ "
                + "\n,max_bt AS "
                // Find the maximum BT for that Dive Plan
                + "\n(SELECT CAST(MAX(ds.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\n,ds.dive_no "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT' ) "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,ds.dive_no AS DIVE_NO "
                + "\n,ta.new_order_no AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,ds.depth AS DEPTH "
                + "\n,ds.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((ds.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((ds.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_segment ds "
                + "\nINNER JOIN max_bt mbt "
                + "\nON (mbt.dive_no = ds.dive_no "
                + "\nAND mbt.max_depth = ds.depth "
                + "\nAND ds.segment_type = 'BT') "
                + "\nINNER JOIN ta "
                + "\nON (ta.dive_no = ds.dive_no) "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                    String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Ascent (AS)
        // All the segments shallower than the deepest segment
        sqlSt = "/*07- Inserts the Ascent (AS)*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH min_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MIN(dp.depth) AS REAL) AS MIN_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no AS ORDER_NO "
                + "\n,'AS' AS SEGMENT_TYPE "
                // Depth has to be the same depth as the previous Bottom Time (BT)
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,? AS CALC_ASCENT_RATE /*ascentRateToSs*/ "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN min_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth > md.min_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC "
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                     String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(ascentRateToSs)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }

        // Inserts the Bottom Time (BT) going up
        // All the segments shallower than the deepest segment
        sqlSt = "/*08- Inserts the Bottom Time (BT) going up*/"
                + "\nINSERT INTO dive_segment "
                + "\nWITH max_depth AS "
                // Find the maximum depth for that Dive Plan
                + "\n(SELECT CAST(MAX(dp.depth) AS REAL) AS MAX_DEPTH "
                + "\n,1 AS ROWID "
                + "\nFROM dive_plan dp "
                + "\nWHERE dive_no = ?) /*diveNo*/ "
                + "\n,max_order AS "
                // Find the maximum ORDER_NO for the last Bottom Time (BT)
                + "\n(SELECT CAST(MAX(ds.order_no) AS INTEGER) AS MAX_ORDER_NO "
                + "\n,1 AS ROWID "
                + "\nFROM dive_segment ds "
                + "\nWHERE ds.diver_no = ? /*diverNo*/ "
                + "\nAND ds.dive_no = ? /*diveNo*/ "
                + "\nAND ds.segment_type = 'BT') "
                + "\nSELECT ? AS DIVER_NO /*diverNo*/ "
                + "\n,dp.dive_no AS DIVE_NO "
                + "\n,((SELECT COUNT(*) "
                + "\nFROM dive_plan dp2 "
                + "\nWHERE dp2.order_no <= dp.order_no AND dp2.dive_no = dp.dive_no) * 10) + mo.max_order_no - 5 AS ORDER_NO "
                + "\n,'BT' AS SEGMENT_TYPE "
                + "\n,dp.depth AS DEPTH "
                + "\n,dp.minute AS MINUTE "
                + "\n,0 AS AIR_CONSUMPTION_PRESSURE "
                + "\n,0 AS AIR_CONSUMPTION_VOLUME "
                + "\n,CASE WHEN '" + myCalc.getUnit() + "' = 'I' THEN "
                + "\nROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nELSE ROUND((dp.depth / ?) + 1,3) /*salinity*/ "
                + "\nEND AS CALC_ATA "
                + "\n,0 AS CALC_AVERAGE_DEPTH "
                + "\n,0 AS CALC_AVERAGE_ATA "
                + "\n,0 AS CALC_DESCENT_RATE "
                + "\n,0 AS CALC_ASCENT_RATE "
                + "\n,0.0 AS CALC_DECREASING_PRESSURE "
                + "\n,0.0 AS CALC_DECREASING_VOLUME "
                + "\nFROM dive_plan dp "
                + "\nINNER JOIN max_depth md "
                + "\nON (md.rowid = 1 "
                + "\nAND dp.depth < md.max_depth) "
                + "\nINNER JOIN max_order mo "
                + "\nON (mo.rowid = 1) "
                + "\nWHERE dp.dive_no = ? /*diveNo*/ "
                + "\nORDER BY 3 ASC"
        ;

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {
                     String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(diveNo)
                    ,String.valueOf(diverNo)
                    ,String.valueOf(salinity)
                    ,String.valueOf(salinity)
                    ,String.valueOf(diveNo)
            });

            Log.d(LOG_TAG, "Total DIVE_SEGMENT rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void updateDiveSegment(DiveSegment diveSegment) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO + "=" + diveSegment.getDiverNo() + " AND " + AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO + "=" + diveSegment.getDiveNo() + " AND " +  AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO + "=" + diveSegment.getOrderNo();
            Log.d(LOG_TAG, "Updated DIVE_NO is " + String.valueOf(diveSegment.getDiveNo()) + " and ORDER_NO is " + String.valueOf(diveSegment.getOrderNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_ORDER_NO, diveSegment.getOrderNo());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_SEGMENT_TYPE, diveSegment.getSegmentType());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_DEPTH, diveSegment.getDepth());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_MINUTE, diveSegment.getMinute());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_PRESSURE, diveSegment.getAirConsumptionPressure());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_AIR_CONSUMPTION_VOLUME, diveSegment.getAirConsumptionVolume());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ATA, diveSegment.getCalcAta());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_DEPTH, diveSegment.getCalcAverageDepth());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_AVERAGE_ATA, diveSegment.getCalcAverageAta());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DESCENT_RATE, diveSegment.getCalcDescentRate());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_ASCENT_RATE, diveSegment.getCalcAscentRate());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_PRESSURE, diveSegment.getCalcDecreasingPressure());
            values.put(AirDBHelper.TABLE_DIVE_SEGMENT_CALC_DECREASING_VOLUME, diveSegment.getCalcDecreasingVolume());
            mDb.update(AirDBHelper.TABLE_DIVE_SEGMENT, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteDiveSegmentByDiverNoDiveNo(long diverNo, long diveNo) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_SEGMENT_DIVER_NO + "=" + diverNo + " AND " + AirDBHelper.TABLE_DIVE_SEGMENT_DIVE_NO + "=" + diveNo;
            Log.d(LOG_TAG, "Deleted DIVER_NO is " + String.valueOf(diverNo) + " AND DIVE_NO is " + String.valueOf(diveNo));
            mDb.delete(AirDBHelper.TABLE_DIVE_SEGMENT, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDiveSegment() {
        try {
            Log.d(LOG_TAG, "Drop DIVE_SEGMENT");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVE_SEGMENT);
            mDb.execSQL(AirDBHelper.TABLE_CREATE_DIVE_SEGMENT);
            mDb.execSQL(AirDBHelper.TABLE_CREATE_DIVE_SEGMENT_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DIVE_TYPE Data Access
    ArrayList<DiveType> getAllDiveTypes() {
        ArrayList<DiveType> diveTypes = new ArrayList<>();
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DIVE_TYPE, COLUMNS_DIVE_TYPE, null, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DiveType diveType = new DiveType();
                    diveType.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_DIVE_TYPE)));
                    diveType.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_DESCRIPTION)));
                    diveType.setSortOrder(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_SORT_ORDER)));
                    diveType.setInPicker(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_IN_PICKER)));
                    diveTypes.add(diveType);
                }
            }
            Log.d(LOG_TAG, "Total DIVE_TYPE rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return diveTypes;
    }

    ArrayList<DiveType> getAllDiveTypePickable() {
        ArrayList<DiveType> diveTypes = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        // Returning the count for all dive types for all divers, including the Buddies
        sqlSt = "SELECT dt.dive_type "
                + "\n,dt.description "
                + "\n,dt.sort_order "
                + "\n,dt.in_picker "
                + "\n,COUNT(d.dive_no) AS DIVES "
                + "\nFROM dive_type dt "
                + "\nLEFT JOIN dive d "
                + "\nON (d.dive_type = dt.dive_type) "
                + "\nWHERE DT.IN_PICKER = 'Y' "
                + "\nGROUP BY dt.dive_type "
                + "\n,dt.description "
                + "\n,dt.in_picker "
                + "\nORDER BY dt.description ASC";

        try {
            cursor = mDb.rawQuery(sqlSt,null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DiveType diveType = new DiveType();
                    diveType.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_DIVE_TYPE)));
                    diveType.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_DESCRIPTION)));
                    diveType.setSortOrder(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_SORT_ORDER)));
                    diveType.setInPicker(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_TYPE_IN_PICKER)));
                    diveType.setDives(cursor.getInt(cursor.getColumnIndex("DIVES")));
                    diveTypes.add(diveType);
                }
            }
            Log.d(LOG_TAG, "Total DIVE_TYPE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return diveTypes;
    }

    Integer createDiveType(DiveType diveType) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_DIVE_TYPE_DIVE_TYPE, diveType.getDiveType());
            values.put(AirDBHelper.TABLE_DIVE_TYPE_DESCRIPTION, diveType.getDescription());
            values.put(AirDBHelper.TABLE_DIVE_TYPE_SORT_ORDER, diveType.getSortOrder());
            values.put(AirDBHelper.TABLE_DIVE_TYPE_IN_PICKER, diveType.getInPicker());
            long id = mDb.insert(AirDBHelper.TABLE_DIVE_TYPE, null, values);
            Log.d(LOG_TAG, "Inserted DIVE_TYPE is " + String.valueOf(diveType.getDiveType()));
            if (id ==  AirDBHelper.FK_CONSTRAINT_UPDATE) {
                return AirDBHelper.FK_CONSTRAINT_UPDATE;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    Integer updateDiveType(DiveType diveType) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_TYPE_DIVE_TYPE + "= '" + diveType.getDiveType() + "'";
            Log.d(LOG_TAG, "Updated DIVE_TYPE is " + String.valueOf(diveType.getDiveType()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_DIVE_TYPE_DESCRIPTION, diveType.getDescription());
            values.put(AirDBHelper.TABLE_DIVE_TYPE_SORT_ORDER, diveType.getSortOrder());
            values.put(AirDBHelper.TABLE_DIVE_TYPE_IN_PICKER, diveType.getInPicker());
            mDb.update(AirDBHelper.TABLE_DIVE_TYPE, values, whereClause, null);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteDiveType(String diveTypePk) {
        try {
            String whereClause = AirDBHelper.TABLE_DIVE_TYPE_DIVE_TYPE + "= '" + diveTypePk + "'";
            Log.d(LOG_TAG, "Deleted DIVE_TYPE is " + String.valueOf(diveTypePk));
            mDb.delete(AirDBHelper.TABLE_DIVE_TYPE, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void dropDiveType() {
        try {
            Log.d(LOG_TAG, "Drop DIVE_TYPE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DIVE_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVE_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DIVE_TYPE_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // DYNAMIC_SPINNER Data Access
    ArrayList<DynamicSpinner> getAllDynamicSpinners() {
        ArrayList<DynamicSpinner> dynamicSpinners = new ArrayList<>();

        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DYNAMIC_SPINNER, COLUMNS_DYNAMIC_SPINNER, null, null, null, null, null)){

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DynamicSpinner dynamicSpinner = new DynamicSpinner();
                    dynamicSpinner.setSpinnerType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE)));
                    dynamicSpinner.setSystemDefined(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED)));
                    dynamicSpinner.setSpinnerText(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TEXT)));
                    dynamicSpinners.add(dynamicSpinner);
                }
            }
            Log.d(LOG_TAG, "Total DYNAMIC_SPINNER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dynamicSpinners;
    }

    ArrayList<DynamicSpinner> getDynamicSpinnerByType(String spinnerType) {
        ArrayList<DynamicSpinner> dynamicSpinners = new ArrayList<>();

        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DYNAMIC_SPINNER, COLUMNS_DYNAMIC_SPINNER, AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE + " = '" + spinnerType + "'", null, null, null, null)){

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    DynamicSpinner dynamicSpinner = new DynamicSpinner();
                    dynamicSpinner.setSpinnerType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE)));
                    dynamicSpinner.setSystemDefined(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED)));
                    dynamicSpinner.setSpinnerText(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TEXT)));
                    dynamicSpinners.add(dynamicSpinner);
                }
            }
            Log.d(LOG_TAG, "Total DYNAMIC_SPINNER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return dynamicSpinners;
    }

    void updateDynamicSpinnerByType(String spinnerType, String oldSpinnerText, String newSpinnerText) {
        String sqlSt;
        int usageCount;

        // If newSpinnerText does not exist in the DYNAMIC_SPINNER table, then add the newSpinnerText
        // If oldSpinnerText is not reference anywhere else, delete the oldSpinnerText from the DYNAMIC_SPINNER table

        // Check if the Spinner Text already exists
        usageCount = spinnerTextExist(spinnerType, newSpinnerText);

        if (usageCount == MyConstants.ZERO_I) {
            // If it does not exist, add it to the Dynamic Spinner table
            DynamicSpinner dynamicSpinner = new DynamicSpinner();
            dynamicSpinner.setSpinnerType(spinnerType);
            dynamicSpinner.setSystemDefined("N");
            dynamicSpinner.setSpinnerText(newSpinnerText.trim());
            createDynamicSpinner(dynamicSpinner);
        }

        // Check if the Spinner Text is still being referenced
        usageCount = spinnerTextUsed(spinnerType, oldSpinnerText);

        if (usageCount == MyConstants.ZERO_I) {
            // If it is not referenced, delete it from the Dynamic Spinner table
            DynamicSpinner dynamicSpinner = new DynamicSpinner();
            dynamicSpinner.setSpinnerType(spinnerType);
            dynamicSpinner.setSystemDefined("N");
            dynamicSpinner.setSpinnerText(oldSpinnerText);
            deleteDynamicSpinner(dynamicSpinner);
        }
    }

    private int spinnerTextExist(String spinnerType, String spinnerText) {
        int spinnerTextCount = MyConstants.ZERO_I;
        ArrayList<DynamicSpinner> dynamicSpinners = new ArrayList<>();

        if(spinnerText.isEmpty()) {
            spinnerText = " ";
        }

        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_DYNAMIC_SPINNER, COLUMNS_DYNAMIC_SPINNER_COUNT, AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE + " = '" + spinnerType + "' AND " + AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TEXT + " = '" + spinnerText + "'", null, null, null, null)){

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    spinnerTextCount = cursor.getInt(cursor.getColumnIndex("SPINNER_TEXT_COUNT"));
                }
            }
            Log.d(LOG_TAG, "Total DYNAMIC_SPINNER rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spinnerTextCount;
    }

    private int spinnerTextUsed(String spinnerType, String spinnerText) {
        Cursor cursor = null;
        String sqlSt;
        int spinnerTextCount;

        if (spinnerType.equals("LO")) {
            // Location
            sqlSt = "SELECT COUNT(*) AS SPINNER_TEXT_COUNT FROM dive WHERE location = ? /*LO*/";
        } else if (spinnerType.equals("DS")) {
            // Dive Site
            sqlSt = "SELECT COUNT(*) AS SPINNER_TEXT_COUNT FROM dive WHERE dive_site = ? /*DS*/";
        } else {
            //Dive Boat
            sqlSt = "SELECT COUNT(*) AS SPINNER_TEXT_COUNT FROM dive WHERE dive_boat = ? /*DB*/";
        }

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {"'" + spinnerText + "'"});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                spinnerTextCount = cursor.getInt(cursor.getColumnIndex("SPINNER_TEXT_COUNT"));
            } else {
                spinnerTextCount = 0;
            }
            Log.d(LOG_TAG, "Total spinnerTextUsed rows = " + cursor.getCount());
            return spinnerTextCount;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    Integer createDynamicSpinner(DynamicSpinner dynamicSpinner) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE, dynamicSpinner.getSpinnerType());
            values.put(AirDBHelper.TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED, dynamicSpinner.getSystemDefined());
            values.put(AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TEXT, dynamicSpinner.getSpinnerText());
            long id = mDb.insert(AirDBHelper.TABLE_DYNAMIC_SPINNER, null, values);
            Log.d(LOG_TAG, "Inserted DYNAMIC_SPINNER_TYPE is " + String.valueOf(dynamicSpinner.getSpinnerType() + " DYNAMIC_SPINNER_TEXT is " + String.valueOf(dynamicSpinner.getSpinnerText())));
            if (id ==  AirDBHelper.FK_CONSTRAINT_UPDATE) {
                return AirDBHelper.FK_CONSTRAINT_UPDATE;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void deleteDynamicSpinner(DynamicSpinner dynamicSpinner) {
        try {
            String whereClause = AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TYPE
                    + " = '" + dynamicSpinner.getSpinnerType()
                    + "' AND "
                    + AirDBHelper.TABLE_DYNAMIC_SPINNER_SPINNER_TEXT
                    + " = '"
                    + dynamicSpinner.getSpinnerText()
                    + "' AND "
                    + AirDBHelper.TABLE_DYNAMIC_SPINNER_SYSTEM_DEFINED
                    + " = 'N'";
            Log.d(LOG_TAG, "Deleted SPINNER_TYPE is " + dynamicSpinner.getSpinnerType() + " and SPINNER_TEXT is" + dynamicSpinner.getSpinnerText());
            mDb.delete(AirDBHelper.TABLE_DYNAMIC_SPINNER, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropDynamicSpinner() {
        try {
            Log.d(LOG_TAG, "Drop DYNAMIC_SPINNER");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_DYNAMIC_SPINNER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_DYNAMIC_SPINNER);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // GROUP_CYLINDER
    ArrayList<GrouppCylinder> getAllGroupCylinders() {
        ArrayList<GrouppCylinder> grouppCylinders = new ArrayList<>();

        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_GROUP_CYLINDER, COLUMNS_GROUPP_CYLINDER, null, null, null, null, null)) {

                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        GrouppCylinder grouppCylinder = new GrouppCylinder();
                        grouppCylinder.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO)));
                        grouppCylinder.setCylinderNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_CYLINDER_NO)));
                        grouppCylinder.setUsageTypeCommon(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                        grouppCylinder.setIsNew(MyConstants.NO);
                        grouppCylinders.add(grouppCylinder);
                    }
                }
                Log.d(LOG_TAG, "Total GROUP_CYLINDER rows = " + cursor.getCount());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return grouppCylinders;
    }

    ArrayList<GrouppCylinder> getAllGroupCylinderByGroup(Long groupNo) {
        ArrayList<GrouppCylinder> grouppCylinders = new ArrayList<>();

        String sqlSt;
        sqlSt = "SELECT gc.group_no "
                + "\n,gc.cylinder_no "
                + "\n,gc.usage_type "
                + "\n,c.diver_no "
                + "\n,c.rated_pressure "
                + "\nFROM group_cylinder gc "
                + "\nINNER JOIN cylinder c "
                + "\nON (c.cylinder_no = gc.cylinder_no) "
                + "\nWHERE gc.group_no = ? /*group_no*/";

        try {
            try (Cursor cursor = mDb.rawQuery(sqlSt, new String[]{String.valueOf(groupNo)})) {
                if (cursor.getCount() > 0) {
                    // Set the data in the Spinner UsageType
                    ArrayList<UsageType> usageTypeList = this.getAllUsageTypes();
                    while (cursor.moveToNext()) {
                        GrouppCylinder grouppCylinder = new GrouppCylinder();
                        grouppCylinder.setItemsUsageType(usageTypeList);
                        grouppCylinder.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO)));
                        grouppCylinder.setCylinderNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_CYLINDER_NO)));
                        grouppCylinder.setUsageType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                        grouppCylinder.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_DIVER_NO)));
                        grouppCylinder.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                        grouppCylinder.setIsNew(MyConstants.NO);
                        grouppCylinders.add(grouppCylinder);
                    }
                }
                Log.d(LOG_TAG, "Total GROUP_CYLINDER rows = " + cursor.getCount());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return grouppCylinders;
    }

    ArrayList<GrouppCylinder> getAllGroupCylinderWUsage(Long diverNo, Long groupNo) {
        ArrayList<GrouppCylinder> grouppCylinders = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "WITH group_count_usage AS "
                + "\n(SELECT ddg.group_no "
                + "\n,IFNULL(COUNT(*),0) AS USAGE_COUNT "
                + "\nFROM diver_dive_group ddg "
                + "\nINNER JOIN dive d "
                + "\nON d.dive_no = ddg.dive_no "
                + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') " // Real in Spanish is Real
                + "\nGROUP BY ddg.group_no) "
                + "\nSELECT g.group_no "
                + "\n,g.diver_no "
                + "\n,gt.description AS GROUP_TYPE "
                + "\n,gc.usage_type "
                + "\n,c.cylinder_no "
                + "\n,c.cylinder_type "
                + "\n,c.volume "
                + "\n,c.rated_pressure "
                + "\n,gcu.usage_count "
                + "\nFROM groupp g "
                + "\nINNER JOIN group_type gt "
                + "\nON (gt.group_type = g.group_type) "
                + "\nINNER JOIN group_cylinder gc "
                + "\nON (gc.group_no = g.group_no) "
                + "\nINNER JOIN cylinder c "
                + "\nON (c.cylinder_no = gc.cylinder_no) "
                + "\nLEFT JOIN group_count_usage gcu "
                + "\nON (gcu.group_no = g.group_no) "
                + "\nWHERE g.diver_no = ? /*diverNo*/ "
                + "\nAND g.group_no = ? /*group_no*/ "
                + "\nORDER BY g.group_no DESC";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diverNo), String.valueOf(groupNo)});

            if (cursor.getCount() > 0) {

                // Set the data in the Spinner UsageType
                ArrayList<UsageType> usageTypeList = this.getAllUsageTypes();

                while (cursor.moveToNext()) {
                    GrouppCylinder grouppCylinder = new GrouppCylinder();
                    grouppCylinder.setItemsUsageType(usageTypeList);
                    grouppCylinder.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO)));
                    grouppCylinder.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DIVER_NO)));
                    grouppCylinder.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_TYPE)));
                    grouppCylinder.setUsageType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                    grouppCylinder.setUsageTypeOld(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE)));
                    grouppCylinder.setUsageCount(cursor.getInt(cursor.getColumnIndex("USAGE_COUNT")));
                    grouppCylinder.setCylinderNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_CYLINDER_CYLINDER_NO)));
                    grouppCylinder.setCylinderType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_CYLINDER_TYPE)));
                    grouppCylinder.setVolume(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_VOLUME)));
                    grouppCylinder.setVolumeOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_VOLUME)));
                    grouppCylinder.setRatedPressure(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                    grouppCylinder.setRatedPressureOld(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_CYLINDER_RATED_PRESSURE)));
                    grouppCylinder.setIsNew(MyConstants.NO);
                    grouppCylinders.add(grouppCylinder);
                }
            }
            Log.d(LOG_TAG, "Total GROUPP rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return grouppCylinders;
    }

    void createGroupCylinder(GrouppCylinder groupCylinder) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO, groupCylinder.getGroupNo());
            values.put(AirDBHelper.TABLE_GROUP_CYLINDER_CYLINDER_NO, groupCylinder.getCylinderNo());
            values.put(AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE, groupCylinder.getUsageType());
            mDb.insert(AirDBHelper.TABLE_GROUP_CYLINDER, null, values);
            Log.d(LOG_TAG, "Inserted GROUP_NO is " + String.valueOf(groupCylinder.getGroupNo()) + " and CYLINDER_NO is " + String.valueOf(groupCylinder.getCylinderNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    Integer deleteGroupCylinder(long groupNo, long cylinderNo) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO + "=" + groupNo + " AND " + AirDBHelper.TABLE_GROUP_CYLINDER_CYLINDER_NO + "=" + cylinderNo;
            Log.d(LOG_TAG, "Deleted GROUP_NO is " + String.valueOf(groupNo) + " and CYLINDER_NO is " + String.valueOf(cylinderNo));
            mDb.delete(AirDBHelper.TABLE_GROUP_CYLINDER, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void deleteGroupCylinderByGroupNo(long groupNo) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO + "=" + groupNo;
            Log.d(LOG_TAG, "Deleted GROUP_NO is " + String.valueOf(groupNo));
            mDb.delete(AirDBHelper.TABLE_GROUP_CYLINDER, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void deleteGroupCylinderByUsageType(long groupNo, String usageType) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_CYLINDER_GROUP_NO + "=" + groupNo + " AND " + AirDBHelper.TABLE_GROUP_CYLINDER_USAGE_TYPE + " = '" + usageType + "'";
            Log.d(LOG_TAG, "Delete GROUP_CYLINDER with GROUP_NO " + String.valueOf(groupNo) + " and USAGE_TYPE is " + usageType);
            mDb.delete(AirDBHelper.TABLE_GROUP_CYLINDER, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropGroup() {
        try {
            Log.d(LOG_TAG, "Drop GROUPP");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_GROUPP);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUPP);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUPP_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropGroupCylinder() {
        try {
            Log.d(LOG_TAG, "Drop GROUP_CYLINDER");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_GROUP_CYLINDER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUP_CYLINDER);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUP_CYLINDER_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // GROUP_TYPE Data Access
    ArrayList<GrouppType> getAllGroupTypes() {
        ArrayList<GrouppType> grouppTypes = new ArrayList<>();
        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_GROUP_TYPE, COLUMNS_GROUP_TYPE, null, null, null, null, null)) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        GrouppType grouppType = new GrouppType();
                        grouppType.setGroupType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_TYPE_GROUP_TYPE)));
                        grouppType.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_TYPE_DESCRIPTION)));
                        grouppType.setSystemDefined(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_TYPE_SYSTEM_DEFINED)));
                        grouppTypes.add(grouppType);
                    }
                }
                Log.d(LOG_TAG, "Total GROUP_TYPE rows = " + cursor.getCount());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return grouppTypes;
    }

    Integer createGroupType(GrouppType grouppType) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_GROUP_TYPE_GROUP_TYPE, grouppType.getGroupType());
            values.put(AirDBHelper.TABLE_GROUP_TYPE_DESCRIPTION, grouppType.getDescription());
            values.put(AirDBHelper.TABLE_GROUP_TYPE_SYSTEM_DEFINED, grouppType.getSystemDefined());
            long id = mDb.insert(AirDBHelper.TABLE_GROUP_TYPE, null, values);
            Log.d(LOG_TAG, "Inserted GROUP_TYPE is " + String.valueOf(grouppType.getGroupType()));
            if (id ==  AirDBHelper.FK_CONSTRAINT_UPDATE) {
                return AirDBHelper.FK_CONSTRAINT_UPDATE;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    Integer updateGroupType(GrouppType grouppType) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_TYPE_GROUP_TYPE + "= '" + grouppType.getGroupType() + "'";
            Log.d(LOG_TAG, "Updated GROUP_TYPE is " + String.valueOf(grouppType.getGroupType()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_GROUP_TYPE_DESCRIPTION, grouppType.getDescription());
            mDb.update(AirDBHelper.TABLE_GROUP_TYPE, values, whereClause, null);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteGroupType(String groupTypePk) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_TYPE_GROUP_TYPE + "= '" + groupTypePk + "'";
            Log.d(LOG_TAG, "Deleted GROUP_TYPE is " + String.valueOf(groupTypePk));
            mDb.delete(AirDBHelper.TABLE_GROUP_TYPE, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void dropGroupType() {
        try {
            Log.d(LOG_TAG, "Drop GROUP_TYPE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_GROUP_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUP_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUP_TYPE_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // GROUPP Data Access
    void getGroup(Long groupNo, Groupp groupp) {
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_GROUPP, COLUMNS_GROUPP, AirDBHelper.TABLE_GROUP_GROUP_NO + " = " + groupNo, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    groupp.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_NO)));
                    groupp.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DIVER_NO)));
                    groupp.setGroupType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_TYPE)));
                    groupp.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DESCRIPTION)));
                }
            }
            Log.d(LOG_TAG, "Total GROUPP rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<Groupp> getAllGroups() {
        ArrayList<Groupp> groupps = new ArrayList<>();

        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_GROUPP, COLUMNS_GROUPP, null, null, null, null, null)) {

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    Groupp groupp = new Groupp();
                    groupp.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_NO)));
                    groupp.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DIVER_NO)));
                    groupp.setGroupTypeCommon(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_TYPE)));
                    groupp.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DESCRIPTION)));
                    groupps.add(groupp);
                }
            }
            Log.d(LOG_TAG, "Total GROUPP rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groupps;
    }

    ArrayList<GrouppPick> getAllGrouppWDesc(Long diverNo, Long diveNo) {
        ArrayList<GrouppPick> grouppPicks = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;

        sqlSt = "SELECT g.group_no "
                + "\n,g.diver_no "
                + "\n," + String.valueOf(diveNo) + " AS DIVE_NO "
                + "\n,g.description "
                + "\n,g.group_type "
                + "\n,gt.description AS GROUP_TYPE_DESCRIPTION "
                + "\n,COUNT(ddg.dive_no) AS DIVES "
                + "\nFROM groupp g "
                + "\nINNER JOIN group_type gt "
                + "\nON (gt.group_type = g.group_type) "
                + "\nLEFT JOIN diver_dive_group ddg "
                + "\nON (ddg.diver_no = g.diver_no "
                + "\nAND ddg.group_no = g.group_no) "
                + "\nWHERE g.diver_no = ? /*diverNo*/ "
                + "\nGROUP BY g.group_no "
                + "\n,g.diver_no "
                + "\n,g.description "
                + "\n,g.group_type "
                + "\n,gt.description "
                + "\nORDER BY g.description, g.group_type ASC";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diverNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    GrouppPick grouppPick = new GrouppPick();
                    grouppPick.setGroupNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_NO)));
                    grouppPick.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DESCRIPTION)));
                    grouppPick.setDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_DIVER_NO)));
                    grouppPick.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    grouppPick.setGroupType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_GROUP_GROUP_TYPE)));
                    grouppPick.setGroupTypeDescription(cursor.getString(cursor.getColumnIndex("GROUP_TYPE_DESCRIPTION")));
                    grouppPick.setDives(cursor.getInt(cursor.getColumnIndex("DIVES")));
                    grouppPicks.add(grouppPick);
                }
            }
            Log.d(LOG_TAG, "Total GROUPP rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return grouppPicks;
    }

    void createGroupp(Groupp groupp, boolean restore) {
        try {
            ContentValues values = new ContentValues();
            // PK is autoincrement but GROUP_NO is populated for a Database Restore
            if (restore) {
                values.put(AirDBHelper.TABLE_GROUP_GROUP_NO, groupp.getGroupNo());
            }
            values.put(AirDBHelper.TABLE_GROUP_DIVER_NO, groupp.getDiverNo());
            values.put(AirDBHelper.TABLE_GROUP_GROUP_TYPE, groupp.getGroupType());
            values.put(AirDBHelper.TABLE_GROUP_DESCRIPTION, groupp.getDescription());
            long id = mDb.insert(AirDBHelper.TABLE_GROUPP, null, values);
            if (!restore) {
                groupp.setGroupNo(id);
            }
            Log.d(LOG_TAG, "Inserted GROUP_NO is " + String.valueOf(groupp.getGroupNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateGroupp(Groupp groupp) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_GROUP_NO + "=" + groupp.getGroupNo();
            Log.d(LOG_TAG, "Updated GROUP_NO is " + String.valueOf(groupp.getGroupNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_GROUP_DIVER_NO, groupp.getDiverNo());
            values.put(AirDBHelper.TABLE_GROUP_GROUP_TYPE, groupp.getGroupType());
            values.put(AirDBHelper.TABLE_GROUP_DESCRIPTION, groupp.getDescription());
            mDb.update(AirDBHelper.TABLE_GROUPP, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteGroupp(long grouppNo) {
        try {
            String whereClause = AirDBHelper.TABLE_GROUP_GROUP_NO + "=" + grouppNo;
            Log.d(LOG_TAG, "Deleted GROUP_NO is " + String.valueOf(grouppNo));
            mDb.delete(AirDBHelper.TABLE_GROUPP, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    Integer groupUsedByRealDive(long groupNo) {
        Cursor cursor = null;
        String sqlSt;
        int groupCount;

        sqlSt = "SELECT COUNT(*) AS GROUP_COUNT "
                + "\nFROM dive d "
                + "\nINNER JOIN diver_dive_group ddg "
                + "\nON (ddg.dive_no = d.dive_no) "
                + "\nWHERE d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') " // Real in Spanish is Real
                + "\nAND ddg.group_no = ? /*groupNo*/";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(groupNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                groupCount = cursor.getInt(cursor.getColumnIndex("GROUP_COUNT"));
            } else {
                groupCount = 0;
            }
            Log.d(LOG_TAG, "Total DIVER_DIVE_GROUP_CYLINDER rows = " + cursor.getCount());
            return groupCount;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    Integer cylinderUsedByRealDive(long cylinderNo) {
        Cursor cursor = null;
        String sqlSt;
        int cylinderCount;

        sqlSt = "SELECT COUNT(*) AS CYLINDER_COUNT  "
                + "\nFROM dive d "
                + "\nINNER JOIN diver_dive_group ddg "
                + "\nON (ddg.dive_no = d.dive_no) "
                + "\nINNER JOIN diver_dive_group_cylinder ddgc "
                + "\nON (ddgc.diver_no = ddg.diver_no "
                + "\nAND ddgc.dive_no = ddg.dive_no "
                + "\nAND ddgc.group_no = ddg.group_no) "
                + "\nWHERE d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                + "\nAND ddgc.cylinder_no = ? /*cylinderNo*/";

        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(cylinderNo)});
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cylinderCount = cursor.getInt(cursor.getColumnIndex("CYLINDER_COUNT"));
            } else {
                cylinderCount = 0;
            }
            Log.d(LOG_TAG, "Total DIVER_DIVE_GROUP_CYLINDER rows = " + cursor.getCount());
            return cylinderCount;
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    void dropGroupp() {
        try {
            Log.d(LOG_TAG, "Drop GROUPP");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_GROUPP);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUPP);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_GROUPP_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // MAIN Data Access
    void getMain(String diveType, Long myBuddyDiverNo, Long myGroup, Long myBuddyGroup, Main main) {
        Cursor cursor = null;
        String sqlSt;

        switch (diveType) {
            case "L":
                // L = Last
                sqlSt = "/*L = Last*/ "
                        + "\nWITH my_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,mb_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no > 1 "
                        + "\nAND dd.diver_no = %1$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,my_last AS "
                        + "\n(SELECT mdd.diver_no "
                        + "\n,mdd.dive_no "
                        + "\n,mdd.rmv "
                        + "\n,%2$s /*dive_type*/ AS dive_type "
                        + "\nFROM diver_dive mdd "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (meld.max_dive_date = md.date) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,mb_last AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,bdd.dive_no "
                        + "\n,bdr.first_name "
                        + "\n,bdd.rmv "
                        + "\n,%3$s /*dive_type*/ AS dive_type "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (mbld.max_dive_date = bd.date) "
                        + "\nINNER JOIN diver bdr "
                        + "\nON (bdr.diver_no = bdd.diver_no) "
                        + "\nWHERE bdd.diver_no > 1 "
                        + "\nAND bdd.diver_no = %4$s /*myBuddyDiverNo*/ "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        // 2020/03/27 Total MM:SS to date
                        + "\n,my_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        // 2020/03/27 Total MM:SS to date
                        + "\n,mb_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_BUDDY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = %5$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        + "\nSELECT "
                        // Dive
                        + "\ndt.description AS DIVE_TYPE "
                        // Me
                        + "\n,ROUND(IFNULL(ddgme.sac,0),3) AS MY_SAC "
                        + "\n,ROUND(IFNULL(mel.rmv,0),3) AS MY_RMV "
                        + "\n,IFNULL(mg.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_GROUP "  // My Group from the last Dive
                        + "\n,IFNULL(mt.my_total_dive, 0.0) AS MY_TOTAL_DIVE "
                        + "\n,IFNULL(meld.max_dive_date, 0.0) AS MY_LAST_DIVE "
                        + "\n,IFNULL(meld.bottom_time, 0.0) AS MY_BOTTOM_TIME "
                        // My Buddy
                        + "\n,CASE WHEN bdr.last_name IS NULL AND bdr.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                        + "\nELSE bdr.last_name" + " || ', ' || " + "bdr.first_name "
                        + "\nEND AS MY_BUDDY_NAME "
                        + "\n,ROUND(IFNULL(ddgmb.sac,0),3) AS MY_BUDDY_SAC "
                        + "\n,ROUND(IFNULL(mbl.rmv,0),3) AS MY_BUDDY_RMV "
                        + "\n,IFNULL(bg.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_BUDDY_GROUP " // My Buddy Group from the last Dive
                        + "\n,IFNULL(mbt.my_buddy_total_dive, 0.0) AS MY_BUDDY_TOTAL_DIVE "
                        + "\n,IFNULL(mbld.max_dive_date, 0.0) AS MY_BUDDY_LAST_DIVE "
                        + "\n,IFNULL(mbld.bottom_time, 0.0) AS MY_BUDDY_BOTTOM_TIME "
                        + "\nFROM dive_type dt "
                        // Me
                        + "\nLEFT OUTER JOIN my_last mel "
                        + "\nON (mel.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver_dive_group ddgme "
                        + "\nON (ddgme.diver_no = 1 "
                        + "\nAND ddgme.dive_no = mel.dive_no) "
                        + "\nLEFT OUTER JOIN groupp mg "
                        + "\nON (mg.group_no = ddgme.group_no) "
                        + "\nLEFT OUTER JOIN my_total mt "
                        + "\nON (mt.diver_no = 1) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (1 = 1) "
                        // My Buddy
                        + "\nLEFT OUTER JOIN mb_last mbl "
                        + "\nON (mbl.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver_dive_group ddgmb "
                        + "\nON (ddgmb.diver_no <> 1 "
                        + "\nAND ddgmb.dive_no = mbl.dive_no) "
                        + "\nLEFT OUTER JOIN groupp bg "
                        + "\nON (bg.group_no  = ddgmb.group_no) "
                        + "\nLEFT OUTER JOIN diver bdr "
                        + "\nON (bdr.diver_no = %6$s /*myBuddyDiverNo*/) "
                        + "\nLEFT OUTER JOIN mb_total mbt "
                        + "\nON (mbt.diver_no = ddgmb.diver_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (1 = 1) "
                        + "\nWHERE dt.dive_type = %7$s /*diveType*/";
                break;
            case "A":
                // A = Average
                sqlSt = "/*A = Average*/ "
                        + "\nWITH my_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,mb_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no > 1 "
                        + "\nAND dd.diver_no = %1$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,real_dive_types AS "
                        + "\n(SELECT dt.dive_type "
                        + "\nFROM dive_type dt "
                        + "\nWHERE dt.in_picker = 'Y') "
                        + "\n,my_average_rmv AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%2$s /*diveType*/ AS dive_type "
                        + "\n,AVG(mdd.rmv) AS RMV "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %3$s /*diveType*/ = 'A' ) "
                        + "\nOR (md.dive_type = rdt.dive_type )) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no) "
                        + "\n,my_average_sac AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%4$s /*diveType*/ AS dive_type "
                        + "\n,mg.description "
                        + "\n,AVG(mddg.sac) AS SAC "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group mddg "
                        + "\nON (mddg.diver_no = mdd.diver_no "
                        + "\nAND mddg.dive_no = mdd.dive_no "
                        + "\nAND mddg.group_no = %5$s /*myGroup*/) "
                        + "\nINNER JOIN groupp mg "
                        + "\nON (mg.group_no = mddg.group_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %6$s /*diveType*/ = 'A' ) "
                        + "\nOR (md.dive_type = rdt.dive_type )) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\n,mg.description) "
                        + "\n,mb_average_rmv AS "
                        + "\n(SELECT bdr.diver_no "
                        + "\n,%7$s /*diveType*/ AS dive_type "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\n,AVG(bdd.rmv) AS RMV "
                        + "\nFROM diver bdr "
                        + "\nINNER JOIN diver_dive bdd "
                        + "\nON (bdd.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %8$s /*diveType*/ = 'A' ) "
                        + "\nOR (bd.dive_type = rdt.dive_type )) "
                        + "\nWHERE bdr.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdr.diver_no = %9$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name) "
                        + "\n,mb_average_sac AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,%10$s /*diveType*/ AS dive_type "
                        + "\n,bg.description "
                        + "\n,AVG(bddg.sac) AS SAC "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group bddg "
                        + "\nON (bddg.diver_no = bdd.diver_no "
                        + "\nAND bddg.dive_no = bdd.dive_no "
                        + "\nAND bddg.group_no = %11$s /*myBuddyGroup*/) "
                        + "\nINNER JOIN groupp bg "
                        + "\nON (bg.group_no = bddg.group_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %12$s /*diveType*/ = 'A' ) "
                        + "\nOR (bd.dive_type = rdt.dive_type )) "
                        + "\nWHERE bdd.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdd.diver_no = %13$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bg.description) "
                        // 2020/03/27 Total MM:SS to date
                        + "\n,my_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        // 2020/03/27 Total MM:SS to date
                        + "\n,mb_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_BUDDY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = %14$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        + "\nSELECT "
                        // Dive
                        + "\ndt.description AS DIVE_TYPE "
                        // Me
                        + "\n,ROUND(IFNULL(meas.sac,0),3) AS MY_SAC "
                        + "\n,ROUND(IFNULL(mear.rmv,0),3) AS MY_RMV "
                        + "\n,IFNULL(meas.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_GROUP "
                        + "\n,IFNULL(mt.my_total_dive, 0.0) AS MY_TOTAL_DIVE "
                        + "\n,IFNULL(meld.max_dive_date, 0.0) AS MY_LAST_DIVE "
                        + "\n,IFNULL(meld.bottom_time, 0.0) AS MY_BOTTOM_TIME "
                        // My Buddy
                        + "\n,CASE WHEN bdr.last_name IS NULL AND bdr.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                        + "\nELSE bdr.last_name" + " || ', ' || " + "bdr.first_name "
                        + "\nEND AS MY_BUDDY_NAME "
                        + "\n,ROUND(IFNULL(mbas.sac,0),3) AS MY_BUDDY_SAC "
                        + "\n,ROUND(IFNULL(mbar.rmv,0),3) AS MY_BUDDY_RMV "
                        + "\n,IFNULL(mbas.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_BUDDY_GROUP "
                        + "\n,IFNULL(mbt.my_buddy_total_dive, 0.0) AS MY_BUDDY_TOTAL_DIVE "
                        + "\n,IFNULL(mbld.max_dive_date, 0.0) AS MY_BUDDY_LAST_DIVE "
                        + "\n,IFNULL(mbld.bottom_time, 0.0) AS MY_BUDDY_BOTTOM_TIME "
                        + "\nFROM dive_type  dt "
                        // Me
                        + "\nLEFT OUTER JOIN my_average_rmv mear "
                        + "\nON (mear.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_average_sac meas "
                        + "\nON (meas.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_total mt "
                        + "\nON (mt.diver_no = 1) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (1 = 1) "
                        // My Buddy
                        + "\nLEFT OUTER JOIN mb_average_rmv mbar "
                        + "\nON (mbar.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN mb_average_sac mbas "
                        + "\nON (mbas.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver bdr "
                        + "\nON (bdr.diver_no = %15$s /*myBuddyDiverNo*/) "
                        + "\nLEFT OUTER JOIN mb_total mbt "
                        + "\nON (mbt.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (1 = 1) "
                        + "\nWHERE dt.dive_type = %16$s /*diveType*/";
                break;
            case "MI":
                // Minimum
                sqlSt = "/*Minimum*/ "
                        + "\nWITH my_last_date AS "

                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "

                        + "\n,mb_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no > 1 "
                        + "\nAND dd.diver_no = %1$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "

                        + "\n,my_min_rmv AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%2$s /*diveType*/ AS dive_type "
                        + "\n,MIN(mdd.rmv) AS RMV "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\nORDER BY mdd.rmv ASC) "

                        + "\n,my_min_sac AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%3$s /*diveType*/ AS dive_type "
                        + "\n,mg.description "
                        + "\n,MIN(mddg.sac) AS SAC "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group mddg "
                        + "\nON (mddg.diver_no = mdd.diver_no "
                        + "\nAND mddg.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN groupp mg "
                        + "\nON (mg.group_no = mddg.group_no) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\n,mg.description "
                        + "\nORDER BY mddg.sac ASC) "

                        + "\n,mb_min_rmv AS "
                        + "\n(SELECT bdr.diver_no "
                        + "\n,%4$s /*diveType*/ AS dive_type "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\n,MIN(bdd.rmv) AS RMV "
                        + "\nFROM diver bdr "
                        + "\nINNER JOIN diver_dive bdd "
                        + "\nON (bdd.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nWHERE bdr.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdr.diver_no = %5$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\nORDER BY bdd.rmv ASC) "

                        + "\n,mb_min_sac AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,%6$s /*diveType*/ AS dive_type "
                        + "\n,bg.description "
                        + "\n,MIN(bddg.sac) AS SAC "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group bddg "
                        + "\nON (bddg.diver_no = bdd.diver_no "
                        + "\nAND bddg.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN groupp bg "
                        + "\nON (bg.group_no = bddg.group_no) "
                        + "\nWHERE bdd.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdd.diver_no = %7$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bg.description "
                        + "\nORDER BY bddg.sac ASC) "

                        // 2020/03/27 Total MM:SS to date
                        + "\n,my_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real

                        // 2020/03/27 Total MM:SS to date
                        + "\n,mb_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_BUDDY_TOTAL_DIVE "
                        + "\nFROM   diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE  dd.diver_no = %8$s /*myBuddyDiverNo*/) "
                        + "\nSELECT "
                        // Dive
                        + "\ndt.description AS DIVE_TYPE "
                        // Me
                        + "\n,ROUND(IFNULL(mems.sac,0),3) AS MY_SAC "
                        + "\n,ROUND(IFNULL(memr.rmv,0),3) AS MY_RMV "
                        + "\n,IFNULL(mems.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_GROUP "
                        + "\n,IFNULL(mt.my_total_dive, 0.0) AS MY_TOTAL_DIVE "
                        + "\n,IFNULL(meld.max_dive_date, 0.0) AS MY_LAST_DIVE "
                        + "\n,IFNULL(meld.bottom_time, 0.0) AS MY_BOTTOM_TIME "
                        // My Buddy
                        + "\n,CASE WHEN bdr.last_name IS NULL AND bdr.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                        + "\nELSE bdr.last_name" + " || ', ' || " + "bdr.first_name "
                        + "\nEND AS MY_BUDDY_NAME "
                        + "\n,ROUND(IFNULL(mbms.sac,0),3) AS MY_BUDDY_SAC "
                        + "\n,ROUND(IFNULL(mbmr.rmv,0),3) AS MY_BUDDY_RMV "
                        + "\n,IFNULL(mbms.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_BUDDY_GROUP "
                        + "\n,IFNULL(mbt.my_buddy_total_dive, 0.0) AS MY_BUDDY_TOTAL_DIVE "
                        + "\n,IFNULL(mbld.max_dive_date, 0.0) AS MY_BUDDY_LAST_DIVE "
                        + "\n,IFNULL(mbld.bottom_time, 0.0) AS MY_BUDDY_BOTTOM_TIME "
                        + "\nFROM dive_type  dt "
                        // Me
                        + "\nLEFT OUTER JOIN my_min_rmv memr "
                        + "\nON (memr.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_min_sac mems "
                        + "\nON (mems.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_total mt "
                        + "\nON (mt.diver_no = 1) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (1 = 1) "
                        // My Buddy
                        + "\nLEFT OUTER JOIN mb_min_rmv mbmr "
                        + "\nON (mbmr.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN mb_min_sac mbms "
                        + "\nON (mbms.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver bdr "
                        + "\nON (bdr.diver_no = %9$s /*myBuddyDiverNo*/) "
                        + "\nLEFT OUTER JOIN mb_total mbt "
                        + "\nON (mbt.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (1 = 1) "
                        + "\nWHERE dt.dive_type = %10$s /*diveType*/ "
                        + "\nLIMIT 1";
                break;

            case "MA":
                // Maximum
                sqlSt = "/*Maximum*/ "
                        + "\nWITH my_last_date AS "

                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "

                        + "\n,mb_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no > 1 "
                        + "\nAND dd.diver_no = %1$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "

                        + "\n,my_max_rmv AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%2$s /*diveType*/ AS dive_type "
                        + "\n,MAX(mdd.rmv) AS RMV "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\nORDER BY mdd.rmv DESC) "

                        + "\n,my_max_sac AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%3$s /*diveType*/ AS dive_type "
                        + "\n,mg.description "
                        + "\n,MAX(mddg.sac) AS SAC "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group mddg "
                        + "\nON (mddg.diver_no = mdd.diver_no "
                        + "\nAND mddg.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN groupp mg "
                        + "\nON (mg.group_no = mddg.group_no) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\n,mg.description "
                        + "\nORDER BY mddg.sac DESC) "

                        + "\n,mb_max_rmv AS "
                        + "\n(SELECT bdr.diver_no "
                        + "\n,%4$s /*diveType*/ AS dive_type "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\n,MAX(bdd.rmv) AS RMV "
                        + "\nFROM diver bdr "
                        + "\nINNER JOIN diver_dive bdd "
                        + "\nON (bdd.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nWHERE bdr.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdr.diver_no = %5$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\nORDER BY bdd.rmv DESC) "

                        + "\n,mb_max_sac AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,%6$s /*diveType*/ AS dive_type "
                        + "\n,bg.description "
                        + "\n,MAX(bddg.sac) AS SAC "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group bddg "
                        + "\nON (bddg.diver_no = bdd.diver_no "
                        + "\nAND bddg.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN groupp bg "
                        + "\nON (bg.group_no = bddg.group_no) "
                        + "\nWHERE bdd.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdd.diver_no = %7$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bg.description "
                        + "\nORDER BY bddg.sac DESC) "

                        // 2020/03/27 Total MM:SS to date
                        + "\n,my_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real

                        // 2020/03/27 Total MM:SS to date
                        + "\n,mb_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_BUDDY_TOTAL_DIVE "
                        + "\nFROM   diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE  dd.diver_no = %8$s /*myBuddyDiverNo*/) "
                        + "\nSELECT "
                        // Dive
                        + "\ndt.description AS DIVE_TYPE "
                        // Me
                        + "\n,ROUND(IFNULL(mems.sac,0),3) AS MY_SAC "
                        + "\n,ROUND(IFNULL(memr.rmv,0),3) AS MY_RMV "
                        + "\n,IFNULL(mems.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_GROUP "
                        + "\n,IFNULL(mt.my_total_dive, 0.0) AS MY_TOTAL_DIVE "
                        + "\n,IFNULL(meld.max_dive_date, 0.0) AS MY_LAST_DIVE "
                        + "\n,IFNULL(meld.bottom_time, 0.0) AS MY_BOTTOM_TIME "
                        // My Buddy
                        + "\n,CASE WHEN bdr.last_name IS NULL AND bdr.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                        + "\nELSE bdr.last_name" + " || ', ' || " + "bdr.first_name "
                        + "\nEND AS MY_BUDDY_NAME "
                        + "\n,ROUND(IFNULL(mbms.sac,0),3) AS MY_BUDDY_SAC "
                        + "\n,ROUND(IFNULL(mbmr.rmv,0),3) AS MY_BUDDY_RMV "
                        + "\n,IFNULL(mbms.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_BUDDY_GROUP "
                        + "\n,IFNULL(mbt.my_buddy_total_dive, 0.0) AS MY_BUDDY_TOTAL_DIVE "
                        + "\n,IFNULL(mbld.max_dive_date, 0.0) AS MY_BUDDY_LAST_DIVE "
                        + "\n,IFNULL(mbld.bottom_time, 0.0) AS MY_BUDDY_BOTTOM_TIME "
                        + "\nFROM dive_type  dt "
                        // Me
                        + "\nLEFT OUTER JOIN my_max_rmv memr "
                        + "\nON (memr.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_max_sac mems "
                        + "\nON (mems.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_total mt "
                        + "\nON (mt.diver_no = 1) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (1 = 1) "
                        // My Buddy
                        + "\nLEFT OUTER JOIN mb_max_rmv mbmr "
                        + "\nON (mbmr.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN mb_max_sac mbms "
                        + "\nON (mbms.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver bdr "
                        + "\nON (bdr.diver_no = %9$s /*myBuddyDiverNo*/) "
                        + "\nLEFT OUTER JOIN mb_total mbt "
                        + "\nON (mbt.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (1 = 1) "
                        + "\nWHERE dt.dive_type = %10$s /*diveType*/ "
                        + "\nLIMIT 1";

                break;
            case "L10":
                // L10 = Last 10
                sqlSt = "/*L10 = Last 10*/ "
                        + "\nWITH my_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,mb_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no > 1 "
                        + "\nAND dd.diver_no = %1$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,real_dive_types AS "
                        + "\n(SELECT dt.dive_type "
                        + "\nFROM dive_type dt "
                        + "\nWHERE dt.in_picker = 'Y') "
                        + "\n,my_last_10 AS "
                        + "\n(SELECT mdd.diver_no "
                        + "\n,'L10' AS DIVE_TYPE "
                        + "\n,md.dive_no AS DIVE_NO "
                        + "\n,md.log_book_no AS LOG_BOOK_NO "
                        + "\nFROM diver_dive mdd "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nWHERE mdd.diver_no = 1 "
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nORDER BY md.date DESC "
                        + "\nLIMIT 10) "
                        + "\n,mb_last_10 AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,'L10' AS DIVE_TYPE "
                        + "\n,bd.dive_no AS DIVE_NO "
                        + "\n,bd.log_book_no AS LOG_BOOK_NO "
                        + "\n,bdr.first_name "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN diver bdr "
                        + "\nON (bdr.diver_no = bdd.diver_no) "
                        + "\nWHERE bdd.diver_no = %2$s /*myBuddyDiverNo*/ "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nORDER BY bd.date DESC "
                        + "\nLIMIT 10) "
                        + "\n,my_average_rmv AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%3$s /*diveType*/ AS dive_type "
                        + "\n,AVG(mdd.rmv) AS RMV "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN my_last_10 myl10 "
                        + "\nON (myl10.dive_no = md.dive_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %4$s /*diveType*/ = 'L10' ) "
                        + "\nOR (md.dive_type = rdt.dive_type )) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no) "
                        + "\n,my_average_sac AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%5$s /*diveType*/ AS dive_type "
                        + "\n,mg.description "
                        + "\n,AVG(mddg.sac) AS SAC "
                        + "\nFROM diver mdr "
                        + "INNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no) "
                        + "\nINNER JOIN diver_dive_group mddg "
                        + "\nON (mddg.diver_no = mdd.diver_no "
                        + "\nAND mddg.dive_no = mdd.dive_no "
                        + "\nAND mddg.group_no = %6$s /*myGroup*/) "
                        + "\nINNER JOIN groupp mg "
                        + "\nON (mg.group_no = mddg.group_no) "
                        + "\nINNER JOIN my_last_10 myl10 "
                        + "\nON (myl10.dive_no = md.dive_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %7$s /*diveType*/ = 'L10' ) "
                        + "\nOR (md.dive_type = rdt.dive_type )) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\n,mg.description) "
                        + "\n,mb_average_rmv AS "
                        + "\n(SELECT bdr.diver_no "
                        + "\n,%8$s /*diveType*/ AS dive_type "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\n,AVG(bdd.rmv) AS RMV "
                        + "\nFROM diver bdr "
                        + "\nINNER JOIN diver_dive bdd "
                        + "\nON (bdd.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN mb_last_10 mbl10 "
                        + "\nON (mbl10.dive_no = bd.dive_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %9$s /*diveType*/ = 'L10' ) "
                        + "\nOR (bd.dive_type = rdt.dive_type )) "
                        + "\nWHERE bdr.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdr.diver_no = %10$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name) "
                        + "\n,mb_average_sac AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,%11$s /*diveType*/ AS dive_type "
                        + "\n,bg.description "
                        + "\n,AVG(bddg.sac) AS SAC "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no) "
                        + "\nINNER JOIN mb_last_10 mbl10 "
                        + "\nON (mbl10.dive_no = bd.dive_no) "
                        + "\nINNER JOIN diver_dive_group bddg "
                        + "\nON (bddg.diver_no = bdd.diver_no "
                        + "\nAND bddg.dive_no = bdd.dive_no "
                        + "\nAND bddg.group_no = %12$s /*myBuddyGroup*/) "
                        + "\nINNER JOIN groupp bg "
                        + "\nON (bg.group_no = bddg.group_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %13$s /*diveType*/ = 'L10' ) "
                        + "\nOR (bd.dive_type = rdt.dive_type )) "
                        + "\nWHERE bdd.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdd.diver_no = %14$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bg.description) "
                        // 2020/03/27 Total MM:SS to date
                        + "\n,my_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        // 2020/03/27 Total MM:SS to date
                        + "\n,mb_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_BUDDY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = %15$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        + "\nSELECT "
                        // Dive
                        + "\ndt.description AS DIVE_TYPE "
                        // Me
                        + "\n,ROUND(IFNULL(meas.sac,0),3) AS MY_SAC "
                        + "\n,ROUND(IFNULL(mear.rmv,0),3) AS MY_RMV "
                        + "\n,IFNULL(meas.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_GROUP "
                        + "\n,IFNULL(mt.my_total_dive, 0.0) AS MY_TOTAL_DIVE "
                        + "\n,IFNULL(meld.max_dive_date, 0.0) AS MY_LAST_DIVE "
                        + "\n,IFNULL(meld.bottom_time, 0.0) AS MY_BOTTOM_TIME "
                        // My Buddy
                        + "\n,CASE WHEN bdr.last_name IS NULL AND bdr.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                        + "\nELSE bdr.last_name" + " || ', ' || " + "bdr.first_name "
                        + "\nEND AS MY_BUDDY_NAME "
                        + "\n,ROUND(IFNULL(mbas.sac,0),3) AS MY_BUDDY_SAC "
                        + "\n,ROUND(IFNULL(mbar.rmv,0),3) AS MY_BUDDY_RMV "
                        + "\n,IFNULL(mbas.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_BUDDY_GROUP "
                        + "\n,IFNULL(mbt.my_buddy_total_dive, 0.0) AS MY_BUDDY_TOTAL_DIVE "
                        + "\n,IFNULL(mbld.max_dive_date, 0.0) AS MY_BUDDY_LAST_DIVE "
                        + "\n,IFNULL(mBld.bottom_time, 0.0) AS MY_BUDDY_BOTTOM_TIME "
                        + "\nFROM dive_type  dt "
                        // Me
                        + "\nLEFT OUTER JOIN my_average_rmv mear "
                        + "\nON (mear.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_average_sac meas "
                        + "\nON (meas.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_total mt "
                        + "\nON (mt.diver_no = 1) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (1 = 1) "
                        // My Buddy
                        + "\nLEFT OUTER JOIN mb_average_rmv mbar "
                        + "\nON (mbar.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN mb_average_sac mbas "
                        + "\nON (mbas.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver bdr "
                        + "\nON (bdr.diver_no = %16$s /*myBuddyDiverNo*/) "
                        + "\nLEFT OUTER JOIN mb_total mbt "
                        + "\nON (mbt.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (1 = 1) "
                        + "\nWHERE dt.dive_type = %17$s /*diveType*/";
                break;
            default:
                // All other types except Last, Average and Last 10
                sqlSt = "/*All other types*/ "
                        + "\nWITH my_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,mb_last_date AS "
                        + "\n(SELECT MAX(d.date) AS max_dive_date "
                        + "\n,d.bottom_time "
                        + "\nFROM dive d "
                        + "\nINNER JOIN diver_dive dd "
                        + "\nON (dd.dive_no = d.dive_no) "
                        + "\nWHERE dd.diver_no > 1 "
                        + "\nAND dd.diver_no = %1$s /*myBuddyDiverNo*/ "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                        + "\n,real_dive_types AS "
                        + "\n(SELECT dt.dive_type "
                        + "\nFROM dive_type dt "
                        + "\nWHERE dt.in_picker = 'Y') "
                        + "\n,my_average_rmv AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%2$s /*diveType*/ AS dive_type "
                        + "\n,AVG(mdd.rmv) AS RMV "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no "
                        + "\nAND md.dive_type IN ('A',%3$s /*diveType*/)) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %4$s /*diveType*/ = 'A' ) "
                        + "\nOR (md.dive_type = rdt.dive_type )) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no) "
                        + "\n,my_average_sac AS "
                        + "\n(SELECT mdr.diver_no "
                        + "\n,%5$s /*diveType*/ AS dive_type "
                        + "\n,mg.description "
                        + "\n,AVG(mddg.sac) AS SAC "
                        + "\nFROM diver mdr "
                        + "\nINNER JOIN diver_dive mdd "
                        + "\nON (mdd.diver_no = mdr.diver_no) "
                        + "\nINNER JOIN dive md "
                        + "\nON (md.dive_no = mdd.dive_no "
                        + "\nAND md.dive_type IN ('A',%6$s /*diveType*/)) "
                        + "\nINNER JOIN diver_dive_group mddg "
                        + "\nON (mddg.diver_no = mdd.diver_no "
                        + "\nAND mddg.dive_no = mdd.dive_no "
                        + "\nAND mddg.group_no = %7$s /*myGroup*/) "
                        + "\nINNER JOIN groupp mg "
                        + "\nON (mg.group_no = mddg.group_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %8$s /*diveType*/ = 'A' ) "
                        + "\nOR (md.dive_type = rdt.dive_type )) "
                        + "\nWHERE mdd.diver_no = 1 " // Always 1 for Me
                        + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nGROUP BY mdr.diver_no "
                        + "\n,mg.description) "
                        + "\n,mb_average_rmv AS "
                        + "\n(SELECT bdr.diver_no "
                        + "\n,%9$s /*diveType*/ AS dive_type "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name "
                        + "\n,AVG(bdd.rmv) AS RMV "
                        + "\nFROM diver bdr "
                        + "\nINNER JOIN diver_dive bdd "
                        + "\nON (bdd.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no "
                        + "\nAND bd.dive_type IN ('A',%10$s /*diveType*/)) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %11$s /*diveType*/ = 'A' ) "
                        + "\nOR (bd.dive_type = rdt.dive_type )) "
                        + "\nWHERE bdr.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdr.diver_no = %12$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bdr.first_name "
                        + "\n,bdr.last_name) "
                        + "\n,mb_average_sac AS "
                        + "\n(SELECT bdd.diver_no "
                        + "\n,%13$s /*diveType*/ AS dive_type "
                        + "\n,bg.description "
                        + "\n,AVG(bddg.sac) AS SAC "
                        + "\nFROM diver_dive bdd "
                        + "\nINNER JOIN dive bd "
                        + "\nON (bd.dive_no = bdd.dive_no "
                        + "\nAND bd.dive_type IN ('A',%14$s /*diveType*/)) "
                        + "\nINNER JOIN diver_dive_group bddg "
                        + "\nON (bddg.diver_no = bdd.diver_no "
                        + "\nAND bddg.dive_no = bdd.dive_no "
                        + "\nAND bddg.group_no = %15$s /*myBuddyGroup*/) "
                        + "\nINNER JOIN groupp bg "
                        + "\nON (bg.group_no = bddg.group_no) "
                        + "\nINNER JOIN real_dive_types rdt "
                        + "\nON (( %16$s /*diveType*/ = 'A' ) "
                        + "\nOR (bd.dive_type = rdt.dive_type )) "
                        + "\nWHERE bdd.diver_no <> 1 "
                        + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                        + "\nAND bdd.diver_no = %17$s /*myBuddyDiverNo*/ "
                        + "\nGROUP BY bdd.diver_no "
                        + "\n,bg.description) "
                        // 2020/03/27 Total MM:SS to date
                        + "\n,my_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_TOTAL_DIVE "
                        + "\nFROM diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE dd.diver_no = 1 "
                        + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) " // Real in Spanish is Real
                        // 2020/03/27 Total MM:SS to date
                        + "\n,mb_total AS "
                        + "\n(SELECT dd.diver_no "
                        + "\n,CAST(SUM(d.bottom_time) AS REAL) AS MY_BUDDY_TOTAL_DIVE "
                        + "\nFROM   diver_dive dd "
                        + "\nINNER JOIN dive d "
                        + "\nON (d.dive_no = dd.dive_no) "
                        + "\nWHERE  dd.diver_no = %18$s /*myBuddyDiverNo*/) "
                        + "\nSELECT "
                        // Dive
                        + "\ndt.description AS DIVE_TYPE "
                        // Me
                        + "\n,ROUND(IFNULL(meas.sac,0),3) AS MY_SAC "
                        + "\n,ROUND(IFNULL(mear.rmv,0),3) AS MY_RMV "
                        + "\n,IFNULL(meas.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_GROUP "
                        + "\n,IFNULL(mt.my_total_dive, 0.0) AS MY_TOTAL_DIVE "
                        + "\n,IFNULL(meld.max_dive_date, 0.0) AS MY_LAST_DIVE "
                        + "\n,IFNULL(meld.bottom_time, 0.0) AS MY_BOTTOM_TIME "
                        // My Buddy
                        + "\n,CASE WHEN bdr.last_name IS NULL AND bdr.first_name IS NULL THEN '" + mContext.getResources().getString(R.string.sql_no_buddy) + "' "
                        + "\nELSE bdr.last_name" + " || ', ' || " + "bdr.first_name "
                        + "\nEND AS MY_BUDDY_NAME "
                        + "\n,ROUND(IFNULL(mbas.sac,0),3) AS MY_BUDDY_SAC "
                        + "\n,ROUND(IFNULL(mbar.rmv,0),3) AS MY_BUDDY_RMV "
                        + "\n,IFNULL(mbas.description,'" + mContext.getResources().getString(R.string.lbl_no_groupp) + "') AS MY_BUDDY_GROUP "
                        + "\n,IFNULL(mbt.my_buddy_total_dive, 0.0) AS MY_BUDDY_TOTAL_DIVE "
                        + "\n,IFNULL(mbld.max_dive_date, 0.0) AS MY_BUDDY_LAST_DIVE "
                        + "\n,IFNULL(mbld.bottom_time, 0.0) AS MY_BUDDY_BOTTOM_TIME "
                        + "\nFROM dive_type  dt "
                        // Me
                        + "\nLEFT OUTER JOIN my_average_rmv mear "
                        + "\nON (mear.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_average_sac meas "
                        + "\nON (meas.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN my_total mt "
                        + "\nON (mt.diver_no = 1) "
                        + "\nINNER JOIN my_last_date meld "
                        + "\nON (1 = 1) "
                        // My Buddy
                        + "\nLEFT OUTER JOIN mb_average_rmv mbar "
                        + "\nON (mbar.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN mb_average_sac mbas "
                        + "\nON (mbas.dive_type = dt.dive_type) "
                        + "\nLEFT OUTER JOIN diver bdr "
                        + "\nON (bdr.diver_no = %19$s /*myBuddyDiverNo*/) "
                        + "\nLEFT OUTER JOIN mb_total mbt "
                        + "\nON (mbt.diver_no = bdr.diver_no) "
                        + "\nINNER JOIN mb_last_date mbld "
                        + "\nON (1 = 1) "
                        + "\nWHERE dt.dive_type = %20$s /*diveType*/";
                break;
        }

        try {
            switch (diveType) {
                case "L":
                    // L = Last
                    sqlSt = String.format(sqlSt
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                    );
                    cursor = mDb.rawQuery(sqlSt, null);
                    break;
                case "A":
                    // A = Average
                    sqlSt = String.format(sqlSt
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myGroup)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyGroup)
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                    );
                    cursor = mDb.rawQuery(sqlSt, null);
                    break;
                case "MI":
                case "MA":
                    // Minimum and Maximum
                    sqlSt = String.format(sqlSt
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                    );
                    cursor = mDb.rawQuery(sqlSt, null);
                    break;
                case "L10":
                    // L10 = Last 10
                    sqlSt = String.format(sqlSt
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myGroup)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyGroup)
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                    );
                    cursor = mDb.rawQuery(sqlSt, null);
                    break;
                default:
                    // All other types except Last, Average and Last 10
                    sqlSt = String.format(sqlSt
                            , String.valueOf(myBuddyGroup)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myGroup)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyGroup)
                            , "'" + diveType + "'"
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , String.valueOf(myBuddyDiverNo)
                            , "'" + diveType + "'"
                    );
                    cursor = mDb.rawQuery(sqlSt, null);
                    break;
            }
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                DecimalFormat df2 = new DecimalFormat("0.000");
                // Dive
                main.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_DIVE_TYPE)));
                // Me
                main.setMySac(df2.format(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_SAC))));
                main.setMyRmv(df2.format(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_RMV))));
                main.setMyGroup(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_GROUP)));
                main.setMyTotalToDate(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_TOTAL_DIVE)));
                // Must be initialized before initializing MyLastDive
                main.setMyBottomTime(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BOTTOM_TIME)));
                // Transform the Last Dive Date from Integer/Long to a Date
                Long lastDive = cursor.getLong(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_LAST_DIVE));
                if (!lastDive.equals(MyConstants.ZERO_L)) {
                    main.setMyLastDive(MyFunctions.convertDateFromLongToDate(lastDive));
                } else {
                    main.setMyLastDiveX(" ");
                    main.setMySurfaceInterval(" ");
                }

                // My Buddy
                main.setMyBuddyName(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_NAME)));
                main.setMyBuddySac(df2.format(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_SAC))));
                main.setMyBuddyRmv(df2.format(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_RMV))));
                main.setMyBuddyGroup(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_GROUP)));
                main.setMyBuddyTotalToDate(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_TOTAL_DIVE)));
                // Must be initialized before initializing MyLastDive
                main.setMyBuddyBottomTime(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_BOTTOM_TIME)));
                // Transform the Last Dive Date from Integer/Long to a Date
                lastDive = cursor.getLong(cursor.getColumnIndex(AirDBHelper.AS_MAIN_ACTIVITY_MY_BUDDY_LAST_DIVE));
                if (!lastDive.equals(MyConstants.ZERO_L)) {
                    main.setMyBuddyLastDive(MyFunctions.convertDateFromLongToDate(lastDive));
                } else {
                    main.setMyBuddyLastDiveX(" ");
                    main.setMyBuddySurfaceInterval(" ");
                }
            }
            Log.d(LOG_TAG, "Total MAIN rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // SAC & RMV Data Access
    ArrayList<SacRmv> getAllSacRmv(String diveTypeSelected, String myBuddyDiverNo) {
        ArrayList<SacRmv> sacRmvs = new ArrayList<>();
        Cursor cursor = null;
        String sqlSt;
        try {
            sqlSt = "WITH my_last_date AS " // Last Date
                    + "\n(SELECT MAX(d.date) AS max_dive_date "
                    + "\nFROM dive d "
                    + "\nINNER JOIN diver_dive dd "
                    + "\nON (dd.dive_no = d.dive_no) "
                    + "\nWHERE  dd.diver_no =  1 "
                    + "\nAND d.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                    + "\n,my_last AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,mdd.dive_no "
                    + "\n,md.log_book_no "
                    + "\n,mdd.rmv "
                    + "\n,'L' AS DIVE_TYPE "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN dive md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nINNER JOIN my_last_date meld "
                    + "\nON (meld.max_dive_date = md.date) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                    + "\n,mb_last AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,bdd.dive_no "
                    + "\n,bd.log_book_no "
                    + "\n,bdr.first_name "
                    + "\n,bdd.rmv "
                    + "\n,'L' AS DIVE_TYPE "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN dive bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN my_last mel "
                    + "\nON (mel.dive_no = bdd.dive_no) "
                    + "\nAND bdd.diver_no = ? /*diverNo*/ "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE  bdd.diver_no = ? /*diverNo*/ "
                    + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "')) "
                    + "\n,my_last_10 AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,'L10' AS DIVE_TYPE "
                    + "\n,md.dive_no AS DIVE_NO "
                    + "\n,md.log_book_no AS LOG_BOOK_NO "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN dive md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nORDER BY md.date DESC "
                    + "\nLIMIT 10) "
                    + "\n,mb_last_10 AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,'L10' AS DIVE_TYPE "
                    + "\n,bd.dive_no AS DIVE_NO "
                    + "\n,bd.log_book_no AS LOG_BOOK_NO "
                    + "\n,bdr.first_name "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN dive bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE bdd.diver_no = ? /*diverNo*/ "
                    + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nORDER BY bd.date DESC "
                    + "\nLIMIT 10) "
                    + "\n,my_last_10_avg AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,'L10' AS DIVE_TYPE "
                    + "\n,0 AS DIVE_NO "
                    + "\n,0 AS LOG_BOOK_NO "
                    + "\n,AVG(mdd.rmv) AS RMV "
                    + "\n,COUNT(DISTINCT mdd.dive_no) AS COUNT_BY_TYPE "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN my_last_10 md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nGROUP BY mdd.diver_no "
                    + "\n,'L10') "
                    + "\n,mb_last_10_avg AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,'L10' AS DIVE_TYPE "
                    + "\n,0 AS DIVE_NO "
                    + "\n,0 AS LOG_BOOK_NO "
                    + "\n,bdr.first_name "
                    + "\n,AVG(bdd.rmv) AS RMV "
                    + "\n,COUNT(DISTINCT bdd.dive_no) AS COUNT_BY_TYPE "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN mb_last_10 bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE bdd.diver_no = ? /*diverNo*/ "
                    + "\nGROUP BY bdd.diver_no "
                    + "\n,bdr.first_name "
                    + "\n,'L10') "
                    + "\n,my_minimum AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,mdd.dive_no "
                    + "\n,md.log_book_no "
                    + "\n,MIN(mdd.rmv) AS RMV "
                    + "\n,'MI' AS DIVE_TYPE "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN dive md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nLIMIT 1) "
                    + "\n,mb_minimum AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,bdd.dive_no "
                    + "\n,bd.log_book_no "
                    + "\n,bdr.first_name "
                    + "\n,MIN(bdd.rmv) AS RMV "
                    + "\n,'MI' AS DIVE_TYPE "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN dive bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE bdd.diver_no = ? /*diverNo*/ "
                    + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') " // Real in Spanish is Real
                    + "\nLIMIT 1) "
                    + "\n,my_maximum AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,mdd.dive_no "
                    + "\n,md.log_book_no "
                    + "\n,MAX(mdd.rmv) AS RMV "
                    + "\n,'MA' AS DIVE_TYPE "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN dive md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nLIMIT 1) "
                    + "\n,mb_maximum AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,bdd.dive_no "
                    + "\n,bd.log_book_no "
                    + "\n,bdr.first_name "
                    + "\n,MAX(bdd.rmv) AS RMV "
                    + "\n,'MA' AS DIVE_TYPE "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN dive bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE bdd.diver_no = ? /*diverNo*/ "
                    + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nLIMIT 1) "
                    + "\n,my_average AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,'A' AS DIVE_TYPE "
                    + "\n,0 AS DIVE_NO "
                    + "\n,0 AS LOG_BOOK_NO "
                    + "\n,AVG(mdd.rmv) AS RMV "
                    + "\n,COUNT(DISTINCT mdd.dive_no) AS COUNT_BY_TYPE "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN dive md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nGROUP BY mdd.diver_no "
                    + "\n,'A') "
                    + "\n,mb_average AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,'A' AS DIVE_TYPE "
                    + "\n,0 AS DIVE_NO "
                    + "\n,0 AS LOG_BOOK_NO "
                    + "\n,bdr.first_name "
                    + "\n,AVG(bdd.rmv) AS RMV "
                    + "\n,COUNT(DISTINCT bdd.dive_no) AS COUNT_BY_TYPE "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN dive bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE bdd.diver_no = ? /*diverNo*/ "
                    + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nGROUP BY bdd.diver_no "
                    + "\n,bdr.first_name "
                    + "\n,'A') "
                    + "\n,my_all AS "
                    + "\n(SELECT mdd.diver_no "
                    + "\n,md.dive_type "
                    + "\n,0 AS DIVE_NO "
                    + "\n,0 AS LOG_BOOK_NO "
                    + "\n,AVG(mdd.rmv) AS RMV "
                    + "\n,COUNT(*) AS COUNT_BY_TYPE "
                    + "\nFROM diver_dive mdd "
                    + "\nINNER JOIN dive md "
                    + "\nON (md.dive_no = mdd.dive_no) "
                    + "\nWHERE mdd.diver_no = 1 "
                    + "\nAND md.dive_type NOT IN ('L','A') "
                    + "\nAND md.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nGROUP BY mdd.diver_no "
                    + "\n,md.dive_type) "
                    + "\n,mb_all AS "
                    + "\n(SELECT bdd.diver_no "
                    + "\n,bd.dive_type "
                    + "\n,0 AS DIVE_NO "
                    + "\n,0 AS LOG_BOOK_NO "
                    + "\n,bdr.first_name "
                    + "\n,AVG(bdd.rmv) AS RMV "
                    + "\n,COUNT(*) AS COUNT_BY_TYPE "
                    + "\nFROM diver_dive bdd "
                    + "\nINNER JOIN dive bd "
                    + "\nON (bd.dive_no = bdd.dive_no) "
                    + "\nINNER JOIN diver bdr "
                    + "\nON (bdr.diver_no = bdd.diver_no) "
                    + "\nWHERE bdd.diver_no = ? /*diverNo*/ "
                    + "\nAND bd.dive_type NOT IN ('L','A') "
                    + "\nAND bd.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') "
                    + "\nGROUP BY bdd.diver_no "
                    + "\n,bdr.first_name "
                    + "\n,bd.dive_type) "
                    + "\nSELECT CASE WHEN dt.dive_type = ? THEN 'Y' /*dive_type = Last*/ "
                    + "\nELSE 'N' "
                    + "\nEND AS DIVE_TYPE_SELECTED "
                    + "\n,mel.dive_no AS DIVE_NO "
                    + "\n,mel.log_book_no AS LOG_BOOK_NO "
                    + "\n,ROUND(IFNULL(mel.rmv,0),3) AS MY_RMV "
                    + "\n,0 AS MY_COUNT "
                    + "\n,dt.dive_type AS DIVE_TYPE "
                    + "\n,dt.description AS DIVE_TYPE_DESC "
                    + "\n,IFNULL(mbl.first_name,' ') AS MY_BUDDY_NAME "
                    + "\n,ROUND(IFNULL(mbl.rmv,0),3) AS MY_BUDDY_RMV "
                    + "\n,0 AS MY_BUDDY_COUNT "
                    + "\n,dt.sort_order "
                    + "\nFROM dive_type  dt "
                    + "\nLEFT OUTER JOIN my_last mel "
                    + "\nON (mel.dive_type = dt.dive_type) "
                    + "\nLEFT OUTER JOIN mb_last mbl "
                    + "\nON (mbl.dive_type = dt.dive_type) "
                    + "\nWHERE dt.dive_type = 'L' "
                    + "\nUNION "
                    + "\nSELECT CASE WHEN dt.dive_type = ? THEN 'Y' /*dive_type = Last 10*/ "
                    + "\nELSE 'N' "
                    + "\nEND AS DIVE_TYPE_SELECTED "
                    + "\n,mel.dive_no AS DIVE_NO "
                    + "\n,mel.log_book_no AS LOG_BOOK_NO "
                    + "\n,ROUND(IFNULL(mel.rmv,0),3) AS MY_RMV "
                    + "\n,0 AS MY_COUNT "
                    + "\n,dt.dive_type AS DIVE_TYPE "
                    + "\n,dt.description AS DIVE_TYPE_DESC "
                    + "\n,IFNULL(mbl.first_name,' ') AS MY_BUDDY_NAME "
                    + "\n,ROUND(IFNULL(mbl.rmv,0),3) AS MY_BUDDY_RMV "
                    + "\n,0 AS MY_BUDDY_COUNT "
                    + "\n,dt.sort_order "
                    + "\nFROM dive_type  dt "
                    + "\nLEFT OUTER JOIN my_last_10_avg mel "
                    + "\nON (mel.dive_type = dt.dive_type) "
                    + "\nLEFT OUTER JOIN mb_last_10_avg mbl "
                    + "\nON (mbl.dive_type = dt.dive_type) "
                    + "\nWHERE dt.dive_type = 'L10' "
                    + "\nUNION "
                    + "\nSELECT CASE WHEN dt.dive_type = ? THEN 'Y' /*dive_type = Average*/ "
                    + "\nELSE 'N' "
                    + "\nEND AS DIVE_TYPE_SELECTED "
                    + "\n,mea.dive_no "
                    + "\n,mea.log_book_no "
                    + "\n,ROUND(IFNULL(mea.rmv,0),3) AS MY_RMV "
                    + "\n,mea.count_by_type AS MY_COUNT "
                    + "\n,dt.dive_type AS DIVE_TYPE "
                    + "\n,dt.description AS DIVE_TYPE_DESC "
                    + "\n,IFNULL(mba.first_name,' ') AS MY_NAME_NAME "
                    + "\n,ROUND(IFNULL(mba.rmv,0),3) AS MY_BUDDY_RMV "
                    + "\n,mba.count_by_type AS MY_BUDDY_COUNT "
                    + "\n,dt.sort_order "
                    + "\nFROM dive_type  dt "
                    + "\nLEFT OUTER JOIN my_average mea "
                    + "\nON (mea.dive_type = dt.dive_type) "
                    + "\nLEFT OUTER JOIN mb_average mba "
                    + "\nON (mba.dive_type = dt.dive_type) "
                    + "\nWHERE dt.dive_type = 'A' "
                    + "\nUNION "
                    + "\nSELECT CASE WHEN dt.dive_type = ? THEN 'Y' /*dive_type = Minimum*/ "
                    + "\nELSE 'N' "
                    + "\nEND AS DIVE_TYPE_SELECTED "
                    + "\n,memi.dive_no "
                    + "\n,memi.log_book_no "
                    + "\n,ROUND(IFNULL(memi.rmv,0),3) AS MY_RMV "
                    + "\n,0 AS MY_COUNT "
                    + "\n,dt.dive_type AS DIVE_TYPE "
                    + "\n,dt.description AS DIVE_TYPE_DESC "
                    + "\n,IFNULL(mbmi.first_name,' ') AS MY_NAME_NAME "
                    + "\n,ROUND(IFNULL(mbmi.rmv,0),3) AS MY_BUDDY_RMV "
                    + "\n,0 AS MY_BUDDY_COUNT "
                    + "\n,dt.sort_order  "
                    + "\nFROM dive_type  dt "
                    + "\nLEFT OUTER JOIN my_minimum memi "
                    + "\nON (memi.dive_type = dt.dive_type) "
                    + "\nLEFT OUTER JOIN mb_minimum mbmi "
                    + "\nON (mbmi.dive_type = dt.dive_type) "
                    + "\nWHERE dt.dive_type = 'MI' "
                    + "\nUNION "
                    + "\nSELECT CASE WHEN dt.dive_type = ? THEN 'Y' /*dive_type = Maximum*/ "
                    + "\nELSE 'N' "
                    + "\nEND AS DIVE_TYPE_SELECTED "
                    + "\n,mema.dive_no "
                    + "\n,mema.log_book_no "
                    + "\n,ROUND(IFNULL(mema.rmv,0),3) AS MY_RMV "
                    + "\n,0 AS MY_COUNT "
                    + "\n,dt.dive_type AS DIVE_TYPE "
                    + "\n,dt.description AS DIVE_TYPE_DESC "
                    + "\n,IFNULL(mbma.first_name,' ') AS MY_NAME_NAME "
                    + "\n,ROUND(IFNULL(mbma.rmv,0),3) AS MY_BUDDY_RMV "
                    + "\n,0 AS MY_BUDDY_COUNT "
                    + "\n,dt.sort_order  "
                    + "\nFROM dive_type  dt "
                    + "\nLEFT OUTER JOIN my_maximum mema "
                    + "\nON (mema.dive_type = dt.dive_type) "
                    + "\nLEFT OUTER JOIN mb_maximum mbma "
                    + "\nON (mbma.dive_type = dt.dive_type) "
                    + "\nWHERE dt.dive_type = 'MA' "
                    + "\nUNION "
                    + "\nSELECT CASE WHEN dt.dive_type = ? THEN 'Y' /*dive_type = X All other types*/ "
                    + "\nELSE 'N' "
                    + "\nEND AS DIVE_TYPE_SELECTED "
                    + "\n,mea.dive_no "
                    + "\n,mea.log_book_no "
                    + "\n,ROUND(IFNULL(mea.rmv,0),3) AS MY_RMV "
                    + "\n,mea.count_by_type AS MY_COUNT "
                    + "\n,dt.dive_type AS DIVE_TYPE "
                    + "\n,dt.description AS DIVE_TYPE_DESC "
                    + "\n,IFNULL(mba.first_name,' ') AS MY_BUDDY_NAME "
                    + "\n,ROUND(IFNULL(mba.rmv,0),3) AS MY_BUDDY_RMV "
                    + "\n,mba.count_by_type AS MY_BUDDY_COUNT "
                    + "\n,dt.sort_order "
                    + "\nFROM dive_type  dt "
                    + "\nLEFT OUTER JOIN my_all mea "
                    + "\nON (mea.dive_type = dt.dive_type) "
                    + "\nLEFT OUTER JOIN mb_all mba "
                    + "\nON (mba.dive_type = dt.dive_type) "
                    + "\nWHERE  dt.dive_type NOT IN ('L','L10','A','MI','MA') "
                    + "\nORDER BY dt.sort_order";

            cursor = mDb.rawQuery(sqlSt, new String [] {
                     myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,myBuddyDiverNo
                    ,diveTypeSelected
                    ,diveTypeSelected
                    ,diveTypeSelected
                    ,diveTypeSelected
                    ,diveTypeSelected
                    ,diveTypeSelected
            });

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    SacRmv sacRmv = new SacRmv();
                    sacRmv.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_DIVE_TYPE)));
                    sacRmv.setDiveTypeDesc(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_DIVE_TYPE_DESC)));
                    sacRmv.setDiveTypeSelected(cursor.getString(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_DIVE_TYPE_SELECTED)));
                    sacRmv.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_DIVE_NO)));
                    sacRmv.setLogBookNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_DIVE_LOG_BOOK_NO)));
                    sacRmv.setMyRmv(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_MY_RMV)));
                    // 04/20/2023 Added
                    sacRmv.setMySac(MyConstants.ZERO_D);
                    sacRmv.setMyCount(cursor.getInt(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_MY_COUNT)));
                    sacRmv.setMyBuddyRmv(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_MY_BUDDY_RMV)));
                    // 04/20/2023 Added
                    sacRmv.setMyBuddySac(MyConstants.ZERO_D);
                    sacRmv.setMyBuddyCount(cursor.getInt(cursor.getColumnIndex(AirDBHelper.AS_SAC_RMV_ACTIVITY_MY_BUDDY_COUNT)));
                    sacRmvs.add(sacRmv);
                }
            }
            Log.d(LOG_TAG, "Total SAC_RMV rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return sacRmvs;
    }

    // get working SAC and RMV for a given diver and group_no
    SacRmvWorking getWorkingSacRmv(Long diverNo, Long diveNo, Double rockbottomSac, Double rockbottomRmv) {
        Cursor cursor = null;
        String sqlSt;

            sqlSt = "WITH sac_group AS " +
                "\n(SELECT ddg.diver_no " +
                "\n,group_no " +
                "\nFROM diver_dive_group ddg " +
                "\nWHERE ddg.dive_no = ?) /*diveNo*/ " +
                "\n,all_sac_w AS " +
                "\n(SELECT dd.diver_no " +
                "\n,AVG(ddg.sac) AS SAC " +
                "\nFROM dive de " +
                "\nINNER JOIN diver_dive dd " +
                "\nON (dd.dive_no = de.dive_no) " +
                "\nINNER JOIN diver_dive_group ddg " +
                "\nON (ddg.diver_no = dd.diver_no " +
                "\nAND ddg.dive_no = dd.dive_no) " +
                "\nINNER JOIN sac_group sg " +
                "\nON (sg.diver_no = dd.diver_no " +
                "\nAND sg.group_no = ddg.group_no) " +
                "\nWHERE de.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') " +
                "\nAND de.dive_type = 'W' " +
                "\nAND ddg.diver_no = sg.diver_no) " +
                "\n,all_rmv_w AS " +
                "\n(SELECT dd.diver_no " +
                "\n,AVG(dd.rmv) AS rmv " +
                "\nFROM dive de " +
                "\nINNER JOIN diver_dive dd " +
                "\nON (dd.dive_no = de.dive_no) " +
                "\nWHERE de.status IN ('" + MyConstants.REAL + "','" + MyConstants.REEL + "') " +
                "\nAND de.dive_type = 'W' " +
                "\nAND dd.dive_no = ?) /*diveNo*/ " +
                // Dive
                "\nSELECT d.dive_no " +
                // Me
                "\n,IFNULL(aswme.sac," + String.valueOf(rockbottomSac) + ") AS SAC_ME " +
                "\n,IFNULL(arwme.rmv," + String.valueOf(rockbottomRmv) + ") AS RMV_ME " +
                // Both
                "\n,IFNULL(aswme.sac," + String.valueOf(rockbottomSac) + ") + IFNULL(aswmb.sac," + String.valueOf(rockbottomSac) + ") AS SAC_BOTH " +
                "\n,IFNULL(arwme.rmv," + String.valueOf(rockbottomRmv) + ") + IFNULL(arwmb.rmv," + String.valueOf(rockbottomRmv) + ") AS RMV_BOTH " +
                // Dive
                "\nFROM dive d " +
                "\nLEFT JOIN diver_dive dd " +
                "\nON (dd.dive_no = d.dive_no) " +
                // Me
                "\nLEFT JOIN all_rmv_w arwme " +
                "\nON (arwme.diver_no = dd.diver_no " +
                "\nAND arwme.diver_no = 1) " +
                "\nLEFT JOIN all_sac_w aswme " +
                "\nON (aswme.diver_no = dd.diver_no " +
                "\nAND aswme.diver_no = ? /*diverNo*/) " +
                // My Buddy
                "\nLEFT JOIN all_rmv_w arwmb " +
                "\nON (arwmb.diver_no = dd.diver_no " +
                "\nAND arwmb.diver_no <> 1) " +
                "\nLEFT JOIN all_sac_w aswmb " +
                "\nON (aswmb.diver_no = dd.diver_no " +
                "\nAND aswmb.diver_no <> aswme.diver_no) " +
                "\nWHERE d.dive_no = ? /*diveNo*/"
                ;

        SacRmvWorking sacRmvWorking = new SacRmvWorking();
        try {
            cursor = mDb.rawQuery(sqlSt, new String [] {String.valueOf(diveNo), String.valueOf(diveNo), String.valueOf(diverNo), String.valueOf(diveNo)});

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    sacRmvWorking.setDiveNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_DIVER_DIVE_DIVE_NO)));
                    sacRmvWorking.setSacMe(cursor.getDouble(cursor.getColumnIndex("SAC_ME")));
                    sacRmvWorking.setRmvMe(cursor.getDouble(cursor.getColumnIndex("RMV_ME")));
                    sacRmvWorking.setSacBoth(cursor.getDouble(cursor.getColumnIndex("SAC_BOTH")));
                    sacRmvWorking.setRmvBoth(cursor.getDouble(cursor.getColumnIndex("RMV_BOTH")));
                }
            }
            Log.d(LOG_TAG, "Total DIVE rows = " + cursor.getCount());
        } catch (SQLException e){
            throw new RuntimeException(e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return sacRmvWorking;
    }

    // SEGMENT_TYPE Data Access
    ArrayList<SegmentType> getAllSegmentTypes() {
        ArrayList<SegmentType> segmentTypes = new ArrayList<>();
        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_SEGMENT_TYPE, COLUMNS_SEGMENT_TYPE, null, null, null, null, null)) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        SegmentType segmentType = new SegmentType();
                        segmentType.setSegmentType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_SEGMENT_TYPE)));
                        segmentType.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_DESCRIPTION)));
                        segmentType.setOrderNo(cursor.getInt(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_ORDER_NO)));
                        segmentType.setDirection(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_DIRECTION)));
                        segmentType.setShowResult(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_SHOW_RESULT)));
                        segmentType.setSystemDefined(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_SYSTEM_DEFINED)));
                        segmentType.setStatus(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_SEGMENT_TYPE_STATUS)));
                        segmentTypes.add(segmentType);
                    }
                }
                Log.d(LOG_TAG, "Total SEGMENT_TYPE rows = " + cursor.getCount());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return segmentTypes;
    }

    Integer createSegmentType(SegmentType segmentType) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_SEGMENT_TYPE, segmentType.getSegmentType());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_DESCRIPTION, segmentType.getDescription());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_ORDER_NO, segmentType.getOrderNo());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_DIRECTION, segmentType.getDirection());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_SHOW_RESULT, segmentType.getShowResult());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_SYSTEM_DEFINED, segmentType.getSystemDefined());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_STATUS, segmentType.getStatus());
            long id = mDb.insert(AirDBHelper.TABLE_SEGMENT_TYPE, null, values);
            Log.d(LOG_TAG, "Inserted SEGMENT_TYPE is " + String.valueOf(segmentType.getSegmentType()));
            if (id ==  AirDBHelper.FK_CONSTRAINT_UPDATE) {
                return AirDBHelper.FK_CONSTRAINT_UPDATE;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    Integer updateSegmentType(SegmentType segmentType) {
        try {
            String whereClause = AirDBHelper.TABLE_SEGMENT_TYPE_SEGMENT_TYPE + "= '" + segmentType.getSegmentType() + "'";
            Log.d(LOG_TAG, "Updated SEGMENT_TYPE is " + String.valueOf(segmentType.getSegmentType()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_DESCRIPTION, segmentType.getDescription());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_ORDER_NO, segmentType.getOrderNo());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_DIRECTION, segmentType.getDirection());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_SHOW_RESULT, segmentType.getShowResult());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_SYSTEM_DEFINED, segmentType.getSystemDefined());
            values.put(AirDBHelper.TABLE_SEGMENT_TYPE_STATUS, segmentType.getStatus());
            mDb.update(AirDBHelper.TABLE_SEGMENT_TYPE, values, whereClause, null);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteSegmentType(String segmentTypePk) {
        try {
            String whereClause = AirDBHelper.TABLE_SEGMENT_TYPE_SEGMENT_TYPE + "= '" + segmentTypePk + "'";
            Log.d(LOG_TAG, "Deleted SEGMENT_TYPE is " + String.valueOf(segmentTypePk));
            mDb.delete(AirDBHelper.TABLE_SEGMENT_TYPE, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void dropSegmentType() {
        try {
            Log.d(LOG_TAG, "Drop SEGMENT_TYPE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_SEGMENT_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_SEGMENT_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_SEGMENT_TYPE_I1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // STATE Data Access
    ArrayList<State> getAllStates() {
        ArrayList<State> states = new ArrayList<>();
        try (Cursor cursor = mDb.query(AirDBHelper.TABLE_STATE, COLUMNS_STATE, null, null, null, null, null)) {
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    State state = new State();
                    state.setStateNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_STATE_NO)));
                    state.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_DIVE_TYPE)));
                    state.setBuddyDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_DIVER_NO)));
                    state.setMySac(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_SAC)));
                    state.setMyRmv(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_RMV)));
                    state.setMyGroup(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_GROUP)));
                    state.setMyBuddySac(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_SAC)));
                    state.setMyBuddyRmv(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_RMV)));
                    state.setMyBuddyGroup(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_GROUP)));
                    states.add(state);
                }
            }
            Log.d(LOG_TAG, "Total STATE rows = " + cursor.getCount());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return states;
    }

    void getState(State state) {
        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_STATE, COLUMNS_STATE, AirDBHelper.TABLE_STATE_STATE_NO + " = " + "1", null, null, null, null)) {
                // There is always only 1 State row with PK = 1
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    state.setStateNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_STATE_NO)));
                    state.setDiveType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_DIVE_TYPE)));
                    state.setBuddyDiverNo(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_DIVER_NO)));
                    state.setMySac(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_SAC)));
                    state.setMyRmv(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_RMV)));
                    state.setMyGroup(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_GROUP)));
                    state.setMyBuddySac(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_SAC)));
                    state.setMyBuddyRmv(cursor.getDouble(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_RMV)));
                    state.setMyBuddyGroup(cursor.getLong(cursor.getColumnIndex(AirDBHelper.TABLE_STATE_MY_BUDDY_GROUP)));
                }
                Log.d(LOG_TAG, "Total STATE rows = " + cursor.getCount());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void createState(State state) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_STATE_STATE_NO, state.getStateNo());
            values.put(AirDBHelper.TABLE_STATE_DIVE_TYPE, state.getDiveType());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_DIVER_NO, state.getBuddyDiverNo());
            values.put(AirDBHelper.TABLE_STATE_MY_SAC, state.getMySac());
            values.put(AirDBHelper.TABLE_STATE_MY_RMV, state.getMyRmv());
            values.put(AirDBHelper.TABLE_STATE_MY_GROUP, state.getMyGroup());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_SAC, state.getMyBuddySac());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_RMV, state.getMyBuddyRmv());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_GROUP, state.getMyBuddyGroup());
            mDb.insert(AirDBHelper.TABLE_STATE, null, values);
            Log.d(LOG_TAG, "Inserted STATE_NO is " + String.valueOf(state.getStateNo()));
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    void updateState(State state) {
        try {
            String whereClause = AirDBHelper.TABLE_STATE_STATE_NO + "=" + state.getStateNo();
            Log.d(LOG_TAG, "Updated STATE_NO is " + String.valueOf(state.getStateNo()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_STATE_DIVE_TYPE, state.getDiveType());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_DIVER_NO, state.getBuddyDiverNo());
            values.put(AirDBHelper.TABLE_STATE_MY_SAC, state.getMySac());
            values.put(AirDBHelper.TABLE_STATE_MY_RMV, state.getMyRmv());
            values.put(AirDBHelper.TABLE_STATE_MY_GROUP, state.getMyGroup());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_SAC, state.getMyBuddySac());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_RMV, state.getMyBuddyRmv());
            values.put(AirDBHelper.TABLE_STATE_MY_BUDDY_GROUP, state.getMyBuddyGroup());
            mDb.update(AirDBHelper.TABLE_STATE, values, whereClause, null);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void dropState() {
        try {
            Log.d(LOG_TAG, "Drop STATE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_STATE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_STATE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // USAGE_TYPE Data Access
    ArrayList<UsageType> getAllUsageTypes() {
        ArrayList<UsageType> usageTypes = new ArrayList<>();
        try {
            try (Cursor cursor = mDb.query(AirDBHelper.TABLE_USAGE_TYPE, COLUMNS_USAGE_TYPE, null, null, null, null, null)) {
                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        UsageType usageType = new UsageType();
                        usageType.setUsageType(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_USAGE_TYPE_USAGE_TYPE)));
                        usageType.setDescription(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_USAGE_TYPE_DESCRIPTION)));
                        usageType.setSystemDefined(cursor.getString(cursor.getColumnIndex(AirDBHelper.TABLE_USAGE_TYPE_SYSTEM_DEFINED)));
                        usageTypes.add(usageType);
                    }
                }
                Log.d(LOG_TAG, "Total USAGE_TYPE rows = " + cursor.getCount());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return usageTypes;
    }

    Integer createUsageType(UsageType usageType) {
        try {
            ContentValues values = new ContentValues();
            // PK is NOT autoincrement
            values.put(AirDBHelper.TABLE_USAGE_TYPE_USAGE_TYPE, usageType.getUsageType());
            values.put(AirDBHelper.TABLE_USAGE_TYPE_DESCRIPTION, usageType.getDescription());
            values.put(AirDBHelper.TABLE_USAGE_TYPE_SYSTEM_DEFINED, usageType.getSystemDefined());
            long id = mDb.insert(AirDBHelper.TABLE_USAGE_TYPE, null, values);
            Log.d(LOG_TAG, "Inserted USAGE_TYPE is " + String.valueOf(usageType.getUsageType()));
            if (id ==  AirDBHelper.FK_CONSTRAINT_UPDATE) {
                return AirDBHelper.FK_CONSTRAINT_UPDATE;
            } else {
                return 0;
            }
        } catch (SQLException e) {
            mSuccess = false;
            throw new RuntimeException(e);
        }
    }

    Integer updateUsageType(UsageType usageType) {
        try {
            String whereClause = AirDBHelper.TABLE_USAGE_TYPE_USAGE_TYPE + "= '" + usageType.getUsageType() + "'";
            Log.d(LOG_TAG, "Updated USAGE_TYPE is " + String.valueOf(usageType.getUsageType()));
            ContentValues values = new ContentValues();
            values.put(AirDBHelper.TABLE_USAGE_TYPE_DESCRIPTION, usageType.getDescription());
            values.put(AirDBHelper.TABLE_USAGE_TYPE_SYSTEM_DEFINED, usageType.getSystemDefined());
            mDb.update(AirDBHelper.TABLE_USAGE_TYPE, values, whereClause, null);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Integer deleteUsageType(String usageTypePk) {
        try {
            String whereClause = AirDBHelper.TABLE_USAGE_TYPE_USAGE_TYPE + "= '" + usageTypePk + "'";
            Log.d(LOG_TAG, "Deleted USAGE_TYPE is " + String.valueOf(usageTypePk));
            mDb.delete(AirDBHelper.TABLE_USAGE_TYPE, whereClause, null);
            return 0;
        } catch (SQLException e) {
            if (String.valueOf(e).indexOf(FK_CONSTRAINT_1811) > 0) {
                // Delete failed because of FK Constraints
                return AirDBHelper.FK_CONSTRAINT_DELETE;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    void dropUsageType() {
        try {
            Log.d(LOG_TAG, "Drop USAGE_TYPE");
            mDb.execSQL("DROP TABLE IF EXISTS " + AirDBHelper.TABLE_USAGE_TYPE);
            mDb.execSQL( AirDBHelper.TABLE_CREATE_USAGE_TYPE);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}