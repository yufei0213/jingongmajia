/**
 * @author mamw
 * @date 2018/1/25
 * @description dot send页面
 */
var page = null;

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function () {

    page = new DotSendPage($('#page'));
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {};

    /*
     * 界面
     **/
    var DotSendPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;

        /* 初始化方法 */
        this.init();
    };

    DotSendPage.prototype.init = function () {

        this.pdfTips = this.view.find("[sid=pdfTips]");
        this.pdfBox = this.view.find("[sid=pdfBox]");
        this.pdfInput = this.view.find("[sid=pdfInput]");
        this.pdfInputBox = this.view.find("[sid=pdfInputBox]");
        this.pdfTipsLabel = this.pdfInputBox.find("[sid=tipsLabel]");
        this.pdfCommentBox = this.view.find("[sid=pdfCommentBox]");
        this.pdfCommentInput = this.view.find("[sid=pdfCommentInput]");
        this.pdfCommentTip = this.view.find('[sid=pdfCommentTip]');
        this.saveBtn = this.view.find('[sid=saveBtn]');

        this.pdfInput.bind("input propertychange", $.proxy(function () {

            this.pdfInputBox.removeClass('warning');
            this.pdfTipsLabel.text("");
        }, this));

        this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));
    };

    DotSendPage.prototype.onSaveBtnClick = function () {

        if (!this.checkFormValid()) {

            return;
        }

        SDK.showDotLoading();

        DOT.requestSendPdf({
            email: this.pdfInput.val(),
            comment: this.pdfCommentInput.val().trim()
        }, $.proxy(this.requestSendPdfCallBack, this));
    };

    DotSendPage.prototype.requestSendPdfCallBack = function (data) {

        SDK.hideDotLoading();
        if (data == Constants.CALLBACK_SUCCESS) {

            SDK.showDotSuccessPrompt($.proxy(this.onSendPdfSuccessPromptCallBack, this));
        } else {

            var dialogConfig = {
                icon: DialogConfig.Icon_Awkward,
                text: String.dotSendFailed,
                positiveBtnText: String.ok,
                cancelable: DialogConfig.Cancelable
            };
            SDK.showDialog(null, dialogConfig);
        }
    };

    DotSendPage.prototype.onSendPdfSuccessPromptCallBack = function () {

        this.pdfInput.val("");
        this.pdfCommentInput.val("");
        this.emailCommentInput.val("");
        DOT.selectReviewTabItem();
    };

    // 表单验证
    DotSendPage.prototype.checkFormValid = function () {

        var isValid = true;

        // 验证邮箱
        var emailFormat = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
        if (this.pdfInput.val().trim().length == 0) {

            this.pdfInputBox.addClass('warning');
            this.pdfTipsLabel.text(String.emailEmptyValid);
            isValid = false;
        } else if (!emailFormat.test(this.pdfInput.val().trim())) {

            this.pdfInputBox.addClass('warning');
            this.pdfTipsLabel.text(String.emailValid);
            isValid = false;
        }

        var pdfComment = this.pdfCommentInput.val().trim();
        if (pdfComment.match(Constants.inputRegexp)) {

            this.pdfCommentBox.addClass("warning");
            this.pdfCommentTip.html(String.remarkDotValid);
            isValid = false;
        }

        return isValid;
    };

    window.DotSendPage = DotSendPage;
}($, window));