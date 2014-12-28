package com.jessicaxu.ReadJiffy.app.ui;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import com.jessicaxu.ReadJiffy.app.R;

public class CustomDialog{
    public View mDialogView;
    public AlertDialog mDialog;
    public MainActivity mActivity;

    public CustomDialog(MainActivity activity){
        mActivity = activity;
    }

    /*
     *创建自定义的AlertDialog.
     */
    public CustomDialog buildDialog(final int resource, String title) {
        final CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(mActivity);

        customDialogBuilder.setCustomView(resource, mActivity);
        customDialogBuilder.setTitle(title);
        customDialogBuilder.setIcon(mActivity.getResources().getDrawable(R.drawable.ic_launcher));
        customDialogBuilder.setPositiveButton(R.string.dialog_ok, null);
        customDialogBuilder.setNegativeButton(R.string.dialog_cancel, null);
        final AlertDialog dialog = customDialogBuilder.show();

        setBtnColor(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
        setBtnColor(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));

        mDialogView = CustomDialogBuilder.mDialogView;
        mDialog = dialog;
        return this;
    }


    /*
     *将对话框按钮设置成点击变为highlight颜色
     */
    private void setBtnColor(Button button) {
        Drawable pressed_highlight = mActivity.getResources().getDrawable
                (R.drawable.customblue_btn_default_holo_light);

        if (Build.VERSION.SDK_INT >= 16) {
            button.setBackground(pressed_highlight);
        } else {
            button.setBackgroundDrawable(pressed_highlight);
        }
    }
}