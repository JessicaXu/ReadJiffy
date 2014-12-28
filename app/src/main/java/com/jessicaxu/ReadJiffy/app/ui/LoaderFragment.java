package com.jessicaxu.ReadJiffy.app.ui;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.jessicaxu.ReadJiffy.app.background.DataAdapter;
import com.jessicaxu.ReadJiffy.app.data.BookCP;
import com.jessicaxu.ReadJiffy.app.global.MetaData;


public class LoaderFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "LoaderFragment";
    public DataAdapter mDataAdapter;

    /**
     * This method is called when a previously created loader has finished its load.
     * This method is guaranteed to be called prior to the release of the last data that
     * was supplied for this loader. At this point you should remove all use of the old
     * data (since it will be released soon), but should not do your own release of the
     * data since its loader owns it and will take care of that.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "enter onLoadFinished");
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.) update Cursor.
        mDataAdapter.changeCursor(data);
        Log.d(TAG, "leave onLoadFinished");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "enter onLoaderReset");
        mDataAdapter.changeCursor(null);
        Log.d(TAG, "leave onLoaderReset");
    }

    /**
     *返回一个Loader,这个方法不能被手动调用。
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "enter onCreateLoader");
        CursorLoader cursorLoader = null;
        if((id == MetaData.BOOKINFO_LOADER) ||
                (id == MetaData.STATISTICINFO_LOADER)){
            //从参数Bundle中获得数据库表的名称
            int position = args.getInt(MetaData.ARG_POSITION);
            String tableName = MainActivity.getTableName(position);

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
        Log.d(TAG, "leave onCreateLoader");
        return cursorLoader;
    }
}
