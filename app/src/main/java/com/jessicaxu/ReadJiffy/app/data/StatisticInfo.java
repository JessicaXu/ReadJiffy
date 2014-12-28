package com.jessicaxu.ReadJiffy.app.data;

import android.content.ContentValues;
import android.util.Log;

import com.jessicaxu.ReadJiffy.app.global.MetaData;

/*
 *用于暂存统计信息的类
 */
public class StatisticInfo {

    private static final String TAG = "StatisticInfo";

    public int mStatisticMinutes;
    public String mTimeString;

    public String mCategoryName;

    public StatisticInfo(){
        mStatisticMinutes = 0;
        mCategoryName = MetaData.STATISTIC_TOTAL;
        mTimeString = "0 小时 0 分钟";
    }

    /*
    *将StatisticInfo封装到ContentValues中
    */
    public ContentValues setContentValues() {
        Log.d(TAG, "enter getContentValues");
        ContentValues contentValues = new ContentValues();

        contentValues.put(MetaData.KEY_CATEGORY_NAME, mCategoryName);
        contentValues.put(MetaData.KEY_STATISTIC_MINUTES, mStatisticMinutes);
        contentValues.put(MetaData.KEY_TIME_STRING, mTimeString);

        Log.d(TAG, "leave getContentValues");
        return contentValues;
    }

    /*
     *将分钟转化成显示的字符串
     */
    public String getTimeString(){
        int hour = mStatisticMinutes / 60;
        int minute = mStatisticMinutes % 60;
        return hour + " 小时" + minute + " 分钟";
    }

}
