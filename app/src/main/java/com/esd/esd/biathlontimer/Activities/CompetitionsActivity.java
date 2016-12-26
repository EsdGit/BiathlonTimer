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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.esd.esd.biathlontimer.CompetitionTableAdapter;
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
    private ErrorsBuffer<String> _buffer;
    private final int ErrorCount = 5;

    private GridLayout _participantGridLayout;
    private LinearLayout _containerTables;
    private TextView _currentRound;
    private TextView _competitionTimer;
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
    //private Participant[] _participants;
    private ArrayList<RecyclerView> _tablesCompetition;
    private ArrayList<RealmMegaSportsmanSaver> _megaSavers;
    private ArrayList<CompetitionTableAdapter> _adapter;
    private int _currentTable = 0;
    private int _number = 0;

    private FrameLayout _dialogOwnerView;

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

        _buffer = new ErrorsBuffer<>(ErrorCount);


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

//                for(int i = 0; i < _participants.length; i++)
//                {
//                    if(participantNumber == Integer.valueOf(_participants[i].GetNumber()))
//                    {
//                        participantNumber = i;
//                        break;
//                    }
//                }

                _dialogSeekBar.setProgress(0);
                _dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");


                _buffer.Write("F "+_button.GetParticipantNumber(_dialogOwnerView)+" "+String.valueOf(lapNumber)+" "+String.valueOf(fineCount));
               // _participants[participantNumber].SetFineTime(fineTime,lapNumber);
               // _participants[participantNumber].SetFineCount(fineCount,lapNumber);
               // _participants[participantNumber].SetPlace(GetPlace(_participants[participantNumber], lapNumber), lapNumber);


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
        _megaSavers = new ArrayList<>();
        final int tablesCount = Integer.valueOf(_currentCompetition.GetCheckPointsCount());
        new Thread(new Runnable() {
            @Override
            public void run() {
                RealmSportsmenSaver saver = new RealmSportsmenSaver(getApplicationContext(), _currentCompetition.GetDbParticipantPath());
                List<Sportsman> fromDb = saver.GetSportsmen("number",true);
                RealmMegaSportsmanSaver megaSportsmanSaver;
                List<MegaSportsman> list = new ArrayList<MegaSportsman>();
                for(int i = 0; i<fromDb.size(); i++)
                {
                    list.add(new MegaSportsman(fromDb.get(i)));
                }

                _adapter = new ArrayList<>();
                for(int i = 0; i < tablesCount; i++)
                {
                    megaSportsmanSaver = new RealmMegaSportsmanSaver(getApplicationContext(), _currentCompetition.GetNameDateString()+"LAP"+String.valueOf(i));
                    megaSportsmanSaver.SaveSportsmen(list);
                    _megaSavers.add(megaSportsmanSaver);

                    _adapter.add(new CompetitionTableAdapter(getApplicationContext(),R.layout.row_competition_table));
                }

                CreateTables(tablesCount);

            }
        }).start();

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
//
//    private View CreateButton(final Sportsman sportsman, String numberCheckPoint)
//    {
//        LayoutInflater inflater = LayoutInflater.from(this);
//        final View view = inflater.inflate(R.layout.my_btn, null);
//        _button.SetParticipantNumberAndBackground(view, String.valueOf(sportsman.getNumber()), sportsman.getColor());
//        _button.SetParticipantLap(view, numberCheckPoint);
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                _dialogOwnerView = (FrameLayout) view;
//                _fineDialog.show();
//                return false;
//            }
//        });
//        view.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                int number = Integer.valueOf(_button.GetParticipantNumber(v));
//                int lap = Integer.valueOf(_button.GetLap(v));
//
//
//                android.text.format.Time newTime = new android.text.format.Time();
//
//                newTime.hour = _currentTime.hour - _participants[number].GetStartTime().hour;
//                newTime.minute = _currentTime.minute - _participants[number].GetStartTime().minute;
//                newTime.second = _currentTime.second - _participants[number].GetStartTime().second;
//                newTime.normalize(false);
//
//                _participants[number].SetResultTime(newTime.format3339(true), _currentCompetition.GetNameDateString()+"LAP"+_button.GetLap(v), _lapSaver);
//
//                String res = _participants[number].GetResultTime(_currentCompetition.GetNameDateString()+"LAP"+_button.GetLap(v), _lapSaver);
//                GetPlace(_participants[number], lap);
//                //_participants[number].SetPlace(GetPlace(participant, lap), lap);
//
//
//                _tablesCompetition.get(lap).removeAllViews();
//                for(int i = 0; i < _laps[lap].GetParticipants().length; i++)
//                {
//                    AddRowCompetitionTable(_laps[lap].GetParticipant(i),lap);
//                }
//
//                if(lap == _laps.length - 1)
//                {
//                    _participantGridLayout.removeView(view);
//                }
//                else
//                {
//                    lap++;
//                    _button.SetParticipantLap(view, Integer.toString(lap));
//                }
//            }
//        });
//        return view;
//    }



