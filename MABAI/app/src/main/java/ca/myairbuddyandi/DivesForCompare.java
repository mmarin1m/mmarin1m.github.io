
package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;

/**
 * Created by Michel on 2020-12-01.
 * Holds all of the logic for the DivesForCompare class
 */

public class DivesForCompare extends BaseObservable {

    // Static
    private static final String LOG_TAG = "DivesForCompare";

    // Public

    // Protected

    // Private
    // Dives
    private boolean mSalinity; // true = Salt, false = Fresh
    private int mLogBookNo1;
    private int mLogBookNo2;
    private int mLogBookNo3;
    private Double mAirTemp;
    private Double mWaterTempBottom;
    private Double mCalcAverageDepth1;
    private Double mCalcAverageDepth2;
    private Double mCalcAverageDepth3;
    private Long mDiveNo1;
    private Long mDiveNo2;
    private Long mDiveNo3;
    private Long mMeMyBuddy1;
    private Long mMeMyBuddy2;
    private Long mMeMyBuddy3;

    // My buddy
    private String mMyBuddy;
    private Double mMyBuddyBeginningPressure1;
    private Long mBuddyDiverNo1;
    private Long mBuddyDiverNo2;
    private Long mBuddyDiverNo3;

    // Me
    private Double mPsiLeft1;
    private Double mPsiLeft2;
    private Double mPsiLeft3;
    private Double mRmv1;
    private Double mRmv2;
    private Double mRmv3;
    private Double mRtPsi1;
    private Double mRtPsi2;
    private Double mRtPsi3;
    private Double mRtVol1;
    private Double mRtVol2;
    private Double mRtVol3;
    private Double mSac1;
    private Double mSac2;
    private Double mSac3;
    private Double mVolLeft1;
    private Double mVolLeft2;
    private Double mVolLeft3;
    private Double mMyBeginningPressure1;
    private Double mMyBeginningPressure2;
    private Double mMyBeginningPressure3;
    private Double mMyEndingPressure1;
    private Double mMyEndingPressure2;
    private Double mMyEndingPressure3;
    private Double mMyBeginningVolume1;
    private Double mMyBeginningVolume2;
    private Double mMyBeginningVolume3;
    private Double mMyEndingVolume1;
    private Double mMyEndingVolume2;
    private Double mMyEndingVolume3;
    private Double mMyRatedPressure1;
    private Double mMyRatedPressure2;
    private Double mMyRatedPressure3;
    private Double mMyRatedVolume1;
    private Double mMyRatedVolume2;
    private Double mMyRatedVolume3;
    private Long mMyDiverNo1;
    private Long mMyDiverNo2;
    private Long mMyDiverNo3;
    private String mMeLabel;
    private String mCylType1;
    private String mCylType2;
    private String mCylType3;
    private String mDiveType;
    private String mRt1;
    private String mRt2;
    private String mRt3;
    private String mStatus1;
    private String mStatus2;
    private String mStatus3;

    // End of variables

    // Public constructor
    public DivesForCompare() {
    }

    // Getters and setters

    // Dive

    public Double getAirTemp() {return mAirTemp;}

    public void setAirTemp(Double airTemp) { mAirTemp = airTemp;}

    //

    public Double getCalcAverageDepth1() {return mCalcAverageDepth1;}

    public void setCalcAverageDepth1(Double calcAverageDepth1) { mCalcAverageDepth1 = calcAverageDepth1;}

    //

    public Double getCalcAverageDepth2() {return mCalcAverageDepth2;}

    public void setCalcAverageDepth2(Double calcAverageDepth2) { mCalcAverageDepth2 = calcAverageDepth2;}

    //

    public Double getCalcAverageDepth3() {return mCalcAverageDepth3;}

    public void setCalcAverageDepth3(Double calcAverageDepth3) { mCalcAverageDepth3 = calcAverageDepth3;}

    //

    public long getDiveNo1() {return mDiveNo1; }

    public void setDiveNo1(long diveNo1) { mDiveNo1 = diveNo1; }

    //

    public long getDiveNo2() {return mDiveNo2; }

    public void setDiveNo2(long diveNo2) { mDiveNo2 = diveNo2; }

    //

    public long getDiveNo3() {return mDiveNo3; }

    public void setDiveNo3(long diveNo3) { mDiveNo3 = diveNo3; }

    //

    public String getDiveType() {return mDiveType; }

    public void setDiveType(String diveType) {mDiveType = diveType; }

    //

    public int getLogBookNo1() {return mLogBookNo1; }

    public void setLogBookNo1(int logBookNo1) { mLogBookNo1 = logBookNo1; }

    //

    public int getLogBookNo2() {return mLogBookNo2; }

    public void setLogBookNo2(int logBookNo2) { mLogBookNo2 = logBookNo2; }

    //

    public int getLogBookNo3() {return mLogBookNo3; }

    public void setLogBookNo3(int logBookNo3) { mLogBookNo3 = logBookNo3; }

    //

    public Long getMeMyBuddy1() {return mMeMyBuddy1;}

    public void setMeMyBuddy1(Long meMyBuddy1) {mMeMyBuddy1 = meMyBuddy1;}

