package com.unitedbustech.eld.exemption;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.MainActivity;
import com.unitedbustech.eld.common.UserFunction;
import com.unitedbustech.eld.fragment.BaseFragment;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.view.ClickProxy;

/**
 * @author yufei0213
 * @date 2018/2/8
 * @description 豁免模式选择界面
 */
public class ExemptionFragment extends BaseFragment {

    private Button exemptionDaysBtn;
    private Button exemptionMilesBtn;

    private ClickProxy clickProxy = new ClickProxy(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {

                case R.id.exemption_days_btn:

                    SystemHelper.setExemptionFunc(UserFunction.DAYS_EXEMPTION);
                    break;
                case R.id.exemption_miles_btn:

                    SystemHelper.setExemptionFunc(UserFunction.MILES_EXEMPTION);
                    break;
                default:
                    break;
            }

            Intent intent = MainActivity.newIntent(getContext());
            startActivity(intent);
            getActivity().finish();
        }
    });

    public static ExemptionFragment newInstance() {

        Bundle args = new Bundle();

        ExemptionFragment fragment = new ExemptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_exempiton, null);

        this.exemptionDaysBtn = view.findViewById(R.id.exemption_days_btn);
        this.exemptionMilesBtn = view.findViewById(R.id.exemption_miles_btn);

        this.exemptionDaysBtn.setOnClickListener(clickProxy);
        this.exemptionMilesBtn.setOnClickListener(clickProxy);

        return view;
    }
}
