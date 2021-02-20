package com.unitedbustech.eld.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description json工具类
 */
public final class JsonUtil {

    private static final String TAG = "JsonUtil";

    /**
     * 获取字符串
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static String getString(JSONObject obj, String key) {

        String result = "";

        try {

            result = obj.getString(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取整数
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static int getInt(JSONObject obj, String key) {

        int result = 0;

        try {

            result = obj.getInteger(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取整数
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static long getLong(JSONObject obj, String key) {

        long result = 0;

        try {

            result = obj.getLong(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取浮点数
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static float getFloat(JSONObject obj, String key) {

        float result = 0f;

        try {

            result = obj.getFloat(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取浮点数
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static double getDouble(JSONObject obj, String key) {

        double result = 0d;

        try {

            result = obj.getDouble(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取布尔值
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static boolean getBoolean(JSONObject obj, String key) {

        boolean result = false;

        try {

            result = obj.getBoolean(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取JSONObject
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static JSONObject getJsonObject(JSONObject obj, String key) {

        JSONObject object = null;

        try {

            object = obj.getJSONObject(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return object;
    }

    /**
     * 获取JJSONArray
     *
     * @param obj JSONObject
     * @param key 主键
     * @return 值
     */
    public static JSONArray getJsonArray(JSONObject obj, String key) {

        JSONArray array = null;

        try {

            array = obj.getJSONArray(key);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return array;
    }

    /**
     * 字符串转JSONObject
     *
     * @param text json字符串
     * @return JSONObject
     */
    public static JSONObject parseObject(String text) {

        JSONObject object = null;

        try {

            object = JSON.parseObject(text);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return object;
    }

    /**
     * 字符串转Object
     *
     * @param text   json字符串
     * @param classz Object.class
     * @param <T>    泛型
     * @return Object
     */
    public static <T> T parseObject(String text, Class<T> classz) {

        try {

            return JSON.parseObject(text, classz);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转化为 JSONArray
     *
     * @param text JSONArray 字符串
     * @return JSONArray
     */
    public static JSONArray parseArray(String text) {

        try {

            return JSON.parseArray(text);
        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转ArrayList
     *
     * @param text  json字符串
     * @param clazz Object.class
     * @param <T>   泛型
     * @return ArrayList
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {

        List<T> list = new ArrayList<>();

        try {

            list = JSON.parseArray(text, clazz);
        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }

    /**
     * 将Object转换为JSONString
     *
     * @param object java object
     * @return JSONString
     */
    public static String toJSONString(Object object) {

        try {

            return JSON.toJSONString(object);
        } catch (Exception e) {

            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将Object转换为JSONString
     * 注意：此方法给JsInterface专用
     *
     * @param object java object
     * @return JSONString
     */
    public static String toJsJSONString(Object object) {

        String result = toJSONString(object);
        result = result.replaceAll("'", "\\\\'").replaceAll("\"", "\\\\\"");

        return result;
    }

    /**
     * 将JSONObject中某个key对应的值修改成指定的value值
     * @param obj JSONObject
     * @param key mapkey
     * @param value mapvalue
     * @return JSONString
     */
    public static String updateStringValue(JSONObject obj, String key, String value) {

        String jsonString = null;
        try {

            Map<String, Object> map = JSON.parseObject(JSON.toJSONString(obj), Map.class);
            map.put(key,value);
            jsonString = JSON.toJSONString(map);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return jsonString;
    }
}
