package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateNitroxActivityBinding;

/**
 * Created by Michel on 2020-08-31.
 * Holds all of the logic for the CalculateNitrox class
 */

public class CalculateNitrox extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateNitrox";

    // Public
    public CalculateNitroxActivityBinding mBinding = null;

    // Protected

    // Private
    private int mPressureBleedTankTotal = MyConstants.ZERO_I;
    private int mPressureDesired = MyConstants.ZERO_I;
    private int mPressureOxygenAdd = MyConstants.ZERO_I;
    private int mPressureOxygenTotal = MyConstants.ZERO_I;
    private int mPressureStart = MyConstants.ZERO_I;
    private int mPressureBankStart = MyConstants.ZERO_I;
    private int mPressureTopOffAdd = MyConstants.ZERO_I;
    private int mPressureTopOffTotal = MyConstants.ZERO_I;
    private Double mBankRatedPressure = MyConstants.ZERO_D;
    private Double mBankRatedVolume = MyConstants.ZERO_D;
    private Double mCylinderRatedPressure = MyConstants.ZERO_D;
    private Double mCylinderRatedVolume = MyConstants.ZERO_D;
    private Double mMixO2Fill = MyConstants.ZERO_D;
    private Double mMixO2Hose = MyConstants.ZERO_D;
    private Double mMixO2Desired = MyConstants.ZERO_D;
    private Double mMixO2Start = MyConstants.ZERO_D;
    private Double mMixTopOff = MyConstants.ZERO_D;
    private String mOxygenAddLbl;
    private String mTopOffAddLbl;

    // End of variables

    // Public constructor
    public CalculateNitrox() {
    }

    // Getters and setters

    @Bindable
    public Double getBankRatedPressure() {return mBankRatedPressure; }

    public void setBankRatedPressure(Double bankRatedPressure) { mBankRatedPressure = bankRatedPressure; }

    //

    @Bindable
    public Double getBankRatedVolume() {return mBankRatedVolume; }

    public void setBankRatedVolume(Double bankRatedVolume) { mBankRatedVolume = bankRatedVolume; }

    //

    @Bindable
    public Double getCylinderRatedPressure() {return mCylinderRatedPressure; }

    public void setCylinderRatedPressure(Double cylinderRatedPressure) { mCylinderRatedPressure = cylinderRatedPressure; }

    //

    @Bindable
    public Double getCylinderRatedVolume() {return mCylinderRatedVolume; }

    public void setCylinderRatedVolume(Double cylinderRatedVolume) { mCylinderRatedVolume = cylinderRatedVolume; }

    //

    @Bindable
    public Double getMixO2Desired() {return mMixO2Desired; }

    public void setMixO2Desired(Double mixO2Desired) { mMixO2Desired = mixO2Desired; }

    //

    @Bindable
    public Double getMixO2Start() {return mMixO2Start; }

    public void setMixO2Start(Double mixO2Start) { mMixO2Start = mixO2Start; }

    // Comes from the settings
    // Typically 100.0 for 100% O2
    @Bindable
    public Double getMixO2Fill() {return mMixO2Fill; }

    public void setMixO2Fill(Double mixO2Fill) { mMixO2Fill = mixO2Fill; }

    //

    @Bindable
    public Double getMixO2Hose() {return mMixO2Hose; }

    public void setMixO2Hose(Double mixO2Hose) { mMixO2Hose = mixO2Hose; }

    // Comes from the settings
    @Bindable
    public Double getMixTopOff() {return mMixTopOff; }

    public void setMixTopOff(Double mixTopOff) { mMixTopOff = mixTopOff; }

    //

    @Bindable
    public String getOxygenAddLbl() {return mOxygenAddLbl; }

    public void setOxygenAddLbl(String oxygenAddLbl) {
        mOxygenAddLbl = oxygenAddLbl;
        notifyPropertyChanged(BR.oxygenAddLbl);
    }

    //

    @Bindable
    public int getPressureBankStart() {return mPressureBankStart; }

    public void setPressureBankStart(int pressureBankStart) { mPressureBankStart = pressureBankStart; }

    //

    // NOTE: Reserved for future use
    @Bindable
    public int getPressureBleedTankTotal() {return mPressureBleedTankTotal; }

    @Bindable
    public String getPressureBleedTankTotalS() {return "(" + mPressureBleedTankTotal + ")"; }

    public void setPressureBleedTankTotal(int pressureBleedTankTotal) {
        mPressureBleedTankTotal = pressureBleedTankTotal;
        notifyPropertyChanged(BR.pressureBleedTankTotalS);
    }

    //

    @Bindable
    public int getPressureDesired() {return mPressureDesired; }

    public void setPressureDesired(int pressureDesired) {
        mPressureDesired = pressureDesired;
        notifyPropertyChanged(BR.pressureDesired);
    }

    //

    @Bindable
    public int getPressureOxygenAdd() {return mPressureOxygenAdd; }

    public void setPressureOxygenAdd(int pressureOxygenAdd) {
        mPressureOxygenAdd = pressureOxygenAdd;
        notifyPropertyChanged(BR.pressureOxygenAdd);
    }

    //

    @Bindable
    public int getPressureOxygenTotal() {return mPressureOxygenTotal; }

    @Bindable
    public String getPressureOxygenTotalS() {return "(" + mPressureOxygenTotal + ")"; }

    public void setPressureOxygenTotal(int pressureOxygenTotal) {
        mPressureOxygenTotal = pressureOxygenTotal;
        notifyPropertyChanged(BR.pressureOxygenTotalS);
    }

    //

    @Bindable
    public int getPressureStart() {return mPressureStart; }

    public void setPressureStart(int pressureStart) {
        mPressureStart = pressureStart;
        notifyPropertyChanged(BR.pressureStart);
    }

    //

    @Bindable
    public int getPressureTopOffAdd() {return mPressureTopOffAdd; }

    public void setPressureTopOffAdd(int pressureTopOffAdd) {
        mPressureTopOffAdd = pressureTopOffAdd;
        notifyPropertyChanged(BR.pressureTopOffAdd);
    }

    //

    // NOTE: Reserved for future use
    @Bindable
    public int getPressureTopOffTotal() {return mPressureTopOffTotal; }

    @Bindable
    public String getPressureTopOffTotalS() {return "(" + mPressureTopOffTotal + ")"; }

    public void setPressureTopOffTotal(int pressureTopOffTotal) {
        mPressureTopOffTotal = pressureTopOffTotal;
        notifyPropertyChanged(BR.pressureTopOffTotalS);
    }

    //

    @Bindable
    public String getTopOffAddLbl() {return mTopOffAddLbl; }

    public void setTopOffAddLbl(String topOffAddLbl) {
        mTopOffAddLbl = topOffAddLbl;
        notifyPropertyChanged(BR.topOffAddLbl);
    }
}
