package com.jessicaxu.readjiffy.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jessicaxu.readjiffy.app.R;
import com.jessicaxu.readjiffy.app.data.BookInfo;
import com.jessicaxu.readjiffy.app.util.TraceLog;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private final String ACTION_NONE = "none";
    private final String ACTION_INSERT = "insert";
    private final String ACTION_UPDATE = "update";
    private final String ACTION_DELETE = "delete";
    private final String CLASSNAME = "MainActivity";

    private CharSequence mTitle;
    private String mAction;
    private TraceLog mTraceLog = new TraceLog(CLASSNAME);

    public final int ADD_BOOK_REQUEST = 1;
    public final int ITEM_NUMBER_READING = 0;
    public final int ITEM_NUMBER_WANT = 1;
    public final int ITEM_NUMBER_FINISHED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTraceLog.printEntrance("onCreate");

        setStrictMode(true);

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException (Thread thread, Throwable e)
            {
               e.printStackTrace();
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mTraceLog.printExit("onCreate");
    }

    /*
    *处理左边的DrawerNavigation选中某一个条目时的界面变化。
    * 现在看起来界面都一样，需要区分三种不同的书籍状态.
    */
    @Override
    public void onNavigationDrawerItemSelected(
            int position, BookInfo bookInfo, String mAction) {
        mTraceLog.printEntrance("onNavigationDrawerItemSelected");
        //将MainActivity的BookDatabase传递给ContentFragment.
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container,
                ContentFragment.newInstance(position + 1, bookInfo, mAction))
                .commit();
        mTraceLog.printExit("onNavigationDrawerItemSelected");
    }

    public void onSectionAttached(int number) {
        mTraceLog.printEntrance("onSectionAttached");
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section_reading);
                break;
            case 2:
                mTitle = getString(R.string.title_section_want);
                break;
            case 3:
                mTitle = getString(R.string.title_section_finished);
                break;
        }
        mTraceLog.printExit("onSectionAttached");
    }

    public void restoreActionBar() {
        mTraceLog.printEntrance("restoreActionBar");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        mTraceLog.printExit("restoreActionBar");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mTraceLog.printEntrance("onCreateOptionsMenu");
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }

        mTraceLog.printExit("onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mTraceLog.printEntrance("onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (item.getItemId() == R.id.add_book) {
            addBook();
            return true;
        }
        mTraceLog.printExit("onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }


    //begin add by JessicaXu
    public void addBook(){
        mTraceLog.printEntrance("addBook");
        Intent addBookIntent = new Intent(this, AddBookActivity.class);
        startActivityForResult(addBookIntent, ADD_BOOK_REQUEST);
        mTraceLog.printExit("addBook");
    }

    /*为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent addBookIntent){
        mTraceLog.printEntrance("onActivityResult");
        if (requestCode == ADD_BOOK_REQUEST){
            if (resultCode == RESULT_OK) {
                BookInfo bookInfo = new BookInfo(addBookIntent);
                //将新得到BookInfo传递给ContentFragment，让ContentFragment将数据添加到数据库。
                mAction = ACTION_INSERT;
                mNavigationDrawerFragment.selectItem(
                        getItemNumber(bookInfo.mStatus), bookInfo, mAction);
                //重画ActionBar,保证标题的正确性。
            }
            else if (resultCode == RESULT_CANCELED){
                // what to do?
            }
        }
        mTraceLog.printExit("onActivityResult");
    }

    //delete a book from ListView
    public  void onDelete(View view){
        mTraceLog.printEntrance("onDelete");

        CustomTextView bookNameTx = (CustomTextView)((View) view.getParent()).
                findViewById(R.id.bookNameText);
        final String bookName = bookNameTx.getText().toString();

        TextView statusTx = (TextView)((View) view.getParent()).findViewById(R.id.statusText);
        final String status = statusTx.getText().toString();

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setTitle("删除");
        alert.setMessage("确定要删除这本书?");
        alert.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BookInfo bookInfo = new BookInfo();
                bookInfo.mBookName = bookName;
                bookInfo.mStatus = status;

                mAction = ACTION_DELETE;
                mNavigationDrawerFragment.selectItem(
                        getItemNumber(bookInfo.mStatus), bookInfo, mAction);
            }
        });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
        mTraceLog.printExit("onDelete");
    }

    /*
     *检测内存泄漏以及可能造成ANR的操作。
     */
    private void setStrictMode(boolean developMode){
        mTraceLog.printEntrance("setStrictMode");
        if (developMode) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        mTraceLog.printExit("setStrictMode");
    }

    private int getItemNumber(String status){
        mTraceLog.printEntrance("getItemNumber");
        if(status.equals("在读")){
            mTraceLog.printExit("getItemNumber");
            return ITEM_NUMBER_READING;
        }
        else if(status.equals("想读")){
            mTraceLog.printExit("getItemNumber");
            return ITEM_NUMBER_WANT;
        }
        else{
            mTraceLog.printExit("getItemNumber");
            return ITEM_NUMBER_FINISHED;
        }
    }
}
