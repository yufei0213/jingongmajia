/**
 * @author yufei0213
 * @date 2018/1/27
 * @description driving页面
 */

/**
 * 警告状态下，className
 * @type {string}
 */
Web.waringClass = "violationah";

/**
 * 车辆运动，className
 * @type {string}
 */
Web.inmotionClass = "inmotion";

/**
 * 车辆静止，className
 * @type {string}
 */
Web.stationaryClass = "stationary";

/**
 * 解锁按钮可用小图标，className
 * @type {string}
 */
Web.btnAvailableClass = "iarrow";

/**
 * ecm可用时，className
 * @type {string}
 */
Web.ecmAvalableClass = "ecm";

/**
 * ecm不可用时，className
 * @type {string}
 */
Web.ecmUnavailableClass = "gps";

/**
 * 是否显示Adverse和Break按钮的Class
 * @type {string}
 */
Web.cycleAndBtnBoxClass = "withOption";

/**
 * 非警告状态主题色
 * @type {string}
 */
Web.normalColor = "#4CD964";

/**
 * 警告状态下主题色
 * @type {string}
 */
Web.warnColor = "#FF6D36";

/**
 * 小时和秒转换比
 * @type {number}
 */
Web.h2sScale = 3600;

/**
 * 分钟和秒的转换比
 * @type {number}
 */
Web.m2sScale = 60;

/**
 * 剩余多长时间开始切换警告样式，单位小时
 * @type {number}
 */
Web.hintTime = 1 / 6;

/**
 * 界面刷新时间
 * @type {number}
 */
Web.refreshTime = 1000;

/**
 * 弹窗同意
 * @type {number}
 */
Web.dialogConfirm = 1;

/**
 * 弹窗拒绝
 * @type {number}
 */
Web.dialogRefuse = -1;

/**
 * 五分钟后开启弹窗
 * @type {number}
 */
Web.dialogDuration = 5 * 60 * 1000;

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 *
 */
Web.init = function (params) {

    //初始化
    this.initViews();

    //添加车辆状态监听
    DRIVING.setVehicleStatusListener($.proxy(this.onVehicleStatusChange, this));
};

/**
 * 页面重新加载时调用
 */
Web.onReload = function (params) {

    //添加车辆状态监听
    DRIVING.setVehicleStatusListener($.proxy(this.onVehicleStatusChange, this));

    if (this.remainTimer) {

        window.clearInterval(this.remainTimer);
    }
    this.remainTimer = setInterval(this.updateRemainTime, this.refreshTime, this);
};

/**
 * 页面暂停时调用
 */
Web.onPause = function () {

    //解除车辆状态监听
    DRIVING.cancelVehicleStatusListener();

    if (this.remainTimer) {

        window.clearInterval(this.remainTimer);
    }
};

/**
 * 团队驾驶状态变化
 * @param data
 */
Web.teamWorkDashBoardChange = function (data) {

    USER.getUserRole($.proxy(function (code) {

        if (code != USERROLE.NORMAL) {

            this.teamWorkIcon.removeClass("dn");
        } else {

            this.teamWorkIcon.addClass("dn");
        }
    }, this));
};

/**
 * 司机状态发生变化
 */
Web.hosModelChangeMessage = function () {

    this.updateCycle();
};

/**
 * 车辆连接状态变化
 */
Web.vehicleConnectStateChange = function (code) {

    if (code == VEHICLECONNECTSTATE.CONNECTED) {

        this.vehicleCodeView.removeClass(this.ecmUnavailableClass);
        this.vehicleCodeView.addClass(this.ecmAvalableClass);
    } else if (code == VEHICLECONNECTSTATE.DISCONNECTED) {

        this.vehicleCodeView.removeClass(this.ecmAvalableClass);
        this.vehicleCodeView.addClass(this.ecmUnavailableClass);
    } else if (code == VEHICLECONNECTSTATE.CLEAR) {

    }
};

/**
 * 初始化解锁按钮
 */
