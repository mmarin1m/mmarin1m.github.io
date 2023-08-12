package ca.myairbuddyandi;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextWatcher;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the Computer class
 *
 * This POJO is used to edit and create a single supported dive computer in MABAI
 *
 * It is used by ComputerActivity
 *
 * It is used by AirDA to create, get and update COMPUTER table
 *
 * It is passed from ComputerActivity to ComputerDiveActivity to start the download process
 */

public class Computer extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Computer";
    private static final int HINT_OFFSET = 1;

    // Public

    // Protected

    // Private
    private int mRssi; // Received Signal Strength Indicator
    private int mTransport = 99; // Same as getType() = BluetoothDevice.DEVICE_TYPE_UNKNOWN = The peripheral is not cached
    private long mComputerNo;
    private String mCharacteristicRx; // Write
    private String mCharacteristicRxCredits; // Write Credits??
    private String mCharacteristicTx; // Read
    private String mCharacteristicTxCredits; // Read Credits
    private String mConnectionType;
    private String mDescription;
    private String mDeviceName;
    private String mFw;
    private String mFwId;
    private String mLanguage;
    private String mMacAddress;
    private String mProduct;
    private String mSerialNumber;
    private String mService;
    private String mStatus = MainApplication.getContext().getResources().getString(R.string.button_connected); // Connection status: Connected/Disconnected
    private String mUnit;
    private String mVendor;
    private Boolean mHasDataChanged = false;

    // Excluded from Parcelable
    private Boolean mInMultiEditMode = false;
    private boolean mVisible = false;
    private boolean mChecked = false;

    // End of variables

    // Public constructor
    public Computer() {
    }

    // Getters and setters

    //

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

    public String getConnectionType() {return mConnectionType;}

    public void setConnectionType(String ConnectionType) { mConnectionType = ConnectionType;}

    //

    public long getComputerNo() {return mComputerNo; }

    public void setComputerNo(long computerNo) {mComputerNo = computerNo;}

    //

    public void setDescription(String description) { mDescription = description;}

    public String getDescription() {return mDescription;}

    //

    public void setDeviceName(String deviceName) { mDeviceName = deviceName;}

    public String getDeviceName() {return mDeviceName;}

    //

    public void setFw(String fw) { mFw = fw;}

    public String getFw() {return mFw;}

    //

    public void setFwId(String fWId) { mFwId = fWId;}

    public String getFwId() {return mFwId;}

    //

    public void setLanguage(String language) { mLanguage = language;}

    public String getLanguage() {return mLanguage;}

    //

    public String getMacAddress() {return mMacAddress;}

    public void setMacAddress(String Address) { mMacAddress = Address;}

    //

    public int getRssi() {return mRssi; }

    public void setRssi(int rssi) {mRssi = rssi;}

    //

    public void setSerialNumber(String serialNumber) { mSerialNumber = serialNumber;}

    public String getSerialNumber() {return mSerialNumber;}

    //

    public String getService() {return mService; }

    public void setService(String service) {mService = service;}

    //

    public void setStatus(String status) { mStatus = status;}

    public String getStatus() {return mStatus;}

    //

    public int getTransport() {return mTransport;}

    public void setTransport(int transport) { mTransport = transport;}

    //

    public String getTransportX() {
        switch(mTransport) {
            case 0:
                return "Device Type Unknown";
            case 1:
                return "Bluetooth Classic";
            case 3:
                return "Bluetooth Dual";
            case 99: // Initial value to return empty string on an empty Computer Activity/Layout
                return "";
            case 2:
            default:
                return "Bluetooth LE";
        }
    }

    public void setTransportX(String transportX) {
        // NOTE: Leave as is
        // This method is empty
    }

    //

    public void setUnit(String unit) { mUnit = unit;}

    public String getUnit() {return mUnit;}

    //

    public String getVendor() {return mVendor;}

    public void setVendor(String vendor) {mVendor = vendor;}

    //

    public void setProduct(String product) {mProduct = product;}

    public String getProduct() {return mProduct;}

    // My functions

    private void checkRequiredFields() {
    }

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) {mHasDataChanged = hasDataChanged;}

    public boolean getVisible() {return mVisible;}

    public void setVisible(boolean visible) {this.mVisible = visible;}

    public Boolean getInMultiEditMode() {return mInMultiEditMode;}

    public void setInMultiEditMode(Boolean inMultiEditMode) {mInMultiEditMode = inMultiEditMode;}

    @Bindable
    public boolean getChecked() {return mChecked;}

    public void setChecked(boolean checked) {this.mChecked = checked;}

    @Bindable
    public TextWatcher getOnTextChanged() {
        return new MyTextWatcher() {
            @Override
            public void onTextChanged(String newValue) {
                mHasDataChanged = true;
            }
        };
    }

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Computer computer = (Computer) o;

        return mComputerNo == computer.mComputerNo;

    }

    @Override
    public int hashCode() {
        return (int) (mComputerNo ^ (mComputerNo >>> 32));
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mTransport);
        dest.writeLong(this.mComputerNo);
        dest.writeString(this.mMacAddress);
        dest.writeString(this.mConnectionType);
        dest.writeString(this.mDescription);
        dest.writeString(this.mFw);
        dest.writeString(this.mFwId);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mProduct);
        dest.writeString(this.mSerialNumber);
        dest.writeString(this.mService);
        dest.writeString(this.mStatus);
        dest.writeString(this.mUnit);
        dest.writeString(this.mVendor);
        dest.writeValue(this.mHasDataChanged);
    }

    protected Computer(Parcel in) {
        this.mRssi = in.readInt();
        this.mTransport = in.readInt();
        this.mComputerNo = in.readLong();
        this.mMacAddress = in.readString();
        this.mConnectionType = in.readString();
        this.mDescription = in.readString();
        this.mFw = in.readString();
        this.mFwId = in.readString();
        this.mLanguage = in.readString();
        this.mProduct = in.readString();
        this.mSerialNumber = in.readString();
        this.mService = in.readString();
        this.mStatus = in.readString();
        this.mUnit = in.readString();
        this.mVendor = in.readString();
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Computer> CREATOR = new Creator<Computer>() {
        @Override
        public Computer createFromParcel(Parcel source) {
            return new Computer(source);
        }

        @Override
        public Computer[] newArray(int size) {
            return new Computer[size];
        }
    };
}
