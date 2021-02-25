/**
 * @author yufei0213
 * @date 2018/1/23
 * @description 修改司机状态页面
 */

/**
 * 司机当前状态
 * @type {number}
 */
Web.currentDriverStatus = 0;
/**
 * 记录当前选择何种状态
 * @type {number}
 */
Web.driverStatus = 0;
/**
 * 用于在html上记录code的属性名称
 * @type {string}
 */
Web.statusCodeAttr = "code";

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 * 打开此页面的参数 {driverStatus:2}
 * web模式调试时，url尾部添加  ?params={"driverStatus":2}
 */
Web.init = function (params) {

    this.currentDriverStatus = params.driverStatus;
    this.driverStatus = this.currentDriverStatus;

    //初始化状态选择
    this.initStatusBox();

    //初始化位置选择器
    this.initLocation();

    //初始化remark输入框
    this.initRemark();

    //save按钮点击事件
    this.saveBtn = $("#saveBtn");
    this.saveBtn.bind("click", $.proxy(this.onSaveBtnClick, this));

    SDK.collectFirebaseScreen("change-status");
};

/**
 * 初始化状态选择
 */
Web.initStatusBox = function () {

    USER.getDriver($.proxy(function (data) {

        this.driver = JSON.parse(data);
        USER.getUserRole($.proxy(function (roleId) {

            if (roleId == USERROLE.COPILOT) {

                $("#drivingBox").addClass("disable");
                $("#yardMoveBox").addClass("disable");
                $("#personalUserBox").addClass("disable");
            } else {

                if (this.driver.yardMove == 1) {

                    $("#yardMoveBox").removeClass("dn");
                }
                if (this.driver.personalUse == 1) {

                    $("#personalUserBox").removeClass("dn");
                }
            }
        }, this));
    }, this));

    VEHICLE.getCurrentVehicle($.proxy(function (data) {

        this.hasSelectedVehicle = false;
        if (data) {

            this.hasSelectedVehicle = true;
        }

        var that = this;
        $("#statusBox").find(".item").each(function (index, element) {

            var item = $(element);
            var itemCode = item.attr(that.statusCodeAttr);

            if (that.driverStatus == itemCode) {

                item.addClass("on");
            }

            if (!that.hasSelectedVehicle &&
                (itemCode == DRIVERSTATUS.DRIVING || itemCode == DRIVERSTATUS.YARDMOVE || itemCode == DRIVERSTATUS.PERSONALUSE)) {

                item.addClass("disable");
            } else {

                item.on("click", function () {

                    if ($(this).hasClass("disable")) {

                        return;
                    }

                    $("#statusBox").find(".item").each(function (index, element) {

                        $(element).removeClass("on");
                    });

                    $(this).addClass("on");
                    that.driverStatus = $(element).attr(that.statusCodeAttr);
                    that.updateRemarkOptions();

                    that.handleSaveBtnStatus();
                });
            }
        });
    }, this));
};

/**
 * 初始化位置选择器
 */
Web.initLocation = function () {

    this.locationInputView = $("#locationInput");
    this.getLocationLoading = $("#getLocationLoading");
    this.getLocationBtn = $("#getLocationBtn");

    this.locationBox = $('[sid=locationBox]');

    var that = this;
    this.locationInputView.focus(function () {

        that.locationBox.addClass("focus");
    });
    this.locationInputView.blur(function () {

        that.locationBox.removeClass("focus");
    });
    this.locationInputView.bind("input propertychange", $.proxy(function () {

        this.latitude = LatLngSpecial.M;
        this.longitude = LatLngSpecial.M;

        //处理save按钮的状态
        this.handleSaveBtnStatus();
        this.locationBox.removeClass("warning");
    }, this));

    this.getGeoLocation();
};

/**
 * 获取geo-location
 */
Web.getGeoLocation = function () {

    this.latitude = 0;
    this.longitude = 0;
    this.locationInputView.val("");

    this.getLocationLoading.removeClass("dn");
    this.getLocationBtn.addClass("dn");
    this.getLocationBtn.unbind("click");

    SDK.getGeoLocation($.proxy(function (code, latitude, longitude, location) {

        this.getLocationLoading.addClass("dn");
        this.getLocationBtn.removeClass("dn");

        this.getLocationBtn.bind("click", $.proxy(this.getGeoLocation, this));

        if (code == Constants.CALLBACK_SUCCESS) {

            this.locationBox.removeClass("warning");

            this.latitude = latitude;
            this.longitude = longitude;

            this.locationInputView.val(location);
        } else if (code == Constants.CALLBACK_FAILURE) {

            this.latitude = LatLngSpecial.M;
            this.longitude = LatLngSpecial.M;

            this.locationInputView.removeAttr("disabled");
        }
    }, this));
};

