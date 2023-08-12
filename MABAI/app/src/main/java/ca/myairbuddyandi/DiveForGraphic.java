package ca.myairbuddyandi;

import android.content.Context;

/**
 * Created by Michel on 2016-11-29.
 * Holds all of the logic for the DiveForGraphic class
 */

public class DiveForGraphic {

    // Static
    private static final String LOG_TAG = "DiveForGraphic";

    // Public

    // Protected

    // Private
    private final Context mContext;
    private final String mUnit;
    // Dive
    private boolean mSalinity; // true = Salt, false = Fresh
    private int mLogBookNo;
    private Double mWaterTempBottom;
    private Double mAirTemp;
    private Long mDiveNo;
    private String mDiveType;
    private String mStatus;
    // My buddy
    private Double mMyBuddyBeginningPressure;
    private Double mMyBuddyBeginningVolume;
    private Double mMyBuddyEndingPressure;
    private Double mMyBuddyFirstAscentPressure;
    private String mMyBuddyFullName;
    private Double mMyBuddyRatedPressure;
    private Double mMyBuddyRatedVolume;
    private Double mMyBuddyRmv;
    private Double mMyBuddySac;
    private Double mMyBuddyTurnaroundPressure;
    private Long mBuddyDiverNo;
    // Me
    private Double mMyBeginningPressure;
    private Double mMyBeginningVolume;
    private Double mMyEndingPressure;
    private Double mMyFirstAscentPressure;
    private Double mMyRatedPressure;
    private Double mMyRatedVolume;
    private Double mMyRmv;
    private Double mMySac;
    private Double mMyTurnaroundPressure;
    private Long mMyDiverNo;
    private String mMeLabel;

    // End of variables

    // Public constructor
    DiveForGraphic(Context context) {
        mContext = context;
        mUnit = MyFunctions.getUnit();
    }

    // Getters and setters

    // Dive

    public Double getAirTemp() {return mAirTemp;}

