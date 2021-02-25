/**
 * @author mamw
 * @date 2018/1/27
 * @description alert页面
 */
var page = null;

/**
 * 初始化
 */
Web.init = function (params) {

    page = new AlertPage($('#page'), params);
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    switch (page.tabIndex) {
        case 0:
            page.onNotCertifiedTabClick();
            break;
        case 1:
            page.onEditTabClick();
            break;
        case 2:
            page.onAssignedClick();
            break;
    }
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {
        tabIndex: 0 // 0:not certified, 1:edit, 2:assigned
    };

    /*
     * 界面
     **/
    var AlertPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.notCertifiedTab = null;
        this.notCertifiedAlertTip = null;
        this.editTab = null;
        this.requestEditAlertTip = null;
        this.assignedTab = null;
        this.assignedAlertTip = null;
        this.containerBox = null;
        this.alertItemList = null;
        this.emptyView = null;
        this.bottomBarBox = null;
        this.allCheckView = null;
        this.itemSelectedCountView = null;
        this.itemTotalCountView = null;
        this.signBtn = null;
        this.rejectBtn = null;
        this.acceptBtn = null;
        this.loadingView = null;
        /* 初始化数据 */
        this.notCertifiedDataList = [];
        this.editDataList = [];
        this.assignedDataList = [];
        this.assignedEngineList = [];
        this.itemSelectedCount = 0;
        this.itemTotalCount = 0;
        this.notCertifiedItemList = [];
        this.editItemList = [];
        this.assignedItemList = [];

        /* 初始化方法 */
        this.init();
    };

    AlertPage.prototype.init = function () {

        /* 绑定组件 */
        this.notCertifiedTab = this.view.find('[sid=notCertifiedTab]');
        this.notCertifiedAlertTip = this.view.find('[sid=notCertifiedAlertTip]');
        this.editTab = this.view.find('[sid=editTab]');
        this.requestEditAlertTip = this.view.find('[sid=editAlertTip]');
        this.assignedTab = this.view.find('[sid=assignedTab]');
        this.assignedAlertTip = this.view.find('[sid=assignedAlertTip]');
        this.containerBox = this.view.find('[sid=containerBox]');
        this.alertItemList = this.view.find('[sid=alertItemList]');
        this.emptyView = this.view.find('[sid=emptyView]');
        this.bottomBarBox = this.view.find('[sid=bottomBarBox]');
        this.allCheckView = this.view.find('[sid=allCheckView]');
        this.itemSelectedCountView = this.view.find('[sid=itemSelectedCountView]');
        this.itemTotalCountView = this.view.find('[sid=itemTotalCountView]');
        this.signBtn = this.view.find('[sid=signBtn]');
        this.rejectBtn = this.view.find('[sid=rejectBtn]');
        this.acceptBtn = this.view.find('[sid=acceptBtn]');
        this.loadingView = this.view.find('[sid=loadingView]');
        /* 绑定事件 */
        this.notCertifiedTab.on('click', $.proxy(this.onNotCertifiedTabClick, this));
        this.editTab.on('click', $.proxy(this.onEditTabClick, this));
        this.assignedTab.on('click', $.proxy(this.onAssignedClick, this));
        this.allCheckView.on('click', $.proxy(this.onAllCheckViewClick, this));
        this.signBtn.on('click', $.proxy(this.onSignBtnClick, this));
        this.rejectBtn.on('click', $.proxy(this.onRejectBtnClick, this));
        this.acceptBtn.on('click', $.proxy(this.onAcceptBtnClick, this));

        /* 加载数据 */
        ALERT.getAlertSummary($.proxy(this.onGetAlertSummaryListener, this));
        if (this.tabIndex === 0) {
            this.onNotCertifiedTabClick();
        } else if (this.tabIndex === 1) {
            this.onEditTabClick();
        } else if (this.tabIndex === 2) {
            this.onAssignedClick();
        }
    };

    AlertPage.prototype.onGetAlertSummaryListener = function (data) {

        var summary = JSON.parse(data);
        if (summary.notCertifiedLogsCnt > 0) {

            this.notCertifiedAlertTip.addClass('dvStatus red vat');
        } else {

            this.notCertifiedAlertTip.removeClass('dvStatus red vat');
        }

        if (summary.requestedEditsCnt > 0) {

            this.requestEditAlertTip.addClass('dvStatus red vat');
        } else {

            this.requestEditAlertTip.removeClass('dvStatus red vat');
        }

        if ( summary.assignedCnt > 0) {

            this.assignedAlertTip.addClass('dvStatus red vat');
        } else {

            this.assignedAlertTip.removeClass('dvStatus red vat');
        }
    };

    AlertPage.prototype.onNotCertifiedTabClick = function () {

        this.initView();

        this.tabIndex = 0;
        this.notCertifiedTab.addClass("on");

        this.loadingView.removeClass("dn");
        var params = {
            tabIndex: this.tabIndex
        };
        ALERT.getNotCertifiedAlertData(params, $.proxy(this.reloadAlertData, this));
    };

    AlertPage.prototype.onEditTabClick = function () {

        this.initView();

        this.tabIndex = 1;
        this.editTab.addClass("on");

        this.loadingView.removeClass("dn");
        var params = {
            tabIndex: this.tabIndex,
            index:-1//请求所有
        };
        ALERT.getEditAlertData(params, $.proxy(this.reloadAlertData, this))
    };

    AlertPage.prototype.onAssignedClick = function () {

        this.initView();

        this.tabIndex = 2;
        this.assignedTab.addClass("on");

        this.loadingView.removeClass("dn");
        var params = {
            tabIndex: this.tabIndex
        };
        ALERT.getAssignedAlertData(params, $.proxy(this.reloadAlertData, this))
    };

    AlertPage.prototype.initView = function () {

        this.notCertifiedTab.removeClass("on");
        this.editTab.removeClass("on");
        this.assignedTab.removeClass("on");

        this.containerBox.addClass('df').addClass('bov');
        this.bottomBarBox.addClass("dn");
        this.signBtn.addClass("dn");
        this.rejectBtn.addClass("dn");
        this.acceptBtn.addClass("dn");
        this.emptyView.addClass("dn");

        this.itemSelectedCount = 0;
        this.itemTotalCount = 0;

        this.allCheckView.removeClass("on");
        this.alertItemList.empty();
    };

    AlertPage.prototype.reloadAlertData = function (tabIndex, data) {

        if (this.tabIndex != tabIndex) {
            return;
        }

        this.alertItemList.empty();

        if (this.tabIndex === 0) {

            this.notCertifiedDataList = JSON.parse(data);
            this.bottomBarBox.removeClass("dn");
            this.signBtn.removeClass("dn");
            if (this.notCertifiedDataList === null || this.notCertifiedDataList.length === 0) {
                this.emptyView.removeClass("dn");
                this.bottomBarBox.addClass("dn");
                this.notCertifiedAlertTip.removeClass('dvStatus red vat');
            } else {
                this.notCertifiedAlertTip.addClass('dvStatus red vat');
                this.containerBox.removeClass('df').removeClass('bov');
                this.itemTotalCount = this.notCertifiedDataList.length;
                this.notCertifiedItemList = [];
                for (var i = 0; i < this.notCertifiedDataList.length; i++) {
                    var notCertifiedItem = new NotCertifiedItem(this.alertItemList, this.notCertifiedDataList[i]);
                    notCertifiedItem.on(NotCertifiedItem.EVENT_NOTCERTIFED_ITEM_CHECK_BOX_CLICK, new EventHandler(this.onItemCheckBoxClickListener, this));
                    this.notCertifiedItemList.push(notCertifiedItem);
                }
            }
        } else if (this.tabIndex === 1) {

            this.editDataList = JSON.parse(data);

            if (this.editDataList === null || this.editDataList.length === 0) {
                this.emptyView.removeClass("dn");
                this.bottomBarBox.addClass("dn");
                this.requestEditAlertTip.removeClass('dvStatus red vat');
            } else {
                this.requestEditAlertTip.addClass('dvStatus red vat');
                this.containerBox.removeClass('df').removeClass('bov');
                this.itemTotalCount = this.editDataList.length;
                this.editItemList = [];
                for (var j = 0; j < this.editDataList.length; j++) {
                    var editItem = new EditItem(this.alertItemList, this.editDataList[j]);
                    this.editItemList.push(editItem);
                }
            }
        } else if (this.tabIndex === 2) {
            var assignedData = JSON.parse(data);
            this.assignedDataList = assignedData.logs;
            this.assignedEngineList = assignedData.engineLogs;
            var assignedTotalItem = this.assignedDataList.length + this.assignedEngineList.length;

            this.bottomBarBox.removeClass("dn");
            this.rejectBtn.removeClass("dn");
            this.acceptBtn.removeClass("dn");
            if (assignedTotalItem === 0) {
                this.emptyView.removeClass("dn");
                this.bottomBarBox.addClass("dn");
                this.assignedAlertTip.removeClass('dvStatus red vat');
            } else {
                this.containerBox.removeClass('df').removeClass('bov');
                this.assignedAlertTip.addClass('dvStatus red vat');
                this.itemTotalCount = assignedTotalItem;
                this.assignedItemList = [];
                for (var k = 0; k < this.assignedDataList.length; k++) {
                    var assignerItem = new AssignedItem(this.alertItemList, this.assignedDataList[k]);
                    assignerItem.on(AssignedItem.EVENT_ASSIGNED_ITEM_CHECK_BOX_CLICK, new EventHandler(this.onItemCheckBoxClickListener, this));
                    this.assignedItemList.push(assignerItem);
                }
                for (var j = 0; j < this.assignedEngineList.length; j++) {
                    var assignerItem = new AssignedItem(this.alertItemList, this.assignedEngineList[j]);
                    assignerItem.on(AssignedItem.EVENT_ASSIGNED_ITEM_CHECK_BOX_CLICK, new EventHandler(this.onItemCheckBoxClickListener, this));
                    this.assignedItemList.push(assignerItem);
                }
            }
        }
        this.itemSelectedCountView.html(this.itemSelectedCount);
        this.itemTotalCountView.html(this.itemTotalCount);

        this.refreshBtnStatus();
        this.loadingView.addClass("dn");
    };

    AlertPage.prototype.onItemCheckBoxClickListener = function (isChecked) {

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

    AlertPage.prototype.onAllCheckViewClick = function () {

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
        if (this.tabIndex === 0) {
            for (var i = 0; i < this.notCertifiedItemList.length; i++) {
                var notCertifiedItem = this.notCertifiedItemList[i];
                notCertifiedItem.setCheckBox(isChecked);
            }
        } else if (this.tabIndex === 2) {
            for (var j = 0; j < this.assignedItemList.length; j++) {
                var assignedItem = this.assignedItemList[j];
                assignedItem.setCheckBox(isChecked);
            }
        }
        this.refreshBtnStatus();
    };

    AlertPage.prototype.refreshBtnStatus = function () {

        if (this.itemSelectedCount === 0) {
            if (this.tabIndex === 0) {
                this.signBtn.addClass("disabled")
            } else if (this.tabIndex === 2) {
                this.rejectBtn.addClass("disabled");
                this.acceptBtn.addClass("disabled");
            }
        } else {
            if (this.tabIndex === 0) {
                this.signBtn.removeClass("disabled")
            } else if (this.tabIndex === 2) {
                this.rejectBtn.removeClass("disabled");
                this.acceptBtn.removeClass("disabled");
            }
        }
    };

    AlertPage.prototype.onSignBtnClick = function () {

        if (this.signBtn.hasClass("disabled")) {
            return;
        }
        var params = {
            date: "",
            fullSize:1
        };
        for (var i = 0; i < this.notCertifiedItemList.length; i++) {
            var notCertifiedItem = this.notCertifiedItemList[i];
            if (notCertifiedItem.checkBox.hasClass('on')) {
                params.date += params.date == "" ? notCertifiedItem.data.backDateStr : "," + notCertifiedItem.data.backDateStr;
            }
        }
        SDK.openPage(PageConfig.AlertsSign.url,
            PageConfig.AlertsSign.title,
            params);
    };

    AlertPage.prototype.onRejectBtnClick = function () {

        if (this.rejectBtn.hasClass("disabled")) {
            return;
        }

        var params = {
            ids: [],
            result: 1
        };

        for (var i = 0; i < this.assignedItemList.length; i++) {
            var assignedItem = this.assignedItemList[i];
            if (assignedItem.checkBox.hasClass('on')) {
                var obj = {};
                if (assignedItem.data.type == 6) {
                    obj.startId = assignedItem.data.id;
                    obj.endId = assignedItem.data.id;
                }else {
                    obj.startId = assignedItem.data.startId;
                    obj.endId = assignedItem.data.endId;
                }
                params.ids.push(obj);
            }
        }

        var replaceArr = [];
        replaceArr.push(String.reject);
        var numStr = "";
        if (params.ids.length > 1) {
            numStr =String.alertLogs.replace("#count#",params.ids.length) ;
        } else {
            numStr = String.alertLog;
        }
        replaceArr.push(numStr);

        var config = {
            icon: DialogConfig.Icon_Msg,
            text: this.formatStr(String.assignDialogTip, replaceArr),
            negativeBtnText: String.cancel,
            neutralBtnText: String.confirm
        };
        SDK.showDialog($.proxy(function (button) {
            if (button == Constants.BUTTON_NEUTRAL) {

                SDK.showLoading();
                ALERT.updateAssignedAlertStatus(params, $.proxy(this.requestUpdateAssignedAlertStatusCallBackListener, this));
            }
        }, this), config);
    };

    AlertPage.prototype.onAcceptBtnClick = function () {

        if (this.acceptBtn.hasClass("disabled")) {
            return;
        }

        var params = {
            ids: [],
            objects: [],
            result: 0
        };

        for (var i = 0; i < this.assignedItemList.length; i++) {
            var assignedItem = this.assignedItemList[i];
            if (assignedItem.checkBox.hasClass('on')) {
                var idObj = {};
                var jsonObj = {};

                if (assignedItem.data.type == 6) {
                    idObj.startId = assignedItem.data.id;
                    idObj.endId = assignedItem.data.id;
                }else {
                    idObj.startId = assignedItem.data.startId;
                    idObj.endId = assignedItem.data.endId;
                    jsonObj.startJson = assignedItem.data.startJson;
                    jsonObj.endJson = assignedItem.data.endJson;
                    params.objects.push(jsonObj);
                }
                params.ids.push(idObj);
            }
        }


        var replaceArr = [];
        replaceArr.push(String.accept);
        var numStr = "";
        if (params.ids.length > 1) {
            numStr =String.alertLogs.replace("#count#",params.ids.length) ;
        } else {
            numStr = String.alertLog;
        }
        replaceArr.push(numStr);

        var config = {
            icon: DialogConfig.Icon_Msg,
            text: this.formatStr(String.assignDialogTip, replaceArr),
            negativeBtnText: String.cancel,
            neutralBtnText: String.confirm
        };
        SDK.showDialog($.proxy(function (button) {
            if (button == Constants.BUTTON_NEUTRAL) {

                SDK.showLoading();
                ALERT.updateAssignedAlertStatus(params, $.proxy(this.requestUpdateAssignedAlertStatusCallBackListener, this));
            }
        }, this), config);
    };

    AlertPage.prototype.requestUpdateAssignedAlertStatusCallBackListener = function (result) {

        SDK.hideLoading();
        var self = this;
        if (result == Constants.CALLBACK_SUCCESS) {

            SDK.showSuccessPrompt(function () {
                self.onAssignedClick();
            });
        } else if (result == Constants.CALLBACK_FAILURE) {

            SDK.showFailedPrompt(function () {
            });
        }
    };

    // 通配符函数
    AlertPage.prototype.formatStr = function (str, replaceArr) {

        if (replaceArr.length == 0) return this;
        for (var i = 0; i < replaceArr.length; i++)
            str = str.replace(new RegExp("\\{" + i + "\\}", "g"), replaceArr[i]);
        return str;
    };

    // ********************* Not Certified Item模型 ********************
    var NotCertifiedItem = function (parentView, data) {

        /* 初始化视图 */
        this.parentView = parentView;
        this.view = null;
        /* 初始化组件 */
        this.checkBox = null;
        this.dateBox = null;
        this.dateLabel = null;
        this.timeLabel = null;
        /* 初始化数据 */
        this.data = data;
        /* 初始化方法 */
        this.init();
    };

    NotCertifiedItem.extends(BaseWidget);

    NotCertifiedItem.EVENT_NOTCERTIFED_ITEM_CHECK_BOX_CLICK = "event_notcertified_item_check_box_click";

    NotCertifiedItem.template = null;
    NotCertifiedItem.prototype.init = function () {

        if (NotCertifiedItem.template === null) {
            NotCertifiedItem.template = $('#notCertifiedItemTemplate').html();
        }
        this.view = $(NotCertifiedItem.template);
        this.parentView.append(this.view);

        /* 绑定组件 */
        this.checkBox = this.view.find('[sid=checkBox]');
        this.dateBox = this.view.find('[sid=dateBox]');
        this.dateLabel = this.view.find('[sid=dateLabel]');
        this.timeLabel = this.view.find('[sid=timeLabel]');
        /* 绑定事件 */
        this.checkBox.on('click', $.proxy(this.onCheckBoxClick, this));
        this.dateBox.on('click', $.proxy(this.onDateBoxClick, this));
        /* 页面渲染 */
        this.dateLabel.text(this.data.showDateStr);
        this.timeLabel.text(this.data.onDutyTime);
    };

    NotCertifiedItem.prototype.onCheckBoxClick = function () {

        var isChecked = false;
        if (this.checkBox.hasClass("on")) {
            this.checkBox.removeClass("on");
        } else {
            this.checkBox.addClass("on");
            isChecked = true;
        }
        this.fire(NotCertifiedItem.EVENT_NOTCERTIFED_ITEM_CHECK_BOX_CLICK, isChecked);
    };

    NotCertifiedItem.prototype.onDateBoxClick = function () {

        if(this.data.showDetail == 0){
            var dialogConfig = {
                icon: DialogConfig.Icon_Msg,
                text: String.notSupportedAssign,
                positiveBtnText: String.ok,
            };
            SDK.showDialog(null , dialogConfig);

        }else{
             var params = {
                date: this.data.timeStamp
             };
             ALERT.openDailyLogDetailPage(params);
        }
    };

    NotCertifiedItem.prototype.setCheckBox = function (isChecked) {

        if (isChecked) {
            this.checkBox.addClass("on");
        } else {
            this.checkBox.removeClass("on");
        }
    };

    // ********************* Edit Item模型 ********************
    var EditItem = function (parentView, data) {

        /* 初始化视图 */
        this.parentView = parentView;
        this.view = null;
        /* 初始化组件 */
        this.createDateLabel = null;
        this.editorLabel = null;
        this.editDateLabel = null;
        /* 初始化数据 */
        this.data = data;
        /* 初始化方法 */
        this.init();
    };

    EditItem.template = null;
    EditItem.prototype.init = function () {

        if (EditItem.template === null) {
            EditItem.template = $('#editItemTemplate').html();
        }
        this.view = $(EditItem.template);
        this.parentView.append(this.view);
        /* 绑定组件 */
        this.createDateLabel = this.view.find('[sid=createDateLabel]');
        this.editorLabel = this.view.find('[sid=editorLabel]');
        this.editDateLabel = this.view.find('[sid=editDateLabel]');
        this.view.on('click', $.proxy(this.onEditItemClick, this));
        /* 页面渲染 */

        this.createDateLabel.text(this.data.createDateStr);
        this.editorLabel.text(this.data.editor);
        this.editDateLabel.text(this.data.editDateStr);
    };

    EditItem.prototype.onEditItemClick = function () {

        SDK.openPage(PageConfig.AlertsEditDetail.url,
            PageConfig.AlertsEditDetail.title,
            this.data);
    };

    // ********************* Assigned Item模型 ********************
    var AssignedItem = function (parentView, data) {

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

    AssignedItem.extends(BaseWidget);

    AssignedItem.EVENT_ASSIGNED_ITEM_CHECK_BOX_CLICK = "event_assigned_item_check_box_click";

    AssignedItem.template = null;
    AssignedItem.prototype.init = function () {

        if (AssignedItem.template === null) {
            AssignedItem.template = $('#assignedItemTemplate').html();
        }
        this.view = $(AssignedItem.template);
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
            this.intervalOdometerLabel.text(this.data.intervalOdometer);
            this.startLocationLabel.text(this.data.startLocation);
        }
    };

    AssignedItem.prototype.onCheckBoxClick = function () {

        var isChecked = false;
        if (this.checkBox.hasClass("on")) {
            this.checkBox.removeClass("on");
        } else {
            this.checkBox.addClass("on");
            isChecked = true;
        }
        this.fire(AssignedItem.EVENT_ASSIGNED_ITEM_CHECK_BOX_CLICK, isChecked);
    };

    AssignedItem.prototype.setCheckBox = function (isChecked) {

        if (isChecked) {
            this.checkBox.addClass("on");
        } else {
            this.checkBox.removeClass("on");
        }
    };

    AssignedItem.prototype.onContentBoxClick = function () {

        // SDK.openPage(PageConfig.AlertsAssignDetail.url,
        //     PageConfig.AlertsAssignDetail.title,
        //     this.data);
    };

    window.AlertPage = AlertPage;

}($, window));
