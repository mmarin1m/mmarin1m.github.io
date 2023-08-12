package ca.myairbuddyandi;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;
import java.util.Date;

import ca.myairbuddyandi.databinding.CylinderActivityBinding;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the Cylinder class
 */

public class Cylinder extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Cylinder";
    private static final int HINT_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mCylinderTypePosition;
    private int mLastHydroHour;
    private int mLastHydroMinute;
    private int mLastVipHour;
    private int mLastVipMinute;
    private long mCylinderNo;
    private long mDiverNo;
    private Date mLastHydro;
    private Date mLastVip;
    private Double mBuoyancyEmpty = MyConstants.ZERO_D;
    private Double mBuoyancyFull = MyConstants.ZERO_D;
    private Double mRatedPressure;
    private Double mRatedPressureOld;
    private Double mVolume;
    private Double mVolumeOld;
    private Double mWeightFull = MyConstants.ZERO_D;
    private Double mWeightEmpty = MyConstants.ZERO_D;
    private String mBrand = "";
    private String mTankColor = "";
    private String mCylinderType;
    private String mCylinderTypeOld;
    private String mIsNew;
    private String mModel = "";
    private String mSerialNo = "";
    private String mUsageType;
    private Boolean mHasDataChanged = false;
    private boolean mVisible = false;
    private boolean mChecked = false;

    // Excluded from parcelable
    private ArrayAdapter<CylinderType> mAdapterCylinderType;
    private ArrayList<CylinderType> mItemsCylinderType;
    public CylinderActivityBinding mBinding = null;
    private transient Context mContext;

    // End of variables

    // Public constructor
    public Cylinder() {
    }

    // Getters and setters

    public void setContext(Context context) {
        mContext = context;
    }

    //

    public String getBrand() {return mBrand; }

    public void setBrand(String brand) {
        mBrand = brand;
    }

    //

    public long getCylinderNo() {return mCylinderNo; }

    public void setCylinderNo(long cylinderNo) {
        mCylinderNo = cylinderNo;
    }

    //

    public Long getDiverNo() {return mDiverNo; }

    public void setDiverNo(Long diverNo) {
        mDiverNo = diverNo;
    }

    //

    String getIsNew() {return mIsNew; }

    void setIsNew(String isNew) {
        mIsNew = isNew;
    }

    //

    Date getLastHydro() {return mLastHydro; }

    public String getLastHydroString() {return MyFunctions.convertDateFromDateToString(mContext, mLastHydro); }

    void setLastHydro(Date lastHydro) {mLastHydro = lastHydro;}

    public void setLastHydroString(String lastHydroString) {setLastHydro(MyFunctions.convertDateFromStringToDate(mContext, lastHydroString)); }

    int getLastHydroHour() {return mLastHydroHour;}

    // NOTE: Reserved for future use
    void setLastHydroHour(int lastHydroHour) {mLastHydroHour = lastHydroHour;}

    int getLastHydroMinute() {return mLastHydroMinute;}

    // NOTE: Reserved for future use
    public void setLastHydroMinute(int lastHydroMinute) {mLastHydroMinute = lastHydroMinute;}

    //

    public Date getLastVip() { return mLastVip; }

    public String getLastVipString() { return MyFunctions.convertDateFromDateToString(mContext, mLastVip); }

    void setLastVip(Date lastVip) { mLastVip = lastVip;}

    public void setLastVipString(String lastVipString) { setLastVip(MyFunctions.convertDateFromStringToDate(mContext, lastVipString)); }

    int getLastVipHour() {return mLastVipHour;}

    // NOTE: Reserved for future use
    void setLastVipHour(int lastVipHour) {mLastVipHour = lastVipHour;}

    int getLastVipMinute() {return mLastVipMinute;}

    // NOTE: Reserved for future use
    public void setLastVipMinute(int lastVipMinute) {mLastVipMinute = lastVipMinute;}

    //

    public String getModel() {return mModel; }

    public void setModel(String model) {
        mModel = model;
    }

    //

    public Double getRatedPressure() {return mRatedPressure; }

    public void setRatedPressure(Double ratedPressure) { mRatedPressure = ratedPressure; }

    void setRatedPressureOld(Double ratedPressureOld) { mRatedPressureOld = ratedPressureOld; }

    //

    public String getSerialNo() {return mSerialNo; }

    public void setSerialNo(String serialNo) {
        mSerialNo = serialNo;
    }

    //

    public void setTankColor(String tankColor) { mTankColor = tankColor; }

    public String getTankColor() {return mTankColor; }

    //

    public String getUsageType() {return mUsageType; }

    public void setUsageType(String usageType) {
        mUsageType = usageType;
    }

    //

    public Double getVolume() {return mVolume; }

    public void setVolume(Double volume) {
        mVolume = volume;
    }

    void setVolumeOld(Double volumeOld) {
        mVolumeOld = volumeOld;
    }

    //

    public Double getWeightEmpty() {return mWeightEmpty; }

    public void setWeightEmpty(Double weightEmpty) { mWeightEmpty = weightEmpty; }

    //

    public Double getWeightFull() {return mWeightFull; }

    public void setWeightFull(Double weightFull) { mWeightFull = weightFull; }

    //

    public Double getBuoyancyEmpty() {return mBuoyancyEmpty; }

    public void setBuoyancyEmpty(Double buoyancyEmpty) { mBuoyancyEmpty = buoyancyEmpty; }

    //

    public Double getBuoyancyFull() {return mBuoyancyFull; }

    public void setBuoyancyFull(Double buoyancyFull) { mBuoyancyFull = buoyancyFull; }

    // GrouppType Spinner

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {
        mCylinderType = cylinderType;
        //Set the position to select and show the data on the activity
        setCylinderTypePosition(getCylinderTypeIndex(mCylinderType));
    }

    void setCylinderTypeOld(String cylinderTypeOld) {
        mCylinderTypeOld = cylinderTypeOld;
    }

    void setCylinderTypeCommon(String cylinderType) {
        mCylinderType = cylinderType;
    }

    void setCylinderTypeLoad(String cylinderType) {
        mCylinderType = cylinderType;
    }

    public int getCylinderTypePosition() {return mCylinderTypePosition; }

    public void setCylinderTypePosition(int cylinderTypePosition) {mCylinderTypePosition = cylinderTypePosition;}

    void setItemsCylinderType(ArrayList<CylinderType> itemsCylinderType) {mItemsCylinderType = itemsCylinderType;}

    void setAdapterCylinderType(ArrayAdapter<CylinderType> adapterCylinderType) {mAdapterCylinderType = adapterCylinderType;}

    public ArrayAdapter<CylinderType> getAdapterCylinderType () {
        return mAdapterCylinderType;
    }

    private int getCylinderTypeIndex(String myCylinderType)
    {
        int position = 0;

        for (int i = 0; i < mItemsCylinderType.size(); i++) {
            CylinderType cylinderType = mItemsCylinderType.get(i);
            if(cylinderType.getCylinderType().equals(myCylinderType)) {
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedCylinderType() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    CylinderType cylinderType = (CylinderType) parent.getAdapter().getItem(position + HINT_OFFSET);
                    mCylinderType = cylinderType.getCylinderType();
                    // The Hint is in the list at item 0
                    if (position + HINT_OFFSET != mCylinderTypePosition) {
                        mHasDataChanged = true;
                    }
                    if ((mCylinderNo == MyConstants.ZERO_L) && (position + HINT_OFFSET != mCylinderTypePosition)) {
                        //Set the default only for a new cylinder where the selection changed
                        cylinderType = (CylinderType) parent.getAdapter().getItem(position + HINT_OFFSET);
                        mVolume = cylinderType.getVolume();
                        mRatedPressure = cylinderType.getRatedPressure();
                        mBinding.editTextVO.setText(String.valueOf(mVolume));
                        mBinding.editTextRP.setText(String.valueOf(mRatedPressure));
                        mBinding.executePendingBindings();
                    }
                }
            }
        };
    }

    // End of Spinners

    // My functions

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public Boolean getHasSpecChanged() {
        return !mCylinderType.equals(mCylinderTypeOld)
                || !mVolume.equals(mVolumeOld)
                || !mRatedPressure.equals(mRatedPressureOld);
        }
    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}

    @Bindable
    public TextWatcher getOnTextChanged() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mHasDataChanged = true;
            }
        };
    }

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cylinder cylinder = (Cylinder) o;

        return mCylinderNo == cylinder.mCylinderNo;

    }

    @Override
    public int hashCode() {
        return (int) (mCylinderNo ^ (mCylinderNo >>> 32));
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mCylinderNo);
        dest.writeLong(this.mDiverNo);
        dest.writeString(this.mCylinderType);
        dest.writeInt(this.mCylinderTypePosition);
        dest.writeValue(this.mVolume);
        dest.writeValue(this.mRatedPressure);
        dest.writeString(this.mBrand);
        dest.writeString(this.mModel);
        dest.writeString(this.mSerialNo);
        dest.writeString(this.mUsageType);
        dest.writeString(this.mTankColor);
        dest.writeLong(this.mLastVip != null ? this.mLastVip.getTime() : -1);
        dest.writeLong(this.mLastHydro != null ? this.mLastHydro.getTime() : -1);
        dest.writeString(this.mIsNew);
        dest.writeValue(this.mHasDataChanged);
        dest.writeByte(this.mVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mChecked ? (byte) 1 : (byte) 0);
    }

    protected Cylinder(Parcel in) {
        this.mCylinderNo = in.readLong();
        this.mDiverNo = in.readLong();
        this.mCylinderType = in.readString();
        this.mCylinderTypePosition = in.readInt();
        this.mVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mRatedPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mBrand = in.readString();
        this.mModel = in.readString();
        this.mSerialNo = in.readString();
        this.mUsageType = in.readString();
        this.mTankColor = in.readString();
        long tmpMDate = in.readLong();
        this.mLastVip = tmpMDate == -1 ? null : new Date(tmpMDate);
        tmpMDate = in.readLong();
        this.mLastHydro = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mIsNew = in.readString();
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.mVisible = in.readByte() != 0;
        this.mChecked = in.readByte() != 0;
    }

    public static final Creator<Cylinder> CREATOR = new Creator<Cylinder>() {
        @Override
        public Cylinder createFromParcel(Parcel source) {
            return new Cylinder(source);
        }

        @Override
        public Cylinder[] newArray(int size) {
            return new Cylinder[size];
        }
    };
}
