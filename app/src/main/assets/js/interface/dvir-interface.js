/**
 * @author zhangyu
 * @date 2018/1/22
 * @description 获取和DAILYLOG相关的数据
 */
var DVIR = function () {

};

/**
 * 上传数据
 * @param params
 * @param callback
 */
DVIR.uploadInspection = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.DVIR_INTERFACE, Constants.DVIR_INTERFACE_UPLOAD_INSPECTION, params, callback);
    }
};

/**
 * 获取检查记录列表
 * @param callback
 */
DVIR.getInspectionList = function (params, callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.inspectionListResult));
    } else {

        callFunction(Constants.DVIR_INTERFACE, Constants.DVIR_INTERFACE_INSPECTION_LIST, params, callback);
    }
};

/**
 * 获取post-trip inspection log
 * @param callback
 */
DVIR.getInspectionLog = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("getInspectionLog");
    } else {

        callFunction(Constants.DVIR_INTERFACE, Constants.DVIR_INTERFACE_GET_INSPECTION_LOG, null, callback);
    }
};
