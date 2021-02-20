package com.unitedbustech.eld.update;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.alibaba.fastjson.JSONObject;
import com.unitedbustech.eld.App;
import com.unitedbustech.eld.common.Constants;
import com.unitedbustech.eld.http.HttpFileDownloadCallback;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.http.HttpRequest;
import com.unitedbustech.eld.http.HttpRequestCallback;
import com.unitedbustech.eld.http.HttpResponse;
import com.unitedbustech.eld.logs.Logger;
import com.unitedbustech.eld.logs.Tags;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.FileUtil;
import com.unitedbustech.eld.util.JsonUtil;

import java.io.File;

/**
 * @author yufei0213
 * @date 2018/8/10
 * @description 版本更新
 */
public class UpdatePresenter implements UpdateContract.Presenter {

    private Activity activity;
    private UpdateContract.View view;

    private boolean isForce;
    private int versionCode;
    private String downloadUrl;

    public UpdatePresenter(Activity activity, UpdateContract.View view) {

        this.activity = activity;
        this.view = view;
        if (this.view != null) {

            this.view.setUpdatePresenter(this);
        }
    }

    @Override
    public void checkVersion() {

        HttpRequest request = new HttpRequest.Builder()
                .url(Constants.API_CHECK_VERSIONCODE)
                .addParam("sys_type", String.valueOf(2))
                .addParam("dev_type", String.valueOf(1))
                .addParam("app_type", String.valueOf(1))
                .build();

        request.get(new HttpRequestCallback() {
            @Override
            public void onRequestFinish(HttpResponse response) {

                if (response.isSuccess()) {

                    try {

                        JSONObject summaryObj = JsonUtil.parseObject(response.getData());
                        JSONObject versionObj = JsonUtil.getJsonObject(summaryObj, "version");

                        isForce = JsonUtil.getInt(versionObj, "forced") == 1;
                        versionCode = JsonUtil.getInt(versionObj, "versionCode");
                        downloadUrl = JsonUtil.getString(versionObj, "downloadUrl");

                        int oldVersionCode = AppUtil.getVersionCode(App.getContext());
                        view.hasNewVersion(oldVersionCode < versionCode, isForce);
                        if (oldVersionCode < versionCode) {

                            Logger.i(Tags.CHECKVERSION, "There is one update version.");
                        } else {

                            Logger.i(Tags.CHECKVERSION, "there is no update version.");
                        }
                    } catch (Exception e) {

                        view.hasNewVersion(false, false);
                        Logger.w(Tags.CHECKVERSION, "analysis response error: " + e.getCause().toString());
                    }
                } else {

                    view.hasNewVersion(false, false);
                    Logger.w(Tags.CHECKVERSION, "request version failed, code=" + response.getCode() + ", msg=" + response.getMsg());
                }
            }
        });
    }

    @Override
    public void update() {

        //安装包存放路径
        String apkPath = Constants.APK_PATH + versionCode + ".apk";
        if (FileUtil.isFileExist(apkPath)) {

            Logger.i(Tags.CHECKVERSION, "install apk...");
            install(apkPath);
        } else {

            view.showDownloadingDialog();
            HttpFileRequest.Builder builder = new HttpFileRequest.Builder()
                    .url(downloadUrl)
                    .downloadListener(new HttpFileDownloadCallback() {
                        @Override
                        public void onDownloadSuccess(String filePath) {

                            Logger.i(Tags.CHECKVERSION, "download apk file success, filePath=" + filePath);

                            //安装包拷贝到指定目录下
                            File fileFolder = new File(Constants.APK_PATH);
                            if (!fileFolder.exists()) {

                                fileFolder.mkdirs();
                            }
                            String targetPath = Constants.APK_PATH + versionCode + ".apk";
                            FileUtil.copyFile(filePath, targetPath);

                            view.hideDownloadingDialog();
                            Logger.i(Tags.CHECKVERSION, "install apk...");
                            install(targetPath);
                        }

                        @Override
                        public void onDownloading(int progress) {

                            Logger.i(Tags.CHECKVERSION, "download progress: " + progress);
                            view.updateDownloadProgress(progress);
                        }

                        @Override
                        public void onDownloadFailed() {

                            view.hideDownloadingDialog();
                            view.downloadFailed(isForce);
                            Logger.w(Tags.CHECKVERSION, "download apk file failed.");
                        }
                    });

            Logger.i(Tags.CHECKVERSION, "start download apk file...");
            builder.build().download();
        }
    }

    /**
     * 调用安装程序
     *
     * @param apkPath 安装包路径
     */
    private void install(String apkPath) {

        File apkFile = new File(apkPath);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(activity, Constants.FILE_PROVIDER, apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {

            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);
    }
}
