package com.unitedbustech.eld.common;

import com.unitedbustech.eld.datacollector.common.VehicleDataRecorder;

/**
 * @author yufei0213
 * @date 2018/1/13
 * @description 车辆数据
 */
public class VehicleDataModel {

    /**
     * 引擎状态，运行和停止
     */
    private int engineState;

    /**
     * 引擎事件，点火和熄火
     */
    private int engineEvent;

    /**
     * 车辆状态
     */
    private int vehicleState;

    /**
     * 总里程，单位英里
     */
    private double totalOdometer;

    /**
     * 累计里程，单位英里
     */
    private double accumulatedOdometer;
    /**
     * 累计里程，单位英里
     */
    private double offsetOdometer;
    /**
     * 里程偏移量是否生效
     */
    private boolean isOdoOffsetValid;

    /**
     * 总引擎时间，单位小时
     */
    private double totalEngineHours;

    /**
     * 累计引擎时间，单位小时
     */
    private double accumulatedEngineHours;

    /**
     * utc时间
     */
    private long utcTime;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 距离上次已知地点的距离。
     * 单位：英里（mi）
     */
    private double lastDistance;
    /**
     * Vehicle ID Number
     */
    private String vin;

    /**
     * 当前车速
     */
    private double speed;

    /**
     * 引擎转速
     */
    private double rpm;

    public VehicleDataModel() {
    }

    public VehicleDataModel(VehicleDataRecorder vehicleDataRecorder) {

        if (vehicleDataRecorder == null) {

            return;
        }

        this.engineState = vehicleDataRecorder.getEngineState();
        this.engineEvent = vehicleDataRecorder.getEngineEvent();
        this.vehicleState = vehicleDataRecorder.getVehicleState();
        this.totalOdometer = vehicleDataRecorder.getTotalOdometer();
        this.accumulatedOdometer = vehicleDataRecorder.getAccumulatedOdometer();
        this.offsetOdometer = vehicleDataRecorder.getOffsetOdometer();
        this.isOdoOffsetValid = vehicleDataRecorder.isOdoOffsetValid();
        this.totalEngineHours = vehicleDataRecorder.getTotalEngineHours();
        this.accumulatedEngineHours = vehicleDataRecorder.getAccumulatedEngineHours();
        this.utcTime = vehicleDataRecorder.getUtcTime();
        this.latitude = vehicleDataRecorder.getLatitude();
        this.longitude = vehicleDataRecorder.getLongitude();
        this.lastDistance = vehicleDataRecorder.getLastDistance();
        this.vin = vehicleDataRecorder.getVin();
        this.rpm = vehicleDataRecorder.getRpm();
        this.speed = vehicleDataRecorder.getSpeed();
    }

    public VehicleDataModel(VehicleDataModel model) {

        if (model == null) {

            return;
        }

        this.engineState = model.getEngineState();
        this.engineEvent = model.getEngineEvent();
        this.vehicleState = model.getVehicleState();
        this.totalOdometer = model.getTotalOdometer();
        this.accumulatedOdometer = model.getAccumulatedOdometer();
        this.offsetOdometer = model.getOffsetOdometer();
        this.isOdoOffsetValid = model.getIsOdoOffsetValid();
        this.totalEngineHours = model.getTotalEngineHours();
        this.accumulatedEngineHours = model.getAccumulatedEngineHours();
        this.utcTime = model.getUtcTime();
        this.latitude = model.getLatitude();
        this.longitude = model.getLongitude();
        this.lastDistance = model.getLastDistance();
        this.vin = model.getVin();
        this.rpm = model.getRpm();
        this.speed = model.getSpeed();
    }

    public double getLastDistance() {
        return lastDistance;
    }

    public void setLastDistance(double lastDistance) {
        this.lastDistance = lastDistance;
    }

    public int getEngineState() {
        return engineState;
    }

    public void setEngineState(int engineState) {
        this.engineState = engineState;
    }

    public int getEngineEvent() {
        return engineEvent;
    }

    public void setEngineEvent(int engineEvent) {
        this.engineEvent = engineEvent;
    }

    public int getVehicleState() {
        return vehicleState;
    }

    public void setVehicleState(int vehicleState) {
        this.vehicleState = vehicleState;
    }

    public double getTotalOdometer() {
        return totalOdometer;
    }

    public void setTotalOdometer(double totalOdometer) {
        this.totalOdometer = totalOdometer;
    }

    public double getAccumulatedOdometer() {
        return accumulatedOdometer;
    }

    public double getOffsetOdometer() {
        return offsetOdometer;
    }

    public void setOffsetOdometer(double offsetOdometer) {
        this.offsetOdometer = offsetOdometer;
    }

    public boolean getIsOdoOffsetValid() {
        return isOdoOffsetValid;
    }

    public void setIsOdoOffsetValid(boolean valid) {
        this.isOdoOffsetValid = valid;
    }

    public void setAccumulatedOdometer(double accumulatedOdometer) {
        this.accumulatedOdometer = accumulatedOdometer;
    }

    public double getTotalEngineHours() {
        return totalEngineHours;
    }

    public void setTotalEngineHours(double totalEngineHours) {
        this.totalEngineHours = totalEngineHours;
    }

    public double getAccumulatedEngineHours() {
        return accumulatedEngineHours;
    }

    public void setAccumulatedEngineHours(double accumulatedEngineHours) {
        this.accumulatedEngineHours = accumulatedEngineHours;
    }

    public long getUtcTime() {
        return utcTime;
    }

    public void setUtcTime(long utcTime) {
        this.utcTime = utcTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public double getRpm() {
        return rpm;
    }

    public void setRpm(double rpm) {
        this.rpm = rpm;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
