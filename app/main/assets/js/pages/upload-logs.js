/**
 * @author yufei0213
 * @date 2018/7/9
 * @description
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.uploadPage = new UploadPage(this.startUpload, this);
    this.uploadingPage = new UploadingPage();
    this.successView = new SuccessView();
    this.failedView = new FailedView(this.startUpload, this);
};

Web.startUpload = function () {

    this.uploadPage.hide();
    this.failedView.hide();
    this.uploadingPage.show();

    SDK.uploadLogs($.proxy(function (code, progress) {

        if (code == Constants.CALLBACK_FAILURE) {

            this.uploadingPage.hide();
            this.failedView.show();
        } else if (code == Constants.CALLBACK_SUCCESS) {

            this.uploadingPage.progress(progress);
        } else if (code == Constants.CALLBACK_COMPLETE) {

            this.uploadingPage.hide();
            this.successView.show();
        }
    }, this));
};

var UploadPage = function (uploadBtnClickListener, context) {

    this.view = $("#uploadView");
    this.uploadBtn = this.view.find("[sid=uploadBtn]");

    this.uploadBtn.bind("click", $.proxy(uploadBtnClickListener, context));
};

UploadPage.prototype.show = function () {

    this.view.removeClass("dn");
};

UploadPage.prototype.hide = function () {

    this.view.addClass("dn");
};

var UploadingPage = function () {

    this.view = $("#uploadingView");
    this.progressView = this.view.find("[sid=progressView]");
};

UploadingPage.prototype.show = function () {

    this.view.removeClass("dn");
    this.progress(0);
};

UploadingPage.prototype.hide = function () {

    this.view.addClass("dn");
};

UploadingPage.prototype.progress = function (progress) {

    this.progressView.css({
        width: progress + "%"
    });
};

var SuccessView = function () {

    this.view = $("#successView");
    this.okBtn = this.view.find("[sid=okBtn]");

    this.okBtn.bind("click", $.proxy(function () {

        SDK.back();
    }, this));
};

SuccessView.prototype.show = function () {

    this.view.removeClass("dn");
};

SuccessView.prototype.hide = function () {

    this.view.addClass("dn");
};

var FailedView = function (uploadBtnClickListener, context) {

    this.view = $("#failedView");
    this.tryAgainBtn = this.view.find("[sid=tryAgainBtn]");
    this.cancelBtn = this.view.find("[sid=cancelBtn]");

    this.tryAgainBtn.bind("click", $.proxy(uploadBtnClickListener, context));
    this.cancelBtn.bind("click", $.proxy(function () {

        SDK.back();
    }, this));
};

FailedView.prototype.show = function () {

    this.view.removeClass("dn");
};

FailedView.prototype.hide = function () {

    this.view.addClass("dn");
};