package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the GrouppPick class
 */

public class GrouppPick extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "GrouppPick";

    // Public

    // Protected

    // Private
    private int mDives;
    private int mLogBookNo;
    private Boolean mHasDataChanged = false;
    private Long mGroupNo;
    private Long mDiverNo;
    private Long mDiveNo;
    private String mGroupType;
    private String mGroupTypeDescription;
    private String mDescription;
    private String mCylinderType;
    private String mUsageType;

    // Excluded from Parcelable
    private Boolean mInMultiEditMode = false;
    private boolean mVisible = false;
    private boolean mChecked = false;

    // End of variables

    // Public constructor
    public GrouppPick() {
    }

    // Getters and setters

    public Long getGroupNo() {return mGroupNo; }

    void setGroupNo(Long groupNo) {
        mGroupNo = groupNo;
    }

    //

    public Long getDiverNo() {return mDiverNo; }

    public void setDiverNo(Long diverNo) { mDiverNo = diverNo; }

    //

    public void setDiveNo(Long diveNo) {
        mDiveNo = diveNo;
    }

    public Long getDiveNo() {return mDiveNo; }

   //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

    public int getDives() {return mDives; }

    public void setDives(int dives) {
        mDives = dives;
    }

    //

    public String getGroupType() {return mGroupType; }

    public void setGroupType(String groupType) {mGroupType = groupType;}

    //

    public String getGroupTypeDescription() {return mGroupTypeDescription; }

    public void setGroupTypeDescription(String groupTypeDescription) {mGroupTypeDescription = groupTypeDescription;}

    //

    public String getDescription() {return mDescription; }

    public void setDescription(String description) {
        mDescription = description;
    }

    //

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {
        mCylinderType = cylinderType;
    }

    //

    public String getUsageType() {return mUsageType; }

    public void setUsageType(String usageType) {mUsageType = usageType;}

    // My functions

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}

    //

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    //

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

        GrouppPick that = (GrouppPick) o;

        return mGroupNo.equals(that.mGroupNo);

    }

    @Override
    public int hashCode() {
        return mGroupNo.hashCode();
    }

    // Starts of parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mGroupNo);
        dest.writeValue(this.mDiverNo);
        dest.writeValue(this.mDiveNo);
        dest.writeInt(this.mDives);
        dest.writeInt(this.mLogBookNo);
        dest.writeString(this.mGroupType);
        dest.writeString(this.mGroupTypeDescription);
        dest.writeString(this.mDescription);
        dest.writeString(this.mCylinderType);
        dest.writeString(this.mUsageType);
        dest.writeValue(this.mHasDataChanged);
    }

    protected GrouppPick(Parcel in) {
        this.mGroupNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiverNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDiveNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDives = in.readInt();
        this.mLogBookNo = in.readInt();
        this.mGroupType = in.readString();
        this.mGroupTypeDescription = in.readString();
        this.mDescription = in.readString();
        this.mCylinderType = in.readString();
        this.mUsageType = in.readString();
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<GrouppPick> CREATOR = new Creator<GrouppPick>() {
        @Override
        public GrouppPick createFromParcel(Parcel source) {
            return new GrouppPick(source);
        }

        @Override
        public GrouppPick[] newArray(int size) {
            return new GrouppPick[size];
        }
    };
}
