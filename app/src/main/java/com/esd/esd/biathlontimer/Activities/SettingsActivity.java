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

import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.SettingsFragment;


public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar actionBar;
        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        actionBar.setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + "Настройки соревнования" + "</big>" + "</font>")));

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
            Intent viewPager = new Intent(this, ViewPagerActivity.class);
            startActivity(viewPager);
            Toast.makeText(getApplicationContext(),"Работает",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
