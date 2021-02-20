package com.unitedbustech.eld.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description DaillLogDetail 顶部菜单选项
 */
public class TopTabMenuItem extends LinearLayout {

    private TextView itemView;

    private View hintView;

    private View lineView;

    public TopTabMenuItem(Context context) {

        this(context, null);
    }

    public TopTabMenuItem(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_tab_menu_dailylog_detail, this, true);

        itemView = this.findViewById(R.id.daily_log_tab_menu_text);
        hintView = this.findViewById(R.id.daily_log_tab_menu_hint);
        lineView = this.findViewById(R.id.daily_log_tab_menu_line);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.tab_menu_item);

        String itemText = typedArray.getString(R.styleable.tab_menu_item_itemText);
        int itemTextColor = typedArray.getResourceId(R.styleable.tab_menu_item_itemTextColor, 0);

        itemView.setTextColor(context.getResources().getColorStateList(itemTextColor));
        itemView.setText(itemText);

        typedArray.recycle();
    }

    public void setSelected() {

        itemView.setSelected(true);
        lineView.setVisibility(VISIBLE);
    }

    public void cancelSelected() {

        itemView.setSelected(false);
        lineView.setVisibility(GONE);
    }

    public void showAlert() {

        hintView.setVisibility(VISIBLE);
    }

    public void hideAlert() {

        hintView.setVisibility(GONE);
    }
}