//    private int GetPlace(Participant participant, int lap)
//    {
//        int place = 0;
//        int participantCount = 0;
//        boolean _isLastPlace = true;
//        android.text.format.Time thisTime = new android.text.format.Time();
//        android.text.format.Time localTime = new android.text.format.Time();
//        String time;
//        time = _lapSaver.GetData(_currentCompetition.GetNameDateString()+"LAP"+lap, participant, DatabaseProvider.DbLapData.COLUMN_RESULT, DatabaseProvider.DbLapData.COLUMN_RESULT);
//        thisTime.parse3339(time);
//        for(int i = 0; i < _participants.length; i++)
//        {
//            time = _lapSaver.GetData(_currentCompetition.GetNameDateString()+"LAP"+lap, _participants[i], DatabaseProvider.DbLapData.COLUMN_RESULT, DatabaseProvider.DbLapData.COLUMN_RESULT);
//            if(time != null)
//            {
//                participantCount++;
//                localTime.parse3339(time);
//                if (android.text.format.Time.compare(localTime, thisTime) > 0) {
//                    place = i + 1;
//                    participant.SetPlace(_currentCompetition.GetNameDateString() + "LAP" + lap, _lapSaver, place);
//                    _isLastPlace = false;
//                    break;
//                }
//            }
//        }
//
//        if(_isLastPlace)
//        {
//            place = participantCount+1;
//            participant.SetPlace(_currentCompetition.GetNameDateString() + "LAP" + lap, _lapSaver, place);
//        }
////        else
////        {
////            for(int i = place; i<_laps[lap].GetParticipants().length; i++)
////            {
////               _laps[lap].GetParticipant(i).SetPlace(i+1, lap);
////            }
////        }
//        return place;
//    }

//    private void AddRowCompetitionTable(Participant participant, int lap)
//    {
//        int rowColor = participant.GetColor();
//
//        TableRow newRow = new TableRow(this);
//        final TextView newTextView0 = new TextView(this);
//        newTextView0.setSingleLine(false);
//        newTextView0.setText(participant.GetNumber());
//        newTextView0.setTextColor(rowColor);
//        newTextView0.setGravity(Gravity.CENTER);
//        newTextView0.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
//        newTextView0.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView0.setLayoutParams(new TableRow.LayoutParams(_numberParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,20f));
//        ((TableRow.LayoutParams) newTextView0.getLayoutParams()).setMargins(2, 0, 2, 2);
//
//        final TextView newTextView1 = new TextView(this);
//        newTextView1.setSingleLine(false);
//        //newTextView1.setText(String.valueOf(participant.GetPlace(lap)));
//        newTextView1.setGravity(Gravity.CENTER);
//        newTextView1.setTextColor(rowColor);
//        newTextView1.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
//        newTextView1.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView1.setLayoutParams(new TableRow.LayoutParams(_positionParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,20f));
//        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        final TextView newTextView2 = new TextView(this);
//        newTextView2.setSingleLine(false);
//        //newTextView2.setText(participant.GetResultTime(lap).format("%H:%M:%S"));
//        newTextView2.setGravity(Gravity.CENTER);
//        newTextView2.setTextColor(rowColor);
//        newTextView2.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
//        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView2.setLayoutParams(new TableRow.LayoutParams(_timeParticipant.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT,30f));
//        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        final TextView newTextView3 = new TextView(this);
//        newTextView3.setSingleLine(false);
//        newTextView3.setText("+"+GetLag(participant, lap).format("%M:%S"));
//        newTextView3.setGravity(Gravity.CENTER);
//        newTextView3.setTextColor(rowColor);
//        newTextView3.setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
//        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView3.setLayoutParams(new TableRow.LayoutParams(_lagParticipant.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT,30f));
//        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        newRow.addView(newTextView0);
//        newRow.addView(newTextView1);
//        newRow.addView(newTextView2);
//        newRow.addView(newTextView3);
//
//        _tablesCompetition.get(lap).addView(newRow);
//    }


    private void CreateTables(int countCheckPoint)
    {
        for(int i = 0; i < countCheckPoint; i++)
        {
            final RecyclerView newTable = new RecyclerView(this);
            newTable.setAdapter(_adapter.get(i));
            newTable.setItemAnimator(new DefaultItemAnimator());
            newTable.setLayoutManager(new LinearLayoutManager(this));
            _tablesCompetition.add(newTable);
            newTable.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            newTable.setBackgroundColor(Color.BLACK);
            if(i!=0)
            {
                newTable.setVisibility(View.GONE);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    _containerTables.addView(newTable);
                }
            });

        }

    }

