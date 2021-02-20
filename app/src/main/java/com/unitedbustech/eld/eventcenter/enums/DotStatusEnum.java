package com.unitedbustech.eld.eventcenter.enums;

/**
 * @author : mamw
 * @date : 2018/1/27
 * @description : 事件记录类型
 */
public enum DotStatusEnum {

    OFF_DUTY(1, 1, "Off Duty"),
    SB(1, 2, "SB"),
    DRIVING(1, 3, "Driving"),
    ODND(1, 4, "ODND"),
    PERSONAL_USE(3, 1, "Personal Use"),
    YARD_MOVE(3, 2, "Yard Move"),
    LOGIN(5, 1, "Login"),
    LOGOUT(5, 2, "Logout"),
    POWER_UP_1(6, 1, "Power Up"),
    POWER_UP_2(6, 2, "Power Up"),
    SHUT_DOWN_1(6, 3, "Shut Down"),
    SHUT_DOWN_2(6, 4, "Shut Down"),
    MALFUNCTION_LOGGED(7, 1, "Malfunction Logged"),
    MALFUNCTION_CLEARED(7, 2, "Malfunction Cleared"),
    DIAGNOSTIC_LOGGED(7, 3, "Diagnostic Logged"),
    DIAGNOSTIC_CLEARED(7, 4, "Diagnostic Cleared"),
    INTERMEDIATE(2, 1, "Intermediate"),
    ADVERSE_DRIVING(101, 0, "Adverse Driving"),
    RULE_7D_60H(103, 1, "Change to USA 60 hour / 7 day"),
    RULE_8D_70H(103, 2, "Change to USA 70 hour / 8 day"),
    CANADA_7D_70H(103, 7, "Change to Canada 70 hour / 7 day"),
    CANADA_14D_120H(103, 8, "Change to Canada 120 hour / 14 day"),
    CERT_1(4, 1, "Cert"),
    CERT_2(4, 2, "Cert"),
    CERT_3(4, 3, "Cert"),
    CERT_4(4, 4, "Cert"),
    CERT_5(4, 5, "Cert"),
    CERT_6(4, 6, "Cert"),
    CERT_7(4, 7, "Cert"),
    CERT_8(4, 8, "Cert"),
    CERT_9(4, 9, "Cert");

    private int type;
    private int code;
    private String desc;

    DotStatusEnum(int type, int code, String desc) {

        this.type = type;
        this.code = code;
        this.desc = desc;
    }

    public static DotStatusEnum parse(Integer type, Integer code) {

        DotStatusEnum originEnum = null;
        if (type == null || code == null) {
            return originEnum;
        }
        if (code != null) {
            for (DotStatusEnum dotStatusEnum : DotStatusEnum.values()) {
                if (dotStatusEnum.type == type && dotStatusEnum.code == code) {
                    originEnum = dotStatusEnum;
                }
            }
        }
        return originEnum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
