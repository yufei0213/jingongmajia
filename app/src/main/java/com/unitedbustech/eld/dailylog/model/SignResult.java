package com.unitedbustech.eld.dailylog.model;

/**
 * @author zhangyu
 * @date 2018/4/19
 * @description
 * 签名结果
 */
public class SignResult {

    /**
     * 是否有签名
     */
    private boolean hasSign;

    /**
     * 签名数据
     */
    private String sign;

    public SignResult(boolean hasSign, String sign) {
        this.hasSign = hasSign;
        this.sign = sign;
    }

    public boolean isHasSign() {
        return hasSign;
    }

    public void setHasSign(boolean hasSign) {
        this.hasSign = hasSign;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
