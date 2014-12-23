package com.jessicaxu.ReadJiffy.app.data;

import android.content.ContentValues;
import android.util.Log;

/*
 *用于暂存统计信息的类
 */
public class StatisticInfo {

    private static final String TAG = "StatisticInfo";

    public int mStatisticMinutes;

    public String mCategoryName;

    public StatisticInfo(){
        mStatisticMinutes = 0;
        mCategoryName = MetaData.STATISTIC_TOTAL;
    }

    /*
    *将StatisticInfo封装到ContentValues中
    */
    public ContentValues setContentValues() {
        Log.d(TAG, "enter getContentValues");
        ContentValues contentValues = new ContentValues();

        contentValues.put(MetaData.KEY_CATEGORY_NAME, mCategoryName);
        contentValues.put(MetaData.KEY_STATISTIC_MINUTES, mStatisticMinutes);

        Log.d(TAG, "leave getContentValues");
        return contentValues;
    }

}
