/**
 * @author yufei0213
 * @date 2018/2/27
 * @description 点击事件的代理方法
 */
var ClickProxy = function (func, funcScope) {

    this.CLICK_DURATION = 1000;

    this.lastClickTime = 0;

    this.func = func;
    this.funcScope = funcScope;
};

/**
 * 点击事件代理方法
 * @param func 方法
 * @param funcScope 方法作用域
 * @return false 禁用事件冒泡
 */
ClickProxy.prototype.click = function (event) {

    if ((new Date().getTime() - this.lastClickTime) > this.CLICK_DURATION) {

        this.func.call(this.funcScope, event);
        this.lastClickTime = new Date().getTime();
    }
};