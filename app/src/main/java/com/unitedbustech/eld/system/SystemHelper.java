package com.unitedbustech.eld.system;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.location.StorageLocation;
import com.unitedbustech.eld.login.LoginActivity;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LocalDataStorageUtil;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 系统帮助类
 */
public class SystemHelper {

    private static final String TAG = "SystemHelper";

    /**
     * Logout事件的key
     */
    private static final String LOGOUT_KEY = "logout_key_v1";

    /**
     * 存储Location的key
     */
    private static final String LOCATION_KEY = "location_key_v3";

    /**
     * 存储user的key
     */
    private static final String USER_KEY = "user_v2";

    /**
     * 存储团队驾驶相关信息的key
     */
    private static final String TEAMWORK_KEY = "teamwork_v2";

    /**
     * 存储webview数据的key
     */
    private static final String WEB_KEY = "web_v1";

    /**
     * 当前用户选择的豁免模式
     */
    private static final String EXEMPTION_FUNC_KEY = "exemption_func_v1";

    /**
     * 记录YardMove状态下的熄火事件
     * 当打火或者状态切换时重置
     */
    private static final String YARD_MOVE_POWER_OFF_KEY = "yard_move_power_off_key_v1";

    /**
     * 记录Personal状态下的熄火事件
     * 当打火或者状态切换时重置
     */
    private static final String PERSONAL_USE_POWER_OFF_KEY = "personal_use_power_off_key_v1";

    /**
     * 当前是否有故障
     */
    private static final String MAL_FUNCTION_KEY = "mal_function_key_v1";

    /**
     * 当前是否有诊断
     */
    private static final String DIAGNOSTIC_KEY = "diagnostic_key_v1";

    /**
     * 当前是否有GPS故障
     */
    private static final String POSITION_MAL_KEY = "position_mal_function_v1";

    /**
     * 当前GPS状态，表示GPS是否有问题
     */
    private static final String POSITION_STATUS = "position_status_v1";

    /**
     * 记录当前Dot界面的打开模式
     */
    private static final String DOT_MODE_KEY = "dot_mode_key_v1";

    /**
     * 认证失败时提示重新登录
     */
    public static void sessionTimeout() {

        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        Intent intent = LoginActivity.newIntent(activity);
        intent.putExtra(LoginActivity.EXTRA_PARAMS_TIPS, LoginActivity.SESSION_TIMEOUT);
        activity.startActivity(intent);
        ActivityStack.getInstance().finishOthers(LoginActivity.class);
    }

    /**
     * 补报时发现登录信息错误，退出
     */
    public static void loginInfoError() {

        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        Intent intent = LoginActivity.newIntent(activity);
        intent.putExtra(LoginActivity.EXTRA_PARAMS_TIPS, LoginActivity.LOGIN_INFO_ERROR);
        activity.startActivity(intent);
        ActivityStack.getInstance().finishOthers(LoginActivity.class);
    }

    /**
     * 离线模式期间，在其他设备上登录
     */
    public static void loginByOtherDuringOffline() {

        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        Intent intent = LoginActivity.newIntent(activity);
        intent.putExtra(LoginActivity.EXTRA_PARAMS_TIPS, LoginActivity.SESSION_DIAGNOSTIC_OFFLINE);
        activity.startActivity(intent);
        ActivityStack.getInstance().finishOthers(LoginActivity.class);
    }

    /**
     * 在其他设备上登录
     */
    public static void loginByOther() {

        Activity activity = ActivityStack.getInstance().getCurrentActivity();
        Intent intent = LoginActivity.newIntent(activity);
        intent.putExtra(LoginActivity.EXTRA_PARAMS_TIPS, LoginActivity.SESSION_DIAGNOSTIC);
        activity.startActivity(intent);
        ActivityStack.getInstance().finishOthers(LoginActivity.class);
    }

    /**
     * 设置Logout事件
     *
     * @param isClear 是否清除事件
     */
    public static void setLogout(boolean isClear) {

        LocalDataStorageUtil.putBoolean(LOGOUT_KEY, !isClear);
    }

    /**
     * 当前是否有Logout事件
     *
     * @return 判断结果
     */
    public static boolean hasLogout() {

        return LocalDataStorageUtil.getBoolean(LOGOUT_KEY);
    }

