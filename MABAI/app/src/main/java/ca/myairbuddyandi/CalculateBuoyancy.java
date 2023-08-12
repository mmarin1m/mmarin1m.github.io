package ca.myairbuddyandi;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateBuoyancyActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateBuoyancy class
 */

public class CalculateBuoyancy extends BaseObservable {
    // Static
    private static final String LOG_TAG = "CalculateBuoyancy";

    // Public
    public CalculateBuoyancyActivityBinding mBinding = null;

    // Protected

    // Private

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private Double mDefaultWeight = MyConstants.ZERO_D;
    private Double mDefaultDisplacement = MyConstants.ZERO_D;
    private Double mDefaultBuoyancy = MyConstants.ZERO_D;

    private int mDefaultSalinityPosition;

    // Other
    private Double mOtherWeight = MyConstants.ZERO_D;
    private Double mOtherDisplacement = MyConstants.ZERO_D;
    private Double mOtherBuoyancy = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateBuoyancy() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultWeight() {return mDefaultWeight; }

    public void setDefaultWeight(Double defaultWeight) {
        mDefaultWeight = defaultWeight;
    }

    @Bindable
    public Double getDefaultDisplacement() {return mDefaultDisplacement; }

    public void setDefaultDisplacement(Double defaultDisplacement) { mDefaultDisplacement = defaultDisplacement; }

    @Bindable
    public Double getDefaultBuoyancy() {return mDefaultBuoyancy; }

    public void setDefaultBuoyancy(Double defaultBuoyancyNeeded) { mDefaultBuoyancy = defaultBuoyancyNeeded; }

    // Salinity Spinner

    boolean getDefaultSalinity() {return mDefaultSalinity;}

    void setDefaultSalinity(boolean defaultSalinity) {mDefaultSalinity = defaultSalinity;}

    public int getDefaultSalinityPosition() {return mDefaultSalinityPosition; }

    public void setDefaultSalinityPosition(int defaultSalinityPosition) {mDefaultSalinityPosition = defaultSalinityPosition;}

    void setAdapterDefaultSalinity(ArrayAdapter<String> adapterDefaultSalinity) {mAdapterDefaultSalinity = adapterDefaultSalinity;}

    public ArrayAdapter<String> getAdapterDefaultSalinity () {return mAdapterDefaultSalinity;}

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedDefaultSalinity() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    mDefaultSalinity = (position == 0);
                }
            }
        };
    }

    // Other
    @Bindable
    public Double getOtherWeight() {return mOtherWeight; }

    public void setOtherWeight(Double otherWeight) {
        mOtherWeight = otherWeight;
        notifyPropertyChanged(BR.otherWeight);
    }

    @Bindable
    public Double getOtherDisplacement() {return mOtherDisplacement; }

    public void setOtherDisplacement(Double otherDisplacement) {
        mOtherDisplacement = otherDisplacement;
        notifyPropertyChanged(BR.otherDisplacement);
    }

    @Bindable
    public Double getOtherBuoyancy() {return mOtherBuoyancy; }

    public void setOtherBuoyancy(Double otherBuoyancy) {
        mOtherBuoyancy = otherBuoyancy;
        notifyPropertyChanged(BR.otherBuoyancy);
    }

    // Common

}