/**
 * 工具类和拓展类
 */

/**
 * 方法拓展，用于动态添加方法
 * @param name 方法名
 * @param fn Function
 */
Function.prototype.addMethod = function (name, fn) {

    if (fn instanceof Function) {

        this[name] = fn;
    } else {

        throw new Error("fn must be function");
    }
};

/**
 * 给函数原型增加一个extend函数，实现继承
 * @param superClass 超类
 * @returns {Function}
 */
Function.prototype.extends = function (superClass) {

    if (typeof superClass === "function") {//类式继承

        var F = function () {
        }; //创建一个中间函数对象以获取父类的原型对象
        F.prototype = superClass.prototype; //设置原型对象
        this.prototype = new F(); //实例化F, 继承父类的原型中的属性和方法，而无需调用父类的构造函数实例化无关的父类成员
        this.prototype.constructor = this; //设置构造函数指向自己
    } else if (typeof superClass === "object") { //方法的扩充

        var pro = this.prototype;
        for (var k in superClass) {

            if (!pro[k]) { //如果原型对象不存在这个属性，则复制

                pro[k] = superClass[k];
            }
        }
    } else {

        throw new Error("fatal error:\"Function.prototype.extends\" expects a function or object");
    }

    return this;
};

/**
 *
 * @param func 注册的方法
 * @param thisValue 方法作用域
 * @constructor
 */
var EventHandler = function (func, funcScope) {

    this.func = func;
    this.funcScope = funcScope;
};

/**
 * 组件基类
 * @constructor
 */
var BaseWidget = function () {

};

/**
 * 注册EventHandler
 * @param type EventHandler类型
 * @param handler EventHandler
 */
BaseWidget.prototype.on = function (type, handler) {

    if (!handler instanceof EventHandler) {

        throw new Error("handler must be a EventHandler");
    }

    if (!this.handlers) {

        this.handlers = {};
    }

    if (!this.handlers[type]) {

        this.handlers[type] = [];
    }

    this.handlers[type].push(handler);
};

/**
 * 触发EventHandler
 * @param type EventHandler type
 * @param data 传递给EventHandler Function的数据
 */
BaseWidget.prototype.fire = function (type, data) {

    if (!this.handlers) {

        return;
    }

    if (this.handlers[type] instanceof Array) {

        var handlers = this.handlers[type];
        for (var i = 0, len = handlers.length; i < len; i++) {

            var handler = handlers[i];
            handler.func.call(handler.funcScope, data);
        }
    }
};

/**
 * 解注册EventHandler
 * @param type EventHandler类型
 * @param handler EventHandler
 */
BaseWidget.prototype.off = function (type, handler) {

    if (!this.handlers) {

        return;
    }

    if (this.handlers[type] instanceof Array) {

        var handlers = this.handlers[type];
        for (var i = 0, len = handlers.length; i < len; i++) {

            if (handlers[i] == handler) {

                handlers.splice(i, 1);
                break;
            }
        }
    }
};

/**
 * 从url上取参数
 * @param name 参数名
 * @return {null} 值
 */
Window.prototype.getUrlParam = function (name) {

    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {

        return unescape(r[2]);
    }

    return null;
};

/**
 * 加载js到body底部
 * @param url 相对路径
 * @param callback 加载完成的回调
 */
Window.prototype.loadScript = function (url, callback) {

    var script = document.createElement("script");
    script.type = "text/javascript";

    if (callback) {

        if (script.readyState) {

            script.onreadystatechange = function () {

                if (script.readyState == "loaded" || script.readyState == "complete") {

                    script.onreadystatechange = null;
                    callback();
                }
            };
        } else {

            script.onload = function () {

                callback();
            };
        }
    }

    script.src = url;
    document.body.appendChild(script);
};

/**
 * 获取屏幕像素比
 * @param context canvas 上下文
 * @return {number}
 */
Window.prototype.getPixelRatio = function (context) {

    var backingStore = context.backingStorePixelRatio
        || context.webkitBackingStorePixelRatio
        || context.mozBackingStorePixelRatio
        || context.msBackingStorePixelRatio
        || context.oBackingStorePixelRatio
        || context.backingStorePixelRatio || 1;

    return (window.devicePixelRatio || 1) / backingStore;
};

/**
 * 拓展方法，生成随机字符串(不包含数字)
 * @param len 默认为4
 */
String.random = function (len) {

    var chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    var length = len || 4;
    var result = "";

    for (var i = 0; i < length; i++) {

        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }

    return result;
};

/**
 * LocalStorage管理类
 * @constructor
 */
var LocalStorage = function () {

};

/**
 * 存值
 * @param key 主键
 * @param value 值
 */
LocalStorage.setItem = function (key, value) {

    window.localStorage.setItem(key, value);
};

/**
 * 获取值
 * @param key 主键
 */
LocalStorage.getItem = function (key) {

    return window.localStorage.getItem(key);
};

/**
 * 移除值
 * @param key 主键
 */
LocalStorage.removeItem = function (key) {

    window.localStorage.removeItem(key);
};

/**
 * 图片工具类
 * @constructor
 */
var ImageUtil = function () {

};

/**
 * 压缩Base64字符串
 * @param base64 BASE64字符串
 * @param callback 回调函数
 * @param ratio 压缩比
 */
ImageUtil.zipBase64 = function (base64, callback, ratio) {

    ratio = ratio || 0.5;

    var image = new Image();
    image.onload = function () {

        image.width = image.width * ratio;
        image.height = image.height * ratio;

        var canvas = document.createElement('canvas');
        var ctx = canvas.getContext("2d");

        ctx.clearRect(0, 0, canvas.width, canvas.height);
        canvas.width = image.width;
        canvas.height = image.height;

        ctx.drawImage(image, 0, 0, image.width, image.height);

        callback(canvas.toDataURL());
    };
    image.src = base64;
}
;