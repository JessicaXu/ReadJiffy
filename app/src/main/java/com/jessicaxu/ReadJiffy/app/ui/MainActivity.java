package com.jessicaxu.ReadJiffy.app.ui;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Button;
import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.others.*;
import com.jessicaxu.ReadJiffy.app.util.*;


public class MainActivity extends ActionBarActivity
    implements NavigationDrawerFragment.NavigationDrawerCallbacks,
               CustomCursorAdapter.CursorAdapterCallbacks{
    //Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    public NavigationDrawerFragment mNavigationDrawerFragment;

    //Used to store the last screen title.
    private CharSequence mTitle;
    private int mContentPosition = MetaData.CONTENT_POSITION_INIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TraceLog.printEntrance(getDebugInfo("onCreate"));

        setStrictMode(true);

        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException (Thread thread, Throwable e) {
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

        TraceLog.printExit(getDebugInfo("onCreate"));
    }

    /*
    *处理左边的DrawerNavigation选中某一个条目时的界面变化。
    * 现在看起来界面都一样，需要区分三种不同的书籍状态.
    */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        TraceLog.printEntrance(getDebugInfo("onNavigationDrawerItemSelected"));
        //将MainActivity的BookDatabase传递给ContentFragment.
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
        .replace(R.id.container, ContentFragment.newInstance(position + 1))
        .commit();
        System.out.println(getMemoryInfo());
        TraceLog.printExit(getDebugInfo("onNavigationDrawerItemSelected"));
    }

    public void onSectionAttached(int number) {
        TraceLog.printEntrance(getDebugInfo("onSectionAttached"));
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
        TraceLog.printExit(getDebugInfo("onSectionAttached"));
    }

    public void restoreActionBar() {
        TraceLog.printEntrance(getDebugInfo("restoreActionBar"));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
        TraceLog.printExit(getDebugInfo("restoreActionBar"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TraceLog.printEntrance(getDebugInfo("onCreateOptionsMenu"));
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }

        TraceLog.printExit(getDebugInfo("onCreateOptionsMenu"));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        TraceLog.printEntrance(getDebugInfo("onOptionsItemSelected"));
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
            case R.id.add_book:
                addBook();
                return true;
            case R.id.action_settings:
                return true;
            case R.id.exit_app:
                finish();
                System.exit(0);
                return true;
        }

        TraceLog.printExit(getDebugInfo("onOptionsItemSelected"));
        return super.onOptionsItemSelected(item);
    }

    /*
     *添加一本书
     */
    void addBook() {
        TraceLog.printEntrance(getDebugInfo("addBook"));
        int position = mNavigationDrawerFragment.getSelectedPosition();
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
        TraceLog.printExit(getDebugInfo("addBook"));
    }

    /*
     *创建添加一本书的对话框
     */
    void createAddBookDialog(final int resource, String title) {
        TraceLog.printEntrance(getDebugInfo("createAddBookDialog"));

        final CustomDialog customDialog = new CustomDialog(this);
        customDialog.buildDialog(resource, title);

        final Button positiveButton = customDialog.mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        setAddDialogButtonListener(positiveButton, customDialog, resource);

        TraceLog.printExit(getDebugInfo("createAddBookDialog"));
    }

    /*
      *set positive button onclickListener
      */
    private void setAddDialogButtonListener(Button positiveButton,
                                            final CustomDialog customDialog, final int resource) {
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
    }

    public void addBookInfo(BookInfo bookInfo){
        TaskParam taskParam = new TaskParam(
                bookInfo,
                MetaData.OPERATION_INSERT,
                CustomCompute.getTableName(mContentPosition),
                this);
        TimeConsumeTask tct = new TimeConsumeTask();
        tct.execute(taskParam);
    }

    /*
     *检测内存泄漏以及可能造成ANR的操作。
     */
    private void setStrictMode(boolean developMode) {
        TraceLog.printEntrance(getDebugInfo("setStrictMode"));
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
        TraceLog.printExit(getDebugInfo("setStrictMode"));
    }

    /*
     *获取内存信息
     */
    public float getMemoryInfo() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager =
            (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        android.os.Debug.MemoryInfo[] memoryInfoArray =
            activityManager.getProcessMemoryInfo(new int[] {pid});
        return (float) memoryInfoArray[0].getTotalPrivateDirty() / 1024;
        //System.out.println(s + ":每个app的内存限制是" + activityManager.getMemoryClass() + "M");
    }

    String getDebugInfo(String methodName) {
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return getString(R.string.file_name) + ste.getFileName() +
               getString(R.string.line_number) + ste.getLineNumber() +
               getString(R.string.method_name) + methodName + "\n" +
               getMemoryInfo();
    }

    public void startTimerService(final String name, final String tableName, final int period){
        TraceLog.printEntrance(getDebugInfo("startTimerService"));
        Intent intent = new Intent(MetaData.ACTION_FOREGROUND);
        intent.setClass(this, TimerService.class);
        intent.putExtra(MetaData.EXTRA_NAME, name);
        intent.putExtra(MetaData.EXTRA_PERIOD, period);
        startService(intent);
        TraceLog.printExit(getDebugInfo("startTimerService"));
    }

    public void stopTimerService(){
        TraceLog.printEntrance(getDebugInfo("stopTimerService"));

        stopService(new Intent(this, TimerService.class));

        TraceLog.printExit(getDebugInfo("stopTimerService"));
    }
}
