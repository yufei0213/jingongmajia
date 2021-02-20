package com.unitedbustech.eld.eventcenter.core;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.unitedbustech.eld.common.DiagnosticType;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.EngineEvent;
import com.unitedbustech.eld.common.HistoryEventType;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.DataCollectorSubscriber;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.datacollector.common.History;
import com.unitedbustech.eld.datacollector.common.HistoryType;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.DailyLog;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.AdverseDrivingModel;
import com.unitedbustech.eld.eventcenter.model.DriverStatusModel;
import com.unitedbustech.eld.eventcenter.model.EngineEventModel;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.eventcenter.model.ExemptionModel;
import com.unitedbustech.eld.eventcenter.model.HistoryModel;
import com.unitedbustech.eld.eventcenter.model.IntermediateEventModel;
import com.unitedbustech.eld.eventcenter.model.LogSignModel;
import com.unitedbustech.eld.eventcenter.model.LoginModel;
import com.unitedbustech.eld.eventcenter.model.ModelCouple;
import com.unitedbustech.eld.eventcenter.model.RuleEventModel;
import com.unitedbustech.eld.eventcenter.model.SelfCheckModel;
import com.unitedbustech.eld.eventcenter.states.Driver.DriverStateManager;
import com.unitedbustech.eld.eventcenter.states.Vechile.EngineStateManager;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.util.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 事件中心，负责分发事件
 */
public class EventCenter implements DataCollectorSubscriber {

    private final String TAG = "EventCenter";

    /**
     * 车辆状态维护器
     */
    private EngineStateManager engineStateManager;

    /**
     * 司机状态管理器
     */
    private DriverStateManager driverStateManager;

    /**
     * 事件中心的数据处理器。
     */
    private EventDataHandler eventDataHandler;

    /**
     * 处理中间事件
     */
    private Handler handler;

    private static EventCenter instance = null;

    private EventCenter() {
    }

