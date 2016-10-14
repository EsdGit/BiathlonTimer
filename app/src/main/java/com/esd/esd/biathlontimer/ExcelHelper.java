package com.esd.esd.biathlontimer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

// Класс для работы с excel файлом
public class ExcelHelper
{

    public ExcelHelper()
    {

    }

    public static boolean IsRightFile(String nameFile)
    {
        if(nameFile.endsWith(".xls"))
        {
            OpenXlsFile(nameFile);
            return true;
        }
        if(nameFile.endsWith(".xlsx"))
        {
            OpenXlsxFile(nameFile);
            return true;
        }
        else
        {
            return false;
        }
    }

    private static void OpenXlsFile(String fullPath)
    {
        try
        {
            String pathFile = Environment.getExternalStorageDirectory().getPath() + "/" + fullPath;
            HSSFWorkbook MyExcelBook = new HSSFWorkbook(new FileInputStream(pathFile));
            // Здесь необходимо добавить xlsx расширение и потом уже создавать объект типа Competition
            // Выхов парсера
            Log.i("XLS","Работаем");
        }
        catch (Exception e)
        {
            Log.i("XLS","Не работает");
        }
    }

    private static void OpenXlsxFile(String fullPath)
    {
        try
        {
            String pathFile = Environment.getExternalStorageDirectory().getPath() + "/" + fullPath;
            XSSFWorkbook MyExcelFile = new XSSFWorkbook(new FileInputStream(pathFile));
            Log.i("XLSX","Работаем");
            // MyExcelFile.getSheetAt(0).getRow(0).getCell(0).
        }
        catch (Exception e1)
        {
            Log.i("XLSX","Не открылся!");
        }
    }

    private static void ParserFile(HSSFWorkbook myEcelBook)
    {

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
