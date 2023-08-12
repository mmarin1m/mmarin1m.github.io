package ca.myairbuddyandi;

/**
 * Created by Michel on 2016-11-29.
 * Holds all of the logic for the DivePlan class
 */

public class CalcAverageDepth {

    // Static

    // Public

    // Protected

    // Private
    private long mMilliSecond = 0;
    private Double mDepth;

    // End of variables

    // Public constructor
    public CalcAverageDepth() {
    }

    // Getters and setters

    long getMilliSecond() {return mMilliSecond; }

    void setMilliSecond(long milliSecond) {
        mMilliSecond = milliSecond;
    }

    public Double getDepth() {return mDepth; }

    public void setDepth(Double Depth) {mDepth = Depth;}
}
