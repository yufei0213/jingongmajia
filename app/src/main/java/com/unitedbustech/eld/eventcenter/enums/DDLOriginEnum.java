package com.unitedbustech.eld.eventcenter.enums;


/**
 * @author : don
 * @date : 2018/1/12
 * @description : 事件记录来源
 */
public enum DDLOriginEnum {

    AUTO_BY_ELD(1, "Auto"),
    EDIT_BY_DRIVER(2, "Driver"),
    EDIT_BY_ADMIN(3, "Fleet Manager"),
    UNIDENTIFIED(4, "Unidentified");

    private int code;
    private String origin;

    DDLOriginEnum(int code, String origin) {
        this.code = code;
        this.origin = origin;
    }

    public static DDLOriginEnum parse(Integer code) {

        if (code != null) {
            for (DDLOriginEnum originEnum : DDLOriginEnum.values()) {
                if (originEnum.code == code) {
                    return originEnum;
                }
            }
        }

        return null;
    }

    public static DDLOriginEnum getEnumByCode(int code) {

        for (DDLOriginEnum ddlOriginEnum : DDLOriginEnum.values()) {
            if (code == ddlOriginEnum.getCode()) {
                return ddlOriginEnum;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }
}
