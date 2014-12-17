package com.jessicaxu.ReadJiffy.app.util;

import android.app.Activity;

public class TaskParam {
    public final BookInfo mBookInfo;
    public final String mOperation;
    public final Activity mActivity;
    public final String mTableName;

    public TaskParam(BookInfo bookInfo, String operation, String tableName, Activity activity) {
        mBookInfo = bookInfo;
        mOperation = operation;
        mTableName = tableName;
        mActivity = activity;
    }
}