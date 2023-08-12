package ca.myairbuddyandi;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateEndActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateEnd class
 */

public class CalculateEnd extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateEnd";

    // Public
    public CalculateEndActivityBinding mBinding = null;

    // Protected

    // Private

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultEnd = MyConstants.ZERO_D;
    private Double mDefaultHe= MyConstants.ZERO_D;
    private int mDefaultSalinityPosition;

    // Other
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherEnd = MyConstants.ZERO_D;
    private Double mOtherHe = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateEnd() {
    }

    // Getters and setters

    // Default
    @Bindable
    public Double getDefaultEnd() {return mDefaultEnd; }

    public void setDefaultEnd(Double defaultEnd) {
        mDefaultEnd = defaultEnd;
    }

    //

    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) {
        mDefaultDepth = defaultDepth;
    }

    //

    @Bindable
    public Double getDefaultHe() {return mDefaultHe; }

    public void setDefaultHe(Double defaultHe) {
        mDefaultHe = defaultHe;
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
    public Double getOtherEnd() {return mOtherEnd; }

    public void setOtherEnd(Double otherEnd) {
        mOtherEnd = otherEnd;
        notifyPropertyChanged(BR.otherEnd);
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
    public Double getOtherHe() {return mOtherHe; }

    public void setOtherHe(Double otherHe) {
        mOtherHe = otherHe;
        notifyPropertyChanged(BR.otherHe);
    }

    // Common

}