/**
 * 初始化remark输入框
 */
Web.initRemark = function () {

    SDK.getRemarkList($.proxy(function (data) {

        this.remarkOptions = [];

        this.allRemarkOptions = [];
        this.odndRemarkOptions = [];
        this.offRemarkOptions = [];
        this.drivingRemarkOption = [];
        this.sbRemarkOptions = [];

        var temp = JSON.parse(data);
        for (var i in temp) {

            var item = temp[i];
            this.allRemarkOptions.push(item.value);

            if (item.type == REMARKTYPE.ODND) {

                this.odndRemarkOptions.push(item.value);
            } else if (item.type == REMARKTYPE.OFF) {

                this.offRemarkOptions.push(item.value);
            } else if (item.type == REMARKTYPE.DRIVING) {

                this.drivingRemarkOption.push(item.value);
            } else if (item.type == REMARKTYPE.SB) {

                this.sbRemarkOptions.push(item.value);
            }
        }

        this.updateRemarkOptions();
    }, this));

    this.remarkInputView = $("#remarkInputView");
    autoTextarea(this.remarkInputView.get(0));
    this.remarkOptionView = $("#remarkOptionView");

    this.remarkBox = $('[sid=remarkBox]');

    var that = this;
    this.remarkInputView.focus(function () {

        that.remarkBox.addClass("focus");
        that.handleRemarkInput();
        if (!that.preventKeyBoard && !that.remarkInputView.val() && that.remarkOptionView.children()) {

            that.preventKeyBoard = true;
            that.remarkInputView.blur();

            $(".page").bind("click", function () {

                $(this).unbind("click");
                that.preventKeyBoard = false;
                that.remarkBox.removeClass("focus");
                setTimeout(function () {

                    that.hideAllOptions();
                }, 150);
            });
        } else {

            that.preventKeyBoard = false;
        }
    });
    this.remarkInputView.blur(function () {

        if (!that.preventKeyBoard) {

            that.remarkBox.removeClass("focus");
            setTimeout(function () {

                that.hideAllOptions();
            }, 150);
        }
    });

    this.remarkInputView.bind("input propertychange", $.proxy(function () {

        this.handleSaveBtnStatus();
        this.handleRemarkInput();
    }, this));
    this.remarkInputView.bind("click", function () {

        return false;
    });
};

/**
 * 处理RemarkInput变化
 */
Web.handleRemarkInput = function () {

    var inputVal = this.remarkInputView.val().toLowerCase();

    if (inputVal.length > 0) {

        this.showPartOptions(inputVal);
    } else {

        this.showAllOptions();
    }
};

/**
 * 展示过滤后的Options
 * @param inputVal 过滤条件
 */
Web.showPartOptions = function (inputVal) {

    this.remarkBox.removeClass("warning");


    inputVal = inputVal.trim();

    var optionsView = "";
    for (var i in this.remarkOptions) {

        var option = this.remarkOptions[i];

        var optionSplit = option.split(" ");
        for (var y in optionSplit) {

            if (optionSplit[y].toLowerCase().indexOf(inputVal) == 0) {

                optionsView += "<li>" + option + "</li>";
                break;
            }
        }
    }

    if (optionsView.length > 0) {

        this.remarkOptionView.empty();
        this.remarkOptionView.append(optionsView);

        this.remarkInputView.parent().parent().parent().addClass("show-option");
        this.remarkOptionView.bind("click", $.proxy(this.onRemarkOptionClick, this));
    } else {

        this.remarkOptionView.empty();
        this.remarkInputView.parent().parent().parent().removeClass("show-option");
        this.remarkOptionView.unbind("click");
    }
};

/**
 * 显示全部remark
 */
Web.showAllOptions = function () {

    var optionsView = "";
    for (var i in this.remarkOptions) {

        optionsView += "<li>" + this.remarkOptions[i] + "</li>";
    }

    this.remarkOptionView.empty();
    this.remarkOptionView.append(optionsView);

    this.remarkInputView.parent().parent().parent().addClass("show-option");
    this.remarkOptionView.bind("click", $.proxy(this.onRemarkOptionClick, this));
};

