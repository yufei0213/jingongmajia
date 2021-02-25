package com.interest.calculator.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.interest.calculator.R;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 自定义DriverId框
 */
public class DriverInputView extends LinearLayout implements TextWatcher {

    private DriverIdInputListener listener;

    private EditText driverIdInput;
    private ImageButton clearBtn;

    private ClickProxy clickProxy = new ClickProxy(new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.clear_btn:

                    driverIdInput.setText("");
                    clearBtn.setVisibility(INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    });

    public DriverInputView(Context context) {

        this(context, null);
    }

    public DriverInputView(@NonNull Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.input_driver_view, this, true);

        driverIdInput = this.findViewById(R.id.driver_input);
        clearBtn = this.findViewById(R.id.clear_btn);

        driverIdInput.addTextChangedListener(this);
        clearBtn.setOnClickListener(clickProxy);
    }

    public void setText(String text) {

        this.driverIdInput.setText(text);
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
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if (listener != null) {

            listener.afterDriverIdTextChanged(editable.toString().trim());
        }
    }

    public void requestInputFocus() {

        driverIdInput.requestFocus();
    }

    public void setListener(DriverIdInputListener listener) {

        this.listener = listener;
    }

    public interface DriverIdInputListener {

        void afterDriverIdTextChanged(String text);
    }
}
