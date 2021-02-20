package com.unitedbustech.eld.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description 自定义CarrierId框
 */
public class CarrierInputView extends LinearLayout implements TextWatcher {

    private CarrierIdInputListener listener;

    private EditText carrierIdInput;
    private ImageButton clearBtn;

    private ClickProxy clickProxy = new ClickProxy(new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.clear_btn:

                    carrierIdInput.setText("");
                    clearBtn.setVisibility(INVISIBLE);
                    break;
                default:
                    break;
            }
        }
    });

    public CarrierInputView(Context context) {

        this(context, null);
    }

    public CarrierInputView(@NonNull Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.input_carrier_view, this, true);

        carrierIdInput = this.findViewById(R.id.carrier_input);
        clearBtn = this.findViewById(R.id.clear_btn);

        carrierIdInput.addTextChangedListener(this);
        clearBtn.setOnClickListener(clickProxy);
    }

    public void setText(String text) {

        this.carrierIdInput.setText(text);
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

            listener.afterCarrierIdTextChanged(editable.toString().trim());
        }
    }

    public void requestInputFocus() {

        carrierIdInput.requestFocus();
    }

    public void setListener(CarrierIdInputListener listener) {

        this.listener = listener;
    }

    public interface CarrierIdInputListener {

        void afterCarrierIdTextChanged(String text);
    }
}
