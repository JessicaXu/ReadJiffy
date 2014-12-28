package com.jessicaxu.ReadJiffy.app.global;

import android.app.Application;
import android.content.Context;

/*
 创建自己的Application类，以便管理程序内全局的状态，比如全局Context.
 */
public class CustomApplication extends Application {
    private static Context context;

    @Override
    public void onCreate(){
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
