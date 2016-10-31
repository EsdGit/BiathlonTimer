package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.PaintDrawable;
import android.icu.text.MessagePattern;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.DatabaseClasses.CompetitionSaver;
import com.esd.esd.biathlontimer.DatabaseClasses.DatabaseProvider;
import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.Participant;
import com.esd.esd.biathlontimer.R;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity
{
    private ParticipantSaver _dbSaver;
    private boolean _isFirstLoad = true;
    private boolean _haveMarkedParticipant = false;
    private boolean _haveMarkedDataBase = false;
    private int _counterMarkedParticipant;
    private int _counterMarkedDataBase;

    private AlertDialog.Builder _addDialogBuilder;
    private AlertDialog _addDialog;
    private PopupMenu _participantPopupMenu;
    private PopupMenu _dataBasePopupMenu;

    // Элементы ParticipantList
    private TableLayout _tableLayoutParticipantList;
    private TextView _nameParticipantList;
    private TextView _birthdayParticipantList;
    private TextView _countryParticipantList;
    private ImageButton _acceptParticipantImBtn;
    private ImageButton _deleteParticipantImBtn;
    private ImageButton _menuParticipantImBtn;
    private ImageButton _editParticipantImBtn;

    // Элементы AlertDialog
    private EditText _nameDialog;
    private EditText _birthdayDialog;
    private EditText _countryDialog;

    // Элементы DataBaseList
    private TableLayout _tableLayoutDataBaseList;
    private TextView _nameDataBaseList;
    private TextView _birthdayDataBaseList;
    private TextView _countryDataBaseList;
    private ImageButton _acceptDataBaseImBtn;
    private ImageButton _deleteDataBaseImBtn;
    private ImageButton _menuDataBaseImBtn;
    private ImageButton _editDataBaseImBtn;
    private ImageButton _secondAcceptDataBaseImBtn;

    //Диалог добавления
    private View _dialogForm;
    private static String TitleDialog = "Добавление участника";
    private static String AddDialogBtn = "Добавить";
    private static String CancelDialogBtn = "Отменить";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _dbSaver = new ParticipantSaver(this);

        FindAllViews();

        // Создание диалогового окна
        _addDialogBuilder = new AlertDialog.Builder(this);
        _addDialogBuilder.setTitle(TitleDialog);  // заголовок
        _addDialogBuilder.setView(_dialogForm);

        // Действия по кнопке "Добавить"
        _addDialogBuilder.setPositiveButton(AddDialogBtn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Participant participant = new Participant(_nameDialog.getText().toString(),
                        _countryDialog.getText().toString(), _birthdayDialog.getText().toString());
                AddRowParticipantList(participant);
                _acceptParticipantImBtn.setVisibility(View.VISIBLE);
                _dbSaver.SaveParticipantToDatabase(participant, DatabaseProvider.DbParticipant.TABLE_NAME);
                _nameDialog.setText("");
                _birthdayDialog.setText("");
                _countryDialog.setText("");
                Toast.makeText(getApplicationContext(), "Участник добавлен",
                        Toast.LENGTH_LONG).show();
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

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (_isFirstLoad) {
            Participant[] localArr = _dbSaver.GetAllParticipants(DatabaseProvider.DbParticipant.TABLE_NAME, DatabaseProvider.DbParticipant.COLUMN_NAME);
            for (int i = 0; i < localArr.length; i++) {
                AddRowParticipantFromBase(localArr[i]);
            }

            //localArr = _dbSaver.GetAllParticipants("TEST");
            //for (int i = 0; i < localArr.length; i++) {
            //    AddRowParticipantList(localArr[i]);
            //}
            _isFirstLoad = false;
        }
    }

    public void OnClick(View view) {
        _addDialog.show();
    }

    // Добавление участника в ParticipantList
    private void AddRowParticipantList(Participant participant) {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(participant.GetFIO());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackground(new PaintDrawable(Color.WHITE));
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameParticipantList.getMeasuredWidth(), _nameParticipantList.getMeasuredHeight(), 3f));
        ((TableRow.LayoutParams) newTextView.getLayoutParams()).setMargins(2, 0, 2, 2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(participant.GetBirthYear());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayParticipantList.getMeasuredWidth(), _birthdayParticipantList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
        TextView newTextView3 = new TextView(this);
        newTextView3.setText(participant.GetCountry());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryParticipantList.getMeasuredWidth(), _countryParticipantList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        newRow.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ArrayList<View> rowView = new ArrayList<View>();
                v.addChildrenForAccessibility(rowView);
                if(!_haveMarkedParticipant)
                {
                    _haveMarkedParticipant = true;
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
                if(!_haveMarkedParticipant)return;
                if (_counterMarkedParticipant == 0) {
                    // Если отмечен первый
                    _counterMarkedParticipant++;
                    SetEditPosition(_tableLayoutParticipantList);
                }
                else
                {
                    // Есди уже были отмечены
                    for (int i = 0; i < rowView.size(); i++)
                    {
                        PaintDrawable drawable = (PaintDrawable) rowView.get(i).getBackground();
                        if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary))
                        {
                            if(i == 0)
                            {
                                _counterMarkedParticipant--;
                            }
                            rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
                        }
                        else
                        {
                            if(i == 0)
                            {
                                _counterMarkedParticipant++;
                            }
                            rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
                        }
                    }
                    switch (_counterMarkedParticipant)
                    {
                        case 0:
                            SetStartPosition(_tableLayoutParticipantList);
                            _haveMarkedParticipant = false;
                            break;
                        case 1:
                            SetEditPosition(_tableLayoutParticipantList);
                            break;
                        default:
                            SetDelPosition(_tableLayoutParticipantList);
                            break;
                    }
                }
            }
        });
        _tableLayoutParticipantList.addView(newRow);
    }

    private void AddRowParticipantFromBase(Participant participant) {
        TableRow newRow = new TableRow(this);
        TextView newTextView = new TextView(this);
        newTextView.setText(participant.GetFIO());
        newTextView.setGravity(Gravity.CENTER);
        newTextView.setBackground(new PaintDrawable(Color.WHITE));
        newTextView.setLayoutParams(new TableRow.LayoutParams(_nameDataBaseList.getMeasuredWidth(), _nameDataBaseList.getMeasuredHeight(), 3f));
        ((TableRow.LayoutParams) newTextView.getLayoutParams()).setMargins(2, 0, 2, 2);
        TextView newTextView2 = new TextView(this);
        newTextView2.setText(participant.GetBirthYear());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayDataBaseList.getMeasuredWidth(), _birthdayDataBaseList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
        TextView newTextView3 = new TextView(this);
        newTextView3.setText(participant.GetCountry());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryDataBaseList.getMeasuredWidth(), _countryDataBaseList.getMeasuredHeight(), 0.5f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
        newRow.addView(newTextView);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        newRow.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                ArrayList<View> rowView = new ArrayList<View>();
                v.addChildrenForAccessibility(rowView);
                if(!_haveMarkedDataBase)
                {
                    _haveMarkedDataBase = true;
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
                if(!_haveMarkedDataBase) return;
                if (_counterMarkedDataBase == 0)
                {
                    // Если отмечен первый
                    _counterMarkedDataBase++;
                    SetEditPosition(_tableLayoutDataBaseList);
                }
                else
                {
                    // Есди уже были отмечены
                    for (int i = 0; i < rowView.size(); i++)
                    {
                        PaintDrawable drawable = (PaintDrawable) rowView.get(i).getBackground();
                        if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary))
                        {
                            if(i == 0)
                            {
                                _counterMarkedDataBase--;
                            }
                            rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
                        }
                        else
                        {
                            if(i == 0)
                            {
                                _counterMarkedDataBase++;
                            }
                            rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
                        }
                    }
                    switch (_counterMarkedDataBase)
                    {
                        case 0:
                            SetStartPosition(_tableLayoutDataBaseList);
                            _haveMarkedDataBase = false;
                            break;
                        case 1:
                            SetEditPosition(_tableLayoutDataBaseList);
                            break;
                        default:
                            SetDelPosition(_tableLayoutDataBaseList);
                            break;
                    }
                }
            }
        });
        _tableLayoutDataBaseList.addView(newRow);
    }

    private void FindAllViews()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<>();

        //Работа с ParticipantList
        View page1 = inflater.inflate(R.layout.activity_participant_list, null);
        pages.add(page1);

        _dialogForm = inflater.inflate(R.layout.dialog_activity_add_participant, null);
        _tableLayoutParticipantList = (TableLayout) page1.findViewById(R.id.tableParticipantListLayout);
        _nameParticipantList = (TextView) page1.findViewById(R.id.nameParticipantList);
        _birthdayParticipantList = (TextView) page1.findViewById(R.id.birthdayParticipantList);
        _countryParticipantList = (TextView) page1.findViewById(R.id.countryParticipantList);
        _acceptParticipantImBtn = (ImageButton) page1.findViewById(R.id.accept_participant);
        _menuParticipantImBtn = (ImageButton) page1.findViewById(R.id.menu_participant);
        _deleteParticipantImBtn = (ImageButton) page1.findViewById(R.id.delete_participant);
        _editParticipantImBtn = (ImageButton) page1.findViewById(R.id.edit_participant);


        _nameDialog = (EditText) _dialogForm.findViewById(R.id.dialogName);
        _birthdayDialog = (EditText) _dialogForm.findViewById(R.id.dialogBirthday);
        _countryDialog = (EditText) _dialogForm.findViewById(R.id.dialogCountry);

        //Работа с DataBaseList
        View page2 = inflater.inflate(R.layout.activity_database_list, null);
        pages.add(page2);

        _tableLayoutDataBaseList = (TableLayout) page2.findViewById(R.id.tableDataBaseLayout);
        _nameDataBaseList = (TextView) page2.findViewById(R.id.nameDataBase);
        _birthdayDataBaseList = (TextView) page2.findViewById(R.id.birthdayDataBase);
        _countryDataBaseList = (TextView) page2.findViewById(R.id.countryDataBase);
        _acceptDataBaseImBtn = (ImageButton) page2.findViewById(R.id.accept_database);
        _deleteDataBaseImBtn = (ImageButton) page2.findViewById(R.id.delete_database);
        _menuDataBaseImBtn = (ImageButton) page2.findViewById(R.id.menu_database);
        _deleteDataBaseImBtn = (ImageButton) page2.findViewById(R.id.delete_database);
        _editDataBaseImBtn = (ImageButton) page2.findViewById(R.id.edit_database);
        _secondAcceptDataBaseImBtn = (ImageButton) page2.findViewById(R.id.second_accept_database);

        PagerAdapterHelper pagerAdapter = new PagerAdapterHelper(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);
        setContentView(viewPager);
    }

    private void MakeNewCompetition()
    {
        Competition competition = new Competition("NewCompetition", "29.02.2017", "", "NewCompetition");
        // Создаём соревнование, генерируем новую таблицу в БД, туда закидываем участников и так далее...
    }

    private void SortTableBy(TableLayout table, String orderBy, boolean sortState)
    {
        String localOrderString;
        String localTableName = "";
        if(table == _tableLayoutDataBaseList)
        {
            localTableName = DatabaseProvider.DbParticipant.TABLE_NAME;
        }
        else
        {
            // Из объекта competition
        }
        if(sortState)
        {
            localOrderString = orderBy + " ASC";
        }
        else
        {
            localOrderString = orderBy + " DESC";
        }
        table.removeAllViews();
        Participant[] localArr = _dbSaver.GetAllParticipants(localTableName, localOrderString);
        for(int i = 0; i < localArr.length; i++)
        {
            AddRowParticipantFromBase(localArr[i]);
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
                                case R.id.nameSort:
                                    SortTableBy(_tableLayoutParticipantList, DatabaseProvider.DbParticipant.COLUMN_NAME, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по региону",Toast.LENGTH_SHORT).show();
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
                    _dataBasePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
                    {
                        @Override
                        public boolean onMenuItemClick(MenuItem item)
                        {
                            item.setChecked(!item.isChecked());
                            switch (item.getItemId())
                            {
                                case R.id.nameSort:
                                    SortTableBy(_tableLayoutDataBaseList, DatabaseProvider.DbParticipant.COLUMN_NAME, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortTableBy(_tableLayoutDataBaseList, DatabaseProvider.DbParticipant.COLUMN_YEAR, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    SortTableBy(_tableLayoutDataBaseList, DatabaseProvider.DbParticipant.COLUMN_COUNTRY, item.isChecked());
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

    private Participant[] GetParticipantsFromTable()
    {
        int participantCount = _tableLayoutParticipantList.getChildCount();
        Participant[] localArr = new Participant[participantCount];
        String[] dataArr = new String[3];
        for(int i = 0; i<participantCount; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                dataArr[j] = ((TextView)((TableRow) _tableLayoutParticipantList.getChildAt(i)).getChildAt(j)).getText().toString();
            }
            localArr[i] = new Participant(dataArr[0], dataArr[1], dataArr[2]);
        }

        return localArr;
    }

    private Participant[] GetCheckedParticipants(TableLayout table)
    {
        int participantCount = table.getChildCount();
        TableRow row;
        TextView textView;
        String[] dataArr = new String[3];
        ArrayList<Participant> localArr = new ArrayList<Participant>();
        boolean flag = false;
        int k = 0;
        for(int i = 0; i < participantCount - k; i++)
        {
            row = (TableRow) table.getChildAt(i);
            for(int j = 0; j < 3; j++)
            {
                textView = (TextView) row.getChildAt(j);
                if(((PaintDrawable)(textView).getBackground()).getPaint().getColor() ==
                        getResources().getColor(R.color.colorPrimary))
                {
                    dataArr[j] = textView.getText().toString();
                    flag = true;
                }
            }
            if(flag)
            {
                flag = false;
                localArr.add(new Participant(dataArr[0], dataArr[2], dataArr[1]));
                table.removeViewAt(i);
                k++;
                i--;
            }
        }
        return localArr.toArray(new Participant[localArr.size()]);
    }

    public void OnClickAcceptParticipant(View view)
    {
        Competition competition = new Competition("Чм России 2015", "28.09.2017", "Test1", "TEST" );

        DatabaseProvider databaseProvider = new DatabaseProvider(this);
        databaseProvider.AddNewTable("TEST");
        Participant[] localArr = GetParticipantsFromTable();
        competition.AddParticipants(localArr);
        for(int i = 0; i < localArr.length; i++)
        {
            _dbSaver.SaveParticipantToDatabase(localArr[i], "TEST");
        }
        CompetitionSaver _competitionSaver = new CompetitionSaver(this);
        _competitionSaver.SaveCompetitionToDatabase(competition);
        Toast.makeText(getApplicationContext(),"Сохранить список и перейти к соревнованию",Toast.LENGTH_SHORT).show();
    }

    public void OnClickEditParticipant(View view)
    {
        Toast.makeText(getApplicationContext(),"Редактировать участника",Toast.LENGTH_SHORT).show();
    }

    public void OnClickDeleteParticipant(View view)
    {
        if(_tableLayoutParticipantList.getChildCount() == 0)
        {
            _acceptParticipantImBtn.setVisibility(View.GONE);
        }
        Participant[] myArr = GetCheckedParticipants(_tableLayoutParticipantList);
        // Удаление из локальной базы
        SetStartPosition(_tableLayoutParticipantList);
        Toast.makeText(getApplicationContext(),"Удаление участника",Toast.LENGTH_SHORT).show();
    }

    public void OnClickDeleteDataBAse(View view)
    {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Удаление");
        dialog.setMessage("Вы уверены, что хотите удалить участников из базы данных?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Participant[] myArr = GetCheckedParticipants(_tableLayoutDataBaseList);
                for(int j = 0; j<myArr.length;j++)
                {
                    _dbSaver.DeleteParticipant(myArr[j], DatabaseProvider.DbParticipant.TABLE_NAME);
                }
                SetStartPosition(_tableLayoutDataBaseList);
                Toast.makeText(getApplicationContext(),"Участники удалены",Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();


    }

    public void OnClickEditDataBase(View view)
    {
        Toast.makeText(getApplicationContext(),"Редактировать участника в базе данных",Toast.LENGTH_SHORT).show();
    }

    public void OnClickAcceptDataBase(View view)
    {
        Participant[] localArr = GetCheckedParticipants(_tableLayoutDataBaseList);
        // Надо проверять нет ли таких уже участников
        for(int i = 0; i<localArr.length; i++)
        {
            AddRowParticipantList(localArr[i]);
        }
        SetStartPosition(_tableLayoutDataBaseList);
        Toast.makeText(getApplicationContext(),"Перенести фамилии из базы данных",Toast.LENGTH_SHORT).show();
    }

    private void SetStartPosition(TableLayout table)
    {

        if(table == _tableLayoutParticipantList)
        {
            if(table.getChildCount()== 0)
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
            _haveMarkedParticipant = false;
        }
        else
        {
            _haveMarkedDataBase = false;
            _secondAcceptDataBaseImBtn.setVisibility(View.GONE);
            _editDataBaseImBtn.setVisibility(View.GONE);
            _acceptDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.GONE);
            _menuDataBaseImBtn.setVisibility(View.VISIBLE);
        }
    }

    private void SetEditPosition(TableLayout table)
    {
        if(table == _tableLayoutParticipantList)
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

    private void SetDelPosition(TableLayout table)
    {
        if(table == _tableLayoutParticipantList)
        {
            _acceptParticipantImBtn.setVisibility(View.GONE);
            _editParticipantImBtn.setVisibility(View.GONE);
            _menuParticipantImBtn.setVisibility(View.GONE);
            _deleteParticipantImBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            _secondAcceptDataBaseImBtn.setVisibility(View.GONE);
            _editDataBaseImBtn.setVisibility(View.GONE);
            _acceptDataBaseImBtn.setVisibility(View.VISIBLE);
            _menuDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == (KeyEvent.KEYCODE_BACK))
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Предупреждение");
            dialog.setMessage("Вы уверены, что хотите выйти?");
            dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // возможно всё надо сохранить)
                    ViewPagerActivity.this.finish();
                }
            });
            dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        return super.onKeyDown(keyCode, event);
    }
}
