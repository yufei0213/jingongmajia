package com.unitedbustech.eld.hos.core;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.InspectionType;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.eventbus.DailylogSignAlertEvent;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventbus.HosModelChangeEvent;
import com.unitedbustech.eld.eventcenter.enums.DDLStatusEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.DriverStatusModel;
import com.unitedbustech.eld.eventcenter.model.EngineEventModel;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.hos.model.GridModel;
import com.unitedbustech.eld.hos.model.HosAdverseDriveEventModel;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.hos.model.HosEventModel;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author zhangyu
 * @date 2018/5/28
 * @description 模型中心，主要的工作为：
 * 1存储并缓存当前的数据模型。
 * 2提供所有与模型有关的方法。
 * 3模型的本地数据库模型的维护。
 * 4发出模型变化的通知。
 */
public class ModelCenter {

    private static final String TAG = "ModelCenter";

    /**
     * 检查是否需要跨天刷新的周期
     */
    private static final long DAY_REFRESH_CHECK_DURATION = 5 * 60 * 1000L;

    /**
     * 模型中心是唯一实例
     */
    private static ModelCenter instance;

    /**
     * 记录是否是break的offduty
     * 需要通过他决定是否断车
     */
    private boolean isBreak;

    /**
     * 数据转换器，将本地缓存，网络等数据加载转化为本地模型的实例。
     */
    private DataTransformer dataTransformer;

    /**
     * 每天的HOS计算模型
     */
    private List<HosDayModel> hosDayModels;

    /**
     * 跨天刷新的handler
     */
    private Handler handler;

    /**
     * 跨天刷新的Runnable
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    Logger.i(Tags.EVENT, "check day refresh!");

                    //如果已经被跨天刷新过。不做任何处理
                    if (hosDayModels != null &&
                            !hosDayModels.isEmpty() &&
                            TimeUtil.isSameDay(hosDayModels.get(0).getDate(), new Date())) {

                        Logger.i(Tags.EVENT, "don't need day refresh!");
                    } else {

                        List<HosDayModel> results = dataTransformer.refreshDayModels();
                        hosDayModels.clear();
                        hosDayModels.addAll(results);

                        Logger.i(Tags.EVENT, "day refresh complete!");
                    }
                }
            });

            handler.postAtTime(this, SystemClock.uptimeMillis() + DAY_REFRESH_CHECK_DURATION);
        }
    };

    private ModelCenter() {

        dataTransformer = new DataTransformer();
        hosDayModels = new CopyOnWriteArrayList<>();
        handler = new Handler(Looper.getMainLooper());
    }

    public static ModelCenter getInstance() {

        if (instance == null) {

            instance = new ModelCenter();
        }

        return instance;
    }

    /**
     * 初始化方法。
     * 本初始化方法有回调，需要在加载模型完成后进行回调。
     */
    public void init(final InitOverCallback callback) {

        dataTransformer.init(new DataTransformer.InitResult() {
            @Override
            public void initData(List<HosDayModel> result) {

                hosDayModels.clear();
                hosDayModels.addAll(result);

                startCheckDayRefresh();

                int ruleId = DataBaseHelper.getDataBase().driverRuleDao().getRuleId(SystemHelper.getUser().getDriverId());
                Rule rule = DataBaseHelper.getDataBase().ruleDao().getRule(ruleId);
                HosHandler.getInstance().init(rule);

                callback.initDataOver();
            }

            @Override
            public void initFailure() {

                callback.initDataFailure();
            }
        });
    }

    /**
     * 销毁方法
     * 进程结束时调用
     */
    public void destroy() {

        try {

            if (handler != null) {

                handler.removeCallbacks(runnable);
            }

            hosDayModels = null;
            dataTransformer = null;
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            instance = null;
        }
    }

    /**
     * 当前是否是Break状态
     *
     * @return Break状态
     */
    public boolean getBreak() {

        return isBreak;
    }

    /**
     * 更新Break状态
     *
     * @param isBreak 是否是Break
     */
    public void setBreak(boolean isBreak) {

        this.isBreak = isBreak;
    }

    /**
     * 重新加载数据
     */
    public void reloadData() {

        List<HosDayModel> results = dataTransformer.reloadDayModels();
        hosDayModels.clear();
        hosDayModels.addAll(results);
    }

    /**
     * 根据日期获取画图需要的模型。
     *
     * @param date          日期
     * @param additionEvent 附加模型
     * @return 可供画图的数据
     */
    public List<GridModel> getGridData(@NonNull Date date, @NonNull List<HosDriveEventModel> additionEvent, List<Long> excludeEventLocalIds) {

        List<GridModel> result = new ArrayList<>();

        //如果日期为空，则默认今天
        if (date == null) {

            date = new Date();
        }

        for (HosDayModel hosDayModel : hosDayModels) {

            if (TimeUtil.isSameDay(date, hosDayModel.getDate())) {

                List<GridModel> temp = getGridModelByHosDayModel(hosDayModel, additionEvent, excludeEventLocalIds);
                result.addAll(temp);
            }
        }

        return result;
    }

