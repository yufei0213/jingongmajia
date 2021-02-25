/**
 * @author mamw
 * @date 2018/1/25
 * @description dot 页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function () {

    new DotPage($('#page'));
};

(function ($, window) {

    var defaultSetting = {};

    var DotPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        this.view = view;
        this.modeBtn = null;

        this.init();
    };

    DotPage.prototype.init = function () {

        this.modeBtn = this.view.find('[sid=modeBtn]');
        this.modeBtn.on('click', $.proxy(this.onModeBtnClick, this));
    };

    DotPage.prototype.onModeBtnClick = function () {

        DOT.openDetailPage();
    };

    window.DotPage = DotPage;
}($, window));