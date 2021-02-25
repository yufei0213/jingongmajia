/**
 * @author zhangyu
 * @date 2018/1/22
 * @description
 */
var page = null;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    page = new DriverPage($('#page'), params);

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-driverinfo");

};
/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    if (params != null && params.key == Constants.UPDATE_DRIVER_KEY) {
        page.reload(params.data);
        SDK.clearWebData();
    }

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-driverinfo");
};

(function ($, window) {

// 默认数据
    var defaultSetting = {
        user: null,
        driver: null
    };

    var DriverPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        this.view = view;

        this.nameLabel = null;
        this.driverIdLabel = null;
        this.licenceLabel = null;
        this.phoneLabel = null;
        this.emailLabel = null;

        this.init();
    };

    DriverPage.prototype.init = function () {

        this.nameLabel = this.view.find('[sid=nameLabel]');
        this.driverIdLabel = this.view.find('[sid=driverIdLabel]');
        this.licenceLabel = this.view.find('[sid=licenceLabel]');
        this.phoneLabel = this.view.find('[sid=phoneLabel]');
        this.emailLabel = this.view.find('[sid=emailLabel]');

        this.nameLabel.text(this.driver.name);
        this.driverIdLabel.text(this.user.accountDriverId);
        this.licenceLabel.text(this.driver.licenseNo);
        this.phoneLabel.text(this.driver.phone);
        this.emailLabel.text(this.driver.email);

        this.phoneLabel.on('click', $.proxy(this.onViewClick, this));
        this.emailLabel.on('click', $.proxy(this.onViewClick, this));
    };

    DriverPage.prototype.reload = function (data) {

        this.driver.phone = data.phone;
        this.driver.email = data.email;
        this.phoneLabel.text(this.driver.phone);
        this.emailLabel.text(this.driver.email);
    };

    DriverPage.prototype.onViewClick = function () {

        var params = {
            driver: this.driver
        };

        SDK.openPage(PageConfig.DriverInfoEdit.url,
            PageConfig.DriverInfoEdit.title,
            params);
    };

    window.DriverPage = DriverPage;
}($, window));
