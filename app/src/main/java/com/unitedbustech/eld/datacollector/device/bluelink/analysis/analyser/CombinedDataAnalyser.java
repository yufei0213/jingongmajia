package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import com.unitedbustech.eld.datacollector.device.bluelink.State;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.IAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.HexUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/1/6
 * @description CombinedGroupData 数据的解析器
 */
public class CombinedDataAnalyser implements IAnalyser {

    private final static String TAG = "CombinedDataAnalyser";

    private String[] originData;

    /**
     * 分析原始数据
     *
     * @param originData 原始数据
     * @return 整理后的数据
     */
    @Override
    public Map<String, Object> analysis(String[] originData) {

        Map<String, Object> resultData = new HashMap<>();
        //验证数据是否合法
        if (!validateOriginData(originData)) {

            Logger.w(Tags.ECM, "CombinedDataAnalyser Data is Invalid");
            return resultData;
        }

        this.originData = originData;

        resultData.put(ReadDataKey.speed, getVehicleSpeed());
        resultData.put(ReadDataKey.odometer, getOdometer());
//        resultData.put(ReadDataKey.totalFuelUsed, getFuelUsed());
//        resultData.put(ReadDataKey.primaryFuelLevel, getPrimaryFuelLevel());
//        resultData.put(ReadDataKey.secondaryFuelLevel, getSecondaryFuelLevel());
//        resultData.put(ReadDataKey.engineLoad, getEngineLoad());
//        resultData.put(ReadDataKey.engineOil, getEngineOil());
        resultData.put(ReadDataKey.engineHours, getEngineHours());
        resultData.put(ReadDataKey.state, getState());
//        resultData.put(ReadDataKey.engineOnTime, getEngineOnTime());
        resultData.put(ReadDataKey.rpm, getRpm());
        resultData.put(ReadDataKey.latitude, getLatitude());
        resultData.put(ReadDataKey.longitude, getLongitude());
        return resultData;
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        return !(originData == null || originData.length < 35);
    }

    /**
     * 计算发动机速度
     */
    private double getVehicleSpeed() {

        String hex = originData[3] + originData[2];
        if (hex.toUpperCase().equals("FFFF")) {

            return 0d;
        }
        try {

            double result = HexUtil.hexConvertToInteger(hex) / 256.0;
            return result;
        } catch (Exception e) {

            return 0d;
        }
    }

    /**
     * 计算总里程
     */
    private double getOdometer() {

        String hex = originData[7] + originData[6] + originData[5] + originData[4];

        Logger.i(Tags.ECM_ORIGIN, "odometer origin: " + hex);

        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return ConvertUtil.decimal2Point(0d);
        }
        try {

            double result = HexUtil.hexConvertToLong(hex) * 0.003106856;
            result = ConvertUtil.decimal2Point(result);
            return result;
        } catch (Exception e) {

            return ConvertUtil.decimal2Point(0d);
        }
    }

    /**
     * 计算油用量
     */
    private String getFuelUsed() {

        String hex = originData[11] + originData[10] + originData[9] + originData[8];
        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return "";
        }
        try {

            return String.format("%.2f", HexUtil.hexConvertToInteger(hex) * 0.001) + " L";
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 计算主油量
     */
    private String getPrimaryFuelLevel() {

        String hex = originData[12];
        if (hex.toUpperCase().equals("FF")) {

            return "";
        }
        try {

            return String.format("%.2f", HexUtil.hexConvertToInteger(hex) * 0.4) + " %";
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 计算次油量
     */
    private String getSecondaryFuelLevel() {

        String hex = originData[13];
        if (hex.toUpperCase().equals("FF")) {

            return "";
        }
        try {

            return String.format("%.2f", HexUtil.hexConvertToInteger(hex) * 0.4) + " %";
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 计算引擎Load
     */
    private String getEngineLoad() {

        String hex = originData[14];
        if (hex.toUpperCase().equals("FF")) {

            return "";
        }
        try {

            return HexUtil.hexConvertToInteger(hex) + " %";
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 计算油压
     */
    private String getEngineOil() {

        String hex = originData[15];
        if (hex.toUpperCase().equals("FF")) {

            return "";
        }
        try {

            return HexUtil.hexConvertToInteger(hex) * 4 + " kPa";
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 计算引擎时间
     */
    private double getEngineHours() {

        String hex = originData[19] + originData[18] + originData[17] + originData[16];

        Logger.i(Tags.ECM_ORIGIN, "engineHours origin: " + hex);

        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return ConvertUtil.decimal2Point(0d);
        }
        try {

            long second = HexUtil.hexConvertToLong(hex);
            double result = second / (60 * 60d);
            result = ConvertUtil.decimal2Point(result);
            return result;
        } catch (Exception e) {

            return ConvertUtil.decimal2Point(0d);
        }
    }

    /**
     * 计算车辆状态
     */
    private String getState() {

        String hex = originData[20];
        if (hex.toUpperCase().equals("FF")) {

            return "";
        }
        try {

            int result = HexUtil.hexConvertToInteger(hex);
            switch (result) {

                case 0: {

                    return State.OFF;
                }
                case 1: {

                    return State.ON;
                }
                case 2: {

                    return State.MOVE;
                }
                case 3: {

                    return State.STOP;
                }
                default: {

                    return "";
                }
            }
        } catch (Exception e) {

            return "";
        }
    }

    /**
     * 计算引擎时间
     */
    private String getEngineOnTime() {

        String hex = originData[24] + originData[23] + originData[22] + originData[21];
        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return "";
        }
        Long utcSeconde = HexUtil.hexConvertToLong(hex);
        Date date = new Date(utcSeconde * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 计算引擎速度
     */
    private double getRpm() {

        String hex = originData[26] + originData[25];
        if (hex.toUpperCase().equals("FFFF")) {

            return 0d;
        }
        try {

            double result = HexUtil.hexConvertToInteger(hex) * 0.25;
            return result;
        } catch (Exception e) {

            return 0d;
        }
    }

    /**
     * 计算纬度
     */
    private double getLatitude() {

        String hex = originData[30] + originData[29] + originData[28] + originData[27];
        if (hex.toUpperCase().equals("80000000")) {

            return 0d;
        }
        try {

            double result = HexUtil.hexToSignedInt(hex) / 600000.0;
            return result;
        } catch (Exception e) {

            return 0d;
        }
    }

    /**
     * 计算经度
     */
    private double getLongitude() {

        String hex = originData[34] + originData[33] + originData[32] + originData[31];
        if (hex.toUpperCase().equals("80000000")) {

            return 0d;
        }
        try {

            double result = HexUtil.hexToSignedInt(hex) / 600000.0;
            return result;
        } catch (Exception e) {

            return 0d;
        }
    }
}