//    private android.text.format.Time GetLag(Participant participant, int lap)
//    {
//        // По lap выбираем arrayList
//        android.text.format.Time lag = new android.text.format.Time();
//       // lag.second = participant.GetResultTime(lap).second - _laps[lap].GetParticipant(0).GetResultTime(lap).second;
//       // lag.minute = participant.GetResultTime(lap).minute - _laps[lap].GetParticipant(0).GetResultTime(lap).minute;
//        //lag.hour = participant.GetResultTime(lap).hour - _laps[lap].GetParticipant(0).GetResultTime(lap).hour;
//        lag.normalize(false);
//        return lag;
//    }


    public void imgBtnSettings_OnClick(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startBtnClick(View view)
    {
        _adapter.get(0).AddSportsman(new MegaSportsman(0,"Красава",1990,"","",1));
        _adapter.get(1).AddSportsman(new MegaSportsman(15,"Красава",1990,"","",1));
        _adapter.get(2).AddSportsman(new MegaSportsman(98,"Красава",1990,"","",1));
    }

//    public void startBtnClick(View view)
//    {
//        if(!_isCompetitionStarted)
//        {
//            _startBtn.setText(getResources().getString(R.string.stop_timer));
//            final android.text.format.Time timeCountDown = new android.text.format.Time();
//            timeCountDown.minute = Integer.valueOf(_currentCompetition.GetTimeToStart().split(":")[0]);
//            timeCountDown.second = Integer.valueOf(_currentCompetition.GetTimeToStart().split(":")[1]);
//            final long ms1 = timeCountDown.minute*60000+timeCountDown.second*1000;
//
//            _currentTime = new android.text.format.Time(_timeNextParticipant);
//            _currentTime.second = 0;
//            _currentTime.minute = 0;
//            _countDownTimer = new CountDownTimer(ms1+1000,1000)
//            {
//                TimerTask task = new TimerTask()
//                {
//                    int ms = 0;
//                    String msStr;
//
//                    @Override
//                    public void run()
//                    {
//                        ms++;
//                        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_mas_start)))
//                        {
////                            _number = _participants.length;
////                            runOnUiThread(new Runnable() {
////                                @Override
////                                public void run()
////                                {
////                                    android.text.format.Time localTime = new android.text.format.Time();
////                                    localTime.second = _currentTime.second;
////                                    localTime.minute = _currentTime.minute;
////                                    localTime.hour = _currentTime.hour;
////
////                                    for(int i = 0; i < _participants.length; i++)
////                                    {
////                                        _participantGridLayout.addView(CreateButton(_participants[i], "0"));
////                                        _participants[i].SetStartTime(localTime);
////                                    }
////
////                                }
////                            });
//
//                            // ЗДЕСЬ У НАС МАССОВЫЙ СТАРТ
//                        }
//                        if(ms>9)
//                        {
//                            _currentTime.second++;
//                            ms = 0;
//                            _currentTime.normalize(false);
//
//                            if(android.text.format.Time.compare(_currentTime,_timeNextParticipant) == 0 && _number < _participants.length)
//                            {
//
//                                if(!_currentCompetition.GetSecondInterval().equals(""))
//                                {
//                                    if (_number == Integer.valueOf(_currentCompetition.GetNumberSecondInterval()) - 1) {
//                                        _currentInterval.second = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[1]);
//                                        _currentInterval.minute = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[0]);
//                                    }
//                                }
//
//                                runOnUiThread(new Runnable()
//                                {
//                                    @Override
//                                    public void run()
//                                    {
//                                        if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_single_start)))
//                                        {
//                                            _participantGridLayout.addView(CreateButton(_participants[_number], "0"));
//                                            _participants[_number].SetStartTime(_currentTime);
//                                            _number++;
//                                        }
//                                        else
//                                        {
//                                            if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_double_start)))
//                                            {
//                                                _participantGridLayout.addView(CreateButton(_participants[_number], "0"));
//                                                _participants[_number].SetStartTime(_currentTime);
//                                                _number++;
//                                                if(_number < _participants.length)
//                                                {
//                                                    _participantGridLayout.addView(CreateButton(_participants[_number], "0"));
//                                                    _participants[_number].SetStartTime(_currentTime);
//                                                    _number++;
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
//                                _timeNextParticipant.hour += _currentInterval.hour;
//                                _timeNextParticipant.minute += _currentInterval.minute;
//                                _timeNextParticipant.second += _currentInterval.second;
//                                _timeNextParticipant.normalize(false);
//
//
//                            }
//
//                        }
//
//
//
//
//                        msStr = String.valueOf(ms);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                _competitionTimer.setText(_currentTime.format("%H:%M:%S")+":"+msStr);
//                                _timerParticipantTable.setText(_currentTime.format("%H:%M:%S")+":"+msStr);
//                            }
//                        });
//                    }
//                };
//                @Override
//                public void onTick(long millisUntilFinished)
//                {
//                    timeCountDown.second--;
//                    if(timeCountDown.second < 0)
//                    {
//                        timeCountDown.minute--;
//                        timeCountDown.second = 59;
//                    }
//                    _competitionTimer.setText(timeCountDown.format("%M:%S"));
//                    _timerParticipantTable.setText(timeCountDown.format("%M:%S"));
//                }
//
//                @Override
//                public void onFinish() {
//                    _timer = new Timer();
//                    _timer.schedule(task, 0,100);
//                    _competitionTimer.setTextColor(getResources().getColor(R.color.white));
//                    _timerParticipantTable.setTextColor(getResources().getColor(R.color.white));
//
//                }
//            };
//            _countDownTimer.start();
//            _isCompetitionStarted = true;
//        }
//        else
//        {
//            // Здесь сброс
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(getResources().getString(R.string.reset_title));
//            builder.setMessage(getResources().getString(R.string.message_dialog_reset));
//            builder.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener()
//            {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i)
//                {
//                    if(_countDownTimer != null) _countDownTimer.cancel();
//                    if(_timer != null) _timer.cancel();
//                    TimerStartPosition();
//                    for(int j = 0; j < _tablesCompetition.size(); j++)
//                    {
//                        _tablesCompetition.get(j).removeAllViews();
//                    }
//                    _participantGridLayout.removeAllViews();
//                    _isCompetitionStarted = false;
//                    _startBtn.setText(getResources().getString(R.string.start_timer));
//                    _number = 0;
//                }
//            });
//
//            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                }
//            });
//
//            builder.show();
//
//        }
//
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(_timer != null) _timer.cancel();
    }

    private void ParseErrorString(String error)
    {
        String[] lastError = error.split(" ");
        switch(lastError[0])
        {
            case "T":
                int lapNumber = Integer.valueOf(lastError[2]);
                int participantNumber = Integer.valueOf(lastError[1]);
                _tablesCompetition.get(lapNumber).removeAllViews();
                android.text.format.Time localTime = null;
//                for(int i = 0; i < _laps[lapNumber].GetParticipants().length; i++)
//                {
//                    if(Integer.valueOf(_laps[lapNumber].GetParticipant(i).GetNumber()) == participantNumber)
//                    {
//                       // localTime = new android.text.format.Time(_laps[lapNumber].GetParticipant(i).GetResultTime(lapNumber));
//                        _laps[lapNumber].RemoveParticipant(_laps[lapNumber].GetParticipant(i));
//                        break;
//                    }
//                }
//
//                _button.ChangeLap(lastError[1], String.valueOf(lapNumber+1));
//
//                if(localTime != null)
//                {
//                    for (int i = 0; i < _laps[lapNumber].GetParticipants().length; i++)
//                    {
////                        if (android.text.format.Time.compare(_laps[lapNumber].GetParticipant(i).GetResultTime(lapNumber), localTime) > 0)
////                        {
////                            _laps[lapNumber].GetParticipant(i).SetPlace(_laps[lapNumber].GetParticipant(i).GetPlace(lapNumber) - 1, lapNumber);
////                        }
//                    }
//                }
//
//                for(int i = 0; i < _laps[lapNumber].GetParticipants().length; i++)
//                {
//                    AddRowCompetitionTable(_laps[lapNumber].GetParticipant(i),lapNumber);
//                }

                break;
            case "F":
                break;
        }
    }


    public void returnLastStepOnClick(View view)
    {
        if(!_buffer.IsEmpty()) {
            String val = _buffer.Read();
            ParseErrorString(val);
        }
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
