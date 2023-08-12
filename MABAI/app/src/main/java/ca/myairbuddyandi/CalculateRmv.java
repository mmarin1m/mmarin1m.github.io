package ca.myairbuddyandi;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.CalculateRmvActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CalculateRmv class
 */

public class CalculateRmv extends BaseObservable {

    // Static
    private static final String LOG_TAG = "CalculateRmv";

    // Public
    public CalculateRmvActivityBinding mBinding = null;

    // Protected

    // Default
    private ArrayAdapter<String> mAdapterDefaultSalinity;
    private boolean mDefaultSalinity = true; // true = Salt, false = Fresh
    private Double mDefaultAta = MyConstants.ZERO_D;
    private Double mDefaultAvgDepth = MyConstants.ZERO_D;
    private Double mDefaultBeginningPressure = MyConstants.ZERO_D;
    private Double mDefaultBottomTime = MyConstants.ZERO_D;
    private Double mDefaultEndingPressure = MyConstants.ZERO_D;
    private Double mDefaultPressureUsed = MyConstants.ZERO_D;
    private Double mDefaultRatedPressure = MyConstants.ZERO_D;
    private Double mDefaultRmv = MyConstants.ZERO_D;
    private Double mDefaultSac = MyConstants.ZERO_D;
    private Double mDefaultTankFactor = MyConstants.ZERO_D;
    private Double mDefaultTankVolume = MyConstants.ZERO_D;
    private Double mDefaultVolumeAvailable = MyConstants.ZERO_D;
    private Double mDefaultVolumeUsed = MyConstants.ZERO_D;
    private int mDefaultSalinityPosition;
    private String mDefaultBottomTimeString;

    // Other
    private Double mOtherAta = MyConstants.ZERO_D;
    private Double mOtherAvgDepth = MyConstants.ZERO_D;
    private Double mOtherBeginningPressure = MyConstants.ZERO_D;
    private Double mOtherBottomTime = MyConstants.ZERO_D;
    private Double mOtherEndingPressure = MyConstants.ZERO_D;
    private Double mOtherPressureUsed = MyConstants.ZERO_D;
    private Double mOtherRatedPressure = MyConstants.ZERO_D;
    private Double mOtherRmv = MyConstants.ZERO_D;
    private Double mOtherSac = MyConstants.ZERO_D;
    private Double mOtherTankFactor = MyConstants.ZERO_D;
    private Double mOtherTankVolume = MyConstants.ZERO_D;
    private Double mOtherVolumeAvailable = MyConstants.ZERO_D;
    private Double mOtherVolumeUsed = MyConstants.ZERO_D;

    // End of variables

    // Public constructor
    public CalculateRmv() {
    }

    // Getters and setters

    // Default
    public Double getDefaultAta() {return mDefaultAta; }

    public void setDefaultAta(Double defaultAta) { mDefaultAta = defaultAta; }

    //
    public Double getDefaultAvgDepth() {return mDefaultAvgDepth; }

    public void setDefaultAvgDepth(Double defaultAvgDepth) { mDefaultAvgDepth = defaultAvgDepth; }

    //
    public Double getDefaultBeginningPressure() {return mDefaultBeginningPressure; }

    public void setDefaultBeginningPressure(Double defaultBeginningPressure) { mDefaultBeginningPressure = defaultBeginningPressure; }

    //
    public Double getDefaultEndingPressure() {return mDefaultEndingPressure; }

    public void setDefaultEndingPressure(Double defaultEndingPressure) { mDefaultEndingPressure = defaultEndingPressure; }

    //
    public Double getDefaultPressureUsed() {return mDefaultPressureUsed; }

    public void setDefaultPressureUsed(Double defaultPressureUsed) { mDefaultPressureUsed = defaultPressureUsed; }

    //
    Double getDefaultBottomTime() {return mDefaultBottomTime;}

    public String getDefaultBottomTimeString() { return MyFunctions.convertToMmSs(mDefaultBottomTime); }

    String getDefaultBottomTimeStringX() { return mDefaultBottomTimeString; }

    void setDefaultBottomTime(Double defaultBottomTime) {mDefaultBottomTime = defaultBottomTime;}

