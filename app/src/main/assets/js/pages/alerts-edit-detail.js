/**
 * @author mamw
 * @date 2018/1/28
 * @description alert edit detail页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    new AlertEditDetail($('#page'), params);
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {
        id: "",
        showGrid: 0,
        createDateStr: "", // MON,JAN 8
        createTimeStr: "",// O6：23AM EST
        editorId: "",
        editor: "",
        editDateStr: "",
        editTypeStr: "",
        comment: "",
        state: "",
        gridModelList: [],
        gridModel: null
    };

    /*
     * 界面
     **/
    var AlertEditDetail = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.editTypeLabel = null;
        this.editorLabel = null;
        this.editIdLabel = null;
        this.grid = null;
        this.stateLabel = null;
        this.timeLabel = null;
        this.odometerLabel = null;
        this.locationLabel = null;
        this.remarkLabel = null;
        this.rejectBtn = null;
        this.acceptBtn = null;
        /* 初始化数据 */
        /* 初始化方法 */
        this.init();
    };

    AlertEditDetail.prototype.init = function () {

        /* 绑定组件 */
        this.editTypeLabel = this.view.find('[sid=editTypeLabel]');
        this.editorLabel = this.view.find('[sid=editorLabel]');
        this.editIdLabel = this.view.find('[sid=editIdLabel]');
        this.gridBox = this.view.find('[sid=gridBox]');
        this.stateLabel = this.view.find('[sid=stateLabel]');
        this.timeLabel = this.view.find('[sid=timeLabel]');
        this.odometerLabel = this.view.find('[sid=odometerLabel]');
        this.locationLabel = this.view.find('[sid=locationLabel]');
        this.remarkLabel = this.view.find('[sid=remarkLabel]');
        this.vehicleLi = this.view.find('[sid=vehicleLi]');
        this.vehicleLabel = this.view.find('[sid=vehicleLabel]');
        this.rejectBtn = this.view.find('[sid=rejectBtn]');
        this.acceptBtn = this.view.find('[sid=acceptBtn]');

        this.rejectBtn.on('click', $.proxy(this.onRejectBtnClick, this));
        this.acceptBtn.on('click', $.proxy(this.onAcceptBtnClick, this));

        if (this.showGrid == 0) {

            this.gridBox.addClass("dn");
        }else {

            SDK.getTotalHourByDateTime($.proxy(function (hour) {
                // 图形内容
                this.grid = new DriverGrid("grid", hour);
                this.grid.draw(this.gridModelList, false);
                this.grid.coverShadow(this.gridModel.startSecond, this.gridModel.endSecond);
            }, this), this.datetime);
        }

        this.editTypeLabel.text(this.editTypeStr);
        this.editorLabel.text(this.editor);
        this.editIdLabel.text(this.editorId);
        if (this.vehicle) {

            this.vehicleLi.removeClass("dn");
            this.vehicleLabel.text(this.vehicle);
        }
        this.editIdLabel.text(this.editorId);

        this.stateLabel.text(this.state);
        this.timeLabel.text(this.createTimeStr);
        this.odometerLabel.text(this.odometer);
        this.locationLabel.text(this.location);
        if (this.comment == "") {

            this.remarkLabel.addClass('dn');
            this.view.find('[sid=remarkEmptyLabel]').removeClass('dn');
        } else {

            this.remarkLabel.text(this.comment);
        }
    };

    AlertEditDetail.prototype.onRejectBtnClick = function () {

        var params = {
            id: this.id,
            status: 0
        };

        SDK.showLoading();
        ALERT.updateEditAlertStatus(params, $.proxy(this.requestCallBackListener, this));
    };

    AlertEditDetail.prototype.onAcceptBtnClick = function () {

        var config = {
            icon: DialogConfig.Icon_Msg,
            text: String.requestEditAcceptTip,
            negativeBtnText: String.cancel,
            neutralBtnText: String.confirm
        };
        SDK.showDialog($.proxy(function (button) {
            if (button == Constants.BUTTON_NEUTRAL) {
                var params = {
                    id: this.id,
                    status: 1,
                    log: this.logJson
                };

                SDK.showLoading();
                ALERT.updateEditAlertStatus(params, $.proxy(this.requestCallBackListener, this));
            }
        }, this), config);
    };

    AlertEditDetail.prototype.requestCallBackListener = function (result) {

        SDK.hideLoading();
        if (result == Constants.CALLBACK_SUCCESS) {

            SDK.back();
        } else if (result == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt(null);
        }
    };

    window.AlertEditDetail = AlertEditDetail;
}($, window));