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
    private String resultTimeString;
    @Ignore
    private boolean isChecked;
    @Ignore
    private Time _resultTime;
    @Ignore
    private Time _startTime;


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

    public void setResultTime(Time result)
    {
        _resultTime = new Time(result);
        resultTimeString = result.format("%H:%M:%S");
    }

    public String getResultTime()
    {
        return resultTimeString;
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
