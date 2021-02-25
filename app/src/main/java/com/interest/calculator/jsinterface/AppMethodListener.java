package com.interest.calculator.jsinterface;

public interface AppMethodListener {

    void takePortraitPicture(String callbackMethod);

    void showTitleBar(boolean visible);

    void shouldForbidSysBackPress(int forbid);

    void forbidBackForJS(int forbid, String methodName);

    void openGoogle(String data);

    void openPayTm(String data);
}
