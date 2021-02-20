package com.unitedbustech.eld.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;

/**
 * @author yufei0213
 * @date 2018/1/26
 * @description 超过一行换行居中
 */
public class CenterTextView extends FontTextView {

    private StaticLayout staticLayout;
    private TextPaint textPaint;

    public CenterTextView(Context context) {

        this(context, null);
    }

    public CenterTextView(Context context, @Nullable AttributeSet attrs) {

        this(context, attrs, android.R.attr.textViewStyle);
    }

    public CenterTextView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);

        initView();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        staticLayout.draw(canvas);
    }

    private void initView() {

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(getTextSize());
        textPaint.setColor(getCurrentTextColor());
        staticLayout = new StaticLayout(getText(), textPaint, getWidth(), Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
    }
}
