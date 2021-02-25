package com.interest.calculator.view;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.interest.calculator.App;
import com.interest.calculator.R;
import com.interest.calculator.common.Constants;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liyan
 * @date 2019/4/2
 * @description Shipping Id
 */

public class TrailerDialog extends AppCompatDialog {

    private static final String TAG = "TrailerDialog";

    private static final int BUTTON_NEGATIVE = -1;
    private static final int BUTTON_NEUTRAL = -2;

    private static final int MAX_TRAILER_COUNT = 5;

    private TextView titleView;

    private EditText shippingEdit;
    private View shippingLine;
    private TextView shippingWarning;

    private LinearLayout trailerLayout;
    private LinearLayout layout;
    private ScrollView scrollView;

    private Button negativeBtn;
    private Button neutralBtn;

    private String title;
    private String negativeBtnStr;
    private String neutralBtnStr;
    private String shippings;
    private String trailers;
    private int scrollMaxHeight;

    private Handler handler;

    /**
     * 存放trailer的集合
     */
    private ArrayList<String> trailerList = new ArrayList<>();
    /**
     * 添加的trailer的个数
     */
    private int trailerCount = 0;

    private OnClickListener negativeListener;
    private OnClickListener neutralListener;

    private LayoutInflater inflater;

    public TrailerDialog(Context context, Builder builder) {

        super(context);

        this.title = builder.title;
        this.negativeBtnStr = builder.negativeBtnStr;
        this.neutralBtnStr = builder.neutralBtnStr;
        this.shippings = builder.shippings;
        this.trailers = builder.trailers;

        this.scrollMaxHeight = builder.scrollMaxHeight;

        this.negativeListener = builder.negativeListener;
        this.neutralListener = builder.neutralListener;

        inflater = LayoutInflater.from(context);
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_trailer);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = display.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(layoutParams);
        initView();
    }

    public void initView() {

        titleView = this.findViewById(R.id.title_text);
        shippingEdit = this.findViewById(R.id.shipping_edit_text);
        shippingLine = this.findViewById(R.id.shipping_line);
        shippingWarning = this.findViewById(R.id.shipping_warning);
        trailerLayout = this.findViewById(R.id.trailer_layout);
        scrollView = this.findViewById(R.id.scrollView);
        layout = this.findViewById(R.id.layout);
        negativeBtn = this.findViewById(R.id.negative_btn);
        neutralBtn = this.findViewById(R.id.neutral_btn);

        if(!TextUtils.isEmpty(title)) {

            titleView.setText(title);
        }

        if(!TextUtils.isEmpty(negativeBtnStr)) {

            negativeBtn.setText(negativeBtnStr);
        }

        if(!TextUtils.isEmpty(neutralBtnStr)) {

            neutralBtn.setText(neutralBtnStr);
        }

        if(negativeListener != null) {

            negativeBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    negativeListener.onClick(TrailerDialog.this, BUTTON_NEGATIVE);
                }
            }));
        }

        if(neutralListener != null) {

            neutralBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    neutralListener.onClick(TrailerDialog.this, BUTTON_NEUTRAL);
                }
            }));
        }

        setData();
    }

    public void resetShippingWarning(boolean warning) {

        if(warning) {

            shippingWarning.setVisibility(View.VISIBLE);
            shippingWarning.setText(App.getContext().getResources().getString(R.string.trailer_dialog_shipping_max));
            shippingLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_waring));
        } else {

            shippingWarning.setVisibility(View.GONE);
            shippingLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dailog_edit_text_line));
            shippingEdit.setTextColor(App.getContext().getResources().getColor(R.color.dialog_input_font));
        }
    }

    /**
     * 检查弹窗是否有输入
     * @return
     */
    public boolean checkShippingAndTrailer() {

        String shippingStr = shippingEdit.getText().toString();
        if(TextUtils.isEmpty(shippingStr)) {

            if(trailerList.size() == 0) {

                return false;
            }
        }
        return true;
    }

    public boolean checkShippingValid() {

        String shippingStr = shippingEdit.getText().toString();
        Matcher matcher = Pattern.compile(Constants.INPUT_REGEXP).matcher(shippingStr);
        if(TextUtils.isEmpty(shippingStr)) {

            return true;
        }
        if(matcher.find()) {

            resetShippingRegWaring(true);
            return false;
        }
        return true;
    }

    /**
     * 正则检查输入的shipping，只允许英文，数字和部分符号
     * 为避免修改上一方法带来耦合，新定义一个方法只做正则检查
     * @param warning true为提醒状态
     */
    private void resetShippingRegWaring(boolean warning) {

        if (warning) {

            this.shippingLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_waring));
            this.shippingWarning.setText(App.getContext().getResources().getString(R.string.trailer_dialog_shipping_regexp_wrong));
            this.shippingWarning.setVisibility(View.VISIBLE);
        } else {

            this.shippingEdit.setTextColor(App.getContext().getResources().getColor(R.color.dialog_input_font));
            this.shippingLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dailog_edit_text_line));
            this.shippingWarning.setVisibility(View.GONE);
        }
    }


    /**
     * 获取设置的shipping
     * @return
     */
    public String getShippingStr() {

        return shippingEdit.getText().toString();
    }

    /**
     * 设置scrollview高度
     */
    private void setScrollHeight() {

        layout.measure(0, 0);
        if(layout.getMeasuredHeight() >= scrollMaxHeight) {

            layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, scrollMaxHeight));
        }
    }


    public void setData() {

        if(!TextUtils.isEmpty(shippings)) {

            shippingEdit.setText(shippings);
            shippingEdit.setSelection(shippings.length());
        }

    }


    @Override
    public void show() {

        super.show();

        setCancelable(false);
    }

    @Override
    public void dismiss() {

        super.dismiss();
    }

    public interface OnClickListener {

        void onClick(TrailerDialog dialog, int which);
    }

    public static class Builder {

        private Context context;

        private String title;

        private String negativeBtnStr;
        private String neutralBtnStr;
        private String shippings;
        private String trailers;

        private int scrollMaxHeight;

        private OnClickListener negativeListener;
        private OnClickListener neutralListener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder setTitle(String title) {

            this.title = title;
            return this;
        }

        public Builder setShippings(String shippings) {

            this.shippings = shippings;
            return this;
        }

        public Builder setTrailers(String trailers) {

            this.trailers = trailers;
            return this;
        }

        public Builder setNegativeBtnStr(String negativeBtnStr) {

            this.negativeBtnStr = negativeBtnStr;
            return this;
        }

        public Builder setNeutralBtnStr(String neutralBtnStr) {

            this.neutralBtnStr = neutralBtnStr;
            return this;
        }

        public Builder setScrollMaxHeight(int scrollMaxHeight) {

            this.scrollMaxHeight = scrollMaxHeight;
            return this;
        }

        public Builder setNegativeListener(OnClickListener negativeListener) {

            this.negativeListener = negativeListener;
            return this;
        }

        public Builder setNeutralListener(OnClickListener neutralListener) {

            this.neutralListener = neutralListener;
            return this;
        }

        public TrailerDialog build() {

            return new TrailerDialog(context, this);
        }
    }
}