    /**
     * 获取单例的方法
     *
     * @return EventCenter
     */
    public static EventCenter getInstance() {

        if (instance == null) {

            instance = new EventCenter();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

        engineStateManager = new EngineStateManager();

        driverStateManager = new DriverStateManager();
        this.driverStateManager.init();

        this.eventDataHandler = new EventDataHandler();

        handler = new Handler(Looper.getMainLooper());

        DataCollectorHandler.getInstance().subscribe(TAG, this);
    }

    /**
     * 销毁
     */
    public void destroy() {

        try {

            engineStateManager = null;

            this.driverStateManager.destroy();
            driverStateManager = null;

            if (handler != null) {

                handler.removeCallbacks(intermediateRunnable);
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            instance = null;
        }
    }

    @Override
    public void onSchedule(VehicleDataModel model, CollectorType type) {

    }

    @Override
    public void onDataItemChange(VehicleDataItem item, VehicleDataModel model, CollectorType type) {

        if (item == VehicleDataItem.ENGINE_EVENT) {

            List<EventModel> eventModels = null;
            if (model.getEngineEvent() == EngineEvent.POWER_ON) {

                eventModels = this.engineStateManager.enginStart();
            } else if (model.getEngineEvent() == EngineEvent.POWER_OFF) {

                List<EventModel> models = this.engineStateManager.enginStop();
                if(models == null) {

                    models = new ArrayList<>();
                    models.add(new EngineEventModel.Builder().isOn(false).build());
                }
                eventModels = models;
            }

            distributeEvents(eventModels);
        }
    }

    /**
     * 登录登出事件
     * <p>
     * 如果是登录事件，则异步上报
     * 如果是登出事件，则同步上报
     *
     * @param isLogin 登录还是登出
     */
    public void notifyLoginEvent(boolean isLogin) {

        final LoginModel eventModel = (LoginModel) new LoginModel.Builder().isLogin(isLogin).build();

        if (isLogin) {

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    Logger.i(Tags.EVENT, "Login event, id=" + eventModel.getId());

                    //入库
                    Long id = recordToLocal(eventModel);
                    //更新内存
                    ModelCenter.getInstance().updateEvent(EventItem.LOGIN_STATE, eventModel, id);

                    //同步请求, 上报
                    if (eventDataHandler != null) {

                        eventDataHandler.uploadNotifySync(eventModel, id);
                    }
                }
            });
        } else {

            Logger.i(Tags.EVENT, "logout event, id=" + eventModel.getId());

            //入库
            Long id = recordToLocal(eventModel);
            //同步上报
            if (eventDataHandler != null) {

                eventDataHandler.uploadNotifySync(eventModel, id);
            }
        }
    }

    /**
     * 根据读取到的历史数据，获取上报未认领日志。
     *
     * @param histories 历史记录原始信息
     */
    public void handleUnidentifiedLogs(List<History> histories, int deviceType) {

        List<ModelCouple> modelCouples = getModelCoupleByOriginHistory(histories);
        List<HistoryModel> historyModels = getHistoryModelListByHistories(histories, deviceType);
        List<EventModel> engineModels = getEngineEventModels();
        List<EventModel> eventModels = new ArrayList<>();
        for (ModelCouple modelCouple : modelCouples) {

            //ODND事件的里程和引擎时间，必须大于DRIVING事件
            EventModel drivingEvent = modelCouple.getDrivingModel();
            EventModel odndEvent = modelCouple.getOdndModel();
            if (drivingEvent.getTotalOdometer() < odndEvent.getTotalOdometer() && drivingEvent.getTotalEngineHours() < odndEvent.getTotalEngineHours()) {

                eventModels.add(modelCouple.getDrivingModel());
                eventModels.add(modelCouple.getOdndModel());
            } else {

                Logger.i(Tags.UNIDENTIFIED, "odometer or engineHours isn't normal.");
            }
        }
        if (engineModels != null && engineModels.size() > 0)
            eventModels.addAll(engineModels);
        eventDataHandler.uploadUnidentifiedHistory(historyModels);
        eventDataHandler.uploadUnidentified(eventModels);
    }

    /**
     * 切换司机的工作状态事件
     *
     * @param driverState       新的工作状态
     * @param stateAdditionInfo 附加信息
     */
    public void changeDriverState(final DriverState driverState, final StateAdditionInfo stateAdditionInfo) {

        List<EventModel> eventModels = driverStateManager.switchStateTo(driverState, stateAdditionInfo);

        if (eventModels == null || eventModels.isEmpty()) {

            return;
        }

        Logger.i(Tags.EVENT, "changeDriverState, new state:[" + driverState.toVisibleString() + "]");

        //检查中间日志
        for (EventModel eventModel : eventModels) {

            Logger.i(Tags.EVENT, "event id=" + eventModel.getId());
            checkIntermediate(eventModel);
        }

        distributeEvents(eventModels);
    }

    /**
     * 新增事件调用该方法
     *
     * @param newState     新状态
     * @param additionInfo 附加信息
     */
    public void addDailyLog(DriverState newState, StateAdditionInfo additionInfo) {

        if (additionInfo.getDate() == null) {

            throw new RuntimeException("need event time...");
        }

        Logger.i(Tags.EVENT, "add state, new state:[" + newState.toVisibleString() + "]");

        //要修改的全部事件
        List<EventModel> newEvents = new ArrayList<>();

        //找到要修改的全部隐藏事件
        List<HosDriveEventModel> allHideEvent = ModelCenter.getInstance().getHideEventByDate(additionInfo.getDate());
        for (HosDriveEventModel hosDayModel : allHideEvent) {

            EventModel newHideEvent = new DriverStatusModel.Builder()
                    .addition(additionInfo)
                    .state(newState)
                    .build();

            DailyLog modifyDailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(hosDayModel.getLocalId());
            EventModel modifyEvent = JsonUtil.parseObject(modifyDailyLog.getJson(), EventModel.class);

            //新事件的originator置为老事件的id
            newHideEvent.setOriginator(modifyEvent.getId());
            //不改变老事件的时间，只改变老事件的状态，时间置为老事件的时间
            newHideEvent.setDatetime(modifyEvent.getDatetime());

            //添加到列表中
            newEvents.add(newHideEvent);
        }

        Logger.i(Tags.EVENT, "modify hide events, " + JsonUtil.toJSONString(newEvents));
        //新建目标事件
        EventModel eventModel = new DriverStatusModel.Builder()
                .state(newState)
                .addition(additionInfo)
                .build();

        //添加到列表中
        newEvents.add(eventModel);

        //检查时间最近的事件，是否是司机的最新事件，如果是的话，检查是否需要开启中间日志（第一个事件是时间最近的事件）
        if (ModelCenter.getInstance().isLastEvent(new Date(newEvents.get(0).getDatetime()))) {

            checkIntermediate(newEvents.get(0));
        }

        List<Long> dailyLogIds = new ArrayList<>();
        for (EventModel model : newEvents) {

            //入库
            Long id = recordToLocal(model);
            dailyLogIds.add(id);
        }

        //更新内存
        ModelCenter.getInstance().updateEvents(newEvents, dailyLogIds);

        //上报服务端
        eventDataHandler.uploadNotifySync(newEvents, dailyLogIds);
    }

    /**
     * 修改事件调用该方法
     *
     * @param newState     新状态
     * @param additionInfo 附加信息
     * @param localId      本地存储的原数据id
     */
    public void modifyDailyLog(DriverState newState, StateAdditionInfo additionInfo, long localId) {

        if (additionInfo.getDate() == null) {

            throw new RuntimeException("need event time...");
        }

        Logger.i(Tags.EVENT, "modify dailylog.");

        //数据库模型
        DailyLog dailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(localId);
        DriverStatusModel driverStatusModel = JsonUtil.parseObject(dailyLog.getJson(), DriverStatusModel.class);

        //要修改的全部事件
        List<EventModel> newEvents = new ArrayList<>();

        if (additionInfo.getDate().getTime() >= driverStatusModel.getDatetime()) {

            //如果是新事件的时间在原始事件后面

            //找到要修改的全部隐藏事件
            List<HosDriveEventModel> allHideEvent = ModelCenter.getInstance().getHideEventByDate(additionInfo.getDate());
            for (HosDriveEventModel hosDayModel : allHideEvent) {

                EventModel newHideEvent = new DriverStatusModel.Builder()
                        .addition(additionInfo)
                        .state(newState)
                        .build();

                DailyLog modifyDailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(hosDayModel.getLocalId());
                EventModel modifyEvent = JsonUtil.parseObject(modifyDailyLog.getJson(), EventModel.class);

                //新事件的originator置为老事件的id
                newHideEvent.setOriginator(modifyEvent.getId());

                if (additionInfo.getDate().getTime() < modifyEvent.getDatetime()) {

                    //如果新事件的时间在要修改的事件前面，则不修改原事件的时间
                    newHideEvent.setDatetime(modifyEvent.getDatetime());
                } else {

                    //如果新事件的时间在要修改的事件后面，则修改原事件的时间
                    newHideEvent.setDatetime(additionInfo.getDate().getTime());
                }

                //添加到列表中
                newEvents.add(newHideEvent);
            }

            Logger.i(Tags.EVENT, "modify hide events, " + JsonUtil.toJSONString(newEvents));

            //新建目标事件
            EventModel eventModel = new DriverStatusModel.Builder()
                    .state(newState)
                    .addition(additionInfo)
                    .build();

            //设置原始id
            eventModel.setOriginator(driverStatusModel.getId());

            newEvents.add(eventModel);
        } else {

            //如果新事件的时间在原始事件的前面

            //找到原始事件之后的全部隐藏事件
            List<HosDriveEventModel> afterHideEvent = ModelCenter.getInstance().getHideEventByDate(new Date(driverStatusModel.getDatetime()));
            for (HosDriveEventModel afterModel : afterHideEvent) {

                EventModel newHideEvent = new DriverStatusModel.Builder()
                        .addition(additionInfo)
                        .state(newState)
                        .build();

                DailyLog modifyDailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(afterModel.getLocalId());
                EventModel modifyEvent = JsonUtil.parseObject(modifyDailyLog.getJson(), EventModel.class);

                //新事件的originator置为老事件的id
                newHideEvent.setOriginator(modifyEvent.getId());
                //不改变老事件的时间，只改变老事件的状态，时间置为老事件的时间
                newHideEvent.setDatetime(modifyEvent.getDatetime());

                //添加到列表中
                newEvents.add(newHideEvent);
            }

            Logger.i(Tags.EVENT, "modify after hide events, " + JsonUtil.toJSONString(afterHideEvent));

            //新建目标事件
            EventModel eventModel = new DriverStatusModel.Builder()
                    .state(newState)
                    .addition(additionInfo)
                    .build();
            //设置原始id
            eventModel.setOriginator(driverStatusModel.getId());

            newEvents.add(eventModel);

            //找到新事件之后，原始事件之前的全部隐藏事件
            List<HosDriveEventModel> beforeHideEvent = new ArrayList<>(); //新事件开始时间之后

            //找到当前的全部驾驶事件
            HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(new Date(driverStatusModel.getDatetime()));
            List<HosDriveEventModel> hosDriveEventModels = new ArrayList<>();
            hosDriveEventModels.addAll(hosDayModel.getHosDriveEventModels());

            //查找隐藏事件
            long startTime = additionInfo.getDate().getTime(), endTime = driverStatusModel.getDatetime();
            for (HosDriveEventModel model : hosDriveEventModels) {

                long eventTime = model.getDate().getTime();
                if (model.getLocalId() != localId && startTime <= eventTime && eventTime < endTime) {

                    beforeHideEvent.add(model);
                }
            }

            Logger.i(Tags.EVENT, "modify before hide events, " + JsonUtil.toJSONString(beforeHideEvent));

            for (HosDriveEventModel beforeModel : beforeHideEvent) {

                EventModel newHideEvent = new DriverStatusModel.Builder()
                        .addition(additionInfo)
                        .state(newState)
                        .build();

                DailyLog modifyDailyLog = DataBaseHelper.getDataBase().dailyLogDao().getDailylogById(beforeModel.getLocalId());
                EventModel modifyEvent = JsonUtil.parseObject(modifyDailyLog.getJson(), EventModel.class);

                //新事件的originator置为老事件的id
                newHideEvent.setOriginator(modifyEvent.getId());
                //不改变老事件的时间，只改变老事件的状态，时间置为老事件的时间
                newHideEvent.setDatetime(modifyEvent.getDatetime());

                //添加到列表中
                newEvents.add(newHideEvent);
            }
        }

        //检查时间最近的事件，是否是司机的最新事件，如果是的话，检查是否需要开启中间日志（第一个事件是时间最近的事件）
        if (ModelCenter.getInstance().isLastEvent(new Date(newEvents.get(0).getDatetime()))) {

            checkIntermediate(newEvents.get(0));
        }

        List<Long> dailyLogIds = new ArrayList<>();
        for (EventModel model : newEvents) {

            //入库
            Long id = recordToLocal(model);
            dailyLogIds.add(id);
        }

        //更新内存
        ModelCenter.getInstance().updateEvents(newEvents, dailyLogIds);

        //上报服务端
        eventDataHandler.uploadNotifySync(newEvents, dailyLogIds);
    }

    /**
     * 新建不利条件驾驶的事件
     *
     * @param additionInfo 附加信息
     */
    public void newAdverseDriving(StateAdditionInfo additionInfo) {

        EventModel eventModel = new AdverseDrivingModel.Builder()
                .addition(additionInfo)
                .build();
        Logger.i(Tags.EVENT, "new AdverseDriving, event id=" + eventModel.getId());
        distributeOneEvent(EventItem.ADVERSE_DRIVING, eventModel);
    }

    /**
     * 新建豁免模式事件
     *
     * @param mode 豁免模式类型
     */
    public void newExemptionEvent(int mode) {

        EventModel eventModel = new ExemptionModel.Builder()
                .mode(mode)
                .build();
        Logger.i(Tags.EVENT, "new ExemptionEvent, event id=" + eventModel.getId());
        distributeOneEvent(EventItem.EXEMPTION_MODE, eventModel);
    }

    /**
     * 新建规则切换事件
     *
     * @param ruleId 规则id
     */
    public void newRuleEvent(int ruleId) {

        EventModel eventModel = new RuleEventModel.Builder()
                .setRuleId(ruleId)
                .build();

        Logger.i(Tags.RULE, "rule event add success");
        distributeOneEvent(EventItem.RULE_CHANGE, eventModel);
    }

    /**
     * 签名事件
     *
     * @param dates 签名所属日期
     */
    public void signEvent(Date... dates) {

        List<EventModel> eventModelList = new ArrayList<>();
        for (Date date : dates) {

            HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(date);

            int code = hosDayModel == null ? 1 : hosDayModel.countCerNumber() + 1;
            code = code > 9 ? 9 : code; //签名次数最大值为9
            EventModel eventModel = new LogSignModel.Builder()
                    .setCount(code)
                    .recordDate(TimeUtil.utcToLocal(date.getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD_YY))
                    .build();
            eventModel.setComment("Sign for " + TimeUtil.utcToLocal(date.getTime(), SystemHelper.getUser().getTimeZone(), TimeUtil.MM_DD) + " Daily Log");
            eventModelList.add(eventModel);
            Logger.i(Tags.EVENT, "new signEvent, event id=" + eventModel.getId());
        }

        distributeEvents(eventModelList);
    }

    /**
     * 上报异常数据
     *
     * @param malfunctionType 故障类型
     * @param isClear         是否清除
     */
    public void malfunctionEvent(MalfunctionType malfunctionType, boolean isClear) {

        SelfCheckModel selfCheckModel = (SelfCheckModel) new SelfCheckModel.Builder()
                .malfunction(malfunctionType)
                .clear(isClear)
                .build();
        Logger.i(Tags.EVENT, "new malfunctionEvent, event id=" + selfCheckModel.getId());
        distributeOneEvent(EventItem.SELF_CHECK, selfCheckModel);
    }

    /**
     * 上报诊断数据
     *
     * @param diagnosticType 诊断类型
     * @param isClear        是否清除
     */
    public void diagnosticEvent(DiagnosticType diagnosticType, boolean isClear, String comment) {

        SelfCheckModel.Builder builder = new SelfCheckModel.Builder()
                .diagnostic(diagnosticType)
                .clear(isClear);

        if (!TextUtils.isEmpty(comment)) {

            builder.comment(comment);
        }
        SelfCheckModel selfCheckModel = (SelfCheckModel) builder.build();
        Logger.i(Tags.EVENT, "new diagnosticEvent, event id=" + selfCheckModel.getId());
        distributeOneEvent(EventItem.SELF_CHECK, selfCheckModel);
    }

    /**
     * 分发事件
     *
     * @param eventModels 所有事件
     */
    private void distributeEvents(final List<EventModel> eventModels) {

        if (eventModels == null || eventModels.isEmpty()) {

            return;
        }

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                List<Long> dailyLogIds = new ArrayList<>();

                for (EventModel eventModel : eventModels) {

                    //入库
                    Long id = recordToLocal(eventModel);
                    dailyLogIds.add(id);
                    //更新内存
                    ModelCenter.getInstance().updateEvent(EventItem.getEventItemByCode(eventModel.getType()), eventModel, id);
                }

                //上报服务端
                eventDataHandler.uploadNotifySync(eventModels, dailyLogIds);
            }
        });
    }

    /**
     * 分发一个事件
     *
     * @param eventItem  事件类型
     * @param eventModel 事件模型
     */
    public void distributeOneEvent(final EventItem eventItem, final EventModel eventModel) {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                distributeEvent(eventItem, eventModel);
            }
        });
    }

    /**
     * 分发事件
     *
     * @param eventModel 事件模型
     */
    private void distributeEvent(EventItem eventItem, EventModel eventModel) {
        //入库
        Long id = recordToLocal(eventModel);
        //更新内存
        ModelCenter.getInstance().updateEvent(eventItem, eventModel, id);
        //上报服务端
        eventDataHandler.uploadNotifySync(eventModel, id);
    }

    /**
     * 将数据存储至本地数据库
     *
     * @param eventModel 事件模型
     * @return 事件数据库ID
     */
    private Long recordToLocal(EventModel eventModel) {

        DailyLog dailyLog = new DailyLog();
        dailyLog.setJson(JsonUtil.toJSONString(eventModel));
        dailyLog.setOriginId(eventModel.getId());
        return DataBaseHelper.getDataBase().dailyLogDao().insert(dailyLog);
    }

    /**
     * 检查上报中间日志。
     * 如果当前是driving事件。开始启动一个一小时的定时器准备上报中间日志。
     * 中间如果切了状态，将定时器取消。
     */
    private Runnable intermediateRunnable = new Runnable() {
        @Override
        public void run() {

            EventModel eventModel = new IntermediateEventModel.Builder().build();
            distributeOneEvent(EventItem.INTERMEDIATE, eventModel);

            handler.postDelayed(intermediateRunnable, IntermediateEventModel.INTERMEDIATE_EVENT_INTERVAL);
        }
    };

    /**
     * 检查中间日志
     *
     * @param eventModel 时间模型
     */
    private void checkIntermediate(EventModel eventModel) {

        DriverState currentDriverState = ModelCenter.getInstance().getCurrentDriverState();

        if (currentDriverState != DriverState.DRIVING && currentDriverState != DriverState.YARD_MOVE && currentDriverState != DriverState.PERSONAL_USE) {

            if (DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.DRIVING ||
                    DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.YARD_MOVE ||
                    DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.PERSONAL_USE) {

                handler.postDelayed(intermediateRunnable, IntermediateEventModel.INTERMEDIATE_EVENT_INTERVAL);
            }
        } else if (currentDriverState == DriverState.DRIVING &&
                DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) != DriverState.DRIVING) {

            handler.removeCallbacks(intermediateRunnable);
            if (DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.YARD_MOVE ||
                    DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.PERSONAL_USE) {

                handler.postDelayed(intermediateRunnable, IntermediateEventModel.INTERMEDIATE_EVENT_INTERVAL);
            }
        } else if (currentDriverState == DriverState.YARD_MOVE &&
                DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) != DriverState.YARD_MOVE) {

            handler.removeCallbacks(intermediateRunnable);
            if (DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.DRIVING ||
                    DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.PERSONAL_USE) {

                handler.postDelayed(intermediateRunnable, IntermediateEventModel.INTERMEDIATE_EVENT_INTERVAL);
            }
        } else if (currentDriverState == DriverState.PERSONAL_USE &&
                DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) != DriverState.PERSONAL_USE) {

            handler.removeCallbacks(intermediateRunnable);
            if (DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.DRIVING ||
                    DriverState.getStateByCodeAndType(eventModel.getType(), eventModel.getCode()) == DriverState.YARD_MOVE) {

                handler.postDelayed(intermediateRunnable, IntermediateEventModel.INTERMEDIATE_EVENT_INTERVAL);
            }
        }
    }

    /**
     * 根据原始记录，合并未认领日志
     *
     * @param histories 历史记录
     * @return List<ModelCouple>
     */
    private List<ModelCouple> getModelCoupleByOriginHistory(List<History> histories) {

        List<ModelCouple> result = new ArrayList<>();
        DriverStatusModel drivingStatusModel = null;
        for (History history : histories) {

            Logger.i(Tags.EVENT, "new History，eventType:" + history.getType());
            if (drivingStatusModel != null) {

                //有了驾驶事件，但是不是停止的话直接丢弃
                if (history.getType() == HistoryType.VEHICLE_STATIC || history.getType() == HistoryType.ENGINE_OFF) {

                    Logger.i(Tags.EVENT, "one new couple log");
                    result.add(new ModelCouple(drivingStatusModel, getDriverStatusByHistory(history)));
                    drivingStatusModel = null;
                } else {

                    Logger.w(Tags.EVENT, "not static or engine off ,abort.");
                    continue;
                }
            } else {

                //没有驾驶事件并获得到了静止事件则直接丢弃。
                if (history.getType() != HistoryType.VEHICLE_MOVE) {

                    Logger.w(Tags.EVENT, "not moving, abort.");
                    continue;
                }
                Logger.i(Tags.EVENT, "new moving history");
                drivingStatusModel = getDriverStatusByHistory(history);
            }
        }

        return mergeCouple(result);
    }


    /**
     * 采用第一个couple的开始事件和第二个事件的结束时间merge成为一个事件
     *
     * @param modelCouples 原始事件
     * @return 合成结果
     */
    private List<ModelCouple> mergeCouple(List<ModelCouple> modelCouples) {

        //将事件的间隔小于五分钟的进行合并。
        for (int i = 0; i < modelCouples.size() - 1; i++) {

            if (modelCouples.get(i + 1).getDrivingModel().getDatetime() - modelCouples.get(i).getOdndModel().getDatetime() < 5 * 60 * 1000L) {

                modelCouples.set(i, new ModelCouple(modelCouples.get(i).getDrivingModel(), modelCouples.get(i + 1).getOdndModel()));
                modelCouples.remove(i + 1);
                mergeCouple(modelCouples);//递归调用本方法。直到没有数据
            }
        }
        List<ModelCouple> removeList = new ArrayList<>();
        for (ModelCouple modelCouple : modelCouples) {

            if ((modelCouple.getOdndModel().getDatetime() - modelCouple.getDrivingModel().getDatetime()) < 5 * 60 * 1000L) {

                removeList.add(modelCouple);
            }
        }
        modelCouples.removeAll(removeList);
        return modelCouples;
    }

    /**
     * 根据历史数据，获取司机状态
     *
     * @param history 历史数据
     * @return 司机状态
     */
    private DriverStatusModel getDriverStatusByHistory(History history) {

        DriverState state;
        if (history.getType() == HistoryType.VEHICLE_STATIC || history.getType() == HistoryType.ENGINE_OFF) {

            state = DriverState.ON_DUTY_NOT_DRIVING;
        } else {

            state = DriverState.DRIVING;
        }

        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
        builder.origin(DDLOriginEnum.AUTO_BY_ELD);
        builder.vehicleId(SystemHelper.getUser().getVehicleId());
        builder.odometer(history.getTotalOdometer());
        builder.engineHour(history.getTotalEngineHours());
        builder.location(null, history.getLatitude(), history.getLongitude());
        builder.date(new Date(history.getEventTime()));

        DriverStatusModel driverStatusModel = (DriverStatusModel) new DriverStatusModel.Builder()
                .state(state)
                .addition(builder.build())
                .build();

        driverStatusModel.setDriverId(0);
        return driverStatusModel;
    }

    /**
     * 根据历史事件，转化需要上报的历史事件
     *
     * @param histories
     * @return
     */
    private List<HistoryModel> getHistoryModelListByHistories(List<History> histories, int deviceType) {

        List<HistoryModel> models = new ArrayList<>();
        List<EventModel> engineModels = new ArrayList<>();
        for (History history : histories) {

            if (history.getType() == HistoryType.ENGINE_ON || history.getType() == HistoryType.ENGINE_OFF) {

                engineModels.add(getEngineEventModelByHistory(history));
            }
            HistoryModel historyModel = getHistoryModelByHistory(history, deviceType);
            models.add(historyModel);
        }
        setEngineEventModels(engineModels);
        return models;
    }

    private List<EventModel> engineEventModels = new ArrayList<>();

    private void setEngineEventModels(List<EventModel> models) {

        engineEventModels.clear();
        engineEventModels.addAll(models);
    }

    public List<EventModel> getEngineEventModels() {

        return engineEventModels;
    }

    /**
     * 原始历史数据转为可上报的数据类型
     *
     * @param history
     * @return
     */
    private HistoryModel getHistoryModelByHistory(History history, int deviceType) {

        HistoryEventType type;
        switch (history.getType()) {
            case HistoryType.ENGINE_ON:
                type = HistoryEventType.POWER_ON;
                break;
            case HistoryType.ENGINE_OFF:
                type = HistoryEventType.POWER_OFF;
                break;
            case HistoryType.VEHICLE_MOVE:
                type = HistoryEventType.MOVE;
                break;
            case HistoryType.VEHICLE_STATIC:
                type = HistoryEventType.STOP;
                break;
            default:
                type = HistoryEventType.OTHER;
                break;
        }

        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
        builder.origin(DDLOriginEnum.AUTO_BY_ELD);
        builder.vehicleId(SystemHelper.getUser().getVehicleId());
        builder.odometer(history.getTotalOdometer());
        builder.engineHour(history.getTotalEngineHours());
        builder.location(null, history.getLatitude(), history.getLongitude());
        builder.date(new Date(history.getEventTime()));

        HistoryModel historyModel = (HistoryModel) new HistoryModel.Builder()
                .state(type, deviceType)
                .addition(builder.build())
                .build();

        return historyModel;
    }

    /**
     * 历史事件转引擎事件
     *
     * @param history
     * @return
     */
    private EngineEventModel getEngineEventModelByHistory(History history) {

        EngineEventModel engineEventModel = (EngineEventModel) new EngineEventModel.Builder().isOn(history.getType() == HistoryType.ENGINE_ON ? true : false).build();
        engineEventModel.setDatetime(history.getEventTime());
        engineEventModel.setDriverId(0);
        return engineEventModel;
    }

}