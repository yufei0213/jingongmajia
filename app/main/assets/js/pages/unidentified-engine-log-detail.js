/**
 * @author mamw
 * @date 2018/1/28
 * @description unidentified driver log list detail页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    new UnidentifiedEngineLogDetailPage($('#page'), params);
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {
        id: "",
        vehicleCode: null,
        type: null,
        code: null,
        datetimeStr: "",
        location: "",
        totalOdometer: "",
        totalEngineHours: "",
    };

    /*
     * 界面
     **/
    var UnidentifiedEngineLogDetailPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.vehicleNoLabel = null;
        this.stateLabel = null;
        this.timeLabel = null;
        this.locationLabel = null;
        this.odometerLabel = null;
        this.engineHourLabel = null;
        this.rejectBtn = null;
        this.acceptBtn = null;
        /* 初始化数据 */
        this.updateStatusParams = null;
        /* 初始化方法 */
        this.init();
    };

    UnidentifiedEngineLogDetailPage.prototype.init = function () {

        /* 绑定组件 */
        this.rejectBtn = this.view.find('[sid=rejectBtn]');
        this.acceptBtn = this.view.find('[sid=acceptBtn]');
        /* 绑定事件 */
        this.vehicleNoLabel = this.view.find('[sid=vehicleNoLabel]');
        this.stateLabel = this.view.find('[sid=stateLabel]');
        this.timeLabel = this.view.find('[sid=timeLabel]');
        this.locationLabel = this.view.find('[sid=locationLabel]');
        this.odometerLabel = this.view.find('[sid=odometerLabel]');
        this.engineHourLabel = this.view.find('[sid=engineHourLabel]');
        this.rejectBtn.on('click', $.proxy(this.onRejectBtnClick, this));
        this.acceptBtn.on('click', $.proxy(this.onAcceptBtnClick, this));

        this.vehicleNoLabel.text(this.vehicleCode);
        var stateName = '';
        if (this.code == 1 || this.code == 2) {
            stateName = 'Power On';
        }else if (this.code == 3 || this.code == 4) {
            stateName = 'Power Off';
        }
        this.stateLabel.text(stateName);
        this.timeLabel.text(this.datetimeStr);
        this.locationLabel.text(this.location);
        this.odometerLabel.text(this.totalOdometer);
        this.engineHourLabel.text(this.totalEngineHours);
    };

    UnidentifiedEngineLogDetailPage.prototype.onRejectBtnClick = function () {

        var params = {
            ids: [
                {
                    startId: this.id,
                    endId: this.id
                }
            ],
            result: 1
        };
        this.updateStatusParams = params;

        SDK.showLoading();
        ALERT.updateUnidentifiedDriverLogStatus(this.updateStatusParams, $.proxy(this.requestUpdateUnidentifiedDriverLogStatusCallBackListener, this));
    };

    UnidentifiedEngineLogDetailPage.prototype.onDialogCallbackListener = function (which) {

        if (which == Constants.BUTTON_NEUTRAL) {

            SDK.showLoading();
            ALERT.updateUnidentifiedDriverLogStatus(this.updateStatusParams, $.proxy(this.requestUpdateUnidentifiedDriverLogStatusCallBackListener, this));
        }
    };

    UnidentifiedEngineLogDetailPage.prototype.requestUpdateUnidentifiedDriverLogStatusCallBackListener = function (result) {

        SDK.hideLoading();
        var self = this;
        if (result == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt(function () {
                SDK.back();
            });
        } else if (result == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt(function () {
            });
        }
    };

    UnidentifiedEngineLogDetailPage.prototype.onAcceptBtnClick = function () {

        var params = {
            ids: [
                {
                    startId: this.id,
                    endId: this.id
                }
            ],
            objects: [],
            result: 0 // 接受是0
        };
        this.updateStatusParams = params;

        var dialogConfig = {
            icon: DialogConfig.Icon_Msg,
            text: String.unidentifiedDetailAcceptConfirm
        };
        SDK.showDialog($.proxy(this.onDialogCallbackListener, this), dialogConfig);
    };

    UnidentifiedEngineLogDetailPage.prototype.requestCallBackListener = function (result) {

        SDK.hideLoading();
        if (result == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt(function () {
                SDK.back();
            });
        } else if (result == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt(function () {
            });
        }
    };

    window.UnidentifiedEngineLogDetailPage = UnidentifiedEngineLogDetailPage;
}($, window));
