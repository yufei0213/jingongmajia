package com.unitedbustech.eld.selfcheck;

import com.unitedbustech.eld.common.SelfCheckEventType;
import com.unitedbustech.eld.common.TeamWorkState;
import com.unitedbustech.eld.common.UserRole;
import com.unitedbustech.eld.eventbus.SelfCheckEvent;
import com.unitedbustech.eld.selfcheck.diagnostic.EngineSyncDiagnosticHandler;
import com.unitedbustech.eld.selfcheck.malfunction.GpsMalFunctionHandler;
import com.unitedbustech.eld.selfcheck.malfunction.TimeMalFunctionHandler;
import com.unitedbustech.eld.selfcheck.resume.ResumeHandler;
import com.unitedbustech.eld.system.SystemHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yufei0213
 * @date 2018/2/10
 * @description 自检服务类，负责处理各类异常和故障
 */
public class SelfCheckService {

    private static final String TAG = "SelfCheckService";

    /**
     * 时间故障处理
     */
    private TimeMalFunctionHandler timeMalFunctionHandler;

    /**
     * 引擎数据同步诊断处理
     */
    private EngineSyncDiagnosticHandler engineSyncDiagnosticHandler;

    /**
     * GPS故障处理
     */
    private GpsMalFunctionHandler gpsMalFunctionHandler;

    private static SelfCheckService instance = null;

    private SelfCheckService() {
    }

    public static SelfCheckService getInstance() {

        if (instance == null) {

            instance = new SelfCheckService();
        }

        return instance;
    }

    /**
     * 初始化
     */
    public void init() {

        timeMalFunctionHandler = new TimeMalFunctionHandler();
        engineSyncDiagnosticHandler = new EngineSyncDiagnosticHandler();
        gpsMalFunctionHandler = new GpsMalFunctionHandler();

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }

        EventBus.getDefault().register(this);
    }

    /**
     * 销毁
     */
    public void destroy() {

        try {

            EventBus.getDefault().unregister(this);

            timeMalFunctionHandler.destroy();
            timeMalFunctionHandler = null;

            engineSyncDiagnosticHandler.destroy();
            engineSyncDiagnosticHandler = null;

            gpsMalFunctionHandler.destroy();
            gpsMalFunctionHandler = null;
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSelfCheckEvent(SelfCheckEvent selfCheckEvent) {

        TeamWorkState teamWorkState = SystemHelper.getTeamWorkState();

        switch (selfCheckEvent.getType()) {

            case SelfCheckEventType.TIME:

                timeMalFunctionHandler.checkTime();
                break;
            case SelfCheckEventType.ECM_LINK:

                if (teamWorkState.getUserRole() != UserRole.COPILOT) {

                    engineSyncDiagnosticHandler.handleEcmLink(selfCheckEvent);
                }
                break;
            case SelfCheckEventType.ECM_DATA:

                if (teamWorkState.getUserRole() != UserRole.COPILOT) {

                    engineSyncDiagnosticHandler.handleEngineSync(selfCheckEvent);
                }
                break;
            case SelfCheckEventType.RESUME:

                new ResumeHandler().selfCheck();
                break;
            default:
                break;
        }
    }
}
