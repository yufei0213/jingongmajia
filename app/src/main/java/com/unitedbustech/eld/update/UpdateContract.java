package com.unitedbustech.eld.update;

/**
 * @author yufei0213
 * @date 2018/8/10
 * @description 版本更新接口定义
 */
public interface UpdateContract {

    interface View {

        void setUpdatePresenter(UpdateContract.Presenter presenter);

        void hasNewVersion(boolean has, boolean isForce);

        void showDownloadingDialog();

        void updateDownloadProgress(int progress);

        void hideDownloadingDialog();

        void downloadFailed(boolean isForce);
    }

    interface Presenter {

        void checkVersion();

        void update();
    }
}
