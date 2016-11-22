package com.esd.esd.biathlontimer.Activities;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.SettingsSaver;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.SettingsFragment;


public class SettingsActivity extends PreferenceActivity
{
    private boolean isEditMode = false;
    private Intent _localIntent;
    private boolean isFirstLoad = true;
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
            Competition localComp = new Competition(name, date, this);
            localComp.GetAllSettingsToComp();
            SettingsFragment.SetAllSummaries(this, name, date, localComp.GetInterval(), localComp.GetStartType(), localComp.GetGroups(),
                    localComp.GetCheckPointsCount(), localComp.GetTimeToStart());
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
            CompetitionSaver saver = new CompetitionSaver(this);
            Competition[] localArr = saver.GetAllCompetitions(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_NAME);
            Competition newComp = SettingsFragment.GetCurrentCompetition(this);
            if(newComp == null)
            {
                Toast.makeText(this,"Заполните все настройки", Toast.LENGTH_LONG).show();
                return true;
            }
            boolean _canAddCompetition = true;
            for(int i =0;i < localArr.length; i++)
            {
                if(newComp.equals(localArr[i]))
                {
                    _canAddCompetition= false;
                    Toast.makeText(this,getResources().getString(R.string.competition_already_exists),Toast.LENGTH_LONG).show();
                    break;
                }
            }

            if(_canAddCompetition)
            {
                if(!isEditMode) {
                    Intent myIntent = SettingsFragment.GetIntent(this);
                    startActivity(myIntent);
                }
                else
                {
                    // все пересохраняем и капец
                    this.finish();
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
