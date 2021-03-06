/**
 * @author zhangyu
 * @date 2018/1/22
 * @description DashboardPage
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.dashboardPage = new DashboardPage(params);
    this.teamWorkManager = new TeamWorkManager();

    SDK.checkResume();
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    this.dashboardPage.getAlertsNum();
    this.dashboardPage.reload();

    if (params != null && params.key == Constants.HANDLE_UNIDENTIFIED_KEY) {

        SDK.clearWebData();
        DAILYLOG.hasUnidentified($.proxy(function (code) {
            if (code == 1) {
                DAILYLOG.openUnidentified()
            }
        }))
    }
};

/**
 * 页面暂停时调用
 */
Web.onPause = function () {

    DASHBOARD.closeStartBreakDialog();
    DASHBOARD.closeAdverseDrivingDialog();
};

/**
 * 页面销毁
 */
Web.onDestroy = function () {

    this.dashboardPage.destroy();
};

/**
 * 司机状态发生变化
 */
Web.hosModelChangeMessage = function () {

    this.dashboardPage.getStatesData();
};

/**
 * 车辆选择状态变化
 */
Web.vehicleSelectChange = function (vehicle) {

    this.dashboardPage.setSelectVehicle(vehicle);
};

/**
 * 车辆连接状态变化
 */
Web.vehicleConnectStateChange = function (code) {

    if (code == VEHICLECONNECTSTATE.CONNECTED) {

        this.dashboardPage.ecmBtn.removeClass("dn");
        this.dashboardPage.gpsBtn.addClass("dn");
    } else if (code == VEHICLECONNECTSTATE.DISCONNECTED) {

        this.dashboardPage.gpsBtn.removeClass("dn");
        this.dashboardPage.ecmBtn.addClass("dn");
    } else if (code == VEHICLECONNECTSTATE.CLEAR) {

        this.dashboardPage.gpsBtn.addClass("dn");
        this.dashboardPage.ecmBtn.addClass("dn");
    }
};

/**
 * 团队工作状态发生变化
 * @param data 团队工作数据
 */
Web.teamWorkDashBoardChange = function (data) {

    this.teamWorkManager.updateView(data);
};

/**
 * 是否存在故障
 */
Web.malFunctionRefresh = function (isShow) {

    if (isShow == MALFUNCTION.SHOW) {

        this.dashboardPage.malfunctionBtn.removeClass("dn");
    } else {

        this.dashboardPage.malfunctionBtn.addClass("dn");
    }
};

/**
 * 是否存在故障
 */
Web.networkStateChange = function (connected) {

    if (connected == NETWORK.CONNECTED) {

        this.dashboardPage.offlineAlert.addClass("dn");
        this.dashboardPage.getAlertsNum();
        if (this.dashboardPage.expireDays <= 30) {

            this.dashboardPage.expireTip.removeClass("dn");
        }
    } else {
        this.dashboardPage.offlineAlert.removeClass("dn");
        this.dashboardPage.requestEditBar.addClass("hide")
        this.dashboardPage.expireTip.addClass("dn");
    }
};

