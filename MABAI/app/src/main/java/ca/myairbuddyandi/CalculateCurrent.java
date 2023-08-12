package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateCurrentActivityBinding;

/**
 * Created by Michel on 2020-09-11.
 * Holds all of the logic for the CalculateCurrent class
 */

public class CalculateCurrent extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateCurrent";

    // Public
    public CalculateCurrentActivityBinding mBinding = null;

    // Protected

    // Private

    // Default
    private Double mDefaultDistance = MyConstants.ZERO_D;
    private Double mDefaultSpeedKnot = MyConstants.ZERO_D;
    private Double mDefaultSpeedMph = MyConstants.ZERO_D;
    private Double mDefaultTime = MyConstants.ZERO_D;

    // Other
    private Double mOtherDistance = MyConstants.ZERO_D;
    private Double mOtherSpeedKnot = MyConstants.ZERO_D;
    private Double mOtherSpeedKph = MyConstants.ZERO_D;
    private Double mOtherTime = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateCurrent() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultDistance() {return mDefaultDistance; }

    public void setDefaultDistance(Double defaultDistance) {
        mDefaultDistance = defaultDistance;
    }

    //

    @Bindable
    public Double getDefaultSpeedKnot() {return mDefaultSpeedKnot; }

    public void setDefaultSpeedKnot(Double defaultSpeedKnot) { mDefaultSpeedKnot = defaultSpeedKnot; }

    //

    @Bindable
    public Double getDefaultSpeedMph() {return mDefaultSpeedMph; }

    public void setDefaultSpeedMph(Double defaultSpeedMph) { mDefaultSpeedMph = defaultSpeedMph; }

    //

    @Bindable
    public Double getDefaultTime() {return mDefaultTime; }

    public void setDefaultTime(Double defaultTime) { mDefaultTime = defaultTime; }

    // Other
    @Bindable
    public Double getOtherDistance() {return mOtherDistance; }

    public void setOtherDistance(Double otherDistance) {
        mOtherDistance = otherDistance;
        notifyPropertyChanged(BR.otherDistance);
    }

    //

    @Bindable
    public Double getOtherSpeedKnot() {return mOtherSpeedKnot; }

    public void setOtherSpeedKnot(Double otherSpeedKnot) {
        mOtherSpeedKnot = otherSpeedKnot;
        notifyPropertyChanged(BR.otherSpeedKnot);
    }

    //

    @Bindable
    public Double getOtherSpeedKph() {return mOtherSpeedKph; }

    public void setOtherSpeedKph(Double otherSpeedKph) {
        mOtherSpeedKph = otherSpeedKph;
        notifyPropertyChanged(BR.otherSpeedKph);
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