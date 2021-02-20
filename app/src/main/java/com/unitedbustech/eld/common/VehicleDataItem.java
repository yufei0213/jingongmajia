package com.unitedbustech.eld.common;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 需要从硬件中读取的数据
 */
public enum VehicleDataItem {

    /**
     * Vehicle Id Number
     */
    VIN,
    /**
     * 引擎状态，运行和停止
     */
    ENGINE_STATE,
    /**
     * 引擎事件，打火和熄火
     */
    ENGINE_EVENT,
    /**
     * 车辆状态，静止和运动
     */
    VEHICLE_STATE,
    /**
     * 车辆总里程
     */
    ODOMETER,
    /**
     * 引擎总时间
     */
    ENGINE_HOURS,
    /**
     * ECM UTC 时间
     */
    UTC_TIME,
    /**
     * 经纬度
     */
    GPS,
    /**
     * 读取转速
     */
    RPM,
    /**
     * 读取车速
     */
    SPEED,
    /**
     * 历史记录
     */
    HISTORY
}
