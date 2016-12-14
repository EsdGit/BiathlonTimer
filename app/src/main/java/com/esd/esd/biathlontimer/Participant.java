package com.esd.esd.biathlontimer;

import android.graphics.Color;
import android.text.format.Time;

import java.util.ArrayList;

// Класс участника
public class Participant
{
    private int _place;
    public int GetPlace()
    {
        return _place;
    }
    public void SetPlace(int place)
    {
        _place = place;
    }

    private Time _resultTime;
    public Time GetResultTime()
    {
        Time returnTime = new Time();
        returnTime.second = _resultTime.second + _fineTime.second;
        returnTime.minute = _resultTime.minute + _fineTime.minute;
        returnTime.hour = _resultTime.hour + _fineTime.hour;
        returnTime.normalize(false);
        return returnTime;
    }

    public void SetResultTime(Time time)
    {
        _resultTime = new Time(time);
    }

    private Time _fullFineTime;
    private Time _fineTime;
    public Time GetFineTime(){return _fineTime;}
    public void SetFineTime(Time fineTime)
    {
        _fullFineTime.second += fineTime.second;
        _fullFineTime.minute += fineTime.minute;
        _fullFineTime.hour += fineTime.hour;
        _fullFineTime.normalize(false);
        _fineTime.second = _fullFineTime.second;
        _fineTime.minute = _fullFineTime.minute;
        _fineTime.hour = _fullFineTime.hour;
        _fineTime.normalize(false);
    }

    private int _fineCount;
    public void SetFineCount(int count)
    {
         _fineCount += count;
    }

    private Time _startTime;
    public Time GetStartTime(){return _startTime;}
    public void SetStartTime(Time time)
    {
        _startTime.second = time.second;
        _startTime.minute = time.minute;
        _startTime.hour = time.hour;
    }

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

    private int _color;
    public int GetColor(){return _color;}

    public Participant(String number,String fio, String country,String birthYear, String group, int color)
    {
        _fullFineTime = new Time();
        _fineTime = new Time();
        _startTime = new Time();
        _resultTime = new Time();

        _FIO = fio;
        _country = country;
        _birthYear = birthYear;
        _number = number;
        if(group == "")
        {
            //Исправить из ресурсов
            _group = "Без группы";
        }
        else
        {
            _group = group;
        }
        _color = color;
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
