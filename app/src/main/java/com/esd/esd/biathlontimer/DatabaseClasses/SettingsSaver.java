package com.esd.esd.biathlontimer.DatabaseClasses;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;


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
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_NAME,settingName);
        val.put(DatabaseProvider.DbSettings.COLUMN_SETTING_DATA, settingVal);
        _database.update(DatabaseProvider.DbSettings.TABLE_NAME,val,null,null);
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
        String order = DatabaseProvider.DbSettings.COLUMN_SETTING_NAME + " DESC";
        Cursor cursor = _database.query(DatabaseProvider.DbSettings.TABLE_NAME, projection, null, null, null, null, order);
        cursor.moveToFirst();
        for(int i = 0; i<cursor.getCount(); i++)
        {
            if(cursor.getString(0).contentEquals(settingsName))
            {
                return cursor.getString(1);
            }
        }
        return "БЕДА";
    }
}
