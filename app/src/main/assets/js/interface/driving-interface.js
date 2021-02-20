/**
 * @author yufei0213
 * @date 2018/1/29
 * @description driving页面接口
 */
var DRIVING = function () {

};

/**
 * 获取司机当前状态
 * @param callback 回调函数
 */
DRIVING.getDriverStatus = function (callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.defaultData.drivingData));
    } else {

        callFunction(Constants.DRIVING_INTERFACE, Constants.DRIVING_INTERFACE_GET_DRIVER_STATUS, null, callback);
    }
};

/**
 * 显示提示窗
 * @param callback
 */
DRIVING.showDialog = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(1);
    } else {

        callFunction(Constants.DRIVING_INTERFACE, Constants.DRIVING_INTERFACE_SHOW_DIALOG, null, callback);
    }
};

/**
 * 添加车辆状态的监听
 * @param callback 回调函数
 */
DRIVING.setVehicleStatusListener = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("添加车辆状态变化监听");
    } else {

        callFunction(Constants.DRIVING_INTERFACE, Constants.DRIVING_INTERFACE_SET_VEHICLE_LISTENER, null, callback);
    }
};

/**
 * 解除车辆状态监听
 */
DRIVING.cancelVehicleStatusListener = function () {

    if (Global.isWebDebug) {

        SDK.log("解除车辆状态监听");
    } else {

        callFunction(Constants.DRIVING_INTERFACE, Constants.DRIVING_INTERFACE_CANCEL_VEHICLE_LISTENER);
    }
};
