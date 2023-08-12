package ca.myairbuddyandi;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the LibDiveComputerFound class
 *
 * It used by myairbuddyandi.cpp.isLibDiveComputer() to return a single dive computers
 * supported by libdivecomputer by known service for Bluetooth BLE
 */

public class LibDiveComputerFound {

    // Static
    private static final String LOG_TAG = "LibDiveComputerFound";

    // Public

    // Protected

    // Private
    private boolean mFound;
    private String mCharacteristicRx; // Write
    private String mCharacteristicRxCredits; // Write Credits
    private String mCharacteristicTx; // Read
    private String mCharacteristicTxCredits; // Read Credits
    private String mService;
    private String mVendor;

    // End of variables

    // Public constructor
    public LibDiveComputerFound() {
    }

    // Getters and setters

    public String getCharacteristicRx() {return mCharacteristicRx;}

    public void setCharacteristicRx(String characteristicRx) { mCharacteristicRx = characteristicRx;}

    //

    public String getCharacteristicRxCredits() {return mCharacteristicRxCredits;}

    public void setCharacteristicRxCredits(String characteristicRxCredits) { mCharacteristicRxCredits = characteristicRxCredits;}

    //

    public String getCharacteristicTx() {return mCharacteristicTx;}

    public void setCharacteristicTx(String characteristicTx) { mCharacteristicTx = characteristicTx;}

    //

    public String getCharacteristicTxCredits() {return mCharacteristicTxCredits;}

    public void setCharacteristicTxCredits(String characteristicTxCredits) { mCharacteristicTxCredits = characteristicTxCredits;}

    //

    public void setFound(boolean found) {mFound = found;}

    public boolean getFound() {return mFound;}


    //

    public String getService() {return mService;}

    public void setService(String service) { mService = service;}

    //

    public String getVendor() {return mVendor;}

    public void setVendor(String vendor) {mVendor = vendor;}
}
