package ca.myairbuddyandi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import ca.myairbuddyandi.databinding.LibdivecomputerPickScanActivityBinding;

/**
 * Created by Michel on 2023-07-10.
 * Holds all of the logic for the LibDiveComputerPickScanActivity class
 *
 * To pick a supported libdivecomputer by vendor and product
 * Must connect to the dive computer using the Connect icon
 * Cannot guarantee the user will pick the dive computer corresponding to the libdivecomputer descriptor
 */

public class LibDiveComputerPickScanActivity extends AppCompatActivity implements PermissionUtil.PermissionsCallBack {

    // Static
    private static final String LOG_TAG = "LibDiveComputerPickScanActivity";

    // Public

    // Protected

    // Private
    private AlertDialog mAlertDialog;
    private final ArrayList<Bluetooth> mBluetoothList = new ArrayList<>();
    private Bluetooth mBluetooth;
    private final Handler mHandler = new Handler(); // Our main handler that will receive callback notifications
    private LibdivecomputerPickScanActivityBinding mBinding = null;
    private LibDiveComputerPickScanAdapter mLibDiveComputerPickScanAdapter;
    private MyFunctionsBle mMyFunctionsBle = null;
    private String mMacAddressFilter;
    private String mNameFilter;
    private String mProduct;
    private String mUuidFilter;
    private String mVendor;

    // End of variables

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.libdivecomputer_pick_scan_activity);

        mBluetooth = new Bluetooth(this);

        mBluetooth.mBinding = mBinding;

        mBinding.setBluetooth(mBluetooth);

        // Set the data in the Spinner Vendor
        String[] vendors = MyFunctionsLibDiveComputer.getSupportedVendors(MyConstants.DC_TRANSPORT_BLE);
        Arrays.sort(vendors);
        ArrayAdapter<String> adapterVendor = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, vendors);
        adapterVendor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mBluetooth.setAdapterVendor(adapterVendor);
        mBluetooth.setItemsVendor(vendors);
        // NOTE: For future reference
        //spinnerStatus.setPaddingSafe(0, 0, 0, 0);

        // Start with no vendor selected
        // Don't set the vendor spinner to its first entry

        // Must set the data in the Spinner Product
        // Otherwise it will crash
        // Set it with one empty entry in the list
        String[] products = new String[1];
        products[0] = "";
//        String[] products = MyFunctionsLibDiveComputer.getSupportedVendors(MyConstants.DC_TRANSPORT_BLE);
        ArrayAdapter<String> adapterProduct = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, products);
        adapterProduct.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mBinding.spinnerProduct.setAdapter(adapterProduct);
        mBluetooth.setAdapterProduct(adapterProduct);
        mBluetooth.setItemsProduct(products);
        // NOTE: For future reference
        //spinnerStatus.setPaddingSafe(0, 0, 0, 0);

        // Start with no product selected
        // Don't set the product spinner to its first entry

        // Must set the data in the Spinner Product
        // Otherwise it will crash
        // Set it with one empty entry in the list
//        String[] products = new String[1];
//        products[0] = "";
//        String[] products = MyFunctionsLibDiveComputer.getSupportedVendors(MyConstants.DC_TRANSPORT_BLE);
        ArrayAdapter<String> adapterProduct2 = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, products);
        adapterProduct2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mBinding.spinnerProduct2.setAdapter(adapterProduct2);
        mBluetooth.setAdapterProduct2(adapterProduct2);
        mBluetooth.setItemsProduct2(products);
        // NOTE: For future reference
        //spinnerStatus.setPaddingSafe(0, 0, 0, 0);

        // Start with no product selected
        // Don't set the product spinner to its first entry

        // Create and load the data in the Recycler View Adapter
        // The list is empty because no scanning has occurred yet
        if (mLibDiveComputerPickScanAdapter == null) {
            mLibDiveComputerPickScanAdapter = new LibDiveComputerPickScanAdapter(this, mBluetoothList);
        } else {
            mLibDiveComputerPickScanAdapter.setBluetoothPickList(mBluetoothList);
        }

        // If the list is empty, make sure there is a valid POJO in the adapter
        if (mBluetoothList.size() == MyConstants.ZERO_I) {
            mLibDiveComputerPickScanAdapter.setBluetooth(mBluetooth);
        }

        // Set the Recycler View
        mBinding.recycler.setAdapter(mLibDiveComputerPickScanAdapter);
        mBinding.recycler.setLayoutManager(new DiveLinearLayoutManager(this));
        mBinding.recycler.setItemAnimator(new DefaultItemAnimator());
        mBinding.recycler.setHasFixedSize(true);

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
            Log.d(LOG_TAG, "onCreate - All permissions have been granted");
        }

        mBinding.scanButton.setOnClickListener(view -> scan());

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
                showError(getResources().getString(R.string.dlg_bluetooth_error), getResources().getString(R.string.msg_bluetooth_adapter_cannot_be_initialized));
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        final MenuItem troubleShootingItem = menu.findItem(R.id.action_troubleshooting);
        troubleShootingItem.setVisible(true);

        // TODO: Implement Share
