package com.unitedbustech.eld.eventbus;

/**
 * @author zhangyu
 * @date 2018/1/31
 * @description 用于EventBus发送消息。
 * 当网络状态发生变化时的事件。
 */
public class NetworkChangeEvent {

    /**
     * 切换的新状态
     */
    private boolean connected;

    public NetworkChangeEvent() {
    }

    public NetworkChangeEvent(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
