/**
 * @author yufei0213
 * @date 2018/6/27
 * @description 选择燃油类型界面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    //初始化搜索框
    this.initSearchBar();

    //初始化燃油列表
    this.initFuelList();
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
Web.initFuelList = function () {

    this.noSearchResultView = $("#noSearchResultView");
    this.allFuelListView = $("#allFuelList");

    IFTA.getFuelType($.proxy(function (data) {

        this.fuelTypeList = JSON.parse(data);
        //初始化全部燃油类型列表
        this.updateAllFuelTypeList();
    }, this));
};

/**
 * 更新全部车辆列表视图
 */
Web.updateAllFuelTypeList = function () {

    this.allFuelItemTemplate = $("#all-fuel-item-template").html();
    this.allFuelListView.empty();

    for (var i in this.fuelTypeList) {

        var fuel = this.fuelTypeList[i];

        var itemView = $(this.allFuelItemTemplate);
        itemView.find("[sid=code]").html(fuel.name);

        this.bindClickEvent(itemView, fuel.name);
        this.allFuelListView.append(itemView);
    }
};

/**
 * 当input发生变化时，处理列表内容
 */
Web.handleInputChange = function (inputText) {

    inputText = inputText.trim().toLowerCase();

    if (inputText.length > 0) {

        this.allFuelListView.empty();

        this.allFuelItemTemplate = $("#all-fuel-item-template").html();
        for (var i in this.fuelTypeList) {

            var fuel = this.fuelTypeList[i];
            if (fuel.name.toLowerCase().indexOf(inputText) != -1) {

                var itemView = $(this.allFuelItemTemplate);
                itemView.find("[sid=code]").html(fuel.name);

                this.bindClickEvent(itemView, fuel.name);
                this.allFuelListView.append(itemView);
            }
        }

        if (this.allFuelListView.children().length > 0) {

            this.noSearchResultView.addClass("dn");
        } else {

            this.noSearchResultView.removeClass("dn");
        }
    } else {

        this.noSearchResultView.addClass("dn");
        this.updateAllFuelTypeList();
    }
};

Web.bindClickEvent = function (itemView, fuelType) {

    if (this.clickProxy == null) {

        this.clickProxy = new ClickProxy(function (event) {

            var fuelType = event.data.fuelType;
            var selectedFuel = null;
            for (var i in this.fuelTypeList) {

                var fuel = this.fuelTypeList[i];
                if (fuel.name == fuelType) {

                    selectedFuel = fuel;
                    break;
                }
            }

            if (this.backResult == Constants.WEB_BACK_FOR_RESULT) {

                SDK.backForResult(selectedFuel.name);
            } else {

                SDK.setWebData(Constants.INSPECTION_SELECT_VEHICLE_KEY, selectedFuel.name);
                SDK.back();
            }
        }, this);
    }

    itemView.bind("click", {fuelType: fuelType}, $.proxy(this.clickProxy.click, this.clickProxy));
};
