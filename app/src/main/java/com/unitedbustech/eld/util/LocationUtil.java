package com.unitedbustech.eld.util;

import android.location.Location;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.system.SystemHelper;

/**
 * @author zhangyu
 * @date 2018/1/25
 * @description 位置有关的工具类
 */
public class LocationUtil {

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static String getGeolocation(String lat, String lng) {

        User user = SystemHelper.getUser();
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {

            return "";
        }

        HttpRequest HttpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_GEO_LOCATION)
                .addParam("access_token", user.getAccessToken())
                .addParam("latitude", lat)
                .addParam("longitude", lng)
                .build();

        HttpResponse httpResponse = HttpRequest.get();
        if (httpResponse.isSuccess()) {

            String data = httpResponse.getData();

            JSONObject object = JsonUtil.parseObject(data);

            return JsonUtil.getString(object, "geoLocation");
        } else {
            return "";
        }
    }

    /**
     * 离线转在线情况下检查地址
     *
     * @param lat
     * @param lng
     * @return
     */
    public static String getGeolocationOffline(String lat, String lng) {

        User user = SystemHelper.getUser();
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) {

            return "";
        }

        HttpRequest HttpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_GEO_LOCATION)
                .addParam("access_token", user.getAccessToken())
                .addParam("latitude", lat)
                .addParam("longitude", lng)
                .ignore(RequestStatus.AUTHENTICATION_FAIL.getCode())
                .build();

        HttpResponse httpResponse = HttpRequest.get();
        if (httpResponse.isSuccess()) {

            String data = httpResponse.getData();

            JSONObject object = JsonUtil.parseObject(data);

            return JsonUtil.getString(object, "geoLocation");
        } else {
            return "";
        }
    }

    public static String getGeolocation(Location location) {

        User user = SystemHelper.getUser();
        if (location == null) {

            return "";
        }
        HttpRequest HttpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_GEO_LOCATION)
                .addParam("access_token", user.getAccessToken())
                .addParam("latitude", String.valueOf(location.getLatitude()))
                .addParam("longitude", String.valueOf(location.getLongitude()))
                .build();

        HttpResponse httpResponse = HttpRequest.get();
        if (httpResponse.isSuccess()) {

            String data = httpResponse.getData();

            JSONObject object = JsonUtil.parseObject(data);

            return JsonUtil.getString(object, "geoLocation");
        } else {

            return "";
        }
    }

    public static String getGeoState(Location location) {

        User user = SystemHelper.getUser();
        if (location == null) {

            return "";
        }
        HttpRequest HttpRequest = new HttpRequest.Builder()
                .url(Constants.API_GET_GEO_LOCATION)
                .addParam("access_token", user.getAccessToken())
                .addParam("latitude", String.valueOf(location.getLatitude()))
                .addParam("longitude", String.valueOf(location.getLongitude()))
                .build();

        HttpResponse httpResponse = HttpRequest.get();
        if (httpResponse.isSuccess()) {

            String data = httpResponse.getData();

            JSONObject object = JsonUtil.parseObject(data);

            return JsonUtil.getString(object, "state");
        } else {
            return "";
        }
    }

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    public static boolean isBetterLocation(Location location, Location currentBestLocation) {

        if (location == null) {
            return false;
        }

        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
