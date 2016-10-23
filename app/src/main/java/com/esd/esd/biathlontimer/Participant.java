package com.esd.esd.biathlontimer;

import android.graphics.Color;

// Класс участника
public class Participant
{
    private String _FIO;
    public String GetFIO()
    {
        return _FIO;
    }

    private Color _color;
    private String _country;
    public String GetCountry(){return _country;}


    private String _birthYear;
    public String GetBirthYear(){return _birthYear;}

    public Participant(String fio, String country, String birthYear )
    {
        _FIO = fio;
        _country = country;
        _birthYear = birthYear;
    }

}
