/**
 * @author yufei0213
 * @date 2017/12/07
 * @description sdk接口
 */
var SDK = function () {

};

/**
 * 检查是否需要恢复团队驾驶
 */
SDK.checkResume = function () {

    if (Global.isWebDebug) {

        SDK.log("检查是否需要恢复团队驾驶");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_CHECK_RESUME);
    }
};

/**
 * 显示loading
 */
SDK.showLoading = function () {

    if (Global.isWebDebug) {

        console.log("showLoading...");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_LOADING);
    }
};

/**
 * 隐藏loading
 */
SDK.hideLoading = function () {

    if (Global.isWebDebug) {

        console.log("hideLoading...");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_HIDE_LOADING);
    }
};

/**
 * 显示loading
 */
SDK.showDotLoading = function () {

    if (Global.isWebDebug) {

        console.log("showLoading...");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_DOT_LOADING);
    }
};

/**
 * 隐藏loading
 */
SDK.hideDotLoading = function () {

    if (Global.isWebDebug) {

        console.log("hideLoading...");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_HIDE_DOT_LOADING);
    }
};

/**
 * 显示弹窗
 */
SDK.showDialog = function (callback, config) {

    if (Global.isWebDebug) {

        eval(callback)(Constants.BUTTON_POSITIVE);
    } else {

        var temp = {};

        if (config) {

            temp.icon = config.icon ? config.icon : DialogConfig.Icon_Msg;
            temp.text = config.text ? config.text : String.hello;
            temp.negativeBtnText = config.negativeBtnText ? config.negativeBtnText : String.cancel;
            temp.neutralBtnText = config.neutralBtnText ? config.neutralBtnText : String.confirm;
            temp.positiveBtnText = config.positiveBtnText ? config.positiveBtnText : "";
            temp.cancelable = config.cancelable ? config.cancelable : DialogConfig.NoCancelable;
        }

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_DIALOG, temp, callback);
    }
};

/**
 * 显示弹窗
 */
SDK.showSpannableDialog = function (callback, config) {

    if (Global.isWebDebug) {

        eval(callback)(Constants.BUTTON_POSITIVE);
    } else {

        var temp = {};

        if (config) {

            temp.icon = config.icon ? config.icon : DialogConfig.Icon_Msg;
            temp.text = config.text ? config.text : String.hello;
            temp.negativeBtnText = config.negativeBtnText ? config.negativeBtnText : String.cancel;
            temp.neutralBtnText = config.neutralBtnText ? config.neutralBtnText : String.confirm;
            temp.positiveBtnText = config.positiveBtnText ? config.positiveBtnText : "";
            temp.cancelable = config.cancelable ? config.cancelable : DialogConfig.NoCancelable;
        }

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_SPANNABLE_DIALOG, temp, callback);
    }
};

/**
 * 显示弹窗
 */
SDK.showVerticalDialog = function (callback, config) {

    if (Global.isWebDebug) {

        eval(callback)(Constants.BUTTON_POSITIVE);
    } else {

        var temp = {};

        if (config) {

            temp.icon = config.icon ? config.icon : DialogConfig.Icon_Msg;
            temp.text = config.text ? config.text : DialogConfig.Text;
            temp.negativeBtnText = config.negativeBtnText ? config.negativeBtnText : String.cancel;
            temp.neutralBtnText = config.neutralBtnText ? config.neutralBtnText : String.confirm;
            temp.positiveBtnText = config.positiveBtnText ? config.positiveBtnText : "";
            temp.cancelable = config.cancelable ? config.cancelable : DialogConfig.NoCancelable;
        }

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_VERTICAL_DIALOG, temp, callback);
    }
};

/**
 * 显示成功提示框
 */
SDK.showSuccessPrompt = function (callback) {

    if (Global.isWebDebug) {

        console.log("show success");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_PROMPT, {type: 1}, callback);
    }
};

/**
 * 显示失败提示框
 */
SDK.showFailedPrompt = function (callback) {

    if (Global.isWebDebug) {

        console.log("show failed");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_PROMPT, {type: -1}, callback);
    }
};

/**
 * 显示成功提示框
 */
SDK.showDotSuccessPrompt = function (callback) {

    if (Global.isWebDebug) {

        console.log("show success");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_DOT_PROMPT, {type: 1}, callback);
    }
};

/**
 * 显示失败提示框
 */
SDK.showDotFailedPrompt = function (callback) {

    if (Global.isWebDebug) {

        console.log("show failed");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_DOT_PROMPT, {type: -1}, callback);
    }
};

/**
 * 展示续费弹窗
 * @param callback
 */
SDK.showExpireDayView = function (days, callback) {
    if (Global.isWebDebug) {

        console.log("show failed");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_EXPIRE_DAY, {days: days}, callback);
    }
};

/**
 * 打开时间选择器
 * @param nowSecond 当前时间描秒数
 * @param callback 回调函数
 */