    /**
     * 更新备注时调用该方法
     *
     * @param index       今天向后数第几天
     * @param beginSecond 在哪个位置增加备注
     * @param remark      备注信息
     */
    public void updateRemark(int index, int beginSecond, String remark) {

        HosDayModel hosDayModel = hosDayModels.get(index);

        if (hosDayModel == null) {
            return;
        }

        Date date = hosDayModel.getDate();

        date = new Date(date.getTime() + beginSecond * 1000L);
        HosDriveEventModel hosDriveEventModel = getHosDriveEventModel(date);
        if (hosDriveEventModel == null) {

            return;
        }

        hosDriveEventModel.setRemark(remark);

        DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(hosDriveEventModel.getLocalId());
        EventModel eventModel = null;
        if (dailyLog != null) {

            eventModel = JsonUtil.parseObject(dailyLog.getJson(), EventModel.class);
        }
        if (eventModel != null) {

            JSONObject eventObject = JsonUtil.parseObject(dailyLog.getJson());
            eventObject.put("comment", remark);
            dailyLog.setJson(eventObject.toJSONString());
            DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(dailyLog);

            HttpRequest HttpRequest = new HttpRequest.Builder()
                    .url(Constants.API_ADD_REMARK).addParam("access_token", SystemHelper.getUser().getAccessToken())
                    .addParam("event_id", String.valueOf(eventModel.getId()))
                    .addParam("comment", remark)
                    .build();
            RequestCacheService.getInstance().cachePost(HttpRequest, RequestType.OTHERS);
        }
    }

    /**
     * 添加Adverse后，追加备注
     *
     * @param remark 备注内容
     */
    public void updateRemarkAfterAdverse(String remark) {

        HosDriveEventModel hosDriveEventModel = getCurrentHosDriveModel();
        if (hosDriveEventModel == null) {

            return;
        }

        //查询数据库
        DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(hosDriveEventModel.getLocalId());
        EventModel eventModel = null;
        if (dailyLog != null) {

            eventModel = JsonUtil.parseObject(dailyLog.getJson(), EventModel.class);
        }
        if (eventModel != null) {

            String nowRemark = eventModel.getComment();
            if (TextUtils.isEmpty(nowRemark) || nowRemark.equals("null")) {

                nowRemark = "";
            }
            //原来的remark长度多余60不做处理
            if (nowRemark.length() < Constants.REMARK_MAX_LENGTH && !TextUtils.isEmpty(remark)) {

                nowRemark = nowRemark + (TextUtils.isEmpty(nowRemark) ? "" : "; ") + "Adverse Driving: " + remark;

                if (nowRemark.length() > Constants.REMARK_MAX_LENGTH) {

                    nowRemark = nowRemark.substring(0, Constants.REMARK_MAX_LENGTH);
                }
                hosDriveEventModel.setRemark(nowRemark);

                JSONObject eventObject = JsonUtil.parseObject(dailyLog.getJson());
                eventObject.put("comment", nowRemark);
                dailyLog.setJson(eventObject.toJSONString());
                DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(dailyLog);

                HttpRequest HttpRequest = new HttpRequest.Builder()
                        .url(Constants.API_ADD_REMARK)
                        .addParam("access_token", SystemHelper.getUser().getAccessToken())
                        .addParam("event_id", String.valueOf(eventModel.getId()))
                        .addParam("comment", nowRemark).build();
                RequestCacheService.getInstance().cachePost(HttpRequest, RequestType.OTHERS);
            }
        }
    }

