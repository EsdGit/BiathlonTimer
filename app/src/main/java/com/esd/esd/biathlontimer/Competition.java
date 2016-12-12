package com.esd.esd.biathlontimer;


import android.content.Context;
import android.icu.text.MessagePattern;
import android.icu.text.TimeZoneFormat;

import com.esd.esd.biathlontimer.Activities.MainActivity;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.SettingsSaver;

import java.util.ArrayList;

// Класс реализующий таблицу участников
public class Competition
{
    private ArrayList<Participant> _participants;

    private String _competitionName;
    public String GetName()
    {
        return _competitionName;
    }

    private String _competitionDate;
    public String GetDate()
    {
        return _competitionDate;
    }

    private String _settingsPath;
    public String GetSettingsPath(){return _settingsPath;}

    private String _dbParticipantPath;
    public String GetDbParticipantPath(){return _dbParticipantPath;};

    private Context _localContext;

    // Настройки
    private String _interval;
    public String GetInterval(){return _interval;}

    private String _timeToStart;
    public String GetTimeToStart(){return _timeToStart;}

    private String _checkPointsCount;
    public String GetCheckPointsCount(){return _checkPointsCount;}

    private String _startType;
    public String GetStartType(){return _startType;}

    private String _groups;
    public String GetGroups(){return _groups;}

    private String _secondInterval;
    public String GetSecondInterval(){return _secondInterval;}

    private String _numberSecondInterval;
    public String GetNumberSecondInterval(){return _numberSecondInterval;}

    private String _fineTime;
    public String GetFineTime(){return _fineTime;}

    private int _maxParticipantCount;
    public int GetMaxParticipantCount(){return _maxParticipantCount;}

    private int _startNumber;
    public int GetStartNumber(){return _startNumber;}

    public Competition(String name, String date, Context context)
    {
        _competitionName = name;
        _competitionDate = date;
        name = removePunct(name);
        date = removePunct(date);
        _settingsPath = "settings"+name+date;
        _dbParticipantPath = "participants"+name+date;
        _participants = new ArrayList<Participant>();
        _localContext = context;
        GenerateParticipantDb();
        GenerateSettingsDb();
    }

    private static final String PUNCT = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";

    public static String removePunct(String str) {
        StringBuilder result = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (PUNCT.indexOf(c) < 0) {
                result.append(c);
            }
        }
        return result.toString();
    }

    public void SetCompetitionSettings(String startType, String interval, String checkPointsCount, String timeToStart,
                                       String groups, String secondInterval, String numberSecondInterval, String fineTime, int startNumber, int maxPartCount)
    {
        _interval = interval;
        _checkPointsCount = checkPointsCount;
        _startType = startType;
        _timeToStart = timeToStart;
        _groups = groups;
        _secondInterval = secondInterval;
        _numberSecondInterval = numberSecondInterval;
        _fineTime = fineTime;
        _startNumber = startNumber;
        _maxParticipantCount = maxPartCount;
        SettingsSaver saver = new SettingsSaver(_localContext);
        saver.SaveSettingsToDb(this);
    }

    public void GetAllSettingsToComp()
    {
        SettingsSaver saver = new SettingsSaver(_localContext);
        _interval = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_INTERVAL);
        _timeToStart = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_TIME_TO_START);
        _checkPointsCount = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_CHECK_POINTS);
        _groups = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_GROUPS);
        _startType = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_START_TYPE);
        _secondInterval = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_SECOND_INTERVAL);
        _numberSecondInterval = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_NUMBER_SECOND_INTERVAL);
        _fineTime = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_FINE);
        String startNumberFromDb = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_PARTICIPANT_COUNT);
        _startNumber = Integer.valueOf(startNumberFromDb.split(",")[0]);
        _maxParticipantCount = Integer.valueOf(startNumberFromDb.split(",")[1]);
    }

    public void GetAllParticipantsToComp()
    {
        ParticipantSaver saver = new ParticipantSaver(_localContext);
        Participant[] localArr = saver.GetAllParticipants(GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
        localArr = SortByNumber(localArr);
        for(int i = 0; i<localArr.length;i++)
        {
            _participants.add(localArr[i]);
        }
    }

    private Participant[] SortByNumber(Participant[] arr)
    {
        Participant[] localArr = arr;
        int count = localArr.length;
        if(count == 0) return null;
        int first;
        int second;
        int k = 0;
        Participant helper;
        while(k != count)
        {
            for(int i = 0; i < count - k - 1; i++)
            {
                first = Integer.valueOf(localArr[i].GetNumber());
                second = Integer.valueOf(localArr[i+1].GetNumber());
                if(first > second)
                {
                    helper = localArr[i];
                    localArr[i]  = localArr[i+1];
                    localArr[i+1] = helper;
                }
            }
            k++;
        }
        return localArr;
    }
    // Метод добавления участников соревнований, если такого участника нет
    public void AddParticipant(Participant participant)
    {
        Participant[] localArr = _participants.toArray(new Participant[_participants.size()]);
        for(int i = 0; i < localArr.length; i++)
        {
            if(localArr[i].equals(participant)) return;
        }

        _participants.add(participant);

        ParticipantSaver ps = new ParticipantSaver(_localContext);
        ps.SaveParticipantToDatabase(participant, _dbParticipantPath);
    }

    public void GenerateSettingsDb()
    {
        DatabaseProvider dbProvider = new DatabaseProvider(_localContext);
        dbProvider.AddNewSettingsTable(_settingsPath);
    }

    private void GenerateParticipantDb()
    {
        DatabaseProvider dbProvider = new DatabaseProvider(_localContext);
        dbProvider.AddNewParticipantTable(_dbParticipantPath);
    }

    public void DeleteParticipantsFromCompetition(Participant participant)
    {
        ParticipantSaver ps = new ParticipantSaver(_localContext);
        Participant[] localArr = _participants.toArray(new Participant[_participants.size()]);
        for(int i = 0; i < localArr.length; i++)
        {
            if(localArr[i].equals(participant))
            {
                _participants.remove(participant);
                ps.DeleteParticipant(participant, _dbParticipantPath);
            }
        }
    }

    public int GetParticipantCount()
    {
        return _participants.size();
    }


    public Participant[] GetAllParticipants()
    {
        return _participants.toArray(new Participant[_participants.size()]);
    }

    public void FinishCompetition()
    {
        _settingsPath = "null";
        // Удалить базу данных с настройками соревнования
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Competition)) return false;
        Competition localObj = (Competition) obj;
        if(this.GetName().equals(localObj.GetName()) && this.GetDate().equals(localObj.GetDate()))
        {
            return true;
        }
        return false;
    }
}
