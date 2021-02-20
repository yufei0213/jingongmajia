/**
 * @author zhangyu
 * @date 2018/1/22
 * @description
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    new HelpPage($('#page'), params);
};

var HelpPage = function (view, params) {

    this.view = view;
    this.emailBtn = null;
    this.emailUrlLabel = null;
    this.phoneBtn = null;
    this.phoneNoLabel = null;

    this.init();
};

HelpPage.prototype.init = function () {

    this.emailBtn = this.view.find('[sid=emailBtn]');
    this.emailUrlLabel = this.view.find('[sid=emailUrlLabel]');
    this.phoneBtn = this.view.find('[sid=phoneBtn]');
    this.phoneNoLabel = this.view.find('[sid=phoneNoLabel]');

    this.emailBtn.on('click', $.proxy(this.onEmailBtnClick, this));
    this.phoneBtn.on('click', $.proxy(this.onPhoneBtnClick, this));
};

HelpPage.prototype.onEmailBtnClick = function () {

    var params = {
        emailUrl: this.emailUrlLabel.text()
    };
    SDK.sendEmail(params);
};

HelpPage.prototype.onPhoneBtnClick = function () {

    var params = {
        phoneNo: this.phoneNoLabel.text()
    };
    SDK.call(params);
};




