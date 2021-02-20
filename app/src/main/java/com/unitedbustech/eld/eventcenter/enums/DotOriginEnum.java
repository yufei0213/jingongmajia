package com.unitedbustech.eld.eventcenter.enums;


/**
 * @author : mamw
 * @date : 2018/1/27
 * @description : 事件记录来源
 */
public enum DotOriginEnum {

    AUTO_BY_ELD(1, "Auto"),
    EDIT_BY_DRIVER(2, "Driver"),
    EDIT_BY_ADMIN(3, "Fleet Manager"),
    UNIDENTIFIED(4, "Unidentified");

    private int code;
    private String desc;

    DotOriginEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static DotOriginEnum parse(Integer code) {

        if (code != null) {
            for (DotOriginEnum originEnum : DotOriginEnum.values()) {
                if (originEnum.code == code) {
                    return originEnum;
                }
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
