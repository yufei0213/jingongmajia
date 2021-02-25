/**
 * @author zhangyu
 * @date 2018/1/24
 * @description TODO
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空 ?params={"index":0}
 */
Web.init = function (params) {

    this.gridView = $("#grid");
    this.startTime = $("#startTime");
    this.startTimeText = $("#startTimeText");
    this.duration = $("#duration");
    this.offBtn = $("#offBtn");
    this.odndBtn = $("#odndBtn");
    this.dBtn = $("#dBtn");
    this.sbBtn = $("#sbBtn");
    this.locationInputView = $("#locationInput");
    this.vid = $("#vid");
    this.vidBtn = $("#vidBtn");
    this.odometer = $("#odometer");
    this.saveBtn = $("#saveBtn");
    this.selectVidLi = $("#selectVidLi");
    this.locationLi = $("#locationLi");
    this.locationLiText = $("#locationLiText");
    this.odometerLi = $("#odometerLi");
    this.odometerLiText = $("#odometerLiText");
    this.remarkLi = $("#remarkLi");
    this.remarkLiText = $("#remarkLiText");
    this.timeLi = $("#timeLi");
    this.remarkInputView = $("#remarkInputView");
    this.remarkOptionView = $("#remarkOptionView");

    this.index = params.index;
    this.state = DRIVERSTATUS.OFFDUTY;

    this.initFocus();
    this.initClicks();
    this.initLocation();
    this.initRemark();
    this.initGrid(params.index);

    SDK.collectFirebaseScreen("dailylog-insertstatus");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    if (params != null && params.key == Constants.INSPECTION_SELECT_VEHICLE_KEY) {

        this.loadVehicleData(params.data);
        this.resetWarning();
        SDK.clearWebData();
    }

    SDK.collectFirebaseScreen("dailylog-insertstatus");
};

/**
 * 处理键盘和saveBtn
 */
Web.initFocus = function () {

    if (!$.contains(".container", ".btnBar" && navigator.userAgent.indexOf('Android') > -1 || navigator.userAgent.indexOf('Linux') > -1)) {

        $("input,textarea").unbind("focus");
        $("input,textarea").unbind("blur");
        $("input,textarea").focus(function () {

            $(".btnBar").appendTo($(".container"));
            $(this).parents("li").get(0).scrollIntoView(false);
        });
        $("input,textarea").blur(function () {

            $(".btnBar").appendTo($(".page"));
        });
        $(window).unbind("resize");
        $(window).resize(function () {

            $("input:focus,textarea:focus").parents("li").get(0).scrollIntoView(false);
        });
    }
};

Web.initGrid = function (index) {

    SDK.getTotalHour($.proxy(function (hour) {

        this.grid = new DriverGrid("grid", hour);
        this.grid.initCanvas();
        DAILYLOG.getGridDataByIndex(index, $.proxy(function (data) {

            data = JSON.parse(data);

            this.gridData = data;
            this.grid.draw(data, false);
        }, this));
    }, this), this.index);
};

