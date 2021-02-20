package com.unitedbustech.eld.dot;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.unitedbustech.eld.R;
import com.unitedbustech.eld.activity.BaseActivity;
import com.unitedbustech.eld.common.User;
import com.unitedbustech.eld.system.SystemHelper;
import com.unitedbustech.eld.view.ClickProxy;
import com.unitedbustech.eld.view.DotExitVerifyDialog;
import com.unitedbustech.eld.view.TabItemTitleBar;
import com.unitedbustech.eld.view.TabMenu;
import com.unitedbustech.eld.view.TabMenuDot;

/**
 * @author mamw
 * @date 2017/01/24
 * @description DotActivity
 */
public class DotActivity extends BaseActivity implements TabMenuDot.TabMenuSelectedListener {

    private Fragment currentFragment;
    private Fragment dotReviewFragment;
    private Fragment dotSendFragment;

    private TextView exitBtn;
    private TabItemTitleBar titleBar;
    private TabMenuDot tabMenu;

    public static Intent newIntent(Context context) {

        Intent intent = new Intent(context, DotActivity.class);

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
    protected View onCreateView(Bundle savedInstanceState) {

        View view = LayoutInflater.from(this).inflate(R.layout.activity_dot, null);

        titleBar = view.findViewById(R.id.title_bar);
        exitBtn = titleBar.getRightTextView();
        exitBtn.setOnClickListener(new ClickProxy(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DotExitVerifyDialog.Builder(DotActivity.this)
                        .setNegativeBtn(new DotExitVerifyDialog.OnClickListener() {
                            @Override
                            public void onClick(DotExitVerifyDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setNeutralBtn(new DotExitVerifyDialog.OnClickListener() {
                            @Override
                            public void onClick(DotExitVerifyDialog dialog) {

                                String passport = dialog.getPassword();
                                User user = SystemHelper.getUser();
                                if (user.getPassword().equals(passport)) {

                                    dialog.dismiss();
                                    openMainPage(TabMenu.DASHBORAD);
                                } else {

                                    dialog.setPasswordWaring();
                                }
                            }
                        })
                        .build()
                        .show();
            }
        }));

        this.tabMenu = view.findViewById(R.id.tab_menu);
        tabMenu.setListener(this);

        tabMenu.setSelectedItem(TabMenuDot.TAB_REVIEW);

        return view;
    }

    @Override
    public void onTabItemSelected(int index) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (index) {

            case TabMenuDot.TAB_REVIEW:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (dotReviewFragment == null) {

                    dotReviewFragment = DotReviewFragment.newInstance();
                    ft.add(R.id.fragment_container, dotReviewFragment);
                } else {

                    ft.show(dotReviewFragment);
                }

                currentFragment = dotReviewFragment;
                break;

            case TabMenuDot.TAB_SEND:

                if (currentFragment != null) {

                    ft.hide(currentFragment);
                }

                if (dotSendFragment == null) {

                    dotSendFragment = DotSendFragment.newInstance();
                    ft.add(R.id.fragment_container, dotSendFragment);
                } else {

                    ft.show(dotSendFragment);
                }

                currentFragment = dotSendFragment;
                break;

            default:
                break;
        }

        ft.commit();
    }

    @Override
    public void onBackPressed() {

//        super.onBackPressed();
    }

    public void onTabSelect(int tabIndex) {

        tabMenu.setSelectedItem(TabMenuDot.TAB_REVIEW);
    }
}