SDK.showTimePicker = function (nowSecond, oneDayHours, callback) {

    if (Global.isWebDebug) {

        eval(callback)(14600);
    } else {

        callFunction(Constants.SDK_INTERFACE,
            Constants.SDK_INTERFACE_SHOW_TIME_PICKER,
            {
                nowSecond: nowSecond,
                oneDayHours: oneDayHours ? oneDayHours : 24
            },
            callback);
    }
};

/**
 *
 * @param callback
 */
SDK.showDatePicker = function (date, callback) {

    if (Global.isWebDebug) {

        eval(callback)("03/14/2018");
    } else {

        callFunction(Constants.SDK_INTERFACE,
            Constants.SDK_INTERFACE_SHOW_DATE_PICKER,
            {
                date: date
            },
            callback);
    }
};

/**
 * 打开页面
 * @param url 页面路径
 * @param title 页面标题
 * @param params 传递给下一个页面的参数
 * @param canBack 打开的页面是否允许返回
 */
SDK.openPage = function (url, title, params, canBack, hideNavigationBar) {

    if (Global.isWebDebug) {

        if (params) {

            params = JSON.stringify(params);
            window.location.href = url.split("/")[url.split("/").length - 1] + "?params=" + params;
        } else {

            window.location.href = url.split("/")[url.split("/").length - 1];
        }
    } else {

        var data = {
            url: url,
            title: title,
            params: params,
            canBack: canBack ? canBack : Constants.WEB_CANBACK,
            hideNavigationBar: hideNavigationBar ? hideNavigationBar : Constants.SHOW_NAVIGATION_BAR
        };

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_OPENPAGE, data);
    }
};

/**
 * 打开主页面
 */
SDK.openGridPage = function (params) {

    if (Global.isWebDebug) {

        window.location.href = url.split("/")[url.split("/").length - 1];
    } else {

        var data = {
            params: params
        };
        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_OPEN_GRID_PAGE, data);
    }
};

/**
 * 打开主页面
 */
SDK.openMainPage = function () {

    if (Global.isWebDebug) {

        window.location.href = url.split("/")[url.split("/").length - 1];
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_OPEN_MAIN_PAGE);
    }
};

/**
 * 开启Dailylog页面
 * @param callback 回调函数
 */
SDK.openDailylogPage = function (data, callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.remarkList));
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_OPEN_DAILY_LOG_PAGE, data, callback);
    }
};

/**
 * 显示信息
 * @param callback 回调函数
 */
SDK.showMessage = function (message) {

    if (Global.isWebDebug) {

        SDK.log(message);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SHOW_MESSAGE, {data: message});
    }
};

/**
 * 获取 geo-loaction
 * @param callback 回调函数
 */
SDK.getGeoLocation = function (callback) {

    if (Global.isWebDebug) {

        var latitude = 35.7766921;
        var longitude = -108.7223383;

        $.get("http://sandbox.ubt.io/eldv2/api/location/geo?access_token=" + Global.accessToken + "&latitude=" + latitude + "&longitude=" + longitude, function (data, status) {

            if (status == "success" && data.code == 0) {

                var location = data.result.geoLocation;
                eval(callback)(Constants.CALLBACK_SUCCESS, latitude, longitude, location);
            } else {

                eval(callback)(Constants.CALLBACK_FAILURE);
            }
        });
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_GEO_LOCATION, null, callback);
    }
};

/**
 * 获取 loaction
 * @param callback 回调函数
 */
SDK.getLocation = function (callback) {

    if (Global.isWebDebug) {

        var latitude = 35.7766921;
        var longitude = -108.7223383;

        eval(callback)(latitude, longitude);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_LOCATION, null, callback);
    }
};

/**
 * 获取全部defect
 * @param callback 回调函数
 */
SDK.getDefectList = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.defectList));
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_DEFECT_LIST, null, callback);
    }
};

/**
 * 获取全部remark 提示
 * @param callback 回调函数
 */
SDK.getRemarkList = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(JSON.stringify(Global.defaultData.remarkList));
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_REMARK_LIST, null, callback);
    }
};

/**
 * 获取语言设置
 * @param callback 回调函数
 */
SDK.getLanguageSetting = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)(2);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_LANGUAGE_SETTING, null, callback);
    }
};

/**
 * 设置语言
 * @param callback 回调函数
 */
SDK.setLanguage = function (languageType) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SET_LANGUAGE, languageType, null);
    }
};

/**
 * 获取通知设置
 * @param callback
 */
SDK.getNotificationSetting = function (data, callback) {

    if (Global.isWebDebug) {

        eval(callback)(2);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_NOTIFICATION_SETTING, data, callback);
    }
};

/**
 * 设置通知开关等
 * @param callback 回调函数
 */
SDK.setNotificationSetting = function (data) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SET_NOTIFICATION, data, null);
    }
};

