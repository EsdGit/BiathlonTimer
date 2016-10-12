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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    public static void OpenExcelFile(String fullPath)
    {
        try
        {
            String path = Environment.getExternalStorageDirectory().getPath();
            HSSFWorkbook MyExcelBook = new HSSFWorkbook(new FileInputStream(fullPath));

            // Здесь необходимо добавить xlsx расширение и потом уже создавать объект типа Competition
            Log.i("XLS","Работаем");
        }catch (Exception e)
        {
            try
            {
                File localFile = new File(fullPath);
                localFile.canRead();
                FileInputStream fis = new FileInputStream(new File(fullPath));
                XSSFWorkbook MyExcelFile = (XSSFWorkbook) WorkbookFactory.create(localFile);
                MyExcelFile.getName("Жопа");
               // MyExcelFile.getSheetAt(0).getRow(0).getCell(0).
            }
            catch (Exception e1)
            {
                Log.i("XLS","Не открылся!");
            }

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
