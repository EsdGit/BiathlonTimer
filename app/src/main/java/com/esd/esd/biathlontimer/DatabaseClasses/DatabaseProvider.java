package com.esd.esd.biathlontimer.DatabaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider.DbSettings.COLUMN_PARTICIPANT_COUNT;

// Класс для работы с локальной базой данных SQLLite
public class DatabaseProvider extends SQLiteOpenHelper
{
    public static final int DatabaseVersion = 1;
    public static final String DatabaseName = "BiathlonDatabase.db";


    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_PARTICIPANT_TABLE = "CREATE TABLE " + DbParticipant.TABLE_NAME +
            " (" + DbParticipant._ID + " INTEGER PRIMARY KEY," + DbParticipant.COLUMN_NUMBER + TEXT_TYPE +COMMA_SEP+ DbParticipant.COLUMN_NAME + TEXT_TYPE +
            COMMA_SEP + DbParticipant.COLUMN_COUNTRY + TEXT_TYPE + COMMA_SEP + DbParticipant.COLUMN_YEAR + TEXT_TYPE + COMMA_SEP+
            DbParticipant.COLUMN_GROUP + TEXT_TYPE + COMMA_SEP + DbParticipant.COLUMN_COLOR + TEXT_TYPE+")";
    private static final String SQL_DELETE_PARTICIPANT_TABLE = "DROP TABLE IF EXISTS " + DbParticipant.TABLE_NAME;


    private static final String SQL_CREATE_COMPETITION_TABLE = "CREATE TABLE " + DbCompetitions.TABLE_NAME +
            " (" + DbCompetitions._ID + " INTEGER PRIMARY KEY," + DbCompetitions.COLUMN_COMPETITION_NAME + TEXT_TYPE +
            COMMA_SEP + DbCompetitions.COLUMN_COMPETITION_DATE + TEXT_TYPE + COMMA_SEP + DbCompetitions.COLUMN_SETTINGS_PATH +
            TEXT_TYPE + COMMA_SEP + DbCompetitions.COLUMN_DB_PATH + TEXT_TYPE + ")";
    private static final String SQL_DELETE_COMPETITION_TABLE = "DROP TABLE IF EXISTS " + DbCompetitions.TABLE_NAME;

    public DatabaseProvider(Context context)
    {
        super(context, DatabaseName, null, DatabaseVersion);
    };

    // Абстрактный класс базы данных с настройками
    public static abstract class DbSettings implements BaseColumns
    {
        public static final String COLUMN_INTERVAL = "Interval";
        public static final String COLUMN_TIME_TO_START = "TimeToStart";
        public static final String COLUMN_START_TYPE = "StartType";
        public static final String COLUMN_CHECK_POINTS = "CheckPointsCount";
        public static final String COLUMN_GROUPS = "GroupsNames";
        public static final String COLUMN_SECOND_INTERVAL = "SecondInterval";
        public static final String COLUMN_NUMBER_SECOND_INTERVAL = "NumberSecondInterval";
        public static final String COLUMN_FINE = "Fine";
        public static final String COLUMN_PARTICIPANT_COUNT = "CountAndStartNumber";
    }

    // Абстрактный класс базы данных участников
    public static abstract class DbParticipant implements BaseColumns
    {
        public static final String TABLE_NAME = "participants";
        public static final String COLUMN_NUMBER = "Number";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_COUNTRY = "Country";
        public static final String COLUMN_YEAR = "BirthYear";
        public static final String COLUMN_GROUP = "GroupName";
        public static final String COLUMN_COLOR = "Color";
    }

    // Абстрактный класс базы данных соревнований
    public static abstract class DbCompetitions implements BaseColumns
    {
        public static final String TABLE_NAME = "competitions";
        public static final String COLUMN_COMPETITION_NAME= "CompetitionName";
        public static final String COLUMN_COMPETITION_DATE = "Date";
        public static final String COLUMN_SETTINGS_PATH = "SettingsDb";
        public static final String COLUMN_DB_PATH = "DbParticipantPath";
    }

