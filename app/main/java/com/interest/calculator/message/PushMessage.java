package com.interest.calculator.message;

public class PushMessage {

    private long createTime; // long 推送的时间
    private String pushTopic; //string 推送的标题
    private String pushContent; // string 展示推送的内容文本
    private String url; // string 点击推送需要跳转的Url

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPushTopic() {
        return pushTopic;
    }

    public void setPushTopic(String pushTopic) {
        this.pushTopic = pushTopic;
    }

    public String getPushContent() {
        return pushContent;
    }

    public void setPushContent(String pushContent) {
        this.pushContent = pushContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
