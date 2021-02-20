package com.unitedbustech.eld.util;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;

import com.unitedbustech.eld.activity.MainActivity;
import com.unitedbustech.eld.hos.core.ModelCenter;
import com.unitedbustech.eld.logs.Logger;

import java.util.Locale;

/**
 * @author zhangyu
 * @date 2018/2/26
 * @description TODO
 */
public class LanguageUtil {


    private static final String TAG = "LanguageUtil";
    private static LanguageUtil instance;
    //是否正在切换语言。
    private boolean isChangingLanguage;
    //保存的语言类型的KEY
    public static final String SAVE_LANGUAGE = "save_language";
    //英文
    public static final int LANGUAGE_EN = 1;
    //中文
    public static final int LANGUAGE_ZH = 2;


    public static LanguageUtil getInstance() {
        if (instance == null) {

            instance = new LanguageUtil();
        }
        return instance;
    }


    private LanguageUtil() {

    }

    /**
     * 设置语言
     */
    public Context setConfiguration(Context context) {

        Locale targetLocale = getLanguageLocale();
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(targetLocale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //解决android8.0的语言问题。需要调用一遍update，一遍create之后再调用update,才能使语言设置全部生效。
            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
            context = context.createConfigurationContext(configuration);

            resources = context.getResources();
            resources.updateConfiguration(configuration, dm);
        } else {

            Resources resources = context.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            resources.updateConfiguration(configuration, dm);
        }
        return context;

    }

    /**
     * 如果不是中文,默认返回简体英文
     *
     * @return
     */
    private Locale getLanguageLocale() {

        int languageType = LocalDataStorageUtil.getInt(LanguageUtil.SAVE_LANGUAGE);
        //只有设置过中文，返回中文，其他情况默认返回英文。
        if (languageType == LANGUAGE_ZH) {

            return Locale.SIMPLIFIED_CHINESE;
        }
        return Locale.ENGLISH;
    }

    /**
     * 更新语言
     *
     * @param languageType
     */
    public Context updateLanguage(Context context, int languageType) {
        if (isChangingLanguage) {
            return context;
        }
        isChangingLanguage = true;
        Logger.i(TAG, "change language to :" + languageType);
        LocalDataStorageUtil.putInt(LanguageUtil.SAVE_LANGUAGE, languageType);
        Context newContext = setConfiguration(context);

        ModelCenter.getInstance().reloadData();

        Intent intent = MainActivity.newIntent(context);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Logger.i(TAG, "clear activity and start main:" + languageType);
        context.startActivity(intent);
        return newContext;
    }

    /**
     * 获取到用户保存的语言类型
     *
     * @return
     */
    public int getLanguageType() {

        int languageType = LocalDataStorageUtil.getInt(LanguageUtil.SAVE_LANGUAGE);
        return languageType == 0 ? LANGUAGE_EN : languageType;
    }

    public boolean isChangingLanguage() {
        return isChangingLanguage;
    }

    public void setChangingLanguage(boolean changingLanguage) {
        isChangingLanguage = changingLanguage;
    }
}
