/**
 * @author zhangyu
 * @date 2018/1/24
 * @description ecminfo
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.speedView = $("#speed");
    this.rpmView = $("#rpm");
    this.totalEngineHoursView = $("#totalEngineHours");
    this.totalOdometerView = $("#totalOdometer");
    this.editOdometerView = $("#editOdometer");
    this.noValidOffOdometerTipsView = $("#noValidOffOdometerTips");
    this.validOffOdometerTipsView = $("#validOffOdometerTips");
    this.noValidOdometerView = $("#noValidOdometer");
    this.validOdometerView = $("#validOdometer");

    this.offsetOdometer = parseFloat('0').toFixed(2);
    this.totalOdometer = parseFloat('0').toFixed(2);
    this.isOdoOffsetValid = false;

    DASHBOARD.getEcmInfo($.proxy(this.onReadSuccess, this));
    this.readInterval = setInterval($.proxy(function () {

        DASHBOARD.getEcmInfo($.proxy(this.onReadSuccess, this));
    }, this), 500);

    this.editOdometerView.on("click", $.proxy(function () {

        var data = {};
        data.totalOdometer = this.totalOdometer;
        data.preOffsetOdometer = this.offsetOdometer;
        data.isOdoOffsetValid = this.isOdoOffsetValid;
        SDK.openPage(PageConfig.ChangeOdometer.url, PageConfig.ChangeOdometer.title, data);
    }, this));
};

/**
 * 页面不可用
 */
Web.onPause = function () {

    clearInterval(this.readInterval);
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    DASHBOARD.getEcmInfo($.proxy(this.onReadSuccess, this));
    this.readInterval = setInterval($.proxy(function () {

        DASHBOARD.getEcmInfo($.proxy(this.onReadSuccess, this));
    }, this), 500);
    SDK.clearWebData();
};

/**
 * 读数据成功
 * @param data 车辆数据
 */
Web.onReadSuccess = function (data) {

    SDK.log("get ecm info success");

    data = JSON.parse(data);

    this.speedView.html(parseInt(data.speed));
    this.speedView.removeClass("h2");
    this.speedView.addClass("h3");

    this.rpmView.html(data.rpm);
    this.rpmView.removeClass("h2");
    this.rpmView.addClass("h3");

    if (data.totalEngineHours && data.totalEngineHours != 0) {

        this.totalEngineHoursView.html(parseFloat(data.totalEngineHours).toFixed(2));
        this.totalEngineHoursView.removeClass("h2");
        this.totalEngineHoursView.addClass("h3");
    }
    if (data.totalOdometer && data.totalOdometer != 0) {

        this.totalOdometer = parseFloat(data.totalOdometer).toFixed(2);
        this.totalOdometerView.html(this.totalOdometer);
        this.totalOdometerView.removeClass("h2");
        this.totalOdometerView.addClass("h3");

        if (data.offsetOdometer && data.offsetOdometer != 0) {

            this.offsetOdometer = parseFloat(data.offsetOdometer).toFixed(2);
            this.isOdoOffsetValid = data.isOdoOffsetValid;

            if (this.isOdoOffsetValid) {

                this.noValidOffOdometerTipsView.hide();
                this.validOffOdometerTipsView.show();
                var originalOdometer = parseFloat(this.totalOdometer) - parseFloat(this.offsetOdometer);
                originalOdometer = originalOdometer > 0 ? originalOdometer : 0;
                this.validOdometerView.html(parseFloat(originalOdometer).toFixed(2));
            } else {

                this.noValidOffOdometerTipsView.show();
                this.validOffOdometerTipsView.hide();
                var result = parseFloat(this.totalOdometer) + parseFloat(this.offsetOdometer);
                result = result > 0 ? result : 0;
                this.noValidOdometerView.html(parseFloat(result).toFixed(2));
            }
        } else {

            this.validOffOdometerTipsView.hide();
            this.noValidOffOdometerTipsView.hide();
        }
    }
};