package com.esd.esd.biathlontimer.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.SettingsChangedEvent;
import com.esd.esd.biathlontimer.SettingsFragment;
import com.esd.esd.biathlontimer.Sportsman;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends PreferenceActivity
{
    private boolean isEditMode = false;
    private Intent _localIntent;
    private boolean isFirstLoad = true;

    private EventBus _eventBus;

    private Competition _oldCompetititon;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + "Настройки соревнования" + "</big>" + "</font>")));

        _localIntent = getIntent();
        isEditMode = Boolean.valueOf(_localIntent.getStringExtra("isEditMode"));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
            onCreatePreferenceActivity();
        }
        else
        {
            onCreatePreferenceFragment();
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        _eventBus = EventBus.getDefault();
    }


    /**
     * code for Android < 3 (i.e. API lvl < 11)
     */
    @SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity()
    {
        addPreferencesFromResource(R.xml.activity_setting);
    }

    /**
     * code for Android >= 3 (i.e. API lvl >= 11)
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onCreatePreferenceFragment()
    {
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(isEditMode && isFirstLoad)
        {
            String name = _localIntent.getStringExtra("Name");
            String date = _localIntent.getStringExtra("Date");
            RealmCompetitionSaver saver = new RealmCompetitionSaver(this, "COMPETITIONS");
            _oldCompetititon = saver.GetCompetition(name, date);
            saver.Dispose();
            SettingsFragment.SetAllSummaries(this, _oldCompetititon);
            isFirstLoad = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.setting_action_bar, menu);
        menu.getItem(menu.size() - 1).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.accept_setting)
        {
            RealmCompetitionSaver saver = new RealmCompetitionSaver(this, "COMPETITIONS");
            List<Competition> localList = saver.GetAllCompetitions(true);
            Competition newComp = SettingsFragment.GetCurrentCompetition(this);
            if(newComp == null)
            {
                Toast.makeText(this,getResources().getString(R.string.wrong_settings_toast), Toast.LENGTH_LONG).show();
                return true;
            }
            boolean _canAddCompetition = true;
            if(!isEditMode)
            {
                for (int i = 0; i < localList.size(); i++) {
                    if (newComp.equals(localList.get(i))) {
                        _canAddCompetition = false;
                        Toast.makeText(this, getResources().getString(R.string.competition_already_exists), Toast.LENGTH_LONG).show();
                        break;
                    }
                }
            }
            if(_canAddCompetition)
            {
                if(!isEditMode)
                {
                    saver.SaveCompetition(newComp);
                    Intent myIntent = SettingsFragment.GetIntent(this);
                    startActivity(myIntent);
                }
                else
                {
                    // Проверяем количество участников
                    RealmSportsmenSaver realmSaver = new RealmSportsmenSaver(this, _oldCompetititon.getDbParticipantPath());
                    List<Sportsman> sportsmenList = realmSaver.GetSportsmen("number", true);
                    realmSaver.DeleteTable();
                    saver.DeleteCompetition(_oldCompetititon);
                    saver.SaveCompetition(newComp);
                    realmSaver = new RealmSportsmenSaver(this, newComp.getDbParticipantPath());
                    realmSaver.SaveSportsmen(sportsmenList);
                    realmSaver.Dispose();
                    saver.Dispose();
//                    SettingsChangedEvent event = new SettingsChangedEvent();
//                    _eventBus.post(event);
                    this.finish();
                    Intent mainActInt = new Intent(this, MainActivity.class);
                    startActivity(mainActInt);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
