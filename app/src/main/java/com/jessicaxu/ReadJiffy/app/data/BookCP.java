package com.jessicaxu.ReadJiffy.app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.jessicaxu.ReadJiffy.app.global.MetaData;

//import java.util.HashMap;

/**
 * Created by root on 14-8-4.
 * 实例化ContentProvider类，用到ContentProvider主要有以下3方面的原因：
 * 1.利用LoaderManager和CursorLoader管理Cursor的生命周期时，需要用到数据库的Uri，使用ContentProvider
 * 可以生成和管理Uri。(也有不使用ContentProvider就能使用CursorLoader的方法，这个不绝对。)
 * 2.第二轮迭代中要实现搜索的功能，提供Custom Search Suggestion，这个一定要用到ContentProvider.
 * 3.将数据备份到云端，与云端的数据进行同步，可能也需要ContentProvider.
 */
public class BookCP extends ContentProvider {

    private BookDbHelper mOpenHelper;
    private SQLiteDatabase db;
    private String mTableName;
    // UriMatcher用于检查Uri是否符合某特定的标准。
    private static UriMatcher uriMatcher;

    private static final String TAG = "BookCP";

    /*
     * 创建数据库
     */
    @Override
    public boolean onCreate() {
        Log.d(TAG, "enter onCreate");

        boolean rv = true;

        addDataUri();

        /*
         * 创建数据库
         */
        mOpenHelper = new BookDbHelper(getContext());

        // 获取权限
        db = mOpenHelper.getWritableDatabase();

        if(null == db)
            rv = false;

        Log.d(TAG, "leave onCreate");
        return rv;
    }

