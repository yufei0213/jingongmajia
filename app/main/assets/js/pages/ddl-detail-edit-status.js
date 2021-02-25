/**
 * @author zhangyu
 * @date 2018/1/22
 * @description TODO
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空  ?params={"id":12666,"index":0,"beginSecond":5506,"endSecond":15967}
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
    this.vehicleText = $("#vid");
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
    this.remarkInputView = $("#remarkInputView");
    this.remarkOptionView = $("#remarkOptionView");

    this.initFocus();
    this.initClicks();

    this.initLocation();
    this.initGrid(params.index);
    this.initRemark();

    this.beginSecond = params.beginSecond;
    this.endSecond = params.endSecond;
    this.id = params.id;
    this.startTimeText.html(TimeUtil.formatWithNoon(this.beginSecond, this.grid.oneDayHour));
    this.updateStart = this.beginSecond == 0;
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

    this.index = index;
    SDK.getTotalHour($.proxy(function (hour) {
        this.grid = new DriverGrid("grid", hour);
        this.grid.initCanvas();
        this.duration.html(TimeUtil.formatHM(this.endSecond - this.beginSecond));
        DAILYLOG.getGridDataByIndex(index, $.proxy(function (data) {

            data = JSON.parse(data);

            this.drawEvent = data;
            if (this.endSecond < 0) {
                //赋值是否是最后一个事件。用于判断选择duration的时候是否需要跨天查询duration
                this.isEndEvent = true;
                this.endSecond = this.drawEvent[this.drawEvent.length - 1].endSecond;
                DAILYLOG.getPastDayEventSecond(index, $.proxy(function (pastSecond) {

                    this.duration.html(TimeUtil.formatHM(this.endSecond - this.beginSecond + parseInt(pastSecond)));
                }, this));
            }
            this.grid.draw(data, false);
            this.initData();
        }, this));

    }, this), index);

};

Web.initClicks = function () {

    var env = this;
    this.startTime.on("click", function () {

        var beginSecond = env.beginSecond;
        SDK.showTimePicker(beginSecond, env.grid.oneDayHour, function (second) {

            if (beginSecond == second) {

                return;
            }

            if (!env.getIsValidSecond(second, env.endSecond)) {
                SDK.showMessage(String.ddlDetailStatusNotEdit);
                return;
            }
            env.startTimeText.html(TimeUtil.formatWithNoon(second, env.grid.oneDayHour));

            if (env.updateStart && second != 0) {

                DAILYLOG.getPastDayEndState(env.index, $.proxy(function (state) {

                    env.grid.modify(new DriverEvent(env.state, second, env.endSecond), true, new DriverEvent(state, 0, second));
                }, env));
            } else {

                env.grid.modify(new DriverEvent(env.state, second, env.endSecond), true);
            }

            env.beginSecond = second;

            if (env.grid.allEvents[env.grid.allEvents.length - 1].endSecond == env.endSecond) {

                DAILYLOG.getPastDayEventSecond(env.index, $.proxy(function (pastSecond) {

                    this.duration.html(TimeUtil.formatHM((this.endSecond - this.beginSecond) + parseInt(pastSecond)));
                }, env));
            } else {

                env.duration.html(TimeUtil.formatHM(env.endSecond - env.beginSecond));
            }
        });
    });
    this.offBtn.on("click", function () {

        env.state = DRIVERSTATUS.OFFDUTY;
        env.setBtn();

        if (env.updateStart && env.beginSecond != 0) {

            DAILYLOG.getPastDayEndState(env.index, $.proxy(function (state) {

                env.grid.modify(new DriverEvent(DRIVERSTATUS.OFFDUTY, env.beginSecond, env.endSecond), true, new DriverEvent(state, 0, env.beginSecond));
            }, env));
        } else {

            env.grid.modify(new DriverEvent(DRIVERSTATUS.OFFDUTY, env.beginSecond, env.endSecond), true);
        }

        env.updateRemarkOptions();
    });
    this.odndBtn.on("click", function () {

        env.state = DRIVERSTATUS.ONDUTY;
        env.setBtn();

        if (env.updateStart && env.beginSecond != 0) {

            DAILYLOG.getPastDayEndState(env.index, $.proxy(function (state) {

                env.grid.modify(new DriverEvent(DRIVERSTATUS.ONDUTY, env.beginSecond, env.endSecond), true, new DriverEvent(state, 0, env.beginSecond));
            }, env));
        } else {

            env.grid.modify(new DriverEvent(DRIVERSTATUS.ONDUTY, env.beginSecond, env.endSecond), true);
        }

        env.updateRemarkOptions();
    });
    this.sbBtn.on("click", function () {

        env.state = DRIVERSTATUS.SLEEPER;
        env.setBtn();

        if (env.updateStart && env.beginSecond != 0) {

            DAILYLOG.getPastDayEndState(env.index, $.proxy(function (state) {

                env.grid.modify(new DriverEvent(DRIVERSTATUS.SLEEPER, env.beginSecond, env.endSecond), true, new DriverEvent(state, 0, env.beginSecond));
            }, env));
        } else {

            env.grid.modify(new DriverEvent(DRIVERSTATUS.SLEEPER, env.beginSecond, env.endSecond), true);
        }

        env.updateRemarkOptions();
    });
    this.dBtn.on("click", function () {

        env.state = DRIVERSTATUS.DRIVING;
        env.setBtn();

        if (env.updateStart && env.beginSecond != 0) {

            DAILYLOG.getPastDayEndState(env.index, $.proxy(function (state) {

                env.grid.modify(new DriverEvent(DRIVERSTATUS.DRIVING, env.beginSecond, env.endSecond), true, new DriverEvent(state, 0, env.beginSecond));
            }, env));
        } else {

            env.grid.modify(new DriverEvent(DRIVERSTATUS.DRIVING, env.beginSecond, env.endSecond), true);
        }

        env.updateRemarkOptions();

    });
    this.saveBtn.on("click", $.proxy(this.onSaveBtnClick, this));

    this.vidBtn.on("click", function () {

        SDK.openPage(PageConfig.SelectVehicle.url,
            PageConfig.SelectVehicle.title);
    });
};

Web.initData = function () {

    var that = this;
    this.odometer.focus(function () {

        that.odometerLi.addClass("focus");
    });
    this.odometer.blur(function () {

        that.odometerLi.removeClass("focus");
    });

    var env = this;
    DAILYLOG.getLogDetail(this.id, function (data) {
        data = JSON.parse(data);

        env.locationInputView.val(data.location);
        env.latitude = data.latitude;
        env.longitude = data.longitude;

        if (data.vehicle) {
            env.vehicleText.html(data.vehicle);
            env.vehicleText.removeClass("pld");
        }
        env.vid = data.vehicleId;

        if (data.odometer > 0) {

            env.odometer.attr("value", parseFloat(data.odometer).toFixed(2));
        }

        env.remarkInputView.html(data.remark);
        env.state = data.state;
        env.grid.modify(new DriverEvent(env.state, env.beginSecond, env.endSecond));
        env.setBtn();
        env.updateRemarkOptions();
    });
    this.startTimeText.html(TimeUtil.formatWithNoon(this.beginSecond, env.grid.oneDayHour));
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
    if (this.vehicleText.html()) {
        data.vehicle = this.vid;
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
    DAILYLOG.modifyEvent(this.id, data, null);

    SDK.back();
};

Web.loadVehicleData = function (data) {

    this.vehicleText.removeClass("pld");
    this.vehicleText.html(data.code);
    this.vid = data.id;
};

/**
 * 初始化位置选择器
 */
