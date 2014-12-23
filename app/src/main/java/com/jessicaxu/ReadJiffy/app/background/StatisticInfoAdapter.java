package com.jessicaxu.ReadJiffy.app.background;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class StatisticInfoAdapter extends DataAdapter{

    private static final String TAG = "StatisticInfoAdapter";

    public StatisticInfoAdapter(Context context, int layout, Cursor c, String[] from,
                                int[] to, int flags){
        super(context, layout, c, from, to, flags);
        Log.d(TAG, "StatisticInfoAdapter");
    }
}
