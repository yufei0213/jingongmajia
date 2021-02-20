package com.unitedbustech.eld.jsinterface;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.TeamWorkDashBoardEventType;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.UserRole;
import com.unitedbustech.eld.common.vo.DriverVo;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.Vehicle;
import com.unitedbustech.eld.eventbus.TeamWorkDashBoardChangeEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.UIWebView;
import com.unitedbustech.eld.web.BaseJsInterface;
import com.unitedbustech.eld.web.JsInterface;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/2/6
 * @description 副驾驶相关的js接口
 */
@JsInterface(name = "teamwork")
public class TeamWorkJsInterface extends BaseJsInterface {

    private static final String TAG = "TeamWorkJsInterface";

    private static final String GET_COPILOT_LIST = "getCopilotList";
    private static final String PILOT_INVITE_COPILOT = "pilotInviteCopilot";
    private static final String PILOT_CANCEL_INVITE_COPILOT = "pilotCancelInviteCopilot";
    private static final String PILOT_REQUEST_SWITCH = "pilotRequestSwitch";
    private static final String PILOT_CANCEL_SWITCH_REQUEST = "pilotCancelSwitchRequest";
    private static final String PILOT_REMOVE_COPILOT = "pilotRemoveCopilot";
    private static final String COPILOT_EXIT = "copilotExit";

    public TeamWorkJsInterface(Context context, UIWebView uiWebView) {

        super(context, uiWebView);
    }

    @Override
    @JavascriptInterface
    public void method(String params) {

        JSONObject obj = JsonUtil.parseObject(params);

        String method = JsonUtil.getString(obj, METHOD_KEY);
        String data = JsonUtil.getString(obj, DATA_KEY);

        switch (method) {

            case GET_COPILOT_LIST:

                getCoDriverList(data);
                break;
            case PILOT_INVITE_COPILOT:

                pilotInviteCopilot(data);
                break;
            case PILOT_CANCEL_INVITE_COPILOT:

                pilotCancelInviteCopilot(data);
                break;
            case PILOT_REQUEST_SWITCH:

                pilotRequestSwitch(data);
                break;
            case PILOT_CANCEL_SWITCH_REQUEST:

                pilotCancelSwitchRequest(data);
                break;
            case PILOT_REMOVE_COPILOT:

                pilotRemoveCopilot(data);
                break;
            case COPILOT_EXIT:

                copilotExit(data);
                break;
            default:
                break;
        }
    }

    /**
     * 获取可用附加列表
     *
     * @param data 回调函数
     */
    private void getCoDriverList(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_COPILOT_LIST)
                        .addParam("access_token", user.getAccessToken())
                        .build();

