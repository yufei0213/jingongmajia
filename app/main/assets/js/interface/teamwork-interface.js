/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 副驾驶相关的接口
 */
var TEAMWORK = function () {

};

/**
 * 获取可以成为副驾驶的司机列表
 * @param callback 回调函数
 */
TEAMWORK.getCoDriverList = function (callback) {

    if (Global.isWebDebug) {

        setTimeout(function () {

            eval(callback)(Constants.CALLBACK_SUCCESS, JSON.stringify(Global.defaultData.coDriver));
            // eval(callback)(Constants.CALLBACK_SUCCESS);
            // eval(callback)(Constants.CALLBACK_FAILURE);
        }, 2000);
    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_GET_COPILOT_LIST, null, callback);
    }
};

/**
 * 向副驾驶发送邀请
 * @param callback 回调函数
 */
TEAMWORK.inviteCopilot = function (copilot, callback) {

    var data = {
        copilot: copilot
    };

    if (Global.isWebDebug) {

        eval(callback)(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_INVITE_COPILOT, data, callback);
    }
};

/**
 * 取消向副驾发起的邀请
 * @param callback 回调函数
 */
TEAMWORK.cancelInviteCopilot = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("cancelInviteCopilot");
    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_PILOT_CANCEL_INVITE_COPILOT, null, callback);
    }
};

/**
 * 主驾驶请求切换角色
 * @param callback 回调函数
 */
TEAMWORK.pilotRequestSwitch = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("pilotRequestSwitch");
    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_PILOT_REQUEST_SWITCH, null, callback);
    }
};

/**
 * 主驾驶取消切换角色请求
 * @param callback 回调函数
 */
TEAMWORK.pilotCancelSwitchRequest = function (callback) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_PILOT_CANCEL_SWITCH_REQUEST, null, callback);
    }
};

/**
 * 主驾驶移除副驾驶
 * @param callback 回调函数
 */
TEAMWORK.pilotRemoveCopilot = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("pilotRemoveCopilot");
    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_PILOT_REMOVE_COPILOT, null, callback);
    }
};

/**
 * 副驾驶离开团队
 * @param callback 回调函数
 */
TEAMWORK.copilotExit = function (callback) {

    if (Global.isWebDebug) {

        SDK.log("copilotExit");
    } else {

        callFunction(Constants.TEAMWORK_INTERFACE, Constants.TEAMWORK_INTERFACE_COPILOT_EXIT, null, callback);
    }
};