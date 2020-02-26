package com.yxf.setup.operator;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.wms.logger.Logger;
import com.yxf.setup.callback.OnAdvertiseStopListener;
import com.yxf.setup.callback.OnAdvertisingListener;
import com.yxf.setup.util.Util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BleOperatorImp implements IBleOperator {
    private static Context mContext;
    private static OnAdvertisingListener mOnAdvertisingListener;
    private static OnAdvertiseStopListener mOnAdvertiseStopListener;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private ExecutorService service;

    private BleOperatorImp() {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BleOperatorImp(Context context) {
        mContext = context;
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (mBluetoothManager != null && mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        //获取BluetoothLeAdvertiser，BLE发送BLE广播用的一个API
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }
        service = Executors.newSingleThreadExecutor();
    }

    public void setOnAdvertisingListener(OnAdvertisingListener onAdvertisingListener) {
        mOnAdvertisingListener = onAdvertisingListener;
    }

    @Override
    public void setOnStopAdvertiseListener(OnAdvertiseStopListener onStopAdvertiseListener) {
        mOnAdvertiseStopListener=onStopAdvertiseListener;
    }

    public static void setmOnAdvertiseStopListener(OnAdvertiseStopListener mOnAdvertiseStopListener) {
        BleOperatorImp.mOnAdvertiseStopListener = mOnAdvertiseStopListener;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void startAdvertising(final AdvertiseSettings settings, final AdvertiseData advertiseData) {
        if (!Util.isBluetoothOpen()) {
            mOnAdvertisingListener.onFail("蓝牙未开启");
            return;
        }

        if (!Util.isBleSupported(mContext)) {
            mOnAdvertisingListener.onFail("设备不支持ble");
            return;
        }

        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        }

        if (mBluetoothManager != null && mBluetoothAdapter == null) {
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }

        //获取BluetoothLeAdvertiser，BLE发送BLE广播用的一个API
        if (mBluetoothLeAdvertiser == null) {
            mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        }

        service.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mBluetoothLeAdvertiser.startAdvertising(settings, advertiseData, mAdvCallback);
                } catch (Exception e) {
                    if (e != null && TextUtils.isEmpty(e.getMessage())) {
                        mOnAdvertisingListener.onFail(e.getMessage());
                    }
                    e.printStackTrace();
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void stopAdvertise() {
        //关闭BluetoothLeAdvertiser,BluetoothAdapter
        if (mBluetoothLeAdvertiser != null) {
            try {
                mBluetoothLeAdvertiser.stopAdvertising(mAdvCallback);
                mOnAdvertiseStopListener.onStopSuccess();
            } catch (Exception e) {
                if (e != null && TextUtils.isEmpty(e.getMessage())) {
                    mOnAdvertiseStopListener.onStopFail(e.getMessage());
                }else {
                    mOnAdvertiseStopListener.onStopFail("停止广播失败");
                }
            }
            mBluetoothLeAdvertiser = null;
        }
    }

    @Override
    public void openBluetooth() {
        Util.openBluetooth();
    }

    @Override
    public void closeBluetooth() {
        Util.closeBluetooth();
    }

    @Override
    public boolean isBluetoothOpened() {
        return false;
    }

    @Override
    public boolean isSupportBle() {
        return false;
    }

    /**
     * 发送广播的回调
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (mOnAdvertisingListener != null) {
                mOnAdvertisingListener.onSuccess();
            }
            if (settingsInEffect != null) {
                Logger.d("onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode() + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Logger.d("onStartSuccess, settingInEffect is null");
            }
        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            String description;
            switch (errorCode) {
                case ADVERTISE_FAILED_DATA_TOO_LARGE:
                    description = "data is to large";
                    break;
                case ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                    description = "no advertising instance is available";
                    break;
                case ADVERTISE_FAILED_ALREADY_STARTED:
                    description = "advertising is already started";
                    break;
                case ADVERTISE_FAILED_INTERNAL_ERROR:
                    description = "Operation failed due to an internal error";
                    break;
                case ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                    description = "This feature is not supported on this platform";
                    break;
                default:
                    description = "fail";
            }
            mOnAdvertisingListener.onFail(description);
        }
    };

}
