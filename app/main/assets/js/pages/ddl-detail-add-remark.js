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
    this.location = $("#location");
    this.remark = $("#remark");
    this.saveBtn = $("#saveBtn");
    this.locationBtn = $("#locationBtn");
    this.remarkOptionView = $("#remarkOptionView");
    this.remarkLi = $("#remarkLi");
    this.remarkLiText = $("#remarkLiText");
    this.timeLi = $("#timeLi");
    this.remarkInputView = $("#remark");
    this.remarkOptionView = $("#remarkOptionView");

    this.index = params.index;
    this.state = null;

    this.initFocus();
    this.initClicks();
    this.initGrid(params.index);
    this.initRemark();

    autoTextarea(this.remark.get(0));

    SDK.collectFirebaseScreen("dailylog-insertremark");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    if (params != null && params.key == Constants.INSPECTION_SELECT_VEHICLE_KEY) {

        this.loadVehicleData(params.data);
        SDK.clearWebData();
    }

    SDK.collectFirebaseScreen("dailylog-insertremark");
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

    var env = this;
    SDK.getTotalHour($.proxy(function (hour) {
        env.grid = new DriverGrid("grid", hour);
        env.grid.initCanvas();
        DAILYLOG.getGridDataByIndex(index, $.proxy(function (data) {

            data = JSON.parse(data);
            env.gridData = data;
            env.endSecond = data[data.length - 1].endSecond;
            env.grid.draw(data, false);
        }, this));
    }, this), index);
};

Web.initClicks = function () {

    var env = this;

    this.startTime.on("click", function () {

        if (!env.beginSecond) {
            env.beginSecond = 0;
        }
        var beginSecond = env.beginSecond;
        SDK.showTimePicker(beginSecond, env.grid.oneDayHour, function (second) {

            if (second > env.endSecond) {

                second = env.endSecond;
            }

            DAILYLOG.getStatusByTime({
                beginSecond: second,
                index: env.index
            }, function (code, state) {

                if (code == Constants.CALLBACK_SUCCESS) {

                    env.resetWarning();
                    env.startTimeText.html(TimeUtil.formatWithNoon(second, env.grid.oneDayHour));
                    env.grid.drawLine(second);
                    env.beginSecond = second;
                    env.startTimeText.removeClass("pld");

                    env.state = state;
                    env.updateRemarkOptions();
                } else {

                    env.state = null;
                    SDK.showMessage(String.ddlAddRemarkNoEvent);
                }
            });
        });
    });

    this.saveBtn.on("click", $.proxy(this.onSaveBtnClick, this));

    this.locationBtn.on("click", $.proxy(this.onLocationBtnClick, this));
};

Web.onSaveBtnClick = function () {

    if (!this.checkSaveBtn()) {
        return;
    }
    var data = {};
    data.remark = this.remark.val();
    data.location = this.location.html();
    data.beginSecond = this.beginSecond;
    data.index = this.index;
    DAILYLOG.addRemark(data);
    SDK.back();
};

Web.onLocationBtnClick = function () {

    var env = this;
    SDK.getGeoLocation(function (code, latitude, longitude, location) {

            if (code == Constants.CALLBACK_SUCCESS) {

                env.location.html(location);
            } else if (code == Constants.CALLBACK_FAILURE) {

                env.location.html("");
            }
        }
    );
};

/**
 * 重置warning
 */
Web.resetWarning = function () {

    this.remarkLi.removeClass("warning");
    this.timeLi.removeClass("warning");
};

/**
 * 处理save按钮的状态
 */
Web.checkSaveBtn = function () {

    this.remarkText = this.remark.val().trim();

    var result = true;
    if (this.remarkText.length < Constants.remarkMinLength) {

        this.remarkLi.addClass("warning");
        this.remarkLiText.html(String.remarkMinValid.replace("#count#", Constants.remarkMinLength.toString()))
        result = false;
    }
    if (this.remarkText.length > Constants.remarkMaxLength) {

        this.remarkLi.addClass("warning");
        this.remarkLiText.html(String.remarkMaxValid.replace("#count#", Constants.remarkMaxLength.toString()))
        result = false;
    }
    if (this.remarkText.match(Constants.inputRegexp)) {

        this.remarkLi.addClass("warning");
        this.remarkLiText.html(String.remarkValid);
        result = false;
    }
    if (!this.beginSecond) {

        this.timeLi.addClass("warning");
        result = false;
    }
    return result;
};

Web.getEndSecond = function (second) {

    for (var i = 0; i < this.gridData.length; i++) {

        if (this.gridData[i].endSecond > second) {
            return this.gridData[i].endSecond;
        }
    }
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

    this.resetWarning();
};