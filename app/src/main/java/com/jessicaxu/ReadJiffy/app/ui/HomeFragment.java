package com.jessicaxu.ReadJiffy.app.ui;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.data.BookCP;
import com.jessicaxu.ReadJiffy.app.data.MetaData;
import com.jessicaxu.ReadJiffy.app.data.StatisticInfo;

/*
 *用来显示主页
 */
public class HomeFragment extends Fragment {

    private String TAG = "HomeFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //int statisticMinutes = getStatisticMinutes();
        int statisticMinutes = 500;
        String strHours = (statisticMinutes / 60) + "";
        String strMinutes = (statisticMinutes % 60) + "";
        TextView hourView = (TextView)rootView.findViewById(R.id.statistic_hours);
        TextView minuteView = (TextView)rootView.findViewById(R.id.statistic_minutes);
        hourView.setText(strHours);
        minuteView.setText(strMinutes);

        Log.d(TAG, "leave onCreateView");
        return rootView;
    }

    private int getStatisticMinutes(){
        String[] categoryName = {MetaData.STATISTIC_TOTAL};
        Cursor cursor = getActivity().getContentResolver().query(
                BookCP.getContentUri(MetaData.SQLite_TABLE_STATISTIC),
                null,
                MetaData.KEY_CATEGORY_NAME + "= ?",
                categoryName,
                MetaData.KEY_CATEGORY_NAME);

        StatisticInfo statisticInfo = BookCP.getStatisticInfo(cursor);
        cursor.close();
        return statisticInfo.mStatisticMinutes;
    }
}
