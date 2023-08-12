package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;

import java.util.ArrayList;

/**
 * Created by Michel on 2023-07-14.
 * Holds all of the logic for the ComputerDivesPick class
 *
 * This POJO is used to pass the list of dives received from the dive computer
 * in order to select and unselect dives
 *
 * The dives are already downloaded from the dives computer.
 *
 * Need to select which one will be saved in MABAI
 */

public class ComputerDivesPick extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "ComputerDivesPick";

    // Public

    // Protected

    // Private
    private Boolean mInMultiSelectionMode = false;
    private ArrayList<ComputerDives> mComputerDivesPickList;

    // End of variables

    // Public constructor
    public ComputerDivesPick() {

    }

    // Getters and setters

    public boolean getInMultiSelectionMode() {
        return mInMultiSelectionMode;
    }

    public void setInMultiSelectionMode(boolean inMultiSelectionMode) {
        mInMultiSelectionMode = inMultiSelectionMode;
    }

    public ArrayList<ComputerDives> getComputerDivesPickList() {
        return mComputerDivesPickList;
    }

    public void setComputerDivesPickList(ArrayList<ComputerDives> computerDivesPickList) {
        mComputerDivesPickList = computerDivesPickList;
    }

    // Equals

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mComputerDivesPickList);
    }

    public void readFromParcel(Parcel source) {
        this.mComputerDivesPickList = source.createTypedArrayList(ComputerDives.CREATOR);
    }

    protected ComputerDivesPick(Parcel in) {
        this.mComputerDivesPickList = in.createTypedArrayList(ComputerDives.CREATOR);
    }

    public static final Creator<ComputerDivesPick> CREATOR = new Creator<ComputerDivesPick>() {
        @Override
        public ComputerDivesPick createFromParcel(Parcel source) {
            return new ComputerDivesPick(source);
        }

        @Override
        public ComputerDivesPick[] newArray(int size) {
            return new ComputerDivesPick[size];
        }
    };
}
