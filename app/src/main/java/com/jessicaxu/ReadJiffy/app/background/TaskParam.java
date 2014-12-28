package com.jessicaxu.ReadJiffy.app.background;

import android.content.ContentValues;

public class TaskParam {
    public final ContentValues mContentValues;
    public final int mOperation;
    public final String mTableName;
    public final String mKeyCol;
    public final String mKeyValue;
    public final String mOrderBy;

    public TaskParam(ContentValues contentValues, int operation, String tableName,
                     String keyCol, String keyValue, String orderBy) {
        mContentValues = contentValues;
        mOperation = operation;
        mTableName = tableName;
        mKeyCol = keyCol;
        mKeyValue = keyValue;
        mOrderBy = orderBy;
    }
}