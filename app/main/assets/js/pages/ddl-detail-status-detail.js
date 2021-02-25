/**
 * @author zhangyu
 * @date 2018/1/24
 * @description ddl detail status detail
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    DAILYLOG.getLogDetail(params.id, function (data) {

        data = JSON.parse(data);
        if (data.eventName) {

            $("#eventName").html(data.eventName);
            $("#eventName").removeClass("pld");
        }
        if (data.vehicle) {

            $("#vehicle").html(data.vehicle);
            $("#vehicle").removeClass("pld");
        }
        if (data.time) {

            $("#time").html(data.time);
            $("#time").removeClass("pld");
        }
        if (data.origin) {

            $("#origin").html(data.origin);
            $("#origin").removeClass("pld");
        }
        if (data.location) {

            $("#location").html(data.location);
            $("#location").removeClass("pld");
        }
        if (data.odometer > 0) {

            $("#odometer").html(parseFloat(data.odometer).toFixed(2));
            $("#odometer").removeClass("pld");
        }
        if (data.engineHour > 0) {

            $("#engineHour").html(parseFloat(data.engineHour).toFixed(2));
            $("#engineHour").removeClass("pld");
        }
        if (data.remark) {

            $("#remark").html(data.remark);
            $("#remark").removeClass("pld");
        }
    });
};
