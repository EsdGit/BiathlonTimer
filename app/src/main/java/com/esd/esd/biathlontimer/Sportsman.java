package com.esd.esd.biathlontimer;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Oleg on 18.12.2016.
 */

public class Sportsman extends RealmObject
{
    @PrimaryKey
    private long id;

    // Свойства
    private int number;
    private String name;
    private int year;
    private String country;
    private String group;
    @Ignore
    private boolean isChecked;

    // Методы GET
    public int getNumber() {return number;}
    public String getName(){return name;}
    public int getYear(){return year;}
    public String getCountry(){return country;}
    public String getGroup(){return group;}
    public boolean isChecked(){return isChecked;}
    public long getId(){return id;}

    public Sportsman(int number, String name, int year, String country, String group)
    {
        this.number = number;
        this.name = name;
        this.year = year;
        this.country = country;
        this.group = group;
    }

    public Sportsman()
    {

    }

    public void setInfo(int number, String name, int year, String country, String group)
    {
        this.number = number;
        this.name = name;
        this.year = year;
        this.country = country;
        this.group = group;
    }

    public void setChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    public void setId(long newId)
    {
        id = newId;
    }


}
