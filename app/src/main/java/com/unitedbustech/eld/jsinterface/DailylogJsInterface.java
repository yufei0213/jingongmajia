package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.activity.WebActivity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.AlertAssignedVo;
import com.unitedbustech.eld.common.vo.UnidentifiedEngineLogVo;
import com.unitedbustech.eld.dailylog.model.Profile;
import com.unitedbustech.eld.dailylog.model.SignResult;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.domain.entry.ProfileEntity;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.driving.DrivingActivity;
import com.unitedbustech.eld.eventbus.DailylogSignAlertEvent;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.hos.core.HosHandler;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.DailyLogsSummary;
import com.unitedbustech.eld.hos.model.GridModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.hos.model.HosEventModel;
import com.unitedbustech.eld.hos.model.HosEventModelVo;
import com.unitedbustech.eld.hos.violation.ViolationModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.request.SyncCallback;
import com.unitedbustech.eld.system.DataCacheService;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.EventUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangyu
 * @date 2018/1/20
 * @description 与日志有关的js接口
 */
@JsInterface(name = "dailylog")
public class DailylogJsInterface extends BaseJsInterface {

    private static final String TAG = "DailylogJsInterface";

    private static final String GET_GRID_DATA_BY_INDEX = "getGridDataByIndex";
    private static final String GET_TODAY_GRID = "getTodayGrid";
    private static final String GET_DAILY_LOG_SUMMARY = "getDailyLogSummary";
    private static final String GET_DAILY_LOG_DETAIL_BY_INDEX = "getDailyLogDetailByIndex";
    private static final String CHANGE_STATE = "changeStatus";
    private static final String GET_PROFILE = "getProfile";
    private static final String UPDATE_PROFILE = "updateProfile";
    private static final String GET_SIGN = "getSign";
    private static final String UPLOAD_SIGN = "uploadSign";
    private static final String GET_LOG_DETAIL = "getLogDetail";
    private static final String MODIFY_EVENT = "modifyEvent";
    private static final String NEW_EVENT = "newEvent";
    private static final String ADD_REMARK = "addRemark";
    private static final String GET_STATUS_BY_TIME = "getStatusByTime";
    private static final String GET_OUT_OF_LINE_EVENT = "getOutOfLineEvents";
    private static final String GET_PAST_DAY_EVENT_SECOND = "getPastDayEventSecond";
    private static final String GET_PAST_DAY_END_STATE = "getPastDayEndState";
    private static final String GET_TOMORROW_DRIVING_REMAIN_TIP = "getTomorrowDrivingRemainTip";
    private static final String SYNC_DDL = "syncDdl";
    private static final String HANDLE_UNIDENTIFIED = "hasUnidentified";
    private static final String OPEN_UNIDENTIFIED = "openUnidentified";

    public DailylogJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case GET_GRID_DATA_BY_INDEX:

                getGridDataByIndex(data);
                break;
            case GET_TODAY_GRID:

                getTodayGrid(data);
                break;
            case GET_DAILY_LOG_SUMMARY:

                getDailyLogSummary(data);
                break;
            case GET_DAILY_LOG_DETAIL_BY_INDEX:

                getDailyLogDetailByIndex(data);
                break;
            case CHANGE_STATE:

                changeStatus(data);
                break;
            case GET_SIGN:

                getSign(data);
                break;
            case GET_PROFILE:

                getProfile(data);
                break;
            case UPDATE_PROFILE:

                updateProfile(data);
                break;
            case UPLOAD_SIGN:

                uploadSign(data);
                break;
            case GET_LOG_DETAIL:

                getLogDetail(data);
                break;
            case MODIFY_EVENT:

                modifyEvent(data);
                break;
            case NEW_EVENT:

                newEvent(data);
                break;
            case ADD_REMARK:

                newRemark(data);
                break;
            case GET_STATUS_BY_TIME:

                getStatusByTime(data);
                break;
            case GET_OUT_OF_LINE_EVENT:

                getOutOfLineEvents(data);
                break;
            case GET_PAST_DAY_EVENT_SECOND:

                getPastDayEventSecond(data);
                break;
            case GET_PAST_DAY_END_STATE:

                getPastDayEndState(data);
                break;
            case GET_TOMORROW_DRIVING_REMAIN_TIP:

                getTomorrowDrivingRemainTip(data);
                break;
            case SYNC_DDL:

                syncDdl(data);
                break;
            case HANDLE_UNIDENTIFIED:

