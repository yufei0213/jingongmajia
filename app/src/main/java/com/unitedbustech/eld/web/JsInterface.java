package com.unitedbustech.eld.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yufei0213
 * @date 2017/12/8
 * @description 注册到WebView的接口
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsInterface {

    String name() default "";
}
