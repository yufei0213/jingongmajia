package com.unitedbustech.eld.view;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.common.RemarkType;
import com.unitedbustech.eld.eventcenter.enums.LatLngSpecialEnum;
import com.unitedbustech.eld.location.LocationHandler;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.util.LocationUtil;
import com.unitedbustech.eld.util.ThreadUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangyu
 * @date 2018/1/25
 * @description 开始休息页面弹窗
 */
public class AdverseDialog extends AppCompatDialog {

    private static final int BUTTON_POSITIVE = -1;
    private static final int BUTTON_NEGATIVE = -2;
    private static final int BUTTON_NEUTRAL = -3;

    private TextView tipView;
    private EditText locationEditText;
    private EditText remarkEditText;
    private ImageButton locationBtn;
    private View locationLine;
    private View remarkLine;
    private TextView locationWaring;
    private TextView remarkWaring;

    private Button positiveBtn;
    private Button negativeBtn;
    private Button neutralBtn;

    private String title;
    private String remark;

    private String latitude;
    private String longitude;
    private String autoAddress;

    private Handler handler;

    private Animation loadingAnim;

    private OnClickListener positiveBtnListener;
    private OnClickListener negativeBtnListener;
    private OnClickListener neutralBtnListener;

    private AdverseDialog(@NonNull Context context, Builder builder) {

        super(context);

        this.positiveBtnListener = builder.positiveBtnListener;
        this.negativeBtnListener = builder.negativeBtnListener;
        this.neutralBtnListener = builder.neutralBtnListener;
        this.handler = new Handler();
        this.title = builder.title;
        this.remark = builder.remark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_adverse);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = display.getWidth(); //设置dialog的宽度为当前手机屏幕的宽度
        getWindow().setAttributes(layoutParams);

        initView();
    }

    private void initView() {

        tipView = this.findViewById(R.id.tip_view);

        positiveBtn = this.findViewById(R.id.positive_btn);
        negativeBtn = this.findViewById(R.id.negative_btn);
        neutralBtn = this.findViewById(R.id.neutral_btn);

        locationEditText = this.findViewById(R.id.location_edit_text);
        remarkEditText = this.findViewById(R.id.remark_edit_text);
        locationBtn = this.findViewById(R.id.location_button);
        locationLine = this.findViewById(R.id.location_line);
        remarkLine = this.findViewById(R.id.remark_line);
        locationWaring = this.findViewById(R.id.location_warning);
        remarkWaring = this.findViewById(R.id.remark_warning);
        loadingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.loading_location_anim);
        LinearInterpolator lin = new LinearInterpolator();
        loadingAnim.setInterpolator(lin);
        //模拟点击
        locationBtn.performClick();
        locationBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        locationBtn.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.ic_location_loading));

                        locationBtn.startAnimation(loadingAnim);

                    }
                });
                ThreadUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {

                        Location location = LocationHandler.getInstance().getCurrentLocation();
                        if (location != null) {

                            latitude = Double.toString(location.getLatitude());
                            longitude = Double.toString(location.getLongitude());
                            autoAddress = LocationUtil.getGeolocation(location);
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run() {

                                if (!TextUtils.isEmpty(autoAddress)) {

                                    locationEditText.setText(autoAddress);
                                }

                                locationBtn.setImageDrawable(App.getContext().getResources().getDrawable(R.drawable.ic_location_gps));
                                locationBtn.clearAnimation();
                            }
                        });
                    }
                });

            }
        }));

        locationBtn.callOnClick();

        locationEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                //获得焦点
                if ((TextUtils.isEmpty(locationEditText.getText().toString()) || checkLocationInput()) && hasFocus) {

                    locationLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_focus));
                }
                //失去焦点
                if (!hasFocus && !locationWaring.isShown()) {

                    locationLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dailog_edit_text_line));
                }
            }
        });
        locationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setLocationWaring(false, 0);
                locationLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_focus));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        remarkEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//
