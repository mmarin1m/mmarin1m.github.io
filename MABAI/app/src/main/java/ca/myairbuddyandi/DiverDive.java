package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;

import java.util.Objects;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the DiverDive class
 */

public class DiverDive extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "DiverDive";

    // Public

    // Protected

    // Private
    private long mDiveNo;
    private long mDiverNo;
    private Double mRmv;
    private String mIsPrimary;

    // End of variables

    // Public constructor
    public DiverDive() {
    }

    // Getters and setters

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }

    //

    public long getDiverNo() {return mDiverNo; }

    public void setDiverNo(long diverNo) { mDiverNo = diverNo; }

    //

    public Double getRmv() {return mRmv; }

    public void setRmv(Double rmv) {mRmv = rmv;}

    //

    public String getIsPrimary() {return mIsPrimary; }

    public void setIsPrimary(String isPrimary) { mIsPrimary = isPrimary; }

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiverDive diverDive = (DiverDive) o;
        return mDiveNo == diverDive.mDiveNo &&
                mDiverNo == diverDive.mDiverNo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mDiveNo, mDiverNo);
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mDiveNo);
        dest.writeLong(this.mDiverNo);
        dest.writeValue(this.mRmv);
        dest.writeString(this.mIsPrimary);
    }

    protected DiverDive(Parcel in) {
        this.mDiveNo = in.readLong();
        this.mDiverNo = in.readLong();
        this.mRmv = (Double) in.readValue(Double.class.getClassLoader());
        this.mIsPrimary = in.readString();
    }

    public static final Creator<DiverDive> CREATOR = new Creator<DiverDive>() {
        @Override
        public DiverDive createFromParcel(Parcel source) {
            return new DiverDive(source);
        }

        @Override
        public DiverDive[] newArray(int size) {
            return new DiverDive[size];
        }
    };
}