Web.initClicks = function () {

    var env = this;
    this.startTime.on("click", function () {

        SDK.showTimePicker(env.beginSecond, env.grid.oneDayHour, function (second) {

            if (!env.getIsValidSecond(second)) {
                SDK.showMessage(String.ddlDetailStatusNotEdit);
                return;
            }

            env.endSecond = env.getEndSecond(second);

            if (second > env.endSecond) {
                second = env.endSecond;
            }

            env.beginSecond = second;
            env.startTimeText.html(TimeUtil.formatWithNoon(second, env.grid.oneDayHour));

            if (env.gridData[env.gridData.length - 1].endSecond == env.endSecond) {

                DAILYLOG.getPastDayEventSecond(env.index, $.proxy(function (pastSecond) {

                    this.duration.html(TimeUtil.formatHM((this.endSecond - this.beginSecond) + parseInt(pastSecond)));
                }, env));
            } else {
                env.duration.html(TimeUtil.formatHM(env.endSecond - env.beginSecond));
            }
            env.resetWarning();
            env.grid.modify(new DriverEvent(env.state, second, env.endSecond));
            env.startTimeText.removeClass("pld");
            env.duration.removeClass("pld");
        });
    });
    this.offBtn.on("click", function () {
        env.state = DRIVERSTATUS.OFFDUTY;
        env.setBtn();
        if (typeof(env.beginSecond) != "undefined") {
            env.grid.modify(new DriverEvent(DRIVERSTATUS.OFFDUTY, env.beginSecond, env.endSecond));
        }

        env.updateRemarkOptions();
    });
    this.odndBtn.on("click", function () {
        env.state = DRIVERSTATUS.ONDUTY;
        env.setBtn();
        if (typeof(env.beginSecond) != "undefined") {
            env.grid.modify(new DriverEvent(DRIVERSTATUS.ONDUTY, env.beginSecond, env.endSecond));
        }
        env.updateRemarkOptions();
    });
    this.sbBtn.on("click", function () {
        env.state = DRIVERSTATUS.SLEEPER;
        env.setBtn();
        if (typeof(env.beginSecond) != "undefined") {
            env.grid.modify(new DriverEvent(DRIVERSTATUS.SLEEPER, env.beginSecond, env.endSecond));
        }
        env.updateRemarkOptions();
    });
    this.dBtn.on("click", function () {
        env.state = DRIVERSTATUS.DRIVING;
        env.setBtn();
        if (typeof(env.beginSecond) != "undefined") {
            env.grid.modify(new DriverEvent(DRIVERSTATUS.DRIVING, env.beginSecond, env.endSecond));
        }
        env.updateRemarkOptions();
    });
    this.saveBtn.on("click", $.proxy(this.onSaveBtnClick, this));

    this.vidBtn.on("click", function () {

        SDK.openPage(PageConfig.SelectVehicle.url,
            PageConfig.SelectVehicle.title);
    });
};

Web.setBtn = function () {

    this.dBtn.removeClass("on");
    this.odndBtn.removeClass("on");
    this.sbBtn.removeClass("on");
    this.offBtn.removeClass("on");
    switch (parseInt(this.state)) {
        case DRIVERSTATUS.DRIVING:
            this.dBtn.addClass("on");
            break;
        case DRIVERSTATUS.ONDUTY:
            this.odndBtn.addClass("on");
            break;
        case DRIVERSTATUS.SLEEPER:
            this.sbBtn.addClass("on");
            break;
        case DRIVERSTATUS.OFFDUTY:
            this.offBtn.addClass("on");
            break;
    }
};

Web.onSaveBtnClick = function () {

    if (!this.checkSaveBtn()) {
        return;
    }

    var data = {};
    if (this.vid.html()) {
        data.vehicle = this.vidId;
    } else {
        data.vehicle = 0;
    }
    data.remark = this.remarkInputView.val();

    data.location = this.locationInputView.val();
    data.latitude = this.latitude;
    data.longitude = this.longitude;

    data.odometer = this.odometer.val();
    data.state = this.state;
    data.beginSecond = this.beginSecond;
    data.endSecond = this.endSecond;
    data.index = this.index;
    DAILYLOG.newEvent(data, null);

    SDK.back();
};

Web.loadVehicleData = function (data) {

    this.vid.removeClass("pld");
    this.vid.html(data.code);
    this.vidId = data.id;
};

Web.getEndSecond = function (second) {

    for (var i = 0; i < this.gridData.length; i++) {

        if (this.gridData[i].endSecond > second) {
            return this.gridData[i].endSecond;
        }
    }
    return this.gridData[this.gridData.length - 1].endSecond;
};

/**
 * 初始化位置选择器
 */
