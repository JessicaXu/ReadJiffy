package com.jessicaxu.ReadJiffy.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.background.StatisticInfoAdapter;
import com.jessicaxu.ReadJiffy.app.global.MetaData;

/*
 *用来显示主页
 */
public class HomeFragment extends LoaderFragment {
    private String TAG = "HomeFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        setViewAdapter(rootView);

        Log.d(TAG, "leave onCreateView");
        return rootView;
    }

    private void setViewAdapter(View rootView){
        Log.d(TAG, "enter setViewAdapter");
        Bundle args = new Bundle();
        args.putInt(MetaData.ARG_POSITION, MetaData.CONTENT_POSITION_HOME);
        getLoaderManager().initLoader(MetaData.STATISTICINFO_LOADER, args, this);

        ListView TimeStringView = (ListView)rootView.findViewById(R.id.total_time);
        mDataAdapter = new StatisticInfoAdapter(getActivity(),
                R.layout.listitem_statistic,
                null,
                new String[]{MetaData.KEY_TIME_STRING},
                new int[]{R.id.time_string},
                0);
        TimeStringView.setAdapter(mDataAdapter);
        Log.d(TAG, "leave setViewAdapter");
    }
}
