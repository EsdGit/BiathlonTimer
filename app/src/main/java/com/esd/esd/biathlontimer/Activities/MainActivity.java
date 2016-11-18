package com.esd.esd.biathlontimer.Activities;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.icu.text.MessagePattern;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
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
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView _nameTextView;
    private TextView _dateTextView;
    private TableLayout _tableLayout;
    private MenuItem _editMenuItem;
    private MenuItem _deleteMenuItem;
    private MenuItem _sortNameMenuItem;
    private MenuItem _sortDataMenuItem;
    private int _counterMarkedCompetition;

    private boolean _haveMarkedCompetition = false;
    private boolean _isFirstLoad = true;

    private CompetitionSaver _saver;

    private Competition[] _competitions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.main_activity_head) + "</big>" + "</font>")));
        _tableLayout = (TableLayout)findViewById(R.id.table);
        _nameTextView = (TextView) findViewById(R.id.CompetitionsNameTextView);
        _dateTextView = (TextView) findViewById(R.id.CompetitionsDateTextView);
        _saver = new CompetitionSaver(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(_isFirstLoad) {
            Competition[] localArr = _saver.GetAllCompetitions(DatabaseProvider.DbCompetitions.COLUMN_COMPETITION_DATE);
            _competitions = new Competition[localArr.length];
            for(int i = 0; i < localArr.length; i++)
            {
                _competitions[i] = localArr[i];
                AddCompetitionRow(localArr[i]);
            }
            _isFirstLoad = false;
        }
    }

    private Competition[] GetCheckedCompetitions()
    {
        ArrayList<Competition> localArr = new ArrayList<Competition>();
        int competitionsCount = _tableLayout.getChildCount();
        TableRow row;
        TextView name;
        TextView date;
        int k = 0;
        for(int i = 0; i < competitionsCount - k; i++)
        {
            row = (TableRow) _tableLayout.getChildAt(i);
            name = (TextView) row.getChildAt(0);
            date = (TextView) row.getChildAt(1);
            if(((PaintDrawable)(name).getBackground()).getPaint().getColor() ==
                    getResources().getColor(R.color.colorPrimary))
            {
                localArr.add(new Competition(name.getText().toString(), date.getText().toString(), this));
                _tableLayout.removeViewAt(i);
                k++;
                i--;
            }
        }
        return localArr.toArray(new Competition[localArr.size()]);
    }

    private void AddCompetitionRow(Competition competition)
    {
        final TableRow newRow = new TableRow(this);
        final TextView newTextView = new TextView(this);
        newTextView.setText(competition.GetName());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackground(new PaintDrawable(Color.WHITE));
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameTextView.getMeasuredWidth(),_nameTextView.getMeasuredHeight(), 0.666f));
        ((TableRow.LayoutParams)newTextView.getLayoutParams()).setMargins(2,0,2,2);
        final TextView newTextView2 = new TextView(this);
        newTextView2.setText(competition.GetDate());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
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
                if(!_haveMarkedCompetition)
                {
                    Intent myIntent = new Intent(MainActivity.this, ViewPagerActivity.class);
                    myIntent.putExtra("CompetitionName", newTextView.getText());
                    myIntent.putExtra("CompetitionDate", newTextView2.getText());
                    myIntent.putExtra("CompetitionStartType", "");
                    myIntent.putExtra("CompetitionInterval", "");
                    myIntent.putExtra("CompetitionCheckPointsCount", "");
                    myIntent.putExtra("NeedDelete", "false");
                    startActivity(myIntent);
                }
                else
                {
                    if (_counterMarkedCompetition == 0)
                    {
                        _counterMarkedCompetition++;
                        SetEditPosition();
                    } else {
                        for (int i = 0; i < rowView.size(); i++) {
                            PaintDrawable drawable = (PaintDrawable) rowView.get(i).getBackground();
                            if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary)) {
                                if (i == 0)
                                {
                                    _counterMarkedCompetition--;
                                }
                                rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
                            } else {
                                if (i == 0)
                                {
                                    _counterMarkedCompetition++;
                                }
                                rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
                            }
                        }
                        switch (_counterMarkedCompetition) {
                            case 0:
                                SetStarPosition();
                                _haveMarkedCompetition = false;
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

            }
        });
        _tableLayout.addView(newRow);
    }

    private void SortCompetitionsBy(String orderBy, boolean sortState)
    {
        String localOrderString;
        Competition[] localArr;
        if(sortState)
        {
            localOrderString = orderBy + " ASC";
        }
        else
        {
            localOrderString = orderBy + " DESC";
        }

        _tableLayout.removeAllViews();

        localArr = _saver.GetAllCompetitions(localOrderString);
        for(int i = 0; i < localArr.length; i++)
        {
            AddCompetitionRow(localArr[i]);
        }

    }

    public void OnClick(View view)
    {
        Intent settingPager = new Intent(this, SettingsActivity.class);
        startActivity(settingPager);
    }

    public void OnClickDeleteCompetition()
    {
        Competition[] localArr = GetCheckedCompetitions();
        for(int i = 0; i<localArr.length; i++)
        {
            _saver.DeleteCompetitionFromDatabase(localArr[i]);
        }
    }

    public void OnClickEditCompetition()
    {
        // Удалить старое и изменить на новое
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.mainMenuNameSort:
                Toast.makeText(getApplicationContext(),"Сортировка по названию", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mainMenuDataSort:
                Toast.makeText(getApplicationContext(),"Сортировка по дате", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mainEditCompetition:
                OnClickEditCompetition();
                SetStarPosition();
                break;
            case R.id.mainDeleteCompetition:
                OnClickDeleteCompetition();
                SetStarPosition();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetStarPosition()
    {
        _sortNameMenuItem.setVisible(true);
        _sortDataMenuItem.setVisible(true);
        _deleteMenuItem.setVisible(false);
        _editMenuItem.setVisible(false);
        _haveMarkedCompetition = false;
        _counterMarkedCompetition = 0;
    }

    private void SetEditPosition()
    {
        _sortNameMenuItem.setVisible(false);
        _sortDataMenuItem.setVisible(false);
        _deleteMenuItem.setVisible(true);
        _editMenuItem.setVisible(true);
    }

    private void SetDelPosition()
    {
        _sortNameMenuItem.setVisible(false);
        _sortDataMenuItem.setVisible(false);
        _deleteMenuItem.setVisible(true);
        _editMenuItem.setVisible(false);
    }
}