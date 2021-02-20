package com.unitedbustech.eld.datacollector.device.bluelink.analysis;

import java.util.Map;

/**
 * Created by zhangyu on 2018/1/6.
 * <p>
 * 分析器接口
 */

public interface IAnalyser {

    /**
     * 分析数据生成数据
     *
     * @param originData 原始数据
     * @return 可读数据，key在ReadDataKey里
     */
    Map<String, Object> analysis(String[] originData);
}
