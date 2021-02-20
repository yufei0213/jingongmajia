/**
 * @author zhangyu
 * @date 2018/1/22
 * @description 主页数据的JS接口
 */

var DASHBOARD = function () {

};

/**
 * 获取当前用户的基础信息
 * @param callback 获得信息的回调函数
 */
DASHBOARD.getDashboardTime = function (callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.defaultData.dashboadData));
    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_GET_DASHBOARD_TIME, null, callback);
    }
};

/**
 * 获取是否是离线状态
 * @param callback 获得信息的回调函数
 */
DASHBOARD.getOffline = function (callback) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_GET_OFF_LINE, null, callback);
    }
};

/**
 * 获取司机当前状态
 * @param callback 回调函数
 */
DASHBOARD.getDriverState = function (callback) {

    if (Global.isWebDebug) {

        callback(DRIVERSTATUS.DRIVING);
    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_GET_DRIVER_STATE, null, callback);
    }
};

/**
 * 打开startbreak
 * @param callback 获得信息的回调函数
 */
DASHBOARD.openStartBreakDialog = function (data, callback) {

    if (Global.isWebDebug) {

        callback();
    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_OPEN_START_BREAK_DIALOG, data, callback);
    }
};

/**
 * 打开AdverseDriving
 * @param callback 获得信息的回调函数
 */
DASHBOARD.openAdverseDrivingDialog = function (callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_OPEN_ADVERSE_DRIVING_DIALOG, null, callback);
    }
};

/**
 * 关闭AdverseDriving
 */
DASHBOARD.closeAdverseDrivingDialog = function () {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_CLOSE_ADVERSE_DRIVING_DIALOG);
    }
};

/**
 * 关闭startbreak
 */
DASHBOARD.closeStartBreakDialog = function () {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_CLOSE_START_BREAK_DIALOG);
    }
};

/**
 * 打开AdverseDriving
 * @param callback 获得信息的回调函数
 */
DASHBOARD.getEcmInfo = function (callback) {

    if (Global.isWebDebug) {

        callback("")
    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_GET_ECM_INFO, null, callback);
    }
};

/**
 * 获取当前Break状态
 * @param callback 回调函数
 */
DASHBOARD.isBreak = function (callback) {

    if (Global.isWebDebug) {

        callback(1);
    } else {

        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_IS_BREAK, null, callback);
    }
};

/**
 * 添加 shippingID
 * @param callback 回调函数
 */
DASHBOARD.showShippingView = function (callBack) {

    if (Global.isWebDebug) {

        callBack("");
    } else {
        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_SHIPPING_DIALOG, null, callBack);
    }
}

/**s
 * 保存 shippingID
 * @param callback 回调函数
 */
DASHBOARD.saveShipping = function (shipping, callBack) {

    if (Global.isWebDebug) {

        callBack("");
    } else {
        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_SAVE_SHIPPING, {data: {shipping: shipping}}, callBack);
    }
}

/**
 * 获取过期时间
 * @param callBack
 */
DASHBOARD.getExpireDays = function (callBack) {

    if (Global.isWebDebug) {
        callBack("30");
    } else {
        callFunction(Constants.DASHBOARD_INTERFACE, Constants.DASHBOARD_INTERFACE_GET_EXPIRE_DAY, null, callBack);
    }
}