package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import ca.myairbuddyandi.databinding.ComputerDivesListActivityBinding;

/**
 * Created by Michel on 2023-03-22.
 * Holds all of the logic for the ComputerDivesList class
 *
 * This POJO is used to hold one computer dive directly from the dive computer, as is
 */

public class ComputerDivesList extends BaseObservable {

    // Static
    private static final String LOG_TAG = "ComputerDivesList";

    // Public
    public ComputerDivesListActivityBinding mBinding = null;

    // Protected

    // Private
    private long mComputerDiveNo;
    private boolean mChecked = false;
    private boolean mVisible = false;

    // FIXME: Wait for libdivecomputer integration
    private String mDateX;

    // FIXME: Wait for libdivecomputer integration
    private String mTimeX;

    // End of variables

    // Public constructor
    public ComputerDivesList() {
    }

    // My functions
    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

    //

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {this.mVisible = visible;}

    //

    public long getComputerDiveNo() {return mComputerDiveNo; }

    public void setComputerDiveNo(long computerDiveNo) {
        mComputerDiveNo = computerDiveNo;
    }

    //

    public void setDateX(String dateX) { mDateX = dateX;}

    public String getDateX() {return mDateX;}

    //

    public void setTimeX(String timeX) { mTimeX = timeX;}

    public String getTimeX() {return mTimeX;}

    // My functions

    // Equals

    // Starts of parcelable

}