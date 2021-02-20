package com.unitedbustech.eld.eventcenter.enums;

/**
 * @author yufei0213
 * @date 2018/2/22
 * @description 特殊状况下，取代纬度和经度字段的值
 */
public enum LatLngSpecialEnum {

    /**
     * 司机手动输入地址时，经纬度填写M
     */
    M("M"),
    /**
     * 当获取不到经纬度时，如果还没有生成故障，经纬度填写X
     */
    X("X"),
    /**
     * 当获取不到经纬度时，如果已经生成了故障，经纬度填写E
     */
    E("E");

    private String code;

    LatLngSpecialEnum(String code) {

        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
