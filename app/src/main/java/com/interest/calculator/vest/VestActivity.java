package com.interest.calculator.vest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.interest.calculator.R;
import com.interest.calculator.activity.AssistActivity;
import com.interest.calculator.activity.BaseActivity;
import com.interest.calculator.ad.WebAdActivity;
import com.interest.calculator.common.Constants;
import com.interest.calculator.common.VestData;
import com.interest.calculator.http.HttpFileDownloadCallback;
import com.interest.calculator.http.HttpFileRequest;
import com.interest.calculator.http.HttpRequest;
import com.interest.calculator.http.HttpRequestCallback;
import com.interest.calculator.http.HttpResponse;
import com.interest.calculator.jsinterface.AppMethodListener;
import com.interest.calculator.logs.Logger;
import com.interest.calculator.util.FileUtil;
import com.interest.calculator.util.JsonUtil;
import com.interest.calculator.util.LocalDataStorageUtil;
import com.interest.calculator.util.MiPictureHelper;
import com.interest.calculator.view.AdView;
import com.interest.calculator.view.AdViewListener;
import com.interest.calculator.view.HorizontalDialog;
import com.interest.calculator.view.LoadingDialog;
import com.interest.calculator.view.PromptDialog;
import com.interest.calculator.view.TitleBar;
import com.interest.calculator.view.UiWebViewClient;
import com.interest.calculator.view.VestUIWebView;

/**
 * @author yufei0213
 * @date 2021/2/21
 * @description TODO
 */
public class VestActivity extends BaseActivity implements UiWebViewClient, TitleBar.TitleBarListener, AppMethodListener, AdViewListener {

    public static final String EXTRA_DATA = "com.interest.eld.vest.data";

    /**
     * 自定义TitleBar
     */
    private TitleBar titleBar;
    /**
     * 自定义webview
     */
    private VestUIWebView uiWebView;

    private LoadingDialog loadingDialog;

    private AdView adView;

    private ValueCallback uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private final static int PHOTO_CHOOSER_RESULT_CODE = 10001;
    private final static int RC_SIGN_IN = 10002;
    private final static int PAYTM_REQUEST_CODE = 10003;

    private int forbid = 0;
    private String methodName;

    private String callbackMethod;

    private VestData vestData;

    private GoogleSignInClient mGoogleSignInClient;
    private String openGoogleData;

    public static Intent newIntent(Context context, String data) {

        Intent intent = new Intent(context, VestActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        new AssistActivity(this);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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

        adView = view.findViewById(R.id.ad_view);
        adView.setAdViewListener(this);
//        if (vestData.getAdvOn() != 1) {
        adView.setVisibility(View.GONE);
//        } else {
//            adView.setUrl(vestData.getAdvImg(), vestData.getAdvUrl());
//        }

        return view;
    }

    @Override
    public void onPageFinished(String title) {
        if (!TextUtils.isEmpty(vestData.getBackgroundCol())) {
            setStatusBar(vestData.getBackgroundCol(), false);
            titleBar.setBackground(vestData.getBackgroundCol());
        }
        if (!TextUtils.isEmpty(vestData.getFieldCol())){
            // TODO: 2021/3/3
            setStatusBarColor(vestData.getFieldCol());
            titleBar.setTitleColor(vestData.getFieldCol());
        }

        if (!TextUtils.isEmpty(title))
            titleBar.setTitle(title);
//        titleBar.setVisibility(View.VISIBLE);
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

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (!visible) {
                    titleBar.setVisibility(View.GONE);
                } else {
                    titleBar.setVisibility(View.VISIBLE);
                }
            }
        });
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
    public void openGoogle(String data) {
        this.openGoogleData = data;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void openPayTm(String data) {

//        PayInfo payInfo = JsonUtil.parseObject(data, PayInfo.class);
        JSONObject payInfo = JsonUtil.parseObject(data);

        try {
            String version = getVersion();
            if (!TextUtils.isEmpty(version) && compareVersion(version, "8.6.0") < 0) {
                Bundle bundle = new Bundle();
                bundle.putDouble("nativeSdkForMerchantAmount", payInfo.getDouble("amount"));
                bundle.putString("orderid", payInfo.getString("orderId"));
                bundle.putString("txnToken", payInfo.getString("textToken"));
                bundle.putString("mid", payInfo.getString("mid"));
                Intent paytmIntent = new Intent();
                ComponentName componentName = new ComponentName("net.one97.paytm", "net.one97.paytm.AJRJarvisSplash");
                paytmIntent.setComponent(componentName);
                // You must have to pass hard coded 2 here, Else your transaction would not proceed.
                paytmIntent.putExtra("paymentmode", 2);
                paytmIntent.putExtra("bill", bundle);
                startActivityForResult(paytmIntent, PAYTM_REQUEST_CODE);
            } else {
                Intent paytmIntent = new Intent();
                ComponentName componentName = new ComponentName("net.one97.paytm", "net.one97.paytm.AJRRechargePaymentActivity");
                paytmIntent.setComponent(componentName);
                paytmIntent.putExtra("paymentmode", 2);
                paytmIntent.putExtra("enable_paytm_invoke", true);
                paytmIntent.putExtra("paytm_invoke", true);
                paytmIntent.putExtra(
                        "price",
                        payInfo.getString("amount")
                );//this is string amount

                paytmIntent.putExtra("nativeSdkEnabled", true);
                paytmIntent.putExtra("orderid", payInfo.getString("orderId"));
                paytmIntent.putExtra("txnToken", payInfo.getString("textToken"));
                paytmIntent.putExtra("mid", payInfo.getString("mid"));
                startActivityForResult(paytmIntent, PAYTM_REQUEST_CODE);
            }
        } catch (ActivityNotFoundException e) {
            StringBuilder postData = new StringBuilder();
            String postUrl =
                    "https://securegw.paytm.in/theia/api/v1/showPaymentPage" + "?mid=" + payInfo.getString("mid") + "&orderId=" + payInfo.getString("orderId");
            postData.append("MID=").append(payInfo.getString("mid"))
                    .append("&txnToken=").append(payInfo.getString("textToken"))
                    .append("&ORDER_ID=").append(payInfo.getString("orderId"));
//            .postUrl(postUrl, postData.toString().toByteArray())
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    uiWebView.postUrl(postUrl, postData.toString().getBytes());
                }
            });
        }
    }

    private String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo("net.one97.paytm", 0);
