package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.esd.esd.biathlontimer.Participant;

/**
 * Created by NIL_RIMS_2 on 14.12.2016.
 */

public class LapDataSaver
{
    private static DatabaseProvider _dbProvider;
    private static SQLiteDatabase _db;

    public LapDataSaver(Context context){_dbProvider = new DatabaseProvider(context);}

    public boolean SaveParticiapntToLap(String tableName, Participant participant)
    {
        // надо бы проверку
        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbLapData.COLUMN_NUMBER, participant.GetNumber());
        val.put(DatabaseProvider.DbLapData.COLUMN_FIO, participant.GetFIO());
        val.put(DatabaseProvider.DbLapData.COLUMN_YEAR, participant.GetBirthYear());
        val.put(DatabaseProvider.DbLapData.COLUMN_COUNTRY, participant.GetCountry());
        _db.insert(tableName, null, val);
        _db.close();
        return true;
    }

    public String GetData(String tableName, Participant participant, String column, String orderBy)
    {
        _db = _dbProvider.getReadableDatabase();
        String[] proj =
        {
            column
        };
        Cursor cursor = _db.query(tableName, proj, DatabaseProvider.DbLapData.COLUMN_FIO + "=?" + " and " + DatabaseProvider.DbLapData.COLUMN_COUNTRY +
                "=?"+" and " + DatabaseProvider.DbLapData.COLUMN_YEAR + "=?", new String[]{participant.GetFIO(), participant.GetCountry(), participant.GetBirthYear()},null, null, orderBy + "ASC");
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public void AddDataToLap(String tableName, Participant participant, String column, String value)
    {
        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(column, value);
        _db.update(tableName, val, DatabaseProvider.DbLapData.COLUMN_FIO + "=?" + " and " + DatabaseProvider.DbLapData.COLUMN_COUNTRY +
                "=?"+" and " + DatabaseProvider.DbLapData.COLUMN_YEAR + "=?", new String[]{participant.GetFIO(), participant.GetCountry(), participant.GetBirthYear()});
        _db.close();
    }
}
