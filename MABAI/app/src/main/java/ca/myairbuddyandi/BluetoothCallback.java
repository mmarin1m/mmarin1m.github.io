package ca.myairbuddyandi;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Michel on 2023-05-29.
 * Holds all of the logic for the BluetoothCallback class
 */

public class BluetoothCallback {

    public void bubbleUpMessage(int iconId, @NotNull String title, @NotNull String message) {
    }

    public void notifyDataSetChanged() {
    }

    public void notifyItemInserted(int pos) {
    }

    public void onCharacteristicRead(@NotNull BluetoothDevice device, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic, int status) {
    }

    public void onCharacteristicUpdate(@NotNull BluetoothDevice device, @NotNull byte[] value, @NotNull BluetoothGattCharacteristic characteristic) {
    }

    public void onConnectionStatusChanged(@NotNull String status) {
    }

    public void onDeviceConnected(boolean connected, Bluetooth bluetooth) {
    }

    public void showBusyDialog(boolean show) {
    }


}
