package com.unitedbustech.eld.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.vo.CarrierVo;
import com.unitedbustech.eld.common.vo.DriverVo;
import com.unitedbustech.eld.dailylog.DailyLogFragment;
import com.unitedbustech.eld.dashboard.DashBoardFragment;
import com.unitedbustech.eld.domain.DataBaseHelper;
import com.unitedbustech.eld.domain.entry.Carrier;
import com.unitedbustech.eld.domain.entry.Driver;
import com.unitedbustech.eld.domain.entry.Rule;
import com.unitedbustech.eld.dot.DotFragment;
import com.unitedbustech.eld.dvir.DvirFragment;
import com.unitedbustech.eld.eventcenter.core.EventCenter;
import com.unitedbustech.eld.launcher.LauncherActivity;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.more.MoreFragment;
import com.unitedbustech.eld.service.CheckForceStopService;
import com.unitedbustech.eld.system.HeartBeatService;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.DeviceUtil;
import com.unitedbustech.eld.util.LanguageUtil;
import com.unitedbustech.eld.util.ThreadUtil;
import com.unitedbustech.eld.view.TabMenu;

import java.util.List;

/**
 * @author yufei0213
 * @date 2017/12/4
 * @description 应用主界面
 */
public class MainActivity extends BaseActivity implements TabMenu.TabMenuSelectedListener {

    private static final String EXTRA_PARAMS_APP_STATUS = "com.unitedbustech.eld.activity";

    /**
     * 返回主界面
     */
    public static final int ACTION_BACK_HOME = 0;

    /**
     * App重新启动
     */
    public static final int ACTION_RESTART = 1;

    /**
     * DashBoard页面
     */
    private Fragment dashboardFragment;

    /**
     * dailylog页面
     */
    private Fragment dailylogFragment;

    /**
     * dvir页面
     */
    private Fragment dvirFragment;

    /**
     * dot路检页面
     */
    private Fragment dotFragment;

    /**
     * more页面
     */
    private Fragment moreFragment;

    /**
     * 当前展示的fragment
     */
    private Fragment currentFragment;

    /**
     * Tab Menu View
     */
    private TabMenu tabMenu;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, MainActivity.class);

        intent.putExtra(EXTRA_ANIM_IN_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_IN_OUT_ID, R.anim.fade_out);
        intent.putExtra(EXTRA_ANIM_OUT_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_OUT_OUT_ID, R.anim.fade_out);

        return intent;
    }

    public static Intent newIntent(Context context, int action) {

        Intent intent = newIntent(context);

        intent.putExtra(EXTRA_PARAMS_APP_STATUS, action);

        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (!LanguageUtil.getInstance().isChangingLanguage()) {

            //清除Logout记录
            SystemHelper.setLogout(true);
            //开启心跳服务
            HeartBeatService.getInstance().start();

            //上报登录事件
            EventCenter.getInstance().notifyLoginEvent(true);

            //开启App退出检测
            startService(CheckForceStopService.newIntent(MainActivity.this));

            //输出必要日志
            initLog();
        }
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {

        //加载视图容器
        View view = LayoutInflater.from(this).inflate(R.layout.activity_main, null);

        //获取底部TabMenu
        tabMenu = view.findViewById(R.id.tab_menu);
        tabMenu.setListener(this);

        return view;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        int action = intent.getIntExtra(EXTRA_PARAMS_APP_STATUS, ACTION_BACK_HOME);
        switch (action) {

            case ACTION_BACK_HOME:

                break;
            case ACTION_RESTART:

                protectApp();
                break;
        }

        tabMenu.currentTabIndex = intent.getIntExtra(TabMenu.TAB_INDEX, tabMenu.currentTabIndex);
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        //默认选择第一页
        tabMenu.setSelectedItem(tabMenu.currentTabIndex);
    }

    @Override
    protected void protectApp() {

        Intent intent = LauncherActivity.newIntent(MainActivity.this);
        startActivity(intent);
        finish();
    }

    @Override
    public void onTabItemSelected(int index) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (index) {

            case TabMenu.DASHBORAD:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (dashboardFragment == null) {

                    dashboardFragment = DashBoardFragment.newInstance();
                    ft.add(R.id.fragment_container, dashboardFragment);
                } else {

                    ft.show(dashboardFragment);
                }

                currentFragment = dashboardFragment;
                break;
            case TabMenu.DAILYLOG:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (dailylogFragment == null) {

                    dailylogFragment = DailyLogFragment.newInstance();
                    ft.add(R.id.fragment_container, dailylogFragment);
                } else {

                    ft.show(dailylogFragment);
                }

                currentFragment = dailylogFragment;
                break;
            case TabMenu.DVIR:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (dvirFragment == null) {

                    dvirFragment = DvirFragment.newInstance();
                    ft.add(R.id.fragment_container, dvirFragment);
                } else {

                    ft.show(dvirFragment);
                }

                currentFragment = dvirFragment;
                break;
            case TabMenu.DOT:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (dotFragment == null) {

                    dotFragment = DotFragment.newInstance();
                    ft.add(R.id.fragment_container, dotFragment);
                } else {

                    ft.show(dotFragment);
                }

                currentFragment = dotFragment;
                break;
            case TabMenu.MORE:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (moreFragment == null) {

                    moreFragment = MoreFragment.newInstance();
                    ft.add(R.id.fragment_container, moreFragment);
                } else {

                    ft.show(moreFragment);
                }

                currentFragment = moreFragment;
                break;
            default:
                break;
        }

        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initLog() {

        ThreadUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                Logger.i(Tags.SYSTEM, DeviceUtil.getDeviceBrand() + ", " + DeviceUtil.getSystemModel() + ", " + DeviceUtil.getSystemVersion());

                User user = SystemHelper.getUser();
                Driver driver = DataBaseHelper.getDataBase().driverDao().getDriver(user.getDriverId());
                int ruleId = DataBaseHelper.getDataBase().driverRuleDao().getRuleId(user.getDriverId());
                Rule rule = DataBaseHelper.getDataBase().ruleDao().getRule(ruleId);

                Carrier carrier = DataBaseHelper.getDataBase().carrierDao().getCarrier(user.getCarriedId());
                List<Integer> ruleIds = DataBaseHelper.getDataBase().carrierRuleDao().getRuleIdList(user.getCarriedId());
                List<Rule> ruleList = DataBaseHelper.getDataBase().ruleDao().getRuleList(ruleIds);

                DriverVo driverVo = new DriverVo(driver, rule);
                CarrierVo carrierVo = new CarrierVo(carrier, ruleList);

                Logger.i(Tags.SYSTEM, "user info: AccountCarrierId=" + user.getAccountCarrierId() +
                        ", AccountDriverId=" + user.getAccountDriverId() +
                        ", AppVersion=" + AppUtil.getVersionName(MainActivity.this));
                Logger.i(Tags.SYSTEM, "driver info: id=" + driverVo.getId() +
                        ", name=" + driverVo.getName() +
                        ", rule=" + driverVo.getRule().getDutyDays() + "/" + (driverVo.getRule().getDutyTime() / 60));
                Logger.i(Tags.SYSTEM, "carrier info: id=" + carrierVo.getId() +
                        ", name=" + carrierVo.getName());
            }
        });
    }
}
