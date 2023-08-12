package ca.myairbuddyandi;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.databinding.BaseObservable;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the LibDiveComputer class
 *
 * It used by myairbuddyandi.cpp to return the dive computers supported by libdivecomputer,
 * with Bluetooth Classic or BLE
 *
 * It is used by BluetoothLePickActivity to hold any type of libdivecomputer,
 * for the Bluetooth test bed
 *
 * It returns a single LibDiveComputer from LibDiveComputerPickScanActivity to ComputerActivity
 * when adding a new supported libdivecomputer
 */

public class LibDiveComputer extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "LibDiveComputer";

    // Public

    // Protected

    // Private
    private Integer mModel = 0;
    private Integer mTransport = 0;
    private Integer mType = 0;
    private String mMacAddress;
    private String mName;
    private String mProduct;
    private String mVendor;

    private BluetoothDevice mDevice;

    // End of variables

    // Public constructor
    public LibDiveComputer(String vendor, String product, Integer type, Integer model, Integer transport) {
        setVendor(vendor);
        setProduct(product);
        setType(type);
        setModel(model);
        setTransport(transport);
    }

    // Getters and setters

    public BluetoothDevice getDevice() {return mDevice; }

    public void setDevice(BluetoothDevice device) {mDevice = device;}

    //

    public String getMacAddress() {return mMacAddress;}

    public void setAddress(String macAddress) { mMacAddress = macAddress;}

    //

    public String getName() {return mName;}

    public void setName(String name) { mName = name;}

    //

    public String getProduct() {return mProduct;}

    public void setProduct(String product) { mProduct = product;}

    //

    public int getModel() {return mModel;}

    public void setModel(int model) { mModel = model;}

    //

    public int getTransport() {return mTransport;}

    public void setTransport(int transport) { mTransport = transport;}

    //

    public int getType() {return mType;}

    public void setType(Integer type) { mType = type;}

    //

    public String getVendor() {return mVendor;}

    public void setVendor(String vendor) { mVendor = vendor;}

    // My functions

    // Equals

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mVendor);
        dest.writeString(this.mProduct);
        dest.writeInt(this.mType);
        dest.writeInt(this.mModel);
        dest.writeInt(this.mTransport);
    }

    protected LibDiveComputer(Parcel in) {
        this.mVendor = in.readString();
        this.mProduct = in.readString();
        this.mType = in.readInt();
        this.mModel = in.readInt();
        this.mTransport = in.readInt();
    }

    public static final Creator<LibDiveComputer> CREATOR = new Creator<LibDiveComputer>() {
        @Override
        public LibDiveComputer createFromParcel(Parcel source) {
            return new LibDiveComputer(source);
        }

        @Override
        public LibDiveComputer[] newArray(int size) {
            return new LibDiveComputer[size];
        }
    };
}
