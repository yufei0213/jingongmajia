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

    page = new LanguagePage($('#page'), params);
};

var LanguagePage = function (view, params) {

    this.view = view;

    this.englishBox = null;
    this.chineseBox = null;
    this.saveBtn = null;

    this.init();
};

LanguagePage.prototype.init = function () {

    this.englishBox = this.view.find('[sid=englishBox]');
    this.chineseBox = this.view.find('[sid=chineseBox]');
    this.saveBtn = this.view.find('[sid=saveBtn]');

    SDK.getLanguageSetting($.proxy(function (type) {

        this.setView(type);
        this.originType = type;
    }, this));
    this.englishBox.on('click', $.proxy(this.onEnglishBoxClick, this));
    this.chineseBox.on('click', $.proxy(this.onChineseBoxClick, this));
    this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));

};

LanguagePage.prototype.onEnglishBoxClick = function () {

    this.setView(Constants.langeuageEN);
};

LanguagePage.prototype.onChineseBoxClick = function () {

    this.setView(Constants.langeuageZH);
};

LanguagePage.prototype.setView = function (type) {

    if (type == Constants.langeuageZH) {
        this.englishBox.find('[sid=checkImg]').addClass('dn');
        this.chineseBox.find('[sid=checkImg]').removeClass('dn');
    } else {
        this.englishBox.find('[sid=checkImg]').removeClass('dn');
        this.chineseBox.find('[sid=checkImg]').addClass('dn');
    }
};

LanguagePage.prototype.onSaveBtnClick = function () {

    if (Global.isAndroid) {

        SDK.showLoading();
    }

    if (this.englishBox.find('[sid=checkImg]').hasClass("dn")) {

        if (this.originType != Constants.langeuageZH) {

            var data = {
                data: Constants.langeuageZH
            };
            SDK.setLanguage(data);
        }
        SDK.back();
    } else if (this.chineseBox.find('[sid=checkImg]').hasClass("dn")) {

        if (this.originType != Constants.langeuageEN) {

            var data = {
                data: Constants.langeuageEN
            };
            SDK.setLanguage(data);
        }
        SDK.back();
    }

};