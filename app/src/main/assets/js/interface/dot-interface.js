/**
 * @author mamw
 * @date 2018/1/25
 * @description 获取和DOT相关的数据
 */
var DOT = function () {
};

/**
 * 启动Dot详情页面
 */
DOT.openDetailPage = function () {

    if (Global.isWebDebug) {

        var url = "html/dot-review.html";
        window.location.href = url.split("/")[url.split("/").length - 1];
    } else {

        callFunction(Constants.DOT_INTERFACE, Constants.DOT_INTERFACE_OPEN_DETAIL_PAGE);
    }
};

/**
 * 获取Dot Review Data List
 * @param callback 获得信息的回调函数
 */
DOT.getReviewDataList = function (callback) {

    if (Global.isWebDebug) {

        callback(JSON.stringify(Global.defaultData.dotReviewDataList));
    } else {

        callFunction(Constants.DOT_INTERFACE, Constants.DOT_INTERFACE_GET_REVIEW_DATA_LIST, null, callback);
    }
};

/**
 * 请求发送Email
 */
DOT.requestSendEmail = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.DOT_INTERFACE, Constants.DOT_INTERFACE_REQUEST_SEND_EMAIL, params, callback);
    }
};

/**
 * 请求发送PDF
 */
DOT.requestSendPdf = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.DOT_INTERFACE, Constants.DOT_INTERFACE_REQUEST_SEND_PDF, params, callback);
    }
};

/**
 * 请求Web Service
 */
DOT.requestWebService = function (params, callback) {

    if (Global.isWebDebug) {

        callback(Constants.CALLBACK_SUCCESS);
    } else {

        callFunction(Constants.DOT_INTERFACE, Constants.DOT_INTERFACE_REQUEST_WEB_SERVICE, params, callback);
    }
};

/**
 * 打开Dot Review页面
 */
DOT.selectReviewTabItem = function () {

    if (Global.isWebDebug) {

        SDK.log("Open Dot Review Page.")
    } else {

        callFunction(Constants.DOT_INTERFACE, Constants.DOT_INTERFACE_SELECT_REVIEW_TAB_ITEM);
    }
};
