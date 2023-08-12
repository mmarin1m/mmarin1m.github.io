package ca.myairbuddyandi;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateAltitudeActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateAltitude class
 */

public class CalculateAltitude extends BaseObservable {

     // Static
    private static final String LOG_TAG = "CalculateAltitude";

    // Public

    public CalculateAltitudeActivityBinding mBinding = null;

    // Protected

    // Private

    private Context mContext;

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private Double mDefaultAltitude = MyConstants.ZERO_D;
    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultSafetyStop = MyConstants.ZERO_D;
    private Double mDefaultSurfacePressure = MyConstants.ZERO_D;
    private Double mDefaultSurfacePressureMbar = MyConstants.ZERO_D;
    private Double mDefaultTheoreticalDepth = MyConstants.ZERO_D;
    private int mDefaultSalinityPosition;
    private String mDefaultSafetyStopUnit;
    private String mDefaultUnit;

    // Other
    private Double mOtherAltitude = MyConstants.ZERO_D;
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherSafetyStop = MyConstants.ZERO_D;
    private Double mOtherSurfacePressure = MyConstants.ZERO_D;
    private Double mOtherSurfacePressureMbar = MyConstants.ZERO_D;
    private Double mOtherTheoreticalDepth = MyConstants.ZERO_D;
    private String mOtherSafetyStopUnit;

    // End of variables

    // Public constructor
    public CalculateAltitude() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    // Default

    public String getDefaultUnit() {return mDefaultUnit; }

    public void setDefaultUnit(String defaultUnit) {
        mDefaultUnit = defaultUnit;
    }
    //
    @Bindable
    public Double getDefaultAltitude() {return mDefaultAltitude; }

    public void setDefaultAltitude(Double defaultAltitude) {
        mDefaultAltitude = defaultAltitude;
    }
    //
    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) {
        mDefaultDepth = defaultDepth;
    }
    //
    @Bindable
    public Double getDefaultSafetyStop() {return mDefaultSafetyStop; }

    void setDefaultSafetyStop(Double defaultSafetyStop) { mDefaultSafetyStop = defaultSafetyStop; }
    //
    @Bindable
    public Double getDefaultSurfacePressure() {return mDefaultSurfacePressure; }

    void setDefaultSurfacePressure(Double defaultSurfacePressure) { mDefaultSurfacePressure = defaultSurfacePressure; }
    //
    @Bindable
    public Double getDefaultSurfacePressureMbar() {return mDefaultSurfacePressureMbar; }

    void setDefaultSurfacePressureMbar(Double defaultSurfacePressureMbar) { mDefaultSurfacePressureMbar = defaultSurfacePressureMbar; }
    //
    @Bindable
    public Double getDefaultTheoreticalDepth() {return mDefaultTheoreticalDepth; }

    void setDefaultTheoreticalDepth(Double defaultTheoreticalDepth) { mDefaultTheoreticalDepth = defaultTheoreticalDepth; }
    //
    @Bindable
    public String getDefaultSafetyStopUnit() {return mDefaultSafetyStopUnit; }

    void setDefaultSafetyStopUnit(String defaultSafetyStopUnit) { mDefaultSafetyStopUnit = defaultSafetyStopUnit; }

    // Salinity Spinner

    boolean getDefaultSalinity() {return mDefaultSalinity;}

    void setDefaultSalinity(boolean defaultSalinity) {
        mDefaultSalinity = defaultSalinity;

        if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
            if (mDefaultSalinity) {
                // true = Salt = 0 position
                mDefaultSafetyStopUnit = mContext.getString(R.string.lbl_imperial_sw_unit);
                mOtherSafetyStopUnit = mContext.getString(R.string.lbl_metric_sw_unit);
            } else {
                mDefaultSafetyStopUnit = mContext.getString(R.string.lbl_imperial_fw_unit);
                mOtherSafetyStopUnit = mContext.getString(R.string.lbl_metric_fw_unit);
            }
        } else {
            if (mDefaultSalinity) {
                // true = Salt = 0 position
                mDefaultSafetyStopUnit = mContext.getString(R.string.lbl_metric_sw_unit);
                mOtherSafetyStopUnit = mContext.getString(R.string.lbl_imperial_sw_unit);
            } else {
                mDefaultSafetyStopUnit = mContext.getString(R.string.lbl_metric_fw_unit);
                mOtherSafetyStopUnit = mContext.getString(R.string.lbl_imperial_fw_unit);
            }
        }
        mBinding.defaultSSULbl.setText(mDefaultSafetyStopUnit);
        mBinding.otherSSULbl.setText(mOtherSafetyStopUnit);
    }

    public int getDefaultSalinityPosition() {return mDefaultSalinityPosition; }

    public void setDefaultSalinityPosition(int defaultSalinityPosition) {
        mDefaultSalinityPosition = defaultSalinityPosition;
    }

    void setAdapterDefaultSalinity(ArrayAdapter<String> adapterDefaultSalinity) {mAdapterDefaultSalinity = adapterDefaultSalinity;}

    public ArrayAdapter<String> getAdapterDefaultSalinity () {return mAdapterDefaultSalinity;}

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedDefaultSalinity() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    setDefaultSalinity ((position == 0));
                }
            }
        };
    }

    // Other
    @Bindable
    public Double getOtherAltitude() {return mOtherAltitude; }

    void setOtherAltitude(Double OtherAltitude) {
        mOtherAltitude = OtherAltitude;
        notifyPropertyChanged(BR.otherAltitude);
    }
    //
    @Bindable
    public Double getOtherDepth() {return mOtherDepth; }

    public void setOtherDepth(Double OtherDepth) {
        mOtherDepth = OtherDepth;
        notifyPropertyChanged(BR.otherDepth);
    }
    //
    @Bindable
    public Double getOtherSafetyStop() {return mOtherSafetyStop; }

    void setOtherSafetyStop(Double OtherSafetyStop) {
        mOtherSafetyStop = OtherSafetyStop;
        notifyPropertyChanged(BR.otherSafetyStop);
    }
    //
    @Bindable
    public Double getOtherSurfacePressure() {return mOtherSurfacePressure; }

    void setOtherSurfacePressure(Double OtherSurfacePressure) {
        mOtherSurfacePressure = OtherSurfacePressure;
        notifyPropertyChanged(BR.otherSurfacePressure);
    }
    //
    @Bindable
    public Double getOtherSurfacePressureMbar() {return mOtherSurfacePressureMbar; }

    void setOtherSurfacePressureMbar(Double otherSurfacePressureMbar) {
        mOtherSurfacePressureMbar = otherSurfacePressureMbar;
        notifyPropertyChanged(BR.otherSurfacePressureMbar);
    }
    //
    @Bindable
    public Double getOtherTheoreticalDepth() {return mOtherTheoreticalDepth; }

    void setOtherTheoreticalDepth(Double OtherTheoreticalDepth) {
        mOtherTheoreticalDepth = OtherTheoreticalDepth;
        notifyPropertyChanged(BR.otherTheoreticalDepth);
    }
    //
    @Bindable
    public String getOtherSafetyStopUnit() {return mOtherSafetyStopUnit; }


    // Common

}