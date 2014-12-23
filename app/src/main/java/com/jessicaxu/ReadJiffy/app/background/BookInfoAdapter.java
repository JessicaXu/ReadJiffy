package com.jessicaxu.ReadJiffy.app.background;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jessicaxu.ReadJiffy.app.R;
import com.jessicaxu.ReadJiffy.app.data.*;
import com.jessicaxu.ReadJiffy.app.ui.*;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;

//ADD THIS CLASS BY JESSICAXU, CUSTOM MY OWN ListView.
public class BookInfoAdapter extends DataAdapter {
    private boolean mIsTiming = false;
    private MainActivity mMainActivity;
    private Calendar mStartCalendar;
    private int mContentPosition;
    private CursorAdapterCallbacks mCallbacks = null;
    private static final String TAG = "BookInfoAdapter";
    /*
     *构造函数
     */
    public BookInfoAdapter(Context context, MainActivity mainActivity,
                           int layout, String[] from, int[] to, int contentPosition){
        super(context, layout, null, from, to, 0);
        mMainActivity = mainActivity;
        mContentPosition = contentPosition;
        mCallbacks = mMainActivity;
    }

    /*
     * 根据position获取一个ListView的Item
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "enter getView");
        //get reference to the row
        View itemView = super.getView(position, convertView, parent);
        switch (mContentPosition){
            case MetaData.CONTENT_POSITION_READING:
                setReadingClickListener(itemView);
                break;
            case MetaData.CONTENT_POSITION_WANT:
                setWantClickListener(itemView);
                break;
            case MetaData.CONTENT_POSITION_FINISHED:
                setFinishedListener(itemView);
                break;
            default:
                throw new IllegalArgumentException(mMainActivity.getString(R.string.illegal_argument));
        }

        //自定义Item颜色
        setItemColor(position, itemView);
        Log.d(TAG, "leave getView");
        return itemView;
    }

    /*
     *设置自定义的条目颜色
     */
    private void setItemColor(int position, View itemView) {
        Log.d(TAG, "enter setItemColor");
        if (position % 2 == 0) {
            itemView.setBackgroundColor(MetaData.READ_JIFFY_ACCENT1);
        }
        else {
            itemView.setBackgroundColor(MetaData.READ_JIFFY_ACCENT2);
        }
        Log.d(TAG, "leave setItemColor");
    }

    /*
     *设置正在阅读状态的ListView的各个View的监听器
     */
    private void setReadingClickListener(View itemView){
        Log.d(TAG, "enter setReadingClickListener");
        //click timer information
        onClickTimer(itemView);

        //click delete
        onClickDelete(itemView, MetaData.SQLite_TABLE_READING);

        //click percent
        onClickPercent(itemView);

        //click menu button
        onClickMenuBtn(itemView);
        Log.d(TAG, "leave setReadingClickListener");
    }

    private void setWantClickListener(View itemView){
        Log.d(TAG, "enter setWantClickListener");
        //start read
        onClickStartRead(itemView);

        //delete
        onClickDelete(itemView, MetaData.SQLite_TABLE_WANT);
        Log.d(TAG, "leave setWantClickListener");
    }

    private void setFinishedListener(View itemView){
        Log.d(TAG, "enter setFinishedListener");
        //delete
        onClickDelete(itemView, MetaData.SQLite_TABLE_FINISHED);
        Log.d(TAG, "leave setFinishedListener");
    }

