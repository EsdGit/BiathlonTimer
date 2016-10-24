package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.esd.esd.biathlontimer.Competition;

/**
 * Created by NIL_RIMS_2 on 24.10.2016.
 */

public class CompetitionSaver
{
    private static DatabaseProvider _dbProvider;
    private static SQLiteDatabase _db;

    public CompetitionSaver(Context context)
    {
        _dbProvider = new DatabaseProvider(context);
    }

    public void SaveCompetitionToDatabase(Competition competition)
    {
        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME, competition.GetName());
        val.put(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE, competition.GetDate());
        val.put(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_STATE, competition.GetState());
        _db.insert(DatabaseProvider.DbCompetitions.TABLE_NAME, null, val);
    }

    public Competition[] GetAllCompetitions()
    {
        Competition[] localArr;
        _db = _dbProvider.getReadableDatabase();
        String[] proj =
                {
                        DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME,
                        DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE,
                        DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_STATE
                };
        Cursor cursor = _db.query(DatabaseProvider.DbCompetitions.TABLE_NAME, proj, null,null,null,null, DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE);
        cursor.moveToFirst();
        int rowsCount = cursor.getCount();
        localArr = new Competition[rowsCount];
        for(int i = 0; i < rowsCount; i++)
        {
            localArr[i] = new Competition(cursor.getString(0), cursor.getString(1), Boolean.valueOf(cursor.getString(2)), cursor.getString(3));
            if(i < rowsCount - 1) cursor.moveToNext();
        }

        return localArr;
    }

    public void DeleteCompetitionFromDatabase(Competition competition)
    {
        _db = _dbProvider.getWritableDatabase();
        _db.delete(DatabaseProvider.DbCompetitions.TABLE_NAME, DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME + "=?" + " and "+
                DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE + "=?"+" and "+ DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_STATE+"=?"+" and "+
                        DatabaseProvider.DbCompetitions.COLUMN_DB_PATH+"=?", new String[]{competition.GetName(), competition.GetDate(), String.valueOf(competition.GetState()),
                        competition.GetDbParticipantPath()});
    }
}