/**
 * @author mamw
 * @date 2018/1/28
 * @description 获取和Alert关的数据
 */

var ALERT = function () {

};

/**
 * 获取Not Certified Alert数据
 * @param callback 获得信息的回调函数
 */
ALERT.getNotCertifiedAlertData = function (params, callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.alertNotCertifiedList));
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_GET_NOTCERTIFED_DATA, params, callback);
    }
};

/**
 * 获取Edit Alert数据
 * @param callback 获得信息的回调函数
 */
ALERT.getEditAlertData = function (params, callback) {

    if (Global.isWebDebug) {

        eval(callback)();
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_GET_EDIT_DATA, params, callback);
    }
};

/**
 * 获取Assigned Alert数据
 * @param callback 获得信息的回调函数
 */
ALERT.getAssignedAlertData = function (params, callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.alertAssignedList));
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_GET_ASSIGNED_DATA, params, callback);
    }
};

/**
 * 上传签名
 * @param callback 获得信息的回调函数
 */
ALERT.uploadNotCertifiedAlertSign = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_UPLOAD_NOTCERTIED_SIGN, params, callback);
    }
};

/**
 * 更新Edit Alert状态
 * @param callback 获得信息的回调函数
 */
ALERT.updateEditAlertStatus = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS)
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_UPDATE_EDIT_STATUS, params, callback);
    }
};

/**
 * 更新Assigned Alert状态
 * @param callback 获得信息的回调函数
 */
ALERT.updateAssignedAlertStatus = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS)
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_UPDATE_ASSIGNED_STATUS, params, callback);
    }
};

/**
 * 获取alerts统计信息
 * @param callback 获得信息的回调函数
 */
ALERT.getAlertSummary = function (callback) {

    if (Global.isWebDebug) {

        callback("{\"assignedCnt\":1,\"diagnosticCnt\":0,\"malfunctionCnt\":0,\"notCertifiedLogsCnt\":0,\"requestedEditsCnt\":0}");
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_GET_SUMMARY, null, callback);
    }
};

/**
 * 获取Unidentified Driver Log数据
 * @param callback 获得信息的回调函数
 */
ALERT.getUnidentifiedDriverLogData = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.unidentifiedDriverLogList));
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_GET_UNIDENTIFIED_DRIVER_LOG_DATA, null, callback);
    }
};

/**
 * 更新Unidentified Driver Log状态
 * @param callback 获得信息的回调函数
 */
ALERT.updateUnidentifiedDriverLogStatus = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS)
    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_UPDATE_UNIDENTIFIED_DRIVER_LOG_STATUS, params, callback);
    }
};

/**
 * 打开日志详情页面
 */
ALERT.openDailyLogDetailPage = function (params) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.ALERT_INTERFACE, Constants.ALERT_INTERFACE_OPEN_DAILYLOG_DETAIL_PAGE, params);
    }
};


