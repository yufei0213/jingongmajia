package com.unitedbustech.eld.datacollector.device.analyer;

import android.support.annotation.NonNull;

import com.unitedbustech.eld.common.VehicleDataItem;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 硬件数据分析器
 */
public interface DeviceAnalyser {

    /**
     * 读取历史记录
     *
     * @param readHistoryCallback ReadHistoryCallback
     */
    void readHistory(ReadHistoryCallback readHistoryCallback);

    /**
     * 分析某项数据
     *
     * @param isCyclicReading 是否循环读取
     * @param items           数据项
     */
    void analysis(boolean isCyclicReading, String uuid, @NonNull VehicleDataItem... items);

    /**
     * 设置回调
     *
     * @param callback 回调函数
     */
    void setCallback(@NonNull DeviceAnalyserCallback callback);
}
