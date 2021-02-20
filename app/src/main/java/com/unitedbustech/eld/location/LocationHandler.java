package com.unitedbustech.eld.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.LocationUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yufei0213
 * @date 2018/1/11
 * @description GPS处理类
 */
public class LocationHandler implements LocationListener {

    private static final String TAG = "LocationHandler";

    /**
     * gps刷新频率 单位：毫秒
     */
    private final long UPDATE_DURATION = 1 * 1000L;
    /**
     * gps刷新距离 单位：米
     */
    private final float UPDATE_DISTANCE = 5F;

    /**
     * gps是否可用
     */
    private boolean isEnable;

    /**
     * 当前最新的Location
     */
    private Location currentLocation;

    /**
     * 系统location管理
     */
    private LocationManager locationManager;

    /**
     * location服务订阅者
     */
    private Map<String, LocationSubscriber> locationSubscriberMap;

    private static volatile LocationHandler instance = null;

    private LocationHandler() {

        locationSubscriberMap = new ConcurrentHashMap<>();
    }

    public static LocationHandler getInstance() {

        if (instance == null) {

            synchronized (LocationHandler.class) {

                if (instance == null) {

                    instance = new LocationHandler();
                }
            }
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

        if (!isAvailable()) {

            return;
        }

        try {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    UPDATE_DURATION,
                    UPDATE_DISTANCE,
                    this,
                    Looper.getMainLooper());

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    UPDATE_DURATION,
                    UPDATE_DISTANCE,
                    this,
                    Looper.getMainLooper());

            // 获取当前位置信息
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                this.currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (this.currentLocation == null) {

                    this.currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
            if (this.currentLocation == null) {

                Logger.w(Tags.LOCATION, "init-location=null");
            } else {

                Logger.d(Tags.LOCATION, "init-location: lat=" + this.currentLocation.getLatitude() + ", lng" + this.currentLocation.getLongitude());
            }
        } catch (SecurityException e) {

            Logger.w(Tags.LOCATION, "LocationHandler init failed. SecurityException");
        } catch (Exception e) {

            Logger.w(Tags.LOCATION, "LocationHandler init exception.");
        }
    }

    /**
     * 销毁
     */
    public void destroy() {

        try {

            if (locationManager != null) {

                locationManager.removeUpdates(this);
                locationManager = null;
            }

            isEnable = false;
            currentLocation = null;

            if (locationSubscriberMap != null) {

                locationSubscriberMap.clear();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }finally {

            instance = null;
        }
    }

    /**
     * 判断gps是否打开
     *
     * @return 是否打开gps
     */
    public boolean isAvailable() {

        locationManager = (LocationManager) App.getContext().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {

            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } else {

            return false;
        }
    }

    /**
     * 是否可用
     *
     * @return 是否可用
     */
    public boolean isEnable() {

        return isEnable;
    }

    /**
     * 获取当前Location
     *
     * @return Location
     */
    public Location getCurrentLocation() {

        // 权限检查
        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Logger.w(TAG, "getCurrentLocation:permissions missing");
            return null;
        }

        // 防止destroy
        if (locationManager == null) {

            locationManager = (LocationManager) App.getContext().getSystemService(Context.LOCATION_SERVICE);
            instance.init();
        }

        // 获取最新Location
        Location location = null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {

                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }

        if (LocationUtil.isBetterLocation(location, this.currentLocation)) {

            Logger.d(TAG, "getCurrentLocation:getBetterLocation");
            this.currentLocation = location;
        }

        if (this.currentLocation == null) {

            Logger.w(TAG, "getCurrentLocation:null");
        } else {

            Logger.d(TAG, "getCurrentLocation:" + this.currentLocation.getProvider() + ", " + this.currentLocation.toString() + ", " + this.currentLocation.getTime());
        }

        return this.currentLocation;
    }

//    /**
//     * 获取当前Location
//     *
//     * @return Location
//     */
//    public Location getCurrentLocation() {
//
//        if (this.currentLocation == null) {
//
//            Logger.w(Tags.LOCATION, "get current location [null]");
//            this.currentLocation = SystemHelper.getLocation();
//            if (this.currentLocation != null) {
//
//                Logger.w(Tags.LOCATION, "get location from storage. lat=" + this.currentLocation.getLatitude() + ", lng=" + this.currentLocation.getLongitude());
//            } else {
//
//                Logger.w(Tags.LOCATION, "get location from storage [null]");
//            }
//        }
//
//        return this.currentLocation;
//    }

//    /**
//     * 获取最新位置，忽略本地存储的Location
//     *
//     * @return Location
//     */
//    public Location getLocation() {
//
//        // 权限检查
//        if (ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(App.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            Logger.w(Tags.LOCATION, "getLocation: permissions missing");
//            return null;
//        }
//
//        if (!isAvailable()) {
//
//            init();
//        }
//
//        // 获取最新Location
//        Location location = null;
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//            try {
//
//                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                if (location == null) {
//
//                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                }
//            } catch (SecurityException e) {
//
//                e.printStackTrace();
//            }
//        }
//
//        return location;
//    }

