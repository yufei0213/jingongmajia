package com.unitedbustech.eld.dailylog.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.eventbus.DailylogSignAlertEvent;
import com.unitedbustech.eld.eventbus.HosModelChangeEvent;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.hos.model.HosDayModel;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.TimeUtil;
import com.unitedbustech.eld.view.ClickProxy;
import com.unitedbustech.eld.view.TopTabMenu;
import com.unitedbustech.eld.view.UIWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

/**
 * @author yufei0213
 * @date 2017/12/4
 * @description 应用主界面
 */
public class DailylogDetailActivity extends BaseActivity implements TopTabMenu.TabMenuSelectedListener {

    private Fragment currentFragment;
    private LogFragment logFragment;
    private ProfileFragment profileFragment;
    private SignFragment signFragment;

    private TextView titleView;
    private ImageButton preDayBtn;
    private ImageButton nextDayBtn;

    private TopTabMenu tabMenu;

    private int index;
    private String title;

    private ClickProxy btnClickListener = new ClickProxy(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.back_btn:

                    finish();
                    break;
                case R.id.left_btn:

                    if (index == Constants.DDL_DAYS - 1) {
                        return;
                    }
                    index++;
                    //默认选择第一页
                    tabMenu.setSelectedItem(TopTabMenu.LOG);
                    //刷新视图
                    refreshView();
                    break;
                case R.id.right_btn:

                    if (index == 0) {
                        return;
                    }

                    index--;
                    //默认选择第一页
                    tabMenu.setSelectedItem(TopTabMenu.LOG);
                    //刷新视图
                    refreshView();
                    break;
                default:
                    break;
            }
        }
    });

    public static Intent newIntent(Context context, String data) {

        Intent intent = new Intent(context, DailylogDetailActivity.class);
        intent.putExtra(UIWebView.EXTRA_PARAMS, data);

        return intent;
    }

    @Override
    protected void initVariables() {

        String json = getIntent().getStringExtra(UIWebView.EXTRA_PARAMS);
        JSONObject jsonObject = JsonUtil.parseObject(json);

        index = JsonUtil.getInt(jsonObject, "index");
        if (index >= Constants.DDL_DAYS) {

            index = Constants.DDL_DAYS - 1;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected View onCreateView(Bundle savedInstanceState) {

        HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);

        View view = LayoutInflater.from(this).inflate(R.layout.activity_dailylog_detail, null);

        view.findViewById(R.id.back_btn).setOnClickListener(btnClickListener);

        preDayBtn = view.findViewById(R.id.left_btn);
        if (index == Constants.DDL_DAYS - 1) {

            preDayBtn.setImageResource(R.drawable.ic_dailylog_left);
        }
        preDayBtn.setOnClickListener(btnClickListener);

        title = TimeUtil.getDailylogFromat(hosDayModel.getDate(), true).toUpperCase();
        titleView = view.findViewById(R.id.title);
        titleView.setText(title);

        nextDayBtn = view.findViewById(R.id.right_btn);
        if (index == 0) {

            nextDayBtn.setImageResource(R.drawable.ic_dailylog_right);
        }
        nextDayBtn.setOnClickListener(btnClickListener);

        tabMenu = view.findViewById(R.id.daily_log_tab_menu);
        tabMenu.setListener(this);
        if (!TextUtils.isEmpty(hosDayModel.getSign())) {

            tabMenu.hideSignAlert();
        } else {

            tabMenu.showSignAlert();
        }

        //默认选择第一页
        tabMenu.setSelectedItem(TopTabMenu.LOG);

        return view;
    }

    @Override
    public void onTabItemSelected(int index) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (index) {

            case TopTabMenu.LOG:

                if (currentFragment != null && currentFragment != logFragment) {

                    ft.hide(currentFragment);
                }

                if (logFragment == null) {

                    logFragment = LogFragment.newInstance();
                    ft.add(R.id.daily_log_fragment_container, logFragment);
                } else {

                    ft.show(logFragment);
                }

                currentFragment = logFragment;
                break;
            case TopTabMenu.PROFILE:

                if (currentFragment != null && currentFragment != profileFragment) {

                    ft.hide(currentFragment);
                }

                if (profileFragment == null) {

                    profileFragment = ProfileFragment.newInstance();
                    ft.add(R.id.daily_log_fragment_container, profileFragment);
                } else {

                    ft.show(profileFragment);
                }

                currentFragment = profileFragment;
                break;
            case TopTabMenu.SIGN:

                if (currentFragment != null && currentFragment != signFragment) {

                    ft.hide(currentFragment);
                }

                if (signFragment == null) {

                    signFragment = SignFragment.newInstance();
                    ft.add(R.id.daily_log_fragment_container, signFragment);
                } else {

                    ft.show(signFragment);
                }

                currentFragment = signFragment;
                break;
            default:
                break;
        }

        ft.commit();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDailylogSignAlertEvent(DailylogSignAlertEvent dailylogSignAlertEvent) {

        HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
        String sign = hosDayModel.getSign();
        if (!TextUtils.isEmpty(sign)) {

            tabMenu.hideSignAlert();
        } else {

            tabMenu.showSignAlert();
        }
        if (currentFragment instanceof SignFragment) {

            signFragment.reloadFile();
        } else {

            signFragment.needReloadFile();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHosModelChangeEvent(HosModelChangeEvent hosModelChangeEvent) {

        if (currentFragment instanceof LogFragment) {

            logFragment.reloadFile();
        } else {

            logFragment.needReloadFile();
        }
    }

    /**
     * 刷新视图
     */
    private void refreshView() {

        //重新赋值，以便js初始化时，能够取到新的index
        JSONObject newObj = new JSONObject();
        newObj.put("index", index);
        Intent intent = new Intent();
        intent.putExtra(UIWebView.EXTRA_PARAMS, newObj.toJSONString());
        setIntent(intent);

        if (index == 0) {

            nextDayBtn.setImageResource(R.drawable.ic_dailylog_right);
        } else if (index == Constants.DDL_DAYS - 1) {

            preDayBtn.setImageResource(R.drawable.ic_dailylog_left);
        } else {

            preDayBtn.setImageResource(R.drawable.btn_dailog_detail_left);
            nextDayBtn.setImageResource(R.drawable.btn_dailog_detail_right);
        }

        HosDayModel hosDayModel = ModelCenter.getInstance().getAllHosDayModels().get(index);
        Date date = hosDayModel.getDate();
        title = TimeUtil.getDailylogFromat(date, true).toUpperCase();
        titleView.setText(title);

        String sign = hosDayModel.getSign();
        if (!TextUtils.isEmpty(sign)) {

            tabMenu.hideSignAlert();
        } else {

            tabMenu.showSignAlert();
        }

        if (currentFragment instanceof LogFragment) {

            logFragment.reloadFile();
            if (profileFragment != null) {

                profileFragment.needReloadFile();
            }
            if (signFragment != null) {

                signFragment.needReloadFile();
            }
        }
        if (currentFragment instanceof ProfileFragment) {

            profileFragment.reloadFile();
            if (logFragment != null) {

                logFragment.needReloadFile();
            }
            if (signFragment != null) {

                signFragment.needReloadFile();
            }
        }
        if (currentFragment instanceof SignFragment) {

            signFragment.reloadFile();
            if (logFragment != null) {

                logFragment.needReloadFile();
            }
            if (profileFragment != null) {

                profileFragment.needReloadFile();
            }
        }
    }
}
