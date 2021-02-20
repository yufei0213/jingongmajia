package com.unitedbustech.eld.logs.config;

import com.elvishew.xlog.printer.file.naming.FileNameGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author yufei0213
 * @date 2018/3/7
 * @description DateFileNameGenerator
 */
public class DateFileNameGenerator implements FileNameGenerator {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMATTER = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            format.setTimeZone(TimeZone.getTimeZone("US/Eastern"));

            return format;
        }
    };

    @Override
    public boolean isFileNameChangeable() {

        return true;
    }

    @Override
    public String generateFileName(int logLevel, long timestamp) {

        return DATE_FORMATTER.get().format(new Date(timestamp));
    }
}
