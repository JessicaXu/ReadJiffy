package com.jessicaxu.ReadJiffy.app.background;

import android.app.Activity;
import android.content.ContentValues;

public class TaskParam {
    public final Activity mActivity;
    public final ContentValues mContentValues;
    public final String mOperation;
    public final String mTableName;
    public final String mKeyCol;
    public final String mKeyValue;
    public final String mOrderBy;

    public TaskParam(ContentValues contentValues, String operation, String tableName,
                     Activity activity, String keyCol, String orderBy, String keyValue) {
        mActivity = activity;
        mContentValues = contentValues;
        mOperation = operation;
        mTableName = tableName;
        mKeyCol = keyCol;
        mKeyValue = keyValue;
        mOrderBy = orderBy;
    }
}