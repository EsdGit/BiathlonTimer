package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
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

    private TextView _place;
    private TextView _number;
    private TextView _name;
    private TextView _time;

    private MenuItem _placeSort;
    private MenuItem _nameSort;
    private MenuItem _send;
    private MenuItem _sendByWhatsApp;
    private MenuItem _sendByMail;

    private int _test = 0;
    List<MegaSportsman> list;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.title_final_activity) + "</big>" + "</font>")));

        _resultTable = (RecyclerView) findViewById(R.id.tableFinalActivity);
//        _place = (TextView) findViewById(R.id.place_final_activity);
//        _number = (TextView) findViewById(R.id.number_final_activity);
//        _name = (TextView) findViewById(R.id.name_final_activity);
//        _time = (TextView) findViewById(R.id.time_final_activity);

        ExcelHelper excelHelper = new ExcelHelper();
        excelHelper.CreateFileWithResult(5);

        RealmMegaSportsmanSaver saver = new RealmMegaSportsmanSaver(this, "RESULTS");
        list = saver.GetSportsmen("number",true);
        List<MegaSportsman> adapterList = new ArrayList<>();
        for(int i = 0; i < list.size();i++)
        {
            if(!list.get(i).getPlaceArr()[0].equals("0"))
            {
                adapterList.add(list.get(i));
            }
        }
        FinalActivityAdapter adapter = new FinalActivityAdapter(SortByPlace(adapterList));
        _resultTable.setAdapter(adapter);
        _resultTable.setItemAnimator(new DefaultItemAnimator());
        _resultTable.setLayoutManager(new LinearLayoutManager(this));

    }

    private List<MegaSportsman> SortByPlace(List<MegaSportsman> sportsmen)
    {
        MegaSportsman helperSportsman;
        MegaSportsman[] localArr = sportsmen.toArray(new MegaSportsman[sportsmen.size()]);
        int k = 0;
        while(k<localArr.length-1) {
            for (int i = 0; i < localArr.length -k- 1; i++) {
                if (Integer.valueOf(localArr[i].getPlaceArr()[0]) > Integer.valueOf(localArr[i + 1].getPlaceArr()[0])) {
                    helperSportsman = localArr[i];
                    localArr[i] = localArr[i + 1];
                    localArr[i + 1] = helperSportsman;
                }
            }
            k++;
        }
        return new ArrayList<MegaSportsman>(Arrays.asList(localArr));

    }

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

    private void AddResultRow(MegaSportsman sportsman, int lap)
    {
        final TableRow newRow = new TableRow(this);
        final TextView newTextView = new TextView(this);
        newTextView.setText(sportsman.getPlaceArr()[lap]);
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setTextColor(Color.BLACK);
        newTextView.setBackground(new PaintDrawable(Color.WHITE));
        newTextView.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView.setLayoutParams(new TableRow.LayoutParams(_place.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT, 0.4f));
        ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);

        final TextView newTextView2 = new TextView(this);
        newTextView2.setText(String.valueOf(sportsman.getNumber()));
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setTextColor(Color.BLACK);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_number.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT, 0.4f));
        ((TableRow.LayoutParams)newTextView2.getLayoutParams()).setMargins(0,0,2,2);

        final TextView newTextView3 = new TextView(this);
        newTextView3.setText(sportsman.getName());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setTextColor(Color.BLACK);
        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_name.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT, 2.2f));
        ((TableRow.LayoutParams)newTextView3.getLayoutParams()).setMargins(0,0,2,2);

        final TextView newTextView4 = new TextView(this);
        newTextView4.setText(sportsman.getResultArr()[lap]);
        newTextView4.setGravity(Gravity.CENTER);
        newTextView4.setTextColor(Color.BLACK);
        newTextView4.setBackground(new PaintDrawable(Color.WHITE));
        newTextView4.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
        newTextView4.setLayoutParams(new TableRow.LayoutParams(_number.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        ((TableRow.LayoutParams)newTextView4.getLayoutParams()).setMargins(0,0,2,2);

        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        newRow.addView(newTextView4);

        _resultTable.addView(newRow);
    }
}

