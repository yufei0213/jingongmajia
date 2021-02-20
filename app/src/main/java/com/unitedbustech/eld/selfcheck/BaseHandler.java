package com.unitedbustech.eld.selfcheck;

import com.unitedbustech.eld.http.HttpRequest;

/**
 * @author yufei0213
 * @date 2018/4/17
 * @description SelfCheck 故障和诊断处理基类
 */
public class BaseHandler {

    protected boolean hasInit;
    protected boolean isProduce;

    protected HttpRequest getMalFunctionRequest;
}
