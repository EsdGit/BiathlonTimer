package com.esd.esd.biathlontimer;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Oleg on 18.12.2016.
 */

public class Sportsman extends RealmObject implements ISportsman
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
    public int getColor(){return color;}

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

    public Sportsman(Sportsman sportsman)
    {
        this.number = sportsman.getNumber();
        this.name = sportsman.getName();
        this.year = sportsman.getYear();
        this.country = sportsman.getCountry();
        this.color = sportsman.getColor();
        this.group = sportsman.getGroup();
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

    public void setColor(int color){this.color = color;}

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
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
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof Sportsman)) return false;

        if(this.name.equals(((Sportsman) obj).name) && this.year == ((Sportsman) obj).year && this.country.equals(((Sportsman) obj).country))
        {
            return true;
        }
        return false;
    }
}
