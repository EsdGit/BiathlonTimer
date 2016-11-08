package com.esd.esd.biathlontimer;


import android.content.Context;
import android.icu.text.MessagePattern;
import android.icu.text.TimeZoneFormat;

import com.esd.esd.biathlontimer.Activities.MainActivity;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;

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
    public Competition(String name, String date, Context context)
    {
        _competitionName = name;
        _competitionDate = date;
        _settingsPath = "settings"+name+date;
        _dbParticipantPath = "participants"+name;
        _participants = new ArrayList<Participant>();
        _localContext = context;
        GenerateParticipantDb();
    }

    public void SetCompetitionSettings(String startType, String interval, String checkPointsCount)
    {

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
        if(this.GetName().equals(localObj.GetName()) && this.GetDate().equals(localObj.GetDate()) &&
                this.GetSettingsPath().equals(localObj.GetSettingsPath()) && this.GetDbParticipantPath().equals(localObj.GetDbParticipantPath()))
        {
            return true;
        }
        return false;
    }
}
