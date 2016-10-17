package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Олег on 16.10.2016.
 */

public class DatabaseParticipantsProvider extends SQLiteOpenHelper {

    public static final int DatabaseVersion = 1;
    public static final String DatabaseName = "ParticipantsDatabase.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + DbParticipant.TABLE_NAME +
            " (" + DbParticipant._ID + " INTEGER PRIMARY KEY," + DbParticipant.COLUMN_NAME + TEXT_TYPE +
            COMMA_SEP + DbParticipant.COLUMN_COUNTRY + TEXT_TYPE + COMMA_SEP + DbParticipant.COLUMN_YEAR + INT_TYPE + ")";
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + DbParticipant.TABLE_NAME;

    public DatabaseParticipantsProvider(Context context)
    {
        super(context, DatabaseName, null, DatabaseVersion);

    }

    public static abstract class DbParticipant implements BaseColumns
    {
        public static final String TABLE_NAME = "participants";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_COUNTRY = "Country";
        public static final String COLUMN_YEAR = "YearOfBirth";

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
