package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Adapters.FineAdapter;
import com.esd.esd.biathlontimer.ChangeColorEvent;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.Adapters.CompetitionTableAdapter;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;
import com.esd.esd.biathlontimer.Adapters.GridViewAdapter;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.MyButton;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Sportsman;
import com.esd.esd.biathlontimer.SportsmanDeleteEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class CompetitionsActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private TableLayout _lastTable;
    private ViewPager _viewPagerLap;
    private GridView _gridView;
    private GridView _fineGridView;
    private TextView _currentRound;
    private TextView _competitionTimer;
    private TextView _timerParticipantTable;

    private AlertDialog _fineDialog;
    private AlertDialog.Builder _builderFineDialog;
    private AlertDialog.Builder _timerDialogBuilder;
    private AlertDialog _timerDialog;
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
    private FineAdapter _fineAdapter;
    private int _currentTable = 0;
    private int _number = 0;
    private int lapsCount;
    private int _currentSportsman;
    private boolean _isFirstLoad = true;
    private ArrayList<RecyclerView> _arrayRecycleView;
    private ArrayList<CompetitionTableAdapter> _arrayAdapters;

    private View _dialogOwnerView;

    private CountDownTimer _countDownTimer;

    private boolean _isPaused = true;

    private CompetitionTableAdapter _tableAdapter;
    private RecyclerView _table;
    private RecyclerView _currentRecyclerView;
    private Thread checkLapNumberThread;

    private EventBus _event;

    private enum CompetitionState {
        NotStarted,
        Started,
        Running
    }

    private ArrayList<MegaSportsman>[] _arrayMegaSportsmen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();


        _timeNextParticipant = new android.text.format.Time();
        _currentInterval = new android.text.format.Time();

        RealmCompetitionSaver saverComp = new RealmCompetitionSaver(this, "COMPETITIONS");
        _currentCompetition = saverComp.GetCompetition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"));
        saverComp.Dispose();
        _competitionState = CompetitionState.NotStarted;

        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_competitions, null);
        pages.add(page1);
        _competitionTimer = (TextView) page1.findViewById(R.id.competitionTimer);

        _event = EventBus.getDefault();
        _event.register(this);
        //Тест
        _gridView = (GridView) page1.findViewById(R.id.gridView);
        _gridView.setOnItemLongClickListener(gridViewOnItemLongClickListener);

        if(!getResources().getBoolean(R.bool.isTablet))
        {
            View page2 = inflater.inflate(R.layout.activity_competition_tables, null);
            pages.add(page2);
            //_containerTables = (LinearLayout) page2.findViewById(R.id.containerTablesCompetition);
            _lastTable = (TableLayout) page2.findViewById(R.id.tableLast);
            _currentRound = (TextView) page2.findViewById(R.id.currentRound);
            _timerParticipantTable = (TextView) page2.findViewById(R.id.competitionTimer);
            _table = (RecyclerView) page2.findViewById(R.id.recyclerViewTableCompetition);

            PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
            ViewPager viewPager = new ViewPager(this);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);
            setContentView(viewPager);
        }
        else
        {
            _lastTable = (TableLayout) page1.findViewById(R.id.tableLast);
            _currentRound = (TextView) page1.findViewById(R.id.currentRound);
            _timerParticipantTable = (TextView) page1.findViewById(R.id.competitionTimerTable);
            _table = (RecyclerView) page1.findViewById(R.id.recyclerViewTableCompetition);
            _viewPagerLap = (ViewPager) page1.findViewById(R.id.viewPagerTableCompetition);
            lapsCount = _currentCompetition.getCheckPointsCount();
            _arrayRecycleView = new ArrayList<>();
            _arrayAdapters = new ArrayList<>();
            List<View> ListPages = new ArrayList<>();
            for (int i = 0; i < lapsCount; i++)
            {
                View page = inflater.inflate(R.layout.table_fragment, null);
                _arrayRecycleView.add((RecyclerView)page.findViewById(R.id.tableFinalActivity));
                _arrayAdapters.add(new CompetitionTableAdapter(this, this.getFragmentManager()));
                _arrayRecycleView.get(i).setLayoutManager(new LinearLayoutManager(this));
                _arrayRecycleView.get(i).setItemAnimator(new DefaultItemAnimator());
                _arrayRecycleView.get(i).setAdapter(_arrayAdapters.get(i));
                ListPages.add(page);
            }
            _currentRecyclerView = _arrayRecycleView.get(0);
            PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(ListPages);
            _viewPagerLap.setAdapter(pagerAdapter);
            _viewPagerLap.setCurrentItem(0);
            checkLapNumberThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    while(true)
                    {
                        final int lapNumber = _viewPagerLap.getCurrentItem();
                        if (!_currentRecyclerView.equals(_arrayRecycleView.get(lapNumber)))
                        {
                            _currentRecyclerView = _arrayRecycleView.get(lapNumber);
                            _currentTable = lapNumber;
                            //_currentRecyclerView.getAdapter().notifyDataSetChanged();
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    _currentRound.setText(getResources().getString(R.string.current_round) + " - " + String.valueOf(lapNumber+1));
                                }
                            });
                        }
                        try
                        {
                            Thread.sleep(100);
                        }
                        catch (Exception ex)
                        {

                        }
                    }
                }
            });
            setContentView(page1);
        }

        _timerDialogBuilder = new AlertDialog.Builder(this);
        _timerDialogBuilder.setTitle("Выберите действие для продолжения.");
        _timerDialogBuilder.setPositiveButton(getResources().getString(R.string.finish), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Завершить соревнование
                if (_competitionState == CompetitionState.Running) {
                    _timer.cancel();
                    SaveResultsToDatabase();
                    Intent intent = new Intent(CompetitionsActivity.this, FinalActivity.class);
                    intent.putExtra("Name", _currentCompetition.getName());
                    intent.putExtra("Date", _currentCompetition.getDate());
                    startActivity(intent);
                    CompetitionsActivity.this.finish();
                }
                Toast.makeText(getApplicationContext(), "Завершить соревнование", Toast.LENGTH_SHORT).show();
            }
        });
        _timerDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Отмена", Toast.LENGTH_SHORT).show();
            }
        });
        _timerDialogBuilder.setNeutralButton(getResources().getString(R.string.reset), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (_competitionState == CompetitionState.NotStarted) return;
                if (_countDownTimer != null) _countDownTimer.cancel();
                if (_timer != null) _timer.cancel();
                TimerStartPosition();
                _tableAdapter.ClearList();
                _tableAdapter.notifyDataSetChanged();
                for (int j = 0; j < lapsCount; j++) {
                    _arrayMegaSportsmen[j].clear();
                }

                for(int j = 0; j < _megaSportsmen.length; j++)
                {
                    _megaSportsmen[j].clearFineCount();
                    _megaSportsmen[j].setFineTime(null);
                }
                _lastTable.removeAllViews();
                _viewAdapter.ClearList();
                _competitionState = CompetitionState.NotStarted;
                _number = 0;
                Toast.makeText(getApplicationContext(), "Сброс", Toast.LENGTH_SHORT).show();
            }
        });
        _timerDialogBuilder.setCancelable(false);
        _timerDialog = _timerDialogBuilder.create();

        _dialogFineForm = inflater.inflate(R.layout.dialog_fine_competition_activity, null);
        //_dialogSeekBar = (SeekBar) _dialogFineForm.findViewById(R.id.seek_bar_competition_activity);
        //_dialogText = (TextView) _dialogFineForm.findViewById(R.id.count_fine);
        //_dialogSeekBar.setOnSeekBarChangeListener(this);
        //_dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");
        _fineGridView = (GridView) _dialogFineForm.findViewById(R.id.fineGridView);
        final String[] countFine = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
        _fineAdapter = new FineAdapter(this, _fineGridView, countFine);
        _fineGridView.setAdapter(_fineAdapter);


        _builderFineDialog = new AlertDialog.Builder(CompetitionsActivity.this, R.style.DialogStyle);
        _builderFineDialog.setView(_dialogFineForm);
        _builderFineDialog.setPositiveButton(getResources().getString(R.string.accept), new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int fineCount = _fineAdapter.getCurrentCountFine() - _megaSportsmen[_currentSportsman].getFineCount();//_dialogSeekBar.getProgress();
                int fineSeconds = Integer.valueOf(_currentCompetition.getFineTime().split(":")[1]);
                int fineMinutes = Integer.valueOf(_currentCompetition.getFineTime().split(":")[0]);
                android.text.format.Time fineTime = new android.text.format.Time();
                TextView NumberStr = (TextView) _dialogOwnerView.findViewById(R.id.numberParticipantMyButton);
                TextView LapStr = (TextView) _dialogOwnerView.findViewById(R.id.lapParticipantMyButton);
                int participantNumber = Integer.valueOf(NumberStr.getText().toString());
                int lapNumber = Integer.valueOf(LapStr.getText().toString());

                fineTime.second = fineCount * fineSeconds;
                fineTime.minute = fineCount * fineMinutes;
                fineTime.normalize(false);
                for (int i = 0; i < _megaSportsmen.length; i++) {
                    if (_megaSportsmen[i].getNumber() == participantNumber) {
                        _megaSportsmen[i].setFineCount(fineCount, lapNumber);
                        _megaSportsmen[i].setFineTime(fineTime);
                        AddRowToLastEventTable(_megaSportsmen[i], false);
                        break;
                    }
                }

                //_dialogSeekBar.setProgress(0);
                //_dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");


            }
        });
        _builderFineDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //_dialogSeekBar.setProgress(0);
                //_dialogText.setText(getResources().getString(R.string.dialog_text_fine_competiton) + " 0");
            }
        });

        _fineDialog = _builderFineDialog.create();

        lapsCount = _currentCompetition.getCheckPointsCount();
        MegaSportsman.setLapsCount(lapsCount);
        _arrayMegaSportsmen = new ArrayList[lapsCount];
        for (int i = 0; i < lapsCount; i++) {
            _arrayMegaSportsmen[i] = new ArrayList<MegaSportsman>();
        }
        _currentRound.setText(_currentRound.getText() + " - " + Integer.toString(_currentTable + 1));
        _tableAdapter = new CompetitionTableAdapter(this, this.getFragmentManager());
        if(!getResources().getBoolean(R.bool.isTablet))
        {
            _table.setAdapter(_tableAdapter);
            _table.setItemAnimator(new DefaultItemAnimator());
            _table.setLayoutManager(new LinearLayoutManager(this));
        }
        else
        {
            for(int i = 0; i < lapsCount; i++)
            {
//                _tableAdapter.ClearList();
//                _tableAdapter.AddSportsmen(_arrayMegaSportsmen[i]);
//                _tableAdapter.notifyDataSetChanged();
//                _arrayRecycleView.get(i).setAdapter(_tableAdapter);
            }
        }
        TimerStartPosition();

    }

    @Subscribe
    public void OnSportsmanDelete(SportsmanDeleteEvent sportsmanDeleteEvent)
    {
        int number = sportsmanDeleteEvent.GetSportsmanNumber();
        for(int i = 0; i < _megaSportsmen.length; i++)
        {
            if(number == _megaSportsmen[i].getNumber())
            {
                _tableAdapter.RemoveSportsman(_megaSportsmen[i]);
                _tableAdapter.notifyDataSetChanged();
                _viewAdapter.ChangeSportsmanLap(number, _megaSportsmen[i].getCurrentLap() - 1);
                _viewAdapter.notifyDataSetChanged();
                _megaSportsmen[i].setCurrentLap(_megaSportsmen[i].getCurrentLap() - 1);
                break;
            }
        }

    }

    @Subscribe
    public void OnColorChanged(ChangeColorEvent changeColorEvent)
    {
        int number = changeColorEvent.GetSportsmanNumber();
        int color = changeColorEvent.GetColor();

        for(int i = 0; i < _megaSportsmen.length; i++)
        {
            if(number == _megaSportsmen[i].getNumber())
            {
                _megaSportsmen[i].setColor(color);
                break;
            }
        }

        for(int i = 0; i < _arrayMegaSportsmen.length; i++)
        {
            for(int j = 0; j < _arrayMegaSportsmen[i].size(); j++)
            {
                if(_arrayMegaSportsmen[i].get(j).getNumber() == number)
                {
                    _arrayMegaSportsmen[i].get(j).setColor(color);
                    break;
                }
            }
        }
        _tableAdapter.notifyDataSetChanged();
        _viewAdapter.notifyDataSetChanged();

    }

    private void AddRowAsTableCompetitions(MegaSportsman megaSportsman) {
        final TableRow newRow = new TableRow(this);
        newRow.setWeightSum(100);
        final TextView newTextView = new TextView(this);
        newTextView.setText(String.valueOf(megaSportsman.getNumber()));
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setTextColor(Color.BLACK);
        newTextView.setBackground(new PaintDrawable(Color.WHITE));
        newTextView.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 8f));
        ((TableRow.LayoutParams) newTextView.getLayoutParams()).setMargins(2, 0, 2, 2);
        final TextView newTextView2 = new TextView(this);
        newTextView2.setText(String.valueOf(megaSportsman.getPlace()));
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setTextColor(Color.BLACK);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
        newTextView2.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 8f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView3 = new TextView(this);
        newTextView3.setText(megaSportsman.getName());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setTextColor(Color.BLACK);
        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
        newTextView3.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 30f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView4 = new TextView(this);
        newTextView4.setText("0-0-0-0-0");
        newTextView4.setGravity(Gravity.CENTER);
        newTextView4.setTextColor(Color.BLACK);
        newTextView4.setBackground(new PaintDrawable(Color.WHITE));
        newTextView4.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView4.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 14f));
        ((TableRow.LayoutParams) newTextView4.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView5 = new TextView(this);
        newTextView5.setText(megaSportsman.getResult());
        newTextView5.setGravity(Gravity.CENTER);
        newTextView5.setTextColor(Color.BLACK);
        newTextView5.setBackground(new PaintDrawable(Color.WHITE));
        newTextView5.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView5.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 20f));
        ((TableRow.LayoutParams) newTextView5.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView6 = new TextView(this);
        newTextView6.setText(megaSportsman.getLag());
        newTextView6.setGravity(Gravity.CENTER);
        newTextView6.setTextColor(Color.BLACK);
        newTextView6.setBackground(new PaintDrawable(Color.WHITE));
        newTextView6.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView6.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 20f));
        ((TableRow.LayoutParams) newTextView6.getLayoutParams()).setMargins(0, 0, 2, 2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        newRow.addView(newTextView4);
        newRow.addView(newTextView5);
        newRow.addView(newTextView6);
        _lastTable.addView(newRow);
    }

    private void AddRowZeroColumn(MegaSportsman megaSportsman)
    {
        //int width = _nameParticipant.getMeasuredWidth() + _numberParticipant.getMeasuredHeight() + _positionParticipant.getMeasuredHeight() + _timeParticipant.getMeasuredHeight() + _lagParticipant.getMeasuredHeight();
        final TableRow newRow = new TableRow(this);
        newRow.setWeightSum(100);
        final TextView newTextView = new TextView(this);
        newTextView.setText("Участнику №"+String.valueOf(megaSportsman.getNumber())+" начислен штраф: "+String.valueOf(megaSportsman.getFineCount()));
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setTextColor(Color.BLACK);
        newTextView.setBackground(new PaintDrawable(Color.WHITE));
        newTextView.setTextSize(getResources().getDimension(R.dimen.text_size_last_step));
        newTextView.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,100f));
        ((TableRow.LayoutParams) newTextView.getLayoutParams()).setMargins(2, 0, 2, 2);
        newRow.addView(newTextView);
        _lastTable.addView(newRow);
    }


    private GridView.OnItemLongClickListener gridViewOnItemLongClickListener = new GridView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
        {
            if(_megaSportsmen[position].getFinished()) return true;
            _dialogOwnerView = view;
            _fineAdapter.setCountFine(_megaSportsmen[position].getFineCount());
            _currentSportsman = position;
            _fineAdapter.SetMegaSportsman(_megaSportsmen[position], Integer.valueOf(((TextView)view.findViewById(R.id.lapParticipantMyButton)).getText().toString()));
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
            FrameLayout finishedLayout = (FrameLayout)view.findViewById(R.id.second_layout);
            int number = Integer.valueOf(NumberStr.getText().toString());
            int lap = Integer.valueOf(LapStr.getText().toString());
            int currentSportsmen = 0;
            boolean isFinished = false;
            for(int i = 0; i < _megaSportsmen.length; i ++)
            {
                if (number == _megaSportsmen[i].getNumber())
                {
                    isFinished = _megaSportsmen[i].getFinished();
                    currentSportsmen = i;
                }
            }
            if(!isFinished)
            {
                MegaSportsman localSportsman = null;
                _viewAdapter.ChangeSportsmanLap(number, lap + 1);
                android.text.format.Time newTime = new android.text.format.Time();
                for (int i = 0; i < _megaSportsmen.length; i++) {
                    if (number == _megaSportsmen[i].getNumber())
                    {
                        newTime.hour = _currentTime.hour - _megaSportsmen[i].getStartTime().hour;
                        newTime.minute = _currentTime.minute - _megaSportsmen[i].getStartTime().minute;
                        newTime.second = _currentTime.second - _megaSportsmen[i].getStartTime().second;
                        newTime.normalize(false);
                        localSportsman = new MegaSportsman(_megaSportsmen[i]);
                        // _megaSportsmen[i].setFineTime(null);
                        // _megaSportsmen[i].setFineCount(0);
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
                AddRowToLastEventTable(localSportsman, true);
                if (!getResources().getBoolean(R.bool.isTablet)) {
                    if (_currentTable == lap) {
                        _tableAdapter.ClearList();
                        _tableAdapter.AddSportsmen(_arrayMegaSportsmen[lap]);
                        _tableAdapter.notifyDataSetChanged();
                    }
                } else {
                    //Видимо тут код добавления
                    _arrayAdapters.get(lap).ClearList();
                    _arrayAdapters.get(lap).AddSportsmen(_arrayMegaSportsmen[lap]);
                    _arrayAdapters.get(lap).notifyDataSetChanged();
                }

                if (lap == lapsCount - 1) {
                    //_viewAdapter.ChangeSportsmanLap(number, 0);
                    //_viewAdapter.RemoveSportsman(number);
                    _megaSportsmen[currentSportsmen].setFinished(true);
                    finishedLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(_isFirstLoad)
        {
            LoadingTask loadingTask = new LoadingTask();
            loadingTask.execute();
            _isFirstLoad = false;
        }
    }

    private void AddRowToLastEventTable(MegaSportsman megaSportsman, boolean isFromButtonClick)
    {
        if(_lastTable.getChildCount() >= 3)
        {
            _lastTable.removeViewAt(0);
        }
        if(isFromButtonClick)
        {
            AddRowAsTableCompetitions(megaSportsman);
        }
        else
        {
            AddRowZeroColumn(megaSportsman);
        }
    }

    private void TimerStartPosition()
    {
        if(_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_mas_start)))
        {
            _currentInterval.second = 0;
            _currentInterval.minute = 0;
        }
        else
        {
            _currentInterval.second = Integer.valueOf(_currentCompetition.getInterval().split(":")[1]);
            _currentInterval.minute = Integer.valueOf(_currentCompetition.getInterval().split(":")[0]);
        }


        _timeNextParticipant.second = _currentInterval.second;
        _timeNextParticipant.minute = _currentInterval.minute;

        _competitionTimer.setTextColor(getResources().getColor(R.color.timerStart));
        _timerParticipantTable.setTextColor(getResources().getColor(R.color.timerStart));
        _competitionTimer.setText(_currentCompetition.getTimeToStart());
        _timerParticipantTable.setText(_currentCompetition.getTimeToStart());
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

    Handler handler;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run()
        {
            while(true) {
                if (android.text.format.Time.compare(_currentTime, _timeNextParticipant) == 0 && _number < _megaSportsmen.length) {

                    if (!_currentCompetition.getSecondInterval().equals("")) {
                        if (_number == Integer.valueOf(_currentCompetition.getNumberSecondInterval()) - 1) {
                            _currentInterval.second = Integer.valueOf(_currentCompetition.getSecondInterval().split(":")[1]);
                            _currentInterval.minute = Integer.valueOf(_currentCompetition.getSecondInterval().split(":")[0]);
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_single_start))) {
                                //_participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                _megaSportsmen[_number].setStartTime(_currentTime);
                                _viewAdapter.AddSportsman(_megaSportsmen[_number]);
                                _number++;
                            } else {
                                if (_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_double_start))) {
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
            timeCountDown.minute = Integer.valueOf(_currentCompetition.getTimeToStart().split(":")[0]);
            timeCountDown.second = Integer.valueOf(_currentCompetition.getTimeToStart().split(":")[1]);
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
                    if(!_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_mas_start)))
                    {
                        if(!thread.isAlive()) thread.start();
                    }
                    _competitionTimer.setTextColor(getResources().getColor(R.color.white));
                    _timerParticipantTable.setTextColor(getResources().getColor(R.color.white));
                    _competitionState = CompetitionState.Running;
                    if(_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_mas_start)))
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
            RealmMegaSportsmanSaver megaSportsmanSaver = new RealmMegaSportsmanSaver(this, "LAP"+i+_currentCompetition.getNameDateString());
            megaSportsmanSaver.DeleteTable();
            megaSportsmanSaver = new RealmMegaSportsmanSaver(this, "LAP"+i+_currentCompetition.getNameDateString());
            megaSportsmanSaver.SaveSportsmen(_arrayMegaSportsmen[i]);
        }
        RealmCompetitionSaver compSaver = new RealmCompetitionSaver(this, "COMPETITIONS");
        compSaver.DeleteCompetition(_currentCompetition);
        _currentCompetition.setFinished(true);
        compSaver.SaveCompetition(_currentCompetition);
        compSaver.Dispose();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.warning_dialog_title));
            dialog.setMessage(getResources().getString(R.string.message_dialog_warning));
            dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (_countDownTimer != null) _countDownTimer.cancel();
                    if (_timer != null) _timer.cancel();
                    Intent intent = new Intent(CompetitionsActivity.this, ViewPagerActivity.class);
                    intent.putExtra("CompetitionName", _currentCompetition.getName());
                    intent.putExtra("CompetitionDate", _currentCompetition.getDate());
                    startActivity(intent);
                    CompetitionsActivity.this.finish();
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setCancelable(false);
            dialog.show();
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
        _timerDialog.show();
        Toast.makeText(getApplicationContext(),"Стоп",Toast.LENGTH_SHORT).show();
    }

    class MyTimerTask extends AsyncTask<Integer, Integer, Integer>
    {

        @Override
        protected Integer doInBackground(Integer... params)
        {
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
        protected void onPostExecute(Integer integer)
        {
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
            RealmSportsmenSaver saver = new RealmSportsmenSaver(getApplicationContext(), _currentCompetition.getDbParticipantPath());
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
        protected void onPreExecute()
        {
            super.onPreExecute();
            dialog = new ProgressDialog(CompetitionsActivity.this);
            dialog.setMessage("Подождите. Идет загрузка...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.setMax(_currentCompetition.getMaxParticipantCount());
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            if(checkLapNumberThread != null) checkLapNumberThread.start();
            dialog.dismiss();
        }
    }
}
