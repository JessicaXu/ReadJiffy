package com.jessicaxu.ReadJiffy.app.background;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class DataAdapter extends SimpleCursorAdapter{

    private static final String TAG = "DataAdapter";

    public DataAdapter(Context context, int layout, Cursor c, String[] from,
                       int[] to, int flags){
        super(context, layout, c, from, to, flags);
        Log.d(TAG, "DataAdapter");
    }
}
