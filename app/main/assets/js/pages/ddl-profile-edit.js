/**
 * @author zhangyu
 * @date 2018/1/24
 * @description ddl profile edit
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.data = params.data;
    this.index = params.index;

    this.vehicleIdLi = $("#vehicleIdLi");
    this.vehiceId = $("#vehicleId");
    this.vehicleIdLiText = $("#vehicleIdLiText");

    this.vinLi = $("#vinLi");
    this.vin = $("#vin");
    this.vinLiText = $("#vinLiText");

    this.coDriverNameLi = $("#coDriverNameLi");
    this.coDriverName = $("#coDriverName");
    this.coDriverNameLiText = $("#coDriverNameLiText");

    this.coDriverIdLi = $("#coDriverIdLi");
    this.coDriverId = $("#coDriverId");
    this.coDriverIdLiText = $("#coDriverIdLiText");

    this.shippingIdLi = $("#shippingIdLi");
    this.shippingId = $("#shippingId");
    this.shippingIdLiText = $("#shippingIdLiText");

    this.odometerLi = $("#odometerLi");
    this.odometerLiText = $("#odometerLiText");
    this.travelDistance = $("#travelDistance");

    this.engineHourLi = $("#engineHourLi");
    this.engineHourLiText = $("#engineHourLiText");
    this.duration = $("#duration");

    this.saveBtn = $("#saveBtn");

    if (this.data.vehicleId)
        this.vehiceId.val(this.data.vehicleId);
    if (this.data.vin)
        this.vin.val(this.data.vin);
    if (this.data.coDriverName)
        this.coDriverName.val(this.data.coDriverName);
    if (this.data.coDriverId)
        this.coDriverId.val(this.data.coDriverId);
    if (this.data.shippingId)
        this.shippingId.val(this.data.shippingId);
    if (this.data.startEndOdometer)
        this.travelDistance.val(parseFloat(this.data.startEndOdometer).toFixed(2));
    if (this.data.startEndEngineHour)
        this.duration.val(parseFloat(this.data.startEndEngineHour).toFixed(2));

    this.vehiceId.on("input propertychange", $.proxy(function () {
        this.vehicleIdLi.removeClass("warning");
    }, this));
    this.vin.on("input propertychange", $.proxy(function () {
        this.vinLi.removeClass("warning");
    }, this));
    this.coDriverName.on("input propertychange", $.proxy(function () {
        this.coDriverNameLi.removeClass("warning");
    }, this));
    this.coDriverId.on("input propertychange", $.proxy(function () {
        this.coDriverIdLi.removeClass("warning");
    }, this));
    this.shippingId.on("input propertychange", $.proxy(function () {
        this.shippingIdLi.removeClass("warning");
    }, this));
    this.travelDistance.on("input propertychange", $.proxy(function () {
        this.odometerLi.removeClass("warning");
    }, this));
    this.duration.on("input propertychange", $.proxy(function () {
        this.engineHourLi.removeClass("warning");
    }, this));

    this.saveBtn.on("click", $.proxy(this.onSaveBtnClick, this));

    this.initFocus();

    if (this.index == 0) {

        this.vehiceId.attr("readonly", "readonly");
        this.vehiceId.unbind("focus");
        this.vehiceId.unbind("blur");

        this.vin.attr("readonly", "readonly");
        this.vin.unbind("focus");
        this.vin.unbind("blur");

        this.coDriverName.attr("readonly", "readonly");
        this.coDriverName.unbind("focus");
        this.coDriverName.unbind("blur");

        this.coDriverId.attr("readonly", "readonly");
        this.coDriverId.unbind("focus");
        this.coDriverId.unbind("blur");

        this.travelDistance.attr("readonly", "readonly");
        this.travelDistance.unbind("focus");
        this.travelDistance.unbind("blur");

        this.duration.attr("readonly", "readonly");
        this.duration.unbind("focus");
        this.duration.unbind("blur");

        $("body").on("click", function () {

            var dialogConfig = {
                icon: DialogConfig.Icon_Love,
                text: String.ddlTodayProfileNotEdit,
                positiveBtnText: String.ok
            };

            SDK.showDialog(null, dialogConfig);
        });

        this.shippingId.bind("click", function () {

            return false;
        });
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

/**
 * 重置warning
 */