//        final MenuItem shareItem = menu.findItem(R.id.action_share);

        // TODO: Implement Share
//        shareItem.setVisible(true);

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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_libdivecomputer_pick_scan));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_troubleshooting) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_libdivecomputer_pick_scan_troubleshooting));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            Intent intent = new Intent();
            intent.putExtra(MyConstants.PICK_A_LIBDIVECOMPUTER, mBluetooth);
            // The connect was not successful
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Hard button on Phone
        Intent intent = new Intent();
        intent.putExtra(MyConstants.PICK_A_LIBDIVECOMPUTER, mBluetooth);
        // The connect was not successful
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
        finish();
    }

    public void onPause() {
        super.onPause();
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
        // NOTE: Use to initialize the Name filter
        mNameFilter = "";
        // NOTE: Use to initialize the Address filter
        mMacAddressFilter = "";
    }

    public void connect(int position) {
        if (!mMyFunctionsBle.isBluetoothAdapterEnabled()) {
            showError(getString(R.string.dlg_bluetooth_error),getString(R.string.msg_bluetooth_not_on));
            return;
        }

        if (mBluetooth == null) {
            // Try to get the Bluetooth POJO from the selected one
            mBluetooth = mLibDiveComputerPickScanAdapter.getBluetooth();
            if (mBluetooth == null) {
                // If it is still null show invalid status
                showError(getString(R.string.dlg_bluetooth_error),getString(R.string.msg_bluetooth_no_device_selected));
            }
        } else {
            if (!validateVendor()) {
                return;
            }
            if (!validateProduct()) {
                return;
            }

            //

            // The device are contained in the Bluetooth of the mBluetoothList
            // The device are NOT included in the mBluetoothPickList of the adapter
            // At this point, the Bluetooth does not contains the Service and the Characteristics Rx, Rx Credits, Tx and Tx Credits
            mBluetooth = mBluetoothList.get(position);
            mBluetooth.setVendorLoad(mVendor);
            mBluetooth.setProductLoad(mProduct);
            // Connect to the selected Bluetooth Low Energy device
            // Product is not provided from the Uart table
            // Use the one from the Spinner
            mMyFunctionsBle.connect(mProduct, mBluetooth.getDevice());
        }
    }

    public void doSmoothScroll(int position) {
        // Scroll to the newly added Computer
        // The screen does not scroll if the newly added Computer is on the same screen
        mBinding.recycler.smoothScrollToPosition(position);
    }

    // Callback from PermissionUtil
    public void permissionsGranted() {
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

    private void requestFocus(View view) {
        if (view instanceof EditText) {
            // Only works for EditText
            view.clearFocus();
            view.requestFocus();
            ((EditText) view).selectAll();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            view.clearFocus();
            view.requestFocus();
        }
    }

    private void scan() {
        // Need all permissions

        // Just making sure the previous scan has been stopped
        if (mMyFunctionsBle.isDiscovering()) {
            mMyFunctionsBle.stopScan();
        }
        mBluetoothList.clear(); // Clear items
        mLibDiveComputerPickScanAdapter.notifyDataSetChanged();
        buildScanningFilters();

        // mBluetoothList is the source of the adapter
        // mMyFunctionsBle.onScanResult() result adds one Bluetooth at a time
        // Since mBluetoothList is passed by reference then it will contains all of the scan results
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

    private void showError(String title, String message) {
        // Running on the Main Thread
        // Needs extends AppCompatActivity
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private boolean validateProduct() {
        // Required
        mProduct = mBinding.spinnerProduct2.getSelectedItem().toString();
        if (mProduct == null || mProduct.trim().isEmpty()) {
            showError(getResources().getString(R.string.dlg_bluetooth_error), getResources().getString(R.string.msg_product));
            requestFocus(mBinding.spinnerProduct2);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateVendor() {
        // Required
        mVendor = mBinding.spinnerVendor.getSelectedItem().toString();
        if (mVendor == null || mVendor.trim().isEmpty()) {
            showError(getResources().getString(R.string.dlg_bluetooth_error), getResources().getString(R.string.msg_vendor));
            requestFocus(mBinding.spinnerVendor);
            return false;
        } else {
            return true;
        }
    }

    // ***** Create my Bluetooth callback *****
    private final BluetoothCallback mBluetoothCallback = new BluetoothCallback() {

        @Override
        public void bubbleUpMessage(int iconId, @NotNull String title, @NotNull String message) {
            super.onConnectionStatusChanged(message);

            // TODO: Need to filter out otherwise it will display all messages sent from MyFunctionsBLE
            if (iconId == 1 || iconId == 2) {
                showError(title,message);
            }
        }

        @Override
        public void onCharacteristicRead(@NotNull BluetoothDevice device, @NotNull byte[] readValue, @NotNull BluetoothGattCharacteristic readCharacteristic, int status) {
            super.onCharacteristicRead(device, readValue, readCharacteristic, status);

//            // FIXME: Not giving the same value. Wait for a valid read
//            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic String" + readCharacteristic.getUuid().toString() + MyFunctions.byteArrayToHex(readValue));
//            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic Integer" + readCharacteristic.getUuid().toString() + MyFunctions.byteArrayToInteger(readValue,1));
//
//            // TODO: Decide what to do with it!
//            BluetoothBytesParser parser = new BluetoothBytesParser(readValue);
//            int valueInt = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8);
//            String valueString = parser.getStringValue();
//            String valueStringO = parser.getStringValue(1);
//            mBluetooth.setReadBuffer(valueStringO);
//            mBinding.readBuffer.setText(valueStringO);
        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothDevice device, @NotNull byte[] updatedValue, @NotNull BluetoothGattCharacteristic updatedCharacteristic) {
            super.onCharacteristicUpdate(device, updatedValue, updatedCharacteristic);

//            // FIXME: Not giving the same value. Wait for a valid update
//            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic String" + updatedCharacteristic.getUuid().toString() + MyFunctions.byteArrayToHex(updatedValue));
//            Log.i(LOG_TAG,"onCharacteristicRead - Read characteristic Integer" + updatedCharacteristic.getUuid().toString() + MyFunctions.byteArrayToInteger(updatedValue,1));
//
//            BluetoothBytesParser parser = new BluetoothBytesParser(updatedValue);
//            int valueInt = parser.getIntValue(BluetoothBytesParser.FORMAT_UINT8);
//            String valueString = parser.getStringValue();
//            String valueStringO = parser.getStringValue(1);
//            // TODO: Decide what to do with it!
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onDeviceConnected(boolean connected, Bluetooth bluetooth) {
            super.onDeviceConnected(connected, bluetooth);

            if (connected) {
                Intent intent = new Intent();
                intent.putExtra(MyConstants.PICK_A_LIBDIVECOMPUTER, bluetooth);
                setResult(RESULT_OK, intent);
                finish();
            }
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

            mLibDiveComputerPickScanAdapter.notifyDataSetChanged();
        }

        @Override
        public void notifyItemInserted(int pos) {
            super.notifyItemInserted(pos);

//            mBluetoothPickAdapter.notifyItemInserted(pos);
        }

        @Override
        public void showBusyDialog(boolean show) {
            super.showBusyDialog(show);

            showBluetoothBusyDialog(show);
        }
    };
}