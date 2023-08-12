package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-11-29.
 * Holds all of the logic for the DivePlan class
 */

public class DivePlan extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DivePlan";

    // Public

    // Protected

    // Private
    private int mLogBookNo;
    private int mMinute;
    private long mDiveNo;
    private long mDivePlanNo;
    private long mOrderNo;
    private Boolean mHasDataChanged = false;
    private Double mDepth;

    // Excluded from Parcelable
    private boolean mVisible = false;
    private boolean mChecked = false;
    private Boolean mInMultiEditMode = false;

    // End of variables

    // Public constructor
    public DivePlan() {
    }

    // Getters and setters

    long getDivePlanNo() {return mDivePlanNo; }

    void setDivePlanNo(long divePlanNo) {
        mDivePlanNo = divePlanNo;
    }

    //

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }

    //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

    public long getOrderNo() {return mOrderNo; }

    public void setOrderNo(long orderNo) {
        mOrderNo = orderNo;
    }

    //

    public Double getDepth() {return mDepth; }

    public void setDepth(Double Depth) {mDepth = Depth;}

    //

    public int getMinute() {return mMinute; }

    public void setMinute(int minute) {mMinute = minute;}

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    //

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {this.mVisible = visible;}

    //

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    //

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

    //

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

        DivePlan divePlan = (DivePlan) o;

        return mDivePlanNo == divePlan.mDivePlanNo;

    }

    @Override
    public int hashCode() {
        return (int) (mDivePlanNo ^ (mDivePlanNo >>> 32));
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mDivePlanNo);
        dest.writeLong(this.mDiveNo);
        dest.writeLong(this.mOrderNo);
        dest.writeValue(this.mDepth);
        dest.writeInt(this.mMinute);
        dest.writeInt(this.mLogBookNo);
        dest.writeValue(this.mHasDataChanged);
    }

    protected DivePlan(Parcel in) {
        this.mDivePlanNo = in.readLong();
        this.mDiveNo = in.readLong();
        this.mOrderNo = in.readLong();
        this.mDepth = (Double) in.readValue(Double.class.getClassLoader());
        this.mMinute = in.readInt();
        this.mLogBookNo = in.readInt();
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<DivePlan> CREATOR = new Creator<DivePlan>() {
        @Override
        public DivePlan createFromParcel(Parcel source) {
            return new DivePlan(source);
        }

        @Override
        public DivePlan[] newArray(int size) {
            return new DivePlan[size];
        }
    };
}
