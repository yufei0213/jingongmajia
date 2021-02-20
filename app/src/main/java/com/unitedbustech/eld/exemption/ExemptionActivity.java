package com.unitedbustech.eld.exemption;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.mvp.BasePresenter;

/**
 * @author yufei0213
 * @date 2018/2/8
 * @description 豁免模式选择界面
 */
public class ExemptionActivity extends BaseFragmentActivity {

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, ExemptionActivity.class);

        intent.putExtra(EXTRA_ANIM_IN_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_IN_OUT_ID, R.anim.fade_out);
        intent.putExtra(EXTRA_ANIM_OUT_IN_ID, R.anim.fade_in);
        intent.putExtra(EXTRA_ANIM_OUT_OUT_ID, R.anim.fade_out);

        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected BasePresenter initPresenter() {
        return null;
    }

    @Override
    protected Fragment createFragment() {

        return ExemptionFragment.newInstance();
    }
}
