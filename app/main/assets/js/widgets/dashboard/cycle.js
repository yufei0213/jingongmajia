/**
 * 定义一个首页的圆环
 * @param domId dom的ID
 * @constructor
 */
var DashboardCircle = function () {

    /**
     * 对应的grid圆环
     * @type {*}
     */
    this.cycleView = $("#remain");
    /**
     * 圆环后的背景
     * @type {*}
     */
    this.bgView = $("#bgstroke");
    /**
     * 已经开车时间的view
     * @type {*}
     */
    this.driveTimeView = $("#remainTime");
    /**
     * 过去的时间的数据
     * @type {*}
     */
    this.pastTimeView = $("#pastTime");
    /**
     * 周期内的数据
     * @type {*}
     */
    this.cycleTimeView = $("#cycleTime");
    /**
     * shift的时间的数据
     * @type {*}
     */
    this.shiftTimeView = $("#shiftTime");
    /**
     * 休息时间的数据
     * @type {*}
     */
    this.breakTimeView = $("#breakTime");
    /**
     * 简写的当前状态
     * @type {*}
     */
    this.stateShort = $("#state_short");

    this.init();
};
/**
 * 初始化方法
 */
DashboardCircle.prototype.init = function () {

    /**
     * 为总的px数量
     * @type {number} px为单位。
     * 作为样式
     */
    this.L = this.cycleView.attr("r") * Math.PI * 2;
    /**
     * 初始化显示cycle
     */
    this.cycleView.css({
        strokeDasharray: this.L + "px",
        strokeDashoffset: "0 px",
        stroke: "#4CD964"
    });
    var env = this;
    //每分钟从原生获取一次时间。不在js计算违规问题。
    this.minTimer = setInterval($.proxy(this.timer, this), 1 * 1000);
};

/**
 * 销毁
 */
DashboardCircle.prototype.destroy = function () {

    if (this.minTimer) {

        window.clearInterval(this.minTimer);
    }
};

DashboardCircle.prototype.timer = function () {

    DASHBOARD.getDashboardTime($.proxy(this.refreshWithData, this));
};

/**
 * 使用数据重置视图
 * @param data 主页有关的时间数据
 */
DashboardCircle.prototype.refreshWithData = function (data) {

    if (typeof(data) == "string") {

        data = JSON.parse(data);
    }

    //当前的状态是什么
    this.currentState = data.currentState;

    //当前cycle的累计时间
    this.cycleSecond = data.cycleSecond;

    //当前的shift累计时间
    this.shiftSecond = data.shiftSecond;

    //当前的休息时间
    this.breakSecond = data.breakSecond;

    //当前剩余的驾驶时间
    this.driveSecond = data.driveSecond;

    //当前已经驾驶的时间
    this.pastSecond = data.pastSecond;

    this.maxDriving = data.maxDriving;

    this.refreshView();
    var env = this;
};

/**
 * 用于切换状态
 * @param state 新状态
 */
DashboardCircle.prototype.resetState = function (state) {

    this.currentState = state;
    if (this.currentState != DRIVERSTATUS.OFFDUTY || this.currentState != DRIVERSTATUS.SLEEPER || this.currentState != DRIVERSTATUS.PERSONALUSE) {

        this.breakSecond = 0;
    }
};

/**
 * 当有数据发生变化时。由此从新进入设置时间以及计时的逻辑
 */
DashboardCircle.prototype.refreshView = function () {

    this.checkState();
    this.driveTimeView.html(this.getTimeString(this.driveSecond));
    this.pastTimeView.html(TimeUtil.format(this.pastSecond));
    this.cycleTimeView.html(TimeUtil.format(this.cycleSecond));
    this.shiftTimeView.html(TimeUtil.format(this.shiftSecond));
    this.breakTimeView.html(TimeUtil.format(this.breakSecond));

    var offset = this.L - (this.driveSecond / this.maxDriving) * this.L;
    if (offset < 0) {
        offset = 0
    }
    offset = parseFloat(offset).toFixed(3) + "px";
    this.cycleView.css({strokeDashoffset: offset});
    if (this.minTimer) {

        clearInterval(this.minTimer);
    }
    this.minTimer = setInterval($.proxy(this.timer, this), 1 * 1000);
};

DashboardCircle.prototype.getTimeString = function (second) {

    if (second <= 0) {
        second = 0;
    }
    if (second < Constants.START_WARING_MIN * 60) {
        return TimeUtil.formatWithSecond(second);
    } else {
        return TimeUtil.format(second);
    }
};

DashboardCircle.prototype.checkState = function () {

    if (this.driveSecond <= Constants.START_WARING_MIN * 60) {

        this.cycleView.css({
            stroke: "#FF6D36"
        });
        if (this.driveSecond == 0) {
            this.bgView.css({
                stroke: "#FF6D36"
            });
        }

        $(".time-t1,.time-t2,.remain-v,.past-v").css({
            fill: "#FF6D36"
        });
        this.driveTimeView.css({
            fontSize: "36px",
            letterSpacing: "-1px"
        });
    } else {
        this.driveTimeView.css({
            fontSize: "44px",
            letterSpacing: ""
        });
        this.bgView.css({
            stroke: ""
        });
        this.cycleView.css({
            stroke: "#4CD964"
        });
        $(".time-t1,.time-t2,.remain-v,.past-v").css({
            fill: ""
        });
    }
};

