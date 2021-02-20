package com.unitedbustech.eld.common;

import com.unitedbustech.eld.eventcenter.enums.EventItem;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 描述司机的工作状态。6种状态
 */
public enum DriverState {

    // 四种普通工作状态
    OFF_DUTY(1),
    SLEEPER_BERTH(2),
    DRIVING(3),
    ON_DUTY_NOT_DRIVING(4),

    //两种特殊工作状态
    YARD_MOVE(5),
    PERSONAL_USE(6);

    private int code;

    DriverState(int code) {
        this.code = code;
    }

    /**
     * 给出一个枚举，判断是否是OnDuty
     *
     * @param driverState 被判断的枚举
     * @return 如果是Onduty 返回true，否则返回false
     */
    public static boolean isOnDuty(DriverState driverState) {

        switch (driverState) {

            case DRIVING:
            case ON_DUTY_NOT_DRIVING:
            case YARD_MOVE:
                return true;
            case OFF_DUTY:
            case SLEEPER_BERTH:
            case PERSONAL_USE:
                return false;
        }
        return false;
    }

    /**
     * 判断是否是特殊状态
     *
     * @param driverState 状态
     * @return 是否
     */
    public static boolean isSpecial(DriverState driverState) {

        return driverState.equals(YARD_MOVE) || driverState.equals(PERSONAL_USE);
    }

    /**
     * 判断是否是OffDuty状态
     *
     * @param driverState 被判断的枚举
     * @return 如果是OffDuty 返回true，否则返回false
     */
    public static boolean isOffDuty(DriverState driverState) {

        boolean ret = false;
        if (driverState.equals(OFF_DUTY)) {

            ret = true;
        }
        return ret;
    }

    /**
     * 根据code获取state
     *
     * @param code code
     * @return state
     */
    public static DriverState getStateByCode(int code) {

        for (DriverState driverState : DriverState.values()) {

            if (driverState.code == code) {

                return driverState;
            }
        }

        return DriverState.OFF_DUTY;
    }

    /**
     * 根据type与code获取state
     *
     * @param code code
     * @return state
     */
    public static DriverState getStateByCodeAndType(int type, int code) {

        if (type == EventItem.DRIVER_WORK_STATE.getCode()) {

            for (DriverState driverState : DriverState.values()) {

                if (driverState.code == code) {

                    return driverState;
                }
            }
        } else {

            if (code == 1) {

                return PERSONAL_USE;
            } else if (code == 2) {

                return YARD_MOVE;
            }
        }

        return DriverState.OFF_DUTY;
    }

    public int getCode() {

        return code;
    }

    public String toVisibleString() {

        return this.name();
    }

    @Override
    public String toString() {

        return String.valueOf(code);
    }
}
