package com.jessicaxu.ReadJiffy.app.data;

import android.content.ContentValues;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.jessicaxu.ReadJiffy.app.R;

import java.text.NumberFormat;

/*
 *用于暂存用户设置的数据，并进行相关的计算和校验。
 */
public class BookInfo {
    public String mBookName;
    public String mAuthor;
    public String mPercent;
    public String mTimeString;
    public String mTimerSeconds;

    public int mReadPage;
    public int mTotalPage;
    public int mMinutes;
    public int mHours;
    private static final String TAG = "BookInfo";

    public BookInfo() {
        Log.d(TAG, "enter BookInfo()");
        setDefaultValue();
        Log.d(TAG, "leave BookInfo()");
    }

    /*
     *根据书籍状态不同，不是所有的字段都进行了设置，先进行默认值的设置。
     */
    private void setDefaultValue() {
        final int DEFAULT_INT = 0;
        final String DEFAULT_STRING = "";

        mBookName = DEFAULT_STRING;
        mAuthor = DEFAULT_STRING;
        mPercent = DEFAULT_STRING;
        mTimeString = DEFAULT_STRING;
        mTimerSeconds = "00:00:00";

        mReadPage = DEFAULT_INT;
        mTotalPage = DEFAULT_INT;
        mMinutes = DEFAULT_INT;
        mHours = DEFAULT_INT;
    }

    /*
     * get BookInfo from dialogView。
     */
    public void getBookInfo(View dialogView, int resource) {
        Log.d(TAG, "enter BookInfo(Activity activity)");

        switch (resource) {
        case R.layout.dialog_add_reading:
            getReadingInfo(dialogView);
            break;
        case R.layout.dialog_add_want:
            setDefaultValue();
            getWantInfo(dialogView);
            break;
        case R.layout.dialog_add_finished:
            setDefaultValue();
            getFinishedInfo(dialogView);
            break;
        default:
            throw new IllegalArgumentException("illegal resource!");
        }

        Log.d(TAG, "leave BookInfo(Activity activity)");
    }

    /*
     *从dialog获取数据
     */
    private void getReadingInfo(View dialogView) {
        mBookName = getEditTextString(dialogView, R.id.bookName);
        mAuthor = getEditTextString(dialogView, R.id.author);
        mReadPage = getEditTextInt(dialogView, R.id.readPage);
        mTotalPage = getEditTextInt(dialogView, R.id.totalPage);
        mMinutes = getEditTextInt(dialogView, R.id.readMinutes);
        mHours = getEditTextInt(dialogView, R.id.readHours);
        mTimeString = getTimeString();
        mPercent = getPercent();
    }

    /*
     *从dialog获取数据
     */
    private void getWantInfo(View dialogView) {
        mBookName = getEditTextString(dialogView, R.id.bookName);
        mAuthor = getEditTextString(dialogView, R.id.author);
        mTotalPage = getEditTextInt(dialogView, R.id.totalPage);
    }
    /*
     *从dialog获取数据
     */
    private void getFinishedInfo(View dialogView) {
        mBookName = getEditTextString(dialogView, R.id.bookName);
        mAuthor = getEditTextString(dialogView, R.id.author);
        mMinutes = getEditTextInt(dialogView, R.id.readMinutes);
        mHours = getEditTextInt(dialogView, R.id.readHours);
        mTimeString = getTimeString();
    }

    /*
 *获取界面上的总时间字符串
 */
    public String getSecondsString(long seconds) {
        return String.format("%02d:%02d:%02d",
                seconds/3600,
                seconds%3600/60,
                seconds%60);
    }


    /*
     *获取界面上的总时间字符串
     */
    public String getTimeString() {
        return String.format("%02d:%02d", mHours, mMinutes);
    }

    /*
     *从总时间字符串解析小时
     */
    public int getHoursNumber() {
        String hoursString = mTimeString.substring(0, mTimeString.indexOf(":"));
        return Integer.parseInt(hoursString);
    }

    /*
     *从总时间字符串解析分钟
     */
    public int getMinutesNumber() {
        String MinutesString = mTimeString.substring(mTimeString.indexOf(":")+1, mTimeString.length());
        return Integer.parseInt(MinutesString);
    }

    /*
     *校验用户输入的图书信息
     */
    public boolean isBookInfoLegal(View dialogView, int resource) {
        switch(resource) {
        case R.layout.dialog_add_reading:
            return isReadingInfoLegal(dialogView);
        case R.layout.dialog_add_want:
            return isWantInfoLegal(dialogView);
        case R.layout.dialog_add_finished:
            return isFinishedInfoLegal(dialogView);
        default:
            throw new IllegalArgumentException("illegal resource!");
        }
    }

    /*
     *校验正在阅读的图书信息
     */
    private boolean isReadingInfoLegal(View dialogView) {
        boolean rv = false;
        if(isBookNameLegal(dialogView) &&
                isTotalPageLegal(dialogView) &&
                isPercentLegal(dialogView) &&
                isTimeLegal(dialogView)) {
            rv = true;
        }

        return rv;
    }

