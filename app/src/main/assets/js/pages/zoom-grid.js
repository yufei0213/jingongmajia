/**
 * @author liujiaqi
 * @date 2018/12/28
 * @description
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    var gridData = params;
    this.body = $("body");
    this.body.on("click", $.proxy(this.backClick, this));
    var height = document.body.offsetHeight;
    SDK.getTotalHour($.proxy(function (hour) {
        this.grid = new DriverGrid("grid", hour, height);
        this.grid.init();
        this.grid.initCanvas();
        this.grid.draw(gridData, true);
    }, this));
};

Web.backClick = function () {

    SDK.back();
};