    /*
     *向UriMatcher中添加Uri
     */
    private static void addDataUri() {
        Log.d(TAG, "enter addDataUri");

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
         *加入的path(第二个参数)不要加"/",API-17及以前的版本不会处理掉多余的"/"
         *多余的"/"会导致split的时候除了需要的path还有一个空字符串。
         */

        //"统计"表中的数据。
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_STATISTIC,
                MetaData.STATISTIC);
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_STATISTIC+ "/#",
                MetaData.STATISTIC_ID);

        //"在读"表中的数据。
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_READING,
                MetaData.READING);
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_READING+ "/#",
                MetaData.READING_ID);

        //"想读"表中的数据。
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_WANT,
                MetaData.WANT);
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_WANT+"/#",
                MetaData.WANT_ID);

        //"已读"表中的数据。
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_FINISHED,
                MetaData.FINISHED);
        uriMatcher.addURI(MetaData.AUTHORITY,
                MetaData.SQLite_TABLE_FINISHED+"/#",
                MetaData.FINISHED_ID);

        Log.d(TAG, "leave addDataUri");
    }


    /*
     *进行查询
     */
    @Override
    public Cursor query (Uri uri, String[] projection, String selection,
                         String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "enter query");
        //SQLiteQueryBuilder这个类可以创建查询语句。
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // the tableName to query on
        mTableName = getTableName(uri);
        queryBuilder.setTables(mTableName);

        switch(uriMatcher.match(uri)) {
        case MetaData.READING:
        case MetaData.WANT:
        case MetaData.FINISHED:
        case MetaData.STATISTIC:
            //queryBuilder.setProjectionMap(BookMap);
            break;
        case MetaData.READING_ID:
        case MetaData.WANT_ID:
        case MetaData.FINISHED_ID:
        case MetaData.STATISTIC_ID:
            queryBuilder.appendWhere( MetaData.KEY_ROW_ID
                                      + "=" + uri.getLastPathSegment());
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if ((uriMatcher.match(uri) != MetaData.STATISTIC) &&
                (uriMatcher.match(uri) != MetaData.STATISTIC_ID) &&
                (sortOrder == null || sortOrder.equals(""))) {
            // No sorting-> sort on names by default
            sortOrder = MetaData.KEY_BOOK_NAME;
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                                           selectionArgs, null, null, sortOrder);

        //register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        Log.d(TAG, "leave query");
        return cursor;
    }

    /*
     * 插入数据
     */
    @Override
    public  Uri insert (Uri uri, ContentValues values) {
        Log.d(TAG, "enter insert");
        /*
         * Gets a writable database. This will trigger its creation if it doesn't already exist.
         */
        db = mOpenHelper.getWritableDatabase();

        mTableName = getTableName(uri);

        long row = db.insert(mTableName, "", values);
        Uri insertedUri;
        // If record is added successfully
        if(row > 0) {
            insertedUri = ContentUris.withAppendedId(getContentUri(mTableName), row);
            //通知监听器(通常是CursorAdapter)，数据已经改变。
            getContext().getContentResolver().notifyChange(insertedUri, null);
        } else {
            throw new SQLException("Fail to add a new record into " + uri);
        }

        Log.d(TAG, "leave insert");
        return insertedUri;
    }

    /*
     * 更新数据
     */
    @Override
    public  int update (Uri uri, ContentValues values,
                        String selection, String[] selectionArgs) {

        Log.d(TAG, "enter update");
        int count;
        mTableName = getTableName(uri);

        switch (uriMatcher.match(uri)) {
        case MetaData.READING:
        case MetaData.WANT:
        case MetaData.FINISHED:
        case MetaData.STATISTIC:
            count = db.update(mTableName, values, selection, selectionArgs);
            break;
        case MetaData.READING_ID:
        case MetaData.WANT_ID:
        case MetaData.FINISHED_ID:
        case MetaData.STATISTIC_ID:
            count = db.update(mTableName, values,
                              MetaData.KEY_ROW_ID +
                              " = " + uri.getLastPathSegment() +
                              (!TextUtils.isEmpty(selection) ? " AND (" +
                               selection + ')' : ""), selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI " + uri );
        }

        getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "leave update");
        return count;
    }

    /*
     * 删除数据
     */
    @Override
    public int delete (Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "enter delete");
        int count;
        mTableName = getTableName(uri);

        switch (uriMatcher.match(uri)) {
        case MetaData.READING:
        case MetaData.WANT:
        case MetaData.FINISHED:
        case MetaData.STATISTIC:
            // delete
            count = db.delete(mTableName, selection, selectionArgs);
            break;
        case MetaData.READING_ID:
        case MetaData.WANT_ID:
        case MetaData.FINISHED_ID:
        case MetaData.STATISTIC_ID:
                String id = uri.getLastPathSegment(); //gets the id
            count = db.delete( mTableName,
                               MetaData.KEY_ROW_ID +  " = " + id +
                               (!TextUtils.isEmpty(selection) ? " AND (" +
                                selection + ')' : ""), selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        Log.d(TAG, "leave delete");
        return count;
    }

    /*
     *从Uri解析数据类型
     */
    @Override
    public  String getType (Uri uri) {
        Log.d(TAG, "enter getType");

        String type;
        switch (uriMatcher.match(uri)) {
        case MetaData.STATISTIC:
            type = MetaData.STATISTIC_TYPE;
            break;
        case MetaData.STATISTIC_ID:
            type = MetaData.STATISTIC_TYPE_ITEM;
            break;
        case MetaData.READING:
            type = MetaData.READING_TYPE;
            break;
        case MetaData.READING_ID:
            type = MetaData.READING_TYPE_ITEM;
            break;
        case MetaData.WANT:
            type = MetaData.WANT_TYPE;
            break;
        case MetaData.WANT_ID:
            type = MetaData.WANT_TYPE_ITEM;
            break;
        case MetaData.FINISHED:
            type = MetaData.FINISHED_TYPE;
            break;
        case MetaData.FINISHED_ID:
            type = MetaData.FINISHED_TYPE_ITEM;
            break;
        default:
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Log.d(TAG, "leave getType");

        return type;
    }

    /*
     *从Uri解析数据库的表名称
     */
    private String getTableName(Uri uri) {
        Log.d(TAG, "enter getTableName");

        switch (uriMatcher.match(uri)) {
        case MetaData.STATISTIC:
        case MetaData.STATISTIC_ID:
            mTableName = MetaData.SQLite_TABLE_STATISTIC;
            break;
        case MetaData.READING:
        case MetaData.READING_ID:
            mTableName = MetaData.SQLite_TABLE_READING;
            break;
        case MetaData.WANT:
        case MetaData.WANT_ID:
            mTableName = MetaData.SQLite_TABLE_WANT;
            break;
        case MetaData.FINISHED:
        case MetaData.FINISHED_ID:
            mTableName = MetaData.SQLite_TABLE_FINISHED;
            break;
        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Log.d(TAG, "leave getTableName");
        return mTableName;
    }

    /*
     *获取ContentUri
     */
    public static Uri getContentUri(String tableName) {
        Log.d(TAG, "enter getContentUri");

        String URL = "content://" + MetaData.AUTHORITY + "/"+tableName;
        Uri uri = Uri.parse(URL);

        Log.d(TAG, "leave getContentUri");
        return uri;
    }

    /*
     * get BookInfo from cursor
     */
    public static BookInfo getBookInfo(Cursor cursor) {
        Log.d(TAG, "enter getBookInfo");

        BookInfo bookInfo = new BookInfo();
        //cursor包含着一个集合，用query查到的结果返回的cursor所在的位置在第一条结果之前。
        if(cursor.moveToFirst()){
            bookInfo.mBookName = cursor.getString(cursor.getColumnIndex
                    (MetaData.KEY_BOOK_NAME));
            bookInfo.mAuthor = cursor.getString(cursor.getColumnIndex
                    (MetaData.KEY_AUTHOR));
            bookInfo.mTimeString = cursor.getString(cursor.getColumnIndex
                    (MetaData.KEY_TIME_STRING));
            bookInfo.mReadPage = cursor.getInt(cursor.getColumnIndex
                    (MetaData.KEY_READ_PAGE));
            bookInfo.mTotalPage = cursor.getInt(cursor.getColumnIndex
                    (MetaData.KEY_TOTAL_PAGE));
            bookInfo.mMinutes = cursor.getInt(cursor.getColumnIndex
                    (MetaData.KEY_MINUTES));
            bookInfo.mHours = cursor.getInt(cursor.getColumnIndex
                    (MetaData.KEY_HOURS));
            bookInfo.mPercent = cursor.getString(cursor.getColumnIndex
                    (MetaData.KEY_PERCENT));
            bookInfo.mTimerSeconds = cursor.getString(cursor.getColumnIndex
                    (MetaData.KEY_TIMER_SECONDS));
        }

        Log.d(TAG, "leave getBookInfo");
        return bookInfo;
    }

    /*
     * get StatisticInfo from cursor
     */
    public static StatisticInfo getStatisticInfo(Cursor cursor) {
        Log.d(TAG, "enter getStatisticInfo");

        StatisticInfo statisticInfo = new StatisticInfo();
        //cursor包含着一个集合，用query查到的结果返回的cursor所在的位置在第一条结果之前。
        if(cursor.moveToFirst()){
            statisticInfo.mCategoryName = cursor.getString(cursor.getColumnIndex
                    (MetaData.KEY_CATEGORY_NAME));
            statisticInfo.mStatisticMinutes = cursor.getInt(cursor.getColumnIndex
                    (MetaData.KEY_STATISTIC_MINUTES));
            statisticInfo.mTimeString = cursor.getString(cursor.getColumnIndex(
                    MetaData.KEY_TIME_STRING));
        }

        Log.d(TAG, "leave getStatisticInfo");
        return statisticInfo;
    }
}
