/**
 * @author sunyq
 * @date 2018/1/24
 * @description 新增检查项页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 * web模式调试时，url尾部添加  ?params={"page":"pre-trip"}
 */
Web.init = function (params) {

    this.newInspectionPage = new NewInspectionPage(params);
    this.backIndex = params.index;

    SDK.collectFirebaseScreen("dvir-new");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    if (params != null && params.key == Constants.ADD_DEFECTS_KEY) {

        this.newInspectionPage.reloadDefects(params);
        SDK.clearWebData();
    }
    if (params != null && params.key == Constants.INSPECTION_SELECT_VEHICLE_KEY) {

        this.newInspectionPage.reloadVehicle(params);
        SDK.clearWebData();
    }

    SDK.collectFirebaseScreen("dvir-new");
};

/**
 * 左上角按钮被点击
 */
Web.onLeftBtnClick = function () {

    SDK.back(this.backIndex);
};

var NewInspectionPage = function (params) {

    // 初始化数据
    this.type = params.type;
    this.currentTime = null;
    this.vehicleInfo = null;
    this.latitude = 0;
    this.longitude = 0;
    this.defectsArr = [];

    this.typeLabel = $('[sid=typeLabel]');
    this.vehicleNoBox = $('[sid=vehicleNoBox]');
    this.vehicleTitleLabel = $('[sid=vehicleTitleLabel]');
    this.vehicleNoLabel = $('[sid=vehicleNoLabel]');
    this.arrowImg = $('[sid=arrowImg]');
    this.dateLabel = $('[sid=dateLabel]');
    this.odometerBox = $('[sid=odometerBox]');
    this.odometerInput = $('[sid=odometerInput]');
    this.locationBox = $('[sid=locationBox]');
    this.locationInput = $('[sid=locationInput]');
    this.locationImg = $('[sid=locationImg]');
    this.defectBox = $('[sid=defectBox]');
    this.defectList = $('[sid=defectList]');
    this.defectAddBtn = $('[sid=defectAddBtn]');
    this.remarkBox = new RemarkSelector($('[sid=remarkBox]'));
    this.saveBtn = $('[sid=saveBtn]');
    this.init();
};

NewInspectionPage.prototype.init = function () {

    this.odometerInput.focus(function () {
        $(this).parent().parent().parent().addClass("focus");
    });
    this.odometerInput.blur(function () {
        $(this).parent().parent().parent().removeClass("focus");
    });
    this.odometerInput.on("input propertychange", $.proxy(function () {
        var value = this.odometerInput.val().trim();
        if (value.indexOf('0') == 0) {
            this.odometerInput.val(value.replace(/^0*/, ""));
        }

        if (this.odometerBox.hasClass('warning')) {
            this.odometerBox.removeClass('warning');
        }
    }, this));
    this.locationInput.focus(function () {
        $(this).parent().parent().parent().addClass("focus");
    });
    this.locationInput.blur(function () {
        $(this).parent().parent().parent().removeClass("focus");
    });
    this.locationInput.on("input propertychange", $.proxy(function () {
        if (this.locationBox.hasClass('warning')) {
            this.locationBox.removeClass('warning');
        }
    }, this));

    this.locationImg.on("click", $.proxy(this.onLocationImgClickListener, this));
    this.defectAddBtn.on('click', $.proxy(this.onDefectBtnClickListener, this));
    this.saveBtn.on('click', $.proxy(this.onSaveBtnClickListener, this));

    $(window).unbind("resize");
    $(window).resize($.proxy(function () {
        if (this.odometerBox.hasClass("focus")) {
            $('[sid=containerBox]').scrollTop($('[sid=odometerBox]').offset().top - $('[sid=odometerBox]').parent().offset().top);
        }
        if (this.locationBox.hasClass("focus")) {
            $('[sid=containerBox]').scrollTop($('[sid=locationBox]').offset().top - $('[sid=locationBox]').parent().offset().top);
        }
        if ($('[sid=remarkBox]').hasClass("focus")) {
            $('[sid=containerBox]').scrollTop($('[sid=remarkBox]').offset().top - $('[sid=remarkBox]').parent().offset().top);
        }
    }, this));
    this.renderPage();
};

NewInspectionPage.prototype.renderPage = function () {

    switch (this.type) {

        case INSPECTION.PRETRIP:

            this.typeLabel.text(String.preTrip);
            break;
        case INSPECTION.INTERIM:

            this.typeLabel.text(String.interim);
            break;
        case INSPECTION.POSTTRIP:

            this.typeLabel.text(String.postTrip);
            break;
        default:
            break;
    }

    // 获取当前车辆信息
    VEHICLE.getCurrentVehicle($.proxy(this.getVehicleInfoListener, this));

    // 获取时间
    SDK.getDate($.proxy(this.onGetDateListener, this));

    // 加载location
    this.locationImg.removeClass('ilocation').addClass('iloading');
    SDK.getGeoLocation($.proxy(this.onLocationListener, this));

    // remark 部分
    SDK.getRemarkList($.proxy(this.onGetRemarkListListener, this));
};

