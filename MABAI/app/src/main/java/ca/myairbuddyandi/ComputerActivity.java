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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import ca.myairbuddyandi.databinding.ComputerActivityBinding;

/**
 * Created by Michel on 2023-03-21.
 * Holds all of the logic for the ComputerActivity class
 *
 * To add a new computer
 * To modify an existing computer
 *
 * Main POJO:   Computer
 * Passes:      A new LibDiveComputer
 * Receives:    Bluetooth
 * Passes back: None
 */

public class ComputerActivity extends AppCompatActivity implements PermissionUtil.PermissionsCallBack {

    // Static
    private static final String LOG_TAG = "ComputerActivity";

    // Public

    // Protected

    // Private
    private boolean mConnected = false;
    private AlertDialog mAlertDialog;
    private final AirDA mAirDa = new AirDA(this);
    private MyFunctionsBle mMyFunctionsBle = null;
    private Computer mComputer = new Computer();
    private ComputerActivityBinding mBinding = null;
    private final MyDialogs mDialogs = new MyDialogs();

    // End of variables

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));}

        // Do DataBinding
        mBinding = DataBindingUtil.setContentView(this, R.layout.computer_activity);

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
            // TODO: Do I need to initialize something?

        }  else {
            // Edit mode
            mAirDa.getComputer(mComputer.getComputerNo(),mComputer);
        }

        if (getSupportActionBar() != null) {
            if (mComputer.getComputerNo() == MyConstants.ZERO_I) {
                getSupportActionBar().setTitle(getString(R.string.act_computer_add));
            } else {
                getSupportActionBar().setTitle(getSupportActionBar().getTitle() + " " + mComputer.getDescription());
            }
        }

        // Set the listeners
        mBinding.testConnectionButton.setOnClickListener(view -> {
            testConnection();
        });

        mBinding.cancelButton.setOnClickListener(view -> {
            if (mComputer.getHasDataChanged()) {
                mDialogs.confirm(ComputerActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
            } else {
                if (mConnected) {
                    mMyFunctionsBle.disconnect();
                }
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });

        mBinding.fabLibDiveComputerPickScan.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LibDiveComputerPickScanActivity.class);
            LibDiveComputer libDiveComputer = new LibDiveComputer("","",0,0,0);
            intent.putExtra(MyConstants.PICK_A_LIBDIVECOMPUTER,libDiveComputer);
            pickScanLauncher.launch(intent);
        });

        mBinding.saveButton.setOnClickListener(view -> {
            // Validate data
            submitForm();
        });

        // Bluetooth

        // Instantiate the Bluetooth helper with my Bluetooth Callback
        mMyFunctionsBle = new MyFunctionsBle(this, mBluetoothCallback);

        if (PermissionUtil.checkAndRequestPermissions(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
                , android.Manifest.permission.ACCESS_FINE_LOCATION
                , android.Manifest.permission.BLUETOOTH
                , android.Manifest.permission.BLUETOOTH_ADMIN
                , android.Manifest.permission.BLUETOOTH_CONNECT
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
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_computer_edit));
            startActivity(intent);
            return true;
        } else if (id == R.id.action_troubleshooting) {
            Intent intent = new Intent(this, HelpActivity.class);
            intent.putExtra(getString(R.string.app_help_topic),getString(R.string.act_libdivecomputer_pick_scan_troubleshooting));
            startActivity(intent);
            return true;
        } else if(id==android.R.id.home) {
            // Action Bar Up button
            // NOTE: Leave as is
            if (mComputer.getHasDataChanged()) {
                mDialogs.confirm(ComputerActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
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
            mDialogs.confirm(ComputerActivity.this,getString(R.string.dlg_confirm_cancel),getString(R.string.dlg_cancel),getString(R.string.dlg_positive),getString(R.string.dlg_negative),yesProc(),noProc());
        } else {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            super.onBackPressed();
        }
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

    ActivityResultLauncher<Intent> pickScanLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    // Get the newly pick supported computer
                    Bluetooth bluetooth;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        bluetooth = data.getParcelableExtra(MyConstants.PICK_A_LIBDIVECOMPUTER,Bluetooth.class);
                    } else {
                        bluetooth = data.getParcelableExtra(MyConstants.PICK_A_LIBDIVECOMPUTER);
                    }
                    if (bluetooth != null) {
                        // It is a supported libdivecomputer
                        // Connection was successful
                        mComputer.setService(bluetooth.getService());
                        mComputer.setCharacteristicRx(bluetooth.getCharacteristicRx());
                        mComputer.setCharacteristicRxCredits(bluetooth.getCharacteristicRxCredits());
                        mComputer.setCharacteristicTx(bluetooth.getCharacteristicTx());
                        mComputer.setCharacteristicTxCredits(bluetooth.getCharacteristicTxCredits());
                        mComputer.setVendor(bluetooth.getVendor());
                        mComputer.setProduct(bluetooth.getProduct());
                        mComputer.setDeviceName(bluetooth.getDeviceName());
                        mComputer.setMacAddress(bluetooth.getMacAddress());
                        mComputer.setTransport(bluetooth.getTransport());
                        mComputer.setSerialNumber(bluetooth.getSerialNumber());
                        mComputer.setFw(bluetooth.getFw());
                        mComputer.setFwId(bluetooth.getFwId());
                        mComputer.setLanguage(bluetooth.getLanguage());
                        mComputer.setUnit(bluetooth.getUnit());
                        mComputer.setConnectionType(bluetooth.getConnectionType());
                        mComputer.setStatus(bluetooth.getStatus());
                        mComputer.setRssi(bluetooth.getRssi());

                        mBinding.editTextVE.setText(mComputer.getVendor());
                        mBinding.editTextPR.setText(mComputer.getProduct());
                        mBinding.editTextMA.setText(String.valueOf(mComputer.getMacAddress()));
                        mBinding.editTextTR.setText(String.valueOf(mComputer.getTransportX()));
                        mBinding.editTextSN.setText(String.valueOf(mComputer.getSerialNumber()));
                        mBinding.editTextFW.setText(String.valueOf(mComputer.getFw()));
                        mBinding.editTextFWId.setText(String.valueOf(mComputer.getFwId()));
                        mBinding.editTextLA.setText(String.valueOf(mComputer.getLanguage()));
                        mBinding.editTextUN.setText(String.valueOf(mComputer.getUnit()));
                        mBinding.editTextCT.setText(String.valueOf(mComputer.getConnectionType()));

                        mBinding.statusButton.setText(getString(R.string.button_connected));
                        mBinding.statusButton.setBackgroundColor(Color.parseColor(MyConstants.GREEN));
                    }
                }
            });

    private void submitForm() {

        if (!validateDescription()) {
            return;
        }

        if (!validateMacAddress()) {
            return;
        }

        // Save COMPUTER data
        if (mComputer.getComputerNo() == MyConstants.ZERO_L) {
            // Add mode
            mAirDa.createComputer(mComputer, false);

        }  else {
            // Edit mode
            mAirDa.updateComputer(mComputer);
        }

        if (mConnected) {
            mMyFunctionsBle.disconnect();
        }

        // Return the parcel object with the newly saved data
        Intent intent = new Intent();
        intent.putExtra(MyConstants.COMPUTER,mComputer);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void testConnection() {
        // Everytime we leave a screen with a Bluetooth connection, MABAI will disconnect that Bluetooth connection
        // If not, MABAI cannot connect a second time while the first connection is still valid
        if (mComputer.getMacAddress() != null && MyFunctions.validateMacAddress(mComputer.getMacAddress().trim())) {
            // Result will be return via the mBluetoothCallback.onDeviceConnected
            mMyFunctionsBle.getRemoteDevice(mComputer.getMacAddress());
        } else {
            // MAC address is invalid
            showError(getResources().getString(R.string.dlg_bluetooth_error), getResources().getString(R.string.msg_mac_address));
        }
    }

    private boolean validateDescription() {
        // Required
        if (mBinding.editTextDE.getText().toString().trim().isEmpty()) {
            mBinding.editTextDE.setError(getString(R.string.msg_description));
            requestFocus(mBinding.editTextDE);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateMacAddress() {
        // Required
        if (mBinding.editTextMA.getText().toString().trim().isEmpty()) {
            mBinding.editTextDE.setError(getString(R.string.msg_mac_address));
            requestFocus(mBinding.editTextDE);
            return false;
        } else {
            return MyFunctions.validateMacAddress(mBinding.editTextMA.getText().toString().trim());
        }
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
                mConnected = true;
                mBinding.testConnectionButton.setEnabled(true);
                mBinding.testConnectionButton.setAlpha(1.0f);
                mBinding.statusButton.setText(getString(R.string.button_connected));
                mBinding.statusButton.setBackgroundColor(Color.parseColor(MyConstants.GREEN));
                mMyFunctionsBle.setBluetooth(bluetooth);
            } else {
                mConnected = false;
                mBinding.testConnectionButton.setEnabled(false);
                mBinding.testConnectionButton.setAlpha(0.5f);
                mBinding.statusButton.setText(getString(R.string.button_disconnected));
                mBinding.statusButton.setBackgroundColor(Color.parseColor(MyConstants.RED));
                mMyFunctionsBle.setBluetooth(null);
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