Web.initViews = function () {

    this.vehicleStateView = $("#vehicleStateView");
    this.vehicleCodeView = $("#vehicleCodeView");

    this.cycleAndBtnBoxView = $("#cycleAndBtnBox");

    this.cycleView = $("#cycleView");

    this.personalUseView = $("#personalUseView");
    this.yardMoveView = $("#yardMoveView");
    this.remainTipView = $("#remainTipView");
    this.remainView = $("#remainView");
    this.pastTipView = $("#pastTipView");
    this.pastView = $("#pastView");

    this.teamWorkIcon = $("#teamWorkIcon");
    this.driverNameView = $("#driverName");

    this.movingBtnGroup = $("#movingBtnGroup");
    this.stationaryBtnGroup = $("#stationaryBtnGroup");

    this.adverseBtn = $("#adverseBtn");
    this.breakBtn = $("#breakBtn");

    this.driverStatusMView = $("#driverStatusMView");
    this.driverStatusSView = $("#driverStatusSView");

    USER.getUserFunc($.proxy(function (code) {

        SDK.log("USER.getUserFunc: " + code, Tags.DRIVINGPAGE, true);

        this.userFunc = code;
        if (this.userFunc != USERFUNCTION.NORMAL) {

            this.cycleAndBtnBoxView.removeClass(this.cycleAndBtnBoxClass);
        } else {

            var adverseBtnClickProxy = new ClickProxy(function () {

                DASHBOARD.openAdverseDrivingDialog();
            }, this);
            this.adverseBtn.bind("click", $.proxy(adverseBtnClickProxy.click, adverseBtnClickProxy));

            var breakBtnClickProxy = new ClickProxy(function () {

                if (this.driverStatus.currentState == DRIVERSTATUS.OFFDUTY || this.driverStatus.currentState == DRIVERSTATUS.PERSONALUSE) {

                    SDK.showDialog(null, {
                        icon: DialogConfig.Icon_Love,
                        text: String.dashboardNotBreakTip,
                        positiveBtnText: String.ok,
                        cancelable: DialogConfig.Cancelable
                    });
                } else {

                    DASHBOARD.openStartBreakDialog({data: {isOn: false}}, $.proxy(function () {

                        SDK.openMainPage();
                    }, this));
                }
            }, this);
            this.breakBtn.bind("click", $.proxy(breakBtnClickProxy.click, breakBtnClickProxy));
        }
    }, this));

    var driverStatusSViewClickProxy = new ClickProxy(function () {

        SDK.openPage(PageConfig.ChangeStatus.url,
            PageConfig.ChangeStatus.title,
            {driverStatus: this.driverStatus.currentState});
    }, this);
    this.driverStatusSView.bind("click", $.proxy(driverStatusSViewClickProxy.click, driverStatusSViewClickProxy));

    USER.getDriver($.proxy(function (data) {

        SDK.log("USER.getDriver: " + data, Tags.DRIVINGPAGE, true);

        this.driver = JSON.parse(data);

        this.driverNameView.html(this.driver.name);

        this.maxDrivingTime = this.driver.rule.maxDriving * this.m2sScale; //能够驾驶的总时间，分钟转换为秒

        this.cycleViewPerimeter = this.cycleView.attr("r") * Math.PI * 2;
        this.updateCycle();
    }, this));
    USER.getUserRole($.proxy(function (code) {

        SDK.log("USER.getUserRole: " + code, Tags.DRIVINGPAGE, true);

        if (code != USERROLE.NORMAL) {

            this.teamWorkIcon.removeClass("dn");
        } else {

            this.teamWorkIcon.addClass("dn");
        }
    }, this));
    VEHICLE.getCurrentVehicle($.proxy(function (data) {

        SDK.log("VEHICLE.getCurrentVehicle: " + data, Tags.DRIVINGPAGE, true);

        this.vehicle = JSON.parse(data);
        this.vehicleCodeView.html(this.vehicle.code);
    }, this));
    SDK.getDataCollectorType($.proxy(function (type) {

        SDK.log("SDK.getDataCollectorType: " + type, Tags.DRIVINGPAGE, true);

        if (type == DATACOLLECTORTYPE.DEVICE) {

            this.vehicleCodeView.removeClass(this.ecmUnavailableClass);
            this.vehicleCodeView.addClass(this.ecmAvalableClass);
        } else if (type == DATACOLLECTORTYPE.GPS) {

            this.vehicleCodeView.removeClass(this.ecmAvalableClass);
            this.vehicleCodeView.addClass(this.ecmUnavailableClass);
        }
    }, this));
};

/**
 * 更新仪表盘
 */
