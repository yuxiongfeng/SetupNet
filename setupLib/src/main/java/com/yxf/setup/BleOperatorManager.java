package com.yxf.setup;

import android.annotation.TargetApi;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.wms.logger.Logger;
import com.yxf.setup.callback.OnAdvertiseStopListener;
import com.yxf.setup.util.AdvertiseUtil;
import com.yxf.setup.callback.OnAdvertisingListener;
import com.yxf.setup.enums.DeviceType;
import com.yxf.setup.operator.BleOperatorImp;
import com.yxf.setup.operator.IBleOperator;

public class BleOperatorManager {

    private static Context mContext;
    private static IBleOperator operator;
    /**
     * 设备类型
     */
    private DeviceType deviceType;
    /**
     * wifi名称
     */
    private String ssid;
    /**
     * wifi密码
     */
    private String pwd;

    private OnAdvertisingListener mOnAdvertisingListener;
    private OnAdvertiseStopListener mOnAdvertiseStopListener;

    public static void init(Context context) {
        mContext = context;
        operator = new BleOperatorImp(mContext);
        //初始化日志
        Logger.newBuilder()
                .tag("setup_net")
                .showThreadInfo(false)
                .methodCount(1)
                .context(mContext)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
    }

    public static BleOperatorManager getInstance() {
        return Inner.instance;
    }

    public BleOperatorManager setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
        return this;
    }

    public String getSsid() {
        return ssid;
    }

    public BleOperatorManager setSsid(String ssid) {
        this.ssid = ssid;
        return this;
    }

    public String getPwd() {
        return pwd;
    }

    public BleOperatorManager setPwd(String pwd) {
        this.pwd = pwd;
        return this;
    }

    public static final class Inner {
        private static final BleOperatorManager instance = new BleOperatorManager();
    }

    /**
     * 发送广播
     *
     * @param onAdvertisingListener
     */
    public void startAdvertising(OnAdvertisingListener onAdvertisingListener) {
        mOnAdvertisingListener = onAdvertisingListener;
        if (deviceType == null) {
            mOnAdvertisingListener.onFail("设备类型不能为空");
            return;
        }
        if (TextUtils.isEmpty(ssid)) {
            mOnAdvertisingListener.onFail("wifi名称不能为空");
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            mOnAdvertisingListener.onFail("密码不能为空");
            return;
        }
        operator.setOnAdvertisingListener(onAdvertisingListener);

        operator.startAdvertising(getAdvertiseSettings(), getAdvertiseData());
    }

    public BleOperatorManager setOnAdvertiseStopListener(OnAdvertiseStopListener mOnAdvertiseStopListener) {
        this.mOnAdvertiseStopListener = mOnAdvertiseStopListener;
        return this;
    }

    public void stopAdvertise() {
        if (mOnAdvertiseStopListener!=null) {
            operator.setOnStopAdvertiseListener(mOnAdvertiseStopListener);
        }
        operator.stopAdvertise();
    }



    private AdvertiseSettings getAdvertiseSettings() {
        return AdvertiseUtil.createAdvSettings();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private AdvertiseData getAdvertiseData() {
        String wifiName = ssid.replace('"', ' ').replace('"', ' ').trim();
        byte[] wifiBytes = wifiName.getBytes();
        String password = pwd.replace('"', ' ').replace('"', ' ').trim();
        byte[] pwdBytes = password.getBytes();
        AdvertiseData advertiseData = AdvertiseUtil.createAdvertiseData(deviceType, wifiBytes, pwdBytes);
        Logger.w("发送的数据: ", advertiseData.getManufacturerSpecificData());
        return advertiseData;
    }

    /**
     * 获取配网对象的设备类型
     *
     * @return
     */
    public DeviceType getDeviceType() {
        return deviceType;
    }

    /**
     * 打开蓝牙设备
     */
    public void openBluetooth() {
        operator.openBluetooth();
    }

    /**
     * 关闭蓝牙设备
     */
    public void closeBluetooth() {
        operator.closeBluetooth();
    }

    /**
     * 蓝牙是否打开
     *
     * @return
     */
    public boolean isBluetoothOpened() {
        return operator.isBluetoothOpened();
    }

    /**
     * 是否支持ble
     *
     * @return
     */
    public boolean isSupportBle() {
        return operator.isSupportBle();
    }


}
