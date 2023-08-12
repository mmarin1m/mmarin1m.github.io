package ca.myairbuddyandi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import ca.myairbuddyandi.databinding.BluetoothLePickActivityBinding;

/**
 * Created by Michel on 2023-05-11.
 * Holds all of the logic for the BluetoothLeActivity class
 *
 * This activity is for exploring and debugging Bluetooth Low Energy connectivity
 *
 *  * All permissions are handled at the UI level, this activity
 */

public class BluetoothLePickActivity extends AppCompatActivity implements PermissionUtil.PermissionsCallBack {

    // Static
    private static final String LOG_TAG = "BluetoothLeActivity";
    private static final String SHEARWATER_SERVICE_UUID = "fe25c237-0ece-443c-b0aa-e02033e7029d";
    private static final String SHEARWATER_TEST1_SERVICE_UUID = "00001800-0000-1000-8000-00805f9b34fb";
    private static final String SHEARWATER_TEST2_SERVICE_UUID = "00001801-0000-1000-8000-00805f9b34fb";
    private static final String TEST_SERVICE_UUID = "0000180a-0000-1000-8000-00805f9b34fb";
    private static final String SHEARWATER_RX_UUID = "27B7570B-359E-45A3-91BB-CF7E70049BD2";
    private static final String SHEARWATER_TX_UUID = "27B7570B-359E-45A3-91BB-CF7E70049BD2";
    private static final String SHEARWATER_TEST_UUID = "00002a00-0000-1000-8000-00805f9b34fb";
    private static final String TEST_1_UUID = "00002a29-0000-1000-8000-00805f9b34fb";
    private static final String TEST_2_UUID = "00002a24-0000-1000-8000-00805f9b34fb";
    private static final String TEST_3_UUID = "00002a25-0000-1000-8000-00805f9b34fb";

    // Public

    // Protected

    // Private

    private final ArrayList<Bluetooth> mBluetoothList = new ArrayList<>();
    private final ArrayList<LibDiveComputer> mLibDiveComputerList = new ArrayList<>();
    private Bluetooth mBluetooth;
    private BluetoothLePickAdapter mBluetoothPickAdapter;
    private BluetoothLePickActivityBinding mBinding = null;
    private AlertDialog mAlertDialog;
    private MyFunctionsBle mMyFunctionsBle = null;
    private String mMacAddressFilter;
    private String mNameFilter;

    private String mReadBuffer;
    private String mUuidFilter;

