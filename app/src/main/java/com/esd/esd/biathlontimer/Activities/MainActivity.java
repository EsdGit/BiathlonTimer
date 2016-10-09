package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.ExcelHelper;
import com.esd.esd.biathlontimer.R;
import java.io.File;
import java.net.URI;

public class MainActivity extends AppCompatActivity {

    private DatabaseProvider myDatabase;
    private TextView _nameTextView;
    private TextView _dateTextView;
    private int _counter;
    private TableLayout _tableLayout;
    private File _chosenXLSFile;
    private ExcelHelper _excelHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _counter=0;
        _tableLayout = (TableLayout)findViewById(R.id.tablelayout);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);
    }

    public void addFileBtn_OnClick(View view)
    {
        Intent xlsIntent = new Intent(Intent.ACTION_GET_CONTENT);
        xlsIntent.setType("application/vnd.ms-excel");
        startActivityForResult(xlsIntent,1);
    }

    private void AddCompetitionRow(String nameCompetition)
    {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText("Соревнование №" + Integer.toString(++_counter));
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackgroundColor(Color.WHITE);
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameTextView.getMeasuredWidth(),_nameTextView.getMeasuredHeight(), 0.666f));
        ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(Integer.toString(_counter++));
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackgroundColor(Color.WHITE);
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_dateTextView.getMeasuredWidth(),_dateTextView.getMeasuredHeight(), 0.334f));
        ((TableRow.LayoutParams)newTextView2.getLayoutParams()).setMargins(0,0,2,2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        _tableLayout.addView(newRow);
        _excelHelper = new ExcelHelper(_chosenXLSFile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if(resultCode == RESULT_OK)
                {
                    Uri chosenFile = data.getData();
                    _chosenXLSFile = new File(chosenFile);
                    AddCompetitionRow(chosenFile.toString());
                }
                break;
        }
    }

    //    public void SaveDataOnClick(View view)
//    {
//        SQLiteDatabase db = myDatabase.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(DatabaseProvider.DbSettings.COLUMN_SETTING_ID, 1);
//        values.put(DatabaseProvider.DbSettings.COLUMN_SETTING_NAME, "Значение в editText");
//        values.put(DatabaseProvider.DbSettings.COLUMN_SETTING_DATA, _editText.getText().toString());
//        long newRowId = db.insert(DatabaseProvider.DbSettings.TABLE_NAME, null,values);
//        _editText.setText(Long.toString(newRowId));
//    }
//
//    int _counter = 0;
//    int _counter2 = 0;
//    public void GetDataonClick(View view)
//    {
//        SQLiteDatabase db = myDatabase.getReadableDatabase();
//        String[] projection =
//                {
//                        DatabaseProvider.DbSettings._ID,
//                        DatabaseProvider.DbSettings.COLUMN_SETTING_ID,
//                        DatabaseProvider.DbSettings.COLUMN_SETTING_NAME,
//                        DatabaseProvider.DbSettings.COLUMN_SETTING_DATA
//                };
//        String order = DatabaseProvider.DbSettings._ID + " DESC";
//        Cursor c = db.query(DatabaseProvider.DbSettings.TABLE_NAME, projection, null, null, null, null, order);
//        c.moveToPosition(_counter2);
//        _counter++;
//        try {
//            _textView.setText(c.getString(_counter));
//        }catch (Exception e)
//        {
//            Toast.makeText(this, "Проблема"+ Integer.toString(_counter), Toast.LENGTH_SHORT).show();
//        }
//        if(_counter > 5)
//        {
//            _counter2++;
//            _counter = 0;
//        }
//    }
}
