package com.unitedbustech.eld.util;

import android.text.TextUtils;

import com.unitedbustech.eld.App;
import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.system.SystemHelper;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 日期工具类
 */
public class TimeUtil {

    private static final SimpleDateFormat usFormatter = new SimpleDateFormat("MM/dd/yyyy",
            Locale.US);
    private static final SimpleDateFormat simpleDayFormatter = new SimpleDateFormat("MMddyy",
            Locale.US);
    private static final SimpleDateFormat usLongFormatter = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final SimpleDateFormat longTimeFormatter = new SimpleDateFormat(
            "yyyyMMddHHmmss", Locale.US);
    private static final SimpleDateFormat usShortFormatter = new SimpleDateFormat(
            "yyyy-MM-dd hh:mm aa", Locale.US);
    private static final SimpleDateFormat usWeekFormatter = new SimpleDateFormat("EEE", Locale.US);
    private static final SimpleDateFormat dailylogFormatter = new SimpleDateFormat("EEE, MMM d", Locale.US);
    private static final SimpleDateFormat dailylogFormatterZH = new SimpleDateFormat("M月d日，EEE", Locale.CHINA);

    private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss", Locale.US);
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat usTimeFormatter = new SimpleDateFormat("hh:mm aa", Locale.US);
    private static final SimpleDateFormat dotReviewHeadRequestFormatter = new SimpleDateFormat("MMddyy", Locale.US);
    private static final SimpleDateFormat ddlEventDetailTime = new SimpleDateFormat("MM/dd hh:mm aa", Locale.US);

    public static final String US_FORMAT = "MM/dd/yyyy";
    public static final String STANDARD_FORMAT = "MM/dd hh:mm aa";
    public static final String DAY_MMM_D = "EEE, MMM d";
    public static final String DAY_MMM_D_ZH = "M月d日，EEE";
    public static final String MM_DD_YY = "MMddyy";
    public static final String MM_DD = "MM/dd";
    public static final String HH_MM_AA = "hh:mm aa";
    public static final String DDL_DETAIL_FORMAT = "MM/dd hh:mm aa";

    public TimeUtil() {

    }

    public static String dateToStr(Date date) {

        return usShortFormatter.format(date);
    }

    public static String dateToStr(Date date, String format) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    public static String getDDLDetailTime(Date date) {

        return ddlEventDetailTime.format(date);
    }

    public static String dateToStrlong(Date date) {

        return usLongFormatter.format(date);
    }

    public static String dateToStrUs(Date dateDate) {

        return usFormatter.format(dateDate);
    }

    public static int getTimezoneOffset() {

        User user = SystemHelper.getUser();
        if (user == null) {
            return 0;
        }
        String tz = user.getTimeZone();
        TimeZone curTimeZone = TimeZone.getTimeZone(tz);
        if (curTimeZone == null) {
            return 0;
        }
        int result = curTimeZone.getRawOffset() / 1000 / 60 / 60;
        return result;
    }

    public static String getSimpleDay(Date dateDate) {

        return simpleDayFormatter.format(dateDate);
    }

    public static Date simpleDayToDate(String dateString) {

        return simpleDayFormatter.parse(dateString, new ParsePosition(0));
    }

    public static String dateToStrNoTime(Date dateDate) {

        return dateFormatter.format(dateDate);
    }

    public static String dateToUsTime(Date dateDate) {

        return usTimeFormatter.format(dateDate);
    }

    public static String dateToLongTime(Date dateDate) {

        return longTimeFormatter.format(dateDate);
    }

    public static String secondToHourString(int second) {

        return second / (60 * 60) + ":" + second % (60 * 60) / 60 + ":" + second % 60;
    }

    public static Date getDateNoTime(Date date) {

        return strUsToDate(dateToStrUs(date));
    }

    public static Date getDate(long time) {

        return new Date(time);
    }

