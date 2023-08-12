package ca.myairbuddyandi;

import androidx.databinding.BaseObservable;

/**
 * Created by Michel on 2018-03-11.
 * Holds all of the logic for the DiveLast class
 */

public class DiveLast extends BaseObservable {

    // Static
    private static final String LOG_TAG = "DiveLast";

    // Public

    // Protected

    // Private
    private Long mLastDate;
    private Double mBottomTime;

    // End of variables

    // Public constructor
    public DiveLast() {
    }

    // Getters and setters

    public Long getLastDate() {return mLastDate; }

    public void setLastDate(Long lastDate) {
        mLastDate = lastDate;
    }

    //

    public Double getBottomTime() {return mBottomTime; }

    public void setBottomTime(Double bottomTime) {
        mBottomTime = bottomTime;
    }
}
