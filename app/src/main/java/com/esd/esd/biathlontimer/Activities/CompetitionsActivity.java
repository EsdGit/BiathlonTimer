package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class CompetitionsActivity extends AppCompatActivity
{
    private GridLayout _participantGridLayout;
    private TextView _competitionTimer;
    private ImageButton _startBtn;

    private Competition _currentCompetition;
    private boolean _isCompetitionStarted = false;
    private Timer _timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        _currentCompetition = new Competition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"), this);
        _currentCompetition.GetAllSettingsToComp();
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_competitions, null);
        pages.add(page1);
        _participantGridLayout = (GridLayout) page1.findViewById(R.id.competitionGridLayout);

        _competitionTimer = (TextView) page1.findViewById(R.id.competitionTimer);
        _competitionTimer.setText(_currentCompetition.GetTimeToStart());
        _startBtn = (ImageButton) page1.findViewById(R.id.competitionStart);

        View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
        pages.add(page2);

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);

        for(int i = 0; i < 36; i++)
        {
            _participantGridLayout.addView(CreateButton(Integer.toString(i),Integer.toString(i+1)));
        }
    }


    private Button CreateButton(String numberParticipant, String numberCheckPoint)
    {
        final Button newButton = new Button(this);
        newButton.setText(numberParticipant + ", " + numberCheckPoint);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),newButton.getText(),Toast.LENGTH_LONG).show();
            }
        });
        return newButton;
    }


    public void imgBtnSettings_OnClick(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    long milliss;
    public void startBtnClick(View view)
    {
        if(!_isCompetitionStarted)
        {
            int sec = Integer.valueOf(_currentCompetition.GetTimeToStart().split(":")[1]);
            int min = Integer.valueOf(_currentCompetition.GetTimeToStart().split(":")[0]);
            long ms = min*60000 + sec*1000;
            milliss = ms;
            CountDownTimer countDownTimer = new CountDownTimer(ms+1000,1000)
            {
                long sec = (milliss % 60000)/1000;
                long min = milliss/60000;
                String secStr;
                String minStr;
                TimerTask task = new TimerTask() {
                    int ms = 0;
                    int sec = 0;
                    int min = 0;
                    int hour = 0;
                    String hourStr;
                    String minStr;
                    String secStr;
                    String msStr;
                    @Override
                    public void run()
                    {
                        ms++;
                        if(ms>9)
                        {
                            sec++;
                            ms = 0;
                        }
                        if(sec>59)
                        {
                            min++;
                            sec = 0;
                        }
                        if(min>59)
                        {
                            hour++;
                            min = 0;
                        }
                        if(sec < 10)
                        {
                            secStr = "0"+String.valueOf(sec);
                        }
                        else
                        {
                            secStr = String.valueOf(sec);
                        }

                        if(min<10)
                        {
                            minStr = "0"+String.valueOf(min);
                        }
                        else
                        {
                            minStr = String.valueOf(min);
                        }

                        msStr = String.valueOf(ms);
                        hourStr = String.valueOf(hour);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                _competitionTimer.setText(hourStr+":"+minStr+":"+secStr+":"+ms);
                            }
                        });
                    }
                };
                @Override
                public void onTick(long millisUntilFinished)
                {
                    sec--;
                    if(sec < 0)
                    {
                        min--;
                        sec = 59;
                    }
                    if(sec < 10)
                    {
                        secStr = "0"+String.valueOf(sec);
                    }
                    else
                    {
                        secStr = String.valueOf(sec);
                    }
                    if(min < 10)
                    {
                        minStr = "0"+String.valueOf(min);
                    }
                    else
                    {
                        minStr = String.valueOf(min);
                    }

                    _competitionTimer.setText(minStr+":"+secStr);
                }

                @Override
                public void onFinish() {
                    _timer = new Timer();
                    _timer.schedule(task, 0,100);

                }
            };
            countDownTimer.start();
            _isCompetitionStarted = true;
        }
        else
        {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        _timer.cancel();
    }
}
