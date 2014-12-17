package com.jessicaxu.ReadJiffy.app.util;

import android.graphics.Color;

/**
 * 定义全局的常量。
 */
public class MetaData {
    //Defines the database name
    public static final String SQLite_TABLE_READING = "Reading";
    public static final String SQLite_TABLE_FINISHED = "Finished";
    public static final String SQLite_TABLE_WANT = "Want";

    // integer values used in content URI
    public static final int READING = 1;
    public static final int READING_ID = 2;
    public static final int WANT = 3;
    public static final int WANT_ID = 4;
    public static final int FINISHED = 5;
    public static final int FINISHED_ID = 6;

    // Defines the database name
    public static final String DB_NAME = "Books";

    //Defines the database version
    public static final int DB_VERSION = 1;

    public static final String AUTHORITY =
        "com.jessicaxu.ReadJiffy.app.content.bookcp";

    // fields for the database
    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_BOOK_NAME = "bookName";
    public static final String KEY_AUTHOR = "author";
    public static final String KEY_READ_PAGE = "readPage";
    public static final String KEY_TOTAL_PAGE = "totalPage";
    public static final String KEY_MINUTES = "minutes";
    public static final String KEY_HOURS = "hours";
    public static final String KEY_TIME_STRING = "time_string";
    public static final String KEY_PERCENT = "percent";
    public static final String KEY_TIMER_SECONDS = "timer_seconds";

    //定义MIME TYPE
    public static final String READING_TYPE =
        "vnd.android.cursor.dir/vnd.ReadJiffy.reading";
    public static final String READING_TYPE_ITEM =
        "vnd.android.cursor.item/vnd.ReadJiffy.reading";
    public static final String WANT_TYPE =
        "vnd.android.cursor.dir/vnd.ReadJiffy.want";
    public static final String WANT_TYPE_ITEM =
        "vnd.android.cursor.item/vnd.ReadJiffy.want";
    public static final String FINISHED_TYPE =
        "vnd.android.cursor.dir/vnd.ReadJiffy.finished";
    public static final String FINISHED_TYPE_ITEM =
        "vnd.android.cursor.item/vnd.ReadJiffy.finished";

    //定义NavigationDrawerFragment的position信息
    public static final int DRAWER_POSITION_READING = 0;
    public static final int DRAWER_POSITION_WANT = 1;
    public static final int DRAWER_POSITION_FINISHED = 2;

    //ContentFragment中引用的NavigationDrawer的position.
    public static final int CONTENT_POSITION_INIT = DRAWER_POSITION_READING;
    public static final int CONTENT_POSITION_READING = DRAWER_POSITION_READING +1;
    public static final int CONTENT_POSITION_WANT = DRAWER_POSITION_WANT + 1;
    public static final int CONTENT_POSITION_FINISHED = DRAWER_POSITION_FINISHED + 1;

    //定义数据库的各种操作字符串
    public static final String OPERATION_QUERY = "query";
    public static final String OPERATION_INSERT = "insert";
    public static final String OPERATION_UPDATE = "update";
    public static final String OPERATION_DELETE = "delete";

    //定义Intent的putExtra的name
    public static final String EXTRA_NAME = "NAME";
    public static final String EXTRA_TABLE_NAME = "TABLE_NAME";
    public static final String EXTRA_PERIOD = "PERIOD";

    //定义TimerService向MainActivity发送的消息字
    public static final int MSG_SET_SECONDS = 0;
    public static final int MSG_RESET_SECONDS = 1;

    //设置颜色
    //public static final int READ_JIFFY_LIGHT = Color.rgb(255,255,255);
    public static final int READ_JIFFY_ACCENT1 = Color.rgb(245,233,1744);
    public static final int READ_JIFFY_ACCENT2 = Color.rgb(220,220,220);
    //public static final int READ_JIFFY_DARK = Color.rgb(121,121,121);
    public static final int READ_JIFFY_HIGHLIGHT = Color.rgb(46, 82, 180);
    public static final int READ_JIFFY_RED = Color.rgb(255, 0, 0);

    //存储listView相关position信息
    public static final int DEFAULT_POSITION = -1;
    public static final int KEY_CREATE_POSITION = 0;
    public static final int KEY_CLICK_POSITION = 1;

    public static final String ACTION_FOREGROUND = "com.jessicaxu.ReadJiffy.app.FOREGROUND";
    public static final String ACTION_BACKGROUND = "com.jessicaxu.ReadJiffy.app.BACKGROUND";

    public static final int ACTION_UPDATE_TOTAL = 0;
    public static final int ACTION_UPDATE_SECONDS = 1;
    public static final int ACTION_RESET_SECONDS = 2;
}
