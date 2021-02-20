package com.unitedbustech.eld.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.unitedbustech.eld.App;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuzhe on 2018/5/11.
 * @date 2018/5/11
 * @description String特殊处理工具类
 */

public class SpannableStringUtil {

    private static final String TAG = "SpannableStringUtil";

    /**
     * 设置字符串部分字体颜色
     * @param str 需要设置的字符串
     * @param matcherStr 需要匹配的正则字符串
     * @param colorInt 需要改变的颜色
     * @param endIndex 需要改变的结束位置
     * 替换类型为从起始下标到终了下标，同时包括起始下标和终了下标
     * @return 结果
     */
    public static SpannableString changeStringForegroundColor(String str, String matcherStr, int colorInt, int endIndex) {

        SpannableString spannableString = null;
        if(!TextUtils.isEmpty(str)) {

            spannableString = new SpannableString(str);
            Matcher matcher = Pattern.compile(matcherStr).matcher(str);
            if(matcher.find()) {

                spannableString.setSpan(new ForegroundColorSpan(App.getContext().getResources().getColor(colorInt)), matcher.start(), endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }
        return spannableString;
    }
}
