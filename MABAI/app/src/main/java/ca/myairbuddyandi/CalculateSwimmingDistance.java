package ca.myairbuddyandi;

import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateSwimmingDistanceActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateSwimmingDistance class
 */

public class CalculateSwimmingDistance extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateSwimmingDistance";

    // Public
    public CalculateSwimmingDistanceActivityBinding mBinding = null;

    // Protected

    // Private
    private String mSource = "distance";

    // Default
    private Double mDefaultDistance = MyConstants.ZERO_D;
    private Double mDefaultDistancePerKick = MyConstants.ZERO_D;
    private Double mDefaultKicks = MyConstants.ZERO_D;
    private Double mDefaultSpeedMin = MyConstants.ZERO_D;
    private Double mDefaultSpeedSec = MyConstants.ZERO_D;
    private Double mDefaultTime = MyConstants.ZERO_D;

    // Other
    private Double mOtherDistance = MyConstants.ZERO_D;
    private Double mOtherDistancePerKick = MyConstants.ZERO_D;
    private Double mOtherKicks = MyConstants.ZERO_D;
    private Double mOtherSpeedMin = MyConstants.ZERO_D;
    private Double mOtherSpeedSec = MyConstants.ZERO_D;
    private Double mOtherTime = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateSwimmingDistance() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultDistance() {return mDefaultDistance; }

    public void setDefaultDistance(Double defaultDistance) { mDefaultDistance = defaultDistance; }

    //

    @Bindable
    public Double getDefaultDistancePerKick() {return mDefaultDistancePerKick; }

    public void setDefaultDistancePerKick(Double defaultDistancePerKick) { mDefaultDistancePerKick = defaultDistancePerKick; }

    //

    @Bindable
    public Double getDefaultKicks() {return mDefaultKicks; }

    public void setDefaultKicks(Double defaultKicks) { mDefaultKicks = defaultKicks; }

    //

    @Bindable
    public Double getDefaultSpeedMin() {return mDefaultSpeedMin; }

    public void setDefaultSpeedMin(Double defaultSpeedMin) { mDefaultSpeedMin = defaultSpeedMin; }

    //

    @Bindable
    public Double getDefaultSpeedSec() {return mDefaultSpeedSec; }

    public void setDefaultSpeedSec(Double defaultSpeedSec) { mDefaultSpeedSec = defaultSpeedSec; }

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
    public Double getOtherDistancePerKick() {return mOtherDistancePerKick; }

    public void setOtherDistancePerKick(Double otherDistancePerKick) {
        mOtherDistancePerKick = otherDistancePerKick;
        notifyPropertyChanged(BR.otherDistancePerKick);
    }

    //

    @Bindable
    public Double getOtherKicks() {return mOtherKicks; }

    public void setOtherKicks(Double otherKicks) {
        mOtherKicks = otherKicks;
        notifyPropertyChanged(BR.otherKicks);
    }

    //

    @Bindable
    public Double getOtherSpeedMin() {return mOtherSpeedMin; }

    public void setOtherSpeedMin(Double otherSpeedMin) {
        mOtherSpeedMin = otherSpeedMin;
        notifyPropertyChanged(BR.otherSpeedMin);
    }

    //

    @Bindable
    public Double getOtherSpeedSec() {return mOtherSpeedSec; }

    public void setOtherSpeedSec(Double otherSpeedSec) {
        mOtherSpeedSec = otherSpeedSec;
        notifyPropertyChanged(BR.otherSpeedSec);
    }

    //

    @Bindable
    public Double getOtherTime() {return mOtherTime; }

    public void setOtherTime(Double otherTime) {
        mOtherTime = otherTime;
        notifyPropertyChanged(BR.otherTime);
    }

    // Common

    public String getSource() {return mSource;}

    @Bindable
    public TextWatcher getOnTextChangedDistance() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "distance";
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedDistancePerKick() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "distanceperkick";
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedKicks() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "kicks";
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedSpeedMin() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "speedmin";
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedTime() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = "time";
            }
        };
    }

}