package com.jessicaxu.ReadJiffy.app.background;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.data.BookCP;
import com.jessicaxu.ReadJiffy.app.global.CustomApplication;
import com.jessicaxu.ReadJiffy.app.global.MetaData;

public class DatabaseTask extends AsyncTask<TaskParam, Integer, Cursor> {
    public static ResultTask mResultTask = null;
    private Cursor mCursor = null;
    private Context mContext;
    private ContentValues mContentValues;
    private int mOperation;
    private String mKeyCol;
    private String mOrderBy;
    private Uri mUri;
    private String[] mStrKeyValue;

    @Override
    protected Cursor doInBackground(TaskParam... taskParam) {
        parseParam(taskParam);

        switch (mOperation){
            case MetaData.QUERY:
            case MetaData.QUERY_AND_INSERT:
            case MetaData.QUERY_AND_UPDATE:
                mCursor = mContext.getContentResolver().query(
                        mUri,
                        null,
                        mKeyCol + "= ?",
                        mStrKeyValue,
                        mOrderBy);
                break;
            case MetaData.UPDATE:
                mContext.getContentResolver().update(
                        mUri,
                        mContentValues,
                        mKeyCol + " = ?",
                        mStrKeyValue);
            case MetaData.INSERT:
                mContext.getContentResolver().insert(
                        mUri,
                        mContentValues);
                break;
            case MetaData.DELETE:
                mContext.getContentResolver().delete(
                        mUri,
                        mKeyCol + " = ?",
                        mStrKeyValue);
                break;
            default:
                throw new IllegalArgumentException(mContext.getString(R.string.illegal_argument));
        }

        return mCursor;
    }
    
    private void parseParam(TaskParam... taskParam){
        mContext = CustomApplication.getContext();
        mContentValues = taskParam[0].mContentValues;
        mOperation = taskParam[0].mOperation;
        mKeyCol = taskParam[0].mKeyCol;
        mOrderBy = taskParam[0].mOrderBy;
        mUri = BookCP.getContentUri(taskParam[0].mTableName);
        mStrKeyValue = new String[]{taskParam[0].mKeyValue};
    }

    @Override
    protected void onPostExecute(Cursor cursor) {
        //处理查询的情况
        if(mCursor != null) {
            if((mOperation == MetaData.QUERY_AND_INSERT) ||
                    (mOperation == MetaData.QUERY_AND_UPDATE)){
                mResultTask.doResultTask(mCursor, mOperation);
            }
            mCursor.close();
        }
    }

    protected void onPreExecute() {}
    protected void onProgressUpdate(Integer... arg1) {}
    protected void onCancelled() {}

    public interface ResultTask{
        public void doResultTask(Cursor cursor, int operation);
    }
}