package com.proton.setupnet;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.proton.setupnet.component.App;
import com.proton.setupnet.utils.DensityUtils;
import com.proton.setupnet.utils.ble.AdvertiseManager;
import com.proton.setupnet.veiw.PopSpinnerView;
import com.wms.logger.Logger;
import com.wms.utils.NetUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yxf.setup.BleOperatorManager;
import com.yxf.setup.callback.OnAdvertiseStopListener;
import com.yxf.setup.callback.OnAdvertisingListener;
import com.yxf.setup.enums.DeviceType;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * 设备类型
     */
    private List<DeviceType> deviceTypeList = new ArrayList<>();

    private boolean isStart = true;
    Button btnStart, btnConfigIp;
    EditText etWifiName;
    EditText etWifiPassword;
    private int deviceTypePosition;
    byte[] wifibytes;
    byte[] pwdbytes;

    /**
     * 定时每一个小时广播一次，每次1分钟
     */
    public static final int INTERVAL = 3 * 60;
    /**
     * 持续广播的时间，1分钟后自动关闭
     */
    public static final int ADVERTISE_INTERVAL = 60 * 1000;
    private static AlarmManager mAlarmManager;
    private static PendingIntent wakeIntent;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    public static MainActivity instance;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        requestPermission();

        PopSpinnerView spinnerView = findViewById(R.id.popSpinnerview);
        btnStart = findViewById(R.id.btn_start);
        btnConfigIp = findViewById(R.id.btn_config_ip);
        etWifiName = findViewById(R.id.et_wifi_name);
        etWifiPassword = findViewById(R.id.et_wifi_password);
        initDeviceType();
        String connectWifiSsid = NetUtils.getConnectWifiSsid(this);
        final String wifiName = connectWifiSsid.replace('"', ' ').replace('"', ' ').trim();

        wifibytes = wifiName.getBytes();
        etWifiName.setText(wifiName);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (isStart) {
                    if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                        Toast.makeText(MainActivity.this, "蓝牙未开启,请重启蓝牙", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(etWifiName.getText().toString())) {
                        Toast.makeText(MainActivity.this, "wifi名称不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (TextUtils.isEmpty(etWifiPassword.getText().toString())) {
                        Toast.makeText(MainActivity.this, "wifi密码不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String pwd = etWifiPassword.getText().toString().replace('"', ' ').replace('"', ' ').trim();
                    pwdbytes = pwd.getBytes();

                    if (wifiName.length() + pwd.length() > 25) {
                        Toast.makeText(MainActivity.this, "wifi长度和密码长度之和不能大于25个字节", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btnStart.setText("停止配网");
                    BleOperatorManager.getInstance()
                            .setDeviceType(deviceTypeList.get(deviceTypePosition))
                            .setSsid(wifiName)
                            .setPwd(pwd)
                            .startAdvertising(new OnAdvertisingListener() {
                                @Override
                                public void onStartAdvertising(byte[] data) {

                                }

                                @Override
                                public void onSuccess() {
                                    Logger.w("发送成功");
                                }

                                @Override
                                public void onFail(String msg) {
                                    Logger.w(msg);
                                }

                            });
                } else {
                    btnStart.setText("开始配网");
                    BleOperatorManager.getInstance()
                            .setOnAdvertiseStopListener(new OnAdvertiseStopListener() {
                                @Override
                                public void onStopSuccess() {
                                    Logger.w("停止成功");
                                    //initAlarmManager(INTERVAL);
                                }

                                @Override
                                public void onStopFail(String msg) {
                                    Logger.w("停止失败");
                                }

                            })
                            .stopAdvertise();

                }
                isStart = !isStart;
            }
        });

        btnConfigIp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ConfigIpActivity.class));
            }
        });

        spinnerView.init(deviceTypeList.size(), DensityUtils.dp2px(this, 200), new PopSpinnerView.NameFilterSpinner() {
            @Override
            public String filter(int position) {
                deviceTypePosition = position;
                return deviceTypeList.get(position).getType();
            }

            @Override
            public void onItemClickListner(int position) {
                deviceTypePosition = position;
            }
        });
        spinnerView.setTextContent(deviceTypeList.get(0).getType());
    }

    private void initDeviceType() {
        deviceTypeList.add(DeviceType.AO_LANG_PEDESTAL);
        deviceTypeList.add(DeviceType.AO_LANG_REPEATER);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPause() {
        super.onPause();
        if (!isStart) {
            isStart = true;
            btnStart.setText("开始配网");
        }
    }

    public static void initAlarmManager(float wait) {
        if (wakeIntent == null) {
            wakeIntent = PendingIntent.getBroadcast(App.get(), 0, new Intent(App.get(), MainActivity.WakeReceiver.class), 0);
        }
        if (mAlarmManager == null) {
            mAlarmManager = (AlarmManager) App.get().getSystemService(Context.ALARM_SERVICE);
        }
        mAlarmManager.cancel(wakeIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) (SystemClock.elapsedRealtime() + wait * 1000), wakeIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) (SystemClock.elapsedRealtime() + wait * 1000), wakeIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static class WakeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.w("唤醒服务");
            if (instance.wifibytes != null && instance.pwdbytes != null) {
                initAlarmManager(INTERVAL);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AdvertiseManager.getInstance().stopAdvertising();
                    }
                }, ADVERTISE_INTERVAL);
//                AdvertiseManager.getInstance().startAdvertising(instance, instance.deviceTypeList.get(instance.deviceTypePosition), instance.etWifiName.getText().toString(), instance.etWifiPassword.getText().toString());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdvertiseManager.getInstance().stopAdvertising();
        wifibytes = null;
        pwdbytes = null;
    }

    @SuppressLint("CheckResult")
    private void requestPermission() {
        Logger.w("开始申请权限。。。");
        AndPermission.with(this)
                .runtime()
                .permission(Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        String connectWifiSsid = NetUtils.getConnectWifiSsid(MainActivity.this);
                        String wifiName = connectWifiSsid.replace('"', ' ').replace('"', ' ').trim();
                        wifibytes = wifiName.getBytes();
                        etWifiName.setText(wifiName);
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        requestPermission();
                    }
                })
                .start();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


}

