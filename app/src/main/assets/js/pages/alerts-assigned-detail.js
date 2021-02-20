/**
 * @author mamw
 * @date 2018/1/28
 * @description alert assigned detail页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    new AlertAssignedDetail($('#page'), params);
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {
        startId: "",
        endId: "",
        vehicleNo: null,
        intervalTime: "",
        intervalTimeStr: "",
        intervalOdometer: "",
        startLocation: "",
        endLocation: "",
        assignedName: "",
        assignedId: "",
        state: "",
        startTime: "",
        endTime: "",
        start_end_odometer: "",
        engineTime: "",
        comment: "",
        gridModelList: [],
        gridModel: null
    };

    /*
     * 界面
     **/
    var AlertAssignedDetail = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.assignedNameLabel = null;
        this.assignedIdLabel = null;
        this.grid = null;
        this.vehicleNoLabel = null;
        this.stateLabel = null;
        this.startTimeLabel = null;
        this.endTimeLabel = null;
        this.intervalLabel = null;
        this.odometerLabel = null;
        this.engineHourLabel = null;
        this.startLocationLabel = null;
        this.endLocationLabel = null;
        this.rejectBtn = null;
        this.acceptBtn = null;
        /* 初始化数据 */
        /* 初始化方法 */
        this.init();
    };

    AlertAssignedDetail.prototype.init = function () {

        /* 绑定组件 */
        this.rejectBtn = this.view.find('[sid=rejectBtn]');
        this.acceptBtn = this.view.find('[sid=acceptBtn]');
        /* 绑定事件 */
        this.assignedNameLabel = this.view.find('[sid=assignedNameLabel]');
        this.assignedIdLabel = this.view.find('[sid=assignedIdLabel]');
        this.vehicleNoLabel = this.view.find('[sid=vehicleNoLabel]');
        this.stateLabel = this.view.find('[sid=stateLabel]');
        this.startTimeLabel = this.view.find('[sid=startTimeLabel]');
        this.endTimeLabel = this.view.find('[sid=endTimeLabel]');
        this.intervalLabel = this.view.find('[sid=intervalLabel]');
        this.odometerLabel = this.view.find('[sid=odometerLabel]');
        this.engineHourLabel = this.view.find('[sid=engineHourLabel]');
        this.startLocationLabel = this.view.find('[sid=startLocationLabel]');
        this.endLocationLabel = this.view.find('[sid=endLocationLabel]');
        this.rejectBtn.on('click', $.proxy(this.onRejectBtnClick, this));
        this.acceptBtn.on('click', $.proxy(this.onAcceptBtnClick, this));
        /* 加载数据 */

        this.assignedNameLabel.text(this.assignedName);
        this.assignedIdLabel.text(this.assignedId);

        SDK.getTotalHourByDateTime($.proxy(function (hour) {
            // 图形内容
            this.grid = new DriverGrid("grid", hour);
            this.grid.draw(this.gridModelList, false);
            this.grid.coverShadow(this.gridModel.startSecond, this.gridModel.endSecond);
        }, this), this.datetime);

        this.vehicleNoLabel.text(this.vehicleNo);
        this.stateLabel.text(this.state);
        this.startTimeLabel.text(this.startTime);
        this.endTimeLabel.text(this.endTime);
        this.intervalLabel.text(this.intervalTime);
        this.odometerLabel.text(this.start_end_odometer);
        this.engineHourLabel.text(this.engineTime);
        this.startLocationLabel.text(this.startLocation);
        this.endLocationLabel.text(this.endLocation);
    };

    AlertAssignedDetail.prototype.onRejectBtnClick = function () {

        var params = {
            ids: [
                {
                    startId: this.startId,
                    endId: this.endId
                }
            ],

            result: 1
        };

        SDK.showLoading();
        ALERT.updateAssignedAlertStatus(params, $.proxy(this.requestCallBackListener, this));
    };

    AlertAssignedDetail.prototype.onAcceptBtnClick = function () {

        var params = {
            ids: [
                {
                    startId: this.startId,
                    endId: this.endId
                }
            ],
            objects: [
                {
                    startJson: this.startJson,
                    endJson: this.endJson
                }
            ],
            result: 0 // 接受是0
        };

        var replaceArr = [];
        replaceArr.push(String.accept);
        var numStr = "";
        if (params.ids.length > 1) {
            numStr = params.ids.length + String.logs;
        } else {
            numStr = String.log;
        }
        replaceArr.push(numStr);

        var config = {
            icon: DialogConfig.Icon_Msg,
            text: this.formatStr(String.assignDialogTip, replaceArr),
            negativeBtnText: String.cancel,
            neutralBtnText: String.confirm,
            cancelable: DialogConfig.NoCancelable
        };
        SDK.showDialog($.proxy(function (button) {
            if (button == Constants.BUTTON_NEUTRAL) {

                SDK.showLoading();
                ALERT.updateAssignedAlertStatus(params, $.proxy(this.requestCallBackListener, this));
            }
        }, this), config);
    };

    AlertAssignedDetail.prototype.requestCallBackListener = function (result) {

        SDK.hideLoading();
        if (result == Constants.CALLBACK_SUCCESS) {

            SDK.back();
        } else if (result == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt(function () {
            });
        }
    };

    // 通配符函数
    AlertAssignedDetail.prototype.formatStr = function (str, replaceArr) {

        if (replaceArr.length == 0) return this;
        for (var i = 0; i < replaceArr.length; i++)
            str = str.replace(new RegExp("\\{" + i + "\\}", "g"), replaceArr[i]);
        return str;
    };

    window.AlertAssignedDetail = AlertAssignedDetail;
}($, window));