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

    private boolean _competitionState;
    public boolean GetState(){return _competitionState;}

    private String _dbParticipantPath;
    public String GetDbParticipantPath(){return _dbParticipantPath;};

    public Competition(String name, String date, boolean state, String dbPath)
    {
        _participants = new ArrayList<Participant>();
        _competitionName = name;
        _competitionDate = date;
        _competitionState = state;
        _dbParticipantPath = dbPath;
    }

    // Метод добавления участников соревнований, если такого участника нет
    public boolean AddParticipant(Participant participant)
    {
        for(Participant localParticipant:_participants)
        {
            if(localParticipant.GetFIO() == participant.GetFIO())
            {
                return false;
            }
        }
        _participants.add(participant);
        return true;
    }

    public int GetParticipantCount()
    {
        return _participants.size();
    }


    public Participant[] GetAllParticipants(){return _participants.toArray(new Participant[_participants.size()]);}
}
