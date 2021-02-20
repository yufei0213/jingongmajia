package com.unitedbustech.eld.prepare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.MainActivity;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.common.UserFunction;
import com.unitedbustech.eld.exemption.ExemptionActivity;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.view.ClickProxy;

/**
 * @author yufei0213
 * @date 2018/1/28
 * @description PrepareFragment
 */
public class PrepareFragment extends BaseFragment implements PrepareContract.View {

    private static final String ARG_ORIGIN = "com.unitedbustech.eld.prepare.preparefragment.origin";

    public static final String ORIGIN_LOGIN = "origin";
    public static final String ORIGIN_WELCOME = "welcome";

    private Animation loadingAnim;

    private TextView driverNameView;
    private TextView carrierNameView;
    private ImageView loadingView;

    private ImageView logoImg;

    private Button tryBtn;

    private View loadingPage;

    private PrepareContract.Presenter presenter;

    private ClickProxy clickProxy = new ClickProxy(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.try_btn) {

                showLoadingPage();

                startLoading();
                PrepareFragment.this.presenter.loadData();
            }
        }
    });

    public static PrepareFragment newInstance(String origin) {

        Bundle args = new Bundle();

        args.putString(ARG_ORIGIN, origin);

        PrepareFragment fragment = new PrepareFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables() {

        loadingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.loading_prepare_anim);
        LinearInterpolator lin = new LinearInterpolator();
        loadingAnim.setInterpolator(lin);
    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prepare, null);

        loadingPage = view.findViewById(R.id.loading_page);

        driverNameView = view.findViewById(R.id.driver_name);
        carrierNameView = view.findViewById(R.id.carrier_name);
        loadingView = view.findViewById(R.id.loading_icon);

        logoImg = view.findViewById(R.id.logo);

        String origin = getArguments().getString(ARG_ORIGIN);
        if (origin.equals(ORIGIN_LOGIN)) {

            logoImg.setImageResource(R.drawable.bg_login_logo);
        } else if (origin.equals(ORIGIN_WELCOME)) {

            logoImg.setImageResource(R.drawable.ic_emoji_hi);
        }

        tryBtn = view.findViewById(R.id.try_btn);
        tryBtn.setOnClickListener(clickProxy);

        startLoading();
        this.presenter.getUserInfo();

        this.presenter.loadData();

        return view;
    }

    @Override
    public void setPresenter(PrepareContract.Presenter presenter) {

        this.presenter = presenter;
    }

    @Override
    public void showLoadingPage() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingPage.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideLoadingPage() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingPage.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void setUserInfo(final String carrierName, final String driverName) {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                carrierNameView.setText(getResources().getString(R.string.welcome_carrier_name).replace("#carrierName#", carrierName));
                driverNameView.setText(driverName);
            }
        });
    }

    @Override
    public void startLoading() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingView.startAnimation(loadingAnim);
            }
        });
    }

    @Override
    public void endLoading() {

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                loadingView.clearAnimation();
            }
        });
    }

    @Override
    public void loadDataFinish(boolean success) {

        if (success) {

            User user = SystemHelper.getUser();
            switch (user.getFunction()) {

                case UserFunction.NORMAL:

                    SystemHelper.setExemptionFunc(UserFunction.NORMAL);
                    startActivity(MainActivity.newIntent(getActivity()));
                    getActivity().finish();
                    break;
                case UserFunction.ALL:

                    SystemHelper.setExemptionFunc(UserFunction.ALL);
                    startActivity(ExemptionActivity.newIntent(getActivity()));
                    getActivity().finish();
                    break;
                case UserFunction.DAYS_EXEMPTION:

                    SystemHelper.setExemptionFunc(UserFunction.DAYS_EXEMPTION);
                    startActivity(MainActivity.newIntent(getActivity()));
                    getActivity().finish();
                    break;
                case UserFunction.MILES_EXEMPTION:

                    SystemHelper.setExemptionFunc(UserFunction.MILES_EXEMPTION);
                    startActivity(MainActivity.newIntent(getActivity()));
                    getActivity().finish();
                    break;
                default:
                    break;
            }
        } else {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    endLoading();
                    hideLoadingPage();
                }
            });
        }
    }
}
