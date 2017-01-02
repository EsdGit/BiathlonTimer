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
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;
import com.esd.esd.biathlontimer.ErrorsBuffer;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.MyButton;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Sportsman;

import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    private TextView _nameParticipant;
    private TextView _timeParticipant;
    private TextView _lagParticipant;
    private TextView _timerParticipantTable;
    private ImageButton _startBtn;
    private MyButton _button;

    private AlertDialog _fineDialog;
    private AlertDialog.Builder _builderFineDialog;
    private View _dialogFineForm;
    private SeekBar _dialogSeekBar;
    private TextView _dialogText;

    private Competition _currentCompetition;
    private CompetitionState _competitionState;
    private Timer _timer;
    private android.text.format.Time _timeNextParticipant;
    private android.text.format.Time _currentInterval;
    private android.text.format.Time _currentTime;
    private MegaSportsman[] _megaSportsmen;
    private ArrayList<TableLayout> _tablesCompetition;
    private int _currentTable = 0;
    private int _number = 0;
    private int lapsCount;

    private FrameLayout _dialogOwnerView;

    private CountDownTimer _countDownTimer;

    private boolean _isPaused = true;

    private enum CompetitionState
    {
        NotStarted,
        Started,
        Running
    }

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

        _competitionState = CompetitionState.NotStarted;

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_competitions, null);
        pages.add(page1);
        _participantGridLayout = (GridLayout) page1.findViewById(R.id.competitionGridLayout);
        _competitionTimer = (TextView) page1.findViewById(R.id.competitionTimer);
        _startBtn = (ImageButton) page1.findViewById(R.id.competitionStart);


        View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
        pages.add(page2);
        _containerTables = (LinearLayout) page2.findViewById(R.id.containerTablesCompetition);
        _currentRound = (TextView) page2.findViewById(R.id.currentRound);
        _numberParticipant = (TextView) page2.findViewById(R.id.numberParticipantCompetitionTable);
        _positionParticipant = (TextView) page2.findViewById(R.id.positionParticipantCompetitionTable);
        _nameParticipant = (TextView) page2.findViewById(R.id.nameParticipantCompetitionTable);
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
                int participantNumber = Integer.valueOf(_button.GetParticipantNumber(_dialogOwnerView));
                int lapNumber = Integer.valueOf(_button.GetLap(_dialogOwnerView));

                fineTime.second = fineCount*fineSeconds;
                fineTime.minute = fineCount*fineMinutes;
                fineTime.normalize(false);

                for(int i = 0; i < _megaSportsmen.length; i++)
                {
                    if(_megaSportsmen[i].getNumber() == participantNumber)
                    {
                        _megaSportsmen[i].setFineCount(fineCount, lapNumber);
                        _megaSportsmen[i].setFineTime(fineTime, lapNumber);
                        break;
                    }
                }
                _dialogSeekBar.setProgress(0);
                _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");


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

        lapsCount = Integer.valueOf(_currentCompetition.GetCheckPointsCount());
        CreateTables(lapsCount);
        RealmSportsmenSaver saver = new RealmSportsmenSaver(this, _currentCompetition.GetDbParticipantPath());
        List<Sportsman> list = saver.GetSportsmen("number", true);
        _megaSportsmen = new MegaSportsman[list.size()];
        for(int i = 0; i<list.size(); i++)
        {
            _megaSportsmen[i] = new MegaSportsman(list.get(i));
            _megaSportsmen[i].setLapsCount(lapsCount);
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

        _competitionTimer.setTextColor(getResources().getColor(R.color.timerStart));
        _timerParticipantTable.setTextColor(getResources().getColor(R.color.timerStart));
        _competitionTimer.setText(_currentCompetition.GetTimeToStart());
        _timerParticipantTable.setText(_currentCompetition.GetTimeToStart());
    }

    private View CreateButton(final MegaSportsman sportsman, String numberCheckPoint)
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View view = inflater.inflate(R.layout.my_btn, null);
        _button.SetParticipantNumberAndBackground(view, String.valueOf(sportsman.getNumber()), sportsman.getColor());
        _button.SetParticipantLap(view, numberCheckPoint);
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                _dialogOwnerView = (FrameLayout) view;
                _fineDialog.show();
                return false;
            }
        });
        view.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int number = Integer.valueOf(_button.GetParticipantNumber(v));
                int lap = Integer.valueOf(_button.GetLap(v));

                // Если номера одинаковые то капец
                for(int i = 0; i < _megaSportsmen.length; i++)
                {
                    if(number == _megaSportsmen[i].getNumber())
                    {
                        number = i;
                        break;
                    }
                }

                android.text.format.Time newTime = new android.text.format.Time();

                newTime.hour = _currentTime.hour - _megaSportsmen[number].getStartTime().hour;
                newTime.minute = _currentTime.minute - _megaSportsmen[number].getStartTime().minute;
                newTime.second = _currentTime.second - _megaSportsmen[number].getStartTime().second;
                newTime.normalize(false);

                _megaSportsmen[number].setResultTime(newTime, lap);

                GetPlace(number, lap);

                _tablesCompetition.get(lap).removeAllViews();
                SortByPlace(lap);
                for(int i = 0; i < _megaSportsmen.length; i++)
                {
                    if(_megaSportsmen[i].getPlace(lap) == 0) continue;
                    AddRowCompetitionTable(_megaSportsmen[i], lap);
                }

                if(lap == lapsCount - 1)
                {
                    _participantGridLayout.removeView(view);
                }
                else
                {
                    lap++;
                    _button.SetParticipantLap(view, Integer.toString(lap));
                }
            }
        });
        return view;
    }

    private void SortByPlace(int lap)
    {
        MegaSportsman helper;
        for(int i = 0; i < _megaSportsmen.length - 1; i++)
        {
            if(_megaSportsmen[i].getPlace(lap) > _megaSportsmen[i+1].getPlace(lap))
            {
                helper = _megaSportsmen[i];
                _megaSportsmen[i] = _megaSportsmen[i+1];
                _megaSportsmen[i+1] = helper;
            }
        }
    }

    private void GetPlace(int numberInArr, int lap)
    {
        int place = 1;
        int participantCount = 0;
        android.text.format.Time thisTime = new android.text.format.Time(_megaSportsmen[numberInArr].getResultTime(lap));
        for(int i = 0; i < _megaSportsmen.length; i++)
        {
            if(i == numberInArr) continue;

            if(_megaSportsmen[i].getResultTime(lap) != null)
            {
                participantCount++;
                if (android.text.format.Time.compare(_megaSportsmen[i].getResultTime(lap), thisTime) < 0)
                {
                    place++;
                }
            }

        }
        _megaSportsmen[numberInArr].setPlace(place, lap);

        for(int i = 0; i < _megaSportsmen.length; i++)
        {
            if(i == numberInArr) continue;
            if(_megaSportsmen[i].getResultTime(lap) != null)
            {
                if(_megaSportsmen[i].getPlace(lap) >= place)
                {
                    _megaSportsmen[i].setPlace(_megaSportsmen[i].getPlace(lap) + 1, lap);
                }
            }
        }
    }

    private void AddRowCompetitionTable(MegaSportsman megaSportsman, int lap)
    {
        int rowColor = megaSportsman.getColor();

        TableRow newRow = new TableRow(this);
        final TextView newTextView0 = new TextView(this);
        newTextView0.setSingleLine(false);
        newTextView0.setText(String.valueOf(megaSportsman.getNumber()));
        newTextView0.setTextColor(rowColor);
        newTextView0.setGravity(Gravity.CENTER);
        newTextView0.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
        newTextView0.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView0.setLayoutParams(new TableRow.LayoutParams(_numberParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,10f));
        ((TableRow.LayoutParams) newTextView0.getLayoutParams()).setMargins(2, 0, 2, 2);

        final TextView newTextView1 = new TextView(this);
        newTextView1.setSingleLine(false);
        newTextView1.setText(String.valueOf(megaSportsman.getPlace(lap)));
        newTextView1.setGravity(Gravity.CENTER);
        newTextView1.setTextColor(rowColor);
        newTextView1.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
        newTextView1.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView1.setLayoutParams(new TableRow.LayoutParams(_positionParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,10f));
        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView2 = new TextView(this);
        newTextView2.setSingleLine(false);
        newTextView2.setText(String.valueOf(megaSportsman.getName()));
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setTextColor(rowColor);
        newTextView2.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_nameParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,40f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView3 = new TextView(this);
        newTextView3.setSingleLine(false);
        newTextView3.setText(megaSportsman.getResultTime(lap).format("%H:%M:%S"));
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setTextColor(rowColor);
        newTextView3.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_timeParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,20f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);

        final TextView newTextView4 = new TextView(this);
        newTextView4.setSingleLine(false);
        newTextView4.setText("+"+GetLag(megaSportsman, lap).format("%M:%S"));
        //newTextView4.setText("test");
        newTextView4.setGravity(Gravity.CENTER);
        newTextView4.setTextColor(rowColor);
        newTextView4.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
        newTextView4.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView4.setLayoutParams(new TableRow.LayoutParams(_lagParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,20f));
        ((TableRow.LayoutParams) newTextView4.getLayoutParams()).setMargins(0, 0, 2, 2);

        newRow.addView(newTextView0);
        newRow.addView(newTextView1);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        newRow.addView(newTextView4);

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

    private android.text.format.Time GetLag(MegaSportsman sportsman, int lap)
    {
        android.text.format.Time lag = new android.text.format.Time();
        for(int i = 0; i < _megaSportsmen.length; i++)
        {
            if(_megaSportsmen[i].getPlace(lap) == 1)
            {
                lag.second = sportsman.getResultTime(lap).second - _megaSportsmen[i].getResultTime(lap).second;
                lag.minute = sportsman.getResultTime(lap).minute - _megaSportsmen[i].getResultTime(lap).minute;
                lag.hour = sportsman.getResultTime(lap).hour - _megaSportsmen[i].getResultTime(lap).hour;
                lag.normalize(false);
                break;
            }
        }


        return lag;
    }


    public void imgBtnSettings_OnClick(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startBtnClick(View view)
    {
        if(_isPaused && _competitionState == CompetitionState.Running)
        {
            _isPaused = false;
            return;
        }
        if(_competitionState == CompetitionState.NotStarted)
        {
            _isPaused = false;
            //_startBtn.setText(getResources().getString(R.string.stop_timer));
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
                        if(!_isPaused) ms++;
//                        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_mas_start)) && _number < _participants.length)
//                        {
//                            _number = _participants.length;
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run()
//                                {
//                                    android.text.format.Time localTime = new android.text.format.Time();
//                                    localTime.second = _currentTime.second;
//                                    localTime.minute = _currentTime.minute;
//                                    localTime.hour = _currentTime.hour;
//
//                                    for(int i = 0; i < _participants.length; i++)
//                                    {
//                                        _participantGridLayout.addView(CreateButton(_participants[i], "0"));
//                                        _participants[i].SetStartTime(localTime);
//                                    }
//
//                                }
//                            });
//                        }
                        if(ms>9)
                        {
                            _currentTime.second++;
                            ms = 0;
                            _currentTime.normalize(false);

                            if(android.text.format.Time.compare(_currentTime,_timeNextParticipant) == 0 && _number < _megaSportsmen.length)
                            {

                                if(!_currentCompetition.GetSecondInterval().equals(""))
                                {
                                    if (_number == Integer.valueOf(_currentCompetition.GetNumberSecondInterval()) - 1) {
                                        _currentInterval.second = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[1]);
                                        _currentInterval.minute = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[0]);
                                    }
                                }

                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_single_start)))
                                        {
                                            _participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                            _megaSportsmen[_number].setStartTime(_currentTime);
                                            _number++;
                                        }
                                        else
                                        {
                                            if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_double_start)))
                                            {
                                                _participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                                _megaSportsmen[_number].setStartTime(_currentTime);
                                                _number++;
                                                if(_number < _megaSportsmen.length)
                                                {
                                                    _participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                                    _megaSportsmen[_number].setStartTime(_currentTime);
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
                    _competitionTimer.setTextColor(getResources().getColor(R.color.white));
                    _timerParticipantTable.setTextColor(getResources().getColor(R.color.white));
                    _competitionState = CompetitionState.Running;

                }
            };
            _countDownTimer.start();
            _competitionState = CompetitionState.Started;
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


    public void returnLastStepOnClick(View view)
    {

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

    private void SaveResultsToDatabase()
    {
        RealmMegaSportsmanSaver megaSportsmanSaver = new RealmMegaSportsmanSaver(this, "RESULTS");

        for(int i = 0; i<_megaSportsmen.length; i++)
        {
            _megaSportsmen[i].makeForSaving();
            megaSportsmanSaver.SaveSportsman(_megaSportsmen[i]);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == (KeyEvent.KEYCODE_BACK))
        {
            if(_competitionState != CompetitionState.NotStarted)
            {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle(getResources().getString(R.string.warning_dialog_title));
                dialog.setMessage(getResources().getString(R.string.message_end_competition));
                dialog.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // заканчиваем нахер
                        SaveResultsToDatabase();
                        Intent intent = new Intent(CompetitionsActivity.this, FinalActivity.class);
                        startActivity(intent);
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

    public void pauseBtnClick(View view)
    {
        if(_competitionState == CompetitionState.Running)
        {
            _isPaused = true;
        }
        Toast.makeText(getApplicationContext(),"Пауза",Toast.LENGTH_SHORT).show();
    }

    public void stopBtnClick(View view)
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
                _competitionState = CompetitionState.NotStarted;
                _number = 0;
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
        Toast.makeText(getApplicationContext(),"Стоп",Toast.LENGTH_SHORT).show();
    }
}
