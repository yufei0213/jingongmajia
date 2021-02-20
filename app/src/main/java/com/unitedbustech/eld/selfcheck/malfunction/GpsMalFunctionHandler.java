package com.unitedbustech.eld.selfcheck.malfunction;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.MalfunctionType;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.eventbus.GetAlertsEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.location.LocationSubscriber;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.selfcheck.BaseHandler;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.ConvertUtil;
import com.unitedbustech.eld.util.CoordinateUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2018/4/17
 * @description GPS故障处理类
 */
public class GpsMalFunctionHandler extends BaseHandler implements LocationSubscriber {

    private static final String TAG = "GpsMalFunctionHandler";

    /**
     * 最大累积时长
     */
    private static final long MAX_DURATION = 60 * 60 * 1000l;

    /**
     * GPS检查时间阈值
     */
    private static final long CHECK_DURATION = 2 * 60 * 1000l;

    /**
     * 产生Gps故障的最小英里数。
     */
    private static final double MIN_DISTANCE_MI = 5.0d;

    /**
     * 代表是否有异常信息。
     */
    private boolean hasMalfunction;

    private Handler handler;

    /**
     * 缓存当前最新的Location
     */
    private Location curLocation;

    /**
     * GPS开始出现问题的时间的时间戳
     */
    private long startAccumulatedTime;

    /**
     * 当前已经累积的时长
     */
    private long accumulatedDuration;

    /**
     * 当前记录的车辆ID
     */
    private int vehicleId;