/**
 * 调用原生方法存储数据
 * @param key 主键
 * @param data 值  js对象
 */
SDK.setWebData = function (key, data) {

    var temp = {
        key: key,
        data: data
    };

    if (Global.isWebDebug) {

        LocalStorage.setItem(Global.webDataKey, JSON.stringify(temp));
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SET_WEBDATA, temp);
    }
};

/**
 * 清除数据
 */
SDK.clearWebData = function () {

    if (Global.isWebDebug) {

        LocalStorage.removeItem(Global.webDataKey);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_CLEAR_WEB_DATA);
    }
};

/**
 * 关闭当前页面
 */
SDK.back = function (index) {

    if (Global.isWebDebug) {

        window.history.back();
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_BACK, {index: index ? index : -1});
    }
};

/**
 * 关闭当前页面，并且传递数据给父界面
 */
SDK.backForResult = function (data) {

    if (Global.isWebDebug) {

        window.history.back();
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_BACK_FOR_RESULT, {data: data ? data : ""});
    }
};

/**
 * 通过java获取时间字符串，带时区
 * 格式 01/14 06:23 AM EST
 * @param callback
 */
SDK.getDate = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)("01/14 06:23 AM");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_DATE, null, callback);
    }
};

/**
 * 通过java获取时间字符串，带时区
 * 格式 01/14 06:23 AM EST
 * @param callback
 */
SDK.getOffsetDate = function (callback, offset) {

    if (Global.isWebDebug) {

        eval(callback)("01/14 06:23 AM");
    } else {

        var data = {
            offset: offset ? offset : 0
        };

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_OFFSET_DATE, data, callback);
    }
};

/**
 * 输出日志
 * @param info 日志内容
 */
SDK.log = function (info, tag, level) {

    if (Global.isWebDebug) {

        console.log(info);
    } else {

        var data = {
            tag: tag ? tag : "web",
            info: info ? info : "",
            level: level ? 1 : 0
        };

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_LOG, data);
    }
};

/**
 * 获取版本
 * @param 回调函数
 */
SDK.getVersionName = function (callback) {

    if (Global.isWebDebug) {

        eval(callback)("1.0.0");
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_VERSION_NAME, null, callback);
    }
};

/**
 * 关闭启动动画
 */
SDK.openLauncherAnimation = function () {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_OPEN_LAUNCHER_ANIMATION);
    }
};

/**
 * 关闭启动动画
 */
SDK.closeLauncherAnimation = function () {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_CLOSE_LAUNCHER_ANIMATION);
    }
};


/**
 * 收集Firebase事件
 */
SDK.collectFirebaseEvent = function (eventCategory, eventAction, eventLabel, eventValue) {

    var data = {
        eventCategory: eventCategory,
        eventAction: eventAction,
        eventLabel: eventLabel,
        eventValue: eventValue
    };

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_COLLECT_FIREBASE_EVENT, data);
    }
};

/**
 * 收集Firebase屏幕
 */
SDK.collectFirebaseScreen = function (screenName) {

    var data = {
        screenName: screenName
    };

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_COLLECT_FIREBASE_SCREEN, data);
    }
};

/**
 * 发送邮件
 * @param params
 */
SDK.sendEmail = function (params) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SEND_EMAIL, params);
    }
};

/**
 * 拨打电话
 */
SDK.call = function (params) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_CALL, params);
    }
};

/**
 * 获取总时间
 */
SDK.getTotalHour = function (callback, index) {

    if (Global.isWebDebug) {

        callback(24);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_TOTAL_HOUR, {index: index}, callback);
    }
};

/**
 * 获取总时间
 */
SDK.getTotalHourByDateTime = function (callback, time) {

    if (Global.isWebDebug) {

        callback(24);
    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_TOTAL_HOUR, {date: time}, callback);
    }
};

/**
 * 获取当前数据采集类型
 */
SDK.getDataCollectorType = function (callback) {

    if (Global.isWebDebug) {


    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_DATA_COLLECTOR_TYPE, null, callback);
    }
};

/**
 * 获取总时间
 */
SDK.getDriverName = function (callback) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_DRIVER_NAME, null, callback);
    }
};

/**
 * 上传日志
 * @param callback
 */
SDK.uploadLogs = function (callback) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_UPLOAD_LOGS, null, callback);
    }
};

/**
 * 获取规则列表
 * @param callback
 */
SDK.getRuleList = function (callback) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_RULE_LIST, null, callback);
    }
};

/**
 * 选择规则
 * @param param
 */
SDK.setRule = function (param) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_SET_RULE, {rule: param});
    }
}

/**
 * 获取当前规则id
 * @param callback
 */
SDK.getCurrentRuleId = function (callback) {

    if (Global.isWebDebug) {

    } else {

        callFunction(Constants.SDK_INTERFACE, Constants.SDK_INTERFACE_GET_CURRENT_RULE, null, callback);
    }
};