    /**
     * 做完Inspection后，检查是否需要更新备注
     *
     * @param type inspection的类型
     */
    public void updateRemarkAfterInspection(int type) {

        //如果当前是ODND状态，则修改事件的remark状态
        DriverState currentDriverState = getCurrentDriverState();
        if (currentDriverState != null && currentDriverState == DriverState.ON_DUTY_NOT_DRIVING) {

            HosDriveEventModel hosDriveEventModel = getCurrentHosDriveModel();
            if (hosDriveEventModel == null) {

                return;
            }

            //查询数据库
            DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(hosDriveEventModel.getLocalId());
            EventModel eventModel = null;
            if (dailyLog != null) {

                eventModel = JsonUtil.parseObject(dailyLog.getJson(), EventModel.class);
            }
            if (eventModel != null) {

                String nowRemark = eventModel.getComment();
                //追加的remark
                String addRemark = "";
                if (TextUtils.isEmpty(nowRemark) || nowRemark.equals("null")) {

                    nowRemark = "";
                }
                //原来的remark长度多余60不做处理
                if (nowRemark.length() < Constants.REMARK_MAX_LENGTH) {

                    if (type == InspectionType.PRE_TRIP) {

                        if (!nowRemark.contains(App.getContext().getResources().getString(R.string.dvir_pre_trip))) {

                            nowRemark = nowRemark + (TextUtils.isEmpty(nowRemark) ? "" : "; ") + App.getContext().getResources().getString(R.string.dvir_pre_trip);
                            addRemark = App.getContext().getResources().getString(R.string.dvir_pre_trip);
                        }
                    } else if (type == InspectionType.INTERIM) {

                        if (!nowRemark.contains(App.getContext().getResources().getString(R.string.dvir_interim))) {

                            nowRemark = nowRemark + (TextUtils.isEmpty(nowRemark) ? "" : "; ") + App.getContext().getResources().getString(R.string.dvir_interim);
                            addRemark = App.getContext().getResources().getString(R.string.dvir_interim);
                        }
                    } else if (type == InspectionType.POST_TRIP) {

                        if (!nowRemark.contains(App.getContext().getResources().getString(R.string.dvir_post_trip))) {

                            nowRemark = nowRemark + (TextUtils.isEmpty(nowRemark) ? "" : "; ") + App.getContext().getResources().getString(R.string.dvir_post_trip);
                            addRemark = App.getContext().getResources().getString(R.string.dvir_post_trip);
                        }
                    }

                    if (!TextUtils.isEmpty(addRemark)) {

                        if (nowRemark.length() > Constants.REMARK_MAX_LENGTH) {

                            nowRemark = nowRemark.substring(0, Constants.REMARK_MAX_LENGTH);
                        }
                        hosDriveEventModel.setRemark(nowRemark);

                        JSONObject eventObject = JsonUtil.parseObject(dailyLog.getJson());
                        eventObject.put("comment", nowRemark);
                        dailyLog.setJson(eventObject.toJSONString());
                        DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(dailyLog);

                        HttpRequest HttpRequest = new HttpRequest.Builder()
                                .url(Constants.API_ADD_REMARK)
                                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                                .addParam("event_id", String.valueOf(eventModel.getId()))
                                .addParam("comment", nowRemark).build();
                        RequestCacheService.getInstance().cachePost(HttpRequest, RequestType.OTHERS);
                    }
                }
            }
        }
    }

    /**
     * 根据alert的edit来更新本地模型
     * <p>
     * 注意，调用此方法时，传进来的事件是最终要入库的事件
     *
     * @param eventModels 所有模型
     */
    public void updateModelByAlert(List<DriverStatusModel> eventModels) {

        EventBus.getDefault().post(new GetAlertsEvent());

        if (eventModels == null || eventModels.isEmpty()) {

            return;
        }
        boolean hasChangeModel = false;
        for (DriverStatusModel driverStatusModel : eventModels) {

            if (driverStatusModel == null) {

                continue;
            }

            //内存中是否找到对应的模型
            boolean findModel = false;

            HosDriveEventModel hosDriveEventModel = (HosDriveEventModel) driverStatusModel.convertLocalHosModel();
            for (HosDayModel hosDayModel : hosDayModels) {

                //注意，此处需要先更新旧模型，再插入新模型
                if (TimeUtil.isSameDay(hosDriveEventModel.getDate(), hosDayModel.getDate())) {

                    if (!TextUtils.isEmpty(driverStatusModel.getOriginator().trim())) {

                        DailyLog oldDailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogByoriginId(driverStatusModel.getOriginator());

                        if (oldDailyLog != null) {

                            JSONObject jsonObject = JsonUtil.parseObject(oldDailyLog.getJson());
                            jsonObject.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());
                            oldDailyLog.setJson(jsonObject.toJSONString());
                            DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(oldDailyLog);
                        }
                    }

                    DailyLog dailyLog = new DailyLog();
                    dailyLog.setJson(JsonUtil.toJSONString(driverStatusModel));
                    dailyLog.setOriginId(driverStatusModel.getId());
                    DataBaseHelper.getDataBase().dailyLogDao().insert(dailyLog);
                    hasChangeModel = true;

                    //重置签名
                    hosDayModel.setSign("");
                    String dateStr = TimeUtil.utcToLocal(driverStatusModel.getDatetime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
                    int driverId = SystemHelper.getUser().getDriverId();
                    DataBaseHelper.getDataBase().signCacheDao().unSignOneDay(dateStr, driverId);

                    findModel = true;
                    break;
                }
            }

            //如果没有找到对应的模型，则说明事件为15天前的事件
            if (!findModel) {

                DailyLog dailyLog = new DailyLog();
                dailyLog.setJson(JsonUtil.toJSONString(driverStatusModel));
                dailyLog.setOriginId(driverStatusModel.getId());
                DataBaseHelper.getDataBase().dailyLogDao().insert(dailyLog);
                hasChangeModel = true;
            }
        }
        if (hasChangeModel) {

            List<HosDayModel> results = dataTransformer.reloadDayModels();
            hosDayModels.clear();
            hosDayModels.addAll(results);
            EventBus.getDefault().post(new HosModelChangeEvent());
        }
    }

