package ca.myairbuddyandi;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothStatusCodes;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Michel on 2023-05-05
 * Holds all of the logic for the MyFunctionsBLE class
 *
 * Bluetooth Low Energy
 *
 * All permissions are handled at the UI level
 */

public final class MyFunctionsBle extends AppCompatActivity {

    // Static
    private static final String LOG_TAG = "MyFunctionsBLE";
    private static final String NO_PERIPHERAL_ADDRESS_PROVIDED = "no peripheral address provided";
    private static final String VALUE_BYTE_ARRAY_IS_EMPTY = "value byte array is empty";
    private static final String VALUE_BYTE_ARRAY_IS_TOO_LONG = "value byte array is too long";
    private static final int DIRECT_CONNECTION_DELAY_IN_MS = 100;
    private static final int DIVE_COMPUTER_NOT_SUPPORTED = 99;
    private static final int ENABLE_NOTIFICATIONS_FAILED = 98;
    private static final int GATT_MAX_MTU_SIZE = 517;
    private static final int GATT_MIN_MTU_SIZE = 23;
    private static final int IDLE = 0;
    private static final int MAX_TRIES = 2;
    private static final int PAIRING_VARIANT_PIN = 0;
    private static final int PAIRING_VARIANT_PASSKEY = 1;
    private static final int PAIRING_VARIANT_PASSKEY_CONFIRMATION = 2;
    private static final int PAIRING_VARIANT_CONSENT = 3;
    private static final int PAIRING_VARIANT_DISPLAY_PASSKEY = 4;
    private static final int PAIRING_VARIANT_DISPLAY_PIN = 5;
    private static final int PAIRING_VARIANT_OOB_CONSENT = 6;
    private static final int PAIRING_VARIANT_PIN_16_DIGITS = 7;
    private static final int REQUEST_MTU_COMMAND = 1;
    private static final long DELAY_AFTER_BOND_LOST = 1000L;
    private static final UUID CCC_DESCRIPTOR_UUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
    private static final UUID CUD_DESCRIPTOR_UUID = UUID.fromString("00002901-0000-1000-8000-00805F9B34FB");

    // Public

    // Protected

    // Private
    private boolean mIsBondLost = false;
    private boolean mIsCommandQueueBusy = false;
    private boolean mDeviceInitiatedBonding = false;
    private boolean mIsManuallyBonding = false;
    private boolean mIsDiscovering = false;
    private boolean mIsConnectTimeoutOn = false;
    private @NotNull byte[] currentWriteBytes = new byte[0]; // Use to keep the same value between the writeCharacteristic() and the onCharacteristicWrite()
    private int mCurrentCommand = IDLE;
    private int mCurrentMtu = GATT_MIN_MTU_SIZE;
    private int mLibDiveComputerTimeout = 1000; // Defaults to one second timeout
    private int mRssi = 0; // Received Signal Strength Indicator
    private int mState = BluetoothProfile.STATE_DISCONNECTED;
    private ArrayList<Bluetooth> mBluetoothList = new ArrayList<>();
    private ArrayList<ComputerDives> mComputerDivesPickList;
    private Bluetooth mBluetooth;
    private BluetoothAdapter mBluetoothAdapter;
    private @NotNull final BluetoothCallback mBluetoothCallback;
    private BluetoothDevice mDevice;
    private BluetoothGatt mGatt;
    private BluetoothLeScanner mBluetoothLeScanner;
    private final Context mContext;
    private final Handler mHandler = new Handler(); // Our main handler that will receive callback notifications
    private LibDiveComputerFound mLibDiveComputerFound;
    private String mProduct;
    private @NotNull final Map<String, String> pinCodes = new ConcurrentHashMap<>();
    LinkedBlockingQueue<byte[]> mPacketQueue = new LinkedBlockingQueue<>();
    private final Queue<Runnable> mCommandQueue = new ConcurrentLinkedQueue<>();
    private @Nullable Runnable mTimeoutRunnable;
    private @Nullable Runnable mDiscoverServicesRunnable;

    // End of variables

    // Public constructor
    public MyFunctionsBle(Context context, @NotNull BluetoothCallback bluetoothCallback) {
        mContext = context;
        mBluetoothCallback = bluetoothCallback;
        MyFunctionsLibDiveComputer.initLibDiveBleComputerList(mContext);
        mProduct = mContext.getResources().getString(R.string.msg_unknown);
    }

    // My functions

    private void cancelConnectionTimer() {
        Log.d(LOG_TAG, "cancelConnectionTimer()");

        if (mTimeoutRunnable != null) {
            mHandler.removeCallbacks(mTimeoutRunnable);
            mTimeoutRunnable = null;
        }
        mIsConnectTimeoutOn = false;
    }

    private void cancelPendingServiceDiscovery() {
        Log.d(LOG_TAG, "cancelPendingServiceDiscovery()");

        if (mDiscoverServicesRunnable != null) {
            mHandler.removeCallbacks(mDiscoverServicesRunnable);
            mDiscoverServicesRunnable = null;
        }
    }

    // Invoke this method to to clear the services cache so that the services
    // are actually discovered again at the next collection.
    // The typical use-case for this would be a scenario where a firmware update
    // would change the services or characteristics that your device has.
    // NOTE: Reserved for future use
    private boolean clearServicesCache() {
        Log.d(LOG_TAG, "clearServicesCache()");

        boolean result = false;
        try {
            Method refreshMethod = mGatt.getClass().getMethod("refresh");
            if(refreshMethod != null) {
                result = (boolean) refreshMethod.invoke(mGatt);
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "clearServicesCache - Could not invoke refresh method");
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(getResources().getString(R.string.msg_bluetooth_clear_service_cache)));
        }
        return result;
    }

    private void completedCommand() {
        Log.d(LOG_TAG, "completedCommand()");

        mIsCommandQueueBusy = false;
        // Returns and removes the command at the front end of the queue
        mCommandQueue.poll();
        nextCommand();
    }

//    private void completedPacket() {
//        // Returns and removes the packet at the front end of the queue
//        mPacketQueue.poll();
//        nextPacket();
//    }

    @SuppressLint("MissingPermission")
    private void connect() {
        Log.d(LOG_TAG, "connect()");

        // Make sure we are disconnected before we start making a connection
        if (mState == BluetoothProfile.STATE_DISCONNECTED) {
            mHandler.postDelayed(() -> {
                // Connect to device with autoConnect = false
                registerBondingBroadcastReceivers();
                mIsDiscovering = false;
                Log.i(LOG_TAG, String.format("connect() - Connecting to Device %s at Address %s", mDevice.getName(), mDevice.getAddress()));
                postMessage(0, "", String.format("connect() - Connecting to Device %s at Address %s", mDevice.getName(), mDevice.getAddress()));
                // Although mDevice.connectGatt() triggers an onConnectionStateChange() it also returns mGatt
                // mDevice.connectGatt() also returns a BluetoothGatt
                BluetoothGatt bluetoothGatt = mDevice.connectGatt(mContext,false, mGattCallback, BluetoothDevice.TRANSPORT_LE);

                if (bluetoothGatt != null) {
                    mGattCallback.onConnectionStateChange(bluetoothGatt, BluetoothHciStatus.SUCCESS.value, BluetoothProfile.STATE_CONNECTING);
                    startConnectionTimer(mDevice);
                } else {
                    Log.e(LOG_TAG, String.format("connect() - Connection failed with status %s for Device %s at address %s", "Unknown", mDevice.getName(), mDevice.getAddress()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_connection_failed), mContext.getResources().getString(R.string.msg_unknown), mDevice.getName(), mDevice.getAddress()));
                }
            }, DIRECT_CONNECTION_DELAY_IN_MS);
        } else {
            Log.e(LOG_TAG, String.format("connect() - Device %s not yet disconnected, will not connect", mDevice.getName()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_device_not_yet_connectede), mDevice.getName()));
        }
    }

    @SuppressLint("MissingPermission")
    public void connect(String product, BluetoothDevice device) {
        Log.d(LOG_TAG, "connect(product, device)");

        if (mIsDiscovering) {
            stopScan();
        }

        // Get the first valid reference to a Device
        mProduct = product;
        mDevice = device;

        // Must happens before the
        registerBondingBroadcastReceivers();

        // NOTE: If device is known, e.g. macAddressFilter is specified, and device.getType() = BluetoothDevice.DEVICE_TYPE_UNKNOWN then use autoconnect = true

        showBusyDialog(true);

        startConnectionTimer(mDevice);

        Log.i(LOG_TAG, String.format("connect(BluetoothDevice device) - Connecting to Device %s at Address %s", mDevice.getName(), mDevice.getAddress()));
        postMessage(0, "", String.format("connect(BluetoothDevice device) - Connecting to Device %s at Address %s", mDevice.getName(), mDevice.getAddress()));

        // Will trigger onConnectionStateChange()
        mDevice.connectGatt(mContext,false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
    }

    @SuppressLint("MissingPermission")
    private void connectFailed(@NotNull final BluetoothDevice device, @NotNull final BluetoothHciStatus status, final boolean isConnectTimeoutOn ) {
        Log.d(LOG_TAG, "connectFailed()");

        if (isConnectTimeoutOn) {
            Log.e(LOG_TAG, String.format("connectFailed - Connection timed out with status %s for Device %s at Address %s. Disconnecting...", status.name(), device.getName(), device.getAddress()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_connection_failed_timed_out), status.name(), device.getName(), device.getAddress()));
        } else {
            Log.e(LOG_TAG, String.format("connectFailed - Connection failed with status %s for Device %s at address %s. Disconnecting...", status.name(), device.getName(), device.getAddress()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_connection_failed), status.name(), device.getName(), device.getAddress()));
        }

        cancelConnectionTimer();
        postOnDeviceConnected(false);

        // TODO: Do we need that?
        //       How to retry without Bluetooth Blesse code?
