package com.unitedbustech.eld.system;

import android.os.Environment;
import android.text.TextUtils;

import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.util.LocalDataStorageUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.UUID;

/**
 * @author yufei0213
 * @date 2018/3/8
 * @description UUIDS
 */
public class UUIDS {

    private static final String TAG = "UUIDS";

    private final static String DEVICE_ID_KEY = ".shield_device_id";
    private final static String DEFAULT_FILE_NAME = ".shield_device_id";

    private final static String FILE_ANDROID = Environment.getExternalStoragePublicDirectory("Android") + File.separator + DEFAULT_FILE_NAME;
    private final static String FILE_DCIM = Environment.getExternalStoragePublicDirectory("DCIM") + File.separator + DEFAULT_FILE_NAME;

    public static String getUUID() {

        return LocalDataStorageUtil.getString(DEVICE_ID_KEY);
    }

    public static void init() {

        Logger.d(TAG, "uuid init");

        String uuid = LocalDataStorageUtil.getString(DEVICE_ID_KEY);

        Logger.d(TAG, "local storage uuid=" + uuid);

        if (TextUtils.isEmpty(uuid)) {

            uuid = checkAndroidFile();

            Logger.d(TAG, "android uuid=" + uuid);

            if (TextUtils.isEmpty(uuid)) {

                uuid = checkDCIMFile();

                Logger.d(TAG, "dcim uuid=" + uuid);

                if (TextUtils.isEmpty(uuid)) {

                    uuid = createUUID();

                    Logger.d(TAG, "create uuid: " + uuid);

                    saveAndroidFile(uuid);
                    saveDCIMFile(uuid);
                } else {

                    saveAndroidFile(uuid);
                }
            } else {

                saveDCIMFile(uuid);
            }

            LocalDataStorageUtil.putString(DEVICE_ID_KEY, uuid);
        } else {

            saveAndroidFile(uuid);
            saveDCIMFile(uuid);
        }
    }

    private static String createUUID() {

        return UUID.randomUUID().toString();
    }

    private static String checkAndroidFile() {

        BufferedReader reader = null;
        try {

            File file = new File(FILE_ANDROID);
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine();
        } catch (Exception e) {

            return null;
        } finally {

            try {

                if (reader != null) {

                    reader.close();
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private static void saveAndroidFile(String id) {

        try {

            File file = new File(FILE_ANDROID);

            FileWriter writer = new FileWriter(file);
            writer.write(id);
            writer.flush();
            writer.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private static String checkDCIMFile() {

        BufferedReader reader = null;
        try {

            File file = new File(FILE_DCIM);
            reader = new BufferedReader(new FileReader(file));
            return reader.readLine();
        } catch (Exception e) {

            return null;
        } finally {

            try {

                if (reader != null) {

                    reader.close();
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    private static void saveDCIMFile(String id) {

        try {

            File file = new File(FILE_DCIM);

            FileWriter writer = new FileWriter(file);
            writer.write(id);
            writer.flush();
            writer.close();
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
