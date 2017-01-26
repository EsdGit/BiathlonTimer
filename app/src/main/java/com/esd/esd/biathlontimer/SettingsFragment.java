package com.esd.esd.biathlontimer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.widget.SwitchCompat;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;


import org.apache.poi.sl.usermodel.Resources;
import org.apache.poi.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.zip.Inflater;


public class SettingsFragment extends PreferenceFragment implements DatePickerDialog.OnDateSetListener
{
    private static SwitchPreference _typeCompetition;
    private static Preference _setData;
    private static Preference _setInterval;
    private static Preference _setSecondInterval;
    private static EditTextPreference _nameCompetition;
    private static ListPreference _typeStart;
    private static Preference _numberStart;
    private static EditTextPreference _countCheckPoint;
    private static Preference _setStartTimer;
    private static Preference _group;
    private static Preference _fine;//штраф если что

    private View _dialogFormInterval;
    private View _dialogFormAddGroup;
    private View _dialogFormSecondInterval;
    private View _dialogFormStartNumber;
    private AlertDialog.Builder _dialogBuilderInterval;
    private AlertDialog.Builder _dialogBuilderSecondInterval;
    private AlertDialog.Builder _dialogBuilderGroup;
    private AlertDialog.Builder _dialogBuilderAddGroup;
    private AlertDialog.Builder _dialogBuilderDelGroup;
    private AlertDialog.Builder _dialogBuilderStartNumber;
    private AlertDialog _dialogInterval;
    private AlertDialog _dialogSecondInterval;
    private AlertDialog _dialogGroup;
    private AlertDialog _dialogAddGroup;
    private AlertDialog _dialogDelGroup;
    private AlertDialog _dialogStartNumber;

    private NumberPicker _minute;
    private NumberPicker _seconds;
    private NumberPicker _minuteSecondInterval;
    private NumberPicker _secondsSecondInterval;
    private EditText _setNumberStartWithSecondInterval;
    private EditText _nameAddGroup;
    private EditText _startNumber;
    private EditText _countParticipant;

    private boolean _isStartTimer = false;
    private boolean _isFine = false;
    private static ArrayList<String> _dialogItemsList;
    private static String[] _dialogItems;
    private int _indexDelGroup;
    private int _minuteInterval;
    private int _secondsInterval;
    private int _minuteStart;
    private int _secondsStart;
    private int _minuteFine;
    private int _secondFine;

    private static final int MIN_VALUE_MINUTE_AND_SECONDS = 0;
    private static final int MAX_VALUE_MINUTE_AND_SECONDS = 59;



    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        _minuteInterval = 0;
        _secondsInterval = 0;
        _minuteStart = 0;
        _secondsStart = 0;
        _minuteFine = 0;
        _secondFine = 0;