Web.initLocation = function () {

    this.locationInputView = $("#locationInput");
    this.getLocationLoading = $("#getLocationLoading");
    this.getLocationBtn = $("#getLocationBtn");

    this.locationInputView.focus(function () {

        $(this).parent().parent().parent().addClass("focus");
    });
    this.locationInputView.blur(function () {

        $(this).parent().parent().parent().removeClass("focus");
    });
    this.locationInputView.bind("input propertychange", $.proxy(function () {

        this.latitude = LatLngSpecial.M;
        this.longitude = LatLngSpecial.M;

        //处理save按钮的状态
        this.resetWarning();
    }, this));
    this.odometer.bind("input propertychange", $.proxy(function () {

        //处理save按钮的状态
        this.resetWarning();
    }, this));
    this.getLocationBtn.bind("click", $.proxy(this.getGeoLocation, this));
};

/**
 * 重置warning
 */
Web.resetWarning = function () {

    this.locationLi.removeClass("warning");
    this.odometerLi.removeClass("warning");
    this.remarkLi.removeClass("warning");
    this.timeLi.removeClass("warning");
    this.selectVidLi.removeClass("warning");
};

/**
 * 处理save按钮的状态
 */
Web.checkSaveBtn = function () {

    this.resetWarning();
    this.geoLocation = this.locationInputView.val().trim();
    this.remark = this.remarkInputView.val().trim();

    var result = true;
    var numberReg = /^[0-9]+\.?[0-9]*$/;
    if (this.dBtn.hasClass("on") && !numberReg.test(this.odometer.val().trim())) {

        this.odometerLi.addClass("warning");
        this.odometerLi.get(0).scrollIntoView(false);
        this.odometerLiText.html(String.odometerTypeError);
        result = false;
    }

    //如果不为空，需要判断是数字
    if (this.odometer.val().trim().length != 0 && !numberReg.test(this.odometer.val().trim())) {

        this.odometerLi.addClass("warning");
        this.odometerLi.get(0).scrollIntoView(false);
        this.odometerLiText.html(String.odometerEmptyValid);
        result = false;
    }

    if (parseInt(this.odometer.val()) > Constants.odometerMax) {

        this.odometerLi.addClass("warning");
        this.odometerLi.get(0).scrollIntoView(false);
        this.odometerLiText.html(String.odometerLengthError);
        result = false;
    }

    if (this.geoLocation.length < Constants.locationMinLength) {

        this.locationLi.addClass("warning");
        this.locationLi.get(0).scrollIntoView(false);
        this.locationLiText.html(String.locationMinValid.replace("#count#", Constants.locationMinLength.toString()));
        result = false;
    }
    if (this.geoLocation.length > Constants.locationMaxLength) {

        this.locationLi.addClass("warning");
        this.locationLi.get(0).scrollIntoView(false);
        this.locationLiText.html(String.locationMaxValid.replace("#count#", Constants.locationMaxLength.toString()));
        result = false;
    }
    if (this.geoLocation.match(Constants.inputRegexp)) {

        this.locationLi.addClass("warning");
        this.locationLi.get(0).scrollIntoView(false);
        this.locationLiText.html(String.locationValid);
        result = false;
    }

    if (this.remark.length < Constants.remarkMinLength) {

        this.remarkLi.addClass("warning");
        this.remarkLiText.html(String.remarkMinValid.replace("#count#", Constants.remarkMinLength.toString()));
        this.remarkLi.get(0).scrollIntoView(false);
        result = false;
    }
    if (this.remark.length > Constants.remarkMaxLength) {

        this.remarkLi.addClass("warning");
        this.remarkLiText.html(String.remarkMaxValid.replace("#count#", Constants.remarkMaxLength.toString()));
        this.remarkLi.get(0).scrollIntoView(false);
        result = false;
    }
    if (this.remark.match(Constants.inputRegexp)) {

        this.remarkLi.addClass("warning");
        this.remarkLiText.html(String.remarkValid);
        this.remarkLi.get(0).scrollIntoView(false);
        result = false;
    }
    if (!this.beginSecond) {

        this.timeLi.addClass("warning");
        this.timeLi.get(0).scrollIntoView(false);

        result = false;
    }
    if (this.dBtn.hasClass("on") && !this.vidId) {

        this.selectVidLi.addClass("warning");
        this.selectVidLi.get(0).scrollIntoView(false);

        result = false;
    }
    return result;
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

    SDK.getGeoLocation($.proxy(function (code, latitude, longitude, location) {

        this.getLocationLoading.addClass("dn");
        this.getLocationBtn.removeClass("dn");

        if (code == Constants.CALLBACK_SUCCESS) {

            this.latitude = latitude;
            this.longitude = longitude;
            this.locationInputView.val(location);
            this.resetWarning();
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

        this.odndRemarkOptions = [];
        this.offRemarkOptions = [];
        this.drivingRemarkOption = [];
        this.sbRemarkOptions = [];

        var temp = JSON.parse(data);
        for (var i in temp) {

            var item = temp[i];
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

    autoTextarea(this.remarkInputView.get(0));

    var that = this;
    this.remarkInputView.focus(function () {

        that.remarkLi.addClass("focus");
        that.handleRemarkInput();
        if (!that.preventKeyBoard && !that.remarkInputView.val() && that.remarkOptionView.children()) {

            that.preventKeyBoard = true;
            that.remarkInputView.blur();
            $(".page").bind("click", function () {

                $(this).unbind("click");
                that.preventKeyBoard = false;
                that.remarkLi.removeClass("focus");
                setTimeout(function () {

                    that.hideAllOptions();
                }, 150);
            });
        } else {

            that.preventKeyBoard = false;
        }

        that.remarkOptionView.parents("li").get(0).scrollIntoView(false);
    });
    this.remarkInputView.blur(function () {

        if (!that.preventKeyBoard) {

            that.remarkLi.removeClass("focus");
            setTimeout(function () {

                that.hideAllOptions();
            }, 150);
        }
    });

    this.remarkInputView.bind("input propertychange", $.proxy(function () {

        this.resetWarning();
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

    if (this.state == DRIVERSTATUS.ONDUTY) {

        this.remarkOptions = this.odndRemarkOptions;
    } else if (this.state == DRIVERSTATUS.OFFDUTY) {

        this.remarkOptions = this.offRemarkOptions;
    } else if (this.state == DRIVERSTATUS.DRIVING) {

        this.remarkOptions = this.drivingRemarkOption;
    } else if (this.state == DRIVERSTATUS.SLEEPER) {

        this.remarkOptions = this.sbRemarkOptions;
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

    this.resetWarning();
};

/**
 * 当时间选择器选择完时间后，判断是否包含了不能修改的内容
 * @param second
 */
Web.getIsValidSecond = function (beginSecond) {

    for (var i = 0; i < this.gridData.length; i++) {

        if (this.gridData[i].endSecond > beginSecond && this.gridData[i].startSecond < beginSecond) {

            if ((this.gridData[i].driverStatus == DRIVERSTATUS.DRIVING && this.gridData[i].autoRecord) || this.gridData[i].driverStatus == DRIVERSTATUS.PERSONALUSE || this.gridData[i].driverStatus == DRIVERSTATUS.YARDMOVE) {
                return false;
            }
        }
        if (this.gridData[i].startSecond == beginSecond) {

            if (!this.gridData[i].invented) {

                return false;
            } else {

                if ((this.gridData[i].driverStatus == DRIVERSTATUS.DRIVING && this.gridData[i].autoRecord) || this.gridData[i].driverStatus == DRIVERSTATUS.PERSONALUSE || this.gridData[i].driverStatus == DRIVERSTATUS.YARDMOVE) {
                    return false;
                }
            }
        }
    }
    return true;
};