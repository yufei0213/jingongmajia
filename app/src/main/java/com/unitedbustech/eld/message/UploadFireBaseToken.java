package com.unitedbustech.eld.message;

import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;

/**
 * @author yufei0213
 * @date 2018/2/5
 * @description 上报服务器本机FireBase Token
 */
public class UploadFireBaseToken implements Runnable {

    private static final String TAG = "UploadFireBaseToken";

    @Override
    public void run() {

        String fireBaseToken = FirebaseInstanceId.getInstance().getToken();
        User user = SystemHelper.getUser();
        if (user == null || TextUtils.isEmpty(fireBaseToken)) {

            Logger.w(Tags.MSG, "user = null || fireBaseToken = " + fireBaseToken);
            return;
        }

        String accessToken = user.getAccessToken();

        HttpRequest request = new HttpRequest.Builder()
                .url(Constants.API_FIREBASE_TOKEN)
                .addParam("access_token", accessToken)
                .addParam("token", fireBaseToken)
                .build();

        HttpResponse httpResponse = request.post();
        if (httpResponse.isSuccess()) {

            Logger.i(Tags.MSG, "upload FireBase Token success: fire_base_token=" + fireBaseToken);
        } else {

            Logger.w(Tags.MSG, "upload FireBase Token failed: code=" + httpResponse.getCode() +
                    ", msg=" + httpResponse.getMsg() +
                    ", fire_base_token=" + fireBaseToken);
        }
    }
}
