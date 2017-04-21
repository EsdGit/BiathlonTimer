package com.esd.esd.biathlontimer;


import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

// Класс реализующий таблицу участников
public class Competition extends RealmObject
{
    @PrimaryKey
    private long id;

    private String name;
    private String date;
    private String interval;
    private String timeToStart;
    private int checkPointsCount;
    private String startType;
    private String groups;
    private String secondInterval;
    private String numberSecondInterval;
    private String fineTime;
    private int maxParticipantCount;
    private int startNumber;
    private String _dbParticipantPath;
    private boolean isFinished;

    @Ignore
    private boolean isChecked;
    @Ignore
    private String _nameDateString;
    @Ignore
    private String _settingsPath;
    @Ignore
    private Context _localContext;
    @Ignore
    private Date Date;
    public String getName()
    {
        return name;
    }


    public String getDate()
    {
        return date;
    }



    public String getDbParticipantPath(){return _dbParticipantPath;};

    public void setId(long newId)
    {
        id = newId;
    }

    // Настройки
    public boolean isFinished(){return isFinished;}
    public void setFinished(boolean isFinished){this.isFinished = isFinished;}

    public String getInterval(){return interval;}

    public boolean isChecked(){return isChecked;}
    public String getTimeToStart(){return timeToStart;}


    public int getCheckPointsCount(){return checkPointsCount;}

    
    public String getStartType(){return startType;}

    
    public String getGroups(){return groups;}


    public String getSecondInterval(){return secondInterval;}


    public String getNumberSecondInterval(){return numberSecondInterval;}


    public String getFineTime(){return fineTime;}


    public int getMaxParticipantCount(){return maxParticipantCount;}


    public int getStartNumber(){return startNumber;}


    public String getNameDateString(){return _nameDateString;}

    public void setChecked(boolean isChecked)
    {
        this.isChecked = isChecked;
    }

    public Competition(String name, String date, Context context)
    {
        this.name = name;
        this.date = date;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            Date = sdf.parse(date);
        }catch (ParseException ex)
        {

        }

        name = removePunct(name);
        date = removePunct(date);
        _nameDateString = name+date;
        _settingsPath = "settings"+name+date;
        _dbParticipantPath = "participants"+name+date;
        _localContext = context;

    }

    public Competition()
    {

    }

    private static final String PUNCT = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~ ";

    public static String removePunct(String str) {
        StringBuilder result = new StringBuilder(str.length());
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (PUNCT.indexOf(c) < 0) {
                result.append(c);
            }
        }
        return result.toString();
    }

    public Date getRealDate()
    {
        if(Date == null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            try {
                Date = sdf.parse(date);
            }catch (ParseException ex)
            {

            }
        }
        return Date;
    }

    public void SetCompetitionSettings(String startType, String interval, int checkPointsCount, String timeToStart,
                                       String groups, String secondInterval, String numberSecondInterval, String fineTime, int startNumber, int maxPartCount)
    {
        this.interval = interval;
        this.checkPointsCount = checkPointsCount;
        this.startType = startType;
        this.timeToStart = timeToStart;
        this.groups = groups;
        this.secondInterval = secondInterval;
        this.numberSecondInterval = numberSecondInterval;
        this.fineTime = fineTime;
        this.startNumber = startNumber;
        maxParticipantCount = maxPartCount;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof Competition)) return false;
        Competition localObj = (Competition) obj;
        if(this.getName().equals(localObj.getName()) && this.getDate().equals(localObj.getDate()))
        {
            return true;
        }
        return false;
    }
}
