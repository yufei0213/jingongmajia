package com.interest.calculator.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.interest.calculator.R;

/**
 * @author yufei0213
 * @date 2018/1/23
 * @description 操作结果提示框
 */
public class PromptDialog extends AppCompatDialog {

    public static final int SUCCESS = 1;
    public static final int FAILURE = -1;

    private static final long SHOW_DURATION = 1 * 1000L;

    private int type;
    private OnHideListener listener;

    private ImageView iconView;
    private TextView textView;

    private PromptDialog(@NonNull Context context, Builder builder) {

        super(context);

        this.type = builder.type;
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_prompt);

        //窗口与屏幕一样大
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        layoutParams.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        getWindow().setAttributes(layoutParams);

        //设置透明背景
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initView();
    }

    @Override
    public void show() {

        super.show();
        setCancelable(false);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (PromptDialog.this.isShowing()) {

                    PromptDialog.this.dismiss();
                }

                if (listener != null) {

                    listener.onHide();
                }
            }
        }, SHOW_DURATION);
    }

    private void initView() {

        iconView = this.findViewById(R.id.icon);
        textView = this.findViewById(R.id.text);

        if (type == SUCCESS) {

            iconView.setImageResource(R.drawable.ic_dialog_prompt_success);
            textView.setText(R.string.dialog_prompt_success);
        } else if (type == FAILURE) {

            iconView.setImageResource(R.drawable.ic_dialog_prompt_failed);
            textView.setText(R.string.dialog_prompt_failed);
        }
    }

    public interface OnHideListener {

        void onHide();
    }

    public static class Builder {

        private Context context;

        private int type;

        private OnHideListener listener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder type(int type) {

            this.type = type;
            return this;
        }

        public Builder listener(OnHideListener listener) {

            this.listener = listener;
            return this;
        }

        public PromptDialog build() {

            return new PromptDialog(context, this);
        }
    }
}
