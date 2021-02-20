/**
 * @author sunyq0213
 * @date 2018/1/25
 * @description 新增检查项选择车辆页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    //初始化搜索框
    this.initSearchBar();

    //初始化车辆列表
    this.initVehicleList();
};

/**
 * 初始化搜索框
 */
Web.initSearchBar = function () {

    this.searchBarView = $("#searchBarView");
    this.searchBarClearBtn = $("#searchBarClearBtn");

    this.searchBarView.bind("input propertychange", $.proxy(function () {

        var inputText = this.searchBarView.val();

        if (inputText.length > 0) {

            if (this.searchBarClearBtn.hasClass("dn")) {

                this.searchBarClearBtn.bind("click", $.proxy(function () {

                    this.searchBarView.val("");
                    this.searchBarView.focus();
                    this.searchBarClearBtn.unbind("click");
                    this.searchBarClearBtn.addClass("dn");

                    this.handleInputChange(this.searchBarView.val());
                }, this));
                this.searchBarClearBtn.removeClass("dn");
            }
        } else {

            if (!this.searchBarClearBtn.hasClass("dn")) {

                this.searchBarClearBtn.unbind("click");
                this.searchBarClearBtn.addClass("dn");
            }
        }

        this.handleInputChange(inputText);
    }, this));
};

/**
 * 初始化车辆列表
 */
Web.initVehicleList = function () {

    this.noSearchResultView = $("#noSearchResultView");
    this.allVehicleListView = $("#allVehicleList");

    VEHICLE.getVehicleList($.proxy(function (recentVehicleList, allVehicleList) {

        this.allVehicleList = JSON.parse(allVehicleList);
        //初始化全部车辆列表
        this.updateAllVehicleList();
    }, this));
};

/**
 * 更新全部车辆列表视图
 */
Web.updateAllVehicleList = function () {

    this.allVehicleItemTemplate = $("#all-vehicle-item-template").html();
    this.allVehicleListView.empty();

    for (var i in this.allVehicleList) {

        var vehicle = this.allVehicleList[i];

        var itemView = $(this.allVehicleItemTemplate);
        itemView.find("[sid=code]").html(vehicle.code);

        this.bindClickEvent(itemView, vehicle.id);
        this.allVehicleListView.append(itemView);
    }
};

/**
 * 当input发生变化时，处理列表内容
 */
Web.handleInputChange = function (inputText) {

    inputText = inputText.trim().toLowerCase();

    if (inputText.length > 0) {

        this.allVehicleListView.empty();

        this.allVehicleItemTemplate = $("#all-vehicle-item-template").html();
        for (var i in this.allVehicleList) {

            var vehicle = this.allVehicleList[i];
            if (vehicle.code.toLowerCase().indexOf(inputText) != -1) {

                var itemView = $(this.allVehicleItemTemplate);
                itemView.find("[sid=code]").html(vehicle.code);

                this.bindClickEvent(itemView, vehicle.id);
                this.allVehicleListView.append(itemView);
            }
        }

        if (this.allVehicleListView.children().length > 0) {

            this.noSearchResultView.addClass("dn");
        } else {

            this.noSearchResultView.removeClass("dn");
        }
    } else {

        this.noSearchResultView.addClass("dn");
        this.updateAllVehicleList();
    }
};

Web.bindClickEvent = function (itemView, vehicleId) {

    if (this.clickProxy == null) {

        this.clickProxy = new ClickProxy(function (event) {

            var vehicleId = event.data.vehicleId;
            var selectedVehicle = null;
            for (var i in this.allVehicleList) {

                var vehicle = this.allVehicleList[i];
                if (vehicle.id == vehicleId) {

                    selectedVehicle = vehicle;
                    break;
                }
            }

            if (this.backResult == Constants.WEB_BACK_FOR_RESULT) {

                SDK.backForResult(selectedVehicle);
            } else {

                SDK.setWebData(Constants.INSPECTION_SELECT_VEHICLE_KEY, selectedVehicle);
                SDK.back();
            }
        }, this);
    }

    itemView.bind("click", {vehicleId: vehicleId}, $.proxy(this.clickProxy.click, this.clickProxy));
};
