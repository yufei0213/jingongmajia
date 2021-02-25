package com.interest.calculator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.interest.calculator.R;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description TitleBar
 */
public class TitleBar extends RelativeLayout {

    private RelativeLayout containerView;
    private ImageButton backBtn;
    private TextView titleView;

    private TitleBarListener listener;

    public TitleBar(Context context) {

        this(context, null);
    }

    public TitleBar(final Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, true);

        containerView = this.findViewById(R.id.container);
        backBtn = this.findViewById(R.id.back_btn);
        titleView = this.findViewById(R.id.title);

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listener != null) {

                    listener.onLeftBtnClick();
                }
            }
        });

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.title_bar);
        String title = typedArray.getString(R.styleable.title_bar_title);

        if (!TextUtils.isEmpty(title)) {

            setTitle(title);
        }

        typedArray.recycle();
    }

    /**
     * 设置监听
     *
     * @param listener 监听者
     */
    public void setListener(TitleBarListener listener) {

        this.listener = listener;
    }

    public void setBackground(String color) {

        containerView.setBackgroundColor(Color.parseColor(color));
    }

    public void setTitleColor(String color) {
        switch (color) {
            case "white":
                titleView.setTextColor(Color.WHITE);
                break;
            case "black":
                titleView.setTextColor(Color.BLACK);
                break;
            default:
                break;

        }
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {

        titleView.setText(title);
    }

    /**
     * 设置是否哟返回按钮
     *
     * @param backAvailable 是否有返回按钮
     */
    public void setBackAvailable(boolean backAvailable) {

        if (!backAvailable) {

            this.backBtn.setVisibility(GONE);
        } else {

            this.backBtn.setVisibility(VISIBLE);
        }
    }

    public interface TitleBarListener {

        void onLeftBtnClick();
    }
}
