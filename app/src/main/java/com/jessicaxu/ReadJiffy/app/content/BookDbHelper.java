package com.jessicaxu.ReadJiffy.app.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jessicaxu.ReadJiffy.app.util.MetaData;
import com.jessicaxu.ReadJiffy.app.util.TraceLog;

class BookDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "BookDbHelper";

    //创建“在读”数据库表
    private static final String DATABASE_CREATE_READING =
        "CREATE TABLE if not exists " +
        MetaData.SQLite_TABLE_READING + " (" +
        MetaData.KEY_ROW_ID + " integer PRIMARY KEY autoincrement," +
        MetaData.KEY_BOOK_NAME + "," +
        MetaData.KEY_AUTHOR + "," +
        MetaData.KEY_READ_PAGE + "," +
        MetaData.KEY_TOTAL_PAGE + "," +
        MetaData.KEY_MINUTES + "," +
        MetaData.KEY_HOURS + "," +
        MetaData.KEY_TIME_STRING + "," +
        MetaData.KEY_PERCENT + "," +
        MetaData.KEY_TIMER_SECONDS + "," +
        " UNIQUE (" + MetaData.KEY_BOOK_NAME +"));";
    //创建“读完”数据库表
    private static final String DATABASE_CREATE_FINISHED =
        "CREATE TABLE if not exists " +
        MetaData.SQLite_TABLE_FINISHED + " (" +
        MetaData.KEY_ROW_ID + " integer PRIMARY KEY autoincrement," +
        MetaData.KEY_BOOK_NAME + "," +
        MetaData.KEY_AUTHOR + "," +
        MetaData.KEY_READ_PAGE + "," +
        MetaData.KEY_TOTAL_PAGE + "," +
        MetaData.KEY_MINUTES + "," +
        MetaData.KEY_HOURS + "," +
        MetaData.KEY_TIME_STRING + "," +
        MetaData.KEY_PERCENT + "," +
        MetaData.KEY_TIMER_SECONDS + "," +
        " UNIQUE (" + MetaData.KEY_BOOK_NAME +"));";
    //创建“想读”数据库表
    private static final String DATABASE_CREATE_WANT =
        "CREATE TABLE if not exists " +
        MetaData.SQLite_TABLE_WANT + " (" +
        MetaData.KEY_ROW_ID + " integer PRIMARY KEY autoincrement," +
        MetaData.KEY_BOOK_NAME + "," +
        MetaData.KEY_AUTHOR + "," +
        MetaData.KEY_READ_PAGE + "," +
        MetaData.KEY_TOTAL_PAGE + "," +
        MetaData.KEY_MINUTES + "," +
        MetaData.KEY_HOURS + "," +
        MetaData.KEY_TIME_STRING + "," +
        MetaData.KEY_PERCENT + "," +
        MetaData.KEY_TIMER_SECONDS + "," +
        " UNIQUE (" + MetaData.KEY_BOOK_NAME +"));";

    BookDbHelper(Context context) {
        super(context, MetaData.DB_NAME, null, MetaData.DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        TraceLog.printEntrance("onCreate");
        Log.w(TAG, DATABASE_CREATE_READING);
        db.execSQL(DATABASE_CREATE_READING);

        Log.w(TAG, DATABASE_CREATE_FINISHED);
        db.execSQL(DATABASE_CREATE_FINISHED);

        Log.w(TAG, DATABASE_CREATE_WANT);
        db.execSQL(DATABASE_CREATE_WANT);
        TraceLog.printExit("onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TraceLog.printEntrance("onUpgrade");
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
              + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + MetaData.SQLite_TABLE_READING);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + MetaData.SQLite_TABLE_FINISHED);
        onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + MetaData.SQLite_TABLE_WANT);
        onCreate(db);
        TraceLog.printExit("onUpgrade");
    }
}