package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Objects;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DiverDiveGroupCyl class
 */

public class DiverDiveGroupCyl extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DiverDiveGroupCyl";

    // Public

    // Protected

    // Private
    private int mLogBookNo;
    private int mO2 = MyConstants.AIR_O2;
    private int mN = MyConstants.AIR_N;
    private int mHe = MyConstants.ZERO_I;
    private long mDiverNo;
    private long mDiveNo;
    private long mGroupNo;
    private long mCylinderNo;
    private Boolean mHasDataChanged = false;
    private Double mBeginningPressure = MyConstants.ZERO_D;
    private Double mEndingPressure = MyConstants.ZERO_D;
    private String mCylinderType;
    private String mUsageType;

    // Excluded from Parcelable
    private boolean mChecked = false;

    // End of variables

    // Public constructor
    public DiverDiveGroupCyl() {
    }

    // Getters and setters

    public long getDiverNo() {return mDiverNo; }

    public void setDiverNo(long diverNo) {mDiverNo = diverNo;}

    //

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {mDiveNo = diveNo;}

    //

    public int getLogBookNo() {return mLogBookNo; }

    public void setLogBookNo(int logBookNo) {mLogBookNo = logBookNo;}

    //

    public long getGroupNo() {return mGroupNo; }

    public void setGroupNo(long groupNo) {mGroupNo = groupNo;}

    //

    public long getCylinderNo() {return mCylinderNo; }

    public void setCylinderNo(long cylinderNo) {mCylinderNo = cylinderNo;}

    //

    public String getCylinderType() {return mCylinderType; }

    public void setCylinderType(String cylinderType) {mCylinderType = cylinderType;}

    //

    public Double getBeginningPressure() {return mBeginningPressure; }

    public void setBeginningPressure(Double beginningPressure) {mBeginningPressure = beginningPressure;}

    //

    public Double getEndingPressure() {return mEndingPressure; }

    public void setEndingPressure(Double endingPressure) {mEndingPressure = endingPressure;}

    //

    public int getO2() {return mO2; }

    public void setO2(int o2) {
        mO2 = o2;
    }

    //

    public int getHe() {return mHe; }

    public void setHe(int he) {
        mHe = he;
    }

    //

    public int getN() {return mN; }

    public void setN(int n) {mN = n;}

    //

    public String getUsageType() {return mUsageType; }

    public void setUsageType(String usageType) {mUsageType = usageType;}

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
        DiverDiveGroupCyl that = (DiverDiveGroupCyl) o;
        return mDiverNo == that.mDiverNo &&
                mDiveNo == that.mDiveNo &&
                mGroupNo == that.mGroupNo &&
                Objects.equals(mUsageType, that.mUsageType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDiverNo, mDiveNo, mGroupNo, mUsageType);
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
        dest.writeLong(this.mCylinderNo);
        dest.writeString(this.mCylinderType);
        dest.writeString(this.mUsageType);
        dest.writeValue(this.mBeginningPressure);
        dest.writeValue(this.mEndingPressure);
        dest.writeInt(this.mLogBookNo);
        dest.writeInt(this.mO2);
        dest.writeInt(this.mN);
        dest.writeInt(this.mHe);
        dest.writeValue(this.mHasDataChanged);
    }

    protected DiverDiveGroupCyl(Parcel in) {
        this.mDiverNo = in.readLong();
        this.mDiveNo = in.readLong();
        this.mGroupNo = in.readLong();
        this.mCylinderNo = in.readLong();
        this.mCylinderType = in.readString();
        this.mUsageType = in.readString();
        this.mBeginningPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mEndingPressure = (Double) in.readValue(Double.class.getClassLoader());
        this.mLogBookNo = in.readInt();
        this.mO2 = in.readInt();
        this.mN = in.readInt();
        this.mHe = in.readInt();
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<DiverDiveGroupCyl> CREATOR = new Creator<DiverDiveGroupCyl>() {
        @Override
        public DiverDiveGroupCyl createFromParcel(Parcel source) {
            return new DiverDiveGroupCyl(source);
        }

        @Override
        public DiverDiveGroupCyl[] newArray(int size) {
            return new DiverDiveGroupCyl[size];
        }
    };
}
