/**
 * @author zhangyu
 * @date 2018/1/24
 * @description TODO
 */
//日志页的Page
var detailLog;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    detailLog = new DetailLogPage(params);
    detailLog.init();
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    detailLog.getAlerts();
    DAILYLOG.getOutOfLine(detailLog.index, $.proxy(detailLog.onOutOfLine, detailLog));
};

var DetailLogPage = function (params) {

    if (Global.isWebDebug) {

        this.index = 0;
    } else {

        this.index = params.index;
    }
};

DetailLogPage.prototype.init = function () {
    SDK.getTotalHour($.proxy(function (hour) {
        //初始化今天的视图
        this.grid = new DriverGrid("grid", hour);
        this.grid.init();
        this.itemTemplate = $("#template-list-item");
        this.listContent = $("#listContent");
        this.addStatus = $("#addStatus");
        this.addRemark = $("#addRemark");
        this.alertBox = $("#alertRequestEdit");
        this.alertNum = $("#alertNumber");
        this.limitAlert = $("#limitAlert");
        this.limitAlertBox = $("#alertBox");
        this.addStatus.on("click", $.proxy(this.clickNewStatus, this));
        this.addRemark.on("click", $.proxy(this.clickAddRemark, this));
        this.alertBox.on("click", $.proxy(this.clickRequestEdit, this));
        //读取数据
        this.getStatesData();
    }, this), this.index);
};

DetailLogPage.prototype.reloadData = function () {

    this.grid.initCanvas();
    this.itemList = [];
    this.listContent.html("");
    this.getStatesData();
};

DetailLogPage.prototype.getStatesData = function () {

    var env = this;
    DAILYLOG.getGridDataByIndex(this.index, function (data) {

        env.grid.initCanvas();
        env.grid.draw(JSON.parse(data), false);
    });
    this.itemList = [];
    this.listContent.empty();
    DAILYLOG.getDailyLogDetailByIndex(this.index, function (data) {
        env.listContent.empty();
        data = JSON.parse(data);

        if (data.length == 0)
            return;
        for (var i = 0; i < data.length; i++) {

            var item = $(env.itemTemplate.html());
            item.data = data[i];

            item.data.endSecond = -1;
            for (var n = i + 1; n < data.length; n++) {

                if (typeof (data[n].driverState) != "undefined" && data[n].driverState != 0) {
                    item.data.endSecond = data[n].startSecond;
                    break;
                }
            }

            item.find("[sid=name]").html(data[i].stateString);
            item.find("[sid=time]").html(data[i].date);
            item.find("[sid=detailBtn]").on("click", {item: item}, $.proxy(env.clickDetail, env));
            item.find("[sid=editBtn]").on("click", {item: item}, $.proxy(env.clickEdit, env));
            item.on("click", {item: item}, $.proxy(env.clickEvent, env));
            if (data[i].driverState == DRIVERSTATUS.SLEEPER || data[i].driverState == DRIVERSTATUS.OFFDUTY) {

                item.find("[sid=editBtn]").removeClass("dn");
            }
            //处理自动记录的odnd
            if (data[i].driverState == DRIVERSTATUS.ONDUTY && data[i].origin != 1 && data[i].origin != 4) {

                item.find("[sid=editBtn]").removeClass("dn");
            }
            //处理自动记录的driving
            if (data[i].driverState == DRIVERSTATUS.DRIVING && data[i].origin != 1 && data[i].origin != 4) {
                item.find("[sid=editBtn]").removeClass("dn");
            }
            env.itemList.push(item);
            env.listContent.append(item);
        }
    });
    this.getAlerts();
    DAILYLOG.getOutOfLine(this.index, $.proxy(this.onOutOfLine, this));
};

/**
 * 请求request edit
 */
DetailLogPage.prototype.getAlerts = function () {

    this.alertBox.addClass("dn");
    ALERT.getEditAlertData({index: this.index}, $.proxy(function (tabIndex, data) {

        data = JSON.parse(data);
        if (typeof (data) != "undefined" && data.length > 0) {

            this.alertBox.removeClass("dn");
            this.alertNum.html(data.length);
        }
    }, this));
};

/**
 * 获得到违规信息
 */
DetailLogPage.prototype.onOutOfLine = function (data) {

    data = JSON.parse(data);
    this.grid.drawRedLine(data.events);
    //如果有提醒
    if ((data.shift + data.driving + data.cycle + data.off) > 0) {

        this.limitAlertBox.removeClass("dn");
    } else {

        this.limitAlertBox.addClass("dn");
        return;
    }

    this.limitAlert.html(data.limitString);
};


DetailLogPage.prototype.clickEvent = function (event) {

    var env = this;
    if (event.data.item.hasClass("logeditOn")) {
        event.data.item.removeClass("logeditOn");
        //解决画图引起的卡顿
        if (this.hasShadow) {
            setTimeout(function () {
                env.grid.redraw();
                env.grid.drawRedLine();
            }, 500);
        }
        return;
    }
    for (var i = 0; i < this.itemList.length; i++) {

        this.itemList[i].removeClass("logeditOn");
    }
    event.data.item.addClass("logeditOn");
    if (event.data.item.data.driverState && event.data.item.data.driverState != 0) {
        this.grid.initCanvas();
        this.grid.draw();
        this.grid.drawRedLine();
        this.grid.coverShadow(event.data.item.data.startSecond);
        this.hasShadow = true;
    } else {
        //解决画图引起的卡顿
        if (this.hasShadow) {
            setTimeout(function () {
                env.grid.redraw();
                env.grid.drawRedLine();
            }, 500);
        }
    }

};

DetailLogPage.prototype.clickDetail = function (event) {

    SDK.openPage(PageConfig.DdlStatusDetail.url,
        PageConfig.DdlStatusDetail.title,
        {id: event.data.item.data.localId});
};

DetailLogPage.prototype.clickEdit = function (event) {

    SDK.openPage(PageConfig.DdlStatusEdit.url,
        PageConfig.DdlStatusEdit.title,
        {
            id: event.data.item.data.localId,
            beginSecond: event.data.item.data.startSecond,
            endSecond: event.data.item.data.endSecond,
            index: this.index
        });
};

DetailLogPage.prototype.clickNewStatus = function () {

    SDK.openPage(PageConfig.DdlInsertStatus.url,
        PageConfig.DdlInsertStatus.title,
        {index: this.index});
};
DetailLogPage.prototype.clickAddRemark = function () {

    SDK.openPage(PageConfig.DdlAddRemark.url,
        PageConfig.DdlAddRemark.title,
        {index: this.index});
};
DetailLogPage.prototype.clickRequestEdit = function () {

    var parmas = {
        tabIndex: 1
    };
    SDK.openPage(PageConfig.Alerts.url,
        PageConfig.Alerts.title,
        parmas);
};