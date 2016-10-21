package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.R;


public class DataBaseActivity extends AppCompatActivity
{
    private TableLayout _tableLayout;
    private TextView _nameTextView;
    private TextView _birthdayTextView;
    private TextView _countryTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_list);
        _tableLayout = (TableLayout)findViewById(R.id.tableDataBaseLayout);
        _nameTextView = (TextView) findViewById(R.id.nameDataBase);
        _birthdayTextView = (TextView) findViewById(R.id.birthdayDataBase);
        _countryTextView = (TextView) findViewById(R.id.countryDataBase);


    }

    public void OnClick(View view)
    {

    }


}
