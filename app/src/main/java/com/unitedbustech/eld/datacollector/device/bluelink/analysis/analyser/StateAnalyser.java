package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import com.unitedbustech.eld.datacollector.device.bluelink.State;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.IAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.HexUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/1/6
 * @description StateAnalyser 数据的解析器
 */
public class StateAnalyser implements IAnalyser {

    private final static String TAG = "StateAnalyser";

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

            Logger.w(Tags.ECM, "State Data is Invalid");
            return resultData;
        }

        this.originData = originData;

        resultData.put(ReadDataKey.state, getState());
//        resultData.put(ReadDataKey.reportTime, getTime());
        return resultData;
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        return !(originData == null || originData.length < 5);
    }

    /**
     * 计算状态
     */
    private String getState() {

        String hex = originData[0];
        if (hex.toUpperCase().equals("FF")) {

            return "";
        }
        try {

            Integer result = HexUtil.hexConvertToInteger(hex);
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
     * 计算时间
     */
    private String getTime() {

        String hex = originData[4] + originData[3] + originData[2] + originData[1];
        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return "";
        }
        Long utcSeconde = HexUtil.hexConvertToLong(hex);
        Date date = new Date(utcSeconde * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);
        return dateString;
    }
}
