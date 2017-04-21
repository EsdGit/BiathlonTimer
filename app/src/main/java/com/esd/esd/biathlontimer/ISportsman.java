package com.esd.esd.biathlontimer;

/**
 * Created by Oleg on 25.12.2016.
 */

public interface ISportsman
{
    int getNumber();
    String getName();
    int getYear();
    String getCountry();
    String getGroup();
    boolean isChecked();
    long getId();
    int getColor();

    void setNumber(int number);
    void setName(String name);
    void setYear(int year);
    void setCountry(String country);
    void setGroup(String group);
    void setChecked(boolean isChecked);
    void setId(long id);
    void setColor(int color);


}
