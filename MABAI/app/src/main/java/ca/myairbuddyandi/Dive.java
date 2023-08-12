package ca.myairbuddyandi;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;
import java.util.Date;

import ca.myairbuddyandi.databinding.DiveEnvironmentBinding;
import ca.myairbuddyandi.databinding.DiveGasBinding;
import ca.myairbuddyandi.databinding.DiveGearBinding;
import ca.myairbuddyandi.databinding.DivePlanningBinding;
import ca.myairbuddyandi.databinding.DiveProblemBinding;
import ca.myairbuddyandi.databinding.DiveSummaryBinding;

/**
 * Created by Michel on 2016-11-29.
 * Holds all of the logic for the Dive class
 */

public class Dive extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Dive";
    private static final int HINT_OFFSET = 1;

    // Public
    public DivePlanningBinding mBindingPlanning = null;
    public DiveSummaryBinding mBindingSummary = null;
    public DiveEnvironmentBinding mBindingEnvironment = null;
    public DiveGasBinding mBindingGas = null;
    public DiveGearBinding mBindingGear = null;
    public DiveProblemBinding mBindingProblem = null;

    // Protected

    // Private
    private boolean mSalinity; // true = 1 = Salt, false = 0 = Fresh
    private int mAnySymptomPosition;
    private int mAltitude;
    private int mConditionPosition;
    private int mDivePlanCount;
    private int mDiveTypePosition;
    private int mEnvironmentPosition;
    private int mExposureAltitudePosition;
    private int mExtraDiversCount;
    private int mHour;
    private int mLogBookNo;
    private int mMalfunctionPosition;
    private int mMinute;
    private int mPlatformPosition;
    private int mProblemPosition;
    private int mStatusPosition;
    private int mSuitPosition;
    private int mThermalComfortPosition;
    private int mWeatherPosition;
    private int mWorkLoadPosition;
    private Date mDate;
    private Double mAirTemp;
    private Double mAverageDepth;
    private Double mBottomTime;
    private Double mMaximumDepth;
    private Double mMyBuddyEndingPressure;
    private Double mMyBuddyPressure;
    private Double mMyBuddyRatedPressure;
    private Double mMyBuddyRatedVolume;
    private Double mMyBuddyRmv;
    private Double mMyBuddySac;
    private Double mMyBuddyVolume;
    private Double mMyEndingPressure;
    private Double mMyPressure;
    private Double mMyRatedPressure;
    private Double mMyRatedVolume;
    private Double mMyRmv;
    private Double mMySac;
    private Double mMyVolume;
    private Double mWaterTempAverage;
    private Double mWaterTempBottom;
    private Double mWaterTempSurface;
    private Double mWeight;
    private Long mBuddyDiverNo;
    private Long mDiveNo;
    private Long mMyBuddyGroupNo;
    private Long mMyDiverNo;
    private Long mMyGroupNo;
    private String mAnySymptom;
    private String mBottomTimeString;
    private String mCondition;
    private String mDiveBoat;
    private String mDiveBoatOld;
    private String mDiveSite;
    private String mDiveSiteOld;
    private String mDiveType;
    private String mDiveTypeDesc;
    private String mEnvironment;
    private String mExposureAltitude;
    private String mLocation;
    private String mLocationOld;
    private String mMalfunction;
    private String mMeLabel;
    private String mMyBuddyFullName;
    private String mMyBuddyGroup;
    private String mMyGroup;
    private String mNoteEnvironment;
    private String mNoteGas;
    private String mNoteGear;
    private String mNoteProblem;
    private String mNoteSummary;
    private String mPlanning;
    private String mPlatform;
    private String mProblem;
    private String mPurpose;
    private String mStatus;
    private String mSuit;
    private String mThermalComfort;
    private String mTimeIn;
    private String mVisibility;
    private String mWeather;
    private String mWorkLoad;

    // Excluded from Parcelable

    private boolean mVisible = false;
    private boolean mChecked = false;
    private ArrayAdapter<DiveType> mAdapterDiveType;
    private ArrayAdapter<DynamicSpinner> mAdapterAnySymptom;
    private ArrayAdapter<DynamicSpinner> mAdapterCondition;
    private ArrayAdapter<DynamicSpinner> mAdapterEnvironment;
    private ArrayAdapter<DynamicSpinner> mAdapterExposureAltitude;
    private ArrayAdapter<DynamicSpinner> mAdapterMalfunction;
    private ArrayAdapter<DynamicSpinner> mAdapterPlatform;
    private ArrayAdapter<DynamicSpinner> mAdapterProblem;
    private ArrayAdapter<DynamicSpinner> mAdapterSuit;
    private ArrayAdapter<DynamicSpinner> mAdapterThermalComfort;
    private ArrayAdapter<DynamicSpinner> mAdapterWeather;
    private ArrayAdapter<DynamicSpinner> mAdapterWorkLoad;
    private ArrayAdapter<String> mAdapterStatus;
    private ArrayList<DiveType> mItemsDiveType;
    private ArrayList<DynamicSpinner> mItemsAnySymptom;
    private ArrayList<DynamicSpinner> mItemsCondition;
    private ArrayList<DynamicSpinner> mItemsEnvironment;
    private ArrayList<DynamicSpinner> mItemsExposureAltitude;
    private ArrayList<DynamicSpinner> mItemsMalfunction;
    private ArrayList<DynamicSpinner> mItemsPlatform;
    private ArrayList<DynamicSpinner> mItemsProblem;
    private ArrayList<DynamicSpinner> mItemsSuit;
    private ArrayList<DynamicSpinner> mItemsThermalComfort;
    private ArrayList<DynamicSpinner> mItemsWeather;
    private ArrayList<DynamicSpinner> mItemsWorkLoad;
    private Boolean mHasDataChanged = false;
    private transient Context mContext;
    private String[] mItemsStatus;

    // End of variables

    // Public constructor
    public Dive() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    //

    public Double getAirTemp() {return mAirTemp;}

    public void setAirTemp(Double airTemp) { mAirTemp = airTemp;}

    //

    public int getAltitude() {return mAltitude;}

    public void setAltitude(int altitude) {mAltitude = altitude;}

    // AnySymptom Spinner

    public String getAnySymptom() {
        return mAnySymptom; }

    public void setAnySymptom(String anySymptom) {
        mAnySymptom = anySymptom;
        //Set the position to select and show the data on the activity
        setAnySymptomPosition(getAnySymptomIndex(mAnySymptom));
    }

    void setAnySymptomLoad(String anySymptom) {
        // Initial load from My History
        // Avoid calling getIndex()
        mAnySymptom = anySymptom;
    }

    public int getAnySymptomPosition() {return mAnySymptomPosition; }

    public void setAnySymptomPosition(int anySymptomPosition) {
        mAnySymptomPosition = anySymptomPosition;
    }

    void setItemsAnySymptom(ArrayList<DynamicSpinner> itemsAnySymptom) {
        mItemsAnySymptom = itemsAnySymptom;
    }

    void setAdapterAnySymptom(ArrayAdapter<DynamicSpinner> adapterAnySymptom) { mAdapterAnySymptom = adapterAnySymptom; }

    public ArrayAdapter<DynamicSpinner> getAdapterAnySymptom () {
        return mAdapterAnySymptom;
    }

    private int getAnySymptomIndex(String anySymptom)
    {
        int position = 0;

        for (int i = 0; i < mItemsAnySymptom.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsAnySymptom.get(i);
            if(dynamicSpinner.getSpinnerText().equals(anySymptom)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedAnySymptom() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String AnySymptom_s = dynamicSpinner.getSpinnerText();
                    if (!AnySymptom_s.equals(mThermalComfort)) {
                        mThermalComfort = AnySymptom_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // AnySymptom Spinner Ends

    public Double getAverageDepth() {return mAverageDepth;}

    public void setAverageDepth(Double averageDepth) {mAverageDepth = averageDepth;}

    //

    Double getBottomTime() {
        return mBottomTime;
    }

    public String getBottomTimeString() { return MyFunctions.convertToMmSs(mBottomTime); }

    String getBottomTimeStringX() { return mBottomTimeString; }

    public void setBottomTime(Double bottomTime) {mBottomTime = bottomTime;}

    public void setBottomTimeString(String bottomTimeString) {
        String newBottomTimeFormatted = MyFunctions.formatBottomTime(bottomTimeString);
        if (!newBottomTimeFormatted.isEmpty()) {
            // Bottom Time entered is valid, use the one formatted
            mBottomTimeString = newBottomTimeFormatted;
            // Convert the Bottom Time entered into a Double minutes.seconds
            setBottomTime(MyFunctions.convertMmSs(mBottomTimeString));
        } else {
            // Bottom Time entered is invalid, keep the one entered
            mBottomTimeString = bottomTimeString;
        }
    }

    // Condition Spinner

    public String getCondition() {
        return mCondition; }

    public void setCondition(String condition) {
        mCondition = condition;
        //Set the position to select and show the data on the activity
        setConditionPosition(getConditionIndex(mCondition));
    }

    void setConditionLoad(String condition) {
        // Initial load from My History
        // Avoid calling getIndex()
        mCondition= condition;
    }

    public int getConditionPosition() {return mConditionPosition; }

    public void setConditionPosition(int conditionPosition) {
        mConditionPosition = conditionPosition;
    }

    void setItemsCondition(ArrayList<DynamicSpinner> itemsCondition) {
        mItemsCondition = itemsCondition;
    }

    void setAdapterCondition(ArrayAdapter<DynamicSpinner> adapterCondition) { mAdapterCondition = adapterCondition; }

    public ArrayAdapter<DynamicSpinner> getAdapterCondition () {
        return mAdapterCondition;
    }

    private int getConditionIndex(String condition)
    {
        int position = 0;

        for (int i = 0; i < mItemsCondition.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsCondition.get(i);
            if(dynamicSpinner.getSpinnerText().equals(condition)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedCondition() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String condition_s = dynamicSpinner.getSpinnerText();
                    if (!condition_s.equals(mCondition)) {
                        mCondition = condition_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Condition Spinner Ends

    public Date getDate() {return mDate; }

    public void setDate(Date date) {mDate = date;}

    public String getDateString() {return MyFunctions.convertDateFromDateToString(mContext, mDate); }

    public void setDateString(String dateString) {setDate(MyFunctions.convertDateFromStringToDate(mContext, dateString));}

    // Dive Boat AutoCompleteTextView

    public String getDiveBoat() {
        return mDiveBoat; }

    public void setDiveBoat(String diveBoat) {
        mDiveBoat = diveBoat.trim();
    }

    String getDiveBoatOld() {
        return mDiveBoatOld; }

    void setDiveBoatOld(String diveBoatOld) {
        mDiveBoatOld = diveBoatOld.trim();
    }

    // Dive Boat AutoCompleteTextView Ends

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }

    //

    long getMyDiverNo() {return mMyDiverNo;}

    void setMyDiverNo(long myDiverNo) {mMyDiverNo = myDiverNo;}

    //

    int getDivePlanCount() {return mDivePlanCount;}

    void setDivePlanCount(int divePlanCount) {mDivePlanCount = divePlanCount;}

    // Dive Site AutoCompleteTextView

    public String getDiveSite() {
        return mDiveSite; }

    public void setDiveSite(String diveSite) {
        mDiveSite = diveSite.trim();
    }

    String getDiveSiteOld() {
        return mDiveSiteOld; }

    void setDiveSiteOld(String diveSiteOld) {
        mDiveSiteOld = diveSiteOld.trim();
    }

    // Dive Site AutoCompleteTextView Ends

    // DiveType Spinner

    public String getDiveType() {
        return mDiveType; }

    public void setDiveType(String diveType) {
        mDiveType = diveType;
        //Set the position to select and show the data on the activity
        setDiveTypePosition(getDiveTypeIndex(mDiveType));
    }

    String getDiveTypeDesc() {return mDiveTypeDesc;}

    private void setDiveTypeDesc(String diveTypeDesc) {mDiveTypeDesc = diveTypeDesc;}

    void setDiveTypeLoad(String diveType) {
        // Initial load from My History
        // Avoid calling getIndex()
        mDiveType = diveType;
    }

    public int getDiveTypePosition() {return mDiveTypePosition; }

    public void setDiveTypePosition(int diveTypePosition) {
        mDiveTypePosition = diveTypePosition;
        // Get the dive type description
        DiveType diveType;
        diveType = mItemsDiveType.get(diveTypePosition - 1);
        setDiveTypeDesc(diveType.getDescription());
    }

    void setItemsDiveType(ArrayList<DiveType> itemsDiveType) {
        mItemsDiveType = itemsDiveType;
    }

    void setAdapterDiveType(ArrayAdapter<DiveType> adapterDiveType) { mAdapterDiveType = adapterDiveType; }

    public ArrayAdapter<DiveType> getAdapterDiveType () {
        return mAdapterDiveType;
    }

    private int getDiveTypeIndex(String diveType)
    {
        int position = 0;

        for (int i = 0; i < mItemsDiveType.size(); i++) {
            DiveType diveType_s = mItemsDiveType.get(i);
            if(diveType_s.getDiveType().equals(diveType)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedDiveType() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DiveType diveType = (DiveType) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String diveType_s = diveType.getDiveType();
                    if (!diveType_s.equals(mDiveType)) {
                        mDiveType = diveType_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // DiveType Spinner Ends

    // Environment Spinner

    public String getEnvironment() {
        return mEnvironment; }

    public void setEnvironment(String environment) {
        mEnvironment = environment;
        //Set the position to select and show the data on the activity
        setEnvironmentPosition(getEnvironmentIndex(mEnvironment));
    }

    void setEnvironmentLoad(String environment) {
        // Initial load from My History
        // Avoid calling getIndex()
        mEnvironment = environment;
    }

    public int getEnvironmentPosition() {return mEnvironmentPosition; }

    public void setEnvironmentPosition(int environmentPosition) {
        mEnvironmentPosition = environmentPosition;
    }

    public int getExtraDiversCount() {return mExtraDiversCount; }

    public void setExtraDiversCount(int extraDiversCount) {
        mExtraDiversCount = extraDiversCount;
    }

    void setItemsEnvironment(ArrayList<DynamicSpinner> itemsEnvironment) {
        mItemsEnvironment = itemsEnvironment;
    }

    void setAdapterEnvironment(ArrayAdapter<DynamicSpinner> adapterEnvironment) { mAdapterEnvironment = adapterEnvironment; }

    public ArrayAdapter<DynamicSpinner> getAdapterEnvironment () {
        return mAdapterEnvironment;
    }

    private int getEnvironmentIndex(String environment)
    {
        int position = 0;

        for (int i = 0; i < mItemsEnvironment.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsEnvironment.get(i);
            if(dynamicSpinner.getSpinnerText().equals(environment)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedEnvironment() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String environment_s = dynamicSpinner.getSpinnerText();
                    if (!environment_s.equals(mEnvironment)) {
                        mEnvironment = environment_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Environment Spinner Ends

    // ExposureAltitude Spinner

    public String getExposureAltitude() {
        return mExposureAltitude; }

    public void setExposureAltitude(String exposureAltitude) {
        mExposureAltitude = exposureAltitude;
        //Set the position to select and show the data on the activity
        setExposureAltitudePosition(getExposureAltitudeIndex(mExposureAltitude));
    }

    void setExposureAltitudeLoad(String exposureAltitude) {
        // Initial load from My History
        // Avoid calling getIndex()
        mExposureAltitude = exposureAltitude;
    }

    void setItemsExposureAltitude(ArrayList<DynamicSpinner> itemsExposureAltitude) {
        mItemsExposureAltitude = itemsExposureAltitude;
    }

    public int getExposureAltitudePosition() {return mExposureAltitudePosition; }

    public void setExposureAltitudePosition(int ExposureAltitudePosition) {mExposureAltitudePosition = ExposureAltitudePosition;}

    void setAdapterExposureAltitude(ArrayAdapter<DynamicSpinner> adapterExposureAltitude) {mAdapterExposureAltitude = adapterExposureAltitude;}

    public ArrayAdapter<DynamicSpinner> getAdapterExposureAltitude () {return mAdapterExposureAltitude;}

    private int getExposureAltitudeIndex(String exposureAltitude)
    {
        int position = 0;

        for (int i = 0; i < mItemsExposureAltitude.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsExposureAltitude.get(i);
            if(dynamicSpinner.getSpinnerText().equals(exposureAltitude)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedExposureAltitude() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String exposureAltitude_s = dynamicSpinner.getSpinnerText();
                    if (!exposureAltitude_s.equals(mSuit)) {
                        mExposureAltitude = exposureAltitude_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // ExposureAltitude Spinner Ends

    public  boolean getFresh() {
        return !mSalinity;
    }

    //

    int getHour() {return mHour;}

    void setHour(int hour) {mHour = hour;}

    // Location AutoCompleteTextView

    public String getLocation() {
        return mLocation; }

    public void setLocation(String location) {
        mLocation = location.trim();
    }

    String getLocationOld() {
        return mLocationOld; }

    void setLocationOld(String locationOld) {
        mLocationOld = locationOld.trim();
    }

    // Location AutoCompleteTextView Ends

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    // Malfunction Spinner

    public String getMalfunction() {
        return mMalfunction; }

    public void setMalfunction(String malfunction) {
        mMalfunction = malfunction;
        //Set the position to select and show the data on the activity
        setMalfunctionPosition(getMalfunctionIndex(mMalfunction));
    }

    void setMalfunctionLoad(String malfunction) {
        // Initial load from My History
        // Avoid calling getIndex()
        mMalfunction = malfunction;
    }

    public int getMalfunctionPosition() {return mMalfunctionPosition; }

    public void setMalfunctionPosition(int malfunctionPosition) {
        mMalfunctionPosition = malfunctionPosition;
    }

    void setItemsMalfunction(ArrayList<DynamicSpinner> itemsMalfunction) {
        mItemsMalfunction = itemsMalfunction;
    }

    void setAdapterMalfunction(ArrayAdapter<DynamicSpinner> adapterMalfunction) { mAdapterMalfunction = adapterMalfunction; }

    public ArrayAdapter<DynamicSpinner> getAdapterMalfunction () {
        return mAdapterMalfunction;
    }

    private int getMalfunctionIndex(String malfunction)
    {
        int position = 0;

        for (int i = 0; i < mItemsMalfunction.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsMalfunction.get(i);
            if(dynamicSpinner.getSpinnerText().equals(malfunction)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedMalfunction() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String malfunction_s = dynamicSpinner.getSpinnerText();
                    if (!malfunction_s.equals(mThermalComfort)) {
                        mThermalComfort = malfunction_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Malfunction Spinner Ends

    public Double getMaximumDepth() {return mMaximumDepth;}

    public void setMaximumDepth(Double maximumDepth) {mMaximumDepth = maximumDepth;}

    //

    void setMeLabel(String meLabel) {mMeLabel = meLabel;}

    //

    public int getMinute() {return mMinute;}

    public void setMinute(int minute) {mMinute = minute;}

    //

    long getMyBuddyDiverNo() {return mBuddyDiverNo;}

    void setMyBuddyDiverNo(long buddyDiverNo) {mBuddyDiverNo = buddyDiverNo;}

    //

    void setMyBuddyEndingPressure(Double myBuddyEndingPressure) {mMyBuddyEndingPressure = myBuddyEndingPressure;}

    //

    public String getMyBuddyFullName() {return mMyBuddyFullName;}

    void setMyBuddyFullName(String myBuddyFullName) {mMyBuddyFullName = myBuddyFullName;}

    //

    public String getMyBuddyGroup() {return mMyBuddyGroup;}

    void setMyBuddyGroup(String myBuddyGroup) {mMyBuddyGroup = myBuddyGroup;}

    //

    long getMyBuddyGroupNo() {return mMyBuddyGroupNo;}

    void setMyBuddyGroupNo(long myBuddyGroupNo) {
        mMyBuddyGroupNo = myBuddyGroupNo;
    }

    //

    private Double getMyBuddyPressure() {return mMyBuddyPressure;}

    void setMyBuddyPressure(Double myBuddyPressure) {mMyBuddyPressure = myBuddyPressure;}

    //

    Double getMyBuddyRatedPressure() {return mMyBuddyRatedPressure;}

    void setMyBuddyRatedPressure(Double myBuddyRatedPressure) {mMyBuddyRatedPressure = myBuddyRatedPressure;}

    //

    Double getMyBuddyRatedVolume() {return mMyBuddyRatedVolume;}

    void setMyBuddyRatedVolume(Double myBuddyRatedVolume) {mMyBuddyRatedVolume = myBuddyRatedVolume;}

    //

    public Double getMyBuddySac() {return mMyBuddySac;}

    void setMyBuddySac(Double buddySac) {
        mMyBuddySac = buddySac;
    }

    //

    public String getMyBuddyRealConsumption() {
        String pressureUnit;
        String volumeUnit;
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }
        pressureUnit = myCalc.getPressureUnit();
        volumeUnit = myCalc.getVolumeUnit();
        if (!mMyBuddyEndingPressure.equals(MyConstants.ZERO_D)) {
            return getMyBuddyPressure() + " " + pressureUnit + " & " + getMyBuddyVolume() + " " + volumeUnit;
        } else {
            return "";
        }
    }

    //

    public Double getMyBuddyRmv() {return mMyBuddyRmv;}

    public void setMyBuddyRmv(Double buddyRmv) {
        mMyBuddyRmv = buddyRmv;
    }

    //

    private Double getMyBuddyVolume() {return mMyBuddyVolume;}

    void setMyBuddyVolume(Double myBuddyVolume) {mMyBuddyVolume = myBuddyVolume;}

    //

    void setMyEndingPressure(Double myEndingPressure) {mMyEndingPressure = myEndingPressure;}

    //

    public String getMyGroup() {return mMyGroup;}

    void setMyGroup(String myGroup) {mMyGroup = myGroup;}

    //

    long getMyGroupNo() {return mMyGroupNo;}

    void setMyGroupNo(long myGroupNo) {mMyGroupNo = myGroupNo;}

    //

    private Double getMyPressure() {return mMyPressure;}

    void setMyPressure(Double myPressure) {mMyPressure = myPressure;}

    //

    Double getMyRatedPressure() {return mMyRatedPressure;}

    void setMyRatedPressure(Double myRatedPressure) {mMyRatedPressure = myRatedPressure;}

    //

    Double getMyRatedVolume() {return mMyRatedVolume;}

    void setMyRatedVolume(Double myRatedVolume) {mMyRatedVolume = myRatedVolume;}

    //

    public String getMyRealConsumption() {
        String pressureUnit;
        String volumeUnit;
        MyCalc myCalc;
        if ( MyFunctions.getUnit().equals(MyConstants.IMPERIAL)) {
            myCalc = new MyCalcImperial(mContext);
        } else {
            myCalc = new MyCalcMetric(mContext);
        }
        pressureUnit = myCalc.getPressureUnit();
        volumeUnit = myCalc.getVolumeUnit();
        if (!mMyEndingPressure.equals(MyConstants.ZERO_D)) {
            return getMyPressure() + " " + pressureUnit + " & " + getMyVolume() + " " + volumeUnit;
        } else {
            return "";
        }
    }

    //

    public Double getMyRmv() {return mMyRmv;}

    public void setMyRmv(Double myRmv) { mMyRmv = myRmv;}

    //

    public Double getMySac() {return mMySac;}

    void setMySac(Double mySac) { mMySac = mySac;}

    //

    private Double getMyVolume() {return mMyVolume;}

    void setMyVolume(Double myVolume) {mMyVolume = myVolume;}

    //

    public String getNoteEnvironment() {return mNoteEnvironment;}

    public void setNoteEnvironment(String noteEnvironment) {mNoteEnvironment = noteEnvironment;}

    //

    public String getNoteGas() {return mNoteGas;}

    public void setNoteGas(String noteGas) {mNoteGas = noteGas;}

    //

    public String getNoteGear() {return mNoteGear;}

    public void setNoteGear(String noteGear) {mNoteGear = noteGear;}

    //

    public String getNoteProblem() {return mNoteProblem;}

    public void setNoteProblem(String noteProblem) {mNoteProblem = noteProblem;}

    //

    public String getNoteSummary() {return mNoteSummary;}

    public void setNoteSummary(String noteSummary) {mNoteSummary = noteSummary;}

    //

    public String getPlanning() {return mPlanning;}

    void setPlanning(String planning) {mPlanning = planning;}

    // Platform Spinner

    public String getPlatform() {
        return mPlatform; }

    public void setPlatform(String platform) {
        mPlatform = platform;
        //Set the position to select and show the data on the activity
        setPlatformPosition(getPlatformIndex(mPlatform));
    }

    void setPlatformLoad(String platform) {
        // Initial load from My History
        // Avoid calling getIndex()
        mPlatform = platform;
    }

    public int getPlatformPosition() {return mPlatformPosition; }

    public void setPlatformPosition(int platformPosition) {
        mPlatformPosition = platformPosition;
    }

    void setItemsPlatform(ArrayList<DynamicSpinner> itemsPlatform) {
        mItemsPlatform = itemsPlatform;
    }

    void setAdapterPlatform(ArrayAdapter<DynamicSpinner> adapterPlatform) { mAdapterPlatform = adapterPlatform; }

    public ArrayAdapter<DynamicSpinner> getAdapterPlatform () {
        return mAdapterPlatform;
    }

    private int getPlatformIndex(String mPlatform)
    {
        int position = 0;

        for (int i = 0; i < mItemsPlatform.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsPlatform.get(i);
            if(dynamicSpinner.getSpinnerText().equals(mPlatform)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedPlatform() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String platform_s = dynamicSpinner.getSpinnerText();
                    if (!platform_s.equals(mPlatform)) {
                        mPlatform = platform_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Platform Spinner Ends

    // Problem Spinner

    public String getProblem() {
        return mProblem; }

    public void setProblem(String problem) {
        mProblem = problem;
        //Set the position to select and show the data on the activity
        setProblemPosition(getProblemIndex(mProblem));
    }

    void setProblemLoad(String problem) {
        // Initial load from My History
        // Avoid calling getIndex()
        mProblem = problem;
    }

    public int getProblemPosition() {return mProblemPosition; }

    public void setProblemPosition(int problemPosition) {
        mProblemPosition = problemPosition;
    }

    void setItemsProblem(ArrayList<DynamicSpinner> itemsProblem) {
        mItemsProblem = itemsProblem;
    }

    void setAdapterProblem(ArrayAdapter<DynamicSpinner> adapterProblem) { mAdapterProblem = adapterProblem; }

    public ArrayAdapter<DynamicSpinner> getAdapterProblem () {
        return mAdapterProblem;
    }

    private int getProblemIndex(String problem)
    {
        int position = 0;

        for (int i = 0; i < mItemsProblem.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsProblem.get(i);
            if(dynamicSpinner.getSpinnerText().equals(problem)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedProblem() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String problem_s = dynamicSpinner.getSpinnerText();
                    if (!problem_s.equals(mThermalComfort)) {
                        mThermalComfort = problem_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Problem Spinner Ends

    public String getPurpose() {return mPurpose;}

    public void setPurpose(String purpose) {mPurpose = purpose;}

    //

    public boolean getSalinity() {return mSalinity;}

    public void setSalinity(boolean salinity) {mSalinity = salinity;}

    // Status Spinner

    public String getStatus() {
        return mStatus; }

    public void setStatus(String status) {
        mStatus = status;
        //Set the position to select and show the data on the activity
        setStatusPosition(getStatusIndex(mStatus));
    }

    void setStatusLoad(String status) {
        // Initial load from My History
        // Avoid calling getIndex()
        mStatus = status;
    }

    void setItemsStatus(String[] itemsStatus) {
        mItemsStatus = itemsStatus;
    }

    public int getStatusPosition() {return mStatusPosition; }

    public void setStatusPosition(int statusPosition) {mStatusPosition = statusPosition;}

    void setAdapterStatus(ArrayAdapter<String> adapterStatus) {mAdapterStatus = adapterStatus;}

    public ArrayAdapter<String> getAdapterStatus () {return mAdapterStatus;}

    private int getStatusIndex(String status)
    {
        int position = 0;

        for (int i = 0; i < mItemsStatus.length; i++) {

            if (mItemsStatus[i].equalsIgnoreCase(status)){
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedStatus() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    String status = String.valueOf(parent.getAdapter().getItem(position + HINT_OFFSET));
                    if (!status.equals(mStatus)) {
                        mStatus = status;
                        setHasDataChanged(true);
                        mBindingPlanning.myRealConsumption.setText(getMyRealConsumption());
                        mBindingPlanning.myBuddyRealConsumption.setText(getMyBuddyRealConsumption());
                        if (mStatus.equals(MyConstants.REAL) || mStatus.equals(MyConstants.REEL)) {
                            checkRequiredFields();
                        }
                    }
                }
            }
        };
    }

    // Status Spinner ends

    // Suit Spinner

    public String getSuit() {
        return mSuit; }

    public void setSuit(String suit) {
        mSuit = suit;
        //Set the position to select and show the data on the activity
        setSuitPosition(getSuitIndex(mSuit));
    }

    void setSuitLoad(String suit) {
        // Initial load from My History
        // Avoid calling getIndex()
        mSuit = suit;
    }

    public int getSuitPosition() {return mSuitPosition; }

    public void setSuitPosition(int suitPosition) {
        mSuitPosition = suitPosition;
    }

    void setItemsSuit(ArrayList<DynamicSpinner> itemsSuit) {
        mItemsSuit = itemsSuit;
    }

    void setAdapterSuit(ArrayAdapter<DynamicSpinner> adapterSuit) { mAdapterSuit = adapterSuit; }

    public ArrayAdapter<DynamicSpinner> getAdapterSuit () {
        return mAdapterSuit;
    }

    private int getSuitIndex(String suit)
    {
        int position = 0;

        for (int i = 0; i < mItemsSuit.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsSuit.get(i);
            if(dynamicSpinner.getSpinnerText().equals(suit)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedSuit() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String suit_s = dynamicSpinner.getSpinnerText();
                    if (!suit_s.equals(mSuit)) {
                        mSuit = suit_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Suit Spinner Ends

    // Thermal Comfort Spinner

    public String getThermalComfort() {
        return mThermalComfort; }

    public void setThermalComfort(String thermalComfort) {
        mThermalComfort = thermalComfort;
        //Set the position to select and show the data on the activity
        setThermalComfortPosition(getThermalComfortIndex(mThermalComfort));
    }

    void setThermalComfortLoad(String thermalComfort) {
        // Initial load from My History
        // Avoid calling getIndex()
        mThermalComfort = thermalComfort;
    }

    public int getThermalComfortPosition() {return mThermalComfortPosition; }

    public void setThermalComfortPosition(int thermalComfortPosition) {
        mThermalComfortPosition = thermalComfortPosition;
    }

    void setItemsThermalComfort(ArrayList<DynamicSpinner> itemsThermalComfort) {
        mItemsThermalComfort = itemsThermalComfort;
    }

    void setAdapterThermalComfort(ArrayAdapter<DynamicSpinner> adapterThermalComfort) { mAdapterThermalComfort = adapterThermalComfort; }

    public ArrayAdapter<DynamicSpinner> getAdapterThermalComfort () {
        return mAdapterThermalComfort;
    }

    private int getThermalComfortIndex(String thermalComfort)
    {
        int position = 0;

        for (int i = 0; i < mItemsThermalComfort.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsThermalComfort.get(i);
            if(dynamicSpinner.getSpinnerText().equals(thermalComfort)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedThermalComfort() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String thermalComfort_s = dynamicSpinner.getSpinnerText();
                    if (!thermalComfort_s.equals(mThermalComfort)) {
                        mThermalComfort = thermalComfort_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Thermal Comfort Spinner

    public String getTimeIn() {return mTimeIn;}

    public void setTimeIn(String timeIn) {mTimeIn = timeIn;}

    //

    public String getVisibility() {return mVisibility;}

    public void setVisibility(String visibility) {mVisibility = visibility;}

    //

    public Double getWaterTempAverage() {return mWaterTempAverage;}

    public void setWaterTempAverage(Double waterTempAverage) { mWaterTempAverage = waterTempAverage;}

    //

    public Double getWaterTempBottom() {return mWaterTempBottom;}

    public void setWaterTempBottom(Double waterTempBottom) { mWaterTempBottom = waterTempBottom;}

    //

    public Double getWaterTempSurface() {return mWaterTempSurface;}

    public void setWaterTempSurface(Double waterTempSurface) { mWaterTempSurface = waterTempSurface;}

    // Weather Spinner

    public String getWeather() {
        return mWeather; }

    public void setWeather(String weather) {
        mWeather = weather;
        //Set the position to select and show the data on the activity
        setWeatherPosition(getWeatherIndex(mWeather));
    }

    void setWeatherLoad(String weather) {
        // Initial load from My History
        // Avoid calling getIndex()
        mWeather = weather;
    }

    public int getWeatherPosition() {return mWeatherPosition; }

    public void setWeatherPosition(int weatherPosition) {
        mWeatherPosition = weatherPosition;
    }

    void setItemsWeather(ArrayList<DynamicSpinner> itemsWeather) {
        mItemsWeather = itemsWeather;
    }

    void setAdapterWeather(ArrayAdapter<DynamicSpinner> adapterWeather) { mAdapterWeather = adapterWeather; }

    public ArrayAdapter<DynamicSpinner> getAdapterWeather () {
        return mAdapterWeather;
    }

    private int getWeatherIndex(String weather)
    {
        int position = 0;

        for (int i = 0; i < mItemsWeather.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsWeather.get(i);
            if(dynamicSpinner.getSpinnerText().equals(weather)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedWeather() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String weather_s = dynamicSpinner.getSpinnerText();
                    if (!weather_s.equals(mWeather)) {
                        mWeather = weather_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Weather Spinner Ends

    // WorkLoad Spinner

    public String getWorkLoad() {
        return mWorkLoad; }

    public void setWorkLoad(String workLoad) {
        mWorkLoad = workLoad;
        //Set the position to select and show the data on the activity
        setWorkLoadPosition(getWorkLoadIndex(mWorkLoad));
    }

    void setWorkLoadLoad(String workLoad) {
        // Initial load from My History
        // Avoid calling getIndex()
        mWorkLoad = workLoad;
    }

    public int getWorkLoadPosition() {return mWorkLoadPosition; }

    public void setWorkLoadPosition(int workLoadPosition) {
        mWorkLoadPosition = workLoadPosition;
    }

    void setItemsWorkLoad(ArrayList<DynamicSpinner> itemsWorkLoad) {
        mItemsWorkLoad = itemsWorkLoad;
    }

    void setAdapterWorkLoad(ArrayAdapter<DynamicSpinner> adapterWorkLoad) { mAdapterWorkLoad = adapterWorkLoad; }

    public ArrayAdapter<DynamicSpinner> getAdapterWorkLoad () {
        return mAdapterWorkLoad;
    }

    private int getWorkLoadIndex(String workLoad)
    {
        int position = 0;

        for (int i = 0; i < mItemsWorkLoad.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsWorkLoad.get(i);
            if(dynamicSpinner.getSpinnerText().equals(workLoad)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedWorkLoad() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    DynamicSpinner dynamicSpinner = (DynamicSpinner) parent.getAdapter().getItem(position + HINT_OFFSET);
                    String workLoad_s = dynamicSpinner.getSpinnerText();
                    if (!workLoad_s.equals(mThermalComfort)) {
                        mThermalComfort = workLoad_s;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // WorkLoad Spinner Ends

    public Double getWeight() {return mWeight;}

    public void setWeight(Double weight) { mWeight = weight;}

    // My functions

    private void checkRequiredFields() {
        AirDA airDa = new AirDA(mContext);
        airDa.open();

        // Me
        if (this.getMyGroupNo() != MyConstants.ZERO_L) {
            if (airDa.getSumEndingPressure(this.getMyDiverNo(), this.getDiveNo()).equals(MyConstants.ZERO_D)) {
                mBindingPlanning.myConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.purple));
            } else {
                mBindingPlanning.myConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.theme_myapp_action_bar));
            }
        } else {
            mBindingPlanning.myConsumption.setTextColor(Color.BLACK);
        }

        // My Buddy
        if (this.getMyBuddyGroupNo() != MyConstants.ZERO_L) {
            mBindingPlanning.myBuddyConsumption.setEnabled(true);
            if (airDa.getSumEndingPressure(this.getMyBuddyDiverNo(), this.getDiveNo()).equals(MyConstants.ZERO_D)) {
                mBindingPlanning.myBuddyConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.purple));
            } else {
                mBindingPlanning.myBuddyConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.theme_myapp_action_bar));
            }
        } else {
            mBindingPlanning.myBuddyConsumption.setTextColor(Color.BLACK);
        }
    }

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) { mHasDataChanged = hasDataChanged;}

    //

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}

    //

    public void setChecked(boolean checked) {mChecked = checked;}

    @Bindable
    public boolean getChecked() {return mChecked;}

    //

    @Bindable
    public TextWatcher getOnTextChanged() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                setHasDataChanged(true);
            }
        };
    }

    //

    @Bindable
    public RadioGroup.OnCheckedChangeListener getOnRadioGroupChanged() {
        return new MyRadioGroupWatcher() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                super.onCheckedChanged(group, checkedId);
                setHasDataChanged(true);
            }
        };
    }

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dive dive = (Dive) o;

        return mDiveNo.equals(dive.mDiveNo);
    }

    @Override
    public int hashCode() {
        return (int) (mDiveNo ^ (mDiveNo >>> 32));
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mSalinity ? (byte) 1 : (byte) 0);
        dest.writeLong(this.mDate != null ? this.mDate.getTime() : -1);
        dest.writeValue(this.mAverageDepth);
        dest.writeValue(this.mMyBuddyRatedPressure);
        dest.writeValue(this.mMyBuddyRatedVolume);
        dest.writeValue(this.mMyBuddyPressure);
        dest.writeValue(this.mMyBuddyVolume);
        dest.writeValue(this.mMyBuddyRmv);
        dest.writeValue(this.mMyBuddySac);
        dest.writeValue(this.mMyRmv);
        dest.writeValue(this.mMySac);
        dest.writeValue(this.mMyRatedPressure);
        dest.writeValue(this.mMyRatedVolume);
        dest.writeValue(this.mMyPressure);
        dest.writeValue(this.mMyVolume);
        dest.writeInt(this.mAltitude);
        dest.writeValue(this.mBottomTime);
        dest.writeInt(this.mDivePlanCount);
        dest.writeInt(this.mDiveTypePosition);
        dest.writeInt(this.mLogBookNo);
        dest.writeInt(this.mStatusPosition);
        dest.writeInt(this.mHour);
        dest.writeInt(this.mMinute);
        dest.writeValue(this.mBuddyDiverNo);
        dest.writeValue(this.mMyGroupNo);
        dest.writeValue(this.mMyBuddyGroupNo);
        dest.writeValue(this.mDiveNo);
        dest.writeValue(this.mMyDiverNo);
        dest.writeString(this.mDiveType);
        dest.writeString(this.mDiveTypeDesc);
        dest.writeString(this.mMeLabel);
        dest.writeString(this.mMyBuddyGroup);
        dest.writeString(this.mMyBuddyFullName);
        dest.writeString(this.mMyGroup);
        dest.writeString(this.mStatus);
        dest.writeString(this.mTimeIn);
        dest.writeString(this.mPlanning);
        // 2020/03/22 New columns DB_VERSION = 2
        dest.writeString(this.mDiveBoat);
        dest.writeString(this.mDiveSite);
        dest.writeString(this.mLocation);
        dest.writeString(this.mPurpose);
        dest.writeString(this.mBottomTimeString);
        dest.writeString(this.mVisibility);
        dest.writeValue(this.mMaximumDepth);
        dest.writeString(this.mSuit);
        dest.writeValue(this.mWeight);
        dest.writeValue(this.mAirTemp);
        dest.writeValue(this.mWaterTempSurface);
        dest.writeValue(this.mWaterTempBottom);
        dest.writeValue(this.mWaterTempAverage);
        // 2023/08/2 DB_VERSION = 7 Renamed from NOTE to NOTE_SUMMARY
        dest.writeString(this.mNoteSummary);
        dest.writeString(this.mEnvironment);
        dest.writeString(this.mPlatform);
        dest.writeString(this.mWeather);
        dest.writeString(this.mCondition);
        // 2023/08/2 DB_VERSION = 7 New columns
        dest.writeString(this.mNoteEnvironment);
        dest.writeString(this.mNoteGas);
        dest.writeString(this.mNoteGear);
        dest.writeString(this.mThermalComfort);
        dest.writeString(this.mWorkLoad);
        dest.writeString(this.mProblem);
        dest.writeString(this.mMalfunction);
        dest.writeString(this.mAnySymptom);
        dest.writeString(this.mExposureAltitude);
        dest.writeString(this.mNoteProblem);
    }

    protected Dive(Parcel in) {
        this.mSalinity = in.readByte() != 0;
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mAverageDepth = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddyRatedPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddyRatedVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddyPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddyVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddyRmv = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddySac = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyRmv = (Double) in.readValue(Double.class.getClassLoader());
        this.mMySac = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyRatedPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyRatedVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mAltitude = in.readInt();
        this.mBottomTime = (Double) in.readValue(Double.class.getClassLoader());
        this.mDivePlanCount = in.readInt();
        this.mDiveTypePosition = in.readInt();
        this.mLogBookNo = in.readInt();
        this.mStatusPosition = in.readInt();
        this.mHour = in.readInt();
        this.mMinute = in.readInt();
        this.mBuddyDiverNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mMyGroupNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mMyBuddyGroupNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiveNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mMyDiverNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiveType = in.readString();
        this.mDiveTypeDesc = in.readString();
        this.mMeLabel = in.readString();
        this.mMyBuddyGroup = in.readString();
        this.mMyBuddyFullName = in.readString();
        this.mMyGroup = in.readString();
        this.mStatus = in.readString();
        this.mTimeIn = in.readString();
        this.mPlanning = in.readString();
        // 2020/03/22 New columns DB_VERSION = 2
        this.mDiveBoat = in.readString();
        this.mDiveSite = in.readString();
        this.mLocation = in.readString();
        this.mPurpose = in.readString();
        this.mBottomTimeString = in.readString();
        this.mVisibility = in.readString();
        this.mMaximumDepth  = (Double) in.readValue(Double.class.getClassLoader());
        this.mSuit = in.readString();
        this.mWeight = (Double) in.readValue(Double.class.getClassLoader());
        this.mAirTemp  = (Double) in.readValue(Double.class.getClassLoader());
        this.mWaterTempSurface = (Double) in.readValue(Double.class.getClassLoader());
        this.mWaterTempBottom = (Double) in.readValue(Double.class.getClassLoader());
        this.mWaterTempAverage = (Double) in.readValue(Double.class.getClassLoader());
        // 2023/08/2 DB_VERSION = 7 Renamed from NOTE to NOTE_SUMMARY
        this.mNoteSummary = in.readString();
        this.mEnvironment = in.readString();
        this.mPlatform = in.readString();
        this.mWeather  = in.readString();
        this.mCondition = in.readString();
        // 2023/08/2 DB_VERSION = 7 New columns
        this.mNoteEnvironment = in.readString();
        this.mNoteGas = in.readString();
        this.mNoteGear = in.readString();
        this.mThermalComfort  = in.readString();
        this.mWorkLoad = in.readString();
        this.mProblem = in.readString();
        this.mMalfunction = in.readString();
        this.mAnySymptom = in.readString();
        this.mExposureAltitude  = in.readString();
        this.mNoteProblem = in.readString();
    }

    public static final Creator<Dive> CREATOR = new Creator<Dive>() {
        @Override
        public Dive createFromParcel(Parcel source) {
            return new Dive(source);
        }

        @Override
        public Dive[] newArray(int size) {
            return new Dive[size];
        }
    };
}
