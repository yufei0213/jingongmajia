package com.interest.calculator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.interest.calculator.R;

/**
 * @author yufei0213
 * @date 2018/1/16
 * @description TitleBar
 */
public class IftaUpdateTitleBar extends RelativeLayout implements View.OnClickListener {

    private ImageButton backBtn;
    private ImageButton deleteBtn;
    private ImageButton editBtn;

    private TextView titleView;

    private IftaUpdateTitleBarListener listener;

    public IftaUpdateTitleBar(Context context) {

        this(context, null);
    }

    public IftaUpdateTitleBar(final Context context, AttributeSet attrs) {

        super(context, attrs);

        LayoutInflater.from(context).inflate(R.layout.view_ifta_update_title_bar, this, true);

        backBtn = this.findViewById(R.id.back_btn);
        deleteBtn = this.findViewById(R.id.delete_btn);
        editBtn = this.findViewById(R.id.edit_btn);
        titleView = this.findViewById(R.id.title);

        backBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.title_bar);
        String title = typedArray.getString(R.styleable.title_bar_title);

        if (!TextUtils.isEmpty(title)) {

            setTitle(title);
        }

        typedArray.recycle();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.back_btn:

                if (listener != null) {

                    listener.onBackBtnClick();
                }
                break;
            case R.id.delete_btn:

                if (listener != null) {

                    listener.onDeleteBtnClick();
                }
                break;
            case R.id.edit_btn:

                if (listener != null) {

                    listener.onEditBtnClick();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置监听
     *
     * @param listener 监听者
     */
    public void setListener(IftaUpdateTitleBarListener listener) {

        this.listener = listener;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {

        titleView.setText(title);
    }

    /**
     * 隐藏删除和编辑按钮
     */
    public void hideDeleteAndEditBtn() {

        deleteBtn.setVisibility(GONE);
        editBtn.setVisibility(GONE);
    }

    /**
     * 设置是否哟返回按钮
     *
     * @param backAvailable 是否有返回按钮
     */
    public void setBackAvailable(boolean backAvailable) {

        if (!backAvailable) {

            this.backBtn.setVisibility(GONE);
        } else {

            this.backBtn.setVisibility(VISIBLE);
        }
    }

    public interface IftaUpdateTitleBarListener {

        void onBackBtnClick();

        void onDeleteBtnClick();

        void onEditBtnClick();
    }
}