var DashboardPage = function (params) {

    this.stateShort = $("#state_short");
    this.state = $("#state");
    this.changeStateBtn = $("#changeStateBtn");
    this.vehicleBtn = $("#vehicleBtn");
    this.adverseDrivingBtn = $("#adverseDriving");
    this.startBreakBtn = $("#startBreakBtn");
    this.breakString = $("#breakString");
    this.vehicleTipView = $("#vehicleTip");
    this.vehicleCodeView = $("#vehicleCode");
    this.noVehicleTip = $("#noVehicleTip");
    this.gpsBtn = $("#gpsBtn");
    this.ecmBtn = $("#ecmBtn");
    this.requestEditBar = $("#requestEditBar");
    this.driverName = $("#driverName");
    this.expireTip = $("#expireTip");
    this.expireTip.addClass("dn");
    this.malfunctionBtn = $("#malfunctionBtn");
    this.iftaBtn = $("#iftaBtn");
    this.americaFlag = $("#america");
    this.canadaFlag = $("#canada");
    this.timeView = $("#ruleTime");
    this.offlineAlert = $("#offline");
    this.tempSeveral = $($("#template-several").html());
    this.tempCertify = $($("#template-certified").html());
    this.tempEdit = $($("#template-edit-request").html());
    this.tempAssigned = $($("#template-assigned").html());
    this.gridContainer = $("#gridContainer");

    this.isOnBreak = false;
    this.isVehicleSelected = false;
    this.expireDays = 3000;
    this.initLogin = true;
    /**
     * 当前状态
     * @type {*}
     */
    this.state = $("#state");

    var startBreakBtnClickProxy = new ClickProxy(function () {

        if (!this.isOnBreak &&
            (this.currentState == DRIVERSTATUS.OFFDUTY || this.currentState == DRIVERSTATUS.PERSONALUSE)) {

            SDK.showDialog(null, {
                icon: DialogConfig.Icon_Love,
                text: String.dashboardNotBreakTip,
                positiveBtnText: String.ok,
                cancelable: DialogConfig.Cancelable
            });
            return;
        }
        var data = {isOn: this.isOnBreak};
        DASHBOARD.openStartBreakDialog({data: data}, $.proxy(this.breakSuccess, this));
    }, this);
    this.startBreakBtn.on("click", $.proxy(startBreakBtnClickProxy.click, startBreakBtnClickProxy));

    var adverseDrivingBtnClickProxy = new ClickProxy(function () {

        DASHBOARD.openAdverseDrivingDialog();
    }, this);
    this.adverseDrivingBtn.on("click", $.proxy(adverseDrivingBtnClickProxy.click, adverseDrivingBtnClickProxy));

    var vehicleBtnClickProxy = new ClickProxy(this.clickVehicleBtn, this);
    this.vehicleBtn.bind("click", $.proxy(vehicleBtnClickProxy.click, vehicleBtnClickProxy));

    var ecmBtnClickProxy = new ClickProxy(function () {

        SDK.openPage(PageConfig.EcmInfo.url,
            PageConfig.EcmInfo.title);
    }, this);
    this.ecmBtn.bind("click", $.proxy(ecmBtnClickProxy.click, ecmBtnClickProxy));

    var requestEditBarClickProxy = new ClickProxy(function () {

        SDK.openPage(PageConfig.Alerts.url,
            PageConfig.Alerts.title,
            {tabIndex: this.requestEditBar.alertIndex});
    }, this);
    this.requestEditBar.on("click", $.proxy(requestEditBarClickProxy.click, requestEditBarClickProxy));

    var malfunctionBtnClickProxy = new ClickProxy(function () {

        SDK.openPage(PageConfig.Malfunction.url,
            PageConfig.Malfunction.title);
    }, this);
    this.malfunctionBtn.on("click", $.proxy(malfunctionBtnClickProxy.click, malfunctionBtnClickProxy));

    var expireTipClickProxy = new ClickProxy(function () {

        SDK.showExpireDayView(this.expireDays, null);
    }, this);
    this.expireTip.on("click", $.proxy(expireTipClickProxy.click, expireTipClickProxy));

    var iftaBtnClickProxy = new ClickProxy(function () {

        SDK.openPage(PageConfig.Ifta.url,
            PageConfig.Ifta.title);
    }, this);
    this.iftaBtn.on("click", $.proxy(iftaBtnClickProxy.click, iftaBtnClickProxy));

    var gridContainerClickProxy = new ClickProxy(function () {

        if (Global.isAndroid) {

            SDK.openGridPage(this.gridData);
        } else {

            SDK.openPage(PageConfig.ZoomGrid.url, PageConfig.ZoomGrid.title, this.gridData, Constants.WEB_CANBACK, Constants.HIDE_NAVIGATION_BAR);
        }
    }, this);
    this.gridContainer.on("click", $.proxy(gridContainerClickProxy.click, gridContainerClickProxy));
    this.init();
};

DashboardPage.prototype.init = function () {

    DASHBOARD.getOffline($.proxy(function (connected) {

        if (connected == NETWORK.CONNECTED) {

            this.offlineAlert.addClass("dn");
            this.getAlertsNum();
            // 过期提醒
            this.getExpireDay();
        } else {

            this.offlineAlert.removeClass("dn");
            this.requestEditBar.addClass("hide")
        }
    }, this));

    this.setNationFlag();
    this.cycle = new DashboardCircle();
    SDK.getTotalHour($.proxy(function (hour) {
        this.grid = new DriverGrid("grid", hour);

        this.grid.init();
        this.getStatesData();
        this.getAlertsNum();
    }, this));
    SDK.getDriverName($.proxy(function (name) {

        this.driverName.html(name);
    }, this));
};