    /*
     *校验想要阅读的图书信息
     */
    private boolean isWantInfoLegal(View dialogView) {
        boolean rv = false;
        if(isBookNameLegal(dialogView) &&
                isTotalPageLegal(dialogView)) {
            rv = true;
        }

        return rv;
    }

    /*
     *校验已经阅读的图书信息
     */
    private boolean isFinishedInfoLegal(View dialogView) {
        boolean rv = false;
        if(isBookNameLegal(dialogView) &&
                isTimeLegal(dialogView)) {
            rv = true;
        }

        return rv;
    }

    /*
     *校验图书名称
     */
    private boolean isBookNameLegal(View dialogView) {
        EditText bookNameET = (EditText)dialogView.findViewById(R.id.bookName);
        //校验书名，不能为空或全空格
        if(isBlank(mBookName)) {
            bookNameET.setError("书名不能为空！");
            return false;
        }
        return true;
    }

    /*
     *校验总页数
     */
    private boolean isTotalPageLegal(View dialogView) {
        EditText totalPageET = (EditText)dialogView.findViewById(R.id.totalPage);
        if(isBlank(mTotalPage + "")) {
            totalPageET.setError("书的总页数不能为空");
            return false;
        }
        return true;
    }

    /*
     *校验阅读进度百分比
     */
    private boolean isPercentLegal(View dialogView) {
        EditText readPageET = (EditText)dialogView.findViewById(R.id.readPage);
        EditText totalPageET = (EditText)dialogView.findViewById(R.id.totalPage);

        if(isBlank(mReadPage + "")) {
            readPageET.setError("已读页数不能为空");
            return false;

        }
        if((mReadPage > mTotalPage) || (mReadPage == mTotalPage)) {
            readPageET.setError("已读的页数要小于总页数");
            return false;
        }
        if((mTotalPage < 0) || (mTotalPage == 0)) {
            totalPageET.setError("书的总页数要大于0");
            return false;
        }
        return true;
    }

    /*
     *校验阅读时间
     */
    public boolean isTimeLegal(View dialogView) {
        EditText readHoursET = (EditText)dialogView.findViewById(R.id.readHours);
        EditText readMinutesET = (EditText)dialogView.findViewById(R.id.readMinutes);
        int readHours = getEditTextInt(dialogView, R.id.readHours);
        int readMinutes = getEditTextInt(dialogView, R.id.readMinutes);

        if(isBlank(readMinutes + "")) {
            readMinutesET.setError("分钟不能为空");
            return false;
        }
        //校验分钟数不能超过59
        if(readMinutes > 59) {
            readMinutesET.setError("分钟数不能超过59.");
            return false;
        }
        if(isBlank(readHours + "")) {
            readHoursET.setError("小时不能为空");
            return false;
        }
        if((readHours == 0) && (readMinutes == 0)) {
            readHoursET.setError("小时，分钟不能都为0");
            readMinutesET.setError("小时，分钟不能都为0");
            return false;
        }
        return true;
    }

    /*
     *从EditText获取String
     */
    private String getEditTextString(View dialogView, int id) {
        Log.d(TAG, "enter getEditTextString");
        EditText editText= (EditText)dialogView.findViewById(id);
        String rv = editText.getText().toString();
        Log.d(TAG, "leave getEditTextString");
        return rv;
    }

    /*
     *从EditText获取int
     */
    public int getEditTextInt(View dialogView, int id) {

        EditText editText = (EditText)dialogView.findViewById(id);
        //从控件得到字符串，转换成整型。
        String s = editText.getText().toString();
        if(isBlank(s)) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    }

    /*
     *从阅读数量获取阅读进度百分比字符串
     */
    public String getPercent() {

        //数据预处理
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(0);

        return nt.format((double)mReadPage/mTotalPage);
    }

    /*
     *校验输入是否为空
     */
    private boolean isBlank(String s) {
        return s.equals("") || (s.trim().equals(""));
    }

    /*
    *将BookInfo封装到ContentValues中
    */
    public ContentValues setContentValues() {
        Log.d(TAG, "enter getContentValues");
        ContentValues contentValues = new ContentValues();

        contentValues.put(MetaData.KEY_BOOK_NAME, mBookName);
        contentValues.put(MetaData.KEY_AUTHOR, mAuthor);
        contentValues.put(MetaData.KEY_READ_PAGE, mReadPage);
        contentValues.put(MetaData.KEY_TOTAL_PAGE, mTotalPage);
        contentValues.put(MetaData.KEY_MINUTES, mMinutes);
        contentValues.put(MetaData.KEY_HOURS, mHours);
        contentValues.put(MetaData.KEY_TIME_STRING, mTimeString);
        contentValues.put(MetaData.KEY_PERCENT, mPercent);
        contentValues.put(MetaData.KEY_TIMER_SECONDS, mTimerSeconds);

        Log.d(TAG, "leave getContentValues");
        return contentValues;
    }
}
