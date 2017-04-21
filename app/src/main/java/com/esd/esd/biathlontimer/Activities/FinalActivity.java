package com.esd.esd.biathlontimer.Activities;

import android.content.Context;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmMegaSportsmanSaver;
import com.esd.esd.biathlontimer.ExcelHelper;
import com.esd.esd.biathlontimer.Adapters.FinalActivityAdapter;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Sportsman;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class FinalActivity extends AppCompatActivity {
    private ViewPager _containerTable;
    private ArrayList<RecyclerView> _arrayRecycleView;
    private TextView _currentRound;
    private PopupMenu _popupMenuSortGroup;


    private int lapsCount;
    private Competition _currentCompetition;
    private List<MegaSportsman>[] _arrayMegaSportsman;
    private String[] _arrayGroup;

    private ExcelHelper excelHelper;
    private String temporaryPath;
    private Context _context;
    Thread checkLapNumberThread;
    RecyclerView _currentRecyclerView;

    ArrayList<FinalActivityAdapter> adapter;

    private ArrayList<String>_groupSortArray;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">"  + "<big>" + getResources().getString(R.string.title_final_activity) + "</big>" + "</font>")));

        _containerTable = (ViewPager) findViewById(R.id.tableContainer);
        _currentRound = (TextView) findViewById(R.id.currentRoundFinalActivity);
        _currentRound.setText("1");
        _context = getApplicationContext();


        RealmCompetitionSaver saverComp = new RealmCompetitionSaver(this, "COMPETITIONS");
        _currentCompetition = saverComp.GetCompetition(getIntent().getStringExtra("Name"), getIntent().getStringExtra("Date"));
        saverComp.Dispose();
        String groups = _currentCompetition.getGroups();
        if(!groups.isEmpty())
        {
            String[] localArray = groups.split(",");
            _arrayGroup = new String[localArray.length + 1];
            for (int i = 0; i < localArray.length; i++) {
                _arrayGroup[i + 1] = localArray[i];
            }
        }
        else
        {
            _arrayGroup = new String[1];
        }
        _arrayGroup[0]=getResources().getString(R.string.default_group);

        _groupSortArray = new ArrayList<>();
        for(int i =0; i < _arrayGroup.length; i++)
        {
            _groupSortArray.add(_arrayGroup[i]);
        }

        lapsCount = _currentCompetition.getCheckPointsCount();
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
        _currentRecyclerView = _arrayRecycleView.get(0);
        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        _containerTable.setAdapter(pagerAdapter);
        _containerTable.setCurrentItem(0);
        excelHelper = new ExcelHelper();
        checkLapNumberThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    final int lapNumber = _containerTable.getCurrentItem();
                    if (!_currentRecyclerView.equals(_arrayRecycleView.get(lapNumber)))
                    {
                        _currentRecyclerView = _arrayRecycleView.get(lapNumber);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                _currentRound.setText(String.valueOf(lapNumber+1));
                            }
                        });
                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {

                    }
                }
            }
        });
        temporaryPath = Environment.getExternalStorageDirectory().getPath() + "/" + _currentCompetition.getName() +" Результат.xls";
        LoadData loading = new LoadData();
        loading.execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.final_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        File file = new File(temporaryPath);
        if(file.exists()) file.delete();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        item.setChecked(!item.isChecked());
        switch (item.getItemId())
        {
            case R.id.action_bar_final_activity_place_sort:
                SortBy("_place", item.isChecked());
                Toast.makeText(getApplicationContext(),"Сортировка по месту",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_name_sort:
                SortBy("name", item.isChecked());
                Toast.makeText(getApplicationContext(),"Сортировка по имени",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_save:
                Intent intentFolders = new Intent(this, FolderPicker.class);
                startActivityForResult(intentFolders,1);
                Toast.makeText(getApplicationContext(),"Сохранить файл",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send:
                Toast.makeText(getApplicationContext(),"Отправка",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send_by_whatsapp:

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/*");
                File file = new File(temporaryPath);
                intent.setPackage("com.whatsapp");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(intent, "Выбор"));
                Toast.makeText(getApplicationContext(),"Отправка через WhatsApp",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_bar_final_activity_send_by_mail:
                Intent intent1 = new Intent(Intent.ACTION_SEND);
                intent1.setType("application/*");
                File file1 = new File(temporaryPath);
                intent1.setPackage("com.google.android.gm");
                intent1.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file1));
                startActivity(Intent.createChooser(intent1, "Выбор"));
                Toast.makeText(getApplicationContext(),"Отправка через Mail",Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_bar_final_activity_send_by_googledrive:
                Intent driveIntent = new Intent(Intent.ACTION_SEND);
                driveIntent.setType("application/*");
                File fileExc = new File(temporaryPath);
                driveIntent.setPackage("com.google.android.apps.docs");
                driveIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileExc));
                startActivity(Intent.createChooser(driveIntent, "Выбор"));

                Toast.makeText(getApplicationContext(),"GoogleDrive",Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_bar_final_activity_group_sort:
                if(_popupMenuSortGroup == null)
                {
                    _popupMenuSortGroup = new PopupMenu(this, this.findViewById(R.id.action_bar_final_activity_send));
                    MenuInflater menuInflater = _popupMenuSortGroup.getMenuInflater();
                    menuInflater.inflate(R.menu.final_activity_popup_menu, _popupMenuSortGroup.getMenu());
                    Menu menu = _popupMenuSortGroup.getMenu();
                    if(_arrayGroup != null)
                    {
                        for(int i = 0; i < _arrayGroup.length; i++)
                        {
                            menu.add(_arrayGroup[i]).setCheckable(true).setChecked(true).setOnMenuItemClickListener(_onMenuItemClickListener);
                        }
                    }
                }

                _popupMenuSortGroup.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void SortBy(String sortBy, boolean order)
    {
        for(int i =0 ; i < lapsCount; i++)
        {
            RealmMegaSportsmanSaver saver = new RealmMegaSportsmanSaver(getApplicationContext(), "LAP"+i+_currentCompetition.getNameDateString());
            _arrayMegaSportsman[i] = saver.GetSportsmen(sortBy, order);
            adapter.get(i).SortList(_arrayMegaSportsman[i]);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;
        String path = data.getStringExtra("url");
        excelHelper.CreateFileWithResult(_arrayMegaSportsman, path + "/"+_currentCompetition.getName()+" Результат.xls");
    }

    MenuItem.OnMenuItemClickListener _onMenuItemClickListener = new MenuItem.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(final MenuItem item)
        {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            item.setActionView(new View(_context));
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem)
                {
                    String name = String.valueOf(item.getTitle());
                    //Если группа выбрана
                    if(item.isChecked())
                    {
                        //Если группа была отменена
                        item.setChecked(false);
                        if(_groupSortArray.contains(name))
                        {
                            _groupSortArray.remove(name);
                        }
                    }
                    else
                    {
                        //Если группа бала отмечена
                        item.setChecked(true);
                        if(!_groupSortArray.contains(name))
                        {
                            _groupSortArray.add(name);
                        }
                    }
                    SortByGroup();
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem menuItem)
                {
                    return false;
                }
            });
            return false;
        }
    };

    private void SortByGroup()
    {
        for(int i = 0; i < lapsCount; i++)
        {
            if (_groupSortArray.size() == 0)
                adapter.get(i).SortList(new ArrayList<MegaSportsman>());
            else
            {
                RealmMegaSportsmanSaver saver = new RealmMegaSportsmanSaver(getApplicationContext(), "LAP"+i+_currentCompetition.getNameDateString());
                List<MegaSportsman> localList = saver.SortByGroup(_groupSortArray);
                adapter.get(i).SortList(localList);
            }
        }
    }

    class LoadData extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... params)
        {
            adapter = new ArrayList<>();
            for(int i = 0; i < lapsCount; i++)
            {
                RealmMegaSportsmanSaver saver = new RealmMegaSportsmanSaver(getApplicationContext(), "LAP"+i+_currentCompetition.getNameDateString());
                _arrayMegaSportsman[i] = saver.GetSportsmen("_place",true);
                adapter.add(new FinalActivityAdapter(_arrayMegaSportsman[i]));
            }
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
            excelHelper.CreateFileWithResult(_arrayMegaSportsman, temporaryPath);
            checkLapNumberThread.start();
        }
    }


}

