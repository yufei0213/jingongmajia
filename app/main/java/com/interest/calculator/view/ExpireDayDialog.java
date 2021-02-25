package com.interest.calculator.view;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.interest.calculator.R;
import com.interest.calculator.common.Constants;


/**
 * Created by liyan.
 *
 * @date 2019/4/3
 * @description 没有icon的弹窗, 专门用于到期续费提醒弹窗
 */

public class ExpireDayDialog extends AppCompatDialog {

    private static final int BUTTON_NEGATIVE = -2;
    private static final int BUTTON_NEUTRAL = -3;

    private TextView contentView;
    private TextView content_text_days_view;
    private TextView content_text_view_phone;
    private Button negativeBtn;
    private Button neutralBtn;
    private String title;
    private String content;

    private OnClickListener negativeListener;
    private OnClickListener neutralListener;

    private boolean cancelable;

    public ExpireDayDialog(Context context, Builder builder) {

        super(context);

        this.title = builder.title;
        this.content = builder.content;
        this.negativeListener = builder.negativeListener;
        this.neutralListener = builder.neutralListener;
        this.cancelable = builder.cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_expireday);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置dialog的宽度为屏幕的宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(layoutParams);

        initView();
    }

    @Override
    public void show() {

        if (isShowing()) {
            cancel();
        }
        super.show();
        setCancelable(cancelable);
    }

    private void initView() {

        contentView = this.findViewById(R.id.content_text_view);
        content_text_days_view = this.findViewById(R.id.content_text_days_view);
        negativeBtn = this.findViewById(R.id.negative_btn);
        neutralBtn = this.findViewById(R.id.neutral_btn);
        content_text_view_phone = this.findViewById(R.id.content_text_view_phone);

        if (!TextUtils.isEmpty(content)) {

            content_text_days_view.setText(content);
            contentView.setText(String.format(getContext().getResources().getString(R.string.expire_dialog_content_text), content));
        }

        if (neutralListener != null) {

            neutralBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    neutralListener.onClick(ExpireDayDialog.this, BUTTON_NEUTRAL);
                }
            }));
        }

        if (negativeListener != null) {

            negativeBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    negativeListener.onClick(ExpireDayDialog.this, BUTTON_NEGATIVE);
                }
            }));
        }

        content_text_view_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPhone();
            }
        });

    }

    public void callPhone() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + Constants.SERVICE_PHONENUMBER);
        intent.setData(data);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {

            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public interface OnClickListener {

        void onClick(ExpireDayDialog dialog, int which);
    }

    public static class Builder {

        private Context context;

        private String title;
        private String content;


        private OnClickListener negativeListener;
        private OnClickListener neutralListener;

        private boolean cancelable;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder setTitle(String title) {

            this.title = title;
            return this;
        }

        public Builder setContent(String content) {

            this.content = content;
            return this;
        }

        public Builder setNeutralListener(OnClickListener neutralListener) {

            this.neutralListener = neutralListener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {

            this.cancelable = cancelable;
            return this;
        }

        public ExpireDayDialog build() {

            return new ExpireDayDialog(context, this);
        }
    }
}
