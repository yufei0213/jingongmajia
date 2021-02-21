package com.unitedbustech.eld;

import android.app.Application;
import android.content.Context;

import com.unitedbustech.eld.activity.ActivityStackListener;
import com.unitedbustech.eld.crash.CrashHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.system.UUIDS;
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
    public void onCreate() {

        super.onCreate();

        context = getApplicationContext();

        Logger.init();
        UUIDS.init();

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


    }

    @Override
    public void onServiceStop() {

    }
}
