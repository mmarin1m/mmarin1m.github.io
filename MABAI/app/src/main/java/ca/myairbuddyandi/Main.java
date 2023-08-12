package ca.myairbuddyandi;

import android.content.Context;

import java.util.Date;

/**
 * Created by Michel on 2016-12-08.
 * Holds all of the logic for the Main class
 */

public class Main {

    // Static
    private static final String LOG_TAG = "Main";

    // Public

    // Protected

    // Private
    private Context mContext;
    private Date mMyBuddyLastDive;
    private Date mMyLastDive;
    private Double mMyBottomTime;
    private Double mMyBuddyBottomTime;
    private Double mMyBuddyTotalToDate;
    private Double mMyTotalToDate;
    private String mDiveType;
    private String mMyBuddyGroup;
    private String mMyBuddyLastDiveX;
    private String mMyBuddyName;
    private String mMyBuddyRmv;
    private String mMyBuddySac;
    private String mMyBuddySurfaceInterval = " ";
    private String mMyBuddyTotalToDateX;
    private String mMyGroup;
    private String mMyLastDiveX;
    private String mMyRmv;
    private String mMySac;
    private String mMySurfaceInterval = " ";
    private String mMyTotalToDateX;

    // End of variables

    // Public constructor
    public Main() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    // Dive

    public String getDiveType() {return mDiveType; }

    public void setDiveType(String diveType) {mDiveType = diveType;}

    // Me

    public void setMyBottomTime(Double myBottomTime) {mMyBottomTime = myBottomTime;}

    //

    public String getMyGroup() {return mMyGroup; }

    public void setMyGroup(String myGroup) {mMyGroup = myGroup;}

    //

    public String getMyLastDiveX() {return mMyLastDiveX; }

    public void setMyLastDiveX(String myLastDiveX) {mMyLastDiveX = myLastDiveX;}

    // NOTE: Reserved for future use
    public Date getMyLastDive() {return mMyLastDive; }

    public void setMyLastDive(Date myLastDive) {
        mMyLastDive = myLastDive;
        setMyLastDiveX(MyFunctions.formatDatetimeString(mContext, mMyLastDive));
        // Calculate surface interval
        mMyLastDive = MyFunctions.addTimeToDate(mMyLastDive,0,MyFunctions.getMm(mMyBottomTime),MyFunctions.getSs(mMyBottomTime));
        String elapsedBetween = MyFunctions.elapsedBetween(mMyLastDive, MyFunctions.getNow(),mContext.getString(R.string.lbl_days));
        setMySurfaceInterval(elapsedBetween);
    }

    //

    public String getMyRmv() {return mMyRmv; }

    public void setMyRmv(String myRmv) {mMyRmv = myRmv;}

    //

    // TODO: Review for version X.X with DB changes to State
    public String getMySac() {return mMySac; }

    public void setMySac(String mySac) {mMySac = mySac;}

    //

    public String getMySurfaceInterval() {return mMySurfaceInterval; }

    public void setMySurfaceInterval(String mySurfaceInterval) {mMySurfaceInterval = mySurfaceInterval;}

    //

    public String getMyTotalToDateX() {return mMyTotalToDateX; }

    private void setMyTotalToDateX(String myTotalToDateX) {mMyTotalToDateX = myTotalToDateX;}

    // NOTE: Reserved for future use
    public Double getMyTotalToDate() {return mMyTotalToDate; }

    public void setMyTotalToDate(Double myTotalToDate) {
        mMyTotalToDate = myTotalToDate;
        setMyTotalToDateX(MyFunctions.convertToHhMmSs(mMyTotalToDate));
    }

    // My Buddy

    public void setMyBuddyBottomTime(Double myBuddyBottomTime) {mMyBuddyBottomTime = myBuddyBottomTime;}

    //

    public String getMyBuddyGroup() {return mMyBuddyGroup; }

    public void setMyBuddyGroup(String myBuddyGroup) {mMyBuddyGroup = myBuddyGroup;}

    //

    public String getMyBuddyLastDiveX() {return mMyBuddyLastDiveX; }

    public void setMyBuddyLastDiveX(String myBuddyLastDiveX) {mMyBuddyLastDiveX = myBuddyLastDiveX;}

    // NOTE: Reserved for future use
    public Date getMyBuddyLastDive() {return mMyBuddyLastDive; }

    public void setMyBuddyLastDive(Date myBuddyLastDive) {
        mMyBuddyLastDive = myBuddyLastDive;
        setMyBuddyLastDiveX(MyFunctions.formatDatetimeString(mContext, mMyLastDive));
        // Calculate surface interval
        mMyBuddyLastDive = MyFunctions.addTimeToDate(mMyBuddyLastDive,0,MyFunctions.getMm(mMyBuddyBottomTime),MyFunctions.getSs(mMyBuddyBottomTime));
        String elapsedBetween = MyFunctions.elapsedBetween(mMyBuddyLastDive, MyFunctions.getNow(),mContext.getString(R.string.lbl_days));
        setMyBuddySurfaceInterval(elapsedBetween);
    }

    //

    public String getMyBuddyName() {return mMyBuddyName; }

    public void setMyBuddyName(String myBuddyName) {mMyBuddyName = myBuddyName;}

    //

    public String getMyBuddyRmv() {return mMyBuddyRmv; }

    public void setMyBuddyRmv(String myBuddyRmv) {mMyBuddyRmv = myBuddyRmv;}

    //

    // TODO: Review for version 2.0 with DB changes to State
    public String getMyBuddySac() {return mMyBuddySac; }

    public void setMyBuddySac(String myBuddySac) {mMyBuddySac = myBuddySac;}

    //

    public String getMyBuddySurfaceInterval() {return mMyBuddySurfaceInterval; }

    public void setMyBuddySurfaceInterval(String myBuddySurfaceInterval) {mMyBuddySurfaceInterval = myBuddySurfaceInterval;}

    //

    public String getMyBuddyTotalToDateX() {return mMyBuddyTotalToDateX; }

    private void setMyBuddyTotalToDateX(String myBuddyTotalToDateX) {mMyBuddyTotalToDateX = myBuddyTotalToDateX;}

    // NOTE: Reserved for future use
    public Double getMyBuddyTotalToDate() {return mMyBuddyTotalToDate; }

    public void setMyBuddyTotalToDate(Double myBuddyTotalToDate) {
        mMyBuddyTotalToDate = myBuddyTotalToDate;
        setMyBuddyTotalToDateX(MyFunctions.convertToHhMmSs(mMyBuddyTotalToDate));
    }
}
