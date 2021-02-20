package com.unitedbustech.eld.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.unitedbustech.eld.util.ScreenUtil;

/**
 * @author yufei0213
 * @date 2018/1/27
 * @description 用于解决webview键盘遮挡
 */
public class AssistActivity {

    private int navigationBarHeight;

    private View childOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    public AssistActivity(Activity activity) {

        navigationBarHeight = ScreenUtil.getNavigationBarHeight(activity);

        FrameLayout content = activity.findViewById(android.R.id.content);
        childOfContent = content.getChildAt(0);
        if (childOfContent == null) {

            return;
        }
        childOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                childOfContent.getWindowVisibleDisplayFrame(r);
                int usableHeightNow = r.bottom - r.top;

                if (usableHeightNow != usableHeightPrevious) {

                    int usableHeightSansKeyboard = childOfContent.getRootView().getHeight();
                    int heightDifference = usableHeightSansKeyboard - usableHeightNow;
                    if (heightDifference > (usableHeightSansKeyboard / 4)) {

                        frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                    } else {

                        frameLayoutParams.height = usableHeightSansKeyboard - navigationBarHeight;
                    }

                    childOfContent.requestLayout();
                    usableHeightPrevious = usableHeightNow;
                }
            }
        });

        frameLayoutParams = (FrameLayout.LayoutParams) childOfContent.getLayoutParams();
    }
}
