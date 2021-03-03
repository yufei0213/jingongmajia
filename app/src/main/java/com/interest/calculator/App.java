package com.interest.calculator;

import android.app.Application;
import android.content.Context;

import com.interest.calculator.activity.ActivityStackListener;
import com.interest.calculator.crash.CrashHandler;
import com.interest.calculator.logs.Logger;
import com.interest.calculator.system.UUIDS;
import com.interest.calculator.util.ThreadUtil;

import io.branch.referral.Branch;

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

        // Branch logging for debugging
        Branch.enableLogging();
        // Branch object initialization
        Branch.getAutoInstance(this);
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
