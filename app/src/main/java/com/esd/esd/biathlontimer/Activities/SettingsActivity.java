package com.esd.esd.biathlontimer.Activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.esd.esd.biathlontimer.DatabaseClasses.SettingsSaver;
import com.esd.esd.biathlontimer.R;

import org.w3c.dom.Text;

public class SettingsActivity extends AppCompatActivity
{
    TextView _intervalTextView;
    EditText _intervalEditText;
    TextView _countTextView;
    EditText _countEditText;
    SettingsSaver _settingsSaver;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        _intervalTextView = (TextView) findViewById(R.id.intervalTextView);
        _intervalEditText = (EditText) findViewById(R.id.intervalEditText);
        _countTextView = (TextView) findViewById(R.id.KPcountTextView);
        _countEditText = (EditText) findViewById(R.id.KPcountEditText);

        _settingsSaver = new SettingsSaver(this);
    }

    public void btnSave_OnClick(View view)
    {
        _settingsSaver.SaveDataToDatabase("Интервал", _intervalEditText.getText().toString());
        _settingsSaver.SaveDataToDatabase("Количество КП", _countEditText.getText().toString());
    }

    public void btnGet_OnClick(View view)
    {
        _intervalTextView.setText(_settingsSaver.GetDataFromDatabase("Интервал"));
        _countTextView.setText(_settingsSaver.GetDataFromDatabase("Количество КП"));
    }
}