Web.updateCycle = function () {

    DRIVING.getDriverStatus($.proxy(function (data) {

        //SDK.log("Web.updateCycle DRIVING.getDriverStatus: " + data, Tags.DRIVINGPAGE, true);

        this.driverStatus = JSON.parse(data);

        if (this.driverStatus.currentState == DRIVERSTATUS.PERSONALUSE) {

            this.showPersonalUse();
        } else if (this.driverStatus.currentState == DRIVERSTATUS.YARDMOVE) {

            this.showYardMove();
        } else {

            this.driverStatus.currentState = DRIVERSTATUS.DRIVING;
            this.showNormal();

            this.remainDriveTime = this.driverStatus.driveSecond; //剩余驾驶时间
            this.pastDriveTime = this.driverStatus.pastSecond; //已经驾驶时间

            var dashArray = this.cycleViewPerimeter;
            var offset = this.cycleViewPerimeter * (1 - this.remainDriveTime / this.maxDrivingTime);
            offset = offset < 0 ? 0 : offset;

            this.cycleView.css({
                strokeDasharray: dashArray + "px",
                strokeDashoffset: offset + "px",
                stroke: this.normalColor
            });

            if (this.remainTimer) {

                window.clearInterval(this.remainTimer);
            }
            this.remainTimer = setInterval(this.updateRemainTime, this.refreshTime, this);

            this.remainView.animate({strokeDashoffset: dashArray - 0.1 + "px"},
                this.remainDriveTime * 1000,
                'linear');
        }
    }, this));
};

/**
 * 更新剩余驾驶时间
 */
Web.updateRemainTime = function (that) {

    DRIVING.getDriverStatus($.proxy(function (data) {

        //SDK.log("Web.updateRemainTime DRIVING.getDriverStatus: " + data, Tags.DRIVINGPAGE, true);

        that.driverStatus = JSON.parse(data);

        if (that.driverStatus.currentState != DRIVERSTATUS.PERSONALUSE && that.driverStatus.currentState != DRIVERSTATUS.YARDMOVE) {

            that.remainDriveTime = that.driverStatus.driveSecond; //剩余驾驶时间
            that.pastDriveTime = that.driverStatus.pastSecond; //已经驾驶时间

            var dashArray = that.cycleViewPerimeter;
            var offset = that.cycleViewPerimeter * (1 - that.remainDriveTime / that.maxDrivingTime);
            offset = offset < 0 ? 0 : offset;

            that.cycleView.css({
                strokeDasharray: dashArray + "px",
                strokeDashoffset: offset + "px"
            });
            var h = parseInt(that.remainDriveTime / that.h2sScale);
            var m = parseInt((that.remainDriveTime - (h * that.h2sScale)) / 60);
            var s = parseInt(that.remainDriveTime - (h * that.h2sScale) - (m * 60));
            if (h < 10) {

                h = "0" + h;
            }
            if (m < 10) {

                m = "0" + m;
            }
            if (s < 10) {

                s = "0" + s;
            }
            if (that.remainDriveTime >= that.hintTime * that.h2sScale) {

                that.remainView.html(h + ":" + m);
            } else {

                that.showWarn();
                that.remainView.html(h + ":" + m + ":" + s);
            }
            if (that.remainDriveTime <= 0) {

                that.remainView.css({
                    fill: that.warnColor
                });
            }

            //处理pasttime
            var h = parseInt(that.pastDriveTime / that.h2sScale);
            var m = parseInt((that.pastDriveTime - (h * that.h2sScale)) / 60);
            if (h < 10) {

                h = "0" + h;
            }
            if (m < 10) {

                m = "0" + m;
            }

            that.pastView.html(h + ":" + m);
        }
    }, that));
};

/**
 * 显示弹窗
 */
Web.showDialog = function (that) {

    DRIVING.showDialog($.proxy(that.onDialogCallback, that));
};

/**
 *
 * @param data
 */
Web.onDialogCallback = function (data) {

    if (data == this.dialogConfirm) {

        SDK.openMainPage();
    }
};

/**
 * 提供给java端，用于监听车辆状态变化
 */
