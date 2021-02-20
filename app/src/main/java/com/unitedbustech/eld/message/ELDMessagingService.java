package com.unitedbustech.eld.message;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.common.vo.TeamWorkMsgVo;
import com.unitedbustech.eld.eventbus.TeamWorkServiceEvent;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.system.UploadLogs;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

/**
 * @author yufei0213
 * @date 2018/2/5
 * @description ELD消息推送服务
 */
public class ELDMessagingService extends FirebaseMessagingService {

    private static final String TAG = "ELDMessagingService";

    public ELDMessagingService() {

        super();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        Logger.d(TAG, "received msg from " + remoteMessage.getFrom());
        Logger.d(TAG, "received msg " + data);

        int type = 0;
        try {

            type = Integer.parseInt(data.get("type"));
        } catch (Exception e) {

            Logger.w(Tags.MSG, "msg type unknown，api error");
        }

        Logger.d(TAG, "msg type " + type);

        if (!ActivityStack.getInstance().isService()) {

            return;
        }

        switch (type) {

            case MessageType.TEAM_WORK:

                String teamWorkMsgStr = data.get("data");

                Logger.i(TAG, "send msg to TeamWorkService: " + teamWorkMsgStr);

                TeamWorkMsgVo teamWorkMsgVo = JsonUtil.parseObject(teamWorkMsgStr, TeamWorkMsgVo.class);
                EventBus.getDefault().post(new TeamWorkServiceEvent(teamWorkMsgVo));
                break;
            case MessageType.SESSION_DIAGNOSTIC:

                Logger.i(TAG, "login on other device");

                SystemHelper.loginByOther();
                break;
            case MessageType.UPLOAD_LOGS:

                Logger.i(Tags.MSG, "upload logs to service");

                String uploadLogsData = data.get("data");
                ThreadUtil.getInstance().execute(new UploadLogs(uploadLogsData));
                break;
            default:
                break;
        }
    }

    @Override
    public void onMessageSent(String s) {

        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {

        super.onSendError(s, e);
    }

    @Override
    public void onDeletedMessages() {

        super.onDeletedMessages();
    }
}
