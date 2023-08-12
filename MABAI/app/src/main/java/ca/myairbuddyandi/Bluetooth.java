package ca.myairbuddyandi;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Arrays;
import java.util.Objects;

import ca.myairbuddyandi.databinding.LibdivecomputerPickScanActivityBinding;

/**
 * Created by Michel on 2023-05-29.
 * Holds all of the logic for the Bluetooth class
 *
 * This POJO is for holding Bluetooth information about a successful connected device.
 * Also the object being passed from MyFunctionsBle to calling POJO via the onDeviceConnected() callback
 */

public class Bluetooth extends BaseObservable implements Parcelable {

    // Static
    private static final String LOG_TAG = "Bluetooth";
    private static final int HINT_OFFSET = 1;

    // Public

    // Protected

    // Private

    private int mBondState;
    private int mConnectionState;
    private int mRssi; // Received Signal Strength Indicator
    private int mTransport; // Same as getType() = BluetoothDevice.DEVICE_TYPE_UNKNOWN = The peripheral is not cached
    private String mAlias;
    private String mCharacteristicRx; // Write
    private String mCharacteristicRxCredits; // Write Credits??
    private String mCharacteristicTx; // Read
    private String mCharacteristicTxCredits; // Read Credits
    private String mConnectionType;
    private String mDeviceName;
    private String mFilterOnVendor;
    private String mFw;
    private String mFwId;
    private String mLanguage;
    private String mMacAddress;

    private String mReadBuffer;
    private String mSerialNumber;
    private String mProduct;
    private String mProduct2;
    private String mService;
    private String mStatus;
    private String mUnit;
    private String mVendor;
    private BluetoothDevice mDevice;

    // Excluded from Parcelable
    public LibdivecomputerPickScanActivityBinding mBinding = null;
    private int mProductPosition;
    private int mProduct2Position;
    private int mVendorPosition;
    private Boolean mHasDataChanged = false;
    private Context mContext;
    private String[] mItemsProduct;
    private String[] mItemsProduct2;
    private String[] mItemsVendor;
    private ArrayAdapter<String> mAdapterProduct;
    private ArrayAdapter<String> mAdapterProduct2;
    private ArrayAdapter<String> mAdapterVendor;

    // End of variables

    // Public constructor
    public Bluetooth(Context context) {
        mContext = context;
    }

    // Getters and setters

    public String getAlias() {return mAlias; }

    public void setAlias(String alias) {mAlias = alias;}

    //

    public int getBondState() {return mBondState; }

    public void setBondState(int bondState) {mBondState = bondState;}

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

    public BluetoothDevice getDevice() {return mDevice; }

    public void setDevice(BluetoothDevice device) {mDevice = device;}

    //

    public int getConnectionState() {return mConnectionState; }

    public void setConnectionState(int connectionState) {mConnectionState = connectionState;}

    //

    public String getConnectionType() {return mConnectionType; }

    public void setConnectionType(String connectionType) {mConnectionType = connectionType;}

    //

    public Boolean getHasDataChanged() {return mHasDataChanged;}

    public void setHasDataChanged(boolean hasDataChanged) { mHasDataChanged = hasDataChanged;}

    //

    public String getStatus() {return mStatus; }

    public void setStatus(String status) {mStatus = status;}

    //

    public String getFilterOnVendor() {return mFilterOnVendor; }

    public void setFilterOnVendor(String filterOnVendor) {mFilterOnVendor = filterOnVendor;}

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

    public String getMacAddress() {return mMacAddress; }

    public void setMacAddress(String macAddress) {mMacAddress = macAddress;}

    //

    public void setSerialNumber(String serialNumber) { mSerialNumber = serialNumber;}

    public String getSerialNumber() {return mSerialNumber;}

    //

    public String getDeviceName() {return mDeviceName; }

    public void setDeviceName(String deviceName) {mDeviceName = deviceName;}

    //

    public void setReadBuffer(String readBuffer) {
        mReadBuffer = readBuffer;
    }

    public String getReadBuffer() {
        return mReadBuffer;
    }

    //

    public int getRssi() {return mRssi; }

    public void setRssi(int rssi) {mRssi = rssi;}

    //

    public String getService() {return mService; }

    public void setService(String service) {mService = service;}

    //

    public int getTransport() {return mTransport; }

    public void setTransport(int transport) {mTransport = transport;}

    //

    public void setUnit(String unit) { mUnit = unit;}

    public String getUnit() {return mUnit;}

    // Product Spinner

    public void setAdapterProductNotifyChanged() {
        mAdapterProduct.notifyDataSetChanged();
    }

    public String getProduct() {
        return mProduct; }

    public void setProduct(String product) {
        mProduct = product;
        //Set the position to select and show the data on the activity
        setProductPosition(getProductIndex(mProduct));
    }

    void setProductLoad(String product) {
        // Initial load from My History
        // Avoid calling getIndex()
        mProduct = product;
    }

    void setItemsProduct(String[] itemsProduct) {
        mItemsProduct = itemsProduct;
    }

