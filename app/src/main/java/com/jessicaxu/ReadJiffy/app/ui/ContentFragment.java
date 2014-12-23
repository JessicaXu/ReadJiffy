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
import com.jessicaxu.ReadJiffy.app.data.*;

/**
 * 点击DrawerNavigation后出现的内容界面，这个界面呈现图书统计信息
 */
public  class ContentFragment extends LoaderFragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView mBookList;
    private static final String TAG = "ContentFragment";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContentFragment newInstance(int sectionNumber) {

        Log.d(TAG, "enter newInstance)");
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        Log.d(TAG, "leave newInstance)");
        return fragment;
    }

    public ContentFragment() {
        super();
        Log.d(TAG, "enter ContentFragment");
        Log.d(TAG, "leave ContentFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);

        mBookList = (ListView) (rootView.findViewById(R.id.bookList));

        //获取NavigationDrawerFragment选中的Item的position
        Bundle args = this.getArguments();
        int position = args.getInt(ARG_SECTION_NUMBER);

        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(MetaData.ARG_TABLE_NAME,
                ((MainActivity) getActivity()).getTableName(position));
        getLoaderManager().initLoader(MetaData.BOOKINFO_LOADER, loaderArgs, this);

        displayListView(position);
        ((MainActivity)getActivity()).restoreActionBar();

        Log.d(TAG, "leave onCreateView");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "enter onAttach");
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
            getArguments().getInt(ARG_SECTION_NUMBER));
        Log.d(TAG, "leave onAttach");
    }


    //将数据库中的书籍信息显示到ListView中。
    void displayListView(int position) {
        Log.d(TAG, "enter displayListView");
        String[] columns;
        int[] to;
        int layout;

        switch (position) {
        case MetaData.CONTENT_POSITION_READING:
            columns = new String[] {
                MetaData.KEY_BOOK_NAME,
                MetaData.KEY_AUTHOR,
                MetaData.KEY_TIMER_SECONDS,
                MetaData.KEY_TIME_STRING,
                MetaData.KEY_READ_PAGE,
                MetaData.KEY_TOTAL_PAGE,
                "" + MetaData.KEY_PERCENT
            };

            to = new int[] {
                R.id.bookNameText,
                R.id.authorText,
                R.id.timerText,
                R.id.totalTimeText,
                R.id.readPageText,
                R.id.totalPageText,
                R.id.percentText
            };
            layout = R.layout.listitem_reading;
            break;
        case MetaData.CONTENT_POSITION_WANT:
            columns = new String[] {
                MetaData.KEY_BOOK_NAME,
                MetaData.KEY_AUTHOR,
                ""+ MetaData.KEY_TOTAL_PAGE
            };
            to = new int[] {
                R.id.bookNameText,
                R.id.authorText,
                R.id.totalPageText
            };
            layout = R.layout.listitem_want;
            break;
        case MetaData.CONTENT_POSITION_FINISHED:
            columns = new String[] {
                MetaData.KEY_BOOK_NAME,
                MetaData.KEY_AUTHOR,
                MetaData.KEY_TIME_STRING,
                "" + MetaData.KEY_TOTAL_PAGE
            };
            to = new int[] {
                R.id.bookNameText,
                R.id.authorText,
                R.id.totalTimeText,
                R.id.totalPageText
            };
            layout = R.layout.listitem_finished;
            break;
        default:
            throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }

        mDataAdapter = new BookInfoAdapter(
                getActivity(),
                (MainActivity)getActivity(),
                layout,
                columns,
                to,
                position);

        // Assign adapter to ListView
        mBookList.setAdapter(mDataAdapter);

        Log.d(TAG, "leave displayListView");
    }
}