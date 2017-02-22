package com.esd.esd.biathlontimer.Activities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.graphics.ColorUtils;
import android.text.format.Time;

import com.esd.esd.biathlontimer.Adapters.GridViewAdapter;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;

import java.util.ArrayList;
import java.util.Arrays;
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

    private static int _number;
    private static GridViewAdapter _viewAdapter;

     static int ms;

    @Override
    public void onCreate() {
        super.onCreate();
        _megaSportsmen = CompetitionsActivity.GetMegaSportsmanArray();
        threadFlag = false;
        thread.start();
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher);
        Notification notification;
        if (Build.VERSION.SDK_INT < 16)
            notification = builder.getNotification();
        else
            notification = builder.build();
        startForeground(777, notification);

    }

    Handler handler;
    private static boolean threadFlag;
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run()
        {
            while(true)
            {
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
                CompetitionsActivity.SetTime(timeCountDown, ms);
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
                CompetitionsActivity.SetTime(_currentTime, ms);
                CompetitionsActivity.SetCompetitionState(CompetitionsActivity.CompetitionState.Running);
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
        _viewAdapter.ClearList();
        ms = 0;
        _number = 0;
        _currentTime = new Time();
        CompetitionsActivity.SetTime(_currentTime, ms);
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent)
    {
        super.onTaskRemoved(rootIntent);
        if(_timer != null) _timer.cancel();
        if(Build.VERSION.SDK_INT == 19)
        {
            Intent restartIntent = new Intent(this, getClass());
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(this, 1, restartIntent,
                    PendingIntent.FLAG_ONE_SHOT);
            //restartIntent.putExtra("RESTART");
            am.setExact(AlarmManager.RTC, System.currentTimeMillis() + 3000, pi);
            startService(restartIntent);
        }
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
        _viewAdapter.ClearList();
        ms = 0;
        _number = 0;
        _currentTime = new Time();
        CompetitionsActivity.SetTime(_currentTime, ms);
    }

    public static boolean IsFinished(int number)
    {
        return _viewAdapter.getFinished(number);
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
            CompetitionsActivity.SetTime(_currentTime, ms);
        }
    }
}

