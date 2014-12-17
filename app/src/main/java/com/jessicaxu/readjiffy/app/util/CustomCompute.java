package com.jessicaxu.ReadJiffy.app.util;

import android.content.ContentValues;

/**
 * 这个类用于处理各种计算和转换。
 */
public class CustomCompute {

    /*
     *从NavigationDrawer的选中位置获取数据库的表名
     */
    public static String getTableName(int position) {
        TraceLog.printEntrance("getTableName");
        String rv = "";

        switch(position) {
        case MetaData.CONTENT_POSITION_READING:
            rv = MetaData.SQLite_TABLE_READING;
            break;
        case MetaData.CONTENT_POSITION_WANT:
            rv = MetaData.SQLite_TABLE_WANT;
            break;
        case MetaData.CONTENT_POSITION_FINISHED:
            rv = MetaData.SQLite_TABLE_FINISHED;
            break;
        default:
            System.out.println("unexpected position: "+position);
            break;
        }

        TraceLog.printExit("getTableName");
        return rv;
    }

    /*
     *将BookInfo封装到ContentValues中
     */
    public static ContentValues getContentValues(BookInfo bookInfo) {
        TraceLog.printEntrance("getContentValues");
        ContentValues contentValues = new ContentValues();

        contentValues.put(MetaData.KEY_BOOK_NAME, bookInfo.mBookName);
        contentValues.put(MetaData.KEY_AUTHOR, bookInfo.mAuthor);
        contentValues.put(MetaData.KEY_READ_PAGE, bookInfo.mReadPage);
        contentValues.put(MetaData.KEY_TOTAL_PAGE, bookInfo.mTotalPage);
        contentValues.put(MetaData.KEY_MINUTES, bookInfo.mMinutes);
        contentValues.put(MetaData.KEY_HOURS, bookInfo.mHours);
        contentValues.put(MetaData.KEY_TIME_STRING, bookInfo.mTimeString);
        contentValues.put(MetaData.KEY_PERCENT, bookInfo.mPercent);
        contentValues.put(MetaData.KEY_TIMER_SECONDS, bookInfo.mTimerSeconds);

        TraceLog.printExit("getContentValues");
        return contentValues;
    }

}
