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
import java.util.Calendar;

/**
 * @author yufei0213
 * @date 2018/1/31
 * @description 时间选择器弹窗
 */
public class DatePickerDialog extends AppCompatDialog implements NumberPicker.OnValueChangeListener, NumberPicker.Formatter {

    private int currentMonth;
    private int currentDay;
    private int currentYear;

    private OnDoneClickListener onDoneClickListener;

    private Button cancelBtn;
    private Button doneBtn;

    private NumberPicker monthPicker;
    private NumberPicker dayPicker;
    private NumberPicker yearPicker;

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

                        String monthStr = Integer.toString(currentMonth);
                        String dayStr = Integer.toString(currentDay);
                        if (currentMonth < 10) {

                            monthStr = "0" + monthStr;
                        }
                        if (currentDay < 10) {

                            dayStr = "0" + dayStr;
                        }
                        onDoneClickListener.onDone(monthStr + "/" + dayStr + "/" + currentYear);
                    }
                    break;
                default:
                    break;
            }
        }
    });

    public DatePickerDialog(Context context, Builder builder) {

        super(context);

        String[] dateArray = builder.initDate.split("/");
        currentMonth = Integer.parseInt(dateArray[0]);
        currentDay = Integer.parseInt(dateArray[1]);
        currentYear = Integer.parseInt(dateArray[2]);

        this.onDoneClickListener = builder.onDoneClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_datepicker);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置dialog的宽度为屏幕的宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(layoutParams);

        initView();
    }

    @Override
    public String format(int i) {

        String str = Integer.toString(i);
        return i < 10 ? "0" + str : str;
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {

        if (i == i1) {

            return;
        }

        if (numberPicker == monthPicker) {

            currentMonth = i1;
            setMonthValue(currentMonth);
        } else if (numberPicker == dayPicker) {

            currentDay = i1;
            setDayValue(currentDay);
        } else if (numberPicker == yearPicker) {

            currentYear = i1;
            yearPicker.setValue(i1);
        }
    }

    private void initView() {

        cancelBtn = this.findViewById(R.id.cancel_btn);
        doneBtn = this.findViewById(R.id.done_btn);
        cancelBtn.setOnClickListener(clickProxy);
        doneBtn.setOnClickListener(clickProxy);

        monthPicker = this.findViewById(R.id.month_picker);
        dayPicker = this.findViewById(R.id.day_picker);
        yearPicker = this.findViewById(R.id.year_picker);

        //取消焦点
        monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        dayPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        //设置分割线颜色
        setNumberPickerDividerColor(monthPicker);
        setNumberPickerDividerColor(dayPicker);
        setNumberPickerDividerColor(yearPicker);

        //设置Margin为零
//        setNumberPickerMargin(monthPicker);
//        setNumberPickerMargin(dayPicker);
//        setNumberPickerMargin(yearPicker);

        initMonthPicker();
        initYearPicker();
    }

    /**
     * 初始化HourPicker
     */
    private void initMonthPicker() {

        int maxValue = 12;
        int minValue = 1;
        monthPicker.setMaxValue(maxValue);
        monthPicker.setMinValue(minValue);

        String[] displayValue = getContext().getResources().getStringArray(R.array.month_array);
        monthPicker.setDisplayedValues(displayValue);

        setMonthValue(currentMonth);

        monthPicker.setFormatter(this);
        monthPicker.setOnValueChangedListener(this);
    }

    /**
     * 设置月份
     *
     * @param value 月份值
     */
    private void setMonthValue(int value) {

        monthPicker.setValue(value);

        initDayPicker();
    }

    /**
     * 初始化MinutePicker
     */
    private void initDayPicker() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, currentMonth - 1);

        int maxValue = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int minValue = 1;

        dayPicker.setMaxValue(maxValue);
        dayPicker.setMinValue(minValue);

        if (currentDay > maxValue) {

            currentDay = maxValue;
        }

        setDayValue(currentDay);

        dayPicker.setFormatter(this);
        dayPicker.setOnValueChangedListener(this);
    }

    /**
     * 设置天
     *
     * @param value 天
     */
    private void setDayValue(int value) {

        dayPicker.setValue(value);
    }

    /**
     * 初始化PeriodPicker
     */
    private void initYearPicker() {

        yearPicker.setMaxValue(currentYear);
        yearPicker.setMinValue(currentYear - 1);

        yearPicker.setValue(currentYear);

        yearPicker.setOnValueChangedListener(this);
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

        void onDone(String date);
    }

    public static class Builder {

        private Context context;

        private String initDate;
        private OnDoneClickListener onDoneClickListener;

        public Builder(Context context) {

            this.context = context;
        }

        public Builder initDate(String date) {

            this.initDate = date;
            return this;
        }

        public Builder listener(OnDoneClickListener onDoneClickListener) {

            this.onDoneClickListener = onDoneClickListener;
            return this;
        }

        public DatePickerDialog build() {

            return new DatePickerDialog(context, this);
        }
    }
}
