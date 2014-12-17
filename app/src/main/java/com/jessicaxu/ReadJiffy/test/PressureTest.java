package com.jessicaxu.ReadJiffy.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.jessicaxu.ReadJiffy.app.ui.MainActivity;
import com.jessicaxu.ReadJiffy.app.util.BookInfo;
import com.jessicaxu.ReadJiffy.app.util.MetaData;

/**
 * 进行压力测试，向数据库添加大量的数据，模拟实际情况下用户输入大量数据的情况
 */
public class PressureTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public PressureTest() {
        super("com.jessicaxu.ReadJiffy.test", MainActivity.class);
    }

    public void testPatchDel() throws Exception{
        /*
        MainActivity mainActivity = (MainActivity)getActivity();
        for(int i = 0; i < 100; i++){
            BookInfo bookInfo = new BookInfo();
            bookInfo.mBookName = "TestBook" + i;
            bookInfo.mStatus = MetaData.STATUS_READING;
            mainActivity.deleteBookInfo(bookInfo);
            synchronized (this){
                wait(200);
            }
        }
        */
    }

    public void testPatchAdd() throws Exception{
        MainActivity mainActivity = (MainActivity)getActivity();
        for(int i = 0; i < 30000; i++){
            BookInfo bookInfo = new BookInfo();
            bookInfo.mBookName = "ReadingBook" + i;
            bookInfo.mAuthor = "some one";
            bookInfo.mReadPage = 1;
            bookInfo.mTotalPage = 3;
            bookInfo.mPercent = bookInfo.getPercent();
            bookInfo.mHours = 1;
            bookInfo.mMinutes = 0;
            bookInfo.mTimeString = bookInfo.getTimeString();
            mainActivity.addBookInfo(bookInfo);
            synchronized (this){
                wait(60);
            }
            System.out.println(i + "---->Memory Usage:" + mainActivity.getMemoryInfo());
        }
    }
}
