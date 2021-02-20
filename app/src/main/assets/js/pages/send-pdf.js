/**
 * @author yufei0213
 * @date 2018/3/14
 * @description 发送pdf邮件
 */

/**
 * 页面初始化
 * @param params
 */
Web.init = function (params) {

    this.startDateBox = $("#startDateBox");
    this.startDateInput = this.startDateBox.find("[sid=startDateInput]");
    this.startDateTipLabel = this.startDateBox.find("[sid=startDateTipLabel]");

    this.endDateBox = $("#endDateBox");
    this.endDateInput = this.endDateBox.find("[sid=endDateInput]");
    this.endDateTipLabel = this.endDateBox.find("[sid=endDateTipLabel]");

    this.emailBox = $("#emailBox");
    this.emailInput = this.emailBox.find("[sid=emailInput]");
    this.emailTipLabel = this.emailBox.find("[sid=emailTipLabel]");

    this.commentBox = $("#commentBox");
    this.commentInput = this.commentBox.find("[sid=commentInput]");
    this.commentTipLabel = this.commentBox.find("[sid=commentTipLabel]");

    this.saveBtn = $("#saveBtn");
    this.saveBtn.bind("click", $.proxy(this.onSaveBtnClick, this));

    this.initInput();
};

/**
 * 初始化输入框
 */
Web.initInput = function () {

    SDK.getOffsetDate($.proxy(function (dateStr) {

        this.startDateInput.removeClass("pld");
        this.startDateInput.html(dateStr);
    }, this), -7);

    SDK.getOffsetDate($.proxy(function (dateStr) {

        this.endDateInput.removeClass("pld");
        this.endDateInput.html(dateStr);
    }, this), 0);

    this.startDateBox.bind("click", $.proxy(function () {

        if (this.startDateInput.hasClass("pld")) {

            return;
        }

        SDK.showDatePicker(this.startDateInput.html().trim(), $.proxy(function (dateStr) {

            var end = this.endDateInput.html().trim();
            if (!this.checkDateValid(dateStr, end)) {

                SDK.showMessage(String.sendPdfValidTip);
            } else if (!this.checkDateValue(dateStr, end)) {

                SDK.showMessage(String.sendPdfValueTip);
            } else {

                this.startDateInput.html(dateStr);
            }
        }, this));
    }, this));

    this.endDateBox.bind("click", $.proxy(function () {

        if (this.endDateInput.hasClass("pld")) {

            return;
        }

        SDK.showDatePicker(this.endDateInput.html().trim(), $.proxy(function (dateStr) {

            var start = this.startDateInput.html().trim();
            if (!this.checkDateValid(start, dateStr)) {

                SDK.showMessage(String.sendPdfValidTip);
            } else if (!this.checkDateValue(start, dateStr)) {

                SDK.showMessage(String.sendPdfValueTip);
            } else {

                this.endDateInput.html(dateStr);
            }
        }, this));
    }, this));

    this.emailInput.bind("input propertychange", $.proxy(function () {

        this.emailBox.removeClass('warning');
        this.emailTipLabel.text("");
    }, this));
    this.commentInput.bind("input propertychange", $.proxy(function () {

        this.commentBox.removeClass('warning');
        this.commentTipLabel.text("");
    }, this));
};

/**
 * 检查日期是否合法
 * @param start 开始日期
 * @param end 结束日期
 */
Web.checkDateValid = function (start, end) {

    var startArray = start.split("/");
    start = startArray[2] + "/" + startArray[0] + "/" + startArray[1];
    var endArray = end.split("/");
    end = endArray[2] + "/" + endArray[0] + "/" + endArray[1];

    var startDate = new Date(start);
    var endDate = new Date(end);

    return endDate.getTime() > startDate.getTime();
};

/**
 * 检查日期是否在半年内
 * @param start 开始日期
 * @param end 结束日期
 */
Web.checkDateValue = function (start, end) {

    var startArray = start.split("/");
    start = startArray[2] + "/" + startArray[0] + "/" + startArray[1];
    var endArray = end.split("/");
    end = endArray[2] + "/" + endArray[0] + "/" + endArray[1];

    var startDate = new Date(start);
    var endDate = new Date(end);
    var offset = endDate.getTime() - startDate.getTime();
    var days = parseInt(offset / (1000 * 60 * 60 * 24));

    return days < 180;
};

/**
 * 检查表单填写是否正确
 */
Web.checkInput = function () {

    var result = true;

    if (this.commentInput.val().match(Constants.inputRegexp)) {

        this.commentBox.addClass("warning");
        this.commentTipLabel.html(String.remarkValid);
        result = false;
    }

    var emailFormat = /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/;
    if (this.emailInput.val().trim().length == 0) {

        this.emailBox.addClass('warning');
        this.emailTipLabel.text(String.emailEmptyValid);
        result = false;
    } else if (!emailFormat.test(this.emailInput.val().trim())) {

        this.emailBox.addClass('warning');
        this.emailTipLabel.text(String.emailValid);
        result = false;
    }

    return result;
};

/**
 * save按钮被点击
 */
Web.onSaveBtnClick = function () {

    if (this.checkInput()) {

        SDK.showLoading();

        var startDate = this.startDateInput.html().trim();
        var endDate = this.endDateInput.html().trim();
        var email = this.emailInput.val().trim();
        var comment = this.commentInput.val().trim();

        DOT.requestSendPdf({
            startDate: startDate,
            endDate: endDate,
            email: email,
            comment: comment
        }, $.proxy(this.requestSendEmailCallback, this));
    }
};

/**
 *
 * @param code
 */
Web.requestSendEmailCallback = function (code) {

    SDK.hideLoading();
    if (code == Constants.CALLBACK_SUCCESS) {

        SDK.showSuccessPrompt($.proxy(function () {

            SDK.back();
        }, this));
    } else {

        SDK.showFailedPrompt();
    }
};
