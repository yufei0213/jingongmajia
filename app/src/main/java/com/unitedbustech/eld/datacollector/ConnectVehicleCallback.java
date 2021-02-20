package com.unitedbustech.eld.datacollector;

/**
 * @author yufei0213
 * @date 2018/2/1
 * @description 连接车辆回调函数
 */
public interface ConnectVehicleCallback {

    void connectSuccess();

    void connectFailed();
}
