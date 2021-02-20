/**
 * @author yufei0213
 * @date 2018/6/27
 * @description 选择州界面
 */

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function (params) {

    //初始化搜索框
    this.initSearchBar();

    //初始化州列表
    this.initStateList();
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
Web.initStateList = function () {

    this.noSearchResultView = $("#noSearchResultView");
    this.allStateListView = $("#allStateList");

    IFTA.getStateList($.proxy(function (data) {

        this.stateList = JSON.parse(data);
        //初始化州列表
        this.updateAllStateList();
    }, this));
};

/**
 * 更新全部车辆列表视图
 */
Web.updateAllStateList = function () {

    this.allStateItemTemplate = $("#all-state-item-template").html();
    this.allStateListView.empty();

    for (var i in this.stateList) {

        var state = this.stateList[i];

        var itemView = $(this.allStateItemTemplate);
        itemView.find("[sid=code]").html(state.shortName);

        this.bindClickEvent(itemView, state.shortName);
        this.allStateListView.append(itemView);
    }
};

/**
 * 当input发生变化时，处理列表内容
 */
Web.handleInputChange = function (inputText) {

    inputText = inputText.trim().toLowerCase();

    if (inputText.length > 0) {

        this.allStateListView.empty();

        this.allStateItemTemplate = $("#all-state-item-template").html();
        for (var i in this.stateList) {

            var state = this.stateList[i];
            if (state.shortName.toLowerCase().indexOf(inputText) != -1) {

                var itemView = $(this.allStateItemTemplate);
                itemView.find("[sid=code]").html(state.shortName);

                this.bindClickEvent(itemView, state.shortName);
                this.allStateListView.append(itemView);
            }
        }

        if (this.allStateListView.children().length > 0) {

            this.noSearchResultView.addClass("dn");
        } else {

            this.noSearchResultView.removeClass("dn");
        }
    } else {

        this.noSearchResultView.addClass("dn");
        this.updateAllStateList();
    }
};

Web.bindClickEvent = function (itemView, shortName) {

    if (this.clickProxy == null) {

        this.clickProxy = new ClickProxy(function (event) {

            var stateShortName = event.data.stateShortName;
            var selectedState = null;
            for (var i in this.stateList) {

                var state = this.stateList[i];
                if (state.shortName == stateShortName) {

                    selectedState = state;
                    break;
                }
            }

            if (this.backResult == Constants.WEB_BACK_FOR_RESULT) {

                SDK.backForResult(selectedState.shortName);
            } else {

                SDK.setWebData(Constants.INSPECTION_SELECT_VEHICLE_KEY, selectedState.shortName);
                SDK.back();
            }
        }, this);
    }

    itemView.bind("click", {stateShortName: shortName}, $.proxy(this.clickProxy.click, this.clickProxy));
};
