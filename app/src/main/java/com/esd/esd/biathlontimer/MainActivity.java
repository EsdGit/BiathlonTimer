package com.esd.esd.biathlontimer;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DatabaseProvider myDatabase;
    private EditText _editText;
    private TextView _textView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabase = new DatabaseProvider(this);
        _editText = (EditText) findViewById(R.id.editText);
        _textView = (TextView) findViewById(R.id.textView);
    }

    public void SaveDataOnClick(View view)
    {
        SQLiteDatabase db = myDatabase.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseProvider.DbSettings.COLUMN_SETTING_ID, 1);
        values.put(DatabaseProvider.DbSettings.COLUMN_SETTING_NAME, "Значение в editText");
        values.put(DatabaseProvider.DbSettings.COLUMN_SETTING_DATA, _editText.getText().toString());
        long newRowId = db.insert(DatabaseProvider.DbSettings.TABLE_NAME, null,values);
        _editText.setText(Long.toString(newRowId));
    }

    int _counter = 0;
    int _counter2 = 0;
    public void GetDataonClick(View view)
    {
        SQLiteDatabase db = myDatabase.getReadableDatabase();
        String[] projection =
                {
                        DatabaseProvider.DbSettings._ID,
                        DatabaseProvider.DbSettings.COLUMN_SETTING_ID,
                        DatabaseProvider.DbSettings.COLUMN_SETTING_NAME,
                        DatabaseProvider.DbSettings.COLUMN_SETTING_DATA
                };
        String order = DatabaseProvider.DbSettings._ID + " DESC";
        Cursor c = db.query(DatabaseProvider.DbSettings.TABLE_NAME, projection, null, null, null, null, order);
        c.moveToPosition(_counter2);
        _counter++;
        try {
            _textView.setText(c.getString(_counter));
        }catch (Exception e)
        {
            Toast.makeText(this, "Проблема"+ Integer.toString(_counter), Toast.LENGTH_SHORT).show();
        }
        if(_counter > 5)
        {
            _counter2++;
            _counter = 0;
        }
    }
}
