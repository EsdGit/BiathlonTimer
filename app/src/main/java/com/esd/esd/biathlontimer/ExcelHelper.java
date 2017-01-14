package com.esd.esd.biathlontimer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

// Класс для работы с excel файлом
public class ExcelHelper
{
    public ExcelHelper()
    {

    }


    // Метод создаёт excel файл и сохраняет его в папку приложения
    public void CreateFileWithResult(List<MegaSportsman>[] arraySportsmen, String path)
    {
        HSSFWorkbook resultWorkbook = new HSSFWorkbook();
        HSSFSheet sheet;
        HSSFRow row;
        int countLap = arraySportsmen.length;
        for (int i = 0 ; i < countLap; i++)
        {
            sheet = resultWorkbook.createSheet("Круг " + Integer.toString(i + 1));
            row = sheet.createRow(0);
            CreateTableHead(row);
            for(int j = 0; j < arraySportsmen[i].size(); j++)
            {
                row = sheet.createRow(j+1);
                MegaSportsman localSportsman = arraySportsmen[i].get(j);
                row.createCell(0).setCellValue(localSportsman.getPlace());
                row.createCell(1).setCellValue(localSportsman.getNumber());
                row.createCell(2).setCellValue(localSportsman.getName());
                row.createCell(3).setCellValue(localSportsman.getYear());
                row.createCell(4).setCellValue(localSportsman.getCountry());
                row.createCell(5).setCellValue(localSportsman.getResultRun());
                row.createCell(6).setCellValue(localSportsman.getFineCount());
                row.createCell(7).setCellValue(localSportsman.getResult());
                row.createCell(8).setCellValue(localSportsman.getLag());
            }
        }
        try {
            try
            {
                resultWorkbook.write(new FileOutputStream(path));
                resultWorkbook.close();
            }catch (FileNotFoundException e)
            {
                Log.i("What", "Error");
            }
            resultWorkbook.close();
        }
        catch (IOException e)
        {
            Log.i("What", "wfe");
        }
    }

    private void CreateTableHead(HSSFRow row)
    {
        row.createCell(0).setCellValue("Позиция");
        row.createCell(1).setCellValue("Номер");
        row.createCell(2).setCellValue("ФИО");
        row.createCell(3).setCellValue("Год рождения");
        row.createCell(4).setCellValue("Страна");
        row.createCell(5).setCellValue("Время круга без штрафа");
        row.createCell(6).setCellValue("Количество штрафов");
        row.createCell(7).setCellValue("Время");
        row.createCell(8).setCellValue("Отставание");
    }
}
