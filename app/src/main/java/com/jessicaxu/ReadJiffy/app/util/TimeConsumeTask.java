package com.jessicaxu.ReadJiffy.app.util;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.content.BookCP;

public class TimeConsumeTask extends AsyncTask<TaskParam, Integer, Cursor> {
    @Override
    protected Cursor doInBackground(TaskParam... taskParam) {

        Cursor cursor = null;
        BookInfo bookInfo = taskParam[0].mBookInfo;
        String operation = taskParam[0].mOperation;
        String tableName = taskParam[0].mTableName;
        Activity activity = taskParam[0].mActivity;

        Uri uri = BookCP.getContentUri(tableName);
        String[] bookName = {bookInfo.mBookName};
        ContentValues contentValues = CustomCompute.getContentValues(bookInfo);

        if(operation.equals(MetaData.OPERATION_QUERY)) {
            cursor = activity.getContentResolver().query(
                    uri,
                    null,
                    MetaData.KEY_BOOK_NAME + "= ?",
                    bookName,
                    MetaData.KEY_BOOK_NAME);
        } else if(operation.equals(MetaData.OPERATION_INSERT)) {
            activity.getContentResolver().insert(
                    uri,
                    contentValues);
        } else if(operation.equals(MetaData.OPERATION_UPDATE)) {
            activity.getContentResolver().update(
                    uri,
                    contentValues,
                    MetaData.KEY_BOOK_NAME + " = ?",
                    bookName);
        } else if(operation.equals(MetaData.OPERATION_DELETE)) {
            activity.getContentResolver().delete(
                    uri,
                    MetaData.KEY_BOOK_NAME + " = ?",
                    bookName);
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