package com.interest.calculator.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import androidx.appcompat.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.interest.calculator.R;

/**
 * @author yufei0213
 * @date 2018/1/22
 * @description 自定义字体的Button
 */
public class FontButton extends AppCompatButton {

    public FontButton(Context context) {

        this(context, null);
    }

    public FontButton(Context context, AttributeSet attrs) {

        this(context, attrs, androidx.appcompat.R.attr.buttonStyle);
    }

    public FontButton(Context context, AttributeSet attrs, int defStyleAttr) {

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
