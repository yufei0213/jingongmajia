package com.unitedbustech.eld.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.unitedbustech.eld.R;

/**
 * @author yufei0213
 * @date 2018/1/22
 * @description 自定义字体的TextView
 */
public class FontTextView extends AppCompatTextView {

    public FontTextView(Context context) {

        this(context, null);
    }

    public FontTextView(Context context, @Nullable AttributeSet attrs) {

        this(context, attrs, android.R.attr.textViewStyle);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.text_font);

        String fontType = typedArray.getString(R.styleable.text_font_fontType);

        if (TextUtils.isEmpty(fontType)) {

            fontType = FontView.REGULAR;
        }

        switch (fontType) {

            case FontView.REGULAR:

                super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/SFUIText-Regular.ttf"));
                break;
            case FontView.MEDIUM:

                super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/SFUIText-Medium.ttf"));
                break;
            case FontView.BLOD:

                super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/SFUIText-Bold.ttf"));
                break;
            default:

                super.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "font/SFUIText-Regular.ttf"));
                break;
        }

        typedArray.recycle();
    }
}
