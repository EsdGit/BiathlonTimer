package com.esd.esd.biathlontimer;

import android.text.format.Time;

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
    private String _placesDb;
    private String _resultsDb;
    private String _finesTimeDb;
    private String _finesCountDb;
    //private String results;
    @Ignore
    private boolean isChecked;
    @Ignore
    private Time[] _resultTime;
    @Ignore
    private Time _startTime;
    @Ignore
    private int[] _places;
    @Ignore
    private int[] _fineCount;
    @Ignore
    private Time[] _fineTime;


    public MegaSportsman(Sportsman sportsman)
    {
        this.number = sportsman.getNumber();
        this.name = sportsman.getName();
        this.year = sportsman.getYear();
        this.country = sportsman.getCountry();
        this.group = sportsman.getGroup();
        this.color = sportsman.getColor();
        this.id = sportsman.getId();
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
    }

    public void setLapsCount(int lapsCount)
    {
        _resultTime = new Time[lapsCount];
        _places = new int[lapsCount];
        _fineCount = new int[lapsCount];
        _fineTime = new Time[lapsCount];
    }

    public void makeForSaving()
    {
        _placesDb = "";
        _resultsDb = "";
        _finesCountDb = "";
        _finesTimeDb = "";
        for(int i = 0;i < _places.length; i++)
        {
            _placesDb += String.valueOf(_places[i])+",";
            if(_resultTime[i] == null) _resultsDb += "null,";
            else _resultsDb += _resultTime[i].format("%H:%M:%S")+",";
            _finesCountDb += String.valueOf(_fineCount[i])+",";
            if(_fineTime[i] == null) _finesTimeDb += "null,";
            else _finesTimeDb += _fineTime[i].format("%H:%M:%S")+",";
        }
    }

    public String[] getPlaceArr()
    {
        return _placesDb.split(",");
    }

    public String[] getResultArr()
    {
        return _resultsDb.split(",");
    }

    public void setFineCount(int fineCount, int lapNumber)
    {
        _fineCount[lapNumber] += fineCount;
    }

    public int getFineCount(int lapNumber)
    {
        return _fineCount[lapNumber];
    }

    public void setFineTime(Time fine, int lapNumber)
    {
        if(_fineTime[lapNumber] == null)
            _fineTime[lapNumber] = new Time(fine);
        else
        {
            _fineTime[lapNumber].second += fine.second;
            _fineTime[lapNumber].minute += fine.minute;
            _fineTime[lapNumber].hour += fine.hour;
            _fineTime[lapNumber].normalize(false);
        }
    }

    public Time getFineTime(int lapNumber)
    {
        return _fineTime[lapNumber];
    }

    public void setResultTime(Time result, int lapNumber)
    {
        _resultTime[lapNumber] = new Time(result);
    }

    public Time getResultTime(int lapNumber)
    {
        if(_fineTime[lapNumber] != null && _resultTime[lapNumber] != null)
        {
            Time localTime = new Time(_resultTime[lapNumber]);
            localTime.hour += _fineTime[lapNumber].hour;
            localTime.minute += _fineTime[lapNumber].minute;
            localTime.second += _fineTime[lapNumber].second;
            localTime.normalize(false);
            return localTime;
        }
        return _resultTime[lapNumber];
    }

    public void setPlace(int place, int lapNumber)
    {
        _places[lapNumber] = place;
    }

    public int getPlace(int lapNumber)
    {
        return _places[lapNumber];
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
}
