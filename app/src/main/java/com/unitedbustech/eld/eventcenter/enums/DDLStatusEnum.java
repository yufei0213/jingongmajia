package com.unitedbustech.eld.eventcenter.enums;

/**
 * @author : don
 * @date : 2018/1/12
 * @description : 事件状态变化
 */
public enum DDLStatusEnum {

    ACTIVE(1, "Active"),
    INACTIVE_CHANGED(2, "Inactive - Changed"),
    INACTIVE_CHANGE_REQUESTED(3, "Inactive - Change Requested"),
    INACTIVE_CHANGE_REJECTED(4, "Inactive - Change Rejected");

    private int code;
    private String status;

    DDLStatusEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public static DDLStatusEnum parse(Integer code) {

        if (code != null) {
            for (DDLStatusEnum statusEnum : DDLStatusEnum.values()) {
                if (statusEnum.code == code) {
                    return statusEnum;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
