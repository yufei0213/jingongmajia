package com.unitedbustech.eld.datacollector.device.bluelink.uuid;

/**
 * @author yufei0213
 * @date 2018/5/5
 * @description ConfigUUID
 */
public enum ConfigUUID {

    UTC("00000301-86d6-11e5-af63-feff819cdc9f"),
    NOTIFY("00002902-0000-1000-8000-00805f9b34fb");

    private String uuid;

    ConfigUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
