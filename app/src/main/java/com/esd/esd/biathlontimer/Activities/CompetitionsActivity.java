package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.LapData;
import com.esd.esd.biathlontimer.MyButton;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;

import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;


public class CompetitionsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener
{
    private GridLayout _participantGridLayout;
    private LinearLayout _containerTables;
    private TextView _currentRound;
    private TextView _competitionTimer;
    private TextView _numberParticipant;
    private TextView _positionParticipant;
    private TextView _timeParticipant;
    private TextView _lagParticipant;
    private TextView _timerParticipantTable;
    private Button _startBtn;
    private MyButton _button;

    private AlertDialog _fineDialog;
    private AlertDialog.Builder _builderFineDialog;
    private View _dialogFineForm;
    private SeekBar _dialogSeekBar;
    private TextView _dialogText;

    private Competition _currentCompetition;
    private boolean _isCompetitionStarted = false;
    private Timer _timer;
    private android.text.format.Time _timeNextParticipant;
    private android.text.format.Time _currentInterval;
    private android.text.format.Time _currentTime;
    private Participant[] _participants;
    private ArrayList<TableLayout> _tablesCompetition;
    private int _currentTable = 0;
    private int _number = 0;

    private Button _dialogOwnerButton;
    private LapData[] _laps;

    private CountDownTimer _countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        _timeNextParticipant = new android.text.format.Time();
        _currentInterval = new android.text.format.Time();
        _tablesCompetition = new ArrayList<>();
        _button = new MyButton(this);

        _currentCompetition = new Competition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"), this);
        _currentCompetition.GetAllSettingsToComp();
        _currentCompetition.GetAllParticipantsToComp();

        _participants = _currentCompetition.GetAllParticipants();


        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_competitions, null);
        pages.add(page1);
        _participantGridLayout = (GridLayout) page1.findViewById(R.id.competitionGridLayout);
        _competitionTimer = (TextView) page1.findViewById(R.id.competitionTimer);
        _startBtn = (Button) page1.findViewById(R.id.competitionStart);


        View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
        pages.add(page2);
        _containerTables = (LinearLayout) page2.findViewById(R.id.containerTablesCompetition);
        _currentRound = (TextView) page2.findViewById(R.id.currentRound);
        _numberParticipant = (TextView) page2.findViewById(R.id.numberParticipantCompetitionTable);
        _positionParticipant = (TextView) page2.findViewById(R.id.positionParticipantCompetitionTable);
        _timeParticipant = (TextView) page2.findViewById(R.id.timeParticipantCompetitionTable);
        _lagParticipant = (TextView) page2.findViewById(R.id.lagParticipantCompetitionTable);
        _timerParticipantTable = (TextView) page2.findViewById(R.id.competitionTimer);

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);

        _dialogFineForm = inflater.inflate(R.layout.dialog_fine_competition_activity, null);
        _dialogSeekBar = (SeekBar) _dialogFineForm.findViewById(R.id.seek_bar_competition_activity);
        _dialogText = (TextView) _dialogFineForm.findViewById(R.id.count_fine);
        _dialogSeekBar.setOnSeekBarChangeListener(this);
        _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");

        _builderFineDialog = new AlertDialog.Builder(CompetitionsActivity.this);
        _builderFineDialog.setView(_dialogFineForm);
        _builderFineDialog.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                int fineCount = _dialogSeekBar.getProgress();
                int fineSeconds = Integer.valueOf(_currentCompetition.GetFineTime().split(":")[1]);
                int fineMinutes = Integer.valueOf(_currentCompetition.GetFineTime().split(":")[0]);
                android.text.format.Time fineTime = new android.text.format.Time();
                int participantNumber = Integer.valueOf(_dialogOwnerButton.getText().toString().split(", ")[0]);
                int lapNumber = Integer.valueOf(_dialogOwnerButton.getText().toString().split(", ")[1]) - 2;

                fineTime.second = fineCount*fineSeconds;
                fineTime.minute = fineCount*fineMinutes;
                fineTime.normalize(false);

                for(int i = 0; i < _participants.length; i++)
                {
                    if(participantNumber == Integer.valueOf(_participants[i].GetNumber()))
                    {
                        participantNumber = i;
                        break;
                    }
                }

                _participants[participantNumber].SetFineTime(fineTime);
                _dialogSeekBar.setProgress(0);
                _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");
                if(lapNumber < 0) return;

                _participants[participantNumber].SetPlace(GetPlace(_participants[participantNumber], lapNumber), lapNumber);

                _tablesCompetition.get(lapNumber).removeAllViews();
                for(int j = 0; j < _laps[lapNumber].GetParticipants().length; j++)
                {
                    AddRowCompetitionTable(_laps[lapNumber].GetParticipant(j),lapNumber);
                }



            }
        });
        _builderFineDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                _dialogSeekBar.setProgress(0);
                _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");
            }
        });

        _fineDialog = _builderFineDialog.create();

