package com.unitedbustech.eld.view;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unitedbustech.eld.R;

/**
 * @author mamw
 * @date 2018/1/26
 * @description Dot Review退出弹窗
 */
public class DotExitVerifyDialog extends AppCompatDialog implements PasswordInputView.PasswordInputListener {

    private ImageView iconView;
    private TextView textView;

    private Button negativeBtn;
    private Button neutralBtn;

    private OnClickListener negativeBtnListener;
    private OnClickListener neutralBtnListener;

    private PasswordInputView passwordInputView;
    private String password;

    public DotExitVerifyDialog(@NonNull Context context, Builder builder) {

        super(context);

        this.negativeBtnListener = builder.negativeBtnListener;
        this.neutralBtnListener = builder.neutralBtnListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_dot_exit_verify);
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
    }

    private void initView() {

        iconView = this.findViewById(R.id.icon_img);
        textView = this.findViewById(R.id.text_view);

        negativeBtn = this.findViewById(R.id.negative_btn);
        neutralBtn = this.findViewById(R.id.neutral_btn);

        iconView.setImageResource(R.drawable.ic_emoji_msg);
        textView.setText(R.string.dot_exit_tip);

        negativeBtn.setText(R.string.cancel);
        neutralBtn.setText(R.string.ok);

        if (negativeBtnListener != null) {

            negativeBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    negativeBtnListener.onClick(DotExitVerifyDialog.this);
                }
            }));
        }

        if (neutralBtnListener != null) {

            neutralBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    neutralBtnListener.onClick(DotExitVerifyDialog.this);
                }
            }));
        }


        passwordInputView = this.findViewById(R.id.password_input_view);
        passwordInputView.setListener(this);

    }

    @Override
    public void afterPasswordTextChanged(String text) {

        this.password = text;
    }

    public String getPassword() {

        return this.password;
    }

    public void clearPassword() {

        this.passwordInputView.clearPasswordInput();
    }

    public void setPasswordWaring() {

        this.passwordInputView.setPasswordWaring(true);
    }

    public interface OnClickListener {

        void onClick(DotExitVerifyDialog dialog);
    }

    public static class Builder {

        private Context context;

        private OnClickListener negativeBtnListener;
        private OnClickListener neutralBtnListener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder setNegativeBtn(OnClickListener listener) {

            this.negativeBtnListener = listener;
            return this;
        }

        public Builder setNeutralBtn(OnClickListener listener) {

            this.neutralBtnListener = listener;
            return this;
        }

        public DotExitVerifyDialog build() {

            return new DotExitVerifyDialog(context, this);
        }
    }
}
