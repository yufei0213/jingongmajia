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

    page = new SettingPage($('#page'), params);

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-setting");
};
/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    if (params != null && params.key == Constants.UPDATE_PASSWORD_KEY) {
        page.reload(params.data);
        SDK.clearWebData();
    }

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-setting");
};

var SettingPage = function (view, params) {

    this.view = view;

    this.changePwdBtn = null;
    this.languageBtn = null;
    this.notificationBtn = null;
    this.smsBtn = null;

    // 初始化数据
    this.user = params.user;
    this.init();
};

SettingPage.prototype.init = function () {

    this.changePwdBtn = this.view.find('[sid=changePwdBtn]');
    this.languageBtn = this.view.find('[sid=languageBtn]');
    this.languageText = this.view.find('[sid=languageText]');
    this.notificationBtn = this.view.find('[sid=notificationBtn]');
    this.smsBtn = this.view.find('[sid=smsBtn]')


    this.changePwdBtn.parent().on('click', $.proxy(this.onChangePwdBoxClick, this));
    this.languageBtn.parent().on('click', $.proxy(this.onLanguageBoxClick, this));
    this.notificationBtn.parent().on('click', $.proxy(this.onNotificationClick, this));
    this.smsBtn.parent().on('click', $.proxy(this.onSMSClick, this));
};

SettingPage.prototype.reload = function (data) {

    this.user.password = data.password;
};

SettingPage.prototype.onChangePwdBoxClick = function () {

    var params = {
        user: this.user
    };
    SDK.openPage(PageConfig.ChangePassword.url,
        PageConfig.ChangePassword.title,
        params);
};

SettingPage.prototype.onLanguageBoxClick = function () {

    SDK.openPage(PageConfig.Language.url,
        PageConfig.Language.title);
};

SettingPage.prototype.onNotificationClick = function () {

    var params = {
        type: Constants.notificationSwitch
    }
    SDK.openPage(PageConfig.Notification.url,
        PageConfig.Notification.title,
        params);
};

SettingPage.prototype.onSMSClick = function () {

    var params = {
        type: Constants.SMSSwitch
    }
    SDK.openPage(PageConfig.SMS.url,
        PageConfig.SMS.title,
        params);
};