//        View view = CreateFrameLayout();
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v)
//            {
//                _fineDialog.show();
//                return false;
//            }
//        });
        //_button.SetParticipantNumber(view, "3");
        //_participantGridLayout.addView(view);

        int tablesCount = Integer.valueOf(_currentCompetition.GetCheckPointsCount());
        CreateTables(tablesCount);
        _laps = new LapData[tablesCount];
        for(int i = 0; i < tablesCount; i++)
        {
            _laps[i] = new LapData(i);
        }

        _currentRound.setText(_currentRound.getText() + " - " + Integer.toString(_currentTable + 1));

        TimerStartPosition();

    }


    private void TimerStartPosition()
    {
        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_mas_start)))
        {
            _currentInterval.second = 0;
            _currentInterval.minute = 0;
        }
        else
        {
            _currentInterval.second = Integer.valueOf(_currentCompetition.GetInterval().split(":")[1]);
            _currentInterval.minute = Integer.valueOf(_currentCompetition.GetInterval().split(":")[0]);
        }


        _timeNextParticipant.second = _currentInterval.second;
        _timeNextParticipant.minute = _currentInterval.minute;

        _competitionTimer.setText(_currentCompetition.GetTimeToStart());
        _timerParticipantTable.setText(_currentCompetition.GetTimeToStart());
    }

    private View CreateButton(final Participant participant, String numberCheckPoint)
    {
        //final Button newButton = new Button(this);
        //newButton.setText(participant.GetNumber() + ", " + numberCheckPoint);
        //newButton.setBackgroundColor(participant.GetColor());
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.my_btn, null);
        _button.SetParticipantNumberAndBackground(view, participant.GetNumber(), participant.GetColor());
        _button.SetParticipantLap(view, numberCheckPoint);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _dialogOwnerButton = (Button)view;
                _fineDialog.show();
                return false;
            }
        });
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int number = Integer.valueOf(_button.GetParticipantNumber(v) /*newButton.getText().toString().split(", ")[0]*/);
                int lap = Integer.valueOf(_button.GetLap(v) /*newButton.getText().toString().split(", ")[1]*/) - 1;

                // Если номера одинаковые то капец
                for(int i = 0; i < _participants.length; i++)
                {
                    if(number == Integer.valueOf(_participants[i].GetNumber()))
                    {
                        number = i;
                        break;
                    }
                }

                android.text.format.Time newTime = new android.text.format.Time();

                newTime.hour = _currentTime.hour - _participants[number].GetStartTime().hour;
                newTime.minute = _currentTime.minute - _participants[number].GetStartTime().minute;
                newTime.second = _currentTime.second - _participants[number].GetStartTime().second;
                newTime.normalize(false);

                _participants[number].SetResultTime(newTime, lap);

                _participants[number].SetPlace(GetPlace(participant, lap), lap);


                _tablesCompetition.get(lap).removeAllViews();
                for(int i = 0; i < _laps[lap].GetParticipants().length; i++)
                {
                    AddRowCompetitionTable(_laps[lap].GetParticipant(i),lap);
                }

                if(lap == _laps.length - 1)
                {
                    _participantGridLayout.removeView(view);
                }
                else
                {
                    lap+=2;
                    _button.SetParticipantLap(view, Integer.toString(lap));
                    //newButton.setText(participant.GetNumber()+", "+lap);
                }

                //Toast.makeText(getApplicationContext(),newButton.getText(),Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }


    private int GetPlace(Participant participant, int lap)
    {
        int place = 0;
        boolean _isLastPlace = true;
        _laps[lap].RemoveParticipant(participant);
        for(int i = 0; i < _laps[lap].GetParticipants().length;i++)
        {
            if(android.text.format.Time.compare(_laps[lap].GetParticipant(i).GetResultTime(lap), participant.GetResultTime(lap)) > 0)
            {
                _laps[lap].AddParticipant(i, participant);
                place = i+1;
                _isLastPlace = false;
                break;
            }
        }

        if(_isLastPlace)
        {
            _laps[lap].AddParticipant(participant);
            place = _laps[lap].GetParticipants().length;
        }
        else
        {
            for(int i = place; i<_laps[lap].GetParticipants().length; i++)
            {
                _laps[lap].GetParticipant(i).SetPlace(i+1, lap);
            }
        }

        return place;
    }

    private void AddRowCompetitionTable(Participant participant, int lap)
    {
        int rowColor = participant.GetColor();
        TableRow newRow = new TableRow(this);
        final TextView newTextView0 = new TextView(this);
        newTextView0.setSingleLine(false);
        newTextView0.setText(participant.GetNumber());
        newTextView0.setGravity(Gravity.CENTER);
        newTextView0.setTextColor(Color.BLACK);
        newTextView0.setBackground(new PaintDrawable(rowColor));
        newTextView0.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView0.setLayoutParams(new TableRow.LayoutParams(_numberParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,20f));
        ((TableRow.LayoutParams) newTextView0.getLayoutParams()).setMargins(2, 0, 2, 2);

        final TextView newTextView1 = new TextView(this);
        newTextView1.setSingleLine(false);
        newTextView1.setText(String.valueOf(participant.GetPlace(lap)));
        newTextView1.setGravity(Gravity.CENTER);
        newTextView1.setTextColor(Color.BLACK);
        newTextView1.setBackground(new PaintDrawable(rowColor));
        newTextView1.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView1.setLayoutParams(new TableRow.LayoutParams(_positionParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,20f));
        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView2 = new TextView(this);
        newTextView2.setSingleLine(false);
        newTextView2.setText(participant.GetResultTime(lap).format("%H:%M:%S"));
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setTextColor(Color.BLACK);
        newTextView2.setBackground(new PaintDrawable(rowColor));
        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_timeParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,30f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView3 = new TextView(this);
        newTextView3.setSingleLine(false);
        newTextView3.setText(GetLag(participant, lap).format("%H:%M:%S"));
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setTextColor(Color.BLACK);
        newTextView3.setBackground(new PaintDrawable(rowColor));
        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_lagParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,30f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);

        newRow.addView(newTextView0);
        newRow.addView(newTextView1);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);

        _tablesCompetition.get(lap).addView(newRow);
    }


    private void CreateTables(int countCheckPoint)
    {
        for(int i = 0; i < countCheckPoint; i++)
        {
            TableLayout newTable = new TableLayout(this);
            _tablesCompetition.add(newTable);
            newTable.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            newTable.setBackgroundColor(Color.BLACK);
            if(i!=0)
            {
                newTable.setVisibility(View.GONE);
            }
            _containerTables.addView(newTable);
        }

    }

    private android.text.format.Time GetLag(Participant participant, int lap)
    {
        // По lap выбираем arrayList
        android.text.format.Time lag = new android.text.format.Time();
        lag.second = participant.GetResultTime(lap).second - _laps[lap].GetParticipant(0).GetResultTime(lap).second;
        lag.minute = participant.GetResultTime(lap).minute - _laps[lap].GetParticipant(0).GetResultTime(lap).minute;
        lag.hour = participant.GetResultTime(lap).hour - _laps[lap].GetParticipant(0).GetResultTime(lap).hour;
        lag.normalize(false);
        return lag;
    }

