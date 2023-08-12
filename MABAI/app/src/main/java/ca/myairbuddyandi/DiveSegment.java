package ca.myairbuddyandi;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DiveSegment class
 */

public class DiveSegment {

    // Static
    private static final String LOG_TAG = "DiveSegment";

    // Public

    // Protected

    // Private
    private long mDiveNo;
    private long mDiverNo;
    private int mOrderNo;
    private int mCalcAscentRate;
    private int mCalcDescentRate;
    private Double mAirConsumptionPressure;
    private Double mAirConsumptionVolume;
    private Double mCalcAta;
    private Double mCalcAverageAta;
    private Double mCalcAverageDepth;
    private Double mCalcDecreasingPressure;
    private Double mCalcDecreasingVolume;
    private Double mDepth;
    private Double mMinute;
    private String mSegmentType;

    // End of variables

    // Public constructor
    public DiveSegment() {
    }

    // Getters and setters

    public long getDiverNo() {return mDiverNo; }

    public void setDiverNo(long diverNo) {mDiverNo = diverNo;}

    //

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {mDiveNo = diveNo;}

    //

    public int getOrderNo() {return mOrderNo; }

    public void setOrderNo(int orderNo) {
        mOrderNo = orderNo;
    }

    //

    public String getSegmentType() {return mSegmentType; }

    public void setSegmentType(String segmentTypeNo) {
        this.mSegmentType = segmentTypeNo;
    }

    //

    public Double getDepth() {return mDepth; }

    public void setDepth(Double depth) {
        mDepth = depth;
    }

    //

    public Double getMinute() {return mMinute; }

    public void setMinute(Double minute) {
        mMinute = minute;
    }

    //

    public Double getAirConsumptionPressure() {return mAirConsumptionPressure; }

    public void setAirConsumptionPressure(Double airConsumptionPressure) {mAirConsumptionPressure = airConsumptionPressure;}

    //

    public Double getAirConsumptionVolume() {return mAirConsumptionVolume; }

    public void setAirConsumptionVolume(Double airConsumptionVolume) {mAirConsumptionVolume = airConsumptionVolume;}

    //

    public Double getCalcAta() {return mCalcAta; }

    public void setCalcAta(Double calcAta) {
        mCalcAta = calcAta;
    }

    //

    public Double getCalcAverageDepth() {return mCalcAverageDepth; }

    public void setCalcAverageDepth(Double calcAverageDepth) {mCalcAverageDepth = calcAverageDepth;}

    //

    public Double getCalcAverageAta() {return mCalcAverageAta; }

    public void setCalcAverageAta(Double calcAverageAta) {mCalcAverageAta = calcAverageAta;}

    //

    public int getCalcDescentRate() {return mCalcDescentRate; }

    public void setCalcDescentRate(int calcDescentRate) {mCalcDescentRate = calcDescentRate;}

    //

    public int getCalcAscentRate() {return mCalcAscentRate; }

    public void setCalcAscentRate(int calcAscentRate) {mCalcAscentRate = calcAscentRate;}

    //

    public Double getCalcDecreasingPressure() {return mCalcDecreasingPressure; }

    public void setCalcDecreasingPressure(Double calcDecreasingPressure) {mCalcDecreasingPressure = calcDecreasingPressure;}

    //

    public Double getCalcDecreasingVolume() {return mCalcDecreasingVolume; }

    public void setCalcDecreasingVolume(Double calcDecreasingVolume) {mCalcDecreasingVolume = calcDecreasingVolume;}
}
