package com.esd.esd.biathlontimer;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.esd.esd.biathlontimer.Activities.MainActivity;
import com.esd.esd.biathlontimer.Activities.SettingsActivity;

import java.util.Calendar;



public class SettingsFragment extends PreferenceFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private Preference _setData;
    private Preference _setInterval;
    private EditTextPreference _nameCompetition;
    private ListPreference _typeStart;
    private EditTextPreference _countCheckPoint;


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);
        _setData = (Preference) findPreference("dataSetting");
        _setInterval = (Preference) findPreference("intervalSetting");
        _countCheckPoint = (EditTextPreference) findPreference("countCheckPointSetting");
        _nameCompetition = (EditTextPreference) findPreference("nameCompetitionSetting");
        _typeStart = (ListPreference) findPreference("typeStartSetting");
        _setData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            ShowDataDialog();
            return false;
        }});
        _setInterval.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference)
        {
            ShowTimeDialog();
            return false;
        }});
        _nameCompetition.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                _nameCompetition.setSummary((String)newValue);
                return false;
            }
        });
        _countCheckPoint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                _countCheckPoint.setSummary("Количество контрольный точек: " + (String)newValue);
                return false;
            }
        });
        _typeStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                _typeStart.setSummary((String)newValue);
                switch ((String)newValue)
                {
                    case "Одиночный старт":
                        _typeStart.setValueIndex(0);
                        _setInterval.setEnabled(true);
                        break;
                    case "Парный старт":
                        _typeStart.setValueIndex(1);
                        _setInterval.setEnabled(true);
                        break;
                    case "Массовый старт":
                        _typeStart.setValueIndex(2);
                        _setInterval.setEnabled(false);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        _setData.setSummary(Integer.toString(dayOfMonth)+"."+Integer.toString(month)+"."+Integer.toString(year));
    }

    private void ShowDataDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(),this,year,month,day).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        _setInterval.setSummary(Integer.toString(hourOfDay)+":"+Integer.toString(minute));
    }

    private void ShowTimeDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int hour = 0;
        int minute = 10;
        new TimePickerDialog(getActivity(),this,hour,minute,true).show();
    }
}