//        final String peripheralAddress = device.getAddress();
//
//        // Get the number of retries for this device
//        int nrRetries = 0;
//        final Integer retries = connectionRetries.get(peripheralAddress);
//        if (retries != null) nrRetries = retries;
//
//        removePeripheralFromCaches(peripheralAddress);
//
//        // Retry connection or conclude the connection has failed
//        if (nrRetries < MAX_CONNECTION_RETRIES && status != HciStatus.CONNECTION_FAILED_ESTABLISHMENT) {
//            Log.d(TAG, String.format("retrying connection to %s (%s)", peripheral.getName(), peripheralAddress));
//            nrRetries++;
//            connectionRetries.put(peripheralAddress, nrRetries);
//            unconnectedPeripherals.put(peripheralAddress, peripheral);
//            peripheral.connect();
//        } else {
//            Log.e(TAG, String.format("connection to %s (%s) failed", peripheral.getName(), peripheralAddress));
//            callBackHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    bluetoothCentralManagerCallback.onConnectionFailed(peripheral, status);
//                }
//            });
//        }
    }

    // If this is needed for your device it is probably not iOS compatible since it does not have an equivalent call.
    // But it is possible in the Bluetooth standard!
    @SuppressLint("MissingPermission")
    private boolean createBond() {
        Log.d(LOG_TAG, "createBond()");

        // Check if we have a Gatt object
        if (mGatt == null) {
            // No gatt object so no connection issued, do create bond immediately
            Log.i(LOG_TAG, String.format(Locale.ENGLISH,"Connecting and creating bond for Device %s", mDevice.getName()));
            postMessage(0,"",String.format(mContext.getResources().getString(R.string.msg_bluetooth_bond_creating),mDevice.getName()));
            registerBondingBroadcastReceivers();
            return mDevice.createBond();
        }

        // Enqueue the bond command because a connection has been issued or we are already connected
        Log.d(LOG_TAG, "createBond-enqueuingCommand-createBond");
        return enqueueCommand(new Runnable() {

            @Override
            public void run() {
                mIsManuallyBonding = true;
                if (!mDevice.createBond()) {
                    Log.e(LOG_TAG, String.format(Locale.ENGLISH,"bonding failed for %s", mDevice.getAddress()));
                    postMessage(2,mContext.getResources().getString(R.string.dlg_bluetooth_error),String.format(mContext.getResources().getString(R.string.msg_bluetooth_bonding_failed), mDevice.getName()));
                    completedCommand();
                } else {
                    Log.i(LOG_TAG, String.format(Locale.ENGLISH,"manually bonding %s", mDevice.getAddress()));
                    postMessage(2,mContext.getResources().getString(R.string.dlg_bluetooth_message),String.format(mContext.getResources().getString(R.string.msg_bluetooth_bond_manually), mDevice.getName()));
//                    nrRetries++;
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void delayedDiscoverServices(BluetoothDevice device, final long delay) {
        Log.d(LOG_TAG, "delayedDiscoverServices()");

        mDiscoverServicesRunnable = () -> {
            Log.i(LOG_TAG, String.format("delayedDiscoverServices - Discovering services of %s with delay of %d ms", device, delay));
            postMessage(0, "", String.format(Locale.ENGLISH,"delayedDiscoverServices - Discovering services of %s with delay of %d ms", device, delay));
            if (mGatt != null && mGatt.discoverServices()) {
                mIsDiscovering = true;
            } else {
                Log.e(LOG_TAG, String.format("delayedDiscoverServices - DiscoverServices failed to start for Device %s", device));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_discover_services_failed), device));
            }
            mDiscoverServicesRunnable = null;
        };
        mHandler.postDelayed(mDiscoverServicesRunnable, delay);
    }

    // NOTE: Reserved for future use
    private boolean disableNotifications(String service,  BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "disableNotifications()");

        if (!isCharacteristicNotifiable(characteristic) && !isCharacteristicNotifiable(characteristic)) {
            // Already logged in disableNotifications()
            Log.w(LOG_TAG, String.format("disableNotifications - Service %s and Characteristic %s do not have a notify or indicate property",service, characteristic.getUuid().toString()));
            postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_not_notifiable), service, characteristic.getUuid().toString()));
            return false;
        }

        final BluetoothGattDescriptor descriptor = characteristic.getDescriptor(CCC_DESCRIPTOR_UUID);

        if (descriptor == null) {
            Log.e(LOG_TAG, String.format("disableNotifications - >Could not get CCC descriptor for Service %1$s and Characteristic %2$s", service, characteristic.getUuid().toString()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_no_descriptor), service, characteristic.getUuid().toString()));
            return false;
        }

        Log.d(LOG_TAG, "disableNotifications-enqueuingCommand-writeDescriptor");
        return enqueueCommand(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (isConnected()) {
                    // The characteristic is readable
                    // Will trigger a onCharacteristicRead
                    if (writeDescriptor(descriptor, BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, true)) {
                        // Request is successful
                        // Command will be completed in gattCallback.onDescriptorWrite
                        Log.i(LOG_TAG, String.format("disableNotifications - Writing Descriptor for Device %s, Service %s and Characteristic %s", mDevice, service, characteristic.getUuid().toString()));
                        postMessage(0, "", String.format("Writing Descriptor for Device %s, Service %s and Characteristic %s", mDevice, service, characteristic.getUuid().toString()));
                    } else {
                        Log.e(LOG_TAG, String.format("disableNotifications - Writing Descriptor failed for Device %s, Service %s and Characteristic %s", mDevice, service, characteristic.getUuid().toString()));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_write_descriptor_failed), mDevice, service, characteristic.getUuid().toString()));
                        // Remove the failed command
                        // Move to the next command
                        completedCommand();
                    }
                } else {
                    Log.e(LOG_TAG, String.format("disableNotifications - writeDescriptor failed. Not connected to device %s", mDevice.getName()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_write_descriptor_failed_not_connected), mDevice.getName()));
                    // Command cannot be executed because not connected
                    // Command still in the queue
                    // Remove the command from the queue
                    // Move to the next command
                    completedCommand();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void disconnect() {
        Log.d(LOG_TAG, "disconnect()");

        if (mState == BluetoothProfile.STATE_CONNECTED || mState == BluetoothProfile.STATE_CONNECTING) {
            if (mGatt != null) {
                mGattCallback.onConnectionStateChange(mGatt, BluetoothHciStatus.SUCCESS.value, BluetoothProfile.STATE_DISCONNECTING);
            }
            mHandler.post(() -> {
                if (mState == BluetoothProfile.STATE_DISCONNECTING && mGatt != null) {
                    // Will trigger a onConnectionStateChange with a BluetoothProfile.STATE_DISCONNECTED
                    mGatt.disconnect();
                    Log.i(LOG_TAG, String.format("disconnect - Disconnecting Gatt for Service %s at Address %s", mDevice.getName(), mDevice.getAddress()));
                    postMessage(0, "", String.format("disconnect - Disconnecting Gatt for Service %s at Address %s", mDevice.getName(), mDevice.getAddress()));
                }
            });
        } else {
            // NOTE: Not needed
            //listener.disconnected(BluetoothPeripheral.this, HciStatus.SUCCESS);
        }
    }

    @SuppressLint("MissingPermission")
    private void disconnectCleanup(final boolean notify, @NotNull final BluetoothHciStatus status) {
        Log.d(LOG_TAG, "disconnectCleanup()");

        if (mGatt != null) {
            mGatt.close();
            mGatt = null;
        }
        mCommandQueue.clear();
        mIsCommandQueueBusy = false;
        mPacketQueue.clear();
        // TODO: Needed?
//        notifyingCharacteristics.clear();
        mCurrentMtu = GATT_MIN_MTU_SIZE;
        mCurrentCommand = IDLE;
        mIsManuallyBonding = false;
        mDeviceInitiatedBonding = false;
        mIsDiscovering = false;
        try {
            mContext.unregisterReceiver(bondStateReceiver);
            mContext.unregisterReceiver(pairingRequestBroadcastReceiver);
        } catch (IllegalArgumentException e) {
            // In case bluetooth is off, unregistering broadcast receivers may fail
        }
        mIsBondLost = false;
        showBusyDialog(false);

        // NOTE: Not needed
//        if (notify) {
//            listener.disconnected(BluetoothPeripheral.this, status);
//        }
    }

    private void diveComputerNotSupported() {
        Log.d(LOG_TAG, "diveComputerNotSupported()");

        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error),mContext.getResources().getString(R.string.msg_bluetooth_not_a_supported_computer));
        cancelConnectionTimer();
        postOnDeviceConnected(false);
    }

    public boolean enableNotifications(String service, String readCharacteristic, boolean enable) {
        Log.d(LOG_TAG, "enableNotifications()");

        if (mGatt == null) {
            return false;
        }

        UUID uuidService = UUID.fromString(service);
        BluetoothGattService bluetoothGattservice = mGatt.getService(uuidService);

        if (bluetoothGattservice == null) {
            Log.e(LOG_TAG, String.format("enableNotifications - Service %s is null", service));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_service_is_null), service));
            return false;
        }

        UUID UuidReadCharacteristic = UUID.fromString(readCharacteristic);
        BluetoothGattCharacteristic readBluetoothGattCharacteristic = mGatt.getService(uuidService).getCharacteristic(UuidReadCharacteristic);

        // Check if characteristic is valid
        if(readBluetoothGattCharacteristic == null) {
            Log.i(LOG_TAG, String.format("enableNotifications - Characteristic is NULL, ignoring setNotify request for Service %s and Characteristic %s", service, readCharacteristic));
            postMessage(0, " ", String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_notify_is_null), service, readCharacteristic));
            return false;
        }

        if (isCharacteristicNotifiable(readBluetoothGattCharacteristic) && isCharacteristicIndicatable(readBluetoothGattCharacteristic)) {
            Log.i(LOG_TAG, String.format("enableNotifications - Service %s and Characteristic %s does not have notify or indicate property", service, readBluetoothGattCharacteristic.getUuid()));
            postMessage(0, " ", String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_not_notifiable), service, readBluetoothGattCharacteristic.getUuid()));
            return false;
        }

        // Get the CCC Descriptor for the characteristic
        final BluetoothGattDescriptor descriptor = readBluetoothGattCharacteristic.getDescriptor(CCC_DESCRIPTOR_UUID);

        if(descriptor == null) {
            Log.e(LOG_TAG, String.format("enableNotifications - Could not get CCC descriptor for Service %s and Characteristic %s", service, readBluetoothGattCharacteristic.getUuid()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error) ,String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_no_descriptor), service, readBluetoothGattCharacteristic.getUuid()));
            return false;
        }

        // Check if characteristic has NOTIFY or INDICATE properties and set the correct byte value to be written
        byte[] value;
        boolean result = true;
        if (isCharacteristicNotifiable(readBluetoothGattCharacteristic)) {
            value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
            final byte[] finalValue = enable ? value : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            result = writeDescriptor(descriptor, finalValue, enable);
        }

        if (isCharacteristicIndicatable(readBluetoothGattCharacteristic)) {
            value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
            final byte[] finalValue = enable ? value : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
            result = writeDescriptor(descriptor, finalValue, enable);
        }

        return result;
    }

    private boolean enqueueCommand(Runnable command) {
        Log.d(LOG_TAG, "enqueueCommand()");

        final boolean result = mCommandQueue.add(command);
        if (result) {
            nextCommand();
        } else {
            Log.e(LOG_TAG, "enqueueCommand - Could not enqueue command");
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error),mContext.getResources().getString(R.string.msg_bluetooth_enqueue_command_failed));
        }
        return result;
    }

    private boolean enqueuePacket(byte[] packet) {
        Log.d(LOG_TAG, "enqueuePacket()");

//        final boolean result = mPacketQueue.add(packet);
//        if (result) {
//            nextPacket();
//        } else {
//            Log.e(LOG_TAG, "enqueuePacket - Could not enqueue command");
//            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error),mContext.getResources().getString(R.string.msg_bluetooth_enqueue_command_failed));
//        }
//        return result;

        Log.d(LOG_TAG, String.format(Locale.ENGLISH,"enqueuePacket with value %s",BluetoothBytesParser.asHexString(packet)));
        final boolean result = mPacketQueue.add(packet);

        if (!result) {
            Log.e(LOG_TAG, "enqueuePacket - Could not enqueue packet");
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error),mContext.getResources().getString(R.string.msg_bluetooth_enqueue_packet_failed));
        }

        return result;
    }

    @SuppressLint("MissingPermission")
    public Set<BluetoothDevice> getBondedDevices() {
        Log.d(LOG_TAG, "getBondedDevices()");

        sendMessage(0, "", String.format(mContext.getResources().getString(R.string.msg_bluetooth_show_paired_devices)));
        return mBluetoothAdapter.getBondedDevices();
    }

    @SuppressLint("MissingPermission")
    private BluetoothBondState getBondState() {
        Log.d(LOG_TAG, "getBondState()");

        return BluetoothBondState.fromValue(mDevice.getBondState());
    }

    @SuppressLint("MissingPermission")
    private BluetoothConnectionState getConnectionState() {
        Log.d(LOG_TAG, "getConnectionState()");

        return BluetoothConnectionState.fromValue(mState);
    }

    @SuppressLint("MissingPermission")
    public void getRemoteDevice(String macAddress) {
        Log.d(LOG_TAG, "getRemoteDevice()");

        if (mIsDiscovering) {
            stopScan();
        }

        // Result will be return via the mBluetoothCallback.onDeviceConnected
        mDevice = mBluetoothAdapter.getRemoteDevice(macAddress);

        startConnectionTimer(mDevice);

        showBusyDialog(true);

        // Will trigger a mBluetoothCallback.onConnectionStateChange()
        mDevice.connectGatt(mContext, false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
    }

    private int getMaximumWriteValueLength(@NotNull final BluetoothWriteType bluetoothWriteType) {
        Log.d(LOG_TAG, "getMaximumWriteValueLength()");

        Objects.requireNonNull(bluetoothWriteType, "bluetoothWriteType is null");

        switch (bluetoothWriteType) {
            case WITH_RESPONSE:
                return 512;
            case SIGNED:
                return mCurrentMtu - 15;
            default:
                return mCurrentMtu - 3;
        }
    }

    private @Nullable String getPinCode(@NotNull final BluetoothDevice device) {
        Log.d(LOG_TAG, "getPinCode()");

        return pinCodes.get(device.getAddress());
    }

    @SuppressLint("MissingPermission")
    private void handleBondStateChange(final int bondState, final int previousBondState) {
        Log.d(LOG_TAG, "handleBondStateChange()");

        switch (bondState) {
            case BOND_BONDING:
                // No action needed other than logging and messaging
                Log.i(LOG_TAG, String.format("handleBondStateChange - Starting bonding with Device  %s at Address %s", mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
                postMessage(0, "", String.format(mContext.getResources().getString(R.string.msg_bluetooth_starting_bonding), mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
                break;
            case BOND_BONDED:
                Log.i(LOG_TAG, String.format("handleBondStateChange - Bonded with Device %s at Address %s", mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
                postMessage(0, "", String.format(mContext.getResources().getString(R.string.msg_bluetooth_bonding_with), mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));

                // NOTE: Nothing to do?
//                callbackHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        peripheralCallback.onBondingSucceeded(BluetoothPeripheral.this);
//                    }
//                });

                // Check if we are missing a gatt object. This is the case if createBond was called on a disconnected peripheral
                if (mGatt == null) {
                    // Bonding succeeded so now we can connect
                    connect();
                    return;
                }

                // If bonding was started at connection time, we may still have to discover the services
                // Also make sure we are not starting a discovery while another one is already in progress
                if (mGatt.getServices().isEmpty() && !mIsDiscovering) {
                    delayedDiscoverServices(mDevice, 0);
                }

                // If we are doing a manual bond, complete the command
                if (mIsManuallyBonding) {
                    mIsManuallyBonding = false;
                    completedCommand();
                }

                // If the peripheral initiated the bonding, continue the queue
                if (mDeviceInitiatedBonding) {
                    mDeviceInitiatedBonding = false;
                    nextCommand();
                }

                break;
            case BOND_NONE:
                if (previousBondState == BOND_BONDING) {
                    // No action needed other than logging and messaging
                    Log.e(LOG_TAG, String.format("handleBondStateChange - Bonding failed for device %s. Disconnecting device", mDevice.getName()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_bonding_failed), mDevice.getName()));

                    // NOTE: Nothing to do?
//                    callbackHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            peripheralCallback.onBondingFailed(BluetoothPeripheral.this);
//                        }
//                    });

                } else {
                    Log.e(LOG_TAG, String.format("handleBondStateChange - Bond lost for device %s", mDevice.getName()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_bond_lost), mDevice.getName()));
                    mIsBondLost = true;

                    // Cancel the discoverServiceRunnable if it is still pending
                    cancelPendingServiceDiscovery();

                    // NOTE: Nothing to do?
//                    callbackHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            peripheralCallback.onBondLost(BluetoothPeripheral.this);
//                        }
//                    });
                }

                // There are 2 scenarios here:
                // 1. The user removed the peripheral from the list of paired devices in the settings menu
                // 2. The peripheral bonded with another phone after the last connection
                //
                // In both scenarios we want to end up in a disconnected state.
                // When removing a bond via the settings menu, Android will disconnect the peripheral itself.
                // However, the disconnected event (CONNECTION_TERMINATED_BY_LOCAL_HOST) will come either before or after the bond state update and on a different thread
                // Note that on the Samsung J5 (J530F) the disconnect happens but no bond change is received!
                // And in case of scenario 2 we may need to issue a disconnect ourselves.
                // Therefor to solve this race condition, add a bit of delay to see if a disconnect is needed for scenario 2
                mHandler.postDelayed(() -> {
                    if (getConnectionState() == BluetoothConnectionState.CONNECTED) {
                        // If we are still connected, then disconnect because we usually can't interact with the peripheral anymore
                        // Some peripherals already do a disconnect by themselves (REMOTE_USER_TERMINATED_CONNECTION) so we may already be disconnected
                        disconnect();
                    }
                }, 100);

                break;
        }
    }

    public boolean initBluetoothAdapter() {
        Log.d(LOG_TAG, "initBluetoothAdapter()");

        // Initializes  Bluetooth adapter
        // For API level 18 and above, get a reference to BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth adapter has been created on this device.
        return mBluetoothAdapter != null;
    }

    @SuppressLint("MissingPermission")
    private boolean internalWriteCharacteristic(@NotNull final BluetoothGattCharacteristic characteristic,
                                                @NotNull final byte[] value,
                                                @NotNull final BluetoothWriteType bluetoothWriteType) {
        Log.d(LOG_TAG, "internalWriteCharacteristic()");

        if (mGatt == null) return false;

        currentWriteBytes = value;

        if (Build.VERSION.SDK_INT >= 33) {
            // Will trigger a gattCallback.onCharacteristicWrite
            final int result = mGatt.writeCharacteristic(characteristic, currentWriteBytes, bluetoothWriteType.writeType);
            return result == BluetoothStatusCodes.SUCCESS;
        } else {
            characteristic.setWriteType(bluetoothWriteType.writeType);
            characteristic.setValue(value);
            // Will trigger a gattCallback.onCharacteristicWrite
            return mGatt.writeCharacteristic(characteristic);
        }
    }

    public boolean isBluetoothAdapterEnabled() {
        Log.d(LOG_TAG, "isBluetoothAdapterEnabled()");

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // the parent activity is responsible to fire an intent to display a dialog asking the user
        // to grant permission to enable it.
        return mBluetoothAdapter.isEnabled();
    }

    public boolean isBluetoothAdapterInitialized() {
        Log.d(LOG_TAG, "isBluetoothAdapterInitialized()");

        return mBluetoothAdapter != null;
    }

    // NOTE: Leave as is
    public boolean isBluetoothEnabled() {
        Log.d(LOG_TAG, "isBluetoothEnabled()");

        // Check if Bluetooth is actually enabled/available on this device
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private boolean isCharacteristicIndicatable(BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "isCharacteristicIndicatable()");

        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) !=0);
    }

    private boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "isCharacteristicNotifiable()");

        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) !=0);
    }

    private boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "isCharacteristicReadable()");

        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    private boolean isCharacteristicWritable(BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "isCharacteristicWritable()");

        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) !=0);
    }

    private boolean isCharacteristicWritableWithoutResponse(BluetoothGattCharacteristic characteristic) {
        Log.d(LOG_TAG, "isCharacteristicWritableWithoutResponse()");

        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) !=0);
    }

    // NOTE: For future reference
