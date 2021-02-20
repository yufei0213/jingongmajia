/**
 * @author sunyq
 * @date 2018/1/24
 * @description 新增故障列表
 */
//主页的page
var inspectionAddPage;
/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 * // ?params=[{"id": "1","value": "Fluid Leaks Under Bus","remarks": ["Coolant Leaks", "Diesel Leaks", "Motor Oil Leaks"],"remark": "1111"}]
 */
Web.init = function (params) {

    inspectionAddPage = new InspectionAddPage(params);
    if (!Global.isAndroid) {

        endKeyboardSetting();
    }
};

var InspectionAddPage = function (params) {

    this.defecfArr = params;
    this.defectMap = {};
    this.defectItemList = [];

    this.defectBox = $('[sid=defectBox]');
    this.nextBtn = $('[sid=nextBtn]');

    // 初始化数据
    this.windowHeight = null;
    this.focusDefectItem = null;

    this.init();
};

InspectionAddPage.prototype.init = function () {

    this.nextBtn.on('click', $.proxy(this.onNextBtnClickListener, this));
    this.renderPage();
};

InspectionAddPage.prototype.renderPage = function () {

    // 转换为map
    for (var i = 0; i < this.defecfArr.length; i++) {
        var defect = this.defecfArr[i];
        this.defectMap[defect.id] = defect;
    }

    SDK.getDefectList($.proxy(this.onGetDefectsListener, this));

    this.windowHeight = $(window).height();
    var self = this;
    $(window).unbind("resize");
    $(window).resize(function () {
        // 减50因为下面有导航栏
        if ($(window).height() < self.windowHeight - 50) {
            self.nextBtn.parent().addClass('dn');
            self.focusDefectItem.scroll();
        } else {
            self.nextBtn.parent().removeClass('dn');
        }
    });
};

InspectionAddPage.prototype.onGetDefectsListener = function (data) {

    var dataArr = JSON.parse(data);
    for (var i = 0, len = dataArr.length; i < len; i++) {
        var defect = dataArr[i];
        var defectItem = null;
        if (this.defectMap[defect.id]) {
            defectItem = new DefectItem(this.defectBox, this.defectMap[defect.id], true);
        } else {
            defectItem = new DefectItem(this.defectBox, defect, false);
        }
        defectItem.on(DefectItem.EVENT_DEFECT_ITEM_FOCUS, new EventHandler(this.onDefectItemFocusListener, this));
        this.defectItemList.push(defectItem);
    }
    // ios 关闭键盘问题处理全局键盘关闭处理
    if (!Global.isAndroid) {

        endKeyboardSetting();
    }
};

InspectionAddPage.prototype.onNextBtnClickListener = function () {

    var params = [];
    for (var i = 0, len = this.defectItemList.length; i < len; i++) {
        var defectItem = this.defectItemList[i];
        if (!defectItem.remarkVaild) {

            return;
        }
        if (defectItem.isSelect) {

            defectItem.receiptData['remark'] = defectItem.getData();
            params.push(defectItem.receiptData);
        }
    }

    if (params.length != 0) {

        SDK.setWebData(Constants.ADD_DEFECTS_KEY, params);
    }
    SDK.back();
};

InspectionAddPage.prototype.onDefectItemFocusListener = function (data) {

    for (var i = 0, len = this.defectItemList.length; i < len; i++) {

        var defectItem = this.defectItemList[i];
        if (defectItem.receiptData.id != data.id) {
            // 隐藏提示框
            defectItem.view.removeClass("show-option");
        } else {
            // 获取焦点的item
            this.focusDefectItem = defectItem;
        }
    }
};

//*******************************defect Item**********************************
var DefectItem = function (parentView, receiptData, isSelect) {

    // 初始化视图
    this.view = null;
    this.parentView = parentView;

    this.checkBtn = null;
    this.titleLabel = null;
    this.remarkBox = null;
    this.remarkInput = null;
    this.tipBox = null;

    this.receiptData = receiptData;
    this.isSelect = isSelect;

    this.init();
}

DefectItem.extends(BaseWidget);

DefectItem.EVENT_DEFECT_ITEM_FOCUS = "event_defect_item_focus";

DefectItem.template = null;

