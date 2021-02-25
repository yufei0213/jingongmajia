/**
 * @author mamw
 * @date 2018/1/25
 * @description dot review页面
 */

/**
 * 初始化
 */
Web.init = function () {

    this.dotReviewPage = new DotReviewPage($('#page'));
};

/**
 * onPause后，页面再次可用时调用
 */
Web.onReload = function (params) {

    this.dotReviewPage.reloadData();
};

(function ($, window) {

    // 默认数据
    var defaultSetting = {};

    /*
     * 界面
     **/
    var DotReviewPage = function (view, option) {

        $.extend(this, defaultSetting, option || {});

        /* 初始化视图 */
        this.view = view;
        /* 初始化组件 */
        this.preBtn = null;
        this.dateLabel = null;
        this.nextBtn = null;
        this.zoomInBtn = null;
        this.zoomOutBtn = null;
        this.reviewList = null;
        this.controlList = null;
        this.dateSwitchBox = null;

        this.swiperContainerView = null;
        this.loadingView = null;

        /* 初始化数据 */
        this.reviewDataList = null;
        this.reviewIndex = 0;
        /* 初始化方法 */
        this.init();
    };

    DotReviewPage.prototype.init = function () {

        /* 绑定组件 */
        this.preBtn = this.view.find('[sid=preBtn]');
        this.dateLabel = this.view.find('[sid=dateLabel]');
        this.nextBtn = this.view.find('[sid=nextBtn]');
        this.zoomInBtn = this.view.find('[sid=zoomInBtn]');
        this.zoomOutBtn = this.view.find('[sid=zoomOutBtn]');
        this.reviewList = this.view.find('[sid=reviewList]');
        this.controlList = this.view.find('[sid=controlList]');
        this.dateSwitchBox = this.view.find('[sid=dateSwitchBox]');

        this.swiperContainerView = this.view.find('[sid=swiperContainerView]');
        this.loadingView = this.view.find('[sid=loadingView]');

        $(".swiper_container_view").height($(window).height() - 49);
        $(".dot-container").height($(window).height() - 49);
        $(".partial_loading.dot").height($(window).height());

        /* 绑定事件 */
        this.zoomInBtn.on('click', $.proxy(this.onZoomInBtnClick, this));
        this.zoomOutBtn.on('click', $.proxy(this.onZoomOutBtnClick, this));
        this.swiperContainerView.on('dblclick', $.proxy(this.onSwiperDoubleTap, this));
        /* 加载数据 */
        this.reloadData();

        this.onZoomOutBtnClick();
    };

    DotReviewPage.prototype.reloadData = function () {

        var self = this;
        this.loadingView.removeClass("dn");
        DOT.getReviewDataList(function (data) {

            self.reviewDataList = JSON.parse(data);
            self.reviewIndex = 0;
            self.setReviewData();
        });
    };

    DotReviewPage.prototype.setReviewData = function () {

        var reviewData = this.reviewDataList[this.reviewIndex];
        // 日期内容
        this.dateLabel.text(reviewData.switchBarDateStr);
        // 表头内容
        this.reviewList.empty();
        this.controlList.empty();
        for (var i = 0; i < this.reviewDataList.length; i++) {
            new DateReview(this.reviewList, {
                data: this.reviewDataList[this.reviewDataList.length - i - 1]
            });
            new SwiperControlItem(this.controlList);
        }

        this.swiperView = new Swiper('#swiper_container_view', {
            noSwiping: true
        });
        var self = this;
        this.swiperControl = new Swiper('#swiper_container_control', {
            controller: {
                control: this.swiperView
            },
            on: {
                // slideNextTransitionStart: $.proxy(this.onSwiperSlideNextTransition, this),
                // slidePrevTransitionStart: $.proxy(this.onSwiperSlidePreTransition, this),
                slideChangeTransitionStart: function () {
                    self.onSlideChangeTransition(this.activeIndex);
                }
            },
            navigation: {
                nextEl: this.nextBtn,
                prevEl: this.preBtn
            }
        });
        this.swiperControl.slideTo(this.reviewDataList.length, 0, false);
        // this.dateSwitchBox.removeClass("dn");
        this.swiperContainerView.removeClass("dn");
        this.loadingView.addClass("dn");
    };

    DotReviewPage.prototype.onPreBtnClick = function () {

        if (this.reviewIndex === this.reviewDataList.length) {
            return;
        }

        this.reviewIndex += 1;
        this.dateLabel.text(this.reviewDataList[this.reviewIndex].switchBarDateStr);
    };

    DotReviewPage.prototype.onNextBtnClick = function () {

        if (this.reviewIndex === 0) {
            return;
        }

        this.reviewIndex -= 1;
        this.dateLabel.text(this.reviewDataList[this.reviewIndex].switchBarDateStr);
    };

    DotReviewPage.prototype.onZoomInBtnClick = function () {

        if (this.zoomInBtn.hasClass("disabled")) {
            return;
        }
        var width = $(".scale_panel").width();
        var ratio = $(window).width() / width;
        $(".scale_panel").each(function () {
            $(this).css({
                height: $(this).height() / ratio + "px",
                transform: "scale(1)",
                transformOrigin: "0 0"
            });
        });
        this.zoomOutBtn.removeClass("disabled");
        this.zoomInBtn.addClass("disabled");
        this.swiperContainerView.addClass("enlarged");
    };

    DotReviewPage.prototype.onZoomOutBtnClick = function () {

        if (this.zoomOutBtn.hasClass("disabled")) {
            return;
        }
        var width = $(".scale_panel").width();
        var ratio = $(window).width() / width;
        $(".scale_panel").each(function () {
            $(this).css({
                height: $("#swiper_container_view").hasClass("enlarged") ? $(this).height() * ratio + "px" : $(this).height() + "px",
                transform: "scale(" + ratio + ")",
                transformOrigin: "0 0"
            });
        });
        this.zoomOutBtn.addClass("disabled");
        this.zoomInBtn.removeClass("disabled");
        this.swiperContainerView.removeClass("enlarged");
    };

    DotReviewPage.prototype.onSwiperDoubleTap = function () {

        if (this.swiperContainerView.hasClass("enlarged")) {
            this.onZoomOutBtnClick();
        } else {
            this.onZoomInBtnClick();
        }
    };

    DotReviewPage.prototype.onSwiperSlideNextTransition = function () {

        this.onZoomOutBtnClick();
        this.onNextBtnClick();
    };

    DotReviewPage.prototype.onSwiperSlidePreTransition = function () {

        this.onZoomOutBtnClick();
        this.onPreBtnClick();
    };

    DotReviewPage.prototype.onSlideChangeTransition = function (index) {

        this.onZoomOutBtnClick();
        this.dateLabel.text(this.reviewDataList[this.reviewDataList.length - (index + 1)].switchBarDateStr);
    };

    // ********************* Swiper Control Item模型 ********************
    var SwiperControlItem = function (parentView) {

        /* 初始化视图 */
        this.parentView = parentView;
        this.view = null;
        /* 初始化组件 */
        /* 初始化数据 */
        /* 初始化方法 */
        this.init();
    };

    SwiperControlItem.template = null;
    SwiperControlItem.prototype.init = function () {

        if (SwiperControlItem.template === null) {
            SwiperControlItem.template = $('#swiper_container_control_item_template').html();
        }
        this.view = $(SwiperControlItem.template);
        /* 绑定组件 */
        /* 页面渲染 */
        this.view.removeClass("dn");
        this.parentView.append(this.view);
    };

    window.DotReviewPage = DotReviewPage;
}($, window));