package com.jessicaxu.readjiffy.app.util;

import android.util.Log;

/**
 * Created by root on 14-7-26.
 * 用这个类来进行入口和出口的打印，便于快速进行流程梳理和问题定位。
 */
public class TraceLog {

    private final String ENTRANCE = "Enter----->";
    private final String EXIT = "Leave----->";
    private boolean mPrintSwitch = false;

    public String mClassName;

    public TraceLog(String className){
        mClassName = className;
    }

    public void printEntrance(String methodName){
        if(!mPrintSwitch){
            return;
        }
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        Log.i(ENTRANCE+"("+mClassName, methodName + ": Line " + ste.getLineNumber() + ")");
    }

    public void printExit(String methodName){
        if(!mPrintSwitch){
            return;
        }
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        Log.i(EXIT+"("+mClassName,methodName + ": Line " + ste.getLineNumber() + ")");
    }
}
