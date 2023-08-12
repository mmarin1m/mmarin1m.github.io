package ca.myairbuddyandi;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateEadActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateEad class
 */

public class CalculateEad extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateEad";

    // Public
    public CalculateEadActivityBinding mBinding = null;

    // Protected

    // Private

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private ArrayAdapter<String> mAdapterDefaultTrimix;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private boolean mDefaultTrimix = true; // true = Yes, false = No
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultEad = MyConstants.ZERO_D;
    private Double mDefaultMixO2 = MyConstants.ZERO_D;
    private Double mDefaultMixHe = MyConstants.ZERO_D;
    private Double mDefaultMixN2 = MyConstants.ZERO_D;
    private int mDefaultSalinityPosition;
    private int mDefaultTrimixPosition;

    // Other
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherEad = MyConstants.ZERO_D;
    private Double mOtherMixO2 = MyConstants.ZERO_D;
    private Double mOtherMixHe = MyConstants.ZERO_D;
    private Double mOtherMixN2 = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateEad() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultEad() {return mDefaultEad; }

    public void setDefaultEad(Double defaultEad) {
        mDefaultEad = defaultEad;
    }

    //

    @Bindable
    public Double getDefaultMixO2() {return mDefaultMixO2; }

    public void setDefaultMixO2(Double defaultMixO2) {
        mDefaultMixO2 = defaultMixO2;
    }

    //

    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) {
        mDefaultDepth = defaultDepth;
    }

    //

    @Bindable
    public Double getDefaultMixHe() {return mDefaultMixHe; }

    public void setDefaultMixHe(Double defaultMixHe) { mDefaultMixHe = defaultMixHe; }

    //

    @Bindable
    public Double getDefaultMixN2() {return mDefaultMixN2; }

    public void setDefaultMixN2(Double defaultN) {
        mDefaultMixN2 = defaultN;
    }

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

    // Trimix Spinner

    boolean getDefaultTrimix() {return mDefaultTrimix;}

    void setDefaultTrimix(boolean defaultTrimix) {mDefaultTrimix = defaultTrimix;}

    //

    public int getDefaultTrimixPosition() {return mDefaultTrimixPosition; }

    public void setDefaultTrimixPosition(int defaultTrimixPosition) {mDefaultTrimixPosition = defaultTrimixPosition;}

    //

    void setAdapterDefaultTrimix(ArrayAdapter<String> adapterDefaultTrimix) {mAdapterDefaultTrimix = adapterDefaultTrimix;}

    public ArrayAdapter<String> getAdapterDefaultTrimix () {return mAdapterDefaultTrimix;}

    //

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedDefaultTrimix() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    mDefaultTrimix = (position == 0);
                    setDefaultTrimix(position == 0);
                    if (getDefaultTrimix()) {
                        // Trimix is OFF
                        mBinding.TableRowMixHe.setVisibility(View.GONE);
                    } else {
                        // Trimix is ON
                        mBinding.TableRowMixHe.setVisibility(View.VISIBLE);
                    }
                    // Initialize variables to zero
                    mDefaultMixO2 = MyConstants.ZERO_D;
                    mDefaultMixHe = MyConstants.ZERO_D;
                    mDefaultMixN2 = MyConstants.ZERO_D;
                    mDefaultDepth = MyConstants.ZERO_D;
                    mDefaultEad = MyConstants.ZERO_D;
                    mBinding.defaultMixO2.setText(String.valueOf(mDefaultMixO2));
                    mBinding.defaultMixHe.setText(String.valueOf(mDefaultMixHe));
                    mBinding.defaultMixN2.setText(String.valueOf(mDefaultMixN2));
                    mBinding.defaultDepth.setText(String.valueOf(mDefaultDepth));
                    mBinding.defaultEad.setText(String.valueOf(mDefaultEad));
                    mOtherMixO2 = MyConstants.ZERO_D;
                    mOtherMixHe = MyConstants.ZERO_D;
                    mOtherMixN2 = MyConstants.ZERO_D;
                    mOtherDepth = MyConstants.ZERO_D;
                    mOtherEad = MyConstants.ZERO_D;
                    mBinding.otherMixO2.setText(String.valueOf(mOtherMixO2));
                    mBinding.otherMixHe.setText(String.valueOf(mOtherMixHe));
                    mBinding.otherMixN2.setText(String.valueOf(mOtherMixN2));
                    mBinding.otherDepth.setText(String.valueOf(mOtherDepth));
                    mBinding.otherEad.setText(String.valueOf(mOtherEad));
                }
            }
        };
    }

    // Other
    @Bindable
    public Double getOtherEad() {return mOtherEad; }

    public void setOtherEad(Double otherEad) {
        mOtherEad = otherEad;
        notifyPropertyChanged(BR.otherEad);
    }

    //

    @Bindable
    public Double getOtherMixO2() {return mOtherMixO2; }

    public void setOtherMixO2(Double otherMixO2) {
        mOtherMixO2 = otherMixO2;
        notifyPropertyChanged(BR.otherMixO2);
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
    public Double getOtherMixHe() {return mOtherMixHe; }

    public void setOtherMixHe(Double otherMixHe) {
        mOtherMixHe = otherMixHe;
        notifyPropertyChanged(BR.otherMixHe);
    }

    //

    @Bindable
    public Double getOtherMixN2() {return mOtherMixN2; }

    public void setOtherMixN2(Double otherMixN2) {
        mOtherMixN2 = otherMixN2;
        notifyPropertyChanged(BR.otherMixN2);
    }

    // Common

}