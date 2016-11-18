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

    private String _country;
    public String GetCountry(){return _country;}


    private String _birthYear;
    public String GetBirthYear(){return _birthYear;}

    private String _number;
    public String GetNumber(){return _number;}

    private String _group;
    public String GetGroup(){return _group;}

    public Participant(String number,String fio, String country,String birthYear, String group)
    {
        _FIO = fio;
        _country = country;
        _birthYear = birthYear;
        _number = number;
        _group = group;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Participant)) return false;
        Participant localObj = (Participant) obj;
        if(this.GetFIO().equals(localObj.GetFIO()) && this.GetBirthYear().equals(localObj.GetBirthYear()) && this.GetCountry().equals(localObj.GetCountry()))
        {
            return true;
        }
        return false;
    }
}
