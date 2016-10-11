package com.esd.esd.biathlontimer;


import android.icu.text.MessagePattern;

import java.util.ArrayList;

// Класс реализующий таблицу участников
public class Competition
{
    private ArrayList<Participant> _participants;
    private String _competitionName;
    private String _filePath;
    private String _competitionDate;

    public Competition()
    {
        _participants = new ArrayList<Participant>();
        // Здесь будем вводить название, дату и путь
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

    public String getName()
    {
        return _competitionName;
    }

    public String getFilePath()
    {
        return _filePath;
    }

    public String getDate()
    {
        return _competitionDate;
    }
}