DashboardPage.prototype.destroy = function () {

    this.cycle.destroy();
    this.grid.destroy();
};

DashboardPage.prototype.setNationFlag = function () {

    var that = this;
    SDK.getCurrentRuleId(function (data) {

        this.ruleId = data;
        if (this.ruleId == RULEID.CAR_7D_60H) {

            that.timeView.html(RULETIME.AMERICA1);
            that.canadaFlag.css({
                display: "none"
            });
            that.americaFlag.css({
                display: "block"
            });
        } else if (this.ruleId == RULEID.CAR_8D_70H) {

            that.timeView.html(RULETIME.AMERICA2);
            that.canadaFlag.css({
                display: "none"
            });
            that.americaFlag.css({
                display: "block"
            });
        } else if (this.ruleId == RULEID.CANADA_7D_70H) {

            that.timeView.html(RULETIME.CANADA1);
            that.americaFlag.css({
                display: "none"
            });
            that.canadaFlag.css({
                display: "block"
            });
        } else if (this.ruleId == RULEID.CANADA_14D_120H) {

            that.timeView.html(RULETIME.CANADA2);
            that.americaFlag.css({
                display: "none"
            });
            that.canadaFlag.css({
                display: "block"
            });
        }
    });
};

DashboardPage.prototype.getStatesData = function () {

    DASHBOARD.getDashboardTime($.proxy(function (data) {

        data = JSON.parse(data);
        this.setStateString(data.currentState);
        this.cycle.refreshWithData(data);
        this.changeStateBtn.unbind("click");
        this.currentState = data.currentState;

        var changeStateBtnClickProxy = new ClickProxy(this.clickChangeState, this);
        this.changeStateBtn.on("click", $.proxy(changeStateBtnClickProxy.click, changeStateBtnClickProxy));
    }, this));
    DAILYLOG.getTodayGrid($.proxy(function (data) {

        this.grid.initCanvas();
        this.gridData = JSON.parse(data);
        this.grid.draw(this.gridData, true);
    }, this));

    DASHBOARD.isBreak($.proxy(function (code) {

        //如果已经不是Break状态，但是页面上还是显示正在Break，则清除Break
        if (code == 0 && this.isOnBreak) {

            this.isOnBreak = false;
            this.resetStartBreakButton(this.isOnBreak);
        }

        //如果现在是Break，但是页面上不显示Break，则显示Break
        if (code == 1 && !this.isOnBreak) {

            this.isOnBreak = true;
            this.resetStartBreakButton(this.isOnBreak);
        }
    }, this));
};

DashboardPage.prototype.setSelectVehicle = function (vehicle) {

    if (vehicle) {

        this.isVehicleSelected = true;

        this.noVehicleTip.addClass("dn");
        this.vehicleTipView.removeClass("dn");
        this.vehicleCodeView.removeClass("dn");
        this.vehicleCodeView.html(vehicle.code);
    } else {

        this.isVehicleSelected = false;

        this.vehicleTipView.addClass("dn");
        this.vehicleCodeView.addClass("dn");
        this.vehicleCodeView.html("");
        this.noVehicleTip.removeClass("dn");
        this.gpsBtn.addClass("dn");
        this.ecmBtn.addClass("dn");
    }
};

DashboardPage.prototype.reload = function () {

    this.getStatesData();
    this.setNationFlag();
    this.getExpireDay();
};

DashboardPage.prototype.clickChangeState = function () {

    if (this.isOnBreak) {

        SDK.showDialog(null, {
            icon: DialogConfig.Icon_Love,
            text: String.dashboardNotChangeStatusTip,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.Cancelable
        });
        return;
    }

    SDK.openPage(PageConfig.ChangeStatus.url,
        PageConfig.ChangeStatus.title,
        {driverStatus: this.currentState});
};

DashboardPage.prototype.clickVehicleBtn = function () {

    if (this.isVehicleSelected) {

        VEHICLE.disconnectVehicle();
    } else {

        SDK.openPage(PageConfig.ConnectVehicle.url,
            PageConfig.ConnectVehicle.title);
    }
};

