package com.unitedbustech.eld.datacollector;

import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.common.CollectorType;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description 数据采集中心订阅者
 */
public interface DataCollectorSubscriber {

    /**
     * 周期性回调数据
     *
     * @param model 数据
     * @param type  数据源
     */
    void onSchedule(VehicleDataModel model, CollectorType type);

    /**
     * 数据变化时回调数据
     *
     * @param item  数据类型
     * @param model 数据
     * @param type  数据源
     */
    void onDataItemChange(VehicleDataItem item, VehicleDataModel model, CollectorType type);
}
