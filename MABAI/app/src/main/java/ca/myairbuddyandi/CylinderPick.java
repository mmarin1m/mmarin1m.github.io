package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the CylinderPick class
 */

public class CylinderPick extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "CylinderPick";

    // Public

    // Protected

    // Private
    private long mDiverNo;
    private long mCylinderNo;
    private long mGroupNo;
    private String mCylinderType;
    private Double mVolume;
    private Double mRatedPressure;
    private String mUsageType;
    private String mGroupDescription;

    // Excluded from Parcelable
    private boolean mVisible = false;
    private boolean mChecked = false;
    private boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;

    // End of variables

    // Public constructor
    public CylinderPick() {
    }

    // Getters and setters

    public Long getDiverNo() {return mDiverNo; }

    public void setDiverNo(Long diverNo) {mDiverNo = diverNo;}

    //

    public Long getCylinderNo() {return mCylinderNo; }

    public void setCylinderNo(Long cylinderNo) {mCylinderNo = cylinderNo;}

    //

    public Long getGroupNo() {return mGroupNo; }

    public void setGroupNo(Long groupNo) {mGroupNo = groupNo;}

    //

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {mCylinderType = cylinderType;}

    //

    public Double getVolume() {return mVolume; }

    public void setVolume(Double volume) {
        mVolume = volume;
    }

    //

    public Double getRatedPressure() {return mRatedPressure; }

    public void setRatedPressure(Double ratedPressure) { mRatedPressure = ratedPressure; }

    //

    public String getUsageType() {return mUsageType;}

    void setUsageType(String usageType) { mUsageType = usageType; }

    //

    public String getGroupDescription() {return mGroupDescription; }

    void setGroupDescription(String groupDescription) {mGroupDescription = groupDescription;}

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CylinderPick that = (CylinderPick) o;

        return (mDiverNo == that.mDiverNo && mCylinderNo == that.mCylinderNo);

    }

    @Override
    public int hashCode() {
        int result = (int) (mDiverNo ^ (mDiverNo >>> 32));
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
        dest.writeLong(this.mDiverNo);
        dest.writeLong(this.mCylinderNo);
        dest.writeLong(this.mGroupNo);
        dest.writeString(this.mCylinderType);
        dest.writeValue(this.mVolume);
        dest.writeDouble(this.mRatedPressure);
        dest.writeString(this.mUsageType);
    }

    protected CylinderPick(Parcel in) {
        this.mDiverNo = in.readLong();
        this.mCylinderNo = in.readLong();
        this.mGroupNo = in.readLong();
        this.mCylinderType = in.readString();
        this.mVolume = (Double) in.readValue(Double.class.getClassLoader());
        this.mRatedPressure = in.readDouble();
        this.mUsageType = in.readString();
    }

    public static final Creator<CylinderPick> CREATOR = new Creator<CylinderPick>() {
        @Override
        public CylinderPick createFromParcel(Parcel source) {
            return new CylinderPick(source);
        }

        @Override
        public CylinderPick[] newArray(int size) {
            return new CylinderPick[size];
        }
    };
}
