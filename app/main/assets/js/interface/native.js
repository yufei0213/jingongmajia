/**
 * @author yufei0213
 * @date 2017/12/07
 * @description 页面相关
 */

/**
 * 提供给java调用的类
 * @constructor
 */
var Global = function () {

};

/**
 * 解决部分机型多次加载的问题
 * @type {boolean}
 */
Global.isInit = false;

/**
 * 是否是web调试模式
 * @type {boolean}
 */
Global.isWebDebug = false;

/**
 * 全局按钮快速点击处理
 * @type {boolean}
 */
Global.isButtonProcessing = false;

/**
 * client类型
 * @type {boolean}
 */
Global.isAndroid = false;
Global.isIPhoneX = false;
var u = navigator.userAgent;
if (u.indexOf('Android') > -1 || u.indexOf('Linux') > -1) {

    Global.isAndroid = true;
}
if (u.indexOf('iPhone X') > -1) { //iphoneX

    Global.isIPhoneX = true;
}

/**
 * 初始化
 * @param params 父页面传递过来的参数，可能为空
 */
Global.init = function (params) {

    if (Global.isInit) {

        return;
    } else {

        Global.isInit = true;
    }

    if (Global.isIPhoneX) {

        $("#header").addClass("pdt45");
    } else if (!Global.isAndroid) {

        $("#header").addClass("pdt22");
    }

    if (!this.isWebDebug) {

        this.initOthers();
    }

    if (typeof (Web.init) === "function") {

        if (params) {

            params = JSON.parse(params);
        }

        if (params && params.backType) {

            Web.backResult = params.backType;
        } else {

            Web.backResult = Constants.WEB_BACK_NO_RESULT;
        }

        Web.init(params);
    }
};

/**
 * onPause后，页面再次可用时调用
 * @param params 子页面传递过来的参数，可能为空
 */
Global.onReload = function (params) {

    if (typeof (Web.onReload) === "function") {

        if (params) {

            params = JSON.parse(params);
        }

        Web.onReload(params);
    }
};

/**
 * 页面不可用
 */
Global.onPause = function () {

    if (typeof (Web.onPause) === "function") {

        Web.onPause();
    }
};

/**
 * 页面销毁
 */
Global.onDestroy = function () {

    if (typeof (Web.onDestroy) === "function") {

        Web.onDestroy();
    }
};

/**
 * 左上角按钮被点击
 */
Global.onLeftBtnClick = function () {

    if (typeof (Web.onLeftBtnClick) === "function") {

        Web.onLeftBtnClick();
    } else {

        SDK.back();
    }
};

/**
 * HosModel发生变化
 */
Global.hosModelChangeMessage = function () {

    if (typeof (Web.hosModelChangeMessage) === "function") {

        Web.hosModelChangeMessage();
    }
};

/**
 * 车辆选择状态发生变化
 */
Global.vehicleSelectChange = function (vehicle) {

    if (typeof (Web.vehicleSelectChange) === "function") {

        if (vehicle) {

            vehicle = JSON.parse(vehicle);
        }

        Web.vehicleSelectChange(vehicle);
    }
};

/**
 * 车辆连接状态变化
 */
Global.vehicleConnectStateChange = function (code) {

    if (typeof (Web.vehicleConnectStateChange) === "function") {

        Web.vehicleConnectStateChange(code);
    }
};

/**
 * 团队工作状态发生变化
 * @param data 团队工作相关数据
 */
Global.teamWorkDashBoardChange = function (data) {

    if (typeof (Web.teamWorkDashBoardChange) === "function") {

        if (data) {

            data = JSON.parse(data);
        }

        Web.teamWorkDashBoardChange(data);
    }
};

/**
 * 是否存在故障检测
 */
Global.malFunctionRefresh = function (isShow) {

    if (typeof (Web.malFunctionRefresh) === "function") {

        Web.malFunctionRefresh(isShow);
    }
};

/**
 * 是否存在Alerts
 */
Global.alertsRefresh = function (data) {

    if (typeof (Web.alertsRefresh) === "function") {

        if (data) {

            data = JSON.parse(data);
        }

        Web.alertsRefresh(data);
    }
};

/**
 * 是否存在故障检测
 */
Global.networkStateChange = function (isConnected) {

    if (typeof (Web.networkStateChange) === "function") {

        Web.networkStateChange(isConnected);
    }
};

/**
 * 在调用Web.init之前调用
 */
Global.initOthers = function () {

    if (!Global.isAndroid) {

        // 消除ios WebView 200ms保护点击机制
        loadScript("../../js/utils/fastclick.js", function () {

            window.addEventListener('load', function () {

                FastClick.attach(document.body);
            }, false);
        });

        //显示 button 选择的样式
        showButtonActiveBackgroundColor();

        // ios 关闭键盘问题处理全局键盘关闭处理
        endKeyboardSetting();
    } else {

        $("input,textarea").focus(function () {

            $(this).parent().parent().parent().get(0).scrollIntoView(false);
        });
        $(window).resize(function () {

            $("input:focus,textarea:focus").parent().parent().parent().get(0).scrollIntoView(false);
        });
    }

    //禁用掉全部回车事件
    $("textarea").each(function () {

        $(this).bind('keydown', function (event) {

            var e = event || window.event;
            if (!e.ctrlKey && e.keyCode == 13) {

                //这句话阻止原有的回车换行事件的冒泡执行
                return false;
            }
        });
    });
};

