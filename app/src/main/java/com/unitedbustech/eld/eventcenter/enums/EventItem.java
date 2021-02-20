package com.unitedbustech.eld.eventcenter.enums;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 事件类型
 */
public enum EventItem {
    /**
     * 司机工作状态改变。
     */
    DRIVER_WORK_STATE(1),
    /**
     * 特殊工作状态
     */
    SPECIAL_WORK_STATE(3),
    /**
     * 引擎状态改变
     */
    ENGINE_STATE(6),
    /**
     * 登录状态改变
     */
    LOGIN_STATE(5),
    /**
     * 中间日志
     */
    INTERMEDIATE(2),
    /**
     * 签名
     */
    SIGN(4),
    /**
     * 自检
     */
    SELF_CHECK(7),
    /**
     * 不利条件驾驶
     */
    ADVERSE_DRIVING(101),
    /**
     * 豁免模式事件
     */
    EXEMPTION_MODE(102),
    /**
     * 规则改变
     */
    RULE_CHANGE(103);

    /**
     * 对应的type
     */
    private int code;

    EventItem(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 根据code判断是否是工作状态事件
     *
     * @param code code
     * @return 是否是司机事件
     */
    public static boolean isDriverEvent(int code) {

        if (code == DRIVER_WORK_STATE.code || code == SPECIAL_WORK_STATE.code) {

            return true;
        }

        return false;
    }

    /**
     * 根据code生成EventItem
     *
     * @param code code
     * @return EventItem
     */
    public static EventItem getEventItemByCode(int code) {

        for (EventItem eventItem : EventItem.values()) {

            if (eventItem.code == code) {

                return eventItem;
            }
        }
        return null;
    }
}
