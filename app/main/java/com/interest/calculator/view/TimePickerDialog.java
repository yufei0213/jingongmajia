package com.interest.calculator.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.interest.calculator.R;

import java.lang.reflect.Field;

/**
 * @author yufei0213
 * @date 2018/1/31
 * @description 时间选择器弹窗
 */
public class TimePickerDialog extends AppCompatDialog implements NumberPicker.OnValueChangeListener {

    private int currentHour;
    private int currentMinute;
    private int currentPeriod;

    private int oneDayHours;
    private int noonHours;

    private OnDoneClickListener onDoneClickListener;

    private Button cancelBtn;
    private Button doneBtn;

    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker periodPicker;

    private ClickProxy clickProxy = new ClickProxy(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.cancel_btn:

                    dismiss();
                    break;
                case R.id.done_btn:

                    dismiss();
                    if (onDoneClickListener != null) {

                        if (currentPeriod == 0) {

                            currentHour = currentHour == noonHours ? 0 : currentHour;
                        } else if (currentPeriod == 1) {

                            if (oneDayHours == 23) {

                                if (currentHour > noonHours) {

                                    currentHour = currentHour - 1;
                                } else {

                                    currentHour = currentHour + noonHours;
                                }
                            } else if (oneDayHours == 25) {

                                currentHour = currentHour < 12 ? currentHour + noonHours : currentHour + 1;
                            } else {

                                currentHour = currentHour < noonHours ? currentHour + noonHours : currentHour;
                            }
                        }

                        onDoneClickListener.onDone(currentHour * 3600 + currentMinute * 60);
                    }
                    break;
                default:
                    break;
            }
        }
    });

    public TimePickerDialog(Context context, Builder builder) {

        super(context);

        this.oneDayHours = builder.oneDayTotalHours == 0 ? 24 : builder.oneDayTotalHours;
        this.noonHours = 12 + (this.oneDayHours - 24);

        int initSecond = builder.initSecond;

        int hours = initSecond / 3600;

        currentPeriod = hours >= this.noonHours ? 1 : 0;
        currentHour = hours >= this.noonHours ? hours - this.noonHours : hours;
        currentMinute = (initSecond - hours * 3600) / 60;

        this.onDoneClickListener = builder.onDoneClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_timepicker);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置dialog的宽度为屏幕的宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(layoutParams);

        initView();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

        if (i == i1) {

            return;
        }

        if (numberPicker == hourPicker) {

            currentHour = i1;
            hourPicker.setValue(i1);
        } else if (numberPicker == minutePicker) {

            currentMinute = i1;
            minutePicker.setValue(i1);
        } else if (numberPicker == periodPicker) {

            currentPeriod = i1;

            if (oneDayHours == 25 && currentHour >= 2) {

                if (currentPeriod == 1)
                    currentHour = currentHour - 1;
                else
                    currentHour = currentHour + 1;
            }

            if (oneDayHours == 23 && currentHour >= 2) {

                if (currentPeriod == 1)
                    currentHour = currentHour + 1;
                else
                    currentHour = currentHour - 1;
            }
            initHourPicker();

            periodPicker.setValue(i1);
        }
    }

    private void initView() {

        cancelBtn = this.findViewById(R.id.cancel_btn);
        doneBtn = this.findViewById(R.id.done_btn);
        cancelBtn.setOnClickListener(clickProxy);
        doneBtn.setOnClickListener(clickProxy);

        hourPicker = this.findViewById(R.id.hour_picker);
        minutePicker = this.findViewById(R.id.minute_picker);
        periodPicker = this.findViewById(R.id.period_picker);

        //取消焦点
        hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        periodPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //设置分割线颜色
        setNumberPickerDividerColor(hourPicker);
        setNumberPickerDividerColor(minutePicker);
        setNumberPickerDividerColor(periodPicker);

        //设置Margin为零
//        setNumberPickerMargin(hourPicker);
//        setNumberPickerMargin(minutePicker);
//        setNumberPickerMargin(periodPicker);

        initPeriodPicker();
        initHourPicker();
        initMinutePicker();
    }

    /**
     * 初始化HourPicker
     */
    private void initHourPicker() {

        int maxValue = noonHours;
        int minValue = 1;

        if (currentPeriod == 1) {

            if (oneDayHours == 25) {

                maxValue = maxValue - 1;
            }
            if (oneDayHours == 23) {

                maxValue = maxValue + 1;
            }
        }

        hourPicker.setMaxValue(maxValue);
        hourPicker.setMinValue(minValue);

        hourPicker.setValue(currentHour);

        hourPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {

                if (oneDayHours == 25) {

                    if (value >= 2 && currentPeriod == 0) {

                        value = value - 1;
                    }
                    String str = Integer.toString(value);
                    return value < 10 ? "0" + str : str;
                } else if (oneDayHours == 23) {

                    if (value >= 2 && currentPeriod == 0) {

                        value = value + 1;
                    }
                    String str = Integer.toString(value);
                    return value < 10 ? "0" + str : str;
                } else {

                    String str = Integer.toString(value);
                    return value < 10 ? "0" + str : str;
                }
            }
        });
        hourPicker.setOnValueChangedListener(this);
    }

    /**
     * 初始化MinutePicker
     */
    private void initMinutePicker() {

        int maxValue = 59;
        int minValue = 0;
        minutePicker.setMaxValue(maxValue);
        minutePicker.setMinValue(minValue);

        minutePicker.setValue(currentMinute);

        minutePicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {

                String str = Integer.toString(value);
                return value < 10 ? "0" + str : str;
            }
        });
        minutePicker.setOnValueChangedListener(this);
    }

    /**
     * 初始化PeriodPicker
     */
    private void initPeriodPicker() {

        periodPicker.setMaxValue(1);
        periodPicker.setMinValue(0);

        String[] displayValue = getContext().getResources().getStringArray(R.array.am_pm);
        periodPicker.setDisplayedValues(displayValue);

        periodPicker.setValue(currentPeriod);

        periodPicker.setOnValueChangedListener(this);
    }

    /**
     * 设置分割线透明
     *
     * @param numberPicker NumberPicker
     */
    private void setNumberPickerDividerColor(NumberPicker numberPicker) {

        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {

            if (pf.getName().equals("mSelectionDivider")) {

                pf.setAccessible(true);
                try {

//                    pf.set(picker, new ColorDrawable(Color.TRANSPARENT));
                    pf.set(picker, new ColorDrawable(Color.parseColor("#C8C7CC")));
                } catch (Exception e) {

                    e.printStackTrace();
                }

                break;
            }
        }
    }

    /**
     * 设置picker之间的间距
     *
     * @param numberPicker NumberPicker
     */
    private void setNumberPickerMargin(NumberPicker numberPicker) {

        LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) numberPicker.getLayoutParams();
        p.setMargins(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {

            p.setMarginStart(0);
            p.setMarginEnd(0);
        }
    }

    public interface OnDoneClickListener {

        void onDone(int seconds);
    }

    public static class Builder {

        private Context context;

        private int initSecond;
        private int oneDayTotalHours;
        private OnDoneClickListener onDoneClickListener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder initSecond(int second) {

            this.initSecond = second;
            return this;
        }

        public Builder oneDayHours(int hours) {

            this.oneDayTotalHours = hours;
            return this;
        }

        public Builder listener(OnDoneClickListener onDoneClickListener) {

            this.onDoneClickListener = onDoneClickListener;
            return this;
        }

        public TimePickerDialog build() {

            return new TimePickerDialog(context, this);
        }
    }
}
