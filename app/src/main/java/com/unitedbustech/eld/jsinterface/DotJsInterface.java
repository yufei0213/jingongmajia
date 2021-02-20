package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DotMode;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.vo.DailyLogDataHeadVo;
import com.unitedbustech.eld.common.vo.DotReviewVo;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.dot.DotActivity;
import com.unitedbustech.eld.eventcenter.enums.DDLStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.DotOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.DotStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.eventcenter.model.SelfCheckModel;
import com.unitedbustech.eld.hos.core.HosHandler;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.DotReviewEventModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.EventUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.TabMenuDot;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author mamw
 * @date 2018/1/25
 * @description 与Dot Review有关的js接口
 */
@JsInterface(name = "dot")
public class DotJsInterface extends BaseJsInterface {

    private static final String TAG = "DotJsInterface";
    private static final int DOT_REVIEW_DATE_INTERVAL_AMERICA = 8;
    private static final int DOT_REVIEW_DATE_INTERVAL_CANADA = 14;

    private static final String DOT_INTERFACE_OPEN_DETAIL_PAGE = "openDetailPage";
    private static final String DOT_INTERFACE_GET_REVIEW_DATA_LIST = "getReviewDataList";
    private static final String DOT_INTERFACE_REQUEST_SEND_EMAIL = "requestSendEmail";
    private static final String DOT_INTERFACE_REQUEST_SEND_PDF_EMAIL = "requestSendPdf";
    private static final String DOT_INTERFACE_REQUEST_WEB_SERVICE = "requestWebService";
    private static final String DOT_INTERFACE_SELECT_REVIEW_TAB_ITEM = "selectReviewTabItem";

    public DotJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case DOT_INTERFACE_OPEN_DETAIL_PAGE:

                openDetailPage(data);
                break;
            case DOT_INTERFACE_GET_REVIEW_DATA_LIST:

                getReviewDataList(data);
                break;
            case DOT_INTERFACE_REQUEST_SEND_EMAIL:

                requestSendEmail(data);
                break;
            case DOT_INTERFACE_REQUEST_SEND_PDF_EMAIL:

                requestSendPdf(data);
                break;
            case DOT_INTERFACE_REQUEST_WEB_SERVICE:

                requestWebService(data);
                break;
            case DOT_INTERFACE_SELECT_REVIEW_TAB_ITEM:

