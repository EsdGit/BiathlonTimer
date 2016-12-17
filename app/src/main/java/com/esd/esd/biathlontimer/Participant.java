package com.esd.esd.biathlontimer;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;

import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.LapDataSaver;

import java.util.ArrayList;

import io.realm.RealmObject;

// Класс участника
public class Participant
{

    public void SetResultTime(String time, String tableName, LapDataSaver lapSaver)
    {
        lapSaver.AddDataToLap(tableName,this, DatabaseProvider.DbLapData.COLUMN_RESULT, time);
    }

    public String GetResultTime(String tableName, LapDataSaver lapSaver)
    {
        return lapSaver.GetData(tableName, this, DatabaseProvider.DbLapData.COLUMN_RESULT, DatabaseProvider.DbLapData.COLUMN_RESULT);
    }

    public void SetPlace(String tableName, LapDataSaver lapSaver, int place)
    {
        lapSaver.AddDataToLap(tableName, this, DatabaseProvider.DbLapData.COLUMN_PLACE, String.valueOf(place));
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

    public boolean wasChecked;

    public Participant(String number, String fio, String country, String birthYear, String group, int color)
    {
        _startTime = new Time();

        _FIO = fio;
        _country = country;
        _birthYear = birthYear;
        _number = number;
        wasChecked = false;
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
