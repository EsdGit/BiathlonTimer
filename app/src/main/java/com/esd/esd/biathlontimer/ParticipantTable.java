package com.esd.esd.biathlontimer;


import android.icu.text.MessagePattern;

import java.util.ArrayList;

// Класс реализующий таблицу участников
public class ParticipantTable
{
    private ArrayList<Participant> _participants;
    public ParticipantTable()
    {
        _participants = new ArrayList<Participant>();
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
}
