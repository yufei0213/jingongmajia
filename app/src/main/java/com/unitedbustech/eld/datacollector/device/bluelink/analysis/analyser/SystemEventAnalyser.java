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

public class SystemEventAnalyser implements IAnalyser {

    private final static String TAG = "SystemEventAnalyser";
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

            Log.d(TAG, "State Data is Invalid");
            return resultData;
        }

        resultData.put(ReadDataKey.newHistory, getIsNewHistory());

        return resultData;
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        if (originData.length > 2) {
            return false;
        }
        return true;
    }

    /**
     * 计算状态
     */
    private String getIsNewHistory() {
        String hex = originData[0];
        Integer result = HexUtil.hexConvertToInteger(hex);
        //根据解析规则，第三四位为1则为历史数据，必然大于或等于4
        if (result < 4) {

            return null;
        } else {
            return "New Record!";
        }
    }

    private String getOriginData() {
        String result = "";
        for (String str : originData) {

            result += str;
        }
        return result;
    }

}
