package com.unitedbustech.eld.datacollector.common;

/**
 * @author yufei0213
 * @date 2018/6/1
 * @description 设备类型
 */
public enum DeviceType {

    DEVICE_TYPE_BLUELINK(1),
    DEVICE_TYPE_PT30(2);

    private int code;

    DeviceType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
