package com.unitedbustech.eld.datacollector.device.bluelink.uuid;

/**
 * @author yufei0213
 * @date 2018/5/5
 * @description ServiceUUID
 */
public enum ServiceUUID {

    CONFIG("00000000-86d6-11e5-af63-feff819cdc9f"),
    DATA("00000000-cb73-437d-8fad-842c16c7aa6f");

    private String uuid;

    ServiceUUID(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
