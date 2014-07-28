package com.jessicaxu.readjiffy.app.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.jessicaxu.readjiffy.app.R;
import com.jessicaxu.readjiffy.app.util.TraceLog;

/**
 * Created by root on 14-7-24.
 */
//ADD THIS CLASS BY JESSICAXU, CUSTOM MY OWN LISTVIEW.
public class MyCursorAdapter extends SimpleCursorAdapter {
    Handler handler;
    boolean START = true;
    int passedSeconds = 0;

    private final String CLASSNAME = "MyCursorAdapter";
    private TraceLog mTraceLog = new TraceLog(CLASSNAME);
    //构造函数
    public MyCursorAdapter(Context context, int layout, Cursor c,
                           String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mTraceLog.printEntrance("getView");
        //get reference to the row
        View view = super.getView(position, convertView, parent);
        final TextView timerView = (TextView)view.findViewById(R.id.timerText);

        setItemColor(position, view);

        //为什么我把handler定义在这里就不可以更新界面？为什么和final无关呢？
        timerView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                /*
                class MyHandler extends Handler {
                    @Override
                    public void handleMessage(Message msg){
                        String s = (String)msg.obj;
                        timerView.setText(s);
                    }
                }
                handler = new MyHandler();

                class TimerRunnable implements Runnable{
                    @Override
                    public void run(){
                        if(START == true) {
                            START = startTimer();
                            passedSeconds = 0;
                        }
                        else{
                            START = stopTimer();
                        }
                    }
                }
                TimerRunnable timerRunnable = new TimerRunnable();
                Thread timerThread = new Thread(timerRunnable);
                timerThread.start();
           */
            }
        });
        mTraceLog.printExit("getView");
        return view;
    }

    private void setItemColor(int position, View view){
        mTraceLog.printEntrance("setItemColor");
        //check for odd or even to set alternate colors to the row background
        if(position % 2 == 0){
            view.setBackgroundColor(Color.rgb(245, 233, 174));
        }
        else {
            view.setBackgroundColor(Color.rgb(220, 220, 220));
        }
        mTraceLog.printExit("setItemColor");
    }

    private boolean startTimer(){
        mTraceLog.printEntrance("startTimer");
        try {
            while (true) {
                Thread.sleep(1000);
                passedSeconds++;
                int seconds = passedSeconds % 60;
                int minutes = (passedSeconds / 60) % 60;
                int hours = (passedSeconds / 3600);
                String s = String.format("%02d:%02d:%02d", hours, minutes, seconds);

                Message msg = handler.obtainMessage();
                msg.obj = s;
                handler.sendMessage(msg);
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        mTraceLog.printExit("startTimer");
        return false;
    }

    private boolean stopTimer(){
        mTraceLog.printEntrance("stopTimer");
        String s = "00:00:00";
        Message msg = handler.obtainMessage();
        msg.obj = s;
        handler.sendMessage(msg);
        mTraceLog.printExit("stopTimer");
        return true;
    }

}