Web.initLocation = function () {

    this.locationInputView = $("#locationInput");
    this.getLocationLoading = $("#getLocationLoading");
    this.getLocationBtn = $("#getLocationBtn");

    var that = this;
    this.locationInputView.focus(function () {

        that.locationLi.addClass("focus");
    });
    this.locationInputView.blur(function () {

        that.locationLi.removeClass("focus");
    });
    this.locationInputView.bind("input propertychange", $.proxy(function () {

        this.latitude = LatLngSpecial.M;
        this.longitude = LatLngSpecial.M;

        //处理输入框的颜色
        this.resetWarning();
    }, this));

    this.odometer.bind("input propertychange", $.proxy(function () {

        //处理输入框的颜色
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
    this.selectVidLi.removeClass("warning");
};
/**
 * 重置warning
 */
Web.refreshWarning = function () {

    if (this.odometer.val() == "" || parseInt(this.odometer.val()) == 0 || isNaN(this.odometer.val())) {

        this.odometerLi.addClass("warning");
    } else {
        this.odometerLi.removeClass("warning");
    }
    if (this.geoLocation.length == 0) {

        this.locationLi.addClass("warning");
    } else {
        this.locationLi.removeClass("warning");

    }
    if (this.remarkText.length < Constants.remarkMinLength || this.remark.length > Constants.remarkMaxLength) {

        this.remarkLi.addClass("warning");
    } else {
        this.remarkLi.removeClass("warning");
    }
    if (this.dBtn.hasClass("on") && this.vehicleText.html() == "") {
        this.selectVidLi.addClass("warning");
    } else {
        this.selectVidLi.removeClass("warning");
    }
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
        this.remarkLi.get(0).scrollIntoView(false);
        this.remarkLiText.html(String.remarkValid);
        result = false;
    }

    if (this.dBtn.hasClass("on") && !this.vid) {
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

            this.locationInputView.val(location);

            this.latitude = latitude;
            this.longitude = longitude;
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
Web.getIsValidSecond = function (beginSecond, endSecond) {

    if (beginSecond >= endSecond) {
        return false;
    }

    for (var i = 0; i < this.drawEvent.length; i++) {

        if (this.drawEvent[i].endSecond > beginSecond && this.drawEvent[i].endSecond < endSecond) {

            if ((this.drawEvent[i].driverStatus == DRIVERSTATUS.DRIVING && this.drawEvent[i].autoRecord) || this.drawEvent[i].driverStatus == DRIVERSTATUS.PERSONALUSE || this.drawEvent[i].driverStatus == DRIVERSTATUS.YARDMOVE) {
                return false;
            }
        }
        if (this.drawEvent[i].startSecond == beginSecond) {

            if (!this.drawEvent[i].invented) {

                return false;
            } else {

                if ((this.drawEvent[i].driverStatus == DRIVERSTATUS.DRIVING && this.drawEvent[i].autoRecord) || this.drawEvent[i].driverStatus == DRIVERSTATUS.PERSONALUSE || this.drawEvent[i].driverStatus == DRIVERSTATUS.YARDMOVE) {
                    return false;
                }
            }
        }
    }
    return true;
};