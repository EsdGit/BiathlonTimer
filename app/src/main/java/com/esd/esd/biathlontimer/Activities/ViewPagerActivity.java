package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {
    private ParticipantSaver _dbSaver;
    private boolean _isFirstLoad = true;

    private AlertDialog.Builder _addDialogBuilder;
    private AlertDialog _addDialog;

    // Элементы ParticipantList
    private TableLayout _tableLayoutParticipantList;
    private TextView _nameParticipantList;
    private TextView _birthdayParticipantList;
    private TextView _countryParticipantList;

    // Элементы AlertDialog
    private EditText _nameDialog;
    private EditText _birthdayDialog;
    private EditText _countryDialog;

    // Элементы DataBaseList
    private TableLayout _tableLayoutDataBaseList;
    private TextView _nameDataBaseList;
    private TextView _birthdayDataBaseList;
    private TextView _countryDataBaseList;

    //Диалог добавления
    private View _dialogForm;
    private static String TitleDialog = "Добавление участника";
    private static String AddDialogBtn = "Добавить";
    private static String CancelDialogBtn = "Отменить";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _dbSaver = new ParticipantSaver(this);

        FindAllViews();

        // Создание диалогового окна
        _addDialogBuilder = new AlertDialog.Builder(this);
        _addDialogBuilder.setTitle(TitleDialog);  // заголовок
        _addDialogBuilder.setView(_dialogForm);

        // Действия по кнопке "Добавить"
        _addDialogBuilder.setPositiveButton(AddDialogBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

                Participant participant = new Participant(_nameDialog.getText().toString(),
                        _countryDialog.getText().toString(), _birthdayDialog.getText().toString());
                AddRowParticipantList(participant);
                _dbSaver.SaveParticipantToDatabase(participant);
                _nameDialog.setText("");
                _birthdayDialog.setText("");
                _countryDialog.setText("");
                Toast.makeText(getApplicationContext(), "Участник добавлен",
                        Toast.LENGTH_LONG).show();
            }

        });

        _addDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        MakeNewCompetition();

        //Запрет на выход с диалогового окна кнопкой "Back"
        _addDialogBuilder.setCancelable(false);
        _addDialog = _addDialogBuilder.create();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (_isFirstLoad) {
            Participant[] localArr = _dbSaver.GetAllParticipants();
            for (int i = 0; i < localArr.length; i++) {
                AddRowParticipantFromBase(localArr[i]);
            }
            _isFirstLoad = false;
        }
    }

    public void OnClick(View view) {
        _addDialog.show();
    }

    // Добавление участника в ParticipantList
    private void AddRowParticipantList(Participant participant) {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(participant.GetFIO());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackgroundColor(Color.WHITE);
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameParticipantList.getMeasuredWidth(), _nameParticipantList.getMeasuredHeight(), 3f));
        ((TableRow.LayoutParams) newTextView.getLayoutParams()).setMargins(2, 0, 2, 2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(participant.GetBirthYear());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackgroundColor(Color.WHITE);
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayParticipantList.getMeasuredWidth(), _birthdayParticipantList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
        TextView newTextView3 = new TextView(this);
        newTextView3.setText(participant.GetCountry());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setBackgroundColor(Color.WHITE);
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryParticipantList.getMeasuredWidth(), _countryParticipantList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        _tableLayoutParticipantList.addView(newRow);
    }

    private void AddRowParticipantFromBase(Participant participant) {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(participant.GetFIO());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackgroundColor(Color.WHITE);
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameDataBaseList.getMeasuredWidth(), _nameDataBaseList.getMeasuredHeight(), 3f));
        ((TableRow.LayoutParams) newTextView.getLayoutParams()).setMargins(2, 0, 2, 2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(participant.GetBirthYear());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackgroundColor(Color.WHITE);
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayDataBaseList.getMeasuredWidth(), _birthdayDataBaseList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
        TextView newTextView3 = new TextView(this);
        newTextView3.setText(participant.GetCountry());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setBackgroundColor(Color.WHITE);
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryDataBaseList.getMeasuredWidth(), _countryDataBaseList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        _tableLayoutDataBaseList.addView(newRow);
    }

    private void FindAllViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        //Работа с ParticipantList
        View page1 = inflater.inflate(R.layout.activity_participant_list, null);
        pages.add(page1);

        _dialogForm = inflater.inflate(R.layout.dialog_activity_add_participant, null);
        _tableLayoutParticipantList = (TableLayout) page1.findViewById(R.id.tableParticipantListLayout);
        _nameParticipantList = (TextView) page1.findViewById(R.id.nameParticipantList);
        _birthdayParticipantList = (TextView) page1.findViewById(R.id.birthdayParticipantList);
        _countryParticipantList = (TextView) page1.findViewById(R.id.countryParticipantList);

        _nameDialog = (EditText) _dialogForm.findViewById(R.id.dialogName);
        _birthdayDialog = (EditText) _dialogForm.findViewById(R.id.dialogBirthday);
        _countryDialog = (EditText) _dialogForm.findViewById(R.id.dialogCountry);

        //Работа с DataBaseList
        View page2 = inflater.inflate(R.layout.activity_database_list, null);
        pages.add(page2);

        _tableLayoutDataBaseList = (TableLayout) page2.findViewById(R.id.tableDataBaseLayout);
        _nameDataBaseList = (TextView) page2.findViewById(R.id.nameDataBase);
        _birthdayDataBaseList = (TextView) page2.findViewById(R.id.birthdayDataBase);
        _countryDataBaseList = (TextView) page2.findViewById(R.id.countryDataBase);

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);
    }

    private void MakeNewCompetition()
    {
        Competition competition = new Competition("NewCompetition", "29.02.2017", true, "NewCompetition");
        // Создаём соревнование, генерируем новую таблицу в БД, туда закидываем участников и так далее...
    }
}
