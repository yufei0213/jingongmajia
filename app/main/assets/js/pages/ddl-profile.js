/**
 * @author zhangyu
 * @date 2018/1/24
 * @description ddl profile
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.params = params;
    this.index = params.index;

    this.getProfile();

    var env = this;
    $("body").on("click", function () {

        SDK.openPage(PageConfig.DdlProfileEdit.url,
            PageConfig.DdlProfileEdit.title,
            {index: env.params.index, data: env.data});
    });
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    if (params != null && params.key == Constants.UPDATE_PROFILE_KEY) {
        this.resetView();
        this.getProfile();
        SDK.clearWebData();
    }
};

/**
 * 刷新表头
 */
Web.getProfile = function (data) {

    var env = this;
    DAILYLOG.getProfile(this.index, function (data) {

        data = JSON.parse(data);
        env.data = data;
        if (data.vehicleId) {

            $("#vehicleId").html(data.vehicleId);
            $("#vehicleId").removeClass("pld");
        } else {

            $("#vehicleId").html(String.empty);
            $("#vehicleId").addClass("pld");
        }
        if (data.vin) {

            $("#vin").html(data.vin);
            $("#vin").removeClass("pld");
        } else {

            $("#vin").html(String.empty);
            $("#vin").addClass("pld");
        }
        if (data.coDriverName) {

            $("#codriverName").html(data.coDriverName);
            $("#codriverName").removeClass("pld");
        } else {

            $("#codriverName").html(String.empty);
            $("#codriverName").addClass("pld");
        }
        if (data.coDriverId) {

            $("#codriverId").html(data.coDriverId);
            $("#codriverId").removeClass("pld");
        } else {

            $("#codriverId").html(String.empty);
            $("#codriverId").addClass("pld");
        }
        if (data.carrier) {

            $("#carrier").html(data.carrier);
            $("#carrier").removeClass("pld");
        } else {

            $("#carrier").html(String.empty);
            $("#carrier").addClass("pld");
        }
        if (data.shippingId) {

            $("#shippingId").html(data.shippingId);
            $("#shippingId").removeClass("pld");
        } else {

            $("#shippingId").html(String.empty);
            $("#shippingId").addClass("pld");
        }

        if (data.startEndOdometer) {

            $("#startEndOdometer").html(parseFloat(data.startEndOdometer).toFixed(2));
            $("#startEndOdometer").removeClass("pld");
        } else {

            $("#startEndOdometer").html(String.empty);
            $("#startEndOdometer").addClass("pld");
        }
        if (data.startEndEngineHour) {

            $("#startEndEngineHour").html(parseFloat(data.startEndEngineHour).toFixed(2));
            $("#startEndEngineHour").removeClass("pld");
        } else {

            $("#startEndEngineHour").html(String.empty);
            $("#startEndEngineHour").addClass("pld");
        }
    });
};

Web.resetView = function () {

    $("#vehicleId").empty();
    $("#vehicleId").addClass("pld");
    $("#vin").empty();
    $("#vin").addClass("pld");
    $("#codriverName").empty();
    $("#codriverName").addClass("pld");
    $("#codriverId").empty();
    $("#codriverId").addClass("pld");
    $("#startEndOdometer").empty();
    $("#startEndOdometer").addClass("pld");
    $("#startEndEngineHour").empty();
    $("#startEndEngineHour").addClass("pld");
};