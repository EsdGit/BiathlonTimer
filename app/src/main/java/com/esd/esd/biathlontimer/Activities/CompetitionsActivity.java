package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.MyButton;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;


public class CompetitionsActivity extends AppCompatActivity
{
    private GridLayout _participantGridLayout;
    private TableLayout _tableCompetition;
    private TextView _competitionTimer;
    private TextView _numberParticipant;
    private TextView _positionParticipant;
    private TextView _timeParticipant;
    private TextView _lagParticipant;
    private Button _startBtn;
    private MyButton _button;

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
        _startBtn = (Button) page1.findViewById(R.id.competitionStart);

        _currentTime.set(_currentInterval.second,_currentInterval.minute,0,0,0,0);

        View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
        pages.add(page2);

        _tableCompetition = (TableLayout) page2.findViewById(R.id.tableCompetitionLayout);
        _numberParticipant = (TextView) page2.findViewById(R.id.numberParticipantCompetitionTable);
        _positionParticipant = (TextView) page2.findViewById(R.id.positionParticipantCompetitionTable);
        _timeParticipant = (TextView) page2.findViewById(R.id.timeParticipantCompetitionTable);
        _lagParticipant = (TextView) page2.findViewById(R.id.lagParticipantCompetitionTable);

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);

        _button = new MyButton(this);
        View view = CreateFrameLayout();
        _button.SetParticipantNumber(view, "3");
        _participantGridLayout.addView(view);

        AddRowCompetitionTable();
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

    private void AddRowCompetitionTable()
    {
        TableRow newRow = new TableRow(this);
        final TextView newTextView0 = new TextView(this);
        newTextView0.setSingleLine(false);
        newTextView0.setText("Номер");
        newTextView0.setGravity(Gravity.CENTER);
        newTextView0.setTextColor(Color.BLACK);
        newTextView0.setBackground(new PaintDrawable(Color.WHITE));
        newTextView0.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView0.setLayoutParams(new TableRow.LayoutParams(_numberParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,20f));
        ((TableRow.LayoutParams) newTextView0.getLayoutParams()).setMargins(2, 0, 2, 2);

        final TextView newTextView1 = new TextView(this);
        newTextView1.setSingleLine(false);
        newTextView1.setText("Позиция");
        newTextView1.setGravity(Gravity.CENTER);
        newTextView1.setTextColor(Color.BLACK);
        newTextView1.setBackground(new PaintDrawable(Color.WHITE));
        newTextView1.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView1.setLayoutParams(new TableRow.LayoutParams(_positionParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,20f));
        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView2 = new TextView(this);
        newTextView2.setSingleLine(false);
        newTextView2.setText("Время");
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setTextColor(Color.BLACK);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_timeParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,30f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView3 = new TextView(this);
        newTextView3.setSingleLine(false);
        newTextView3.setText("Отставание");
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setTextColor(Color.BLACK);
        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_lagParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,30f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);

        newRow.addView(newTextView0);
        newRow.addView(newTextView1);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);

        _tableCompetition.addView(newRow);
    }

    private View CreateFrameLayout()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.my_btn, null);
        return view;
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
            _startBtn.setText(getResources().getString(R.string.stop_timer));
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
            _startBtn.setText(getResources().getString(R.string.start_timer));
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_timer != null) _timer.cancel();
    }
}
