package com.unitedbustech.eld.eventcenter.states.Driver;

import android.os.Handler;
import android.os.Looper;

import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.VehicleAutoConnectType;
import com.unitedbustech.eld.eventbus.HosModelChangeEvent;
import com.unitedbustech.eld.eventbus.VehicleAutoConnectEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.eventcenter.enums.EventItem;
import com.unitedbustech.eld.eventcenter.model.DriverStatusModel;
import com.unitedbustech.eld.eventcenter.model.EventModel;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.hos.model.HosDriveEventModel;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyu
 * @date 2018/1/13
 * @description 司机状态管理者
 */
public class DriverStateManager {

    /**
     * ecm等待重连的时间。
     */
    private static final long ECM_WAIT_SECOND = 30 * 60 * 1000L;

    /**
     * 当前状态
     */
    private IDriverState nowState;

    /**
     * 司机的当前状态
     */
    private DriverState driverState;

    /**
     * 延迟时间处理器
     */
    private Handler handler;

    /**
     * 是否调用执行。
     */
    private boolean hasPostDelayed;

    /**
     * 初始化方法
     */
    public void init() {

        handler = new Handler(Looper.getMainLooper());
        EventBus.getDefault().register(this);
    }

    /**
     * 销毁方法
     */
    public void destroy() {

        if (handler != null) {

            handler.removeCallbacks(postGpsRunnable);
            handler = null;
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接收自检事件。用于解决当ecm频繁切换时，事件的响应。
     *
     * @param vehicleConnectEvent 车辆链接状态变化通知。
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onVehicleConnectChange(VehicleAutoConnectEvent vehicleConnectEvent) {

        //当前不是driving状态，直接不处理。
        //如果没有模型，可能当前不是driving状态，也有可能是状态恢复的driving，不考虑。
        HosDriveEventModel driveEventModel = ModelCenter.getInstance().getCurrentHosDriveModel();
        if (driveEventModel == null || driveEventModel.getDriverState() != DriverState.DRIVING) {

            Logger.d(Tags.EVENT, "connect don't need handle,driverState:" + driverState + ",drivingModel:" + driveEventModel);
            return;
        }
        Logger.d(Tags.EVENT, "new connect auto change event,type:" + vehicleConnectEvent.getType() + ",origin:" + driveEventModel.getOrigin());
        //如果状态从GPS切换到ECM
        if (vehicleConnectEvent.getType() == VehicleAutoConnectType.ECM) {
            handler.removeCallbacks(postGpsRunnable);
            //如果当前已经是手动模式了。切换ECM模式
            if (driveEventModel.getOrigin() != DDLOriginEnum.AUTO_BY_ELD.getCode()) {
                changeToEcm();
            }
        }
        //如果状态从ECM切换到GPS
        if (vehicleConnectEvent.getType() == VehicleAutoConnectType.GPS && driveEventModel.getOrigin() == DDLOriginEnum.AUTO_BY_ELD.getCode()) {

            changeToGPS();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onHosChangeEvent(HosModelChangeEvent hosModelChangeEvent) {

        resetLocalState(ModelCenter.getInstance().getCurrentDriverState());
    }

    public List<EventModel> switchStateTo(DriverState newDriverState, StateAdditionInfo stateAdditionInfo) {

        checkNowState(newDriverState);
        List<EventModel> eventModels = new ArrayList<>();
        Logger.d("DriverManger 的新状态为" + newDriverState.toVisibleString());
        //解决nowstate为null的崩溃问题。

        switch (newDriverState) {

            case ON_DUTY_NOT_DRIVING:

                eventModels.addAll(nowState.changeStateToODND(stateAdditionInfo));
                break;
            case OFF_DUTY:

                eventModels.addAll(nowState.changeStateToOD(stateAdditionInfo));
                break;
            case DRIVING:

                eventModels.addAll(nowState.changeStateToD(stateAdditionInfo));
                break;
            case SLEEPER_BERTH:

                eventModels.addAll(nowState.changeStateToSB(stateAdditionInfo));
                break;
            case YARD_MOVE:

                eventModels.addAll(nowState.changeStateToYM(stateAdditionInfo));
                break;
            case PERSONAL_USE:

                eventModels.addAll(nowState.changeStateToPU(stateAdditionInfo));
                break;
        }

        resetLocalState(newDriverState);
        return eventModels;
    }

    public IDriverState getNowState() {
        return nowState;
    }

    public void setNowState(IDriverState nowState) {
        this.nowState = nowState;
    }

    public DriverState getDriverState() {
        return driverState;
    }

    public void setDriverState(DriverState driverState) {
        this.driverState = driverState;
    }

    /**
     * 根据新的状态，改变本地真是状态。
     *
     * @param state 新状态
     */
    private void resetLocalState(DriverState state) {

        this.driverState = state;
        this.nowState = null;
        switch (state) {

            case ON_DUTY_NOT_DRIVING:
                this.nowState = new DriverOnDutyState();
                break;
            case OFF_DUTY:
                this.nowState = new DriverOffDutyState();
                break;
            case DRIVING:
                this.nowState = new DriverDrivingState();
                break;
            case SLEEPER_BERTH:
                this.nowState = new DriverSleeperBerthState();
                break;
            case YARD_MOVE:
                this.nowState = new DriverYardMoveState();
                break;
            case PERSONAL_USE:
                this.nowState = new DriverPersonalUseState();
                break;
        }
    }

    /**
     * 检查当前的状态
     */
    private void checkNowState(DriverState driverState) {

        if (nowState == null) {
            Logger.d("DriverManager 的当前状态为空");
            HosDayModel hosDayModel = ModelCenter.getInstance().getHosDayModel(new Date());
            if (hosDayModel != null) {
                if (hosDayModel.getHosDriveEventModels().size() == 0) {
                    Logger.d("DriverManager 的状态reset为初始状态：" + hosDayModel.getStartDriverState().toVisibleString());
                    resetLocalState(hosDayModel.getStartDriverState());
                    return;
                }
                Logger.d("DriverManager 的状态设置为：" + hosDayModel.getHosDriveEventModels().get(0).getDriverState().toVisibleString());

                resetLocalState(hosDayModel.getHosDriveEventModels().get(0).getDriverState());
            } else {
                //当跨天刷新并且没有事件的时候。会导致nowstate为null。进而导致崩溃。
                //这种情况下状态为offduty
                nowState = new DriverOffDutyState();
            }
        }
        //如果从driving切换到其他状态。
        //情况gps的延迟上报定时
        if (!driverState.equals(DriverState.DRIVING) && nowState instanceof DriverDrivingState) {

            handler.removeCallbacks(postGpsRunnable);
        }
    }

    /**
     * 从gps切换到了ecm的处理
     */
    private void changeToEcm() {
        Logger.i(Tags.EVENT, "new ECM Changed Driving!");

        //同步化，防止链接抖动
        synchronized (DriverStateManager.class) {

            DriverStatusModel drivingModel = (DriverStatusModel) new DriverStatusModel.Builder().state(DriverState.DRIVING).build();
            drivingModel.setOrigin(DDLOriginEnum.AUTO_BY_ELD.getCode());
            EventCenter.getInstance().distributeOneEvent(EventItem.DRIVER_WORK_STATE, drivingModel);
        }
    }

    /**
     * 发送延迟的GPS模型
     */
    private Runnable postGpsRunnable = new Runnable() {
        @Override
        public void run() {

            //同步化，防止链接抖动
            synchronized (DriverStateManager.class) {
                HosDriveEventModel driveEventModel = ModelCenter.getInstance().getCurrentHosDriveModel();
                if (driveEventModel == null || driveEventModel.getDriverState() != DriverState.DRIVING || gpsModel == null) {
                    Logger.i(Tags.EVENT, "connect dont need handle,driverState:" + driverState + ",drivingModel:" + driveEventModel + "gpsModel" + gpsModel);
                    return;
                }
                Logger.i(Tags.EVENT, "new Gps Changed Driving Event!");
                //生成一个之前时间的数据。
                EventCenter.getInstance().distributeOneEvent(EventItem.DRIVER_WORK_STATE, gpsModel);

                hasPostDelayed = false;
            }
        }
    };
    /**
     * 需要上报的gps的driving模型
     */
    private EventModel gpsModel;

    /**
     * 从ecm切换到了gps的处理
     */
    private void changeToGPS() {
        if (!hasPostDelayed) {
            Logger.i(Tags.EVENT, "new Gps Changed Driving,Start Post Delay!");

            handler.removeCallbacks(postGpsRunnable);
            gpsModel = new DriverStatusModel.Builder().state(DriverState.DRIVING).build();
            gpsModel.setOrigin(DDLOriginEnum.EDIT_BY_DRIVER.getCode());
            handler.postDelayed(postGpsRunnable, ECM_WAIT_SECOND);
            hasPostDelayed = true;
        } else {
            return;
        }
    }
}