    // End of variables

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        }

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.bluetooth_le_pick_activity);

        // Create and load the data in the Recycler View Adapter
        if (mBluetoothPickAdapter == null) {
            mBluetoothPickAdapter = new BluetoothLePickAdapter(this, mBluetoothList);
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mBluetoothPickAdapter);
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);
        mBinding.recycler.setNestedScrollingEnabled(false);

        // Bluetooth

        // Instantiate the Bluetooth helper with my Bluetooth Callback
        mMyFunctionsBle = new MyFunctionsBle(this, mBluetoothCallback);

        if (PermissionUtil.checkAndRequestPermissions(this,
                  Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION
                , Manifest.permission.BLUETOOTH
                , Manifest.permission.BLUETOOTH_ADMIN
                , Manifest.permission.BLUETOOTH_CONNECT
                , Manifest.permission.BLUETOOTH_SCAN)
        ) {
            // All permission granted
            // No trigger of the PermissionUtil.permissionsGranted() callback
            enableTopButtons();
        }

        // Set the listeners

        mBinding.connectButton.setOnClickListener(view -> connect());

        mBinding.diveComputerOnlyButton.setOnClickListener(view -> diveComputerOnly());

        mBinding.enableNotificationButton.setOnClickListener(view -> enableNotifications());

        mBinding.showPairedButton.setOnClickListener(view -> showPairedDevices());

        mBinding.readButton.setOnClickListener(view -> read());

        mBinding.scanButton.setOnClickListener(view -> scan());

        mBinding.writeButton.setOnClickListener(view -> write());

        Log.d(LOG_TAG, "onCreate done");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // In real life we want the app to have Bluetooth turned on right away, no need to a connect/disconnect button
        // We need to check if Bluetooth is on
        // Because the user might have turned it off on this device between operation

        // bluetoothAdapter — itself a lazy property — relies on the getSystemService() function,
        // which is only available after onCreate() has already been called.
        // This is why we are creating and making sure they are still created in the onResume()

        // Check if Bluetooth is available on the device
        if (!mMyFunctionsBle.isBluetoothEnabled()) {
            showErrorAndFinish(getResources().getString(R.string.dlg_bluetooth_error),getResources().getString(R.string.msg_bluetooth_le_not_supported));
        }

        if (!mMyFunctionsBle.isBluetoothAdapterInitialized()) {
            // Ensures Bluetooth is initialized on the device
            if (!mMyFunctionsBle.initBluetoothAdapter()) {
                showError(getResources().getString(R.string.dlg_bluetooth_error), getResources().getString(R.string.msg_bluetooth_adapter_cannot_be_initialized), this);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mMyFunctionsBle.isBluetoothAdapterInitialized() && mMyFunctionsBle.isBluetoothAdapterEnabled()) {
            mMyFunctionsBle.stopScan();
            mMyFunctionsBle.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMyFunctionsBle.isBluetoothAdapterInitialized() && mMyFunctionsBle.isBluetoothAdapterEnabled()) {
            mMyFunctionsBle.stopScan();
            mMyFunctionsBle.disconnect();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_contact_us) {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic), getString(R.string.act_bluetooth_le));
            startActivity(intent);
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        super.onBackPressed();
        finish();
    }

    // Called from: - requestLocationPermission
    //              - requestBluetoothPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    // My functions

    private void buildScanningFilters() {
        // NOTE: Use to initialize the UUID filter
        mUuidFilter = "";
//        mUuidFilter = SHEARWATER_SERVICE_UUID;
//        mUuidFilter = SHEARWATER_TEST2_SERVICE_UUID;
        // NOTE: Use to initialize the Name filter
        mNameFilter = "";
        // NOTE: Use to initialize the Address filter
        mMacAddressFilter = "";
    }

    private void connect() {
        if (!mMyFunctionsBle.isBluetoothAdapterEnabled()) {
            mBinding.bluetoothStatus.setText(getString(R.string.msg_bluetooth_not_on));
        }

        if (mBluetooth == null) {
            // Try to get the Bluetooth POJO from the selected one
            mBluetooth = mBluetoothPickAdapter.getBluetooth();
            if (mBluetooth == null) {
                // If it is still null show invalid status
                mBinding.bluetoothStatus.setText(getString(R.string.msg_bluetooth_no_device_selected));
                return;
            }
        } else {
            // Connect to the selected Bluetooth Low Energy device
            mBinding.bluetoothStatus.setText("");
            // Product is not provided from the Uart table
            mMyFunctionsBle.connect("Product",mBluetooth.getDevice());
        }
    }

    private void diveComputerNotInList() {
        String getName;
        String getProduct;
        for (int i=mBluetoothList.size()-1;i>=0;i--)
        {
            Bluetooth bluetooth = mBluetoothList.get(i);
            boolean inList = false;
            for (int ii=0;ii<mLibDiveComputerList.size();ii++)
            {
                LibDiveComputer libDiveComputer = mLibDiveComputerList.get(ii);
                // The equivalent of bluetooth device.getName() is libdivecomputer.getProduct()
                getName = bluetooth.getDeviceName();
                getProduct = libDiveComputer.getProduct();
                if (getName == null) {
                    getName = "";
                }
                if (getProduct == null) {
                    getProduct = "";
                }
                if (getName.equalsIgnoreCase(getProduct)) {
                    inList = true;
                    break;
                }
            }
            if (!inList) {
                mBluetoothList.remove(i);
            }
        }
    }

    private void diveComputerOnly() {
        // Get all the supported computers from the LibDiveComputer library
        LibDiveComputer[] libDiveComputerJniList = MyFunctionsLibDiveComputer.getSupportedDiveComputers(MyConstants.DC_TRANSPORT_BLE);

        // Convert into ArrayList<LibDiveComputer>
        Collections.addAll(mLibDiveComputerList, libDiveComputerJniList);

        // Nonobstant the vendor or product
        diveComputerNotInList();

        // Refresh the RecyclerView
        mBluetoothPickAdapter.notifyDataSetChanged();
    }

    private void enableOtherButtons() {
        mBinding.connectButton.setEnabled(true);
        mBinding.readButton.setEnabled(true);
        mBinding.writeButton.setEnabled(true);
        mBinding.enableNotificationButton.setEnabled(true);
        mBinding.diveComputerOnlyButton.setEnabled(true);
        mBinding.connectButton.setAlpha(1.0f);
        mBinding.readButton.setAlpha(1.0f);
        mBinding.writeButton.setAlpha(1.0f);
        mBinding.enableNotificationButton.setAlpha(1.0f);
        mBinding.diveComputerOnlyButton.setAlpha(1.0f);
    }

    private void enableConnectButton() {
        mBinding.connectButton.setEnabled(true);
        mBinding.connectButton.setAlpha(1.0f);
    }

    private void enableNotifications() {
        mBinding.bluetoothStatus.setText(getString(R.string.msg_bluetooth_notifications_enabling));
        if (mMyFunctionsBle.enableNotifications(SHEARWATER_SERVICE_UUID,SHEARWATER_RX_UUID,true)) {
            mBinding.bluetoothStatus.setText(getString(R.string.msg_bluetooth_notifications_enabled));
        } else {
            mBinding.bluetoothStatus.setText(String.format(getString(R.string.msg_bluetooth_notifications_failed), mBluetooth.getDevice(), mBluetooth.getCharacteristicTx()));
        }
    }

    private void enableTopButtons() {
        mBinding.showPairedButton.setEnabled(true);
        mBinding.scanButton.setEnabled(true);
        mBinding.showPairedButton.setAlpha(1.0f);
        mBinding.scanButton.setAlpha(1.0f);
    }

    private void getSupportedDiveComputerPerVendor(String vendor) {
        // Get all the supported computers from the LibDiveComputer library
        LibDiveComputer[] libDiveComputerJniList = MyFunctionsLibDiveComputer.getSupportedDiveComputerPerVendor(MyConstants.DC_TRANSPORT_BLE, vendor);

        // Convert into ArrayList<LibDiveComputer>
        Collections.addAll(mLibDiveComputerList, libDiveComputerJniList);
    }

    private boolean isDiveComputerInList(String name) {
        for (int i=0;i<mLibDiveComputerList.size();i++)
        {
            LibDiveComputer libDiveComputer = mLibDiveComputerList.get(i);
            // The equivalent of bluetooth device.getName() is libdivecomputer.getProduct()
            if (Objects.equals(name, libDiveComputer.getProduct())) {
                return true;
            }
        }
        return false;
    }

    // Callback from PermissionUtil
    public void permissionsGranted() {

        enableTopButtons();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(getString(R.string.code_bluetooth_permissions_granted), Boolean.TRUE);
        edit.apply();
    }

    // Callback from PermissionUtil
    @Override
    public void permissionsDenied() {
        showErrorAndFinish(getResources().getString(R.string.dlg_bluetooth_error),getResources().getString(R.string.msg_bluetooth_missing_permission));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(getString(R.string.code_bluetooth_permissions_granted), Boolean.FALSE);
        edit.apply();
    }

    private void read() {
        if (mBluetooth != null) {
            mMyFunctionsBle.readCharacteristic(mBluetooth.getService(), mBluetooth.getCharacteristicTx());
        }
    }

    private void scan() {
        // Need all permissions

        // Just making sure the previous scan has been stopped
        if (mMyFunctionsBle.isDiscovering()) {
            mMyFunctionsBle.stopScan();
        }
        mBluetoothList.clear(); // Clear items
        mBluetoothPickAdapter.notifyDataSetChanged();
        buildScanningFilters();

        // Result keep showing in the mBluetoothPickAdapter
        mMyFunctionsBle.scan(mBluetoothList, mUuidFilter, mNameFilter, mMacAddressFilter);
    }

    public void setBluetooth(Bluetooth bluetooth) {
        mBluetooth = bluetooth;
    }

    private void showBluetoothBusyDialog(boolean show) {
        if (show) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false); // If you want user to wait for some process to finish,
            builder.setView(R.layout.busy_dialog);
            mAlertDialog = builder.create();
            mAlertDialog.show();
        } else {
            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
        }
    }

    private void showError(String title, String message, Context context) {
        // Running on the Main Thread
        // Needs extends AppCompatActivity
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setTitle(title)
                    .setIcon(R.drawable.ic_alert)
                    .setPositiveButton(R.string.dlg_ok, (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    private void showErrorAndFinish(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title)
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(R.string.dlg_ok, (dialog, id) -> {
                    //dialog.dismiss();
                    finish();
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @SuppressLint("MissingPermission")
    private void showPairedDevices() {

        // Need all permissions

        if (mMyFunctionsBle.isDiscovering()) {
            mMyFunctionsBle.stopScan();
        }

        if (mMyFunctionsBle.isBluetoothAdapterEnabled()) {
            mBluetoothList.clear(); // Clear items
            mBluetoothPickAdapter.notifyDataSetChanged();
            Set<BluetoothDevice> pairedDevices = mMyFunctionsBle.getBondedDevices();
            // Get the filtered list
            if (!mBinding.editTextVE.getText().toString().trim().isEmpty()) {
                getSupportedDiveComputerPerVendor(mBinding.editTextVE.getText().toString().trim());
            }
            if (pairedDevices != null) {
                // put it's one to the adapter
                for (BluetoothDevice device : pairedDevices) {
                    Bluetooth bluetooth = new Bluetooth(this);
                    bluetooth.setDevice(device);
                    bluetooth.setDeviceName(device.getName());
                    bluetooth.setMacAddress(device.getAddress()); // MAC address
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        bluetooth.setAlias(device.getAlias());
                    } else {
                        bluetooth.setAlias("");
                    }
                    bluetooth.setTransport(device.getType());
                    bluetooth.setBondState(device.getBondState());
                    if (!mBinding.editTextVE.getText().toString().trim().isEmpty()) {
                        if (isDiveComputerInList(device.getName().trim())) {
                            mBluetoothList.add(bluetooth);
                        }
                    } else {
                        mBluetoothList.add(bluetooth);
                    }
                }
                mBluetoothPickAdapter.notifyDataSetChanged();
            }
        }
    }

    private void write() {
        if (mBluetooth != null) {
            String payload = "";
            mMyFunctionsBle.writeCharacteristic(mBluetooth.getService(), mBluetooth.getCharacteristicRx(), payload);
        }
    }

    // ***** Create my Bluetooth callback *****
    private final BluetoothCallback mBluetoothCallback = new BluetoothCallback() {

        @Override
        public void bubbleUpMessage(int iconId, @NotNull String title, @NotNull String message) {
            super.onConnectionStatusChanged(message);
            mBinding.bluetoothStatus.setText(message);
        }

        @Override
        public void onCharacteristicRead(@NotNull BluetoothDevice device, @NotNull byte[] readValue, @NotNull BluetoothGattCharacteristic readCharacteristic, int status) {
            super.onCharacteristicRead(device, readValue, readCharacteristic, status);

            // FIXME: Not giving the same value. Wait for a valid read
            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic String" + readCharacteristic.getUuid().toString() + MyFunctions.byteArrayToHex(readValue));
            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic Integer" + readCharacteristic.getUuid().toString() + MyFunctions.byteArrayToInteger(readValue,1));

            // TODO: Decide what to do with it!
            BluetoothBytesParser parser = new BluetoothBytesParser(readValue);
            int valueInt = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8);
            String valueString = parser.getStringValue();
            String valueStringO = parser.getStringValue(1);
            mBluetooth.setReadBuffer(valueStringO);
            mBinding.readBuffer.setText(valueStringO);
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothDevice device, @NotNull byte[] updatedValue, @NotNull BluetoothGattCharacteristic updatedCharacteristic) {
            super.onCharacteristicUpdate(device, updatedValue, updatedCharacteristic);

            // FIXME: Not giving the same value. Wait for a valid update
            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic String" + updatedCharacteristic.getUuid().toString() + MyFunctions.byteArrayToHex(updatedValue));
            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic Integer" + updatedCharacteristic.getUuid().toString() + MyFunctions.byteArrayToInteger(updatedValue,1));

            BluetoothBytesParser parser = new BluetoothBytesParser(updatedValue);
            int valueInt = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8);
            String valueString = parser.getStringValue();
            String valueStringO = parser.getStringValue(1);
            // TODO: Decide what to do with it!
        }

        @Override
        public void onDeviceConnected(boolean connected, Bluetooth bluetooth) {
            super.onDeviceConnected(connected, bluetooth);

            mMyFunctionsBle.setBluetooth(bluetooth);
            enableOtherButtons();
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            mBluetoothPickAdapter.notifyDataSetChanged();
            enableConnectButton();
        }

        @Override
        public void notifyItemInserted(int pos) {
            super.notifyItemInserted(pos);

            mBluetoothPickAdapter.notifyItemInserted(pos);
            enableConnectButton();
        }

        @Override
        public void showBusyDialog(boolean show) {
            super.showBusyDialog(show);

            showBluetoothBusyDialog(show);
        }
    };
}