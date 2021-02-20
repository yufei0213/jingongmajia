package com.unitedbustech.eld.datacollector.device.analyer;

import com.unitedbustech.eld.datacollector.common.History;

import java.util.List;

/**
 * @author yufei0213
 * @date 2018/2/1
 * @description BlueLink历史记录读取回调
 */
public interface ReadHistoryCallback {

    void onReadFinish(List<History> history);
}
