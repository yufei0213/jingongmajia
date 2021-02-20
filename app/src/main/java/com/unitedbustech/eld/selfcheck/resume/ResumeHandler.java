package com.unitedbustech.eld.selfcheck.resume;

import android.app.Activity;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.TeamWorkDashBoardEventType;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.UserRole;
import com.unitedbustech.eld.common.VehicleConnectType;
import com.unitedbustech.eld.common.vo.TeamWorkMsgVo;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.TeamWorkDashBoardChangeEvent;
import com.unitedbustech.eld.eventbus.TeamWorkServiceEvent;
import com.unitedbustech.eld.eventbus.VehicleConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.PromptDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * @author yufei0213
 * @date 2018/4/17
 * @description ResumeHandler
 */
public class ResumeHandler {

    /**
     * App启动后启动自检
     */
    @WorkerThread
    public void selfCheck() {

        Logger.i(Tags.SELF_CHECK, "start check to resume");

        User user = SystemHelper.getUser();

        if (LanguageUtil.getInstance().isChangingLanguage()) {

            Logger.i(Tags.SELF_CHECK, "isChangingLanguage, request teamWorkState");

            LanguageUtil.getInstance().setChangingLanguage(false);

            TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
            if (teamWorkState != null) {

                switch (teamWorkState.getUserRole()) {

                    case UserRole.NORMAL:

                        Logger.i(Tags.SELF_CHECK, "UserRole.NORMAL, resume international");
                        handleNormalResumeInternational(user);
                        break;
                    case UserRole.PILOT:

                        Logger.i(Tags.SELF_CHECK, "UserRole.PILOT, resume international");
                        handlePilotResumeInternational(user, teamWorkState.getEvent());
                        break;
                    case UserRole.COPILOT:

                        Logger.i(Tags.SELF_CHECK, "UserRole.COPILOT, resume international");
                        handleCopilotResumeInternational(user, teamWorkState.getEvent());
                        break;
                    default:
                        break;
                }
            } else {

                Logger.i(Tags.SELF_CHECK, "there is no teamWork info, resume international");
                handleNormalResumeInternational(user);
            }
        } else {

            Logger.i(Tags.SELF_CHECK, "app start, request teamWorkState");
            TeamWorkMsgVo teamWorkMsgVo = getTeamWorkState(user);
            if (teamWorkMsgVo == null) {

                Logger.i(Tags.SELF_CHECK, "there is no teamWork info");

                //重置团队驾驶
                SystemHelper.setTeamWorkState(null);

                int vehicleId = SystemHelper.getUser().getVehicleId();
                final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
                DriverState driverState = ModelCenter.getInstance().getCurrentDriverState();

                Logger.i(Tags.SELF_CHECK, "vehicleId=" + vehicleId + ", driverState=" + driverState.toVisibleString());

                if (vehicle != null && (driverState == DriverState.DRIVING || driverState == DriverState.PERSONAL_USE || driverState == DriverState.YARD_MOVE)) {

                    Activity activity = ActivityStack.getInstance().getCurrentActivity();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            EventBus.getDefault().post(new VehicleSelectEvent());
                            EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.DIS_CONNECTED));
                            ConfigOption option = new ConfigOption.Builder()
                                    .vehicleNumber(vehicle.getCode())
                                    .bluetoothAddress(vehicle.getEcmSn())
                                    .deviceType(ConfigOption.getDeviceTypeByEcmLinkType(vehicle.getEcmLinkType()))
                                    .build();
                            DataCollectorHandler.getInstance().startDeviceModel(option);
                        }
                    });
                } else {

                    //清除车辆选择
                    SystemHelper.clearVehicle();
                }
            } else {

                Logger.i(Tags.SELF_CHECK, "teamWorkState: " + JsonUtil.toJSONString(teamWorkMsgVo));

                //如果当前用户是主驾驶
                if (teamWorkMsgVo.getDriverId() == user.getDriverId()) {

                    handlePilotResume(user, new TeamWorkServiceEvent(teamWorkMsgVo));
                }

                //如果当前用户是副驾驶
                if (teamWorkMsgVo.getCoDriverId() == user.getDriverId()) {

                    handleCopilotResume(user, new TeamWorkServiceEvent(teamWorkMsgVo));
                }
            }
        }
    }

    /**
     * 获取团队驾驶的状态
     *
     * @param user 用户信息
     * @return 团队驾驶信息
     */
    private TeamWorkMsgVo getTeamWorkState(User user) {

        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_TEAMWORK_STATE)
                .addParam("access_token", user.getAccessToken())
                .build();

        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            JSONObject jsonObject = JsonUtil.parseObject(httpResponse.getData());
            String stateData = JsonUtil.getString(jsonObject, "coDriver");
            if (TextUtils.isEmpty(stateData)) {

                return null;
            } else {

                TeamWorkMsgVo msgVo = JsonUtil.parseObject(stateData, TeamWorkMsgVo.class);
                return msgVo;
            }
        } else {

            return null;
        }
    }

    /**
     * 处理恢复主驾驶的逻辑
     *
     * @param user  用户信息
     * @param event 消息内容
     */
    private void handlePilotResume(final User user, final TeamWorkServiceEvent event) {

        int vehicleId = user.getVehicleId();
        final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);

        final Activity activity = ActivityStack.getInstance().getCurrentActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                HorizontalDialog dialog = new HorizontalDialog.Builder(activity)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(R.string.team_work_resume)
                        .setNegativeBtn(R.string.no, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(final HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                                loadingDialog.show();

                                ThreadUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        HttpRequest httpRequest = new HttpRequest.Builder()
                                                .url(Constants.API_PILOT_REMOVE_COPILOT)
                                                .addParam("access_token", user.getAccessToken())
                                                .addParam("team_id", Integer.toString(event.getTeamId()))
                                                .build();

                                        HttpResponse httpResponse = httpRequest.get();
                                        if (httpResponse.isSuccess()) {

                                            //更新本地用户角色
                                            SystemHelper.setTeamWorkState(null);
                                            //清除车辆选择
                                            SystemHelper.clearVehicle();

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    loadingDialog.dismiss();

                                                    PromptDialog promptDialog = new PromptDialog.Builder(activity)
                                                            .type(PromptDialog.SUCCESS)
                                                            .build();
                                                    promptDialog.show();
                                                }
                                            });
                                        } else {

                                            loadingDialog.dismiss();

                                            PromptDialog promptDialog = new PromptDialog.Builder(activity)
                                                    .type(PromptDialog.FAILURE)
                                                    .listener(new PromptDialog.OnHideListener() {
                                                        @Override
                                                        public void onHide() {

                                                            //如果失败了，递归处理
                                                            handlePilotResume(user, event);
                                                        }
                                                    })
                                                    .build();
                                            promptDialog.show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNeutralBtn(R.string.yes, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                //更新本地用户角色
                                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
                                if (teamWorkState == null) {

                                    teamWorkState = new TeamWorkState();
                                }
                                teamWorkState.setTeamId(event.getTeamId());
                                teamWorkState.setPartnerId(event.getCopilotId());
                                teamWorkState.setUserRole(UserRole.PILOT);
                                teamWorkState.setEvent(event);
                                SystemHelper.setTeamWorkState(teamWorkState);

                                //通知界面刷新
                                EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_ACCEPT_INVITE));
                                if (vehicle != null) {

                                    EventBus.getDefault().post(new VehicleSelectEvent());
                                    EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.DIS_CONNECTED));
                                    ConfigOption option = new ConfigOption.Builder()
                                            .vehicleNumber(vehicle.getCode())
                                            .bluetoothAddress(vehicle.getEcmSn())
                                            .deviceType(ConfigOption.getDeviceTypeByEcmLinkType(vehicle.getEcmLinkType()))
                                            .build();
                                    DataCollectorHandler.getInstance().startDeviceModel(option);
                                }

                                PromptDialog promptDialog = new PromptDialog.Builder(activity)
                                        .type(PromptDialog.SUCCESS)
                                        .build();
                                promptDialog.show();
                            }
                        })
                        .setCancelable(false)
                        .build();
                dialog.show();
            }
        });
    }

    /**
     * 处理恢复主驾驶的逻辑（国际化）
     *
     * @param user  用户信息
     * @param event 消息内容
     */
    private void handlePilotResumeInternational(User user, TeamWorkServiceEvent event) {

        //通知界面刷新
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_ACCEPT_INVITE));
        int vehicleId = user.getVehicleId();
        final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
        if (vehicle != null) {

            EventBus.getDefault().post(new VehicleSelectEvent());

            Activity activity = ActivityStack.getInstance().getCurrentActivity();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    CollectorType collectorType = DataCollectorHandler.getInstance().getCurrentCollectorType();
                    if (collectorType == CollectorType.DEVICE) {

                        EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.CONNECTED));
                    } else {

                        EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.DIS_CONNECTED));
                    }
                }
            });
        }
    }

    /**
     * 处理恢复正常状态下链接车辆（国际化）
     *
     * @param user 用户信息
     */
    private void handleNormalResumeInternational(User user) {

        //通知界面刷新
        int vehicleId = user.getVehicleId();
        final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
        if (vehicle != null) {

            EventBus.getDefault().post(new VehicleSelectEvent());

            CollectorType collectorType = DataCollectorHandler.getInstance().getCurrentCollectorType();
            if (collectorType == CollectorType.DEVICE) {

                EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.CONNECTED));
            } else {

                EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.DIS_CONNECTED));
            }
        }
    }

    /**
     * 处理恢复副驾驶的逻辑
     *
     * @param user  用户信息
     * @param event 消息内容
     */
    private void handleCopilotResume(final User user, final TeamWorkServiceEvent event) {

        int vehicleId = user.getVehicleId();
        final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);

        final Activity activity = ActivityStack.getInstance().getCurrentActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                HorizontalDialog dialog = new HorizontalDialog.Builder(activity)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(R.string.team_work_resume)
                        .setNegativeBtn(R.string.no, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(final HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                                loadingDialog.show();

                                ThreadUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        HttpRequest httpRequest = new HttpRequest.Builder()
                                                .url(Constants.API_COPILOT_EXIT)
                                                .addParam("access_token", user.getAccessToken())
                                                .addParam("team_id", Integer.toString(event.getTeamId()))
                                                .build();

                                        HttpResponse httpResponse = httpRequest.get();
                                        if (httpResponse.isSuccess()) {

                                            //更新本地用户角色
                                            SystemHelper.setTeamWorkState(null);
                                            //清除车辆选择
                                            SystemHelper.clearVehicle();

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    loadingDialog.dismiss();

                                                    PromptDialog promptDialog = new PromptDialog.Builder(activity)
                                                            .type(PromptDialog.SUCCESS)
                                                            .build();
                                                    promptDialog.show();
                                                }
                                            });
                                        } else {

                                            loadingDialog.dismiss();

                                            PromptDialog promptDialog = new PromptDialog.Builder(activity)
                                                    .type(PromptDialog.FAILURE)
                                                    .listener(new PromptDialog.OnHideListener() {
                                                        @Override
                                                        public void onHide() {

                                                            //如果失败了，递归处理
                                                            handleCopilotResume(user, event);
                                                        }
                                                    })
                                                    .build();
                                            promptDialog.show();
                                        }
                                    }
                                });
                            }
                        })
                        .setNeutralBtn(R.string.yes, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                //通知界面刷新
                                EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.BECOME_COPILOT));
                                if (vehicle != null) {

                                    EventBus.getDefault().post(new VehicleSelectEvent());
                                }

                                //更新本地用户角色
                                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
                                if (teamWorkState == null) {

                                    teamWorkState = new TeamWorkState();
                                }
                                teamWorkState.setTeamId(event.getTeamId());
                                teamWorkState.setPartnerId(event.getPilotId());
                                teamWorkState.setUserRole(UserRole.COPILOT);
                                teamWorkState.setEvent(event);
                                SystemHelper.setTeamWorkState(teamWorkState);

                                PromptDialog promptDialog = new PromptDialog.Builder(activity)
                                        .type(PromptDialog.SUCCESS)
                                        .build();
                                promptDialog.show();
                            }
                        })
                        .setCancelable(false)
                        .build();
                dialog.show();
            }
        });
    }

    /**
     * 处理回复副驾驶的逻辑 （国际化）
     *
     * @param user  用户信息
     * @param event 消息内容
     */
    private void handleCopilotResumeInternational(User user, TeamWorkServiceEvent event) {

        //通知界面刷新
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.BECOME_COPILOT));
        int vehicleId = user.getVehicleId();
        final Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);
        if (vehicle != null) {

            EventBus.getDefault().post(new VehicleSelectEvent());
        }
    }
}
