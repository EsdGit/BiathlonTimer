package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.ExcelHelper;
import com.esd.esd.biathlontimer.FinalActivityAdapter;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;

import org.apache.poi.util.IntegerField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class FinalActivity extends AppCompatActivity
{
    private RecyclerView _resultTable;

    private MenuItem _placeSort;
    private MenuItem _nameSort;
    private MenuItem _send;
    private MenuItem _sendByWhatsApp;
    private MenuItem _sendByMail;

    private int _test = 0;
    private int lapsCount;
    private Competition _currentCompetition;
    private List<MegaSportsman>[] _arrayMegaSportsman;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.title_final_activity) + "</big>" + "</font>")));

        _resultTable = (RecyclerView) findViewById(R.id.tableFinalActivity);

        CompetitionSaver saver = new CompetitionSaver(this);
        _currentCompetition = new Competition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"), this);
        _currentCompetition.GetAllSettingsToComp();
        lapsCount = Integer.valueOf(_currentCompetition.GetCheckPointsCount());
        _arrayMegaSportsman = new ArrayList[lapsCount];
        //ExcelHelper excelHelper = new ExcelHelper();
        //excelHelper.CreateFileWithResult(5);
        LoadData loading = new LoadData();
        loading.execute();
    }

//    private List<MegaSportsman> SortByPlace(List<MegaSportsman> sportsmen)
//    {
//        MegaSportsman helperSportsman;
//        MegaSportsman[] localArr = sportsmen.toArray(new MegaSportsman[sportsmen.size()]);
//        int k = 0;
//        while(k<localArr.length-1) {
//            for (int i = 0; i < localArr.length -k- 1; i++) {
//                if (Integer.valueOf(localArr[i].getPlaceArr()[0]) > Integer.valueOf(localArr[i + 1].getPlaceArr()[0])) {
//                    helperSportsman = localArr[i];
//                    localArr[i] = localArr[i + 1];
//                    localArr[i + 1] = helperSportsman;
//                }
//            }
//            k++;
//        }
//        return new ArrayList<MegaSportsman>(Arrays.asList(localArr));
//
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        for(int i = 0; i<list.size(); i++)
//        {
//            AddResultRow(list.get(i), 0);
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.final_action_bar, menu);

        _placeSort = (MenuItem) menu.findItem(R.id.action_bar_final_activity_place_sort);
        _nameSort = (MenuItem) menu.findItem(R.id.action_bar_final_activity_name_sort);
        _send = (MenuItem) menu.findItem(R.id.action_bar_final_activity_send);
        _sendByWhatsApp = (MenuItem) menu.findItem(R.id.action_bar_final_activity_send_by_whatsapp);
        _sendByMail = (MenuItem) menu.findItem(R.id.action_bar_final_activity_send_by_mail);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_bar_final_activity_place_sort:
                Toast.makeText(getApplicationContext(),"Сортировка по месту",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_name_sort:
                Toast.makeText(getApplicationContext(),"Сортировка по имени",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send:
                Toast.makeText(getApplicationContext(),"Отправка",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send_by_whatsapp:
                Toast.makeText(getApplicationContext(),"Отправка через WhatsApp",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send_by_mail:
                Toast.makeText(getApplicationContext(),"Отправка через Mail",Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    class LoadData extends AsyncTask<Void, Void, Void>
    {
        FinalActivityAdapter adapter;
        @Override
        protected Void doInBackground(Void... params) {
            for(int i = 0; i < lapsCount; i++)
            {
                RealmMegaSportsmanSaver saver = new RealmMegaSportsmanSaver(getApplicationContext(), "LAP"+i+_currentCompetition.GetNameDateString());
                _arrayMegaSportsman[i] = saver.GetSportsmen("_place",true);
            }
            adapter = new FinalActivityAdapter(_arrayMegaSportsman[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _resultTable.setAdapter(adapter);
            _resultTable.setItemAnimator(new DefaultItemAnimator());
            _resultTable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }
}

