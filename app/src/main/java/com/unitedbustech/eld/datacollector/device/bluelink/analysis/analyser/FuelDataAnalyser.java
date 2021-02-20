package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import android.util.Log;

import com.unitedbustech.eld.datacollector.device.bluelink.analysis.IAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.util.HexUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyu on 2018/1/6.
 * CombinedGroupData 数据的解析器
 */

public class FuelDataAnalyser implements IAnalyser {

    private final static String TAG = "FuelDataAnalyser";
    private String[] originData;

    /**
     * 分析原始数据
     *
     * @param originData 原始数据
     * @return 整理后的数据
     */
    @Override
    public Map<String, Object> analysis(String[] originData) {

        this.originData = originData;
        Map<String, Object> resultData = new HashMap<>();
        //验证数据是否合法
        if (!validateOriginData(originData)) {

            Log.d(TAG, "FuelDataAnalyser Data is Invalid");
            return resultData;
        }

        resultData.put(ReadDataKey.totalFuelUsed, getFuelUsed());
        resultData.put(ReadDataKey.primaryFuelLevel, getPrimaryFuelLevel());
        resultData.put(ReadDataKey.secondaryFuelLevel, getSecondaryFuelLevel());
        resultData.put(ReadDataKey.engineAverageFuelEconomy, getAverageFuelEconomy());
        resultData.put(ReadDataKey.totalAmountOfFuelUsedSenderSourceAddress, getSourceAddress());
        resultData.put(ReadDataKey.senderBus, getSenderBus());
        resultData.put(ReadDataKey.instantaneousFuelRate, getInstantaneousFuelRate());
        resultData.put(ReadDataKey.instantaneousFuelEconomy, getInstantaneousFuelEconomy());
        return resultData;
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        if (originData.length < 14) {
            return false;
        }
        return true;
    }

    /**
     * 计算主油量
     */
    private String getPrimaryFuelLevel() {
        String hex = originData[4];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return String.format("%.2f", HexUtil.hexConvertToInteger(hex) * 0.4) + " %";
    }

    /**
     * 计算油用量
     */
    private String getFuelUsed() {
        String hex = originData[3] + originData[2] + originData[1] + originData[0];
        if (hex.toUpperCase().equals("FFFFFFFF")) {
            return null;
        }
        return HexUtil.hexConvertToInteger(hex) * 0.001 + " L";
    }


    /**
     * 计算次油量
     */
    private String getSecondaryFuelLevel() {
        String hex = originData[5];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return String.format("%.2f", HexUtil.hexConvertToInteger(hex) * 0.4) + " %";
    }

    /**
     * 计算平均燃油经济性
     *
     * @return
     */
    private String getAverageFuelEconomy() {

        String hex = originData[7] + originData[6];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) / 512 + " km/L";
    }

    /**
     * 计算来源地址
     */
    private String getSourceAddress() {

        String hex = originData[8];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return String.valueOf(HexUtil.hexConvertToInteger(hex));
    }

    /**
     * 计算来源地址
     */
    private String getSenderBus() {

        String hex = originData[9];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        int value = HexUtil.hexConvertToInteger(hex);
        if (value == 0) {
            return "J1708";
        }
        if (value == 1) {
            return "J1939";
        }
        return null;
    }

    private String getInstantaneousFuelRate() {
        String hex = originData[11] + originData[10];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) * 0.05 + " L/h";
    }

    private String getInstantaneousFuelEconomy() {
        String hex = originData[13] + originData[12];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) / 512 + " km/L";
    }

    private String getOriginData() {
        String result = "";
        for (String str : originData) {

            result += str;
        }
        return result;
    }

}
