package com.example.murakoshi.animationcalendarsplash;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by murakoshi on 15/11/18.
 */
public class CustomTextView extends TextView{
    private String mFont = "Let's go Digital Regular.ttf";

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getFont(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getFont(context, attrs);
        init();
    }

    public CustomTextView(Context context) {
        super(context);
        init();
    }

    /**
     * フォントファイルを読み込む
     *
     * @param context
     * @param attrs
     */
    private void getFont(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
        mFont = a.getString(R.styleable.CustomTextView_font);
        a.recycle();
    }

    /**
     * フォントを反映
     */
    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mFont);
        setTypeface(tf);
    }
}
