/**
 * Created by zhangyu on 18/01/22.
 */

/**
 * 绘制粗实线
 * @param tar 目标图形
 * @param p1 起始点
 * @param p2 结束点
 * @param color 绘制颜色
 */
function drawStrongSolidLine(tar, p1, p2, color) {
    tar.beginPath();
    tar.setLineDash([1, 0]);
    tar.moveTo(p1.x, p1.y);
    tar.lineTo(p2.x, p2.y);
    tar.strokeStyle = color;
    tar.lineWidth = 4 * getPixelRatio(tar);
    tar.stroke();
    tar.closePath();
};

/**
 * 绘制实线
 * @param tar 目标图形
 * @param p1 起始点
 * @param p2 结束点
 * @param color 绘制颜色
 */
function drawSolidLine(tar, p1, p2, color) {
    tar.beginPath();
    tar.setLineDash([1, 0]);
    tar.moveTo(p1.x, p1.y);
    tar.lineTo(p2.x, p2.y);
    tar.strokeStyle = color;
    tar.lineWidth = 1 * getPixelRatio(tar);
    tar.stroke();
    tar.closePath();
};

/**
 * 绘制虚线
 * @param tar 目标图形
 * @param p1 起始点
 * @param p2 结束点
 * @param color 绘制颜色
 */
function drawDashLine(tar, p1, p2, color) {

    tar.beginPath();
    tar.setLineDash([5, 5]);
    tar.moveTo(p1.x, p1.y);
    tar.lineTo(p2.x, p2.y);
    tar.strokeStyle = color;
    tar.lineWidth = 1 * getPixelRatio(tar);
    tar.stroke();
    tar.setLineDash([1, 0]);
    tar.closePath();
}

/**
 * 司机状态时间模型
 * @param driverStatus 司机状态
 * @param startSecond 该状态的起始时间(秒)
 * @param endSecond 该状态的结束时间(秒)
 * @constructor
 */
var DriverEvent = function (driverStatus, startSecond, endSecond) {
    //司机的状态
    this.driverStatus = driverStatus;
    //开始时间
    this.startSecond = startSecond;
    //结束时间
    this.endSecond = endSecond;
};


/**
 * 状态图形组件
 * @param domId 组件的Dom元素的ID,有些元素计算需要用Dom元素.
 * @param driverEvents 司机所有状态事件
 * @constructor
 */
var DriverGrid = function (object, oneDayHour, width) {
    if (typeof(object) === "string") {
        //存储View，使用#去拼接
        var domSelector = "#" + object;
        this.view = $(domSelector)
    } else {
        this.view = object;
    }
    this.oneDayHour = oneDayHour;

    this.width = width;
    this.init();
};

/**
 * 组件初始化方法
 */
DriverGrid.prototype.init = function () {

    //将视图初始化
    this.initCanvas();
    //计算总时间
    this.onDutyTimes = 0;
    this.offDutyTimes = 0;
    this.sleeperBerthTimes = 0;
    this.drivingTimes = 0;
};

/**
 *  绘制图形方法
 * 每次改变状态都需要重新绘制整个Grid
 * @param driverEvents 所有事件
 * @param active 是否是需要跟随时间变动
 */