//            String version = info.versionName;
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 版本号比较
     *
     * @param v1
     * @param v2
     * @return 0代表相等，1代表左边大，-1代表右边大
     * Utils.compareVersion("1.0.358_20180820090554","1.0.358_20180820090553")=1
     */
    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }

    }

    @Override
    public void onAdSkip() {
        adView.setVisibility(View.GONE);
    }

    @Override
    public void onAdClick(String adUrl) {
        adView.setVisibility(View.GONE);
        Intent intent = WebAdActivity.newIntent(VestActivity.this, adUrl);
        startActivity(intent);
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
                return true;
            }

            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
//            return super.onKeyDown(keyCode, event);
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

//                String filePath = FileUtil.getFilePathByUri(this, data.getData());
                String filePath = MiPictureHelper.getPath(this, data.getData());
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

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
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

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.

            LocalDataStorageUtil.putString("googleId", account.getId());

            HttpRequest.Builder builder = new HttpRequest.Builder();
            HttpRequest request = builder.url(JsonUtil.parseObject(openGoogleData).getString("host") + Constants.VEST_GOOGLE_LOGIN_URL)
                    .addParam("id", account.getId())
                    .addParam("name", account.getDisplayName())
                    .addParam("email", account.getEmail())
                    .addParam("type", "1")
                    .addParam("sign", JsonUtil.parseObject(openGoogleData).getString("sign"))
                    .build();

            request.get(new HttpRequestCallback() {
                @Override
                public void onRequestFinish(HttpResponse response) {
                    if (response.isSuccess()) {

                        JSONObject object = JsonUtil.parseObject(response.getData());
                        String token1 = object.getString("token1");
                        String token2 = object.getString("token2");
                        String url = object.getString("url");

                        String host = JsonUtil.parseObject(openGoogleData).getString("host");

                        if (!TextUtils.isEmpty(token1))
                            CookieManager.getInstance().setCookie(host, "token1=" + token1 + ";expires=1; path=/");
                        if (!TextUtils.isEmpty(token2))
                            CookieManager.getInstance().setCookie(host, "token2=" + token2 + ";expires=1; path=/");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uiWebView.loadUrl(url);
                            }
                        });
                    } else {
                        Logger.e("Google 登录接口请求失败");
                    }
                }
            });
        } catch (ApiException e) {
            Logger.e(e.toString());
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
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
