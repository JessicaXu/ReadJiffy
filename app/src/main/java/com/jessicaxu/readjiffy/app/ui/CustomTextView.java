package com.jessicaxu.readjiffy.app.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.jessicaxu.readjiffy.app.util.TraceLog;

public class CustomTextView extends TextView {
    private final String CLASSNAME = "CustomTextView";
    private TraceLog mTraceLog = new TraceLog(CLASSNAME);

    public CustomTextView(Context context) {

        super(context);
        setFont();
    }
    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        mTraceLog.printEntrance("setFont");
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "tangshi.ttf");
        setTypeface(font, Typeface.BOLD);
        mTraceLog.printExit("setFont");
    }
}