    public int getProductPosition() {return mProductPosition; }

    public void setProductPosition(int productPosition) {mProductPosition = productPosition;}

    void setAdapterProduct(ArrayAdapter<String> adapterProduct) {
        mAdapterProduct = adapterProduct;
    }

    public ArrayAdapter<String> getAdapterProduct () {return mAdapterProduct;}

    private int getProductIndex(String product)
    {
        int position = 0;

        for (int i = 0; i < mItemsProduct.length; i++) {

            if (mItemsProduct[i].equalsIgnoreCase(product)){
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedProduct() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    String product = String.valueOf(parent.getAdapter().getItem(position + HINT_OFFSET));
                    if (!product.equals(mProduct)) {
                        mProduct = product;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Product2 Spinner

    public void setAdapterProduct2NotifyChanged() {
        mAdapterProduct2.notifyDataSetChanged();
    }

    public String getProduct2() {
        return mProduct2; }

    public void setProduct2(String product2) {
        mProduct2 = product2;
        //Set the position to select and show the data on the activity
        setProduct2Position(getProductIndex(mProduct2));
    }

    void setProduct2Load(String product2) {
        // Initial load from My History
        // Avoid calling getIndex()
        mProduct2 = product2;
    }

    void setItemsProduct2(String[] itemsProduct2) {
        mItemsProduct2 = itemsProduct2;
    }

    public int getProduct2Position() {return mProduct2Position; }

    public void setProduct2Position(int product2Position) {mProduct2Position = product2Position;}

    void setAdapterProduct2(ArrayAdapter<String> adapterProduct2) {
        mAdapterProduct2 = adapterProduct2;
    }

    public ArrayAdapter<String> getAdapterProduct2() {return mAdapterProduct2;}

    private int getProduct2Index(String product2)
    {
        int position = 0;

        for (int i = 0; i < mItemsProduct2.length; i++) {

            if (mItemsProduct[i].equalsIgnoreCase(product2)){
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedProduct2() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    String product2 = String.valueOf(parent.getAdapter().getItem(position + HINT_OFFSET));
                    if (!product2.equals(mProduct2)) {
                        mProduct2 = product2;
                        setHasDataChanged(true);
                    }
                }
            }
        };
    }

    // Vendor Spinner

    public String getVendor() {
        return mVendor; }

    public void setVendor(String vendor) {
        mVendor = vendor;
        //Set the position to select and show the data on the activity
        setVendorPosition(getVendorIndex(mVendor));
    }

    void setVendorLoad(String vendor) {
        // Initial load from My History
        // Avoid calling getIndex()
        mVendor = vendor;
    }

    void setItemsVendor(String[] itemsVendor) {
        mItemsVendor = itemsVendor;
    }

    public int getVendorPosition() {return mVendorPosition; }

    public void setVendorPosition(int vendorPosition) {mVendorPosition = vendorPosition;}

    void setAdapterVendor(ArrayAdapter<String> adapterVendor) {mAdapterVendor = adapterVendor;}

    public ArrayAdapter<String> getAdapterVendor () {return mAdapterVendor;}

    private int getVendorIndex(String vendor)
    {
        int position = 0;

        for (int i = 0; i < mItemsVendor.length; i++) {

            if (mItemsVendor[i].equalsIgnoreCase(vendor)){
                position = i;
                break;
            }
        }

        // The Hint is in the list at item 0
        return position + HINT_OFFSET;
    }

    @Bindable
    public AdapterView.OnItemSelectedListener getOnSpinnerChangedVendor() {
        return new MySpinnerWatcher() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                super.onItemSelected(parent, v, position, id);
                if (position >= 0) {
                    // The Hint is in the list at item 0
                    String vendor = String.valueOf(parent.getAdapter().getItem(position + HINT_OFFSET));
                    if (!vendor.equals(mVendor)) {
                        mVendor = vendor;
                        setHasDataChanged(true);

//                        // Set the Product Spinner
//                        String[] products = MyFunctionsLibDiveComputer.getSupportedProductsPerVendor(MyConstants.DC_TRANSPORT_BLE, vendor);
//                        Arrays.sort(products);
//                        ArrayAdapter<String> adapterProduct = new ArrayAdapter<>(mContext, R.layout.support_simple_spinner_dropdown_item, products);
//                        adapterProduct.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                        mBinding.spinnerProduct.setAdapter(adapterProduct);
//                        setAdapterProduct(adapterProduct);
//                        setItemsProduct(products);
//                        mAdapterProduct.notifyDataSetChanged();
//                        // NOTE: For future reference
//                        //spinnerStatus.setPaddingSafe(0, 0, 0, 0);

                        // Set the Product Spinner
                        // TODO: OnItemSelectedListener is not fired on product, only on product2
                        String[] products2 = MyFunctionsLibDiveComputer.getSupportedProductsPerVendor(MyConstants.DC_TRANSPORT_BLE, vendor);
                        Arrays.sort(products2);
                        ArrayAdapter<String> adapterProduct2 = new ArrayAdapter<>(mContext, R.layout.support_simple_spinner_dropdown_item, products2);
                        adapterProduct2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        mBinding.spinnerProduct2.setAdapter(adapterProduct2);
                        setAdapterProduct2(adapterProduct2);
                        setItemsProduct2(products2);
                        mAdapterProduct2.notifyDataSetChanged();
                        // NOTE: For future reference
                        //spinnerStatus.setPaddingSafe(0, 0, 0, 0);

                        // Start with no product selected
                        // Don't set the spinner to its first entry
                    }
                }
            }
        };
    }

    // Spinner ends

    // Equals

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bluetooth bluetooth = (Bluetooth) o;
        return mBondState == bluetooth.mBondState && Objects.equals(mMacAddress, bluetooth.mMacAddress) && Objects.equals(mAlias, bluetooth.mAlias) && Objects.equals(mFilterOnVendor, bluetooth.mFilterOnVendor) && Objects.equals(mDeviceName, bluetooth.mDeviceName) && Objects.equals(mReadBuffer, bluetooth.mReadBuffer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mBondState, mMacAddress, mAlias, mFilterOnVendor, mDeviceName, mReadBuffer);
    }

