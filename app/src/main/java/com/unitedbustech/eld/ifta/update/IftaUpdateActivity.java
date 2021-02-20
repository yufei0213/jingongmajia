package com.unitedbustech.eld.ifta.update;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.unitedbustech.eld.activity.BaseFragmentActivity;
import com.unitedbustech.eld.common.vo.IftaDetailVo;
import com.unitedbustech.eld.mvp.BasePresenter;

/**
 * @author yufei0213
 * @date 2018/6/25
 * @description 更新IFTA的界面，包含删除和编辑
 */
public class IftaUpdateActivity extends BaseFragmentActivity {

    private static final String EXTRA_IFTA_DETAIL = "com.unitedbustech.eld.ifta.update.IftaUpdateActivity.IftaDetailVo";

    private IftaUpdateFragment fragment;

    public static Intent newIntent(Context context, @NonNull IftaDetailVo iftaDetailVo) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_IFTA_DETAIL, iftaDetailVo);

        Intent intent = new Intent(context, IftaUpdateActivity.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    protected void initVariables() {

    }

    @Override
    protected BasePresenter initPresenter() {

        return new IftaUpdatePresenter(IftaUpdateActivity.this, fragment);
    }

    @Override
    protected Fragment createFragment() {

        IftaDetailVo iftaDetailVo = getIntent().getParcelableExtra(EXTRA_IFTA_DETAIL);
        fragment = IftaUpdateFragment.newInstance(iftaDetailVo);
        return fragment;
    }
}
