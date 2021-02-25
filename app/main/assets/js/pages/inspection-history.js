/**
 * @author sunyq
 * @date 2018/1/24
 * @description 车辆检查历史列表
 */
//主页的page
var dvirHistory;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    dvirHistory = new DvirHistory(params);

    // FireBase Analytics
    SDK.collectFirebaseScreen("dvir-new-sign");
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    // FireBase Analytics
    SDK.collectFirebaseScreen("dvir-new-sign");
};

var DvirHistory = function (params) {

    this.vehicleBtn = $('[sid=vehicleBtn]');
    this.driverBtn = $('[sid=driverBtn]');

    this.vehicleBox = $('[sid=vehicleBox]');
    this.driverBox = $('[sid=driverBox]');
    // 初始化数据

    this.init();
};

DvirHistory.prototype.init = function () {

    // 绑定事件
    this.vehicleBtn.on('click', $.proxy(this.onVehicleBtnClick, this));
    this.driverBtn.on('click', $.proxy(this.onDriverBtnClick, this));

    // 检查连接车辆
    this.vehicleBox.find('[sid=loadingView]').removeClass("dn");
    VEHICLE.getCurrentVehicle($.proxy(this.onGetCurrentVehicleListener, this));

    //获取driver的检查信息
    var params = {
        type: 0, // 0是司机，1是车辆
        vehicle_id: 0 // 司机为0时，这个不重要
    };
    this.driverBox.find('[sid=loadingView]').removeClass("dn");
    DVIR.getInspectionList(params, $.proxy(this.onGetDriverInspectionListener, this));
};

DvirHistory.prototype.onVehicleBtnClick = function () {

    if (!this.vehicleBtn.hasClass('on')) {
        this.driverBtn.removeClass('on');
        this.vehicleBtn.addClass('on');
        this.driverBox.addClass('dn');
        this.vehicleBox.removeClass('dn');
    }
};

DvirHistory.prototype.onDriverBtnClick = function () {

    if (!this.driverBtn.hasClass('on')) {
        this.vehicleBtn.removeClass('on');
        this.driverBtn.addClass('on');
        this.vehicleBox.addClass('dn');
        this.driverBox.removeClass('dn');
    }
};

DvirHistory.prototype.onGetCurrentVehicleListener = function (data) {

    if (!data) {
        // 没有检测到车辆
        this.vehicleBox.find('[sid=loadingView]').addClass('dn');
        this.vehicleBox.find('[sid=emptyBox]').removeClass('dn');
    } else {
        // 检测到车辆
        var vehicleInfo = JSON.parse(data);
        var params = {
            type: 1, // 0是司机，1是车辆
            vehicle_id: vehicleInfo.id
        };
        DVIR.getInspectionList(params, $.proxy(this.onGetVehicleInspectionListener, this));
    }
};

DvirHistory.prototype.onGetVehicleInspectionListener = function (data) {

    this.vehicleBox.find('[sid=loadingView]').addClass('dn');
    var inspectionList = JSON.parse(data);
    if (inspectionList.length == 0) {
        this.vehicleBox.find('[sid=emptyBox]').removeClass('dn');
    } else {
        this.vehicleBox.removeClass('df').removeClass('bov');
        this.vehicleBox.find('[sid=inspectionBox]').removeClass('dn');
        this.vehicleBox.find('[sid=inspectionBox]').empty();
        for (var i = 0; i < inspectionList.length; i++) {
            new InspectionItem(this.vehicleBox.find('[sid=inspectionBox]'), inspectionList[i], "vehicle");
        }
    }
};

DvirHistory.prototype.onGetDriverInspectionListener = function (data) {

    this.driverBox.find('[sid=loadingView]').addClass('dn');
    var inspectionList = JSON.parse(data);
    if (inspectionList.length == 0) {

        this.driverBox.find('[sid=emptyBox]').removeClass('dn');
    } else {
        this.driverBox.removeClass('df').removeClass('bov');
        this.driverBox.find('[sid=inspectionBox]').removeClass('dn');
        this.driverBox.find('[sid=inspectionBox]').empty();
        for (var i = 0; i < inspectionList.length; i++) {
            new InspectionItem(this.driverBox.find('[sid=inspectionBox]'), inspectionList[i], "driver");
        }
    }
};

/*********************InspectionItem********************************/

var InspectionItem = function (parentView, receiveData, type) {

    // 初始化视图
    this.parentView = parentView;
    this.view = null;

    this.nameLabel = null;
    this.timeLabel = null;
    this.typeLabel = null;
    this.statusImg = null;
    this.resultLabel = null;

    this.receiveData = receiveData;
    this.type = type;

    this.init();
};

InspectionItem.template = null;

InspectionItem.prototype.init = function () {

    if (InspectionItem.template == null)
        InspectionItem.template = $('#inspection_template').html();
    this.view = $(InspectionItem.template);
    this.parentView.append(this.view);

    this.nameLabel = this.view.find('[sid=nameLabel]');
    this.timeLabel = this.view.find('[sid=timeLabel]');
    this.typeLabel = this.view.find('[sid=typeLabel]');
    this.statusImg = this.view.find('[sid=statusImg]');
    this.resultLabel = this.view.find('[sid=resultLabel]');

    this.view.on('click', $.proxy(this.onViewClickListener, this));

    if (this.type == "driver") {
        this.nameLabel.text(this.receiveData.busId);
    } else {
        this.nameLabel.text(this.receiveData.creatorName);
    }
    this.timeLabel.text(this.receiveData.time);

    switch (parseInt(this.receiveData.inspResult)) {
        case 0:
            this.statusImg.addClass('green');
            this.resultLabel.text(String.inspectionGreenTip);
            break;
        case 1:
            this.statusImg.addClass('org');
            this.resultLabel.text(String.inspectionOrgTip);
            break;
        case 2:
            this.statusImg.addClass('red');
            this.resultLabel.text(String.inspectionRedTip);
            break;
        default:
            break;
    }
    switch (this.receiveData.type) {
        case 1:
            this.typeLabel.text(String.preTrip);
            break;
        case 2:
            this.typeLabel.text(String.interim);
            break;
        case 3:
            this.typeLabel.text(String.postTrip);
            break;
        default:
            break;
    }
};

InspectionItem.prototype.onViewClickListener = function () {

    SDK.openPage(PageConfig.InspectionDetail.url,
        PageConfig.InspectionDetail.title,
        this.receiveData);
};