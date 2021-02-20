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

        this.type = 0;
        /* 初始化方法 */
        this.init();
    };

    DotSendPage.prototype.init = function () {

        /* 绑定组件 */
        this.emailTab = this.view.find('[sid=emailTab]');
        this.webServiceTab = this.view.find('[sid=webServiceTab]');
        this.pdfTab = this.view.find("[sid=pdfTab]");

        this.webServiceBox = this.view.find('[sid=webServiceBox]');
        this.webServiceCommentBox = this.view.find('[sid=webServiceCommentBox]');
        this.webServiceCommentInput = this.view.find('[sid=webServiceCommentInput]');
        this.webServiceCommentTip = this.view.find('[sid=webServiceCommentTip]');

        this.emailBox = this.view.find('[sid=emailBox]');
        this.emailInput = this.view.find('[sid=emailInput]');
        this.emailCommentBox = this.view.find('[sid=emailCommentBox]');
        this.emailCommentInput = this.view.find('[sid=emailCommentInput]');
        this.emailCommentTip = this.view.find('[sid=emailCommentTip]');

        this.pdfTips = this.view.find("[sid=pdfTips]");
        this.pdfBox = this.view.find("[sid=pdfBox]");
        this.pdfInput = this.view.find("[sid=pdfInput]");
        this.pdfInputBox = this.view.find("[sid=pdfInputBox]");
        this.pdfTipsLabel = this.pdfInputBox.find("[sid=tipsLabel]");
        this.pdfCommentBox = this.view.find("[sid=pdfCommentBox]");
        this.pdfCommentInput = this.view.find("[sid=pdfCommentInput]");
        this.pdfCommentTip = this.view.find('[sid=pdfCommentTip]');

        this.saveBtn = this.view.find('[sid=saveBtn]');

        this.webServiceTab.on('click', $.proxy(this.onWebServiceTabClick, this));
        this.emailTab.on('click', $.proxy(this.onEmailTabClick, this));
        this.pdfTab.on("click", $.proxy(this.onPdfTabClick, this));

        this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));

        this.webServiceCommentInput.bind("input propertychange", $.proxy(function () {

            this.webServiceCommentBox.removeClass('warning');
        }, this));
        this.emailCommentInput.bind("input propertychange", $.proxy(function () {

            this.emailCommentBox.removeClass('warning');
        }, this));
        this.pdfInput.bind("input propertychange", $.proxy(function () {

            this.pdfInputBox.removeClass('warning');
            this.pdfTipsLabel.text("");
        }, this));
    };

    DotSendPage.prototype.onWebServiceTabClick = function () {

        this.webServiceTab.addClass("on");
        this.emailTab.removeClass("on");
        this.pdfTab.removeClass("on");

        this.webServiceBox.removeClass("dn");
        this.emailBox.addClass("dn");
        this.pdfTips.addClass("dn");
        this.pdfBox.addClass("dn");

        this.emailCommentBox.removeClass("warning");
        this.webServiceCommentBox.removeClass("warning");
        this.pdfCommentBox.removeClass("warning");

        this.type = 0;
    };

    DotSendPage.prototype.onEmailTabClick = function () {

        this.emailTab.addClass("on");
        this.webServiceTab.removeClass("on");
        this.pdfTab.removeClass("on");

        this.emailBox.removeClass("dn");
        this.webServiceBox.addClass("dn");
        this.pdfTips.addClass("dn");
        this.pdfBox.addClass("dn");

        this.emailCommentBox.removeClass("warning");
        this.webServiceCommentBox.removeClass("warning");
        this.pdfCommentBox.removeClass("warning");

        this.type = 1;
    };

    DotSendPage.prototype.onPdfTabClick = function () {

        this.pdfTab.addClass("on");
        this.emailTab.removeClass("on");
        this.webServiceTab.removeClass("on");

        this.pdfTips.removeClass("dn");
        this.pdfBox.removeClass("dn");
        this.emailBox.addClass("dn");
        this.webServiceBox.addClass("dn");

        this.emailCommentBox.removeClass("warning");
        this.webServiceCommentBox.removeClass("warning");
        this.pdfCommentBox.removeClass("warning");

        this.type = 2;
    };

    DotSendPage.prototype.onSaveBtnClick = function () {

        if (!this.checkFormValid()) {

            return;
        }

        SDK.showDotLoading();

        if (this.type == 0) {

            DOT.requestWebService({
                email: this.emailInput.val(),
                comment: this.webServiceCommentInput.val().trim()
            }, $.proxy(this.requestWebServiceCallBack, this));
        } else if (this.type == 1) {

            DOT.requestSendEmail({
                email: this.emailInput.val(),
                comment: this.emailCommentInput.val().trim()
            }, $.proxy(this.requestSendEmailCallBack, this));
        } else if (this.type == 2) {

            DOT.requestSendPdf({
                email: this.pdfInput.val(),
                comment: this.pdfCommentInput.val().trim()
            }, $.proxy(this.requestSendEmailCallBack, this));
        }
    };

    DotSendPage.prototype.requestSendEmailCallBack = function (data) {

        SDK.hideDotLoading();
        if (data == Constants.CALLBACK_SUCCESS) {

            SDK.showDotSuccessPrompt($.proxy(this.onSendEmailSuccessPromptCallBack, this));
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

    DotSendPage.prototype.requestWebServiceCallBack = function (status, submissionId) {

        SDK.hideDotLoading();
        if (status == Constants.CALLBACK_SUCCESS) {

            this.submissionId = submissionId;
            SDK.showDotSuccessPrompt($.proxy(this.onSendWebServiceSuccessPromptCallBack, this));
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

    DotSendPage.prototype.onSendEmailSuccessPromptCallBack = function () {

        this.pdfInput.val("");
        this.pdfCommentInput.val("");
        this.emailCommentInput.val("");
        DOT.selectReviewTabItem();
    };

    DotSendPage.prototype.onSendWebServiceSuccessPromptCallBack = function () {

        var tips = "Submission ID : \n" + this.submissionId;
        var dialogConfig = {
            icon: DialogConfig.Icon_Love,
            text: tips,
            positiveBtnText: String.ok
        };
        SDK.showDialog($.proxy(function () {

            this.webServiceCommentInput.val("");
            DOT.selectReviewTabItem();
        }, this), dialogConfig);
    };

    // 表单验证
    DotSendPage.prototype.checkFormValid = function () {

        var isValid = true;

        if (this.type == 0) {

            var webServiceComment = this.webServiceCommentInput.val().trim();
            if (webServiceComment.length < Constants.remarkMinLength) {

                this.webServiceCommentBox.addClass("warning");
                this.webServiceCommentTip.html(String.remarkMinDotValid.replace("#count#", Constants.remarkMinLength.toString()));
                isValid = false;
            }

            if (webServiceComment.length > Constants.remarkMaxLength) {

                this.webServiceCommentBox.addClass("warning");
                this.webServiceCommentTip.html(String.remarkMaxDotValid.replace("#count#", Constants.remarkMaxLength.toString()));
                isValid = false;
            }

            if (webServiceComment.match(Constants.inputRegexp)) {

                this.webServiceCommentBox.addClass("warning");
                this.webServiceCommentTip.html(String.remarkDotValid);
                isValid = false;
            }
        }

        if (this.type == 1) {

            var emailComment = this.emailCommentInput.val().trim();
            if (emailComment.length < Constants.remarkMinLength) {

                this.emailCommentBox.addClass("warning");
                this.emailCommentTip.html(String.remarkMinDotValid.replace("#count#", Constants.remarkMinLength.toString()));
                isValid = false;
            }

            if (emailComment.length > Constants.remarkMaxLength) {

                this.emailCommentBox.addClass("warning");
                this.emailCommentTip.html(String.remarkMaxDotValid.replace("#count#", Constants.remarkMaxLength.toString()));
                isValid = false;
            }

            if (emailComment.match(Constants.inputRegexp)) {

                this.emailCommentBox.addClass("warning");
                this.emailCommentTip.html(String.remarkDotValid);
                isValid = false;
            }
        }

        if (this.type == 2) {

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
        }

        return isValid;
    };

    window.DotSendPage = DotSendPage;
}($, window));