DefectItem.prototype.init = function () {

    if (DefectItem.template == null)
        DefectItem.template = $('#defect_template').html();
    this.view = $(DefectItem.template);
    this.parentView.append(this.view);

    // 绑定组件
    this.checkBtn = this.view.find('[sid=checkBtn]');
    this.titleLabel = this.view.find('[sid=titleLabel]');
    this.remarkBox = this.view.find('[sid=remarkBox]');
    this.remarkInput = this.view.find('[sid=remarkInput]');
    this.tipBox = this.view.find('[sid=tipBox]');
    this.tipLabel = this.view.find('[sid=tipLabel]');
    this.remarkVaild = true;
    // 绑定事件
    this.checkBtn.on('click', $.proxy(this.onViewClickListener, this));
    this.titleLabel.on('click', $.proxy(this.onViewClickListener, this));
    this.remarkInput.on("input propertychange", $.proxy(this.onRemarkValueChange, this));
    this.remarkInput.bind('keydown', function (event) {

        var e = event || window.event;
        if (!e.ctrlKey && e.keyCode == 13) {

            //这句话阻止原有的回车换行事件的冒泡执行
            return false;
        }
    });
    this.remarkInput.focus($.proxy(function () {

        this.view.addClass("focus");
        // 检查提示框
        this.onRemarkValueChange();
        // 隐藏其它item的提示框
        this.fire(DefectItem.EVENT_DEFECT_ITEM_FOCUS, this.receiptData);

        this.scroll();
    }, this));
    this.remarkInput.blur($.proxy(function () {

        this.view.removeClass("focus");
    }, this));

    this.checkSelectStatus();
    this.titleLabel.text(this.receiptData.value);

    if (this.receiptData.remark) {
        this.remarkInput.val(this.receiptData.remark);
    }

    autoTextarea(this.remarkInput.get(0));
};

DefectItem.prototype.onViewClickListener = function () {

    if (this.isSelect) {
        // 隐藏提示框
        this.view.removeClass("show-option");
        if (this.receiptData.remark) {

            this.receiptData.remark = "";
        }
        this.remarkInput.val('');
        this.isSelect = false;
    } else {

        this.isSelect = true;
    }
    this.checkSelectStatus();

    // 隐藏其它item的提示框
    this.fire(DefectItem.EVENT_DEFECT_ITEM_FOCUS, this.receiptData);
};

DefectItem.prototype.checkSelectStatus = function () {

    if (this.isSelect) {

        this.view.addClass('adddft');
        this.remarkBox.removeClass('dn');
    } else {

        this.view.removeClass('adddft');
        this.remarkBox.addClass('dn');
        this.view.removeClass("warning");
    }
};

DefectItem.prototype.onRemarkValueChange = function () {

    this.view.removeClass("warning");

    var len = this.remarkInput.val().length;
    this.remarkVaild = true;
    if (len > Constants.remarkMaxLength) {

        this.remarkVaild = false;
        this.view.addClass("warning");
        this.tipLabel.html(String.remarkMaxValid.replace("#count#", Constants.remarkMaxLength.toString()));
    }

    var value = this.remarkInput.val();
    var filterList = [];
    if (value.match(Constants.inputRegexp)) {

        this.remarkVaild = false;
        this.view.addClass("warning");
        this.tipLabel.html(String.remarkValid);
    }
    if (value && value.length > 0) {

        for (var i = 0; i < this.receiptData.remarks.length; i++) {

            var remark = this.receiptData.remarks[i];
            if (remark.toLowerCase().indexOf(value.toLowerCase()) != -1)
                filterList.push(remark);
        }
    }

    if (filterList.length > 0) {

        if (this.view.offset().top < 10) {
            this.tipBox.addClass('bottom');
        } else {
            this.tipBox.removeClass('bottom');
        }
        this.tipBox.empty();
        this.view.addClass("show-option");
        for (var i = 0; i < filterList.length; i++) {
            var remarkItem = new RemarkItem(this.tipBox, filterList[i]);
            remarkItem.on(RemarkItem.EVENT_REMARK_ITEM_CLICK, new EventHandler(this.onRemarkItemListener, this));
        }
    } else {
        this.view.removeClass("show-option");
    }
};

DefectItem.prototype.onRemarkItemListener = function (data) {

    this.remarkInput.val(data);
    this.view.removeClass("show-option");
};

DefectItem.prototype.getData = function () {

    return this.remarkInput.val();
};

DefectItem.prototype.scroll = function () {

    this.parentView.parent().parent().scrollTop(this.view.offset().top - this.parentView.offset().top + this.parentView.scrollTop());
};

/***********************remarkItem********************************/
var RemarkItem = function (parentView, receiveData) {

    this.parentView = parentView;
    this.view = null;

    this.receiptData = receiveData;

    this.init();
};

RemarkItem.extends(BaseWidget);

RemarkItem.EVENT_REMARK_ITEM_CLICK = "event_remark_item_click";

RemarkItem.template = null;

RemarkItem.prototype.init = function () {

    if (RemarkItem.template == null)
        RemarkItem.template = "<li></li>";
    this.view = $(RemarkItem.template);
    this.parentView.append(this.view);

    this.view.text(this.receiptData);
    this.view.on('click', $.proxy(this.onViewClickListener, this));
};

RemarkItem.prototype.onViewClickListener = function () {

    this.fire(RemarkItem.EVENT_REMARK_ITEM_CLICK, this.receiptData);
};