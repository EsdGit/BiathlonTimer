package com.esd.esd.biathlontimer;


import android.content.Context;

import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;

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
        name = removePunct(name);
        date = removePunct(date);
        _nameDateString = name+date;
        _settingsPath = "settings"+name+date;
        _dbParticipantPath = "participants"+name+date;
        _localContext = context;
        //GenerateParticipantDb();
        //GenerateSettingsDb();
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

//    public void GetAllSettingsToComp()
//    {
//        SettingsSaver saver = new SettingsSaver(_localContext);
//        interval = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_INTERVAL);
//        timeToStart = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_TIME_TO_START);
//        checkPointsCount = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_CHECK_POINTS);
//        groups = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_GROUPS);
//        startType = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_START_TYPE);
//        secondInterval = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_SECOND_INTERVAL);
//        numberSecondInterval = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_NUMBER_SECOND_INTERVAL);
//        fineTime = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_FINE);
//        String startNumberFromDb = saver.GetSetting(this, DatabaseProvider.DbSettings.COLUMN_PARTICIPANT_COUNT);
//        startNumber = Integer.valueOf(startNumberFromDb.split(",")[0]);
//        maxParticipantCount = Integer.valueOf(startNumberFromDb.split(",")[1]);
//    }

//    public void GetAllParticipantsToComp()
//    {
//        ParticipantSaver saver = new ParticipantSaver(_localContext);
//        Participant[] localArr = saver.GetAllParticipants(GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
//        localArr = SortByNumber(localArr);
//        for(int i = 0; i<localArr.length;i++)
//        {
//            _participants.add(localArr[i]);
//        }
//    }

//    private Participant[] SortByNumber(Participant[] arr)
//    {
//        Participant[] localArr = arr;
//        int count = localArr.length;
//        if(count == 0) return null;
//        int first;
//        int second;
//        int k = 0;
//        Participant helper;
//        while(k != count)
//        {
//            for(int i = 0; i < count - k - 1; i++)
//            {
//                first = Integer.valueOf(localArr[i].GetNumber());
//                second = Integer.valueOf(localArr[i+1].GetNumber());
//                if(first > second)
//                {
//                    helper = localArr[i];
//                    localArr[i]  = localArr[i+1];
//                    localArr[i+1] = helper;
//                }
//            }
//            k++;
//        }
//        return localArr;
//    }
    // Метод добавления участников соревнований, если такого участника нет
//    public void AddParticipant(Participant participant)
//    {
//        Participant[] localArr = _participants.toArray(new Participant[_participants.size()]);
//        for(int i = 0; i < localArr.length; i++)
//        {
//            if(localArr[i].equals(participant)) return;
//        }
//
//        _participants.add(participant);
//
//        ParticipantSaver ps = new ParticipantSaver(_localContext);
//        ps.SaveParticipantToDatabase(participant, _dbParticipantPath);
//    }

//    public void GenerateSettingsDb()
//    {
//        DatabaseProvider dbProvider = new DatabaseProvider(_localContext);
//        dbProvider.AddNewSettingsTable(_settingsPath);
//    }
//
//    private void GenerateParticipantDb()
//    {
//        DatabaseProvider dbProvider = new DatabaseProvider(_localContext);
//        dbProvider.AddNewParticipantTable(_dbParticipantPath);
//    }

//    public void DeleteParticipantsFromCompetition(Participant participant)
//    {
//        ParticipantSaver ps = new ParticipantSaver(_localContext);
//        Participant[] localArr = _participants.toArray(new Participant[_participants.size()]);
//        for(int i = 0; i < localArr.length; i++)
//        {
//            if(localArr[i].equals(participant))
//            {
//                _participants.remove(participant);
//                ps.DeleteParticipant(participant, _dbParticipantPath);
//            }
//        }
//    }
//
//    public int GetParticipantCount()
//    {
//        return _participants.size();
//    }


//    public Participant[] GetAllParticipants()
//    {
//        return _participants.toArray(new Participant[_participants.size()]);
//    }

    public void FinishCompetition()
    {
        _settingsPath = "null";
        // Удалить базу данных с настройками соревнования
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
