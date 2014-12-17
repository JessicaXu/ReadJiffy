package com.jessicaxu.ReadJiffy.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import com.jessicaxu.ReadJiffy.app.R;


public class CustomDialog {
    public View mDialogView;
    public AlertDialog mDialog;
    private MainActivity mMainActivity;

    public CustomDialog(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    /*
     *创建自定义的AlertDialog.
     */
    public void buildDialog(final int resource, String title) {
        final CustomDialogBuilder customDialogBuilder = new CustomDialogBuilder(mMainActivity);
        final AlertDialog dialog = customDialogBuilder
                .setTitle(title)
                .setCustomView(resource, mMainActivity)
                .setIcon(mMainActivity.getResources().getDrawable(R.drawable.ic_launcher))
                .setPositiveButton(R.string.dialog_ok, null)
                .setNegativeButton(R.string.dialog_cancel, null)
                .show();

        setBtnColor(dialog.getButton(AlertDialog.BUTTON_POSITIVE));
        setBtnColor(dialog.getButton(AlertDialog.BUTTON_NEGATIVE));

        mDialogView = CustomDialogBuilder.mDialogView;
        mDialog = dialog;
    }


    /*
     *将对话框按钮设置成点击变为highlight颜色
     */
    private void setBtnColor(Button button) {
        Drawable pressed_highlight = mMainActivity.getResources().getDrawable
                (R.drawable.customblue_btn_default_holo_light);

        if (Build.VERSION.SDK_INT >= 16) {
            button.setBackground(pressed_highlight);
        } else {
            button.setBackgroundDrawable(pressed_highlight);
        }
    }
}
