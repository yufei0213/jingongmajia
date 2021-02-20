package com.unitedbustech.eld.message;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.unitedbustech.eld.util.ThreadUtil;

/**
 * @author yufei0213
 * @date 2018/2/5
 * @description ELD消息推送服务
 */
public class ELDInstanceIDService extends FirebaseInstanceIdService {

    public ELDInstanceIDService() {
        super();
    }

    @Override
    public void onTokenRefresh() {

        super.onTokenRefresh();
        ThreadUtil.getInstance().execute(new UploadFireBaseToken());
    }
}
