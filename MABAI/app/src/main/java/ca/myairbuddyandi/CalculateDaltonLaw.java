package ca.myairbuddyandi;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateDaltonLawActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateDaltonLaw class
 */

public class CalculateDaltonLaw extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateDaltonLaw";

    // Public
    public CalculateDaltonLawActivityBinding mBinding = null;

    // Protected

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private ArrayAdapter<String> mAdapterDefaultTrimix;
    private ArrayAdapter<EndType> mAdapterDefaultOptimizeEnd;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private boolean mDefaultTrimix = true; // true = Yes, false = No
    private Double mDefaultPartialPressure = MyConstants.DEFAULT_PARTIAL_PRESSURE;
    private Double mDefaultBestMixHe = MyConstants.DEFAULT_BEST_MIX_HE;
    private Double mDefaultBestMixN2 = MyConstants.DEFAULT_BEST_MIX_N2;
    private Double mDefaultBestMixO2 = MyConstants.DEFAULT_BEST_MIX_O2;
    private Double mDefaultEabd = MyConstants.ZERO_D;
    private Double mDefaultEnd = MyConstants.ZERO_D;
    private Double mDefaultEndN2Narc = MyConstants.ZERO_D;
    private Double mDefaultEndN2O2Narc = MyConstants.ZERO_D;
    private Double mDefaultEndN2O2HeNarc = MyConstants.ZERO_D;
    private Double mDefaultMod = MyConstants.ZERO_D;
    private int mDefaultSalinityPosition;
    private int mDefaultTrimixPosition;
    private int mDefaultOptimizeEndPosition;
    private String mDefaultOptimizeEndType = MyConstants.ENDN2;

    // Other
    private Double mOtherPartialPressure = MyConstants.DEFAULT_PARTIAL_PRESSURE;
    private Double mOtherBestMixHe = MyConstants.DEFAULT_BEST_MIX_HE;
    private Double mOtherBestMixN2 = MyConstants.DEFAULT_BEST_MIX_N2;
    private Double mOtherBestMixO2 = MyConstants.DEFAULT_BEST_MIX_O2;
    private Double mOtherEabd = MyConstants.ZERO_D;
    private Double mOtherEnd = MyConstants.ZERO_D;
    private Double mOtherEndN2Narc = MyConstants.ZERO_D;
    private Double mOtherEndN2O2Narc = MyConstants.ZERO_D;
    private Double mOtherEndN2O2HeNarc = MyConstants.ZERO_D;
    private Double mOtherMod = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateDaltonLaw() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultPartialPressure() {return mDefaultPartialPressure; }

    public void setDefaultPartialPressure(Double defaultPartialPressure) { mDefaultPartialPressure = defaultPartialPressure; }

    //

    @Bindable
    public Double getDefaultBestMixHe() {return mDefaultBestMixHe; }

    public void setDefaultBestMixHe(Double defaultBestMixHe) { mDefaultBestMixHe = defaultBestMixHe; }

    //

    @Bindable
    public Double getDefaultBestMixN2() {return mDefaultBestMixN2; }

    public void setDefaultBestMixN2(Double defaultBestMixN2) { mDefaultBestMixN2 = defaultBestMixN2; }

    //

    @Bindable
    public Double getDefaultBestMixO2() {return mDefaultBestMixO2; }

    public void setDefaultBestMixO2(Double defaultBestMixO2) { mDefaultBestMixO2 = defaultBestMixO2; }

    //

    @Bindable
    public Double getDefaultMod() {return mDefaultMod; }

    public void setDefaultMod(Double defaultMod) { mDefaultMod = defaultMod; }

    //

    @Bindable
    public Double getDefaultEabd() {return mDefaultEabd; }

    public void setDefaultEabd(Double defaultEabd) { mDefaultEabd = defaultEabd; }

    //

    @Bindable
    public Double getDefaultEnd() {return mDefaultEnd; }

    public void setDefaultEnd(Double defaultEnd) { mDefaultEnd = defaultEnd; }

    //

    @Bindable
    public Double getDefaultEndN2Narc() {return mDefaultEndN2Narc; }

    public void setDefaultEndN2Narc(Double defaultEndN2Narc) { mDefaultEndN2Narc = defaultEndN2Narc; }

    //

    @Bindable
    public Double getDefaultEndN2O2Narc() {return mDefaultEndN2O2Narc; }

    public void setDefaultEndN2O2Narc(Double defaultEndN2O2Narc) { mDefaultEndN2O2Narc = defaultEndN2O2Narc; }

    //

    @Bindable
    public Double getDefaultEndN2O2HeNarc() {return mDefaultEndN2O2HeNarc; }

    public void setDefaultEndN2O2HeNarc(Double defaultEndN2O2HeNarc) { mDefaultEndN2O2HeNarc = defaultEndN2O2HeNarc; }

    // Salinity Spinner

    boolean getDefaultSalinity() {return mDefaultSalinity;}

    void setDefaultSalinity(boolean defaultSalinity) {mDefaultSalinity = defaultSalinity;}

    //

    public int getDefaultSalinityPosition() {return mDefaultSalinityPosition; }

    public void setDefaultSalinityPosition(int defaultSalinityPosition) {mDefaultSalinityPosition = defaultSalinityPosition;}

    //

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
                    setDefaultTrimix(position == 0);
                    if (getDefaultTrimix()) {
                        // Trimix is OFF
                        mBinding.TableRowBestMixHe.setVisibility(View.GONE);
                        mBinding.TableRowEnd.setVisibility(View.GONE);
                        mBinding.TableRowOptimizeEnd.setVisibility(View.GONE);
                        mBinding.TableRowEndN2Narc.setVisibility(View.GONE);
                        mBinding.TableRowEndN2O2Narc.setVisibility(View.GONE);
                        mBinding.TableRowEndN2O2HeNarc.setVisibility(View.GONE);
                        mBinding.TableRowEabd.setVisibility(View.GONE);
                        mBinding.calculateEndButton.setVisibility(View.GONE);
                        mBinding.calculateEndButton.setEnabled(false);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                2.0f
                        );
                        mBinding.clearButton.setLayoutParams(param);
                        // Initialize variables to Nitrox values
                        mDefaultBestMixO2 = MyConstants.DEFAULT_BEST_MIX_O2;
                        mDefaultBestMixHe = MyConstants.DEFAULT_BEST_MIX_HE;
                        mDefaultBestMixN2 = MyConstants.DEFAULT_BEST_MIX_N2;
                        mBinding.defaultBestMixO2.setText(String.valueOf(mDefaultBestMixO2));
                        mBinding.defaultBestMixHe.setText(String.valueOf(mDefaultBestMixHe));
                        mBinding.defaultBestMixN2.setText(String.valueOf(mDefaultBestMixN2));
                        mOtherBestMixO2 = MyConstants.DEFAULT_BEST_MIX_O2;
                        mOtherBestMixHe = MyConstants.DEFAULT_BEST_MIX_HE;
                        mOtherBestMixN2 = MyConstants.DEFAULT_BEST_MIX_N2;
                    } else {
                        // Trimix is ON
                        mBinding.TableRowBestMixHe.setVisibility(View.VISIBLE);
                        mBinding.TableRowEnd.setVisibility(View.VISIBLE);
                        mBinding.TableRowOptimizeEnd.setVisibility(View.VISIBLE);
                        mBinding.TableRowEndN2Narc.setVisibility(View.VISIBLE);
                        mBinding.TableRowEndN2O2Narc.setVisibility(View.VISIBLE);
                        mBinding.TableRowEndN2O2HeNarc.setVisibility(View.VISIBLE);
                        mBinding.TableRowEabd.setVisibility(View.VISIBLE);
                        mBinding.calculateEndButton.setVisibility(View.VISIBLE);
                        mBinding.calculateEndButton.setEnabled(true);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1.0f
                        );
                        mBinding.clearButton.setLayoutParams(param);
                        // Initialize variables to zero
                        mDefaultBestMixO2 = MyConstants.ZERO_D;
                        mDefaultBestMixHe = MyConstants.ZERO_D;
                        mDefaultBestMixN2 = MyConstants.ZERO_D;
                        mBinding.defaultBestMixO2.setText(String.valueOf(mDefaultBestMixO2));
                        mBinding.defaultBestMixHe.setText(String.valueOf(mDefaultBestMixHe));
                        mBinding.defaultBestMixN2.setText(String.valueOf(mDefaultBestMixN2));
                        mOtherBestMixO2 = MyConstants.ZERO_D;
                        mOtherBestMixHe = MyConstants.ZERO_D;
                        mOtherBestMixN2 = MyConstants.ZERO_D;
                    }
                    mBinding.otherBestMixO2.setText(String.valueOf(mOtherBestMixO2));
                    mBinding.otherBestMixHe.setText(String.valueOf(mOtherBestMixHe));
                    mBinding.otherBestMixN2.setText(String.valueOf(mOtherBestMixN2));
                }
            }
        };
    }

    // Optimize END Spinner

    String getDefaultOptimizeEndType() {return mDefaultOptimizeEndType;}

    //

    public int getDefaultOptimizeEndPosition() {return mDefaultOptimizeEndPosition; }

    public void setDefaultOptimizeEndPosition(int defaultOptimizeEndPosition) {mDefaultOptimizeEndPosition = defaultOptimizeEndPosition;}

    //

    void setAdapterDefaultOptimizeEnd(ArrayAdapter<EndType> adapterDefaultOptimizeEnd) {mAdapterDefaultOptimizeEnd = adapterDefaultOptimizeEnd;}

    public ArrayAdapter<EndType> getAdapterDefaultOptimizeEnd () {return mAdapterDefaultOptimizeEnd;}

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedDefaultOptimizeEnd() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    EndType endType = (EndType) parent.getAdapter().getItem(position);
                    mDefaultOptimizeEndType = endType.getEndType();
                }
            }
        };
    }

    // Other
    @Bindable
    public Double getOtherPartialPressure() {return mOtherPartialPressure; }

    public void setOtherPartialPressure(Double otherPartialPressure) {
        mOtherPartialPressure = otherPartialPressure;
        notifyPropertyChanged(BR.otherPartialPressure);
    }

    //

    @Bindable
    public Double getOtherBestMixHe() {return mOtherBestMixHe; }

    public void setOtherBestMixHe(Double otherBestMixHe) {
        mOtherBestMixHe = otherBestMixHe;
        notifyPropertyChanged(BR.otherBestMixHe);
    }

    //

    @Bindable
    public Double getOtherBestMixN2() {return mOtherBestMixN2; }

    public void setOtherBestMixN2(Double otherBestMixN2) {
        mOtherBestMixN2 = otherBestMixN2;
        notifyPropertyChanged(BR.otherBestMixN2);
    }

    //

    @Bindable
    public Double getOtherBestMixO2() {return mOtherBestMixO2; }

    public void setOtherBestMixO2(Double otherBestMixO2) {
        mOtherBestMixO2 = otherBestMixO2;
        notifyPropertyChanged(BR.otherBestMixO2);
    }

    //

    @Bindable
    public Double getOtherMod() {return mOtherMod; }

    public void setOtherMod(Double otherMod) {
        mOtherMod = otherMod;
        notifyPropertyChanged(BR.otherMod);
    }

    //

    @Bindable
    public Double getOtherEabd() {return mOtherEabd; }

    public void setOtherEabd(Double otherEabd) {
        mOtherEabd = otherEabd;
        notifyPropertyChanged(BR.otherEabd);
    }

    //

    @Bindable
    public Double getOtherEnd() {return mOtherEnd; }

    public void setOtherEnd(Double otherEnd) {
        mOtherEnd = otherEnd;
        notifyPropertyChanged(BR.otherEnd);
    }

    //

    @Bindable
    public Double getOtherEndN2Narc() {return mOtherEndN2Narc; }

    public void setOtherEndN2Narc(Double otherEndN2Narc) {
        mOtherEndN2Narc = otherEndN2Narc;
        notifyPropertyChanged(BR.otherEndN2Narc);
    }

    //

    @Bindable
    public Double getOtherEndN2O2Narc() {return mOtherEndN2O2Narc; }

    public void setOtherEndN2O2Narc(Double otherEndN2O2Narc) {
        mOtherEndN2O2Narc = otherEndN2O2Narc;
        notifyPropertyChanged(BR.otherEndN2O2Narc);
    }

    //

    @Bindable
    public Double getOtherEndN2O2HeNarc() {return mOtherEndN2O2HeNarc; }

    public void setOtherEndN2O2HeNarc(Double otherEndN2O2HeNarc) {
        mOtherEndN2O2HeNarc = otherEndN2O2HeNarc;
        notifyPropertyChanged(BR.otherEndN2O2HeNarc);
    }

    // Common

}