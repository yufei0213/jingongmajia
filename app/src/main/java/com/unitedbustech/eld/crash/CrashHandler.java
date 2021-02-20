package com.unitedbustech.eld.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *  @author liuzhe
 * @date 2018/4/20
 * @description CrashHandler全局异常捕获
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";
    private static CrashHandler INSTANCE;
    private Context mContext;

    // 系统默认的 UncaughtException 处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // 用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();

    private static  Object syncRoot = new Object();

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {

        // 防止多线程访问安全，这里使用了双重锁
        if (INSTANCE == null)
        {

            synchronized (syncRoot)
            {

                if (INSTANCE == null)
                {
                    INSTANCE =  new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {

        mContext = context;

        // 获取系统默认的 UncaughtException 处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该 CrashHandler 为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {

            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {

            try {

                Thread.sleep(3000);
            } catch (InterruptedException e) {

                e.printStackTrace();
            }

            // 退出程序
//			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(0);
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private boolean handleException(Throwable ex) {

        if (ex == null) {

            return false;
        }
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        // 保存日志文件
        saveCrashInfo2File(ex);
        return true;
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {

        try {

            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);

            if (pi != null) {

                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
                infos.put("model", android.os.Build.MODEL);// 手机型号
                infos.put("versionSDK", android.os.Build.VERSION.RELEASE);// 系统版本
            }
        } catch (NameNotFoundException e) {

            e.printStackTrace();
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {

            try {

                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中*
     *
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {

            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {

            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        if(printWriter != null) {

            printWriter.close();
        }

        String result = writer.toString();
        sb.append(result);
        Logger.w(Tags.CRASH,sb.toString());

        return null;
    }

}
