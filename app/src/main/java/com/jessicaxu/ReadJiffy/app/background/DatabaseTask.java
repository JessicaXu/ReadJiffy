package com.jessicaxu.ReadJiffy.app.background;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.jessicaxu.ReadJiffy.app.data.BookCP;
import com.jessicaxu.ReadJiffy.app.data.MetaData;

public class DatabaseTask extends AsyncTask<TaskParam, Integer, Cursor> {
    @Override
    protected Cursor doInBackground(TaskParam... taskParam) {

        Cursor cursor = null;
        Activity activity = taskParam[0].mActivity;
        ContentValues contentValues = taskParam[0].mContentValues;
        String operation = taskParam[0].mOperation;
        String tableName = taskParam[0].mTableName;
        String keyCol = taskParam[0].mKeyCol;
        String keyValue = taskParam[0].mKeyValue;
        String orderBy = taskParam[0].mOrderBy;


        Uri uri = BookCP.getContentUri(tableName);
        String[] sKeyValue = {keyValue};

        if(operation.equals(MetaData.OPERATION_QUERY)) {
            cursor = activity.getContentResolver().query(
                    uri,
                    null,
                    keyCol + "= ?",
                    sKeyValue,
                    orderBy);
        } else if(operation.equals(MetaData.OPERATION_INSERT)) {
            activity.getContentResolver().insert(
                    uri,
                    contentValues);
        } else if(operation.equals(MetaData.OPERATION_UPDATE)) {
            activity.getContentResolver().update(
                    uri,
                    contentValues,
                    keyCol + " = ?",
                    sKeyValue);
        } else if(operation.equals(MetaData.OPERATION_DELETE)) {
            activity.getContentResolver().delete(
                    uri,
                    keyCol + " = ?",
                    sKeyValue);
        } else {
            throw new IllegalArgumentException("illegal_argument");
        }

        return cursor;
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        //处理查询的情况
        if(cursor != null) {
            cursor.close();
        }
    }

    protected void onPreExecute() {}
    protected void onProgressUpdate(Integer... arg1) {}
    protected void onCancelled() {}
}