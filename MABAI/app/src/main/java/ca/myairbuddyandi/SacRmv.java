package ca.myairbuddyandi;

import java.text.DecimalFormat;

/**
 * Created by Michel on 2016-12-08.
 * Holds all of the logic for the SacRmv class
 */

public class SacRmv {

    // Static
    private static final String LOG_TAG = "SacRmv";

    // Public

    // Protected

    // Private
    private int mLogBookNo;
    private long mDiveNo;
    private Double mMyBuddyRmv;
    private Double mMyRmv;
    private Double mMySac;
    private Double mMyBuddySac;
    private Integer mMyBuddyCount;
    private Integer mMyCount;
    private String mDiveType;
    private String mDiveTypeDesc;
    private String mDiveTypeSelected;

    // End of variables

    // Public constructor
    public SacRmv() {
    }

    // Getters and setters

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }

    //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {
        mLogBookNo = logBookNo;
    }

    //

    public String getDiveType() {return mDiveType; }

    public void setDiveType(String diveType) {mDiveType = diveType;}

    //

    public String getDiveTypeDesc() {return mDiveTypeDesc; }

    void setDiveTypeDesc(String diveTypeDesc) {mDiveTypeDesc = diveTypeDesc;}

    //

    String getDiveTypeSelected() {return mDiveTypeSelected; }

    void setDiveTypeSelected(String diveTypeSelected) {mDiveTypeSelected = diveTypeSelected;}

    //

    public Double getMyRmv() {return mMyRmv; }

    public String getMyRmvString() {
        DecimalFormat df2 = new DecimalFormat("0.000");
        return df2.format(mMyRmv) + getMyCountString();
    }

    public void setMyRmv(Double myRmv) {mMyRmv = myRmv;}

    //

    public Double getMySac() {return mMySac; }

    public void setMySac(Double mySac) {mMySac = mySac;}

    //

    private Integer getMyCount() {return mMyCount; }

    private String getMyCountString() {
        if (getMyCount() == 0) {
            return "";
        } else {
              return " (" + getMyCount() + ")";
        }
    }

    void setMyCount(Integer myCount) {mMyCount = myCount;}

    //

    public Double getMyBuddyRmv() {return mMyBuddyRmv; }

    public String getMyBuddyRmvString() {
        DecimalFormat df2 = new DecimalFormat("0.000");
        return df2.format(mMyBuddyRmv) + getMyBuddyCountString();
    }

    public void setMyBuddyRmv(Double myBuddyRmv) {mMyBuddyRmv = myBuddyRmv;}

    //

    public Double getMyBuddySac() {return mMyBuddySac; }

    public void setMyBuddySac(Double myBuddySac) {mMyBuddySac = myBuddySac;}

    //

    private Integer getMyBuddyCount() {return mMyBuddyCount; }

    private String getMyBuddyCountString() {
        if (getMyBuddyCount() == 0) {
            return "";
        } else {
            return " (" + getMyBuddyCount() + ")";
        }
    }

    void setMyBuddyCount(Integer myBuddyCount) {mMyBuddyCount = myBuddyCount;}
}
