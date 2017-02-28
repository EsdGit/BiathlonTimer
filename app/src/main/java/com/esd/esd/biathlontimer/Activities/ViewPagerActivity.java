package com.esd.esd.biathlontimer.Activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmCompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Adapters.RecyclerViewDatabaseAdapter;
import com.esd.esd.biathlontimer.Adapters.RecyclerViewLocalDatabaseAdapter;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ViewPagerActivity extends AppCompatActivity
{
    private static RecyclerViewLocalDatabaseAdapter _recyclerViewLocalDatabaseAdapter;
    private static RecyclerViewDatabaseAdapter _recyclerViewDatabaseAdapter;

    private Sportsman _renameSportsman;
    private RecyclerView _renameRecyclerView;

    private boolean _isFirstLoad = true;
    private static TextView _emptyDataBaseList;
    private static TextView _emptyParticipantList;
    private static LinearLayout _headDataBase;
    private static LinearLayout _headParticipant;
    private AlertDialog.Builder _addDialogBuilder;
    private AlertDialog _addDialog;
    private PopupMenu _participantPopupMenu;
    private PopupMenu _dataBasePopupMenu;
    private static RecyclerView _recyclerView;
    private ColorPickerDialog _addColorToParticipantDialog;
    private int _colorParticipant;
    private String[] _arrayGroup;

    // Элементы ParticipantList
    private RelativeLayout _myProgressBar;
    private ProgressBar _progressBar;
    private TextView _textProgress;
    private static TextView _nameOfParticipantList;
    private static ImageButton _acceptParticipantImBtn;
    private static ImageButton _deleteParticipantImBtn;
    private static ImageButton _menuParticipantImBtn;
    private static ImageButton _editParticipantImBtn;

    // Элементы AlertDialog
    private EditText _nameDialog;
    private EditText _birthdayDialog;
    private EditText _countryDialog;
    private EditText _numberDialog;
    private TextView _colorDialog;
    private Spinner _spinnerOfGroup;

    private  View _renameForm;
    private EditText _numberRenameDialog;
    private EditText _nameRenameDialog;
    private EditText _birthdayRenameDialog;
    private EditText _countryRenameDialog;
    private TextView _colorRenameDialog;
    private Spinner _spinnerOfGroupRename;
    private AlertDialog.Builder _renameDialogBuilder;
    private AlertDialog _renameDialog;

    // Элементы DataBaseList
    private static TextView _nameOfDataBaseList;
    private static ImageButton _acceptDataBaseImBtn;
    private static ImageButton _deleteDataBaseImBtn;
    private static ImageButton _menuDataBaseImBtn;
    private static ImageButton _editDataBaseImBtn;
    private static ImageButton _secondAcceptDataBaseImBtn;
    private static RecyclerView _recyclerViewDatabase;

    //Диалог добавления
    private View _dialogForm;
    private static String TitleDialog;
    private static String AddDialogBtn;
    private static String CancelDialogBtn;
    private static String DefaultGroup;

    private Competition _currentCompetition;

    private boolean _needDeleteTables = false;

    private static RealmSportsmenSaver saver;
    private RealmSportsmenSaver mainSaver;

    private Context _viewPagerContext;
    private ProgressDialog _progressDialog;

    private ArrayList<String>_groupSortArray;
    private ActionBar test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        _viewPagerContext = this;
        TitleDialog = getResources().getString(R.string.add_dialog_title);
        AddDialogBtn = getResources().getString(R.string.add);
        CancelDialogBtn = getResources().getString(R.string.cancel);
        DefaultGroup = getResources().getString(R.string.default_group);
        Intent intent = getIntent();
        String name = intent.getStringExtra("CompetitionName");
        String date = intent.getStringExtra("CompetitionDate");
        RealmCompetitionSaver saverComp = new RealmCompetitionSaver(this, "COMPETITIONS");
        _currentCompetition = saverComp.GetCompetition(name, date);
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

        _needDeleteTables = Boolean.valueOf(intent.getStringExtra("NeedDelete"));

        FindAllViews();

        // Создание диалогового окна
        _addDialogBuilder = new AlertDialog.Builder(this);
        _addDialogBuilder.setTitle(TitleDialog);
        _addDialogBuilder.setView(_dialogForm);

        // Действия по кнопке "Добавить"
        _addDialogBuilder.setPositiveButton(AddDialogBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1)
            {
                if(ParseSportsman(Integer.valueOf(_numberDialog.getText().toString()), _nameDialog.getText().toString(), false))
                {
                    _colorParticipant = ((ColorDrawable) _colorDialog.getBackground()).getColor();
                    Sportsman sportsman = new Sportsman(Integer.valueOf(_numberDialog.getText().toString()), _nameDialog.getText().toString(),
                            Integer.valueOf(_birthdayDialog.getText().toString()), _countryDialog.getText().toString(), _spinnerOfGroup.getSelectedItem().toString());

                    sportsman.setColor(_colorParticipant);

                    saver.SaveSportsman(sportsman);
                    sportsman.setColor(Color.BLACK);
                    sportsman.setNumber(0);
                    sportsman.setGroup(getResources().getString(R.string.default_group));
                    mainSaver.SaveSportsman(sportsman);
                    _recyclerViewLocalDatabaseAdapter.SortList(saver.GetSportsmen("number", true));
                    _recyclerViewDatabaseAdapter.AddSportsman(sportsman);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),getResources().getString(R.string.participant_already_exists_in_database),Toast.LENGTH_SHORT).show();
                }
                SetStartPosition(1, _recyclerViewDatabaseAdapter.getItemCount());
                _nameDialog.setText("");
                _birthdayDialog.setText("");
                _countryDialog.setText("");
                _numberDialog.setText("");
                _colorDialog.setBackgroundColor(Color.BLACK);
                _addColorToParticipantDialog.setSelectedColor(Color.BLACK);
                _spinnerOfGroup.setSelection(0);
            }

        });
        _addDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //Запрет на выход с диалогового окна кнопкой "Back"
        _addDialogBuilder.setCancelable(false);
        _addDialog = _addDialogBuilder.create();

        _renameDialogBuilder = new AlertDialog.Builder(this);
        _renameDialogBuilder.setTitle(getResources().getString(R.string.rename_dialog_title));
        _renameDialogBuilder.setView(_renameForm);
        _renameDialogBuilder.setPositiveButton(AddDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String number = _numberRenameDialog.getText().toString();
                int numberInt;
                if(number.equals("")) numberInt = 0;
                else numberInt = Integer.valueOf(number);

                    Sportsman sportsman = new Sportsman(numberInt, _nameRenameDialog.getText().toString(),
                            Integer.valueOf(_birthdayRenameDialog.getText().toString()), _countryRenameDialog.getText().toString(), _spinnerOfGroupRename.getSelectedItem().toString());

                    if (_renameRecyclerView == _recyclerView)
                    {
                        if(ParseSportsman(numberInt, _nameRenameDialog.getText().toString(), true))
                        {
                            int color = ((ColorDrawable) _colorRenameDialog.getBackground()).getColor();
                            sportsman.setColor(color);
                            if(_recyclerViewLocalDatabaseAdapter.ChangeSportsman(sportsman, _renameSportsman))
                            {
                                saver.DeleteSportsman(_renameSportsman);
                                saver.SaveSportsman(sportsman);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.participant_already_exists_in_database),Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.participant_already_exists_in_database),Toast.LENGTH_SHORT).show();
                        }
                        SetStartPosition(1, _recyclerViewLocalDatabaseAdapter.getItemCount());
                    }
                    else
                    {
                        sportsman.setColor(Color.BLACK);
                        sportsman.setNumber(0);
                        if(_recyclerViewDatabaseAdapter.ChangeSportsman(sportsman, _renameSportsman))
                        {
                            mainSaver.DeleteSportsman(_renameSportsman);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.participant_already_exists_in_database),Toast.LENGTH_SHORT).show();
                        }

                        SetStartPosition(2, _recyclerViewDatabaseAdapter.getItemCount());
                    }
                    sportsman.setColor(Color.BLACK);
                    sportsman.setNumber(0);
                    sportsman.setGroup(getResources().getString(R.string.default_group));
                    mainSaver.SaveSportsman(sportsman);
                    _recyclerViewDatabaseAdapter.AddSportsman(sportsman);
                    _colorDialog.setBackgroundColor(Color.BLACK);
            }
        });
        _renameDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if(_renameRecyclerView == _recyclerView)  SetStartPosition(1,_recyclerViewLocalDatabaseAdapter.getItemCount());
                else SetStartPosition(2,_recyclerViewDatabaseAdapter.getItemCount());

                _colorDialog.setBackgroundColor(Color.BLACK);

            }
        });
        _renameDialogBuilder.setCancelable(false);
        _renameDialog = _renameDialogBuilder.create();

        //Диалог выбора цвета
        _addColorToParticipantDialog = new ColorPickerDialog();
        _addColorToParticipantDialog.initialize(R.string.color_picker_default_title,
                new int[] {
                        Color.RED,
                        Color.BLACK,
                        getResources().getColor(R.color.darkBlue),
                        getResources().getColor(R.color.orange),
                        getResources().getColor(R.color.brown),
                        getResources().getColor(R.color.violet),
                        getResources().getColor(R.color.green),
                        getResources().getColor(R.color.yellow)
                }, Color.BLACK, 4, 30);

        _addColorToParticipantDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int colour)
            {
                _colorDialog.setBackgroundColor(colour);
                _colorRenameDialog.setBackgroundColor(colour);
                _colorParticipant = colour;
            }
        });

        //Создание spinner в диалоговом окне добавления участника
        ArrayAdapter<String> adapterGroupAddParticipant = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _arrayGroup);
        adapterGroupAddParticipant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerOfGroup.setAdapter(adapterGroupAddParticipant);
        _spinnerOfGroup.setSelection(0);
        _spinnerOfGroup.setScrollContainer(true);

        ArrayAdapter<String> adapterGroupRenameParticipant = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, _arrayGroup);
        adapterGroupRenameParticipant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerOfGroupRename.setAdapter(adapterGroupAddParticipant);
        _spinnerOfGroupRename.setSelection(0);
        _spinnerOfGroupRename.setScrollContainer(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(_isFirstLoad) {
            AddDataFromBases();
            _isFirstLoad = false;
        }
    }

    /*
        void AddDataFromBases() - метод для загрузки данных из общей базы участников и из базы текущего соревнования.
        Возвращаемый тип: -
        Аргументы: -
    */
    private void AddDataFromBases()
    {
        mainSaver = new RealmSportsmenSaver(this, "MAIN");
        saver = new RealmSportsmenSaver(this, _currentCompetition.getDbParticipantPath());

        if(_needDeleteTables)
        {
            GenerateStandartParticipants(_currentCompetition.getStartNumber(), _currentCompetition.getMaxParticipantCount());
        }
        else
        {
            _recyclerViewLocalDatabaseAdapter = new RecyclerViewLocalDatabaseAdapter(this, saver.GetSportsmen("number", true), _currentCompetition);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

            _recyclerView.setAdapter(_recyclerViewLocalDatabaseAdapter);
            _recyclerView.setLayoutManager(layoutManager);
            _recyclerView.setItemAnimator(itemAnimator);
        }

        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator1 = new DefaultItemAnimator();
        _recyclerViewDatabaseAdapter = new RecyclerViewDatabaseAdapter(this, mainSaver.GetSportsmen("name", true));
        _recyclerViewDatabase.setAdapter(_recyclerViewDatabaseAdapter);
        _recyclerViewDatabase.setLayoutManager(layoutManager1);
        _recyclerViewDatabase.setItemAnimator(itemAnimator1);
    }

    /*
        void onWindowFocusChanged() - метод, вызывающийся по изменению фокуса на активности.
        Возвращаемый тип: -
        Аргументы:
            boolean hasFocus - показывает есть фокус на данной активности в данный момент времени или нет.
    */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //EmptyParticipantCompetition();
        EmptyDataBaseCompetition();
    }

    /*
        void OnClick() - метод, вызывающийся по клику на кнопку добавления участника.
        Возвращаемый тип: -
        Аргументы:
            View view - элемент, вызвавший этот метод.
    */
    public void OnClick(View view)
    {
        _addDialog.show();
    }

    /*
        void GenerateStandartParticipnats() - метод, создающий спортсменов по умолчанию при первом создании соревнования.
        Возвращаемый тип: -
        Аргументы:
            final int firstNumber - номер, с которого начинается нумерация всех участников.
            final int count - количество участников в соревновании.
    */
    private void GenerateStandartParticipants(final int firstNumber, final int count)
    {
        _progressDialog = new ProgressDialog(this);
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _progressDialog.setMessage("Подождите. Идет загрузка...");
        _progressDialog.setIndeterminate(false);
        _progressDialog.setCancelable(false);
        _progressDialog.setMax(_currentCompetition.getMaxParticipantCount());
        _progressDialog.show();
        CreateTableOfSportsman createTableOfSportsman = new CreateTableOfSportsman();
        createTableOfSportsman.execute();
    }

    /*
        void FindAllViews() - метод,в котором находим все view элементы данной активности.
        Возвращаемый тип: -
        Аргументы: -
    */
    private void FindAllViews()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        View page1 = inflater.inflate(R.layout.activity_participant_list, null);
        pages.add(page1);

        _renameForm = inflater.inflate(R.layout.dialog_activity_add_participant, null);
        _numberRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogNumber);
        _nameRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogName);
        _birthdayRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogBirthday);
        _countryRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogCountry);
        _colorRenameDialog = (TextView) _renameForm.findViewById(R.id.dialogColor);
        _spinnerOfGroupRename = (Spinner) _renameForm.findViewById(R.id.spinnerOfGroup);

        _dialogForm = inflater.inflate(R.layout.dialog_activity_add_participant, null);
        _recyclerView = (RecyclerView) page1.findViewById(R.id.gridViewParticipantList);

        _headParticipant = (LinearLayout) page1.findViewById(R.id.headTableParticipantListLayout);
        _myProgressBar = (RelativeLayout) page1.findViewById(R.id.my_progress_bar);
        _progressBar = (ProgressBar) page1.findViewById(R.id.progress_bar);
        _textProgress = (TextView) page1.findViewById(R.id.text_progress);
        _nameOfParticipantList = (TextView) page1.findViewById(R.id.participant_list_head);
        _emptyParticipantList = (TextView) page1.findViewById(R.id.emptyParticipantListTextView);
        _acceptParticipantImBtn = (ImageButton) page1.findViewById(R.id.accept_participant);
        _menuParticipantImBtn = (ImageButton) page1.findViewById(R.id.menu_participant);
        _deleteParticipantImBtn = (ImageButton) page1.findViewById(R.id.delete_participant);
        _editParticipantImBtn = (ImageButton) page1.findViewById(R.id.edit_participant);

        _nameDialog = (EditText) _dialogForm.findViewById(R.id.dialogName);
        _birthdayDialog = (EditText) _dialogForm.findViewById(R.id.dialogBirthday);
        _countryDialog = (EditText) _dialogForm.findViewById(R.id.dialogCountry);
        _numberDialog = (EditText) _dialogForm.findViewById(R.id.dialogNumber);
        _colorDialog = (TextView) _dialogForm.findViewById(R.id.dialogColor);
        _spinnerOfGroup = (Spinner) _dialogForm.findViewById(R.id.spinnerOfGroup);

        if(!getResources().getBoolean(R.bool.isTablet))
        {
            _nameOfParticipantList.setText(Html.fromHtml("<font>"  + "<big>"  + getResources().getString(R.string.participant_list_actvity_head) +  "</big>" + "</font>"));
            //Работа с DataBaseList
            View page2 = inflater.inflate(R.layout.activity_database_list, null);
            pages.add(page2);

            _recyclerViewDatabase = (RecyclerView) page2.findViewById(R.id.gridViewDataBaseLayout);
            _headDataBase = (LinearLayout) page2.findViewById(R.id.headTableDataBaseLayout);
            _nameOfDataBaseList = (TextView) page2.findViewById(R.id.database_list_head);
            _emptyDataBaseList = (TextView) page2.findViewById(R.id.emptyDataBaseTextView);
            _acceptDataBaseImBtn = (ImageButton) page2.findViewById(R.id.accept_database);
            _deleteDataBaseImBtn = (ImageButton) page2.findViewById(R.id.delete_database);
            _menuDataBaseImBtn = (ImageButton) page2.findViewById(R.id.menu_database);
            _deleteDataBaseImBtn = (ImageButton) page2.findViewById(R.id.delete_database);
            _editDataBaseImBtn = (ImageButton) page2.findViewById(R.id.edit_database);
            _secondAcceptDataBaseImBtn = (ImageButton) page2.findViewById(R.id.second_accept_database);

            _nameOfDataBaseList.setText(Html.fromHtml("<font>" + "<big>" + getResources().getString(R.string.db_list_activity_head) + "</big>" + "</font>"));

            PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
            ViewPager viewPager = new ViewPager(this);
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);
            setContentView(viewPager);
        }
        else
        {
            _nameOfParticipantList.setText(Html.fromHtml("<font>"   + getResources().getString(R.string.participant_list_actvity_head) +  "</font>"));
            _recyclerViewDatabase = (RecyclerView) page1.findViewById(R.id.gridViewDataBaseLayout);
            _headDataBase = (LinearLayout) page1.findViewById(R.id.headTableDataBaseLayout);
            _nameOfDataBaseList = (TextView) page1.findViewById(R.id.database_list_head);
            _emptyDataBaseList = (TextView) page1.findViewById(R.id.emptyDataBaseTextView);
            _acceptDataBaseImBtn = (ImageButton) page1.findViewById(R.id.accept_database);
            _deleteDataBaseImBtn = (ImageButton) page1.findViewById(R.id.delete_database);
            _menuDataBaseImBtn = (ImageButton) page1.findViewById(R.id.menu_database);
            _deleteDataBaseImBtn = (ImageButton) page1.findViewById(R.id.delete_database);
            _editDataBaseImBtn = (ImageButton) page1.findViewById(R.id.edit_database);
            _secondAcceptDataBaseImBtn = (ImageButton) page1.findViewById(R.id.second_accept_database);

            _nameOfDataBaseList.setText(Html.fromHtml("<font>" + getResources().getString(R.string.db_list_activity_head) + "</font>"));
            setContentView(page1);
        }
    }

    /*
        void SortTableBy() - метод, сортирующий таблицу по заданным параметрам.
        Возвращаемый тип: -
        Аргументы:
            RecyclerView recyclerView - элемент RecyclerView в котором будем сортировать данные, необходим для
                                       того, чтобы определять сортируем локальную базу или общую.
            String orderBy - параметр по которому сортируем, пример: "name", "number";
            boolean sortState - сортировка в прямом порядке или в инверсном.
    */
    private void SortTableBy(RecyclerView recyclerView, String orderBy, boolean sortState)
    {
        List<Sportsman> sortedList;
        if(recyclerView == _recyclerView)
        {
            //sortedList = saver.GetSportsmen(orderBy, sortState);
            sortedList = saver.SortBy(orderBy, sortState, _groupSortArray);
            _recyclerViewLocalDatabaseAdapter.SortList(sortedList);
        }
        else
        {
            sortedList = mainSaver.GetSportsmen(orderBy, sortState);
            //sortedList = mainSaver.SortBy(orderBy, sortState, _groupSortArray);
            _recyclerViewDatabaseAdapter.SortList(sortedList);
        }

    }

    /*
        void OnClickMenu() - метод, вызывающийся при клике на меню сортировок.
        Возвращаемый тип: -
        Аргументы:
            View view - элемент, который вызвал данный метод.
    */
    public void OnClickMenu(View view)
    {
        switch (view.getId())
        {
            case R.id.menu_participant:
                if(_participantPopupMenu == null)
                {
                    _participantPopupMenu = new PopupMenu(this, view);
                    MenuInflater menuInflater = _participantPopupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.sort_menu, _participantPopupMenu.getMenu());
                    SubMenu localMenu =  _participantPopupMenu.getMenu().findItem(R.id.groupSort).getSubMenu();
                    if(_arrayGroup != null)
                    {
                        for(int i = 0; i < _arrayGroup.length; i++)
                        {
                            localMenu.add(_arrayGroup[i]).setCheckable(true).setChecked(true).setOnMenuItemClickListener(_onMenuItemClickListener);
                        }
                    }
                    _participantPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            item.setChecked(!item.isChecked());
                            switch (item.getItemId())
                            {
                                case R.id.groupSort:
                                    //Toast.makeText(getApplicationContext(),"Сортировка списка участников по группам",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.nameSort:
                                    SortTableBy(_recyclerView, "name" , item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка списка участников по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortTableBy(_recyclerView, "year", item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка списка участников по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    SortTableBy(_recyclerView, "country", item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка списка участников по региону",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.numberSort:
                                    SortTableBy(_recyclerView, "number", item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка списка участников по номеру",Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                }
                _participantPopupMenu.show();
                return;
            case R.id.menu_database:
                if(_dataBasePopupMenu == null)
                {
                    _dataBasePopupMenu = new PopupMenu(this, view);
                    MenuInflater menuInflater = _dataBasePopupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.sort_menu, _dataBasePopupMenu.getMenu());
                    _dataBasePopupMenu.getMenu().findItem(R.id.numberSort).setVisible(false);
                    _dataBasePopupMenu.getMenu().findItem(R.id.groupSort).setVisible(false);
                    _dataBasePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            item.setChecked(!item.isChecked());
                            switch (item.getItemId())
                            {
                                case R.id.nameSort:
                                    SortTableBy(_recyclerViewDatabase, "name", item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка базы данных по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortTableBy(_recyclerViewDatabase, "year", item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка базы данных по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    SortTableBy(_recyclerViewDatabase, "country", item.isChecked());
                                    //Toast.makeText(getApplicationContext(),"Сортировка базы данных по региону",Toast.LENGTH_SHORT).show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                }
                _dataBasePopupMenu.show();
                return;

        }
    }

    public void OnClickAcceptParticipant(View view)
    {
        // проверка нет ли повторяющихся номеров
        if(HaveParticipantsZeroNumber())
        {
            Toast.makeText(this, "В соревнование не могут быть спортсмены с нулевым номером", Toast.LENGTH_SHORT).show();
            return;
        }
        _needDeleteTables = false;
        Intent intent = new Intent(this, CompetitionsActivity.class);
        intent.putExtra("Name", _currentCompetition.getName());
        intent.putExtra("Date", _currentCompetition.getDate());
        startActivity(intent);
        this.finish();
    }

    private boolean HaveParticipantsZeroNumber()
    {
        List<Sportsman> arr = saver.GetSportsmen("number", true);
        for(int i = 0; i < arr.size(); i++)
        {
            if(arr.get(i).getNumber() == 0)
            {
                return true;
            }
        }
        return false;
    }

    public void OnClickEditParticipant(View view)
    {
        Sportsman sportsmanToEdit = _recyclerViewLocalDatabaseAdapter.GetCheckedSportsmen().get(0);
        _numberRenameDialog.setText(String.valueOf(sportsmanToEdit.getNumber()));
        _nameRenameDialog.setText(sportsmanToEdit.getName());
        _birthdayRenameDialog.setText(String.valueOf(sportsmanToEdit.getYear()));
        _countryRenameDialog.setText(sportsmanToEdit.getCountry());
        _colorRenameDialog.setBackgroundColor(sportsmanToEdit.getColor());
        _renameRecyclerView = _recyclerView;
        _renameSportsman = sportsmanToEdit;
        for(int i = 0; i < _arrayGroup.length; i++)
        {
            if(_arrayGroup[i].equals(sportsmanToEdit.getGroup()))
            {
                _spinnerOfGroupRename.setSelection(i);
                break;
            }
        }
        _renameDialog.show();
        //Toast.makeText(getApplicationContext(),"Редактировать участника",Toast.LENGTH_SHORT).show();
    }

    public void OnClickDeleteParticipant(View view)
    {
        List<Sportsman> listToDelete = _recyclerViewLocalDatabaseAdapter.GetCheckedSportsmen();
        _recyclerViewLocalDatabaseAdapter.RemoveSportsmen(listToDelete);
        saver.DeleteSportsmen(listToDelete);
        _recyclerViewLocalDatabaseAdapter.ResetHaveMarkedFlag();
        SetStartPosition(1,_recyclerViewLocalDatabaseAdapter.getItemCount());
        //Toast.makeText(getApplicationContext(),"Удаление участника",Toast.LENGTH_SHORT).show();
    }

    public void OnClickDeleteDataBAse(View view)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.delete_dialog_title));
        dialog.setMessage(getResources().getString(R.string.messege_del_dialog_from_database));
        dialog.setCancelable(false);
        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                List<Sportsman> listToDelete = _recyclerViewDatabaseAdapter.GetCheckedSportsmen();
                mainSaver.DeleteSportsmen(listToDelete);
                _recyclerViewDatabaseAdapter.RemoveSportsmen(listToDelete);
                _recyclerViewDatabaseAdapter.ResetHaveMarkedFlag();
                SetStartPosition(2, _recyclerViewDatabaseAdapter.getItemCount());
                //Toast.makeText(getApplicationContext(),"Участники удалены",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();


    }

    public void OnClickEditDataBase(View view)
    {
        Sportsman sportsmanToEdit = _recyclerViewDatabaseAdapter.GetCheckedSportsmen().get(0);
        _numberRenameDialog.setText("");
        _nameRenameDialog.setText(sportsmanToEdit.getName());
        _birthdayRenameDialog.setText(String.valueOf(sportsmanToEdit.getYear()));
        _countryRenameDialog.setText(sportsmanToEdit.getCountry());
        _colorDialog.setBackgroundColor(Color.BLACK);
        _renameRecyclerView = _recyclerViewDatabase;
        _renameSportsman = sportsmanToEdit;
        _renameDialog.show();
        //Toast.makeText(getApplicationContext(),"Редактировать участника в базе данных",Toast.LENGTH_SHORT).show();
    }

    public void OnClickAcceptDataBase(View view)
    {
        List<Sportsman> checkedList = _recyclerViewDatabaseAdapter.GetCheckedSportsmen();
        _recyclerViewDatabaseAdapter.RemoveSportsmen(checkedList);
        saver.SaveSportsmen(checkedList);
        _recyclerViewLocalDatabaseAdapter.SortList(saver.GetSportsmen("number", true));
        _recyclerViewLocalDatabaseAdapter.AddSportsmen(checkedList);
        SetStartPosition(2, _recyclerViewDatabaseAdapter.getItemCount());
        //Toast.makeText(getApplicationContext(),"Участники были добавлены в соревнование",Toast.LENGTH_SHORT).show();
        EmptyDataBaseCompetition();
        EmptyParticipantCompetition();
    }

    static public void SetStartPosition(int mode, int childCount)
    {

        if(mode == 1)
        {
            if(childCount== 0)
            {
                _acceptParticipantImBtn.setVisibility(View.GONE);
            }
            else
            {
                _acceptParticipantImBtn.setVisibility(View.VISIBLE);
            }
            _editParticipantImBtn.setVisibility(View.GONE);
            _menuParticipantImBtn.setVisibility(View.VISIBLE);
            _deleteParticipantImBtn.setVisibility(View.GONE);
            _recyclerViewLocalDatabaseAdapter.ResetHaveMarkedFlag();
        }
        else
        {
            _secondAcceptDataBaseImBtn.setVisibility(View.INVISIBLE);
            _editDataBaseImBtn.setVisibility(View.GONE);
            _acceptDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.GONE);
            _menuDataBaseImBtn.setVisibility(View.VISIBLE);
            _recyclerViewDatabaseAdapter.ResetHaveMarkedFlag();
        }
    }

    static public void SetEditPosition(int mode)
    {
        if(mode == 1)
        {
            _acceptParticipantImBtn.setVisibility(View.GONE);
            _editParticipantImBtn.setVisibility(View.VISIBLE);
            _menuParticipantImBtn.setVisibility(View.GONE);
            _deleteParticipantImBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            _secondAcceptDataBaseImBtn.setVisibility(View.VISIBLE);
            _acceptDataBaseImBtn.setVisibility(View.GONE);
            _editDataBaseImBtn.setVisibility(View.VISIBLE);
            _menuDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.VISIBLE);
        }
    }

    static public void SetDelPosition(int mode)
    {
        if(mode == 1)
        {
            EmptyParticipantCompetition();
            _acceptParticipantImBtn.setVisibility(View.GONE);
            _editParticipantImBtn.setVisibility(View.GONE);
            _menuParticipantImBtn.setVisibility(View.GONE);
            _deleteParticipantImBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            EmptyDataBaseCompetition();
            _secondAcceptDataBaseImBtn.setVisibility(View.INVISIBLE);
            _editDataBaseImBtn.setVisibility(View.GONE);
            _acceptDataBaseImBtn.setVisibility(View.VISIBLE);
            _menuDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.VISIBLE);
        }
    }

    static private void EmptyParticipantCompetition()
    {
        if(_recyclerView.getChildCount() == 0)
        {
            _emptyParticipantList.setVisibility(View.VISIBLE);
            _headParticipant.setVisibility(View.GONE);
        }
        else
        {
            _emptyParticipantList.setVisibility(View.GONE);
            _headParticipant.setVisibility(View.VISIBLE);
        }
    }

    static private void EmptyDataBaseCompetition()
    {
        if(_recyclerViewDatabase.getChildCount() == 0)
        {
            _emptyDataBaseList.setVisibility(View.VISIBLE);
            _headDataBase.setVisibility(View.GONE);
        }
        else
        {
            _emptyDataBaseList.setVisibility(View.GONE);
            _headDataBase.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == (KeyEvent.KEYCODE_BACK))
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(getResources().getString(R.string.warning_dialog_title));
            dialog.setMessage(getResources().getString(R.string.message_dialog_warning));
            dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(ViewPagerActivity.this, MainActivity.class);
                    ViewPagerActivity.this.finish();
                    startActivity(intent);
                }
            });
            dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saver.Dispose();
        mainSaver.Dispose();
        if(_needDeleteTables)
        {
            saver.DeleteTable();
        }
    }

    public void OnClickColorParticipant(View view)
    {
        android.app.FragmentManager fm = this.getFragmentManager();
        _addColorToParticipantDialog.show(fm, "colorpicker");
    }


    private boolean ParseSportsman(int number, String fio, boolean isRename)
    {
        List<Sportsman> localList = saver.GetSportsmen("number", true);

            for (int i = 0; i < localList.size(); i++)
            {
                if(isRename)
                {
                    if(localList.get(i).getNumber() == number && localList.get(i).getName().equals(fio))
                    {
                        return true;
                    }
                }
                else
                {
                    if(localList.get(i).getNumber() == number)
                    {
                        return false;
                    }

                }
            }

        if((_currentCompetition.getStartNumber() + _currentCompetition.getMaxParticipantCount()) - 1 < number || _currentCompetition.getStartNumber() > number)
            return false;
        else
            return true;
    }

    MenuItem.OnMenuItemClickListener _onMenuItemClickListener = new MenuItem.OnMenuItemClickListener()
    {
        @Override
        public boolean onMenuItemClick(final MenuItem item)
        {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
            item.setActionView(new View(_viewPagerContext));
            item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem menuItem)
                {
                    String name = String.valueOf(item.getTitle());
                    //Если группа выбрана
                    if(!item.isChecked())
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
        if(_groupSortArray.size() == 0) _recyclerViewLocalDatabaseAdapter.SortList(new ArrayList<Sportsman>());
        else
        {
            List<Sportsman> localList = saver.SortByGroup(_groupSortArray);
            _recyclerViewLocalDatabaseAdapter.SortList(localList);
        }
    }

    private class CreateTableOfSportsman extends AsyncTask<Void, Sportsman, Void>
    {
        private int _firstNumber = _currentCompetition.getStartNumber();
        private int _count = _currentCompetition.getStartNumber() +  _currentCompetition.getMaxParticipantCount();
        @Override
        protected Void doInBackground(Void... params) {
            try {
                int counter = 0;
                for (int i = _firstNumber; i < _count; i++)
                {
                    final Sportsman sportsman = new Sportsman(i, "Спортсмен " + String.valueOf(counter + 1), 1996, "Россия", "Без группы");
                    sportsman.setColor(Color.BLACK);
                    publishProgress(sportsman);
                    SleepProgress(0);
                    counter++;
                }
                TimeUnit.NANOSECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _recyclerViewLocalDatabaseAdapter = new RecyclerViewLocalDatabaseAdapter(getApplicationContext(), saver.GetSportsmen("number", true), _currentCompetition);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            _recyclerView.setAdapter(_recyclerViewLocalDatabaseAdapter);
            _recyclerView.setLayoutManager(layoutManager);
            _recyclerView.setItemAnimator(itemAnimator);
            _progressDialog.dismiss();
        }

        @Override
        protected void onProgressUpdate(Sportsman... values) {
            super.onProgressUpdate(values);
            _progressDialog.incrementProgressBy(1);
            saver.SaveSportsman(values[0]);
        }

        private void SleepProgress(int floor) throws InterruptedException {
            //TimeUnit.NANOSECONDS.sleep(1000);
            TimeUnit.MILLISECONDS.sleep(1);
        }
    }

}
