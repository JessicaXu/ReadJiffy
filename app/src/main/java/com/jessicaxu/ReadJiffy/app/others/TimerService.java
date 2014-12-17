package com.jessicaxu.ReadJiffy.app.others;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.content.BookCP;
import com.jessicaxu.ReadJiffy.app.util.BookInfo;
import com.jessicaxu.ReadJiffy.app.util.CustomCompute;
import com.jessicaxu.ReadJiffy.app.util.MetaData;
import com.jessicaxu.ReadJiffy.app.util.TraceLog;

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

    private void getBookInfo(Intent intent){
        final String name = intent.getStringExtra(MetaData.EXTRA_NAME);

        String[] bookName = {name};
        Cursor cursor = getContentResolver().query(
                BookCP.getContentUri(MetaData.SQLite_TABLE_READING),
                null,
                MetaData.KEY_BOOK_NAME + "= ?",
                bookName,
                MetaData.KEY_BOOK_NAME);

        mBookInfo = BookCP.getBookInfo(cursor);
        cursor.close();
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            updateBookInfo(MetaData.ACTION_UPDATE_SECONDS);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        TraceLog.printEntrance("TimerService.java, onStartCommand()");
        startForegroundService();
        getBookInfo(intent);
        handleCommand(intent);

        TraceLog.printExit("TimerService.java, onStartCommand()");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        TraceLog.printEntrance("TimerService.java, onUnbind()");
        super.onUnbind(intent);
        TraceLog.printExit("TimerService.java, onUnbind()");

        return false;
    }

    @Override
    public void onDestroy(){
        TraceLog.printEntrance("TimerService.java, onDestroy()");
        super.onDestroy();
        cancelCommand();
        stopForeground(true);
        TraceLog.printExit("TimerService.java, onDestroy()");
    }

    /*
     *处理计时的任务
     */
    private void handleCommand(final Intent intent){

        int period = intent.getIntExtra(MetaData.EXTRA_PERIOD, 0);

        mPassedSeconds = period * 60;
        mTimerTask = new TimerTask() {
            public void run() {
                mPassedSeconds++;
                if(mPassedSeconds%60 == 0) {
                    getBookInfo(intent);
                    updateBookInfo(MetaData.ACTION_UPDATE_TOTAL);
                }
                mHandler.post(timerRunnable);
                System.out.println("run timer task!");
            }
        };

        t.scheduleAtFixedRate(mTimerTask, 1000, 1000);
    }

    /*
     *停止计时任务
     */
    private void cancelCommand(){
        if(mTimerTask!=null) {
            mTimerTask.cancel();
        }
        updateBookInfo(MetaData.ACTION_RESET_SECONDS);
    }

    /*
    * 将计时数据更新到BookInfo中，用来稍后更新数据库的记录
    */
    private void updateBookInfo(int action) {
        switch (action){
            case MetaData.ACTION_UPDATE_TOTAL:
                int totalMinutes = mBookInfo.mMinutes + mBookInfo.mHours * 60 + 1;
                mBookInfo.mMinutes = totalMinutes % 60;
                mBookInfo.mHours = totalMinutes / 60;
                mBookInfo.mTimeString = mBookInfo.getTimeString();
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
            CustomCompute.getContentValues(mBookInfo),
            MetaData.KEY_BOOK_NAME + " = ?",
            bookName);
    }

    private void startForegroundService(){
        Notification notification = new Notification(R.drawable.ic_launcher,
                "ReadJiffy", System.currentTimeMillis());
        notification.setLatestEventInfo(this, "ReadJiffy", "ReadJiffy正在跟踪您的阅读情况", null);
        startForeground(1, notification);
    }
}