    /**
     * 根据alert的edit来更新本地模型
     * <p>
     * 注意，调用此方法时，传进来的事件是最终要入库的事件
     *
     * @param eventModels 所有模型
     */
    public void updateEngineModelByAlert(List<EngineEventModel> eventModels) {

        EventBus.getDefault().post(new GetAlertsEvent());

        if (eventModels == null || eventModels.isEmpty()) {

            return;
        }
        boolean hasChangeModel = false;
        for (EngineEventModel engineEventModel : eventModels) {

            if (engineEventModel == null) {

                continue;
            }

            //内存中是否找到对应的模型
            boolean findModel = false;

            HosEventModel hosEventModel = (HosEventModel) engineEventModel.convertLocalHosModel();
            for (HosDayModel hosDayModel : hosDayModels) {

                //注意，此处需要先更新旧模型，再插入新模型
                if (TimeUtil.isSameDay(hosEventModel.getDate(), hosDayModel.getDate())) {

                    if (engineEventModel.getOriginator() != null && !TextUtils.isEmpty(engineEventModel.getOriginator().trim())) {

                        DailyLog oldDailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogByoriginId(engineEventModel.getOriginator());

                        if (oldDailyLog != null) {

                            JSONObject jsonObject = JsonUtil.parseObject(oldDailyLog.getJson());
                            jsonObject.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());
                            oldDailyLog.setJson(jsonObject.toJSONString());
                            DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(oldDailyLog);
                        }
                    }

                    DailyLog dailyLog = new DailyLog();
                    dailyLog.setJson(JsonUtil.toJSONString(engineEventModel));
                    dailyLog.setOriginId(engineEventModel.getId());
                    DataBaseHelper.getDataBase().dailyLogDao().insert(dailyLog);
                    hasChangeModel = true;

                    //重置签名
                    hosDayModel.setSign("");
                    String dateStr = TimeUtil.utcToLocal(engineEventModel.getDatetime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
                    int driverId = SystemHelper.getUser().getDriverId();
                    DataBaseHelper.getDataBase().signCacheDao().unSignOneDay(dateStr, driverId);

                    findModel = true;
                    break;
                }
            }

            //如果没有找到对应的模型，则说明事件为15天前的事件
            if (!findModel) {

                DailyLog dailyLog = new DailyLog();
                dailyLog.setJson(JsonUtil.toJSONString(engineEventModel));
                dailyLog.setOriginId(engineEventModel.getId());
                DataBaseHelper.getDataBase().dailyLogDao().insert(dailyLog);
                hasChangeModel = true;
            }
        }
        if (hasChangeModel) {

            List<HosDayModel> results = dataTransformer.reloadDayModels();
            hosDayModels.clear();
            hosDayModels.addAll(results);
            EventBus.getDefault().post(new HosModelChangeEvent());
        }
    }

