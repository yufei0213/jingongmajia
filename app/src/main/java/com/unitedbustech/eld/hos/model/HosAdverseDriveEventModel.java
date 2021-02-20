package com.unitedbustech.eld.hos.model;

import com.unitedbustech.eld.common.DriverState;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description HOS驾驶状态事件使用的事件模型。该模型支持页面显示与时间逻辑的计算。从基础事件转化而来。
 * 只有6种驾驶状态的切换的模型。
 * 该事件模型聚合形成HosDayModel。
 */
public class HosAdverseDriveEventModel extends HosEventModel {

    /**
     * 开始的新状态
     */
    private DriverState driverState;

    public DriverState getDriverState() {
        return driverState;
    }

    public void setDriverState(DriverState driverState) {
        this.driverState = driverState;
    }
}
