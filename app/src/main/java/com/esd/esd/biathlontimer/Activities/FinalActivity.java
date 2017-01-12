package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.ExcelHelper;
import com.esd.esd.biathlontimer.FinalActivityAdapter;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FinalActivity extends AppCompatActivity {
    private RecyclerView _resultTable;
    private ViewPager _containerTable;
    private ArrayList<RecyclerView> _arrayRecycleView;
    private PagerAdapter _pageAdapter;

    private MenuItem _placeSort;
    private MenuItem _nameSort;
    private MenuItem _send;
    private MenuItem _sendByWhatsApp;
    private MenuItem _sendByMail;

    private int _test = 0;
    private int lapsCount;
    private float fromPosition;
    private Competition _currentCompetition;
    private List<MegaSportsman>[] _arrayMegaSportsman;

    private static final float MOVE_LENGTH = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.title_final_activity) + "</big>" + "</font>")));

        //_resultTable = (RecyclerView) findViewById(R.id.tableFinalActivity);
        _containerTable = (ViewPager) findViewById(R.id.tableContainer);

        CompetitionSaver saver = new CompetitionSaver(this);
        _currentCompetition = new Competition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"), this);
        _currentCompetition.GetAllSettingsToComp();
        lapsCount = Integer.valueOf(_currentCompetition.GetCheckPointsCount());
        _arrayMegaSportsman = new ArrayList[lapsCount];

        LayoutInflater inflater = LayoutInflater.from(this);
        _arrayRecycleView = new ArrayList<>();
        List<View> pages = new ArrayList<>();
        for (int i = 0; i < lapsCount; i++)
        {
            View page = inflater.inflate(R.layout.table_fragment, null);
            _arrayRecycleView.add((RecyclerView)page.findViewById(R.id.tableFinalActivity));
            pages.add(page);
        }
        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        _containerTable.setAdapter(pagerAdapter);
        _containerTable.setCurrentItem(0);
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
                ExcelHelper excelHelper = new ExcelHelper();
                excelHelper.CreateFileWithResult(_arrayMegaSportsman, _currentCompetition.GetNameDateString());

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/*");
                File file = new File(Environment.getExternalStorageDirectory().getPath() +"/" + _currentCompetition.GetName()+" Результат" + ".xls");
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(intent, "Выбор"));

                Toast.makeText(getApplicationContext(),"Отправка через WhatsApp",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send_by_mail:
                ExcelHelper excelHelper1 = new ExcelHelper();
                excelHelper1.CreateFileWithResult(_arrayMegaSportsman, _currentCompetition.GetNameDateString());

                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("application/*");
                File file1 = new File(Environment.getExternalStorageDirectory().getPath() +"/" + _currentCompetition.GetName()+" Результат.xls");
                intent1.setPackage("com.google.android.gm");
                intent1.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file1));
                startActivity(Intent.createChooser(intent1, "Выбор"));
                Toast.makeText(getApplicationContext(),"Отправка через Mail",Toast.LENGTH_SHORT).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    class LoadData extends AsyncTask<Void, Void, Void>
    {
        ArrayList<FinalActivityAdapter> adapter;
        @Override
        protected Void doInBackground(Void... params)
        {
            adapter = new ArrayList<>();
            for(int i = 0; i < lapsCount; i++)
            {
                RealmMegaSportsmanSaver saver = new RealmMegaSportsmanSaver(getApplicationContext(), "LAP"+i+_currentCompetition.GetNameDateString());
                _arrayMegaSportsman[i] = saver.GetSportsmen("_place",true);
                adapter.add(new FinalActivityAdapter(_arrayMegaSportsman[i]));
            }
            //adapter = new FinalActivityAdapter(_arrayMegaSportsman[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for(int i = 0; i < adapter.size(); i++)
            {
                _arrayRecycleView.get(i).setAdapter(adapter.get(i));
                _arrayRecycleView.get(i).setItemAnimator(new DefaultItemAnimator());
                _arrayRecycleView.get(i).setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }
//            _resultTable.setAdapter(adapter);
//            _resultTable.setItemAnimator(new DefaultItemAnimator());
//            _resultTable.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }


}

