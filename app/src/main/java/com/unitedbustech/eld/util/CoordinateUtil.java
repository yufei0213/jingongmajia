package com.unitedbustech.eld.util;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description 经纬度计算工具类
 */
public class CoordinateUtil {

    /**
     * 计算两坐标之间的距离  单位米
     *
     * @param lat1 纬度
     * @param lng1 经度
     * @param lat2 纬度
     * @param lng2 经度
     * @return 距离
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {

        double radLat1 = lat1 * Math.PI / 180.0;
        double radLat2 = lat2 * Math.PI / 180.0;
        double a = radLat1 - radLat2;
        double b = lng1 * Math.PI / 180.0 - lng2 * Math.PI / 180.0;

        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137d;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 根据一个坐标点和半径，计算出该范围内最大最小坐标点
     *
     * @param lat    纬度
     * @param lng    经度
     * @param radius 范围
     * @return 坐标点数据
     */
    public static double[] getAround(double lat, double lng, double radius) {

        double degree = (24901 * 1609) / 360.0;

        double dpmLat = 1 / degree;
        double radiusLat = dpmLat * radius;
        double minLat = lat - radiusLat;
        double maxLat = lat + radiusLat;

        double mpdLng = degree * Math.cos(lat * (Math.PI / 180));
        double dpmLng = 1 / mpdLng;
        double radiusLng = dpmLng * radius;
        double minLng = lng - radiusLng;
        double maxLng = lng + radiusLng;

        return new double[]{minLat, minLng, maxLat, maxLng};
    }

    /**
     * 判断某个坐标点是否在指定范围内
     *
     * @param lat    纬度
     * @param lng    经度
     * @param around {minLat, minLng, maxLat, maxLng}
     * @return
     */
    public static boolean isInRange(double lat, double lng, double[] around) {

        double minLat = around[0];
        double minLng = around[1];
        double maxLat = around[2];
        double maxLng = around[3];

        boolean flag1 = lat < maxLat && lng > minLng;
        boolean flag2 = lat > minLat && lng < maxLng;

        return flag1 && flag2;
    }

    /**
     * 判断坐标1是否在坐标2的圈内
     *
     * @param lat1   纬度
     * @param lng1   经度
     * @param lat2   纬度
     * @param lng2   经度
     * @param radius 范围
     * @return 是否在范围内
     */
    public static boolean isInRange(double lat1, double lng1, double lat2, double lng2, double radius) {

        double distance = getDistance(lat1, lng1, lat2, lng2);
        return distance <= radius;
    }
}
