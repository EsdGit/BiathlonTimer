package com.esd.esd.biathlontimer;


import android.icu.text.MessagePattern;

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

    public Competition(String name, String date, String settingsPath, String dbPath)
    {
        _competitionName = name;
        _competitionDate = date;
        _settingsPath = settingsPath;
        _dbParticipantPath = dbPath;
        _participants = new ArrayList<Participant>();
    }

    // Метод добавления участников соревнований, если такого участника нет
    public void AddParticipant(Participant participant)
    {
        // Нет ли уже такого участника
        Participant[] localArr = _participants.toArray(new Participant[_participants.size()]);
        for(int i = 0; i < localArr.length; i++)
        {
            if(localArr[i] == participant) return;
        }
        _participants.add(participant);
    }

    public void GenerateSettingsDb()
    {

    }

    public void DeleteParticipantsFromCompetition(Participant participant)
    {
        Participant[] localArr = _participants.toArray(new Participant[_participants.size()]);
        for(int i = 0; i < localArr.length; i++)
        {
            if(localArr[i].equals(participant))
            {
                _participants.remove(participant);
            }
        }
//        for(Participant part : _participants)
//        {
//            if(part.equals(participant))
//            {
//                _participants.remove(participant);
//            }
//        }
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
}
