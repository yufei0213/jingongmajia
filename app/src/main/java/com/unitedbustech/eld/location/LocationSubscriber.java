package com.unitedbustech.eld.location;

import android.location.Location;

/**
 * @author yufei0213
 * @date 2018/1/12
 * @description GPS订阅者
 */
public interface LocationSubscriber {

    /**
     * GPS更新
     *
     * @param location Location
     */
    void onLocationUpdate(Location location);

    /**
     * 状态变化监听
     *
     * @param state 是否可用
     */
    void onStateChange(boolean state);
}
