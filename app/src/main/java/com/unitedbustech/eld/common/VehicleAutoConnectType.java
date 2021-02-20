package com.unitedbustech.eld.common;

/**
 * @author zhangyu
 * @date 2018/3/12
 * @description 车辆自动变化类型
 */
public class VehicleAutoConnectType {

    /**
     * 自动重连至ECM信息。
     * 用于从GPS自动链接至ECM。
     */
    public static final int ECM = 1;

    /**
     * 自动切换至GPS模式。
     * 用于从ECM断开切换到GPS
     */
    public static final int GPS = 2;

    /**
     * 用于手动断开链接的状态
     */
    public static final int DISCONNECTED = 3;
}
