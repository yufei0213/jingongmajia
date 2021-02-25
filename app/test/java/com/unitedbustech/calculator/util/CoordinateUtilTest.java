package com.unitedbustech.calculator.util;

import org.junit.Test;

/**
 * @author yufei0213
 * @date 2018/4/11
 * @description TODO
 */
public class CoordinateUtilTest {
    @Test
    public void getDistance() throws Exception {

        double distance = CoordinateUtil.getDistance(39.1431913,-77.21929958,39.14329432,-77.21929825);
        System.out.println(distance);
    }

}