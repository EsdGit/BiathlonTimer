package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.R;


public class DataBaseActivity extends AppCompatActivity
{
    private AlertDialog.Builder _addDialog;
    private TableLayout _tableLayout;
    private Context _context;
    private TextView _nameTextView;
    private TextView _birthdayTextView;
    private TextView _countryTextView;
    private View _form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_list);
        _tableLayout = (TableLayout)findViewById(R.id.tableDataBaseLayout);
        _nameTextView = (TextView) findViewById(R.id.nameDataBase);
        _birthdayTextView = (TextView) findViewById(R.id.birthdayDataBase);
        _countryTextView = (TextView) findViewById(R.id.countryDataBase);

        // Создание диалогового окна
        String title = "Добавление участника";
        String button1String = "Добавить";
        String button2String = "Отмена";

        _form = this.getLayoutInflater().inflate(R.layout.dialog_activity_add_participant, null);
        _context = DataBaseActivity.this;
        _addDialog = new AlertDialog.Builder(_context);
        _addDialog.setTitle(title);  // заголовок
        _addDialog.setView(_form);

        // Действия по кнопке "Добавить"
        _addDialog.setPositiveButton(button1String, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                EditText _nameEditText = (EditText) _form.findViewById(R.id.dialogName);
                EditText _birthdayEditText = (EditText) _form.findViewById(R.id.dialogBirthday);
                EditText _countryEditText = (EditText) _form.findViewById(R.id.dialogCountry);

                String name = _nameEditText.getText().toString();
                String birthday = _birthdayEditText.getText().toString();
                String country = _countryEditText.getText().toString();

                TableRow newRow = new TableRow(_context);
                TextView newTextView = new TextView(_context);
                newTextView.setText(name);
                newTextView.setGravity(Gravity.CENTER);
                newTextView.setBackgroundColor(Color.WHITE);
                newTextView.setLayoutParams(new TableRow.LayoutParams(_nameTextView.getMeasuredWidth(),_nameTextView.getMeasuredHeight(), 3f));
                ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);
                TextView newTextView2 = new TextView(_context);
                newTextView2.setText(birthday);
                newTextView2.setGravity(Gravity.CENTER);
                newTextView2.setBackgroundColor(Color.WHITE);
                newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayTextView.getMeasuredWidth(),_birthdayTextView.getMeasuredHeight(), 0.5f));
                ((TableRow.LayoutParams)newTextView2.getLayoutParams()).setMargins(0,0,2,2);
                TextView newTextView3 = new TextView(_context);
                newTextView3.setText(country);
                newTextView3.setGravity(Gravity.CENTER);
                newTextView3.setBackgroundColor(Color.WHITE);
                newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryTextView.getMeasuredWidth(),_countryTextView.getMeasuredHeight(), 0.5f));
                ((TableRow.LayoutParams)newTextView3.getLayoutParams()).setMargins(0,0,2,2);
                newRow.addView(newTextView);
                newRow.addView(newTextView2);
                newRow.addView(newTextView3);
                _tableLayout.addView(newRow);

                Toast.makeText(getApplicationContext(), "Добавил участника",
                        Toast.LENGTH_LONG).show();
            }
        });

        // Действия по кнопке "Отмента"
        _addDialog.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getApplicationContext(), "Думать надо перед тем как нажимать", Toast.LENGTH_LONG)
                        .show();
            }
        });

        //Запрет на выход с диалогового окна кнопкой "Back"
        _addDialog.setCancelable(false);
    }

    public void OnClick(View view)
    {
        _addDialog.show();
    }


}
