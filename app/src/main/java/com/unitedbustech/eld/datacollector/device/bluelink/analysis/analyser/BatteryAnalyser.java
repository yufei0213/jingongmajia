package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import com.unitedbustech.eld.datacollector.device.bluelink.analysis.IAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.util.HexUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyu on 2018/1/6.
 * CombinedGroupData 数据的解析器
 */

public class BatteryAnalyser implements IAnalyser {

    private final static String TAG = "BatteryAnalyser";
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

            Logger.d("BatteryAnalyser Data is Invalid");
            return resultData;
        }

        resultData.put(ReadDataKey.vehicleBatteryPotential, getVehicleBatteryPotential());
        resultData.put(ReadDataKey.netBatteryCurrent, getNetBatteryCurrent());
        resultData.put(ReadDataKey.engineCoolantTemperature, getEngineCoolantTemperature());
        resultData.put(ReadDataKey.engineCoolantLevel, getEngineCoolantLevel());
        resultData.put(ReadDataKey.engineOiltemperature, getEngineOiltemperature());
        resultData.put(ReadDataKey.engineOillevel, getEngineOillevel());
        resultData.put(ReadDataKey.transmissionOilTemperature, getTransmissionOilTemperature());
        resultData.put(ReadDataKey.transmissionOillevel, getTransmissionOillevel());
        resultData.put(ReadDataKey.blueLinkExternalPowerLevel, getBlueLinkExternalPowerLevel());

        return resultData;
    }


    private String getVehicleBatteryPotential() {
        String hex = originData[1] + originData[0];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) * 0.05 + " V";
    }

    private String getNetBatteryCurrent() {
        String hex = originData[2];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        if (HexUtil.hexConvertToLong(hex) > 0) {
            return "the current flows out of the battery";
        } else {
            return "the current flows into the battery";
        }
    }

    private String getEngineCoolantTemperature() {
        String hex = originData[3];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) - 40 + " °C";
    }

    private String getEngineCoolantLevel() {
        String hex = originData[4];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) + 0.4 + " %";
    }

    private String getEngineOiltemperature() {
        String hex = originData[6] + originData[5];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) / 32.0 - 273 + " °C";
    }

    private String getEngineOillevel() {
        String hex = originData[7];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) + 0.4 + " %";
    }

    private String getTransmissionOilTemperature() {
        String hex = originData[9] + originData[8];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) / 32.0 - 273 + " °C";
    }

    private String getTransmissionOillevel() {
        String hex = originData[10];
        if (hex.toUpperCase().equals("FF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) + 0.4 + " %";
    }

    private String getBlueLinkExternalPowerLevel() {
        String hex = originData[12] + originData[11];
        if (hex.toUpperCase().equals("FFFF")) {
            return null;
        }
        return HexUtil.hexConvertToLong(hex) + 0.1 + " V";
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        if (originData.length < 11) {
            return false;
        }
        return true;
    }


    private String getOriginData() {
        String result = "";
        for (String str : originData) {

            result += str;
        }
        return result;
    }

}
