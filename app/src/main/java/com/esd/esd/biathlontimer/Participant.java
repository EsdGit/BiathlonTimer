package com.esd.esd.biathlontimer;

import android.graphics.Color;
import android.text.format.Time;

import java.util.ArrayList;

// Класс участника
public class Participant
{
    private ArrayList<Integer> _places;
    public int GetPlace(int lapNumber)
    {
        return _places.get(lapNumber).intValue();
    }
    public void SetPlace(int place, int lapNumber)
    {
        _places.add(lapNumber, place);
    }

    private ArrayList<Time> _resultTimes;
    public Time GetResultTime(int lapNumber)
    {
        Time returnTime = new Time();
        returnTime.second = _resultTimes.get(lapNumber).second + _fineTime.second;
        returnTime.minute = _resultTimes.get(lapNumber).minute + _fineTime.minute;
        returnTime.hour = _resultTimes.get(lapNumber).hour + _fineTime.hour;
        returnTime.normalize(false);
        return returnTime;
    }
    public void SetResultTime(Time time, int lapNumber)
    {
        _resultTimes.add(lapNumber, time);
    }

    private Time _fineTime;
    public Time GetFineTime(){return _fineTime;}
    public void SetFineTime(Time fineTime)
    {
        _fineTime.second = fineTime.second;
        _fineTime.minute = fineTime.minute;
        _fineTime.hour = fineTime.hour;
        _fineTime.normalize(false);
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
        _fineTime = new Time();
        _startTime = new Time();
        _resultTimes = new ArrayList<Time>();
        _places = new ArrayList<Integer>();
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
