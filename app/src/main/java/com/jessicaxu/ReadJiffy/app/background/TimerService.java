package com.jessicaxu.ReadJiffy.app.background;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.data.BookCP;
import com.jessicaxu.ReadJiffy.app.data.BookInfo;
import com.jessicaxu.ReadJiffy.app.global.MetaData;
import com.jessicaxu.ReadJiffy.app.data.StatisticInfo;

import java.util.Timer;
import java.util.TimerTask;

/*
 *TimerService保证在用户离开本app界面时也可以在后台进行计时。
 */
public class TimerService extends Service {
    //Binder return to client
    private final IBinder mBinder = new Binder();
    private static TimerTask mTimerTask;
    private final Timer t = new Timer();
    private int mPassedSeconds;
    private static Handler mHandler = new Handler();
    private BookInfo mBookInfo;
    private static final String TAG = "TimerService";

    private BookInfo getBookInfo(Intent intent){
        Log.d(TAG, "enter getBookInfo");
        final String name = intent.getStringExtra(MetaData.EXTRA_NAME);

        String[] bookName = {name};
        Cursor cursor = getContentResolver().query(
                BookCP.getContentUri(MetaData.SQLite_TABLE_READING),
                null,
                MetaData.KEY_BOOK_NAME + "= ?",
                bookName,
                MetaData.KEY_BOOK_NAME);

        BookInfo bookInfo = BookCP.getBookInfo(cursor);
        cursor.close();
        Log.d(TAG, "leave getBookInfo");
        return bookInfo;
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "enter run");
            updateTimeInfo(MetaData.ACTION_UPDATE_SECONDS);
            Log.d(TAG, "leave run");
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "enter onStartCommand");
        startForegroundService();
        mBookInfo = getBookInfo(intent);
        handleCommand(intent);

        Log.d(TAG, "leave onStartCommand");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "enter onUnbind");
        super.onUnbind(intent);
        Log.d(TAG, "leave onUnbind");

        return false;
    }

    @Override
    public void onDestroy(){
        Log.d(TAG, "enter onDestroy");
        super.onDestroy();
        cancelCommand();
        stopForeground(true);
        Log.d(TAG, "leave onDestroy");
    }

    /*
     *处理计时的任务
     */
    private void handleCommand(final Intent intent){
        Log.d(TAG, "enter handleCommand");
        int period = intent.getIntExtra(MetaData.EXTRA_PERIOD, 0);

        mPassedSeconds = period * 60;
        mTimerTask = new TimerTask() {
            public void run() {
                mPassedSeconds++;
                if(mPassedSeconds%60 == 0) {
                    mBookInfo = getBookInfo(intent);
                    updateTimeInfo(MetaData.ACTION_UPDATE_TOTAL);
                }
                mHandler.post(timerRunnable);
            }
        };

        t.scheduleAtFixedRate(mTimerTask, 1000, 1000);
        Log.d(TAG, "leave handleCommand");
    }

    /*
     *停止计时任务
     */
    private void cancelCommand(){
        Log.d(TAG, "enter cancelCommand");
        if(mTimerTask!=null) {
            mTimerTask.cancel();
        }
        updateTimeInfo(MetaData.ACTION_RESET_SECONDS);
        Log.d(TAG, "leave cancelCommand");
    }

    /*
    * 将计时数据更新到BookInfo中，用来稍后更新数据库的记录
    */
    private void updateTimeInfo(int action) {
        Log.d(TAG, "enter updateTimeInfo");
        switch (action){
            case MetaData.ACTION_UPDATE_TOTAL:
                updateBookInfo();
                updateStatisticInfo(1);
                break;
            case MetaData.ACTION_UPDATE_SECONDS:
                mBookInfo.mTimerSeconds = mBookInfo.getSecondsString(mPassedSeconds);
                break;
            case MetaData.ACTION_RESET_SECONDS:
                mBookInfo.mTimerSeconds = mBookInfo.getSecondsString(0);
                break;
            default:
                throw new IllegalArgumentException("illegal argument!");
        }

        String[] bookName = {mBookInfo.mBookName};
        getContentResolver().update(
            BookCP.getContentUri(MetaData.SQLite_TABLE_READING),
                mBookInfo.setContentValues(),
            MetaData.KEY_BOOK_NAME + " = ?",
            bookName);
        Log.d(TAG, "leave updateTimeInfo");
    }

    private void updateBookInfo(){
        Log.d(TAG, "enter updateBookInfo");
        int totalMinutes = mBookInfo.mMinutes + mBookInfo.mHours * 60 + 1;
        mBookInfo.mMinutes = totalMinutes % 60;
        mBookInfo.mHours = totalMinutes / 60;
        mBookInfo.mTimeString = mBookInfo.getTimeString();
        Log.d(TAG, "leave updateBookInfo");
    }

    private void updateStatisticInfo(int increase){
        Log.d(TAG, "enter updateStatisticInfo");
        String[] categoryName = {MetaData.STATISTIC_TOTAL};
        Cursor cursor = getContentResolver().query(
                BookCP.getContentUri(MetaData.SQLite_TABLE_STATISTIC),
                null,
                MetaData.KEY_CATEGORY_NAME + "= ?",
                categoryName,
                MetaData.KEY_CATEGORY_NAME);

        StatisticInfo statisticInfo = BookCP.getStatisticInfo(cursor);
        cursor.close();

        statisticInfo.mStatisticMinutes += increase;
        statisticInfo.mTimeString = statisticInfo.getTimeString();
        getContentResolver().update(
                BookCP.getContentUri(MetaData.SQLite_TABLE_STATISTIC),
                statisticInfo.setContentValues(),
                MetaData.KEY_CATEGORY_NAME + " = ?",
                categoryName);
        Log.d(TAG, "leave updateStatisticInfo");
    }


    private void startForegroundService(){
        Log.d(TAG, "enter startForegroundService");
        Notification notification = new Notification(R.drawable.ic_launcher,
                "ReadJiffy", System.currentTimeMillis());
        notification.setLatestEventInfo(this, "ReadJiffy", "ReadJiffy正在跟踪您的阅读情况", null);
        startForeground(1, notification);
        Log.d(TAG, "leave startForegroundService");
    }
}