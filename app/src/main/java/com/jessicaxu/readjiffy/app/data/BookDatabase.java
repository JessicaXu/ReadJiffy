package com.jessicaxu.readjiffy.app.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jessicaxu.readjiffy.app.util.TraceLog;

/*
* because SQLiteDatabase is a final class,
* so it's not allowed to extends SQLiteDatabase.
 */
public class BookDatabase{

    public static final String KEY_ROWID = "_id";
    public static final String KEY_BOOKNAME = "BookName";
    public static final String KEY_AUTHOR = "Author";
    public static final String KEY_PERCENT = "Percent";
    public static final String KEY_TIME = "Time";
    public static final String KEY_STATUS = "Status";

    private static final String TAG = "BookDbAdapter";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    //static是数据库的相关操作要求的，一定要这么用。
    private static final String DATABASE_NAME = "Books";
    private static final String SQLITE_TABLE_READING = "Reading";
    private static final String SQLITE_TABLE_FINISHED = "Finished";
    private static final String SQLITE_TABLE_WANT = "Want";
    private static final int DATABASE_VERSION = 1;

    private static final String CLASSNAME = "BookDatabase";
    private static TraceLog mTraceLog = new TraceLog(CLASSNAME);

    private final Context mCtx;

    //创建“在读”数据库表
    private static final String DATABASE_CREATE_READING =
            "CREATE TABLE if not exists " + SQLITE_TABLE_READING + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_BOOKNAME + "," +
                    KEY_AUTHOR + "," +
                    KEY_STATUS + "," +
                    KEY_PERCENT + "," +
                    KEY_TIME + "," +
                    " UNIQUE (" + KEY_BOOKNAME +"));";
    //创建“读完”数据库表
    private static final String DATABASE_CREATE_FINISHED =
            "CREATE TABLE if not exists " + SQLITE_TABLE_FINISHED + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_BOOKNAME + "," +
                    KEY_AUTHOR + "," +
                    KEY_STATUS + "," +
                    KEY_PERCENT + "," +
                    KEY_TIME + "," +
                    " UNIQUE (" + KEY_BOOKNAME +"));";
    //创建“想读”数据库表
    private static final String DATABASE_CREATE_WANT =
            "CREATE TABLE if not exists " + SQLITE_TABLE_WANT + " (" +
                    KEY_ROWID + " integer PRIMARY KEY autoincrement," +
                    KEY_BOOKNAME + "," +
                    KEY_AUTHOR + "," +
                    KEY_STATUS + "," +
                    KEY_PERCENT + "," +
                    KEY_TIME + "," +
                    " UNIQUE (" + KEY_BOOKNAME +"));";

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            mTraceLog.printEntrance("DatabaseHelper:onCreate");
            Log.w(TAG, DATABASE_CREATE_READING);
            db.execSQL(DATABASE_CREATE_READING);

            Log.w(TAG, DATABASE_CREATE_FINISHED);
            db.execSQL(DATABASE_CREATE_FINISHED);

            Log.w(TAG, DATABASE_CREATE_WANT);
            db.execSQL(DATABASE_CREATE_WANT);
            mTraceLog.printExit("DatabaseHelper:onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            mTraceLog.printEntrance("DatabaseHelper:onUpgrade");
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_READING);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_FINISHED);
            onCreate(db);

            db.execSQL("DROP TABLE IF EXISTS " + SQLITE_TABLE_WANT);
            onCreate(db);
            mTraceLog.printExit("DatabaseHelper:onUpgrade");
        }
    }

    public BookDatabase(Context ctx) {
        mTraceLog.printEntrance("BookDatabase");
        this.mCtx = ctx;
        mTraceLog.printExit("BookDatabase");
    }

    //打开数据库
    public BookDatabase open() throws SQLException {
        mTraceLog.printEntrance("open");
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        mTraceLog.printExit("open");
        return this;
    }

    //关闭数据库
    public void close() {
        mTraceLog.printEntrance("close");
        if (mDbHelper != null) {
            mDbHelper.close();
        }
        mTraceLog.printExit("close");
    }

    //生成一条数据
    public long insertBookItem(String tableName, BookInfo bookInfo) {
        mTraceLog.printEntrance("insertBookItem");
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_BOOKNAME, bookInfo.mBookName);
        initialValues.put(KEY_AUTHOR, bookInfo.mAuthor);
        initialValues.put(KEY_STATUS, bookInfo.mStatus);
        initialValues.put(KEY_PERCENT, bookInfo.mPercent);
        initialValues.put(KEY_TIME, bookInfo.mTime);

        long rv = mDb.insert(tableName, null, initialValues);
        mTraceLog.printExit("insertBookItem");
        return rv;
    }

    public int updateItem(String tableName, ContentValues values,
                          String whereClause, String[] whereArgs){
        mTraceLog.printEntrance("updateItem");
        int rv = mDb.update(tableName, values, whereClause, whereArgs);
        mTraceLog.printExit("updateItem");
        return rv;
    }

    //删除一本书，需要传入书名和作者
    public boolean deleteOneBook(String tableName, String bookName){
        mTraceLog.printEntrance("deleteOneBook");
        int doneDelete = 0;

        //第二和第三个参数暂时设为null，第二个参数要写删除语句，第三个参数是语句里面的参数值。
        String[] whereValue ={bookName};
        doneDelete = mDb.delete(tableName, KEY_BOOKNAME + " = ?", whereValue);
        mTraceLog.printExit("deleteOneBook");
        return doneDelete > 0;
    }

    public boolean deleteAllBooks(String tableName){
        mTraceLog.printEntrance("deleteAllBooks");
        int doneDelete = 0;

        //第二和第三个参数设为null，条件放宽到所有的数据。
        doneDelete = mDb.delete(tableName, null, null);
        mTraceLog.printExit("deleteAllBooks");
        return doneDelete > 0;
    }

    public Cursor queryOneBook(String tableName, String[] bookName){
        mTraceLog.printEntrance("queryOneBook");
        Cursor mCursor = mDb.query(tableName,
                new String[] {KEY_BOOKNAME, KEY_AUTHOR, KEY_STATUS, KEY_PERCENT, KEY_TIME},
                KEY_BOOKNAME + "= ?", bookName, null, null, null, null);
        mTraceLog.printExit("queryOneBook");
        return mCursor;
    }

    public Cursor fetchAllBooks(String tableName) {
        mTraceLog.printEntrance("fetchAllBooks");
        Cursor mCursor = mDb.query(tableName, new String[] {
                            KEY_ROWID, KEY_BOOKNAME, KEY_AUTHOR,
                            KEY_STATUS, KEY_PERCENT, KEY_TIME},
                            null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        mTraceLog.printExit("fetchAllBooks");
        return mCursor;
    }

}