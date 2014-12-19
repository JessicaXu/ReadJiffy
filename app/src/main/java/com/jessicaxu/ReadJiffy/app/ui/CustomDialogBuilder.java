package com.jessicaxu.ReadJiffy.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jessicaxu.ReadJiffy.app.R;

/*
 *创建这个类来设置自定义的AlertDialog的Builder，将Dialog的标题和分割线设置成自定义的
 * 的Holo颜色。
 */
public class CustomDialogBuilder extends AlertDialog.Builder {

    /** The custom_body layout */
    public static View mDialogView;

    /** optional dialog title layout */
    private final TextView mTitle;
    /** optional alert dialog image */
    private final ImageView mIcon;
    private static final String TAG = "CustomDialogBuilder";


    public CustomDialogBuilder(Context context) {
        super(context);
        Log.d(TAG, "enter CustomDialogBuilder");
        mDialogView = View.inflate(context, R.layout.custom_dialog_layout, null);
        setView(mDialogView);

        mTitle = (TextView) mDialogView.findViewById(R.id.alertTitle);
        mIcon = (ImageView) mDialogView.findViewById(R.id.icon);
        Log.d(TAG, "leave CustomDialogBuilder");
    }

    @Override
    public CustomDialogBuilder setTitle(CharSequence text) {
        Log.d(TAG, "enter setTitle");
        mTitle.setText(text);
        Log.d(TAG, "leave setTitle");
        return this;
    }

    @Override
    public CustomDialogBuilder setIcon( Drawable icon) {
        Log.d(TAG, "enter setIcon");
        mIcon.setImageDrawable(icon);
        Log.d(TAG, "leave setIcon");
        return this;
    }

    /**
     * This allows you to specify a custom layout for the area below the title divider bar
     * in the dialog. As an example you can look at example_ip_address_layout.xml and how
     * I added it in TestDialogActivity.java
     *
     * @param resId  of the layout you would like to add
     * @param context 上下文
     */
    public CustomDialogBuilder setCustomView(int resId, Context context) {
        Log.d(TAG, "enter setCustomView");
        View customView = View.inflate(context, resId, null);
        ((FrameLayout)mDialogView.findViewById(R.id.customPanel)).addView(customView);
        Log.d(TAG, "leave setCustomView");
        return this;
    }

    @Override
    public AlertDialog show() {
        Log.d(TAG, "enter show");
        if (mTitle.getText().equals("")) mDialogView.findViewById(R.id.topPanel).setVisibility(View.GONE);
        Log.d(TAG, "leave show");
        return super.show();
    }

}
