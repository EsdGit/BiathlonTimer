package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.esd.esd.biathlontimer.R;

public class ParticipantListActivity extends AppCompatActivity
{
    private AlertDialog.Builder _addDialogBuilder;
    private AlertDialog _addDialog;
    private Context _context;
    private TableLayout _tableLayout;
    private TextView _nameTextView;
    private TextView _birthdayTextView;
    private TextView _countryTextView;
    private EditText _nameEditText;
    private EditText _birthdayEditText;
    private  EditText _countryEditText;
    private View _form;

    private static String Title = "Добавление участника";
    private static String AddDialogBtn = "Добавить";
    private static String CancelDialogBtn = "Отмена";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_list);
        Log.i("onCreate","PL");

        _form = this.getLayoutInflater().inflate(R.layout.dialog_activity_add_participant, null);
        _context = ParticipantListActivity.this;
        _tableLayout = (TableLayout) findViewById(R.id.tableParticipantListLayout);
        _nameTextView = (TextView) findViewById(R.id.nameParticipantList);
        _birthdayTextView = (TextView) findViewById(R.id.birthdayParticipantList);
        _countryTextView = (TextView) findViewById(R.id.countryParticipantList);

        _nameEditText = (EditText) _form.findViewById(R.id.dialogName);
        _birthdayEditText = (EditText) _form.findViewById(R.id.dialogBirthday);
        _countryEditText = (EditText) _form.findViewById(R.id.dialogCountry);

        // Создание диалогового окна
        _addDialogBuilder = new AlertDialog.Builder(_context);
        _addDialogBuilder.setTitle(Title);  // заголовок
        _addDialogBuilder.setView(_form);

        // Действия по кнопке "Добавить"
        _addDialogBuilder.setPositiveButton(AddDialogBtn, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int arg1)
            {
                AddRowParticipant();

            }

        });

        // Действия по кнопке "Отмента"
        _addDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Toast.makeText(getApplicationContext(), "Думать надо перед тем как нажимать", Toast.LENGTH_LONG)
                        .show();
            }
        });

        //Запрет на выход с диалогового окна кнопкой "Back"
        _addDialogBuilder.setCancelable(false);
        _addDialog = _addDialogBuilder.create();

    }

    public void OnClick(View view)
    {
        _addDialog.show();
    }


    private void AddRowParticipant()
    {
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

        _nameEditText.setText("");
        _birthdayEditText.setText("");
        _countryEditText.setText("");

        Toast.makeText(getApplicationContext(), "Добавил участника",
                Toast.LENGTH_LONG).show();
    }

}