/**
 * 页面声明周期函数
 * @constructor
 */
var Web = function () {

};

/**
 * 初始化签名面板
 * 当某个页面有签名面板时，在Web.init方法中调用此方法
 * canvas父元素必须有 'signCanvasBox' ID，canvas 必须有 'signCanvas' ID，canvas的宽高为父元素的宽高
 * 这样处理是为了兼容某些机型给面板设置宽高无效的Bug，多数是三星和谷歌亲儿子
 */
Web.initSignCanvas = function () {

    var signCanvasBox = document.getElementById("signCanvasBox");
    var signCanvas = document.getElementById("signCanvas");
    signCanvas.width = signCanvasBox.offsetWidth;
    signCanvas.height = signCanvasBox.offsetHeight;
};

/**
 * 用于java调用的回调函数
 * @constructor
 */
var Callback = function () {

};

/**
 * 添加回调方法
 * @param fn function
 * @return 字符串表示的function
 */
Callback.addMethod = function (fn) {

    var name = String.random();
    if (Callback[name]) {

        Callback.addMethod(fn);
    } else {

        Callback[name] = fn;
    }

    return "Callback." + name;
};

/**
 * iOS 桥接
 * @constructor
 */
var OcBridge = function () {

};

/**
 * 用于iOS
 * @type {boolean}
 */
OcBridge.isWebView = false;

/**
 * iOS 桥接函数
 * @param callback
 */
OcBridge.connectWebViewJavascriptBridge = function (callback) {

    if (window.WebViewJavascriptBridge) {

        callback(WebViewJavascriptBridge);
    } else {

        document.addEventListener('WebViewJavascriptBridgeReady', function () {

            callback(WebViewJavascriptBridge)
        }, false);
    }
}(function (bridge) {

    //如果是web调试模式，不执行
    if (Global.isWebDebug) {

        return;
    }

    // 错误日志打印
    window.onerror = function (err) {

        SDK.log('window.onerror: ' + err);
    };

    if (bridge) {

        OcBridge.isWebView = true;
    }

    OcBridge.callOcFunc2 = function (interface, params) {

        bridge.callHandler(interface, params);
    };
});

/**
 * 调用iOS原生方法
 * @param interface 接口分类
 * @param params 参数
 */
OcBridge.callOcFunc = function (interface, params) {

    window.webkit.messageHandlers[interface].postMessage(params);
};

/**
 * 调试模式使用
 */
OcBridge.callOcFunc2 = function () {

};

/**
 * 调用原生方法
 * @param interface 接口分类
 * @param method 方法名
 * @param data 数据
 * @param callBack 回调
 */
var callFunction = function (interface, method, data, callBack) {

    var params = {

        method: method,
        data: data ? data : {}
    };

    if (callBack) {

        params.data.callBack = Callback.addMethod(callBack);
    }

    if (Global.isAndroid) {

        window[interface].method(JSON.stringify(params));
    } else {

        if (OcBridge.isWebView) {

            OcBridge.callOcFunc2(interface, JSON.stringify(params));
        } else {

            OcBridge.callOcFunc(interface, JSON.stringify(params));
        }
    }
};

//支持web调试模式
$(function () {

    if (Global.isWebDebug) {

        Global.accessToken = "df95a74b928e4fa182a87e975d42e88e";
        Global.webDataKey = "web_data_key";

        loadScript("../../js/test/test_data.js", function () {

            var params = getUrlParam("params");

            Global.init(params);

            var data = LocalStorage.getItem(Global.webDataKey);
            LocalStorage.removeItem(Global.webDataKey);

            Global.onReload(data);
        });
    }
});

// ios 关闭键盘问题处理全局键盘关闭处理
var endKeyboardSetting = function () {

    /**
     * 阻止input点击向下传递
     */
    $('input').click(function () {

        return false;
    });
    $('textarea').click(function () {

        return false;
    });

    /**
     * 关闭键盘
     */
    $('.pages').click(function () {

        document.activeElement.blur();
    });
};


/**
 * 特殊字符串处理
 */
var SpecialCharDeal = {};

SpecialCharDeal.charSingleQuotes = "#SingleQuotes#";
SpecialCharDeal.charBackslash = "<br>";
SpecialCharDeal.charGt = "&gt;";
SpecialCharDeal.charLt = "&lt;";

/**
 * 将特殊字符替换为特定字符
 */
SpecialCharDeal.deal = function (str) {

    // 替换所有尖括号
    str = str.replace(/</g, SpecialCharDeal.charGt);
    str = str.replace(/>/g, SpecialCharDeal.charLt);
    // 现将\n替换为<br>
    str = str.replace(/\n/g, SpecialCharDeal.charBackslash);
    // 将单引号替换
    str = str.replace(/'/g, SpecialCharDeal.charSingleQuotes);

    return str;
};

/**
 * 还原特殊字符串
 */
SpecialCharDeal.restore = function (str) {

    // 将单引号还原 SpecialCharDeal.charSingleQuotes
    str = str.replace(/#SingleQuotes#/g, "'");

    return str;
};

//ios 页面 button 点击无选中状态
var showButtonActiveBackgroundColor = function () {
    var a = document.getElementsByTagName('body');

    for (var i = 0; i < a.length; i++) {

        a[i].addEventListener('touchstart', function () {
        }, false);

    }
}