package com.unitedbustech.eld.datacollector.device.bluelink.analysis.analyser;

import com.unitedbustech.eld.util.HexUtil;

import org.junit.Test;

/**
 * @author yufei0213
 * @date 2018/4/25
 * @description TODO
 */
public class CombinedDataAnalyserTest {
    @Test
    public void analysis() throws Exception {

        String hex = "1C053868";

        double result = HexUtil.hexConvertToLong(hex) * 0.003106856;
        System.out.println(result);
    }

}