package com.interest.calculator.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.interest.calculator.R;

/**
 * @author yufei0213
 * @date 2018/1/23
 * @description loading弹窗
 */
public class DotLoadingDialog extends AppCompatDialog {

    private Context context;

    private ImageView loadingIconView;

    private Animation loadingAnim;

    public DotLoadingDialog(@NonNull Context context) {

        super(context);

        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_loading_dot);

        //窗口与屏幕一样大
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        layoutParams.height = getWindow().getWindowManager().getDefaultDisplay().getHeight();
        getWindow().setAttributes(layoutParams);

        //设置透明背景
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        loadingIconView = this.findViewById(R.id.loading_icon);

        loadingAnim = AnimationUtils.loadAnimation(context, R.anim.loading_prepare_anim);
        LinearInterpolator lin = new LinearInterpolator();
        loadingAnim.setInterpolator(lin);
    }

    @Override
    public void show() {

        super.show();
        setCancelable(false);

        loadingIconView.startAnimation(loadingAnim);
    }

    @Override
    public void dismiss() {

        loadingIconView.clearAnimation();
        super.dismiss();
    }
}
