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
    private Context _localContext;

    public CompetitionSaver(Context context)
    {
        _localContext = context;
        _dbProvider = new DatabaseProvider(context);
    }

    public boolean SaveCompetitionToDatabase(Competition competition)
    {
        Competition[] localArr = GetAllCompetitions(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME);
        for(int i = 0; i<localArr.length; i++)
        {
            if(localArr[i].equals(competition))
            {
                return false;
            }
        }
        _db = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME, competition.GetName());
        val.put(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE, competition.GetDate());
        val.put(DatabaseProvider.DbCompetitions.COLUMN_SETTINGS_PATH, competition.GetSettingsPath());
        val.put(DatabaseProvider.DbCompetitions.COLUMN_DB_PATH, competition.GetDbParticipantPath());
        _db.insert(DatabaseProvider.DbCompetitions.TABLE_NAME, null, val);
        _db.close();
        return true;
    }

    public Competition[] GetAllCompetitions(String orderBy)
    {
        Competition[] localArr;
        _db = _dbProvider.getReadableDatabase();
        String[] proj =
                {
                        DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME,
                        DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE,
                };
        Cursor cursor = _db.query(DatabaseProvider.DbCompetitions.TABLE_NAME, proj, null,null,null,null, orderBy);
        cursor.moveToFirst();
        int rowsCount = cursor.getCount();
        localArr = new Competition[rowsCount];
        for(int i = 0; i < rowsCount; i++)
        {
            localArr[i] = new Competition(cursor.getString(0), cursor.getString(1), _localContext);
            if(i < rowsCount - 1) cursor.moveToNext();
        }
        _db.close();
        return localArr;
    }

    public void DeleteCompetitionFromDatabase(Competition competition)
    {
        _db = _dbProvider.getWritableDatabase();
        _db.delete(DatabaseProvider.DbCompetitions.TABLE_NAME, DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME + "=?" + " and "+
                DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE + "=?"+" and "+ DatabaseProvider.DbCompetitions.COLUMN_SETTINGS_PATH+"=?"+" and "+
                        DatabaseProvider.DbCompetitions.COLUMN_DB_PATH+"=?", new String[]{competition.GetName(), competition.GetDate(), competition.GetSettingsPath(),
                        competition.GetDbParticipantPath()});
        _db.close();
    }
}
