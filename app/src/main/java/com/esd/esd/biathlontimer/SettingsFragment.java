package com.esd.esd.biathlontimer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;


import org.apache.poi.util.StringUtil;

import java.util.Calendar;


public class SettingsFragment extends PreferenceFragment implements DatePickerDialog.OnDateSetListener
{
    private static Preference _setData;
    private static Preference _setInterval;
    private static EditTextPreference _nameCompetition;
    private static ListPreference _typeStart;
    private static EditTextPreference _countCheckPoint;
    private static Preference _setStartTimer;
    private static EditTextPreference _countGroup;

    private View _dialogForm;
    private AlertDialog.Builder _dialogBuilder;
    private AlertDialog _dialog;
    private NumberPicker _minute;
    private NumberPicker _seconds;

    private boolean _isStartTimer = false;

    private static final int MIN_VALUE_MINUTE_AND_SECONDS = 0;
    private static final int MAX_VALUE_MINUTE_AND_SECONDS = 60;


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
        _setStartTimer = (Preference) findPreference("startTimer");
        _countGroup = (EditTextPreference) findPreference("groupCounter");


        LayoutInflater inflater = LayoutInflater.from(getActivity());
        _dialogForm = inflater.inflate(R.layout.dialog_interval_setting_activity, null);
        _minute = (NumberPicker) _dialogForm.findViewById(R.id.dialog_minute);
        _seconds= (NumberPicker) _dialogForm.findViewById(R.id.dialog_seconds);
        _minute.setMinValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _minute.setMaxValue(MAX_VALUE_MINUTE_AND_SECONDS);
        _seconds.setMinValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _seconds.setMaxValue(MAX_VALUE_MINUTE_AND_SECONDS);
        _dialogBuilder = new AlertDialog.Builder(getActivity());
        _dialogBuilder.setTitle("Установите интервал");
        _dialogBuilder.setView(_dialogForm);
        _dialogBuilder.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(_isStartTimer)
                {
                    _setStartTimer.setSummary(SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                    _dialog.setTitle("Установаите интервал");
                    _isStartTimer = false;
                }
                else
                {
                    _setInterval.setSummary(SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                }
            }
        });
        _dialogBuilder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        _dialogBuilder.setCancelable(false);
        _dialog = _dialogBuilder.create();

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
            SetStartPositionInTimeDialog();
            _dialog.show();
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

        _setStartTimer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                SetStartPositionInTimeDialog();
                _isStartTimer = true;
                _dialog.setTitle("Установите время до старта");
                _dialog.show();
                return false;
            }
        });
        _countGroup.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                _countGroup.setSummary("Количество групп на соревнованиях: " + (String)newValue);
                return false;
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        String str = Integer.toString(dayOfMonth)+":"+Integer.toString(month)+":"+Integer.toString(year);
        _setData.setSummary(SetNormalFormatDataTime(str , false));
    }

    private void ShowDataDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(getActivity(),this,year,month,day).show();
    }

    public static Intent GetIntent(Context context)
    {
        Intent intent = new Intent(context, ViewPagerActivity.class);
        intent.putExtra("CompetitionName", _nameCompetition.getSummary().toString());
        intent.putExtra("CompetitionDate", _setData.getSummary().toString());
        intent.putExtra("CompetitionStartType", _typeStart.getSummary().toString());
        intent.putExtra("CompetitionInterval", _setInterval.getSummary().toString());
        intent.putExtra("CompetitionCheckPointsCount", _countCheckPoint.getSummary().toString());
        return intent;
    }

    private String SetNormalFormatDataTime(String str, boolean isTime)
    {
        String[] localArray =  localArray = str.split(":");
        String result;
        for (int i = 0; i < localArray.length;i++)
        {
            if(Integer.valueOf(localArray[i]) < 10)
            {
                localArray[i] = "0" + localArray[i];
            }
        }
        if(isTime)
        {
            result = StringUtil.join(localArray, ":");
        }
        else
        {
            result = StringUtil.join(localArray, ".");
        }
        return result;
    }

    private void SetStartPositionInTimeDialog()
    {
        _minute.setValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _seconds.setValue(MIN_VALUE_MINUTE_AND_SECONDS);
    }
}
