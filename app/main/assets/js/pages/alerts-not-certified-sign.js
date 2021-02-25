/**
 * @author mamw
 * @date 2018/1/28
 * @description alert not certified sign页面
 */
var page = null;

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.initSignCanvas();

    page = new AlertNotCertifiedSign($('#page'), params);
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {
        date: null
    };

    /*
     * 界面
     **/
    var AlertNotCertifiedSign = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.signaturePad = null;
        this.clearBtn = null;
        this.saveBtn = null;
        /* 初始化数据 */
        /* 初始化方法 */
        this.init();
    };

    AlertNotCertifiedSign.prototype.init = function () {

        /* 绑定组件 */
        this.signaturePad = new SignaturePad($('[sid=signCanvas]').get(0), {
            backgroundColor: 'rgb(255, 255, 255)',
            minWidth: 1,
            maxWidth: 1
        });
        this.clearBtn = this.view.find('[sid=clearBtn]');
        this.saveBtn = this.view.find('[sid=saveBtn]');
        this.editTab = this.view.find('[sid=editTab]');
        /* 绑定事件 */
        this.clearBtn.on('click', $.proxy(this.onClearBtnClick, this));
        this.saveBtn.on('click', $.proxy(this.onSaveBtnClick, this));
        /* 加载数据 */
    };

    AlertNotCertifiedSign.prototype.onClearBtnClick = function () {

        this.signaturePad.clear();
    };

    AlertNotCertifiedSign.prototype.onSaveBtnClick = function () {

        if (this.signaturePad.isEmpty()) {
            var config = {
                icon: DialogConfig.Icon_Love,
                text: String.notSignatureTip,
                positiveBtnText: String.ok,
                cancelable: DialogConfig.NoCancelable
            };
            SDK.showDialog(null, config);
            return;
        }

        ImageUtil.zipBase64(this.signaturePad.toDataURL(), $.proxy(function (driver_sign) {

            var params = {
                date: this.date,
                content: driver_sign
            };

            SDK.showLoading();
            ALERT.uploadNotCertifiedAlertSign(params, function (result) {

                SDK.hideLoading();
                if (result == Constants.CALLBACK_SUCCESS) {

                    SDK.back();
                } else if (result == Constants.CALLBACK_FAILURE) {

                    SDK.showFailedPrompt(null);
                }
            });
        }, this));
    };

    window.AlertNotCertifiedSign = AlertNotCertifiedSign;
}($, window));