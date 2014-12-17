package com.jessicaxu.ReadJiffy.app.ui;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.others.*;
import com.jessicaxu.ReadJiffy.app.content.*;
import com.jessicaxu.ReadJiffy.app.util.*;

/**
 * 点击DrawerNavigation后出现的内容界面，这个界面呈现图书统计信息
 */
public  class ContentFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int LOADER_ID = 1;
    private static final String TABLE_NAME = "tableName";
    private ListView mBookList;
    private CustomCursorAdapter mDataAdapter;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContentFragment newInstance(int sectionNumber) {

        TraceLog.printEntrance("newInstance)");
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        TraceLog.printExit("newInstance)");
        return fragment;
    }

    public ContentFragment() {
        super();
        TraceLog.printEntrance("ContentFragment");
        TraceLog.printExit("ContentFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        TraceLog.printEntrance("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);

        /*mBookList的值为null，要搞清楚为什么。
        *因为获得的Activity是MainActivity,MainActivity对应的界面是activity_main.xml文件，其中
        * 没有bookList这个控件，获取失败，返回null。
        * bookList控件的parent层是fragment_main.xml。因此需要从PlaceholderFragment里面获取。
        * 此时要调用View的findViewById()来获取子View。而不是Activity的findViewById()。
        */
        //mBookList = (ListView) (getActivity().findViewById(R.id.bookList));
        mBookList = (ListView) (rootView.findViewById(R.id.bookList));

        //获取NavigationDrawerFragment选中的Item的position
        Bundle args = this.getArguments();
        int position = args.getInt(ARG_SECTION_NUMBER);

        Bundle loaderArgs = new Bundle();
        loaderArgs.putString(TABLE_NAME, CustomCompute.getTableName(position));
        getLoaderManager().initLoader(LOADER_ID, loaderArgs, this);

        displayListView(position);
        ((MainActivity)getActivity()).restoreActionBar();

        TraceLog.printExit("onCreateView");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        TraceLog.printEntrance("onAttach");
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
            getArguments().getInt(ARG_SECTION_NUMBER));
        TraceLog.printExit("onAttach");
    }


    //将数据库中的书籍信息显示到ListView中。
    void displayListView(int position) {
        TraceLog.printEntrance("displayListView");
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

        mDataAdapter = new CustomCursorAdapter(
                getActivity(),
                (MainActivity)getActivity(),
                layout,
                columns,
                to,
                position);

        // Assign adapter to ListView
        mBookList.setAdapter(mDataAdapter);

        TraceLog.printExit("displayListView");
    }

    /**
     * This method is called when a previously created loader has finished its load.
     * This method is guaranteed to be called prior to the release of the last data that
     * was supplied for this loader. At this point you should remove all use of the old
     * data (since it will be released soon), but should not do your own release of the
     * data since its loader owns it and will take care of that.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        TraceLog.printEntrance("onLoadFinished");
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.) update Cursor.
        mDataAdapter.changeCursor(data);
        TraceLog.printExit("onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        TraceLog.printEntrance("onLoaderReset");
        mDataAdapter.changeCursor(null);
        TraceLog.printExit("onLoaderReset");
    }

    /**
     *返回一个Loader,这个方法不能被手动调用。
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        TraceLog.printEntrance("onCreateLoader");
        CursorLoader cursorLoader = null;
        if(id == LOADER_ID) {
            //从参数Bundle中获得数据库表的名称
            String tableName = args.getString(TABLE_NAME);

            /**
             * URI: The URI for the content to retrieve.内容的URI.
             * projection: 指明返回数据库表的哪些栏，若为null就返回所有栏。
             * selection: 指明返回哪些行的数据，通常格式是SQL的where后面的内容，若为null就返回所有行。
             * selectionArgs: 代替selection中的?,是一个值。
             * sortOrder: 查询结果的排序方式
             */
            cursorLoader = new CursorLoader(getActivity(),
                                            BookCP.getContentUri(tableName),
                                            null,
                                            null,
                                            null,
                                            MetaData.KEY_ROW_ID);//书籍按照添加的先后顺序加入ListView
        }
        TraceLog.printExit("onCreateLoader");
        return cursorLoader;
    }
}