        _dialogItemsList = new ArrayList<>();
        _dialogItemsList.add(getResources().getString(R.string.add_dialog_item_group));
        _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);

        _typeCompetition = (SwitchPreference) findPreference("typeCompetition");
        _typeCompetition.setChecked(false);
        _setData = (Preference) findPreference("dataSetting");
        _setInterval = (Preference) findPreference("intervalSetting");
        _setSecondInterval = (Preference) findPreference("secondIntervalSetting");
        _countCheckPoint = (EditTextPreference) findPreference("countCheckPointSetting");
        if(_countCheckPoint.getSummary().equals(getResources().getString(R.string.summary_checkpoint)))
        {
            _countCheckPoint.setText("");
        }
        _nameCompetition = (EditTextPreference) findPreference("nameCompetitionSetting");
        if(_nameCompetition.getSummary().equals(getResources().getString(R.string.main_activity_name_col)))
        {
            _nameCompetition.setText("");
        }
        _typeStart = (ListPreference) findPreference("typeStartSetting");
        _numberStart = (Preference) findPreference("firstNumberStart");
        _setStartTimer = (Preference) findPreference("startTimer");
        _group = (Preference) findPreference("group");
        _fine = (Preference) findPreference("fine");


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
        _dialogBuilderInterval.setTitle(getResources().getString(R.string.interval_dialog_title));
        _dialogBuilderInterval.setView(_dialogFormInterval);
        _dialogBuilderInterval.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(_isStartTimer)
                {
                    _setStartTimer.setSummary(SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                    _dialogInterval.setTitle(getResources().getString(R.string.interval_dialog_title));
                    //_minuteStart = _minute.getValue();
                    //_secondsStart = _seconds.getValue();
                    _isStartTimer = false;
                }
                else
                {
                    if(_isFine)
                    {
                        _fine.setSummary(getResources().getString(R.string.summary_fine_after_set) + SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                        _dialogInterval.setTitle(getResources().getString(R.string.interval_dialog_title));
                        //_minuteFine = _minute.getValue();
                        //_secondFine = _seconds.getValue();
                        _isFine = false;
                    }
                    else
                    {
                        _setInterval.setSummary(SetNormalFormatDataTime(_minute.getValue() + ":" + _seconds.getValue(), true));
                        //_minuteInterval = _minute.getValue();
                        //_secondsInterval = _seconds.getValue();
                    }
                }
            }
        });
        _dialogBuilderInterval.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {

            }
        });
        _dialogBuilderInterval.setCancelable(false);
        _dialogInterval = _dialogBuilderInterval.create();



        //Диалог установки второго интервала
        LayoutInflater secondInflater = LayoutInflater.from(getActivity());
        _dialogFormSecondInterval = secondInflater.inflate(R.layout.dialog_second_interval, null);
        _minuteSecondInterval = (NumberPicker) _dialogFormSecondInterval.findViewById(R.id.dialog_minute_second_interval);
        _secondsSecondInterval = (NumberPicker) _dialogFormSecondInterval.findViewById(R.id.dialog_seconds_second_interval);
        _setNumberStartWithSecondInterval = (EditText) _dialogFormSecondInterval.findViewById(R.id.startPositionWithSecondInterval);
        _minuteSecondInterval.setMinValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _minuteSecondInterval.setMaxValue(MAX_VALUE_MINUTE_AND_SECONDS);
        _secondsSecondInterval.setMinValue(MIN_VALUE_MINUTE_AND_SECONDS);
        _secondsSecondInterval.setMaxValue(MAX_VALUE_MINUTE_AND_SECONDS);
        _dialogBuilderSecondInterval = new AlertDialog.Builder(getActivity());
        _dialogBuilderSecondInterval.setTitle(getResources().getString(R.string.interval_dialog_title));
        _dialogBuilderSecondInterval.setView(_dialogFormSecondInterval);
        _dialogBuilderSecondInterval.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        _dialogBuilderSecondInterval.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        _dialogBuilderSecondInterval.setCancelable(false);
        _dialogSecondInterval = _dialogBuilderSecondInterval.create();

        //Диалог добавления группы
        _dialogFormAddGroup = inflater.inflate(R.layout.dialog_add_group_setting_activity, null);
        _nameAddGroup = (EditText) _dialogFormAddGroup.findViewById(R.id.add_group_setting_activity);
        _dialogBuilderAddGroup = new AlertDialog.Builder(getActivity());
        _dialogBuilderAddGroup.setTitle(getResources().getString(R.string.add_group_dialog_title));
        _dialogBuilderAddGroup.setView(_dialogFormAddGroup);
        _dialogBuilderAddGroup.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String getString = _nameAddGroup.getText().toString().replace(" ","");
                if((!getString.isEmpty()) && (!_dialogItemsList.contains(getString)))
                {
                    if(getString.contains(","))
                    {
                        String[] localArray = getString.split(",");
                        for (int i =0; i < localArray.length; i++)
                        {
                            _dialogItemsList.add(_dialogItemsList.size() - 1,localArray[i]);
                        }
                    }
                    else
                    {
                        _dialogItemsList.add(_dialogItemsList.size() - 1, getString);
                    }
                    _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);
                }
                _nameAddGroup.setText("");
                _group.setSummary(SetSummaryPreferenceGroup());
            }
        });
        _dialogBuilderAddGroup.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        _dialogAddGroup = _dialogBuilderAddGroup.create();

        //Диалог добавления номера участника
        _dialogFormStartNumber = inflater.inflate(R.layout.dialog_start_number, null);
        _startNumber = (EditText) _dialogFormStartNumber.findViewById(R.id.startNumber);
        _countParticipant = (EditText) _dialogFormStartNumber.findViewById(R.id.countParticipant);
        _dialogBuilderStartNumber = new AlertDialog.Builder(getActivity());
        _dialogBuilderStartNumber.setTitle(getResources().getString(R.string.dialog_title_start_number));
        _dialogBuilderStartNumber.setView(_dialogFormStartNumber);
        _dialogBuilderStartNumber.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                _numberStart.setSummary(getResources().getString(R.string.summary_after_set_start_nmber) + _startNumber.getText() + getResources().getString(R.string.summary_after_set_count_participant) + _countParticipant.getText() + "." );
            }
        });
        _dialogBuilderStartNumber.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        _dialogStartNumber = _dialogBuilderStartNumber.create();

        //Диалог удаления группы
        _dialogBuilderDelGroup = new AlertDialog.Builder(getActivity());
        _dialogBuilderDelGroup.setTitle(getResources().getString(R.string.delete_dialog_group));
        _dialogBuilderDelGroup.setMessage(getResources().getString(R.string.message_dialog_delete_group));
        _dialogBuilderDelGroup.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                _dialogItemsList.remove(_indexDelGroup);
                _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);
                _group.setSummary(SetSummaryPreferenceGroup());
            }
        });
        _dialogBuilderDelGroup.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
                if (!_typeCompetition.isChecked())
                {
                   _typeCompetition.setChecked(true);
                    _typeCompetition.setSummary(getResources().getString(R.string.summary_international_competition));
                }
                else
                {
                    _typeCompetition.setChecked(false);
                    _typeCompetition.setSummary(getResources().getString(R.string.summary_regional_competition));
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
            //SetStartPositionInTimeDialog();
            String[] currentTime;
            if(!_setInterval.getSummary().equals(getResources().getString(R.string.summary_interval)))
            {
                currentTime = _setInterval.getSummary().toString().split(":");
                _minute.setValue(Integer.valueOf(currentTime[0]));
                _seconds.setValue(Integer.valueOf(currentTime[1]));
            }
            else
            {
                _minute.setValue(0);
                _seconds.setValue(0);
            }
            _dialogInterval.show();
            return false;
        }});

        _setSecondInterval.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {

                _dialogSecondInterval.show();
                _dialogSecondInterval.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        String position = _setNumberStartWithSecondInterval.getText().toString();
                        if(!position.isEmpty())
                        {
                            position = _setNumberStartWithSecondInterval.getText().toString();
                            _setSecondInterval.setSummary(SetNormalFormatDataTime(_minuteSecondInterval.getValue() + ":" +
                                    _secondsSecondInterval.getValue(), true) + getResources().getString(R.string.summary_second_interval_helper) + position);
                            _dialogSecondInterval.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),getResources().getString(R.string.check_input_data),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //SetStartPositionInTimeDialog();
                //_setNumberStartWithSecondInterval.setText("");
                return false;
            }
        });

        _nameCompetition.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                String getString = (String)newValue;
                if(!getString.isEmpty())
                {
                    _nameCompetition.setSummary((String) newValue);
                    _nameCompetition.setText((String) newValue);

                }
                return false;
            }
        });
        _countCheckPoint.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        _countCheckPoint.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue)
            {
                String getString = (String)newValue;
                if(!getString.isEmpty())
                {
                    _countCheckPoint.setSummary(getResources().getString(R.string.summary_count_checkpoint) + (String) newValue);
                    _countCheckPoint.setText((String) newValue);
                }
                return false;
            }
        });
        _typeStart.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                _typeStart.setSummary((String)newValue);
                if(((String)newValue).equals(getResources().getString(R.string.item_type_single_start)))
                {
                    _typeStart.setValueIndex(0);
                    _setInterval.setEnabled(true);
                    _setSecondInterval.setEnabled(true);
                }
                else
                {
                    if(((String)newValue).equals(getResources().getString(R.string.item_type_double_start)))
                    {
                        _typeStart.setValueIndex(1);
                        _setInterval.setEnabled(true);
                        _setSecondInterval.setEnabled(true);
                    }
                    else
                    {
                        _typeStart.setValueIndex(2);
                        _setInterval.setEnabled(false);
                        _setSecondInterval.setEnabled(false);
                    }
                }
                return false;
            }
        });

        _numberStart.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                //_startNumber.setText("");
                //_countParticipant.setText("");
                _dialogStartNumber.show();
                return false;
            }
        });

        _setStartTimer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                //SetStartPositionInTimeDialog();
                String[] currentTime;
                if(!_setStartTimer.getSummary().equals(getResources().getString(R.string.summary_time_to_start)))
                {
                    currentTime = _setStartTimer.getSummary().toString().split(":");
                    _minute.setValue(Integer.valueOf(currentTime[0]));
                    _seconds.setValue(Integer.valueOf(currentTime[1]));
                }
                else
                {
                    _minute.setValue(0);
                    _seconds.setValue(0);
                }
                _isStartTimer = true;
                _dialogInterval.setTitle(getResources().getString(R.string.dialog_time_to_start_title));
                _dialogInterval.show();
                return false;
            }
        });

        _fine.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                //SetStartPositionInTimeDialog();
                String[] currentTime;
                if(!_fine.getSummary().equals(getResources().getString(R.string.summary_fine)))
                {
                    currentTime = _fine.getSummary().toString().split(" ");
                    currentTime = currentTime[2].split(":");
                    _minute.setValue(Integer.valueOf(currentTime[0]));
                    _seconds.setValue(Integer.valueOf(currentTime[1]));
                }
                else
                {
                    _minute.setValue(0);
                    _seconds.setValue(0);
                }
                _isFine = true;
                _dialogInterval.setTitle(getResources().getString(R.string.dialog_fine_title));
                _dialogInterval.show();
                return false;
            }
        });
        _group.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference)
            {
                _dialogBuilderGroup = new AlertDialog.Builder(getActivity());
                _dialogBuilderGroup.setTitle(getResources().getString(R.string.dialog_add_group_title));
                _dialogBuilderGroup.setItems(_dialogItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(_dialogItems[which] == getResources().getString(R.string.add_dialog_item_group))
                        {
                            _dialogAddGroup.show();
                        }
                        else
                        {
                            _dialogDelGroup.show();
                            _indexDelGroup = which;
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
        _dialogItemsList.remove(_dialogItemsList.size()-1);
        _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);
        Intent intent = new Intent(context, ViewPagerActivity.class);
        intent.putExtra("CompetitionName", _nameCompetition.getSummary().toString());
        intent.putExtra("CompetitionDate", _setData.getSummary().toString());
        intent.putExtra("NeedDelete", "true");
        intent.putExtra("ArrayGroup", _dialogItems);
        return intent;
    }

    public static Competition GetCurrentCompetition(Context context)
    {
        android.content.res.Resources myRes = context.getResources();
        String groups = _group.getSummary().toString();


        if(_setData.getSummary().toString().equals(myRes.getString(R.string.summary_data_competition))) return null;
        if(_countCheckPoint.getSummary().toString().equals(myRes.getString(R.string.summary_count_checkpoint))) return null;
        if(_setStartTimer.getSummary().toString().equals(myRes.getString(R.string.summary_time_to_start))) return null;
        if(_typeStart.getSummary().toString().equals(myRes.getString(R.string.summary_type_start))) return null;
        if(_numberStart.getSummary().toString().equals(myRes.getString(R.string.summary_number_start))) return null;
        if(!_typeStart.getSummary().toString().equals(myRes.getString(R.string.item_type_mas_start)))
        {
            if(_setInterval.getSummary().toString().equals(myRes.getString(R.string.summary_interval))) return null;
            //if(_setSecondInterval.getSummary().toString().equals(myRes.getString(R.string.summary_interval)))return null;
        }
        if(_fine.getSummary().toString().equals(myRes.getString(R.string.summary_fine))) return null;
        if(!_group.getSummary().toString().equals(myRes.getString(R.string.summary_group)))
            groups = groups.split(":")[1];
        else
            groups = "";
        String fine = _fine.getSummary().toString().split("- ")[1];
        String[] secondInterval;
        if(!_setSecondInterval.getSummary().toString().equals(myRes.getString(R.string.summary_interval)))
        {
            secondInterval = _setSecondInterval.getSummary().toString().split(myRes.getString(R.string.summary_second_interval_helper));
        }
        else
        {
            secondInterval = new String[]{"", ""};
        }
        int startNumber = Integer.valueOf(_numberStart.getSummary().toString().split("- ")[1].split("\\.")[0]);
        int maxPartCount = Integer.valueOf(_numberStart.getSummary().toString().split("- ")[2].split("\\.")[0]);
        String countCheckPoints = _countCheckPoint.getSummary().toString().split(": ")[1];
        if(Integer.valueOf(countCheckPoints) <= 0) return null;
        Competition localCompetition = new Competition(_nameCompetition.getSummary().toString(),_setData.getSummary().toString(), context);
        localCompetition.SetCompetitionSettings(_typeStart.getSummary().toString(), _setInterval.getSummary().toString(),
                Integer.valueOf(countCheckPoints),_setStartTimer.getSummary().toString(),groups, secondInterval[0], secondInterval[1],fine, startNumber, maxPartCount);
        localCompetition.setFinished(false);
        return localCompetition;
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

//    private void SetStartPositionInTimeDialog()
//    {
//        _minute.setValue(MIN_VALUE_MINUTE_AND_SECONDS);
//        _seconds.setValue(MIN_VALUE_MINUTE_AND_SECONDS);
//        _minuteSecondInterval.setValue(MIN_VALUE_MINUTE_AND_SECONDS);
//        _secondsSecondInterval.setValue(MIN_VALUE_MINUTE_AND_SECONDS);
//    }

    private String SetSummaryPreferenceGroup()
    {
        String result;
        if((_dialogItems.length - 1) == 0)
        {
            result = getResources().getString(R.string.start_summary_group);
        }
        else
        {
            result = getResources().getString(R.string.aftter_add_summary_group) + _dialogItems[0];
            for (int i = 1; i < _dialogItems.length - 1; i++)
            {
                result = result + ", " + _dialogItems[i];
            }
        }
        return result;
    }

    public static void SetAllSummaries(Context context,Competition competition)
    {
        _nameCompetition.setSummary(competition.getName());
        _setData.setSummary(competition.getDate());
        _setInterval.setSummary(competition.getInterval());
        _setStartTimer.setSummary(competition.getTimeToStart());
        if(competition.getSecondInterval().isEmpty())
        {
            _setSecondInterval.setSummary(context.getResources().getString(R.string.summary_interval));
        }
        else
        {
            _setSecondInterval.setSummary(competition.getSecondInterval() + context.getResources().getString(R.string.summary_second_interval_helper) + String.valueOf(competition.getNumberSecondInterval()));
        }
        _fine.setSummary(context.getResources().getString(R.string.summary_fine_after_set)+competition.getFineTime());
        if(!competition.getGroups().isEmpty())
        {
            _group.setSummary(context.getResources().getString(R.string.aftter_add_summary_group) + competition.getGroups());
            String[] allGroups = competition.getGroups().split(",");
            for(int i = 0; i < allGroups.length; i++)
            {
                _dialogItemsList.add(i ,allGroups[i]);
            }
            _dialogItems = _dialogItemsList.toArray(new String[_dialogItemsList.size()]);
        }
        _typeStart.setSummary(competition.getStartType());
        _numberStart.setSummary(context.getResources().getString(R.string.summary_after_set_start_nmber) + String.valueOf(competition.getStartNumber()) + context.getResources().getString(R.string.summary_after_set_count_participant) + String.valueOf(competition.getMaxParticipantCount()) + "." );
        _countCheckPoint.setSummary(context.getResources().getString(R.string.summary_count_checkpoint)+String.valueOf(competition.getCheckPointsCount()));
    }

}
