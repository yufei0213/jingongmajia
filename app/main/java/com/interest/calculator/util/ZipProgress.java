package com.interest.calculator.util;

/**
 * @author yufei0213
 * @date 2018/7/9
 * @description ZipProgress
 */
public interface ZipProgress {

    void onZipStart();

    void onZipProgress(int progress);

    void onZipComplete(String zipPath);
}
