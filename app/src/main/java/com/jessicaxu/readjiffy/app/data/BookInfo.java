package com.jessicaxu.readjiffy.app.data;

import android.app.Activity;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;

import com.jessicaxu.readjiffy.app.R;

import java.io.Serializable;
import java.text.NumberFormat;

/**
 * Created by root on 14-7-24.
 */
public class BookInfo implements Serializable {
    public String mBookName;
    public String mAuthor;
    public String mPercent;
    public String mTime;
    public String mStatus;

    public int mReadPage;
    public int mTotalPage;
    public int mReadHours;
    public int mReadMinutes;
    public int mReadSeconds;

    //private final String CLASSNAME = "BookInfo";
    //private TraceLog mTraceLog = new TraceLog(CLASSNAME);
    public BookInfo(){
        //mTraceLog.printEntrance("BookInfo()");

        final int DEFAULT_INT = 0;
        final String DEFAULT_STRING = "";

        mBookName = DEFAULT_STRING;
        mAuthor = DEFAULT_STRING;
        mPercent = DEFAULT_STRING;
        mTime = DEFAULT_STRING;
        mStatus = DEFAULT_STRING;

        mReadPage = DEFAULT_INT;
        mTotalPage = DEFAULT_INT;
        mReadHours = DEFAULT_INT;
        mReadMinutes = DEFAULT_INT;
        mReadSeconds = DEFAULT_INT;
        //mTraceLog.printExit("BookInfo()");
    }
    public BookInfo(Activity activity){
        //mTraceLog.printEntrance("BookInfo(Activity activity)");
        //从EditText获取String数据
        mBookName = getEditTextString(activity, R.id.bookName);
        mAuthor = getEditTextString(activity, R.id.author);

        //从Spinner获取选择结果
        Spinner spinner = (Spinner)activity.findViewById(R.id.stateSpinner);
        mStatus = spinner.getSelectedItem().toString();

        //从EditText获取整型数据
        mReadPage = getEditTextInt(activity, R.id.readPage);
        mTotalPage = getEditTextInt(activity, R.id.totalPage);
        mReadHours = getEditTextInt(activity, R.id.readHours);
        mReadMinutes = getEditTextInt(activity, R.id.readMinutes);
        mReadSeconds = getEditTextInt(activity, R.id.readSeconds);

        //数据预处理
        NumberFormat nt = NumberFormat.getPercentInstance();
        //设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(0);

        mPercent = "("+mReadPage+"/"+mTotalPage+", "+
                nt.format((double)mReadPage/mTotalPage)+")";
        mTime = String.format("%02d:%02d:%02d", mReadHours,
                mReadMinutes, mReadSeconds);
        //mTraceLog.printExit("BookInfo(Activity activity)");
    }

    public BookInfo(Intent addBookIntent){
        //mTraceLog.printEntrance("BookInfo(Intent addBookIntent)");
        final int DEFAULT_VALUE = 0;
        //获取数据
        mBookName = addBookIntent.getExtras().getString("bookName");
        mAuthor = addBookIntent.getExtras().getString("author");
        mStatus = addBookIntent.getExtras().getString("status");
        mReadPage = addBookIntent.getIntExtra("readPage", DEFAULT_VALUE);
        mTotalPage = addBookIntent.getIntExtra("totalPage", DEFAULT_VALUE);
        mReadHours = addBookIntent.getIntExtra("readHours", DEFAULT_VALUE);
        mReadMinutes = addBookIntent.getIntExtra("readMinutes", DEFAULT_VALUE);
        mReadSeconds = addBookIntent.getIntExtra("readSeconds", DEFAULT_VALUE);
        mPercent = addBookIntent.getExtras().getString("percent");
        mTime = addBookIntent.getExtras().getString("time");
        //mTraceLog.printExit("BookInfo(Intent addBookIntent)");
    }

