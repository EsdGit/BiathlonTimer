package com.esd.esd.biathlontimer;

import android.graphics.Color;
import android.text.format.Time;

import java.util.ArrayList;

// Класс участника
public class Participant
{
    public static int LapsCount = 0;

    private int[] _places;
    public int GetPlace(int lapNumber)
    {
        return _places[lapNumber];
    }
    public void SetPlace(int place, int lapNumber)
    {
        _places[lapNumber] = place;
    }

    private Time[] _resultTimes;
    public Time GetResultTime(int lapNumber)
    {
        Time returnTime = new Time();
        returnTime.second = _resultTimes[lapNumber].second + _fullFineTime.second;
        returnTime.minute = _resultTimes[lapNumber].minute + _fullFineTime.minute;
        returnTime.hour = _resultTimes[lapNumber].hour + _fullFineTime.hour;
        returnTime.normalize(false);
        return returnTime;
    }

    public void SetResultTime(Time time, int lapNumber)
    {
        _resultTimes[lapNumber] = new Time(time);
    }

    private Time _fullFineTime;
    private Time[] _fineTime;
    public Time GetFineTime(int lapNumber){return _fineTime[lapNumber];}
    public void SetFineTime(Time fineTime, int lapNumber)
    {
        _fullFineTime.second += fineTime.second;
        _fullFineTime.minute += fineTime.minute;
        _fullFineTime.hour += fineTime.hour;
        _fullFineTime.normalize(false);
        if(lapNumber < _fineTime.length)
        {
            _fineTime[lapNumber].second += fineTime.second;
            _fineTime[lapNumber].minute += fineTime.minute;
            _fineTime[lapNumber].hour += fineTime.hour;
            _fineTime[lapNumber].normalize(false);
        }
        else
        {
            _fineTime[lapNumber].second = fineTime.second;
            _fineTime[lapNumber].minute = fineTime.minute;
            _fineTime[lapNumber].hour = fineTime.hour;
            _fineTime[lapNumber].normalize(false);
        }
    }

    private int[] _fineCount;
    public void SetFineCount(int count, int lapNumber)
    {
         _fineCount[lapNumber] += count;
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
        _fineTime = new Time[LapsCount];
        _fineCount = new int[LapsCount];
        _startTime = new Time();
        _resultTimes = new Time[LapsCount];
        _places = new int[LapsCount];

        StartPositionArrays();

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

    private void StartPositionArrays()
    {
        for(int i = 0; i < LapsCount; i++)
        {
            _fineTime[i] = new Time();
            _fineCount[i] = 0;
            _resultTimes[i] = new Time();
            _places[i] = 0;
        }
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
