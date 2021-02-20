package com.unitedbustech.eld.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.unitedbustech.eld.R;

/**
 * @author mamw
 * @date 2018/1/24
 * @description DOT Insepction 菜单
 */
public class TabMenuDot extends LinearLayout {

    public static final int TAB_REVIEW = 0;
    public static final int TAB_SEND = 1;

    private TabMenuItem reviewTabMenuItem;
    private TabMenuItem sendTabMenuItem;

    private TabMenuSelectedListener listener;

    private ClickProxy clickProxy = new ClickProxy(new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.tab_review:

                    resetSelected();
                    reviewTabMenuItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(TAB_REVIEW);
                    }
                    break;
                case R.id.tab_send:

                    resetSelected();
                    sendTabMenuItem.setSelected();

                    if (listener != null) {

                        listener.onTabItemSelected(TAB_SEND);
                    }
                    break;

                default:
                    break;
            }
        }
    });

    public TabMenuDot(Context context) {

        this(context, null);
    }

    public TabMenuDot(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.tab_menu_dot, this, true);

        reviewTabMenuItem = this.findViewById(R.id.tab_review);
        sendTabMenuItem = this.findViewById(R.id.tab_send);

        reviewTabMenuItem.setOnClickListener(clickProxy);
        sendTabMenuItem.setOnClickListener(clickProxy);
    }

    public void setListener(TabMenuSelectedListener listener) {

        this.listener = listener;
    }

    public void setSelectedItem(int index) {

        switch (index) {

            case TAB_REVIEW:

                reviewTabMenuItem.performClick();
                break;
            case TAB_SEND:

                sendTabMenuItem.performClick();
                break;
            default:
                break;
        }
    }

    private void resetSelected() {

        reviewTabMenuItem.cancelSelected();
        sendTabMenuItem.cancelSelected();
    }

    public interface TabMenuSelectedListener {

        void onTabItemSelected(int index);
    }
}
