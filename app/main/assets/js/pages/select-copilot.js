/**
 * @author yufei0213
 * @date 2018/1/25
 * @description 选择副驾驶页面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    //初始化搜索框
    this.initSearchBar();

    //初始化车辆列表
    this.initDriverList();
};

/**
 * 初始化搜索框
 */
Web.initSearchBar = function () {

    this.loadingView = $("#loadingView");
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
 * 初始化副驾驶列表
 */
Web.initDriverList = function () {

    this.containerView = $("#container");
    this.noResultView = $("#noResultView");
    this.driverListView = $("#driverListView");

    TEAMWORK.getCoDriverList($.proxy(this.onGetCopilotList, this));
};

/**
 * 获取到可用副驾的列表
 * @param code
 * @param driverList
 */
Web.onGetCopilotList = function (code, driverList) {

    this.loadingView.addClass("dn");

    if (code == Constants.CALLBACK_SUCCESS) {

        if (driverList) {

            this.driverList = JSON.parse(driverList);
            //初始化全部车辆列表
            this.updateDriverListView();
        } else {

            this.noResultView.html(String.addCopilotNoCopilotOnlineTip);
            this.noResultView.removeClass("dn");

            this.containerView.bind("click", $.proxy(function () {

                this.containerView.unbind("click");

                this.noResultView.html("");
                this.noResultView.addClass("dn");
                this.loadingView.removeClass("dn");

                TEAMWORK.getCoDriverList($.proxy(this.onGetCopilotList, this));
            }, this));
        }
    } else if (code == Constants.CALLBACK_FAILURE) {

        this.noResultView.html(String.addCopilotRequestListFiledTip);
        this.noResultView.removeClass("dn");

        this.containerView.bind("click", $.proxy(function () {

            this.containerView.unbind("click");

            this.noResultView.html("");
            this.noResultView.addClass("dn");
            this.loadingView.removeClass("dn");

            TEAMWORK.getCoDriverList($.proxy(this.onGetCopilotList, this));
        }, this));
    }
};

/**
 * 更新副驾驶列表视图
 */
Web.updateDriverListView = function () {

    this.driverItemTemplate = $("#driver-item-template").html();
    this.driverListView.empty();

    for (var i in this.driverList) {

        var driver = this.driverList[i];

        var itemView = $(this.driverItemTemplate);
        itemView.attr("data-origin", driver.id);
        itemView.find("[sid=driverName]").html(driver.name);

        var that = this;
        itemView.bind("click", function () {

            SDK.showLoading();
            var driverId = $(this).attr("data-origin");

            var driver = null;
            for (var i in that.driverList) {

                if (that.driverList[i].id == driverId) {

                    driver = that.driverList[i];
                }
            }
            TEAMWORK.inviteCopilot(driver, $.proxy(that.addCopilotCallback, that));
        });

        this.driverListView.append(itemView);
    }
};

/**
 * 当input发生变化时，处理列表内容
 */
Web.handleInputChange = function (inputText) {

    inputText = inputText.trim().toLowerCase();

    if (inputText.length > 0) {

        this.driverListView.empty();

        this.driverItemTemplate = $("#driver-item-template").html();
        for (var i in this.driverList) {

            var driver = this.driverList[i];
            if (driver.name.toLowerCase().indexOf(inputText) != -1) {

                var itemView = $(this.driverItemTemplate);
                itemView.attr("data-origin", driver.id);
                itemView.find("[sid=driverName]").html(driver.name);

                var that = this;
                itemView.bind("click", function () {

                    SDK.showLoading();
                    var driverId = $(this).attr("data-origin");

                    var driver = null;
                    for (var i in that.driverList) {

                        if (that.driverList[i].id == driverId) {

                            driver = that.driverList[i];
                        }
                    }
                    TEAMWORK.inviteCopilot(driver, $.proxy(that.addCopilotCallback, that));
                });

                this.driverListView.append(itemView);
            }
        }
        if (this.driverListView.children().length > 0) {

            this.noResultView.html("");
            this.noResultView.addClass("dn");
        } else {

            this.noResultView.html(String.addCopilotNoMatchTip);
            this.noResultView.removeClass("dn");
        }
    } else {

        this.noResultView.html("");
        this.noResultView.addClass("dn");

        this.updateDriverListView();
    }
};

/**
 * 邀请副驾驶回调函数
 * @param code 邀请结果
 */
Web.addCopilotCallback = function (code) {

    SDK.hideLoading();
    if (code == Constants.CALLBACK_SUCCESS) {

        SDK.showSuccessPrompt($.proxy(function () {

            SDK.openMainPage();
        }, this));
    } else if (code == Constants.CALLBACK_FAILURE) {

        SDK.showFailedPrompt();
    }
};