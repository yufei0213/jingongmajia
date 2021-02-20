package com.unitedbustech.eld.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.prepare.PrepareActivity;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.update.UpdateContract;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.util.KeyboardUtils;
import com.unitedbustech.eld.view.CarrierInputView;
import com.unitedbustech.eld.view.ClickProxy;
import com.unitedbustech.eld.view.DriverInputView;
import com.unitedbustech.eld.view.FontTextView;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.PasswordInputView;
import com.unitedbustech.eld.view.UpdateDownloadDialog;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description 登录
 */
public class LoginFragment extends BaseFragment implements
        View.OnTouchListener,
        LoginContract.View,
        UpdateContract.View,
        CarrierInputView.CarrierIdInputListener,
        DriverInputView.DriverIdInputListener,
        PasswordInputView.PasswordInputListener,
        KeyboardUtils.OnSoftInputChangedListener {

    private static final String TAG = "LoginFragment";

    private static final String REGEX = "^[A-Za-z0-9]+$";
    private static final String DRIVERID_REGEX = "^[.]+$";
    private static final int PASSWORD_MIN_LENGTH = 4;

    private boolean isShow = false;

    private String carrierId;
    private String driverId;
    private String password;

    private LinearLayout content;
    private LinearLayout requestContent;
    private CarrierInputView carrierInputView;
    private DriverInputView driverInputView;
    private PasswordInputView passwordInputView;
    private Button loginBtn;
    private FontTextView trialAccountTip;

    private LoadingDialog loadingDialog;
    private UpdateDownloadDialog updateDownloadDialog;

    private LoginContract.Presenter presenter;
    private UpdateContract.Presenter updatePresenter;

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(args);
        return loginFragment;
    }

    @Override
    protected void initStatusBar() {

        setStatusBar(R.color.transparent, true);
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, null);
        content = view.findViewById(R.id.content);
        requestContent = view.findViewById(R.id.request_content);

        content.setOnTouchListener(this);

        carrierInputView = view.findViewById(R.id.carrier_id);
        carrierInputView.setListener(this);
        driverInputView = view.findViewById(R.id.driver_id);
        driverInputView.setListener(this);
        passwordInputView = view.findViewById(R.id.password_id);
        passwordInputView.setListener(this);

        loginBtn = view.findViewById(R.id.login_btn);
        trialAccountTip = view.findViewById(R.id.login_trial_accout);

        loginBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View focusView = getActivity().getCurrentFocus();
                if (focusView != null) {

                    focusView.clearFocus();
                    AppUtil.hideSoftInput(getContext(), focusView.getWindowToken());
                }

                showLoading();
                updatePresenter.checkVersion();
            }
        }));

        trialAccountTip.setOnClickListener(new ClickProxy(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse(getResources().getString(R.string.login_account_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }));

        ((TextView) view.findViewById(R.id.version)).setText(getString(R.string.version)
                .replace("#version#", AppUtil.getVersionName(getActivity())));

        loadingDialog = new LoadingDialog(getContext());

        presenter.loadUser();

        KeyboardUtils.registerSoftInputChangedListener(getActivity(), this);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (isShow) {

            if (carrierInputView != null) {

                carrierInputView.setText(SystemHelper.getUser().getAccountCarrierId());
            }
            if (driverInputView != null) {

                driverInputView.setText(SystemHelper.getUser().getAccountDriverId());
            }
            isShow = !isShow;
        }
    }

    @Override
    public void setPresenter(LoginContract.Presenter presenter) {

        this.presenter = presenter;
    }

    @Override
    public void setUpdatePresenter(UpdateContract.Presenter presenter) {

        this.updatePresenter = presenter;
    }

    @Override
    public void initInputValue(final User user) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                carrierInputView.setText(user.getAccountCarrierId());
