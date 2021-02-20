package com.unitedbustech.eld.location;

import android.location.Location;

/**
 * @author yufei0213
 * @date 2018/3/6
 * @description StorageLocation
 */
public class StorageLocation {

    private String provider;

    private long time;

    private double latitude;
    private double longitude;

    private float speed;

    public StorageLocation() {
    }

    public StorageLocation(Location location) {

        this.provider = location.getProvider();

        this.time = location.getTime();

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();

        this.speed = location.getSpeed();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
}
