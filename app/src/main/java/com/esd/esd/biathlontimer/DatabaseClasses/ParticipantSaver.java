package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.MessagePattern;

import com.esd.esd.biathlontimer.Participant;

/**
 * Created by NIL_RIMS_2 on 19.10.2016.
 */

public class ParticipantSaver
{
    private static DatabaseProvider _dbProvider;
    private static SQLiteDatabase _db;

    public ParticipantSaver(Context context)
    {
        _dbProvider = new DatabaseProvider(context);
    }

    public boolean SaveParticipantToDatabase(Participant participant, String tableName)
    {
        Participant[] localArr = GetAllParticipants(tableName, DatabaseProvider.DbParticipant.COLUMN_NAME);
        for(int i = 0; i<localArr.length; i++)
        {
            if(localArr[i].equals(participant))
            {
                return false;
            }
        }

        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbParticipant.COLUMN_NUMBER, participant.GetNumber());
        val.put(DatabaseProvider.DbParticipant.COLUMN_NAME, participant.GetFIO());
        val.put(DatabaseProvider.DbParticipant.COLUMN_COUNTRY, participant.GetCountry());
        val.put(DatabaseProvider.DbParticipant.COLUMN_YEAR, participant.GetBirthYear());
        _db.insert(tableName, null, val);
        _db.close();
        return true;
    }

    public Participant[] GetAllParticipants(String tableName, String orderBy)
    {
        Participant[] localArr;
        _db = _dbProvider.getReadableDatabase();
        String[] proj=
                {
                        DatabaseProvider.DbParticipant.COLUMN_NUMBER,
                        DatabaseProvider.DbParticipant.COLUMN_NAME,
                        DatabaseProvider.DbParticipant.COLUMN_COUNTRY,
                        DatabaseProvider.DbParticipant.COLUMN_YEAR
                };
        Cursor cursor = _db.query(tableName, proj, null, null, null, null, orderBy);
        cursor.moveToFirst();
        int rowsCount = cursor.getCount();
        localArr = new Participant[rowsCount];
        for(int i = 0; i < rowsCount; i++)
        {
            localArr[i] = new Participant(cursor.getString(0), cursor.getString(1), cursor.getString(2),cursor.getString(3));
            if(i < rowsCount - 1) cursor.moveToNext();
        }
        _db.close();
        return localArr;
    }

    public void DeleteParticipant(Participant participant, String tableName)
    {
        _db = _dbProvider.getWritableDatabase();
        _db.delete(tableName, DatabaseProvider.DbParticipant.COLUMN_NAME + "=?" + " and " + DatabaseProvider.DbParticipant.COLUMN_COUNTRY
                + "=?"+" and " + DatabaseProvider.DbParticipant.COLUMN_YEAR
                + "=?", new String[]{participant.GetFIO(), participant.GetCountry(), participant.GetBirthYear()});
        _db.close();
    }
}