    /**
     * 存储Location
     *
     * @param location 位置
     */
    public static void setLocation(Location location) {

        if (location == null) {

            return;
        }

        StorageLocation storageLocation = new StorageLocation(location);
        LocalDataStorageUtil.putString(LOCATION_KEY, JsonUtil.toJSONString(storageLocation));
    }

    /**
     * 获取本地存储的Location
     *
     * @return Location
     */
    public static Location getLocation() {

        String locationStr = LocalDataStorageUtil.getString(LOCATION_KEY);
        StorageLocation storageLocation = JsonUtil.parseObject(locationStr, StorageLocation.class);

        Location location = null;
        if (storageLocation != null) {

            location = new Location(storageLocation.getProvider());
            location.setLatitude(storageLocation.getLatitude());
            location.setLongitude(storageLocation.getLongitude());
            location.setTime(storageLocation.getTime());
            location.setSpeed(storageLocation.getSpeed());
        }

        return location;
    }

    /**
     * 缓存用户信息
     *
     * @param user 用户
     */
    public static void setUser(@Nullable User user) {

        synchronized (USER_KEY) {

            if (user == null) {

                LocalDataStorageUtil.remove(USER_KEY);
            } else {

                String userStr = JsonUtil.toJSONString(user);

                LocalDataStorageUtil.putString(USER_KEY, userStr);
            }
        }
    }

    /**
     * 获取缓存的用户信息
     *
     * @return User
     */
    public static User getUser() {

        synchronized (USER_KEY) {

            String userStr = LocalDataStorageUtil.getString(USER_KEY);

            return JsonUtil.parseObject(userStr, User.class);
        }
    }

    /**
     * 设置当前连接的车辆
     *
     * @param vehicleId 车辆id
     */
    public static void setVehicle(int vehicleId) {

        synchronized (USER_KEY) {

            User user = SystemHelper.getUser();
            if (user != null) {

                user.setVehicleId(vehicleId);
                SystemHelper.setUser(user);
            }
        }
    }

    /**
     * 当前是否有车辆
     *
     * @return 是否有车辆
     */
    public static boolean hasVehicle() {

        synchronized (USER_KEY) {

            User user = SystemHelper.getUser();

            if (user == null) {

                return false;
            } else {

                return user.getVehicleId() == 0 ? false : true;
            }
        }
    }

    /**
     * 清除当前选择的车辆
     */
    public static void clearVehicle() {

        synchronized (USER_KEY) {

            User user = SystemHelper.getUser();
            if (user != null) {

                user.setVehicleId(0);
                SystemHelper.setUser(user);
            }

            //清除车辆选择时，清除故障和诊断相关的状态
            setMalFunction(true);
            setDiagnostic(true);
            setPositionMalFunction(true);
            setPositionStatus(true);
        }
    }

    /**
     * 记录团队驾驶状态
     *
     * @param state 状态
     */
    public static void setTeamWorkState(TeamWorkState state) {

        if (state == null) {

            TeamWorkState teamWorkState = new TeamWorkState();
            LocalDataStorageUtil.putString(TEAMWORK_KEY, JsonUtil.toJSONString(teamWorkState));
        } else {

            String stateStr = JsonUtil.toJSONString(state);
            LocalDataStorageUtil.putString(TEAMWORK_KEY, stateStr);
        }
    }

    /**
     * 获取团队驾驶状态
     *
     * @return TeamWorkState
     */
    public static TeamWorkState getTeamWorkState() {

        String stateStr = LocalDataStorageUtil.getString(TEAMWORK_KEY);

        if (TextUtils.isEmpty(stateStr)) {

            setTeamWorkState(null);
            return getTeamWorkState();
        } else {

            return JsonUtil.parseObject(stateStr, TeamWorkState.class);
        }
    }

    /**
     * 存储webview数据
     *
     * @param data json字符串 "{"key": "", "data": {}}"
     */
    public static void setWebData(String data) {

        LocalDataStorageUtil.putString(WEB_KEY, data);
    }

    /**
     * 获取webview存储的数据
     *
     * @return json字符串 "{"key": "", "data": {}}"
     */
    public static String getWebData() {

        return LocalDataStorageUtil.getString(WEB_KEY);
    }

    /**
     * 清空webview存储的数据
     */
    public static void clearWebData() {

        LocalDataStorageUtil.remove(WEB_KEY);
    }

