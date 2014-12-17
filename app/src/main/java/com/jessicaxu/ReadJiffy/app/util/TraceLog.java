package com.jessicaxu.ReadJiffy.app.util;

import android.util.Log;

/**
 * 用这个类来进行入口和出口的打印，便于快速进行流程梳理和问题定位。
 */
public class TraceLog {

    private final static String ENTRANCE = "Enter----->";
    private final static String EXIT = "Leave----->";
    private final static boolean mPrintSwitch = true;

    /*
     *打印入口
     */
    public static void printEntrance(String debugInfo) {
        if(!mPrintSwitch) {
            return;
        }

        Log.i(ENTRANCE, debugInfo);
    }

    /*
     *打印出口
     */
    public static void printExit(String debugInfo) {
        if(!mPrintSwitch) {
            return;
        }

        Log.i(EXIT, debugInfo);
    }
}
