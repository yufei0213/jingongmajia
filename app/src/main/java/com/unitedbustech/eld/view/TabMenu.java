package com.unitedbustech.eld.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.eventbus.AlertsRefreshViewEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 主页菜单
 */
public class TabMenu extends LinearLayout {

    public static final String TAB_INDEX = "tabIndex";
    public static final int DASHBORAD = 0;
    public static final int DAILYLOG = 1;
    public static final int DVIR = 2;
    public static final int DOT = 3;
    public static final int MORE = 4;

    private TabMenuItem dashboardItem;
    private TabMenuItem dailylogItem;
    private TabMenuItem dvirItem;
    private TabMenuItem dotItem;
    private RedDotTabMenuItem moreItem;

    private TabMenuSelectedListener listener;

    public int currentTabIndex = 0;

    private ClickProxy clickProxy = new ClickProxy(new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.dashboard:

                    resetSelected();
                    dashboardItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(DASHBORAD);
                        currentTabIndex = DASHBORAD;
                    }
                    break;
                case R.id.dailylog:

                    resetSelected();
                    dailylogItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(DAILYLOG);
                        currentTabIndex = DAILYLOG;
                    }
                    break;
                case R.id.dvir:

                    resetSelected();
                    dvirItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(DVIR);
                        currentTabIndex = DVIR;
                    }
                    break;
                case R.id.dot:

                    resetSelected();
                    dotItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(DOT);
                        currentTabIndex = DOT;
                    }
                    break;
                case R.id.more:

                    resetSelected();
                    moreItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(MORE);
                        currentTabIndex = MORE;
                    }
                default:
                    break;
            }
        }
    });

    public TabMenu(Context context) {

        this(context, null);
    }

    public TabMenu(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.tab_menu, this, true);

        dashboardItem = this.findViewById(R.id.dashboard);
        dailylogItem = this.findViewById(R.id.dailylog);
        dvirItem = this.findViewById(R.id.dvir);
        dotItem = this.findViewById(R.id.dot);
        moreItem = this.findViewById(R.id.more);

        dashboardItem.setOnClickListener(clickProxy);
        dailylogItem.setOnClickListener(clickProxy);
        dvirItem.setOnClickListener(clickProxy);
        dotItem.setOnClickListener(clickProxy);
        moreItem.setOnClickListener(clickProxy);

        if (EventBus.getDefault().isRegistered(this)) {

            EventBus.getDefault().unregister(this);
        }
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAlertsRefreshViewEvent(AlertsRefreshViewEvent alertsRefreshViewEvent) {

        int count = alertsRefreshViewEvent.getNotCertifiedLogsCnt() +
                alertsRefreshViewEvent.getRequestedEditsCnt() +
                alertsRefreshViewEvent.getAssignedCnt();

        moreItem.setMoreTabRedDotVisible(count > 0);
    }

    public void setListener(TabMenuSelectedListener listener) {

        this.listener = listener;
    }

    public void setSelectedItem(int index) {

        switch (index) {

            case DASHBORAD:

                dashboardItem.performClick();
                break;
            case DAILYLOG:

                dailylogItem.performClick();
                break;
            case DVIR:

                dvirItem.performClick();
                break;
            case DOT:

                dotItem.performClick();
                break;
            case MORE:

                moreItem.performClick();
                break;
            default:
                break;
        }
    }

    private void resetSelected() {

        dashboardItem.cancelSelected();
        dailylogItem.cancelSelected();
        dvirItem.cancelSelected();
        dotItem.cancelSelected();
        moreItem.cancelSelected();
    }

    public interface TabMenuSelectedListener {

        void onTabItemSelected(int index);
    }
}
