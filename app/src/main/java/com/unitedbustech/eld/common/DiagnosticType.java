package com.unitedbustech.eld.common;

/**
 * @author zhangyu
 * @date 2018/2/11
 * @description DiagnosticType
 */
public enum DiagnosticType {

    POWER_DATA("1"),
    ENGINE_SYNCHRONIZATION("2"),
    MISSING_REQUIRED_DATA_ELEMENTS_DATA("3"),
    DATA_TRANSFER_DATA("4"),
    UNIDENTIFIED_DRIVING_RECORDS_DATA("5"),
    OTHER_DIAGNOSTIC("6");

    private String code;

    DiagnosticType(String code) {

        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static DiagnosticType getEnumByCode(String code) {

        for (DiagnosticType diagnosticType : DiagnosticType.values()) {

            if (diagnosticType.getCode().equals(code)) {

                return diagnosticType;
            }
        }
        return null;
    }
}
