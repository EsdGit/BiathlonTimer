package com.esd.esd.biathlontimer;

import android.text.format.Time;

import org.apache.poi.ss.formula.ExternSheetReferenceToken;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Oleg on 25.12.2016.
 */

public class MegaSportsman extends RealmObject implements ISportsman
{
    @PrimaryKey
    private long id;

    // Свойства
    private int number;
    private String name;
    private int year;
    private String country;
    private String group;
    private int color;

    private Integer _place;
    private int _fineCount;
    private String _resultsDb;
    private String _finesTimeDb;
    @Ignore
    private boolean isChecked;
    @Ignore
    //private Time[] _resultTime;
    private Time _resultTime;
    @Ignore
    private Time _startTime;
    @Ignore
    //private Time[] _fineTime;
    private Time _fineTime;
    @Ignore
    private int _currentLap;


    public MegaSportsman(Sportsman sportsman)
    {
        this.number = sportsman.getNumber();
        this.name = sportsman.getName();
        this.year = sportsman.getYear();
        this.country = sportsman.getCountry();
        this.group = sportsman.getGroup();
        this.color = sportsman.getColor();
        this.id = sportsman.getId();
        _currentLap = 0;
    }

    public MegaSportsman(MegaSportsman sportsman)
    {
        this.number = sportsman.getNumber();
        this.name = sportsman.getName();
        this.year = sportsman.getYear();
        this.country = sportsman.getCountry();
        this.group = sportsman.getGroup();
        this.color = sportsman.getColor();
        this.id = sportsman.getId();
        this._startTime = sportsman.getStartTime();
        this._fineCount = sportsman.getFineCount();
        this._fineTime = sportsman.getFineTime();
        _currentLap = 0;
    }


    public MegaSportsman()
    {

    }

    public MegaSportsman(int number, String name, int year, String country, String group, int color)
    {
        this.number = number;
        this.name = name;
        this.year = year;
        this.country = country;
        this.group = group;
        this.color = color;
        _currentLap = 0;
    }

//    public void setLapsCount(int lapsCount)
//    {
//        _resultTime = new Time[lapsCount];
//        _places = new int[lapsCount];
//        _fineCount = new int[lapsCount];
//        _fineTime = new Time[lapsCount];
//    }

//    public void makeForSaving()
//    {
//        _placesDb = "";
//        _resultsDb = "";
//        _finesCountDb = "";
//        _finesTimeDb = "";
//        for(int i = 0;i < _places.length; i++)
//        {
//            _placesDb += String.valueOf(_places[i])+",";
//            if(_resultTime[i] == null) _resultsDb += "null,";
//            else _resultsDb += _resultTime[i].format("%H:%M:%S")+",";
//            _finesCountDb += String.valueOf(_fineCount[i])+",";
//            if(_fineTime[i] == null) _finesTimeDb += "null,";
//            else _finesTimeDb += _fineTime[i].format("%H:%M:%S")+",";
//        }
//    }

//    public void setFineCount(int fineCount, int lapNumber)
//    {
//        _fineCount[lapNumber] += fineCount;
//    }
    public void setFineCount(int fineCount)
    {
        _fineCount += fineCount;
    }

//    public int getFineCount(int lapNumber)
//    {
//        return _fineCount[lapNumber];
//    }
    public int getFineCount()
    {
        return _fineCount;
    }

//    public void setFineTime(Time fine, int lapNumber)
//    {
//        if(_fineTime[lapNumber] == null)
//            _fineTime[lapNumber] = new Time(fine);
//        else
//        {
//            _fineTime[lapNumber].second += fine.second;
//            _fineTime[lapNumber].minute += fine.minute;
//            _fineTime[lapNumber].hour += fine.hour;
//            _fineTime[lapNumber].normalize(false);
//        }
//    }

    public void setFineTime(Time fine)
    {
        if(fine == null)
        {
            _fineTime = null;
            return;
        }
        if(_fineTime == null)
            _fineTime = new Time(fine);
        else
        {
            _fineTime.second += fine.second;
            _fineTime.minute += fine.minute;
            _fineTime.hour += fine.hour;
            _fineTime.normalize(false);
        }
        _finesTimeDb = _fineTime.format("%H:%M:%S");
    }

    public Time getFineTime()
    {
        return _fineTime;
    }

    public void setResultTime(Time result)
    {
        _resultTime = new Time(result);
        _resultsDb = _resultTime.format("%H:%M:%S");
    }

    public Time getResultTime()
    {
        if(_fineTime != null && _resultTime != null)
        {
            Time localTime = new Time(_resultTime);
            localTime.hour += _fineTime.hour;
            localTime.minute += _fineTime.minute;
            localTime.second += _fineTime.second;
            localTime.normalize(false);
            return localTime;
        }
        return _resultTime;
    }

    public String getResult()
    {
        return _resultsDb;
    }

    public void setPlace(int place)
    {
        _place = place;
    }

    public Integer getPlace()
    {
        return _place;
    }

    public void setStartTime(Time startTime)
    {
        _startTime = new Time(startTime);
    }

    public Time getStartTime()
    {
        return _startTime;
    }

    @Override
    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getCountry() {
        return country;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    public void setCurrentLap(int lap){_currentLap = lap;}

    public int getCurrentLap(){return _currentLap;}

}
