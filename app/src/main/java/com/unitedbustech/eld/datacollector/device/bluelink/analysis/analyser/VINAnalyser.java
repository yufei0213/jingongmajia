package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import com.unitedbustech.eld.datacollector.device.bluelink.analysis.IAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.HexUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/1/6
 * @description VINAnalyser 数据的解析器
 */
public class VINAnalyser implements IAnalyser {

    private final static String TAG = "VINAnalyser";

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

            Logger.w(Tags.ECM, "VINAnalyser Data is Invalid");
            return resultData;
        }

        this.originData = originData;

        resultData.put(ReadDataKey.vin, getVin());

        return resultData;
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        return !(originData == null || originData.length == 0);
    }

    /**
     * 获取VIN
     *
     * @return VIN
     */
    private String getVin() {

        String result = HexUtil.hexConvertToString(originData);
        if (result == null || !result.contains("*")) {

            return "";
        }
        String[] vin = result.split("\\*");
        if (vin.length < 2 || vin[0].length() < 1) {

            return "";
        }

        return vin[0];
    }

}
