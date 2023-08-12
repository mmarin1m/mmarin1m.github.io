package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateDescentAscentActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateDescentAscent class
 */

public class CalculateDescentAscent extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateDescentAscent";

    // Public
    public CalculateDescentAscentActivityBinding mBinding = null;

    // Protected

    // Default
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultRate = MyConstants.ZERO_D;
    private Double mDefaultTime = MyConstants.ZERO_D;

    // Other
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherRate = MyConstants.ZERO_D;
    private Double mOtherTime = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateDescentAscent() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) {
        mDefaultDepth = defaultDepth;
    }

    //

    @Bindable
    public Double getDefaultRate() {return mDefaultRate; }

    public void setDefaultRate(Double defaultRate) { mDefaultRate = defaultRate; }

    //

    @Bindable
    public Double getDefaultTime() {return mDefaultTime; }

    public void setDefaultTime(Double defaultTime) { mDefaultTime = defaultTime; }

    //

    // Other
    @Bindable
    public Double getOtherDepth() {return mOtherDepth; }

    public void setOtherDepth(Double otherDepth) {
        mOtherDepth = otherDepth;
        notifyPropertyChanged(BR.otherDepth);
    }

    //

    @Bindable
    public Double getOtherRate() {return mOtherRate; }

    public void setOtherRate(Double otherRate) {
        mOtherRate = otherRate;
        notifyPropertyChanged(BR.otherRate);
    }

    //

    @Bindable
    public Double getOtherTime() {return mOtherTime; }

    public void setOtherTime(Double otherTime) {
        mOtherTime = otherTime;
        notifyPropertyChanged(BR.otherTime);
    }

    // Common

}