    /**
     * 记录当前用户选择的豁免模式
     *
     * @param func 豁免模式类型
     */
    public static void setExemptionFunc(int func) {

        LocalDataStorageUtil.putInt(EXEMPTION_FUNC_KEY, func);
    }

    /**
     * 获取当前用户的豁免模式
     *
     * @return 当前用户的豁免模式类型
     */
    public static int getExemptionFunc() {

        return LocalDataStorageUtil.getInt(EXEMPTION_FUNC_KEY);
    }

    /**
     * 记录YardMove状态下的熄火事件
     *
     * @param isClear 是否清除
     */
    public static void recordYardMovePowerOff(boolean isClear) {

        LocalDataStorageUtil.putBoolean(YARD_MOVE_POWER_OFF_KEY, !isClear);
    }

    /**
     * 是否存在YardMove状态下的熄火事件
     *
     * @return 是否存在
     */
    public static boolean hasYardMovePowerOff() {

        return LocalDataStorageUtil.getBoolean(YARD_MOVE_POWER_OFF_KEY);
    }

    /**
     * 记录PersonalUse状态下的熄火事件
     *
     * @param isClear 是否清除
     */
    public static void recordPersonalUsePowerOff(boolean isClear) {

        LocalDataStorageUtil.putBoolean(PERSONAL_USE_POWER_OFF_KEY, !isClear);
    }

    /**
     * 是否存在YardMove状态下的熄火事件
     *
     * @return 是否存在
     */
    public static boolean hasPersonalUsePowerOff() {

        return LocalDataStorageUtil.getBoolean(PERSONAL_USE_POWER_OFF_KEY);
    }

    /**
     * 更新当前是否有故障
     *
     * @param isClear 是否清除
     */
    public static void setMalFunction(boolean isClear) {

        synchronized (MAL_FUNCTION_KEY) {

            LocalDataStorageUtil.putBoolean(MAL_FUNCTION_KEY, !isClear);
        }
    }

    /**
     * 当前是否有故障
     *
     * @return 结果
     */
    public static boolean hasMalFunction() {

        synchronized (MAL_FUNCTION_KEY) {

            return LocalDataStorageUtil.getBoolean(MAL_FUNCTION_KEY);
        }
    }

    /**
     * 更新当前是否有诊断
     *
     * @param isClear 是否清除
     */
    public static void setDiagnostic(boolean isClear) {

        synchronized (DIAGNOSTIC_KEY) {

            LocalDataStorageUtil.putBoolean(DIAGNOSTIC_KEY, !isClear);
        }
    }

    /**
     * 当前是否有故障
     *
     * @return 结果
     */
    public static boolean hasDiagnostic() {

        synchronized (DIAGNOSTIC_KEY) {

            return LocalDataStorageUtil.getBoolean(DIAGNOSTIC_KEY);
        }
    }

    /**
     * 更新当前是否有GPS故障
     *
     * @param isClear 是否清除GPS故障
     */
    public static void setPositionMalFunction(boolean isClear) {

        synchronized (POSITION_MAL_KEY) {

            LocalDataStorageUtil.putBoolean(POSITION_MAL_KEY, !isClear);
        }
    }

    /**
     * 当前是否有GPS故障
     *
     * @return 结果
     */
    public static boolean hasPositionMalFunction() {

        synchronized (POSITION_MAL_KEY) {

            return LocalDataStorageUtil.getBoolean(POSITION_MAL_KEY);
        }
    }

    /**
     * 当前GPS是否有问题
     *
     * @param isNormal 是否清除问题
     */
    public static void setPositionStatus(boolean isNormal) {

        synchronized (POSITION_STATUS) {

            LocalDataStorageUtil.putBoolean(POSITION_STATUS, !isNormal);
        }
    }

    /**
     * 当前GPS是否有问题
     *
     * @return 结果
     */
    public static boolean isPositionNormal() {

        synchronized (POSITION_STATUS) {

            return !LocalDataStorageUtil.getBoolean(POSITION_STATUS);
        }
    }

    /**
     * 获取当前的DotMode
     *
     * @return DotModeType
     */
    public static int getDotMode() {

        synchronized (DOT_MODE_KEY) {

            return LocalDataStorageUtil.getInt(DOT_MODE_KEY);
        }
    }
}
