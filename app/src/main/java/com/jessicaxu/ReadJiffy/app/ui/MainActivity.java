package com.jessicaxu.ReadJiffy.app.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.StrictMode;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.data.*;
import com.jessicaxu.ReadJiffy.app.background.*;
import com.jessicaxu.ReadJiffy.app.global.MetaData;


public class MainActivity extends ActionBarActivity
    implements DrawerFragment.NavigationDrawerCallbacks,
               BookInfoAdapter.CursorAdapterCallbacks,
               DatabaseTask.ResultTask{
    //Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    public DrawerFragment mDrawerFragment;

    //Used to store the last screen title.
    private CharSequence mTitle;
    private int mContentPosition = MetaData.CONTENT_POSITION_INIT;
    private int mIncreaseMinutes;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "enter onCreate");

        setStrictMode(false);

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException (Thread thread, Throwable e) {
                e.printStackTrace();
            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseTask.mResultTask = this;
        BookInfoAdapter.mCallbacks = this;

        mDrawerFragment = (DrawerFragment)getSupportFragmentManager().
                findFragmentById(R.id.navigation_drawer);

        mTitle = getTitle();

        // Set up the drawer.
        mDrawerFragment.setUp(
            R.id.navigation_drawer,
            (DrawerLayout) findViewById(R.id.drawer_layout));

        handleStatisticInfo(new StatisticInfo(), MetaData.QUERY_AND_INSERT);

        Log.d(TAG, "leave onCreate");
    }

    /*
    *处理左边的DrawerNavigation选中某一个条目时的界面变化。
    * 现在看起来界面都一样，需要区分三种不同的书籍状态.
    */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.d(TAG, "enter onNavigationDrawerItemSelected");
        Fragment fragment;
        switch (position){
            case MetaData.DRAWER_POSITION_HOME:
                fragment = new HomeFragment();
                break;
            case MetaData.DRAWER_POSITION_READING:
            case MetaData.DRAWER_POSITION_WANT:
            case MetaData.DRAWER_POSITION_FINISHED:
                fragment = ContentFragment.newInstance(position + 1);
                break;
            default:
                throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();

        Log.d(TAG, "leave onNavigationDrawerItemSelected");
    }

    public void onSectionAttached(int number) {
        Log.d(TAG, "enter onSectionAttached");
        switch (number) {
        case MetaData.CONTENT_POSITION_READING:
            mTitle = getString(R.string.title_section_reading);
            break;
        case MetaData.CONTENT_POSITION_WANT:
            mTitle = getString(R.string.title_section_want);
            break;
        case MetaData.CONTENT_POSITION_FINISHED:
            mTitle = getString(R.string.title_section_finished);
            break;
        default:
            throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }
        mContentPosition = number;
        Log.d(TAG, "enter onSectionAttached");
    }

    public void restoreActionBar() {
        Log.d(TAG, "enter restoreActionBar");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        Log.d(TAG, "leave restoreActionBar");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "enter onCreateOptionsMenu");
        if (!mDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.global, menu);
            restoreActionBar();
            return true;
        }

        Log.d(TAG, "leave onCreateOptionsMenu");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "enter onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.exit_app:
                finish();
                System.exit(0);
                return true;
        }

        Log.d(TAG, "leave onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    /*
     *添加一本书
     */
    void addBook() {
        Log.d(TAG, "enter addBook");
        int position = mDrawerFragment.getSelectedPosition();
        switch (position) {
        case MetaData.DRAWER_POSITION_READING:
            createAddBookDialog(R.layout.dialog_add_reading,
                                getString(R.string.title_reading));
            break;
        case MetaData.DRAWER_POSITION_WANT:
            createAddBookDialog(R.layout.dialog_add_want,
                                getString(R.string.title_want));
            break;
        case MetaData.DRAWER_POSITION_FINISHED:
            createAddBookDialog(R.layout.dialog_add_finished,
                                getString(R.string.title_finished));
            break;
        default:
            throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }
        Log.d(TAG, "leave addBook");
    }

    /*
     *创建添加一本书的对话框
     */
    void createAddBookDialog(final int resource, String title) {
        Log.d(TAG, "enter createAddBookDialog");

        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.buildDialog(resource, title);

        final Button positiveButton = customDialog.mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        setAddDialogButtonListener(positiveButton, customDialog, resource);

        Log.d(TAG, "leave createAddBookDialog");
    }

    /*
      *set positive button onclickListener
      */
    private void setAddDialogButtonListener(Button positiveButton,
                                            final CustomDialog customDialog, final int resource) {
        Log.d(TAG, "enter setAddDialogButtonListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BookInfo bookInfo = new BookInfo();
                bookInfo.getBookInfo(customDialog.mDialogView, resource);
                if (bookInfo.isBookInfoLegal(customDialog.mDialogView, resource)) {
                    addBookInfo(bookInfo);
                    //Dismiss once everything is OK.
                    customDialog.mDialog.dismiss();
                }
                //如果校验不ok,就不可以dismiss,继续显示当前界面，让用户再次输入;
            }
        });
        Log.d(TAG, "leave setAddDialogButtonListener");
    }

    public void addBookInfo(BookInfo bookInfo){
        Log.d(TAG, "enter addBookInfo");
        mIncreaseMinutes = bookInfo.mMinutes + bookInfo.mHours * 60;
        TaskParam bookInfoParam = new TaskParam(
                bookInfo.setContentValues(),
                MetaData.INSERT,
                getTableName(mContentPosition),
                MetaData.KEY_BOOK_NAME,
                bookInfo.mBookName,
                null);
        DatabaseTask bookInfoTask = new DatabaseTask();
        bookInfoTask.execute(bookInfoParam);

        if((mContentPosition == MetaData.CONTENT_POSITION_FINISHED) ||
                (mContentPosition == MetaData.CONTENT_POSITION_READING)){
            handleStatisticInfo(new StatisticInfo(), MetaData.QUERY_AND_UPDATE);
        }

        Log.d(TAG, "leave addBookInfo");
    }

    /*
     *检测内存泄漏以及可能造成ANR的操作。
     */
    private void setStrictMode(boolean developMode) {
        Log.d(TAG, "enter setStrictMode");
        if (developMode) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                       .detectAll()   // or .detectAll() for all detectable problems
                                       .penaltyLog()
                                       .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        Log.d(TAG, "leave setStrictMode");
    }

    public void startTimerService(final String name, final String tableName, final int period){
        Log.d(TAG, "enter startTimerService");
        Intent intent = new Intent(MetaData.ACTION_FOREGROUND);
        intent.setClass(this, TimerService.class);
        intent.putExtra(MetaData.EXTRA_NAME, name);
        intent.putExtra(MetaData.EXTRA_PERIOD, period);
        startService(intent);
        Log.d(TAG, "leave startTimerService");
    }

    public void stopTimerService(){
        Log.d(TAG, "enter stopTimerService");

        stopService(new Intent(this, TimerService.class));

        Log.d(TAG, "leave stopTimerService");
    }

    /*
     *从NavigationDrawer的选中位置获取数据库的表名
     */
    public static String getTableName(int position) {
        Log.d(TAG, "enter getTableName");
        String rv = "";

        switch(position) {
            case MetaData.CONTENT_POSITION_HOME:
                rv = MetaData.SQLite_TABLE_STATISTIC;
                break;
            case MetaData.CONTENT_POSITION_READING:
                rv = MetaData.SQLite_TABLE_READING;
                break;
            case MetaData.CONTENT_POSITION_WANT:
                rv = MetaData.SQLite_TABLE_WANT;
                break;
            case MetaData.CONTENT_POSITION_FINISHED:
                rv = MetaData.SQLite_TABLE_FINISHED;
                break;
            default:
                System.out.println("unexpected position: "+position);
                break;
        }

        Log.d(TAG, "leave getTableName");
        return rv;
    }

    /*
     实现DatabaseTask中的接口
     */
    public void doResultTask(Cursor cursor, int operation){
        switch (operation){
            case MetaData.QUERY_AND_INSERT:
                if(!cursor.moveToFirst()){
                    TaskParam insertParam = new TaskParam(
                            new StatisticInfo().setContentValues(),
                            MetaData.INSERT,
                            MetaData.SQLite_TABLE_STATISTIC,
                            null,
                            null,
                            null);
                    DatabaseTask insertTask = new DatabaseTask();
                    insertTask.execute(insertParam);
                }
                break;
            case MetaData.QUERY_AND_UPDATE:
                StatisticInfo statisticInfo = BookCP.getStatisticInfo(cursor);
                statisticInfo.mStatisticMinutes += mIncreaseMinutes;
                statisticInfo.mTimeString = statisticInfo.getTimeString();
                handleStatisticInfo(statisticInfo, MetaData.UPDATE);
                break;
            default:
                throw new IllegalArgumentException(getString(R.string.illegal_argument));
        }
    }

    private void handleStatisticInfo(StatisticInfo statisticInfo, int operation){
        TaskParam statisticInfoParam = new TaskParam(
                statisticInfo.setContentValues(),
                operation,
                MetaData.SQLite_TABLE_STATISTIC,
                MetaData.KEY_CATEGORY_NAME,
                MetaData.STATISTIC_TOTAL,
                MetaData.KEY_CATEGORY_NAME);
        DatabaseTask statisticInfoTask = new DatabaseTask();
        statisticInfoTask.execute(statisticInfoParam);
    }
}
