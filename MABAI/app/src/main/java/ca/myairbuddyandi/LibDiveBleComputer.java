package ca.myairbuddyandi;

/**
 * Created by Michel on 2023-06-14.
 * Holds all of the logic for the LibDiveBleComputer class
 *
 * It is only used by MyFunctionsLibDivComputer to init the ArrayList<LibDiveBleComputer>
 * and to find out if the dive computer is supported by libdivecomputer with transport BLE only
 */

public class LibDiveBleComputer {

    // Static
    private static final String LOG_TAG = "LibDiveBleComputer";

    // Public

    // Protected

    // Private
    private String mCharacteristic;
    private String mService;
    private String mVendor;

    // End of variables

    // Public constructor
    public LibDiveBleComputer() {
    }

    // Getters and setters

    public String getCharacteristic() {return mCharacteristic;}

    public void setCharacteristic(String characteristic) { mCharacteristic = characteristic;}

    //

    public String getVendor() {return mVendor;}

    public void setVendor(String vendor) { mVendor = vendor;}

    //

    public String getService() {return mService;}

    public void setService(String service) { mService = service;}


    // My functions

    // Equals

    // Starts of parcelable

}
