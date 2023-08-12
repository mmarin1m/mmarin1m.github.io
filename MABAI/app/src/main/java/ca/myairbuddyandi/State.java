package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Michel on 2016-12-01.
 * Holds all of the logic for the State class
 */

public class State implements Parcelable {

    // Static
    private static final String LOG_TAG = "State";

    // Public

    // Protected

    // Private
    private long mBuddyDiverNo;
    private long mMyBuddyGroup;
    private long mMyGroup;
    private long mStateNo;
    private Double mMyBuddySac;
    private Double mMyBuddyRmv;
    private Double mMyRmv;
    private Double mMySac;
    private String mDiveType;

    // End of variables

    // Public constructor
    public State() {
    }

    // Getters and setters

    public long getStateNo() {return mStateNo; }

    public void setStateNo(long stateNo) {mStateNo = stateNo;}

    //

    public String getDiveType() {return mDiveType; }

    public void setDiveType(String diveType) {mDiveType = diveType;}

    //

    public long getBuddyDiverNo() {return mBuddyDiverNo; }

    public void setBuddyDiverNo(long buddyDiverNo) {
        mBuddyDiverNo = buddyDiverNo;
    }

    //

    public Double getMySac() {return mMySac; }

    public void setMySac(Double mySac) {mMySac = mySac;}

    //

    public Double getMyRmv() {return mMyRmv; }

    public void setMyRmv(Double myRmv) {mMyRmv = myRmv;}

    //

    public Double getMyBuddySac() {return mMyBuddySac; }

    public void setMyBuddySac(Double myBuddySac) {
        mMyBuddySac = myBuddySac;
    }

    //

    public Double getMyBuddyRmv() {return mMyBuddyRmv; }

    public void setMyBuddyRmv(Double myBuddyRmv) {mMyBuddyRmv = myBuddyRmv;}

    //

    public long getMyGroup() {return mMyGroup; }

    public void setMyGroup(long myGroup) {mMyGroup = myGroup;}

    //

    public long getMyBuddyGroup() {return mMyBuddyGroup; }

    public void setMyBuddyGroup(long myBuddyGroup) {mMyBuddyGroup = myBuddyGroup;}

    // Start Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mBuddyDiverNo);
        dest.writeLong(this.mMyBuddyGroup);
        dest.writeLong(this.mMyGroup);
        dest.writeLong(this.mStateNo);
        dest.writeValue(this.mMyBuddySac);
        dest.writeValue(this.mMyBuddyRmv);
        dest.writeValue(this.mMySac);
        dest.writeValue(this.mMyRmv);
        dest.writeString(this.mDiveType);
    }

    private State(Parcel in) {
        this.mBuddyDiverNo = in.readLong();
        this.mMyBuddyGroup = in.readLong();
        this.mMyGroup = in.readLong();
        this.mStateNo = in.readLong();
        this.mMyBuddySac = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyBuddyRmv = (Double) in.readValue(Double.class.getClassLoader());
        this.mMySac = (Double) in.readValue(Double.class.getClassLoader());
        this.mMyRmv = (Double) in.readValue(Double.class.getClassLoader());
        this.mDiveType = in.readString();
    }

    public static final Creator<State> CREATOR = new Creator<State>() {
        @Override
        public State createFromParcel(Parcel source) {
            return new State(source);
        }

        @Override
        public State[] newArray(int size) {
            return new State[size];
        }
    };
}
