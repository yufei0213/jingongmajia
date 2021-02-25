package com.interest.calculator.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yufei0213
 * @date 2018/2/2
 * @description 线程池工具
 */
public class ThreadUtil {

    /**
     * 线程池
     */
    private ExecutorService cachedThreadPool;

    private static ThreadUtil instance = null;

    private ThreadUtil() {
    }

    public static ThreadUtil getInstance() {

        if (instance == null) {

            instance = new ThreadUtil();
        }

        return instance;
    }

    public void init() {

        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {

        if (cachedThreadPool != null) {

            try {

                cachedThreadPool.execute(runnable);
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    public void destroy() {

        cachedThreadPool.shutdown();
    }
}
