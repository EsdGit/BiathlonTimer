package com.esd.esd.biathlontimer.DatabaseClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.esd.esd.biathlontimer.Competition;


// Класс отвечающий за сохранение настроек в базе данных
public class SettingsSaver
{
    private DatabaseProvider _dbProvider;
    private SQLiteDatabase _db;

    public SettingsSaver(Context context)
    {
        _dbProvider = new DatabaseProvider(context);
    }

    public void SaveSettingsToDb(Competition competition)
    {
        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbSettings.COLUMN_INTERVAL, competition.GetInterval());
        val.put(DatabaseProvider.DbSettings.COLUMN_CHECK_POINTS, competition.GetCheckPointsCount());
        val.put(DatabaseProvider.DbSettings.COLUMN_START_TYPE, competition.GetStartType());
        val.put(DatabaseProvider.DbSettings.COLUMN_TIME_TO_START, competition.GetTimeToStart());
        val.put(DatabaseProvider.DbSettings.COLUMN_GROUPS, competition.GetGroups());
        val.put(DatabaseProvider.DbSettings.COLUMN_SECOND_INTERVAL, competition.GetSecondInterval());
        val.put(DatabaseProvider.DbSettings.COLUMN_NUMBER_SECOND_INTERVAL, competition.GetNumberSecondInterval());
        val.put(DatabaseProvider.DbSettings.COLUMN_FINE, competition.GetFineTime());
        _db.insert(competition.GetSettingsPath(), null, val);
        _db.close();
    }

    public String GetSetting(Competition competition, String column)
    {
        _db = _dbProvider.getReadableDatabase();
        String[] proj =
                {
                    column
                };
        Cursor cursor = _db.query(competition.GetSettingsPath(), proj, null, null, null, null,null);
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public void DeleteSettingsFromDb(Competition competition)
    {
        _db = _dbProvider.getWritableDatabase();
        _db.delete(competition.GetSettingsPath(), null, null);
        _db.close();
    }
}
