package com.esd.esd.biathlontimer;

import java.util.ArrayList;

/**
 * Created by Олег on 03.12.2016.
 */

public class LapData
{
    private ArrayList<Participant> participants;
    public int Number;
    public LapData(int number)
    {
        Number = number;
        participants = new ArrayList<Participant>();
    }

    public void AddParticipant(int position, Participant participant)
    {
        participants.add(position, participant);
    }

    public void AddParticipant(Participant participant)
    {
        participants.add(participant);
    }

    public Participant[] GetParticipants()
    {
        return participants.toArray(new Participant[participants.size()]);
    }

    public Participant GetParticipant(int index)
    {
        return participants.get(index);
    }

    public void RemoveParticipant(Participant participant)
    {
        participants.remove(participant);
    }
}
