package ca.myairbuddyandi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import ca.myairbuddyandi.databinding.ComputerDiveActivityBinding;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the ComputerDiveActivity class
 *
 * To Connect/Disconnect to/from an existing dive computer
 * To View dive(s) on the existing dive computer
 * To Download dive(s) from an existing and connected dive computer
 *
 * Main POJO:   Computer
 * Passes:      A new ComputerDivesPick
 * Receives:    ComputerDivesPick
 * Passes back: None
 */

public class ComputerDiveActivity extends AppCompatActivity implements PermissionUtil.PermissionsCallBack {

    // Static
    private static final String LOG_TAG = "ComputerDiveActivity";

    // Public

    // Protected

    // Private
    private boolean mConnected = false;
    private AlertDialog mAlertDialog;
    private final AirDA mAirDa = new AirDA(this);
    private MyFunctionsBle mMyFunctionsBle = null;
    private Computer mComputer = new Computer();
    private ComputerDivesPick mComputerDivesPick;
    private ComputerDiveActivityBinding mBinding = null;
    private final ArrayList<ComputerDives> mComputerDivesPickList = new ArrayList<ComputerDives>();
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.computer_dive_activity);

        // Get the data from the Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mComputer = getIntent().getParcelableExtra(MyConstants.COMPUTER, Computer.class);
        } else {
            mComputer = getIntent().getParcelableExtra(MyConstants.COMPUTER);
        }

        mBinding.setComputer(mComputer);

        mAirDa.open();

        if (mComputer.getComputerNo() == MyConstants.ZERO_L) {
            // Add mode (New Computer)
            // Data must be in the Model first in order to bind
            // All data initialization must be done to the Model and not the View to avoid Data Changed Event
            // TODO: Do I need to initialize something

        }  else {
            // Connect and Download mode
            mAirDa.getComputer(mComputer.getComputerNo(),mComputer);
        }

        assert mComputer != null;
        if (mComputer.getComputerNo() != MyConstants.ZERO_I) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(mComputer.getDescription().trim() + " " + getSupportActionBar().getTitle());
            }
        }

        // Set the listeners
        mBinding.connectButton.setOnClickListener(view -> {
            connect();
        });

        mBinding.viewDives.setOnClickListener(view -> {
            viewDives();
        });

        mBinding.downloadDives.setOnClickListener(view -> {
            downloadComputerDives();
        });

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

        mComputer.setHasDataChanged(false);
        
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_computer_dive));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_troubleshooting) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_computer_dive_troubleshooting));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mComputer.getHasDataChanged()) {
                mDialogs.confirm(ComputerDiveActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
                return true;
            } else {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // Action Bar Up button
        if (mComputer.getHasDataChanged()) {
            mDialogs.confirm(ComputerDiveActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
    }

    public void onPause() {
        super.onPause();

        if (mMyFunctionsBle.isBluetoothAdapterInitialized() && mMyFunctionsBle.isBluetoothAdapterEnabled()) {
            mMyFunctionsBle.disconnect();
        }
    }

    // Called from: - requestLocationPermission
    //              - requestBluetoothPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    // My functions

    private void connect() {
        // Coming from Computer Edit, the device might be connected or not
        // Will trigger a mBluetoothCallback.onDeviceConnected()
        if (mConnected) {
            mMyFunctionsBle.disconnect();
        } else if (mComputer != null && MyFunctions.validateMacAddress(mComputer.getMacAddress().trim())) {
                mMyFunctionsBle.getRemoteDevice(mComputer.getMacAddress());
        } else {
            // MAC address is invalid
            showError(getResources().getString(R.string.dlg_bluetooth_error), getResources().getString(R.string.msg_mac_address));
        }
    }

//    @SuppressLint("MissingPermission")
//    private void downloadComputerDives() {
//        LibDiveComputerReturnData returnData;
//        LibDiveComputerStatus status;
//        long iostream = 0;
//        long device = 0;
//        long diveData = 0;
//
//        // Initiate libdivecomputer
//        returnData = MyFunctionsLibDiveComputer.customOpen(mMyFunctionsBle);
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//        iostream = returnData.iostream;
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//            return;
//        }
//
//        // Open the device
//        returnData = MyFunctionsLibDiveComputer.deviceOpen(mComputer.getVendor(), mComputer.getProduct(), "", iostream);
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//        device = returnData.device;
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//            return;
//        }
//
//        // TODO: Get the deviceName
//        //       Get the lastDiveFingerprint of the last dive
//
//        String lastFingerprint = null;
//
//        try{
//            returnData = MyFunctionsLibDiveComputer.deviceForeach(iostream, device, mComputer.getDeviceName(), lastFingerprint);
//        } catch (Exception e) {
//            String msg = e.getMessage();
//        }
//
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//        diveData = returnData.diveData;
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//            return;
//        }
//
//        // TODO: Process diveData
//
//        // TODO: Check return data
//
//        returnData = MyFunctionsLibDiveComputer.iostreamClose(iostream);
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//        }
//
////        // Load mComputerDivesPickList temporarily
////        selectComputerDives();
//    }

    @SuppressLint("MissingPermission")
    private void downloadComputerDives() {
        LibDiveComputerReturnData returnData = null;
        LibDiveComputerStatus status;
//        long iostream = 0;
//        long device = 0;
//        long diveData = 0;

//        // Initiate libdivecomputer
//        returnData = MyFunctionsLibDiveComputer.customOpen(mMyFunctionsBle);
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//        iostream = returnData.iostream;
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//            return;
//        }

//        // Open the device
//        returnData = MyFunctionsLibDiveComputer.deviceOpen(mComputer.getVendor(), mComputer.getProduct(), "", iostream);
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//        device = returnData.device;
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//            return;
//        }

        // TODO: Get the deviceName
        //       Get the lastDiveFingerprint of the last dive

        String lastFingerprint = null;

        try{
            returnData = MyFunctionsLibDiveComputer.download(mMyFunctionsBle, mComputer.getVendor(), mComputer.getProduct(), "", lastFingerprint);
        } catch (Exception e) {
            String msg = e.getMessage();
        }

        status = LibDiveComputerStatus.fromValue(returnData.status);
//        diveData = returnData.diveData;

        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
            return;
        }

        // TODO: Process diveData
        // Dive data is actually returned in MyFunctionsBle.dve_cb

        // TODO: Check return data??

//        returnData = MyFunctionsLibDiveComputer.iostreamClose(iostream);
//        status = LibDiveComputerStatus.fromValue(returnData.status);
//
//        if (status != LibDiveComputerStatus.DC_STATUS_SUCCESS) {
//            showError(getResources().getString(R.string.dlg_libdivecomputer_error), String.format(getResources().getString(R.string.msg_libdivecomputer_custom_open_failed),status.value));
//        }

//        // Load mComputerDivesPickList temporarily
//        selectComputerDives();
    }

    private void showProgressDialog() {
        Intent intent = new Intent(getApplicationContext(), ComputerDiveDownloadProgressBarActivity.class);
        intent.putExtra(MyConstants.COMPUTER_DIVES_PICK, mComputerDivesPick);
        startActivity(intent);
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

    private void selectComputerDives() {

        // TODO: To be removed
        // Load mComputerDivesPickList temporarily
        mComputerDivesPickList.clear();
        for (int i=0;i<20;i++)
        {
            ComputerDives computerDives = new ComputerDives();
            computerDives.setDiveNo(i+1);
            computerDives.setChecked(true);
            computerDives.setDate("07/15/2023");
            mComputerDivesPickList.add(computerDives);
        }

        Intent intent = new Intent(getApplicationContext(), ComputerDivesPickActivity.class);
        mComputerDivesPick = new ComputerDivesPick();
        mComputerDivesPick.setComputerDivesPickList(mComputerDivesPickList);
        intent.putExtra(MyConstants.COMPUTER_DIVES_PICK, mComputerDivesPick);
        selectComputerDivesLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> selectComputerDivesLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the selected Computer Dives to save to MABAI
                    Computer computer;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mComputerDivesPick = data.getParcelableExtra(MyConstants.COMPUTER_DIVES_PICK,ComputerDivesPick.class);
                    } else {
                        mComputerDivesPick = data.getParcelableExtra(MyConstants.COMPUTER_DIVES_PICK);
                    }

                    showProgressDialog();
                }
            });

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

    // Validating and Saving functions
    public Runnable yesProc(){
        return () -> {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        };
    }

    public Runnable noProc(){
        return () -> {
            //Do nothing, stay on this activity
        };
    }

    private void saveDives() {

    }

    private void saveDive() {

    }

    private void viewDives() {
// TODO: To be removed
        // Load mComputerDivesPickList temporarily
        mComputerDivesPickList.clear();
        for (int i=0;i<20;i++)
        {
            ComputerDives computerDives = new ComputerDives();
            computerDives.setDiveNo(i+1);
            computerDives.setChecked(true);
            computerDives.setDate("07/15/2023");
            mComputerDivesPickList.add(computerDives);
        }

        Intent intent = new Intent(getApplicationContext(), ComputerDivesListActivity.class);
        mComputerDivesPick = new ComputerDivesPick();
        mComputerDivesPick.setComputerDivesPickList(mComputerDivesPickList);
        intent.putExtra(MyConstants.COMPUTER_DIVES_PICK, mComputerDivesPick);
        startActivity(intent);
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

        }

        @Override
        public void onCharacteristicUpdate(@NotNull BluetoothDevice device, @NotNull byte[] updatedValue, @NotNull BluetoothGattCharacteristic updatedCharacteristic) {
            super.onCharacteristicUpdate(device, updatedValue, updatedCharacteristic);

        }

        @SuppressLint("MissingPermission")
        @Override
        public void onDeviceConnected(boolean connected, Bluetooth bluetooth) {
            super.onDeviceConnected(connected, bluetooth);

            if (connected) {
                mConnected = true;
                mBinding.viewDives.setEnabled(true);
                mBinding.downloadDives.setEnabled(true);
                mBinding.viewDives.setAlpha(1.0f);
                mBinding.downloadDives.setAlpha(1.0f);
                mMyFunctionsBle.setBluetooth(bluetooth);
                mBinding.statusButton.setText(getString(R.string.button_connected));
                mBinding.statusButton.setBackgroundColor(Color.parseColor(MyConstants.GREEN));
                mBinding.connectButton.setText(getString(R.string.button_disconnect));
            } else {
                mConnected = false;
                mBinding.viewDives.setEnabled(false);
                mBinding.downloadDives.setEnabled(false);
                mBinding.viewDives.setAlpha(0.5f);
                mBinding.downloadDives.setAlpha(0.5f);
                mMyFunctionsBle.setBluetooth(null);
                mBinding.statusButton.setText(getString(R.string.button_disconnected));
                mBinding.statusButton.setBackgroundColor(Color.parseColor(MyConstants.RED));
                mBinding.connectButton.setText(getString(R.string.button_connect));
            }
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();

        }

        @Override
        public void notifyItemInserted(int pos) {
            super.notifyItemInserted(pos);

        }

        @Override
        public void showBusyDialog(boolean show) {
            super.showBusyDialog(show);

            showBluetoothBusyDialog(show);
        }
    };
}
