package com.unitedbustech.eld.system;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.ActivityStack;
import com.unitedbustech.eld.activity.MainActivity;
import com.unitedbustech.eld.common.DriverState;
import com.unitedbustech.eld.common.EngineEvent;
import com.unitedbustech.eld.common.VehicleDataItem;
import com.unitedbustech.eld.common.VehicleDataModel;
import com.unitedbustech.eld.common.VehicleState;
import com.unitedbustech.eld.datacollector.DataCollectorHandler;
import com.unitedbustech.eld.datacollector.DataCollectorSubscriber;
import com.unitedbustech.eld.datacollector.common.CollectorType;
import com.unitedbustech.eld.driving.DrivingActivity;
import com.unitedbustech.eld.eventbus.HosModelChangeEvent;
import com.unitedbustech.eld.eventbus.VehicleSelectEvent;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.eventcenter.core.StateAdditionInfo;
import com.unitedbustech.eld.eventcenter.enums.DDLOriginEnum;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.TabMenu;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yufei0213
 * @date 2018/3/1
 * @description SystemMonitor
 */
public class SystemMonitor implements DataCollectorSubscriber {

    private static final String TAG = "SystemMonitor";

    private Handler handler;

    /**
     * 当前车辆状态
     */
    private int vehicleState;

    /**
     * 当前司机状态
     */
    private DriverState driverState;

    /**
     * PersonalUse弹窗提示后，司机选择了No按钮
     */
    private boolean personalUseSelectNo;
    /**
     * driverState在personalUse状态下是否显示过提示
     */
    private boolean personalUseHasShow;
    /**
     * personalUse的提示
     */
    private HorizontalDialog personalUseDialog;

    /**
     * driverState在yardMove状态下是否显示过提示
     */
    private boolean yardMoveDialogHasShow;
    /**
     * yardMove的提示
     */
    private HorizontalDialog yardMoveDialog;

    private static SystemMonitor instance = null;

    private SystemMonitor() {

    }

    public static SystemMonitor getInstance() {

        if (instance == null) {

            instance = new SystemMonitor();
        }

        return instance;
    }

    public void init() {

        handler = new Handler(Looper.getMainLooper());

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);

