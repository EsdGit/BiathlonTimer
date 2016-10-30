package com.esd.esd.biathlontimer;


import android.icu.text.MessagePattern;

import java.util.ArrayList;

// Класс реализующий таблицу участников
public class Competition
{
    private Participant[] _participants;

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

    public Competition(String name, String date, String settingsPath, String dbPath)
    {
        _competitionName = name;
        _competitionDate = date;
        _settingsPath = settingsPath;
        _dbParticipantPath = dbPath;
    }

    // Метод добавления участников соревнований, если такого участника нет
    public void AddParticipants(Participant[] participant)
    {
        _participants = participant;
    }

    public void GenerateSettingsDb()
    {

    }

    public int GetParticipantCount()
    {
        return _participants.length;
    }


    public Participant[] GetAllParticipants(){return _participants;}

    public void FinishCompetition()
    {
        _settingsPath = "null";
    }
}
