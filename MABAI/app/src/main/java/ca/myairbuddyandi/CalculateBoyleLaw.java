package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateBoyleLawActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateBoyleLaw class
 */

public class CalculateBoyleLaw extends BaseObservable {
    // Static

    private static final String LOG_TAG = "CalculateBoyleLaw";

    // Public
    public CalculateBoyleLawActivityBinding mBinding = null;

    // Protected

    // Private

    // Default
    private Double mDefaultP1 = MyConstants.ZERO_D;
    private Double mDefaultV1 = MyConstants.ZERO_D;
    private Double mDefaultP2 = MyConstants.ZERO_D;
    private Double mDefaultV2 = MyConstants.ZERO_D;
    // Other
    private Double mOtherP1 = MyConstants.ZERO_D;
    private Double mOtherV1 = MyConstants.ZERO_D;
    private Double mOtherP2 = MyConstants.ZERO_D;
    private Double mOtherV2 = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateBoyleLaw() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultP1() {return mDefaultP1; }

    public void setDefaultP1(Double defaultP1) { mDefaultP1 = defaultP1; }

    @Bindable
    public Double getDefaultV1() {return mDefaultV1; }

    public void setDefaultV1(Double defaultV1) { mDefaultV1 = defaultV1; }

    @Bindable
    public Double getDefaultP2() {return mDefaultP2; }

    public void setDefaultP2(Double defaultP2) { mDefaultP2 = defaultP2; }

    @Bindable
    public Double getDefaultV2() {return mDefaultV2; }

    public void setDefaultV2(Double defaultV2) { mDefaultV2 = defaultV2; }

    // Other
    @Bindable
    public Double getOtherP1() {return mOtherP1; }

    public void setOtherP1(Double otherP1) {
        mOtherP1 = otherP1;
        notifyPropertyChanged(BR.otherP1);
    }

    @Bindable
    public Double getOtherV1() {return mOtherV1; }

    public void setOtherV1(Double otherV1) {
        mOtherV1 = otherV1;
        notifyPropertyChanged(BR.otherV1);
    }

    @Bindable
    public Double getOtherP2() {return mOtherP2; }

    @Bindable
    public Double getOtherV2() {return mOtherV2; }

    public void setOtherP2(Double otherP2) {
        mOtherP2 = otherP2;
        notifyPropertyChanged(BR.otherP2);
    }

    public void setOtherV2(Double otherV2) {
        mOtherV2 = otherV2;
        notifyPropertyChanged(BR.otherV2);
    }

    // Common

}