/**
 * @author yufei0213
 * @date 2018/6/25
 * @description ifta接口
 */
var IFTA = function () {

};

/**
 * 打开创建界面
 *
 * @param data 数据
 */
IFTA.openCreatePage = function (data) {

    if (Global.isWebDebug) {

        console.log("open update page...")
    } else {

        callFunction(Constants.IFTA_INTERFACE, Constants.IFTA_INTERFACE_OPEN_CREATE_PAGE, data);
    }
};

/**
 * 打开详情界面
 * <p>
 * 包含删除和编辑
 *
 * @param data 数据
 */
IFTA.openUpdatePage = function (data) {

    if (Global.isWebDebug) {

        console.log("open update page...")
    } else {

        callFunction(Constants.IFTA_INTERFACE, Constants.IFTA_INTERFACE_OPEN_UPDATE_PAGE, data);
    }
};

/**
 * 获取全部燃油类型
 * @param callback 回调函数
 */
IFTA.getFuelType = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.fuelType));
    } else {

        callFunction(Constants.IFTA_INTERFACE, Constants.IFTA_INTERFACE_GET_FUEL_TYPE, null, callback);
    }
};

/**
 * 获取全部的州列表
 * @param callback 回调函数
 */
IFTA.getStateList = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.stateList));
    } else {

        callFunction(Constants.IFTA_INTERFACE, Constants.IFTA_INTERFACE_GET_STATE_LIST, null, callback);
    }
};

/**
 * 获取加油历史列表
 * @param callback 回调函数
 */
IFTA.getFuelHistoryList = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.fuelHistory));
    } else {

        callFunction(Constants.IFTA_INTERFACE, Constants.IFTA_INTERFACE_GET_FUEL_HISTORY_LIST, null, callback);
    }
};
