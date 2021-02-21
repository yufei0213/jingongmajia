package com.unitedbustech.eld.view;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description DaillLogDetail 顶部菜单栏
 */
public class TopTabMenu extends LinearLayout implements View.OnClickListener {

    public static final int LOG = 0;
    public static final int PROFILE = 1;
    public static final int SIGN = 2;

    private TopTabMenuItem curItem;
    private TopTabMenuItem logItem;
    private TopTabMenuItem profileItem;
    private TopTabMenuItem signItem;

    private TabMenuSelectedListener listener;

    public TopTabMenu(Context context) {

        this(context, null);
    }

    public TopTabMenu(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.tab_menu_dailylog_detail, this, true);

        logItem = this.findViewById(R.id.dailylog_log);
        profileItem = this.findViewById(R.id.dailylog_profile);
        signItem = this.findViewById(R.id.dailylog_sign);

        logItem.setOnClickListener(this);
        profileItem.setOnClickListener(this);
        signItem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.dailylog_log:

                if (curItem == logItem) {

                    return;
                } else {

                    curItem = logItem;
                }
                resetSelected();
                logItem.setSelected();

                if (listener != null) {

                    listener.onTabItemSelected(LOG);
                }
                break;
            case R.id.dailylog_profile:

                if (curItem == profileItem) {

                    return;
                } else {

                    curItem = profileItem;
                }
                resetSelected();
                profileItem.setSelected();

                if (listener != null) {

                    listener.onTabItemSelected(PROFILE);
                }
                break;
            case R.id.dailylog_sign:

                if (curItem == signItem) {

                    return;
                } else {

                    curItem = signItem;
                }
                resetSelected();
                signItem.setSelected();

                if (listener != null) {

                    listener.onTabItemSelected(SIGN);
                }
                break;
            default:
                break;
        }
    }

    public void setListener(TabMenuSelectedListener listener) {

        this.listener = listener;
    }

    public void setSelectedItem(int index) {

        switch (index) {

            case LOG:

                logItem.performClick();
                break;
            case PROFILE:

                profileItem.performClick();
                break;
            case SIGN:

                signItem.performClick();
                break;
            default:
                break;
        }
    }

    public void showSignAlert() {

        signItem.showAlert();
    }

    public void hideSignAlert() {

        signItem.hideAlert();
    }

    private void resetSelected() {

        logItem.cancelSelected();
        profileItem.cancelSelected();
        signItem.cancelSelected();
    }

    public interface TabMenuSelectedListener {

        void onTabItemSelected(int index);
    }
}