DriverGrid.prototype.draw = function (events, active) {
    if (events) {
        this.allEvents = events;
    }
    var driverEvents = this.allEvents;

    if (this.modifyEvent) {
        driverEvents = this.modifyEvent;
    }
    var coordinate_x;
    var start = {};
    var end = {};

    var length = driverEvents.length;
    for (var i = 0; i < length; i++) {
        var event = driverEvents[i];
        coordinate_x = this.getLinePosition(event.startSecond, event.endSecond);
        var values = TimeUtil.compute(event.startSecond, event.endSecond);
        start.x = coordinate_x.start;
        end.x = coordinate_x.end;
        // SDK.log("values:" + values);
        // SDK.log("startSecond:" + event.startSecond + " endSecond:" + event.endSecond);
        // SDK.log("开始画图！events为：[" + JSON.stringify(event) + "],开始时间为:" + event.startSecond + "结束时间为:" + event.endSecond + "values为：" + values)

        switch (parseInt(event.driverStatus)) {
            case DRIVERSTATUS.DRIVING:
                this.drivingTimes += values;
                start.y = this.Y_D;
                end.y = this.Y_D;
                drawSolidLine(this.ctx, start, end, "#069AD6");
                break;
            case DRIVERSTATUS.ONDUTY:
                this.onDutyTimes += values;
                start.y = this.Y_ODND;
                end.y = this.Y_ODND;
                drawSolidLine(this.ctx, start, end, "#069AD6");
                break;
            case DRIVERSTATUS.YARDMOVE:
                this.onDutyTimes += values;
                start.y = this.Y_ODND;
                end.y = this.Y_ODND;
                drawDashLine(this.ctx, start, end, "#FF6D36");
                break;
            case DRIVERSTATUS.PERSONALUSE:
                this.offDutyTimes += values;
                start.y = this.Y_OF;
                end.y = this.Y_OF;
                drawDashLine(this.ctx, start, end, "#069AD6");
                break;
            case DRIVERSTATUS.SLEEPER:
                this.sleeperBerthTimes += values;
                start.y = this.Y_SB;
                end.y = this.Y_SB;
                drawSolidLine(this.ctx, start, end, "#069AD6");
                break;
            case DRIVERSTATUS.OFFDUTY:
                this.offDutyTimes += values;
                start.y = this.Y_OF;
                end.y = this.Y_OF;
                drawSolidLine(this.ctx, start, end, "#069AD6");
                break;
        }
        // SDK.log("this.sleeperBerthTimes:" + this.sleeperBerthTimes);
        // SDK.log("this.offDutyTimes:" + this.offDutyTimes);
        // SDK.log("this.onDutyTimes:" + this.onDutyTimes);
        // SDK.log("this.drivingTimes:" + this.drivingTimes);

        if (i < length - 1) {
            // 绘制竖线
            // 计算出当前事件的终止点,与下一个事件的起始点,将其传入绘制竖线的函数中进行竖线绘制
            var verticalStart = {};
            verticalStart.x = coordinate_x.end;
            verticalStart.y = this.getCoordinateY(event.driverStatus);
            var verticalEnd = {};
            verticalEnd.x = coordinate_x.end;
            verticalEnd.y = this.getCoordinateY(driverEvents[i + 1].driverStatus);
            drawSolidLine(this.ctx, verticalStart, verticalEnd, "#069AD6");
        }
    }
    this.drawHour();
    if (active) {
        if (this.refresh) {
            clearInterval(this.refresh);
        }
        //每分钟刷新一次
        this.refresh = setInterval($.proxy(this.timer, this), 60 * 1000);
    }
};

DriverGrid.prototype.reload = function (state) {
    state = parseInt(state);
    var lastEvent = this.allEvents[this.allEvents.length - 1];
    var newEvent = new DriverEvent(state, lastEvent.endSecond, lastEvent.endSecond);
    SDK.log(JSON.stringify(newEvent))
    this.allEvents.push(newEvent);
    this.init();
    this.draw(this.allEvents, false);
};

DriverGrid.prototype.destroy = function () {

    if (this.refresh) {

        window.clearInterval(this.refresh);
    }
}

/**
 * 开启定时状态
 */
DriverGrid.prototype.timer = function () {

    if (!this.allEvents[this.allEvents.length - 1]) {
        return;
    }
    this.allEvents[this.allEvents.length - 1].endSecond += 60;
    //跨天清空
    if (this.allEvents[this.allEvents.length - 1].endSecond >= (this.oneDayHour * 60 * 60)) {
        this.allEvents[this.allEvents.length - 1].endSecond -= this.oneDayHour * 60 * 60;
        this.allEvents[this.allEvents.length - 1].startSecond = 0;
        this.allEvents = [this.allEvents[this.allEvents.length - 1]];
    }
    this.ctx.clearRect(0, 0, this.view.width(), this.view.height());
    this.init();
    this.draw(this.allEvents, false);
};

