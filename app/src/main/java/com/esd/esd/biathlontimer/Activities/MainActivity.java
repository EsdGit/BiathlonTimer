package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.R;

public class MainActivity extends AppCompatActivity {

    private TextView _nameTextView;
    private TextView _dateTextView;
    private TableLayout _tableLayout;
    private ImageButton _menuMainImBtn;
    private PopupMenu _popupMenu;

    private boolean _isFirstLoad = true;

    private CompetitionSaver _saver;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _tableLayout = (TableLayout)findViewById(R.id.table);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);
        _menuMainImBtn = (ImageButton) findViewById(R.id.menu);

        _saver = new CompetitionSaver(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(_isFirstLoad) {
            Competition[] localArr = _saver.GetAllCompetitions();
            for(int i = 0; i < localArr.length; i++)
            {
                AddCompetitionRow(localArr[i]);
            }
            _isFirstLoad = false;
        }
    }

    private void AddCompetitionRow(Competition competition)
    {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(competition.GetName());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackgroundColor(Color.WHITE);
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameTextView.getMeasuredWidth(),_nameTextView.getMeasuredHeight(), 0.666f));
        ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(competition.GetDate());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackgroundColor(Color.WHITE);
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_dateTextView.getMeasuredWidth(),_dateTextView.getMeasuredHeight(), 0.334f));
        ((TableRow.LayoutParams)newTextView2.getLayoutParams()).setMargins(0,0,2,2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        _tableLayout.addView(newRow);
    }

    public void OnClick(View view)
    {
        //Competition competition = new Competition("Чм России среди юношей", "25.10.2016", "");
        DatabaseProvider db = new DatabaseProvider(this);
        db.AddNewTable("TEST");
        ParticipantSaver pS = new ParticipantSaver(this);
        pS.SaveParticipantToDatabase(new Participant("Жуков Юрий", "1975", "Россия"), "TEST");
        pS.SaveParticipantToDatabase(new Participant("Слободзян Никитос", "1990", "Казахстан"), "TEST");
        Intent viewPager = new Intent(this, ViewPagerActivity.class);
        startActivity(viewPager);
    }

    public void OnClickMainMenu(View view)
    {
        if(_popupMenu == null)
        {
        _popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = _popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.main_sort_menu, _popupMenu.getMenu());
        _popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.mainManuNameSort:
                        Toast.makeText(getApplicationContext(),"Сортировка файлов по имени",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.mainMenuDataSort:
                        Toast.makeText(getApplicationContext(),"Сортировка файлов по дате",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        }
        _popupMenu.show();
    }
}