    public boolean validateBookInfo(Activity activity){
        //mTraceLog.printEntrance("validateBookInfo");
        boolean isOk = true;

        //validate data
        EditText bookNameET = (EditText)activity.findViewById(R.id.bookName);
        //EditText authorET = (EditText)activity.findViewById(R.id.author);
        EditText readPageET = (EditText)activity.findViewById(R.id.readPage);
        EditText totalPageET = (EditText)activity.findViewById(R.id.totalPage);
        EditText readHoursET = (EditText)activity.findViewById(R.id.readHours);
        EditText readMinutesET = (EditText)activity.findViewById(R.id.readMinutes);
        EditText readSecondsET = (EditText)activity.findViewById(R.id.readSeconds);

        //校验书名，不能为空或全空格
        //TODO: 访问数据库，检查书名是否唯一
        if(mBookName.equals("") || (mBookName.trim().equals(""))){
            bookNameET.setError("书名是空或者是空格，请输入正确的书名。");
            isOk = false;
        }

        //校验分钟数不能超过59
        if(mReadMinutes > 59){
            readMinutesET.setError("分钟数不能超过60.");
            isOk = false;
        }
        //校验秒数不能超过59
        if(mReadSeconds > 59){
            readSecondsET.setError("秒数不能超过60.");
            isOk = false;
        }

        //阅读状态相关的校验
        if(mStatus.equals("在读")){
            if((mReadPage < mTotalPage)&&(mReadPage!= 0)){
                ;
            }
            else{
                readPageET.setError("已读的页数要小于总页数，并且不为0。");
                isOk = false;
            }

            if((mReadHours == 0) && (mReadMinutes == 0) && (mReadSeconds == 0)){
                readHoursET.setError("“在读”状态下，阅读的小时，分钟，秒数不能都为0");
                readMinutesET.setError("“在读”状态下，阅读的小时，分钟，秒数不能都为0");
                readSecondsET.setError("“在读”状态下，阅读的小时，分钟，秒数不能都为0");
                isOk = false;
            }
        }
        else if(mStatus.equals("想读")){
            /*不能限定的这么严格，因为可能出现一本书很久以前读过一部分，想接着读下去的情况。
            if((readPage != 0) || (totalPage <= 0){
                isOk = false;
            }
            */
        }
        else{
            //校验已经完成阅读的情况
            if((mReadPage == mTotalPage) && (mTotalPage !=0 )){
                ;
            }
            if((mReadHours == mReadMinutes) &&(mReadMinutes == mReadSeconds) &&(mReadMinutes == 0) ){
                readHoursET.setError("“已读”状态下，阅读的小时，分钟，秒数不能都为0");
                readMinutesET.setError("“已读”状态下，阅读的小时，分钟，秒数不能都为0");
                readSecondsET.setError("“已读”状态下，阅读的小时，分钟，秒数不能都为0");
                isOk = false;
            }
        }
        //mTraceLog.printExit("validateBookInfo");
        return isOk;
    }

    public Intent putDataToIntent(){
        //mTraceLog.printEntrance("putDataToIntent");
        Intent saveIntent = new Intent();

        saveIntent.putExtra("bookName", mBookName);
        saveIntent.putExtra("author", mAuthor);
        saveIntent.putExtra("status", mStatus);
        saveIntent.putExtra("readPage", mReadPage);
        saveIntent.putExtra("totalPage", mTotalPage);
        saveIntent.putExtra("readHours", mReadHours);
        saveIntent.putExtra("readMinutes", mReadMinutes);
        saveIntent.putExtra("readSeconds", mReadSeconds);
        saveIntent.putExtra("percent", mPercent);
        saveIntent.putExtra("time", mTime);
        //mTraceLog.printExit("putDataToIntent");
        return saveIntent;
    }

    /*
     *从EditText获取String
     */
    private String getEditTextString(Activity activity, int id){
        //mTraceLog.printEntrance("getEditTextString");
        EditText editText= (EditText)activity.findViewById(id);
        String rv = editText.getText().toString();
        //mTraceLog.printExit("getEditTextString");
        return rv;
    }

    /*
     *从EditText获取int
     */
    private int getEditTextInt(Activity activity, int id){
        //mTraceLog.printEntrance("getEditTextInt");
        EditText editText = (EditText)activity.findViewById(id);

        //从控件得到字符串，转换成整型。
        int retValue = Integer.parseInt(editText.getText().toString());
        //mTraceLog.printExit("getEditTextInt");
        return retValue;
    }

}