/**
 * 隐藏全部option
 */
Web.hideAllOptions = function () {

    this.remarkOptionView.empty();
    this.remarkInputView.parent().parent().parent().removeClass("show-option");
    this.remarkOptionView.unbind("click");
};

/**
 * 更新remarkOptions
 */
Web.updateRemarkOptions = function () {

    if (this.driverStatus == DRIVERSTATUS.ONDUTY) {

        this.remarkOptions = this.odndRemarkOptions;
    } else if (this.driverStatus == DRIVERSTATUS.OFFDUTY) {

        this.remarkOptions = this.offRemarkOptions;
    } else if (this.driverStatus == DRIVERSTATUS.DRIVING) {

        this.remarkOptions = this.drivingRemarkOption;
    } else if (this.driverStatus == DRIVERSTATUS.SLEEPER) {

        this.remarkOptions = this.sbRemarkOptions;
    } else {

        this.remarkOptions = this.allRemarkOptions;
    }
};

/**
 * 选中某个remark
 * @param event
 */
Web.onRemarkOptionClick = function (event) {

    var target = $(event.target);
    this.remarkInputView.val(target.html());
    // this.remarkInputView.focus();

    this.remarkOptionView.empty();
    this.remarkInputView.parent().parent().parent().removeClass("show-option");
    this.remarkOptionView.unbind("click");

    //处理save按钮的状态
    this.handleSaveBtnStatus();
};

/**
 * 处理save按钮的状态
 */
Web.handleSaveBtnStatus = function () {

    this.geoLocation = this.locationInputView.val().trim();
    this.remark = this.remarkInputView.val().trim();

    if (this.currentDriverStatus != this.driverStatus) {

        this.saveBtn.removeClass("disabled");
    } else {

        this.saveBtn.addClass("disabled");
    }
};

/**
 * 保存按钮被点击
 */
Web.onSaveBtnClick = function () {

    if (this.saveBtn.hasClass("disabled")) {

        return;
    }

    //验证
    this.validateFlag = false;
    this.geoLocation = this.locationInputView.val().trim();
    this.remark = this.remarkInputView.val().trim();

    if (this.geoLocation.length < Constants.locationMinLength) {

        this.locationBox.find("[sid=tipLabel]").html(String.locationMinValid.replace("#count#", Constants.locationMinLength.toString()));
        this.locationBox.addClass("warning");
        this.validateFlag = true;
    }

    if (this.geoLocation.length > Constants.locationMaxLength) {

        this.locationBox.find("[sid=tipLabel]").html(String.locationMaxValid.replace("#count#", Constants.locationMaxLength.toString()));
        this.locationBox.addClass("warning");
        this.validateFlag = true;
    }

    if (this.geoLocation.match(Constants.inputRegexp)) {

        this.locationBox.find("[sid=tipLabel]").html(String.locationValid);
        this.locationBox.addClass("warning");
        this.validateFlag = true;
    }

    if ((this.remark.length < Constants.remarkMinLength && this.remark.length > 0)) {

        this.remarkBox.find("[sid=tipLabel]").html(String.remarkMinValid.replace("#count#", Constants.remarkMinLength.toString()));
        this.remarkBox.addClass("warning");
        this.validateFlag = true;
    }

    if (this.remark.length > Constants.remarkMaxLength) {

        this.remarkBox.find("[sid=tipLabel]").html(String.remarkMaxValid.replace("#count#", Constants.remarkMaxLength.toString()));
        this.remarkBox.addClass("warning");
        this.validateFlag = true;
    }

    if (this.remark.match(Constants.inputRegexp)) {

        this.remarkBox.find("[sid=tipLabel]").html(String.remarkValid);
        this.remarkBox.addClass("warning");
        this.validateFlag = true;
    }

    if (this.validateFlag) {

        return;
    }

    var data = {
        state: this.driverStatus,
        location: this.geoLocation,
        latitude: this.latitude,
        longitude: this.longitude,
        remark: this.remark
    };
    USER.getUserRole($.proxy(function (roleId) {

        if (this.hasSelectedVehicle) {

            if (roleId != USERROLE.COPILOT) { //不是副驾驶

                if (this.currentDriverStatus == DRIVERSTATUS.OFFDUTY && data.state == DRIVERSTATUS.ONDUTY) { //如果是OFF切换为ODND，提示司机做车前检查

                    this.switchOffToOdnd(data);
                } else if (data.state == DRIVERSTATUS.OFFDUTY) { //如果是切换为OFF，提示司机做车后检查

                    this.switchToOff(data);
                } else {

                    DAILYLOG.changeState(data);
                    SDK.openMainPage();
                }
            } else { //是副驾驶

                DAILYLOG.changeState(data);
                SDK.openMainPage();
            }
        } else {

            if (roleId != USERROLE.COPILOT) {//不是副驾驶

                if (this.currentDriverStatus == DRIVERSTATUS.OFFDUTY && data.state == DRIVERSTATUS.ONDUTY) { //如果是OFF切换ODND，提示司机选车

                    this.switchOffToOdndHasNotVehicle(data);
                } else {

                    DAILYLOG.changeState(data);
                    SDK.openMainPage();
                }
            } else {

                DAILYLOG.changeState(data);
                SDK.openMainPage();
            }
        }
    }, this));
};

