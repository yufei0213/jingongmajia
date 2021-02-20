/**
 * Created by zhangyu on 18/01/22.
 */


/**
 * 时间工具类
 * @constructor
 */
var TimeUtil = function () {
};

/**
 * 比较两个时间的大小
 * 如果第一个时间大,返回true,反之返回false
 * @param time1 hh:mm 型
 * @param time2 hh:mm 型
 * @constructor
 */
TimeUtil.compare = function (time1, time2) {
    //张宇添加。解决下面的代码当time1为24:00时，返回false的问题。
    if (time1 == "24:00") {
        return true;
    }
    var t1 = '12/28/2016 ' + time1; // 不要想太多,只是因为今天是12/28
    var t2 = '12/28/2016 ' + time2;


    return (new Date(t1) > new Date(t2));
};

/**
 * 比较两个是否相等，张宇添加，用于修改在时间节点开始时无法画阴影的问题。代码出自 12/28/2016 鲁商国奥城6号楼2214，作者张前园
 * 如果相等，则返回true,反之返回false
 * @param time1 hh:mm 型
 * @param time2 hh:mm 型
 * @constructor
 */
TimeUtil.isEqual = function (time1, time2) {

    var t1 = '12/28/2016 ' + time1; // 不要想太多,只是因为今天是12/28
    var t2 = '12/28/2016 ' + time2;

    return (new Date(t1).getTime() == new Date(t2).getTime());
};
/**
 * 时间转换函数
 * 将时间转换成时间小数
 * @param time 需要拆分的时间
 */
TimeUtil.transform = function (time) {

    return parseFloat(time) / 3600;
};

/**
 * 计算时间差
 * @param startTime 开始时间
 * @param endTime 结束时间
 * @returns {number}
 */
TimeUtil.compute = function (startTime, endTime) {

    return endTime - startTime;
};

/**
 * 判断时间是否在范围内
 * @param time 指定时间
 * @param startTime 其实时间
 * @param endTime 结束时间
 * @returns {*}
 */
TimeUtil.isInRange = function (time, startTime, endTime) {

    //张宇添加，用于判断是否是开始节点，可以排除结束点,如果是开始节点，则判断为在时间区间内。
    return TimeUtil.isEqual(time, startTime) || ( TimeUtil.compare(time, startTime) && TimeUtil.compare(endTime, time) );
};

/**
 * 时间转换
 * 由浮点型转换成 h m 型
 * @param time
 */
TimeUtil.formatHM = function (time, totalHours) {

    time = parseInt(time);
    if (totalHours)
        totalHours = parseInt(totalHours);

    if (time >= 2 * 3600 && totalHours == 25) {

        time = time - 3600;
    }
    if (time >= 2 * 3600 && totalHours == 23) {

        time = time + 3600;
    }

    var hour = parseInt(time / 3600);
    var min = parseInt((time - hour * 3600 ) / 60);
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (min < 10) {
        min = "0" + min;
    }
    return hour + String.timeH + min + String.timeM;
};

/**
 * 时间转换，不做国际化
 * 由浮点型转换成 h m 型
 * @param time
 */
TimeUtil.formatHMWithoutLanguage = function (time, totalHours) {

    time = parseInt(time);
    if (totalHours)
        totalHours = parseInt(totalHours);

    if (time >= 2 * 3600 && totalHours == 25) {

        time = time - 3600;
    }
    if (time >= 2 * 3600 && totalHours == 23) {

        time = time + 3600;
    }

    var hour = parseInt(time / 3600);
    var min = parseInt((time - hour * 3600 ) / 60);
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (min < 10) {
        min = "0" + min;
    }
    return hour + "h " + min + "m";
};

/**
 * 秒数转为上午下午的时间
 * 由浮点型转换成 h m 型
 * @param time
 */
TimeUtil.formatWithNoon = function (time, totalHours) {

    time = parseInt(time);
    if (totalHours)
        totalHours = parseInt(totalHours);

    if (time >= 2 * 3600 && totalHours == 25) {

        time = time - 3600;
    }
    if (time >= 2 * 3600 && totalHours == 23) {

        time = time + 3600;
    }

    var hour = parseInt(time / 3600);
    var min = parseInt((time - hour * 3600 ) / 60);  // Math.round是小数四舍五入的方法
    var noon = String.timeAM;
    if (hour > 12) {
        noon = String.timePM;
        hour = hour - 12;
    }
    if (hour == 12) {
        noon = String.timePM;
    }
    if (hour == 0) {
        hour = 12;
    }
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (min < 10) {
        min = "0" + min;
    }
    return hour + ":" + min + " " + noon;
};

