package com.unitedbustech.eld.hos.model;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description HOS的其他事件使用的事件模型。该模型不支持页面显示与时间逻辑的计算。
 * 事件从服务端采集的基础事件转化而来。
 * 该事件模型聚合形成HosDayModel。
 */
public class HosEventModel implements Comparable<HosEventModel> {

    /**
     * 本地缓存的ID，用于数据关系查询
     */
    protected long localId;

    /**
     * 该事件的开始时间。
     * 单位:秒。
     * 意义:由于该模型依附于HosDayModel，因此该时间是从一天的0点开始经过的秒数。
     * 例如:上午10:00发生的事件。该值为 10 * 60 * 60 = 36000;
     */
    protected int startSecond;

    /**
     * 当前状态描述.例如。登录事件，该值为 Login
     */
    protected String stateString;

    /**
     * 时间
     */
    protected Date date;

    /**
     * 时间
     */
    protected String dateStr;

    /**
     * 文字的地址。格式化后的地址
     */
    protected String location;

    /**
     * 纬度
     */
    protected Double latitude;

    /**
     * 经度
     */
    protected Double longitude;

    /**
     * 里程数
     */
    protected double odometer;

    /**
     * 引擎时间
     */
    protected double engineHour;

    /**
     * 原始类型
     */
    protected int type;

    /**
     * 原始code
     */
    protected int code;

    /**
     * 所属的车辆。
     */
    protected String vehicle;

    /**
     * 备注信息
     */
    protected String remark;

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public int getStartSecond() {
        return startSecond;
    }

    public void setStartSecond(int startSecond) {
        this.startSecond = startSecond;
    }

    public String getStateString() {
        return stateString;
    }

    public void setStateString(String stateString) {
        this.stateString = stateString;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getOdometer() {
        return odometer;
    }

    public void setOdometer(double odometer) {
        this.odometer = odometer;
    }

    public double getEngineHour() {
        return engineHour;
    }

    public void setEngineHour(double engineHour) {
        this.engineHour = engineHour;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public int compareTo(@NonNull HosEventModel hosEventModel) {

        return hosEventModel.date.compareTo(this.date);
    }
}
