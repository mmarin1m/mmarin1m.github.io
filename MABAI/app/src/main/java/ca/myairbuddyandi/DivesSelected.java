package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michel on 2020-12-1.
 * Holds all of the Dive Nos to compare
 */

public class DivesSelected implements Parcelable {

    // Static
    private static final String LOG_TAG = "DivesSelected";

    // Public

    // Protected

    // Private
    private long mDiveNo1;
    private long mDiveNo2;
    private long mDiveNo3;

    // End of variables

    // Public constructor
    public DivesSelected() {
    }

    // Getters and setters

    public long getDiveNo1() {return mDiveNo1; }

    public void setDiveNo1(long diveNo1) {
        mDiveNo1 = diveNo1;
    }

    //

    public long getDiveNo2() {return mDiveNo2; }

    public void setDiveNo2(long diveNo2) {
        mDiveNo2 = diveNo2;
    }

    //

    public long getDiveNo3() {return mDiveNo3; }

    public void setDiveNo3(long diveNo3) {
        mDiveNo3 = diveNo3;
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mDiveNo1);
        dest.writeLong(this.mDiveNo2);
        dest.writeLong(this.mDiveNo3);

    }

    protected DivesSelected(Parcel in) {
        this.mDiveNo1 = in.readLong();
        this.mDiveNo2 = in.readLong();
        this.mDiveNo3 = in.readLong();

    }

    public static final Creator<DivesSelected> CREATOR = new Creator<DivesSelected>() {
        @Override
        public DivesSelected createFromParcel(Parcel source) {
            return new DivesSelected(source);
        }

        @Override
        public DivesSelected[] newArray(int size) {
            return new DivesSelected[size];
        }
    };
}