NewInspectionPage.prototype.onVehicleNoBoxClickListener = function () {

    SDK.openPage(PageConfig.SelectVehicle.url,
        PageConfig.SelectVehicle.title);
};

NewInspectionPage.prototype.onGetDateListener = function (dateStr, date) {

    this.dateLabel.text(dateStr);
    this.currentTime = date;
};

NewInspectionPage.prototype.onGetRemarkListListener = function (data) {

    // 设置remark数据
    this.remarkBox.setData(JSON.parse(data));
};

NewInspectionPage.prototype.getVehicleInfoListener = function (vehicleInfo) {

    if (vehicleInfo) {
        this.vehicleInfo = JSON.parse(vehicleInfo);
        this.vehicleNoLabel.removeClass('pld');
        this.vehicleNoLabel.text(this.vehicleInfo.code);
        // 当里程数为0时也不显示
        if (this.vehicleInfo.odometer != 0) {
            this.odometerInput.val(parseFloat(this.vehicleInfo.odometer).toFixed(2));
        }
        // 隐藏箭头
        this.vehicleTitleLabel.removeClass('necessary');
        this.arrowImg.addClass('dn');
    } else {
        // 查询不到当前车辆时，可以手动选择
        this.vehicleNoLabel.addClass('pld');
        this.vehicleNoLabel.text(String.inspectionSelectVehicle);
        this.vehicleNoBox.on('click', $.proxy(this.onVehicleNoBoxClickListener, this));
        this.vehicleTitleLabel.addClass('necessary');
        this.arrowImg.removeClass('dn');
    }
};

NewInspectionPage.prototype.onLocationListener = function (code, latitude, longitude, location) {

    this.locationImg.removeClass('iloading').addClass('ilocation');

    if (code == Constants.CALLBACK_SUCCESS) {

        if (this.locationBox.hasClass('warning')) {

            this.locationBox.removeClass('warning');
        }

        this.latitude = latitude;
        this.longitude = longitude;

        this.locationInput.val(location);
    } else if (code == Constants.CALLBACK_FAILURE) {

        this.latitude = LatLngSpecial.M;
        this.longitude = LatLngSpecial.M;

        this.locationInput.removeAttr("disabled");
    }
};
/**
 * 获取位置监听
 */
NewInspectionPage.prototype.onLocationImgClickListener = function () {

    // 加载location
    this.locationImg.removeClass('ilocation').addClass('iloading');
    SDK.getGeoLocation($.proxy(this.onLocationListener, this));
};
/**
 * 添加故障监听
 */
NewInspectionPage.prototype.onDefectBtnClickListener = function () {

    var params = this.defectsArr;
    SDK.openPage(PageConfig.InspectionAddDefects.url,
        PageConfig.InspectionAddDefects.title,
        params);
};
/**
 * 加载defects
 */
NewInspectionPage.prototype.reloadDefects = function (params) {

    this.defectList.empty();
    if (params) {
        this.defectsArr = params.data;
    }
    if (this.defectsArr.length > 0) {
        this.defectBox.addClass('ondft');
    } else {
        this.defectBox.removeClass('ondft');
    }

    for (var i = 0; i < this.defectsArr.length; i++) {

        var defectItem = new DefectItem(this.defectList, this.defectsArr[i]);
        defectItem.on(DefectItem.EVENT_DEFECT_ITEM_CLICK, new EventHandler(this.defectListener, this));
    }
};
/**
 * 加载车辆信息
 */
NewInspectionPage.prototype.reloadVehicle = function (params) {

    this.vehicleInfo = params.data;
    this.vehicleNoLabel.removeClass('pld');
    this.vehicleNoLabel.text(this.vehicleInfo.code);
    // 当里程数为0时也不显示
    if (this.vehicleInfo.odometer != 0) {
        this.odometerInput.val(parseFloat(this.vehicleInfo.odometer).toFixed(0));
        if (this.odometerBox.hasClass('warning')) {
            this.odometerBox.removeClass('warning')
        }
    }

    if (this.vehicleNoBox.hasClass('warning')) {
        this.vehicleNoBox.removeClass('warning');
    }
};

NewInspectionPage.prototype.defectListener = function (data) {

    var temArr = [];
    for (var i = 0; i < this.defectsArr.length; i++) {
        if (this.defectsArr[i].id != data.id) {
            temArr.push(this.defectsArr[i]);
        }
    }
    this.defectsArr = temArr;

    this.reloadDefects();
};

