package com.unitedbustech.eld.datacollector.device.analyer;

import android.support.annotation.Nullable;

import com.unitedbustech.eld.common.VehicleDataModel;

/**
 * @author yufei0213
 * @date 2018/1/10
 * @description 硬件数据分析器回到
 */
public interface DeviceEcmInfoCallback {

    /**
     * 分析完成
     *
     * @param model 数据模型
     * @param state 状态
     */
    void onReadSuccess(@Nullable VehicleDataModel model, boolean state);
}
