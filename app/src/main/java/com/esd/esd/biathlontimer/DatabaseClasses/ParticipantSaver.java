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

    public void SaveParticipantToDatabase(Participant participant)
    {
        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbParticipant.COLUMN_NAME, participant.GetFIO());
        val.put(DatabaseProvider.DbParticipant.COLUMN_COUNTRY, participant.GetCountry());
        val.put(DatabaseProvider.DbParticipant.COLUMN_YEAR, participant.GetBirthYear());
        _db.insert(DatabaseProvider.DbParticipant.TABLE_NAME, null, val);
    }

    public Participant[] GetAllParticipants()
    {
        Participant[] localArr;
        _db = _dbProvider.getReadableDatabase();
        String[] proj=
                {
                        DatabaseProvider.DbParticipant.COLUMN_NAME,
                        DatabaseProvider.DbParticipant.COLUMN_COUNTRY,
                        DatabaseProvider.DbParticipant.COLUMN_YEAR
                };
        Cursor cursor = _db.query(DatabaseProvider.DbParticipant.TABLE_NAME, proj, null, null, null, null, DatabaseProvider.DbParticipant.COLUMN_NAME);
        cursor.moveToFirst();
        int rowsCount = cursor.getCount();
        localArr = new Participant[rowsCount];
        for(int i = 0; i < rowsCount; i++)
        {
            localArr[i] = new Participant(cursor.getString(0), cursor.getString(1),1996);
            if(i < rowsCount - 1) cursor.moveToNext();
        }

        return localArr;
    }

    public void DeleteParticipant(Participant participant)
    {
        _db = _dbProvider.getWritableDatabase();
        _db.delete(DatabaseProvider.DbParticipant.TABLE_NAME, DatabaseProvider.DbParticipant.COLUMN_NAME + "=?" + " and " + DatabaseProvider.DbParticipant.COLUMN_COUNTRY
                + "=?", new String[]{participant.GetFIO(), participant.GetCountry()});
    }
}
