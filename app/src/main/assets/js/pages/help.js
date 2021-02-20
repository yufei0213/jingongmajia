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

    $("#uploadLogs").bind("click", $.proxy(function () {

        SDK.openPage(PageConfig.UploadLogs.url, PageConfig.UploadLogs.title);
    }, this));
    $("#syncDdl").bind("click", $.proxy(function () {

        SDK.openPage(PageConfig.SyncDdl.url, PageConfig.SyncDdl.title);
    }, this));
    $("#contactUs").bind("click", $.proxy(function () {

        SDK.openPage(PageConfig.Contact.url, PageConfig.Contact.title);
    }, this));
};