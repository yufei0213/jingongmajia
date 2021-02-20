package com.unitedbustech.eld.request;

/**
 * @author zhangyu
 * @date 2018/3/23
 * @description RequestType
 */
public enum RequestType {

    OTHERS(1),
    UNIDENTIFIED(2),
    DAILYLOG(3),
    GPS(4),
    IFTA(5),
    RULE(6),
    ORIGINALHISROEY(7);

    public int code;

    RequestType(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static int getCodeByType(RequestType requestType) {

        for (RequestType type : RequestType.values()) {

            if (requestType == type) {

                return type.code;
            }
        }
        return 0;
    }
}