        DataCollectorHandler.getInstance().subscribe(TAG, this);
    }

    public void destroy() {

        try {

            EventBus.getDefault().unregister(this);

            handler = null;
            vehicleState = 0;
            driverState = null;

            personalUseHasShow = false;
            if (personalUseDialog != null) {

                personalUseDialog.dismiss();
            }

            yardMoveDialogHasShow = false;
            if (yardMoveDialog != null) {

                yardMoveDialog.dismiss();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHosChangeEvent(HosModelChangeEvent hosModelChangeEvent) {

        DriverState state = ModelCenter.getInstance().getCurrentDriverState();
        if (state == null || state == driverState) {

            return;
        }

        driverState = state;
        if (driverState == DriverState.PERSONAL_USE) {

            personalUseHasShow = false;
            SystemHelper.recordPersonalUsePowerOff(true);
        } else if (driverState == DriverState.YARD_MOVE) {

            yardMoveDialogHasShow = false;
            SystemHelper.recordYardMovePowerOff(true);
        } else if (driverState == DriverState.OFF_DUTY) {

            //如果是在Break或者是PersonalUse弹窗选择了No，则不断开车辆连接
            if (!ModelCenter.getInstance().getBreak() && !personalUseSelectNo) {

                DataCollectorHandler.getInstance().stop();
                SystemHelper.clearVehicle();
                EventBus.getDefault().post(new VehicleSelectEvent());
            }

            personalUseSelectNo = false;
        }
    }

    @Override
    public void onSchedule(final VehicleDataModel model, final CollectorType type) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                handleVehicleStateChange(model);
            }
        });
    }

    @Override
    public void onDataItemChange(final VehicleDataItem item, final VehicleDataModel model, final CollectorType type) {

        handler.post(new Runnable() {
            @Override
            public void run() {

                switch (item) {

                    case VEHICLE_STATE:

//                        handleVehicleStateChange(model);
                        break;
                    case ENGINE_EVENT:

                        handleEngineStateChange(model);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * 处理车辆状态变化
     *
     * @param model VehicleDataModel
     */
    private void handleVehicleStateChange(VehicleDataModel model) {

        if (driverState == null) {

            driverState = ModelCenter.getInstance().getCurrentDriverState();
        }

        if (driverState == null) {

            Logger.w(Tags.VEHICLE, "SystemMonitor: driver has no state");
            return;
        }

        Logger.i(Tags.VEHICLE, "SystemMonitor: current vehicleState [" + vehicleState + "]");
        Logger.i(Tags.VEHICLE, "SystemMonitor: new vehicleState [" + model.getVehicleState() + "]");
        if (model.getVehicleState() == VehicleState.MOVING && vehicleState != VehicleState.MOVING) {

            Logger.i(Tags.VEHICLE, "SystemMonitor: driverState [" + driverState.toVisibleString() + "], vehicle become moving.");
            switch (driverState) {

                case ON_DUTY_NOT_DRIVING:
                    if (yardMoveDialog != null) {

                        yardMoveDialog.dismiss();
                        yardMoveDialog = null;
                    }

                    switchToDriving();
                    break;
                case PERSONAL_USE:

                    if (personalUseDialog != null) {

                        personalUseDialog.dismiss();
                        personalUseDialog = null;
                        switchToDriving();
                    }
                    break;
                case YARD_MOVE:
                case DRIVING:
                    break;
                default:

                    switchToDriving();
                    break;
            }

            Activity activity = ActivityStack.getInstance().getCurrentActivity();
            if (!(activity instanceof DrivingActivity)) {

                Logger.i(Tags.VEHICLE, "SystemMonitor: open driving page.");
                Intent intent = DrivingActivity.newIntent(activity);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            } else {

                Logger.i(Tags.VEHICLE, "SystemMonitor: driving page has opened.");
            }
        }

        vehicleState = model.getVehicleState();

        Logger.i(Tags.VEHICLE, "SystemMonitor: modify vehicleState to [" + vehicleState + "]");
    }

    /**
     * 处理引擎状态变化
     *
     * @param model 数据模型
     */
    private void handleEngineStateChange(VehicleDataModel model) {

        if (driverState == null) {

            driverState = ModelCenter.getInstance().getCurrentDriverState();
        }

        if (driverState == null) {

            Logger.w(Tags.VEHICLE, "SystemMonitor: driver has no state");
            return;
        }

        if (model.getEngineEvent() == EngineEvent.POWER_ON) {

            switch (driverState) {

                case PERSONAL_USE:

                    if (SystemHelper.hasPersonalUsePowerOff() && !personalUseHasShow && personalUseDialog == null) {

                        SystemHelper.recordPersonalUsePowerOff(true);

                        Activity activity = ActivityStack.getInstance().getCurrentActivity();
                        personalUseDialog = new HorizontalDialog.Builder(activity)
                                .setIcon(R.drawable.ic_emoji_msg)
                                .setText(R.string.dialog_personal_use_tip)
                                .setNegativeBtn(R.string.no, new HorizontalDialog.OnClickListener() {
                                    @Override
                                    public void onClick(HorizontalDialog dialog, int which) {

                                        //选择No的时候，状态切换为OFF，但是此时不应该断开车辆
                                        personalUseSelectNo = true;

                                        dialog.dismiss();
                                        switchToOffDuty();

                                        Activity activity = ActivityStack.getInstance().getCurrentActivity();
                                        if (activity instanceof DrivingActivity) {

                                            activity.finish();
                                        }
                                        personalUseDialog = null;
                                    }
                                })
                                .setNeutralBtn(R.string.yes, new HorizontalDialog.OnClickListener() {
                                    @Override
                                    public void onClick(HorizontalDialog dialog, int which) {

                                        dialog.dismiss();
                                        personalUseDialog = null;
                                    }
                                })
                                .setCancelable(false)
                                .build();
                        personalUseDialog.show();
                        personalUseHasShow = true;
                    }
                    break;
                case YARD_MOVE:

                    if (SystemHelper.hasYardMovePowerOff() && !yardMoveDialogHasShow && yardMoveDialog == null) {

                        SystemHelper.recordYardMovePowerOff(true);

                        switchOnDuty();

                        Activity activity = ActivityStack.getInstance().getCurrentActivity();

                        //如果当前界面不是主界面，跳转到主界面
                        if (!(activity instanceof MainActivity)) {

                            Intent intent = MainActivity.newIntent(activity);
                            intent.putExtra(TabMenu.TAB_INDEX, TabMenu.DASHBORAD);
                            activity.startActivity(intent);

                            activity = ActivityStack.getInstance().getActivity(MainActivity.class);
                        }

                        yardMoveDialog = new HorizontalDialog.Builder(activity)
                                .setIcon(R.drawable.ic_emoji_love)
                                .setText(R.string.dialog_yard_move_tip)
                                .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                                    @Override
                                    public void onClick(HorizontalDialog dialog, int which) {

                                        dialog.dismiss();
                                        yardMoveDialog = null;
                                    }
                                })
                                .setCancelable(false)
                                .build();
                        yardMoveDialog.show();
                        yardMoveDialogHasShow = true;
                    }
                    break;
                default:
                    break;
            }
        } else if (model.getEngineEvent() == EngineEvent.POWER_OFF) {

            switch (driverState) {

                case PERSONAL_USE:

                    SystemHelper.recordPersonalUsePowerOff(false);
                    break;
                case YARD_MOVE:

                    SystemHelper.recordYardMovePowerOff(false);
                    break;
                default:
                    break;
            }
            personalUseHasShow = false;
            yardMoveDialogHasShow = false;
        }
    }

    /**
     * 状态切换为Driving
     */
    private void switchToDriving() {

        Location location = LocationHandler.getInstance().getCurrentLocation();

        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();

        if (DataCollectorHandler.getInstance().getCurrentCollectorType() == CollectorType.GPS) {

            builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);
        } else {

            builder.origin(DDLOriginEnum.AUTO_BY_ELD);
        }

        if (location != null) {

            builder.location(null, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
        }

        //如果此时HosCenter记录的状态是正在Break，则取消Break状态
        if (ModelCenter.getInstance().getBreak()) {

            ModelCenter.getInstance().setBreak(false);
        }

        EventCenter.getInstance().changeDriverState(DriverState.DRIVING, builder.build());
    }

    /**
     * 状态切换为ODND
     */
    private void switchOnDuty() {

        Location location = LocationHandler.getInstance().getCurrentLocation();

        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
        builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);

        if (location != null) {

            builder.location(null, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
        }

        EventCenter.getInstance().changeDriverState(DriverState.ON_DUTY_NOT_DRIVING, builder.build());
    }

    /**
     * 状态切换为OFF
     */
    private void switchToOffDuty() {

        Location location = LocationHandler.getInstance().getCurrentLocation();
        StateAdditionInfo.Builder builder = new StateAdditionInfo.Builder();
        builder.origin(DDLOriginEnum.EDIT_BY_DRIVER);

        if (location != null) {

            builder.location(null, Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
        }

        EventCenter.getInstance().changeDriverState(DriverState.OFF_DUTY, builder.build());
    }
}
