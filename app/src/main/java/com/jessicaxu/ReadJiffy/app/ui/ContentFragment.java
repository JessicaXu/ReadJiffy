package com.jessicaxu.ReadJiffy.app.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.background.*;
import com.jessicaxu.ReadJiffy.app.global.MetaData;

/**
 * 点击DrawerNavigation后出现的内容界面，这个界面呈现图书统计信息
 */
public  class ContentFragment extends LoaderFragment {
    private static final String TAG = "ContentFragment";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContentFragment newInstance(int sectionNumber) {

        Log.d(TAG, "enter newInstance)");
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(MetaData.ARG_POSITION, sectionNumber);
        fragment.setArguments(args);
        Log.d(TAG, "leave newInstance)");
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        setViewAdapter(rootView);
        ((MainActivity)getActivity()).restoreActionBar();

        Log.d(TAG, "leave onCreateView");
        return rootView;
    }

    private void setViewAdapter(View rootView){
        Log.d(TAG, "enter getFromArray");
        //获取NavigationDrawerFragment选中的Item的position
        Bundle args = this.getArguments();
        int position = args.getInt(MetaData.ARG_POSITION);
        getLoaderManager().initLoader(MetaData.BOOKINFO_LOADER, args, this);

        ListView bookList = (ListView) (rootView.findViewById(R.id.bookList));
        mDataAdapter = new BookInfoAdapter(
                getActivity(),
                getLayout(position),
                getFromArray(position),
                getToArray(position),
                position);

        // Assign adapter to ListView
        bookList.setAdapter(mDataAdapter);
    }

    private String[] getFromArray(int position){
        Log.d(TAG, "enter getFromArray");
        String[] from;
        switch (position){
            case MetaData.CONTENT_POSITION_READING:
                from = new String[] {MetaData.KEY_BOOK_NAME, MetaData.KEY_AUTHOR,
                        MetaData.KEY_TIMER_SECONDS, MetaData.KEY_TIME_STRING, MetaData.KEY_READ_PAGE,
                        MetaData.KEY_TOTAL_PAGE, "" + MetaData.KEY_PERCENT };
                break;
            case MetaData.CONTENT_POSITION_WANT:
                from = new String[] {MetaData.KEY_BOOK_NAME, MetaData.KEY_AUTHOR,
                        ""+ MetaData.KEY_TOTAL_PAGE};
                break;
            case MetaData.CONTENT_POSITION_FINISHED:
                from = new String[] {MetaData.KEY_BOOK_NAME, MetaData.KEY_AUTHOR,
                        MetaData.KEY_TIME_STRING, "" + MetaData.KEY_TOTAL_PAGE};
                break;
            default:
                throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }
        Log.d(TAG, "leave getFromArray");
        return from;
    }

    private int[] getToArray(int position){
        Log.d(TAG, "enter getToArray");
        int[] to;
        switch (position) {
            case MetaData.CONTENT_POSITION_READING:
                to = new int[] {R.id.bookNameText, R.id.authorText, R.id.timerText,
                        R.id.totalTimeText, R.id.readPageText, R.id.totalPageText, R.id.percentText};
                break;
            case MetaData.CONTENT_POSITION_WANT:
                to = new int[] {R.id.bookNameText, R.id.authorText, R.id.totalPageText};
                break;
            case MetaData.CONTENT_POSITION_FINISHED:
                to = new int[] {R.id.bookNameText, R.id.authorText,
                        R.id.totalTimeText, R.id.totalPageText};
                break;
            default:
                throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }
        Log.d(TAG, "leave getToArray");
        return to;
    }

    private int getLayout(int position){
        Log.d(TAG, "enter getLayout");
        int layout;
        switch (position) {
            case MetaData.CONTENT_POSITION_READING:
                layout = R.layout.listitem_reading;
                break;
            case MetaData.CONTENT_POSITION_WANT:
                layout = R.layout.listitem_want;
                break;
            case MetaData.CONTENT_POSITION_FINISHED:
                layout = R.layout.listitem_finished;
                break;
            default:
                throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }
        Log.d(TAG, "leave getLayout");
        return layout;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "enter onAttach");
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(MetaData.ARG_POSITION));
        Log.d(TAG, "leave onAttach");
    }
}