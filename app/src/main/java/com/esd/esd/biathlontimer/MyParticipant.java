package com.esd.esd.biathlontimer;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.processor.Constants;

/**
 * Created by Oleg on 18.12.2016.
 */

public class MyParticipant extends RealmObject
{
    @PrimaryKey
    private long id;

    private String _name;
    private int _year;
    private String _country;
    private int _number;
    private String _group;

    public MyParticipant()
    {

    }

    public void setData(String name, int year,String country, int number, String group)
    {
        _name = name;
        _year = year;
        _country = country;
        _number = number;
        _group = group;
    }
}
