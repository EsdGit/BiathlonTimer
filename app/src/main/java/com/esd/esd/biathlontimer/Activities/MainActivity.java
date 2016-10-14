package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.ExcelHelper;
import com.esd.esd.biathlontimer.R;

public class MainActivity extends AppCompatActivity {

    private DatabaseProvider myDatabase;
    private TextView _nameTextView;
    private TextView _dateTextView;
    private int _counter;
    private TableLayout _tableLayout;
    private String _chosenXLSFile;
    private String _pathXLSFile;

    private ExcelHelper _excelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _counter=0;
        _tableLayout = (TableLayout)findViewById(R.id.tablelayout);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);
    }

    public void addFileBtn_OnClick(View view)
    {
        Intent xlsIntent = new Intent(Intent.ACTION_GET_CONTENT);
        xlsIntent.setType("files/*");
        String[] mimeType = {"application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"};
        xlsIntent.putExtra(Intent.EXTRA_MIME_TYPES,mimeType);
        startActivityForResult(xlsIntent,1);
    }

    private void AddCompetitionRow(String nameCompetition)
    {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(nameCompetition);
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackgroundColor(Color.WHITE);
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameTextView.getMeasuredWidth(),_nameTextView.getMeasuredHeight(), 0.666f));
        ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(Integer.toString(_counter++));
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackgroundColor(Color.WHITE);
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_dateTextView.getMeasuredWidth(),_dateTextView.getMeasuredHeight(), 0.334f));
        ((TableRow.LayoutParams)newTextView2.getLayoutParams()).setMargins(0,0,2,2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        _tableLayout.addView(newRow);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case 1:
                if(resultCode == RESULT_OK)
                {
                    String filePath = data.getData().getPath();
                    String fileName = data.getData().getLastPathSegment();
                    String[] pathFile = filePath.split(":");
                    if(ExcelHelper.IsRightFile(pathFile[pathFile.length - 1]))
                    {
                        AddCompetitionRow(fileName);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Неверный формат файла, выберите .xls или .xlsx",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