/**
 * 时间转换成使用":"
 * 由浮点型转换成 06：06 型
 * @param time
 */
TimeUtil.format = function (time) {

    var hour = parseInt(time / 3600);
    var min = parseInt((time - hour * 3600 ) / 60);  // Math.round是小数四舍五入的方法
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (min < 10) {
        min = "0" + min;
    }
    return hour + ":" + min;
};

/**
 * 时间转换成使用":",包含秒
 * 由浮点型转换成 06：06 型
 * @param time
 */
TimeUtil.formatWithSecond = function (time) {

    var hour = parseInt(time / 3600);
    var min = parseInt((time - hour * 3600 ) / 60);  // Math.round是小数四舍五入的方法
    var second = time - hour * 3600 - min * 60;
    if (hour < 10) {
        hour = "0" + hour;
    }
    if (min < 10) {
        min = "0" + min;
    }
    if (second < 10) {
        second = "0" + second;
    }
    return hour + ":" + min + ":" + second;
};

/**
 * date.formatHM("ddd mmm dd") Thu,Nov 19
 *
 * date.formatHM("ddd,mmm dd yyyy") Thu,Nov 19 2015
 */
var dateFormat = function () {

    var token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
        timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
        timezoneClip = /[^-+\dA-Z]/g, pad = function (val, len) {

            val = String(val);
            len = len || 2;
            while (val.length < len)
                val = "0" + val;
            return val;
        };

    // Regexes and supporting functions are cached through closure
    return function (date, mask, utc) {

        var dF = dateFormat;

        // You can't provide utc if you skip other args (use the "UTC:" mask prefix)
        if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]"
            && !/\d/.test(date)) {
            mask = date;
            date = undefined;
        }

        // Passing date through Date applies Date.parse, if necessary
        date = date ? new Date(date) : new Date;
        if (isNaN(date))
            throw SyntaxError("invalid date");

        mask = String(dF.masks[mask] || mask || dF.masks["default"]);

        // Allow setting the utc argument via the mask
        if (mask.slice(0, 4) == "UTC:") {
            mask = mask.slice(4);
            utc = true;
        }

        var _ = utc ? "getUTC" : "get", d = date[_ + "Date"](), D = date[_ + "Day"](), m = date[_
        + "Month"](), y = date[_ + "FullYear"](), H = date[_ + "Hours"](), M = date[_
        + "Minutes"](), s = date[_ + "Seconds"](), L = date[_ + "Milliseconds"](), o = utc ? 0
            : date.getTimezoneOffset(), flags = {
            d: d,
            dd: pad(d),
            ddd: dF.i18n.dayNames[D],
            dddd: dF.i18n.dayNames[D + 7],
            m: m + 1,
            mm: pad(m + 1),
            mmm: dF.i18n.monthNames[m],
            mmmm: dF.i18n.monthNames[m + 12],
            yy: String(y).slice(2),
            yyyy: y,
            h: H % 12 || 12,
            hh: pad(H % 12 || 12),
            H: H,
            HH: pad(H),
            M: M,
            MM: pad(M),
            s: s,
            ss: pad(s),
            l: pad(L, 3),
            L: pad(L > 99 ? Math.round(L / 10) : L),
            t: H < 12 ? "a" : "p",
            tt: H < 12 ? "am" : "pm",
            T: H < 12 ? "A" : "P",
            TT: H < 12 ? "AM" : "PM",
            Z: utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip,
                ""),
            o: (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
            S: ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
        };

        return mask.replace(token, function ($0) {

            return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
        });
    };
}();

// Some common formatHM strings
dateFormat.masks = {
    "default": "ddd mmm dd yyyy HH:MM:ss",
    shortDate: "m/d/yy",
    mediumDate: "mmm d, yyyy",
    longDate: "mmmm d, yyyy",
    fullDate: "dddd, mmmm d, yyyy",
    shortTime: "h:MM TT",
    mediumTime: "h:MM:ss TT",
    longTime: "h:MM:ss TT Z",
    isoDate: "yyyy-mm-dd",
    isoTime: "HH:MM:ss",
    isoDateTime: "yyyy-mm-dd'T'HH:MM:ss",
    isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};

// Internationalization strings
dateFormat.i18n = {
    dayNames: ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sunday", "Monday", "Tuesday",
        "Wednesday", "Thursday", "Friday", "Saturday"],
    monthNames: ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
        "Dec", "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"]
};

// For convenience...
Date.prototype.formatHM = function (mask, utc) {

    return dateFormat(this, mask, utc);
};