//    private boolean isCharacteristicWritableWithoutResponse(BluetoothGattCharacteristic characteristic) {
//    Log.d(LOG_TAG, "isCharacteristicWritableWithoutResponse()");
//
//        return (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE
//                | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
//    }

    private boolean isConnected() {
        Log.d(LOG_TAG, "isConnected()");

        return mGatt != null && mState == BluetoothProfile.STATE_CONNECTED;
    }

    private boolean isDescriptorReadable(BluetoothGattDescriptor d) {
        Log.d(LOG_TAG, "isDescriptorReadable()");

        return ((d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_READ) !=0
                || (d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED) !=0
                || (d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM) !=0);
    }

    private boolean isDescriptorWritable(BluetoothGattDescriptor d) {
        Log.d(LOG_TAG, "isDescriptorWritable()");

        return ((d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_WRITE) !=0
                || (d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED) !=0
                || (d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM) !=0
                || (d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED) !=0
                || (d.getPermissions() & BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM) !=0);
    }

    public boolean isDiscovering() {
        Log.d(LOG_TAG, "isDiscovering()");

        return mIsDiscovering;
    }


    private boolean isLibDiveComputer(BluetoothGatt gatt) {
        Log.d(LOG_TAG, "isLibDiveComputer()");

        // Make sure the dive computer is supported by libdivecomputer
        for (BluetoothGattService service : gatt.getServices()) {
            mLibDiveComputerFound = MyFunctionsLibDiveComputer.isLibDiveComputer(service.getUuid().toString());
            if (mLibDiveComputerFound.getFound()) {
                // mLibDiveComputerFound will be copied to a Bluetooth POJO in post OnDeviceConnected()
                return true;
            }
        }
       return false ;
    }

    @SuppressLint("MissingPermission")
    private void nextCommand() {
        Log.d(LOG_TAG, "nextCommand()");

        // If there is still a command being executed then bail out
        if(mIsCommandQueueBusy) {
            return;
        }

        // Check if there is something to do at all
        Runnable bluetoothCommand = mCommandQueue.peek();
        if (bluetoothCommand == null) return;

        // Check if we still have a valid gatt object
        if (mGatt == null) {
            assert false;
            Log.e(LOG_TAG, String.format("nextCommand - GATT is NULL for Device %s at Address %s, clearing command queue", mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_gatt_null), mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
            mCommandQueue.clear();
            mIsCommandQueueBusy = false;
            return;
        }

        // Check if the peripheral has initiated bonding as this may be a reason for failures
        if (getBondState() == BluetoothBondState.BONDING) {
            Log.w(LOG_TAG, String.format("nextCommand - Bonding is in progress for Device %s at Address %s, waiting for bonding to complete", mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_bonding_in_progress), mGatt.getDevice().getName(), mGatt.getDevice().getAddress()));
            mDeviceInitiatedBonding = true;
            return;
        }

        // Execute the next command in the queue
        if (mCommandQueue.size() > 0) {
            // Get the command at the front of the queue
            bluetoothCommand = mCommandQueue.peek();
            mIsCommandQueueBusy = true;

            Runnable finalBluetoothCommand = bluetoothCommand;
            @SuppressLint("MissingPermission") Runnable bluetoothCommandRunnable = () -> {
                try {
                    assert finalBluetoothCommand != null;
                    finalBluetoothCommand.run();
                } catch (Exception ex) {
                    Log.e(LOG_TAG, String.format("nextCommand - Command exception for Device %s. Exemption:", mGatt.getDevice().getName()), ex);
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_command_exception), mGatt.getDevice().getName()));

                }
            };
            mHandler.post(bluetoothCommandRunnable);
        }
    }

