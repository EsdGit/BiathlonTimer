package com.esd.esd.biathlontimer;

import android.os.Environment;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

// Класс для работы с excel файлом
public class ExcelHelper
{
    private InputStream _inputStreamFile;

    public ExcelHelper(File nameXLSFile)
    {
        try
        {
            HSSFWorkbook MyExcelBook = new HSSFWorkbook(new FileInputStream(Environment.getExternalStorageDirectory().getPath() + "/Test.xls"));
            //Environment.getExternalStorageDirectory().getPath() + "/rating.xls"
            Log.i("XLS","Работаем");
        }catch (Exception e)
        {
            Log.i("XLS","Не открылся!");
        }

    }
}
