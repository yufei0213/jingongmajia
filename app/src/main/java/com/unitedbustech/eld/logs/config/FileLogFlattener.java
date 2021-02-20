package com.unitedbustech.eld.logs.config;

import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.flattener.Flattener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yufei0213
 * @date 2018/3/7
 * @description FileLogFlattener
 */
public class FileLogFlattener implements Flattener {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

            return format;
        }
    };

    private static final Pattern WRAP = Pattern.compile("(\r\n|\r|\n|\n\r)");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    @Override
    public CharSequence flatten(int logLevel, String tag, String message) {

        Matcher crlf = WRAP.matcher(message);
        if (crlf.find()) {

            message = crlf.replaceAll(" ");
        }

        Matcher space = MULTI_SPACE.matcher(message);
        if (space.find()) {

            message = space.replaceAll(" ");
        }

        return DATE_FORMATTER.get().format(new Date()) + "|" +
                LogLevel.getLevelName(logLevel) + "|" +
                tag + "|" +
                message;
    }
}