DriverGrid.prototype.drawHour = function () {
    //绘制右侧的时间。初始化为0h 00m
    this.ctx.font = 12 * this.x + "px Verdana";
    this.ctx.fillStyle = '#333';
    this.ctx.textAlign = "start";
    var texts = this.getValues();
    this.ctx.fillText(texts.offDutyTimes, this.gridW + this.spaceX + (3 * this.x), this.cellhx * 1 + this.spaceY + (3 * this.x));
    this.ctx.fillText(texts.sleeperBerthTimes, this.gridW + this.spaceX + (3 * this.x), this.cellhx * 3 + this.spaceY + (3 * this.x));
    this.ctx.fillText(texts.drivingTimes, this.gridW + this.spaceX + (3 * this.x), this.cellhx * 5 + this.spaceY + (3 * this.x));
    this.ctx.fillText(texts.onDutyTimes, this.gridW + this.spaceX + (3 * this.x), this.cellhx * 7 + this.spaceY + (3 * this.x));
};

DriverGrid.prototype.modify = function (newEvent, isEdit, startEvent) {
    //如果没有结束时间，则是只画开始到下一结束点。
    if (!newEvent.endSecond) {
        for (var i = 0; i < this.allEvents.length; i++) {
            if (this.allEvents[i].endSecond > newEvent.startSecond) {
                newEvent.endSecond = TimeUtil.transform(this.allEvents[i].endSecond) * this.cellw;
                break;
            }
        }
    } else {
        if (newEvent.endSecond < 0) {
            newEvent.endSecond = this.allEvents[this.allEvents.length - 1].endSecond;
        }
    }

    this.modifyEvent = [];
    for (var i = 0; i < this.allEvents.length; i++) {

        //如果该事件的开始时间和结束时间都在新事件的开始时间之前，直接加进去该事件。
        if (this.allEvents[i].startSecond <= newEvent.startSecond && this.allEvents[i].endSecond <= newEvent.startSecond) {

            this.modifyEvent.push(this.allEvents[i]);
        }
        //如果该事件的开始时间在新事件的结束时间之后，直接加入该事件。
        if (this.allEvents[i].startSecond >= newEvent.endSecond) {

            this.modifyEvent.push(this.allEvents[i]);
        }
        //如果老事件完全覆盖新事件
        //并且将前后的事件进行处理
        if (this.allEvents[i].startSecond <= newEvent.startSecond && this.allEvents[i].endSecond >= newEvent.endSecond) {

            if (this.allEvents[i].startSecond != newEvent.startSecond) {
                if (i > 0 && isEdit) {
                    this.modifyEvent.push(new DriverEvent(this.allEvents[i - 1].driverStatus, this.allEvents[i].startSecond, newEvent.startSecond));
                } else {
                    this.modifyEvent.push(new DriverEvent(this.allEvents[i].driverStatus, this.allEvents[i].startSecond, newEvent.startSecond));
                }
            }
            this.modifyEvent.push(newEvent);
            if (this.allEvents[i].endSecond != newEvent.endSecond) {
                this.modifyEvent.push(new DriverEvent(this.allEvents[i].driverStatus, newEvent.endSecond, this.allEvents[i].endSecond));

            }
            continue;
        }
        //如果该时间被新事件覆盖，则直接放新事件的状态老事件的时间。
        if (this.allEvents[i].startSecond >= newEvent.startSecond && this.allEvents[i].endSecond <= newEvent.endSecond) {

            this.modifyEvent.push(new DriverEvent(newEvent.driverStatus, this.allEvents[i].startSecond, this.allEvents[i].endSecond));
            continue;

        }
        //如果该事件被新事件部分覆盖。则将该事件拆成两半。
        if (this.allEvents[i].startSecond <= newEvent.startSecond && this.allEvents[i].endSecond < newEvent.endSecond && this.allEvents[i].endSecond > newEvent.startSecond) {

            this.modifyEvent.push(new DriverEvent(this.allEvents[i].driverStatus, this.allEvents[i].startSecond, newEvent.startSecond));
            this.modifyEvent.push(new DriverEvent(newEvent.driverStatus, newEvent.startSecond, this.allEvents[i].endSecond));
        }
        if (this.allEvents[i].startSecond >= newEvent.startSecond && this.allEvents[i].startSecond < newEvent.endSecond && this.allEvents[i].endSecond < newEvent.endSecond) {

            this.modifyEvent.push(new DriverEvent(newEvent.driverStatus, this.allEvents[i].startSecond, newEvent.endSecond));
            // this.modifyEvent.push(new DriverEvent(this.allEvents[i].driverStatus,this.allEvents[i].endSecond, newEvent.endSecond));
        }
    }

    if (startEvent) {

        var temp = [];
        temp.push(startEvent);
        for (var i in this.modifyEvent) {

            var item = this.modifyEvent[i];
            if (item.startSecond != 0) {

                temp.push(item);
            }
        }

        this.modifyEvent = temp;
    }

    this.initCanvas();
    this.draw(this.allEvents, false);
    this.coverShadow(newEvent.startSecond, newEvent.endSecond);
};