//    private View CreateFrameLayout()
//    {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.my_btn, null);
//        return view;
//    }


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
            final long ms1 = timeCountDown.minute*60000+timeCountDown.second*1000;

            _currentTime = new android.text.format.Time(_timeNextParticipant);
            _currentTime.second = 0;
            _currentTime.minute = 0;
            _countDownTimer = new CountDownTimer(ms1+1000,1000)
            {
                TimerTask task = new TimerTask()
                {
                    int ms = 0;
                    String msStr;

                    @Override
                    public void run()
                    {
                        ms++;
                        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_mas_start)) && _number < _participants.length)
                        {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                    android.text.format.Time localTime = new android.text.format.Time();
                                    localTime.second = _currentTime.second;
                                    localTime.minute = _currentTime.minute;
                                    localTime.hour = _currentTime.hour;
                                    for(int i = 0; i < _participants.length; i++)
                                    {
                                        _participantGridLayout.addView(CreateButton(_participants[i], "1"));
                                        _participants[i].SetStartTime(localTime);
                                    }
                                    _number = _participants.length;
                                }
                            });
                        }
                        if(ms>9)
                        {
                            _currentTime.second++;
                            ms = 0;
                            _currentTime.normalize(false);

                            if(android.text.format.Time.compare(_currentTime,_timeNextParticipant) == 0 && _number < _participants.length)
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
                                        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_single_start)))
                                        {
                                            _participantGridLayout.addView(CreateButton(_participants[_number], "1"));
                                            _participants[_number].SetStartTime(_currentTime);
                                            _number++;
                                        }
                                        else
                                        {
                                            if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_double_start)))
                                            {
                                                _participantGridLayout.addView(CreateButton(_participants[_number], "1"));
                                                _participants[_number].SetStartTime(_currentTime);
                                                _number++;
                                                if(_number < _participants.length)
                                                {
                                                    _participantGridLayout.addView(CreateButton(_participants[_number], "1"));
                                                    _participants[_number].SetStartTime(_currentTime);
                                                    _number++;
                                                }
                                            }
                                        }
                                    }
                                });
                                _timeNextParticipant.hour += _currentInterval.hour;
                                _timeNextParticipant.minute += _currentInterval.minute;
                                _timeNextParticipant.second += _currentInterval.second;
                                _timeNextParticipant.normalize(false);


                            }

                        }




                        msStr = String.valueOf(ms);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                _competitionTimer.setText(_currentTime.format("%H:%M:%S")+":"+msStr);
                                _timerParticipantTable.setText(_currentTime.format("%H:%M:%S")+":"+msStr);
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
                    _timerParticipantTable.setText(timeCountDown.format("%M:%S"));
                }

                @Override
                public void onFinish() {
                    _timer = new Timer();
                    _timer.schedule(task, 0,100);

                }
            };
            _countDownTimer.start();
            _isCompetitionStarted = true;
        }
        else
        {
            // Здесь сброс
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.reset_title));
            builder.setMessage(getResources().getString(R.string.message_dialog_reset));
            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    if(_countDownTimer != null) _countDownTimer.cancel();
                    if(_timer != null) _timer.cancel();
                    TimerStartPosition();
                    for(int j = 0; j < _tablesCompetition.size(); j++)
                    {
                        _tablesCompetition.get(j).removeAllViews();
                    }
                    _participantGridLayout.removeAllViews();
                    _isCompetitionStarted = false;
                    _startBtn.setText(getResources().getString(R.string.start_timer));
                    _number = 0;
                }
            });

            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            builder.show();

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_timer != null) _timer.cancel();
    }

    public void OnClickNextTable(View view)
    {
        if(_currentTable < _tablesCompetition.size() - 1)
        {
            _tablesCompetition.get(_currentTable).setVisibility(View.GONE);
            _tablesCompetition.get(++_currentTable).setVisibility(View.VISIBLE);
            _currentRound.setText(getResources().getString(R.string.current_round) + " - " + Integer.toString(_currentTable + 1));
        }
    }

    public void OnClickPreviousTable(View view)
    {
        if(_currentTable > 0)
        {
            _tablesCompetition.get(_currentTable).setVisibility(View.GONE);
            _tablesCompetition.get(--_currentTable).setVisibility(View.VISIBLE);
            _currentRound.setText(getResources().getString(R.string.current_round) + " - " + Integer.toString(_currentTable + 1));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " " + Integer.toString(seekBar.getProgress()));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " " + Integer.toString(seekBar.getProgress()));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == (KeyEvent.KEYCODE_BACK))
        {
            if(_isCompetitionStarted)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getResources().getString(R.string.warning_dialog_title));
                dialog.setMessage(getResources().getString(R.string.message_end_competition));
                dialog.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // заканчиваем нахер
                        CompetitionsActivity.this.finish();
                    }
                });

                dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
