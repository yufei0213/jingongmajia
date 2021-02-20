package com.unitedbustech.eld.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.unitedbustech.eld.bluetooth.BluetoothHandler;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.system.HeartBeatService;

/**
 * @author zhangyu
 * @date 2018/5/7
 * @description
 *
 * 本服务是使用的Service组件。用于监听App是否被强制关闭。
 * 如果强制关闭。则会调用原生的onTaskRemoved方法。
 * 内部进行内存回收。
 * 参考：@link https://stackoverflow.com/questions/19568315/how-to-handle-code-when-app-is-killed-by-swiping-in-android
 */
public class CheckForceStopService extends Service {

    private final String TAG = "CheckForceStopService";

    public static Intent newIntent(Context context){

        Intent intent = new Intent(context, CheckForceStopService.class);
        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG,"CheckForceStopService Has Been Binded!!");
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        Logger.i(TAG,"Check Task Removed！start stop services!!");
        //回收进程
        stopServices();
        //结束自己
        stopSelf();
    }

    /**
     * 内存回收函数。
     */
    private void stopServices(){

        //关闭蓝牙
        BluetoothHandler.getInstance().destroy();
        //关闭Gps信息
        LocationHandler.getInstance().destroy();
        //关闭心跳
        HeartBeatService.getInstance().stop();
    }
}
