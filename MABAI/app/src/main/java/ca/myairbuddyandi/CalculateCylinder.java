package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateCylinderActivityBinding;

/**
 * Created by Michel on 2020-09-15.
 * Holds all of the logic for the CalculateCylinder class
 */

public class CalculateCylinder extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateCylinder";

    // Public
    public CalculateCylinderActivityBinding mBinding = null;

    // Protected

    // Default
    private Double mDefaultRatedPressure = MyConstants.ZERO_D;
    private Double mDefaultRatedVolume = MyConstants.ZERO_D;

    // Other
    private Double mOtherRatedPressure = MyConstants.ZERO_D;
    private Double mOtherRatedVolume = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateCylinder() {
    }

    // Getters and setters

    // Default

    @Bindable
    public Double getDefaultRatedPressure() {return mDefaultRatedPressure; }


    public void setDefaultRatedPressure(Double defaultRatedPressure) { mDefaultRatedPressure = defaultRatedPressure; }

    @Bindable
    public Double getDefaultRatedVolume() {return mDefaultRatedVolume; }

    public void setDefaultRatedVolume(Double defaultRatedVolume) { mDefaultRatedVolume = defaultRatedVolume; }

    // Other

    @Bindable
    public Double getOtherRatedPressure() {return mOtherRatedPressure; }

    public void setOtherRatedPressure(Double otherRatedPressure) {
        mOtherRatedPressure = otherRatedPressure;
    }

    @Bindable
    public Double getOtherRatedVolume() {return mOtherRatedVolume; }

    public void setOtherRatedVolume(Double otherRatedVolume) {

        mOtherRatedVolume = otherRatedVolume;
        notifyPropertyChanged(BR.otherRatedVolume);
    }
}