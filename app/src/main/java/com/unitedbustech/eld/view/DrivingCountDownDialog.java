package com.unitedbustech.eld.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/30
 * @description DrivingCountDownDialog
 */
public class DrivingCountDownDialog extends AppCompatDialog {

    private static final String TAG = "DrivingCountDownDialog";

    private static final int BUTTON_NEGATIVE = -2;
    private static final int BUTTON_NEUTRAL = -3;

    private static final int COUNT_DOWN_MAX = 60;
    private static final int COUNT_DOWN_MIN = 0;
    private static final long COUNT_DOWN_DURATION = 1 * 1000L;

    private static final String NEUTRALBTN_REPLACE = "#number#";

    private Handler handler;
    private Runnable runnable;

    private int count;
    private String neutralBtnTextTemplate;

    private Button negativeBtn;
    private Button neutralBtn;

    private OnClickListener negativeBtnListener;
    private OnClickListener neutralBtnListener;

    private OnCountDownListener onCountDownListener;

    private DrivingCountDownDialog(@NonNull Context context, Builder builder) {

        super(context);

        this.handler = new Handler(Looper.getMainLooper());
        this.count = COUNT_DOWN_MAX;
        this.neutralBtnTextTemplate = context.getString(R.string.dialog_driving_count_down_yes);

        this.negativeBtnListener = builder.negativeBtnListener;
        this.neutralBtnListener = builder.neutralBtnListener;

        this.onCountDownListener = builder.onCountDownListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_driving_count_down);
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

        setCancelable(false);

        runnable = new Runnable() {
            @Override
            public void run() {

                if (count < COUNT_DOWN_MIN) {

                    handler.removeCallbacks(this);
                    if (onCountDownListener != null) {

                        onCountDownListener.onCountDownEnd(DrivingCountDownDialog.this);
                    }
                } else {

                    String number = String.format("%02d", count);
                    neutralBtn.setText(neutralBtnTextTemplate.replace(NEUTRALBTN_REPLACE, number));

                    count--;

                    handler.postDelayed(this, COUNT_DOWN_DURATION);
                }
            }
        };

        handler.post(runnable);
    }

    @Override
    public void dismiss() {

        if (runnable != null) {

            handler.removeCallbacks(runnable);
        }

        runnable = null;
        count = COUNT_DOWN_MAX;

        super.dismiss();
    }

    private void initView() {

        negativeBtn = this.findViewById(R.id.negative_btn);
        neutralBtn = this.findViewById(R.id.neutral_btn);

        if (negativeBtnListener != null) {

            negativeBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    negativeBtnListener.onClick(DrivingCountDownDialog.this, BUTTON_NEGATIVE);
                }
            }));
        }

        if (neutralBtnListener != null) {

            neutralBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    neutralBtnListener.onClick(DrivingCountDownDialog.this, BUTTON_NEUTRAL);
                }
            }));
        }
    }

    public interface OnClickListener {

        void onClick(DrivingCountDownDialog dialog, int which);
    }

    public interface OnCountDownListener {

        void onCountDownEnd(DrivingCountDownDialog dialog);
    }

    public static class Builder {

        private Context context;

        private OnClickListener negativeBtnListener;
        private OnClickListener neutralBtnListener;

        private OnCountDownListener onCountDownListener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder countDownListener(OnCountDownListener listener) {

            this.onCountDownListener = listener;
            return this;
        }

        public Builder negativeBtnListener(OnClickListener listener) {

            this.negativeBtnListener = listener;
            return this;
        }

        public Builder neutralBtnListener(OnClickListener listener) {

            this.neutralBtnListener = listener;
            return this;
        }

        public DrivingCountDownDialog build() {

            return new DrivingCountDownDialog(context, this);
        }
    }
}
