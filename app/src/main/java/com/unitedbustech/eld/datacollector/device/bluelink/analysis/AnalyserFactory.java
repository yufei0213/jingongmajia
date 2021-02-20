package com.unitedbustech.eld.datacollector.device.bluelink.analysis;

import com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser.CombinedDataAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser.HistoryAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser.StateAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser.VINAnalyser;
import com.unitedbustech.eld.datacollector.device.bluelink.uuid.DataUUID;

/**
 * @author zhangyu
 * @date 2018/1/6
 * @description 分析器工厂，根据UUID产生分析器进行分析
 */
public class AnalyserFactory {

    private static StateAnalyser stateAnalyser;
    private static CombinedDataAnalyser combinedDataAnalyser;
    private static VINAnalyser vinAnalyser;
    private static HistoryAnalyser historyAnalyser;

    /**
     * 根据UUID生产分析器，目前只有一种设备，暂时不去实现其他的设备
     *
     * @param uuid characteristic的UUID
     * @return 分析器
     */
    public static IAnalyser getAnalyser(String uuid) {

        if (uuid.equals(DataUUID.STATE.getUuid())) {

            if (stateAnalyser == null) {

                stateAnalyser = new StateAnalyser();
            }
            return stateAnalyser;
        }
        if (uuid.equals(DataUUID.COMBINED.getUuid())) {

            if (combinedDataAnalyser == null) {

                combinedDataAnalyser = new CombinedDataAnalyser();
            }
            return combinedDataAnalyser;
        }
        if (uuid.equals(DataUUID.VIN.getUuid())) {

            if (vinAnalyser == null) {

                vinAnalyser = new VINAnalyser();
            }
            return vinAnalyser;
        }
        if (uuid.equals(DataUUID.HISTORY.getUuid())) {

            if (historyAnalyser == null) {

                historyAnalyser = new HistoryAnalyser();
            }
            return historyAnalyser;
        }

        return null;
    }
}
