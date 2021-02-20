package com.unitedbustech.eld.eventcenter.core;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.eventcenter.model.HistoryModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/17
 * @description 事件数据的处理器。
 * 监听新事件，进行上报以及本地存储。
 * 如果发生上报异常会缓存队列等待定时上报。
 */
public class EventDataHandler {

    private static final String TAG = "EventDataHandler";

    /**
     * 同步上报事件
     *
     * @param eventModel 事件模型
     */
    public void uploadNotifySync(EventModel eventModel, Long dailyLogId) {

        List<EventModel> eventModels = new ArrayList<>();
        eventModels.add(eventModel);

        HttpRequest HttpRequest = new HttpRequest.Builder()
                .url(Constants.API_DDL_CREATE)
                .addParam("data", JsonUtil.toJSONString(eventModels))
                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                .build();

        RequestCacheService.getInstance().cachePost(HttpRequest, RequestType.DAILYLOG, dailyLogId);
    }

    /**
     * 同步上报事件
     *
     * @param eventModelList 事件列表
     * @param dailyLogIds    每个事件对应的本地数据库的Id
     */
    public void uploadNotifySync(List<EventModel> eventModelList, List<Long> dailyLogIds) {

        if (eventModelList.size() != dailyLogIds.size()) {

            Logger.w(Tags.EVENT, "event size != dailyLogId size");
            return;
        }

        String accessToken = SystemHelper.getUser().getAccessToken();

        List<HttpRequest> httpRequestList = new ArrayList<>();
        for (EventModel eventModel : eventModelList) {

            List<EventModel> eventModels = new ArrayList<>();
            eventModels.add(eventModel);

            HttpRequest httpRequest = new HttpRequest.Builder()
                    .url(Constants.API_DDL_CREATE)
                    .addParam("data", JsonUtil.toJSONString(eventModels))
                    .addParam("access_token", accessToken)
                    .build();

            httpRequestList.add(httpRequest);
        }

        RequestCacheService.getInstance().cachePost(httpRequestList, RequestType.DAILYLOG, dailyLogIds);
    }

    /**
     * 同步上报事件
     *
     * @param eventModelList 事件模型
     */
    public void uploadUnidentified(List<EventModel> eventModelList) {

        if (eventModelList != null && eventModelList.size() > 0) {

            HttpRequest HttpRequest = new HttpRequest.Builder()
                    .url(Constants.API_DDL_CREATE)
                    .addParam("data", JsonUtil.toJSONString(eventModelList))
                    .addParam("access_token", SystemHelper.getUser().getAccessToken())
                    .build();

            RequestCacheService.getInstance().cachePost(HttpRequest, RequestType.UNIDENTIFIED, 0L);
        }
    }

    /**
     * 同步上报未认领日志
     * @param historyModels 历史事件模型
     */
    public void uploadUnidentifiedHistory(List<HistoryModel> historyModels) {

        if (historyModels != null && historyModels.size() > 0) {

            HttpRequest httpRequest = new HttpRequest.Builder()
                    .url(Constants.API_UPLOAD_ECM)
                    .addParam("data", JsonUtil.toJSONString(historyModels))
                    .addParam("access_token", SystemHelper.getUser().getAccessToken())
                    .build();
            RequestCacheService.getInstance().cachePost(httpRequest, RequestType.ORIGINALHISROEY, 0L);
//            HttpResponse response = httpRequest.post();
//            if(response.isSuccess()) {
//
//                Logger.i(Tags.EVENT, "upload original history event successful.");
//            } else {
//
//                Logger.i(Tags.EVENT, "upload original history event failed.");
//            }
        }
    }
}
