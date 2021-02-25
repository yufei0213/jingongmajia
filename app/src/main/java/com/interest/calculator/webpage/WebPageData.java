package com.interest.calculator.webpage;

public class WebPageData {

    private String title; // 标题
    private String url; // 加载的地址
    private boolean hasTitleBar; // 是否显示标题栏
    private boolean rewriteTitle; // 是否通过加载的Web重写标题
    private String stateBarTextColor; // 状态栏字体颜色 black|white
    private String titleTextColor; // 标题字体颜色
    private String titleColor; // 状态栏和标题背景色
    private String postData; // webView post方法时会用到
    private String html; // 加载htmlCode（例如：<body></body>）,
    private boolean webBack; // true:web回退(点击返回键webview可以回退就回退，无法回退的时候关闭该页面)|false(点击返回键关闭该页面) 直接关闭页面

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isHasTitleBar() {
        return hasTitleBar;
    }

    public void setHasTitleBar(boolean hasTitleBar) {
        this.hasTitleBar = hasTitleBar;
    }

    public boolean isRewriteTitle() {
        return rewriteTitle;
    }

    public void setRewriteTitle(boolean rewriteTitle) {
        this.rewriteTitle = rewriteTitle;
    }

    public String getStateBarTextColor() {
        return stateBarTextColor;
    }

    public void setStateBarTextColor(String stateBarTextColor) {
        this.stateBarTextColor = stateBarTextColor;
    }

    public String getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(String titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public String getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    public String getPostData() {
        return postData;
    }

    public void setPostData(String postData) {
        this.postData = postData;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean isWebBack() {
        return webBack;
    }

    public void setWebBack(boolean webBack) {
        this.webBack = webBack;
    }
}