    // Starts of parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mBondState);
        dest.writeInt(this.mConnectionState);
        dest.writeInt(this.mRssi);
        dest.writeInt(this.mTransport);
        dest.writeString(this.mAlias);
        dest.writeString(this.mCharacteristicRx);
        dest.writeString(this.mCharacteristicRxCredits);
        dest.writeString(this.mCharacteristicTx);
        dest.writeString(this.mCharacteristicTxCredits);
        dest.writeString(this.mConnectionType);
        dest.writeString(this.mFilterOnVendor);
        dest.writeString(this.mFw);
        dest.writeString(this.mFwId);
        dest.writeString(this.mLanguage);
        dest.writeString(this.mMacAddress);
        dest.writeString(this.mDeviceName);
        dest.writeString(this.mReadBuffer);
        dest.writeString(this.mSerialNumber);
        dest.writeString(this.mProduct);
        dest.writeString(this.mProduct2);
        dest.writeString(this.mService);
        dest.writeString(this.mStatus);
        dest.writeString(this.mUnit);
        dest.writeString(this.mVendor);
        dest.writeParcelable(this.mDevice, flags);
        dest.writeValue(this.mHasDataChanged);
    }

    public void readFromParcel(Parcel source) {
        this.mBondState = source.readInt();
        this.mConnectionState = source.readInt();
        this.mRssi = source.readInt();
        this.mTransport = source.readInt();
        this.mAlias = source.readString();
        this.mCharacteristicRx = source.readString();
        this.mCharacteristicRxCredits = source.readString();
        this.mCharacteristicTx = source.readString();
        this.mCharacteristicTxCredits = source.readString();
        this.mConnectionType = source.readString();
        this.mFilterOnVendor = source.readString();
        this.mFw = source.readString();
        this.mFwId = source.readString();
        this.mLanguage = source.readString();
        this.mMacAddress = source.readString();
        this.mDeviceName = source.readString();
        this.mReadBuffer = source.readString();
        this.mSerialNumber = source.readString();
        this.mProduct = source.readString();
        this.mProduct2 = source.readString();
        this.mService = source.readString();
        this.mStatus = source.readString();
        this.mUnit = source.readString();
        this.mVendor = source.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.mDevice = source.readParcelable(BluetoothDevice.class.getClassLoader(),BluetoothDevice.class);
        } else {
            this.mDevice = source.readParcelable(BluetoothDevice.class.getClassLoader());
        }
        this.mHasDataChanged = (Boolean) source.readValue(Boolean.class.getClassLoader());
    }

    protected Bluetooth(Parcel in) {
        this.mBondState = in.readInt();
        this.mConnectionState = in.readInt();
        this.mRssi = in.readInt();
        this.mTransport = in.readInt();
        this.mAlias = in.readString();
        this.mCharacteristicRx = in.readString();
        this.mCharacteristicRxCredits = in.readString();
        this.mCharacteristicTx = in.readString();
        this.mCharacteristicTxCredits = in.readString();
        this.mConnectionType = in.readString();
        this.mFilterOnVendor = in.readString();
        this.mFw = in.readString();
        this.mFwId = in.readString();
        this.mLanguage = in.readString();
        this.mMacAddress = in.readString();
        this.mDeviceName = in.readString();
        this.mReadBuffer = in.readString();
        this.mSerialNumber = in.readString();
        this.mProduct = in.readString();
        this.mProduct2 = in.readString();
        this.mService = in.readString();
        this.mStatus = in.readString();
        this.mUnit = in.readString();
        this.mVendor = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader(),BluetoothDevice.class);
        } else {
            this.mDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        }
        this.mHasDataChanged = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Bluetooth> CREATOR = new Creator<Bluetooth>() {
        @Override
        public Bluetooth createFromParcel(Parcel source) {
            return new Bluetooth(source);
        }

        @Override
        public Bluetooth[] newArray(int size) {
            return new Bluetooth[size];
        }
    };
}