package ca.myairbuddyandi;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Date;

/**
 * Created by Michel on 2016-11-29.
 * Holds all of the logic for the DivePick class
 */

public class DivePick extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DivePick";

    // Public

    // Protected

    // Private
    private boolean mChecked = false;
    private boolean mCheckedCompare = false;
    private boolean mVisible = false;
    private long mDiveNo;
    private int mLogBookNo;
    private long mMyBuddyDiverNo;
    private long mMyDiverNo;
    private Date mDate;
    private String mDiveType;
    private String mDiveTypeDesc;
    private String mFullName;
    private String mGroupDesc;
    private String mStatus;

    // Excluded from Parcelable
    private Boolean mInMultiEditMode = false;
    private transient Context mContext;
    private String mDiveSite;
    private String mLocation;

    // End of variables

    // Public constructor
    public DivePick() {
    }

    // Getters and setters

    public void setContext(@Nullable Context context) {
        mContext = context;
    }

    //

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }

    //

    void setMyDiverNo(long myDiverNo) {
        mMyDiverNo = myDiverNo;
    }

    void setMyBuddyDiverNo(long myBuddyDiverNo) {
        mMyBuddyDiverNo = myBuddyDiverNo;
    }

    //

    public String getDiveSite() {return mDiveSite; }

    public void setDiveSite(String diveSite) {
        mDiveSite = diveSite;
    }

    //

    public String getLocation() {return mLocation; }

    public void setLocation(String location) {
        mLocation = location;
    }

    //

    public int getLogBookNo() {return mLogBookNo; }

    void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

    public Date getDate() {return mDate; }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getDateString() {
        return MyFunctions.convertDateFromDateToString(mContext, mDate);
    }

    //

    public String getStatus() {return mStatus; }

    public void setStatus(String status) {
        mStatus = status;
    }

    //

    public String getDiveType() {return mDiveType; }

    public void setDiveType(String diveType) {mDiveType = diveType;}

    //

    public String getDiveTypeDesc() {return mDiveTypeDesc; }

    void setDiveTypeDesc(String diveTypeDesc) {mDiveTypeDesc = diveTypeDesc;}

    //

    public String getGroupDesc() {return mGroupDesc; }

    void setGroupDesc(String groupDesc) {mGroupDesc = groupDesc;}

    //

    public String getMyBuddyFullName() {return mFullName; }

    void setMyBuddyFullName(String fullName) {mFullName = fullName; }

    // My functions

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {mVisible = visible;}
    //
    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {mChecked = checked;}
    //
    @Bindable
    public boolean getCheckedCompare() {return mCheckedCompare;}

    public void setCheckedCompare(boolean checkedCompare) {mCheckedCompare = checkedCompare;}

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DivePick divePick = (DivePick) o;

        return mDiveNo == divePick.mDiveNo;
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
        dest.writeLong(this.mDiveNo);
        dest.writeLong(this.mMyDiverNo);
        dest.writeLong(this.mMyBuddyDiverNo);
        dest.writeString(this.mDiveType);
        dest.writeString(this.mDiveTypeDesc);
        dest.writeLong(this.mDate != null ? this.mDate.getTime() : -1);
        dest.writeInt(this.mLogBookNo);
        dest.writeString(this.mStatus);
        dest.writeString(this.mFullName);
        dest.writeString(this.mGroupDesc);
    }

    protected DivePick(Parcel in) {
        this.mDiveNo = in.readLong();
        this.mMyDiverNo = in.readLong();
        this.mMyBuddyDiverNo = in.readLong();
        this.mDiveType = in.readString();
        this.mDiveTypeDesc = in.readString();
        long tmpMDate = in.readLong();
        this.mDate = tmpMDate == -1 ? null : new Date(tmpMDate);
        this.mLogBookNo = in.readInt();
        this.mStatus = in.readString();
        this.mFullName = in.readString();
        this.mGroupDesc = in.readString();
    }

    public static final Creator<DivePick> CREATOR = new Creator<DivePick>() {
        @Override
        public DivePick createFromParcel(Parcel source) {
            return new DivePick(source);
        }

        @Override
        public DivePick[] newArray(int size) {
            return new DivePick[size];
        }
    };
}
