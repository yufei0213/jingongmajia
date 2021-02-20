/**
 * @author mamw
 * @date 2018/1/27
 * @description unidentified driver log list页面
 */
var page = null;

/**
 * 初始化
 */
Web.init = function () {

    page = new UnidentifiedDriverLogPage($('#page'));
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function () {

    page.reloadData(true);
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {};

    /*
     * 界面
     **/
    var UnidentifiedDriverLogPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.containerBox = null;
        this.itemTotalCountBox = null;
        this.itemTotalCountTopView = null;
        this.logItemListBox = null;
        this.logItemListView = null;
        this.emptyView = null;
        this.loadingView = null;
        this.bottomBarBox = null;
        this.allCheckView = null;
        this.itemSelectedCountView = null;
        this.itemTotalCountView = null;
        this.rejectBtn = null;
        this.acceptBtn = null;
        /* 初始化数据 */
        this.itemDataList = [];
        this.enginDataList = [];
        this.itemSelectedCount = 0;
        this.itemTotalCount = 0;
        this.itemViewList = [];
        this.updateStatusParams = null;
        this.isInit = true;

        /* 初始化方法 */
        this.init();
    };

    UnidentifiedDriverLogPage.prototype.init = function () {

        /* 绑定组件 */
        this.containerBox = this.view.find('[sid=containerBox]');
        this.itemTotalCountBox = this.view.find('[sid=itemTotalCountBox]');
        this.itemTotalCountTopView = this.view.find('[sid=itemTotalCountTopView]');
        this.logItemListBox = this.view.find('[sid=logItemListBox]');
        this.logItemListView = this.view.find('[sid=logItemListView]');
        this.emptyView = this.view.find('[sid=emptyView]');
        this.loadingView = this.view.find('[sid=loadingView]');
        this.bottomBarBox = this.view.find('[sid=bottomBarBox]');
        this.allCheckView = this.view.find('[sid=allCheckView]');
        this.itemSelectedCountView = this.view.find('[sid=itemSelectedCountView]');
        this.itemTotalCountView = this.view.find('[sid=itemTotalCountView]');
        this.rejectBtn = this.view.find('[sid=rejectBtn]');
        this.acceptBtn = this.view.find('[sid=acceptBtn]');
        /* 绑定事件 */
        this.allCheckView.on('click', $.proxy(this.onAllCheckViewClick, this));
        this.rejectBtn.on('click', $.proxy(this.onRejectBtnClick, this));
        this.acceptBtn.on('click', $.proxy(this.onAcceptBtnClick, this));
        /* 加载数据 */
        this.reloadData(true);
    };

    UnidentifiedDriverLogPage.prototype.reloadData = function (isInit) {

        this.isInit = isInit;
        this.initView();
        this.loadingView.removeClass("dn");
        ALERT.getUnidentifiedDriverLogData($.proxy(this.reloadView, this));
    };

    UnidentifiedDriverLogPage.prototype.reloadView = function (data) {

        this.itemSelectedCount = 0;
        var data = JSON.parse(data);
        this.itemDataList = data.logs;
        this.enginDataList = data.engineLogs;
        this.itemTotalCount = this.itemDataList.length +this.enginDataList.length;
        this.itemViewList = [];

        if (this.itemTotalCount === 0) {
            if (Global.isAndroid) {

                SDK.back();
            } else {

                SDK.openMainPage();
            }
            return;
        } else {
            for (var i = 0; i < this.itemDataList.length; i++) {
                var itemTemplate = new ItemTemplate(this.logItemListView, this.itemDataList[i]);
                itemTemplate.on(ItemTemplate.EVENT_ITEM_CHECK_BOX_CLICK, new EventHandler(this.onItemCheckBoxClickListener, this));
                this.itemViewList.push(itemTemplate);
            }
            for (var i = 0; i < this.enginDataList.length; i++) {
                var itemTemplate = new ItemTemplate(this.logItemListView, this.enginDataList[i]);
                itemTemplate.on(ItemTemplate.EVENT_ITEM_CHECK_BOX_CLICK, new EventHandler(this.onItemCheckBoxClickListener, this));
                this.itemViewList.push(itemTemplate);
            }
            this.itemTotalCountBox.removeClass("dn");
            this.containerBox.removeClass('df').removeClass('bov');
            this.logItemListBox.removeClass("dn");
            this.bottomBarBox.removeClass("dn");
        }

        this.itemTotalCountTopView.html(this.itemTotalCount);
        this.itemSelectedCountView.html(this.itemSelectedCount);
        this.itemTotalCountView.html(this.itemTotalCount);

        this.refreshBtnStatus();
        // if (!this.isInit) {
        // 提醒剩余Item操作
        // this.automaticReminderotherItems();
        // }
        this.loadingView.addClass("dn");
    };

    UnidentifiedDriverLogPage.prototype.initView = function () {

        this.itemTotalCountBox.addClass("dn");
        this.logItemListBox.addClass("dn");
        this.logItemListView.empty();
        this.emptyView.addClass("dn");
        this.bottomBarBox.addClass("dn");
        this.allCheckView.removeClass("on");
    };

    UnidentifiedDriverLogPage.prototype.onItemCheckBoxClickListener = function (isChecked) {

        if (isChecked) {
            this.itemSelectedCount += 1;
        } else {
            this.itemSelectedCount -= 1;
        }
        this.itemSelectedCountView.html(this.itemSelectedCount);
        if (this.itemSelectedCount == this.itemTotalCount) {
            this.allCheckView.addClass("on");
        } else {
            this.allCheckView.removeClass("on");
        }

        this.refreshBtnStatus();
    };

    UnidentifiedDriverLogPage.prototype.onAllCheckViewClick = function () {

        var isChecked = false;
        if (this.allCheckView.hasClass("on")) {
            this.allCheckView.removeClass("on");
            this.itemSelectedCount = 0;
        } else {
            this.allCheckView.addClass("on");
            this.itemSelectedCount = this.itemTotalCount;
            isChecked = true;
        }
        this.itemSelectedCountView.html(this.itemSelectedCount);
        for (var j = 0; j < this.itemViewList.length; j++) {
            var itemView = this.itemViewList[j];
            itemView.setCheckBox(isChecked);
        }
        this.refreshBtnStatus();
    };

    UnidentifiedDriverLogPage.prototype.refreshBtnStatus = function () {

        if (this.itemSelectedCount === 0) {
            this.rejectBtn.addClass("disabled");
            this.acceptBtn.addClass("disabled");
        } else {
            this.rejectBtn.removeClass("disabled");
            this.acceptBtn.removeClass("disabled");
        }
    };

    UnidentifiedDriverLogPage.prototype.onRejectBtnClick = function () {

        if (this.rejectBtn.hasClass("disabled")) {
            return;
        }

        var params = {
            ids: [],
            result: 1
        };

        for (var i = 0; i < this.itemViewList.length; i++) {
            var itemView = this.itemViewList[i];
            if (itemView.checkBox.hasClass('on')) {
                var obj = {};
                if (itemView.data.type == 6) {
                    obj.startId = itemView.data.id;
                    obj.endId = itemView.data.id;
                }else {
                    obj.startId = itemView.data.startId;
                    obj.endId = itemView.data.endId;
                }
                params.ids.push(obj);
            }
        }
        this.updateStatusParams = params;

        var tips = null;
        if (this.itemSelectedCount > 1) {
            tips = String.unidentifiedRejectTip.replace("#count#", this.itemSelectedCount.toString());
        } else {
            tips = String.unidentifiedRejectConfirm;
        }
        var dialogConfig = {
            icon: DialogConfig.Icon_Msg,
            text: tips
        };
        SDK.showDialog($.proxy(this.onDialogCallbackListener, this), dialogConfig);
    };

    UnidentifiedDriverLogPage.prototype.onDialogCallbackListener = function (which) {

        if (which == Constants.BUTTON_NEUTRAL) {
            SDK.showLoading();
            ALERT.updateUnidentifiedDriverLogStatus(this.updateStatusParams, $.proxy(this.requestUpdateUnidentifiedDriverLogStatusCallBackListener, this));
        }
    };

    UnidentifiedDriverLogPage.prototype.onAcceptBtnClick = function () {

        if (this.acceptBtn.hasClass("disabled")) {
            return;
        }

        var params = {
            ids: [],
            objects: [],
            result: 0
        };

        for (var i = 0; i < this.itemViewList.length; i++) {
            var itemView = this.itemViewList[i];
            if (itemView.checkBox.hasClass('on')) {
                var obj = {};
                if (itemView.data.type == 6) {
                    obj.startId = itemView.data.id;
                    obj.endId = itemView.data.id;
                    var object = {};
                    object.data = itemView.data;
                    params.objects.push(object);
                }else {
                    obj.startId = itemView.data.startId;
                    obj.endId = itemView.data.endId;
                    var object = {};
                    object.startJson = itemView.data.startJson;
                    object.endJson = itemView.data.endJson;
                    params.objects.push(object);
                }
                params.ids.push(obj);
            }
        }
        this.updateStatusParams = params;

        var tips = null;
        if (this.itemSelectedCount > 1) {
            tips = String.unidentifiedAcceptTip.replace("#count#", this.itemSelectedCount.toString());
        } else {
            tips = String.unidentifiedAcceptConfirm;
        }
        var dialogConfig = {
            icon: DialogConfig.Icon_Msg,
            text: tips
        };
        SDK.showDialog($.proxy(this.onDialogCallbackListener, this), dialogConfig);
    };

    UnidentifiedDriverLogPage.prototype.requestUpdateUnidentifiedDriverLogStatusCallBackListener = function (result) {

        SDK.hideLoading();
        var self = this;
        if (result == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt(function () {
                if (self.itemSelectedCount == self.itemTotalCount) {

                    if (Global.isAndroid) {

                        SDK.back();
                    } else {
                        SDK.openMainPage();
                    }
                } else {

                    self.reloadData(false);
                }
            });
        } else if (result == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt(function () {
            });
        }
    };

    UnidentifiedDriverLogPage.prototype.automaticReminderotherItems = function (result) {

        var params = {
            ids: [],
            result: 1
        };
        for (var i = 0; i < this.itemViewList.length; i++) {
            var itemView = this.itemViewList[i];
            var obj = {};
            if (itemView.data.type == 6) {
                obj.startId = itemView.data.id;
                obj.endId = itemView.data.id;
            }else {
                obj.startId = itemView.data.startId;
                obj.endId = itemView.data.endId;
            }
            params.ids.push(obj);
        }

        var tips = null;
        if (this.updateStatusParams.result == 1) {

            params.result = 0;
            if (this.itemViewList.length > 1) {

                tips = String.unidentifiedRemainAccept.replace("#count#", this.itemViewList.length.toString());
            } else {

                tips = String.unidentifiedRemainZeroAccept;
            }
        } else {

            params.result = 1;
            if (this.itemViewList.length > 1) {

                tips = String.unidentifiedRemainReject.replace("#count#", this.itemViewList.length.toString());
            } else {

                tips = String.unidentifiedRemainZeroReject;
            }
        }
        this.updateStatusParams = params;

        var dialogConfig = {
            icon: DialogConfig.Icon_Msg,
            text: tips
        };
        SDK.showDialog($.proxy(this.onDialogCallbackListener, this), dialogConfig);
    };

    // ********************* Item模型 ********************
    var ItemTemplate = function (parentView, data) {

        /* 初始化视图 */
        /**
         * type = null 普通 logs, type = 6 engine 事件
         * */
        this.type = data.type;
        this.parentView = parentView;
        this.view = null;
        /* 初始化组件 */
        this.checkBox = null;
        this.contentBox = null;
        this.vehicleNoLabel = null;
        this.intervalTimeLabel = null;
        this.intervalTimeStrLabel = null;
        this.odometerLabel = null;
        this.intervalOdometerLabel = null;
        this.startLocationLabel = null;
        /* 初始化数据 */
        this.data = data;
        /* 初始化方法 */
        this.init();
    };

    ItemTemplate.extends(BaseWidget);

    ItemTemplate.EVENT_ITEM_CHECK_BOX_CLICK = "event_item_check_box_click";

    ItemTemplate.template = null;
    ItemTemplate.prototype.init = function () {

        if (ItemTemplate.template === null) {
            ItemTemplate.template = $('#log_item_template').html();
        }
        this.view = $(ItemTemplate.template);
        this.parentView.append(this.view);
        /* 绑定组件 */
        this.checkBox = this.view.find('[sid=checkBox]');
        this.contentBox = this.view.find('[sid=contentBox]');
        this.vehicleNoLabel = this.view.find('[sid=vehicleNoLabel]');
        this.intervalTimeLabel = this.view.find('[sid=intervalTimeLabel]');
        this.intervalTimeStrLabel = this.view.find('[sid=intervalTimeStrLabel]');
        this.odometerLabel = this.view.find('[sid=odometerLabel]');
        this.intervalOdometerLabel = this.view.find('[sid=intervalOdometerLabel]');
        this.startLocationLabel = this.view.find('[sid=startLocationLabel]');
        /* 绑定事件 */
        this.checkBox.on('click', $.proxy(this.onCheckBoxClick, this));
        this.contentBox.on('click', $.proxy(this.onContentBoxClick, this));
        /* 页面渲染 */
        if (this.type == 6) {
            // 引擎事件
            this.vehicleNoLabel.text(this.data.vehicleCode);
            var eventName = '';
            if (this.data.code == 1 || this.data.code == 2) {
                eventName = 'Power On';
            }else if (this.data.code == 3 || this.data.code == 4) {
                eventName = 'Power Off';
            }
            this.intervalTimeLabel.text(eventName);
            this.intervalTimeStrLabel.text(this.data.datetimeStr);
            this.odometerLabel.addClass('dn');
            this.startLocationLabel.text(this.data.location);
        }else {
            this.vehicleNoLabel.text(this.data.vehicleNo);
            this.intervalTimeLabel.text(this.data.intervalTime);
            this.intervalTimeStrLabel.text(this.data.intervalTimeStr);
            this.intervalOdometerLabel.text(parseFloat(this.data.intervalOdometer).toFixed(2));
            this.startLocationLabel.text(this.data.startLocation);
        }

    };

    ItemTemplate.prototype.onCheckBoxClick = function () {

        var isChecked = false;
        if (this.checkBox.hasClass("on")) {
            this.checkBox.removeClass("on");
        } else {
            this.checkBox.addClass("on");
            isChecked = true;
        }
        this.fire(ItemTemplate.EVENT_ITEM_CHECK_BOX_CLICK, isChecked);
    };

    ItemTemplate.prototype.setCheckBox = function (isChecked) {

        if (isChecked) {
            this.checkBox.addClass("on");
        } else {
            this.checkBox.removeClass("on");
        }
    };

    ItemTemplate.prototype.onContentBoxClick = function () {

        // 引擎时间
        if (this.type == 6) {
            SDK.openPage(PageConfig.UnidentifiedEngineDetail.url,
                PageConfig.UnidentifiedEngineDetail.title,
                this.data);
        }else {
            SDK.openPage(PageConfig.UnidentifiedDetail.url,
                PageConfig.UnidentifiedDetail.title,
                this.data);
        }
    };

    window.UnidentifiedDriverLogPage = UnidentifiedDriverLogPage;
}($, window));
