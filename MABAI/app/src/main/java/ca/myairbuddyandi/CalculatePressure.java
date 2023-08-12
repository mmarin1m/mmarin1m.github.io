package ca.myairbuddyandi;

import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculatePressureActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculatePressure class
 */

public class CalculatePressure extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculatePressure";

    // Public
    public CalculatePressureActivityBinding mBinding = null;

    // Protected
    private String mSource = MainApplication.getContext().getResources().getString(R.string.button_depth); // Uses Depth as the arbitrary start of the calculation

    // Private

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private Double mDefaultAta = MyConstants.ZERO_D;
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultPsi = MyConstants.ZERO_D;
    private Double mDefaultPsia = MyConstants.ZERO_D;
    private Double mDefaultPsig = MyConstants.ZERO_D;
    private int mDefaultSalinityPosition;

    // Other
    private Double mOtherAta = MyConstants.ZERO_D;
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherPsi = MyConstants.ZERO_D;
    private Double mOtherPsia = MyConstants.ZERO_D;
    private Double mOtherPsig = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculatePressure() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultAta() {return mDefaultAta; }

    public void setDefaultAta(Double defaultAta) {
        mDefaultAta = defaultAta;
    }

    //

    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) {
        mDefaultDepth = defaultDepth;
    }

    //

    @Bindable
    public Double getDefaultPsi() {return mDefaultPsi; }

    public void setDefaultPsi(Double defaultPsi) {
        mDefaultPsi = defaultPsi;
    }

    //

    @Bindable
    public Double getDefaultPsia() {return mDefaultPsia; }

    public void setDefaultPsia(Double defaultPsia) {
        mDefaultPsia = defaultPsia;
    }

    //

    @Bindable
    public Double getDefaultPsig() {return mDefaultPsig; }

    public void setDefaultPsig(Double defaultPsig) {
        mDefaultPsig = defaultPsig;
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
    public Double getOtherPsi() {return mOtherPsi; }

    public void setOtherPsi(Double otherPsi) {
        mOtherPsi = otherPsi;
        notifyPropertyChanged(BR.otherPsi);
    }

    //

    @Bindable
    public Double getOtherPsia() {return mOtherPsia; }

    public void setOtherPsia(Double otherPsia) {
        mOtherPsia = otherPsia;
        notifyPropertyChanged(BR.otherPsia);
    }

    //

    @Bindable
    public Double getOtherPsig() {return mOtherPsig; }

    public void setOtherPsig(Double otherPsig) {
        mOtherPsig = otherPsig;
        notifyPropertyChanged(BR.otherPsig);
    }

    // Common

    public String getSource() {return mSource;}

    @Bindable
    public TextWatcher getOnTextChangedDepth() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = MainApplication.getContext().getResources().getString(R.string.button_depth);
            }
        };
    }

    //

    @Bindable
    public TextWatcher getOnTextChangedAta() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = MainApplication.getContext().getResources().getString(R.string.button_ata);
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedPsi() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = MainApplication.getContext().getResources().getString(R.string.button_psi);
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedPsia() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = MainApplication.getContext().getResources().getString(R.string.button_psia);
            }
        };
    }

    @Bindable
    public TextWatcher getOnTextChangedPsig() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mSource = MainApplication.getContext().getResources().getString(R.string.button_psig);
            }
        };
    }
}