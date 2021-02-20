package com.unitedbustech.eld.http;

/**
 * @author yufei0213
 * @date 2018/7/9
 * @description HttpFileUploadCallback
 */
public interface HttpFileUploadCallback {

    void onUploadSuccess();

    void onUploading(int progress);

    void onUploadFailed();
}
