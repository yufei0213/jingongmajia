/**
 * @author yufei0213
 * @date 2017/12/07
 * @description user接口
 */
var USER = function () {

};

/**
 * 获取当前用户的基础信息
 * @param callback 获得信息的回调函数
 */
USER.getUser = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)("C:\\MediaLib");
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_GET_USER, null, callback);
    }
};

/**
 * 获取当前司机的基础信息
 * @param callback 获得信息的回调函数
 */
USER.getDriver = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.driver));
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_GET_DRIVER, null, callback);
    }
};

/**
 * 获取用户角色
 * @param callback 获得信息的回调函数
 */
USER.getUserRole = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(USERROLE.COPILOT);
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_GET_USERROLE, null, callback);
    }
};

/**
 * 获取用户权限
 * @param callback 获得信息的回调函数
 */
USER.getUserFunc = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(USERFUNCTION.NORMAL);
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_GET_USERFUNC, null, callback);
    }
};

/**
 * 获取当前公司的基础信息
 * @param callback 获得信息的回调函数
 */
USER.getCarrier = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.carrier));
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_GET_CARRIER, null, callback);
    }
};

/**
 * 更新driver的电话，邮箱
 * @param callback 获得信息的回调函数
 */
USER.updateDriverInfo = function (params, callback) {

    if (Global.isWebDebug) {

        eval(callback)(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_UPDATE_DRIVER, params, callback);
    }
};

/**
 * 修改账户密码
 * @param callback 获得信息的回调函数
 */
USER.updatePassword = function (params, callback) {

    if (Global.isWebDebug) {

        eval(callback)(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_UPDATE_PASSWORD, params, callback);
    }
};

/**
 * 检查司机是否在OffDuty状态
 */
USER.getDriverNotCertifiedAlertStatus = function (callback) {

    if (Global.isWebDebug) {

        callback(0);
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_GET_DRIVER_NOTCERTIFIED_ALERT_COUNT, null, callback);
    }
};

/**
 * 司机请求服务器登出
 */
USER.logoutRequest = function (callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_LOGOUT_REQUEST, null, callback);
    }
};

/**
 * 司机请求服务器登出
 */
USER.logoutLocal = function () {

    if (Global.isWebDebug) {

        SDK.log("open login page");
    } else {

        callFunction(Constants.USER_INTERFACE, Constants.USER_INTERFACE_LOGOUT_LOCAL, null, null);
    }
};
