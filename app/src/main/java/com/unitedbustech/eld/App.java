package com.unitedbustech.eld;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.activity.ActivityStackListener;
import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.crash.CrashHandler;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.message.UploadFireBaseToken;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.selfcheck.SelfCheckService;
import com.unitedbustech.eld.system.DataCacheService;
import com.unitedbustech.eld.system.GpsLogService;
import com.unitedbustech.eld.system.HeartBeatService;
import com.unitedbustech.eld.system.SystemMonitor;
import com.unitedbustech.eld.system.TeamWorkService;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.ThreadUtil;

/**
 * @author yufei0213
 * @date 2018/1/5
 * @description app
 */
public class App extends Application implements ActivityStackListener {

    private static final String TAG = "App";

    private static Context context;

    /**
     * 获取Context
     *
     * @return Context
     */
    public static Context getContext() {

        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {

        //因为在设置语言的时候，需要context，因此需要进行先赋值
        context = base;
        Context newContext = LanguageUtil.getInstance().setConfiguration(base);
        super.attachBaseContext(newContext);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        LanguageUtil.getInstance().setConfiguration(this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {

        super.onCreate();

        context = getApplicationContext();

        Logger.init();

        ActivityStack.getInstance().setActivityStackListener(this);

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    @Override
    public void onAppCreate() {

        ThreadUtil.getInstance().init();
    }

    @Override
    public void onAppDestroy() {

        ThreadUtil.getInstance().destroy();
    }

    @Override
    public void onServiceStart() {

        LanguageUtil.getInstance().setConfiguration(context);

        RequestCacheService.getInstance().init();

        SelfCheckService.getInstance().init();

        DataCacheService.getInstance().init();

        BluetoothHandler.getInstance().init();

        DataCollectorHandler.getInstance().init();

        EventCenter.getInstance().init();

        SystemMonitor.getInstance().init();

        ThreadUtil.getInstance().execute(new UploadFireBaseToken());

        TeamWorkService.getInstance().init();

        GpsLogService.getInstance().init();
    }

    @Override
    public void onServiceStop() {

        SystemMonitor.getInstance().destroy();

        DataCollectorHandler.getInstance().destroy();

        SelfCheckService.getInstance().destroy();

        DataCacheService.getInstance().destroy();

        BluetoothHandler.getInstance().destroy();

        LocationHandler.getInstance().destroy();

        HeartBeatService.getInstance().stop();

        GpsLogService.getInstance().destroy();

        TeamWorkService.getInstance().destroy();

        RequestCacheService.getInstance().destroy();

        ModelCenter.getInstance().destroy();

        EventCenter.getInstance().destroy();
    }
}
