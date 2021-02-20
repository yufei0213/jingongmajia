package com.unitedbustech.eld.logs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.unitedbustech.eld.BuildConfig;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.logs.config.DateFileNameGenerator;
import com.unitedbustech.eld.logs.config.FileLogFlattener;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description 日志工具类
 */
public final class Logger {

    private static final int VERBOSE = 1;

    private static final int DEBUG = 2;

    private static final int INFO = 3;

    private static final int WARN = 4;

    private static final int ERROR = 5;

    private static final int NOTHING = BuildConfig.DEBUG ? 1 : 3; //修改此处控制日志输出

    /**
     * 初始化
     * <p>
     * https://github.com/elvishew/xLog
     */
    public static void init() {

//        try {
//
//            LogUtils.checkValid();
//        } catch (Exception e) {
//
//            e.printStackTrace();
//        }

        LogConfiguration.Builder configBuilder = new LogConfiguration.Builder()
                .tag(Constants.LOG_DEFAULT_TAG)
                .t();

        Printer androidPrinter = new AndroidPrinter();
        FilePrinter.Builder filePrinterBuilder = new FilePrinter
                .Builder(Constants.LOG_DEFAULT_PATH)
                .backupStrategy(new NeverBackupStrategy())
                .fileNameGenerator(new DateFileNameGenerator());

        if (BuildConfig.DEBUG) {

            configBuilder.st(15).b();
            XLog.init(configBuilder.build(), androidPrinter, filePrinterBuilder.build());
        } else {

            configBuilder.st(1);
            filePrinterBuilder.logFlattener(new FileLogFlattener());
            XLog.init(configBuilder.build(), filePrinterBuilder.build());
        }
    }

    /**
     * 输出verbose日志
     *
     * @param msg 日志
     */
    public static void v(String msg) {

        if (VERBOSE >= NOTHING) {

            XLog.logLevel(LogLevel.VERBOSE).v(msg);
        }
    }

    /**
     * 输出verbose日志
     *
     * @param json 日志
     */
    public static void v(JSONObject json) {

        if (VERBOSE >= NOTHING) {

            XLog.logLevel(LogLevel.VERBOSE).json(json.toJSONString());
        }
    }

    /**
     * 输出verbose日志
     *
     * @param json 日志
     */
    public static void v(JSONArray json) {

        if (VERBOSE >= NOTHING) {

            XLog.logLevel(LogLevel.VERBOSE).json(json.toJSONString());
        }
    }

    /**
     * 输出verbose日志
     *
     * @param tag 标识
     * @param msg 日志
     */
    public static void v(String tag, String msg) {

        if (VERBOSE >= NOTHING) {

            XLog.logLevel(LogLevel.VERBOSE).tag(tag).v(msg);
        }
    }

    /**
     * 输出verbose日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void v(String tag, JSONObject json) {

        if (VERBOSE >= NOTHING) {

            XLog.logLevel(LogLevel.VERBOSE).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出verbose日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void v(String tag, JSONArray json) {

        if (VERBOSE >= NOTHING) {

            XLog.logLevel(LogLevel.VERBOSE).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出debug日志
     *
     * @param msg 日志
     */
    public static void d(String msg) {

        if (DEBUG >= NOTHING) {

            XLog.logLevel(LogLevel.DEBUG).d(msg);
        }
    }

    /**
     * 输出debug日志
     *
     * @param json 日志
     */
    public static void d(JSONObject json) {

        if (DEBUG >= NOTHING) {

            XLog.logLevel(LogLevel.DEBUG).json(json.toJSONString());
        }
    }

    /**
     * 输出debug日志
     *
     * @param json 日志
     */
    public static void d(JSONArray json) {

        if (DEBUG >= NOTHING) {

            XLog.logLevel(LogLevel.DEBUG).json(json.toJSONString());
        }
    }

    /**
     * 输出debug日志
     *
     * @param tag 标识
     * @param msg 日志
     */
    public static void d(String tag, String msg) {

        if (DEBUG >= NOTHING) {

            XLog.logLevel(LogLevel.DEBUG).tag(tag).d(msg);
        }
    }