//    private byte[] nextPacket() {
//        Log.d(LOG_TAG, "nextPacket()");
//        // Pull the next packet from the queue
//        if (mPacketQueue.size() > 0) {
//            // Get the packet at the front of the queue
//            final byte[] packet = mPacketQueue.peek();
//            return packet;
//        } else {
//            return null;
//        }
//    }

    private void printGattTable(BluetoothGatt gatt) {
        Log.d(LOG_TAG, "printGattTable()");

        if (gatt.getServices().isEmpty()) {
            Log.i(LOG_TAG, "printGattTable - No Service and Characteristic available, call discoverServices() first?");
            // No postMessage() needed
            return;
        }

        String gattTable = "printGattTable";

        for (BluetoothGattService service : gatt.getServices()) {
            gattTable = gattTable + "\nService: " + service.getUuid().toString() + "\nCharacteristics:";
            List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
            if (characteristics.size() == 0) {
                gattTable = gattTable + "\nNone";
            } else {
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    gattTable = gattTable + "\nCharacteristic: " + characteristic.getUuid().toString();

                    String read = "R: N,";
                    String write = " W: N,";
                    String writeWwr = " WWR: N,";
                    String notify = " N: N,";
                    String indicate = " I: N";

                    if (isCharacteristicReadable(characteristic)) {
                        read = "R: Y,";
                    }
                    if (isCharacteristicWritable(characteristic)) {
                        write = " W: Y,";
                    }
                    if (isCharacteristicWritableWithoutResponse(characteristic)) {
                        writeWwr = " WWR: Y,";
                    }
                    if (isCharacteristicNotifiable(characteristic)) {
                        notify = " N: Y,";
                    }
                    if (isCharacteristicIndicatable(characteristic)) {
                        indicate = " I: Y";
                    }
                    gattTable = gattTable + " Is: " + read + write + writeWwr + notify + indicate + "\nDescriptors:";
                    List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                    if (descriptors.size() == 0) {
                        gattTable = gattTable + "\nNone";
                    } else {
                        for (BluetoothGattDescriptor descriptor : descriptors) {
                            gattTable = gattTable + "\nDescriptor: " + descriptor.getUuid().toString();
                            String readDescriptor = "R: N,";
                            String writeDescriptor = " W: N";
                            if (isDescriptorReadable(descriptor)) {
                                readDescriptor = "R: Y,";
                            }
                            if (isDescriptorWritable(descriptor)) {
                                writeDescriptor = " W: Y";
                            }
                            gattTable = gattTable + " Is: " + readDescriptor + writeDescriptor;
                        }
                    }
                }
            }
        }

        Log.i(LOG_TAG, gattTable);
        // No postMessage() needed
    }

    @SuppressLint("MissingPermission")
    public boolean readCharacteristic(@NotNull String service, @NotNull String readCharacteristic) {
        Log.d(LOG_TAG, "readCharacteristic()");

        if (mGatt == null) {
            return false;
        }

        UUID uuidService = UUID.fromString(service);
        BluetoothGattService bluetoothGattservice = mGatt.getService(uuidService);

        if (bluetoothGattservice == null) {
            Log.e(LOG_TAG, String.format("readCharacteristic - Service %s is null",service));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_service_is_null), service));
            return false;
        }

        UUID UuidReadCharacteristic = UUID.fromString(readCharacteristic);
        BluetoothGattCharacteristic readBluetoothGattCharacteristic = mGatt.getService(uuidService).getCharacteristic(UuidReadCharacteristic);

        if (readBluetoothGattCharacteristic == null) {
            Log.e(LOG_TAG, String.format("readCharacteristic - Characteristic is null for Service %s and Characteristic %s",service, readCharacteristic));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_is_null), service, readCharacteristic));
            return false;
        }

        if (!isCharacteristicReadable(readBluetoothGattCharacteristic)) {
            Log.w(LOG_TAG, String.format("readCharacteristic - Service %1$s and Characteristic %2$s does not have a read property",service, readBluetoothGattCharacteristic.getUuid()));
            postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_not_readable), service, readBluetoothGattCharacteristic.getUuid()));
            return false;
        }

        Log.d(LOG_TAG, "readCharacteristic-enqueuingCommand-readCharacteristic");
        return enqueueCommand(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    // The characteristic is readable
                    // Will trigger a onCharacteristicRead
                    if (mGatt.readCharacteristic(readBluetoothGattCharacteristic)) {
                        // Request is successful
                        // Command will be completed in gattCallback.onCharacteristicRead
                        Log.i(LOG_TAG, String.format("readCharacteristic - Reading Service %s and Characteristic %s", service, readCharacteristic));
                        postMessage(0, "", String.format("Reading Service %s and Characteristic %s", service, readCharacteristic));
                    } else {
                        Log.e(LOG_TAG, String.format("readCharacteristic - readCharacteristic failed for Service %s and Characteristic %s", service, readCharacteristic));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_read_request_failed), service, readCharacteristic));
                        // Remove the failed command
                        // Move to the next command
                        completedCommand();
                    }
                } else {
                    Log.e(LOG_TAG, String.format("readCharacteristic - readCharacteristic failed. Not connected to Device %s", mDevice.getName()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_read_failed_not_connected), mDevice.getName()));
                    // Command cannot be executed because not connected
                    // Command still in the queue
                    // Remove the command from the queue
                    // Move to the next command
                    completedCommand();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private boolean readDescriptor(@NotNull final BluetoothGattDescriptor descriptor) {
        Log.d(LOG_TAG, "readDescriptor()");

        Log.d(LOG_TAG, "readDescriptor-enqueuingCommand-readDescriptor");
        return enqueueCommand(new Runnable() {
                @Override
                public void run () {
                if (isConnected()) {
                    if (mGatt.readDescriptor(descriptor)) {
                        // Request is successful
                        // Command will be completed in gattCallback.onDescriptorRead
                        Log.i(LOG_TAG, String.format("readDescriptor - Reading descriptor %s", descriptor.getUuid()));
                        postMessage(0, "", String.format("Reading descriptor %s", descriptor.getUuid()));
                    } else {
                        Log.e(LOG_TAG, String.format(Locale.ENGLISH, "readDescriptor failed for characteristic: %s", descriptor.getUuid()));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_read_descriptor_failed), descriptor.getUuid()));

                        // Remove the failed command
                        // Move to the next command
                        completedCommand();
                    }
                } else {
                    Log.e(LOG_TAG, String.format("readDescriptor - readDescriptor failed. Not connected to device %s",mDevice.getName()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_read_descriptor_failed_not_connected), mDevice.getName()));
                    // Command cannot be executed because not connected
                    // Command still in the queue
                    // Remove the command from the queue
                    // Move to the next command
                    completedCommand();
                }
            }
        });
    }

    public boolean readRemoteRssi() {
        Log.d(LOG_TAG, "readRemoteRssi()");

        Log.d(LOG_TAG, "readRemoteRssi-enqueuingCommand-readRemoteRssi");
        return enqueueCommand(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                if (isConnected()) {
                    if (!mGatt.readRemoteRssi()) {
                        Log.e(LOG_TAG, "readRemoteRssi - readRemoteRssi request failed");
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), mContext.getResources().getString(R.string.msg_bluetooth_rssi_request_failed));
                        completedCommand();
                    }
                } else {
                    completedCommand();
                }
            }
        });
    }

    private void registerBondingBroadcastReceivers() {
        Log.d(LOG_TAG, "registerBondingBroadcastReceivers()");

        mContext.registerReceiver(bondStateReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        // TODO: Is it needed?
        mContext.registerReceiver(pairingRequestBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST));
    }

    @SuppressLint("MissingPermission")
    private boolean requestMtu(int mtu) {
        Log.d(LOG_TAG, "requestMtu()");

        if (mtu < GATT_MIN_MTU_SIZE || mtu > GATT_MAX_MTU_SIZE) {
            throw new IllegalArgumentException("mtu must be between 23 and 517");
        }

        if (!isConnected()) {
            Log.e(LOG_TAG, "requestMtu - Device is not connected");
            return false;
        }

        // Add the command to the queue
        // And try to execute the next command
        Log.d(LOG_TAG, "requestMtu-enqueuingCommand-requestMtu");
        return enqueueCommand(() -> {
            if (isConnected()) {
                // No need to loop to find the perfect MTU
                // The device will assigned the maximum MTU it supports in gattCallback.onMTUChanged()
                // Will trigger gattCallback.onMTUChanged()
                if (mGatt.requestMtu(mtu)) {
                    mCurrentCommand = REQUEST_MTU_COMMAND;
                    // Request is successful
                    // Command will be completed in gattCallback.onMTUChanged()
                    Log.i(LOG_TAG, String.format("requestMtu - Requesting MTU of %d", mtu));
                    postMessage(0, "", String.format(Locale.ENGLISH,"requestMtu - Requesting MTU of %d", mtu));
                } else {
                    Log.e(LOG_TAG, "requestMtu - RequestMtu failed");
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error),mContext.getResources().getString(R.string.msg_bluetooth_request_mtu_failed));
                    // Remove the failed command
                    // Move to the next command
                    completedCommand();
                }
            } else {
                Log.e(LOG_TAG, "requestMtu - RequestMtu failed. Not connected to the device");
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_request_mtu_failed_not_connected), mDevice.getName()));
                // Command cannot be executed because not connected
                // Command still in the queue
                // Remove the command from the queue
                // Move to the next command
                completedCommand();
            }
        });
    }

    private String pairingVariantToString(final int variant) {
        Log.d(LOG_TAG, "pairingVariantToString()");

        switch (variant) {
            case PAIRING_VARIANT_PIN:
                return "PAIRING_VARIANT_PIN";
            case PAIRING_VARIANT_PASSKEY:
                return "PAIRING_VARIANT_PASSKEY";
            case PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                return "PAIRING_VARIANT_PASSKEY_CONFIRMATION";
            case PAIRING_VARIANT_CONSENT:
                return "PAIRING_VARIANT_CONSENT";
            case PAIRING_VARIANT_DISPLAY_PASSKEY:
                return "PAIRING_VARIANT_DISPLAY_PASSKEY";
            case PAIRING_VARIANT_DISPLAY_PIN:
                return "PAIRING_VARIANT_DISPLAY_PIN";
            case PAIRING_VARIANT_OOB_CONSENT:
                return "PAIRING_VARIANT_OOB_CONSENT";
            default:
                return "UNKNOWN";
        }
    }

    // Use this function when into a running process
    // iconId 0 : Logging. No display necessary. No translation. No String Log.i Same as Log.d
    //        1 : Alert                                                    Log.w
    //        2 : Stop                                                     Log.e
    private void postMessage(int iconId, String title, String message) {
        Log.d(LOG_TAG, "postMessage()");

        // Post the message without delay
        Runnable postMessageRunnable = () -> mBluetoothCallback.bubbleUpMessage(iconId, title, message);
        mHandler.post(postMessageRunnable);
    }

    private void postOnCharacteristicRead(@NotNull BluetoothDevice device, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, int status) {
        Log.d(LOG_TAG, "postOnCharacteristicRead()");

        Runnable postOnCharacteristicReadRunnable = () -> mBluetoothCallback.onCharacteristicRead(device, value, characteristic, status);
        mHandler.post(postOnCharacteristicReadRunnable);
    }

    @SuppressLint("MissingPermission")
    private void postOnDeviceConnected(boolean connected) {
        Log.d(LOG_TAG, "postOnDeviceConnected()");

        Bluetooth bluetooth = new Bluetooth(mContext);

        if (connected) {
            // Copy the Vendor, Product and Uart values found
            bluetooth.setService(mLibDiveComputerFound.getService());
            bluetooth.setVendorLoad(mLibDiveComputerFound.getVendor());
            bluetooth.setProductLoad(mProduct);
            bluetooth.setCharacteristicRx(mLibDiveComputerFound.getCharacteristicRx());
            bluetooth.setCharacteristicRxCredits(mLibDiveComputerFound.getCharacteristicRxCredits());
            bluetooth.setCharacteristicTx(mLibDiveComputerFound.getCharacteristicTx());
            bluetooth.setCharacteristicTxCredits(mLibDiveComputerFound.getCharacteristicTxCredits());
            // Initialize the rest from the connection
            bluetooth.setDevice(mDevice);
            bluetooth.setTransport(mDevice.getType());
            bluetooth.setStatus(mContext.getResources().getString(R.string.button_connected));
            bluetooth.setRssi(mRssi);
            bluetooth.setMacAddress(mDevice.getAddress());
            bluetooth.setDeviceName(mDevice.getName());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bluetooth.setAlias(mDevice.getAlias());
            } else {
                bluetooth.setAlias("");
            }
            bluetooth.setConnectionType(mContext.getResources().getString(R.string.code_bluetooth));
            bluetooth.setBondState(mDevice.getBondState());
            // TODO: Ask Shearwater
            bluetooth.setFw("Fw");
            // TODO: Ask Shearwater
            bluetooth.setFwId("FwId");
            // TODO: Ask Shearwater
            bluetooth.setSerialNumber("SerialNumber");
            // TODO: Ask Shearwater
            bluetooth.setUnit("Imperial");
            // TODO: Ask Shearwater
            bluetooth.setLanguage("English");
            // TODO:
//        peripheral.readCharacteristic(DIS_SERVICE_UUID, MANUFACTURER_NAME_CHARACTERISTIC_UUID);
//        peripheral.readCharacteristic(DIS_SERVICE_UUID, MODEL_NUMBER_CHARACTERISTIC_UUID);
        }

        // If connection failed, the code in mBluetoothCallback.onDeviceConnected() checks the connected boolean
        // We are safe!
        Runnable postOnDeviceConnectedRunnable = () -> mBluetoothCallback.onDeviceConnected(connected, bluetooth);
        mHandler.post(postOnDeviceConnectedRunnable);
    }

    private void postNotifyDataSetChanged() {
        Log.d(LOG_TAG, "postNotifyDataSetChanged()");

        Runnable postNotifyDataSetChangedRunnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothCallback.notifyDataSetChanged();
            }
        };
        mHandler.post(postNotifyDataSetChangedRunnable);

    }

    private void postNotifyItemInserted(int pos) {
        Log.d(LOG_TAG, "postNotifyItemInserted()");

        Runnable postNotifyItemInsertedRunnable = new Runnable() {
            @Override
            public void run() {
                mBluetoothCallback.notifyItemInserted(pos);
            }
        };
        mHandler.post(postNotifyItemInsertedRunnable);

    }

    // NOTE: Reserved for future use
    @SuppressLint("MissingPermission")
    public boolean removeBond(@NotNull final String peripheralAddress) {
        Log.d(LOG_TAG, "removeBond()");

        Objects.requireNonNull(peripheralAddress, NO_PERIPHERAL_ADDRESS_PROVIDED);

        // Get the set of bonded devices
         final Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();

        // See if the device is bonded
        BluetoothDevice peripheralToUnBond = null;
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                if (device.getAddress().equals(peripheralAddress)) {
                    peripheralToUnBond = device;
                }
            }
        } else {
            return true;
        }

        // Try to remove the bond
        if (peripheralToUnBond != null) {
            try {
                Method method = peripheralToUnBond.getClass().getMethod("removeBond", (Class[]) null);
                boolean result = (boolean) method.invoke(peripheralToUnBond, (Object[]) null);
                if (result) {
                    Log.i(LOG_TAG, String.format("removeBond - Successfully removed bond for %s", peripheralToUnBond.getName()));
                    postMessage(0, "", String.format("removeBond - Successfully removed bond for %s", peripheralToUnBond.getName()));
                }
                return result;
            } catch (Exception e) {
                Log.i(LOG_TAG,"removeBond - could not remove bond");
                postMessage(0, "", "removeBond - could not remove bond");
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    // Will trigger onScanResult()
    // This function is used to find a single device
    // and test its connection
    @SuppressLint("MissingPermission")
    private void scan(String uuidFilter, String nameFilter, String macAddressFilter) {
        Log.d(LOG_TAG, "scan(uuidFilter, nameFilter, macAddressFilter)");

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (!mIsDiscovering) {
            // Not yet scanning
            // Start the scan
            sendMessage(0, "", String.format(mContext.getResources().getString(R.string.msg_bluetooth_scanning_started)));

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            int scanningTimeout = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SCANNING_TIMEOUT, "10000"))));

            // Stops SCANNING after a pre-defined scan period
            Runnable setTimeOut = new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            };
            mHandler.postDelayed(setTimeOut, scanningTimeout);

            // Set the ScanSettings
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                    .setReportDelay(0L)
                    .build();

            if (uuidFilter.isEmpty() && nameFilter.isEmpty() && macAddressFilter.isEmpty()) {
                // No filters have been provided
                mBluetoothLeScanner.startScan(null, scanSettings, mScanCallback);
            } else if (!uuidFilter.isEmpty()) {
                // Build the filter
                UUID FILTER_UUID = UUID.fromString(uuidFilter);
                UUID[] serviceUUIDs = new UUID[]{FILTER_UUID};
                // Build filters list
                List<ScanFilter> filters;
                filters = new ArrayList<>();
                for (UUID serviceUUID : serviceUUIDs) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setServiceUuid(new ParcelUuid(serviceUUID))
                            .build();
                    filters.add(filter);
                }
                mBluetoothLeScanner.startScan(filters, scanSettings, mScanCallback);
            } else if (!nameFilter.isEmpty()) {
                String[] names = new String[]{nameFilter};
                // Build filters list
                List<ScanFilter> filters;
                filters = new ArrayList<>();
                for (String name : names) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setDeviceName(name)
                            .build();
                    filters.add(filter);
                }
                mBluetoothLeScanner.startScan(filters, scanSettings, mScanCallback);
            } else {
                // macAddressFilter has been provided
                String[] peripheralAddresses = new String[]{macAddressFilter};
                // Build filters list
                List<ScanFilter> filters;
                filters = new ArrayList<>();
                for (String address : peripheralAddresses) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setDeviceAddress(address)
                            .build();
                    filters.add(filter);
                }
                mBluetoothLeScanner.startScan(filters, scanSettings, mScanCallback);
            }
            postMessage(0, "", mContext.getString(R.string.msg_bluetooth_scanning_started));
            mIsDiscovering = true;
        }
    }

    // Will trigger onScanResult()
    // This function is used to scan and return all of the found devices
    @SuppressLint("MissingPermission")
    public void scan(ArrayList<Bluetooth> bluetoothList, String uuidFilter, String nameFilter, String macAddressFilter) {
        Log.d(LOG_TAG, "scan(bluetoothList, uuidFilter, nameFilter, macAddressFilter)");

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        if (!mIsDiscovering) {
            // Not yet scanning
            // Start the scan
            sendMessage(0, "", String.format(mContext.getResources().getString(R.string.msg_bluetooth_scanning_started)));

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            int scanningTimeout = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.SCANNING_TIMEOUT, "10000"))));

            // Stops SCANNING after a pre-defined scan period
            Runnable setTimeOut = new Runnable() {
                @Override
                public void run() {
                    stopScan();
                }
            };
            mHandler.postDelayed(setTimeOut, scanningTimeout);

            // bluetoothList is mBluetoothList on the calling activity
            // bluetoothList is passed empty
            // mBluetoothList is populated by onScanResult
            mBluetoothList = bluetoothList;

            // Set the ScanSettings
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                    .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                    .setReportDelay(0L)
                    .build();

            if (uuidFilter.isEmpty() && nameFilter.isEmpty() && macAddressFilter.isEmpty()) {
                // No filters have been provided
                mBluetoothLeScanner.startScan(null, scanSettings, mScanCallback);
            } else if (!uuidFilter.isEmpty()) {
                // Build the filter
                UUID FILTER_UUID = UUID.fromString(uuidFilter);
                UUID[] serviceUUIDs = new UUID[]{FILTER_UUID};
                // Build filters list
                List<ScanFilter> filters;
                filters = new ArrayList<>();
                for (UUID serviceUUID : serviceUUIDs) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setServiceUuid(new ParcelUuid(serviceUUID))
                            .build();
                    filters.add(filter);
                }
                mBluetoothLeScanner.startScan(filters, scanSettings, mScanCallback);
            } else if (!nameFilter.isEmpty()) {
                String[] names = new String[]{nameFilter};
                // Build filters list
                List<ScanFilter> filters;
                filters = new ArrayList<>();
                for (String name : names) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setDeviceName(name)
                            .build();
                    filters.add(filter);
                }
                mBluetoothLeScanner.startScan(filters, scanSettings, mScanCallback);
            } else {
                // macAddressFilter has been provided
                String[] peripheralAddresses = new String[]{macAddressFilter};
                // Build filters list
                List<ScanFilter> filters;
                filters = new ArrayList<>();
                for (String address : peripheralAddresses) {
                    ScanFilter filter = new ScanFilter.Builder()
                            .setDeviceAddress(address)
                            .build();
                    filters.add(filter);
                }
                mBluetoothLeScanner.startScan(filters, scanSettings, mScanCallback);
            }
            postMessage(0, "", mContext.getString(R.string.msg_bluetooth_scanning_started));
            mIsDiscovering = true;
        }
    }

    // Use this function to send a message on the main thread
    // iconId 0 : Logging. No display necessary. No translation. No String Log.i Same as Log.d
    //        1 : Alert                                                    Log.w
    //        2 : Stop                                                     Log.e
    private void sendMessage(int iconId, String title, String message) {
        Log.d(LOG_TAG, "sendMessage()");

        // Post the message on the Main Thread without delay
        runOnUiThread(() -> mBluetoothCallback.bubbleUpMessage(iconId, title, message));
    }

    // NOTE: Reserve for future use
    private boolean setPinCodeForDevice(@NotNull final String deviceAddress, @NotNull final String pin) {
        Log.d(LOG_TAG, "setPinCodeForDevice()");

        Objects.requireNonNull(deviceAddress, NO_PERIPHERAL_ADDRESS_PROVIDED);
        Objects.requireNonNull(pin, "no pin provided");

        if (!BluetoothAdapter.checkBluetoothAddress(deviceAddress)) {
            Log.e(LOG_TAG, String.format("setPinCodeForDevice - Invalid Address %s for Pin Code. Make sure all alphabetic characters are uppercase.", deviceAddress));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_pincode_invalid_address), deviceAddress));
            return false;
        }

        if (pin.length() != 6) {
            Log.e(LOG_TAG, String.format("setPinCodeForDevice - Invalid length for Pin Code %s, is not 6 digits long", pin));
            postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_pincode_invalid_length), pin));
            return false;
        }

        pinCodes.put(deviceAddress, pin);
        return true;
    }

    public void setBluetooth(Bluetooth bluetooth) {
        Log.d(LOG_TAG, "setBluetooth()");

        mBluetooth = bluetooth;
    }

    private void showBusyDialog(boolean show) {
        Log.d(LOG_TAG, "showBusyDialog()");

        if (show) {
            // Show the busy dialog
            Runnable postShowBusyDialog = () -> mBluetoothCallback.showBusyDialog(true);
            mHandler.post(postShowBusyDialog);
        } else {
            // Hide the busy dialog
            Runnable postHideBusyDialog = () -> mBluetoothCallback.showBusyDialog(false);
            mHandler.post(postHideBusyDialog);
        }
    }

    @SuppressLint("MissingPermission")
    private void startConnectionTimer(@NotNull final BluetoothDevice device) {
        Log.d(LOG_TAG, "startConnectionTimer()");

        cancelConnectionTimer();
        mIsConnectTimeoutOn = true;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        int connectingTimeout = Integer.parseInt(MyFunctions.replaceEmptyByZero(Objects.requireNonNull(preferences.getString(MyConstants.CONNECTING_TIMEOUT, "10000"))));

        // Stops scanning after a pre-defined scan period
        mTimeoutRunnable = () -> {
            Log.i(LOG_TAG, String.format("startConnectionTimer - Connection timeout, disconnecting %s", device.getName()));
            postMessage(0, "", String.format("startConnectionTimer - Connection timeout, disconnecting %s", device.getName()));
            disconnect();

            mHandler.postDelayed(() -> {
                // If mGatt is null after the time out it means the connect failed
                if (mGatt != null) {
                    mGattCallback.onConnectionStateChange(mGatt, BluetoothHciStatus.CONNECTION_FAILED_ESTABLISHMENT.value, BluetoothProfile.STATE_DISCONNECTED);
                }
            }, 50);

            mTimeoutRunnable = null;
        };

        mHandler.postDelayed(mTimeoutRunnable, connectingTimeout);
    }

    // NOTE: Reserved for future use
    @SuppressLint("MissingPermission")
    private void startPairingPopupHack() {
        Log.d(LOG_TAG, "startPairingPopupHack()");

        String manufacturer = Build.MANUFACTURER;
        if(!manufacturer.equals("samsung")) {
            mBluetoothAdapter.startDiscovery();

            mHandler.postDelayed(() -> {
                Log.i(LOG_TAG, "startPairingPopupHack - popup hack completed");
                postMessage(0, "", "startPairingPopupHack - popup hack completed");
                mBluetoothAdapter.cancelDiscovery();
            }, 1000);
        }
    }

    @SuppressLint("MissingPermission")
    public void stopScan() {
        Log.d(LOG_TAG, "stopScan()");

        if (mBluetoothLeScanner != null) {
            // Will trigger an onScanResult()
            mBluetoothLeScanner.stopScan(mScanCallback);
            postMessage(0, "", mContext.getString(R.string.msg_bluetooth_scanning_stopped));
            mIsDiscovering = false;
        }
    }

    // Called from: onConnectionStateChange with BluetoothProfile.STATE_DISCONNECTED
    //              onConnectionStateChange with status = 98 - ENABLE_NOTIFICATIONS_FAILED - (Dive Computer supported by libdivecomputer but enableNotifications() failed)
    //              onConnectionStateChange with status = 99 - DIVE_COMPUTER_NOT_SUPPORTED - (Dive Computer not supported by libdivecomputer)
    //              onConnectionStateChange with other status. e.g. 133
    @SuppressLint("MissingPermission")
    private void successfullyDisconnected(final int previousState) {
        Log.d(LOG_TAG, "successfullyDisconnected()");

        if (previousState == BluetoothProfile.STATE_CONNECTED || previousState == BluetoothProfile.STATE_DISCONNECTING) {
            Log.i(LOG_TAG, String.format("successfullyDisconnected - Disconnected device %s on request", mDevice.getName()));
            postMessage(0, "", String.format("successfullyDisconnected - Disconnected device %s on request", mDevice.getName()));
        } else if (previousState == BluetoothProfile.STATE_CONNECTING) {
            Log.i(LOG_TAG, "successfullyDisconnected - Cancelling connect attempt");
            postMessage(0, "", "successfullyDisconnected - Cancelling connect attempt");
        }

        if (mIsBondLost) {
            Log.i(LOG_TAG, "successfullyDisconnected - Disconnected because of bond lost");
            postMessage(0, "", "successfullyDisconnected - Disconnected because of bond lost");

            // Give the stack some time to register the bond loss internally. This is needed on most phones...
            mHandler.postDelayed(() -> {
                // NOTE: Leave as is
                // Service discovery was not completed yet so consider it a connectionFailure
                // Bond was lost after a successful connection was established
                disconnectCleanup(false, BluetoothHciStatus.AUTHENTICATION_FAILURE);
            }, DELAY_AFTER_BOND_LOST);
        } else {
            disconnectCleanup(false, BluetoothHciStatus.SUCCESS);
        }

//        // Tell the caller that we are disconnected
//        postOnDeviceConnected(false);
    }

    private boolean willCauseLongWrite(@NotNull final byte[] value, @NotNull final BluetoothWriteType bluetoothWriteType) {
        Log.d(LOG_TAG, "willCauseLongWrite()");

        return value.length > mCurrentMtu - 3 && bluetoothWriteType == BluetoothWriteType.WITH_RESPONSE;
    }

    public boolean writeCharacteristic(String service, String writeCharacteristic, String value) {
        Log.d(LOG_TAG, "writeCharacteristic(String service, String writeCharacteristic, String value)");

        UUID uuidService = UUID.fromString(service);
        UUID UuidWriteCharacteristic = UUID.fromString(writeCharacteristic);
        byte[] newValue = value.getBytes(StandardCharsets.UTF_8);
        return writeCharacteristic(uuidService, UuidWriteCharacteristic, newValue);
    }

    @SuppressLint("MissingPermission")
    private boolean writeCharacteristic(UUID service, UUID writeCharacteristic, byte[] value) {
        Log.d(LOG_TAG, "writeCharacteristic(UUID service, UUID writeCharacteristic, byte[] value)");

        BluetoothGattCharacteristic writeBluetoothCharacteristic = mGatt.getService(service).getCharacteristic(writeCharacteristic);

        BluetoothWriteType bluetoothWriteType;
        if (isCharacteristicWritable(writeBluetoothCharacteristic)) {
            // Use WRITE_TYPE_DEFAULT over WRITE_TYPE_NO_RESPONSE if the characteristic supports both write types
            bluetoothWriteType = BluetoothWriteType.WITH_RESPONSE;
        } else if (isCharacteristicWritableWithoutResponse(writeBluetoothCharacteristic)) {
            bluetoothWriteType = BluetoothWriteType.WITHOUT_RESPONSE;
        } else {
            Log.i(LOG_TAG, String.format("Write Characteristic - Service %s and Characteristic %s cannot be written to", service, writeCharacteristic));
            postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_write_failed), service, writeCharacteristic));
            return false;
        }

        // Copy the value to avoid race conditions
        final byte[] bytesToWrite = MyFunctions.copyOf(value);

        Log.d(LOG_TAG, "writeCharacteristic-enqueuingCommand-willCauseLongWrite-internalWriteCharacteristic");
        return enqueueCommand(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    if (willCauseLongWrite(bytesToWrite, bluetoothWriteType)) {
                        // Android will turn this into a Long Write because it is larger than the MTU - 3.
                        // When doing a Long Write the byte array will be automatically split in chunks of size MTU - 3.
                        // However, the peripheral's firmware must also support it, so it is not guaranteed to work.
                        // Long writes are also very inefficient because of the confirmation of each write operation.
                        // So it is better to increase MTU if possible. Hence a warning if this write becomes a long write...
                        // See https://stackoverflow.com/questions/48216517/rxandroidble-write-only-sends-the-first-20b
                        Log.e(LOG_TAG, "writeCharacteristic - Value byte array is longer than allowed by MTU, write will fail if peripheral does not support long writes");
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_characteristic_failed_value_too_long)));
                    }

                    if (internalWriteCharacteristic(writeBluetoothCharacteristic, bytesToWrite, bluetoothWriteType)) {
                        // Request is successful
                        // Command will be completed in gattCallback.onCharacteristicWrite
                        Log.i(LOG_TAG, String.format(Locale.ENGLISH,"writeCharacteristic - Writing to characteristic %s with value %s",  writeCharacteristic.toString(), BluetoothBytesParser.asHexString(bytesToWrite)));
                        postMessage(0, "", String.format(Locale.ENGLISH,"Writing to characteristic %s with value  %s", writeCharacteristic, BluetoothBytesParser.asHexString(bytesToWrite)));
                    } else {
                        Log.e(LOG_TAG, String.format(Locale.ENGLISH,"writeCharacteristic - writeCharacteristic failed for characteristic %s", writeCharacteristic.toString()));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_characteristic_failed), writeCharacteristic));
                        // Remove the failed command
                        // Move to the next command
                        completedCommand();
                    }
                } else {
                    Log.e(LOG_TAG, String.format("writeCharacteristic - writeCharacteristic failed for characteristic %s. Not connected to Device %s", writeCharacteristic.toString(), mDevice.getName()));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_characteristic_failed_not_connected), writeCharacteristic.toString(), mDevice.getName()));
                    // Command cannot be executed because not connected
                    // Command still in the queue
                    // Remove the command from the queue
                    // Move to the next command
                    completedCommand();
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    private boolean writeDescriptor(BluetoothGattDescriptor descriptor, byte[] value, boolean enable ) {
        Log.d(LOG_TAG, "writeDescriptor()");

        if (value.length == 0) {
            throw new IllegalArgumentException(VALUE_BYTE_ARRAY_IS_EMPTY);
        }

        if (value.length > getMaximumWriteValueLength(BluetoothWriteType.WITH_RESPONSE)) {
            throw new IllegalArgumentException(VALUE_BYTE_ARRAY_IS_TOO_LONG);
        }

        // Queue Runnable to turn on/off the notification now that all checks have been passed
        Log.d(LOG_TAG, "writeDescriptor-enqueuingCommand-setCharacteristicNotification");
        return enqueueCommand(new Runnable() {
            @Override
            public void run() {
                if (isConnected()) {
                    // First set notification for Gatt object
                    // enable must be true for the notifications to work
                    // There is no setCharacteristicIndication method
                    if(!mGatt.setCharacteristicNotification(descriptor.getCharacteristic(), enable)) {
                        Log.e(LOG_TAG, String.format("writeDescriptor - setCharacteristicNotification request failed for descriptor %s", descriptor.getUuid()));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_descriptor_set_notification_request_failed_), descriptor.getUuid()));
                    }

                    // Then write to descriptor
                    descriptor.setValue(value);

                    // Will trigger onDescriptorWrite
                    boolean resultWrite = mGatt.writeDescriptor(descriptor);
                    if(!resultWrite) {
                        // Remove the failed command
                        // Move to the next command
                        completedCommand();
                        Log.e(LOG_TAG, String.format("writeDescriptor - Write descriptor request failed with value %s for Device %s and Characteristic: %s", Arrays.toString(value), mDevice, descriptor.getUuid()));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_descriptor_request_failed), Arrays.toString(value), mDevice, descriptor.getUuid()));
                    }
                    // Requests are successful
                    // Command will be completed in gattCallback.onDescriptorWrite
                } else {
                    Log.e(LOG_TAG, String.format("writeDescriptor - writeDescriptor failed. Not connected to device %s", mDevice));
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_descriptor_failed_not_connected), mDevice));
                    // Command cannot be executed because not connected
                    // Command still in the queue
                    // Remove the command from the queue
                    // Move to the next command
                    completedCommand();
                }
            }
        });
    }

    // libdivecomputer functions for callbacks

    // Cleanup. e.g. Reset on DC to terminate the Bluetooth connection on the DC
    private long close() {
        Log.d(LOG_TAG, "close()");

        // TODO:
        return 1;
    }

    // NOTE: Required only for serial communication and not needed for BLE
    private long configure(int baudRate, int dataBits, int parity, int stopBits, int flowControl) {
        Log.d(LOG_TAG, "configure()");

        return 1;
    }

    // NOTE: Not needed for BLE because we shouldn't buffer packets (when sending).
    private long flush() {
        Log.d(LOG_TAG, "flush()");

        return 1;
    }

    // NOTE: Required only for serial communication and not needed for BLE
    private long getAvailable(long value) {
        Log.d(LOG_TAG, "getAvailable()");

        return 1;
    }

    // NOTE: Required only for serial communication and not needed for BLE
    private long getLines(long value) {
        Log.d(LOG_TAG, "getLines()");

        return 1;
    }

    // Perform some advanced operation not covered by any of the other function
    private long iotcl(int request, byte[] data, int size) {
        Log.d(LOG_TAG, "iotcl()");

        // TODO:
        return 1;
    }

    // Check for a packet without retrieving it
    private long poll(int timeout) {
        Log.d(LOG_TAG, "poll()");

        // TODO:
        return 1;
    }

    // Discard any buffered packets (input and/or output)
    // Mainly used when recovering from errors.
    private long purge(int direction) {
        Log.d(LOG_TAG, "purge()");

        // TODO:
        return 1;
    }

    // Read the next packet from the queue upon request by libdivecomputer
    private byte[] read() {
        Log.d(LOG_TAG, "read()");

        // TODO: Convert data type??
        // libdivecomputer is the one pulling the packets
        // Pull one packet at a time from the mPacketQueue
        
        byte[] packet = new byte[0];
        try {
            packet = mPacketQueue.poll(mLibDiveComputerTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (packet == null) {
            // Return DC_STATUS_TIMEOUT
            // TODO: Fix return code all
//            return -2;
        }

        // Return the data to myairbuddyandi.cpp
        return packet;
    }

    // NOTE: Required only for serial communication and not needed for BLE
    private long setBreak(long value) {
        Log.d(LOG_TAG, "setBreak()");

        return 1;
    }

    // NOTE: Required only for serial communication and not needed for BLE
    private long setDtr(long value) {
        Log.d(LOG_TAG, "setDtr()");

        return 1;
    }

    // NOTE: Required only for serial communication and not needed for BLE
    private long setRts(long value) {
        Log.d(LOG_TAG, "setRts()");

        return 1;
    }

    // Set the timeout to avoid runaway read or write
    public long setTimeout(int timeout) {
        Log.d(LOG_TAG, "setTimeout()");

        mLibDiveComputerTimeout = timeout;
        return 1;
    }

    // Wait and do nothing for the specified amount
    // Used when the dive computer can't handle sending commands too fast
    private long sleep(long milliseconds) {
        Log.d(LOG_TAG, "sleep()");

        // TODO:
        return 1;
    }

    // libdivecomputer sends command under the form of a payload to be written to the DC
    private long write(byte[] data, int size, long actual) {
        Log.d(LOG_TAG, "write()");

        if (mBluetooth != null) {

            UUID uUidService = UUID.fromString(mBluetooth.getService());
            UUID UuidCharacteristic = UUID.fromString(mBluetooth.getCharacteristicRx());

            // TODO: Do we need size and actual??

            if (!writeCharacteristic(uUidService, UuidCharacteristic, data)) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

    // ***** Create a BluetoothGattCallback *****
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(LOG_TAG, "onConnectionStateChange()");

            if (newState != BluetoothProfile.STATE_CONNECTING) {
                cancelConnectionTimer();
            }

            final int previousState = mState;
            mState = newState;

            showBusyDialog(false);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        cancelConnectionTimer();
                        int bondState = gatt.getDevice().getBondState();
                        Log.i(LOG_TAG, String.format("onConnectionStateChange - Successfully connected to Device %s at Address %s with Bond State %s", gatt.getDevice(), gatt.getDevice().getAddress(), bondState));
                        postMessage(0, "", String.format("Successfully connected to Device %s at Address %s with Bond State %s", gatt.getDevice(), gatt.getDevice().getAddress(), bondState));
                        // Take action depending on the bond state
                        if (bondState == BOND_NONE || bondState == BOND_BONDED) {
                            mGatt = gatt;
                            // Try to read the RSSI (Received Signal Strength Indicator) right off the bat!
                            readRemoteRssi();
                            // No need to wait because Build.VERSION.SDK_INT is never < 26
                            // Running on the Main Thread without delay
                            // Needs extends AppCompatActivity
                            // Bonding process has completed
                            // Connected to device, now proceed to discover it's services
                            // Check if there are services
                            if (mGatt.getServices().isEmpty()) {
                                // No services discovered yet
                                runOnUiThread(() -> {
                                    Log.i(LOG_TAG, String.format("onConnectionStateChange - Discovering services for %s", gatt.getDevice()));
                                    postMessage(0, "", String.format("Discovering services for %s", gatt.getDevice()));
                                    // Will trigger onServicesDiscovered()
                                    // Once you are connected to a device you must discover it’s services by calling discoverServices()
                                    boolean result = gatt.discoverServices();
                                    if (!result) {
                                        Log.e(LOG_TAG, String.format("onConnectionStateChange - DiscoverServices failed to start for Device %s", gatt.getDevice()));
                                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_discover_services_failed), gatt.getDevice()));
                                    }
                                });
                            }

                            if (bondState == BOND_BONDED) {
                                // If bonding was triggered by a read/write, we must retry it
                                Log.i(LOG_TAG, "onConnectionStateChange - Bonded completed");
                                postMessage(0, "", "Bonded completed");
                            }
                        } else if (bondState == BOND_BONDING) {
                            // Bonding process in progress, let it complete
                            Log.i(LOG_TAG, "onConnectionStateChange - Waiting for bonding to complete");
                            postMessage(0, "", "Waiting for bonding to complete");
                        }
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        // Called by mGatt.disconnect();
                        Log.i(LOG_TAG, String.format("onConnectionStateChange - Successfully disconnected from %s", gatt.getDevice().getAddress()));
                        postMessage(0, "", String.format("Successfully disconnected from %s", gatt.getDevice().getAddress()));
                        successfullyDisconnected(previousState);
                        break;
                    case BluetoothProfile.STATE_CONNECTING:
                        Log.i(LOG_TAG, String.format("onConnectionStateChange - Connecting to %s", gatt.getDevice().getAddress()));
                        postMessage(0, "", String.format("Connecting to %s", gatt.getDevice().getAddress()));
                        // TODO: Test nothing to do
                        break;
                    case BluetoothProfile.STATE_DISCONNECTING:
                        Log.i(LOG_TAG, String.format("onConnectionStateChange - Disconnecting from %s", gatt.getDevice().getAddress()));
                        postMessage(0, "", String.format("Disconnecting from %s", gatt.getDevice().getAddress()));
                        // TODO: Test nothing to do
                        break;
                    default:
                        // Should trigger the BluetoothProfile.STATE_DISCONNECTED and do the gatt.close()
                        disconnect();
                        cancelConnectionTimer();
                        Log.e(LOG_TAG, String.format("onConnectionStateChange - Connection failed with status %s for Device %s at address %s. Disconnecting... ", status, gatt.getDevice(), gatt.getDevice().getAddress()));
                        postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_connection_failed), String.valueOf(status), gatt.getDevice(), gatt.getDevice().getAddress()));
                }
            } else if (status == BluetoothGatt.HID_DEVICE) {
                // Status 19
                // User cancelled the Bluetooth connection on the device
                // Ot the device timed out and the device disconnected itself
                // TODO: fix log and add a message
                Log.i(LOG_TAG, String.format("onConnectionStateChange - ?? failed for Device %s and CharacteristicTx %s. Disconnecting...", gatt.getDevice().getName(), mBluetooth.getCharacteristicTx()));
            } else if (status == ENABLE_NOTIFICATIONS_FAILED) {
                // Dive Computer supported by libdivecomputer but enableNotifications() failed
                Log.i(LOG_TAG, String.format("onConnectionStateChange - EnableNotifications() failed for Device %s and CharacteristicTx %s. Disconnecting...", gatt.getDevice().getName(), mBluetooth.getCharacteristicTx()));
                postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_notifications_failed), gatt.getDevice().getName(), mBluetooth.getCharacteristicTx()));
                disconnect();
                successfullyDisconnected(previousState);
                diveComputerNotSupported();
            } else if (status == DIVE_COMPUTER_NOT_SUPPORTED) {
                // Dive Computer not supported by libdivecomputer
                Log.i(LOG_TAG, String.format("Dive computer %1$s not supported by libdivecomputer.\n\nSee Option Menus \u22EE Troubleshooting. Disconnecting...", gatt.getDevice().getName()));
                postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_not_a_supported_computer), gatt.getDevice().getName()));
                disconnect();
                successfullyDisconnected(previousState);
                diveComputerNotSupported();
            } else {
                // NOTE: BluetoothGatt.GATT_INTERNAL_ERROR does not exist
                // NOTE: Might be the infamous 133
                final BluetoothHciStatus bluetoothHciStatus = BluetoothHciStatus.fromValue(status);
                // Most likely will do nothng since not yet connected
                disconnect();
                successfullyDisconnected(previousState);
                connectFailed(gatt.getDevice(), bluetoothHciStatus,mIsConnectTimeoutOn);
            }
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(LOG_TAG, String.format("onServicesDiscovered() - %s services discovered for Device %s at Address %s", gatt.getServices().size(), gatt.getDevice(), gatt.getDevice().getAddress()));
            postMessage(0, "", String.format("%s services discovered for Device %s at Address %s", gatt.getServices().size(), gatt.getDevice(), gatt.getDevice().getAddress()));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(LOG_TAG, String.format("onServicesDiscovered - Connected to device %s", gatt.getDevice()));
                postMessage(0, "", String.format("onServicesDiscovered - Connected to device %s", gatt.getDevice()));
                printGattTable(gatt);
                // Request a higher MTU, iOS always asks for 185
                // But I'm trying for the maximum
                // Will trigger a gattCallback.onMTUChanged()
                requestMtu(GATT_MAX_MTU_SIZE);
                // TODO: But after the callback from requestMtu()
