package com.esd.esd.biathlontimer;

/**
 * Created by NIL_RIMS_2 on 16.02.2017.
 */

public class SportsmanDeleteEvent
{
    private int lapNumber;
    private int sportsmanNumber;
    public SportsmanDeleteEvent(int sportsmanNumber,int lapNumber)
    {
        this.sportsmanNumber = sportsmanNumber;
        this.lapNumber = lapNumber;
    }

    public int GetSportsmanNumber()
    {
        return sportsmanNumber;
    }
    public int GetLapNumber(){return lapNumber;}
}
