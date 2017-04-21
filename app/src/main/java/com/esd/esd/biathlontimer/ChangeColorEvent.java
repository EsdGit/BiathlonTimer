package com.esd.esd.biathlontimer;

/**
 * Created by NIL_RIMS_2 on 16.02.2017.
 */

public class ChangeColorEvent
{
    private int color;
    private int sportsmanNumber;
    public ChangeColorEvent(int color, int sportsmanNumber)
    {
        this.color = color;
        this.sportsmanNumber = sportsmanNumber;
    }

    public int GetColor()
    {
        return color;
    }

    public int GetSportsmanNumber()
    {
        return sportsmanNumber;
    }
}
