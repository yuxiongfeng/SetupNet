package com.proton.setupnet.component;

import android.app.Application;
import android.content.Context;

import com.proton.setupnet.BuildConfig;
import com.proton.setupnet.utils.ble.AdvertiseManager;
import com.wms.logger.Logger;
import com.yxf.setup.BleOperatorManager;


/**
 * Created by yuxiongfeng.
 * Date: 2019/7/30
 */
public class App extends Application {
    private static App context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
//        AdvertiseManager.getInstance().init(this);
        Logger.newBuilder()
                .tag("setup_net")
                .showThreadInfo(false)
                .methodCount(1)
                .saveLogCount(7)
                .context(this)
                .deleteOnLaunch(false)
                .saveFile(BuildConfig.DEBUG)
                .isDebug(BuildConfig.DEBUG)
                .build();
        BleOperatorManager.init(this);
    }

    public static Context get() {
        return context;
    }
}
