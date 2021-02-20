package com.unitedbustech.eld.datacollector.common;

import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;

import java.util.Map;

/**
 * @author yufei0213
 * @date 2018/2/1
 * @description BlueLink历史数据
 */
public class History {

    /**
     * 事件id
     */
    private int id;

    /**
     * 事件类型
     */
    private int type;

    /**
     * 事件发生时间，单位毫秒
     */
    private long eventTime;

    /**
     * 总里程，单位英里
     */
    private double totalOdometer;

    /**
     * 总引擎时间，单位小时
     */
    private double totalEngineHours;

    /**
     * 纬度
     */
    private String latitude;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 当前车速
     */
    private int speed;

    public History() {
    }

    public History(Map<String, Object> result) {

        this.id = (Integer) result.get(ReadDataKey.historyId);
        this.type = (Integer) result.get(ReadDataKey.historyType);
        this.eventTime = (Long) result.get(ReadDataKey.historyTime);
        this.totalOdometer = (Double) result.get(ReadDataKey.historyOdometer);
        this.totalEngineHours = (Double) result.get(ReadDataKey.historyEngineHour);
        this.latitude = Double.toString((Double) result.get(ReadDataKey.historyLat));
        this.longitude = Double.toString((Double) result.get(ReadDataKey.historyLng));
        this.speed = (Integer) result.get(ReadDataKey.historySpeed);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public double getTotalOdometer() {
        return totalOdometer;
    }

    public void setTotalOdometer(double totalOdometer) {
        this.totalOdometer = totalOdometer;
    }

    public double getTotalEngineHours() {
        return totalEngineHours;
    }

    public void setTotalEngineHours(double totalEngineHours) {
        this.totalEngineHours = totalEngineHours;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
