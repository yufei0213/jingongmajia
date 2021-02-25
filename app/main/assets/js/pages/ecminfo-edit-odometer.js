/**
 * @author liuzhe
 * @date 2018/5/07
 * @description ecminfo-edit-odometer
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.textCancel = $("#textCancel");
    this.saveBtn = $("#saveBtn");
    this.currentOdometer = $("#currentOdometer");
    this.currentOdometerLi = $("#currentOdometerLi");
    var totalOdometer = params.totalOdometer;
    var isOdoOffsetValid = params.isOdoOffsetValid;
    var preOffsetOdometer = params.preOffsetOdometer;

    var that = this;
    this.currentOdometer.attr("placeholder", totalOdometer);

    this.currentOdometer.bind("input propertychange", $.proxy(function () {

        //处理save按钮的状态
        this.resetWarning();
    }, this));

    this.saveBtn.on("click", $.proxy(function () {

        if (!this.checkSaveBtn()) {
            return;
        }

        SDK.showLoading();
        var data = {};

        if (isOdoOffsetValid) {

            totalOdometer = totalOdometer - preOffsetOdometer;
        }
        data.odo_offset = this.currentOdometer.val() - totalOdometer;

        VEHICLE.changeOdometer(data, function (result) {

            SDK.hideLoading();
            if (result == Constants.CALLBACK_SUCCESS) {

                SDK.back();
            }
        });
    }, this));

    this.textCancel.on("click", $.proxy(function () {

        this.currentOdometer.val("");
        this.resetWarning();
    }, this));

};

/**
 * 重置warning
 */
Web.resetWarning = function () {

    this.currentOdometerLi.removeClass("warning");
};

Web.checkSaveBtn = function () {

    this.resetWarning();

    var result = true;

    if (parseFloat(this.currentOdometer.val()) > Constants.odometerMax) {

        this.currentOdometerLi.addClass("warning");
        this.currentOdometerLi.find('[sid=odometerTips]').html(String.odometerLengthError);
        result = false;
    }
    //如果不为空，需要判断是数字
    if (this.currentOdometer.val().trim() != "" && this.currentOdometer.val().trim().match('^[0-9]+\.?[0-9]*$') == null) {

        this.currentOdometerLi.addClass("warning");
        this.currentOdometerLi.find('[sid=odometerTips]').html(String.odometerTypeError);
        result = false;
    }
    if (this.currentOdometer.val() == "" || parseInt(this.currentOdometer.val()) == 0 || isNaN(this.currentOdometer.val())) {

        this.currentOdometerLi.addClass("warning");
        result = false;
    }
    return result;
};