    void setAirTemp(Double airTemp) { mAirTemp = airTemp;}
    //
    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }
    //
    public String getDiveType() {return mDiveType; }

    void setDiveType(String diveType) {mDiveType = diveType; }
    //
    public int getLogBookNo() {return mLogBookNo; }

    void setLogBookNo(int logBookNo) {
        mLogBookNo = logBookNo;
    }
    //
    boolean getSalinity() {return mSalinity;}

    void setSalinity(boolean salinity) {mSalinity = salinity;}
    //
    public String getStatus() {return mStatus; }

    void setStatus(String status) {mStatus = status; }
    //
    public Double getWaterTempBottom() {return mWaterTempBottom;}

    void setWaterTempBottom(Double waterTempBottom) { mWaterTempBottom = waterTempBottom;}

    // Me

    long getMyDiverNo() {return mMyDiverNo; }

    void setMyDiverNo(long myDiverNo) {mMyDiverNo = myDiverNo;}
    //
    public String getMeLabel() {return mMeLabel; }

    void setMeLabel(String meLabel) {mMeLabel = meLabel; }
    //
    public Double getMySac() {return mMySac; }

    void setMySac(Double mySac) {mMySac = mySac;}
    //
    public Double getMyRmv() {return mMyRmv; }

    public void setMyRmv(Double myRmv) {mMyRmv = myRmv;}
    //
    Double getMyBeginningVolume() {return mMyBeginningVolume; }

    void setMyBeginningVolume(Double myBeginningVolume) {mMyBeginningVolume = myBeginningVolume;}
    //
    Double getMyBeginningPressure() {return mMyBeginningPressure; }

    void setMyBeginningPressure(Double myBeginningPressure) {mMyBeginningPressure = myBeginningPressure;}
    //
    Double getMyRatedPressure() {return mMyRatedPressure; }

    void setMyRatedPressure(Double myRatedPressure) {mMyRatedPressure = myRatedPressure;}
    //
    Double getMyRatedVolume() {return mMyRatedVolume; }

    void setMyRatedVolume(Double myRatedVolume) {mMyRatedVolume = myRatedVolume;}
    //
    Double getMyEndingPressure() {return mMyEndingPressure; }

    void setMyEndingPressure(Double myEndingPressure) {mMyEndingPressure = myEndingPressure;}
    //
    Double getMyFirstsAscentPressure() {return mMyFirstAscentPressure; }

    void setMyFirstsAscentPressure(Double myFirstsAscentPressure) {mMyFirstAscentPressure = myFirstsAscentPressure;}
    //
    public String getMyFirstAscentPressureUnit() {

        return mMyFirstAscentPressure + " " + (mUnit.equals(MyConstants.IMPERIAL) ? mContext.getResources().getString(R.string.lbl_imperial_pressure_unit) : mContext.getResources().getString(R.string.lbl_metric_pressure_unit));
    }
    //
    public String getMyEndingPressureUnit () {
        return mMyEndingPressure + " " + (mUnit.equals(MyConstants.IMPERIAL) ? mContext.getResources().getString(R.string.lbl_imperial_pressure_unit) : mContext.getResources().getString(R.string.lbl_metric_pressure_unit));
    }
    //
    Double getMyTurnaroundPressure() {return mMyTurnaroundPressure; }

    void setMyTurnaroundPressure(Double myTurnaroundPressure) {mMyTurnaroundPressure = myTurnaroundPressure;}
    //
    public String getMyTurnaroundPressureUnit() {

        return mMyTurnaroundPressure + " " + (mUnit.equals(MyConstants.IMPERIAL) ? mContext.getResources().getString(R.string.lbl_imperial_pressure_unit) : mContext.getResources().getString(R.string.lbl_metric_pressure_unit));
    }

    // My Buddy

    long getMyBuddyDiverNo() {return mBuddyDiverNo; }

    void setMyBuddyDiverNo(long buddyDiverNo) {mBuddyDiverNo = buddyDiverNo;}
    //
    public String getMyBuddyFullName() {return mMyBuddyFullName; }

    void setMyBuddyFullName(String myBuddyFullName) {mMyBuddyFullName = myBuddyFullName; }

    //
    public Double getMyBuddySac() {return mMyBuddySac; }

    void setMyBuddySac(Double buddySac) {
        mMyBuddySac = buddySac;
    }
    //
    public Double getMyBuddyRmv() {return mMyBuddyRmv; }

    public void setMyBuddyRmv(Double buddyRmv) {mMyBuddyRmv = buddyRmv;}
    //
    Double getMyBuddyBeginningVolume() {return mMyBuddyBeginningVolume; }

    void setMyBuddyBeginningVolume(Double myBuddyBeginningVolume) {mMyBuddyBeginningVolume = myBuddyBeginningVolume;}
    //
    Double getMyBuddyBeginningPressure() {return mMyBuddyBeginningPressure; }

    void setMyBuddyBeginningPressure(Double myBuddyBeginningPressure) {mMyBuddyBeginningPressure = myBuddyBeginningPressure;}
    //
    Double getMyBuddyRatedPressure() {return mMyBuddyRatedPressure; }

    void setMyBuddyRatedPressure(Double myBuddyRatedPressure) {mMyBuddyRatedPressure = myBuddyRatedPressure;}
    //
    Double getMyBuddyRatedVolume() {return mMyBuddyRatedVolume; }

    void setMyBuddyRatedVolume(Double myBuddyRatedVolume) {mMyBuddyRatedVolume = myBuddyRatedVolume;}
    //
    Double getMyBuddyEndingPressure() {return mMyBuddyEndingPressure; }

    void setMyBuddyEndingPressure(Double myBuddyEndingPressure) {mMyBuddyEndingPressure = myBuddyEndingPressure;}
    //
    Double getMyBuddyFirstsAscentPressure() {return mMyBuddyFirstAscentPressure; }

    void setMyBuddyFirstsAscentPressure(Double myBuddyFirstsAscentPressure) {mMyBuddyFirstAscentPressure = myBuddyFirstsAscentPressure;}
    //
    public String getMyBuddyFirstAscentPressureUnit() {

        return mMyBuddyFirstAscentPressure + " " + (mUnit.equals(MyConstants.IMPERIAL) ? mContext.getResources().getString(R.string.lbl_imperial_pressure_unit) : mContext.getResources().getString(R.string.lbl_metric_pressure_unit));
    }
    //
    public String getMyBuddyEndingPressureUnit () {
        return mMyBuddyEndingPressure + " " + (mUnit.equals(MyConstants.IMPERIAL) ? mContext.getResources().getString(R.string.lbl_imperial_pressure_unit) : mContext.getResources().getString(R.string.lbl_metric_pressure_unit));
    }
    //
    Double getMyBuddyTurnaroundPressure() {return mMyBuddyTurnaroundPressure; }

    void setMyBuddyTurnaroundPressure(Double myBuddyTurnaroundPressure) {mMyBuddyTurnaroundPressure = myBuddyTurnaroundPressure;}
    //
    public String getMyBuddyTurnaroundPressureUnit() {

        return mMyBuddyTurnaroundPressure + " " + (mUnit.equals(MyConstants.IMPERIAL) ? mContext.getResources().getString(R.string.lbl_imperial_pressure_unit) : mContext.getResources().getString(R.string.lbl_metric_pressure_unit));
    }
}