                hasUnidentified(data);
                break;
            case OPEN_UNIDENTIFIED:

                openUnidentified(data);
                break;
            default:
                break;
        }
    }

    /**
     * 通过日期获取图表数据
     *
     * @param data 回调函数
     */
    private void getGridDataByIndex(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final int index = JsonUtil.getInt(jsonObject, "index");

        List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
        if (index < 0 || index >= Constants.DDL_DAYS) {
            return;
        }
        final HosDayModel hosDayModel = hosDayModels.get(index);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                final List<GridModel> gridModels = ModelCenter.getInstance().getGridData(hosDayModel.getDate(), null, null);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(gridModels) + "');");
                    }
                });
            }
        });
    }

    /**
     * 通过今天的图表
     *
     * @param data 回调函数
     */
    private void getTodayGrid(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                Date now = new Date();
                final List<GridModel> gridModels = ModelCenter.getInstance().getGridData(now, null, null);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(gridModels) + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取日志的汇总。用于显示列表数据
     *
     * @param data 回调函数
     */
    private void getDailyLogSummary(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final List<DailyLogsSummary> summaries = HosHandler.getInstance().getCalculator().summary();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(summaries) + "');");
            }
        });
    }

    /**
     * 获取一天日志的详细数据
     *
     * @param data 回调函数，包含日期
     */
    private void getDailyLogDetailByIndex(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final int index = JsonUtil.getInt(jsonObject, "index");

        final List<HosEventModelVo> result = new ArrayList<>();

        List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
        if (!(index >= Constants.DDL_DAYS || index < 0)) {

            HosDayModel hosDayModel = hosDayModels.get(index);
            List<HosEventModel> events = new ArrayList<>();
            List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();
            hosDriveEventModels.addAll(hosDayModel.getHosDriveEventModels());

            ModelCenter.getInstance().resetDriveModels(hosDriveEventModels);
            events.addAll(hosDriveEventModels);

            events.addAll(hosDayModel.getHosEventModels());
            Collections.sort(events);

            for (HosEventModel hosEventModel : events) {

                result.add(new HosEventModelVo(hosEventModel));
            }
        }

        Collections.reverse(result);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(result) + "');");
            }
        });
    }

    /**
     * 切换状态
     *
     * @param data 回调函数，包含日期
     */
    private void changeStatus(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);

        final int state = JsonUtil.getInt(jsonObject, "state");
        final String location = JsonUtil.getString(jsonObject, "location");
        final String remark = JsonUtil.getString(jsonObject, "remark");
        final String latitude = JsonUtil.getString(jsonObject, "latitude");
        final String longitude = JsonUtil.getString(jsonObject, "longitude");

        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
        builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
        builder.location(location, latitude, longitude);
        builder.remark(remark);

        //如果当前正在Break，并且要切换的状态不是OFF，则取消Break
        if (ModelCenter.getInstance().getBreak() && state != DriverState.OFF_DUTY.getCode()) {

            ModelCenter.getInstance().setBreak(false);
        }

        EventCenter.getInstance().changeDriverState(DriverState.getStateByCode(state), builder.build());
    }

    /**
     * dailylog的表头获取接口
     *
     * @param data 回调函数
     */
    private void getProfile(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);

        int index = JsonUtil.getInt(jsonObject, "index");
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        if (index >= Constants.DDL_DAYS) {

            index = Constants.DDL_DAYS - 1;
        }

        HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
        final Profile profile = hosDayModel.getProfile();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(profile) + "');");
            }
        });
    }

    /**
     * dailylog的表头获取接口
     *
     * @param data 回调函数
     */
    private void updateProfile(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);

        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final JSONObject dataObj = JsonUtil.getJsonObject(jsonObject, DATA_KEY);

        int index = JsonUtil.getInt(dataObj, "index");
        final String profileJson = JsonUtil.getString(dataObj, "data");
        if (index >= Constants.DDL_DAYS) {

            index = Constants.DDL_DAYS - 1;
        }

        final HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                Profile profile = JsonUtil.parseObject(profileJson, Profile.class);
                hosDayModel.getProfile().updateProfile(profile);

                String dateStr = TimeUtil.utcToLocal(hosDayModel.getDate().getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
                ProfileEntity profileEntity = DataBaseHelper.getDataBase().profileDao().getByDate(dateStr);

                if (profileEntity == null) {

                    profileEntity = new ProfileEntity();
                    profileEntity.setProfileJson(JsonUtil.toJSONString(profile));
                    profileEntity.setDate(dateStr);

                    DataBaseHelper.getDataBase().profileDao().insert(profileEntity);
                } else {

                    profileEntity.setProfileJson(JsonUtil.toJSONString(profile));
                    DataBaseHelper.getDataBase().profileDao().update(profileEntity);
                }

                JSONObject uploadObject = Profile.getServerJsonByProfile(profile, hosDayModel.getDate());
                HttpRequest.Builder builder = new HttpRequest.Builder()
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .url(Constants.API_SAVE_DOT_REVIEW_HEAD_DATA);
                for (String key : uploadObject.keySet()) {

                    builder = builder.addParam(key, uploadObject.getString(key));
                }

                RequestCacheService.getInstance().cachePost(builder.build(), RequestType.OTHERS);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                    }
                });
            }
        });
    }

    /**
     * dailylog的签名获取接口
     *
     * @param data 回调函数
     */
    private void getSign(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);

        int index = JsonUtil.getInt(jsonObject, "index");
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        if (index >= Constants.DDL_DAYS) {

            index = Constants.DDL_DAYS - 1;
        }

        HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
        String signStr = hosDayModel.getSign();

        final SignResult result = new SignResult(!TextUtils.isEmpty(signStr), signStr);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(result) + "');");
            }
        });
    }

    /**
     * dailylog的签名上传
     *
     * @param data 回调函数
     */
    private void uploadSign(final String data) {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                JSONObject jsonObject = JsonUtil.parseObject(data);
                int index = JsonUtil.getInt(jsonObject, "index");
                String sign = JsonUtil.getString(jsonObject, "sign");
                final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

                if (index >= Constants.DDL_DAYS) {

                    index = Constants.DDL_DAYS - 1;
                }

                HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
                hosDayModel.setSign(sign);

                Date date = hosDayModel.getDate();
                String dateStr = TimeUtil.utcToLocal(date.getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);

                DataBaseHelper.getDataBase().signCacheDao().signOneDay(dateStr, user.getDriverId(), sign);

                HttpRequest profileReq = new HttpRequest.Builder()
                        .url(Constants.API_UPLOAD_SIGN)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("date", dateStr)
                        .addParam("driver_sign", sign)
                        .addParam("comment", "")
                        .build();

                RequestCacheService.getInstance().cachePost(profileReq, RequestType.OTHERS);

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                    }
                });

                EventCenter.getInstance().signEvent(date);

                EventBus.getDefault().post(new GetAlertsEvent());
                EventBus.getDefault().post(new DailylogSignAlertEvent());
            }
        });
    }

    /**
     * 获取某天的违规信息
     *
     * @param data 格式为 "MMddyyyy"
     * @return
     */
    private void getOutOfLineEvents(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final int index = JsonUtil.getInt(jsonObject, "index");
        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
                if (index < 0 || index >= Constants.DDL_DAYS) {
                    return;
                }
                final HosDayModel hosDayModel = hosDayModels.get(index);
                if (hosDayModel == null) {

                    return;
                }

                final ViolationModel violationModel = HosHandler.getInstance().getCalculator().calculateViolation(hosDayModel.getDate());
                if (violationModel == null) {

                    return;
                }
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(violationModel) + "');");
                    }
                });
            }
        });

    }

    /**
     * 获取从某一个index开始，第二天开始查找第一个不是隐藏的点。
     * 如果第二天都是隐藏的点，则继续寻找直至寻找到今天。
     * 计算从那个点开始，到index第二天的0点的过去的秒数。
     * 用途是计算duration如果是跨天的事件，duration的真正值。
     *
     * @return
     */
    private void getPastDayEventSecond(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final int index = JsonUtil.getInt(jsonObject, "index");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                long result = 0;
                List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
                //如果index过大或者index为0，如果为0，第二天不会有事件。
                if (index >= Constants.DDL_DAYS || index <= 0) {

                    result = 0;
                } else {

                    //从该天的第二天开始查找。
                    for (int i = index - 1; i >= 0; i--) {

                        HosDayModel hosDayModel = hosDayModels.get(i);
                        if (hosDayModel != null) {

                            HosDriveEventModel hosDriveEventModel = null;

                            List<HosDriveEventModel> driveEventModels = hosDayModel.getHosDriveEventModels();
                            List<HosDriveEventModel> reversedModels = new ArrayList<>(driveEventModels);
                            Collections.reverse(reversedModels);

                            for (HosDriveEventModel model : reversedModels) {

                                if (!model.isHide()) {

                                    hosDriveEventModel = model;
                                    break;
                                }
                            }

                            //没有显示的事件。
                            if (hosDriveEventModel == null) {

                                if (i == 0) {

                                    result += TimeUtil.intervalSecond(new Date(), hosDayModel.getDate());
                                } else {

                                    result += TimeUtil.getTotalHours(hosDayModel.getDate()) * 60 * 60;
                                }
                            } else {

                                result += hosDriveEventModel.getStartSecond();
                                break;
                            }
                        }
                    }
                }

                final long second = result;
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + second + "');");
                    }
                });
            }
        });
    }

    /**
     * 获取昨天结束时，司机的状态
     *
     * @param data 数据
     */
    private void getPastDayEndState(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        int index = JsonUtil.getInt(jsonObject, "index");

        List<HosDayModel> hosDayModels = ModelCenter.getInstance().getAllHosDayModels();
        if (index >= Constants.DDL_DAYS - 1) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uiWebView.loadUrl("javascript:" + callback + "('" + DriverState.OFF_DUTY.getCode() + "');");
                }
            });
        } else {

            final HosDayModel hosDayModel = hosDayModels.get(index + 1);
            final List<HosDriveEventModel> hosDriveEventModels = hosDayModel.getHosDriveEventModels();
            if (hosDriveEventModels.isEmpty()) {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + hosDayModel.getStartDriverState().getCode() + "');");
                    }
                });
            } else {

                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        uiWebView.loadUrl("javascript:" + callback + "('" + hosDriveEventModels.get(0).getDriverState().getCode() + "');");
                    }
                });
            }
        }
    }

    /**
     * 获取预计明天可驾驶时间
     *
     * @param data 回调函数
     */
    private void getTomorrowDrivingRemainTip(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        final String tips = HosHandler.getInstance().getCalculator().getTomorrowDrivingRemainTip();

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "('" + tips + "');");
            }
        });
    }

    /**
     * 获取事件的详情
     *
     * @param data 回调函数
     */
    private void getLogDetail(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final int id = JsonUtil.getInt(jsonObject, "id");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById((long) id);
                if (dailyLog != null) {
                    String json = dailyLog.getJson();
                    JSONObject eventObject = JsonUtil.parseObject(json);
                    final Map<String, String> result = new HashMap<>();
                    int type = Integer.valueOf(eventObject.getString("type"));
                    int code = Integer.valueOf(eventObject.getString("code"));
                    if (type == EventItem.SELF_CHECK.getCode()) {
                        String abnormalCode = eventObject.getString("abnormalCode");
                        result.put("eventName", EventUtil.getSelfCheckModelTitle(code, abnormalCode, false));
                    } else {
                        result.put("eventName", EventUtil.getHosTitle(type, code, false));
                    }
                    result.put("state", String.valueOf(DriverState.getStateByCodeAndType(eventObject.getInteger("type"), eventObject.getInteger("code")).getCode()));
                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(eventObject.getIntValue("vehicleId"));
                    if (vehicle != null) {
                        result.put("vehicle", vehicle.getCode());
                        result.put("vehicleId", String.valueOf(vehicle.getId()));
                    }
                    result.put("time", TimeUtil.utcToLocal(eventObject.getLong("datetime"), SystemHelper.getUser().getTimeZone(), TimeUtil.DDL_DETAIL_FORMAT));
                    result.put("origin", DDLOriginEnum.getEnumByCode(eventObject.getInteger("origin")) == null ? "" : DDLOriginEnum.getEnumByCode(eventObject.getInteger("origin")).getOrigin());
                    result.put("location", eventObject.getString("location"));
                    result.put("latitude", eventObject.getString("latitude"));
                    result.put("longitude", eventObject.getString("longitude"));
                    result.put("odometer", eventObject.getString("totalOdometer"));
                    result.put("engineHour", eventObject.getString("totalEngineHours"));
                    result.put("remark", eventObject.getString("comment"));
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + JsonUtil.toJsJSONString(result) + "');");
                        }
                    });
                }
            }
        });
    }

    /**
     * 新增修改事件
     *
     * @param data 回调函数
     */
    private void modifyEvent(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final long localId = JsonUtil.getInt(jsonObject, "localId");
        final JSONObject jsonData = JsonUtil.getJsonObject(jsonObject, "data");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                int beginSecond = jsonData.getInteger("beginSecond");
                int endSecond = jsonData.getInteger("endSecond");

                StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
                builder.modify();
                Integer vehicle = jsonData.getInteger("vehicle");
                if (vehicle != null) {
                    builder.vehicleId(vehicle);
                }
                builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
                builder.location(jsonData.getString("location"), jsonData.getString("latitude"), jsonData.getString("longitude"));
                builder.odometer(Double.valueOf(TextUtils.isEmpty(jsonData.getString("odometer")) ? "0" : jsonData.getString("odometer")));
                builder.remark(jsonData.getString("remark"));

                int index = jsonData.getInteger("index");
                if (index >= Constants.DDL_DAYS) {

                    index = Constants.DDL_DAYS - 1;
                }
                HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
                if (hosDayModel == null) {

                    return;
                }
                builder.date(new Date(hosDayModel.getDate().getTime() + beginSecond * 1000L));

                DriverState driverState = DriverState.getStateByCode(jsonData.getInteger("state"));

                EventCenter.getInstance().modifyDailyLog(driverState, builder.build(), localId);
            }
        });
    }

    /**
     * 新增修改事件
     *
     * @param data 回调函数
     */
    private void newEvent(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final JSONObject jsonData = JsonUtil.getJsonObject(jsonObject, "data");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
                builder.modify();
                builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
                if (jsonData.getString("vehicle") != null) {

                    builder.vehicleId(Integer.valueOf(jsonData.getString("vehicle")));
                }
                builder.location(jsonData.getString("location"),
                        jsonData.getString("latitude"),
                        jsonData.getString("longitude"));
                builder.odometer(Double.valueOf(TextUtils.isEmpty(jsonData.getString("odometer")) ? "0" : jsonData.getString("odometer")));
                builder.remark(jsonData.getString("remark"));

                DriverState driverState = DriverState.getStateByCode(jsonData.getInteger("state"));
                int beginSecond = jsonData.getInteger("beginSecond");
                int index = jsonData.getInteger("index");
                if (index >= Constants.DDL_DAYS) {

                    index = Constants.DDL_DAYS - 1;
                }
                HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
                if (hosDayModel == null) {

                    return;
                }
                Date date = hosDayModel.getDate();
                date = new Date(date.getTime() + beginSecond * 1000L);
                builder.date(date);
                StateAdditionInfo additionInfo = builder.build();
                EventCenter.getInstance().addDailyLog(driverState, additionInfo);
            }
        });
    }

    /**
     * 新增备注
     *
     * @param data 回调函数
     */
    private void newRemark(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final JSONObject jsonData = JsonUtil.getJsonObject(jsonObject, "data");
        String remark = jsonData.getString("remark");
        int beginSecond = jsonData.getInteger("beginSecond");
        int index = jsonData.getInteger("index");

        ModelCenter.getInstance().updateRemark(index, beginSecond, remark);
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                uiWebView.loadUrl("javascript:" + callback + "();");
            }
        });
    }

    /**
     * 根据时间获取司机状态
     *
     * @param data
     */
    private void getStatusByTime(String data) {

        final JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);
        final JSONObject jsonData = JsonUtil.getJsonObject(jsonObject, "data");
        int beginSecond = jsonData.getInteger("beginSecond");
        int index = jsonData.getInteger("index");
        if (index >= Constants.DDL_DAYS) {

            index = Constants.DDL_DAYS - 1;
        }

        HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
        if (hosDayModel == null) {

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                }
            });
        } else {

            Date date = new Date(hosDayModel.getDate().getTime() + beginSecond * 1000L);
            final HosDriveEventModel hosDriveEventModel = ModelCenter.getInstance().getHosDriveEventModel(date);

            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (hosDriveEventModel != null) {

                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "', '" + hosDriveEventModel.getDriverState().getCode() + "');");
                    } else {

                        uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                    }
                }
            });
        }
    }

    /**
     * 与服务端同步DDL
     *
     * @param data 回调函数
     */
    private void syncDdl(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        Logger.i(Tags.SYNC, "start sync ddl....");

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_DDL_COMPARE)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .build();

                Logger.i(Tags.SYNC, "request ddl ids from server...");
                HttpResponse httpResponse = httpRequest.get();
                if (httpResponse.isSuccess()) {

                    Logger.i(Tags.SYNC, "request ddl ids from server success.");
                    String data = httpResponse.getData();
                    JSONObject dataObj = JsonUtil.parseObject(data);

                    String dataArrayStr = JsonUtil.getString(dataObj, "data");
                    //服务端15天内DDL的id
                    List<String> serverIds = JsonUtil.parseArray(dataArrayStr, String.class);
                    Logger.i(Tags.SYNC, "server ids=" + JsonUtil.toJSONString(serverIds));

                    //本地15天内DDL的id
                    List<String> localIds = new ArrayList<>();
                    //15天的开始时间
                    long startTime = TimeUtil.getDayBegin(TimeUtil.getPreviousDate(new Date(), Constants.DDL_DAYS - 1)).getTime();
                    List<DailyLog> dailyLogs = DataBaseHelper.getDataBase().dailyLogDao().listAll();
                    for (DailyLog dailyLog : dailyLogs) {

                        String jsonStr = dailyLog.getJson();
                        JSONObject jsonObj = JsonUtil.parseObject(jsonStr);

                        long datetime = JsonUtil.getLong(jsonObj, "datetime");
                        if (datetime >= startTime) {

                            localIds.add(dailyLog.getOriginId());
                        }
                    }

                    Logger.i(Tags.SYNC, "local ids=" + JsonUtil.toJSONString(localIds));
                    //本地缺失的DDL的id
                    final List<String> localDefectIds = new ArrayList<>();
                    //服务端缺失的DDL的id
                    List<String> serverDefectIds = new ArrayList<>();
                    for (String serverId : serverIds) {

                        boolean findId = false;
                        for (String localId : localIds) {

                            if (serverId.equals(localId)) {

                                findId = true;
                                break;
                            }
                        }
                        if (!findId) {

                            localDefectIds.add(serverId);
                        }
                    }
                    Logger.i(Tags.SYNC, "local defect ids=" + JsonUtil.toJSONString(localDefectIds));
                    for (String localId : localIds) {

                        boolean findId = false;
                        for (String serverId : serverIds) {

                            if (localId.equals(serverId)) {

                                findId = true;
                                break;
                            }
                        }
                        if (!findId) {

                            serverDefectIds.add(localId);
                        }
                    }

                    Logger.i(Tags.SYNC, "server defect ids=" + JsonUtil.toJSONString(serverDefectIds));
                    RequestCacheService.getInstance().syncWithServer(localDefectIds, serverDefectIds, new SyncCallback() {
                        @Override
                        public void onSuccess() {

                            Logger.i(Tags.SYNC, "sync ddl success.");
                            //重新加载内存数据
                            if (!localDefectIds.isEmpty()) {

                                ModelCenter.getInstance().reloadData();
                            }

                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                                }
                            });
                        }

                        @Override
                        public void onFailed() {

                            Logger.i(Tags.SYNC, "sync ddl failed.");
                            ((Activity) context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_FAILURE + "');");
                                }
                            });
                        }
                    });
                } else {

                    Logger.w(Tags.SYNC, "request ddl ids from server failed. code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
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
     * 判断是否需要处理未认领日志
     *
     * @param data 回调函数
     */
    private void hasUnidentified(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        DataCacheService.getInstance().getUnidentifiedLogFromOrigin();
        final List<AlertAssignedVo> alertAssignedVos = DataCacheService.getInstance().getUnidentifiedLogs();
        final List<UnidentifiedEngineLogVo> unidentifiedEngineLogVos = DataCacheService.getInstance().getUnidentifiedEngineLogs();
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {

                int result = (alertAssignedVos.isEmpty() && unidentifiedEngineLogVos.isEmpty()) ? 0 : 1;
                uiWebView.loadUrl("javascript:" + callback + "('" + result + "');");
            }
        });
    }

    /**
     * 打开未认领日志处理界面
     *
     * @param data 回调函数
     */
    private void openUnidentified(String data) {

        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        if (!(activity instanceof DrivingActivity)) {

            Logger.i(Tags.UNIDENTIFIED, "open unidentifiedLogPage");

            String fileName = "unidentified-driver-log.html";
            String title = activity.getResources().getString(R.string.unidentified_page_title);

            Intent intent = WebActivity.newIntent(activity, fileName, title, WebActivity.CANNOT_BACK, null);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } else {

            Logger.w(Tags.UNIDENTIFIED, "current page is Driving Page, can't open unidentifiedLogPage");
        }
    }
}