//                // Request a new connection priority
//                peripheral.requestConnectionPriority(ConnectionPriority.HIGH);
//
//                peripheral.setPreferredPhy(PhyType.LE_2M, PhyType.LE_2M, PhyOptions.S2);
//
//                // Read manufacturer and model number from the Device Information Service
//                peripheral.readCharacteristic(DIS_SERVICE_UUID, MANUFACTURER_NAME_CHARACTERISTIC_UUID);
//                peripheral.readCharacteristic(DIS_SERVICE_UUID, MODEL_NUMBER_CHARACTERISTIC_UUID);
            } else {
                disconnect();
                connectFailed(gatt.getDevice(), BluetoothHciStatus.fromValue(status), false);
            }
            // NOTE: Consider connection setup as complete here
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            Log.d(LOG_TAG, "onCharacteristicChanged()");

            // NOTE: Most likely not from writeDescriptor

            // Copy the byte array so we have a threadsafe copy
            final byte[] safeValue = new byte[characteristic.getValue().length];
            System.arraycopy(characteristic.getValue(), 0, safeValue, 0, characteristic.getValue().length );
            // Characteristic has new value so pass it on for processing
            mHandler.post(() -> mBluetoothCallback.onCharacteristicUpdate(gatt.getDevice(), safeValue, characteristic));
            // If the characteristic cannot be read, enqueue the packet here
            // If the characteristic can be read, enqueue the packet in onCharacteristicRead
            // They cannot be read from both places??
            // TODO: To be tested??
            enqueuePacket(safeValue);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic readCharacteristic, int status) {
            Log.d(LOG_TAG, "onCharacteristicRead()");

            // I believe it will fire only if the Characteristic is readable

            // Perform some checks on the status field
            if (status == BluetoothGatt.GATT_SUCCESS) {

                final byte[] safeValue = MyFunctions.nonnullOf(readCharacteristic.getValue());

                // Not a queued command
                // To Send the result to MyCallback
                // To send the packet to libdivecomputer
                Runnable onCharacteristicUpdate = () -> {
                    Log.i(LOG_TAG, String.format("onCharacteristicRead - Read Characteristic %s with value of %s", readCharacteristic.getUuid().toString(), MyFunctions.byteArrayToHex(safeValue)));
                    postMessage(0, "", String.format("Read Characteristic %s with value of %s", readCharacteristic.getUuid().toString(), MyFunctions.byteArrayToHex(safeValue)));
                    // Post to the calling activity
                    // The calling activity might not even be using this
                    // Maybe used only for testing and debugging
                    postOnCharacteristicRead(mGatt.getDevice(), safeValue, readCharacteristic, status);
                    // If the characteristic can be read, enqueue the packet here
                    // If not, enqueue the packet in onCharacteristicChanged
                    // They cannot be read from both places??
                    // TODO: To be tested??
                    enqueuePacket(safeValue);
//                    completedPacket();
                };
                mHandler.post(onCharacteristicUpdate);
            } else if (status == BluetoothGatt.GATT_READ_NOT_PERMITTED) {
                Log.e(LOG_TAG, String.format("onCharacteristicRead - readCharacteristic failed for Service %s and Characteristic %s with Status %s", mBluetooth.getService(), readCharacteristic.getUuid().toString(), status));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_read_failed), mBluetooth.getService(), readCharacteristic.getUuid().toString(), status));
            } else if (status == BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION ) {
                // Characteristic encrypted and needs bonding,
                // So retry operation after bonding completes
                // This only happens on Android 5/6/7
                Log.w(LOG_TAG, String.format("onCharacteristicRead - readCharacteristic %s needs bonding, bonding in progress", readCharacteristic.getUuid().toString()));
                postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_message), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_read_bonding), readCharacteristic.getUuid().toString()));
                return;
            } else {
                Log.e(LOG_TAG, String.format("onCharacteristicRead - readCharacteristic failed for Service %s and Characteristic %s with Status %s", mBluetooth.getService(), readCharacteristic.getUuid().toString(), status));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_characteristic_read_failed), mBluetooth.getService(), readCharacteristic.getUuid().toString(), status));
                return;
            }

            // Command has completed successfully
            // Remove the command
            // Move to the next command
            completedCommand();
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic writeCharacteristic, int status) {
            super.onCharacteristicWrite(gatt, writeCharacteristic, status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                Log.i(LOG_TAG, String.format("onCharacteristicWrite() - Wrote to Characteristic %s with value %s", writeCharacteristic.getUuid(), BluetoothBytesParser.asHexString(currentWriteBytes)));
                postMessage(0, "", String.format("onCharacteristicWrite - Wrote to Characteristic %s with value %s", writeCharacteristic.getUuid(), BluetoothBytesParser.asHexString(currentWriteBytes)));
            } else if (status == BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH) {
                Log.e(LOG_TAG, "onCharacteristicWrite() - Write invalid MTU length");
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_invalid_mtu_length)));
            } else if (status == BluetoothGatt.GATT_WRITE_NOT_PERMITTED) {
                Log.e(LOG_TAG, String.format("onCharacteristicWrite() - Write not permitted for Characteristic %s", writeCharacteristic.getUuid()));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_not_permitted),writeCharacteristic.getUuid()));
            } else {
                Log.e(LOG_TAG, String.format("onCharacteristicWrite() - Characteristic write failed for Characteristic %s with status %s", writeCharacteristic.getUuid(), status));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_failed),writeCharacteristic.getUuid(), status));
            }

            // Command has completed successfully
            // Remove the command
            // Move to the next command
            completedCommand();
        }

        @Override
        public void onReadRemoteRssi(@NotNull final BluetoothGatt gatt, final int rssi, final int status) {
            Log.d(LOG_TAG, "onReadRemoteRssi()");

            if (status != BluetoothGatt.GATT_SUCCESS) {
                Log.e(LOG_TAG, String.format("onReadRemoteRssi - Reading RSSI failed with status %d", status));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_rssi_failed), status));
            }

