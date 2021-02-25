/** remark 组件 **/
var RemarkSelector = function (view) {

    // 初始化视图
    this.view = view;
    // 初始化控件
    this.remarkInput = null;
    this.tipBox = null;
    this.tipLable = null;

    this.receiptData = null;

    this.init();
};

RemarkSelector.prototype.init = function () {

    this.remarkInput = this.view.find("[sid=remarkInput]")
    this.tipBox = this.view.find("[sid=tipBox]");
    this.tipLable = this.view.find("[sid=tipLabel]");

    // 绑定事件
    this.remarkInput.on("input propertychange", $.proxy(this.onRemarkValueChange, this));
    this.remarkInput.focus(function () {

        $(this).parent().parent().parent().addClass("focus");
    });
    this.remarkInput.blur(function () {

        $(this).parent().parent().parent().removeClass("focus");
    });

    autoTextarea(this.remarkInput.get(0));
};

RemarkSelector.prototype.setData = function (receiveDate) {

    // [{"id":"1","value":"30 Min Break"}]
    this.receiptData = receiveDate;
};

RemarkSelector.prototype.getRemark = function () {

    return this.remarkInput.val();
};

RemarkSelector.prototype.isRemarkValid = function () {

    return !this.view.hasClass('warning');
};

RemarkSelector.prototype.onRemarkValueChange = function () {

    var len = this.remarkInput.val().length;
    var value = this.remarkInput.val();

    if (len > Constants.remarkMaxLength) {

        this.view.addClass("warning");
        this.tipLable.text(String.remarkMaxValid.replace("#count#", Constants.remarkMaxLength.toString()));
    } else if(value.match(Constants.inputRegexp)) {

        this.view.addClass("warning");
        this.tipLable.text(String.remarkValid);
    } else {

        this.view.removeClass("warning");
    }

    // 提示框 现在不显示
    // var value = this.remarkInput.val();
    //
    // var filterList = [];
    // if (value && value.length > 0) {
    //
    //     for (var i = 0; i < this.receiptData.length; i++) {
    //
    //         var remark = this.receiptData[i];
    //         if (remark.value.toLowerCase().indexOf(value.toLowerCase()) != -1)
    //             filterList.push(remark);
    //     }
    // }
    // if (filterList.length > 0) {
    //     this.tipBox.empty();
    //     this.view.addClass("show-option");
    //     for (var i = 0; i < filterList.length; i++) {
    //         var remarkItem = new RemarkItem(this.tipBox, filterList[i]);
    //         remarkItem.on(RemarkItem.EVENT_REMARK_ITEM_CLICK, new EventHandler(this.onRemarkItemListener, this));
    //     }
    // } else {
    //     this.view.removeClass("show-option");
    // }
};

RemarkSelector.prototype.onRemarkItemListener = function (data) {

    this.remarkInput.val(data.value);
    this.view.removeClass("show-option");
};


/**************************remarkItem******************************/
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

    this.view.text(this.receiptData.value);
    this.view.on('click', $.proxy(this.onViewClickListener, this));
};

RemarkItem.prototype.onViewClickListener = function () {

    this.fire(RemarkItem.EVENT_REMARK_ITEM_CLICK, this.receiptData);
};