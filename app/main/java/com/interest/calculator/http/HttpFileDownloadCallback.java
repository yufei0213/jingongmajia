package com.interest.calculator.http;

/**
 * @author liuzhe
 * @date 2018/5/16
 * @description HttpFileDownloadCallback
 */
public interface HttpFileDownloadCallback {

    void onDownloadSuccess(String filePath);

    void onDownloading(int progress);

    void onDownloadFailed();
}
