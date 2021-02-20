package com.unitedbustech.eld.http;

import android.text.TextUtils;

import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yufei0213
 * @date 2018/1/15
 * @description http请求结果
 */
public class HttpResponse {

    private int code;
    private String msg;

    private String data;

    private HttpResponse(Builder builder) {

        this.code = builder.code;
        this.msg = builder.msg;

        this.data = builder.data;

        boolean isIgnore = false;
        for (Integer code : builder.ignoreCodes) {

            if (this.code == code) {

                isIgnore = true;
                break;
            }
        }

        if (!isIgnore) {

            if (this.code == RequestStatus.AUTHENTICATION_FAIL.getCode()) {

                Logger.w(Tags.SYSTEM, "token authentication failed, token=" + SystemHelper.getUser().getAccessToken());
                SystemHelper.sessionTimeout();
            }
        }
    }

    public boolean isSuccess() {

        return this.code == RequestStatus.SUCCESS.getCode();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getData() {
        return data;
    }

    public static class Builder {

        private int code;
        private String msg;

        private String data;

        private List<Integer> ignoreCodes;

        public Builder() {

            ignoreCodes = new ArrayList<>();
        }

        public Builder status(int code) {

            this.code = code;
            this.msg = RequestStatus.getMsg(code);
            return this;
        }

        public Builder data(String data) {

            if (!TextUtils.isEmpty(data)) {

                //删除数据中的回车和换行，替换为空格
                Pattern p = Pattern.compile("\\\\r|\\\\n");
                Matcher m = p.matcher(data);
                this.data = m.replaceAll(" ");
            }

            return this;
        }

        public Builder ignore(List<Integer> codes) {

            ignoreCodes.addAll(codes);
            return this;
        }

        public HttpResponse build() {

            return new HttpResponse(this);
        }
    }
}
