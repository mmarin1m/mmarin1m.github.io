package ca.myairbuddyandi;

/**
 * Created by Michel on 2016-12-08.
 * Holds all of the logic for the SacRmvWorking class
 */

public class SacRmvWorking {

    // Static
    private static final String LOG_TAG = "SacRmvWorking";

    // Public

    // Protected

    // Private

    private Double mSacMe;
    private Double mRmvMe;
    private Double mSacBoth;
    private Double mRmvBoth;
    private Long mDiveNo;

    // End of variables

    // Public constructor
    public SacRmvWorking() {
    }

    // Getters and setters

    public Long getDiveNo() {return mDiveNo; }

    public void setDiveNo(Long diveNo) {mDiveNo = diveNo;}

    //

    Double getSacMe() {return mSacMe; }

    void setSacMe(Double sacMe) {mSacMe = sacMe;}

    //

    Double getRmvMe() {return mRmvMe; }

    void setRmvMe(Double rmvMe) {mRmvMe = rmvMe;}

    //

    Double getSacBoth() {return mSacBoth; }

    void setSacBoth(Double sacBoth) {mSacBoth = sacBoth;}

    //

    Double getRmvBoth() {return mRmvBoth; }

    void setRmvBoth(Double rmvBoth) {mRmvBoth = rmvBoth;}
}