    public static abstract class DbLapData implements BaseColumns
    {
        public static final String COLUMN_NUMBER = "Number";
        public static final String COLUMN_PLACE = "Place";
        public static final String COLUMN_FIO = "FIO";
        public static final String COLUMN_YEAR = "Year";
        public static final String COLUMN_COUNTRY = "Country";
        public static final String COLUMN_RESULT = "Result";
        public static final String COLUMN_START_TIME = "StartTime";
        public static final String COLUMN_FINE_COUNT = "FineCount";
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(SQL_CREATE_COMPETITION_TABLE);
        db.execSQL(SQL_CREATE_PARTICIPANT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(SQL_DELETE_PARTICIPANT_TABLE);
        db.execSQL(SQL_DELETE_COMPETITION_TABLE);
        onCreate(db);
    }

    public void AddNewParticipantTable(String name)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + name +
                " (" + DbParticipant._ID + " INTEGER PRIMARY KEY, " + DbParticipant.COLUMN_NUMBER + TEXT_TYPE + COMMA_SEP+
                DbParticipant.COLUMN_NAME + TEXT_TYPE + COMMA_SEP + DbParticipant.COLUMN_COUNTRY + TEXT_TYPE + COMMA_SEP
                + DbParticipant.COLUMN_YEAR + TEXT_TYPE + COMMA_SEP+DbParticipant.COLUMN_GROUP+TEXT_TYPE+COMMA_SEP + DbParticipant.COLUMN_COLOR + TEXT_TYPE+")";
        this.getWritableDatabase().execSQL(sql);
    }

    public void AddNewSettingsTable(String name)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + name +
                " (" + DbSettings._ID + " INTEGER PRIMARY KEY," + DbSettings.COLUMN_INTERVAL + TEXT_TYPE +
                COMMA_SEP + DbSettings.COLUMN_CHECK_POINTS + TEXT_TYPE + COMMA_SEP + DbSettings.COLUMN_START_TYPE + TEXT_TYPE +
                COMMA_SEP + DbSettings.COLUMN_TIME_TO_START + TEXT_TYPE + COMMA_SEP + DbSettings.COLUMN_GROUPS + TEXT_TYPE+
                COMMA_SEP + DbSettings.COLUMN_SECOND_INTERVAL + TEXT_TYPE+COMMA_SEP + DbSettings.COLUMN_NUMBER_SECOND_INTERVAL +
                TEXT_TYPE+ COMMA_SEP + DbSettings.COLUMN_FINE + TEXT_TYPE+ COMMA_SEP+ DbSettings.COLUMN_PARTICIPANT_COUNT+ TEXT_TYPE+")";
        this.getWritableDatabase().execSQL(sql);
    }

    public void AddNewLapTable(String name)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (" + DbLapData._ID + " INTEGER PRIMARY KEY,"+DbLapData.COLUMN_NUMBER + INTEGER_TYPE + COMMA_SEP +
                DbLapData.COLUMN_PLACE + INTEGER_TYPE + COMMA_SEP + DbLapData.COLUMN_FIO + TEXT_TYPE + COMMA_SEP + DbLapData.COLUMN_YEAR + TEXT_TYPE + COMMA_SEP +
                DbLapData.COLUMN_COUNTRY  + TEXT_TYPE + COMMA_SEP + DbLapData.COLUMN_RESULT + TEXT_TYPE + COMMA_SEP + DbLapData.COLUMN_START_TIME + TEXT_TYPE + COMMA_SEP +
                DbLapData.COLUMN_FINE_COUNT + INTEGER_TYPE+") ";
        this.getWritableDatabase().execSQL(sql);
    }

    public void DeleteTable(String name)
    {
        String sql = "DROP TABLE IF EXISTS "+name;
        this.getWritableDatabase().execSQL(sql);
    }
}
