package com.unitedbustech.eld.datacollector.device.pacific;

import com.pt.sdk.AbstractTrackerStateObserver;
import com.pt.sdk.TSError;

/**
 * @author yufei0213
 * @date 2018/6/4
 * @description PT30设备状态监听
 */
public abstract class PT30DeviceStateListener extends AbstractTrackerStateObserver {

    public abstract void onDeviceConnected();

    public abstract void onDeviceInitComplete();

    public abstract void onDeviceDisconnected();

    @Override
    public void onDiscovered() {

    }

    @Override
    public void onConnected() {

        onDeviceConnected();
    }

    @Override
    public void onSynced() {

        onDeviceInitComplete();
    }

    @Override
    public void onDisconnected() {

        onDeviceDisconnected();
    }

    @Override
    public void onError(TSError tsError) {

    }

    @Override
    public void onFwUpgradeCompleted() {

    }

    @Override
    public void onFwUpgradeFailed(TSError tsError) {

    }

    @Override
    public void onFwUpgradeProgress(Integer integer) {

    }

    @Override
    public void onFwUptodate() {

    }
}
