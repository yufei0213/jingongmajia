/**
 * @author sunyq
 * @date 2018/1/24
 * @description 检查项确认页面
 */
//主页的page
var confirmPage;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 * //?params= {"access_token":"","defect_level":"","defects":[{"comment":"ty","id":"1","name":"Fluid Leaks Under Bus"},{"comment":"","id":"2","name":"Loose Wired, Hose Connections"}],"driver_sign":"","latitude":"M","location":"hhh","longitude":"M","odometer":"1000","remark":"","result":0,"time":"01/26 05:05 AM","type":1,"vehicle_id":1}
 */
Web.init = function (params) {

    this.initSignCanvas();

    confirmPage = new ConfirmPage(params);
};

var ConfirmPage = function (params) {

    this.inspectionData = params;

    this.satisfactoryBox = $('[sid=satisfactoryBox]');
    this.safeBox = $('[sid=safeBox]');
    this.notSafeBox = $('[sid=notSafeBox]');
    this.init();
};

ConfirmPage.prototype.init = function () {

    // level 有三个级别
    if (this.inspectionData.defects.length == 0) {

        this.inspectionData.result = 0;
        this.satisfactoryBox.removeClass('dn');
        this.safeBox.addClass('dn');
        this.notSafeBox.addClass('dn');
    } else {
        this.inspectionData.result = 1;
        this.satisfactoryBox.addClass('dn');
        this.safeBox.removeClass('dn');
        this.notSafeBox.removeClass('dn');
        this.safeBox.find('[sid=checkBtn]').addClass('on');
        this.notSafeBox.find('[sid=checkBtn]').removeClass('on');
    }

    this.safeBox.on('click', $.proxy(this.onSafeBoxListener, this));
    this.notSafeBox.on('click', $.proxy(this.onNotSafeBoxListener, this));

    this.canvas = $("#signCanvas");
    this.signaturePad = new SignaturePad(this.canvas.get(0), {
        backgroundColor: 'rgb(255, 255, 255)',
        minWidth: 1,
        maxWidth: 1
    });
    this.clearBtn = $("#clearBtn");
    var env = this;
    this.clearBtn.on("click", function () {

        env.signaturePad.clear();
    });

    this.saveBtn = $("#saveBtn");
    this.saveBtn.on("click", $.proxy(this.saveBtnClickListener, this));
};

ConfirmPage.prototype.saveBtnClickListener = function () {

    if (this.signaturePad.isEmpty()) {
        var config = {
            icon: DialogConfig.Icon_Love,
            text: String.notSignatureTip,
            positiveBtnText: String.ok
        };
        SDK.showDialog(null, config);
        return;
    }
    ImageUtil.zipBase64(this.signaturePad.toDataURL(), $.proxy(function (driver_sign) {

        this.inspectionData.driver_sign = driver_sign;
        // 上传数据
        SDK.showLoading();
        DVIR.uploadInspection(this.inspectionData, $.proxy(this.onUploadListener, this));
    }, this));
};
ConfirmPage.prototype.onUploadListener = function (result) {

    SDK.hideLoading();
    if (result == Constants.CALLBACK_SUCCESS) {

        // 检查项目为post-trip时
        if (this.inspectionData.type == INSPECTION.POSTTRIP) {

            // 检查司机是否在OffDuty状态
            DRIVING.getDriverStatus(function (data) {

                var driver = JSON.parse(data);
                if (driver.currentState != DRIVERSTATUS.OFFDUTY) {

                    DAILYLOG.changeState({state: DRIVERSTATUS.OFFDUTY}); //切换状态为OFF
                    VEHICLE.disconnectVehicleAuto(); //断开车辆

                    DAILYLOG.getTomorrowDrivingRemainTip($.proxy(function (tips) {

                        var config = {
                            icon: DialogConfig.Icon_Love,
                            text: tips,
                            positiveBtnText: String.ok
                        };
                        SDK.showSpannableDialog($.proxy(function (which) {

                            SDK.openMainPage();
                        }, this), config);
                    }, this));
                } else {

                    SDK.openMainPage();
                }
            });
        } else if (this.inspectionData.type == INSPECTION.PRETRIP) {

            SDK.setWebData(Constants.HANDLE_UNIDENTIFIED_KEY);
            SDK.openMainPage();
        } else {

            SDK.openMainPage();
        }
    } else {

        SDK.showFailedPrompt();
    }
};

ConfirmPage.prototype.onSafeBoxListener = function () {

    if (!this.safeBox.find('[sid=checkBtn]').hasClass('on')) {
        this.inspectionData.result = 1;
        this.safeBox.find('[sid=checkBtn]').addClass('on');
        this.notSafeBox.find('[sid=checkBtn]').removeClass('on');
    }
};

ConfirmPage.prototype.onNotSafeBoxListener = function () {

    if (!this.notSafeBox.find('[sid=checkBtn]').hasClass('on')) {
        this.inspectionData.result = 2;
        this.notSafeBox.find('[sid=checkBtn]').addClass('on');
        this.safeBox.find('[sid=checkBtn]').removeClass('on');
    }
};



