package com.esd.esd.biathlontimer;

import android.icu.text.MessagePattern;

/**
 * Created by Олег on 31.10.2016.
 */

public class Group
{
    // Возможно необходимо сделать 1ый номер в группе и последний
    // По группам будем делить только в самом конце, непосредственно перед стартом
    // Возможно объекты Group надо создавать внутри Competition
    private Participant[] _participants;

    private String _name;
    public String GetName(){return _name;};

    public int GetParticipantCount(){return _participants.length;};

    public Group(String name)
    {
        _name = name;
    }

    public void AddParticipants(Participant[] participants)
    {
        _participants = participants;
    }
}
