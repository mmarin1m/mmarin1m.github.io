package ca.myairbuddyandi;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateCharlesLawActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateCharlesLaw class
 */

public class CalculateCharlesLaw extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateCharlesLaw";

    // Public
    public CalculateCharlesLawActivityBinding mBinding = null;

    // Protected

    // Private
    private Context mContext;

    // Default
    private ArrayAdapter<String> mAdapterDefaultCharlesConstant;
    private boolean mDefaultCharlesConstant = true; // true = position 0 = Constant pressure, false = position 1 = Constant volume
    private int mDefaultCharlesConstantPosition;

    private Double mDefaultDepth = MyConstants.ZERO_D;
    private Double mDefaultT1 = MyConstants.ZERO_D;
    private Double mDefaultT2 = MyConstants.ZERO_D;
    private Double mDefaultPV1 = MyConstants.ZERO_D; // Used for Pressure and Volume
    private Double mDefaultPV2 = MyConstants.ZERO_D; // Used for Pressure and Volume
    private String mDefaultUnit;

    // Other
    private Double mOtherDepth = MyConstants.ZERO_D;
    private Double mOtherT1 = MyConstants.ZERO_D;
    private Double mOtherT2 = MyConstants.ZERO_D;
    private Double mOtherPV1 = MyConstants.ZERO_D; // Used for Pressure and Volume
    private Double mOtherPV2 = MyConstants.ZERO_D; // Used for Pressure and Volume

    // End of variables

    // Public constructor
    public CalculateCharlesLaw() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    public String getDefaultUnit() {return mDefaultUnit; }

    public void setDefaultUnit(String defaultUnit) {
        mDefaultUnit = defaultUnit;
    }

    // Default

    @Bindable
    public Double getDefaultDepth() {return mDefaultDepth; }

    public void setDefaultDepth(Double defaultDepth) {
        mDefaultDepth = defaultDepth;
    }
    //
    @Bindable
    public Double getDefaultPV1() {return mDefaultPV1; }

    public void setDefaultPV1(Double defaultPV1) {
        mDefaultPV1 = defaultPV1;
    }
    //
    @Bindable
    public Double getDefaultPV2() {return mDefaultPV2; }

    public void setDefaultPV2(Double defaultPV2) {
        mDefaultPV2 = defaultPV2;
    }
    //
    @Bindable
    public Double getDefaultT1() {return mDefaultT1; }

    public void setDefaultT1(Double defaultT1) {
        mDefaultT1 = defaultT1;
    }
    //
    @Bindable
    public Double getDefaultT2() {return mDefaultT2; }

    public void setDefaultT2(Double defaultT2) {
        mDefaultT2 = defaultT2;
    }

    // Charles Constant Spinner

    boolean getDefaultCharlesConstant() {return mDefaultCharlesConstant;}

    void setDefaultCharlesConstant(boolean defaultCharlesConstant) {
        mDefaultCharlesConstant = defaultCharlesConstant;
        if (mDefaultCharlesConstant) {
            // Calculating volume with constant pressure
            mBinding.pv1Button2.setText(mContext.getString(R.string.hdr_v1));
            mBinding.pv2Button2.setText(mContext.getString(R.string.hdr_v2));
            mBinding.pv1Button.setText(mContext.getString(R.string.hdr_v1));
            mBinding.pv2Button.setText(mContext.getString(R.string.hdr_v2));
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                mBinding.defaultPV1Lbl.setText(mContext.getString(R.string.lbl_imperial_volume_unit));
                mBinding.otherPV1Lbl.setText(mContext.getString(R.string.lbl_metric_volume_unit));
                mBinding.defaultPV2Lbl.setText(mContext.getString(R.string.lbl_imperial_volume_unit));
                mBinding.otherPV2Lbl.setText(mContext.getString(R.string.lbl_metric_volume_unit));
            } else {
                mBinding.defaultPV1Lbl.setText(mContext.getString(R.string.lbl_metric_volume_unit));
                mBinding.otherPV1Lbl.setText(mContext.getString(R.string.lbl_imperial_volume_unit));
                mBinding.defaultPV2Lbl.setText(mContext.getString(R.string.lbl_metric_volume_unit));
                mBinding.otherPV2Lbl.setText(mContext.getString(R.string.lbl_imperial_volume_unit));
            }
        } else {
            // Calculating pressure with constant volume
            mBinding.pv1Button2.setText(mContext.getString(R.string.hdr_p1));
            mBinding.pv2Button2.setText(mContext.getString(R.string.hdr_p2));
            mBinding.pv1Button.setText(mContext.getString(R.string.hdr_p1));
            mBinding.pv2Button.setText(mContext.getString(R.string.hdr_p2));
            if (mDefaultUnit.equals(MyConstants.IMPERIAL)) {
                mBinding.defaultPV1Lbl.setText(mContext.getString(R.string.lbl_imperial_pressure_unit));
                mBinding.otherPV1Lbl.setText(mContext.getString(R.string.lbl_metric_pressure_unit));
                mBinding.defaultPV2Lbl.setText(mContext.getString(R.string.lbl_imperial_pressure_unit));
                mBinding.otherPV2Lbl.setText(mContext.getString(R.string.lbl_metric_pressure_unit));
            } else {
                mBinding.defaultPV1Lbl.setText(mContext.getString(R.string.lbl_metric_pressure_unit));
                mBinding.otherPV1Lbl.setText(mContext.getString(R.string.lbl_imperial_pressure_unit));
                mBinding.defaultPV2Lbl.setText(mContext.getString(R.string.lbl_metric_pressure_unit));
                mBinding.otherPV2Lbl.setText(mContext.getString(R.string.lbl_imperial_pressure_unit));
            }
        }
    }

    public int getDefaultCharlesConstantPosition() {return mDefaultCharlesConstantPosition; }

    public void setDefaultCharlesConstantPosition(int defaultCharlesConstantPosition) {mDefaultCharlesConstantPosition = defaultCharlesConstantPosition;}

    void setAdapterDefaultCharlesConstant(ArrayAdapter<String> adapterDefaultCharlesConstant) {mAdapterDefaultCharlesConstant = adapterDefaultCharlesConstant;}

    public ArrayAdapter<String> getAdapterDefaultCharlesConstant () {return mAdapterDefaultCharlesConstant;}

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedDefaultCharlesConstant() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    setDefaultCharlesConstant((position == 0));
                    clear();
                }
            }
        };
    }

    // Other

    @Bindable
    public Double getOtherDepth() {return mOtherDepth; }

    public void setOtherDepth(Double otherDepth) {
        mOtherDepth = otherDepth;
        notifyPropertyChanged(BR.otherDepth);
    }
    //
    @Bindable
    public Double getOtherPV1() {return mOtherPV1; }

    public void setOtherPV1(Double otherPV1) {
        mOtherPV1 = otherPV1;
        notifyPropertyChanged(BR.otherPV1);
    }
    //
    @Bindable
    public Double getOtherPV2() {return mOtherPV2; }

    public void setOtherPV2(Double otherPV2) {
        mOtherPV2 = otherPV2;
        notifyPropertyChanged(BR.otherPV2);
    }
    //
    @Bindable
    public Double getOtherT1() {return mOtherT1; }

    public void setOtherT1(Double otherT1) {
        mOtherT1 = otherT1;
        notifyPropertyChanged(BR.otherT1);
    }
    //
    @Bindable
    public Double getOtherT2() {return mOtherT2; }

    public void setOtherT2(Double otherT2) {
        mOtherT2 = otherT2;
        notifyPropertyChanged(BR.otherT2);
    }

    // Common

    public void clear() {
        // Reset the Default
        setDefaultPV1(MyConstants.ZERO_D);
        setDefaultPV2(MyConstants.ZERO_D);
        setDefaultT1(MyConstants.ZERO_D);
        setDefaultT2(MyConstants.ZERO_D);

        mBinding.defaultPV1.setText("0.0");
        mBinding.defaultPV2.setText("0.0");
        mBinding.defaultT1.setText("0.0");
        mBinding.defaultT2.setText("0.0");

        // Reset the Other
        setOtherPV1(MyConstants.ZERO_D);
        setOtherPV2(MyConstants.ZERO_D);
        setOtherT1(MyConstants.ZERO_D);
        setOtherT2(MyConstants.ZERO_D);

        mBinding.defaultPV1.clearFocus();
        mBinding.defaultPV1.requestFocus();
        mBinding.defaultPV1.selectAll();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(mBinding.defaultPV1, InputMethodManager.SHOW_IMPLICIT);
    }

}