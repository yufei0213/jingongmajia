package com.interest.calculator.view;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.interest.calculator.R;

/**
 * @author zhangyu
 * @date 2018/1/16
 * @description 带有红点的主页菜单项
 */
public class RedDotTabMenuItem extends LinearLayout {

    private TextView itemView;

    private TextView redDot;

    public RedDotTabMenuItem(Context context) {

        this(context, null);
    }

    public RedDotTabMenuItem(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.item_tab_menu_red_dot, this, true);

        itemView = this.findViewById(R.id.tab_menu_text);
        redDot = this.findViewById(R.id.tab_menu_hint);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.tab_menu_item);

        String itemText = typedArray.getString(R.styleable.tab_menu_item_itemText);
        int itemTextColor = typedArray.getResourceId(R.styleable.tab_menu_item_itemTextColors, 0);
        int itemIcon = typedArray.getResourceId(R.styleable.tab_menu_item_itemIcon, 0);

        itemView.setCompoundDrawablesWithIntrinsicBounds(null, context.getResources().getDrawable(itemIcon), null, null);
        itemView.setTextColor(context.getResources().getColorStateList(itemTextColor));
        itemView.setText(itemText);

        typedArray.recycle();
    }

    public void setSelected() {

        itemView.setSelected(true);
    }

    public void cancelSelected() {

        itemView.setSelected(false);
    }

    public void setMoreTabRedDotVisible(boolean isVisible) {

        if (isVisible) {

            redDot.setVisibility(VISIBLE);
        } else {

            redDot.setVisibility(GONE);
        }
    }
}
