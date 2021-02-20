package com.unitedbustech.eld.eventcenter.model;

/**
 * @author zhangyu
 * @date 2018/2/1
 * @description 一对模型，对应的是一对driving与odnd的未认领日志的模型。
 */
public class ModelCouple {

    /**
     * 开始的驾驶事件模型。
     */
    private DriverStatusModel drivingModel;

    /**
     * 结束的odnd模型。
     */
    private DriverStatusModel odndModel;

    public ModelCouple(DriverStatusModel drivingModel, DriverStatusModel odndModel) {
        this.drivingModel = drivingModel;
        this.odndModel = odndModel;
    }

    public DriverStatusModel getDrivingModel() {
        return drivingModel;
    }

    public void setDrivingModel(DriverStatusModel drivingModel) {
        this.drivingModel = drivingModel;
    }

    public DriverStatusModel getOdndModel() {
        return odndModel;
    }

    public void setOdndModel(DriverStatusModel odndModel) {
        this.odndModel = odndModel;
    }
}
