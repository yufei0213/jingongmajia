/**
 * @author zhangyu
 * @date 2018/1/22
 * @description 获取和DAILYLOG相关的数据
 */
var DAILYLOG = function () {

};

/**
 * 获取当前用户的基础信息
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getTodayGrid = function (callback) {

    if (Global.isWebDebug) {

        callback(Global.defaultData.TodayGrid);
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_TODAY_GRID, null, callback);
    }
};

/**
 * 获取指定下标的图表数据
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getGridDataByIndex = function (index, callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.GridDataByIndex));
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_GRID_DATA_BY_INDEX, {index: index}, callback);
    }
};

/**
 * 获取指定下标的全部日志
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getDailyLogDetailByIndex = function (index, callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.DailyLogDetailByIndex));
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_DAILY_LOG_DETAIL_BY_INDEX, {index: index}, callback);
    }
};

/**
 * 获取dailylog的汇总信息
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getGridSummary = function (callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.GridSummary));
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_DAILY_LOG_SUMMARY, null, callback);
    }
};

/**
 * 切换状态
 * @param callback 获得信息的回调函数
 */
DAILYLOG.changeState = function (data, callback) {

    if (Global.isWebDebug) {

        callback();
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_CHANGE_STATE, data, callback);
    }
};

/**
 * 更新表头信息
 * @param callback 获得信息的回调函数
 */
DAILYLOG.updateProfile = function (data, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_UPDATE_PROFILE, {data: data}, callback);
    }
};

/**
 * 获取表头
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getProfile = function (index, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_PROFILE, {index: index}, callback);
    }
};

/**
 * 获取签名
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getSign = function (index, callback) {

    if (Global.isWebDebug) {

        callback();
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_SIGN, {index: index}, callback);
    }
};

/**
 * 上传签名
 * @param callback 获得信息的回调函数
 */
DAILYLOG.uploadSign = function (index, sign, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_UPLOAD_SIGN, {
            index: index,
            sign: sign
        }, callback);
    }
};

/**
 * 获取日志内容
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getLogDetail = function (id, callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.defaultData.logDetail));
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_LOG_DETAIL, {id: id}, callback);
    }
};

/**
 * 获取日志内容
 * @param callback 获得信息的回调函数
 */
DAILYLOG.modifyEvent = function (localId, data, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_MODIFY_EVENT, {
            localId: localId,
            data: data
        }, callback);
    }
};

/**
 * 获取日志内容
 * @param callback 获得信息的回调函数
 */
DAILYLOG.newEvent = function (data, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_NEW_EVENT, {data: data}, callback);
    }
};

/**
 * 获取日志内容
 * @param callback 获得信息的回调函数
 */
DAILYLOG.addRemark = function (data, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_ADD_REMARK, {data: data}, callback);
    }
};

/**
 * 根据时间获取司机状态
 * @param data 数据
 * @param callback  回调函数
 */
DAILYLOG.getStatusByTime = function (data, callback) {

    if (Global.isWebDebug) {

        callback()
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_STATUS_BY_TIME, {data: data}, callback);
    }
};

/**
 * 获取日志内容
 * @param callback 获得信息的回调函数
 */
DAILYLOG.getOutOfLine = function (index, callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.OutOfLine));
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_OUT_OF_LINE_EVENT, {index: index}, callback);
    }
};

/**
 * 获取从某一个index开始，第二天开始查找第一个不是隐藏的点。
 * 如果第二天都是隐藏的点，则继续寻找直至寻找到今天。
 * 计算从那个点开始，到index第二天的0点的过去的秒数。
 * 用途是计算duration如果是跨天的事件，duration的真正值。
 *
 * * @param callback 获得信息的回调函数
 */
DAILYLOG.getPastDayEventSecond = function (index, callback) {

    if (Global.isWebDebug) {

        callback(86400);
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_PAST_DAY_EVENT_SECOND, {index: index}, callback);
    }
};

/**
 * 获取前一天最后的结束状态
 * @param index
 * @param callback
 */
DAILYLOG.getPastDayEndState = function (index, callback) {

    if (Global.isWebDebug) {

        callback(86400);
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_PAST_DAY_END_STATE, {index: index}, callback);
    }
};

/**
 * 获取预计明天可驾驶时间
 * @param callback 回调函数
 */
DAILYLOG.getTomorrowDrivingRemainTip = function (callback) {

    if (Global.isWebDebug) {

        callback("Off Duty from the work. Have a great day! The estimated available driving time for your next day is X h X min.");
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_GET_TOMORROW_DRIVING_REMAIN_TIP, null, callback);
    }
};

/**
 * 与服务端同步ddl
 * @param callback 回调函数
 */
DAILYLOG.syncDdl = function (callback) {

    if (Global.isWebDebug) {

        callback(1);
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_SYNC_DDL, null, callback);
    }
};

/**
 * 判断是否需要处理未认领日志
 * @param callback 回调函数
 */
DAILYLOG.hasUnidentified = function (callback) {

    if (Global.isWebDebug) {

        callback(1);
    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_HAS_UNIDENTIFIED, null, callback);
    }
};

/**
 * 打开未认领日志处理界面
 * @param callback 回调函数
 */
DAILYLOG.openUnidentified = function () {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.DAILYLOG_INTERFACE, Constants.DAILYLOG_INTERFACE_OPEN_UNIDENTIFIED);
    }
};