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

    public Participant(String fio, String country,String birthYear )
    {
        _FIO = fio;
        _country = country;
        _birthYear = birthYear;
    }

    @Override
    public boolean equals(Object obj)
    {
       // super.equals(obj);
        if(!(obj instanceof Participant)) return false;
        Participant localObj = (Participant) obj;
//        if(this.GetFIO().toString() == localObj.GetFIO().toString() && this.GetBirthYear().toString() == localObj.GetBirthYear().toString() && this.GetCountry().toString() == localObj.GetCountry().toString())
//        {
//            return true;
//        }
        if(this.GetFIO().equals(localObj.GetFIO()) && this.GetBirthYear().equals(localObj.GetBirthYear()) && this.GetCountry().equals(localObj.GetCountry()))
        {
            return true;
        }
        return false;
    }
}