    //

    public Long getMeMyBuddy2() {return mMeMyBuddy2;}

    public void setMeMyBuddy2(Long meMyBuddy2) {mMeMyBuddy2 = meMyBuddy2;}

    //

    public Long getMeMyBuddy3() {return mMeMyBuddy3;}

    public void setMeMyBuddy3(Long meMyBuddy3) {mMeMyBuddy3 = meMyBuddy3;}

    //

    public boolean getSalinity() {return mSalinity;}

    public void setSalinity(boolean salinity) {mSalinity = salinity;}

    //

    public String getStatus1() {return mStatus1; }

    public void setStatus1(String status1) {mStatus1 = status1; }

    //

    public String getStatus2() {return mStatus2; }

    public void setStatus2(String status2) {mStatus2 = status2; }

    //

    public String getStatus3() {return mStatus3; }

    public void setStatus3(String status3) {mStatus3 = status3; }

    //

    public Double getWaterTempBottom() {return mWaterTempBottom;}

    public void setWaterTempBottom(Double waterTempBottom) { mWaterTempBottom = waterTempBottom;}

    // Me

    public String getCylType1() {return mCylType1; }

    public void setCylType1(String cylType1) {mCylType1 = cylType1; }

    //

    public String getCylType2() {return mCylType2; }

    public void setCylType2(String cylType2) {mCylType2 = cylType2; }

    //

    public String getCylType3() {return mCylType3; }

    public void setCylType3(String cylType3) {mCylType3 = cylType3; }

    //

    public Double getMyBeginningPressure1() {return mMyBeginningPressure1; }

    public void setMyBeginningPressure1(Double myBeginningPressure1) {mMyBeginningPressure1 = myBeginningPressure1;}

    //

    public Double getMyBeginningPressure2() {return mMyBeginningPressure2; }

    public void setMyBeginningPressure2(Double myBeginningPressure2) {mMyBeginningPressure2 = myBeginningPressure2;}

    //

    public Double getMyBeginningPressure3() {return mMyBeginningPressure3; }

    public void setMyBeginningPressure3(Double myBeginningPressure3) {mMyBeginningPressure3 = myBeginningPressure3;}

    //

    public Double getMyBeginningVolume1() {return mMyBeginningVolume1; }

    public void setMyBeginningVolume1(Double myBeginningVolume1) {mMyBeginningVolume1 = myBeginningVolume1;}

    //

    public Double getMyBeginningVolume2() {return mMyBeginningVolume2; }

    public void setMyBeginningVolume2(Double myBeginningVolume2) {mMyBeginningVolume2 = myBeginningVolume2;}

    //

    public Double getMyBeginningVolume3() {return mMyBeginningVolume3; }

    public void setMyBeginningVolume3(Double myBeginningVolume3) {mMyBeginningVolume3 = myBeginningVolume3;}

    //

    public long getMyDiverNo1() {return mMyDiverNo1; }

    public void setMyDiverNo1(long myDiverNo1) {mMyDiverNo1 = myDiverNo1;}

    //

    public Double getMyEndingPressure1() {return mMyEndingPressure1; }

    public void setMyEndingPressure1(Double myEndingPressure1) {mMyEndingPressure1 = myEndingPressure1;}

    //

    public Double getMyEndingPressure2() {return mMyEndingPressure2; }

    public void setMyEndingPressure2(Double myEndingPressure2) {mMyEndingPressure2 = myEndingPressure2;}

    //

    public Double getMyEndingPressure3() {return mMyEndingPressure3; }

    public void setMyEndingPressure3(Double myEndingPressure3) {mMyEndingPressure3 = myEndingPressure3;}

    //

    public Double getMyEndingVolume1() {return mMyEndingVolume1; }

    public void setMyEndingVolume1(Double myEndingVolume1) {mMyEndingVolume1 = myEndingVolume1;}

    //

    public Double getMyEndingVolume2() {return mMyEndingVolume2; }

    public void setMyEndingVolume2(Double myEndingVolume2) {mMyEndingVolume2 = myEndingVolume2;}

    //

    public Double getMyEndingVolume3() {return mMyEndingVolume3; }

    public void setMyEndingVolume3(Double myEndingVolume3) {mMyEndingVolume3 = myEndingVolume3;}

    //

    public long getMyDiverNo2() {return mMyDiverNo2; }

    public void setMyDiverNo2(long myDiverNo2) {mMyDiverNo2 = myDiverNo2;}

    //

    public long getMyDiverNo3() {return mMyDiverNo3; }

    public void setMyDiverNo3(long myDiverNo3) {mMyDiverNo3 = myDiverNo3;}

    //

    public String getMeLabel() {return mMeLabel; }

    public void setMeLabel(String meLabel) {mMeLabel = meLabel; }

    //

    public Double getMyRatedPressure1() {return mMyRatedPressure1; }

    public void setMyRatedPressure1(Double myRatedPressure1) {mMyRatedPressure1 = myRatedPressure1;}

    //

    public Double getMyRatedPressure2() {return mMyRatedPressure2; }

