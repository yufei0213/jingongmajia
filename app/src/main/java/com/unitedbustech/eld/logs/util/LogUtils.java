package com.unitedbustech.eld.logs.util;

import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.util.TimeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/3/13
 * @description 日志工具类，内部使用
 */
public class LogUtils {

    /**
     * 日志有效期
     */
    private static final int VALID_DAYS = 7;

    public static void checkValid() throws Exception {

        //获取从今天开始过去VALID_DAYS天的日期字符串
        List<String> fileNameArray = new ArrayList<>();
        for (int i = 0; i < VALID_DAYS; i++) {

            fileNameArray.add(TimeUtil.getPastDate(i, "yyyy-MM-dd", "US/Eastern"));
        }

        File f = new File(Constants.LOG_DEFAULT_PATH);
        //如果文件存在，并且是文件夹，找到文件夹下全部文件
        //如果文件名不符合过去VALID_DAYS天的限制，则删除
        if (f.exists() && f.isDirectory() && f.listFiles().length > 0) {

            File[] delFile = f.listFiles();

            int i = f.listFiles().length;
            for (int j = 0; j < i; j++) {

                if (!fileNameArray.contains(delFile[j].getName())) {

                    delFile[j].delete();
                }
            }
        }
    }
}
