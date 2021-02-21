package com.unitedbustech.eld.http;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/15
 * @description Http请求状态码，与服务端保持一致
 */
public enum RequestStatus {

    /**
     * 成功
     */
    SUCCESS(200, R.string.request_status_success),
    /**
     * 未知异常
     */
    UNKNOWN_ERROR(-1, R.string.request_status_unknown_error),
    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(-2, R.string.request_status_request_timeout),
    /**
     * 网络连接异常
     */
    NETWORK_CONNECT_ERROR(-3, R.string.request_status_network_connect_error),
    /**
     * 解析请求结果异常
     */
    PARSE_RESULTS_ERROR(-4, R.string.request_status_parse_result_error),
    /**
     * 未处理异常
     */
    UNTREATED_ERROR(-5, R.string.request_status_untreated_error),

    /**
     * ******************************************************************************
     * <p>
     * 系统异常，错误码范围 10001-10010
     * <p>
     * ******************************************************************************
     */

    /**
     * 系统错误
     */
    SYSTEM_ERROR(10001, R.string.request_status_system_error),
    /**
     * 未找到请求的接口
     */
    API_NOT_FOUND(10002, R.string.request_status_api_not_found),
    /**
     * 非法请求
     */
    ILLEGAL_REQUEST(10003, R.string.request_status_illegal_request),
    /**
     * 认证失败
     */
    AUTHENTICATION_FAIL(10004, R.string.request_status_authentication_fail),
    /**
     * 接口暂未实现
     */
    API_NOT_IMPLEMENT(10005, R.string.request_status_api_not_implement),

    /**
     * ******************************************************************************
     * <p>
     * 通用业务异常，错误码范围 20001-20010
     * <p>
     * ******************************************************************************
     */

    /**
     * 参数处理失败
     */
    PARSE_PARAMETERS_ERROR(20001, R.string.request_status_parse_parameters_error),
    /**
     * 参数验证失败
     */
    VALIDATE_PARAMETERS_ERROR(20002, R.string.request_status_validate_parameters_error),

    /**
     * ******************************************************************************
     * <p>
     * 登录相关错误码，错误码范围 20101 -20199
     * <p>
     * ******************************************************************************
     */

    /**
     * carrier id不合法
     */
    INVALID_CARRIER_ID(20107, R.string.request_status_invalid_carrier_id),
    /**
     * driver id不合法
     */
    INVALID_DRIVER_ID(20101, R.string.request_status_invalid_driver_id),
    /**
     * 密码错误
     */
    INVALID_PASSWORD(20102, R.string.request_status_invalid_password),

    /**
     * 主驾驶已经将组队销毁
     */
    PILOT_HAS_CANCEL_TEAMWORK(40201, R.string.request_status_pilot_has_cancel_teamwork),
    /**
     * 组队不存在
     */
    TEAMWORK_NOT_EXIST(40203, R.string.request_status_teamwork_not_exist),
    /**
     * 主驾驶已经取消角色切换
     */
    PILOT_HAS_CANCEL_SWITCH(40204, R.string.request_status_pilot_has_cancel_switch),

    /**
     * 账户在别处登录，并且账户状态为Driving
     */
    LOGIN_ON_DRIVING(20108, R.string.login_other_driving),
    /**
     * 账户在别处登录，但是账户状态不是Driving
     */
    LOGIN_NOT_DRIVING(20109, R.string.login_other_not_driving),
    /**
     * 使用了卡车账号登录
     */
    ACCOUNT_INVALID(20106, R.string.account_invalid),
    /**
     * 公司未续费
     */
    ACCOUNT_NORENEWALS(20103, R.string.account_invalid),

    /**
     * 油税记录已经被删除
     */
    IFTA_DELETED(500001, R.string.ifta_deleted),

    /**
     * ******************************************************************************
     * <p>
     * dailylog业务，错误码范围
     * <p>
     * ******************************************************************************
     */
    /**
     * 签名错误
     */
    SIGN_ERROR(40101, R.string.request_status_sign_error);

    private int code;
    private int resId;

    RequestStatus(int code, int resId) {

        this.code = code;
        this.resId = resId;
    }

    /**
     * 获取code
     *
     * @return code
     */
    public int getCode() {

        return code;
    }

    /**
     * 获取信息
     *
     * @return 信息
     */
    public String getMsg() {

        return App.getContext().getResources().getString(this.resId);
    }

    /**
     * 根据code获取信息
     *
     * @param code code
     * @return 信息
     */
    public static String getMsg(int code) {

        String msg = "";
        for (RequestStatus item : RequestStatus.values()) {

            if (item.code == code) {

                msg = item.getMsg();
                break;
            }
        }

        return msg;
    }
}