    /*
     *设置Timer TextView的监听器
     */
    private void onClickTimer(final View itemView){
        Log.d(TAG, "enter onClickTimer");
        final TextView timerText = (TextView)itemView.findViewById(R.id.timerText);
        timerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTiming(itemView)) {
                    startTimer(getBookName(itemView),
                            MetaData.SQLite_TABLE_READING,
                            0);
                } else {
                    stopTimer();
                }
            }
        });
        Log.d(TAG, "leave  onClickTimer");
    }

    /*
     *设置删除按钮的点击响应函数
     */
    private void onClickDelete(final View itemView, final String tableName) {
        Log.d(TAG, "enter onClickDelete");
        final ImageButton delButton = (ImageButton)itemView.findViewById(R.id.delButton);
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果当前的条目正在进行计时，先停止计时。防止出现计时查找不到相应数据的异常。
                TextView timerView = (TextView)itemView.findViewById(R.id.timerText);
                if(!timerView.getText().toString().equals(mMainActivity.getString(R.string.init_time))){
                    stopTimer();
                }

                final CustomDialog customDialog = new CustomDialog(mMainActivity);
                customDialog.buildDialog(R.layout.dialog_alert,
                        mMainActivity.getString(R.string.delete));

                final String bookName = getBookName(itemView);
                Button positiveButton = customDialog.mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                setDeleteDialogButtonListener(positiveButton, customDialog, bookName, tableName);
            }
        });
        Log.d(TAG, "leave onClickDelete");
    }

    /*
     *设置删除对话框的确认按钮监监听器
     */
    private void setDeleteDialogButtonListener(Button positiveButton, final CustomDialog customDialog,
                                               final String bookName, final String tableName) {
        Log.d(TAG, "enter setDeleteDialogButtonListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookInfo bookInfo = new BookInfo();
                bookInfo.mBookName = bookName;
                deleteBookInfo(bookInfo, tableName);
                customDialog.mDialog.dismiss();
            }
        });
        Log.d(TAG, "leave setDeleteDialogButtonListener");
    }

    public void deleteBookInfo(BookInfo bookInfo, String tableName){
        Log.d(TAG, "enter deleteBookInfo");
        TaskParam taskParam = new TaskParam(
                bookInfo.setContentValues(),
                MetaData.OPERATION_DELETE,
                tableName,
                mMainActivity,
                MetaData.KEY_BOOK_NAME,
                bookInfo.mBookName,
                MetaData.KEY_BOOK_NAME);
        DatabaseTask tct = new DatabaseTask();
        tct.execute(taskParam);
        Log.d(TAG, "leave deleteBookInfo");
    }

    /*
     *用来判断当前的ListView的Item是否正在进行计时
     * 虽然只有一句话，但还是写一个函数放在这里，以防将来有什么变化。
     */
    private boolean isTiming(View itemView) {
        Log.d(TAG, "enter isTiming");
        BookInfo bookInfo = getViewInfo(itemView);
        boolean rv;
        if((!bookInfo.mTimerSeconds.equals(mMainActivity.getString(R.string.init_time)))
                || mIsTiming){
            rv = true;
        }
        else{
            rv = false;
        }
        Log.d(TAG, "leave isTiming");
        return rv;
    }

    /*
     * 点击和长按timerText的时候开始计时
     */
    private void startTimer(final String name,
            final String tableName,
            final int period) {
        Log.d(TAG, "enter startTimer");
        mIsTiming = true;
        if(period == 0) {
            mStartCalendar = Calendar.getInstance();
        }
        mCallbacks.startTimerService(name, tableName, period);

        Toast.makeText(mMainActivity.getApplicationContext(),
                mMainActivity.getString(R.string.start_timer),
                Toast.LENGTH_SHORT).show();

        Log.d(TAG, "leave startTimer");
    }

    /*
     * 点击和长按timerText的时候停止计时
     */
    private void stopTimer() {
        Log.d(TAG, "enter stopTimer");

        mCallbacks.stopTimerService();
        mIsTiming = false;

        Toast.makeText(mMainActivity.getApplicationContext(),
                mMainActivity.getString(R.string.stop_timer),
                Toast.LENGTH_SHORT).show();
        Log.d(TAG, "leave stopTimer");
    }

    public static interface CursorAdapterCallbacks{
        void startTimerService(String name, String tableName, int period);
        void stopTimerService();
    }

    /*
     *get bookName from view
     */
    private String getBookName(View itemView) {
        Log.d(TAG, "enter getBookName");
        TextView bookNameTx = (TextView)itemView.findViewById(R.id.bookNameText);
        String rv = bookNameTx.getText().toString();
        Log.d(TAG, "leave getBookName");
        return rv;
    }

    /*
     *获取条目的作者
     */
    private String getAuthor(View itemView) {
        Log.d(TAG, "enter getAuthor");
        TextView authorView = (TextView)itemView.findViewById(R.id.authorText);
        String rv = authorView.getText().toString();
        Log.d(TAG, "leave getAuthor");
        return rv;
    }

    /*
     *获取已读页数
     */
    private int getReadPages(View itemView) {
        Log.d(TAG, "enter getReadPages");
        TextView readPageView = (TextView)itemView.findViewById(R.id.readPageText);
        int rv = Integer.parseInt(readPageView.getText().toString());
        Log.d(TAG, "leave getReadPages");
        return rv;
    }

    /*
     *获得总页数
     */
    private int getTotalPages(View itemView) {
        Log.d(TAG, "enter getTotalPages");
        TextView totalPageView = (TextView)itemView.findViewById(R.id.totalPageText);
        int rv = Integer.parseInt(totalPageView.getText().toString());
        Log.d(TAG, "leave getTotalPages");
        return rv;
    }

    /*
     *获取时间字符串
     */
    private String getTimeString(View itemView) {
        Log.d(TAG, "enter getTimeString");
        TextView totalTimeView = (TextView)itemView.findViewById(R.id.totalTimeText);
        String rv = totalTimeView.getText().toString();
        Log.d(TAG, "leave getTimeString");
        return rv;
    }

    /*
     *获取计时器的显示
     */
    private String getTimerSeconds(View itemView) {
        Log.d(TAG, "enter getTimerSeconds");
        TextView timerSecondsView = (TextView)itemView.findViewById(R.id.timerText);
        String rv = timerSecondsView.getText().toString();
        Log.d(TAG, "leave getTimerSeconds");
        return rv;
    }

    /*
    *获取ListView的Item中的某个TextView所在Item对应的BookInfo
    */
    private BookInfo getViewInfo(View itemView) {
        Log.d(TAG, "enter getViewInfo");
        BookInfo bookInfo = new BookInfo();
        bookInfo.mBookName = getBookName(itemView);
        bookInfo.mAuthor = getAuthor(itemView);
        switch(mContentPosition){
            case MetaData.CONTENT_POSITION_READING:
                bookInfo.mReadPage = getReadPages(itemView);
                bookInfo.mTotalPage = getTotalPages(itemView);
                bookInfo.mPercent = bookInfo.getPercent();
                bookInfo.mTimeString = getTimeString(itemView);
                bookInfo.mMinutes = bookInfo.getMinutesNumber();
                bookInfo.mHours = bookInfo.getHoursNumber();
                bookInfo.mTimerSeconds = getTimerSeconds(itemView);
                break;
            case MetaData.CONTENT_POSITION_WANT:
                bookInfo.mTotalPage = getTotalPages(itemView);
                break;
            case MetaData.CONTENT_POSITION_FINISHED:
                bookInfo.mTimeString = getTimeString(itemView);
                bookInfo.mMinutes = bookInfo.getMinutesNumber();
                bookInfo.mHours = bookInfo.getHoursNumber();
                break;
            default:
                throw new IllegalArgumentException(mMainActivity.getString(R.string.illegal_argument));
        }
        Log.d(TAG, "leave getViewInfo");
        return bookInfo;
    }

    /*
     *当点击百分比时，进行修改百分比的操作
     */
    public void onClickPercent(final View itemView) {
        Log.d(TAG, "enter onClickPercent");
        LinearLayout percentView = (LinearLayout)itemView.findViewById(R.id.readPercent);
        percentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取到当前view所在的ListView的Item的对应信息,保存在bookInfo中。
                final BookInfo bookInfo = getViewInfo(itemView);
                final CustomDialog customDialog = new CustomDialog(mMainActivity);
                customDialog.buildDialog(
                        R.layout.dialog_set_percent,
                        mMainActivity.getString(R.string.read_page_title));
                final Button positiveButton = customDialog.mDialog.
                        getButton(AlertDialog.BUTTON_POSITIVE);
                setPercentDialogButtonListener(positiveButton, customDialog, bookInfo);
            }
        });
        Log.d(TAG, "leave onClickPercent");
    }

    /*
     *设置百分比对话框的确认按钮监听器
     */
    private void setPercentDialogButtonListener(Button positiveButton,
                                                final CustomDialog customDialog,
                                                final BookInfo bookInfo) {
        Log.d(TAG, "enter setPercentDialogButtonListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText readView = (EditText)customDialog.mDialogView.
                        findViewById(R.id.editReadPage);
                int readPages = Integer.parseInt(readView.getText().toString());

                if(readPages == 0) {
                    readView.setError(mMainActivity.getString(R.string.read_page_error));
                } else if(readPages > bookInfo.mTotalPage) {
                    readView.setError(mMainActivity.getString(R.string.read_page_suggestion) +
                            ":" + bookInfo.mTotalPage);
                } else if(readPages == bookInfo.mTotalPage) {
                    switchStatusToFinish(bookInfo);
                    customDialog.mDialog.dismiss();
                } else {
                    updateReadPercent(bookInfo, readPages);
                    customDialog.mDialog.dismiss();
                }
            }
        });
        Log.d(TAG, "leave setPercentDialogButtonListener");
    }

    /*
     *当书籍的阅读页数等于总页数时，将书籍状态设定为已读
     */
    private void switchStatusToFinish(BookInfo bookInfo) {
        Log.d(TAG, "enter switchStatusToFinish");
        BookInfo tempBookInfo = new BookInfo();
        tempBookInfo.mBookName = bookInfo.mBookName;
        tempBookInfo.mAuthor = bookInfo.mAuthor;
        tempBookInfo.mMinutes = bookInfo.mMinutes;
        tempBookInfo.mHours = bookInfo.mHours;
        tempBookInfo.mTimeString = tempBookInfo.getTimeString();

        TaskParam taskParam1 = new TaskParam(
                tempBookInfo.setContentValues(),
                MetaData.OPERATION_INSERT,
                MetaData.SQLite_TABLE_FINISHED,
                mMainActivity,
                MetaData.KEY_BOOK_NAME,
                tempBookInfo.mBookName,
                null);
        DatabaseTask tct1 = new DatabaseTask();
        tct1.execute(taskParam1);

        TaskParam taskParam2 = new TaskParam(
                bookInfo.setContentValues(),
                MetaData.OPERATION_DELETE,
                MetaData.SQLite_TABLE_READING,
                mMainActivity,
                MetaData.KEY_BOOK_NAME,
                bookInfo.mBookName,
                null);
        DatabaseTask tct2 = new DatabaseTask();
        tct2.execute(taskParam2);

        Toast.makeText(mMainActivity.getApplicationContext(),
                R.string.switch_to_reading,
                Toast.LENGTH_SHORT).show();
        //mMainActivity.mNavigationDrawerFragment.selectItem(MetaData.DRAWER_POSITION_FINISHED);
        Log.d(TAG, "leave switchStatusToFinish");
    }

    /*
     *将新的页码更新到数据库
     */
    private void updateReadPercent(BookInfo bookInfo, int readPages) {
        Log.d(TAG, "enter updateReadPercent");
        bookInfo.mReadPage = readPages;
        bookInfo.mPercent = bookInfo.getPercent();
        TaskParam taskParam = new TaskParam(
                bookInfo.setContentValues(),
                MetaData.OPERATION_UPDATE,
                MetaData.SQLite_TABLE_READING,
                mMainActivity,
                MetaData.KEY_BOOK_NAME,
                bookInfo.mBookName,
                null);
        DatabaseTask tct = new DatabaseTask();
        tct.execute(taskParam);
        Log.d(TAG, "leave updateReadPercent");
    }

    /*
     *设置菜单按钮的响应函数
     */
    public void onClickMenuBtn(final View itemView) {
        Log.d(TAG, "enter onClickMenuBtn");
        final ImageButton menuButton = (ImageButton)itemView.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a PopupMenu, giving it the clicked view for an anchor
                PopupMenu popup = new PopupMenu(mMainActivity, menuButton);

                if(isTiming(itemView)) {
                    //显示包含“stop, stop at, edit, edit running time”的菜单
                    popup.getMenuInflater().inflate(R.menu.stop_menu, popup.getMenu());
                    setStopMenuListener(popup, itemView);
                } else {
                    //显示包含“start, start at, edit”的菜单
                    popup.getMenuInflater().inflate(R.menu.start_menu, popup.getMenu());
                    setStartMenuListener(popup, itemView);
                }
                // Finally show the PopupMenu
                popup.show();
            }
        });
        Log.d(TAG, "leave onClickMenuBtn");
    }

    /*
     *处理正在计时时点击菜单按钮的情况
     */
    private void setStopMenuListener(PopupMenu popup, final View itemView) {
        Log.d(TAG, "enter setStopMenuListener");
        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                switch (menuId) {
                    case R.id.stop:
                        stopTimer();
                        break;
                    case R.id.stop_at:
                        onClickSetTime(itemView);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                mMainActivity.getString(R.string.illegal_argument));
                }
                return true;
            }
        });
        Log.d(TAG, "leave setStopMenuListener");
    }

    /*
     *处理不进行计时时点击菜单按钮的情况
     */
    private void setStartMenuListener(PopupMenu popup, final View itemView) {
        Log.d(TAG, "enter setStartMenuListener");
        // Set a listener so we are notified if a menu item is clicked
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int menuId = menuItem.getItemId();
                switch (menuId) {
                    case R.id.start:
                        BookInfo bookInfo = getViewInfo(itemView);
                        startTimer(bookInfo.mBookName,
                                MetaData.SQLite_TABLE_READING,
                                0);
                        break;
                    case R.id.start_at:
                        onClickSetTime(itemView);
                        break;
                    case R.id.adjust_time:
                        onClickAdjustTime(itemView);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                mMainActivity.getString(R.string.illegal_argument));
                }
                return false;
            }
        });
        Log.d(TAG, "leave setStartMenuListener");
    }


    /*
     *调整开始或结束时间
     */
    void onClickSetTime(View itemView) {
        Log.d(TAG, "enter onClickSetTime");
        //获取到当前view所在的ListView的Item的对应信息,保存在bookInfo中。
        final BookInfo bookInfo = getViewInfo(itemView);
        final CustomDialog customDialog = new CustomDialog(mMainActivity);
        customDialog.buildDialog(
                R.layout.dialog_set_time,
                setDialogTitle(itemView));
        setViewData(customDialog.mDialogView);
        setSuggestionText(customDialog.mDialogView, mMainActivity.getString(R.string.init));
        TimePicker timePicker = (TimePicker)customDialog.mDialogView.findViewById(R.id.timePicker);
        setTimePickerDivider(timePicker);

        final TextView dateTextView = (TextView)customDialog.mDialogView.
                findViewById(R.id.dateText);
        final Calendar calendar = Calendar.getInstance();
        setDateTextViewAppearance(dateTextView);
        setDateTextViewListener(dateTextView, calendar);

        final Button positiveButton = customDialog.mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        setTimeDialogListener(positiveButton, customDialog, calendar, bookInfo, itemView);
        Log.d(TAG, "leave onClickSetTime");
    }

    /*
     *设置date TextView的外观
     */
    private void setDateTextViewAppearance(TextView dateTextView) {
        Log.d(TAG, "enter setDateTextViewAppearance");
        //设置下划线
        Paint p = new Paint();
        p.setColor(MetaData.READ_JIFFY_HIGHLIGHT);
        dateTextView.setPaintFlags(p.getColor());
        dateTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        Log.d(TAG, "leave setDateTextViewAppearance");
    }

    /*
     *设置date TextView的点击响应函数
     */
    private void setDateTextViewListener(final TextView dateTextView, final Calendar calendar) {
        Log.d(TAG, "enter setDateTextViewListener");
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTextView.setBackgroundColor(MetaData.READ_JIFFY_HIGHLIGHT);
                final CustomDialog datePickerDialog = new CustomDialog(mMainActivity);
                datePickerDialog.buildDialog(
                        R.layout.dialog_set_date,
                        mMainActivity.getString(R.string.set_date));

                final TextView dateSuggestionView = (TextView) datePickerDialog.mDialogView.
                        findViewById(R.id.dateSuggestion);
                setDatePickerAppearance(datePickerDialog, calendar, dateSuggestionView);

                Button positiveButton = datePickerDialog.mDialog.
                        getButton(AlertDialog.BUTTON_POSITIVE);
                setDateDialogListener(positiveButton, datePickerDialog,
                        calendar, dateTextView, dateSuggestionView);
            }
        });
        Log.d(TAG, "leave setDateTextViewListener");
    }

    /*
     *设置datePickerDialog的外观
     */
    private void setDatePickerAppearance(CustomDialog datePickerDialog,
                                         Calendar calendar,
                                         TextView dateSuggestionView) {
        Log.d(TAG, "enter setDatePickerAppearance");
        SimpleDateFormat sdf = new SimpleDateFormat(mMainActivity.getString(R.string.date_format));

        dateSuggestionView.setText(mMainActivity.getString(R.string.date_suggestion) +
                sdf.format(calendar.getTime()));
        dateSuggestionView.setTextColor(MetaData.READ_JIFFY_HIGHLIGHT);
        DatePicker datePicker = (DatePicker)datePickerDialog.mDialogView.
                findViewById(R.id.datePicker);
        setDatePickerDivider(datePicker);
        Log.d(TAG, "leave setDatePickerAppearance");
    }

    /*
     *设置DatePickerDialog的确定按钮的监听器
     */
    private void setDateDialogListener(Button positiveButton,
                                       final CustomDialog datePickerDialog,
                                       final Calendar calendar,
                                       final TextView dateTextView,
                                       final TextView dateSuggestionView) {
        Log.d(TAG, "enter setDateDialogListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = (DatePicker) datePickerDialog.mDialogView.
                        findViewById(R.id.datePicker);
                calendar.set(Calendar.YEAR, datePicker.getYear());
                calendar.set(Calendar.MONTH, datePicker.getMonth());
                calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                if (isDateLegal(calendar)) {
                    SimpleDateFormat sdf = new SimpleDateFormat
                            (mMainActivity.getString(R.string.date_format));
                    dateTextView.setText(sdf.format(calendar.getTime()));
                    datePickerDialog.mDialog.dismiss();
                } else {
                    dateSuggestionView.setTextColor(MetaData.READ_JIFFY_RED);
                }
            }
        });
        Log.d(TAG, "leave setDateDialogListener");
    }

    /*
     *设置时间对话框的确认按钮的监听器
     */
    private void setTimeDialogListener(Button positiveButton ,
                                       final CustomDialog customDialog,
                                       final Calendar calendar,
                                       final BookInfo bookInfo,
                                       final View itemView) {
        Log.d(TAG, "enter setTimeDialogListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String str = validateSetTime(customDialog.mDialogView, calendar, itemView);
                if(!str.equals("")) {
                    setSuggestionText(customDialog.mDialogView, str);
                } else {
                    int period = getPeriod(customDialog.mDialogView, calendar);
                    if(!isTiming(itemView)) {
                        //校正数据库时间
                        updateTimeIncrease(period, bookInfo);
                        //开始计时
                        startTimer(bookInfo.mBookName,
                                MetaData.SQLite_TABLE_READING,
                                period);
                    } else {
                        //校正数据库时间
                        updateTimeDecrease(period, bookInfo);
                        //停止计时
                        stopTimer();
                    }
                    customDialog.mDialog.dismiss();
                }
            }
        });
        Log.d(TAG, "leave setTimeDialogListener");
    }

    /*
     *校正时间的增加
     */
    private void updateTimeIncrease(int period, BookInfo bookInfo) {
        Log.d(TAG, "enter updateTimeIncrease");
        int totalMinutes = bookInfo.mMinutes + bookInfo.mHours * 60 + period;
        bookInfo.mMinutes = totalMinutes % 60;
        bookInfo.mHours = totalMinutes / 60;
        bookInfo.mTimeString = bookInfo.getTimeString();
        TaskParam taskParam = new TaskParam(
                bookInfo.setContentValues(),
                MetaData.OPERATION_UPDATE,
                MetaData.SQLite_TABLE_READING,
                mMainActivity,
                MetaData.KEY_BOOK_NAME,
                bookInfo.mBookName,
                null);
        DatabaseTask tct = new DatabaseTask();
        tct.execute(taskParam);
        Log.d(TAG, "leave updateTimeIncrease");
    }

    /*
     *校正时间的减少
     */
    private void updateTimeDecrease(int period, BookInfo bookInfo) {
        Log.d(TAG, "enter updateTimeDecrease");
        int totalMinutes = bookInfo.mMinutes + bookInfo.mHours * 60 - period;
        bookInfo.mMinutes = totalMinutes % 60;
        bookInfo.mHours = totalMinutes / 60;
        bookInfo.mTimeString = bookInfo.getTimeString();
        TaskParam taskParam = new TaskParam(
                bookInfo.setContentValues(),
                MetaData.OPERATION_UPDATE,
                MetaData.SQLite_TABLE_READING,
                mMainActivity,
                MetaData.KEY_BOOK_NAME,
                bookInfo.mBookName,
                null);
        DatabaseTask tct = new DatabaseTask();
        tct.execute(taskParam);
        Log.d(TAG, "leave updateTimeDecrease");
    }

    /*
     *用于自定义Picker的分割线颜色
     */
    private void setDividerColor(NumberPicker picker) {
        Log.d(TAG, "enter setDividerColor");
        Field[] numberPickerFields = NumberPicker.class.getDeclaredFields();
        for (Field field : numberPickerFields) {
            if (field.getName().equals("mSelectionDivider")) {
                field.setAccessible(true);
                try {
                    field.set(picker,
                            mMainActivity.getResources().getDrawable(R.drawable.dialog_divider));
                    System.out.println("set divider color!");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "leave setDividerColor");
    }

    /*
     *自定义TimePicker
     */
    private void setTimePickerDivider(TimePicker timePicker) {
        Log.d(TAG, "enter setTimePickerDivider");
        try {
            Resources system = Resources.getSystem();
            int hourPickerId = system.getIdentifier("hour", "id", "android");
            int minutePickerId = system.getIdentifier("minute", "id", "android");

            NumberPicker hourPicker = (NumberPicker) timePicker.findViewById(hourPickerId);
            NumberPicker minutePicker = (NumberPicker) timePicker.findViewById(minutePickerId);

            setDividerColor(hourPicker);
            setDividerColor(minutePicker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "leave setTimePickerDivider");
    }

    /*
    *自定义DatePicker样设
    */
    private void setDatePickerDivider(DatePicker datePicker) {
        Log.d(TAG, "enter setDatePickerDivider");
        try {
            Field datePickerFields[] = datePicker.getClass().getDeclaredFields();
            for (Field field : datePickerFields) {
                if ("mSpinners".equals(field.getName())) {
                    field.setAccessible(true);
                    Object spinnersObj = field.get(datePicker);
                    LinearLayout mSpinners = (LinearLayout) spinnersObj;
                    NumberPicker monthPicker = (NumberPicker) mSpinners.getChildAt(0);
                    NumberPicker dayPicker = (NumberPicker) mSpinners.getChildAt(1);
                    NumberPicker yearPicker = (NumberPicker) mSpinners.getChildAt(2);
                    setDividerColor(monthPicker);
                    setDividerColor(dayPicker);
                    setDividerColor(yearPicker);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "leave setDatePickerDivider");
    }

    /*
     *校验设置的Date是否符合要求
     */
    private boolean isDateLegal(Calendar calendar) {
        Log.d(TAG, "enter isDateLegal");
        boolean rv = true;
        Calendar curCalendar = Calendar.getInstance();
        resetTime(curCalendar);
        resetTime(calendar);
        if(curCalendar.before(calendar)) {
            rv = false;
        }
        Log.d(TAG, "leave isDateLegal");
        return rv;
    }

    private void resetTime(Calendar calendar){
        Log.d(TAG, "enter resetTime");
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Log.d(TAG, "leave resetTime");
    }

    /*
     *设置DateText和TimePicker默认的数值
     */
    private void setViewData(View dialogView) {
        Log.d(TAG, "enter setViewData");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(mMainActivity.getString(R.string.date_format));
        TextView dateText = (TextView)dialogView.findViewById(R.id.dateText);
        dateText.setText( sdf.format(calendar.getTime()));

        TimePicker timePicker = (TimePicker)dialogView.findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        Log.d(TAG, "leave setViewData");
    }

    /*
     *设置suggestion信息
     */
    private void setSuggestionText(View dialogView, String str) {
        Log.d(TAG, "enter setSuggestionText");
        final TextView suggestionText =
                (TextView)dialogView.findViewById(R.id.suggestion);

        if(str.equals(mMainActivity.getString(R.string.init))) {
            Calendar curCalendar = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat(mMainActivity.getString(R.string.time_format));
            suggestionText.setText(mMainActivity.getString(R.string.time_suggestion)+
                    sdf.format(curCalendar.getTime()));
            suggestionText.setTextColor(MetaData.READ_JIFFY_HIGHLIGHT);
        } else {
            suggestionText.setText(str);
            suggestionText.setTextColor(MetaData.READ_JIFFY_RED);
        }
        Log.d(TAG, "leave setSuggestionText");
    }

    /*
     *获取“设置时间”对话框的标题
     */
    private String setDialogTitle(final View itemView) {
        Log.d(TAG, "enter setDialogTitle");
        String rv;
        if(!isTiming(itemView)) {
            rv = mMainActivity.getString(R.string.start_at);
        } else {
            rv = mMainActivity.getString(R.string.stop_at);
        }
        Log.d(TAG, "leave setDialogTitle");
        return rv;
    }

    /*
     *从时间对话框中获取日历信息
     */
    Calendar getTimeDialogInfo(View dialogView, Calendar calendar) {
        Log.d(TAG, "enter getTimeDialogInfo");
        TimePicker timePicker = (TimePicker)dialogView.findViewById(R.id.timePicker);
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
        Log.d(TAG, "leave getTimeDialogInfo");
        return calendar;
    }

    /*
     *对时间进行校准，用于补上忘记记录的时间
     */
    void onClickAdjustTime(View itemView) {
        Log.d(TAG, "enter onClickAdjustTime");
        final BookInfo bookInfo = getViewInfo(itemView);

        final CustomDialog customDialog = new CustomDialog(mMainActivity);
        customDialog.buildDialog(
                R.layout.dialog_adjust_time,
                mMainActivity.getString(R.string.adjust_time));
        setSpinnerAppearance(customDialog);

        final Button positiveButton = customDialog.mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        setAdjustDialogListener(positiveButton, bookInfo, customDialog);
        Log.d(TAG, "leave onClickAdjustTime");
    }

    /*
     *设置下拉菜单的外观
     */
    private void setSpinnerAppearance(CustomDialog customDialog) {
        Log.d(TAG, "enter setSpinnerAppearance");
        //设置"+"和"-"的下拉菜单
        Spinner spinner = (Spinner) customDialog.mDialogView.findViewById(R.id.operationSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mMainActivity,
                R.array.operationSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Log.d(TAG, "leave setSpinnerAppearance");
    }

    /*
     设置校正时间对话框的监听器
     */
    private void setAdjustDialogListener(Button positiveButton,
                                         final BookInfo bookInfo,
                                         final CustomDialog customDialog) {
        Log.d(TAG, "enter setAdjustDialogListener");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookInfo.isTimeLegal(customDialog.mDialogView)) {
                    String operation = getOperationFromDialog(customDialog);
                    int period = getPeriodFromDialog(customDialog, bookInfo);

                    if ((operation.equals("-")) &&
                            (period > (bookInfo.mMinutes + bookInfo.mHours * 60))) {
                        String suggestion = mMainActivity.getString(R.string.adjust_suggestion) +
                                bookInfo.mTimeString;
                        TextView suggestionText = (TextView) customDialog.mDialogView.
                                findViewById(R.id.suggestion);
                        suggestionText.setText(suggestion);
                    }
                    else {
                        if ((operation.equals("-")) &&
                                (period < (bookInfo.mMinutes + bookInfo.mHours * 60))) {
                            updateTimeDecrease(period, bookInfo);
                        } else if (operation.equals("+")) {
                            updateTimeIncrease(period, bookInfo);
                        } else {
                            throw new IllegalArgumentException(
                                    mMainActivity.getString(R.string.illegal_argument));
                        }
                        customDialog.mDialog.dismiss();
                    }
                }
            }
        });
        Log.d(TAG, "leave setAdjustDialogListener");
    }

    /*
     *获取操作类型是增是减
     */
    private String getOperationFromDialog(CustomDialog customDialog) {
        Log.d(TAG, "enter getOperationFromDialog");
        Spinner spinner = (Spinner) customDialog.mDialogView.
                findViewById(R.id.operationSpinner);
        String rv = spinner.getSelectedItem().toString();
        Log.d(TAG, "leave getOperationFromDialog");
        return rv;
    }

    /*
     *从调整时间对话框获取时间间隔分钟数
     */
    private int getPeriodFromDialog(CustomDialog customDialog, BookInfo bookInfo) {
        Log.d(TAG, "enter getPeriodFromDialog");
        int readHours = bookInfo.
                getEditTextInt(customDialog.mDialogView, R.id.readHours);
        int readMinutes = bookInfo.
                getEditTextInt(customDialog.mDialogView, R.id.readMinutes);
        int rv = readMinutes + readHours * 60;
        Log.d(TAG, "leave getPeriodFromDialog");
        return rv;
    }

    /*
     *对于输入的时间进行校验：
     * 1.输入的时间要早于当前的时间
     * 2.停止时间时不能早于此时间段的开始时间
     */
    private String validateSetTime(View dialogView, Calendar calendar, View itemView) {
        Log.d(TAG, "enter validateSetTime");
        Calendar setCalendar = getTimeDialogInfo(dialogView, calendar);
        Calendar curCalendar = Calendar.getInstance();
        if(setCalendar.after(curCalendar)) {
            return mMainActivity.getString(R.string.time_alarm);
        }
        if(isTiming(itemView) && (setCalendar.before(mStartCalendar))) {
            return mMainActivity.getString(R.string.period_alarm);
        }
        if(!isTiming(itemView)) {
            mStartCalendar = setCalendar;
        }
        Log.d(TAG, "leave validateSetTime");
        return "";
    }

    /*
     * 用来计算设置的阅读开始和结束时刻之间的时间段长度。
     */
    private int getPeriod(View dialogView, Calendar calendar) {
        Log.d(TAG, "enter getPeriod");
        Calendar setCalendar = getTimeDialogInfo(dialogView, calendar);
        Calendar curCalendar = Calendar.getInstance();
        long milliseconds = curCalendar.getTimeInMillis() - setCalendar.getTimeInMillis();

        int rv = (int)milliseconds / 1000 / 60;
        Log.d(TAG, "leave getPeriod");
        return rv;
    }

    /*
  *处于“想读”状态的图书点击"开始阅读"
  */
    public void onClickStartRead(final View itemView) {
        Log.d(TAG, "enter onClickStartRead");
        Button startButton = (Button)itemView.findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookInfo bookInfo = getViewInfo(itemView);
                BookInfo tempBookInfo = new BookInfo();
                tempBookInfo.mBookName = bookInfo.mBookName;
                tempBookInfo.mAuthor = bookInfo.mAuthor;
                tempBookInfo.mTotalPage = bookInfo.mTotalPage;
                tempBookInfo.mReadPage = 1;
                tempBookInfo.mPercent = bookInfo.getPercent();
                tempBookInfo.mMinutes = 0;
                tempBookInfo.mHours = 0;
                tempBookInfo.mTimeString = tempBookInfo.getTimeString();
                TaskParam taskParam1 = new TaskParam(
                        tempBookInfo.setContentValues(),
                        MetaData.OPERATION_INSERT,
                        MetaData.SQLite_TABLE_READING,
                        mMainActivity,
                        MetaData.KEY_BOOK_NAME,
                        tempBookInfo.mBookName,
                        null);
                DatabaseTask tct1 = new DatabaseTask();
                tct1.execute(taskParam1);

                TaskParam taskParam2 = new TaskParam(
                        bookInfo.setContentValues(),
                        MetaData.OPERATION_DELETE,
                        MetaData.SQLite_TABLE_WANT,
                        mMainActivity,
                        MetaData.KEY_BOOK_NAME,
                        bookInfo.mBookName,
                        null);
                DatabaseTask tct2 = new DatabaseTask();
                tct2.execute(taskParam2);
                //mMainActivity.mNavigationDrawerFragment.selectItem(MetaData.DRAWER_POSITION_READING);
            }
        });
        Log.d(TAG, "leave onClickStartRead");
    }
}
