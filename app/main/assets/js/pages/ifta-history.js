/**
 * @author sunyq
 * @date 2018/1/24
 * @description 列表
 */
//主页的page
var fuelHistory;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    fuelHistory = new FuelHistory(params);
};

/**
 * 界面重新加载
 * @param params
 */
Web.onReload = function (params) {

    if (params && params.key == Constants.FUEL_HISTORY_DELETE) {

        fuelHistory.deleteItem(params.data);
        SDK.clearWebData();
    }
    if (params && params.key == Constants.FUEL_HISTORY_UPDATE) {

        fuelHistory.updateItem(params.data);
        SDK.clearWebData();
    }
};

var FuelHistory = function (params) {

    this.fuel = $('[sid=fuel]');

    this.itemList;
    // 初始化数据
    this.init();
};

FuelHistory.prototype.init = function () {

    IFTA.getFuelHistoryList($.proxy(this.onGetFuelHistoryListener, this));
};

FuelHistory.prototype.deleteItem = function (data) {

    for (var i in this.itemList) {

        var item = this.itemList[i];
        var itemData = item.receiveData;
        if (itemData.id == data.id) {

            item.removeSelf();
            this.itemList.splice(i, 1);
            break;
        }
    }
    if (this.itemList.length == 0) {

        this.fuel.addClass('df').addClass('bov');
        this.fuel.find('[sid=fuelBox]').addClass('dn');
        this.fuel.find('[sid=fuelBox]').empty();

        this.fuel.find('[sid=emptyBox]').removeClass('dn');
    }
};

FuelHistory.prototype.updateItem = function (data) {

    for (var i in this.itemList) {

        var item = this.itemList[i];
        var itemData = item.receiveData;
        if (itemData.id == data.id) {

            item.update(data);
            break;
        }
    }
};

FuelHistory.prototype.onGetFuelHistoryListener = function (data) {

    this.fuel.find('[sid=loadingView]').addClass('dn');

    var fuelList = JSON.parse(data);
    if (fuelList.length == 0) {

        this.fuel.find('[sid=emptyBox]').removeClass('dn');
    } else {

        this.fuel.removeClass('df').removeClass('bov');
        this.fuel.find('[sid=fuelBox]').removeClass('dn');
        this.fuel.find('[sid=fuelBox]').empty();

        this.itemList = [];
        for (var i = 0; i < fuelList.length; i++) {

            this.itemList.push(new FuelItem(this.fuel.find('[sid=fuelBox]'), fuelList[i]));
        }
    }
};

/*********************FuelItem********************************/

var FuelItem = function (parentView, receiveData) {

    // 初始化视图
    this.parentView = parentView;
    this.view = null;

    this.nameLabel = null;
    this.timeLabel = null;
    this.typeLabel = null;
    this.statusImg = null;
    this.resultLabel = null;

    this.receiveData = receiveData;

    this.init();
};

FuelItem.template = null;

FuelItem.prototype.init = function () {

    if (FuelItem.template == null)
        FuelItem.template = $('#fuel_template').html();

    this.view = $(FuelItem.template);

    this.stateLabel = this.view.find('[sid=state]');
    this.fuelTypeLabel = this.view.find('[sid=fuelType]');
    this.priceLabel = this.view.find('[sid=price]');
    this.vehicleIDLable = this.view.find('[sid=vehicleID]');
    this.dateLabel = this.view.find('[sid=date]');

    this.view.on('click', $.proxy(this.onViewClickListener, this));

    this.stateLabel.text(this.receiveData.state);
    this.fuelTypeLabel.text(this.receiveData.fuelType);
    this.priceLabel.text(this.receiveData.price);
    this.vehicleIDLable.text(this.receiveData.busCode);
    this.dateLabel.text(this.receiveData.fuelTime);

    this.parentView.append(this.view);
};

FuelItem.prototype.removeSelf = function () {

    this.view.remove();
};

FuelItem.prototype.update = function (data) {

    this.receiveData = data;

    this.stateLabel.text(this.receiveData.state);
    this.fuelTypeLabel.text(this.receiveData.fuelType);
    this.priceLabel.text(this.receiveData.price);
    this.vehicleIDLable.text(this.receiveData.busCode);
    this.dateLabel.text(this.receiveData.fuelTime);
};

FuelItem.prototype.onViewClickListener = function () {

    IFTA.openUpdatePage(this.receiveData);
};