    /**
     * 输出debug日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void d(String tag, JSONObject json) {

        if (DEBUG >= NOTHING) {

            XLog.logLevel(LogLevel.DEBUG).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出debug日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void d(String tag, JSONArray json) {

        if (DEBUG >= NOTHING) {

            XLog.logLevel(LogLevel.DEBUG).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出info日志
     *
     * @param msg 日志
     */
    public static void i(String msg) {

        if (INFO >= NOTHING) {

            XLog.logLevel(LogLevel.INFO).i(msg);
        }
    }

    /**
     * 输出info日志
     *
     * @param json 日志
     */
    public static void i(JSONObject json) {

        if (INFO >= NOTHING) {

            XLog.logLevel(LogLevel.INFO).json(json.toJSONString());
        }
    }

    /**
     * 输出info日志
     *
     * @param json 日志
     */
    public static void i(JSONArray json) {

        if (INFO >= NOTHING) {

            XLog.logLevel(LogLevel.INFO).json(json.toJSONString());
        }
    }

    /**
     * 输出info日志
     *
     * @param tag 标识
     * @param msg 日志
     */
    public static void i(String tag, String msg) {

        if (INFO >= NOTHING) {

            XLog.logLevel(LogLevel.INFO).tag(tag).i(msg);
        }
    }

    /**
     * 输出info日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void i(String tag, JSONObject json) {

        if (INFO >= NOTHING) {

            XLog.logLevel(LogLevel.INFO).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出info日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void i(String tag, JSONArray json) {

        if (INFO >= NOTHING) {

            XLog.logLevel(LogLevel.INFO).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出warn日志
     *
     * @param msg 日志
     */
    public static void w(String msg) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).w(msg);
        }
    }

    /**
     * 输出warn日志
     *
     * @param json 日志
     */
    public static void w(JSONObject json) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).json(json.toJSONString());
        }
    }

    /**
     * 输出warn日志
     *
     * @param json 日志
     */
    public static void w(JSONArray json) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).json(json.toJSONString());
        }
    }

    /**
     * 输出warn日志
     *
     * @param tag 标识
     * @param msg 日志
     */
    public static void w(String tag, String msg) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).tag(tag).w(msg);
        }
    }

    /**
     * 输出warn日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void w(String tag, JSONObject json) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出warn日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void w(String tag, JSONArray json) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出warn日志
     *
     * @param tag 标识
     * @param e   异常
     */
    public static void w(String tag, Exception e) {

        if (WARN >= NOTHING) {

            XLog.logLevel(LogLevel.WARN).tag(tag).w(e.getCause());
        }
    }

    /**
     * 输出error日志
     *
     * @param msg 日志
     */
    public static void e(String msg) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).e(msg);
        }
    }

    /**
     * 输出error日志
     *
     * @param json 日志
     */
    public static void e(JSONObject json) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).json(json.toJSONString());
        }
    }

    /**
     * 输出error日志
     *
     * @param json 日志
     */
    public static void e(JSONArray json) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).json(json.toJSONString());
        }
    }

    /**
     * 输出error日志
     *
     * @param e 异常
     */
    public static void e(Exception e) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).e(e.getCause());
        }
    }

    /**
     * 输出error日志
     *
     * @param tag 标识
     * @param msg 日志
     */
    public static void e(String tag, String msg) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).tag(tag).e(msg);
        }
    }

    /**
     * 输出error日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void e(String tag, JSONObject json) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出error日志
     *
     * @param tag  标识
     * @param json 日志
     */
    public static void e(String tag, JSONArray json) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).tag(tag).json(json.toJSONString());
        }
    }

    /**
     * 输出error日志
     *
     * @param tag 标识
     * @param e   异常
     */
    public static void e(String tag, Exception e) {

        if (ERROR >= NOTHING) {

            XLog.logLevel(LogLevel.ERROR).tag(tag).e(e.getCause());
        }
    }
}