                HttpResponse httpResponse = httpRequest.get();
                if (httpResponse.isSuccess()) {

                    JSONObject jsonObject = JsonUtil.parseObject(httpResponse.getData());
                    String copilotListStr = JsonUtil.getString(jsonObject, "driverList");

                    Logger.d(TAG, "获取可用副驾列表成功，driverList=" + copilotListStr);

                    final List<DriverVo> result = JsonUtil.parseArray(copilotListStr, DriverVo.class);
                    Collections.sort(result);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "', '" + JsonUtil.toJsJSONString(result) + "');");
                        }
                    });
                } else {

                    Logger.d(TAG, "获取可用副驾驶列表失败, code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg());

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
     * 发起副驾驶邀请
     *
     * @param data 回调函数
     */
    private void pilotInviteCopilot(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        String driverStr = JsonUtil.getString(jsonObject, "copilot");
        final DriverVo copilotVo = JsonUtil.parseObject(driverStr, DriverVo.class);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_PILOT_INVITE_COPILOT)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                        .addParam("co_driver_id", Integer.toString(copilotVo.getId()))
                        .build();

                HttpResponse httpResponse = httpRequest.get();

                if (httpResponse.isSuccess()) {

                    Logger.i(TAG, "主驾驶向副驾驶发送邀请成功");

                    JSONObject teamIdObj = JsonUtil.parseObject(httpResponse.getData());
                    int teamId = JsonUtil.getInt(teamIdObj, "teamId");

                    Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(user.getDriverId());
                    Vehicle vehicle = DataBaseHelper.getDataBase().vehicleDao().getVehicle(user.getVehicleId());

                    //构建主驾驶邀请副驾驶的事件，发送给DashBoard刷新页面
                    TeamWorkDashBoardChangeEvent event = new TeamWorkDashBoardChangeEvent();
                    event.setTeamId(teamId);
                    event.setType(TeamWorkDashBoardEventType.BECOME_PILOT);
                    event.setPilotId(driver.getId());
                    event.setPilotName(driver.getName());
                    event.setCopilotId(copilotVo.getId());
                    event.setCopilotName(copilotVo.getName());
                    event.setVehicleId(vehicle.getId());
                    event.setVehicleCode(vehicle.getCode());

                    EventBus.getDefault().post(event);

                    //当前用户角色为主驾驶
                    TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();
                    teamWorkState.setUserRole(UserRole.PILOT);
                    teamWorkState.setTeamId(teamId);
                    teamWorkState.setPartnerId(copilotVo.getId());
                    SystemHelper.setTeamWorkState(teamWorkState);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {

                    Logger.i(TAG, "主驾驶向副驾驶发送邀请失败，code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg() +
                            ", co_driver_id=" + copilotVo.getId() +
                            ", vehicle_id=" + user.getVehicleId());

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
     * 主驾驶取消邀请
     *
     * @param data 数据体
     */
    private void pilotCancelInviteCopilot(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_PILOT_CANCEL_INVITE_COPILOT)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("team_id", Integer.toString(teamWorkState.getTeamId()))
                        .build();

                HttpResponse httpResponse = httpRequest.get();

                if (httpResponse.isSuccess()) {

                    Logger.d(TAG, "主驾驶取消邀请成功");

                    //用户角色为普通
                    SystemHelper.setTeamWorkState(null);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {

                    Logger.d(TAG, "主驾驶取消邀请失败，code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg() +
                            ", team_id=" + teamWorkState.getTeamId());

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
     * 主驾驶请求切换角色
     *
     * @param data 数据体
     */
    private void pilotRequestSwitch(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_PILOT_REQUEST_SWITCH)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("switch_driver_id", Integer.toString(teamWorkState.getPartnerId()))
                        .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                        .build();

                HttpResponse httpResponse = httpRequest.get();

                if (httpResponse.isSuccess()) {

                    JSONObject teamIdObj = JsonUtil.parseObject(httpResponse.getData());
                    int switchTeamId = JsonUtil.getInt(teamIdObj, "teamId");

                    Logger.d(TAG, "主驾驶请求切换角色成功, 新的teamId=" + switchTeamId);

                    //存储switchTeamId
                    teamWorkState.setSwitchTeamId(switchTeamId);
                    SystemHelper.setTeamWorkState(teamWorkState);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {

                    Logger.d(TAG, "主驾驶请求切换角色失败，code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg() +
                            ", switch_driver_id=" + teamWorkState.getSwitchTeamId() +
                            ", vehicle_id=" + user.getVehicleId());

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
     * 主驾驶取消切换角色请求
     *
     * @param data 数据体
     */
    private void pilotCancelSwitchRequest(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_PILOT_CANCEL_SWITCH_REQUEST)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("switch_team_id", Integer.toString(teamWorkState.getSwitchTeamId()))
                        .build();

                HttpResponse httpResponse = httpRequest.get();

                if (httpResponse.isSuccess()) {

                    Logger.d(TAG, "主驾驶成功取消角色切换申请");

                    //清除switchTeamId
                    teamWorkState.setSwitchTeamId(0);
                    SystemHelper.setTeamWorkState(teamWorkState);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {

                    Logger.d(TAG, "主驾驶取消角色切换失败，code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg() +
                            "switch_team_id=" + teamWorkState.getSwitchTeamId());

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
     * 主驾驶移除副驾驶
     *
     * @param data 数据体
     */
    private void pilotRemoveCopilot(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_PILOT_REMOVE_COPILOT)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("team_id", Integer.toString(teamWorkState.getTeamId()))
                        .build();

                HttpResponse httpResponse = httpRequest.get();

                if (httpResponse.isSuccess()) {

                    Logger.d(TAG, "主驾驶移除副驾驶成功");

                    //用户角色普通
                    SystemHelper.setTeamWorkState(null);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {

                    Logger.d(TAG, "主驾驶移除副驾驶失败，code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg() +
                            ", team_id=" + teamWorkState.getTeamId());

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
     * 副驾驶离开团队
     *
     * @param data 数据体
     */
    private void copilotExit(String data) {

        JSONObject jsonObject = JsonUtil.parseObject(data);
        final String callback = JsonUtil.getString(jsonObject, CALLBACK_KEY);

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                User user = SystemHelper.getUser();
                TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

                HttpRequest httpRequest = new HttpRequest.Builder()
                        .url(Constants.API_COPILOT_EXIT)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("team_id", Integer.toString(teamWorkState.getTeamId()))
                        .build();

                HttpResponse httpResponse = httpRequest.get();

                if (httpResponse.isSuccess()) {

                    Logger.d(TAG, "副驾驶离开团队成功");

                    //用户角色普通
                    SystemHelper.setTeamWorkState(null);
                    //清除选择的车辆
                    SystemHelper.clearVehicle();

                    EventBus.getDefault().post(new VehicleSelectEvent());

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uiWebView.loadUrl("javascript:" + callback + "('" + CALLBACK_SUCCESS + "');");
                        }
                    });
                } else {

                    Logger.d(TAG, "副驾驶离开团队失败，code=" + httpResponse.getCode() +
                            ", msg=" + httpResponse.getMsg() +
                            ", team_id=" + teamWorkState.getTeamId());

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
}
