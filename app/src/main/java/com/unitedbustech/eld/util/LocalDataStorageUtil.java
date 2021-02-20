package com.unitedbustech.eld.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.unitedbustech.eld.App;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description LocalDataStorage
 */
public class LocalDataStorageUtil {

    private static final String Tag = "eld_data_v1";

    public static void putString(String key, String value) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void putBoolean(String key, boolean value) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void putInt(String key, int value) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putFloat(String key, float value) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    public static void putDouble(String key, double value) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, Double.doubleToRawLongBits(value));
        editor.commit();
    }

    public static void putLong(String key, long value) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static String getString(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        return settings.getString(key, "");
    }

    public static boolean getBoolean(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        return settings.getBoolean(key, false);
    }

    public static int getInt(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        return settings.getInt(key, 0);
    }

    public static float getFloat(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        return settings.getFloat(key, 0f);
    }

    public static float getDouble(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);

        Long value = settings.getLong(key, 0l);
        return Double.doubleToLongBits(value);
    }

    public static long getLong(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        return settings.getLong(key, 0l);
    }

    public static void remove(String key) {

        SharedPreferences settings = App.getContext().getSharedPreferences(Tag, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(key);
        editor.commit();
    }
}
