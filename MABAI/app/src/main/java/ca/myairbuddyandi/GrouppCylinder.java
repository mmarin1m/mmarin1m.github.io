package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.ArrayList;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the GrouppCylinder class
 */

public class GrouppCylinder extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "GrouppCylinder";

    // Public

    // Protected

    // Private
    private int mUsageCount;
    private int mUsageTypePosition;
    private long mGroupNo;
    private long mDiverNo;
    private long mCylinderNo;
    private Double mVolume;
    private Double mVolumeOld;
    private Double mRatedPressure;
    private Double mRatedPressureOld;
    private String mUsageType;
    private String mUsageTypeOld;
    private String mCylinderType;
    private String mIsNew;
    private String mUsageDescription;

    // Excluded from Parcelable
    private Boolean mHasDataChanged = false;
    private boolean mVisible = false;
    private boolean mChecked = false;
    private ArrayList<UsageType> mItemsUsageType;

    // End of variables

    // Public constructor
    public GrouppCylinder() {
    }

    // Getters and setters

    public Long getGroupNo() {return mGroupNo; }

    void setGroupNo(Long groupNo) {
        mGroupNo = groupNo;
    }

    //

    public Long getDiverNo() {return mDiverNo; }

    public void setDiverNo(Long diverNo) {mDiverNo = diverNo;}

    //

    public Long getCylinderNo() {return mCylinderNo; }

    public void setCylinderNo(Long cylinderNo) {mCylinderNo = cylinderNo;}

    //

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {mCylinderType = cylinderType;}

    //

    public Double getVolume() {return mVolume; }

    public void setVolume(Double volume) {
        mVolume = volume;
    }

    public void setVolumeOld(Double volumeOld) {mVolumeOld = volumeOld;}

    //

    public Double getRatedPressure() {return mRatedPressure; }

    public void setRatedPressure(Double ratedPressure) {
        mRatedPressure = ratedPressure;
    }

    public void setRatedPressureOld(Double ratedPressureOld) {mRatedPressureOld = ratedPressureOld;}

    //

    public String getIsNew() {return mIsNew; }

    public void setIsNew(String isNew) {mIsNew = isNew;}

    //

    public int getUsageCount() {return mUsageCount; }

    public void setUsageCount(int usageCount) {mUsageCount = usageCount;}

    // UsageType Spinner

    public String getUsageType() {return mUsageType; }

    public void setUsageType(String usageType) {
        mUsageType = usageType;
        //Set the position to select and show the data on the activity
        setUsageTypePosition(getUsageTypeIndex(mUsageType));
    }

    public void setUsageTypeOld(String usageTypeOld) {mUsageTypeOld = usageTypeOld; }

    public void setUsageTypeCommon(String usageType) {
        mUsageType = usageType;
    }

    public void setUsageTypeLoad(String usageType) {
        mUsageType = usageType;
    }

    public int getUsageTypePosition() {return mUsageTypePosition; }

    public void setUsageTypePosition(int usageTypePosition) {mUsageTypePosition = usageTypePosition;}

    public void setItemsUsageType(ArrayList<UsageType> itemsUsageType) {mItemsUsageType = itemsUsageType;}

    public int getUsageTypeIndex(String myUsageType)
    {
        int position = 0;

        for (int i = 0; i < mItemsUsageType.size(); i++) {
            UsageType usageType = mItemsUsageType.get(i);
            if(usageType.getUsageType().equals(myUsageType)) {
                position = i;
                mUsageDescription = usageType.getDescription();
                break;
            }
        }

        // No Hint at position 0
        return position;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedUsageType() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    UsageType usageType = (UsageType) parent.getAdapter().getItem(position);
                    mUsageType = usageType.getUsageType();
                    mUsageDescription = usageType.getDescription();
                    if (position != mUsageTypePosition) {
                        mHasDataChanged = true;
                    }
                }
            }
        };
    }

    public String getUsageDescription() {return mUsageDescription; }

    // End of Spinner

    // My functions

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}

    //

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    //

    public Boolean getHasSpecChanged() {
        return !mUsageTypeOld.equals(mUsageType)
                || !mVolumeOld.equals(mVolume)
                || !mRatedPressureOld.equals(mRatedPressure);
    }

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GrouppCylinder that = (GrouppCylinder) o;

        return mGroupNo == that.mGroupNo && mCylinderNo == that.mCylinderNo;

    }

    @Override
    public int hashCode() {
        int result = (int) (mGroupNo ^ (mGroupNo >>> 32));
        result = 31 * result + (int) (mCylinderNo ^ (mCylinderNo >>> 32));
        return result;
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mGroupNo);
        dest.writeLong(this.mDiverNo);
        dest.writeLong(this.mCylinderNo);
        dest.writeString(this.mCylinderType);
        dest.writeDouble(this.mVolume);
        dest.writeDouble(this.mRatedPressure);
        dest.writeString(this.mUsageType);
        dest.writeString(this.mIsNew);
    }

    protected GrouppCylinder(Parcel in) {
        this.mGroupNo = in.readLong();
        this.mDiverNo = in.readLong();
        this.mCylinderNo = in.readLong();
        this.mCylinderType = in.readString();
        this.mVolume = in.readDouble();
        this.mRatedPressure = in.readDouble();
        this.mUsageType = in.readString();
        this.mIsNew = in.readString();
    }

    public static final Creator<GrouppCylinder> CREATOR = new Creator<GrouppCylinder>() {
        @Override
        public GrouppCylinder createFromParcel(Parcel source) {
            return new GrouppCylinder(source);
        }

        @Override
        public GrouppCylinder[] newArray(int size) {
            return new GrouppCylinder[size];
        }
    };
}
