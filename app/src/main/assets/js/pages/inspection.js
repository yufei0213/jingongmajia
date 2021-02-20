/**
 * @author sunyq
 * @date 2018/1/24
 * @description dvir
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    this.preTripBtn = $('[sid=preTripBtn]');
    this.interimBtn = $('[sid=interimBtn]');
    this.postTripBtn = $('[sid=postTripBtn]');
    this.historyBtn = $('[sid=historyBtn]');

    this.preTripBtn.on('click', $.proxy(this.onPreTripBtnClick, this));
    this.interimBtn.on('click', $.proxy(this.onInterimBtnClick, this));
    this.postTripBtn.on('click', $.proxy(this.onPostTripBtnClick, this));
    this.historyBtn.on('click', $.proxy(this.onHistoryBtnClick, this));

    this.currentInspection = INSPECTION.PRETRIP;
};

Web.onPreTripBtnClick = function () {

    USER.getUserRole($.proxy(function (roleId) {

        if (roleId == USERROLE.COPILOT) {

            this.showDisableDialog();
        } else {

            //判断当前状态是否为ODND
            this.currentInspection = INSPECTION.PRETRIP;
            DASHBOARD.getDriverState($.proxy(this.getStateResult, this));
        }
    }, this));
};

Web.onInterimBtnClick = function () {

    USER.getUserRole($.proxy(function (roleId) {

        if (roleId == USERROLE.COPILOT) {

            this.showDisableDialog();
        } else {

            //判断当前状态是否为ODND
            this.currentInspection = INSPECTION.INTERIM;
            DASHBOARD.getDriverState($.proxy(this.getStateResult, this));
        }
    }, this));
};

Web.onPostTripBtnClick = function () {

    USER.getUserRole($.proxy(function (roleId) {

        if (roleId == USERROLE.COPILOT) {

            this.showDisableDialog();
        } else {

            //判断当前状态是否为ODND
            this.currentInspection = INSPECTION.POSTTRIP;
            DASHBOARD.getDriverState($.proxy(this.getStateResult, this));
        }
    }, this));
};

Web.getStateResult = function (code) {

    var inspectionDriveStateTip = String.inspectionDriveStateTip;
    if (code == DRIVERSTATUS.DRIVING) {

        inspectionDriveStateTip = String.inspectionDriveStateTip.replace("#state#", String.driverStateD);
    } else if (code == DRIVERSTATUS.OFFDUTY) {

        inspectionDriveStateTip = String.inspectionDriveStateTip.replace("#state#", String.driverStateOff);
    } else if (code == DRIVERSTATUS.PERSONALUSE) {

        inspectionDriveStateTip = String.inspectionDriveStateTip.replace("#state#", String.driverStatePersonal);
    } else if (code == DRIVERSTATUS.YARDMOVE) {

        inspectionDriveStateTip = String.inspectionDriveStateTip.replace("#state#", String.driverYardMove);
    } else if (code == DRIVERSTATUS.SLEEPER) {

        inspectionDriveStateTip = String.inspectionDriveStateTip.replace("#state#", String.driverStateSb);
    }
    var config = {
        icon: DialogConfig.Icon_Msg,
        text: inspectionDriveStateTip,
        negativeBtnText: String.inspectionDriveStateCheckNo,
        neutralBtnText: String.inspectionDriveStateCheckYes,
        cancelable: DialogConfig.NoCancelable
    };

    if (code == DRIVERSTATUS.ONDUTY) {

        SDK.openPage(PageConfig.InspectionNew.url,
            PageConfig.InspectionNew.title,
            {type: this.currentInspection});
    } else {//不是odnd

        SDK.showVerticalDialog($.proxy(function (button) {

            if (button == Constants.BUTTON_NEUTRAL) {

                var inspection = "Pre-Trip Inspection";
                if (this.currentInspection == INSPECTION.PRETRIP) {

                    inspection = "Pre-Trip Inspection";
                } else if (this.currentInspection == INSPECTION.INTERIM) {

                    inspection = "Interim Inspection";
                } else {

                    inspection = "Post-Trip Inspection"
                }
                var data = {

                    state: DRIVERSTATUS.ONDUTY,
                    remark: inspection
                };
                DAILYLOG.changeState(data);
                SDK.openPage(PageConfig.InspectionNew.url,
                    PageConfig.InspectionNew.title,
                    {type: this.currentInspection});
            }
        }, this), config);
    }
};

Web.onHistoryBtnClick = function () {

    SDK.openPage(PageConfig.InspectionHistory.url,
        PageConfig.InspectionHistory.title);
};

Web.showDisableDialog = function () {

    SDK.showDialog(null,
        {
            icon: DialogConfig.Icon_Love,
            text: String.inspectionCopilotNot,
            positiveBtnText: String.ok,
            cancelable: DialogConfig.Cancelable
        });
};