DashboardPage.prototype.breakSuccess = function () {

    if (this.isOnBreak) {
        this.currentState = DRIVERSTATUS.ONDUTY;
    } else {
        this.beforeState = this.currentState;
        this.currentState = DRIVERSTATUS.OFFDUTY;
    }
    this.setStateString(this.currentState);
    this.cycle.resetState(this.currentState);
    this.grid.reload(this.currentState);

    this.isOnBreak = !this.isOnBreak;
    this.resetStartBreakButton(this.isOnBreak);
};

DashboardPage.prototype.resetStartBreakButton = function (isOn) {

    if (isOn) {

        this.startBreakBtn.addClass("on");
        this.breakString.html(String.stop);
    } else {

        this.startBreakBtn.removeClass("on");
        this.breakString.html(String.start);
    }
};

DashboardPage.prototype.setStateString = function (status) {
    switch (parseInt(status)) {
        case DRIVERSTATUS.DRIVING:
            this.stateShort.html(String.driverStateDShort);
            this.state.html(String.driverStateD);
            break;
        case DRIVERSTATUS.ONDUTY:
            this.stateShort.html(String.driverStateOdndShort);
            this.state.html(String.driverStateOdnd);
            break;
        case DRIVERSTATUS.YARDMOVE:
            this.stateShort.html(String.driverYardMoveShort);
            this.state.html(String.driverYardMove);
            break;
        case DRIVERSTATUS.PERSONALUSE:
            this.stateShort.html(String.driverStatePersonalShort);
            this.state.html(String.driverStatePersonal);
            break;
        case DRIVERSTATUS.SLEEPER:
            this.stateShort.html(String.driverStateSbShort);
            this.state.html(String.driverStateSb);
            break;
        case DRIVERSTATUS.OFFDUTY:
            this.stateShort.html(String.driverStateOffShort);
            this.state.html(String.driverStateOff);
            break;
    }
};

DashboardPage.prototype.getAlertsNum = function () {

    ALERT.getAlertSummary($.proxy(this.reloadAlertData, this));
};

DashboardPage.prototype.reloadAlertData = function (data) {

    var summary = JSON.parse(data);
    var alertTypeNum = 0;
    if (summary.notCertifiedLogsCnt > 0) {
        alertTypeNum++;
        this.requestEditBar.empty();
        this.requestEditBar.append(this.tempCertify);
        this.tempCertify.find("[sid=number]").html(summary.notCertifiedLogsCnt);
        this.requestEditBar.alertIndex = 0;
    }
    if (summary.requestedEditsCnt > 0) {
        alertTypeNum++;
        this.requestEditBar.empty();
        this.requestEditBar.append(this.tempEdit);
        this.tempEdit.find("[sid=number]").html(summary.requestedEditsCnt);
        this.requestEditBar.alertIndex = 1;
    }
    if (summary.assignedCnt > 0) {
        alertTypeNum++;
        this.requestEditBar.empty();
        this.requestEditBar.append(this.tempAssigned);
        this.tempAssigned.find("[sid=number]").html(summary.assignedCnt);
        this.requestEditBar.alertIndex = 2;
    }
    if (alertTypeNum > 1) {
        this.requestEditBar.empty();
        this.requestEditBar.append(this.tempSeveral);
        this.requestEditBar.alertIndex = 0;
    }
    if (alertTypeNum > 0) {
        this.requestEditBar.removeClass("hide");
    } else {
        this.requestEditBar.addClass("hide");
    }
};

/**
 * 获取过期时间
 */
DashboardPage.prototype.getExpireDay = function () {

    DASHBOARD.getExpireDays($.proxy(this.setExpireDay, this));
};

DashboardPage.prototype.setExpireDay = function (expireDays) {

    this.expireDays = expireDays;
    if (expireDays <= 30) {

        this.expireTip.removeClass("dn");
        var days = String.daysLeft.replace("#day#", expireDays);
        if (expireDays <= 1) {
            days.replace("days", "day");
        }
        this.expireTip.html(days);
        if (this.initLogin) {
            SDK.showExpireDayView(this.expireDays, null);
        }
    } else {
        this.expireTip.addClass("dn");
    }
    this.initLogin = false;
};
