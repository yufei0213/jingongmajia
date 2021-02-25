package com.interest.calculator.view;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.interest.calculator.R;
import com.interest.calculator.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yufei0213
 * @date 2018/3/17
 * @description RemarkListDialog
 */
public class RemarkListDialog extends AppCompatDialog {

    private List<String> remarkList;

    private OnRemarkItemClickListener listener;

    private ClickProxy clickProxy = new ClickProxy(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            TextView textView = (TextView) v;
            if (listener != null) {

                listener.onRemarkItemClick(textView.getText().toString());
                dismiss();
            }
        }
    });

    public RemarkListDialog(Context context, Builder builder) {

        super(context);

        this.remarkList = builder.remarkList;
        this.listener = builder.listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_remark);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //设置dialog的宽度为屏幕的宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        getWindow().setAttributes(layoutParams);

        //初始化视图
        initView();
    }

    private void initView() {

        LinearLayout container = this.findViewById(R.id.container);

        int size = remarkList.size();
        for (int i = 0; i < size; i++) {

            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_dialog_remark, null);
            textView.setText(remarkList.get(i));
            textView.setOnClickListener(clickProxy);

            container.addView(textView);

            if (i < size - 1) {

                View lineView = LayoutInflater.from(getContext()).inflate(R.layout.line_dialog_remark, null);
                container.addView(lineView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(getContext(), 1)));
            }
        }
    }

    public interface OnRemarkItemClickListener {

        void onRemarkItemClick(String remark);
    }

    public static class Builder {

        private Context context;

        private List<String> remarkList;
        private OnRemarkItemClickListener listener;

        public Builder(Context context) {

            this.context = context;

            this.remarkList = new ArrayList<>();
        }

        public Builder addRemark(String... remarks) {

            for (String remark : remarks) {

                this.remarkList.add(remark);
            }

            return this;
        }

        public Builder listener(OnRemarkItemClickListener listener) {

            this.listener = listener;
            return this;
        }

        public RemarkListDialog build() {

            return new RemarkListDialog(context, this);
        }
    }
}
