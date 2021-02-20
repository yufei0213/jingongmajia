package com.unitedbustech.eld.system;

import android.app.Activity;
import android.location.Location;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.TeamWorkDashBoardEventType;
import com.unitedbustech.eld.common.TeamWorkMsgType;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.UserRole;
import com.unitedbustech.eld.common.VehicleConnectType;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.device.ConfigOption;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.TeamWorkDashBoardChangeEvent;
import com.unitedbustech.eld.eventbus.TeamWorkServiceEvent;
import com.unitedbustech.eld.eventbus.VehicleConnectEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.PromptDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 团队驾驶服务类
 */
public class TeamWorkService {

    private static final String TAG = "TeamWorkService";

    private static TeamWorkService instance = null;

    public static TeamWorkService getInstance() {

        if (instance == null) {

            instance = new TeamWorkService();
        }

        return instance;
    }

    public void init() {

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }

    public void destroy() {

        try {

            EventBus.getDefault().unregister(this);
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            instance = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onTeamWorkServiceEvent(TeamWorkServiceEvent event) {

        TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
        int role = teamWorkState.getUserRole();

        Logger.i(TAG, "收到teamwork事件, 当前用户: user=" + JsonUtil.toJSONString(SystemHelper.getUser()));
        Logger.i(TAG, "收到的消息: content=" + JsonUtil.toJSONString(event));

        switch (role) {

            case UserRole.NORMAL: {

                //当前是正常模式，只会收到邀请成为副驾的信息
                if (event.getType() == TeamWorkMsgType.PILOT_INVITE_COPILOT) {

                    pilotInviteCopilot(event);
                } else {

                    Logger.w(Tags.MSG, "received teamwork msg which doesn't belong to driver");
                }
                break;
            }
            case UserRole.PILOT: {

                switch (event.getType()) {

                    case TeamWorkMsgType.COPILOT_ACCEPT_INVITE: {
                        //副驾驶接受邀请
                        copilotAcceptInvite(event);
                        break;
                    }
                    case TeamWorkMsgType.COPILOT_REFUSE_INVITE: {
                        //副驾驶拒绝邀请
                        copilotRefuseInvite(event);
                        break;
                    }
                    case TeamWorkMsgType.COPILOT_ACCEPT_SWITCH: {
                        //副驾驶接受角色切换请求
                        copilotAcceptSwitch(event);
                        break;
                    }
                    case TeamWorkMsgType.COPILOT_REFUSE_SWITCH: {
                        //副驾驶拒绝角色切换请求
                        copilotRefuseSwitch(event);
                        break;
                    }
                    case TeamWorkMsgType.ORIGIN_PILOT_DISCONNECT_VEHICLE: {
                        //切换角色后，原主驾驶已经断开车辆连接
                        originPilotDisconnectVehicle(event);
                        break;
                    }
                    case TeamWorkMsgType.COPILOT_EXIT: {
                        //副驾驶离开
                        copilotExit(event);
                        break;
                    }
                    default: {

                        Logger.w(Tags.MSG, "received teamwork msg which doesn't belong to driver");
                        break;
                    }
                }
                break;
            }
            case UserRole.COPILOT: {

                switch (event.getType()) {

                    case TeamWorkMsgType.PILOT_REQUEST_SWITCH: {
                        //主驾驶请求切换角色
                        pilotRequestSwitch(event);
                        break;
                    }
                    case TeamWorkMsgType.PILOT_REMOVE_COPILOT: {
                        //主驾驶移除副驾驶
                        pilotRemoveCopilot(event);
                        break;
                    }
                    default: {

                        Logger.w(Tags.MSG, "received teamwork msg which doesn't belong to driver");
                        break;
                    }
                }
                break;
            }
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleSelectEvent(VehicleSelectEvent vehicleSelectEvent) {

        int vehicleId = SystemHelper.getUser().getVehicleId();
        Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(vehicleId);

        //如果车辆选择被清除，并且当前处于团队驾驶模式，则将团队解散
        if (vehicle == null) {

            User user = SystemHelper.getUser();

            TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
            int role = teamWorkState.getUserRole();

            switch (role) {

                case UserRole.PILOT:

                    HttpRequest removeCopilotHttpRequest = new HttpRequest.Builder()
                            .url(Constants.API_PILOT_REMOVE_COPILOT)
                            .addParam("access_token", user.getAccessToken())
                            .addParam("team_id", Integer.toString(teamWorkState.getTeamId()))
                            .build();

                    HttpResponse removeCopilotHttpResponse = removeCopilotHttpRequest.get();

                    if (removeCopilotHttpResponse.isSuccess()) {

                        Logger.d(TAG, "主驾驶移除副驾驶成功");

                        //用户角色普通
                        SystemHelper.setTeamWorkState(null);
                        //通知界面更新
                        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(TeamWorkDashBoardEventType.BECOME_NORMAL));
                    } else {

                        Logger.d(TAG, "主驾驶移除副驾驶失败，code=" + removeCopilotHttpResponse.getCode() +
                                ", msg=" + removeCopilotHttpResponse.getMsg() +
                                ", team_id=" + teamWorkState.getTeamId());
                    }
                    break;
                case UserRole.COPILOT:

                    HttpRequest copilotExitHttpRequest = new HttpRequest.Builder()
                            .url(Constants.API_COPILOT_EXIT)
                            .addParam("access_token", user.getAccessToken())
                            .addParam("team_id", Integer.toString(teamWorkState.getTeamId()))
                            .build();

                    HttpResponse copilotExitHttpResponse = copilotExitHttpRequest.get();

                    if (copilotExitHttpResponse.isSuccess()) {

                        Logger.d(TAG, "副驾驶离开团队成功");

                        //用户角色普通
                        SystemHelper.setTeamWorkState(null);
                        //通知界面更新
                        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(TeamWorkDashBoardEventType.BECOME_NORMAL));
                    } else {

                        Logger.d(TAG, "副驾驶离开团队失败，code=" + copilotExitHttpResponse.getCode() +
                                ", msg=" + copilotExitHttpResponse.getMsg() +
                                ", team_id=" + teamWorkState.getTeamId());
                    }

                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 被邀请成为副驾驶
     * 弹窗提示，可以选择拒绝和接受
     *
     * @param event 消息体
     */
    private void pilotInviteCopilot(final TeamWorkServiceEvent event) {

        final Activity activity = ActivityStack.getInstance().getCurrentActivity();
        final String text = activity.getResources().getString(R.string.teamwork_pilot_invite_copilot)
                .replace("#pilot#", event.getPilotName());
        Logger.i(TAG, "被邀请成为副驾驶，弹窗提示，可以选择拒绝和接受");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new HorizontalDialog.Builder(activity)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(text)
                        .setNegativeBtn(R.string.reject, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                                loadingDialog.show();

                                ThreadUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        Logger.i(TAG, "拒绝邀请成为副驾驶");
                                        User user = SystemHelper.getUser();

                                        HttpRequest httpRequest = new HttpRequest.Builder()
                                                .url(Constants.API_COPILOT_RESPONSE_PILOT_INVITE)
                                                .addParam("access_token", user.getAccessToken())
                                                .addParam("team_id", Integer.toString(event.getTeamId()))
                                                .addParam("approve", "0")
                                                .build();

                                        httpRequest.get();

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                loadingDialog.dismiss();
                                                new PromptDialog.Builder(activity)
                                                        .type(PromptDialog.SUCCESS)
                                                        .build()
                                                        .show();
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNeutralBtn(R.string.accept, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                Logger.i(TAG, "接受邀请成为副驾驶");
                                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                                loadingDialog.show();

                                ThreadUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        User user = SystemHelper.getUser();

                                        HttpRequest httpRequest = new HttpRequest.Builder()
                                                .url(Constants.API_COPILOT_RESPONSE_PILOT_INVITE)
                                                .addParam("access_token", user.getAccessToken())
                                                .addParam("team_id", Integer.toString(event.getTeamId()))
                                                .addParam("approve", "1")
                                                .build();

                                        final HttpResponse httpResponse = httpRequest.get();

                                        if (httpResponse.isSuccess()) {

                                            //选择车辆
                                            SystemHelper.setVehicle(event.getVehicleId());
                                            //通知DashBoard
                                            EventBus.getDefault().post(new VehicleSelectEvent());

                                            //用户角色为副驾驶
                                            TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
                                            teamWorkState.setUserRole(UserRole.COPILOT);
                                            teamWorkState.setTeamId(event.getTeamId());
                                            teamWorkState.setPartnerId(event.getPilotId());
                                            teamWorkState.setEvent(event);
                                            SystemHelper.setTeamWorkState(teamWorkState);

                                            //发送通知，刷新DashBoard页面
                                            EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.BECOME_COPILOT));

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    loadingDialog.dismiss();

                                                    new PromptDialog.Builder(activity)
                                                            .type(PromptDialog.SUCCESS)
                                                            .build()
                                                            .show();
                                                }
                                            });
                                        } else {

                                            //主驾驶已经取消组队
                                            if (httpResponse.getCode() == RequestStatus.PILOT_HAS_CANCEL_TEAMWORK.getCode()) {

                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        loadingDialog.dismiss();

                                                        new HorizontalDialog.Builder(activity)
                                                                .setIcon(R.drawable.ic_emoji_love)
                                                                .setText(activity.getString(R.string.teamwork_pilot_has_cancel))
                                                                .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(HorizontalDialog dialog, int which) {

                                                                        dialog.dismiss();
                                                                    }
                                                                })
                                                                .build()
                                                                .show();
                                                    }
                                                });
                                            } else {

                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        new PromptDialog.Builder(activity)
                                                                .type(PromptDialog.FAILURE)
                                                                .build()
                                                                .show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .setCancelable(false)
                        .build().show();
            }
        });
    }

    /**
     * 副驾驶接受邀请
     *
     * @param event 消息体
     */
    private void copilotAcceptInvite(TeamWorkServiceEvent event) {

        Logger.i(TAG, "副驾驶接受邀请---copilotAcceptInvite");
        TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
        teamWorkState.setEvent(event);
        SystemHelper.setTeamWorkState(teamWorkState);

        //发送通知，刷新DashBoard页面
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_ACCEPT_INVITE));
    }

    /**
     * 副驾驶拒绝邀请
     *
     * @param event 消息体
     */
    private void copilotRefuseInvite(TeamWorkServiceEvent event) {

        Logger.i(TAG, "副驾驶拒绝邀请---copilotRefuseInvite");
        //用户角色普通
        SystemHelper.setTeamWorkState(null);

        //发送通知，刷新DashBoard页面
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_REFUSE_INVITE));
    }

    /**
     * 副驾驶接受角色切换请求
     * <p>
     * 主驾驶成为副驾驶
     *
     * @param event 消息体
     */
    private void copilotAcceptSwitch(TeamWorkServiceEvent event) {

        TeamWorkState state = SystemHelper.getTeamWorkState();

        //用户角色副驾驶
        state.setUserRole(UserRole.COPILOT);
        state.setTeamId(event.getTeamId());
        state.setSwitchTeamId(0);
        state.setPartnerId(event.getPilotId());
        state.setEvent(event);
        SystemHelper.setTeamWorkState(state);

        //如果当前主驾驶不是ODND状态，自动切换为ODND
        if (ModelCenter.getInstance().getCurrentDriverState() != DriverState.ON_DUTY_NOT_DRIVING) {

            Location location = LocationHandler.getInstance().getCurrentLocation();
            StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
            builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
            if (location != null) {

                builder.location(null, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
            }

            EventCenter.getInstance().changeDriverState(DriverState.ON_DUTY_NOT_DRIVING, builder.build());
        }

        //发送通知，刷新DashBoard页面
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_ACCEPT_SWITCH));

        //断开车辆连接
        DataCollectorHandler.getInstance().stopDeviceModel();

        //通知当前主驾驶，可以连接车辆
        HttpRequest httpRequest = new HttpRequest.Builder()
                .url(Constants.API_ORIGIN_PILOT_DISCONNNECT_VEHICLE)
                .addParam("access_token", SystemHelper.getUser().getAccessToken())
                .addParam("team_id", Integer.toString(event.getTeamId()))
                .build();
        HttpResponse httpResponse = httpRequest.get();
        if (httpResponse.isSuccess()) {

            Logger.d(TAG, "已经断开车辆连接并且通知新的主驾驶");
        } else {

            Logger.w(Tags.MSG, "has disconnect vehicle, but notify copilot failed.");
        }
    }

    /**
     * 副驾驶拒绝角色切换
     * <p>
     * 主驾驶依然是主驾驶，副驾驶依然是副驾驶
     *
     * @param event 消息体
     */
    private void copilotRefuseSwitch(TeamWorkServiceEvent event) {

        TeamWorkState state = SystemHelper.getTeamWorkState();
        state.setSwitchTeamId(0);
        SystemHelper.setTeamWorkState(state);

        //发送通知，刷新DashBoard页面
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_REFUSE_SWITCH));
    }

    /**
     * 原主驾驶已经断开车辆连接
     *
     * @param event 消息体
     */
    private void originPilotDisconnectVehicle(final TeamWorkServiceEvent event) {

        Logger.d(TAG, "原主驾驶已经断开车辆连接，现在开始连接车辆");

        Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(event.getVehicleId());
        if (vehicle == null) {

            Logger.w(Tags.MSG, "old pilot has disconnect vehicle, but new pilot has not such vehicle.");
            return;
        }
        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        final ConfigOption option = new ConfigOption.Builder()
                .vehicleNumber(vehicle.getCode())
                .bluetoothAddress(vehicle.getEcmSn())
                .deviceType(ConfigOption.getDeviceTypeByEcmLinkType(vehicle.getEcmLinkType()))
                .build();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                DataCollectorHandler.getInstance().startDeviceModel(option);
            }
        });
    }

    /**
     * 副驾驶离开团队
     * <p>
     * 团队解散，用户角色 normal
     *
     * @param event 消息体
     */
    private void copilotExit(TeamWorkServiceEvent event) {

        //用户角色普通
        SystemHelper.setTeamWorkState(null);

        //发送通知，刷新DashBoard页面
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.COPILOT_EXIT));
    }

    /**
     * 主驾驶发起角色切换请求
     *
     * @param event 消息体
     */
    private void pilotRequestSwitch(final TeamWorkServiceEvent event) {

        final Activity activity = ActivityStack.getInstance().getCurrentActivity();
        final String text = activity.getResources().getString(R.string.teamwork_pilot_request_switch)
                .replace("#pilot#", event.getCopilotName());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new HorizontalDialog.Builder(activity)
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(text)
                        .setNegativeBtn(R.string.reject, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                                loadingDialog.show();

                                ThreadUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        User user = SystemHelper.getUser();
                                        TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                                        HttpRequest httpRequest = new HttpRequest.Builder()
                                                .url(Constants.API_COPILOT_RESPONSE_REQUEST_SWITCH)
                                                .addParam("access_token", user.getAccessToken())
                                                .addParam("old_team_id", Integer.toString(teamWorkState.getTeamId()))
                                                .addParam("new_team_id", Integer.toString(event.getTeamId()))
                                                .addParam("approve", "0")
                                                .build();

                                        httpRequest.get();

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                loadingDialog.dismiss();
                                                new PromptDialog.Builder(activity)
                                                        .type(PromptDialog.SUCCESS)
                                                        .build()
                                                        .show();
                                            }
                                        });
                                    }
                                });
                            }
                        })
                        .setNeutralBtn(R.string.accept, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();

                                final LoadingDialog loadingDialog = new LoadingDialog(activity);
                                loadingDialog.show();

                                ThreadUtil.getInstance().execute(new Runnable() {
                                    @Override
                                    public void run() {

                                        User user = SystemHelper.getUser();
                                        TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                                        HttpRequest httpRequest = new HttpRequest.Builder()
                                                .url(Constants.API_COPILOT_RESPONSE_REQUEST_SWITCH)
                                                .addParam("access_token", user.getAccessToken())
                                                .addParam("old_team_id", Integer.toString(teamWorkState.getTeamId()))
                                                .addParam("new_team_id", Integer.toString(event.getTeamId()))
                                                .addParam("approve", "1")
                                                .build();

                                        final HttpResponse httpResponse = httpRequest.get();

                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                loadingDialog.dismiss();
                                            }
                                        });

                                        if (httpResponse.isSuccess()) {

                                            //用户角色为主驾驶
                                            teamWorkState.setUserRole(UserRole.PILOT);
                                            teamWorkState.setTeamId(event.getTeamId());
                                            teamWorkState.setEvent(event);
                                            SystemHelper.setTeamWorkState(teamWorkState);

                                            //发送通知，刷新DashBoard页面
                                            EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.BECOME_PILOT));
                                            EventBus.getDefault().post(new VehicleConnectEvent(VehicleConnectType.DIS_CONNECTED));

                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {

                                                    new PromptDialog.Builder(activity)
                                                            .type(PromptDialog.SUCCESS)
                                                            .build()
                                                            .show();
                                                }
                                            });
                                        } else {

                                            //主驾驶已经取消角色切换请求
                                            if (httpResponse.getCode() == RequestStatus.PILOT_HAS_CANCEL_SWITCH.getCode()) {

                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        new HorizontalDialog.Builder(activity)
                                                                .setIcon(R.drawable.ic_emoji_love)
                                                                .setText(RequestStatus.getMsg(httpResponse.getCode()))
                                                                .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(HorizontalDialog dialog, int which) {

                                                                        dialog.dismiss();
                                                                    }
                                                                })
                                                                .build()
                                                                .show();
                                                    }
                                                });
                                            } else {

                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        new PromptDialog.Builder(activity)
                                                                .type(PromptDialog.FAILURE)
                                                                .build()
                                                                .show();
                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .setCancelable(false)
                        .build()
                        .show();
            }
        });
    }

    /**
     * 主驾驶移除副驾驶
     * <p>
     * 团队解散，用户角色 normal
     *
     * @param event 消息体
     */
    private void pilotRemoveCopilot(TeamWorkServiceEvent event) {

        //用户角色普通
        SystemHelper.setTeamWorkState(null);
        //清除车辆选择
        SystemHelper.clearVehicle();

        //发送通知，刷新DashBoard页面
        EventBus.getDefault().post(new VehicleSelectEvent());
        EventBus.getDefault().post(new TeamWorkDashBoardChangeEvent(event, TeamWorkDashBoardEventType.PILOT_REMOVE_COPILOT));
    }
}