// 验证表单 全验证
NewInspectionPage.prototype.checkFormValid = function () {

    var numberReg = /^[0-9]+\.?[0-9]*$/;

    var isValid = true;
    // 验证Vehicle NO.
    if (this.vehicleNoLabel.text().trim() == String.inspectionSelectVehicle) {

        this.vehicleNoBox.addClass('warning');
        this.vehicleNoBox.find('[sid=tipLabel]').text(String.inspectionVehicleValid);
        isValid = false;
    }
    // 验证odometer
    if (!numberReg.test(this.odometerInput.val().trim()) || parseInt(this.odometerInput.val()) > Constants.odometerMax) {

        if (this.odometerInput.val().trim() == "") {

            this.odometerBox.addClass('warning');
            this.odometerBox.find('[sid=tipLabel]').text(String.inspectionOdometerValid);
        } else if (!numberReg.test(this.odometerInput.val().trim())) {

            this.odometerBox.addClass('warning');
            this.odometerBox.find('[sid=tipLabel]').text(String.inspectionOdometerTypeValid);
        } else {

            this.odometerBox.addClass('warning');
            this.odometerBox.find('[sid=tipLabel]').text(String.inspectionOdometerLengthValid);
        }
        isValid = false;
    }
    if (this.locationInput.val().length < Constants.locationMinLength) {

        this.locationBox.addClass('warning');
        this.locationBox.find('[sid=tipLabel]').text(String.locationMinValid.replace("#count#", Constants.locationMinLength.toString()));
        isValid = false;
    }
    if (this.locationInput.val().length > Constants.locationMaxLength) {

        this.locationBox.addClass('warning');
        this.locationBox.find('[sid=tipLabel]').text(String.locationMaxValid.replace("#count#", Constants.locationMaxLength.toString()));
        isValid = false;
    }
    if (this.locationInput.val().match(Constants.inputRegexp)) {

        this.locationBox.addClass('warning');
        this.locationBox.find('[sid=tipLabel]').text(String.locationValid);
        isValid = false;
    }

    //如果当前已经是有问题的。就不需要在判断了
    if (isValid) {

        isValid = this.remarkBox.isRemarkValid();
    }

    return isValid;
};

NewInspectionPage.prototype.onSaveBtnClickListener = function () {

    if (!this.checkFormValid()) return;

    //如果检查时间不足十五分钟，则提醒用户
        var duration = new Date().getTime() - this.currentTime;
        if (duration < Constants.inspectionMinTime) {
             var minute = Math.ceil((Constants.inspectionMinTime - duration)/60000);
                var content = String.inspectionTimeTip.replace("#minute#", minute);
                var config = {
                    icon: DialogConfig.Icon_Love,
                    text: content,
                    neutralBtnText: String.inspectionContinue,
                    negativeBtnText: String.inspectionStop
                };
                SDK.showVerticalDialog($.proxy(this.onDialogCallbackListener, this), config);
                return;
        }

        this.SaveData();
};

NewInspectionPage.prototype.onDialogCallbackListener = function (which) {

    if (which == Constants.BUTTON_NEUTRAL) {

    }
    if (which == Constants.BUTTON_NEGATIVE) {

        this.SaveData();
    }
};

NewInspectionPage.prototype.SaveData = function () {

    var params = {
                   access_token: "",
                   vehicle_id: this.vehicleInfo.id,
                   type: this.type,
                   odometer: this.odometerInput.val(),
                   time: this.currentTime,
                   location: this.locationInput.val(),
                   latitude: this.latitude,
                   longitude: this.longitude,
                   defects: [],
                   result: 0,
                   remark: this.remarkBox.getRemark(),
                   defect_level: "", // 先空着
                   driver_sign: ""
               };

               for (var i = 0; i < this.defectsArr.length; i++) {
                   var obj = {};
                   obj.id = this.defectsArr[i].id;
                   obj.name = this.defectsArr[i].value;
                   obj.comment = this.defectsArr[i].remark;
                   params.defects.push(obj);
               }

               SDK.openPage(PageConfig.InspectionConfirm.url,
                   PageConfig.InspectionConfirm.title,
                   params);
};

/***************************defectItem**********************************/
var DefectItem = function (parentView, receiptData) {

    // 初始化视图
    this.view = null;
    this.parentView = parentView;

    this.deleteBtn = null;
    this.titleLabel = null;
    this.remarkLabel = null;

    this.receiptData = receiptData;

    this.init();
};

DefectItem.extends(BaseWidget);

DefectItem.EVENT_DEFECT_ITEM_CLICK = "event_defect_item_click";

DefectItem.template = null;

DefectItem.prototype.init = function () {

    if (DefectItem.template == null)
        DefectItem.template = $('#defect_template').html();
    this.view = $(DefectItem.template);
    this.parentView.append(this.view);

    // 绑定组件
    this.deleteBtn = this.view.find('[sid=deleteBtn]');
    this.titleLabel = this.view.find('[sid=titleLabel]');
    this.remarkLabel = this.view.find('[sid=remarkLabel]');
    // 绑定事件
    this.deleteBtn.on('click', $.proxy(this.onDeleteBtnClickListener, this));

    this.titleLabel.text(this.receiptData.value);
    this.remarkLabel.html(this.receiptData.remark);
};

DefectItem.prototype.onDeleteBtnClickListener = function () {

    this.fire(DefectItem.EVENT_DEFECT_ITEM_CLICK, this.receiptData);
};


