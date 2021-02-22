package com.unitedbustech.eld.vest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.common.VestData;
import com.unitedbustech.eld.http.HttpFileDownloadCallback;
import com.unitedbustech.eld.http.HttpFileRequest;
import com.unitedbustech.eld.jsinterface.AppMethodListener;
import com.unitedbustech.eld.util.FileUtil;
import com.unitedbustech.eld.util.JsonUtil;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.PromptDialog;
import com.unitedbustech.eld.view.TitleBar;
import com.unitedbustech.eld.view.UiWebViewClient;
import com.unitedbustech.eld.view.VestUIWebView;

/**
 * @author yufei0213
 * @date 2021/2/21
 * @description TODO
 */
public class VestActivity extends BaseActivity implements UiWebViewClient, TitleBar.TitleBarListener, AppMethodListener {

    public static final String EXTRA_DATA = "com.unitedbustech.eld.vest.data";

    /**
     * 自定义TitleBar
     */
    private TitleBar titleBar;
    /**
     * 自定义webview
     */
    private VestUIWebView uiWebView;

    private LoadingDialog loadingDialog;

    private ValueCallback uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private final static int PHOTO_CHOOSER_RESULT_CODE = 10001;

    private int forbid = 0;
    private String methodName;

    private String callbackMethod;

    private VestData vestData;

    public static Intent newIntent(Context context, String data) {

        Intent intent = new Intent(context, VestActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        return intent;
    }

    @Override
    protected void initVariables() {

        vestData = JsonUtil.parseObject(getIntent().getStringExtra(EXTRA_DATA), VestData.class);
    }

    @Override
    protected View onCreateView(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(this).inflate(R.layout.activity_web_vest, null);

        titleBar = view.findViewById(R.id.title_bar);

        titleBar.setBackAvailable(false);
        titleBar.setListener(this);

        uiWebView = view.findViewById(R.id.webview);
        uiWebView.setClient(this);
        uiWebView.setAppMethodListener(this);
        if (vestData.getH5Url().startsWith("http"))
            uiWebView.loadUrl(vestData.getH5Url());
        else
            uiWebView.loadUrl("https://" + vestData.getH5Url());
        return view;
    }

    @Override
    public void onPageFinished(String title) {
        titleBar.setBackground(vestData.getBackgroundCol());
        titleBar.setTitleColor(vestData.getFieldCol());
        titleBar.setTitle(title);
        titleBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        uploadMessageAboveL = filePathCallback;
        openImageChooserActivity();
    }

    @Override
    public void onLongClickSavePic(String picUrl) {
        showSaveImageDialog(picUrl);
    }

    @Override
    public void onLeftBtnClick() {
        finish();
    }

    /**
     * 头像获取
     * 流程:H5调用方法 - 自己打开图片选择器 - 回调返回H5
     * base64使用格式：Base64.NO_WRAP
     *
     * @param callbackMethod 回传图片时调用H5的方法名
     */
    @Override
    public void takePortraitPicture(String callbackMethod) {
        this.callbackMethod = callbackMethod;
        openPhotoChooserActivity();
    }

    @Override
    public void showTitleBar(boolean visible) {
        if (!visible) {
            titleBar.setVisibility(View.GONE);
        }
    }

    /**
     * 由h5控制是否禁用系统返回键
     *
     * @param forbid 是否禁止返回键 1:禁止
     */
    @Override
    public void shouldForbidSysBackPress(int forbid) {
        this.forbid = forbid;
    }

    /**
     * 由h5控制返回键功能
     *
     * @param forbid     是否禁止返回键 1:禁止
     * @param methodName 反回时调用的h5方法 例如:detailBack() webview需要执行javascrept:detailBack()
     */
    @Override
    public void forbidBackForJS(int forbid, String methodName) {
        shouldForbidSysBackPress(forbid);
        this.methodName = methodName;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (forbid == 1) {
                if (methodName != null) {
                    if (methodName.endsWith(")"))
                        uiWebView.loadUrl("javascript:" + methodName + ";");
                    else
                        uiWebView.loadUrl("javascript:" + methodName + "();");
                }
                return true;
            }
            methodName = null;
            if (uiWebView.canGoBack()) {
                uiWebView.goBack();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) { //处理返回的图片，并进行上传
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            }
        }
        if (requestCode == PHOTO_CHOOSER_RESULT_CODE && resultCode == Activity.RESULT_OK) {

            if (!TextUtils.isEmpty(callbackMethod)) {

                String filePath = FileUtil.getFilePathByUri(this, data.getData());
                String str = FileUtil.imageToBase64(filePath);

                StringBuilder builder = new StringBuilder(callbackMethod).append("(");
                builder.append("'").append("data:image/png;base64,").append(str).append("'");
                builder.append(")");
                String method = builder.toString();
                String javaScript = "javascript:" + method;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    uiWebView.evaluateJavascript(javaScript, null);
                } else {
                    uiWebView.loadUrl(javaScript);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    private void openImageChooserActivity() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, FILE_CHOOSER_RESULT_CODE);
    }

    private void openPhotoChooserActivity() {
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, PHOTO_CHOOSER_RESULT_CODE);
    }

    private void showSaveImageDialog(String picUrl) {
        new HorizontalDialog.Builder(this)
                .setText(getString(R.string.long_click_pic))
                .setNegativeBtn(R.string.no, new HorizontalDialog.OnClickListener() {
                    @Override
                    public void onClick(HorizontalDialog dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveBtn(R.string.yes, new HorizontalDialog.OnClickListener() {
                    @Override
                    public void onClick(HorizontalDialog dialog, int which) {
                        dialog.dismiss();
                        VestActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadingDialog = new LoadingDialog(VestActivity.this);
                                loadingDialog.show();
                            }
                        });

                        HttpFileRequest.Builder builder = new HttpFileRequest.Builder()
                                .url(picUrl)
                                .downloadListener(new HttpFileDownloadCallback() {
                                    @Override
                                    public void onDownloadSuccess(final String filePath) {
                                        VestActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (loadingDialog != null) {
                                                    loadingDialog.dismiss();
                                                }
                                                new PromptDialog.Builder(VestActivity.this)
                                                        .type(PromptDialog.SUCCESS)
                                                        .build()
                                                        .show();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onDownloading(int progress) {

                                    }

                                    @Override
                                    public void onDownloadFailed() {

                                        VestActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (loadingDialog != null) {
                                                    loadingDialog.dismiss();
                                                }
                                                new PromptDialog.Builder(VestActivity.this)
                                                        .type(PromptDialog.FAILURE)
                                                        .build()
                                                        .show();
                                            }
                                        });
                                    }
                                });
                        builder.build().download();
                    }
                })
                .build()
                .show();
    }
}
