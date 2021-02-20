package com.unitedbustech.eld.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 自定义密码框
 */
public class PasswordInputView extends LinearLayout implements TextWatcher {

    private boolean isShowPassword;

    private PasswordInputListener listener;

    private EditText passwordInput;
    private ImageButton clearBtn;
    private ImageButton stateBtn;
    private View lineView;
    private TextView warningText;

    private ClickProxy clickProxy = new ClickProxy(new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.clear_btn:

                    passwordInput.setText("");
                    clearBtn.setVisibility(INVISIBLE);
                    setPasswordWaring(false);
                    break;
                case R.id.state_btn:

                    if (isShowPassword) {

                        stateBtn.setImageResource(R.drawable.ic_password_input_hide);
                        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordInput.setSelection(passwordInput.getText().toString().length());
                    } else {

                        stateBtn.setImageResource(R.drawable.ic_password_input_show);
                        passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordInput.setSelection(passwordInput.getText().toString().length());
                    }

                    isShowPassword = !isShowPassword;
                    break;
                default:
                    break;
            }
        }
    });

    public PasswordInputView(Context context) {

        this(context, null);
    }

    public PasswordInputView(@NonNull Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.input_password_view, this, true);

        passwordInput = this.findViewById(R.id.password_input);
        clearBtn = this.findViewById(R.id.clear_btn);
        stateBtn = this.findViewById(R.id.state_btn);
        lineView = this.findViewById(R.id.line_view);
        warningText = this.findViewById(R.id.warning_text);

        passwordInput.addTextChangedListener(this);
        clearBtn.setOnClickListener(clickProxy);
        stateBtn.setOnClickListener(clickProxy);

        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInput.setSelection(passwordInput.getText().toString().length());
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        if (charSequence.length() > 0) {

            clearBtn.setVisibility(VISIBLE);
        } else {

            clearBtn.setVisibility(INVISIBLE);
        }

        setPasswordWaring(false);
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (listener != null) {

            listener.afterPasswordTextChanged(editable.toString().trim());
        }
    }

    public void requestInputFocus() {

        passwordInput.requestFocus();
    }

    public void setListener(PasswordInputListener listener) {

        this.listener = listener;
    }

    public interface PasswordInputListener {

        void afterPasswordTextChanged(String text);
    }

    public void clearPasswordInput() {

        passwordInput.setText("");
    }

    public void setPasswordWaring(boolean warning) {

        if (warning) {
            this.passwordInput.setTextColor(App.getContext().getResources().getColor(R.color.password_waring));
            this.lineView.setBackgroundColor(App.getContext().getResources().getColor(R.color.password_waring));
            this.warningText.setVisibility(View.VISIBLE);
        } else {
            this.passwordInput.setTextColor(App.getContext().getResources().getColor(R.color.password_input_font));
            this.lineView.setBackgroundColor(App.getContext().getResources().getColor(R.color.login_input_stroke));
            this.warningText.setVisibility(View.GONE);
        }
    }

}
