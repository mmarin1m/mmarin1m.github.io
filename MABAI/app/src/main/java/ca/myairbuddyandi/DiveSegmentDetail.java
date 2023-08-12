package ca.myairbuddyandi;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DiveSegmentDetail class
 */

public class DiveSegmentDetail {

    // Static
    private static final String LOG_TAG = "DiveSegmentDetail";

    // Public

    // Protected

    // Private
    private Double mMaxDepth;
    private Double mBeginningPressure;
    private Double mTurnaroundPressure;
    private Double mEndingPressure;
    private Double mRunningTotalPressure;
    private Double mRunningTotalTime;
    private Double mRunningTotalVolume;
    private Double mRunningDescentPressure;
    private Double mRunningDescentTime;
    private Double mRunningDescentVolume;
    private Double mRunningAscentPressure;
    private Double mRunningAscentTime;
    private Double mRunningAscentVolume;

    // End of variables

    // Public constructor
    public DiveSegmentDetail() {
    }

    // Getters and setters

    public Double getMaxDepth() {return mMaxDepth; }

    public void setMaxDepth(Double maxDepth) {mMaxDepth = maxDepth;}

    public Double getBeginningPressure() {return mBeginningPressure; }

    public void setBeginningPressure(Double beginningPressure) {mBeginningPressure = beginningPressure;}

    public Double getTurnaroundPressure() {return mTurnaroundPressure; }

    public void setTurnaroundPressure(Double turnaroundPressure) {mTurnaroundPressure = turnaroundPressure;}

    public Double getEndingPressure() {return mEndingPressure; }

    public void setEndingPressure(Double endingPressure) {mEndingPressure = endingPressure;}

    public Double getRunningTotalPressure() {return mRunningTotalPressure; }

    public void setRunningTotalPressure(Double runningTotalPressure) {mRunningTotalPressure = runningTotalPressure;}

    public Double getRunningTotalTime() {return mRunningTotalTime; }

    public void setRunningTotalTime(Double runningTotalTime) {mRunningTotalTime = runningTotalTime;}

    public Double getRunningTotalVolume() {return mRunningTotalVolume; }

    public void setRunningTotalVolume(Double runningTotalVolume) {mRunningTotalVolume = runningTotalVolume;}

    public Double getRunningDescentPressure() {return mRunningDescentPressure; }

    public void setRunningDescentPressure(Double runningDescentPressure) {mRunningDescentPressure = runningDescentPressure;}

    public Double getRunningDescentTime() {return mRunningDescentTime; }

    public void setRunningDescentTime(Double runningDescentTime) {mRunningDescentTime = runningDescentTime;}

    public Double getRunningDescentVolume() {return mRunningDescentVolume; }

    public void setRunningDescentVolume(Double runningDescentVolume) {mRunningDescentVolume = runningDescentVolume;}

    public Double getRunningAscentPressure() {return mRunningAscentPressure; }

    public void setRunningAscentPressure(Double runningAscentPressure) {mRunningAscentPressure = runningAscentPressure;}

    public Double getRunningAscentTime() {return mRunningAscentTime; }

    public void setRunningAscentTime(Double runningAscentTime) {mRunningAscentTime = runningAscentTime;}

    public Double getRunningAscentVolume() {return mRunningAscentVolume; }

    public void setRunningAscentVolume(Double runningAscentVolume) {mRunningAscentVolume = runningAscentVolume;}
}
