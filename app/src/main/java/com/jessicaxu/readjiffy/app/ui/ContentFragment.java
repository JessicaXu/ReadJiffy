package com.jessicaxu.readjiffy.app.ui;

/**
 * Created by JessicaXu on 14-6-27.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jessicaxu.readjiffy.app.R;
import com.jessicaxu.readjiffy.app.adapter.MyCursorAdapter;
import com.jessicaxu.readjiffy.app.data.BookDatabase;
import com.jessicaxu.readjiffy.app.data.BookInfo;
import com.jessicaxu.readjiffy.app.util.TraceLog;

/**
 * A placeholder fragment containing a simple view.
 * 点击DrawerNavigation后出现的界面，主要开发的界面也是它。需要添加书籍条目ListView.
 * 打开软件后第一个出现的界面也是它。
 */
public  class ContentFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_ACTION = "action";
    private static final String ARG_BOOKINFO = "bookinfo";

    //Define selection numbers
    private static final int SECTION_READING = 1;
    private static final int SECTION_WANT = 2;
    private static final int SECTION_FINISHED = 3;

    //define table name.
    private static final String SQLITE_TABLE_READING = "Reading";
    private static final String SQLITE_TABLE_FINISHED = "Finished";
    private static final String SQLITE_TABLE_WANT = "Want";


    //define action
    private final String ACTION_NONE = "none";
    private final String ACTION_INSERT = "insert";
    private final String ACTION_UPDATE = "update";
    private final String ACTION_DELETE = "delete";

    private static final String CLASSNAME = "ContentFragment";
    private static TraceLog mTraceLog = new TraceLog(CLASSNAME);

    public ListView mBookList;
    BookDatabase mBookDatabase;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ContentFragment newInstance(
            int sectionNumber, BookInfo bookInfo, String action) {

        mTraceLog.printEntrance("newInstance)");
        ContentFragment fragment = new ContentFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putString(ARG_ACTION, action);
        args.putSerializable(ARG_BOOKINFO, bookInfo);
        fragment.setArguments(args);
        mTraceLog.printExit("newInstance)");
        return fragment;
    }

    public ContentFragment() {
        mTraceLog.printEntrance("ContentFragment");
        mTraceLog.printExit("ContentFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTraceLog.printEntrance("onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);

        /*mBookList的值为null，要搞清楚为什么。
        *因为获得的Activity是MainActivity,MainActivity对应的界面是activity_main.xml文件，其中
        * 没有bookList这个控件，获取失败，返回null。
        * bookList控件的parent层是fragment_main.xml。因此需要从PlaceholderFragment里面获取。
        * 此时要调用View的findViewById()来获取子View。而不是Activity的findViewById()。
        */
        //mBookList = (ListView) (getActivity().findViewById(R.id.bookList));
        mBookList = (ListView) (rootView.findViewById(R.id.bookList));
        mBookDatabase = new BookDatabase(getActivity());

        //获取NavigationDrawerFragment选中的Item的position
        Bundle args = this.getArguments();
        int position = args.getInt(ARG_SECTION_NUMBER);
        String action = args.getString(ARG_ACTION);
        BookInfo bookInfo = (BookInfo)args.getSerializable(ARG_BOOKINFO);

        operateDatabase(action, bookInfo);
        displayListView(position);
        ((MainActivity)getActivity()).restoreActionBar();
        mTraceLog.printExit("onCreateView");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        mTraceLog.printEntrance("onAttach");
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        mTraceLog.printExit("onAttach");
    }


    //将数据库中的书籍信息显示到ListView中。
    public void displayListView(int position) {
        mTraceLog.printEntrance("displayListView");
        String tableName = SQLITE_TABLE_READING;

        //获取数据库表的名称。
        switch (position){
            case SECTION_READING:
                tableName = SQLITE_TABLE_READING;
                break;
            case SECTION_WANT:
                tableName = SQLITE_TABLE_WANT;
                break;
            case SECTION_FINISHED:
                tableName = SQLITE_TABLE_FINISHED;
                break;
            default:
                break;
        }

        //the desired columns to be bound
        String[] columns = new String[]{
                    BookDatabase.KEY_BOOKNAME,
                    BookDatabase.KEY_AUTHOR,
                    BookDatabase.KEY_STATUS,
                    ""+BookDatabase.KEY_PERCENT,
                    ""+BookDatabase.KEY_TIME
            };

        //the XML defined views which the data will be bound to
        int[] to = new int[] {
                R.id.bookNameText,
                R.id.authorText,
                R.id.statusText,
                R.id.percentText,
                R.id.totalTimerText
        };

        //为什么这里显示mBookDb无法识别，奇怪了。
        //mBookDatabase = getActivity().mBookDb;
        mBookDatabase.open();
        Cursor cursor = mBookDatabase.fetchAllBooks(tableName);
        MyCursorAdapter dataAdapter = new MyCursorAdapter(
                    getActivity(),
                    R.layout.book_list_item,
                    cursor,
                    columns,
                    to,
                    0);
            // Assign adapter to ListView
        mBookList.setAdapter(dataAdapter);
        //cursor.close();
        //android管理cursor,在适当的时机关闭cursor.
        getActivity().startManagingCursor(cursor);
        mBookDatabase.close();
        mTraceLog.printExit("displayListView");
    }

    private String getTableName(String status){
        mTraceLog.printEntrance("getTableName");
        if(status.equals("在读")){
            mTraceLog.printExit("getTableName");
            return SQLITE_TABLE_READING;
        }
        else if(status.equals("想读")){
            mTraceLog.printExit("getTableName");
            return SQLITE_TABLE_WANT;
        }
        else{
            mTraceLog.printExit("getTableName");
            return SQLITE_TABLE_FINISHED;
        }
    }

    private void operateDatabase(String action, BookInfo bookInfo){
        mTraceLog.printEntrance("operateDatabase");
        mBookDatabase.open();

        //根据action的值进行不同的处理。
        if(action.equals(ACTION_INSERT)){
            String[] bookName = {bookInfo.mBookName};
            Cursor cursor = mBookDatabase.queryOneBook(
                    getTableName(bookInfo.mStatus), bookName);
            if(cursor.isAfterLast()){
                mBookDatabase.insertBookItem(
                        getTableName(bookInfo.mStatus), bookInfo);
            }
            else{
                //弹出对话框，提示书名重复。
                System.out.println("书名重复，已经有这本书了.");
            }
            getActivity().startManagingCursor(cursor);
        }
        else if(action.equals(ACTION_DELETE)){
            mBookDatabase.deleteOneBook(
                    getTableName(bookInfo.mStatus), bookInfo.mBookName);

        }
        else if(action.equals(ACTION_UPDATE)){
            // to-do:编辑一本图书的信息

        }
        else{
            // do nothing
        }
        mBookDatabase.close();
        mTraceLog.printExit("operateDatabase");
    }

}