    public void setMyRatedPressure2(Double myRatedPressure2) {mMyRatedPressure2 = myRatedPressure2;}

    //

    public Double getMyRatedPressure3() {return mMyRatedPressure3; }

    public void setMyRatedPressure3(Double myRatedPressure3) {mMyRatedPressure3 = myRatedPressure3;}

    //

    public Double getMyRatedVolume1() {return mMyRatedVolume1; }

    public void setMyRatedVolume1(Double myRatedVolume1) {mMyRatedVolume1 = myRatedVolume1;}

    //

    public Double getMyRatedVolume2() {return mMyRatedVolume2; }

    public void setMyRatedVolume2(Double myRatedVolume2) {mMyRatedVolume2 = myRatedVolume2;}

    //

    public Double getMyRatedVolume3() {return mMyRatedVolume3; }

    public void setMyRatedVolume3(Double myRatedVolume3) {mMyRatedVolume3 = myRatedVolume3;}

    //

    public Double getPsiLeft1() {return mPsiLeft1; }

    public void setPsiLeft1(Double psiLeft1) {mPsiLeft1 = psiLeft1;}

    //

    public Double getPsiLeft2() {return mPsiLeft2; }

    public void setPsiLeft2(Double psiLeft2) {mPsiLeft2 = psiLeft2;}

    //

    public Double getPsiLeft3() {return mPsiLeft3; }

    public void setPsiLeft3(Double psiLeft3) {mPsiLeft3 = psiLeft3;}

    //

    public Double getRmv1() {return mRmv1; }

    public void setRmv1(Double rmv1) {mRmv1 = rmv1;}

    //

    public Double getRmv2() {return mRmv2; }

    public void setRmv2(Double rmv2) {mRmv2 = rmv2;}

    //

    public Double getRmv3() {return mRmv3; }

    public void setRmv3(Double rmv3) {mRmv3 = rmv3;}

    //

    public String getRt1() {return mRt1; }

    public void setRt1(String rt1) {mRt1 = rt1; }

    //

    public String getRt2() {return mRt2; }

    public void setRt2(String rt2) {mRt2 = rt2; }

    //

    public String getRt3() {return mRt3; }

    public void setRt3(String rt3) {mRt3 = rt3; }

    //

    public Double getRtPsi1() {return mRtPsi1; }

    public void setRtPsi1(Double rtPsi1) {mRtPsi1 = rtPsi1;}

    //

    public Double getRtPsi2() {return mRtPsi2; }

    public void setRtPsi2(Double rtPsi2) {mRtPsi2 = rtPsi2;}

    //

    public Double getRtPsi3() {return mRtPsi3; }

    public void setRtPsi3(Double rtPsi3) {mRtPsi3 = rtPsi3;}

    //

    public Double getRtVol1() {return mRtVol1; }

    public void setRtVol1(Double rtVol1) {mRtVol1 = rtVol1;}

    //

    public Double getRtVol2() {return mRtVol2; }

    public void setRtVol2(Double rtVol2) {mRtVol2 = rtVol2;}

    //

    public Double getRtVol3() {return mRtVol3; }

    public void setRtVol3(Double rtVol3) {mRtVol3 = rtVol3;}

    //

    public Double getSac1() {return mSac1; }

    public void setSac1(Double sac1) {mSac1 = sac1;}

    //

    public Double getSac2() {return mSac2; }

    public void setSac2(Double sac2) {mSac2 = sac2;}

    //

    public Double getSac3() {return mSac3; }

    public void setSac3(Double sac3) {mSac3 = sac3;}

    //

    public Double getVolLeft1() {return mVolLeft1; }

    public void setVolLeft1(Double volLeft1) {mVolLeft1 = volLeft1;}

    //

    public Double getVolLeft2() {return mVolLeft2; }

    public void setVolLeft2(Double volLeft2) {mVolLeft2 = volLeft2;}

    //

    public Double getVolLeft3() {return mVolLeft3; }

    public void setVolLeft3(Double volLeft3) {mVolLeft3 = volLeft3;}

    // My Buddy

    public String getMyBuddy() {return mMyBuddy; }

    public void setMyBuddy(String myBuddy) {mMyBuddy = myBuddy; }

    //

    public long getMyBuddyDiverNo1() {return mBuddyDiverNo1; }

    public void setMyBuddyDiverNo1(long buddyDiverNo1) {mBuddyDiverNo1 = buddyDiverNo1;}

    //

    public long getMyBuddyDiverNo2() {return mBuddyDiverNo2; }

    public void setMyBuddyDiverNo2(long buddyDiverNo2) {mBuddyDiverNo2 = buddyDiverNo2;}

    //

    public long getMyBuddyDiverNo3() {return mBuddyDiverNo3; }

    public void setMyBuddyDiverNo3(long buddyDiverNo3) {mBuddyDiverNo3 = buddyDiverNo3;}

    //

    public Double getMyBuddyBeginningPressure1() {return mMyBuddyBeginningPressure1; }

    public void setMyBuddyBeginningPressure1(Double myBuddyBeginningPressure1) {mMyBuddyBeginningPressure1 = myBuddyBeginningPressure1;}
}