//            callbackHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    peripheralCallback.onReadRemoteRssi(BluetoothPeripheral.this, rssi, gattStatus);
//                }
//            });

            completedCommand();

            mRssi = rssi;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            final BluetoothGattStatus bluetoothGattStatus = BluetoothGattStatus.fromValue(status);
            Log.i(LOG_TAG, String.format("onMtuChanged() - ATT MTU changed to %d with status %s", mtu, bluetoothGattStatus.name()));
            postMessage(0, "", String.format(Locale.ENGLISH, "onMtuChanged - ATT MTU changed to %d with status %s", mtu, bluetoothGattStatus.name()));

            if (bluetoothGattStatus != BluetoothGattStatus.SUCCESS) {
                Log.i(LOG_TAG, String.format("onMtuChanged - onChange MTU failed with status '%s'", bluetoothGattStatus.name()));
                postMessage(1, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_request_mtu_failed_on_change), bluetoothGattStatus));
            }

            mCurrentMtu = mtu;

            Log.i(LOG_TAG, String.format("new MTU set: %d", mtu));
            postMessage(0, "", String.format(Locale.ENGLISH,"new MTU set to %d", mtu));

            // Only complete the command if we initiated the operation. It can also be initiated by the remote peripheral...
            if (mCurrentCommand == REQUEST_MTU_COMMAND) {
                mCurrentCommand = IDLE;
                // Command has completed successfully
                // Remove the command
                // Move to the next command
                completedCommand();
            }

            // TODO: Put in a function
            // Check if the computer is a BLE libdivecomputer
            if (isLibDiveComputer(gatt)) {
                // The computer is a BLE libdivecomputer
                // Enable notification on the CharacteristicTx??
                if (enableNotifications(mLibDiveComputerFound.getService(), mLibDiveComputerFound.getCharacteristicTx(),true)) {
                    // Signal the calling parent that the connection and enableNotifications() were successful
                    postOnDeviceConnected(true);
                    Log.i(LOG_TAG, String.format("onMtuChanged - Connection and enableNotifications were successful for Device %s", gatt.getDevice()));
                    postMessage(0, "", String.format(mContext.getResources().getString(R.string.msg_bluetooth_on_mtu_changed_success), gatt.getDevice()));
                } else {
                    // The connection was successful but not the enableNotifications()
                    mGattCallback.onConnectionStateChange(mGatt, ENABLE_NOTIFICATIONS_FAILED, BluetoothProfile.STATE_DISCONNECTED);
                }
            } else {
                // The connect was successful but not a supported libdivecomputer
                mGattCallback.onConnectionStateChange(mGatt, DIVE_COMPUTER_NOT_SUPPORTED, BluetoothProfile.STATE_DISCONNECTED);
            }
        }

        // NOTE the signature of this method is inconsistent with the other callbacks, i.e. position of status
        @Override
        public void onDescriptorRead(@NotNull final BluetoothGatt gatt, @NotNull final BluetoothGattDescriptor descriptor, final int status, @Nullable final byte[] value) {
            Log.d(LOG_TAG, "onDescriptorRead()");

            final BluetoothGattStatus bluetoothGattStatus = BluetoothGattStatus.fromValue(status);
            if (bluetoothGattStatus != BluetoothGattStatus.SUCCESS) {
                Log.e(LOG_TAG, String.format("onDescriptorRead - Reading descriptor %s failed for device %s with status %s", descriptor.getUuid(), mDevice.getAddress(), bluetoothGattStatus.name()));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_read_descriptor_failed_on_read), descriptor.getUuid(), mDevice.getAddress(), bluetoothGattStatus.name()));
            }

            // NOTE: Nothing to do!
