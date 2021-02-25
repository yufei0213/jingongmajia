/**
 * @author zhangyu
 * @date 2018/1/22
 * @description
 */

var page = null;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    page = new MorePage($('#page'), params);
};

/**
 * onReload后，页面再次可用时调用
 */
Web.onReload = function (params) {

    page.reload();
};

/**
 * 是否存在故障
 */
Web.networkStateChange = function (connected) {

    if (connected == NETWORK.CONNECTED) {

        ALERT.getAlertSummary($.proxy(page.onGetAlertSummaryListener, page));
        if (page.expireDays <= 30) {

            page.expireTip.removeClass("dn");
         }
    } else {

        page.alertTip.removeClass("di");
        page.expireTip.addClass("dn");
    }
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {};

    var MorePage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        this.view = view;

        this.userBox = null;
        this.nameLabel = null;
        this.timeLabel = null;
        this.carrierBox = null;
        this.carrierNameLabel = null;
        this.timeZoneLabel = null;
        this.addCodriverBtn = null;
        this.alertBtn = null;
        this.alertTip = null;
        this.ruleBtn = null;
        this.iftaBtn = null;
        this.settingBtn = null;
        this.aboutBtn = null;
        this.versionLabel = null;
        this.logoutBtn = null;
        this.helpBtn = null;

        // 初始化数据
        this.user = null;
        this.driver = null;
        this.carrier = null;
        this.expireDays = 3000;
        this.init();
    };

    MorePage.prototype.init = function () {

        this.userBox = this.view.find('[sid=userBox]');
        this.nameLabel = this.view.find('[sid=nameLabel]');
        this.timeLabel = this.view.find('[sid=timeLabel]');
        this.carrierBox = this.view.find('[sid=carrierBox]');
        this.carrierNameLabel = this.view.find('[sid=carrierNameLabel]');
        this.timeZoneLabel = this.view.find('[sid=timeZoneLabel]');
        this.addCodriverBtn = this.view.find('[sid=addCodriverBtn]');
        this.sendPdfBtn = this.view.find('[sid=sendPdfBtn]');
        this.alertBtn = this.view.find('[sid=alertBtn]');
        this.alertTip = this.view.find('[sid=alertTip]');
        this.ruleBtn = this.view.find('[sid=ruleBtn]');
        this.iftaBtn = this.view.find('[sid=iftaBtn]');
        this.settingBtn = this.view.find('[sid=settingBtn]');
        this.aboutBtn = this.view.find('[sid=aboutBtn]');
        this.versionLabel = this.view.find('[sid=versionLabel]');
        this.logoutBtn = this.view.find('[sid=logoutBtn]');
        this.helpBtn = this.view.find('[sid=helpBtn]');
        this.expireTip = this.view.find('[sid=expireTip]');
        this.expireTip.addClass("dn");

        this.userBox.on('click', $.proxy(this.onUserBoxClick, this));
        this.carrierBox.on("click", $.proxy(this.onCarrierBoxClick, this));
        this.addCodriverBtn.on("click", $.proxy(this.onAddCodriverBtnClick, this));
        this.sendPdfBtn.on("click", $.proxy(this.onSendPdfBtnClick, this));
        this.alertBtn.on('click', $.proxy(this.onAlertBtnClick, this));
        this.ruleBtn.on('click', $.proxy(this.onruleBtnClick, this));
        this.iftaBtn.on('click', $.proxy(this.oniftaBtnClick, this));
        this.settingBtn.on('click', $.proxy(this.onSettingBtnClick, this));
        this.aboutBtn.on('click', $.proxy(this.onAboutBtnClick, this));
        this.logoutBtn.on('click', $.proxy(this.onLogoutBtnClick, this));
        this.helpBtn.on('click', $.proxy(this.onHelpBtnClick, this));
        this.expireTip.on('click', $.proxy(this.exprieTipClick, this));

        USER.getUserFunc($.proxy(function (funcId) {

            if (funcId != USERFUNCTION.NORMAL) {

                this.addCodriverBtn.addClass("dn");
            } else {

                USER.getUserRole($.proxy(function (roleId) {

                    if (roleId == USERROLE.NORMAL) {

                        this.addCodriverBtn.removeClass("dn");
                    } else {

                        this.addCodriverBtn.addClass("dn");
                    }
                }, this));
            }
        }, this));

        USER.getUser($.proxy(this.getUserDataListener, this));
        USER.getDriver($.proxy(this.getDriverDataListener, this));
        USER.getCarrier($.proxy(this.getCarrierDataListener, this));
        SDK.getVersionName($.proxy(this.getVersionNameListener, this));

        ALERT.getAlertSummary($.proxy(this.onGetAlertSummaryListener, this));
        // 设置过期时间
        this.getExpireDay();
    };

    MorePage.prototype.reload = function () {

        USER.getDriver($.proxy(this.getDriverDataListener, this));
        ALERT.getAlertSummary($.proxy(this.onGetAlertSummaryListener, this));
        USER.getUserFunc($.proxy(function (funcId) {

            if (funcId != USERFUNCTION.NORMAL) {

                this.addCodriverBtn.addClass("dn");
            } else {

                USER.getUserRole($.proxy(function (roleId) {

                    if (roleId == USERROLE.NORMAL) {

                        this.addCodriverBtn.removeClass("dn");
                    } else {

                        this.addCodriverBtn.addClass("dn");
                    }
                }, this));
            }
        }, this));

        // 设置过期提箱
        this.getExpireDay();
    };

    MorePage.prototype.getUserDataListener = function (data) {

        this.user = JSON.parse(data);
    };

    MorePage.prototype.getDriverDataListener = function (data) {

        this.driver = JSON.parse(data);
        this.nameLabel.text(this.driver.name);
        this.timeLabel.text(Math.round(this.driver.rule.dutyTime / 60) + "h/" + this.driver.rule.dutyDays + "days");
    };

    MorePage.prototype.getCarrierDataListener = function (data) {

        this.carrier = JSON.parse(data);
        this.carrierNameLabel.text(this.carrier.name);
        this.timeZoneLabel.text(this.carrier.timeZoneAlias);
    };

    MorePage.prototype.getVersionNameListener = function (data) {

        this.versionLabel.text(String.moreVersionTip.replace("#code#", data));
    };

    MorePage.prototype.onGetAlertSummaryListener = function (data) {

        var summary = JSON.parse(data);
        if ((summary.notCertifiedLogsCnt + summary.requestedEditsCnt + summary.assignedCnt) > 0) {

            this.alertTip.addClass('di');
        } else {

            this.alertTip.removeClass('di');
        }
    };

    MorePage.prototype.onUserBoxClick = function () {

        var params = {
            user: this.user,
            driver: this.driver
        };

        SDK.openPage(PageConfig.DriverInfo.url,
            PageConfig.DriverInfo.title,
            params);
    };

    MorePage.prototype.onCarrierBoxClick = function () {

        var params = {
            carrier: this.carrier
        };

        SDK.openPage(PageConfig.CarrierInfo.url,
            PageConfig.CarrierInfo.title,
            params);
    };

    MorePage.prototype.onAddCodriverBtnClick = function () {

        DASHBOARD.getOffline($.proxy(function (connected) {

            if (connected == NETWORK.CONNECTED) {

                VEHICLE.getCurrentVehicle($.proxy(function (vehicle) {

                    if (vehicle) {

                        SDK.openPage(PageConfig.AddCopilot.url,
                            PageConfig.AddCopilot.title);
                    } else {

                        SDK.showDialog(null,
                            {
                                icon: DialogConfig.Icon_Love,
                                text: String.notCopilot,
                                positiveBtnText: String.ok,
                                cancelable: DialogConfig.Cancelable
                            });
                    }
                }, this));

            } else {

                SDK.showDialog(null,
                    {
                        icon: DialogConfig.Icon_Love,
                        text: String.offlineNotCopilot,
                        positiveBtnText: String.ok,
                        cancelable: DialogConfig.Cancelable
                    });
            }
        }, this));
    };

    MorePage.prototype.exprieTipClick = function () {
        SDK.showExpireDayView(this.expireDays, null);
        return false;
    };

    MorePage.prototype.onSendPdfBtnClick = function () {

        SDK.openPage(PageConfig.SendPdf.url,
            PageConfig.SendPdf.title);
    };

    MorePage.prototype.onAlertBtnClick = function () {

        SDK.openPage(PageConfig.Alerts.url,
            PageConfig.Alerts.title);
    };

    MorePage.prototype.onruleBtnClick = function () {

        SDK.openPage(PageConfig.CycleRule.url,
            PageConfig.CycleRule.title);
    };

    MorePage.prototype.oniftaBtnClick = function () {

        SDK.openPage(PageConfig.Ifta.url,
            PageConfig.Ifta.title);
    };

    MorePage.prototype.onSettingBtnClick = function () {

        var params = {
            user: this.user
        };

        SDK.openPage(PageConfig.Settings.url,
            PageConfig.Settings.title,
            params);
    };

    MorePage.prototype.onAboutBtnClick = function () {

        var params = {
            versionName: this.versionLabel.text(),
            fullSize: 1
        };

        SDK.openPage(PageConfig.About.url,
            PageConfig.About.title,
            params);
    };

    MorePage.prototype.onLogoutBtnClick = function () {

        if (Global.isButtonProcessing) {
            return;
        }
        Global.isButtonProcessing = true;

        // 检查司机是否在OffDuty状态
        DRIVING.getDriverStatus(function (data) {

            var driver = JSON.parse(data);
            if (driver.currentState == DRIVERSTATUS.OFFDUTY) {
                SDK.showLoading();
                // 获取司机Not Certified Alert数量
                USER.getDriverNotCertifiedAlertStatus(function (notCertifiedLogsCnt) {

                    SDK.hideLoading();
                    Global.isButtonProcessing = false;
                    if (notCertifiedLogsCnt == Constants.CALLBACK_FAILURE) {
                        // 提醒请求失败再次尝试
                        var dialogConfig = {
                            icon: DialogConfig.Icon_Awkward,
                            text: String.logoutRequestFailed,
                            positiveBtnText: String.ok,
                            cancelable: DialogConfig.Cancelable
                        };
                        Global.isButtonProcessing = false;
                        SDK.showDialog(function () {
                        }, dialogConfig);
                    } else if (notCertifiedLogsCnt == 0) {
                        // 司机请求登出确认
                        var dialogConfig = {
                            icon: DialogConfig.Icon_Msg,
                            text: String.logoutConfirm,
                            negativeBtnText: String.cancel,
                            neutralBtnText: String.logout
                        };
                        Global.isButtonProcessing = false;
                        SDK.showDialog(function (which) {
                            if (which == Constants.BUTTON_NEUTRAL) {
                                // 司机请求服务器登出
                                SDK.showLoading();
                                USER.logoutRequest(function (data) {
                                    SDK.hideLoading();
                                    Global.isButtonProcessing = false;
                                    if (data == Constants.CALLBACK_SUCCESS) {
                                        // 司机本地登出
                                        USER.logoutLocal();
                                    } else {
                                        // 提醒请求失败再次尝试
                                        var dialogConfig = {
                                            icon: DialogConfig.Icon_Awkward,
                                            text: String.logoutRequestFailed,
                                            positiveBtnText: String.ok,
                                            cancelable: DialogConfig.Cancelable
                                        };
                                        Global.isButtonProcessing = false;
                                        SDK.showDialog(function () {
                                        }, dialogConfig);
                                    }
                                })
                            } else {
                                Global.isButtonProcessing = false;
                            }
                        }, dialogConfig);
                    } else {
                        // 提醒司机去签名
                        var dialogConfig = {
                            icon: DialogConfig.Icon_Msg,
                            text: String.logoutNotCertifiedTip,
                            negativeBtnText: String.logoutAnyway,
                            neutralBtnText: String.logoutSign,
                            cancelable: 1
                        };
                        Global.isButtonProcessing = false;
                        SDK.showVerticalDialog(function (which) {
                            if (which == Constants.BUTTON_NEGATIVE) {
                                // 司机请求服务器登出
                                SDK.showLoading();
                                USER.logoutRequest(function (data) {
                                    SDK.hideLoading();
                                    Global.isButtonProcessing = false;
                                    if (data == Constants.CALLBACK_SUCCESS) {
                                        // 司机本地登出
                                        USER.logoutLocal();
                                    } else {
                                        // 提醒请求失败再次尝试
                                        var dialogConfig = {
                                            icon: DialogConfig.Icon_Awkward,
                                            text: String.logoutRequestFailed,
                                            positiveBtnText: String.ok,
                                            cancelable: DialogConfig.Cancelable
                                        };
                                        Global.isButtonProcessing = false;
                                        SDK.showDialog(function () {
                                        }, dialogConfig);
                                    }
                                });


                            } else if (which == Constants.BUTTON_NEUTRAL) {

                                // Firebase 埋点
                                SDK.collectFirebaseEvent(FIREBASE.EVENT_CATEGORY_LOGOUT, FIREBASE.EVENT_ACTION_CLICK, "notsignalert-sign");

                                // 跳转到Alert列表页面
                                SDK.openPage(PageConfig.Alerts.url,
                                    PageConfig.Alerts.title);
                            }
                        }, dialogConfig);

                        // Firebase 埋点
                        SDK.collectFirebaseEvent(FIREBASE.EVENT_CATEGORY_LOGOUT, FIREBASE.EVENT_ACTION_CLICK, "notsignalert");
                    }
                })
            } else {
                // 提醒设定司机状态为OffDuty
                var driverStatus = String.driverStateD;
                switch (parseInt(driver.currentState)) {
                    case DRIVERSTATUS.DRIVING:
                        driverStatus = String.driverStateD;
                        break;
                    case DRIVERSTATUS.ONDUTY:
                        driverStatus = String.driverStateOdnd;
                        break;
                    case DRIVERSTATUS.YARDMOVE:
                        driverStatus = String.driverYardMove;
                        break;
                    case DRIVERSTATUS.PERSONALUSE:
                        driverStatus = String.driverStatePersonal;
                        break;
                    case DRIVERSTATUS.SLEEPER:
                        driverStatus = String.driverStateSb;
                        break;
                    case DRIVERSTATUS.OFFDUTY:
                        driverStatus = String.driverStateOff;
                        break;
                }
                var tips = String.moreLogoutStatusTip.replace("#driverStatus#", driverStatus);
                var dialogConfig = {
                    icon: DialogConfig.Icon_Msg,
                    text: tips,
                    negativeBtnText: String.logoutAnyway,
                    neutralBtnText: String.logoutChange,
                    cancelable: 1
                };
                Global.isButtonProcessing = false;
                SDK.showVerticalDialog(function (which) {
                    if (which == Constants.BUTTON_NEGATIVE) {
                        // 司机请求服务器登出
                        SDK.showLoading();
                        USER.logoutRequest(function (data) {
                            SDK.hideLoading();
                            Global.isButtonProcessing = false;
                            if (data == Constants.CALLBACK_SUCCESS) {
                                // 司机本地登出
                                USER.logoutLocal();
                            } else {
                                // 提醒请求失败再次尝试
                                var dialogConfig = {
                                    icon: DialogConfig.Icon_Awkward,
                                    text: String.logoutRequestFailed,
                                    positiveBtnText: String.ok,
                                    cancelable: DialogConfig.Cancelable
                                };
                                Global.isButtonProcessing = false;
                                SDK.showDialog(function () {
                                }, dialogConfig);
                            }
                        })
                    } else if (which == Constants.BUTTON_NEUTRAL) {

                        // Firebase 埋点
                        SDK.collectFirebaseEvent(FIREBASE.EVENT_CATEGORY_LOGOUT, FIREBASE.EVENT_ACTION_CLICK, "notoffalert-change");

                        // 跳转到司机状态设定页面
                        SDK.openPage(PageConfig.ChangeStatus.url,
                            PageConfig.ChangeStatus.title,
                            {driverStatus: driver.currentState});
                    }
                }, dialogConfig);

                // Firebase 埋点
                SDK.collectFirebaseEvent(FIREBASE.EVENT_CATEGORY_LOGOUT, FIREBASE.EVENT_ACTION_CLICK, "notoffalert");

            }
        })
    };
    // 获取过期时间
    MorePage.prototype.getExpireDay =  function (){

        DASHBOARD.getExpireDays($.proxy(this.setExpireDay, this));
    };

    MorePage.prototype.setExpireDay = function (expireDays) {

        this.expireDays = expireDays;
        if (expireDays <= 30){

                    this.expireTip.removeClass("dn");
                    var days = String.daysLeft.replace("#day#", expireDays);
                    if (expireDays <= 1) {
                        days.replace("days", "day");
                    }
                    this.expireTip.html(days);
                }else {
                    this.expireTip.addClass("dn");
                }
    };
    MorePage.prototype.onHelpBtnClick = function () {

        SDK.openPage(PageConfig.Help.url,
            PageConfig.Help.title);
    };

    window.MorePage = MorePage;
}($, window));