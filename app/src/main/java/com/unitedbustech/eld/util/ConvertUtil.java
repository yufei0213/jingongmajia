package com.unitedbustech.eld.util;

import java.math.BigDecimal;

/**
 * @author yufei0213
 * @date 2018/1/13
 * @description 单位转换工具类
 */
public class ConvertUtil {

    /**
     * 英里转千米比例
     */
    public static final double MILE_M_RADIO = 1.609344d;

    /**
     * 英里每小时转千米每小时
     *
     * @param speed mile/h
     * @return km/h
     */
    public static double mph2kh(double speed) {

        return speed * MILE_M_RADIO;
    }

    /**
     * 千米每小时转英里每小时
     *
     * @param speed km/h
     * @return mile/h
     */
    public static double kh2mph(double speed) {

        return speed / MILE_M_RADIO;
    }

    /**
     * 英里每小时转米每秒
     *
     * @param speed mile/h
     * @return m/s
     */
    public static double mph2ms(double speed) {

        return speed * MILE_M_RADIO / 1000d / 3600d;
    }

    /**
     * 米每秒转英里每小时
     *
     * @param speed m/s
     * @return mile/h
     */
    public static double ms2mph(double speed) {

        return speed * 3600d * 1000d / MILE_M_RADIO;
    }

    /**
     * 英里转米
     *
     * @param mile 英里
     * @return 米
     */
    public static double mile2m(double mile) {

        return mile * MILE_M_RADIO * 1000d;
    }

    /**
     * 米转英里
     *
     * @param m 米
     * @return 英里
     */
    public static double m2mile(double m) {

        return (m / 1000) / MILE_M_RADIO;
    }

    /**
     * 英里转千米
     *
     * @param mile 英里
     * @return 千米
     */
    public static double mile2km(double mile) {

        return mile * MILE_M_RADIO;
    }

    /**
     * 千米转英里
     *
     * @param km 千米
     * @return 英里
     */
    public static double km2mile(double km) {

        return km / MILE_M_RADIO;
    }


    /**
     * 毫秒数转分钟数
     *
     * @param millsSecond 毫秒数
     * @return 分钟数
     */
    public static double mill2Minute(long millsSecond) {

        return (millsSecond / 1000d) / 60d;
    }

    /**
     * 分钟数转毫秒数
     *
     * @param minutes 分钟数
     * @return 毫秒数
     */
    public static long minute2Mill(double minutes) {

        return (long) (minutes * 60l * 1000l);
    }

    /**
     * 小时数转分钟数
     *
     * @param hours 小时数
     * @return 分钟数
     */
    public static double hour2Minute(double hours) {

        return hours * 60d;
    }

    /**
     * 分钟数转小时数
     *
     * @param minutes 分钟数
     * @return 小时数
     */
    public static double minute2Hour(double minutes) {

        return minutes / 60d;
    }

    /**
     * double保留到小数据点后两位
     *
     * @param value 原始值
     * @return 格式化后的值
     */
    public static double decimal2Point(double value) {

        return new BigDecimal(value)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * double保留到小数据点后三位
     *
     * @param value 原始值
     * @return 格式化后的值
     */
    public static double decimal3Point(double value) {

        return new BigDecimal(value)
                .setScale(3, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * double保留到小数据点后一位
     *
     * @param value 原始值
     * @return 格式化后的值
     */
    public static double decimal1Point(double value) {

        return new BigDecimal(value)
                .setScale(1, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }

    /**
     * 转化double类型
     * <p>
     * 结果是3.0就显示只显示3，结果是3.1就显示3.1
     *
     * @param d 数值
     * @return 转换后的值
     */
    public static String doubleTrans(double d) {

        if (Math.round(d) - d == 0) {

            return String.valueOf((long) d);
        }

        return String.valueOf(d);
    }
}