    public void setDefaultBottomTimeString(String bottomTimeString) {
        String newBottomTimeFormatted = MyFunctions.formatBottomTime(bottomTimeString);
        if (!newBottomTimeFormatted.isEmpty()) {
            // Bottom Time entered is valid, use the one formatted
            mDefaultBottomTimeString = newBottomTimeFormatted;
            // Convert the Bottom Time entered into a Double minutes.seconds
            setDefaultBottomTime(MyFunctions.convertMmSs(mDefaultBottomTimeString));
        } else {
            // Bottom Time entered is invalid, keep the one entered
            mDefaultBottomTimeString = bottomTimeString;
        }
    }

    //

    public Double getDefaultTankVolume() {return mDefaultTankVolume; }

    public void setDefaultTankVolume(Double defaultTankVolume) { mDefaultTankVolume = defaultTankVolume; }

    //

    public Double getDefaultRatedPressure() {return mDefaultRatedPressure; }

    public void setDefaultRatedPressure(Double defaultRatedPressure) { mDefaultRatedPressure = defaultRatedPressure; }

    //

    public Double getDefaultSac() {return mDefaultSac; }

    public void setDefaultSac(Double defaultSac) { mDefaultSac = defaultSac; }

    //

    public Double getDefaultTankFactor() {return mDefaultTankFactor; }

    public void setDefaultTankFactor(Double defaultTankFactor) { mDefaultTankFactor = defaultTankFactor; }

    //

    public Double getDefaultRmv() {return mDefaultRmv; }

    public void setDefaultRmv(Double defaultRmv) { mDefaultRmv = defaultRmv; }

    //

    public Double getDefaultVolumeUsed() {return mDefaultVolumeUsed; }

    public void setDefaultVolumeUsed(Double defaultVolumeUsed) { mDefaultVolumeUsed = defaultVolumeUsed; }

    //

    public Double getDefaultVolumeAvailable() {return mDefaultVolumeAvailable; }

    public void setDefaultVolumeAvailable(Double defaultVolumeAvailable) { mDefaultVolumeAvailable = defaultVolumeAvailable; }

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
    public Double getOtherAta() {return mOtherAta; }

    public void setOtherAta(Double otherAta) { mOtherAta = otherAta; }

    //

    public Double getOtherAvgDepth() {return mOtherAvgDepth; }

    public void setOtherAvgDepth(Double otherAvgDepth) { mOtherAvgDepth = otherAvgDepth; }

    //

    public Double getOtherBeginningPressure() {return mOtherBeginningPressure; }

    public void setOtherBeginningPressure(Double otherBeginningPressure) { mOtherBeginningPressure = otherBeginningPressure; }

    //

    Double getOtherEndingPressure() {return mOtherEndingPressure; }

    void setOtherEndingPressure(Double otherEndingPressure) { mOtherEndingPressure = otherEndingPressure; }

    //

    public Double getOtherPressureUsed() {return mOtherPressureUsed; }

    public void setOtherPressureUsed(Double otherPressureUsed) { mOtherPressureUsed = otherPressureUsed; }

    //

    public String getOtherBottomTimeString() { return MyFunctions.convertToMmSs(mOtherBottomTime); }

    public void setOtherBottomTime(Double otherBottomTime) {
        mOtherBottomTime = otherBottomTime;
    }

    //

    public Double getOtherTankVolume() {return mOtherTankVolume; }

    public void setOtherTankVolume(Double otherTankVolume) { mOtherTankVolume = otherTankVolume; }

    //

    public Double getOtherRatedPressure() {return mOtherRatedPressure; }

    public void setOtherRatedPressure(Double otherRatedPressure) { mOtherRatedPressure = otherRatedPressure; }

    //

    public Double getOtherSac() {return mOtherSac; }

    public void setOtherSac(Double otherSac) { mOtherSac = otherSac; }

    //

    public Double getOtherTankFactor() {return mOtherTankFactor; }

    public void setOtherTankFactor(Double otherTankFactor) { mOtherTankFactor = otherTankFactor; }

    //

    public Double getOtherRmv() {return mOtherRmv; }

    public void setOtherRmv(Double otherRmv) { mOtherRmv = otherRmv; }

    //

    public Double getOtherVolumeUsed() {return mOtherVolumeUsed; }

    public void setOtherVolumeUsed(Double otherVolumeUsed) { mOtherVolumeUsed = otherVolumeUsed; }

    //

    public Double getOtherVolumeAvailable() {return mOtherVolumeAvailable; }

    public void setOtherVolumeAvailable(Double otherVolumeAvailable) { mOtherVolumeAvailable = otherVolumeAvailable; }
}