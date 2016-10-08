package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

// Класс для работы с локальной базой данных SQLLite
public class DatabaseProvider extends SQLiteOpenHelper
{
    public static final int DatabaseVersion = 1;
    public static final String DatabaseName = "SettingsDatabase.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + DbSettings.TABLE_NAME+
            " (" + DbSettings._ID + " INTEGER PRIMARY KEY," + DbSettings.COLUMN_SETTING_NAME + TEXT_TYPE +
            COMMA_SEP + DbSettings.COLUMN_SETTING_DATA + TEXT_TYPE + ")";
    private static final String SQL_DELETE_TABLE = "DROP TABLE IF EXISTS " + DbSettings.TABLE_NAME;

    public DatabaseProvider(Context context)
    {
        super(context, DatabaseName, null, DatabaseVersion);
    };

    public static abstract class DbSettings implements BaseColumns
    {
        public static final String TABLE_NAME = "settings";
        public static final String COLUMN_SETTING_NAME = "settingName";
        public static final String COLUMN_SETTING_DATA = "settingData";
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }
}
