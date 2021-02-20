package com.unitedbustech.eld.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description TitleBar
 */
public class TabItemTitleBar extends RelativeLayout {

    private TextView titleView;
    private ImageView iconView;
    private TextView rightTextView;

    public TabItemTitleBar(Context context) {

        this(context, null);
    }

    public TabItemTitleBar(final Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_tab_title_bar, this, true);

        titleView = this.findViewById(R.id.title);
        iconView = this.findViewById(R.id.icon);
        rightTextView = this.findViewById(R.id.rightText);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.tab_item_title_bar);
        String title = typedArray.getString(R.styleable.tab_item_title_bar_title);
        boolean isShowIcon = typedArray.getBoolean(R.styleable.tab_item_title_bar_isShowIcon, false);
        String rightText = typedArray.getString(R.styleable.tab_item_title_bar_rightText);

        if (isShowIcon) {

            iconView.setVisibility(VISIBLE);
        }

        if (!TextUtils.isEmpty(title)) {

            titleView.setText(title);
        }

        if (!TextUtils.isEmpty(rightText)) {

            rightTextView.setVisibility(VISIBLE);
            rightTextView.setText(rightText);
        }

        typedArray.recycle();
    }

    public TextView getRightTextView() {
        return rightTextView;
    }

    public void setTitle(int resId) {

        this.titleView.setText(resId);
    }
}
