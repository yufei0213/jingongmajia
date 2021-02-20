package com.unitedbustech.eld.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.http.RequestStatus;
import com.unitedbustech.eld.login.LoginActivity;
import com.unitedbustech.eld.prepare.PrepareActivity;
import com.unitedbustech.eld.update.UpdateContract;
import com.unitedbustech.eld.util.AppUtil;
import com.unitedbustech.eld.view.ClickProxy;
import com.unitedbustech.eld.view.HorizontalDialog;
import com.unitedbustech.eld.view.LoadingDialog;
import com.unitedbustech.eld.view.UpdateDownloadDialog;

/**
 * @author yufei0213
 * @date 2018/1/17
 * @description WelcomePrepareFragment
 */
public class WelcomeFragment extends BaseFragment implements UpdateContract.View, WelcomeContract.View {

    private TextView driverNameView;
    private TextView carrierNameView;

    private Button confirmBtn;
    private Button rejectBtn;

    private LoadingDialog loadingDialog;
    private UpdateDownloadDialog updateDownloadDialog;

    private WelcomeContract.Presenter presenter;
    private UpdateContract.Presenter updatePresenter;

    private ClickProxy clickProxy = new ClickProxy(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.confirm_btn:

                    confirmBtnClick();
                    break;
                case R.id.reject_btn:

                    rejectBtnClick();
                    break;
                default:
                    break;
            }
        }
    });

    public static WelcomeFragment newInstance() {

        Bundle args = new Bundle();

        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_welcome, null);

        driverNameView = view.findViewById(R.id.driver_name);
        carrierNameView = view.findViewById(R.id.carrier_name);

        confirmBtn = view.findViewById(R.id.confirm_btn);
        rejectBtn = view.findViewById(R.id.reject_btn);

        confirmBtn.setOnClickListener(clickProxy);
        rejectBtn.setOnClickListener(clickProxy);

        ((TextView) view.findViewById(R.id.version)).setText(getString(R.string.version)
                .replace("#version#", AppUtil.getVersionName(getActivity())));

        loadingDialog = new LoadingDialog(getContext());

        this.presenter.getUserInfo();

        return view;
    }

    @Override
    protected void initStatusBar() {

        setStatusBar(R.color.white, true);
    }

    @Override
    public void setPresenter(WelcomeContract.Presenter presenter) {

        this.presenter = presenter;
    }

    @Override
    public void setUserInfo(String carrierName, String driverName) {

        carrierNameView.setText(getResources().getString(R.string.welcome_carrier_name).replace("#carrierName#", carrierName));
        driverNameView.setText(driverName);
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
    public void loginFailed(final int code, final String msg) {

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
                                    presenter.login(true);
                                }
                            })
                            .build()
                            .show();
                }
            }
        });
    }

    @Override
    public void loadSuccess() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent intent = PrepareActivity.newIntent(getActivity(), PrepareActivity.ORIGIN_WELCOME);
                startActivity(intent);

                getActivity().finish();
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
    public void setUpdatePresenter(UpdateContract.Presenter presenter) {

        this.updatePresenter = presenter;
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
                                presenter.login(false);
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

            presenter.login(false);
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
                            presenter.login(false);
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

    private void confirmBtnClick() {

        showLoading();
        updatePresenter.checkVersion();
    }

    private void rejectBtnClick() {

        Intent intent = LoginActivity.newIntent(getActivity());
        startActivity(intent);

        getActivity().finish();
    }
}