                selectReviewTabItem();
                break;
            default:
                break;
        }
    }

    /**
     * 打开页面
     */
    private void openDetailPage(String data) {

        Intent intent = DotActivity.newIntent(context);
        context.startActivity(intent);
    }

    /**
     * 获取Dot Review数据
     *
     * @param data 回调函数
     */
    private void getReviewDataList(final String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                Date reportEndDate = TimeUtil.getDayBegin(new Date());

                final List<DotReviewVo> dotReviewVoList = new ArrayList<>();

                // 获取本地所有原始日志
                List<DailyLog> dailyLogList = DataBaseHelper.getDataBase().dailyLogDao().listAll();
                List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();

                int days = DOT_REVIEW_DATE_INTERVAL_AMERICA;
                if (DotMode.getModeType(HosHandler.getInstance().getRule().getId()) == DotMode.Canada_14_120) {

                    days = DOT_REVIEW_DATE_INTERVAL_CANADA;
                }
                for (int i = 0; i < days; i++) {

                    Date curDate = TimeUtil.getNextDate(reportEndDate, -i);
                    DotReviewVo dotReviewVo = new DotReviewVo();
                    dotReviewVo.setDatetime(curDate.getTime());
                    // 当前日期
                    dotReviewVo.setStandDateStr(TimeUtil.utcToLocal(curDate.getTime(),
                            SystemHelper.getUser().getTimeZone(),
                            TimeUtil.STANDARD_FORMAT));
                    dotReviewVo.setSwitchBarDateStr(TimeUtil.getDailylogFromat(curDate, false));
                    if (i >= hosDayModels.size()) {
                        continue;
                    }
                    // 表头信息
                    DailyLogDataHeadVo dailyLogDataHeadVo = hosDayModels.get(i).getProfile().getDailyLogDataHeadVo();
                    dotReviewVo.setDotReviewDataHeadVo(dailyLogDataHeadVo);
                    // 画图集合
                    dotReviewVo.setGridModelList(ModelCenter.getInstance().getGridData(curDate, null, null));
                    // 事件集合
                    List<DotReviewEventModel> eventList = getDotReviewEvent(curDate, dailyLogList);//查库
                    for (int j = 0; j < eventList.size(); j++) {

                        DotReviewEventModel event = eventList.get(j);
                        if (event.getOdometer().equals("0")) {

                            event.setOdometer("");
                        }
                        if (event.getEngineHour().equals("0")) {

                            event.setEngineHour("");
                        }
                    }
                    dotReviewVo.setDotReviewEventModelList(eventList);
                    dotReviewVoList.add(dotReviewVo);
                }

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String retStr = JsonUtil.toJsJSONString(dotReviewVoList);
                        uiWebView.loadUrl("javascript:" + callback + "('" + retStr + "');");
                    }
                });
            }
        });
    }

    /**
     * 根据日期获取一天的全部日志Dot Review用
     *
     * @param date 日期
     * @return 日期事件汇总
     */
    private List<DotReviewEventModel> getDotReviewEvent(Date date, List<DailyLog> dailyLogList) {

        List<DotReviewEventModel> result = new ArrayList<>();

        for (DailyLog dailyLog : dailyLogList) {

            EventModel eventModel = JsonUtil.parseObject(dailyLog.getJson(), EventModel.class);

            //过滤无效的工作状态事件
            if ((eventModel.getType() == EventItem.DRIVER_WORK_STATE.getCode() && eventModel.getStatus() != DDLStatusEnum.ACTIVE.getCode()) ||
                    (eventModel.getType() == EventItem.EXEMPTION_MODE.getCode())) {

                continue;
            }

            if (TimeUtil.isSameDay(date, new Date(eventModel.getDatetime()))) {

                // 筛选事件
                DotStatusEnum dotStatusEnum = DotStatusEnum.parse(eventModel.getType(), eventModel.getCode());
                DotOriginEnum dotOriginEnum = DotOriginEnum.parse(JsonUtil.getInt(JsonUtil.parseObject(dailyLog.getJson()), "origin"));
                if (dotStatusEnum == null) {
                    continue;
                }

                DotReviewEventModel dotReviewEventModel = new DotReviewEventModel();
                dotReviewEventModel.setTime(TimeUtil.utcToEnLocal(eventModel.getDatetime(),
                        SystemHelper.getUser().getTimeZone(),
                        TimeUtil.HH_MM_AA));
                dotReviewEventModel.setLocation(JsonUtil.getString(JsonUtil.parseObject(dailyLog.getJson()), "location"));

                String totalOdometerStr = JsonUtil.getString(JsonUtil.parseObject(dailyLog.getJson()), "totalOdometer");
                if (totalOdometerStr.equals("0")) {

                    dotReviewEventModel.setOdometer("");
                } else {

                    dotReviewEventModel.setOdometer(totalOdometerStr);
                }

                String totalEngineHoursStr = JsonUtil.getString(JsonUtil.parseObject(dailyLog.getJson()), "totalEngineHours");
                if (totalOdometerStr.equals("0")) {

                    dotReviewEventModel.setEngineHour("");
                } else {

                    dotReviewEventModel.setEngineHour(totalEngineHoursStr);
                }

                //修改，根据code和type获取
                if (eventModel.getType() == EventItem.SELF_CHECK.getCode()) {

                    SelfCheckModel selfCheckModel = JsonUtil.parseObject(dailyLog.getJson(), SelfCheckModel.class);
                    if (selfCheckModel != null) {

                        String abnormalCode = selfCheckModel.getAbnormalCode();
                        dotReviewEventModel.setType(EventUtil.getSelfCheckModelTitle(eventModel.getCode(), abnormalCode, true));
                    }
                } else {

                    dotReviewEventModel.setType(EventUtil.getHosTitle(eventModel.getType(), eventModel.getCode(), true));
                }

                if (dotOriginEnum == null) {

                    dotReviewEventModel.setOrigin("");
                } else {

                    dotReviewEventModel.setOrigin(dotOriginEnum.getDesc());
                }
                dotReviewEventModel.setRemark(JsonUtil.getString(JsonUtil.parseObject(dailyLog.getJson()), "comment"));

                dotReviewEventModel.setDateTime(JsonUtil.getLong(JsonUtil.parseObject(dailyLog.getJson()), "datetime"));
                result.add(dotReviewEventModel);
            }
        }

        // 排序设置序列号
        Collections.sort(result);
        int eventIndex = 1;
        for (DotReviewEventModel dotReviewEventModel : result) {

            dotReviewEventModel.setId(eventIndex++);
        }
        return result;
    }

    /**
     * 请求发送邮件
     *
     * @param data 请求参数
     */
    private void requestSendEmail(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String email = jsonObject.getString("email");
        final String comment = jsonObject.getString("comment");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                // 获取最新车辆信息
                VehicleDataModel dataModel = DataCollectorHandler.getInstance().getDataModel();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_REQUEST_DOT_SEND_EMAIL)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", String.valueOf(user.getVehicleId()))
                        .addParam("odometer", String.valueOf(dataModel.getTotalOdometer()))
                        .addParam("eng_hour", String.valueOf(dataModel.getTotalEngineHours()))
                        .addParam("longitude", String.valueOf(dataModel.getLongitude()))
                        .addParam("latitude", String.valueOf(dataModel.getLatitude()))
                        .addParam("time", String.valueOf(new Date().getTime()))
                        .addParam("email", email)
                        .addParam("comment", TextUtils.isEmpty(comment) ? "" : comment)
                        .addParam("mailType", "0")
                        .build();

                HttpResponse response = HttpRequest.post();

                if (response.isSuccess()) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求发送PDF
     *
     * @param data 请求参数
     */
    private void requestSendPdf(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
                String startDate = JsonUtil.getString(jsonObject, "startDate");
                String endDate = JsonUtil.getString(jsonObject, "endDate");
                String email = jsonObject.getString("email");
                String comment = jsonObject.getString("comment");

                User user = SystemHelper.getUser();

                //如果是加拿大规则，并且日期为空，则将开始日期置为今天，结束日期定位十五天前
                if (DotMode.getModeType(HosHandler.getInstance().getRule().getId()) == DotMode.Canada_14_120 && (TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate))) {

                    endDate = TimeUtil.utcToLocal(new Date().getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.US_FORMAT);
                    startDate = TimeUtil.getPastDate(Constants.DDL_DAYS - 1, TimeUtil.US_FORMAT, SystemHelper.getUser().getTimeZone());
                }

                // 获取最新车辆信息
                VehicleDataModel dataModel = DataCollectorHandler.getInstance().getDataModel();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_REQUEST_DOT_SEND_EMAIL)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", String.valueOf(user.getVehicleId()))
                        .addParam("odometer", String.valueOf(dataModel.getTotalOdometer()))
                        .addParam("eng_hour", String.valueOf(dataModel.getTotalEngineHours()))
                        .addParam("longitude", String.valueOf(dataModel.getLongitude()))
                        .addParam("latitude", String.valueOf(dataModel.getLatitude()))
                        .addParam("time", String.valueOf(new Date().getTime()))
                        .addParam("email", email)
                        .addParam("comment", TextUtils.isEmpty(comment) ? "" : comment)
                        .addParam("startDate", TextUtils.isEmpty(startDate) ? "" : startDate)
                        .addParam("endDate", TextUtils.isEmpty(endDate) ? "" : endDate)
                        .addParam("mailType", "1")
                        .build();

                HttpResponse response = HttpRequest.post();

                if (response.isSuccess()) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 请求Web Service
     *
     * @param data 请求参数
     */
    private void requestWebService(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final String comment = JsonUtil.getString(jsonObject, "comment");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                // 获取最新车辆信息
                VehicleDataModel dataModel = DataCollectorHandler.getInstance().getDataModel();

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_REQUEST_DOT_WEB_SERVICE)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", String.valueOf(user.getVehicleId()))
                        .addParam("odometer", String.valueOf(dataModel.getTotalOdometer()))
                        .addParam("eng_hour", String.valueOf(dataModel.getTotalEngineHours()))
                        .addParam("longitude", String.valueOf(dataModel.getLongitude()))
                        .addParam("latitude", String.valueOf(dataModel.getLatitude()))
                        .addParam("time", String.valueOf(new Date().getTime()))
                        .addParam("comment", comment)
                        .build();

                HttpResponse response = HttpRequest.post();

                if (response.isSuccess()) {
                    JSONObject object = JsonUtil.parseObject(response.getData());
                    final String submissionId = JsonUtil.getString(object, "submissionId");
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "','" + submissionId + "');");
                        }
                    });
                } else {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 打开Dot Review页面
     */
    private void selectReviewTabItem() {

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ((DotActivity) context).onTabSelect(TabMenuDot.TAB_REVIEW);
            }
        });
    }


}
