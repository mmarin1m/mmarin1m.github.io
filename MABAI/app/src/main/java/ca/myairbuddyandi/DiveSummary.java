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

import ca.myairbuddyandi.databinding.DiveSummaryBinding;

/**
 * Created by Michel on 2016-11-29.
 * Holds all of the logic for the Dive class
 *
 * ??
 */

public class DiveSummary extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Dive";

    // Public
    public DiveSummaryBinding mBinding = null;
    private static final int HINT_OFFSET = 1;

    // Protected

    // Private
    private boolean mSalinity; // true = 1 = Salt, false = 0 = Fresh
    private Date mDate;
    private Double mAverageDepth;
    private Double mBottomTime;
    private Double mMyBuddyRatedPressure;
    private Double mMyBuddyRatedVolume;
    private Double mMyBuddyEndingPressure;
    private Double mMyBuddyPressure;
    private Double mMyBuddyVolume;
    private Double mMyBuddyRmv;
    private Double mMyBuddySac;
    private Double mMyRmv;
    private Double mMySac;
    private Double mMyRatedPressure;
    private Double mMyRatedVolume;
    private Double mMyEndingPressure;
    private Double mMyPressure;
    private Double mMyVolume;
    private int mAltitude;
    private int mConditionPosition;
    private int mDivePlanCount;
    private int mDiveTypePosition;
    private int mEnvironmentPosition;
    private int mHour;
    private int mLogBookNo;
    private int mMinute;
    private int mPlatformPosition;
    private int mStatusPosition;
    private int mSuitPosition;
    private int mWeatherPosition;
    private Long mBuddyDiverNo;
    private Long mMyGroupNo;
    private Long mMyBuddyGroupNo;
    private Long mDiveNo;
    private Long mMyDiverNo;
    private String mDiveType;
    private String mDiveTypeDesc;
    private String mMeLabel;
    private String mMyBuddyGroup;
    private String mMyBuddyFullName;
    private String mMyGroup;
    private String mPlanning;
    private String mStatus;
    private String mTimeIn;
    // 2020/03/22 New columns DB_VERSION = 2
    private Double mAirTemp;
    private Double mMaximumDepth;
    private Double mWaterTempAverage;
    private Double mWaterTempBottom;
    private Double mWaterTempSurface;
    private Double mWeight;
    private String mBottomTimeString;
    private String mCondition;
    private String mDiveBoat;
    private String mDiveBoatOld;
    private String mDiveSite;
    private String mDiveSiteOld;
    private String mEnvironment;
    private String mLocation;
    private String mLocationOld;
    private String mNote;
    private String mPlatform;
    private String mPurpose;
    private String mSuit;
    private String mVisibility;
    private String mWeather;

    // Excluded from Parcelable

    private ArrayAdapter<DiveType> mAdapterDiveType;
    private ArrayAdapter<DynamicSpinner> mAdapterCondition;
    private ArrayAdapter<DynamicSpinner> mAdapterEnvironment;
    private ArrayAdapter<DynamicSpinner> mAdapterPlatform;
    private ArrayAdapter<DynamicSpinner> mAdapterSuit;
    private ArrayAdapter<DynamicSpinner> mAdapterWeather;
    private ArrayAdapter<String> mAdapterStatus;
    private ArrayList<DiveType> mItemsDiveType;
    private ArrayList<DynamicSpinner> mItemsCondition;
    private ArrayList<DynamicSpinner> mItemsEnvironment;
    private ArrayList<DynamicSpinner> mItemsPlatform;
    private ArrayList<DynamicSpinner> mItemsSuit;
    private ArrayList<DynamicSpinner> mItemsWeather;
    private Boolean mHasDataChanged = false;
    private boolean mVisible = false;
    private boolean mChecked = false;
    private transient Context mContext;
    private String[] mItemsStatus;

    // End of variables

    // Public constructor
    public DiveSummary() {
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

    //

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

    //

    public Date getDate() {return mDate; }

    public void setDate(Date date) {mDate = date;}

    public String getDateString() {return MyFunctions.convertDateFromDateToString(mContext, mDate); }

    public void setDateString(String dateString) {setDate(MyFunctions.convertDateFromStringToDate(mContext, dateString));}

    //

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

    //

    public  boolean getFresh() {
        return !mSalinity;
    }

    //

    int getHour() {return mHour;}

    void setHour(int hour) {mHour = hour;}

    //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

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

    public String getNote() {return mNote;}

    public void setNote(String note) {mNote = note;}

    //

    public String getPlanning() {return mPlanning;}

    void setPlanning(String planning) {mPlanning = planning;}

    //

    public String getPurpose() {return mPurpose;}

    public void setPurpose(String purpose) {mPurpose = purpose;}

    //

    public boolean getSalinity() {return mSalinity;}

    public void setSalinity(boolean salinity) {mSalinity = salinity;}

    //

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

    //

    public Double getWeight() {return mWeight;}

    public void setWeight(Double weight) { mWeight = weight;}

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
                        mBinding.myRealConsumption.setText(getMyRealConsumption());
                        mBinding.myBuddyRealConsumption.setText(getMyBuddyRealConsumption());
                        if (mStatus.equals(MyConstants.REAL) || mStatus.equals(MyConstants.REEL)) {
                            checkRequiredFields();
                        }
                    }
                }
            }
        };
    }

    // Status Spinner ends

    private void checkRequiredFields() {
        AirDA airDa = new AirDA(mContext);
        airDa.open();

        // Me
        if (this.getMyGroupNo() != MyConstants.ZERO_L) {
            if (airDa.getSumEndingPressure(this.getMyDiverNo(), this.getDiveNo()).equals(MyConstants.ZERO_D)) {
                mBinding.myConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.purple));
            } else {
                mBinding.myConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.theme_myapp_action_bar));
            }
        } else {
            mBinding.myConsumption.setTextColor(Color.BLACK);
        }

        // My Buddy
        if (this.getMyBuddyGroupNo() != MyConstants.ZERO_L) {
            mBinding.myBuddyConsumption.setEnabled(true);
            if (airDa.getSumEndingPressure(this.getMyBuddyDiverNo(), this.getDiveNo()).equals(MyConstants.ZERO_D)) {
                mBinding.myBuddyConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.purple));
            } else {
                mBinding.myBuddyConsumption.setTextColor(ContextCompat.getColor(mContext, R.color.theme_myapp_action_bar));
            }
        } else {
            mBinding.myBuddyConsumption.setTextColor(Color.BLACK);
        }
    }

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

    private int getDiveTypeIndex(String mDiveType)
    {
        int position = 0;

        for (int i = 0; i < mItemsDiveType.size(); i++) {
            DiveType diveType = mItemsDiveType.get(i);
            if(diveType.getDiveType().equals(mDiveType)) {
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

    private int getConditionIndex(String mCondition)
    {
        int position = 0;

        for (int i = 0; i < mItemsCondition.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsCondition.get(i);
            if(dynamicSpinner.getSpinnerText().equals(mCondition)) {
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

    void setItemsEnvironment(ArrayList<DynamicSpinner> itemsEnvironment) {
        mItemsEnvironment = itemsEnvironment;
    }

    void setAdapterEnvironment(ArrayAdapter<DynamicSpinner> adapterEnvironment) { mAdapterEnvironment = adapterEnvironment; }

    public ArrayAdapter<DynamicSpinner> getAdapterEnvironment () {
        return mAdapterEnvironment;
    }

    private int getEnvironmentIndex(String mEnvironment)
    {
        int position = 0;

        for (int i = 0; i < mItemsEnvironment.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsEnvironment.get(i);
            if(dynamicSpinner.getSpinnerText().equals(mEnvironment)) {
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

    private int getSuitIndex(String mSuit)
    {
        int position = 0;

        for (int i = 0; i < mItemsSuit.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsSuit.get(i);
            if(dynamicSpinner.getSpinnerText().equals(mSuit)) {
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

    private int getWeatherIndex(String mWeather)
    {
        int position = 0;

        for (int i = 0; i < mItemsWeather.size(); i++) {
            DynamicSpinner dynamicSpinner = mItemsWeather.get(i);
            if(dynamicSpinner.getSpinnerText().equals(mWeather)) {
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


    // End of Spinners and AutoCompleteTextView

    // My functions

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

        DiveSummary dive = (DiveSummary) o;

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
        dest.writeString(this.mNote);
        dest.writeString(this.mEnvironment);
        dest.writeString(this.mPlatform);
        dest.writeString(this.mWeather);
        dest.writeString(this.mCondition);
    }

    protected DiveSummary(Parcel in) {
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
        this.mNote = in.readString();
        this.mEnvironment = in.readString();
        this.mPlatform = in.readString();
        this.mWeather  = in.readString();
        this.mCondition = in.readString();
    }

    public static final Creator<DiveSummary> CREATOR = new Creator<DiveSummary>() {
        @Override
        public DiveSummary createFromParcel(Parcel source) {
            return new DiveSummary(source);
        }

        @Override
        public DiveSummary[] newArray(int size) {
            return new DiveSummary[size];
        }
    };
}
