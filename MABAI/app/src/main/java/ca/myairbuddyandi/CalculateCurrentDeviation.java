package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateCurrentDeviationActivityBinding;

/**
 * Created by Michel on 2022-01-01.
 * Holds all of the logic for the CalculateCurrentDeviation class
 */

public class CalculateCurrentDeviation extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateCurrent";

    // Public
    public CalculateCurrentDeviationActivityBinding mBinding = null;

    // Protected

    // Private

    // Default
    private Double mDefaultDistance = MyConstants.ZERO_D;
    private Double mDefaultCurrentDeviation = MyConstants.ZERO_D;
    private Double mDefaultCurrentSpeedKnot = MyConstants.ZERO_D;
    private Double mDefaultCurrentSpeedMph = MyConstants.ZERO_D;
    private Double mDefaultSwimSpeed = MyConstants.ZERO_D;
    private Double mDefaultSwimTime = MyConstants.ZERO_D;

    // Other

    private Double mOtherDistance = MyConstants.ZERO_D;
    private Double mOtherCurrentDeviation = MyConstants.ZERO_D;
    private Double mOtherCurrentSpeedKnot = MyConstants.ZERO_D;
    private Double mOtherCurrentSpeedKph = MyConstants.ZERO_D;
    private Double mOtherSwimSpeed = MyConstants.ZERO_D;
    private Double mOtherSwimTime = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateCurrentDeviation() {
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
    public Double getDefaultCurrentDeviation() {return mDefaultCurrentDeviation; }

    public void setDefaultCurrentDeviation(Double defaultCurrentDeviation) { mDefaultCurrentDeviation = defaultCurrentDeviation; }

    //

    @Bindable
    public Double getDefaultCurrentSpeedKnot() {return mDefaultCurrentSpeedKnot; }

    public void setDefaultCurrentSpeedKnot(Double defaultSpeedKnot) { mDefaultCurrentSpeedKnot = defaultSpeedKnot; }

    //

    // NOTE: Reserved for future used
    @Bindable
    public Double getDefaultCurrentSpeedMph() {return mDefaultCurrentSpeedMph; }

    // NOTE: Reserved for future used
    public void setDefaultCurrentSpeedMph(Double defaultSpeedMph) { mDefaultCurrentSpeedMph = defaultSpeedMph; }

    //

    @Bindable
    public Double getDefaultSwimSpeed() {return mDefaultSwimSpeed; }

    public void setDefaultSwimSpeed(Double defaultSwimSpeed) { mDefaultSwimSpeed = defaultSwimSpeed; }

    //

    @Bindable
    public Double getDefaultSwimTime() {return mDefaultSwimTime; }

    public void setDefaultSwimTime(Double defaultSwimTime) { mDefaultSwimTime = defaultSwimTime; }

    // Other
    @Bindable
    public Double getOtherDistance() {return mOtherDistance; }

    public void setOtherDistance(Double otherDistance) {
        mOtherDistance = otherDistance;
        notifyPropertyChanged(BR.otherDistance);
    }

    //

    @Bindable
    public Double getOtherCurrentDeviation() {return mOtherCurrentDeviation; }

    public void setOtherCurrentDeviation(Double otherCurrentDeviation) {
        mOtherCurrentDeviation = otherCurrentDeviation;
        notifyPropertyChanged(BR.otherCurrentDeviation);
    }

    //

    @Bindable
    public Double getOtherCurrentSpeedKnot() {return mOtherCurrentSpeedKnot; }

    public void setOtherCurrentSpeedKnot(Double otherSpeedKnot) {
        mOtherCurrentSpeedKnot = otherSpeedKnot;
        notifyPropertyChanged(BR.otherSpeedKnot);
    }

    //

    // NOTE: Reserved for future used
    @Bindable
    public Double getOtherCurrentSpeedKph() {return mOtherCurrentSpeedKph; }

    // NOTE: Reserved for future used
    public void setOtherCurrentSpeedKph(Double otherSpeedKph) {
        mOtherCurrentSpeedKph = otherSpeedKph;
        notifyPropertyChanged(BR.otherSpeedKph);
    }

    //

    @Bindable
    public Double getOtherSwimSpeed() {return mOtherSwimSpeed; }

    public void setOtherSwimSpeed(Double otherSwimSpeed) {
        mOtherSwimSpeed = otherSwimSpeed;
        notifyPropertyChanged(BR.otherSwimSpeed);
    }

    //

    @Bindable
    public Double getOtherSwimTime() {return mOtherSwimTime; }

    public void setOtherSwimTime(Double otherSwimTime) {
        mOtherSwimTime = otherSwimTime;
        notifyPropertyChanged(BR.otherSwimTime);
    }

    // Common

}