/**
 * 覆盖阴影
 * 用于Modify方法
 * @param startTime 开始时间
 */
DriverGrid.prototype.coverShadow = function (startSecond, endSecond) {
    this.ctx.beginPath();
    this.ctx.lineWidth = 0;
    this.ctx.fillStyle = "#069AD6";
    this.ctx.globalAlpha = 0.3;
    var start = TimeUtil.transform(startSecond) * this.cellw;
    //如果没有结束时间，则是只画开始到下一结束点。
    if (!endSecond) {
        for (var i = 0; i < this.allEvents.length; i++) {
            if (this.allEvents[i].endSecond > startSecond) {
                var end = TimeUtil.transform(this.allEvents[i].endSecond) * this.cellw;
                break;
            }
        }
    }
    else {
        if (endSecond == -1) {
            var end = this.oneDayHour * this.cellw;
        } else {
            var end = TimeUtil.transform(endSecond) * this.cellw;
        }
    }
    var width = end - start;
    this.ctx.fillRect(start + this.spaceX, this.spaceY, width, this.gridH);
    this.ctx.closePath();
};

/**
 * 重绘
 * 用于Modify方法
 * @param startTime 开始时间
 */
DriverGrid.prototype.redraw = function () {

    this.initCanvas();
    this.draw();
};

/**
 * 画竖线
 * 用于Modify方法
 * @param startTime 开始时间
 */
DriverGrid.prototype.drawLine = function (second) {
    this.initCanvas();
    this.draw(this.allEvents, false);
    var fillColor = "#FF8600";
    this.ctx.globalAlpha = 0.3;
    var startx = TimeUtil.transform(second) * this.cellw + this.spaceX;
    drawStrongSolidLine(this.ctx, {x: startx, y: this.spaceY}, {x: startx, y: (this.spaceY + this.gridH)}, fillColor);
};

/**
 * 画竖线
 * 用于Modify方法
 * @param startTime 开始时间
 */
DriverGrid.prototype.drawRedLine = function (events) {
    var redLineEvents;
    if (events) {
        this.redEvents = events;
        redLineEvents = events;
    } else {
        if (!this.redEvents) {
            return;
        }
        redLineEvents = this.redEvents;
    }
    for (var i = 0; i < redLineEvents.length; i++) {
        var fillColor = "#ff2f2e";
        var y = this.Y_D;
        var startx = TimeUtil.transform(redLineEvents[i].start) * this.cellw + this.spaceX;
        var endx = TimeUtil.transform(redLineEvents[i].end) * this.cellw + this.spaceX;
        drawSolidLine(this.ctx, {x: startx, y: this.Y_D}, {x: endx, y: this.Y_D}, fillColor);
    }

};

/**
 * 根据状态获取对应的纵坐标
 * @param status
 * @returns {*}
 */
DriverGrid.prototype.getCoordinateY = function (status) {

    switch (parseInt(status)) {
        case DRIVERSTATUS.DRIVING:
            return this.Y_D;
        case DRIVERSTATUS.ONDUTY:
            return this.Y_ODND;
        case DRIVERSTATUS.YARDMOVE:
            return this.Y_ODND;
        case DRIVERSTATUS.PERSONALUSE:
            return this.Y_OF;
        case DRIVERSTATUS.SLEEPER:
            return this.Y_SB;
        case DRIVERSTATUS.OFFDUTY:
            return this.Y_OF;
    }
};