/**
 * 从OFF切换到ODND
 * @param data
 */
Web.switchOffToOdnd = function (data) {

    SDK.showVerticalDialog($.proxy(function (button) {

            if (button == Constants.BUTTON_NEUTRAL) {

                data.remark = data.remark ? data.remark : "Pre-Trip Inspection";
                DAILYLOG.changeState(data);

                SDK.openPage(PageConfig.InspectionNew.url,
                    PageConfig.InspectionNew.title,
                    {
                        type: INSPECTION.PRETRIP,
                        index: -2
                    });
            } else if (button == Constants.BUTTON_NEGATIVE) {

                DAILYLOG.changeState(data);
                SDK.openMainPage();
            }
        }, this),
        {
            icon: DialogConfig.Icon_Love,
            text: String.changeStatusPreInspectionTip,
            neutralBtnText: String.changeStatusPreInspectionNeutralBtnText,
            negativeBtnText: String.changeStatusInspectionNegativeBtnText
        });
};

/**
 * 从OFF切换到ODND
 * @param data
 */
Web.switchOffToOdndHasNotVehicle = function (data) {

    SDK.showDialog($.proxy(function (button) {

            if (button == Constants.BUTTON_NEUTRAL) {

                DAILYLOG.changeState(data);
                SDK.openPage(PageConfig.ConnectVehicle.url,
                    PageConfig.ConnectVehicle.title,
                    {
                        index: -2
                    });
            } else {

                DAILYLOG.changeState(data);
                SDK.openMainPage();
            }
        }, this),
        {
            icon: DialogConfig.Icon_Love,
            text: String.changeStatusSelectVehicle,
            neutralBtnText: String.yes,
            negativeBtnText: String.no
        });
};

/**
 * 切换到OFF
 * @param data
 */
Web.switchToOff = function (data) {

    // 检查1小时内是否做过连接车辆的Post-trip inspection
    DVIR.getInspectionLog($.proxy(function (inspectionLog) {

        if (inspectionLog) {

            DAILYLOG.changeState(data); // 状态切换为OFF
            VEHICLE.disconnectVehicleAuto(); //断开车辆
            SDK.openMainPage();
        } else {

            SDK.showVerticalDialog($.proxy(function (button) {

                    if (button == Constants.BUTTON_NEUTRAL) {

                        if (this.currentDriverStatus != DRIVERSTATUS.ONDUTY) {

                            // 切换到On Duty Not Driving
                            data.state = DRIVERSTATUS.ONDUTY;
                            data.remark = "Post-Trip Inspection";
                            DAILYLOG.changeState(data);
                        }

                        SDK.openPage(PageConfig.InspectionNew.url,
                            PageConfig.InspectionNew.title,
                            {
                                type: INSPECTION.POSTTRIP,
                                index: -3
                            });
                    } else if (button == Constants.BUTTON_NEGATIVE) {

                        DAILYLOG.changeState(data); // 状态切换为OFF
                        VEHICLE.disconnectVehicleAuto(); //断开车辆
                        SDK.openMainPage();
                    }
                }, this),
                {
                    icon: DialogConfig.Icon_Love,
                    text: String.changeStatusPostInspectionTip,
                    neutralBtnText: String.changeStatusPostInspectionNeutralBtnText,
                    negativeBtnText: String.changeStatusInspectionNegativeBtnText
                });
        }
    }, this));
};
