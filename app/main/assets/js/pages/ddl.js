/**
 * @author zhangyu
 * @date 2018/1/22
 * @description TODO
 */
//日志页的Page
var dailylogPage;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    dailylogPage = new DailylogPage(params);
    dailylogPage.init();

    SDK.collectFirebaseScreen("dailylog-list");
};
/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    dailylogPage.getStatesData();

    SDK.collectFirebaseScreen("dailylog-list");
};

var DailylogPage = function (params) {

    this.itemTemplate = $("#template-list-item");
    this.listContent = $("#listContent");
};

DailylogPage.prototype.init = function () {

    SDK.getTotalHour($.proxy(function (hour) {
        //初始化今天的视图
        this.grid = new DriverGrid("grid", hour);
        this.grid.init();

        $("#today").on("click",{"index":0},$.proxy(this.clickDailylog,this));

        //读取数据
        this.getStatesData();
    },this));
};

DailylogPage.prototype.getStatesData = function () {

    var env = this;
    DAILYLOG.getTodayGrid(function (data) {

        env.grid.init();
        env.grid.draw(JSON.parse(data), false);
    });
    DAILYLOG.getGridSummary(function (data) {
        data=JSON.parse(data);
        if(data.length == 0)
            return;
        $("#todayDate").html(data[0].date);
        $("#todayOneDuty").html(TimeUtil.formatHM(data[0].ondutySecond));
        env.listContent.html("");
        for (var i = 1; i < data.length; i++) {

            var item = $(env.itemTemplate.html());
            item.attr("date",data[i].date);
            var dateItem = item.find("[sid=date]");
            dateItem.html(data[i].date);
            item.find("[sid=onduty]").html(TimeUtil.formatHM(data[i].ondutySecond));
            item.on("click",{"index":i},$.proxy(env.clickDailylog,env));
            env.listContent.append(item);
        }
    });
};

DailylogPage.prototype.clickDailylog = function (event) {

    SDK.openDailylogPage({index: event.data.index});
};