//                driverInputView.setText(user.getAccountDriverId());
            }
        });
    }

    @Override
    public void hasNewVersion(boolean has, final boolean isForce) {

        if (has) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    HorizontalDialog.Builder builder = new HorizontalDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_emoji_love)
                            .setCancelable(!isForce)
                            .setText(isForce ? getString(R.string.version_install_apk_force) : getString(R.string.version_install_apk));
                    if (isForce) {

                        builder.setPositiveBtn(R.string.update, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                                updatePresenter.update();
                            }
                        });
                    } else {

                        builder.setNegativeBtn(R.string.cancel, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                                LoginFragment.this.presenter.login(carrierId, driverId, password, false);
                            }
                        });
                        builder.setNeutralBtn(R.string.update, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                                updatePresenter.update();
                            }
                        });
                    }

                    hideLoading();
                    builder.build().show();
                }
            });
        } else {

            LoginFragment.this.presenter.login(carrierId, driverId, password, false);
        }
    }

    @Override
    public void showDownloadingDialog() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                updateDownloadDialog = new UpdateDownloadDialog(getActivity());
                updateDownloadDialog.show();
            }
        });
    }

    @Override
    public void updateDownloadProgress(final int progress) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                updateDownloadDialog.updateProgress(progress);
            }
        });
    }

    @Override
    public void hideDownloadingDialog() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                updateDownloadDialog.hide();
                updateDownloadDialog = null;
            }
        });
    }

    @Override
    public void downloadFailed(final boolean isForce) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                HorizontalDialog.Builder builder = new HorizontalDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_emoji_love)
                        .setCancelable(!isForce)
                        .setText(R.string.version_download_failed);

                if (isForce) {

                    builder.setPositiveBtn(R.string.retry, new HorizontalDialog.OnClickListener() {
                        @Override
                        public void onClick(HorizontalDialog dialog, int which) {

                            dialog.dismiss();
                            updatePresenter.update();
                        }
                    });
                } else {

                    builder.setNegativeBtn(R.string.cancel, new HorizontalDialog.OnClickListener() {
                        @Override
                        public void onClick(HorizontalDialog dialog, int which) {

                            dialog.dismiss();
                            LoginFragment.this.presenter.login(carrierId, driverId, password, false);
                        }
                    });
                    builder.setNeutralBtn(R.string.retry, new HorizontalDialog.OnClickListener() {
                        @Override
                        public void onClick(HorizontalDialog dialog, int which) {

                            dialog.dismiss();
                            updatePresenter.update();
                        }
                    });
                }

                builder.build().show();
            }
        });
    }

    @Override
    public void showLoading() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingDialog.show();
            }
        });
    }

    @Override
    public void hideLoading() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingDialog.dismiss();
            }
        });
    }

    @Override
    public void loginSuccess() {

        this.presenter.loadData();
    }

    @Override
    public void loadSuccess() {

        Intent intent = PrepareActivity.newIntent(getActivity(), PrepareActivity.ORIGIN_LOGIN);
        startActivity(intent);

        getActivity().finish();
    }

    @Override
    public void accountOnline(final int code, final String msg) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (code == RequestStatus.LOGIN_ON_DRIVING.getCode()) {

                    new HorizontalDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_emoji_love)
                            .setText(msg)
                            .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                                @Override
                                public void onClick(HorizontalDialog dialog, int which) {

                                    dialog.dismiss();
                                }
                            })
                            .build()
                            .show();
                } else if (code == RequestStatus.LOGIN_NOT_DRIVING.getCode()) {

                    new HorizontalDialog.Builder(getActivity())
                            .setIcon(R.drawable.ic_emoji_love)
                            .setText(msg)
                            .setNegativeBtn(R.string.no, new HorizontalDialog.OnClickListener() {
                                @Override
                                public void onClick(HorizontalDialog dialog, int which) {

                                    dialog.dismiss();
                                }
                            })
                            .setNeutralBtn(R.string.yes, new HorizontalDialog.OnClickListener() {
                                @Override
                                public void onClick(HorizontalDialog dialog, int which) {

                                    dialog.dismiss();
                                    presenter.login(carrierId, driverId, password, true);
                                }
                            })
                            .build()
                            .show();
                }
            }
        });
    }

    @Override
    public void accountInvalid(int code, final String msg) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                new HorizontalDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_emoji_love)
                        .setText(msg)
                        .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();

            }
        });
    }

    @Override
    public void loadFailed(final int code, final String msg) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String tempMsg = msg;

                if (code == RequestStatus.UNTREATED_ERROR.getCode() ||
                        code == RequestStatus.NETWORK_CONNECT_ERROR.getCode() ||
                        code == RequestStatus.REQUEST_TIMEOUT.getCode()) {

                    tempMsg = getResources().getString(R.string.network_error);
                }

                new HorizontalDialog.Builder(getActivity())
                        .setIcon(R.drawable.ic_emoji_awkward)
                        .setText(tempMsg)
                        .setPositiveBtn(R.string.ok, new HorizontalDialog.OnClickListener() {
                            @Override
                            public void onClick(HorizontalDialog dialog, int which) {

                                dialog.dismiss();
                            }
                        })
                        .build()
                        .show();
            }
        });
    }

    @Override
    public void afterCarrierIdTextChanged(String text) {

        carrierId = text;
        changeLoginBtnState();
    }

    @Override
    public void afterDriverIdTextChanged(String text) {

        driverId = text;
        changeLoginBtnState();
    }

    @Override
    public void afterPasswordTextChanged(String text) {

        password = text;
        changeLoginBtnState();
    }

    @Override
    public void onSoftInputChanged(int height) {

        if (height > 100) {
            requestContent.setVisibility(View.GONE);
        } else {
            requestContent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        AppUtil.hideSoftInput(getContext(), view.getWindowToken());

        return false;
    }

    private void changeLoginBtnState() {

        if (!TextUtils.isEmpty(carrierId) &&
                !TextUtils.isEmpty(driverId) &&
                !TextUtils.isEmpty(password) &&
                carrierId.matches(REGEX) &&
                password.length() >= PASSWORD_MIN_LENGTH &&
                password.matches(REGEX)) {

            loginBtn.setEnabled(true);
        } else {

            loginBtn.setEnabled(false);
        }
    }

    /**
     * 被其他设备顶出/长时间未使用，进入登录界面，显示Carrier ID、Driver ID，不显示Password
     *
     * @param isShow 是否显示
     */
    public void setCarrierIdAndDriverIdShow(boolean isShow) {

        this.isShow = isShow;
    }
}
