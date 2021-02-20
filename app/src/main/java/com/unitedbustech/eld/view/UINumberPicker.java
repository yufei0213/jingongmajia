package com.unitedbustech.eld.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * @author yufei0213
 * @date 2018/1/31
 * @description 自定义数字选择器
 */
public class UINumberPicker extends NumberPicker {

    public UINumberPicker(Context context) {

        super(context);
    }

    public UINumberPicker(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public UINumberPicker(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }

    @Override
    public void addView(View child) {

        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index, android.view.ViewGroup.LayoutParams params) {

        super.addView(child, index, params);
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {

        super.addView(child, params);
        updateView(child);
    }

    private void updateView(View view) {

        if (view instanceof EditText) {

            EditText editText = (EditText) view;

            editText.setTextColor(Color.parseColor("#333333"));
            editText.setTextSize(22);
            editText.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/SFUIText-Regular.ttf"));
        }
    }
}