//            final byte[] safeValue = MyFunctions.nonnullOf(value);
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    gattCallback.onDescriptorRead(BluetoothPeripheral.this, safeValue, descriptor, gattStatus);
//                }
//            });

            // Command has completed successfully
            // Remove the command
            // Move to the next command
            completedCommand();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, final BluetoothGattDescriptor descriptor, final int status) {
            Log.d(LOG_TAG, "onDescriptorWrite()");

            // Do some checks first
            final BluetoothGattStatus bluetoothGattStatus = BluetoothGattStatus.fromValue(status);
            final BluetoothGattCharacteristic parentCharacteristic = descriptor.getCharacteristic();
            if(bluetoothGattStatus != BluetoothGattStatus.SUCCESS) {
                // TODO: Test result of BluetoothBytesParser.asHexString
                Log.e(LOG_TAG, String.format("onDescriptorWrite - Write descriptor failed value %s for Device %s and Characteristic %s with Status %s", BluetoothBytesParser.asHexString(descriptor.getValue()), gatt.getDevice().getAddress(), parentCharacteristic.getUuid().toString(), bluetoothGattStatus.name()));
                postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), String.format(mContext.getResources().getString(R.string.msg_bluetooth_write_descriptor_failed),BluetoothBytesParser.asHexString(descriptor.getValue()), gatt.getDevice().getAddress(), parentCharacteristic.getUuid().toString(), bluetoothGattStatus.name()));
                return;
            }

            // Check if this was the Client Configuration Descriptor
            if(descriptor.getUuid().equals(CCC_DESCRIPTOR_UUID)) {
                // This is a setNotify operation
                if(status==BluetoothGatt.GATT_SUCCESS) {
                    // Check if we were turning notify on or off
                    byte[] value = descriptor.getValue();
                    if (value != null) {
                        if (value[0] != 0) {
                            // Notify set to on, add it to the set of notifying characteristics
                            // TODO: Is it needed?
                            // notifyingCharacteristics.add(parentCharacteristic.getUuid());
                        }
                    } else {
                        // Notify was turned off, so remove it from the set of notifying characteristics
                        // TODO: Is it needed?
                        // notifyingCharacteristics.remove(parentCharacteristic.getUuid());
                    }
                }
            } else {
            // This was a normal descriptor write....
            }

            // Command has completed successfully
            // Remove the command
            // Move to the next command
            completedCommand();
        }
    };

    // ***** Create a Device scan callback *****
    private final ScanCallback mScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.d(LOG_TAG, "onScanResult()");

            Bluetooth bluetooth = new Bluetooth(mContext);
            bluetooth.setDevice(result.getDevice());
            bluetooth.setService("Not discovered yet");// TODO: Put in string/code
            bluetooth.setDeviceName(result.getDevice().getName());
            bluetooth.setMacAddress(result.getDevice().getAddress()); // MAC address
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                bluetooth.setAlias(result.getDevice().getAlias());
            } else {
                bluetooth.setAlias("");
            }
            bluetooth.setTransport(result.getDevice().getType());
            bluetooth.setBondState(result.getDevice().getBondState());
            bluetooth.setRssi(result.getRssi());
            // Make sure the device is not already in the list
            // Make sure it is a Bluetooth Low Energy device or Dual
            if (!mBluetoothList.contains(bluetooth)
                    && (result.getDevice().getType() == BluetoothDevice.DEVICE_TYPE_LE || result.getDevice().getType() == BluetoothDevice.DEVICE_TYPE_DUAL)) {
                // Add a Bluetooth POJO at the end of the list
                mBluetoothList.add(bluetooth);
                // Post a notifyDataSetChanged to show every added device one at a time
                postNotifyDataSetChanged();
                // TODO: Does it make sense to issue a postNotifyItemInserted
                //       for an ever changing result set
                //       Need more testing
//                postNotifyItemInserted(mBluetoothList.size() - 1);
            }

            // TODO: Build a different kind of list?
