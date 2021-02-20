/**
 * @author yufei0213
 * @date 2018/7/9
 * @description
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    $("#syncBtn").on("click", $.proxy(function () {

        SDK.showLoading();
        DAILYLOG.syncDdl($.proxy(function (code) {

            SDK.hideLoading();
            if (code == Constants.CALLBACK_SUCCESS) {

                SDK.showSuccessPrompt($.proxy(function () {

                    SDK.back();
                }, this));
            } else {

                SDK.showFailedPrompt();
            }
        }, this));
    }, this));
};