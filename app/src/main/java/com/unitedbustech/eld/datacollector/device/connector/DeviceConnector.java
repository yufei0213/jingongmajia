package com.unitedbustech.eld.datacollector.device.connector;

import android.support.annotation.NonNull;

import com.unitedbustech.eld.datacollector.device.ConfigOption;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 硬件连接器
 */
public interface DeviceConnector {

    /**
     * 初始化
     *
     * @param configOption 配置
     */
    void init(ConfigOption configOption);

    /**
     * 连接
     */
    void connect();

    /**
     * 断开连接
     */
    void disconnect();

    /**
     * 设置回调
     *
     * @param callback 回调
     */
    void setCallback(@NonNull DeviceConnectorCallback callback);
}
