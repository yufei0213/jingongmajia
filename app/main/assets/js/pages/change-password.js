/**
 * @author zhangyu
 * @date 2018/1/22
 * @description
 * ?params={"user":{"accessToken": "40850a99f7b94eed9d62cb7d6c0bd2e9","accountCarrierId": "ubt","accountDriverId": "eldtest14","carriedId": 1,"driverId": 91,"password": "123456","timeZone": "US/Eastern","vehicleId": 0}}
 */
var page = null;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    page = new PasswordPage($('#page'), params);

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-setting-changepwd");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function () {

    // FireBase Analytics
    SDK.collectFirebaseScreen("more-setting-changepwd");
};

var PasswordPage = function (view, params) {

    this.view = view;

    this.currentPwdInput = null;
    this.newPwdInput = null;
    this.confirmPwdInput = null;
    this.deletePwdBtn = null;
    this.saveBtn = null;

    // 初始化数据
    this.user = params.user;
    this.windowHeight = null;

    this.init();
};

PasswordPage.prototype.init = function () {

    this.currentPwdInput = this.view.find('[sid=currentPwdInput]');
    this.newPwdInput = this.view.find('[sid=newPwdInput]');
    this.confirmPwdInput = this.view.find('[sid=confirmPwdInput]');
    this.deletePwdBtn = this.view.find('[sid=deletePwdBtn]');
    this.saveBtn = this.view.find('[sid=saveBtn]');

    this.currentPwdInput.on("input propertychange", $.proxy(this.onValueChange, this));
    this.newPwdInput.on("input propertychange", $.proxy(this.onValueChange, this));
    this.confirmPwdInput.on("input propertychange", $.proxy(this.onValueChange, this));
    this.deletePwdBtn.on('click', this, this.onDeletePwdBtnClick);
    this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));

    this.checkSaveBtnStatus();

    this.windowHeight = $(window).height();
    var self = this;
    
    $(window).unbind("resize");
    $(window).resize(function () {
        // 减50因为下面有导航栏
        if ($(window).height() < self.windowHeight - 50) {
            self.saveBtn.parent().addClass('dn');
        } else {
            self.saveBtn.parent().removeClass('dn');
        }
    });
};

PasswordPage.prototype.onDeletePwdBtnClick = function (event) {

    var self = event.data;
    switch ($(this).attr("code")) {
        case '1':
            self.currentPwdInput.val('');
            break;
        case '2':
            self.newPwdInput.val('');
            break;
        case '3':
            self.confirmPwdInput.val('');
            break;
        default:
            break;
    }
    $(this).addClass('dn');
};

PasswordPage.prototype.onValueChange = function () {

    this.checkDeleteBtnStatus();

    this.checkSaveBtnStatus();
};

PasswordPage.prototype.checkDeleteBtnStatus = function () {

    if (this.currentPwdInput.val().trim() == "") {
        this.deletePwdBtn.filter("[code=1]").addClass('dn');
    } else {
        this.deletePwdBtn.filter("[code=1]").removeClass('dn');
    }
    if (this.newPwdInput.val().trim() == "") {
        this.deletePwdBtn.filter("[code=2]").addClass('dn');
    } else {
        this.deletePwdBtn.filter("[code=2]").removeClass('dn');
    }
    if (this.confirmPwdInput.val().trim() == "") {
        this.deletePwdBtn.filter("[code=3]").addClass('dn');
    } else {
        this.deletePwdBtn.filter("[code=3]").removeClass('dn');
    }
};

PasswordPage.prototype.checkSaveBtnStatus = function () {

    if (this.currentPwdInput.val().trim() == "" ||
        this.newPwdInput.val().trim() == "" ||
        this.confirmPwdInput.val().trim() == "") {
        this.saveBtn.addClass('disabled');
    } else {
        this.saveBtn.removeClass('disabled');
    }
};

PasswordPage.prototype.onSaveBtnClick = function () {

    if (this.saveBtn.hasClass('disabled')) {
        return;
    }

    // 输入当前密码不正确
    if (this.currentPwdInput.val().trim() != this.user.password) {
        var config = {
            icon: DialogConfig.Icon_Love,
            text: String.changePsdInvalidPsd,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.NoCancelable
        }
        SDK.showDialog(null, config);
        return;
    }
    // 两次输入新密码不相同
    if (this.newPwdInput.val().trim() != this.confirmPwdInput.val().trim()) {

        var config = {
            icon: DialogConfig.Icon_Love,
            text: String.changePsdConfirmSame,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.NoCancelable
        }
        SDK.showDialog(null, config);
        return;
    }
    // 当前密码和新密码相同
    if (this.currentPwdInput.val().trim() == this.newPwdInput.val().trim()) {
        var config = {
            icon: DialogConfig.Icon_Love,
            text: String.changePsdNotSame,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.NoCancelable
        }
        SDK.showDialog(null, config);
        return;
    }
    // 新密码不大于4位
    if (this.newPwdInput.val().trim().length <= 4) {
        var config = {
            icon: DialogConfig.Icon_Love,
            text: String.changePsdLengthInvalid,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.NoCancelable
        }
        SDK.showDialog(null, config);
        return;
    }

    var params = {
        old_passwd: this.currentPwdInput.val().trim(),
        new_passwd: this.newPwdInput.val().trim(),
        confirm_passwd: this.confirmPwdInput.val().trim()
    };
    SDK.showLoading();
    USER.updatePassword(params, $.proxy(this.onUpdatePwdListener, this));
};

PasswordPage.prototype.onUpdatePwdListener = function (result) {

    SDK.hideLoading();
    if (result == Constants.CALLBACK_SUCCESS) {

        var params = {
            password: this.newPwdInput.val().trim()
        }
        SDK.setWebData(Constants.UPDATE_PASSWORD_KEY, params);
        SDK.back();
    } else {
        SDK.showFailedPrompt();
    }
};