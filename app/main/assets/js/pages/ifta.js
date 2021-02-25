/**
 * @author yufei02123
 * @date 2018/6/25
 * @description ifta
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.fuelingBtn = $("#fuelingBtn");
    this.historyBtn = $("#historyBtn");

    this.fuelingBtn.on("click", $.proxy(this.onFuelingBtnClick, this));
    this.historyBtn.on("click", $.proxy(this.onHistoryBtnClick, this));
};

/**
 * 新建油税记录按钮被点击
 */
Web.onFuelingBtnClick = function () {

    IFTA.openCreatePage();
};

/**
 * 历史记录按钮被点击
 */
Web.onHistoryBtnClick = function () {

    SDK.openPage(PageConfig.IftaHistory.url, PageConfig.IftaHistory.title);
};