Web.resetWarning = function () {

    this.vehicleIdLi.removeClass("warning");
    this.vinLi.removeClass("warning");
    this.coDriverNameLi.removeClass("warning");
    this.coDriverIdLi.removeClass("warning");
    this.shippingIdLi.removeClass("warning");
    this.odometerLi.removeClass("warning");
    this.engineHourLi.removeClass("warning");
};

/**
 * 处理save按钮的状态
 */
Web.checkSaveBtn = function () {

    this.resetWarning();
    var result = true;

    var numberReg = /^[0-9]+\.?[0-9]*$/;
    if (this.vehiceId.val().match(Constants.inputRegexp)) {

        this.vehicleIdLi.addClass("warning");
        this.vehicleIdLiText.html(String.vehicleIdValid);
        this.vehicleIdLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (this.vin.val().match(Constants.inputRegexp)) {

        this.vinLi.addClass("warning");
        this.vinLiText.html(String.vinValid);
        this.vinLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (this.coDriverName.val().match(Constants.inputRegexp)) {

        this.coDriverNameLi.addClass("warning");
        this.coDriverNameLiText.html(String.coDriverNameValid);
        this.coDriverNameLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (this.coDriverId.val().match(Constants.inputRegexp)) {

        this.coDriverIdLi.addClass("warning");
        this.coDriverIdLiText.html(String.coDriverIdValid);
        this.coDriverIdLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (this.shippingId.val().match(Constants.inputRegexp)) {

        this.shippingIdLi.addClass("warning");
        this.shippingIdLiText.html(String.shippingIdValid);
        this.shippingIdLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (parseFloat(this.duration.val()) > Constants.engineHourMax) {

        this.engineHourLi.addClass("warning");
        this.engineHourLiText.html(String.engineHourLengthError);
        this.engineHourLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (!numberReg.test(this.duration.val().trim())) {

        this.engineHourLi.addClass("warning");
        this.engineHourLiText.html(String.engineHourTypeError);
        this.engineHourLi.get(0).scrollIntoView(false);
        result = false;
    }

    if (parseFloat(this.travelDistance.val()) > Constants.odometerMax) {

        this.odometerLi.addClass("warning");
        this.odometerLi.get(0).scrollIntoView(false);
        this.odometerLiText.html(String.odometerLengthError);
        result = false;
    }

    if (!numberReg.test(this.travelDistance.val().trim())) {

        this.odometerLi.addClass("warning");
        this.odometerLi.get(0).scrollIntoView(false);
        this.odometerLiText.html(String.odometerTypeError);
        result = false;
    }

    return result;
};

/**
 * SAVE按钮点击
 */
Web.onSaveBtnClick = function () {

    if (!this.checkSaveBtn()) {

        return false;
    }
    SDK.showLoading();

    var data = {};
    data.vehicleId = this.vehiceId.val();
    data.vin = this.vin.val();
    data.carrier = this.data.carrier;
    data.codriverName = this.coDriverName.val();
    data.shippingId = this.shippingId.val();
    data.codriverId = this.coDriverId.val();
    // data.carrier = this.carrier.val();
    if (isNaN(parseFloat(this.travelDistance.val()))) {
        data.startEndOdometer = 0.00;
    } else {
        data.startEndOdometer = parseFloat(this.travelDistance.val()).toFixed(2);
    }
    if (isNaN(parseFloat(this.duration.val()))) {
        data.startEndEngineHour = 0.00;
    } else {
        data.startEndEngineHour = parseFloat(this.duration.val()).toFixed(2);
    }

    DAILYLOG.updateProfile({index: this.index, data: data}, $.proxy(function (info) {

        SDK.hideLoading();
        if (info != Constants.CALLBACK_SUCCESS) {

            var dialogConfig = {
                icon: DialogConfig.Icon_Awkward,
                text: String.ddlRequesetailure,
                positiveBtnText: String.ok,
                cancelable: 1
            };
            SDK.showDialog(null, dialogConfig);
        } else {

            SDK.setWebData(Constants.UPDATE_PROFILE_KEY, data);
            SDK.back();
        }
    }, this));

    return false;
};