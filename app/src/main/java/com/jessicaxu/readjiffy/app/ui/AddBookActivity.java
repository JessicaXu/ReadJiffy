package com.jessicaxu.readjiffy.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.jessicaxu.readjiffy.app.R;
import com.jessicaxu.readjiffy.app.data.BookInfo;
import com.jessicaxu.readjiffy.app.util.TraceLog;


public class AddBookActivity extends ActionBarActivity {
    private final String CLASSNAME = "AddBookActivity";
    private TraceLog mTraceLog = new TraceLog(CLASSNAME);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTraceLog.printEntrance("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        //add Spinner by JessicaXu
        Spinner spinner = (Spinner) findViewById(R.id.stateSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stateSpinner, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        mTraceLog.printExit("onCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        mTraceLog.printEntrance("onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.add_book, menu);
        mTraceLog.printExit("onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        mTraceLog.printEntrance("onOptionsItemSelected");
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            mTraceLog.printExit("onOptionsItemSelected");
            return true;
        }
        mTraceLog.printExit("onOptionsItemSelected");
        return super.onOptionsItemSelected(item);
    }

    //add by JessicaXu 20140626
    //将数据存储起来，通过Intent传递给NavigationDrawer.
    public void onAddSave(View view) {
        mTraceLog.printEntrance("onAddSave");
        BookInfo bookInfo = new BookInfo(this);

        if (!bookInfo.validateBookInfo(this)) {
            return;
        }

        //设置resultCode
        AddBookActivity.this.setResult(RESULT_OK, bookInfo.putDataToIntent());
        //关闭Activity
        AddBookActivity.this.finish();

        mTraceLog.printExit("onAddSave");
        return;
    }

    public void onAddCancel(View view) {
        mTraceLog.printEntrance("onAddCancel");
        Intent cancelIntent = new Intent();

        //设置resultCode
        AddBookActivity.this.setResult(RESULT_CANCELED, cancelIntent);
        //关闭Activity
        AddBookActivity.this.finish();
        mTraceLog.printExit("onAddCancel");
        return;
    }
}