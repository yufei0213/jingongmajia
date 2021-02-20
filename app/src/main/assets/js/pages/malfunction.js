var page = null;

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Web.init = function () {

    page = new Page($('#page'));
};

(function ($, window) {

    var PageSetting = {};

    var Page = function (view, options) {

        $.extend(this, PageSetting, options || {});

        /* 初始化视图 */
        this.view = view;

        /* 初始化组件 */
        this.container = this.view.find("[sid=container]");
        this.malfunctionBox = this.view.find("[sid=malfunctionBox]");
        this.malfunctionList = this.view.find("[sid=malfunctionList]");
        this.diagnosticBox = this.view.find("[sid=diagnosticBox]");
        this.diagnosticList = this.view.find("[sid=diagnosticList]");
        this.emptyView = this.view.find("[sid=emptyView]");
        this.loadingView = this.view.find("[sid=loadingView]");

        /* 初始化数据 */
        this.malList = [];
        this.diaList = [];

        /* 初始化方法 */
        this.init();
    };

    Page.prototype.init = function () {

        var self = this;

        /* 获取数据 */
        VEHICLE.getVehicleMalfunctionList(function (malList, diaList) {

            malList = JSON.parse(malList);
            diaList = JSON.parse(diaList);

            self.malList = malList;
            self.diaList = diaList;

            // 列表展示
            self.showList();
        });
    };

    /**
     * 列表展示
     */
    Page.prototype.showList = function () {

        if (this.malList.length == 0 && this.diaList.length == 0) {

            this.closeLoading();
            this.emptyView.removeClass('dn');
            return;
        }

        // malfunction 列表
        if (this.malList.length > 0) {

            // 添加项
            for (var i = 0; i < this.malList.length; i++) {

                var data = this.malList[i];
                new Item(this.malfunctionList, {data: data});
            }

            this.malfunctionBox.removeClass('dn');
        }

        // diagnostic 列表
        if (this.diaList.length > 0) {

            // 添加项
            for (var i = 0; i < this.diaList.length; i++) {

                var data = this.diaList[i];
                new Item(this.diagnosticList, {data: data});
            }

            this.diagnosticBox.removeClass('dn');
        }

        this.closeLoading();
    };

    /**
     * 关闭loading
     */
    Page.prototype.closeLoading = function () {

        this.container.removeClass("df").removeClass("bov");
        this.loadingView.addClass('dn');
    };

    window.Page = Page;
}($, window));

// ******************** item ********************
(function ($, window) {

    var ItemSetting = {
        data: null
    };

    var Item = function (parentView, options) {

        $.extend(this, ItemSetting, options || {});

        /* 初始化视图 */
        this.parentView = parentView;
        this.view = null;

        /* 初始化方法 */
        this.init();
    };

    Item.template = null;
    Item.prototype.init = function () {

        if (!Item.template) {
            Item.template = $("#malfunction_template").html();
        }
        this.view = $(Item.template);
        this.parentView.append(this.view);

        this.title = this.view.find("[sid=title]");
        this.time = this.view.find("[sid=time]");

        this.title.html(this.data.name);
        this.time.html(this.data.updateTime);
    };

    window.Item = Item;
}($, window));