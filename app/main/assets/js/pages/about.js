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

    page = new AboutPage($('#page'), params);

    SDK.collectFirebaseScreen("more-about");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function () {

    SDK.collectFirebaseScreen("more-about");
};

var AboutPage = function (view, params) {

    this.view = view;
    this.versionNameLabel = null;
    this.featuresBtn = null;
    this.versionBtn = null;

    this.versionName = params.versionName;
    this.init();
};

AboutPage.prototype.init = function () {

    this.versionNameLabel = this.view.find('[sid=versionNameLabel]');
    this.featuresBtn = this.view.find('[sid=featuresBtn]');
    this.versionBtn = this.view.find('[sid=versionBtn]');

    var featuresBtnClickProxy = new ClickProxy(this.onFeaturesBtnClick, this);
    this.featuresBtn.on('click', $.proxy(featuresBtnClickProxy.click, featuresBtnClickProxy));

    var versionBtnClickProxy = new ClickProxy(this.onVersionBtnClick, this);
    this.versionBtn.on('click', $.proxy(versionBtnClickProxy.click, versionBtnClickProxy));

    this.versionNameLabel.text(this.versionName);
};

AboutPage.prototype.onFeaturesBtnClick = function () {

    SDK.openPage(PageConfig.Features.url,
        PageConfig.Features.title);
};

AboutPage.prototype.onVersionBtnClick = function () {

};