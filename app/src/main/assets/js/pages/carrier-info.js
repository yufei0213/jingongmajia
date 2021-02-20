/**
 * @author zhangyu
 * @date 2018/1/24
 * @description TODO
 */
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    var carrier = params.carrier;

    $("#name").text(carrier.name);
    $("#dot").text(carrier.usdot);
    $("#contact").text(carrier.contact);
    $("#address").text(carrier.address);
    $("#timeZone").text(carrier.timeZone);

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-carrier-info");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function () {

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-carrier-info");
};
