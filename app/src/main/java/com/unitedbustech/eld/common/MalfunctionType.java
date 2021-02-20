package com.unitedbustech.eld.common;

/**
 * @author zhangyu
 * @date 2018/2/11
 * @description MalfunctionType
 */
public enum MalfunctionType {

    POWER_COMPLIANCE("P"),
    ENGINE_SYNCHRONIZATION_COMPLIANCE("E"),
    TIMING_COMPLIANCE("T"),
    POSITIONING_COMPLIANCE("L"),
    DATA_RECORDING_COMPLIANCE("R"),
    DATA_TRANSFER_COMPLIANCE("S"),
    OTHER_ELD_DETECT("O");

    private String code;

    MalfunctionType(String code) {

        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static MalfunctionType getEnumByCode(String code) {

        for (MalfunctionType malfunctionType : MalfunctionType.values()) {

            if (malfunctionType.getCode().equals(code)) {

                return malfunctionType;
            }
        }
        return null;
    }
}
