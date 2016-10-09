package com.esd.esd.biathlontimer.DatabaseClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


// Класс отвечающий за сохранение настроек в базе данных
public class SettingsSaver
{
    private DatabaseProvider _dbProvider;
    private SQLiteDatabase _database;

    public SettingsSaver(Context context)
    {
        _dbProvider = new DatabaseProvider(context);
    }

    // Метод сохраняющие значения в базу данных
    public void SaveDataToDatabase(String settingName, String settingVal)
    {
        _database = _dbProvider.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_NAME, settingName);
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_DATA, settingVal);
        _database.update(DatabaseProvider.DbSettings.TABLE_NAME, val, DatabaseProvider.DbSettings.COLUMN_SETTING_NAME +
                " = ?", new String[]{settingName});
        _database.close();
    }

    // Метод получения данных из базы данных по имени настройки
    public String GetDataFromDatabase(String settingsName)
    {
        _database = _dbProvider.getReadableDatabase();
        String[] projection =
                {
                        DatabaseProvider.DbSettings.COLUMN_SETTING_NAME,
                        DatabaseProvider.DbSettings.COLUMN_SETTING_DATA
                };
        Cursor cursor = _database.query(DatabaseProvider.DbSettings.TABLE_NAME, projection, DatabaseProvider.DbSettings.COLUMN_SETTING_NAME+
                " = ?", new String[]{settingsName}, null, null, null);
        cursor.moveToFirst();
        _database.close();
        return cursor.getString(1);
    }
}
