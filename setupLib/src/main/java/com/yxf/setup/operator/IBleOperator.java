package com.yxf.setup.operator;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;

import com.yxf.setup.callback.OnAdvertiseStopListener;
import com.yxf.setup.callback.OnAdvertisingListener;

/**
 * 蓝牙的相关操作
 */
public interface IBleOperator {

    /**
     * start advertising
     *
     * @param settings
     * @param advertiseData
     */
    void startAdvertising(AdvertiseSettings settings,
                          AdvertiseData advertiseData);

    /**
     * stop advertise
     */
    void stopAdvertise();

    /**
     * open bluetooth
     *
     * @return true is open, false is close
     */
    void openBluetooth();

    /**
     * close bluetooth
     *
     * @return true is success, false is fail
     */
    void closeBluetooth();

    /**
     * whether or not bluetooth opens successfully
     *
     * @return true is sucess,false is fail
     */
    boolean isBluetoothOpened();

    /**
     * check device is support ble
     */
    boolean isSupportBle();

    /**
     * 发送广播的相关回调
     *
     * @param onAdvertisingListener
     */
    void setOnAdvertisingListener(OnAdvertisingListener onAdvertisingListener);

    /**
     * 停止发送
     *
     * @param onStopAdvertiseListener
     */
    void setOnStopAdvertiseListener(OnAdvertiseStopListener onStopAdvertiseListener);

}
