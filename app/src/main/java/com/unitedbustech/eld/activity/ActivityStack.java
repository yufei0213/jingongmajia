package com.unitedbustech.eld.activity;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.unitedbustech.eld.prepare.PrepareActivity;
import com.unitedbustech.eld.util.LanguageUtil;

import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * @author yufei0213
 * @date 2017/10/20
 * @description activity栈
 */
public final class ActivityStack {

    private static final String TAG = "ActivityStack";

    /**
     * app启动时，activity的数目
     */
    private static final int COUNT_APP_CREATE = 1;

    /**
     * app退出时，activity的数目
     */
    private static final int COUNT_APP_DESTROY = 0;

    /**
     * 当前是否在服务期间
     */
    private boolean isService;

    /**
     * ActivityStack监听
     */
    private ActivityStackListener activityStackListener;

    /**
     * activity栈
     */
    private Stack<Activity> activityList;

    /**
     * 私有构造函数
     */
    private ActivityStack() {

        activityList = new Stack<>();
    }

    /**
     * ActivityStack实例
     */
    private static class ActivityStackHolder {

        private static final ActivityStack INSTANCE = new ActivityStack();
    }

    /**
     * 获取ActivityStack
     *
     * @return ActivityStack
     */
    public static ActivityStack getInstance() {

        return ActivityStackHolder.INSTANCE;
    }

    /**
     * 当前是否在服务期
     *
     * @return 当前是否在服务期
     */
    public boolean isService() {

        return isService;
    }

    /**
     * 设置监听
     *
     * @param activityStackListener ActivityStackListener
     */
    public void setActivityStackListener(ActivityStackListener activityStackListener) {

        this.activityStackListener = activityStackListener;
    }

    /**
     * 获取当前activity
     *
     * @return Activity
     */
    public Activity getCurrentActivity() {

        Activity activity = null;

        try {

            activity = activityList.peek();
        } catch (NoSuchElementException e) {

            e.printStackTrace();
        }

        return activity;
    }

    /**
     * 获取指定的Activity
     *
     * @param activityClass ActivityClass
     * @return
     */
    public Activity getActivity(Class<? extends Activity> activityClass) {

        Activity result = null;
        for (Activity activity : activityList) {

            if (activity.getClass().equals(activityClass)) {

                result = activity;
                break;
            }
        }

        return result;
    }

    /**
     * 向栈中添加activity
     *
     * @param activity Activity
     */
    public void addActivity(@NonNull Activity activity) {

        activityList.push(activity);

        if (activityList.size() == COUNT_APP_CREATE && activityStackListener != null) {

            activityStackListener.onAppCreate();
        }

        if (activity.getClass().equals(PrepareActivity.class) && activityStackListener != null) {

            isService = true;
            activityStackListener.onServiceStart();
        }
    }

    /**
     * 从栈中移除activity
     *
     * @param activity Activity
     */
    public void removeActivity(@NonNull Activity activity) {

        if (getActivity(activity.getClass()) == null) {

            return;
        }
        activityList.remove(activity);

        if (activity.getClass().equals(MainActivity.class) && activityStackListener != null) {

            if (!LanguageUtil.getInstance().isChangingLanguage()) {
                isService = false;
                activityStackListener.onServiceStop();
            }
        }

        if (activityList.size() == COUNT_APP_DESTROY && activityStackListener != null) {

            activityStackListener.onAppDestroy();
        }
    }

    /**
     * 遍历index个Activity并销毁
     *
     * @param index activity个数 最大值为-1
     */
    public void finishActivityToIndex(int index) {

        int size = 0 - index;
        size = size >= activityList.size() ? activityList.size() - 1 : size;
        while (size > 0) {

            Activity activity = activityList.get(activityList.size() - 1);
            activity.finish();
            this.removeActivity(activity);
            size--;
        }
    }

    /**
     * 关闭指定activity
     *
     * @param activityClass Activity.class
     */
    public void finishActivity(@NonNull Class<? extends Activity> activityClass) {

        for (Activity activity : activityList) {

            if (activity.getClass().equals(activityClass)) {

                activity.finish();
                break;
            }
        }
    }

    /**
     * 关闭除指定activity外的其他activity
     *
     * @param activityClass Activity.class
     */
    public void finishOthers(@NonNull Class<? extends Activity> activityClass) {

        for (Activity activity : activityList) {

            if (!activity.getClass().equals(activityClass)) {

                activity.finish();
            }
        }
    }

    /**
     * 关闭全部activity
     */
    public void finishAll() {

        for (Activity activity : activityList) {

            activity.finish();
        }
    }
}
