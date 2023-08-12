package ca.myairbuddyandi;

import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateGasActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateGas class
 */

public class CalculateGas extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateGas";

    // Public
    public CalculateGasActivityBinding mBinding = null;

    // Protected

    // Private
    private String mSource = "pressure";

    // Default
    private Double mDefaultPressure = MyConstants.ZERO_D;
    private Double mDefaultRatedPressure = MyConstants.ZERO_D;
    private Double mDefaultTankFactor = MyConstants.ZERO_D;
    private Double mDefaultTankVolume = MyConstants.ZERO_D;
    private Double mDefaultVolume = MyConstants.ZERO_D;
    // Other
    private Double mOtherPressure = MyConstants.ZERO_D;
    private Double mOtherRatedPressure = MyConstants.ZERO_D;
    private Double mOtherTankFactor = MyConstants.ZERO_D;
    private Double mOtherTankVolume = MyConstants.ZERO_D;
    private Double mOtherVolume = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateGas() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultPressure() {return mDefaultPressure; }

    public void setDefaultPressure(Double defaultPressure) {
        mDefaultPressure = defaultPressure;
    }

    //

    @Bindable
    public Double getDefaultRatedPressure() {return mDefaultRatedPressure; }

    public void setDefaultRatedPressure(Double defaultRatedPressure) { mDefaultRatedPressure = defaultRatedPressure; }

    //

    @Bindable
    public Double getDefaultTankFactor() {return mDefaultTankFactor; }

    public void setDefaultTankFactor(Double defaultTankFactor) { mDefaultTankFactor = defaultTankFactor; }

    //

    @Bindable
    public Double getDefaultTankVolume() {return mDefaultTankVolume; }

    public void setDefaultTankVolume(Double defaultTankVolume) { mDefaultTankVolume = defaultTankVolume; }

    //

    @Bindable
    public Double getDefaultVolume() {return mDefaultVolume; }

    public void setDefaultVolume(Double defaultVolume) { mDefaultVolume = defaultVolume; }

    // Other
    @Bindable
    public Double getOtherPressure() {return mOtherPressure; }

    public void setOtherPressure(Double otherPressure) {
        mOtherPressure = otherPressure;
        notifyPropertyChanged(BR.otherPressure);
    }

    //

    @Bindable
    public Double getOtherRatedPressure() {return mOtherRatedPressure; }

    public void setOtherRatedPressure(Double otherRatedPressure) {
        mOtherRatedPressure = otherRatedPressure;
    }

    //

    @Bindable
    public Double getOtherTankFactor() {return mOtherTankFactor; }

    public void setOtherTankFactor(Double otherTankFactor) {
        mOtherTankFactor = otherTankFactor;
        notifyPropertyChanged(BR.otherTankFactor);
    }

    //

    @Bindable
    public Double getOtherTankVolume() {return mOtherTankVolume; }

    public void setOtherTankVolume(Double otherTankVolume) {

        mOtherTankVolume = otherTankVolume;
    }

    //

    @Bindable
    public Double getOtherVolume() {return mOtherVolume; }

    public void setOtherVolume(Double otherVolume) {

        mOtherVolume = otherVolume;
        notifyPropertyChanged(BR.otherVolume);
    }

    // Common

    public String getSource() {return mSource;}

    @Bindable
    public TextWatcher getOnTextChangedVolume() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "volume";
            }
        };
    }

    //

    @Bindable
    public TextWatcher getOnTextChangedPressure() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "pressure";
            }
        };
    }
}