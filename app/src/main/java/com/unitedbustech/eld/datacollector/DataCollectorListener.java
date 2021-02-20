package com.unitedbustech.eld.datacollector;

import com.unitedbustech.eld.datacollector.common.CollectorType;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description 数据采集中心状态监听
 */
public interface DataCollectorListener {

    /**
     * 数据源变化监听
     *
     * @param type  数据源类型
     * @param state 是否可用
     */
    void onTypeChange(CollectorType type, boolean state);
}