Web.onVehicleStatusChange = function (data) {

    if (data == VEHICLESTATUS.MOVING) {

        DASHBOARD.closeAdverseDrivingDialog();
        DASHBOARD.closeStartBreakDialog();

        this.vehicleStateView.html(String.drivingModelVehicleMove);
        this.vehicleStateView.removeClass(this.stationaryClass);
        this.vehicleStateView.addClass(this.inmotionClass);

        this.lock();

        // if (this.dialogDelay) {
        //
        //     SDK.log("vehicle moving, stop 5 mins count down.", Tags.DRIVINGPAGE, true);
        //     clearTimeout(this.dialogDelay);
        //     this.dialogDelay = null;
        // }
    } else if (data == VEHICLESTATUS.STATIC) {

        this.vehicleStateView.html(String.drivingModelVehicleStatic);
        this.vehicleStateView.removeClass(this.inmotionClass);
        this.vehicleStateView.addClass(this.stationaryClass);

        this.unlock();

        // if (this.driverStatus.currentState == DRIVERSTATUS.DRIVING && !this.dialogDelay) {
        //
        //     SDK.log("vehicle stopped, start 5 mins count down.", Tags.ECM, true);
        //     this.dialogDelay = setTimeout(this.showDialog, this.dialogDuration, this);
        // }
    } else if (data == VEHICLESTATUS.UNKNOWN) {

        this.unlock();

        // if (this.dialogDelay) {
        //
        //     clearTimeout(this.dialogDelay);
        //     this.dialogDelay = null;
        // }
    }
};

/**
 * 锁住界面
 */
Web.lock = function () {

    this.cycleAndBtnBoxView.removeClass(this.cycleAndBtnBoxClass);

    this.stationaryBtnGroup.addClass("dn");
    this.movingBtnGroup.removeClass("dn");
};

/**
 * 解锁界面
 */
Web.unlock = function () {

    this.movingBtnGroup.addClass("dn");
    this.stationaryBtnGroup.removeClass("dn");

    //如果当前状态不是Driving，则隐藏不利条件驾驶按钮
    if (this.driverStatus.currentState != DRIVERSTATUS.DRIVING || this.userFunc != USERFUNCTION.NORMAL) {

        this.cycleAndBtnBoxView.removeClass(this.cycleAndBtnBoxClass);
    } else {

        this.cycleAndBtnBoxView.addClass(this.cycleAndBtnBoxClass);
    }
};

/**
 * 展示personaluse模式
 */
Web.showPersonalUse = function () {

    this.remainTipView.css({
        display: "none"
    });
    this.remainView.css({
        display: "none"
    });
    this.pastTipView.css({
        display: "none"
    });
    this.pastView.css({
        display: "none"
    });
    this.yardMoveView.css({
        display: "none"
    });
    this.personalUseView.css({
        display: "block"
    });
    this.driverStatusMView.html(String.drivingModelPersonalUse);
    this.driverStatusSView.html(String.drivingModelPersonalUse);
};

/**
 * 展示yardMove模式
 */
Web.showYardMove = function () {

    this.remainTipView.css({
        display: "none"
    });
    this.remainView.css({
        display: "none"
    });
    this.pastTipView.css({
        display: "none"
    });
    this.pastView.css({
        display: "none"
    });
    this.yardMoveView.css({
        display: "block"
    });
    this.personalUseView.css({
        display: "none"
    });
    this.driverStatusMView.html(String.drivingModelYardMove);
    this.driverStatusSView.html(String.drivingModelYardMove);
};

/**
 * 展示正常模式
 */
Web.showNormal = function () {

    this.remainTipView.css({
        display: "block"
    });
    this.remainView.css({
        display: "block"
    });
    this.pastTipView.css({
        display: "block"
    });
    this.pastView.css({
        display: "block"
    });
    this.yardMoveView.css({
        display: "none"
    });
    this.personalUseView.css({
        display: "none"
    });
    this.driverStatusMView.html(String.drivingModelDriving);
    this.driverStatusSView.html(String.drivingModelDriving);
};

/**
 * 进入倒计时后的样式
 */
Web.showWarn = function () {

    this.cycleView.css({
        stroke: this.warnColor
    });
    this.remainTipView.css({
        fill: this.warnColor
    });
    this.remainView.css({
        fill: this.warnColor,
        fontSize: "36px",
        letterSpacing: "-1px"
    });
    this.pastTipView.css({
        fill: this.warnColor
    });
    this.pastView.css({
        fill: this.warnColor
    });
    this.movingBtnGroup.addClass(this.waringClass);
};