    /**
     * 定时检查Location
     */
    private Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {

            Logger.d(TAG, "start check location");
            //如果位置更新成功。则代表本次循环周期内，一切工作正常。
            if (isLocationUpdateSuccess()) {
                Logger.d(TAG, "location update success!");

                //如果当前是有异常的，上报清除数据
                if (hasInit && hasMalfunction) {
                    Logger.i(TAG, "location has malfunction! upload clear!");

                    //上报清除事件。
                    //并处理没有告警的情况。
                    handleMalfunctionChange(false);
                }
            } else {//位置更新不成功
                Logger.d(TAG, "location update failed!!!!");

                //统计失败变量
                countFailed();
                /**
                 * 产生故障的四个条件：
                 * 1：已经进行了初始化，获得到了服务端当前的故障状态。
                 * 2：已经触及到了阈值，长时间没有位置更新
                 * 3：当前服务端没有故障。
                 * 4：当前距离距离上一次的记录的距离已经超过了最小的阈值
                 * 以上四个条件均具备时，产生故障。
                 */
                if (hasInit &&
                        hasTouchThreshold() &&
                        !hasMalfunction &&
                        justifyHasOverDistanceMiles()) {
                    Logger.i(TAG, "touch threshold! upload produce malfunction!!!");

                    //上报产生事件。
                    //并处理产生告警的情况。
                    handleMalfunctionChange(true);
                    //重新开始计数。如果不重新开始计数，会导致一旦超过阈值就会频繁切换状态。
                    resetCounts();
                }
            }
            handler.postDelayed(checkRunnable, CHECK_DURATION);
        }
    };

    public GpsMalFunctionHandler() {

        LocationHandler.getInstance().subscribe(TAG, this);
        curLocation = LocationHandler.getInstance().getCurrentLocation();

        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(checkRunnable, CHECK_DURATION);
        startAccumulatedTime = new Date().getTime();
        resetMalfunctionState();
        EventBus.getDefault().register(this);
    }

    /**
     * 销毁
     */
    public void destroy() {

        handler.removeCallbacks(checkRunnable);
        LocationHandler.getInstance().unSubscribe(TAG);
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收车辆选择状态变化事件
     *
     * @param vehicleSelectEvent
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleSelectChange(VehicleSelectEvent vehicleSelectEvent) {

        //如果车辆发生变化
        if ((vehicleId != SystemHelper.getUser().getVehicleId()) && SystemHelper.hasVehicle()) {
            Logger.d(TAG, "car changed!! reset count!!");

            //重置计数
            resetCounts();
            hasInit = false;
            resetMalfunctionState();
        }
        //如果当前没有车，而本地记着有车。则是清除车辆的操作。
        //不在进行任何操作。
        if (vehicleId != 0 && !SystemHelper.hasVehicle()) {
            Logger.d(TAG, "car cleared!!! reset count!!");

            resetCounts();
            //清除车辆时，将MalFunction的状态置为false
            hasInit = false;
            hasMalfunction = false;
            SystemHelper.setPositionMalFunction(false);
        }
        vehicleId = SystemHelper.getUser().getVehicleId();

    }

    /**
     * 判断地址是否正确更新。
     * 如果正确更新，则返回true。
     * 如果更新有异常。返回false。
     *
     * @return
     */
    private boolean isLocationUpdateSuccess() {

        Date now = new Date();
        //第一种失败情况。有位置但是是很久之前的位置。
        if (curLocation != null && now.getTime() - curLocation.getTime() > CHECK_DURATION) {

            return false;
        }
        //第二种情况。从初始化到duration之间，一直都没有产生地址
        if (curLocation == null && now.getTime() - startAccumulatedTime > CHECK_DURATION) {

            return false;
        }
        //除了以上两种情况，都认为是成功
        return true;
    }

    /**
     * 是否到达能够上报的阈值。
     *
     * @return true:已经到达，false:未到达
     */
    private boolean hasTouchThreshold() {

        return accumulatedDuration > MAX_DURATION;
    }

    /**
     * 重置计数器。
     * 重新开启计数。调用此方法，gps的计数器会完全重新进行统计。变成初始化的状态。
     */
    private void resetCounts() {

        accumulatedDuration = 0l;
    }

    /**
     * 对失败进行计数。
     * 记录下本次失败。
     */
    private void countFailed() {

        //只有在有车的时候才计数
        if (SystemHelper.hasVehicle()) {

            accumulatedDuration += CHECK_DURATION;
        }
    }

    /**
     * 判断是否没有超过五英里。
     * 获取最新的location，与存储的location进行对比。
     * 如果获取为null，认为Gps模块不可用。
     * 如果获取不为null，判断距离是否超过5英里。
     *
     * @return true:获取位置不成功或者获取成功并且超过五英里,false:没有超过五英里。
     */
    private boolean justifyHasOverDistanceMiles() {

        Location location = LocationHandler.getInstance().getCurrentLocation();
        //如果为空，地址获取失败或者本地址还没有更新。认为是现在Gps模块可能有故障。返回true
        if (location == null || curLocation == null) {
            return true;
        }
        double distanceM = CoordinateUtil.getDistance(curLocation.getLatitude(), curLocation.getLongitude(), location.getLatitude(), location.getLongitude());
        double distanceMile = ConvertUtil.m2mile(distanceM);
        //计算距离大于五英里
        if (distanceMile > MIN_DISTANCE_MI) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理当Malfunction的状态发生变化的处理。
     *
     * @param isProduce 是否是生成
     */
    private void handleMalfunctionChange(boolean isProduce) {

        SystemHelper.setPositionMalFunction(!isProduce);
        hasMalfunction = isProduce;
        EventCenter.getInstance().malfunctionEvent(MalfunctionType.POSITIONING_COMPLIANCE, !isProduce);
        EventBus.getDefault().post(new GetAlertsEvent());
    }

    @Override
    public void onLocationUpdate(Location location) {

        curLocation = location;
    }

    @Override
    public void onStateChange(boolean state) {

    }

    @WorkerThread
    private void resetMalfunctionState() {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Logger.d(TAG, "start reset malfunction state");

                User user = SystemHelper.getUser();

                //如果当前被置为0，认为是没有车的状态。不做任何处理。
                if (vehicleId == 0) {

                    Logger.d(TAG, "当前没有车辆，不重置状态");
                    return;
                }

                getMalFunctionRequest = new HttpRequest.Builder()
                        .url(Constants.API_GET_MALFUNCTION_LIST)
                        .addParam("access_token", user.getAccessToken())
                        .addParam("vehicle_id", Integer.toString(user.getVehicleId()))
                        .addParam("code", MalfunctionType.POSITIONING_COMPLIANCE.getCode())
                        .build();

                Logger.d(TAG, "请求GPS故障列表参数：access_token=" + user.getAccessToken() +
                        ", vehicle_id=" + user.getVehicleId() +
                        ", code=" + MalfunctionType.POSITIONING_COMPLIANCE.getCode());

                HttpResponse httpResponse = getMalFunctionRequest.get();

                if (httpResponse.isSuccess()) {

                    JSONObject dataObj = JsonUtil.parseObject(httpResponse.getData());
                    JSONArray malfunctionArray = JsonUtil.getJsonArray(dataObj, "malfunctions");

                    Logger.d(TAG, "请求GPS故障列表成功，data=" + httpResponse.getData());

                    if (malfunctionArray.size() > 0) {

                        hasMalfunction = true;
                        SystemHelper.setPositionMalFunction(true);
                    }

                    if (malfunctionArray.size() == 0) {

                        hasMalfunction = false;
                        SystemHelper.setPositionMalFunction(false);
                    }
                    hasInit = true;
                } else {

                    Logger.d(TAG, "请求当前是否有故障失败，code=" + httpResponse.getCode() + ", msg=" + httpResponse.getMsg());
                }
            }
        });

    }
}
