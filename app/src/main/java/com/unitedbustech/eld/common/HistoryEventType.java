package com.unitedbustech.eld.common;

/**
 * @author lzh
 * @date 2019/10/29
 * @description 历史事件类型
 */
public enum HistoryEventType {

    POWER_ON(1),
    POWER_OFF(2),
    MOVE(3),
    STOP(4),
    OTHER(5);

    private int code;

    HistoryEventType(int coce) {
        this.code = coce;
    }

    public int getCode() {

        return code;
    }
}
