package com.esd.esd.biathlontimer.Activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.esd.esd.biathlontimer.Adapters.CompetitionTableAdapter;
import com.esd.esd.biathlontimer.Adapters.GridViewAdapter;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class TestService extends Service {
    private static Timer _timer;
    private Competition _currentCompetition;
    private android.text.format.Time _timeNextParticipant;
    private android.text.format.Time _currentInterval;
    private static android.text.format.Time _currentTime;
    private CountDownTimer _countDownTimer;
    private MegaSportsman[] _megaSportsmen;
    private Intent _myIntent;

    private static String SingleStart;
    private static String PairStart;
    private static CompetitionsActivity.CompetitionState _currentState;
    private static ArrayList<MegaSportsman>[] _arrayMegaSportsmen;

    private static int _number;
    private static GridViewAdapter _viewAdapter;
    private static CompetitionTableAdapter _tableAdapter;

    private static ArrayList<String> _lastStep;
    private static List<MegaSportsman> _megaSportsmanCurrentList;
    private static Context _context;

     static int ms;

    private static Notification.Builder builder;
    private static NotificationManager notificationManager;

    @Override
    public void onCreate()
    {
        super.onCreate();
        _lastStep = new ArrayList<>();
        _arrayMegaSportsmen = null;
        _megaSportsmen = CompetitionsActivity.GetMegaSportsmanArray();
        threadFlag = false;
        thread.start();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.launcher).setContentTitle(getApplicationContext().getResources().getString(R.string.app_name));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, CompetitionsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

    }

    Handler handler;
    private static boolean threadFlag;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run()
        {
            while(true)
            {
                if(thread.isInterrupted()) break;
                if(!threadFlag) continue;
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
                            if (_currentCompetition.getStartType().equals(SingleStart)) {
                                //_participantGridLayout.addView(CreateButton(_megaSportsmen[_number], "0"));
                                _megaSportsmen[_number].setStartTime(_currentTime);
                                _viewAdapter.AddSportsman(_megaSportsmen[_number]);
                                _number++;
                            } else {
                                if (_currentCompetition.getStartType().equals(PairStart)) {
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(_myIntent == null) _myIntent = intent;
        _tableAdapter = CompetitionsActivity.GetCompetitionTableAdapter();
        RealmCompetitionSaver compSaver = new RealmCompetitionSaver(this, "COMPETITIONS");
        _currentCompetition = compSaver.GetCompetition(intent.getStringExtra("CompetitionName"), intent.getStringExtra("CompetitionDate"));
        SingleStart = getResources().getString(R.string.item_type_single_start);
        PairStart = getResources().getString(R.string.item_type_double_start);
        _currentInterval = new Time();
        _currentInterval.second = Integer.valueOf(_currentCompetition.getInterval().split(":")[1]);
        _currentInterval.minute = Integer.valueOf(_currentCompetition.getInterval().split(":")[0]);
        handler = new Handler();
        final android.text.format.Time timeCountDown = new android.text.format.Time();
        timeCountDown.minute = Integer.valueOf(_currentCompetition.getTimeToStart().split(":")[0]);
        timeCountDown.second = Integer.valueOf(_currentCompetition.getTimeToStart().split(":")[1]);
        final long ms1 = timeCountDown.minute*60000+timeCountDown.second*1000;

        _timeNextParticipant = new Time(_currentInterval);
        _currentTime = new android.text.format.Time();
        _currentTime.second = 0;
        _currentTime.minute = 0;
        _countDownTimer = new CountDownTimer(ms1+1000,1000)
        {
            TimerTask task = new TimerTask()
            {
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
                CompetitionsActivity.SetTime(timeCountDown, ms, true);
            }

            @Override
            public void onFinish()
            {
                _timer = new Timer();
                _timer.schedule(task, 0,100);
                if(!_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_mas_start)))
                {
                    threadFlag = true;
                }
                CompetitionsActivity.SetTime(_currentTime, ms, false);
                CompetitionsActivity.SetCompetitionState(CompetitionsActivity.CompetitionState.Running);
                CompetitionsActivity.SetColorTimer();
                if(_currentCompetition.getStartType().equals(getResources().getString(R.string.item_type_mas_start)))
                {
                    for(int i = 0; i < _megaSportsmen.length; i++)
                    {
                        _megaSportsmen[i].setStartTime(new android.text.format.Time());
                    }
                    _viewAdapter = new GridViewAdapter(TestService.this, new ArrayList<MegaSportsman>(Arrays.asList(_megaSportsmen)));
                }
                else
                {

                }
                _viewAdapter = new GridViewAdapter(TestService.this, new ArrayList<MegaSportsman>());
                CompetitionsActivity.SetAdapterToGridView(_viewAdapter);

            }
        };
        _countDownTimer.start();
        CompetitionsActivity.SetCompetitionState(CompetitionsActivity.CompetitionState.Started);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        if(_timer != null) _timer.cancel();
        if(_countDownTimer != null) _countDownTimer.cancel();
        if(_viewAdapter != null) _viewAdapter.ClearList();
        ms = 0;
        _number = 0;
        _currentTime = new Time();
        thread.interrupt();
        notificationManager.cancelAll();
        CompetitionsActivity.SetTime(_currentTime, ms, true);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        if(_timer != null) _timer.cancel();
        _tableAdapter.ClearList();
        _viewAdapter.ClearList();
    }

    public static void SetAdapterTableCompetition(CompetitionTableAdapter adapter)
    {
        _tableAdapter = adapter;
        if(_megaSportsmanCurrentList!=null)
        {
            _tableAdapter.AddSportsmen(_megaSportsmanCurrentList);
        }
    }

    public static void SetLastStep(TableLayout table)
    {
        if(_lastStep == null) return;
        TableRow row;
        String resultString = "";
        _lastStep.clear();
        for(int i = 0; i < table.getChildCount(); i++)
        {
            row = (TableRow)table.getChildAt(i);
            for(int j = 0; j < row.getChildCount(); j++)
            {
                    resultString += ((TextView) row.getChildAt(j)).getText().toString();
                    resultString += ';';
            }
            _lastStep.add(resultString);
            resultString = "";
        }

    }

    public static void SetContext(Context context)
    {
        _context = context;
    }

    public static Context GetContext()
    {
        return _context;
    }
    public static void SaveCurrentListMegaSportsman(List<MegaSportsman> megaSportsmanList)
    {
        _megaSportsmanCurrentList = megaSportsmanList;
    }

    public static void ResetAllStaticParameters()
    {
        _timer = null;

        _currentTime = null;

        threadFlag = false;
        SingleStart = null;
        PairStart = null;
        _currentState = null;
        _arrayMegaSportsmen = null;

        _number = 0;
        _viewAdapter = null;
        _tableAdapter = null;

        _lastStep = null;
        _megaSportsmanCurrentList = null;
        _context = null;
        ms = 0;

    }

    public static ArrayList<String[]> GetRowFromLastTable()
    {
        String[] localString;
        ArrayList<String[]> _rows = new ArrayList<>();
        for(int i = 0; i < _lastStep.size(); i++ )
        {
            localString = ((String)_lastStep.get(i)).split(";");
            _rows.add(localString);
        }
        return _rows;
    }

    public static void SetArrayMegaSportsman(ArrayList<MegaSportsman>[] arrayMegaSportsmen)
    {
        _arrayMegaSportsmen = arrayMegaSportsmen;
    }

    public static ArrayList<MegaSportsman>[] GetArrayMegaSportsman()
    {
        return _arrayMegaSportsmen;
    }

    public static void SetCurrentState(CompetitionsActivity.CompetitionState state)
    {
        _currentState = state;
    }


    public static CompetitionsActivity.CompetitionState GetCurrentState()
    {
        return _currentState;
    }

    public static void ResetService()
    {
        if(_timer != null) _timer.cancel();
        threadFlag = false;
        if(_viewAdapter != null) _viewAdapter.ClearList();
        ms = 0;
        _number = 0;
        _tableAdapter.ClearList();
        _currentTime = new Time();
        _currentState = CompetitionsActivity.CompetitionState.NotStarted;
        if(builder != null)
        {
            builder.setContentText("00:00:00");
            if (notificationManager != null) notificationManager.notify(1, builder.build());
        }
        CompetitionsActivity.SetCompetitionState(_currentState);
        CompetitionsActivity.SetTime(_currentTime, ms,true);
    }

    public static boolean IsFinished(int number)
    {
        return _viewAdapter.getFinished(number);
    }

    public static void NotifyViewAdapter()
    {
        _viewAdapter.notifyDataSetChanged();
    }

    public static void ChangeSportsmanLap(int number, int lap)
    {
        _viewAdapter.ChangeSportsmanLap(number, lap);
    }

    public static void SetFinish(int number, boolean finish)
    {
        _viewAdapter.setIsFinished(number, finish);
    }

    public static Time GetCurrentTime()
    {
        return _currentTime;
    }

    public static void AdapterClearList()
    {
        _viewAdapter.ClearList();
    }

    public static CompetitionTableAdapter GetCompetitionTableAdapter()
    {
        return _tableAdapter;
    }

    public static void AddSportsmanToCompetitionAdapter(List<MegaSportsman> megaSportsmanList)
    {
        _tableAdapter.ClearList();
        if(megaSportsmanList != null)
        {
            _tableAdapter.AddSportsmen(megaSportsmanList);
        }
        _tableAdapter.notifyDataSetChanged();
    }

    public static void RemoveSportsman(MegaSportsman megaSportsman)
    {
            _tableAdapter.RemoveSportsman(megaSportsman);
            _tableAdapter.ChangePlacesAfterDelete();
    }

    class MyTimerTask extends AsyncTask<Integer, Integer, Integer>
    {
        @Override
        protected Integer doInBackground(Integer... params)
        {
            if(!CompetitionsActivity.IsPaused()) ms++;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(CompetitionsActivity.GetAdapterFromGridView() == null) CompetitionsActivity.SetAdapterToGridView(_viewAdapter);
                    if(CompetitionsActivity.GetIntentService() == null) CompetitionsActivity.SetIntentService(_myIntent);
                    if(CompetitionsActivity.GetAdapterFromCompetitionTable() == null) CompetitionsActivity.SetAdapterToTableCompetition(_tableAdapter);
                }
            });
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
            builder.setContentText(_currentTime.format("%H:%M:%S"));
            notificationManager.notify(1, builder.build());
            CompetitionsActivity.SetTime(_currentTime, ms, false);
        }
    }
}

