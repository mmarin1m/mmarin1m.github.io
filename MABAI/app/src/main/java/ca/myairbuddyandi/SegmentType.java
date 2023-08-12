package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the SegmentType class
 */

public class SegmentType extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "SegmentType";

    // Public

    // Protected

    // Private
    private int mOrderNo;
    private String mDescription;
    private String mDirection;
    private String mSegmentType;
    private String mShowResult;
    private String mStatus;
    private String mSystemDefined;

    // Excluded from parcelable
    private boolean mChecked = false;
    private int mVisible = View.GONE;
    private Boolean mHasDataChanged = false;
    private Boolean mInMultiEditMode = false;

    // End of variables

    // Public constructor
    public SegmentType() {
    }

    // Getters and setters

    public String getSegmentType() {return mSegmentType; }

    public void setSegmentType(String segmentTypeNo) {
        mSegmentType = segmentTypeNo;
    }

    public String getStatus() {return mStatus; }

    public void setStatus(String status) {
        mStatus = status;
    }

    public String getDescription() {return mDescription; }

    public void setDescription(String description) {
        mDescription = description;
    }

    int getOrderNo() {return mOrderNo; }

    void setOrderNo(int orderNo) {mOrderNo = orderNo;}

    String getDirection() {return mDirection; }

    void setDirection(String direction) {
        mDirection = direction;
    }

    String getShowResult() {return mShowResult; }

    void setShowResult(String showResult) {
        mShowResult = showResult;
    }

    String getSystemDefined() {return mSystemDefined; }

    void setSystemDefined(String systemDefined) {
        mSystemDefined = systemDefined;
    }

    // My functions

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    public int getVisible() {return mVisible;}

    public void setVisible(int visible) {mVisible = visible;}

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

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

        SegmentType that = (SegmentType) o;

        return mSegmentType.equals(that.mSegmentType);

    }

    @Override
    public int hashCode() {
        return mSegmentType.hashCode();
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSegmentType);
        dest.writeString(this.mDescription);
        dest.writeInt(this.mOrderNo);
        dest.writeString(this.mDirection);
        dest.writeString(this.mShowResult);
        dest.writeString(this.mSystemDefined);
        dest.writeString(this.mStatus);

    }

    protected SegmentType(Parcel in) {
        this.mSegmentType = in.readString();
        this.mDescription = in.readString();
        this.mOrderNo = in.readInt();
        this.mDirection = in.readString();
        this.mShowResult = in.readString();
        this.mSystemDefined = in.readString();
        this.mStatus = in.readString();
    }

    public static final Creator<SegmentType> CREATOR = new Creator<SegmentType>() {
        @Override
        public SegmentType createFromParcel(Parcel source) {
            return new SegmentType(source);
        }

        @Override
        public SegmentType[] newArray(int size) {
            return new SegmentType[size];
        }
    };
}