//                //获的焦点
//                if ((TextUtils.isEmpty(remarkEditText.getText().toString()) || checkRemarkInput()) && hasFocus) {
//
//                    remarkLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_focus));
//                }
//                //失去焦点
//                if (!hasFocus && !remarkWaring.isShown()) {
//
//                    remarkLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dailog_edit_text_line));
//                }
//            }
//        });
        remarkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                setRemarkWarning(false, 0);
                remarkLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_focus));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        remarkEditText.setOnClickListener(new ClickProxy(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String adverseRemarkListStr = AppUtil.readAssetsFile(getContext(), "data/remark.txt");
                JSONArray jsonArray = JsonUtil.parseArray(adverseRemarkListStr);

                RemarkListDialog.Builder builder = new RemarkListDialog.Builder(getContext())
                        .listener(new RemarkListDialog.OnRemarkItemClickListener() {
                            @Override
                            public void onRemarkItemClick(String remark) {

                                remarkEditText.setText(remark);
                                setRemarkWarning(false, 0);
                            }
                        });

                for (Object object : jsonArray) {

                    int type = JsonUtil.getInt((JSONObject) object, "type");
                    if (type == RemarkType.ADVERSE) {

                        builder.addRemark(JsonUtil.getString((JSONObject) object, "value"));
                    }
                }

                builder.build().show();
            }
        }));

        if (positiveBtnListener != null) {

            positiveBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    positiveBtnListener.onClick(AdverseDialog.this, BUTTON_POSITIVE);
                }
            }));
        }

        if (negativeBtnListener != null) {

            negativeBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    negativeBtnListener.onClick(AdverseDialog.this, BUTTON_NEGATIVE);
                }
            }));
        }

        if (neutralBtnListener != null) {

            neutralBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    neutralBtnListener.onClick(AdverseDialog.this, BUTTON_NEUTRAL);
                }
            }));
        }
        if (this.title != null && !this.title.isEmpty()) {

            this.tipView.setText(title);
        }
        if (this.remark != null && !this.remark.isEmpty()) {

            this.remarkEditText.setText(remark);
        }
    }

    public String getLocation() {

        return this.locationEditText.getText().toString();
    }

    public String getLatitude() {

        return this.getLocation().equals(autoAddress) ? this.latitude : LatLngSpecialEnum.M.getCode();
    }

    public String getLongitude() {

        return this.getLocation().equals(autoAddress) ? this.longitude : LatLngSpecialEnum.M.getCode();
    }

    public String getRemark() {

        return this.remarkEditText.getText().toString();
    }

    /**
     * 校验输入是否通过
     *
     * @return 校验结果
     */
    public boolean checkStatus() {

        boolean location = checkLocationInput();
        boolean remark = checkRemarkInput();
        return location && remark;
    }

    private boolean checkLocationInput() {

        boolean result = true;
        Pattern pattern = Pattern.compile(Constants.INPUT_REGEXP);

        String locationStr = this.locationEditText.getText().toString();
        if (locationStr.length() < Constants.LOCATION_MIN_LENGTH) {

            setLocationWaring(true, R.string.location_min_wrong);
            result = false;
        } else if (locationStr.length() > Constants.LOCATION_MAX_LENGTH) {

            setLocationWaring(true, R.string.location_max_wrong);
            result = false;
        } else {

            Matcher locationMatcher = pattern.matcher(locationStr);
            if (locationMatcher.find()) {

                setLocationWaring(true, R.string.location_regexp_wrong);
                result = false;
            } else {

                setLocationWaring(false, 0);
            }
        }

        return result;
    }

    private boolean checkRemarkInput() {

        boolean result = true;
        Pattern pattern = Pattern.compile(Constants.INPUT_REGEXP);

        String remarkStr = this.remarkEditText.getText().toString();
        if (remarkStr.length() < Constants.REMARK_MIN_LENGTH) {

            setRemarkWarning(true, R.string.remark_min_wrong);
            result = false;
        } else if (remarkStr.length() > Constants.REMARK_MAX_LENGTH) {

            setRemarkWarning(true, R.string.remark_max_wrong);
            result = false;
        } else {

            Matcher remarkMatcher = pattern.matcher(remarkStr);
            if (remarkMatcher.find()) {

                setRemarkWarning(true, R.string.remark_regexp_wrong);
                result = false;
            } else {

                setRemarkWarning(false, 0);
            }
        }

        return result;
    }

    /**
     * 设置可用状态
     *
     * @param warning true为提醒状态
     * @param strId   提示语资源id
     */
    private void setLocationWaring(boolean warning, int strId) {

        if (warning) {

            this.locationLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_waring));
            this.locationWaring.setText(App.getContext().getResources().getString(strId));
            this.locationWaring.setVisibility(View.VISIBLE);
        } else {

            this.locationLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dailog_edit_text_line));
            this.locationWaring.setVisibility(View.GONE);
        }
    }

    /**
     * 设置可用状态
     *
     * @param warning true为提醒状态
     * @param strId   提示语资源id
     */
    private void setRemarkWarning(boolean warning, int strId) {

        if (warning) {

            this.remarkLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dialog_edit_waring));
            this.remarkWaring.setText(App.getContext().getResources().getString(strId));
            this.remarkWaring.setVisibility(View.VISIBLE);
        } else {

            this.remarkLine.setBackgroundColor(App.getContext().getResources().getColor(R.color.dailog_edit_text_line));
            this.remarkWaring.setVisibility(View.GONE);
        }
    }

    public interface OnClickListener {

        void onClick(AdverseDialog dialog, int which);
    }

    public static class Builder {

        private Context context;

        private String title;

        private String remark;

        private OnClickListener positiveBtnListener;
        private OnClickListener negativeBtnListener;
        private OnClickListener neutralBtnListener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder setNegativeBtn(OnClickListener listener) {

            this.negativeBtnListener = listener;
            return this;
        }

        public Builder setTitle(String title) {

            this.title = title;
            return this;
        }

        public Builder setRemark(String remark) {

            this.remark = remark;
            return this;
        }

        public Builder setNeutralBtn(OnClickListener listener) {

            this.neutralBtnListener = listener;
            return this;
        }

        public Builder setPositiveBtn(OnClickListener listener) {

            this.positiveBtnListener = listener;
            return this;
        }

        public AdverseDialog build() {

            return new AdverseDialog(context, this);
        }
    }
}