    /**
     * location服务订阅者
     *
     * @param name       订阅者名字
     * @param subscriber 订阅者
     */
    public void subscribe(@NonNull String name, @NonNull LocationSubscriber subscriber) {

        if (TextUtils.isEmpty(name)) {

            throw new RuntimeException("subscribe need a name");
        }
        if (subscriber == null) {

            throw new RuntimeException("subscribe need a LocationSubscriber");
        }

        unSubscribe(name);

        locationSubscriberMap.put(name, subscriber);
    }

    /**
     * 取消订阅
     *
     * @param name 订阅者名
     */
    public void unSubscribe(@NonNull String name) {

        if (locationSubscriberMap.containsKey(name)) {

            locationSubscriberMap.remove(name);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        if (this.currentLocation == null || location.getTime() > this.currentLocation.getTime()) {

            this.currentLocation = location;

            for (Map.Entry<String, LocationSubscriber> entry : locationSubscriberMap.entrySet()) {

                entry.getValue().onLocationUpdate(location);
            }
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

        if (LocationManager.GPS_PROVIDER.equals(s)) {

            isEnable = (i == LocationProvider.AVAILABLE);

            if (isEnable) {

                Logger.d(Tags.LOCATION, "gps_provider AVAILABLE");
            } else {

                Logger.w(Tags.LOCATION, "gps_provider OUT_OF_SERVICE");
            }

            for (Map.Entry<String, LocationSubscriber> entry : locationSubscriberMap.entrySet()) {

                entry.getValue().onStateChange(isEnable);
            }
        } else if (LocationManager.NETWORK_PROVIDER.equals(s)) {

            if (i == LocationProvider.AVAILABLE) {

                Logger.d(Tags.LOCATION, "network_provider AVAILABLE");
            } else {

                Logger.w(Tags.LOCATION, "network_provider OUT_OF_SERVICE");
            }
        }
    }

    @Override
    public void onProviderEnabled(String s) {

        if (LocationManager.GPS_PROVIDER.equals(s)) {

            Logger.d(Tags.LOCATION, "gps_provider enabled");

            isEnable = true;

            for (Map.Entry<String, LocationSubscriber> entry : locationSubscriberMap.entrySet()) {

                entry.getValue().onStateChange(isEnable);
            }
        } else if (LocationManager.NETWORK_PROVIDER.equals(s)) {

            Logger.d(Tags.LOCATION, "network_provider enabled");
        }
    }

    @Override
    public void onProviderDisabled(String s) {

        if (LocationManager.GPS_PROVIDER.equals(s)) {

            Logger.w(Tags.LOCATION, "gps_provider disabled");

            isEnable = false;

            for (Map.Entry<String, LocationSubscriber> entry : locationSubscriberMap.entrySet()) {

                entry.getValue().onStateChange(isEnable);
            }
        } else if (LocationManager.NETWORK_PROVIDER.equals(s)) {

            Logger.w(Tags.LOCATION, "network_provider disabled");
        }
    }
}
