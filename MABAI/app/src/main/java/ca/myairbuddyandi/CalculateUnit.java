package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateUnitActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateUnit class
 */

public class CalculateUnit extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateUnit";

    // Public

    // Protected

    // Private

    // Default
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultPressure = MyConstants.ZERO_D;
    private Double mDefaultTemperature = MyConstants.ZERO_D;
    // 2020/03/27 New unit conversion
    private Double mDefaultVolume = MyConstants.ZERO_D;
    private Double mDefaultWeight = MyConstants.ZERO_D;
    private Double mDefaultAta = MyConstants.ZERO_D;

    // Other
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherPressure = MyConstants.ZERO_D;
    private Double mOtherTemperature = MyConstants.ZERO_D;
    // 2020/03/27 New unit conversion
    private Double mOtherVolume = MyConstants.ZERO_D;
    private Double mOtherWeight = MyConstants.ZERO_D;
    private Double mOtherAta = MyConstants.ZERO_D;

    public CalculateUnitActivityBinding mBinding = null;

    // End of variables

    // Public constructor
    public CalculateUnit() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultAta() {return mDefaultAta; }

    public void setDefaultAta(Double defaultAta) { mDefaultAta = defaultAta; }

    //

    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) { mDefaultDepth = defaultDepth; }

    //

    @Bindable
    public Double getDefaultPressure() {return mDefaultPressure; }

    public void setDefaultPressure(Double defaultPressure) { mDefaultPressure = defaultPressure; }

    //

    @Bindable
    public Double getDefaultTemperature() {return mDefaultTemperature; }

    public void setDefaultTemperature(Double defaultTemperature) { mDefaultTemperature = defaultTemperature; }

    //

    @Bindable
    public Double getDefaultVolume() {return mDefaultVolume; }

    public void setDefaultVolume(Double defaultVolume) { mDefaultVolume = defaultVolume; }

    //

    @Bindable
    public Double getDefaultWeight() {return mDefaultWeight; }

    public void setDefaultWeight(Double defaultWeight) { mDefaultWeight = defaultWeight; }

    // Other

    @Bindable
    public Double getOtherAta() {return mOtherAta; }

    public void setOtherAta(Double otherAta) {
        mOtherAta = otherAta;
        notifyPropertyChanged(BR.otherAta);
    }

    //

    @Bindable
    public Double getOtherDepth() {return mOtherDepth; }

    public void setOtherDepth(Double otherDepth) {
        mOtherDepth = otherDepth;
        notifyPropertyChanged(BR.otherDepth);
    }

    //

    @Bindable
    public Double getOtherPressure() {return mOtherPressure; }

    public void setOtherPressure(Double otherPressure) {
        mOtherPressure = otherPressure;
        notifyPropertyChanged(BR.otherPressure);
    }

    //

    @Bindable
    public Double getOtherTemperature() {return mOtherTemperature; }

    public void setOtherTemperature(Double otherTemperature) {
        mOtherTemperature = otherTemperature;
        notifyPropertyChanged(BR.otherTemperature);
    }

    //

    @Bindable
    public Double getOtherVolume() {return mOtherVolume; }

    public void setOtherVolume(Double otherVolume) {
        mOtherVolume = otherVolume;
        notifyPropertyChanged(BR.otherVolume);
    }

    //

    @Bindable
    public Double getOtherWeight() {return mOtherWeight; }

    public void setOtherWeight(Double otherWeight) {
        mOtherWeight = otherWeight;
        notifyPropertyChanged(BR.otherWeight);
    }
}
