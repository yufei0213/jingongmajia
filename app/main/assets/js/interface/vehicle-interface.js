/**
 * @author yufei0213
 * @date 2017/12/07
 * @description vehicle接口
 */
var VEHICLE = function () {

};

/**
 * 获取车辆列表
 * @param callback 回调函数
 */
VEHICLE.getVehicleList = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.vehicleList.recentVehicleList), JSON.stringify(Global.defaultData.vehicleList.allVehicleList));
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_GET_VEHICLE_LIST, null, callback);
    }
};

/**
 * 获取当前连接的车辆
 * @param callback 回调函数
 */
VEHICLE.getCurrentVehicle = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.currentVehicle));
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_GET_CURRENT_VEHICLE, null, callback);
    }
};

/**
 * 连接车辆
 * @param vehicle 车辆信息
 * @param callback 回调函数
 */
VEHICLE.connectVehicle = function (vehicle, callback) {

    if (Global.isWebDebug) {

        SDK.log("连接车辆：" + JSON.stringify(vehicle));
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_CONNECT_VEHICLE, {vehicle: vehicle}, callback);
    }
};

/**
 * 与车辆连接断开
 */
VEHICLE.disconnectVehicle = function () {

    if (Global.isWebDebug) {

        SDK.log("断开车辆连接");
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_DISCONNECT_VEHICLE);
    }
};

/**
 * 自动断开车辆连接
 *
 * 当从其他状态切换为OFF时调用此方法
 */
VEHICLE.disconnectVehicleAuto = function () {

    if (Global.isWebDebug) {

        SDK.log("断开车辆连接");
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_DISCONNECT_VEHICLE_AUTO);
    }
};

/**
 * 获取异常故障
 */
VEHICLE.getVehicleMalfunctionList = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("获取异常故障");
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_GET_VEHICLE_MALFUCNTION_LIST, null, callback);
    }
};

/**
 * 里程数纠错
 * @param data
 * @param callback
 */
VEHICLE.changeOdometer = function (data, callback) {

    if(Global.isWebDebug) {

        SDK.log("里程数纠错");
    } else {

        callFunction(Constants.VEHICLE_INTERFACE, Constants.VEHICLE_INTERFACE_CHANGE_ODOMETER, data, callback);
    }

};