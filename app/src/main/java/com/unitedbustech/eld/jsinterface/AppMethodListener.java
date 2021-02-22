package com.unitedbustech.eld.jsinterface;

public interface AppMethodListener {

    void takePortraitPicture(String callbackMethod);

    void showTitleBar(boolean visible);

    void shouldForbidSysBackPress(int forbid);

    void forbidBackForJS(int forbid, String methodName);
}