//            final List<BluetoothGattService> services = mGatt.getServices();
//            Log.i(LOG_TAG, String.format("onScanResult - Discovered %d services'", services.size()));
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(LOG_TAG, "onBatchScanResults()");

            // TODO: Test nothing to do
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(LOG_TAG, "onScanFailed()");

            // TODO: Test nothing to do
        }
    };

    // ***** New class *****
    private final BroadcastReceiver bondStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "bondStateReceiver -onReceive");

            final String action = intent.getAction();
            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            // Ignore updates for other devices
            if (mGatt == null || !Objects.requireNonNull(device).getAddress().equals(mGatt.getDevice().getAddress()))
                return;

            // Check if action is valid
            if(action == null) return;

            // Take action depending on new bond state
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                final int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int previousBondState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, -1);
                handleBondStateChange(bondState, previousBondState);
            } else if (action.equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {
                // NOTE: Never called
                // NOTE: Handled in pairingRequestBroadcastReceiver
                final int paringVariant = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                final int pairingKey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR);
            }
        }
    };

    // ***** New class *****
    private final BroadcastReceiver pairingRequestBroadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(LOG_TAG, "pairingRequestBroadcastReceiver -onReceive()");

//            String action = intent.getAction();
//            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
//                BluetoothDevice dev = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                //pair from device: dev.getName()
//                if (dev != null) {
//                    dev.setPairingConfirmation(true);
//                }
//            }

            boolean result2;

            try {
                // The device sent the Pairing Request and put itself in the intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
                // The pin in case you need to accept for an specific pin
                Log.d(LOG_TAG, "pairingRequestBroadcastReceiver-PIN: " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY",0));
                // TODO: Maybe you look for a name or address??
                Log.d(LOG_TAG, "pairingRequestBroadcastReceiver-Bonded with device: " + device.getName());
                byte[] pinBytes;
                pinBytes = (""+pin).getBytes("UTF-8");
                result2 = device.setPin(pinBytes); // TRUE
//                //setPairing confirmation if needed
//                result = device.setPairingConfirmation(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

            final BluetoothDevice receivedDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (receivedDevice == null) return;

            // Skip other devices
            if (!receivedDevice.getAddress().equalsIgnoreCase(mDevice.getAddress())) return;

            final int paringVariant = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
            // NOTE: NULL
            final String pairingKey = intent.getStringExtra(BluetoothDevice.EXTRA_PAIRING_KEY);//null
            Log.i(LOG_TAG, String.format("pairingRequestBroadcastReceiver - Pairing request received %s for variant %s", pairingVariantToString(paringVariant), paringVariant));
            postMessage(0, "", String.format("pairingRequestBroadcastReceiver - Pairing request received %s for variant %s", pairingVariantToString(paringVariant), paringVariant));

            switch(paringVariant) {
                case PAIRING_VARIANT_PIN:
                    final String pin = getPinCode(receivedDevice);
                    if (pin != null) {
                        Log.i(LOG_TAG, String.format("pairingRequestBroadcastReceiver - Setting PIN code for this peripheral using %s", pin));
                        postMessage(0, "", String.format("pairingRequestBroadcastReceiver - Setting PIN code for this peripheral using %s", pin));
                        receivedDevice.setPin(pin.getBytes());
                        abortBroadcast();
                    }
                    break;
                case PAIRING_VARIANT_PASSKEY:
                    break;
                case PAIRING_VARIANT_PASSKEY_CONFIRMATION:
                    break;
                case PAIRING_VARIANT_CONSENT: // NOTE: Received
                    break;
                case PAIRING_VARIANT_DISPLAY_PASSKEY:
                    break;
                case PAIRING_VARIANT_DISPLAY_PIN:
                    break;
                case PAIRING_VARIANT_OOB_CONSENT:
                    break;
                case PAIRING_VARIANT_PIN_16_DIGITS:
                    break;
                default:
                    Log.e(LOG_TAG, "pairingRequestBroadcastReceiver - Received an invalid EXTRA_PAIRING_VARIANT");
                    postMessage(2, mContext.getResources().getString(R.string.dlg_bluetooth_error), mContext.getResources().getString(R.string.msg_bluetooth_bonding_invalid_varian));
                    break;
            }
        }
    };

    // Equals

    // Starts of parcelable

}
