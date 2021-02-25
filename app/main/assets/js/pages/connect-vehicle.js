/**
 * @author yufei0213
 * @date 2018/1/25
 * @description 选择车辆页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    if (params) {

        this.backIndex = params.index;
    }

    //初始化搜索框
    this.initSearchBar();

    //初始化车辆列表
    this.initVehicleList();
};

/**
 * 左上角按钮被点击
 */
Web.onLeftBtnClick = function () {

    SDK.back(this.backIndex);
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
    this.recentVehicleListTitle = $("#recentVehicleListTitle");
    this.recentVehicleListView = $("#recentVehicleList");
    this.allVechielListTitle = $("#allVechielListTitle");
    this.allVehicleListView = $("#allVehicleList");

    VEHICLE.getVehicleList($.proxy(function (recentVehicleList, allVehicleList) {

        if (recentVehicleList && JSON.parse(recentVehicleList).length > 0) {

            this.recentVehicleList = JSON.parse(recentVehicleList);
            //初始化最近使用的车辆列表
            this.updateRecentVehicleList();
        }

        this.allVehicleList = JSON.parse(allVehicleList);
        //初始化全部车辆列表
        this.updateAllVehicleList();
    }, this));
};

/**
 * 更新最近使用的车辆列表
 */
Web.updateRecentVehicleList = function () {

    if (this.recentVehicleList) {

        this.recentVehicleItemTemplate = $("#recent-vehicle-item-template").html();

        this.recentVehicleListTitle.removeClass("dn");
        this.allVechielListTitle.removeClass("dn");

        this.recentVehicleListView.empty();

        for (var i in this.recentVehicleList) {

            var recentVehicle = this.recentVehicleList[i];
            var itemView = $(this.recentVehicleItemTemplate);
            itemView.find("[sid=code]").html(recentVehicle.code);
            itemView.find("[sid=connectTime]").html(recentVehicle.connectedTime);

            this.bindClickEvent(itemView, recentVehicle.id);
            this.recentVehicleListView.append(itemView);
        }
    } else {

        this.recentVehicleListTitle.addClass("dn");
        this.allVechielListTitle.addClass("dn");
    }
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

        this.recentVehicleListTitle.addClass("dn");
        this.allVechielListTitle.addClass("dn");

        this.recentVehicleListView.empty();
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

        this.updateRecentVehicleList();
        this.updateAllVehicleList();
    }
};

/**
 * 绑定车辆选择事件
 * @param itemView 被点击的视图（被选择的车辆）
 */
Web.bindClickEvent = function (itemView, vehicleId) {

    if (this.clickProxy == null) {

        this.clickProxy = new ClickProxy(function (event) {

            var vehicleId = event.data.vehicleId;
            this.connectVehicle(vehicleId);
        }, this);
    }

    itemView.bind("click", {vehicleId: vehicleId}, $.proxy(this.clickProxy.click, this.clickProxy));
};

/**
 * 连接车辆
 */
Web.connectVehicle = function (vehicleId) {

    var connectingVehicle = null;
    for (var i in this.allVehicleList) {

        var vehicle = this.allVehicleList[i];
        if (vehicle.id == vehicleId) {

            connectingVehicle = vehicle;
            break;
        }
    }
    VEHICLE.connectVehicle(connectingVehicle, $.proxy(function (code) {

        if (code == Constants.CALLBACK_SUCCESS) {

            //弹出添加 shipping 界面
            DASHBOARD.showShippingView($.proxy(function (code, shipping) {

                //获取到shipping，保存shipping
                if (code == Constants.CALLBACK_SUCCESS) {

                    SDK.showLoading();
                    DASHBOARD.saveShipping(shipping, $.proxy(function (code) {

                        SDK.hideLoading();
                        SDK.showSuccessPrompt($.proxy(function () {

                            DASHBOARD.getDriverState($.proxy(this.getStateResult, this));
                        }, this))
                    }, this))
                } else {

                    DASHBOARD.getDriverState($.proxy(this.getStateResult, this));
                }
            }, this));
        } else if (code == Constants.CALLBACK_FAILURE) {

            //连接失败的逻辑已经在原生部分处理
        }
    }, this));
};

Web.getStateResult = function (code) {

    if (code == DRIVERSTATUS.ONDUTY) {

        SDK.showVerticalDialog($.proxy(function (button) {

                if (button == Constants.BUTTON_NEUTRAL) {

                    SDK.openPage(PageConfig.InspectionNew.url,
                        PageConfig.InspectionNew.title,
                        {
                            type: INSPECTION.PRETRIP,
                            index: this.backIndex ? this.backIndex - 2 : -2
                        });
                } else if (button == Constants.BUTTON_NEGATIVE) {

                    //选择不检查，直接退出
                    SDK.setWebData(Constants.HANDLE_UNIDENTIFIED_KEY);
                    SDK.back(this.backIndex);
                }
            }, this),
            {
                icon: DialogConfig.Icon_Love,
                text: String.changeStatusPreInspectionTip,
                neutralBtnText: String.changeStatusPreInspectionNeutralBtnText,
                negativeBtnText: String.changeStatusInspectionNegativeBtnText
            });
    } else {//不是odnd，直接back

        SDK.setWebData(Constants.HANDLE_UNIDENTIFIED_KEY);
        SDK.back(this.backIndex);
    }
};