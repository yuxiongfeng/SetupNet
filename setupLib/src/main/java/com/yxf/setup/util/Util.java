package com.yxf.setup.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Build;

public class Util {

    private static BluetoothAdapter mBluetoothAdapter = null;

    public static boolean isBluetoothOpen() {
        return getBluetoothState() == 12;
    }

    public static int getBluetoothState() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        return adapter != null ? adapter.getState() : 0;
    }

    /**
     * open Bluetooth
     * @return
     */
    public static boolean openBluetooth() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        return adapter != null ? adapter.enable() : false;
    }

    /**
     * close bluetooth
     * @return
     */
    public static boolean closeBluetooth() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        return adapter != null ? adapter.disable() : false;
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }
    /**
     * 是否支持ble
     * @param context
     * @return
     */
    public static boolean isBleSupported(Context context) {
        return Build.VERSION.SDK_INT >= 18 && context != null && context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le");
    }
}
