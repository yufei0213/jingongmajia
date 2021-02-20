package com.unitedbustech.eld.eventbus;

/**
 * @author zhangyu
 * @date 2018/2/3
 * @description 车辆自动链接变化事件。
 * 只有自动切换时才发出通知，用于监听车辆的自动状态发生变化、
 */
public class VehicleAutoConnectEvent {

    /**
     * 自动状态发生变化的类型。
     * 对应的值为:
     * VehicleAutoConnectType
     */
    private int type;

    public VehicleAutoConnectEvent() {
    }

    public VehicleAutoConnectEvent(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
