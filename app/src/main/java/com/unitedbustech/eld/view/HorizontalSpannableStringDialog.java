package com.unitedbustech.eld.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatDialog;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/23
 * @description 横向通用弹窗
 */
public class HorizontalSpannableStringDialog extends AppCompatDialog {

    private static final int BUTTON_POSITIVE = -1;
    private static final int BUTTON_NEGATIVE = -2;
    private static final int BUTTON_NEUTRAL = -3;

    private ImageView iconView;
    private TextView textView;
    private View btnGroupView;

    private Button positiveBtn;
    private Button negativeBtn;
    private Button neutralBtn;

    private int iconResId;
    private SpannableString spannableString;

    private String negativeBtnText;
    private String neutralBtnText;
    private String positiveBtnText;

    private OnClickListener positiveBtnListener;
    private OnClickListener negativeBtnListener;
    private OnClickListener neutralBtnListener;

    private boolean cancelable;

    private HorizontalSpannableStringDialog(@NonNull Context context, Builder builder) {

        super(context);

        this.iconResId = builder.iconResId;
        this.spannableString = builder.spannableString;

        this.negativeBtnText = builder.negativeBtnText;
        this.neutralBtnText = builder.neutralBtnText;
        this.positiveBtnText = builder.positiveBtnText;

        this.positiveBtnListener = builder.positiveBtnListener;
        this.negativeBtnListener = builder.negativeBtnListener;
        this.neutralBtnListener = builder.neutralBtnListener;

        this.cancelable = builder.cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_horizontal);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置dialog的宽度为屏幕的宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(layoutParams);

        initView();
    }

    @Override
    public void show() {

        super.show();

        setCancelable(cancelable);
    }

    private void initView() {

        iconView = this.findViewById(R.id.icon_img);
        textView = this.findViewById(R.id.text_view);
        btnGroupView = this.findViewById(R.id.btn_group);

        positiveBtn = this.findViewById(R.id.positive_btn);
        negativeBtn = this.findViewById(R.id.negative_btn);
        neutralBtn = this.findViewById(R.id.neutral_btn);

        if (iconResId != 0) {

            iconView.setImageResource(iconResId);
        }

        if(spannableString != null) {

            textView.setText(spannableString);
        }

        if (TextUtils.isEmpty(positiveBtnText)) {

            positiveBtn.setVisibility(View.GONE);
            btnGroupView.setVisibility(View.VISIBLE);

            negativeBtn.setText(negativeBtnText);
            neutralBtn.setText(neutralBtnText);
        } else {

            positiveBtn.setVisibility(View.VISIBLE);
            btnGroupView.setVisibility(View.GONE);

            positiveBtn.setText(positiveBtnText);
        }

        if (positiveBtnListener != null) {

            positiveBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    positiveBtnListener.onClick(HorizontalSpannableStringDialog.this, BUTTON_POSITIVE);
                }
            }));
        }

        if (negativeBtnListener != null) {

            negativeBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    negativeBtnListener.onClick(HorizontalSpannableStringDialog.this, BUTTON_NEGATIVE);
                }
            }));
        }

        if (neutralBtnListener != null) {

            neutralBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    neutralBtnListener.onClick(HorizontalSpannableStringDialog.this, BUTTON_NEUTRAL);
                }
            }));
        }
    }

    public interface OnClickListener {

        void onClick(HorizontalSpannableStringDialog dialog, int which);
    }

    public static class Builder {

        private Context context;

        private int iconResId;
        private SpannableString spannableString;
        private String negativeBtnText;
        private String neutralBtnText;
        private String positiveBtnText;

        private OnClickListener positiveBtnListener;
        private OnClickListener negativeBtnListener;
        private OnClickListener neutralBtnListener;

        private boolean cancelable = true;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder setCancelable(boolean cancelable) {

            this.cancelable = cancelable;
            return this;
        }

        public Builder setIcon(@DrawableRes int resId) {

            this.iconResId = resId;
            return this;
        }

        public Builder setText(SpannableString spannableString) {

            this.spannableString = spannableString;
            return this;
        }

        public Builder setNegativeBtn(String text, OnClickListener listener) {

            this.negativeBtnText = text;
            this.negativeBtnListener = listener;
            return this;
        }

        public Builder setNegativeBtn(@StringRes int resId, OnClickListener listener) {

            this.negativeBtnText = context.getString(resId);
            this.negativeBtnListener = listener;
            return this;
        }

        public Builder setNeutralBtn(String text, OnClickListener listener) {

            this.neutralBtnText = text;
            this.neutralBtnListener = listener;
            return this;
        }

        public Builder setNeutralBtn(@StringRes int resId, OnClickListener listener) {

            this.neutralBtnText = context.getString(resId);
            this.neutralBtnListener = listener;
            return this;
        }

        public Builder setPositiveBtn(String text, OnClickListener listener) {

            this.positiveBtnText = text;
            this.positiveBtnListener = listener;
            return this;
        }

        public Builder setPositiveBtn(@StringRes int resId, OnClickListener listener) {

            this.positiveBtnText = context.getString(resId);
            this.positiveBtnListener = listener;
            return this;
        }

        public HorizontalSpannableStringDialog build() {

            return new HorizontalSpannableStringDialog(context, this);
        }
    }
}
