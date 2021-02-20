package com.unitedbustech.eld.datacollector.common;

import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description 采集器回调
 */
public interface CollectorItemCallback {

    /**
     * 采集器状态发生变化
     *
     * @param state 是否可用
     * @param type  采集器类型
     */
    void onItemStateChange(boolean state, CollectorType type);

    /**
     * 某项数据发生变化
     *
     * @param item  数据项
     * @param model 数据
     */
    void onDataItemChange(VehicleDataItem item, VehicleDataModel model);

    /**
     * 周期性推送数据
     *
     * @param model 数据
     */
    void onSchedule(VehicleDataModel model);
}