    public static String getWeekEn(Date date) {

        return usWeekFormatter.format(date);
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past     过去几天
     * @param pattern  日期格式
     * @param timeZone 时区
     * @return 日期字符串
     */
    public static String getPastDate(int past, String pattern, String timeZone) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date date = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));

        String result = format.format(date);
        return result;
    }

    /**
     * 获取未来 第 past 天的日期
     *
     * @param past     未来几天
     * @param pattern  日期格式
     * @param timeZone 时区
     * @return 日期字符串
     */
    public static String getFutureDate(int past, String pattern, String timeZone) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + past);
        Date date = calendar.getTime();

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getTimeZone(timeZone));

        String result = format.format(date);
        return result;
    }

    public static Date strUsToDate(String strDate) {

        ParsePosition pos = new ParsePosition(0);
        Date strtodate = usFormatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date getDayBegin(Date date) {
        User user = SystemHelper.getUser();
        String tz;
        if (user == null) {
            //解决目前存在的登出后js仍在调用原生方法的问题。
            //因为正常情况下，user不会为null。只有非常极端的情况下才为null。
            //例如：用户选择退出的同时，js请求原生。原生调用该方法会获取到null。
            //因此设置一个默认值。不会影响主业务。因为正常业务不会进入到这里来。只要进入。就是发生了极端并发情况，默认值也不会影响业务。
            tz = "US/Eastern";
        } else {
            tz = user.getTimeZone();
        }
        TimeZone curTimeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取某天有多少个小时。
     *
     * @param date 那一天
     * @return
     */
    public static int getTotalHours(Date date) {

        Date begin = getDayBegin(date);
        Date end = getDayBegin(new Date(TimeUtil.getDayBegin(date).getTime() + 26 * 60 * 60 * 1000l));
        return (int) ((end.getTime() - begin.getTime()) / (3600 * 1000l));
    }

    public static long intervalSecond(Date newerDate, Date olderDate) {
        return (newerDate.getTime() - olderDate.getTime()) / 1000l;
    }

    public static boolean isSameDay(Date dateOne, Date dateTwo) {

        return getDayBegin(dateOne).getTime() == getDayBegin(dateTwo).getTime();
    }

    /**
     * 获取日期的格式，符合dailylog的格式
     *
     * @return
     */
    public static String getDailylogFromat(Date date, boolean changeLanguage) {
        User user = SystemHelper.getUser();
        String tz;
        if (user == null) {
            //解决目前存在的登出后js仍在调用原生方法的问题。
            //因为正常情况下，user不会为null。只有非常极端的情况下才为null。
            //例如：用户选择退出的同时，js请求原生。原生调用该方法会获取到null。
            //因此设置一个默认值。不会影响主业务。因为正常业务不会进入到这里来。只要进入。就是发生了极端并发情况，默认值也不会影响业务。
            tz = "US/Eastern";
        } else {
            tz = user.getTimeZone();
        }

        dailylogFormatterZH.setTimeZone(TimeZone.getTimeZone(tz));

        if (LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH && changeLanguage) {
            return dailylogFormatterZH.format(date);
        }
        dailylogFormatter.setTimeZone(TimeZone.getTimeZone(tz));
        return dailylogFormatter.format(date);
    }

    /**
     * utc时间转换为指定时区，指定格式的日期字符串
     *
     * @param utcTime  utc时间戳
     * @param timeZone 时区
     * @param format   待转换的日期格式
     * @return 日期字符串
     */
    public static String utcToLocal(long utcTime, String timeZone, String format) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(utcTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        if (LanguageUtil.getInstance().getLanguageType() == LanguageUtil.LANGUAGE_ZH) {
            simpleDateFormat = new SimpleDateFormat(format, Locale.CHINA);
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * utc时间转换为指定时区，指定格式的日期字符串
     *
     * @param utcTime  utc时间戳
     * @param timeZone 时区
     * @param format   待转换的日期格式
     * @return 日期字符串
     */
    public static String utcToEnLocal(long utcTime, String timeZone, String format) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(utcTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * 根据司机所在时区获取日期
     *
     * @param date
     * @return
     */
    public static Date getDateByTimeZone(Date date) {

        User user = SystemHelper.getUser();
        String tz;
        if (user == null) {
            //解决目前存在的登出后js仍在调用原生方法的问题。
            //因为正常情况下，user不会为null。只有非常极端的情况下才为null。
            //例如：用户选择退出的同时，js请求原生。原生调用该方法会获取到null。
            //因此设置一个默认值。不会影响主业务。因为正常业务不会进入到这里来。只要进入。就是发生了极端并发情况，默认值也不会影响业务。
            tz = "US/Eastern";
        } else {
            tz = user.getTimeZone();
        }
        TimeZone curTimeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTime(date);
        return calendar.getTime();
    }

    public static Date getNextDate(Date date, int days) {

        User user = SystemHelper.getUser();
        String tz;
        if (user == null) {
            //解决目前存在的登出后js仍在调用原生方法的问题。
            //因为正常情况下，user不会为null。只有非常极端的情况下才为null。
            //例如：用户选择退出的同时，js请求原生。原生调用该方法会获取到null。
            //因此设置一个默认值。不会影响主业务。因为正常业务不会进入到这里来。只要进入。就是发生了极端并发情况，默认值也不会影响业务。
            tz = "US/Eastern";
        } else {
            tz = user.getTimeZone();
        }
        TimeZone curTimeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    public static Date getPreviousDate(Date date, int days) {
        User user = SystemHelper.getUser();
        String tz;
        if (user == null) {
            //解决目前存在的登出后js仍在调用原生方法的问题。
            //因为正常情况下，user不会为null。只有非常极端的情况下才为null。
            //例如：用户选择退出的同时，js请求原生。原生调用该方法会获取到null。
            //因此设置一个默认值。不会影响主业务。因为正常业务不会进入到这里来。只要进入。就是发生了极端并发情况，默认值也不会影响业务。
            tz = "US/Eastern";
        } else {
            tz = user.getTimeZone();
        }
        TimeZone curTimeZone = TimeZone.getTimeZone(tz);
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }


    /**
     * 获取日期的格式，符合dot review head request的格式
     *
     * @return
     */
    public static String getDotReviewHeadRequestFormat(Date date) {

        return dotReviewHeadRequestFormatter.format(date);
    }

    public static String secondToHourMinStr(long second) {

        return second / (60 * 60) + App.getContext().getString(R.string.time_h) + +second % (60 * 60) / 60 + App.getContext().getString(R.string.time_m);
    }

    /**
     * 获取指定格式的时间
     *
     * @param date
     * @param format
     * @param timeZone
     * @return
     */
    public static Date strToDate(String date, String format, String timeZone) {

        if (TextUtils.isEmpty(date)) {
            return null;
        }

        SimpleDateFormat myFormat = getDateFormat(format, timeZone);

        try {
            return myFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 时间的格式化
     *
     * @param timeZone 时区
     * @param format   格式
     * @return
     */
    public static SimpleDateFormat getDateFormat(String format, String timeZone) {

        SimpleDateFormat formater = new SimpleDateFormat(format);

        if (!TextUtils.isEmpty(timeZone)) {

            TimeZone userTimeZone = TimeZone.getTimeZone(timeZone);
            formater.setTimeZone(userTimeZone);
        }
        return formater;
    }

    public static int hourMinSecondStrToSecond(String hourMinSecondStr) {

        int second = 0;
        char[] stringArr = hourMinSecondStr.toCharArray();
        second = ((Integer.valueOf(String.valueOf(stringArr[0]))) * 10 * 60 * 60)
                + ((Integer.valueOf(String.valueOf(stringArr[1]))) * 60 * 60)
                + ((Integer.valueOf(String.valueOf(stringArr[2]))) * 10 * 60)
                + ((Integer.valueOf(String.valueOf(stringArr[3]))) * 60)
                + ((Integer.valueOf(String.valueOf(stringArr[4]))) * 10)
                + ((Integer.valueOf(String.valueOf(stringArr[5]))));
        return second;
    }

    /**
     * 判断里程偏差有没有过0时
     *
     * @param odoOffsetUpdateTime
     * @return
     */
    public static boolean compareOdoOffsetUpdateTime(String odoOffsetUpdateTime) {

        try {

            if (!TextUtils.isEmpty(odoOffsetUpdateTime) && !odoOffsetUpdateTime.equals("0")) {

                Date updateDate = new Date(Long.parseLong(odoOffsetUpdateTime));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(updateDate);
                int odoOffsetYear = calendar.get(Calendar.YEAR);
                int odoOffsetDay = calendar.get(Calendar.DAY_OF_YEAR);

                Date nowDate = new Date();
                calendar.setTime(nowDate);
                int nowYear = calendar.get(Calendar.YEAR);
                int nowDay = calendar.get(Calendar.DAY_OF_YEAR);

                if (nowYear >= odoOffsetYear && nowDay > odoOffsetDay) {

                    return true;
                } else {

                    return false;
                }
            }
            return false;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断两个日期是不是同一天
     *
     * @param firstDate  第一个日期
     * @param secondDate 第二个日期
     * @return
     */
    public static boolean compareDaybyDate(long firstDate, long secondDate) {

        try {

            User user = SystemHelper.getUser();
            String tz;
            if (user == null) {
                //解决目前存在的登出后js仍在调用原生方法的问题。
                //因为正常情况下，user不会为null。只有非常极端的情况下才为null。
                //例如：用户选择退出的同时，js请求原生。原生调用该方法会获取到null。
                //因此设置一个默认值。不会影响主业务。因为正常业务不会进入到这里来。只要进入。就是发生了极端并发情况，默认值也不会影响业务。
                tz = "US/Eastern";
            } else {
                tz = user.getTimeZone();
            }
            TimeZone curTimeZone = TimeZone.getTimeZone(tz);
            Calendar calendar = Calendar.getInstance(curTimeZone);
            calendar.setTimeInMillis(firstDate);
            int firstYear = calendar.get(Calendar.YEAR);
            int firstDay = calendar.get(Calendar.DAY_OF_YEAR);

            calendar.setTimeInMillis(secondDate);
            int secondYear = calendar.get(Calendar.YEAR);
            int secondDay = calendar.get(Calendar.DAY_OF_YEAR);

            if (secondYear > firstYear) {

                return true;
            } else if (secondYear == firstYear) {

                if (secondDay > firstDay) {

                    return true;
                }
            }
            return false;
        } catch (Exception e) {

            Logger.w(Tags.TIMEUTIL, "compare day accure execption");
            return false;
        }
    }

    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDays(Date date1, Date date2) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2)   //同一年
        {
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0)    //闰年
                {
                    timeDistance += 366;
                } else    //不是闰年
                {
                    timeDistance += 365;
                }
            }

            return timeDistance + (day2 - day1);
        } else    //不同年
        {
            return day2 - day1;
        }
    }

}
