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
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;

import org.w3c.dom.Text;

import java.sql.Time;
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
    private android.text.format.Time _currentTime;
    private android.text.format.Time _currentInterval;
    private Participant[] _participants;
    private int _number = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        _currentTime = new android.text.format.Time();
        _currentInterval = new android.text.format.Time();

        _currentCompetition = new Competition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"), this);
        _currentCompetition.GetAllSettingsToComp();
        _currentCompetition.GetAllParticipantsToComp();

        _participants = _currentCompetition.GetAllParticipants();

        _currentInterval.second = Integer.valueOf(_currentCompetition.GetInterval().split(":")[1]);
        _currentInterval.minute = Integer.valueOf(_currentCompetition.GetInterval().split(":")[0]);
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_competitions, null);
        pages.add(page1);
        _participantGridLayout = (GridLayout) page1.findViewById(R.id.competitionGridLayout);

        _competitionTimer = (TextView) page1.findViewById(R.id.competitionTimer);
        _competitionTimer.setText(_currentCompetition.GetTimeToStart());
        _startBtn = (ImageButton) page1.findViewById(R.id.competitionStart);

        _currentTime.set(_currentInterval.second,_currentInterval.minute,0,0,0,0);
        View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
        pages.add(page2);

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);

    }


    private Button CreateButton(Participant participant, String numberCheckPoint)
    {
        final Button newButton = new Button(this);
        newButton.setText(participant.GetNumber() + ", " + numberCheckPoint);
        newButton.setBackgroundColor(participant.GetColor());
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

    public void startBtnClick(View view)
    {
        if(!_isCompetitionStarted)
        {
            final android.text.format.Time timeCountDown = new android.text.format.Time();
            timeCountDown.minute = Integer.valueOf(_currentCompetition.GetTimeToStart().split(":")[0]);
            timeCountDown.second = Integer.valueOf(_currentCompetition.GetTimeToStart().split(":")[1]);
            final long ms = timeCountDown.minute*60000+timeCountDown.second*1000;

            final android.text.format.Time time = new android.text.format.Time();
            time.set(0,0,0,0,0,0);
            CountDownTimer countDownTimer = new CountDownTimer(ms+1000,1000)
            {
                TimerTask task = new TimerTask() {
                    int ms = 0;
                    String msStr;

                    @Override
                    public void run()
                    {
                        ms++;
                        if(ms>9)
                        {
                            time.second++;
                            ms = 0;


                            if(android.text.format.Time.compare(time,_currentTime) == 0 && _number < _participants.length)
                            {
                                if(_number == Integer.valueOf(_currentCompetition.GetNumberSecondInterval()) - 1)
                                {
                                    _currentInterval.second = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[1]);
                                    _currentInterval.minute = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[0]);
                                }
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        _participantGridLayout.addView(CreateButton(_participants[_number],"1"));
                                        _number++;

                                    }
                                });
                                _currentTime.second += _currentInterval.second;
                                _currentTime.minute += _currentInterval.minute;
                            }



                        }
                        if(time.second > 59)
                        {
                            time.minute++;
                            time.second = 0;
                        }
                        if(time.minute > 59)
                        {
                            time.hour++;
                            time.minute = 0;
                        }
                        //_currentTime.second++;


                        msStr = String.valueOf(ms);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                _competitionTimer.setText(time.format("%H:%M:%S")+":"+msStr);
                            }
                        });
                    }
                };
                @Override
                public void onTick(long millisUntilFinished)
                {
                    timeCountDown.second--;
                    if(timeCountDown.second < 0)
                    {
                        timeCountDown.minute--;
                        timeCountDown.second = 59;
                    }
                    _competitionTimer.setText(timeCountDown.format("%M:%S"));
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
        if(_timer != null) _timer.cancel();
    }
}
