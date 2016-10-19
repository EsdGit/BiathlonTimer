package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

// Класс для работы с локальной базой данных SQLLite
public class DatabaseProvider extends SQLiteOpenHelper
{
    public static final int DatabaseVersion = 1;
    public static final String DatabaseName = "BiathlonDatabase.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_SETTINGS_TABLE = "CREATE TABLE " + DbSettings.TABLE_NAME +
            " (" + DbSettings._ID + " INTEGER PRIMARY KEY," + DbSettings.COLUMN_SETTING_NAME + TEXT_TYPE +
            COMMA_SEP + DbSettings.COLUMN_SETTING_DATA + TEXT_TYPE + ")";
    private static final String SQL_DELETE_SETTINGS_TABLE = "DROP TABLE IF EXISTS " + DbSettings.TABLE_NAME;


    private static final String SQL_CREATE_PARTICIPANT_TABLE = "CREATE TABLE " + DbParticipant.TABLE_NAME +
            " (" + DbParticipant._ID + " INTEGER PRIMARY KEY," + DbParticipant.COLUMN_NAME + TEXT_TYPE +
            COMMA_SEP + DbParticipant.COLUMN_COUNTRY + TEXT_TYPE + COMMA_SEP + DbParticipant.COLUMN_YEAR + INT_TYPE + ")";
    private static final String SQL_DELETE_PARTICIPANT_TABLE = "DROP TABLE IF EXISTS " + DbParticipant.TABLE_NAME;

    public DatabaseProvider(Context context)
    {
        super(context, DatabaseName, null, DatabaseVersion);
    };

    // Абстрактный класс базы данных с настройками
    public static abstract class DbSettings implements BaseColumns
    {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_SETTING_NAME = "settingName";
        public static final String COLUMN_SETTING_DATA = "settingData";
    }

    // Абстрактный класс базы данных участников
    public static abstract class DbParticipant implements BaseColumns
    {
        public static final String TABLE_NAME = "participants";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_COUNTRY = "Country";
        public static final String COLUMN_YEAR = "BirthYear";

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_PARTICIPANT_TABLE);
        db.execSQL(SQL_CREATE_SETTINGS_TABLE);
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_NAME, "Интервал");
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_DATA, "30");
        db.insert(DbSettings.TABLE_NAME,null,val);
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_NAME, "Количество КП");
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_DATA, "5");
        db.insert(DbSettings.TABLE_NAME,null,val);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_SETTINGS_TABLE);
        db.execSQL(SQL_DELETE_PARTICIPANT_TABLE);
        onCreate(db);
    }
}
