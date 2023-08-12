package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2023-07-14.
 * Holds all of the logic for the ComputerDives class
 *
 * This POJO represents one dive to be selected or unselected
 * A selected dive will be saved to MABAI
 * An unselected dive will NOT be saved to MABAI
 *
 * This POJO is used to hold one new computer dives in the ComputerDivesPickAdapter
 *
 * It is also used in the ComputerDiveActivity in the received ComputerDivesPickList to hold the new computer dives that
 * have been selected/unselected
 */

public class ComputerDives extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "ComputerDives";

    // Public

    // Protected

    // Private
    private boolean mChecked = false;
    private Long mDiveNo;
    private String mDate;

    // Excluded from Parcelable

    // End of variables

    // Public constructor
    public ComputerDives() {
    }

    // Getters and setters

    public String getDate() {return mDate; }

    public void setDate(String date) {mDate = date;}

    //

    public long getDiveNo() {return mDiveNo; }

    public void setDiveNo(long diveNo) {
        mDiveNo = diveNo;
    }

    //

    public void setChecked(boolean checked) {mChecked = checked;}

    @Bindable
    public boolean getChecked() {return mChecked;}

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComputerDives dive = (ComputerDives) o;

        return mDiveNo.equals(dive.mDiveNo);
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
        dest.writeByte(this.mChecked ? (byte) 1 : (byte) 0);
        dest.writeValue(this.mDiveNo);
        dest.writeString(this.mDate);
    }

    public void readFromParcel(Parcel source) {
        this.mChecked = source.readByte() != 0;
        this.mDiveNo = (Long) source.readValue(Long.class.getClassLoader());
        this.mDate = source.readString();
    }

    protected ComputerDives(Parcel in) {
        this.mChecked = in.readByte() != 0;
        this.mDiveNo = (Long) in.readValue(Long.class.getClassLoader());
        this.mDate = in.readString();
    }

    public static final Creator<ComputerDives> CREATOR = new Creator<ComputerDives>() {
        @Override
        public ComputerDives createFromParcel(Parcel source) {
            return new ComputerDives(source);
        }

        @Override
        public ComputerDives[] newArray(int size) {
            return new ComputerDives[size];
        }
    };
}
