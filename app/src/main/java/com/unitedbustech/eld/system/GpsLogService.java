package com.unitedbustech.eld.system;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.GpsLog;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.location.LocationSubscriber;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.request.RequestCacheService;
import com.unitedbustech.eld.request.RequestType;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/6/29
 * @description Gps上报服务
 */
public class GpsLogService implements LocationSubscriber {

    private static final String TAG = "GpsLogService";

    private static final long DURATION = 1 * 60 * 1000L;

    private Handler handler;
    private GpsLogRunnable gpsLogRunnable;

    private GpsLog gpsLog;

    private static GpsLogService instance = null;

    private GpsLogService() {
    }

    public static GpsLogService getInstance() {

        if (instance == null) {

            instance = new GpsLogService();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);

        LocationHandler.getInstance().subscribe(TAG, this);

        if (gpsLogRunnable == null) {

            gpsLogRunnable = new GpsLogRunnable();
        }

        if (handler == null) {

            handler = new Handler(Looper.getMainLooper());
        }

        gpsLog = new GpsLog();
    }

    /**
     * 销毁
     */
    public void destroy() {

        try {

            EventBus.getDefault().unregister(this);

            if (handler != null) {

                handler.removeCallbacks(gpsLogRunnable);
                handler = null;
                gpsLogRunnable = null;

                gpsLog = null;
            }
        } catch (Exception e) {

            e.printStackTrace();
        } finally {

            instance = null;
        }
    }

    /**
     * 开始上报
     */
    public void start() {

        handler.removeCallbacks(gpsLogRunnable);
        handler.post(gpsLogRunnable);
    }

    /**
     * 停止上报
     */
    public void stop() {

        handler.removeCallbacks(gpsLogRunnable);
    }

    @Override
    public void onLocationUpdate(Location location) {

        gpsLog.update(location);
    }

    @Override
    public void onStateChange(boolean state) {

    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleSelectEvent(VehicleSelectEvent vehicleSelectEvent) {

        if (SystemHelper.hasVehicle()) {

            this.gpsLog.setVehicleId(SystemHelper.getUser().getVehicleId());
            this.start();
        } else {

            this.stop();
        }
    }

    /**
     * 上报GPS的任务
     */
    private class GpsLogRunnable implements Runnable {
        @Override
        public void run() {

            if (handler != null) {

                handler.postDelayed(this, DURATION);
            }

            uploadGpslog();
        }

        /**
         * 上报心跳
         */
        public void uploadGpslog() {

            //如果当前缓存的GPS是一分钟前的，则不再上报
            long nowTime = new Date().getTime();
            if ((nowTime - gpsLog.getUpdateTime()) > DURATION) {

                Location location = LocationHandler.getInstance().getCurrentLocation();
                if (location != null) {

                    gpsLog.update(location);
                    if ((nowTime - gpsLog.getUpdateTime()) > DURATION) {

                        Logger.w(Tags.GPS_LOG, "gps info is expire， nowTime=" + nowTime + ", gpsUpdateTime=" + gpsLog.getUpdateTime());
                        return;
                    }
                }
            }

            ThreadUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {

                    User user = SystemHelper.getUser();

                    HttpRequest.Builder builder = new HttpRequest.Builder()
                            .url(Constants.API_GPS_LOG);

                    String access_token = user.getAccessToken();
                    builder.addParam("access_token", access_token);

                    List<GpsLog> data = new ArrayList<>();
                    data.add(new GpsLog(gpsLog));

                    builder.addParam("data", JsonUtil.toJSONString(data));

                    RequestCacheService.getInstance().cachePost(builder.build(), RequestType.GPS);
                }
            });
        }
    }
}
