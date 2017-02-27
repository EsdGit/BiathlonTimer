package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.PaintDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Adapters.CompetitionAdapter;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.SettingsChangedEvent;
import com.esd.esd.biathlontimer.Sportsman;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

   private EventBus _eventBus;

    private TextView _nameTextView;
    private TextView _dateTextView;
    private TextView _emptyText;
    private TableLayout _tableLayout;
    private LinearLayout _headTableLayout;
    private static MenuItem _editMenuItem;
    private static MenuItem _deleteMenuItem;
    private static MenuItem _sortNameMenuItem;
    private static MenuItem _sortDataMenuItem;
    private int _counterMarkedCompetition;

    private RecyclerView _recyclerView;

    private boolean _haveMarkedCompetition = false;
    private boolean _isFirstLoad = true;


    private static CompetitionAdapter competitionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.main_activity_head) + "</big>" + "</font>")));
//        _tableLayout = (TableLayout)findViewById(R.id.table);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);
        _emptyText = (TextView) findViewById(R.id.emptyListTextView);
        _headTableLayout = (LinearLayout) findViewById(R.id.headTable);

        RealmCompetitionSaver saver = new RealmCompetitionSaver(this, "COMPETITIONS");
        competitionAdapter = new CompetitionAdapter(this, saver.GetAllCompetitions(true));
        _recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        _recyclerView.setAdapter(competitionAdapter);
        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        _eventBus = EventBus.getDefault();
//        _eventBus.register(this);
        saver.Dispose();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
       EmptyListCompetition();
    }

//    @Subscribe
//    public void SettinsChanged(final SettingsChangedEvent event)
//    {
//        _tableLayout.removeAllViews();
//        Competition[] localArr = _saver.GetAllCompetitions(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE);
//        _competitions = new Competition[localArr.length];
//        for (int i = 0; i < localArr.length; i++)
//        {
//            _competitions[i] = localArr[i];
//            AddCompetitionRow(localArr[i]);
//        }
//    }



    public void OnClick(View view)
    {
        Intent settingPager = new Intent(this, SettingsActivity.class);
        settingPager.putExtra("isEditMode", "false");
        startActivity(settingPager);
        this.finish();

//        Intent finalPage = new Intent(this, FinalActivity.class);
//        startActivity(finalPage);
    }

    public void OnClickDeleteCompetition()
    {
        List<Competition> localList = competitionAdapter.GetCheckedCompetitions();
        RealmSportsmenSaver realmSaver;
        RealmCompetitionSaver saver = new RealmCompetitionSaver(this, "COMPETITIONS");
        for(int i = 0; i<localList.size(); i++)
        {
            saver.DeleteCompetition(localList.get(i));
            competitionAdapter.RemoveCompetition(localList.get(i));
            realmSaver = new RealmSportsmenSaver(this, localList.get(i).getDbParticipantPath());
            realmSaver.DeleteTable();
        }
        EmptyListCompetition();
    }

    public void OnClickEditCompetition()
    {
        List<Competition> localList = competitionAdapter.GetCheckedCompetitions();
        if(localList.get(0).isFinished())
        {
            Toast.makeText(this, "Нельзя редактировать завершённое соревнование", Toast.LENGTH_LONG).show();
        }
        else
        {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("isEditMode", "true");
            intent.putExtra("Name", localList.get(0).getName());
            intent.putExtra("Date", localList.get(0).getDate());
            startActivity(intent);
            this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main_action_bar, menu);
        for(int i = 0; i<menu.size(); i++)
        {
            if( menu.getItem(i).getIcon() != null)
                menu.getItem(i).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        }
        _deleteMenuItem = (MenuItem) menu.findItem(R.id.mainDeleteCompetition);
        _editMenuItem = (MenuItem) menu.findItem(R.id.mainEditCompetition);
        _sortDataMenuItem = (MenuItem) menu.findItem(R.id.mainMenuDataSort);
        _sortNameMenuItem = (MenuItem) menu.findItem(R.id.mainMenuNameSort);
        return super.onCreateOptionsMenu(menu);
    }

    public void SortByName(boolean isChecked)
    {
        RealmCompetitionSaver saver = new RealmCompetitionSaver(this, "COMPETITIONS");
        List<Competition> list = saver.GetAllCompetitions(isChecked);
        saver.Dispose();
        competitionAdapter.SortBy(list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        item.setChecked(!item.isChecked());
        switch (item.getItemId())
        {
            case R.id.mainMenuNameSort:
                SortByName(item.isChecked());
                Toast.makeText(getApplicationContext(),"Сортировка по названию", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mainMenuDataSort:
                competitionAdapter.SortByDate();
                Toast.makeText(getApplicationContext(),"Сортировка по дате", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mainEditCompetition:
                OnClickEditCompetition();
                SetStartPosition();
                break;
            case R.id.mainDeleteCompetition:
                OnClickDeleteCompetition();
                SetStartPosition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public static void SetStartPosition()
    {
        _sortNameMenuItem.setVisible(true);
        _sortDataMenuItem.setVisible(true);
        _deleteMenuItem.setVisible(false);
        _editMenuItem.setVisible(false);
        competitionAdapter.ResetHaveMarkedFlag();
    }

    public static void SetEditPosition()
    {
        _sortNameMenuItem.setVisible(false);
        _sortDataMenuItem.setVisible(false);
        _deleteMenuItem.setVisible(true);
        _editMenuItem.setVisible(true);
    }

    public static void SetDelPosition()
    {
        _sortNameMenuItem.setVisible(false);
        _sortDataMenuItem.setVisible(false);
        _deleteMenuItem.setVisible(true);
        _editMenuItem.setVisible(false);
    }

    private void EmptyListCompetition()
    {
        if(competitionAdapter.getItemCount() == 0)
        {
            _emptyText.setVisibility(View.VISIBLE);
            _headTableLayout.setVisibility(View.GONE);
        }
        else
        {
            _emptyText.setVisibility(View.GONE);
            _headTableLayout.setVisibility(View.VISIBLE);
        }
    }

}