/**
 * 计算需要绘制点的左右坐标
 * 返回的是相对坐标
 * @param startSecond 起始时间
 * @param endSecond 结束时间
 */
DriverGrid.prototype.getLinePosition = function (startSecond, endSecond) {

    var result = {};
    result.start = TimeUtil.transform(startSecond) * this.cellw + this.spaceX;
    result.end = TimeUtil.transform(endSecond) * this.cellw + this.spaceX;
    return result;
};

/**
 * 获取四种状态的时间
 */
DriverGrid.prototype.getValues = function () {

    var result = {};
    result.drivingTimes = TimeUtil.formatHMWithoutLanguage(this.drivingTimes);

    result.onDutyTimes = TimeUtil.formatHMWithoutLanguage(this.onDutyTimes);

    result.sleeperBerthTimes = TimeUtil.formatHMWithoutLanguage(this.sleeperBerthTimes);

    result.offDutyTimes = TimeUtil.formatHMWithoutLanguage(this.offDutyTimes);

    return result;
};

/**
 * 初始化页面，画背景
 */
DriverGrid.prototype.initCanvas = function () {

    this.ctx = this.view.get(0).getContext("2d");

    this.drivingTimes = 0;
    this.onDutyTimes = 0;
    this.sleeperBerthTimes = 0;
    this.offDutyTimes = 0;
    //像素密度，视图大小及宽高比
    this.x = getPixelRatio(this.ctx);
    if (!this.width) {

        this.width = $(window).width();
    }

    this.w = this.width * this.x;

    this.whRatio = 0.32;
    this.view.css({
        width: this.width + "px",
        height: this.width * this.whRatio + "px"
    }).attr({
        width: this.w,
        height: this.w * this.whRatio
    });

    //绘图使用的变量
    //x的大小
    this.spaceX = 40 * this.x;
    //y的大小
    this.spaceY = 20 * this.x;
    this.gridW = this.w - (this.spaceX * 2) - ((50 / 3) * this.x);
    this.gridH = this.w * this.whRatio - (this.spaceY * 2);

    this.cellw = this.gridW / this.oneDayHour;
    this.cellwx = this.gridW / (this.oneDayHour * 4);
    this.cellh = this.gridH / 4;
    this.cellhx = this.gridH / 8;
    this.ctx.lineCap = "round";
    this.ctx.lineJoin = "round";

    //绘制网格
    this.ctx.beginPath();
    for (var i = 0; i <= this.oneDayHour; i++) {
        this.ctx.moveTo(this.cellw * i + this.spaceX, this.spaceY);
        this.ctx.lineTo(this.cellw * i + this.spaceX, this.gridH + this.spaceY);
    }
    for (var i = 0; i <= 4; i++) {
        this.ctx.moveTo(this.spaceX, this.cellh * i + this.spaceY);
        this.ctx.lineTo(this.gridW + this.spaceX, this.cellh * i + this.spaceY);
    }
    this.ctx.strokeStyle = "#999";
    this.ctx.lineWidth = 0.75 * this.x;
    this.ctx.stroke();
    this.ctx.closePath();

    //绘制网格内的刻度线
    this.ctx.beginPath();
    for (var i = 0; i <= (this.oneDayHour * 4); i++) {
        if (i % 4 == 1 || i % 4 == 3) {
            for (var j = 1; j <= 4; j++) {
                this.ctx.moveTo((this.cellwx * i + this.spaceX), this.cellh * j + this.spaceY);
                this.ctx.lineTo((this.cellwx * i + this.spaceX), this.cellh * j + this.spaceY - (this.cellh * 0.1));
            }
        }
        if (i % 4 == 2) {
            for (var j = 1; j <= 4; j++) {
                this.ctx.moveTo((this.cellwx * i + this.spaceX), this.cellh * j + this.spaceY);
                this.ctx.lineTo((this.cellwx * i + this.spaceX), this.cellh * j + this.spaceY - (this.cellh * 0.15));
            }
        }
    }
    this.ctx.strokeStyle = "#999";
    this.ctx.lineWidth = 0.25 * this.x;
    this.ctx.stroke();
    this.ctx.closePath();
    //绘制左侧的状态
    this.ctx.font = 12 * this.x + "px Verdana";
    this.ctx.fillStyle = '#333';
    this.ctx.textAlign = "end";
    //offDuty的y坐标
    this.Y_OF = this.cellhx * 1 + this.spaceY;
    this.Y_SB = this.cellhx * 3 + this.spaceY;
    this.Y_D = this.cellhx * 5 + this.spaceY;
    this.Y_ODND = this.cellhx * 7 + this.spaceY;
    this.ctx.fillText("OFF", this.spaceX - (3 * this.x), this.Y_OF + (3 * this.x));
    this.ctx.fillText("SB", this.spaceX - (3 * this.x), this.Y_SB + (3 * this.x));
    this.ctx.fillText("D", this.spaceX - (3 * this.x), this.Y_D + (3 * this.x));
    this.ctx.fillText("ODND", this.spaceX - (3 * this.x), this.Y_ODND + (3 * this.x));

    //绘制下面的提示线
    this.ctx.font = 12 * this.x + "px Verdana";
    this.ctx.fillStyle = '#333';
    this.ctx.textAlign = "start";
    this.ctx.fillText("Yard Move", this.cellw * 7 + this.spaceX + (3 * this.x), this.gridH + this.spaceY + (11 * this.x));
    this.ctx.fillText("Personal Use", this.cellw * 16 + this.spaceX + (3 * this.x), this.gridH + this.spaceY + (11 * this.x));

    this.ctx.beginPath();
    this.ctx.setLineDash([5, 5]);
    this.ctx.moveTo(this.cellw * 3 + this.spaceX, this.gridH + this.spaceY + (9 * this.x));
    this.ctx.lineTo(this.cellw * 7 + this.spaceX, this.gridH + this.spaceY + (9 * this.x));
    this.ctx.strokeStyle = "#FF6D36";
    this.ctx.lineWidth = this.x;
    this.ctx.stroke();
    this.ctx.setLineDash([1, 0]);
    this.ctx.closePath();

    this.ctx.beginPath();
    this.ctx.setLineDash([5, 5]);
    this.ctx.moveTo(this.cellw * 12 + this.spaceX, this.gridH + this.spaceY + (9 * this.x));
    this.ctx.lineTo(this.cellw * 16 + this.spaceX, this.gridH + this.spaceY + (9 * this.x));
    this.ctx.strokeStyle = "#069AD6";
    this.ctx.lineWidth = this.x;
    this.ctx.stroke();
    this.ctx.setLineDash([1, 0]);
    this.ctx.closePath();
    //绘制表格上面的时间数据
    this.ctx.font = 15 * this.x + "px Verdana";
    this.ctx.fillStyle = '#999';
    this.ctx.textAlign = "center";

    var noon = 12 + (this.oneDayHour - 24);
    for (var i = 0; i <= this.oneDayHour; i++) {

        if (i == 0 || i == this.oneDayHour) {

            this.ctx.fillText("M", this.cellw * i + this.spaceX, this.spaceY - (3 * this.x));
        } else if (i == noon) {

            this.ctx.fillText("N", this.cellw * i + this.spaceX, this.spaceY - (3 * this.x));
        } else {

            var hour = i;
            if (this.oneDayHour == 23 && i >= 2) {

                hour = hour + 1;
            }
            if (this.oneDayHour == 25 && i == 2) {

                this.ctx.fillText("DST", this.cellw * i + this.spaceX, this.spaceY - (3 * this.x));
                continue;
            }
            if (this.oneDayHour == 25 && i > 2) {

                hour = hour - 1;
            }

            hour = hour > 12 ? hour - 12 : hour;
            this.ctx.fillText(hour, this.cellw * i + this.spaceX, this.spaceY - (3 * this.x));
        }
    }
};
