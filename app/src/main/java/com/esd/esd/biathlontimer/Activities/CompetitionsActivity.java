package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.icu.text.MessagePattern;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
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
import com.esd.esd.biathlontimer.GridViewAdapter;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.MyButton;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Sportsman;

import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.w3c.dom.Text;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;


public class CompetitionsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener
{

    private GridLayout _participantGridLayout;
    private GridView _gridView;
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
    private GridViewAdapter _viewAdapter;
    private int _currentTable = 0;
    private int _number = 0;
    private int lapsCount;

    private View _dialogOwnerView;

    private CountDownTimer _countDownTimer;

    private boolean _isPaused = true;

    private CompetitionTableAdapter _tableAdapter;
    private RecyclerView _table;

    private enum CompetitionState
    {
        NotStarted,
        Started,
        Running
    }

    private ArrayList<MegaSportsman>[] _arrayMegaSportsmen;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        _timeNextParticipant = new android.text.format.Time();
        _currentInterval = new android.text.format.Time();
        _button = new MyButton(this);

        _currentCompetition = new Competition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"), this);
        _currentCompetition.GetAllSettingsToComp();

        _competitionState = CompetitionState.NotStarted;

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_competitions, null);
        pages.add(page1);
        //_participantGridLayout = (GridLayout) page1.findViewById(R.id.competitionGridLayout);
        _competitionTimer = (TextView) page1.findViewById(R.id.competitionTimer);
        _startBtn = (ImageButton) page1.findViewById(R.id.competitionStart);

        //Тест
        _gridView = (GridView) page1.findViewById(R.id.gridView);
        _gridView.setOnItemLongClickListener(gridViewOnItemLongClickListener);

        View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
        pages.add(page2);
        //_containerTables = (LinearLayout) page2.findViewById(R.id.containerTablesCompetition);
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
                TextView NumberStr = (TextView) _dialogOwnerView.findViewById(R.id.numberParticipantMyButton);
                TextView LapStr = (TextView) _dialogOwnerView.findViewById(R.id.lapParticipantMyButton);
                int participantNumber = Integer.valueOf(NumberStr.getText().toString());
                int lapNumber = Integer.valueOf(LapStr.getText().toString());

                fineTime.second = fineCount*fineSeconds;
                fineTime.minute = fineCount*fineMinutes;
                fineTime.normalize(false);
                for(int i = 0; i < _megaSportsmen.length; i++)
                {
                    if(_megaSportsmen[i].getNumber() == participantNumber)
                    {
                        _megaSportsmen[i].setFineCount(fineCount);
                        _megaSportsmen[i].setFineTime(fineTime);
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
        _arrayMegaSportsmen = new ArrayList[lapsCount];
        for(int i = 0; i < lapsCount; i++)
        {
            _arrayMegaSportsmen[i] = new ArrayList<MegaSportsman>();
        }
        //CreateTables(lapsCount);
        _currentRound.setText(_currentRound.getText() + " - " + Integer.toString(_currentTable + 1));
        _tableAdapter = new CompetitionTableAdapter(this);
        _table = (RecyclerView) page2.findViewById(R.id.recyclerViewTableCompetition);
        _table.setAdapter(_tableAdapter);
        _table.setItemAnimator(new DefaultItemAnimator());
        _table.setLayoutManager(new LinearLayoutManager(this));
        TimerStartPosition();

    }

    private GridView.OnItemLongClickListener gridViewOnItemLongClickListener = new GridView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
        {
//            TextView NumberStr = (TextView) view.findViewById(R.id.numberParticipantMyButton);
//            TextView LapStr = (TextView) view.findViewById(R.id.lapParticipantMyButton);
//            int number = Integer.valueOf(NumberStr.getText().toString());
//            int lap = Integer.valueOf(LapStr.getText().toString());
            _dialogOwnerView = view;
            _fineDialog.show();
            return true;
        }
    };
    //Здесь клик на ячейку
    private GridView.OnItemClickListener gridViewOnItemClickListner = new GridView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            TextView NumberStr = (TextView) view.findViewById(R.id.numberParticipantMyButton);
            TextView LapStr = (TextView) view.findViewById(R.id.lapParticipantMyButton);
            int number = Integer.valueOf(NumberStr.getText().toString());
            int lap = Integer.valueOf(LapStr.getText().toString());
            MegaSportsman localSportsman = null;
            _viewAdapter.ChangeSportsmanLap(number, lap+1);
            android.text.format.Time newTime = new android.text.format.Time();
            for (int i = 0; i < _megaSportsmen.length; i++) {
                if (number == _megaSportsmen[i].getNumber()) {
                    newTime.hour = _currentTime.hour - _megaSportsmen[i].getStartTime().hour;
                    newTime.minute = _currentTime.minute - _megaSportsmen[i].getStartTime().minute;
                    newTime.second = _currentTime.second - _megaSportsmen[i].getStartTime().second;
                    newTime.normalize(false);
                    localSportsman = new MegaSportsman(_megaSportsmen[i]);
                   // _megaSportsmen[i].setFineTime(null);
                    _megaSportsmen[i].setFineCount(0);
                    break;
                }
            }

            localSportsman.setResultTime(newTime);
            GetPlace(localSportsman, lap);
            Collections.sort(_arrayMegaSportsmen[lap], new Comparator<MegaSportsman>() {
                @Override
                public int compare(MegaSportsman o1, MegaSportsman o2) {
                    return o1.getPlace().compareTo(o2.getPlace());
                }
            });
            GetLag(lap);
            if(_currentTable == lap)
            {
                _tableAdapter.ClearList();
                _tableAdapter.AddSportsmen(_arrayMegaSportsmen[lap]);
                _tableAdapter.notifyDataSetChanged();
            }
            if (lap == lapsCount - 1)
            {
                _viewAdapter.RemoveSportsman(number);
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LoadingTask loadingTask = new LoadingTask();
        loadingTask.execute();
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

    private void GetPlace(MegaSportsman sportsman, int lap)
    {
        int place = 1;
        android.text.format.Time thisTime = new android.text.format.Time();
        for(int i = 0; i<_arrayMegaSportsmen[lap].size(); i++)
        {
            thisTime = new android.text.format.Time(_arrayMegaSportsmen[lap].get(i).getResultTime());
            if(android.text.format.Time.compare(sportsman.getResultTime(), thisTime) > 0)
            {
                place++;
            }
        }
        sportsman.setPlace(place);
        _arrayMegaSportsmen[lap].add(sportsman);
        for(int i = 0; i < _arrayMegaSportsmen[lap].size(); i++)
        {
            if(_arrayMegaSportsmen[lap].get(i).equals(sportsman)) continue;
            if(_arrayMegaSportsmen[lap].get(i).getPlace() >= place)
            {
                _arrayMegaSportsmen[lap].get(i).setPlace(_arrayMegaSportsmen[lap].get(i).getPlace()+1);
            }
        }
    }

    public void imgBtnSettings_OnClick(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    Handler handler;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run()
        {
            while(true) {
                if (android.text.format.Time.compare(_currentTime, _timeNextParticipant) == 0 && _number < _megaSportsmen.length) {

                    if (!_currentCompetition.GetSecondInterval().equals("")) {
                        if (_number == Integer.valueOf(_currentCompetition.GetNumberSecondInterval()) - 1) {
                            _currentInterval.second = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[1]);
                            _currentInterval.minute = Integer.valueOf(_currentCompetition.GetSecondInterval().split(":")[0]);
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_single_start))) {
                                //_participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                _megaSportsmen[_number].setStartTime(_currentTime);
                                _viewAdapter.AddSportsman(_megaSportsmen[_number]);
                                _number++;
                            } else {
                                if (_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_double_start))) {
                                    //_participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                    _megaSportsmen[_number].setStartTime(_currentTime);
                                    _viewAdapter.AddSportsman(_megaSportsmen[_number]);
                                    _number++;
                                    if (_number < _megaSportsmen.length) {
                                        //_participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                        _megaSportsmen[_number].setStartTime(_currentTime);
                                        _viewAdapter.AddSportsman(_megaSportsmen[_number]);
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
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {

                }
            }
        }
    });

    int ms;
    public void startBtnClick(View view)
    {
        handler = new Handler();
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
                    //int ms = 0;
                    //String msStr;

                    @Override
                    public void run()
                    {
                        MyTimerTask task = new MyTimerTask();
                        task.execute(ms);
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
                public void onFinish()
                {
                    _timer = new Timer();
                    _timer.schedule(task, 0,100);
                    if(!_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_mas_start))) thread.start();
                    _competitionTimer.setTextColor(getResources().getColor(R.color.white));
                    _timerParticipantTable.setTextColor(getResources().getColor(R.color.white));
                    _competitionState = CompetitionState.Running;
                    if(_currentCompetition.GetStartType().equals(getResources().getString(R.string.item_type_mas_start)))
                    {
                        for(int i = 0; i < _megaSportsmen.length; i++)
                        {
                            _megaSportsmen[i].setStartTime(new android.text.format.Time());
                        }
                        _viewAdapter = new GridViewAdapter(CompetitionsActivity.this, new ArrayList<MegaSportsman>(Arrays.asList(_megaSportsmen)));
                    }
                    else
                        _viewAdapter = new GridViewAdapter(CompetitionsActivity.this, new ArrayList<MegaSportsman>());
                    _gridView.setAdapter(_viewAdapter);
                    _gridView.setOnItemClickListener(gridViewOnItemClickListner);

                }
            };
            _countDownTimer.start();
            _competitionState = CompetitionState.Started;
        }
        else
        {


        }

    }

    public void GetLag(int lap)
    {
        _arrayMegaSportsmen[lap].get(0).setLag("00:00:00");
        android.text.format.Time bestTime = new android.text.format.Time(_arrayMegaSportsmen[lap].get(0).getResultTime());
        android.text.format.Time localTime;
        for(int i = 1; i < _arrayMegaSportsmen[lap].size(); i++)
        {
           localTime = new android.text.format.Time(_arrayMegaSportsmen[lap].get(i).getResultTime());
           localTime.hour -= bestTime.hour;
           localTime.minute -= bestTime.minute;
           localTime.second -= bestTime.second;
           localTime.normalize(false);
           _arrayMegaSportsmen[lap].get(i).setLag(localTime.format("%H:%M:%S"));

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
        if(_currentTable < lapsCount - 1)
        {
            _currentTable++;
            _tableAdapter.ClearList();
            _tableAdapter.AddSportsmen(_arrayMegaSportsmen[_currentTable]);
            _tableAdapter.notifyDataSetChanged();
            _currentRound.setText(getResources().getString(R.string.current_round) + " - " + Integer.toString(_currentTable + 1));
        }
    }

    public void OnClickPreviousTable(View view)
    {
        if(_currentTable > 0)
        {
            _currentTable--;
            _tableAdapter.ClearList();
            _tableAdapter.AddSportsmen(_arrayMegaSportsmen[_currentTable]);
            _tableAdapter.notifyDataSetChanged();
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
        for(int i = 0; i < lapsCount; i++)
        {
            RealmMegaSportsmanSaver megaSportsmanSaver = new RealmMegaSportsmanSaver(this, "LAP"+i+_currentCompetition.GetNameDateString());
            megaSportsmanSaver.DeleteTable();
            megaSportsmanSaver = new RealmMegaSportsmanSaver(this, "LAP"+i+_currentCompetition.GetNameDateString());
            megaSportsmanSaver.SaveSportsmen(_arrayMegaSportsmen[i]);
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
                        _timer.cancel();
                        SaveResultsToDatabase();
                        Intent intent = new Intent(CompetitionsActivity.this, FinalActivity.class);
                        intent.putExtra("Name",_currentCompetition.GetName());
                        intent.putExtra("Date",_currentCompetition.GetDate());
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
//                for(int j = 0; j < _tablesCompetition.size(); j++)
//                {
//                    _tablesCompetition.get(j).removeAllViews();
//                }
                _tableAdapter.ClearList();
                _tableAdapter.notifyDataSetChanged();
                for(int j = 0; j < lapsCount; j++)
                {
                    _arrayMegaSportsmen[j].clear();
                }
                _viewAdapter.ClearList();
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

    class MyTimerTask extends AsyncTask<Integer, Integer, Integer>
    {

        @Override
        protected Integer doInBackground(Integer... params) {
            if(!_isPaused) ms++;
            if(ms>9)
            {
                _currentTime.second++;
                ms = 0;
                _currentTime.normalize(false);

            }
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            _competitionTimer.setText(_currentTime.format("%H:%M:%S")+":"+String.valueOf(ms));
            _timerParticipantTable.setText(_currentTime.format("%H:%M:%S")+":"+String.valueOf(ms));
        }
    }

    class LoadingTask extends AsyncTask<Void, Integer, Void>
    {
        ProgressDialog dialog;

        @Override
        protected Void doInBackground(Void... params)
        {
            RealmSportsmenSaver saver = new RealmSportsmenSaver(getApplicationContext(), _currentCompetition.GetDbParticipantPath());
            List<Sportsman> list = saver.GetSportsmen("number", true);
            _megaSportsmen = new MegaSportsman[list.size()];
            for(int i = 0; i<list.size(); i++)
            {
                _megaSportsmen[i] = new MegaSportsman(list.get(i));
                //_megaSportsmen[i].setLapsCount(lapsCount);
                publishProgress(i);
                try {
                    TimeUnit.MILLISECONDS.sleep(2);
                }catch (InterruptedException ex)
                {

                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CompetitionsActivity.this);
            dialog.setMessage("fegeg");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMax(_currentCompetition.GetMaxParticipantCount());
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }
}
