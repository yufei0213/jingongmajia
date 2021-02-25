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

    page = new NotificationPage($('#page'), params);
};

var NotificationPage = function (view, params) {

    this.view = view;

    this.openBox = null;
    this.closeBox = null;
    this.saveBtn = null;

    this.type = params.type;

    this.init();
};

NotificationPage.prototype.init = function () {

    this.openBox = this.view.find('[sid=openBox]');
    this.closeBox = this.view.find('[sid=closeBox]');
    this.saveBtn = this.view.find('[sid=saveBtn]');

    var param = {
        type: this.type
    }
    SDK.getNotificationSetting(param, $.proxy(function (status) {

        this.setView(status);
        this.originStatus = status;
    }, this));
    this.openBox.on('click', $.proxy(this.onOpenBoxClick, this));
    this.closeBox.on('click', $.proxy(this.onCloseBoxClick, this));
    this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));

};

NotificationPage.prototype.onOpenBoxClick = function () {

    this.setView(Constants.notificationOpen);
};

NotificationPage.prototype.onCloseBoxClick = function () {

    this.setView(Constants.notificationClose);
};

NotificationPage.prototype.setView = function (type) {

    if (type == Constants.notificationOpen) {
        this.openBox.find('[sid=checkImg]').removeClass('dn');
        this.closeBox.find('[sid=checkImg]').addClass('dn');
    } else {
        this.openBox.find('[sid=checkImg]').addClass('dn');
        this.closeBox.find('[sid=checkImg]').removeClass('dn');
    }
};

NotificationPage.prototype.onSaveBtnClick = function () {

    // if (Global.isAndroid) {
    //
    //     SDK.showLoading();
    // }

    if (this.openBox.find('[sid=checkImg]').hasClass("dn")) {

        if (this.originStatus != Constants.notificationClose) {

            var data = {
                type: this.type,
                data: Constants.notificationClose
            };
            SDK.setNotificationSetting(data);
        }
        SDK.back();
    } else if (this.closeBox.find('[sid=checkImg]').hasClass("dn")) {

        if (this.originStatus != Constants.notificationOpen) {

            var data = {
                type: this.type,
                data: Constants.notificationOpen
            };
            SDK.setNotificationSetting(data);
        }
        SDK.back();
    }

};