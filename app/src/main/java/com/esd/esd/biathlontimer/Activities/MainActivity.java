package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView _nameTextView;
    private TextView _dateTextView;
    private TableLayout _tableLayout;
    private ImageButton _menuMainImBtn;
    private ImageButton _deleteMainImBtn;
    private ImageButton _editMainImBtn;
    private PopupMenu _popupMenu;
    private int _counterMarkedCompetition;

    private boolean _haveMarkedCompetition = false;
    private boolean _isFirstLoad = true;

    private CompetitionSaver _saver;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _tableLayout = (TableLayout)findViewById(R.id.table);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);
        _menuMainImBtn = (ImageButton) findViewById(R.id.menu);
        _deleteMainImBtn = (ImageButton) findViewById(R.id.delete);
        _editMainImBtn = (ImageButton) findViewById(R.id.edit);

        _saver = new CompetitionSaver(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(_isFirstLoad) {
            Competition[] localArr = _saver.GetAllCompetitions(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE);
            for(int i = 0; i < localArr.length; i++)
            {
                AddCompetitionRow(localArr[i]);
            }
            _isFirstLoad = false;
        }
    }

    private void AddCompetitionRow(Competition competition)
    {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(competition.GetName());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackgroundColor(Color.WHITE);
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameTextView.getMeasuredWidth(),_nameTextView.getMeasuredHeight(), 0.666f));
        ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(competition.GetDate());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackgroundColor(Color.WHITE);
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_dateTextView.getMeasuredWidth(),_dateTextView.getMeasuredHeight(), 0.334f));
        ((TableRow.LayoutParams)newTextView2.getLayoutParams()).setMargins(0,0,2,2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ArrayList<View> rowView = new ArrayList<View>();
                v.addChildrenForAccessibility(rowView);
                if(!_haveMarkedCompetition)
                {
                    _haveMarkedCompetition = true;
                    for (int i = 0; i < rowView.size(); i++)
                    {
                        rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
                    }
                }
                return false;
            }
        });
        newRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ArrayList<View> rowView = new ArrayList<View>();
                v.addChildrenForAccessibility(rowView);
                if(!_haveMarkedCompetition)return;
                if(_counterMarkedCompetition == 0)
                {
                    _counterMarkedCompetition++;
                    SetEditPosition();
                }
                else
                {
                    for(int i = 0; i < rowView.size(); i++)
                    {
                        PaintDrawable drawable = (PaintDrawable) rowView.get(i).getBackground();
                        if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary))
                        {
                            if(i == 0)
                            {
                                _counterMarkedCompetition--;
                            }
                            rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
                        }
                        else
                        {
                            if(i == 0)
                            {
                                _counterMarkedCompetition++;
                            }
                            rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
                        }
                    }
                    switch (_counterMarkedCompetition)
                    {
                        case 0:
                            SetStarPosition();
                            break;
                        case 1:
                            SetEditPosition();
                            break;
                        default:
                            SetDelPosition();
                            break;
                    }
                }
            }
        });
        _tableLayout.addView(newRow);
    }

    public void OnClick(View view)
    {
        Intent viewPager = new Intent(this, ViewPagerActivity.class);
        startActivity(viewPager);
    }

    public void OnClickMainMenu(View view)
    {
        if(_popupMenu == null)
        {
        _popupMenu = new PopupMenu(this, view);
        MenuInflater menuInflater = _popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.main_sort_menu, _popupMenu.getMenu());
        _popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.mainManuNameSort:
                        Toast.makeText(getApplicationContext(),"Сортировка файлов по имени",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.mainMenuDataSort:
                        Toast.makeText(getApplicationContext(),"Сортировка файлов по дате",Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        }
        _popupMenu.show();
    }

    private void SetStarPosition()
    {
        _deleteMainImBtn.setVisibility(View.GONE);
        _editMainImBtn.setVisibility(View.GONE);
    }

    private void SetEditPosition()
    {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        _deleteMainImBtn.setVisibility(View.VISIBLE);
        _deleteMainImBtn.setLayoutParams(params);
        params.gravity = Gravity.RIGHT;
        _editMainImBtn.setVisibility(View.VISIBLE);
        _editMainImBtn.setLayoutParams(params);
    }

    private void SetDelPosition()
    {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        _deleteMainImBtn.setVisibility(View.VISIBLE);
        _deleteMainImBtn.setLayoutParams(params);
        _editMainImBtn.setVisibility(View.GONE);
    }
}