package com.esd.esd.biathlontimer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;


import org.apache.poi.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;


public class SettingsFragment extends PreferenceFragment implements DatePickerDialog.OnDateSetListener
{
    private static SwitchPreference _typeCompetition;
    private static Preference _setData;
    private static Preference _setInterval;
    private static EditTextPreference _nameCompetition;
    private static ListPreference _typeStart;
    private static EditTextPreference _countCheckPoint;
    private static Preference _setStartTimer;
    private static Preference _group;

    private View _dialogFormInterval;
    private View _dialogFormAddGroup;
    private AlertDialog.Builder _dialogBuilderInterval;
    private AlertDialog.Builder _dialogBuilderGroup;
    private AlertDialog.Builder _dialogBuilderAddGroup;
    private AlertDialog.Builder _dialogBuilderDelGroup;
    private AlertDialog _dialogInterval;
    private AlertDialog _dialogGroup;
    private AlertDialog _dialogAddGroup;
    private AlertDialog _dialogDelGroup;

    private NumberPicker _minute;
    private NumberPicker _seconds;
    private EditText _nameAddGroup;

    private boolean _isStartTimer = false;
    private ArrayList<String> _dialogItemsList;
    private String[] _dialogItems;
    private int _indexDelGroup;

    private static final int MIN_VALUE_MINUTE_AND_SECONDS = 0;
    private static final int MAX_VALUE_MINUTE_AND_SECONDS = 60;


    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        _dialogItemsList = new ArrayList<>();
        _dialogItemsList.add("Добавить группу");
        _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);

        _typeCompetition = (SwitchPreference) findPreference("typeCompetition");
        _typeCompetition.setChecked(false);
        _setData = (Preference) findPreference("dataSetting");
        _setInterval = (Preference) findPreference("intervalSetting");
        _countCheckPoint = (EditTextPreference) findPreference("countCheckPointSetting");
        _nameCompetition = (EditTextPreference) findPreference("nameCompetitionSetting");
        _typeStart = (ListPreference) findPreference("typeStartSetting");
        _setStartTimer = (Preference) findPreference("startTimer");
        _group = (Preference) findPreference("group");

        //Диалог установки интервала
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        _dialogFormInterval = inflater.inflate(R.layout.dialog_interval_setting_activity, null);
        _minute = (NumberPicker) _dialogFormInterval.findViewById(R.id.dialog_minute);
        _seconds= (NumberPicker) _dialogFormInterval.findViewById(R.id.dialog_seconds);
        _minute.setMinValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _minute.setMaxValue(MAX_VALUE_MINUTE_AND_SECONDS);
        _seconds.setMinValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _seconds.setMaxValue(MAX_VALUE_MINUTE_AND_SECONDS);
        _dialogBuilderInterval = new AlertDialog.Builder(getActivity());
        _dialogBuilderInterval.setTitle("Установите интервал");
        _dialogBuilderInterval.setView(_dialogFormInterval);
        _dialogBuilderInterval.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(_isStartTimer)
                {
                    _setStartTimer.setSummary(SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                    _dialogInterval.setTitle("Установаите интервал");
                    _isStartTimer = false;
                }
                else
                {
                    _setInterval.setSummary(SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                }
            }
        });
        _dialogBuilderInterval.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        _dialogBuilderInterval.setCancelable(false);
        _dialogInterval = _dialogBuilderInterval.create();


        //Диалог добавления группы
        _dialogFormAddGroup = inflater.inflate(R.layout.dialog_add_group_setting_activity, null);
        _nameAddGroup = (EditText) _dialogFormAddGroup.findViewById(R.id.add_group_setting_activity);
        _dialogBuilderAddGroup = new AlertDialog.Builder(getActivity());
        _dialogBuilderAddGroup.setTitle("Название группы");
        _dialogBuilderAddGroup.setView(_dialogFormAddGroup);
        _dialogBuilderAddGroup.setPositiveButton("Принять", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String getString = _nameAddGroup.getText().toString().replace(" ","");
                if(_nameAddGroup.getText().toString() != "" && !_dialogItemsList.contains(getString))
                {
                    _dialogItemsList.add(_dialogItemsList.size() - 1,getString);
                    _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);
                }
                _nameAddGroup.setText("");
            }
        });
        _dialogBuilderAddGroup.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        _dialogAddGroup = _dialogBuilderAddGroup.create();

        //Диалог удаления группы
        _dialogBuilderDelGroup = new AlertDialog.Builder(getActivity());
        _dialogBuilderDelGroup.setTitle("Удаление группы");
        _dialogBuilderDelGroup.setMessage("Вы уверены, что хотите удалить группу?");
        _dialogBuilderDelGroup.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                _dialogItemsList.remove(_indexDelGroup);
                _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);
            }
        });
        _dialogBuilderDelGroup.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getActivity(),"Правильный выбор, чувак)",Toast.LENGTH_SHORT).show();
            }
        });
        _dialogDelGroup = _dialogBuilderDelGroup.create();

        _typeCompetition.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                if(!_typeCompetition.isChecked())
                {
                    _typeCompetition.setChecked(true);
                    _typeCompetition.setSummary("Международные соревнования");
                }
                else
                {
                    _typeCompetition.setChecked(false);
                    _typeCompetition.setSummary("Региональные соревнования");
                }
                return false;
            }
        });

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
            _dialogInterval.show();
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
                _dialogInterval.setTitle("Установите время до старта");
                _dialogInterval.show();
                return false;
            }
        });
        _group.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                _dialogBuilderGroup = new AlertDialog.Builder(getActivity());
                _dialogBuilderGroup.setTitle("Группы соревнования");
                _dialogBuilderGroup.setItems(_dialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if(_dialogItems[which] == "Добавить группу")
                        {
                            _dialogAddGroup.show();
                        }
                        else
                        {
                            _nameAddGroup.setText(_dialogItemsList.get(which).toString());
                            _dialogItemsList.remove(which);
                            _dialogAddGroup.show();
                        }
                    }
                });
                _dialogGroup = _dialogBuilderGroup.create();
                _dialogGroup.show();
                return false;
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        String str = Integer.toString(dayOfMonth)+":"+Integer.toString(month + 1)+":"+Integer.toString(year);
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
        intent.putExtra("NeedDelete", "true");
        return intent;
    }

    public static Competition GetCurrentCompetition(Context context)
    {
        return new Competition(_nameCompetition.getSummary().toString(),_setData.getSummary().toString(), context);
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
