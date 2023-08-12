package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DiverDiveGroup class
 */

public class DiverDiveGroup extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DiverDiveGroup";

    // Public

    // Protected

    // Private
    private long mDiveNo;
    private long mDiverNo;
    private long mGroupNo;
    private Double mSac;

    // Excluded from Parcelable
    private boolean mChecked = false;
    private Boolean mHasDataChanged = false;

    // End of variables

    // Public constructor
    public DiverDiveGroup() {
    }

    // Getters and setters

    public long getDiverNo() {return mDiverNo; }

    public void setDiverNo(long diverNo) {mDiverNo = diverNo;}

    //

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {mDiveNo = diveNo;}

    //

    public long getGroupNo() {return mGroupNo; }

    public void setGroupNo(long groupNo) {mGroupNo = groupNo;}

    //

    public Double getSac() {return mSac; }

    public void setSac(Double sac) {mSac = sac;}

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    // Data Binding

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

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

        DiverDiveGroup that = (DiverDiveGroup) o;

        return (mDiverNo == that.mDiverNo && mDiveNo == that.mDiveNo && mGroupNo == that.mGroupNo);
    }

    @Override
    public int hashCode() {
        int result = (int) (mDiverNo ^ (mDiverNo >>> 32));
        result = 31 * result + (int) (mDiveNo ^ (mDiveNo >>> 32));
        result = 31 * result + (int) (mGroupNo ^ (mGroupNo >>> 32));
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
        dest.writeLong(this.mDiveNo);
        dest.writeLong(this.mGroupNo);
        dest.writeValue(this.mSac);
    }

    private DiverDiveGroup(Parcel in) {
        this.mDiverNo = in.readLong();
        this.mDiveNo = in.readLong();
        this.mGroupNo = in.readLong();
        this.mSac = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<DiverDiveGroup> CREATOR = new Creator<DiverDiveGroup>() {
        @Override
        public DiverDiveGroup createFromParcel(Parcel source) {
            return new DiverDiveGroup(source);
        }

        @Override
        public DiverDiveGroup[] newArray(int size) {
            return new DiverDiveGroup[size];
        }
    };
}
