package com.unitedbustech.eld.eventbus;

/**
 * @author yufei0213
 * @date 2018/2/3
 * @description 车辆连接状态消息
 */
public class VehicleConnectEvent {

    private int type;

    public VehicleConnectEvent() {
    }

    public VehicleConnectEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