    /**
     * 修改请求缓存中的地址信息
     *
     * @param dataTime 事件发生事件
     * @param localId  事件在本地数据库中的id
     * @param location 地址
     */
    public void updateLocationInMemory(Long dataTime, Long localId, String location) {

        try {

            HosDayModel hosDayModel = getHosDayModel(new Date(dataTime));
            if (hosDayModel == null) {

                return;
            }

            hosDayModel.setModelLocation(localId, location);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 更新内存模型
     *
     * @param item    事件类型
     * @param model   事件模型
     * @param localId 事件的数据库本地id
     */
    public void updateEvent(final EventItem item, EventModel model, Long localId) {

        //如果是未认领日志。直接返回。
        if (model.getDriverId() == 0) {

            return;
        }
        HosDayModel hosDayModel = getHosDayModel(new Date(model.getDatetime()));
        if (hosDayModel == null) {

            //有可能是跨天刷新导致的，进行一次刷新
            List<HosDayModel> results = dataTransformer.refreshDayModels();
            this.hosDayModels.clear();
            this.hosDayModels.addAll(results);

            hosDayModel = getHosDayModel(new Date(model.getDatetime()));
            //跨天刷新也没有出现模型，基本不会出现这种情况
            if (hosDayModel == null) {

                return;
            }
        }
        //驾驶事件的处理
        if (item.equals(EventItem.DRIVER_WORK_STATE) || item.equals(EventItem.SPECIAL_WORK_STATE)) {
            //过滤status clear事件
            if ((model.getCode() == 0 && item.equals(EventItem.SPECIAL_WORK_STATE))) {

                return;
            }
            //判断修改事件
            if (model.getOriginator() != null && !model.getOriginator().isEmpty()) {

                String originId = model.getOriginator();
                DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogByoriginId(originId);
                if (dailyLog != null) {

                    if (hosDayModel.removeHosDriveEventModel(dailyLog.getId())) {

                        JSONObject jsonObject = JsonUtil.parseObject(dailyLog.getJson());
                        jsonObject.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());
                        dailyLog.setJson(jsonObject.toJSONString());
                        DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(dailyLog);
                    }

                    HosDriveEventModel driveEventModel = (HosDriveEventModel) model.convertLocalHosModel();
                    driveEventModel.setLocalId(localId);
                    hosDayModel.getHosDriveEventModels().add(driveEventModel);
                }
            } else {

                HosDriveEventModel driveEventModel = (HosDriveEventModel) model.convertLocalHosModel();
                driveEventModel.setLocalId(localId);
                hosDayModel.getHosDriveEventModels().add(driveEventModel);
            }

            hosDayModel.sortHosDriveEvent();

            resetStartAndHideEvents();

            //重置签名
            hosDayModel.setSign("");
            String dateStr = TimeUtil.utcToLocal(hosDayModel.getDate().getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
            int driverId = SystemHelper.getUser().getDriverId();
            DataBaseHelper.getDataBase().signCacheDao().unSignOneDay(dateStr, driverId);

            EventBus.getDefault().post(new HosModelChangeEvent());
            EventBus.getDefault().post(new DailylogSignAlertEvent());
        } else {

            if (model.getType() == EventItem.EXEMPTION_MODE.getCode()) {

                return;
            }
            HosEventModel eventModel = model.convertLocalHosModel();
            eventModel.setLocalId(localId);
            hosDayModel.getHosEventModels().add(eventModel);
            hosDayModel.sortHosEvent();
        }
        if (item.equals(EventItem.ADVERSE_DRIVING)) {

            this.updateRemarkAfterAdverse(model.getComment());
            EventBus.getDefault().post(new HosModelChangeEvent());
        }
    }

    /**
     * 批量更新内存模型
     * <p>
     * 注意：该事件全部都是驾驶事件，事件列表和id列表必须意义对应
     *
     * @param eventModelList 事件列表
     * @param localIdList    本地id集合
     */
    public void updateEvents(List<EventModel> eventModelList, List<Long> localIdList) {

        if (eventModelList.size() != localIdList.size()) {

            throw new RuntimeException("eventList is not equal localIdList");
        }
        for (EventModel eventModel : eventModelList) {

            if (!EventItem.isDriverEvent(eventModel.getType())) {

                throw new RuntimeException("event is not DriverEvent" + JsonUtil.toJSONString(eventModel));
            }
        }

        List<HosDayModel> hosDayModelList = new ArrayList<>();

        int lastIndex = eventModelList.size() - 1;
        for (; lastIndex >= 0; lastIndex--) {

            EventModel model = eventModelList.get(lastIndex);
            Long localId = localIdList.get(lastIndex);

            HosDayModel hosDayModel = getHosDayModel(new Date(model.getDatetime()));
            //判断修改事件
            if (model.getOriginator() != null && !model.getOriginator().isEmpty()) {

                String originId = model.getOriginator();
                DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogByoriginId(originId);
                if (dailyLog != null) {

                    if (hosDayModel.removeHosDriveEventModel(dailyLog.getId())) {

                        JSONObject jsonObject = JsonUtil.parseObject(dailyLog.getJson());
                        jsonObject.put("status", DDLStatusEnum.INACTIVE_CHANGED.getCode());
                        dailyLog.setJson(jsonObject.toJSONString());
                        DataBaseHelper.getDataBase().dailyLogDao().updateDailylog(dailyLog);
                    }

                    HosDriveEventModel driveEventModel = (HosDriveEventModel) model.convertLocalHosModel();
                    driveEventModel.setLocalId(localId);
                    hosDayModel.getHosDriveEventModels().add(driveEventModel);
                }
            } else {

                HosDriveEventModel driveEventModel = (HosDriveEventModel) model.convertLocalHosModel();
                driveEventModel.setLocalId(localId);
                hosDayModel.getHosDriveEventModels().add(driveEventModel);
            }

            //重置签名
            hosDayModel.setSign("");
            String dateStr = TimeUtil.utcToLocal(hosDayModel.getDate().getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY);
            int driverId = SystemHelper.getUser().getDriverId();
            DataBaseHelper.getDataBase().signCacheDao().unSignOneDay(dateStr, driverId);

            hosDayModelList.add(hosDayModel);
        }

        for (HosDayModel hosDayModel : hosDayModelList) {

            hosDayModel.sortHosDriveEvent();
        }

        resetStartAndHideEvents();

        EventBus.getDefault().post(new HosModelChangeEvent());
        EventBus.getDefault().post(new DailylogSignAlertEvent());
    }

    /**
     * 获取司机当前状态
     *
     * @return DriverState DriverState
     */
    public DriverState getCurrentDriverState() {

        if (hosDayModels == null || hosDayModels.isEmpty()) {

            return null;
        }

        HosDayModel hosDayModel = hosDayModels.get(0);
        List<HosDriveEventModel> hosDriveEventModelList = hosDayModel.getHosDriveEventModels();
        int size = hosDriveEventModelList.size();
        if (hosDriveEventModelList != null && size > 0) {

            HosDriveEventModel hosDriveEventModel = hosDriveEventModelList.get(0);
            return hosDriveEventModel.getDriverState();
        } else {

            return hosDayModel.getStartDriverState();
        }
    }

    /**
     * 获取所有的模型
     *
     * @return List<HosDayModel>
     */
    public List<HosDayModel> getAllHosDayModels() {

        return hosDayModels;
    }

    /**
     * 根据date获得HosDayModel的模型
     *
     * @param date 日期
     * @return HosDayModel
     */
    public HosDayModel getHosDayModel(Date date) {

        for (HosDayModel dayModel : hosDayModels) {

            if (TimeUtil.isSameDay(dayModel.getDate(), date)) {

                return dayModel;
            }
        }
        return null;
    }

    /**
     * 获取全部所有的[司机状态变化]的event
     *
     * @return List<HosDriveEventModel>
     */
    public List<HosDriveEventModel> getDaysAllEvent() {

        List<HosDriveEventModel> result = new ArrayList<>();
        for (HosDayModel hosDayModel : hosDayModels) {

            for (HosDriveEventModel hosDriveEventModel : hosDayModel.getHosDriveEventModels()) {

                result.add(hosDriveEventModel);
            }
        }
        Collections.sort(result);
        return result;
    }

    /**
     * 根据时间，找到该时间后全部的隐藏事件
     *
     * @param date 事件
     * @return 隐藏时间列表
     */
    public List<HosDriveEventModel> getHideEventByDate(Date date) {

        List<HosDriveEventModel> allEvent = getDaysAllEvent();
        if (allEvent.isEmpty()) {

            return new ArrayList<>();
        }

        //首先找到指定时间之后的全部事件
        int index = 0;
        for (int i = index; i < allEvent.size(); i++) {

            HosDriveEventModel eventModel = allEvent.get(i);
            if (eventModel.getDate().getTime() < date.getTime()) {

                index = i - 1;
                break;
            }
        }

        Logger.i(Tags.EVENT, "getHideEventByDate(" + date + ")");

        allEvent = allEvent.subList(0, index + 1);
        Logger.i(Tags.EVENT, "getHideEventByDate: > " + date + ", " + JsonUtil.toJSONString(allEvent));

        //去掉时间相同的非隐藏事件
        List<HosDriveEventModel> removeModelList = new ArrayList<>();
        for (HosDriveEventModel eventModel : allEvent) {

            if (eventModel.getDate().getTime() == date.getTime() && !eventModel.isHide()) {

                removeModelList.add(eventModel);
            }
        }
        Logger.i(Tags.EVENT, "getHideEventByDate: removeModelList " + JsonUtil.toJSONString(removeModelList));
        allEvent.removeAll(removeModelList);
        Logger.i(Tags.EVENT, "getHideEventByDate: allEvent " + JsonUtil.toJSONString(allEvent));

        //找到指定时间之后的全部隐藏事件
        int lastIndex = allEvent.size() - 1;
        boolean findNotHideEvent = false;
        for (int i = lastIndex; i >= 0; i--) {

            HosDriveEventModel eventModel = allEvent.get(i);
            if (!eventModel.isHide()) {

                lastIndex = i;
                findNotHideEvent = true;
                break;
            }
        }

        List<HosDriveEventModel> result = allEvent.subList(findNotHideEvent ? lastIndex + 1 : 0, allEvent.size());
        Logger.i(Tags.EVENT, "getHideEventByDate: hideEvent " + JsonUtil.toJSONString(result));
        return result;
    }

    /**
     * 根据一个日期，获得到该天的最后的驾驶事件
     *
     * @param date 日期
     * @return HosDriveEventModel
     */
    public HosDriveEventModel getHosDriveEventModel(Date date) {

        List<HosDriveEventModel> allEvent = getDaysAllEvent();

        // 删除隐藏事件
        HosDriveEventModel lastEvent = null;
        List<HosDriveEventModel> removeEventList = new ArrayList<>();
        for (HosDriveEventModel event : allEvent) {

            if (lastEvent != null && lastEvent.getDriverState() == event.getDriverState()) {

                removeEventList.add(lastEvent);
            }

            lastEvent = event;
        }

        allEvent.removeAll(removeEventList);

        for (HosDriveEventModel hosDriveEventModel : allEvent) {

            //找到第一个开始时间比传入的日期小的模型。
            if (hosDriveEventModel.getDate().getTime() <= date.getTime()) {

                return hosDriveEventModel;
            }
        }
        return null;
    }

    /**
     * 获取司机当前[司机状态变化]的event
     *
     * @return HosDriveEventModel 可能为null
     */
    public HosDriveEventModel getCurrentHosDriveModel() {

        if (hosDayModels == null || hosDayModels.isEmpty()) {

            return null;
        }

        HosDayModel hosDayModel = hosDayModels.get(0);
        List<HosDriveEventModel> hosDriveEventModelList = hosDayModel.getHosDriveEventModels();
        int size = hosDriveEventModelList.size();
        if (hosDriveEventModelList != null && size > 0) {

            HosDriveEventModel hosDriveEventModel = hosDriveEventModelList.get(0);
            return hosDriveEventModel;
        } else {

            List<HosDriveEventModel> allDriveModel = getDaysAllEvent();
            if (allDriveModel != null && !allDriveModel.isEmpty()) {

                HosDriveEventModel hosDriveEventModel = allDriveModel.get(0);
                return hosDriveEventModel;
            } else {

                return null;
            }
        }
    }

    /**
     * 根据一个事件的时间，判断该事件是否是最后一个事件
     * 如果后面没有事件或者只有隐藏事件，都认为是最后一个事件
     *
     * @param date 事件的时间
     * @return true:是最后一个事件。false反之
     */
    public boolean isLastEvent(Date date) {

        List<HosDriveEventModel> allEvent = getDaysAllEvent();
        for (HosDriveEventModel model : allEvent) {

            //如果找到了发生时间之前的事件，直接返回true
            if (model.getDate().getTime() < date.getTime()) {

                return true;
            }
            if (!model.isHide()) {

                return false;
            }
        }
        //没有事件或者全部遍历完毕，证明是最后一个事件。
        return true;
    }

    /**
     * 获取某个时间段内全部的Adverse
     *
     * @param startDate 开始时间
     * @param endDate   截止时间
     * @return Adverse 列表
     */
    public List<HosAdverseDriveEventModel> getAdverseDriving(Date startDate, Date endDate) {

        List<HosEventModel> eventModels = new ArrayList<>();

        for (HosDayModel hosDayModel : hosDayModels) {

            List<HosEventModel> dayHosEventModels = hosDayModel.getHosEventModels();
            List<HosDriveEventModel> dayHosDriveEventModels = hosDayModel.getHosDriveEventModels();
            if (dayHosEventModels != null && !dayHosEventModels.isEmpty()) {

                eventModels.addAll(dayHosEventModels);
            }
            if (dayHosDriveEventModels != null && !dayHosDriveEventModels.isEmpty()) {

                eventModels.addAll(dayHosDriveEventModels);
            }
        }

        List<HosAdverseDriveEventModel> tempEvents = new ArrayList<>();
        for (HosEventModel hosEventModel : eventModels) {

            if (hosEventModel instanceof HosAdverseDriveEventModel) {

                tempEvents.add((HosAdverseDriveEventModel) hosEventModel);
            }
        }

        Collections.sort(tempEvents);

        List<HosAdverseDriveEventModel> result = new ArrayList<>();
        for (HosAdverseDriveEventModel model : tempEvents) {

            if (model.getDate().getTime() > startDate.getTime() && model.getDate().getTime() < endDate.getTime()) {

                result.add(model);
            }
        }

        return result;
    }

    /**
     * 重置事件，处理一条事件会发生多次的问题
     * driving事件不处理
     *
     * @param hosDriveEventModels List<HosDriveEventModel>
     */
    public void resetDriveModels(List<HosDriveEventModel> hosDriveEventModels) {

        List<HosDriveEventModel> removes = new ArrayList<>();
        for (HosDriveEventModel hosDriveEventModel : hosDriveEventModels) {

            if (hosDriveEventModel.isHide()) {

                removes.add(hosDriveEventModel);
            }
        }
        hosDriveEventModels.removeAll(removes);
    }

    /**
     * 跨天刷新实现函数
     */
    private void startCheckDayRefresh() {

        if (handler != null) {

            handler.removeCallbacks(runnable);
        }

        handler.postAtTime(runnable, SystemClock.uptimeMillis() + DAY_REFRESH_CHECK_DURATION);
    }

    /**
     * 重置每天的开始事件和隐藏事件
     * <p>
     * 因为修改事件会导致跨天修改的问题，因此每次修改都要将每天的开始事件重置，并且需要重置隐藏事件
     */
    private void resetStartAndHideEvents() {

        DriverState lastState = null;
        int lastOrigin = 0;
        //设置初始状态
        int size = hosDayModels.size();
        for (int i = size; i > 0; i--) {

            HosDayModel dayModel = hosDayModels.get(i - 1);
            List<HosDriveEventModel> hosDriveEventModels = dayModel.getHosDriveEventModels();

            if (lastState == null) {

                dayModel.setStartDriverState(DriverState.OFF_DUTY);
            } else {

                if (hosDriveEventModels.isEmpty()) {

                    dayModel.setStartDriverState(lastState);
                    dayModel.setStartEventOrigin(lastOrigin);
                } else {

                    HosDriveEventModel firstEvent = hosDriveEventModels.get(hosDriveEventModels.size() - 1);
                    if (firstEvent.getDate().getTime() == TimeUtil.getDayBegin(dayModel.getDate()).getTime()) {

                        dayModel.setStartDriverState(firstEvent.getDriverState());
                        dayModel.setStartEventOrigin(firstEvent.getOrigin());
                    } else {

                        dayModel.setStartDriverState(lastState);
                        dayModel.setStartEventOrigin(lastOrigin);
                    }
                }
            }

            DriverState preDayLastEventState = null; //前一天最后一个事件的状态
            if (i < size) {

                List<HosDriveEventModel> preDayHosEventModelList = hosDayModels.get(i).getHosDriveEventModels();
                if (preDayHosEventModelList.isEmpty()) {

                    preDayLastEventState = lastState;
                } else {

                    preDayLastEventState = preDayHosEventModelList.get(0).getDriverState();
                }
            } else {

                preDayLastEventState = DriverState.OFF_DUTY;
            }

            //设置隐藏事件
            dayModel.resetHideCase(preDayLastEventState);

            if (hosDriveEventModels.isEmpty()) {

                lastState = dayModel.getStartDriverState();
                lastOrigin = dayModel.getStartEventOrigin();
            } else {

                lastState = dayModel.getHosDriveEventModels().get(0).getDriverState();
                lastOrigin = dayModel.getHosDriveEventModels().get(0).getOrigin();
            }
        }
    }

    /**
     * 通过HosDayModel获取可供画图的数据
     *
     * @param hosDayModel 当天的hosDayModel
     * @return 可供画图的数据
     */
    private List<GridModel> getGridModelByHosDayModel(HosDayModel hosDayModel, List<HosDriveEventModel> additionEvent, List<Long> excludeEventLocalIds) {

        List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();
        hosDriveEventModels.addAll(hosDayModel.getHosDriveEventModels());
        if (additionEvent != null && !additionEvent.isEmpty()) {

            if (excludeEventLocalIds != null) {

                for (Long localId : excludeEventLocalIds) {

                    for (HosDriveEventModel model : hosDriveEventModels) {

                        if (model.getLocalId() == localId) {

                            hosDriveEventModels.remove(model);
                            break;
                        }
                    }
                }
            }

            hosDriveEventModels.addAll(additionEvent);
            Collections.sort(hosDriveEventModels);
        }
        resetDriveModels(hosDriveEventModels);
        return convertGridModelByHosDriveModels(hosDayModel, hosDriveEventModels);
    }

    /**
     * 将HosDayModel 转化为GridModel
     *
     * @param hosDayModel         HosDayModel
     * @param hosDriveEventModels List<HosDriveEventModel>
     * @return List<GridModel>
     */
    private List<GridModel> convertGridModelByHosDriveModels(HosDayModel hosDayModel, List<HosDriveEventModel> hosDriveEventModels) {

        List<GridModel> result = new ArrayList<>();

        Date now = new Date();

        int totalSecond = TimeUtil.getTotalHours(hosDayModel.getDate()) * 60 * 60;
        //如果是今天的，需要对现在的时间进行判断
        if (TimeUtil.isSameDay(now, hosDayModel.getDate())) {

            Date dayBegin = TimeUtil.getDayBegin(now);
            totalSecond = (int) ((now.getTime() - dayBegin.getTime()) / 1000L);
        }
        resetDriveModels(hosDriveEventModels);
        //如果没有数据
        if (hosDriveEventModels.size() == 0) {

            result.add(new GridModel(0,
                    totalSecond,
                    hosDayModel.getStartDriverState(),
                    hosDayModel.getStartEventOrigin(),
                    true));
            return result;
        }
        //第一段线
        result.add(new GridModel(0,
                hosDriveEventModels.get(hosDriveEventModels.size() - 1).getStartSecond(),
                hosDayModel.getStartDriverState(),
                hosDayModel.getStartEventOrigin(),
                hosDriveEventModels.get(hosDriveEventModels.size() - 1).getStartSecond() != 0));

        int i = 0;
        for (i = hosDriveEventModels.size() - 1; i > 0; i--) {

            HosDriveEventModel nowModel = hosDriveEventModels.get(i);
            HosDriveEventModel secondModel = hosDriveEventModels.get(i - 1);
            if (secondModel.getStartSecond() > totalSecond && nowModel.getStartSecond() <= totalSecond) {

                result.add(new GridModel(nowModel.getStartSecond(),
                        totalSecond,
                        nowModel.getDriverState(),
                        nowModel.getOrigin()));
                break;
            }
            //跨天事件处理，如果最新事件的开始秒数小于上一个事件的开始秒数则视为跨天
            if (TimeUtil.compareDaybyDate(nowModel.getDate().getTime(), secondModel.getDate().getTime())) {

                result.add(new GridModel(nowModel.getStartSecond(),
                        totalSecond,
                        nowModel.getDriverState(),
                        nowModel.getOrigin()));
                break;
            }
            //过滤错误数据。
            if (secondModel.getDate().getTime() < nowModel.getDate().getTime()) {

                continue;
            }
            result.add(new GridModel(nowModel.getStartSecond(),
                    secondModel.getStartSecond() > totalSecond ? totalSecond : secondModel.getStartSecond(),
                    nowModel.getDriverState(),
                    nowModel.getOrigin()));
        }
        //如果最后一个事件没结束。则证明是最后还有事件
        if (result.get(result.size() - 1).getEndSecond() <= totalSecond) {

            result.add(new GridModel(hosDriveEventModels.get(i).getStartSecond(),
                    totalSecond,
                    hosDriveEventModels.get(i).getDriverState(),
                    hosDriveEventModels.get(i).getOrigin()));
        }

        return result;
    }

    /**
     * 初始化回调
     */
    public interface InitOverCallback {

        /**
         * 该回调表明，主页数据已经准备好，可以进入主页
         */
        void initDataOver();

        /**
         * 该回调进入表明。主页数据无法加载，会出现在本地的事件为空的同时网络请求失败。
         * 完全获取不到数据。会进入该回调。
         * 如果进入该回调，表明主页数据完全无法展示。
         */
        void initDataFailure();
    }
}
