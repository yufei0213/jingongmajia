package com.unitedbustech.eld.common;

import android.location.Location;
import android.support.annotation.NonNull;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/6/29
 * @description GPS轨迹
 */
public class GpsLog implements Comparable<GpsLog> {

    private int vehicleId;

    private int status;

    private double latitude;

    private double longitude;

    private double speed;

    private double course;

    private long time;

    private long updateTime;

    public GpsLog() {
    }

    public GpsLog(GpsLog gpsLog) {

        this.vehicleId = gpsLog.getVehicleId();
        this.status = gpsLog.getStatus();
        this.latitude = gpsLog.getLatitude();
        this.longitude = gpsLog.getLongitude();
        this.speed = gpsLog.getSpeed();
        this.course = gpsLog.getCourse();
        this.time = gpsLog.getTime();

        this.updateTime = new Date().getTime();
    }

    public void update(Location location) {

        if (location == null) {

            return;
        }
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.speed = location.getSpeed() > 0 ? location.getSpeed() : 0d;
        this.course = location.getBearing() > 0 ? location.getBearing() : 0d;
        this.time = location.getTime();

        this.updateTime = new Date().getTime();
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getCourse() {
        return course;
    }

    public void setCourse(double course) {
        this.course = course;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public int compareTo(@NonNull GpsLog o) {

        return new Date(o.getTime()).compareTo(new Date(this.getTime()));
    }
}
