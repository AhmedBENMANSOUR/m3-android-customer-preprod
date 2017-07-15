package com.dioolcustomer.ui;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Button;

public class WeCareButton extends Button {

    float windowHeight;
    float windowWidth;

    public WeCareButton(Context context) {
        super(context);
        getWindowDimensions(context);
        setFont();

    }

    public WeCareButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        getWindowDimensions(context);
        setFont();
    }

    public WeCareButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        getWindowDimensions(context);
        setFont();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measure = widthMeasureSpec;
        // 32px margin
        if (measure > ((windowHeight - 32) / 7)) {
            measure = (int)((windowHeight - 32) / 7);
        }
        measure = MeasureSpec.makeMeasureSpec((int)measure,
                MeasureSpec.UNSPECIFIED);
        setMeasuredDimension(measure, measure);
    }

    private void getWindowDimensions(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        windowHeight = metrics.heightPixels;
        windowWidth = metrics.widthPixels;
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Roboto-Medium.ttf");
        this.setTypeface(font);
    }

}

