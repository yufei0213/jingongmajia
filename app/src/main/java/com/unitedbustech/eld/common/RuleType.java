package com.unitedbustech.eld.common;

/**
 * @author liuzhe
 * @date 2018/7/20
 * @description 记录规则类型
 */
public final class RuleType {

    /**
     * 客车 60 hours 7 days
     */
    public final static int CAR_7D_60H = 1;

    /**
     * 客车 70 hours 8 days
     */
    public final static int CAR_8D_70H = 2;

    /**
     * 卡车 60 hours 7 days
     */
    public final static int TRUCK_7D_60H = 3;

    /**
     * 卡车 70 hours 8 days
     */
    public final static int TRUCK_8D_70H = 4;

    /**
     * 卡车 60 hours 7 days 34hours重置
     */
    public final static int TRUCK_RESER_34H_7D_60H = 5;

    /**
     * 卡车 70 hours 8 days 34hours重置
     */
    public final static int TRUCK_RESER_34H_8D_70H = 6;

    /**
     * 加拿大 70 hours 7 days
     */
    public final static int CANADA_7D_70H = 7;

    /**
     * 加拿大 120 hours 14 days
     */
    public final static int CANADA_14D_120H = 8;
}