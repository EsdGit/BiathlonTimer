package com.esd.esd.biathlontimer;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Activities.MainActivity;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

// Класс для работы с excel файлом
public class ExcelHelper
{

    public ExcelHelper()
    {

    }

    public static boolean CheckChooseFile(String nameFile, Context context)
    {
        if(nameFile.endsWith(".xls") || nameFile.endsWith(".xlsx"))
        {
            return true;
        }
        else
        {
            Toast.makeText(context,"Неверный формат файла, выберите .xls или .xlsx",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static void OpenExcelFile(String fullPath)
    {
        try
        {
            HSSFWorkbook MyExcelBook = new HSSFWorkbook(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/" + fullPath));
            // Здесь необходимо добавить xlsx расширение и потом уже создавать объект типа Competition
            Log.i("XLS","Работаем");
        }catch (Exception e)
        {
            Log.i("XLS","Не открылся!");
        }
    }

    // Метод создаёт excel файл и сохраняет его в папку приложения
    public static void CreateFileWithResult(Context context)
    {
        HSSFWorkbook resultWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = resultWorkbook.createSheet("Лист1");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Привет");

        try {
            try {
                resultWorkbook.write(new FileOutputStream(context.getExternalFilesDir(Environment.DIRECTORY_ALARMS) + "test.xls"));
            }catch (FileNotFoundException e)
            {
                Log.i("New", "Error");
            }
            resultWorkbook.close();
        }catch (IOException e)
        {
            Log.i("gh", "wfe");
        }
    }
}
