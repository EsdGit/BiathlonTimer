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

    public void SetCompetitionSettings(String startType, String interval, String checkPointsCount, String timeToStart, String groups)
    {
        _interval = interval;
        _checkPointsCount = checkPointsCount;
        _startType = startType;
        _timeToStart = timeToStart;
        _groups = groups;
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
    }

    public void GetAllParticipantsToComp()
    {
        ParticipantSaver saver = new ParticipantSaver(_localContext);
        Participant[] localArr = saver.GetAllParticipants(GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NUMBER);
        for(int i = 0; i<localArr.length;i++)
        {
            _participants.add(localArr[i]);
        }
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
