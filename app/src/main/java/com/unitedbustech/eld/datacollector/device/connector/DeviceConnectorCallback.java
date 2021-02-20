package com.unitedbustech.eld.datacollector.device.connector;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 硬件连接器回调
 */
public interface DeviceConnectorCallback {

    /**
     * 连接状态变化
     *
     * @param state 状态
     */
    void onConnectionStateChange(boolean state);
}
