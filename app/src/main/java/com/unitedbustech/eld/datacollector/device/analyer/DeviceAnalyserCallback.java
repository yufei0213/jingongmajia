package com.unitedbustech.eld.datacollector.device.analyer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 硬件数据分析器回到
 */
public interface DeviceAnalyserCallback {

    /**
     * 分析完成
     *
     * @param item  数据项
     * @param model 数据模型
     * @param state 状态
     */
    void onAnalysisFinish(@NonNull VehicleDataItem item, @Nullable VehicleDataModel model, boolean state);

    /**
     * 循环读取
     *
     * @param uuid 本次分析的uuid
     */
    void onCyclicReadingFinish(String uuid);
}
