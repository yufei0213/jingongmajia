package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import com.unitedbustech.eld.datacollector.device.bluelink.analysis.IAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.ReadDataKey;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.HexUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/1/6
 * @description HistoryAnalyser 数据的解析器
 */
public class HistoryAnalyser implements IAnalyser {

    private final static String TAG = "HistoryAnalyser";

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

            Logger.d(Tags.ECM, "HistoryAnalyser Data is Invalid");
            return resultData;
        }

        this.originData = originData;

        boolean recordState = getRecordState();
        resultData.put(ReadDataKey.hasHistory, recordState);

        if (recordState) {

            resultData.put(ReadDataKey.historyId, getRecordID());
            resultData.put(ReadDataKey.historyType, getRecordType());
            resultData.put(ReadDataKey.historyTime, getHistoryTime());
            resultData.put(ReadDataKey.historyOdometer, ConvertUtil.decimal2Point(getOdometer()));
            resultData.put(ReadDataKey.historyEngineHour, ConvertUtil.decimal2Point(getEngineHours()));
            resultData.put(ReadDataKey.historyLat, getLatitude());
            resultData.put(ReadDataKey.historyLng, getLongitude());
            resultData.put(ReadDataKey.historySpeed, getSpeed());
        }

        return resultData;
    }

    /**
     * 验证参数是否合法
     *
     * @param originData 原始数据
     * @return 是否
     */
    private boolean validateOriginData(String[] originData) {

        return !(originData == null || originData.length < 24);
    }

    /**
     * 判断是否有数据
     */
    private boolean getRecordState() {

        String hex = originData[0];
        if (hex.toUpperCase().equals("00")) {

            return false;
        } else if (hex.toUpperCase().equals("81")) {

            return true;
        }
        return false;
    }

    /**
     * 计算记录的ID
     */
    private int getRecordID() {

        String hex = originData[2] + originData[1];
        if (hex.toUpperCase().equals("0000")) {

            return 0;
        }
        try {

            return HexUtil.hexConvertToInteger(hex);
        } catch (Exception e) {

            return 0;
        }
    }

    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    private int getRecordType() {

        try {

            String hex = originData[3];
            int type = HexUtil.hexConvertToInteger(hex);
            return type;
        } catch (Exception e) {

            return -1;
        }
    }

    /**
     * 计算速度
     */
    private int getSpeed() {

        if (originData.length < 26) {

            return 0;
        }
        try {

            String hex = originData[25];
            return HexUtil.hexConvertToInteger(hex);
        } catch (Exception e) {

            return 0;
        }
    }

    /**
     * 计算总里程
     */
    private double getOdometer() {

        String hex = originData[11] + originData[10] + originData[9] + originData[8];
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
     * 计算引擎时间
     */
    private double getEngineHours() {

        String hex = originData[15] + originData[14] + originData[13] + originData[12];
        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return ConvertUtil.decimal2Point(0d);
        }
        try {

            long second = HexUtil.hexConvertToLong(hex);
            double result = (second / (60 * 60D));
            result = ConvertUtil.decimal2Point(result);
            return result;
        } catch (Exception e) {

            return ConvertUtil.decimal2Point(0d);
        }
    }

    /**
     * 计算事件事件
     */
    private long getHistoryTime() {

        String hex = originData[7] + originData[6] + originData[5] + originData[4];
        if (hex.toUpperCase().equals("FFFFFFFF")) {

            return 0l;
        }
        try {

            long utcSecond = HexUtil.hexConvertToLong(hex);
            return utcSecond * 1000L;
        } catch (Exception e) {

            return 0l;
        }
    }

    /**
     * 计算纬度
     */
    private double getLatitude() {

        String hex = originData[19] + originData[18] + originData[17] + originData[16];
        if (hex.toUpperCase().equals("80000000")) {

            return 0d;
        }
        try {

            return HexUtil.hexToSignedInt(hex) / 600000.0;
        } catch (Exception e) {

            return 0d;
        }
    }

    /**
     * 计算经度
     */
    private double getLongitude() {

        String hex = originData[23] + originData[22] + originData[21] + originData[20];
        if (hex.toUpperCase().equals("80000000")) {

            return 0d;
        }
        try {

            return HexUtil.hexToSignedInt(hex) / 600000.0;
        } catch (Exception e) {

            return 0d;
        }
    }
}
