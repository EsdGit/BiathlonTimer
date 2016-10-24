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

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;
import com.esd.esd.biathlontimer.ExcelHelper;
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView _nameTextView;
    private TextView _dateTextView;
    private TableLayout _tableLayout;

    private boolean _isFirstLoad = true;

    private CompetitionSaver _saver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _tableLayout = (TableLayout)findViewById(R.id.table);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);

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

    public void addFileBtn_OnClick(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
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
        Intent viewPager = new Intent(this, ViewPagerActivity.class);
        startActivity(viewPager);
    }
}
