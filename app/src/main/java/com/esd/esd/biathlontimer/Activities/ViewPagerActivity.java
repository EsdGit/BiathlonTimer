package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.RecyclerViewDatabaseAdapter;
import com.esd.esd.biathlontimer.RecyclerViewLocalDatabaseAdapter;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity
{
    private RecyclerViewLocalDatabaseAdapter _recyclerViewLocalDatabaseAdapter;
    private RecyclerViewDatabaseAdapter _recyclerViewDatabaseAdapter;
    //private ParticipantSaver _dbSaver;
    private boolean _isFirstLoad = true;
    private static boolean _haveMarkedParticipant = false;
    private static boolean _haveMarkedDataBase = false;
    private static int _counterMarkedParticipant;
    private static int _counterMarkedDataBase;
    private TableRow _renameTableRow;
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
    private static TextView _nameParticipantList;
    private static TextView _birthdayParticipantList;
    private static TextView _countryParticipantList;
    private static TextView _numberParticipantList;
    private static TextView _nameOfParticipantList;
    private static TextView _groupParticipantList;
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

    private View _renameForm;
    private EditText _numberRenameDialog;
    private EditText _nameRenameDialog;
    private EditText _birthdayRenameDialog;
    private EditText _countryRenameDialog;
    private TextView _colorRenameDialog;
    private Spinner _spinnerOfGroupRename;
    private AlertDialog.Builder _renameDialogBuilder;
    private AlertDialog _renameDialog;

    // Элементы DataBaseList
    private GridView _gridViewDatabase;
    private static TextView _nameDataBaseList;
    private static TextView _birthdayDataBaseList;
    private static TextView _countryDataBaseList;
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

    private RealmSportsmenSaver saver;
    private RealmSportsmenSaver mainSaver;

    private static Context _viewPagerContext;
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
        _currentCompetition = new Competition(name, date, this);
        _currentCompetition.GetAllSettingsToComp();
        String groups = _currentCompetition.GetGroups();
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
        _arrayGroup[0]=getResources().getString(R.string.default_group);;

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
                _colorParticipant = ((ColorDrawable) _colorDialog.getBackground()).getColor();
                Sportsman sportsman = new Sportsman(Integer.valueOf(_numberDialog.getText().toString()), _nameDialog.getText().toString(),
                        Integer.valueOf(_birthdayDialog.getText().toString()), _countryDialog.getText().toString(), _spinnerOfGroup.getSelectedItem().toString());

                 sportsman.setColor(_colorParticipant);

                _recyclerViewLocalDatabaseAdapter.AddSportsman(sportsman);
                _recyclerViewDatabaseAdapter.AddSportsman(sportsman);

                saver.SaveSportsman(sportsman);
                mainSaver.SaveSportsman(sportsman);

                SetStartPosition(1,_recyclerViewDatabaseAdapter.getItemCount(), 0);
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
                RecyclerView currGridView = (RecyclerView) _renameTableRow.getParent();
                //Participant localParticipant;
                String number;
                String name;
                String year;
                String country;
                String group;
                int cellsCount;
                _colorParticipant = ((ColorDrawable) _colorRenameDialog.getBackground()).getColor();
                if(currGridView == _recyclerView)
                {
                    number = ((TextView) _renameTableRow.getChildAt(0)).getText().toString();
                    name = ((TextView)_renameTableRow.getChildAt(1)).getText().toString();
                    year = ((TextView)_renameTableRow.getChildAt(2)).getText().toString();
                    group = _spinnerOfGroupRename.getSelectedItem().toString();
                    country = ((TextView)_renameTableRow.getChildAt(4)).getText().toString();

                }
                else
                {
                    number = "";
                    name = ((TextView)_renameTableRow.getChildAt(0)).getText().toString();
                    year = ((TextView)_renameTableRow.getChildAt(1)).getText().toString();
                    country = ((TextView)_renameTableRow.getChildAt(2)).getText().toString();

                }

//                localParticipant = new Participant(number, name, country, year, "",_colorParticipant);
//
//                _dbSaver.DeleteParticipant(localParticipant, DatabaseProvider.DbParticipant.TABLE_NAME);
//                _dbSaver.DeleteParticipant(localParticipant, _currentCompetition.GetDbParticipantPath());
//                localParticipant = new Participant(_numberRenameDialog.getText().toString(), _nameRenameDialog.getText().toString(),
//                        _countryRenameDialog.getText().toString(), _birthdayRenameDialog.getText().toString(), _spinnerOfGroupRename.getSelectedItem().toString(),_colorParticipant);
//                _dbSaver.SaveParticipantToDatabase(localParticipant, DatabaseProvider.DbParticipant.TABLE_NAME);
//                if(currGridView == _gridView)
//                {
//                    _dbSaver.SaveParticipantToDatabase(localParticipant, _currentCompetition.GetDbParticipantPath());
//                }

//                for(int j = 0; j < cellsCount; j++)
//                {
//                    ((TextView) _renameTableRow.getChildAt(j)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
//                }

                _colorDialog.setBackgroundColor(Color.BLACK);
                //SetStartPosition(currGridView);
            }
        });
        _renameDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //SetStartPosition(1);
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
                        Color.BLUE,
                        Color.CYAN,
                        Color.DKGRAY,
                        Color.GRAY,
                        Color.YELLOW,
                        Color.parseColor("#0b8722")
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


        AddDataFromBases();

    }

    private void AddDataFromBases()
    {

        mainSaver = new RealmSportsmenSaver(this, "MAIN");
        saver = new RealmSportsmenSaver(this, _currentCompetition.GetDbParticipantPath());

        if(_needDeleteTables)
        {
            List<Sportsman> sportsmen = new ArrayList<Sportsman>();
            GenerateStandartParticipants(_currentCompetition.GetStartNumber(), _currentCompetition.GetMaxParticipantCount(), sportsmen);
            saver.SaveSportsmen(sportsmen);
            _recyclerViewLocalDatabaseAdapter = new RecyclerViewLocalDatabaseAdapter(sportsmen);
        }
        else
        {
            List<Sportsman> list = saver.GetSportsmen();
            _recyclerViewLocalDatabaseAdapter = new RecyclerViewLocalDatabaseAdapter(list);
        }

        _recyclerViewDatabaseAdapter = new RecyclerViewDatabaseAdapter(mainSaver.GetSportsmen());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator1 = new DefaultItemAnimator();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        _recyclerView.setAdapter(_recyclerViewLocalDatabaseAdapter);
        _recyclerView.setLayoutManager(layoutManager);
        _recyclerView.setItemAnimator(itemAnimator);

        _recyclerViewDatabase.setAdapter(_recyclerViewDatabaseAdapter);
        _recyclerViewDatabase.setLayoutManager(layoutManager1);
        _recyclerViewDatabase.setItemAnimator(itemAnimator1);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        EmptyDataBaseCompetition();
        EmptyParticipantCompetition();
    }

    public void OnClick(View view)
    {
        _addDialog.show();
    }

    private void GenerateStandartParticipants(int firstNumber, int count, List<Sportsman> sportsmen)
    {
        int counter = 1;
        for(int i = firstNumber; i <= count + firstNumber; i++)
        {
            Sportsman sportsman = new Sportsman();
            sportsman.setInfo(i, "Спортсмен "+ String.valueOf(counter), 1996, "Россия", "Без группы");
            sportsman.setColor(Color.BLACK);
            sportsmen.add(sportsman);
            counter++;
        }
    }


    private void FindAllViews()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        //Работа с ParticipantList
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
        _nameParticipantList = (TextView) page1.findViewById(R.id.nameParticipantList);
        _birthdayParticipantList = (TextView) page1.findViewById(R.id.birthdayParticipantList);
        _countryParticipantList = (TextView) page1.findViewById(R.id.countryParticipantList);
        _numberParticipantList = (TextView) page1.findViewById(R.id.numberParticipantList);
        _nameOfParticipantList = (TextView) page1.findViewById(R.id.participant_list_head);
        _groupParticipantList = (TextView) page1.findViewById(R.id.groupParticipantList);
        _emptyParticipantList = (TextView) page1.findViewById(R.id.emptyParticipantListTextView);
        _acceptParticipantImBtn = (ImageButton) page1.findViewById(R.id.accept_participant);
        _menuParticipantImBtn = (ImageButton) page1.findViewById(R.id.menu_participant);
        _deleteParticipantImBtn = (ImageButton) page1.findViewById(R.id.delete_participant);
        _editParticipantImBtn = (ImageButton) page1.findViewById(R.id.edit_participant);

        _nameOfParticipantList.setText(Html.fromHtml("<font>"  + "<big>" + getResources().getString(R.string.participant_list_actvity_head) + "</big>" + "</font>"));


        _nameDialog = (EditText) _dialogForm.findViewById(R.id.dialogName);
        _birthdayDialog = (EditText) _dialogForm.findViewById(R.id.dialogBirthday);
        _countryDialog = (EditText) _dialogForm.findViewById(R.id.dialogCountry);
        _numberDialog = (EditText) _dialogForm.findViewById(R.id.dialogNumber);
        _colorDialog = (TextView) _dialogForm.findViewById(R.id.dialogColor);
        _spinnerOfGroup = (Spinner) _dialogForm.findViewById(R.id.spinnerOfGroup);

        //Работа с DataBaseList
        View page2 = inflater.inflate(R.layout.activity_database_list, null);
        pages.add(page2);

        _recyclerViewDatabase = (RecyclerView) page2.findViewById(R.id.gridViewDataBaseLayout);
        _headDataBase = (LinearLayout) page2.findViewById(R.id.headTableDataBaseLayout);
        _nameDataBaseList = (TextView) page2.findViewById(R.id.nameDataBase);
        _birthdayDataBaseList = (TextView) page2.findViewById(R.id.birthdayDataBase);
        _countryDataBaseList = (TextView) page2.findViewById(R.id.countryDataBase);
        _nameOfDataBaseList = (TextView) page2.findViewById(R.id.database_list_head);
        _emptyDataBaseList = (TextView) page2.findViewById(R.id.emptyDataBaseTextView);
        _acceptDataBaseImBtn = (ImageButton) page2.findViewById(R.id.accept_database);
        _deleteDataBaseImBtn = (ImageButton) page2.findViewById(R.id.delete_database);
        _menuDataBaseImBtn = (ImageButton) page2.findViewById(R.id.menu_database);
        _deleteDataBaseImBtn = (ImageButton) page2.findViewById(R.id.delete_database);
        _editDataBaseImBtn = (ImageButton) page2.findViewById(R.id.edit_database);
        _secondAcceptDataBaseImBtn = (ImageButton) page2.findViewById(R.id.second_accept_database);

        _nameOfDataBaseList.setText(Html.fromHtml("<font>"  + "<big>" + getResources().getString(R.string.db_list_activity_head) + "</big>" + "</font>"));

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);
    }

    private void SortTableBy(RecyclerView recyclerView, String orderBy, boolean sortState)
    {
        List<Sportsman> sortedList;
        if(recyclerView == _recyclerView)
        {
            sortedList = saver.GetSortedSportsmen(orderBy, sortState);
            _recyclerViewLocalDatabaseAdapter.SortList(sortedList);
        }
        else
        {
            sortedList = mainSaver.GetSortedSportsmen(orderBy, sortState);
            _recyclerViewDatabaseAdapter.SortList(sortedList);
        }

    }

    // Обработка нажатий меню сортировок
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

                    _participantPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            item.setChecked(!item.isChecked());
                            switch (item.getItemId())
                            {
                                case R.id.groupSort:
                                    SortTableBy(_recyclerView, "group", item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по группам",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.nameSort:
                                    SortTableBy(_recyclerView, "name" , item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortTableBy(_recyclerView, "year", item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    SortTableBy(_recyclerView, "country", item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по региону",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.numberSort:
                                    SortTableBy(_recyclerView, "number", item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по номеру",Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortTableBy(_recyclerViewDatabase, "year", item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    SortTableBy(_recyclerViewDatabase, "country", item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по региону",Toast.LENGTH_SHORT).show();
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
        CompetitionSaver competitionSaver = new CompetitionSaver(this);
        competitionSaver.SaveCompetitionToDatabase(_currentCompetition);
        _needDeleteTables = false;
        Toast.makeText(getApplicationContext(),"Сохранить список и перейти к соревнованию",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, CompetitionsActivity.class);
        intent.putExtra("Name", _currentCompetition.GetName());
        intent.putExtra("Date", _currentCompetition.GetDate());
        startActivity(intent);
    }

    public void OnClickEditParticipant(View view)
    {
        Sportsman sportsmanToEdit = _recyclerViewLocalDatabaseAdapter.GetCheckedSportsmen().get(0);
        _numberRenameDialog.setText(sportsmanToEdit.getNumber());
        _nameRenameDialog.setText(sportsmanToEdit.getName());
        _birthdayRenameDialog.setText(sportsmanToEdit.getYear());
        _countryRenameDialog.setText(sportsmanToEdit.getCountry());
        _colorRenameDialog.setBackgroundColor(sportsmanToEdit.getColor());
        for(int i = 0; i < _arrayGroup.length; i++)
        {
            if(_arrayGroup[i].equals(sportsmanToEdit.getGroup()))
            {
                _spinnerOfGroupRename.setSelection(i);
                break;
            }
        }
        _renameDialog.show();
        Toast.makeText(getApplicationContext(),"Редактировать участника",Toast.LENGTH_SHORT).show();
    }

    public void OnClickDeleteParticipant(View view)
    {
        List<Sportsman> listToDelete = _recyclerViewLocalDatabaseAdapter.GetCheckedSportsmen();
        _recyclerViewLocalDatabaseAdapter.RemoveSportsmen(listToDelete);
        saver.DeleteSportsmen(listToDelete);
        _recyclerViewLocalDatabaseAdapter.ResetHaveMarkedFlag();
        SetStartPosition(1,_recyclerViewLocalDatabaseAdapter.getItemCount(),0);
        Toast.makeText(getApplicationContext(),"Удаление участника",Toast.LENGTH_SHORT).show();
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
                SetStartPosition(2, _recyclerViewDatabaseAdapter.getItemCount(), 0);
                Toast.makeText(getApplicationContext(),"Участники удалены",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();


    }

//    public void OnClickEditDataBase(View view)
//    {
//        Participant[] currentParticipant = GetCheckedParticipants(_gridViewDatabase, false);
//        _numberRenameDialog.setText("");
//        _nameRenameDialog.setText(currentParticipant[0].GetFIO());
//        _birthdayRenameDialog.setText(currentParticipant[0].GetBirthYear());
//        _countryRenameDialog.setText(currentParticipant[0].GetCountry());
//        _colorDialog.setBackgroundColor(Color.BLACK);
//        _renameDialog.show();
//        Toast.makeText(getApplicationContext(),"Редактировать участника в базе данных",Toast.LENGTH_SHORT).show();
//    }

    public void OnClickAcceptDataBase(View view)
    {
        List<Sportsman> checkedList = _recyclerViewDatabaseAdapter.GetCheckedSportsmen();
        _recyclerViewDatabaseAdapter.RemoveSportsmen(checkedList);
        saver.SaveSportsmen(checkedList);
        _recyclerViewLocalDatabaseAdapter.AddSportsmen(checkedList);
        SetStartPosition(2, _recyclerViewDatabaseAdapter.getItemCount(), 0);
        Toast.makeText(getApplicationContext(),"Участники были добавлены в соревнование",Toast.LENGTH_SHORT).show();
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
        }
        else
        {
            _haveMarkedDataBase = false;
            _secondAcceptDataBaseImBtn.setVisibility(View.INVISIBLE);
            _editDataBaseImBtn.setVisibility(View.GONE);
            _acceptDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.GONE);
            _menuDataBaseImBtn.setVisibility(View.VISIBLE);
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
            _headParticipant.setVisibility(View.INVISIBLE);
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
            _headDataBase.setVisibility(View.INVISIBLE);
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
        if(_needDeleteTables)
        {
            DatabaseProvider provider = new DatabaseProvider(ViewPagerActivity.this);
            provider.DeleteTable(_currentCompetition.GetDbParticipantPath());
            provider.DeleteTable(_currentCompetition.GetSettingsPath());
        }
    }

    public void OnClickColorParticipant(View view)
    {
        android.app.FragmentManager fm = this.getFragmentManager();
        //Подумать как сделать
        //_addColorToParticipantDialog.setSelectedColor(_colorParticipant);
        _addColorToParticipantDialog.show(fm, "colorpicker");
    }


}
