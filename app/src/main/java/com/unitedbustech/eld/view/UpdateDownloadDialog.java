package com.unitedbustech.eld.view;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/8/10
 * @description 版本更新，下载安装包进度
 */
public class UpdateDownloadDialog extends AppCompatDialog {

    private TextView progressView;
    private ProgressBar progressBarView;

    public UpdateDownloadDialog(Context context) {

        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_update_download);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置dialog的宽度为屏幕的宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(layoutParams);

        setCancelable(false);

        initView();
    }

    private void initView() {

        this.progressView = this.findViewById(R.id.progress);
        this.progressBarView = this.findViewById(R.id.progress_bar);
    }

    public void updateProgress(int progress) {

        this.progressView.setText(progress + "");
        this.progressBarView.setProgress(progress);
    }
}
