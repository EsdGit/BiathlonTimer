package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
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
    private TableRow _renameTableRow;

    private AlertDialog.Builder _addDialogBuilder;
    private AlertDialog _addDialog;
    private PopupMenu _participantPopupMenu;
    private PopupMenu _dataBasePopupMenu;
    private ColorPickerDialog _addColorToParticipantDialog;
    private int _colorParticipant;
    private String[] _arrayGroup;

    // Элементы ParticipantList
    private TableLayout _tableLayoutParticipantList;
    private TextView _nameParticipantList;
    private TextView _birthdayParticipantList;
    private TextView _countryParticipantList;
    private TextView _numberParticipantList;
    private TextView _nameOfParticipantList;
    private TextView _groupParticipantList;
    private ImageButton _acceptParticipantImBtn;
    private ImageButton _deleteParticipantImBtn;
    private ImageButton _menuParticipantImBtn;
    private ImageButton _editParticipantImBtn;

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
    private AlertDialog.Builder _renameDialogBuilder;
    private AlertDialog _renameDialog;

    // Элементы DataBaseList
    private TableLayout _tableLayoutDataBaseList;
    private TextView _nameDataBaseList;
    private TextView _birthdayDataBaseList;
    private TextView _countryDataBaseList;
    private TextView _nameOfDataBaseList;
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

    private Competition _currentCompetition;

    private boolean _needDeleteTables = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        _dbSaver = new ParticipantSaver(this);

        Intent intent = getIntent();
        String name = intent.getStringExtra("CompetitionName");
        String date = intent.getStringExtra("CompetitionDate");
        String startType = intent.getStringExtra("CompetitionStartType");
        String interval = intent.getStringExtra("CompetitionInterval");
        String checkPoints = intent.getStringExtra("CompetitionCheckPointsCount");
        String[] localArray = intent.getStringArrayExtra("ArrayGroup");
        _arrayGroup = new String[localArray.length + 1];
        for(int i = 0 ; i<localArray.length;i++)
        {
            _arrayGroup[i+1] = localArray[i];
        }
        _arrayGroup[0]="Без группы";



        _needDeleteTables = Boolean.valueOf(intent.getStringExtra("NeedDelete"));
        _currentCompetition = new Competition(name, date, this);
        _currentCompetition.SetCompetitionSettings(startType, interval, checkPoints, "");
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
                Participant participant = new Participant(_numberDialog.getText().toString(),_nameDialog.getText().toString(),
                        _countryDialog.getText().toString(), _birthdayDialog.getText().toString(),"",_colorParticipant);

                 _acceptParticipantImBtn.setVisibility(View.VISIBLE);
                if(_dbSaver.SaveParticipantToDatabase(participant, DatabaseProvider.DbParticipant.TABLE_NAME))
                {
                    Toast.makeText(getApplicationContext(), "Участник добавлен",
                            Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Такой участник уже есть в базе данных",
                            Toast.LENGTH_LONG).show();
                }
                Participant[] localArr = GetParticipantsFromTable(_tableLayoutParticipantList);
                boolean needAdd = true;
                for(int i = 0; i < localArr.length; i++)
                {
                    if(participant.equals(localArr[i]))
                    {
                        needAdd = false;
                    }
                }
                if(needAdd) AddRowParticipantList(participant);
                _currentCompetition.AddParticipant(participant);
                SetStartPosition(_tableLayoutParticipantList);
                _nameDialog.setText("");
                _birthdayDialog.setText("");
                _countryDialog.setText("");
                _numberDialog.setText("");
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
        _renameDialogBuilder.setTitle("Редактирование");
        _renameDialogBuilder.setView(_renameForm);
        _renameDialogBuilder.setPositiveButton(AddDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                TableLayout currTable = (TableLayout) _renameTableRow.getParent();
                Participant localParticipant;
                String number;
                String name;
                String year;
                String country;
                int cellsCount;
                _colorParticipant = ((ColorDrawable) _colorRenameDialog.getBackground()).getColor();
                if(currTable == _tableLayoutParticipantList)
                {
                    number = ((TextView) _renameTableRow.getChildAt(0)).getText().toString();
                    name = ((TextView)_renameTableRow.getChildAt(1)).getText().toString();
                    year = ((TextView)_renameTableRow.getChildAt(2)).getText().toString();
                    country = ((TextView)_renameTableRow.getChildAt(3)).getText().toString();
                    ((TextView) _renameTableRow.getChildAt(0)).setText(_numberRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(1)).setText(_nameRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(2)).setText(_birthdayRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(3)).setText(_countryRenameDialog.getText());
                    cellsCount = 4;
                }
                else
                {
                    number = "";
                    name = ((TextView)_renameTableRow.getChildAt(0)).getText().toString();
                    year = ((TextView)_renameTableRow.getChildAt(1)).getText().toString();
                    country = ((TextView)_renameTableRow.getChildAt(2)).getText().toString();
                    ((TextView) _renameTableRow.getChildAt(0)).setText(_nameRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(1)).setText(_birthdayRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(2)).setText(_countryRenameDialog.getText());
                    cellsCount = 3;
                }

                localParticipant = new Participant(number, name, country, year, "",_colorParticipant);

                _dbSaver.DeleteParticipant(localParticipant, DatabaseProvider.DbParticipant.TABLE_NAME);
                _dbSaver.DeleteParticipant(localParticipant, _currentCompetition.GetDbParticipantPath());
                localParticipant = new Participant(_numberRenameDialog.getText().toString(), _nameRenameDialog.getText().toString(),
                        _countryRenameDialog.getText().toString(), _birthdayRenameDialog.getText().toString(), "",_colorParticipant);
                _dbSaver.SaveParticipantToDatabase(localParticipant, DatabaseProvider.DbParticipant.TABLE_NAME);
                if(currTable == _tableLayoutParticipantList)
                {
                    _dbSaver.SaveParticipantToDatabase(localParticipant, _currentCompetition.GetDbParticipantPath());
                    ((TextView) _renameTableRow.getChildAt(0)).setBackgroundColor(localParticipant.GetColor());
                    for(int j = 1; j < cellsCount; j++)
                    {
                        ((TextView) _renameTableRow.getChildAt(j)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
                    }
                }
                else
                {
                    for(int j = 0; j < cellsCount; j++)
                    {
                        ((TextView) _renameTableRow.getChildAt(j)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
                    }
                }


                SetStartPosition(currTable);
            }
        });
        _renameDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int cellsCount = 4;
                if((TableLayout)_renameTableRow.getParent() == _tableLayoutDataBaseList)
                {
                    cellsCount = 3;
                }
                for(int j = 0; j < cellsCount; j++)
                {
                    ((TextView) _renameTableRow.getChildAt(j)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
                }
                SetStartPosition((TableLayout) _renameTableRow.getParent());

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
                        Color.GREEN
                }, Color.WHITE, 4, 3);


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
        _spinnerOfGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selected = _spinnerOfGroup.getSelectedItem().toString();
                Toast.makeText(getApplicationContext(),"Выбранная группа - " + selected, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (_isFirstLoad) {
            Participant[] localArr = _dbSaver.GetAllParticipants(DatabaseProvider.DbParticipant.TABLE_NAME, DatabaseProvider.DbParticipant.COLUMN_NAME);
            for (int i = 0; i < localArr.length; i++) {
                AddRowParticipantFromBase(localArr[i]);
            }

            localArr = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
            for (int i = 0; i < localArr.length; i++) {
                AddRowParticipantList(localArr[i]);
            }
            _isFirstLoad = false;
        }
    }

    public void OnClick(View view) {
        _addDialog.show();
    }

    // Добавление участника в ParticipantList
    private void AddRowParticipantList(Participant participant) {
        TableRow newRow = new TableRow(this);
        final TextView newTextView0 = new TextView(this);
        newTextView0.setText(participant.GetNumber());
        newTextView0.setGravity(Gravity.CENTER);
        //newTextView0.setBackground(new PaintDrawable(Color.WHITE));
        newTextView0.setBackgroundColor(participant.GetColor());
        newTextView0.setLayoutParams(new TableRow.LayoutParams(_numberParticipantList.getMeasuredWidth(), _numberParticipantList.getMeasuredHeight(), 0.4f));
        ((TableRow.LayoutParams) newTextView0.getLayoutParams()).setMargins(2, 0, 2, 2);
        final TextView newTextView1 = new TextView(this);
        newTextView1.setText(participant.GetFIO());
        newTextView1.setGravity(Gravity.CENTER);
        newTextView1.setBackground(new PaintDrawable(Color.WHITE));
        newTextView1.setLayoutParams(new TableRow.LayoutParams(_nameParticipantList.getMeasuredWidth(), _nameParticipantList.getMeasuredHeight(), 3.0f));
        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView2 = new TextView(this);
        newTextView2.setText(participant.GetBirthYear());
        newTextView2.setGravity(Gravity.CENTER);
        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayParticipantList.getMeasuredWidth(), _birthdayParticipantList.getMeasuredHeight(), 0.4f));
        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView3 = new TextView(this);
        newTextView3.setText(participant.GetCountry());
        newTextView3.setGravity(Gravity.CENTER);
        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
        newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryParticipantList.getMeasuredWidth(), _countryParticipantList.getMeasuredHeight(), 0.6f));
        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
        final TextView newTextView4 = new TextView(this);
        newTextView4.setText("Юноши");
        newTextView4.setGravity(Gravity.CENTER);
        newTextView4.setBackground(new PaintDrawable(Color.WHITE));
        newTextView4.setLayoutParams(new TableRow.LayoutParams(_countryParticipantList.getMeasuredWidth(), _countryParticipantList.getMeasuredHeight(), 0.6f));
        ((TableRow.LayoutParams) newTextView4.getLayoutParams()).setMargins(0, 0, 2, 2);
        newRow.addView(newTextView0);
        newRow.addView(newTextView1);
        newRow.addView(newTextView2);
        newRow.addView(newTextView3);
        newRow.addView(newTextView4);
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
                        PaintDrawable drawable = (PaintDrawable) rowView.get(1).getBackground();
                        if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary))
                        {
                            _counterMarkedParticipant--;
                            String name = ((TextView) rowView.get(1)).getText().toString();
                            String year = ((TextView) rowView.get(2)).getText().toString();
                            String country = ((TextView) rowView.get(3)).getText().toString();
                            Participant currParticipant = new Participant("",name,country,year,"",1);
                            Participant[] localArr = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
                            for(int i = 0; i < rowView.size(); i++)
                            {
                                rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
                            }

                            for(int j = 0; j<localArr.length;j++)
                            {
                                if(currParticipant.equals(localArr[j]))
                                {
                                    rowView.get(0).setBackgroundColor(localArr[j].GetColor());
                                    break;
                                }
                            }

                        }
                        else
                        {
                            _counterMarkedParticipant++;
                            for(int i = 0; i<4;i++)
                            {
                                rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
                            }
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
        });
        _tableLayoutParticipantList.addView(newRow);
        SetStartPosition(_tableLayoutParticipantList);
    }

    private void AddRowParticipantFromBase(Participant participant) {
        TableRow newRow = new TableRow(this);
        TextView newTextView1 = new TextView(this);
        newTextView1.setText(participant.GetFIO());
        newTextView1.setGravity(Gravity.CENTER);
        newTextView1.setBackground(new PaintDrawable(Color.WHITE));
        newTextView1.setLayoutParams(new TableRow.LayoutParams(_nameDataBaseList.getMeasuredWidth(), _nameDataBaseList.getMeasuredHeight(), 3f));
        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(2, 0, 2, 2);
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
        newRow.addView(newTextView1);
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

        _renameForm = inflater.inflate(R.layout.dialog_activity_add_participant, null);
        _numberRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogNumber);
        _nameRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogName);
        _birthdayRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogBirthday);
        _countryRenameDialog = (EditText) _renameForm.findViewById(R.id.dialogCountry);
        _colorRenameDialog = (TextView) _renameForm.findViewById(R.id.dialogColor);


        _dialogForm = inflater.inflate(R.layout.dialog_activity_add_participant, null);
        _tableLayoutParticipantList = (TableLayout) page1.findViewById(R.id.tableParticipantListLayout);
        _nameParticipantList = (TextView) page1.findViewById(R.id.nameParticipantList);
        _birthdayParticipantList = (TextView) page1.findViewById(R.id.birthdayParticipantList);
        _countryParticipantList = (TextView) page1.findViewById(R.id.countryParticipantList);
        _numberParticipantList = (TextView) page1.findViewById(R.id.numberParticipantList);
        _nameOfParticipantList = (TextView) page1.findViewById(R.id.participant_list_head);
        _groupParticipantList = (TextView) page1.findViewById(R.id.groupParticipantList);
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

        _tableLayoutDataBaseList = (TableLayout) page2.findViewById(R.id.tableDataBaseLayout);
        _nameDataBaseList = (TextView) page2.findViewById(R.id.nameDataBase);
        _birthdayDataBaseList = (TextView) page2.findViewById(R.id.birthdayDataBase);
        _countryDataBaseList = (TextView) page2.findViewById(R.id.countryDataBase);
        _nameOfDataBaseList = (TextView) page2.findViewById(R.id.database_list_head);
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

    private void SortTableBy(TableLayout table, String orderBy, boolean sortState)
    {
        String localOrderString;
        String localTableName = "";
        Participant[] localArr;
        if(sortState)
        {
            localOrderString = orderBy + " ASC";
        }
        else
        {
            localOrderString = orderBy + " DESC";
        }
        table.removeAllViews();

        if(table == _tableLayoutDataBaseList)
        {
            localTableName = DatabaseProvider.DbParticipant.TABLE_NAME;
            localArr = _dbSaver.GetAllParticipants(localTableName, localOrderString);
            for(int i = 0; i < localArr.length; i++)
            {
                AddRowParticipantFromBase(localArr[i]);
            }
        }
        else
        {
            localTableName = _currentCompetition.GetDbParticipantPath();
            localArr = _dbSaver.GetAllParticipants(localTableName, localOrderString);
            for(int i = 0; i < localArr.length; i++)
            {
                AddRowParticipantList(localArr[i]);
            }
        }


    }

    private void SortByYear(TableLayout table,boolean sortState, boolean sortByYear)
    {
        //Participant[] localArr = GetParticipantsFromTable(table);
        Participant[] localArr = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
        int participantCount = localArr.length;
        table.removeAllViews();
        int k = 0;
        Participant helpPart;
        int firstParticipantData;
        int secondParticipantData;
        while(k != participantCount)
        {
            for(int i = 0; i < participantCount - k - 1; i++)
            {
                if(sortByYear)
                {
                    firstParticipantData = Integer.valueOf(localArr[i].GetBirthYear());
                    secondParticipantData = Integer.valueOf(localArr[i + 1].GetBirthYear());
                }
                else
                {
                    try {
                        firstParticipantData = Integer.valueOf(localArr[i].GetNumber());
                    }catch (Exception e) {firstParticipantData = 0;}
                    try {
                        secondParticipantData = Integer.valueOf(localArr[i + 1].GetNumber());
                    }catch (Exception e1) {secondParticipantData = 0;}
                }
                if(sortState)
                {
                    if (firstParticipantData > secondParticipantData) {
                        helpPart = localArr[i];
                        localArr[i] = localArr[i + 1];
                        localArr[i + 1] = helpPart;
                    }
                }
                else
                {
                    if (firstParticipantData < secondParticipantData) {
                        helpPart = localArr[i];
                        localArr[i] = localArr[i + 1];
                        localArr[i + 1] = helpPart;
                    }
                }
            }
            k++;
        }
        if(table == _tableLayoutDataBaseList)
        {
            for (int j = 0; j < participantCount; j++)
            {
                AddRowParticipantFromBase(localArr[j]);
            }
        }
        else
        {
            for (int j = 0; j < participantCount; j++)
            {
                AddRowParticipantList(localArr[j]);
            }
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
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по группам",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.nameSort:
                                    SortTableBy(_tableLayoutParticipantList, DatabaseProvider.DbParticipant.COLUMN_NAME, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortByYear(_tableLayoutParticipantList, item.isChecked(), true);
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    SortTableBy(_tableLayoutParticipantList, DatabaseProvider.DbParticipant.COLUMN_COUNTRY, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по региону",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.numberSort:
                                    SortByYear(_tableLayoutParticipantList, item.isChecked(), false);
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
                                    SortTableBy(_tableLayoutDataBaseList, DatabaseProvider.DbParticipant.COLUMN_NAME, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    SortByYear(_tableLayoutDataBaseList, item.isChecked(),true);
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

    private Participant[] GetParticipantsFromTable(TableLayout table)
    {
        int participantCount = table.getChildCount();
        Participant[] localArr = new Participant[participantCount];
        String[] dataArr;
        int cellsCount;
        if(table == _tableLayoutDataBaseList)
        {
            cellsCount = 3;
            dataArr = new String[cellsCount];
        }
        else
        {
            cellsCount = 4;
            dataArr = new String[cellsCount];
        }
        for(int i = 0; i<participantCount; i++)
        {
            for(int j = 0; j < cellsCount; j++)
            {
                dataArr[j] = ((TextView)((TableRow) table.getChildAt(i)).getChildAt(j)).getText().toString();
            }
            if(table == _tableLayoutParticipantList) localArr[i] = new Participant(dataArr[0],dataArr[1], dataArr[3], dataArr[2],"",1);
            else localArr[i] = new Participant("",dataArr[0], dataArr[2], dataArr[1], "",1);
        }

        return localArr;
    }

    private Participant[] GetCheckedParticipants(TableLayout table, boolean needDelete)
    {
        // Переделать!!!
        int participantCount = table.getChildCount();
        TableRow row;
        TextView textView;
        int cellsCount = 3;
        String[] dataArr = new String[cellsCount];
        ArrayList<Participant> localArr = new ArrayList<Participant>();
        boolean flag = false;
        int k = 0;
        int color = 1;
        if(table == _tableLayoutParticipantList)
        {
            cellsCount = 4;
            dataArr = new String[cellsCount];
        }


        for(int i = 0; i < participantCount - k; i++)
        {
            row = (TableRow) table.getChildAt(i);
            textView = (TextView) row.getChildAt(1);
            if(((PaintDrawable)(textView).getBackground()).getPaint().getColor() ==
                    getResources().getColor(R.color.colorPrimary))
            {
                for(int j = 0; j < cellsCount; j++)
                {
                    dataArr[j] = ((TextView)row.getChildAt(j)).getText().toString();
                }
                flag = true;
            }

            if(flag)
            {
                flag = false;
                if(table == _tableLayoutParticipantList)
                {
                    Participant[] arrFromBase = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
                    Participant localPart = new Participant("",dataArr[1], dataArr[3], dataArr[2],"",1);
                    for(int j = 0; j<arrFromBase.length;j++)
                    {
                        if(localPart.equals(arrFromBase[j]))
                        {
                            color = arrFromBase[j].GetColor();
                            localArr.add(arrFromBase[j]);
                            break;
                        }
                    }

                }
                else
                {
//                    Participant[] arrFromBase = _dbSaver.GetAllParticipants(DatabaseProvider.DbParticipant.TABLE_NAME, DatabaseProvider.DbParticipant.COLUMN_NAME);
//                    Participant localPart = new Participant("",dataArr[0], dataArr[2], dataArr[1],"",1);
//                    for(int j = 0; j<arrFromBase.length;j++)
//                    {
//                        if(localPart.equals(arrFromBase[i]))
//                        {
//                            color = arrFromBase[i].GetColor();
//                            break;
//                        }
//                    }
                    localArr.add(new Participant("",dataArr[0], dataArr[2], dataArr[1], "",-1));
                }
                if(needDelete) table.removeViewAt(i);
                else
                {
                    _renameTableRow = row;
                }
                k++;
                i--;
            }
        }
        return localArr.toArray(new Participant[localArr.size()]);
    }

    public void OnClickAcceptParticipant(View view)
    {
        CompetitionSaver competitionSaver = new CompetitionSaver(this);
        competitionSaver.SaveCompetitionToDatabase(_currentCompetition);
        _needDeleteTables = false;
        Toast.makeText(getApplicationContext(),"Сохранить список и перейти к соревнованию",Toast.LENGTH_SHORT).show();
    }

    public void OnClickEditParticipant(View view)
    {
        Participant[] currentParticipant = GetCheckedParticipants(_tableLayoutParticipantList, false);
        _numberRenameDialog.setText(currentParticipant[0].GetNumber());
        _nameRenameDialog.setText(currentParticipant[0].GetFIO());
        _birthdayRenameDialog.setText(currentParticipant[0].GetBirthYear());
        _countryRenameDialog.setText(currentParticipant[0].GetCountry());
        _colorRenameDialog.setBackgroundColor(currentParticipant[0].GetColor());
        _renameDialog.show();
        Toast.makeText(getApplicationContext(),"Редактировать участника",Toast.LENGTH_SHORT).show();
    }

    public void OnClickDeleteParticipant(View view)
    {
        if(_tableLayoutParticipantList.getChildCount() == 0)
        {
            _acceptParticipantImBtn.setVisibility(View.GONE);
        }
        Participant[] myArr = GetCheckedParticipants(_tableLayoutParticipantList, true);
        for(int i = 0; i<myArr.length; i++)
        {
            _currentCompetition.DeleteParticipantsFromCompetition(myArr[i]);
            _dbSaver.DeleteParticipant(myArr[i], _currentCompetition.GetDbParticipantPath());
        }
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
                Participant[] myArr = GetCheckedParticipants(_tableLayoutDataBaseList, true);
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
        Participant[] currentParticipant = GetCheckedParticipants(_tableLayoutDataBaseList, false);
        _numberRenameDialog.setText("");
        _nameRenameDialog.setText(currentParticipant[0].GetFIO());
        _birthdayRenameDialog.setText(currentParticipant[0].GetBirthYear());
        _countryRenameDialog.setText(currentParticipant[0].GetCountry());
        _colorDialog.setBackgroundColor(-1);
        _renameDialog.show();
        Toast.makeText(getApplicationContext(),"Редактировать участника в базе данных",Toast.LENGTH_SHORT).show();
    }

    public void OnClickAcceptDataBase(View view)
    {
        Participant[] localArr = GetCheckedParticipants(_tableLayoutDataBaseList, true);
        Participant[] localCompParticipants = GetParticipantsFromTable(_tableLayoutParticipantList);
        boolean localFlag;
        for(int i = 0; i<localArr.length; i++)
        {
            localFlag = true;
            for(int j = 0; j<localCompParticipants.length; j++)
            {
                if(localArr[i].equals(localCompParticipants[j]))
                {
                    localFlag = false;
                    break;
                }
            }
            if(localFlag)
            {
                AddRowParticipantList(localArr[i]);
                _currentCompetition.AddParticipant(localArr[i]);
            }
        }
        SetStartPosition(_tableLayoutDataBaseList);
        Toast.makeText(getApplicationContext(),"Участники были добавлены в соревнование",Toast.LENGTH_SHORT).show();
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
            _counterMarkedParticipant = 0;
        }
        else
        {
            _haveMarkedDataBase = false;
            _secondAcceptDataBaseImBtn.setVisibility(View.INVISIBLE);
            _editDataBaseImBtn.setVisibility(View.GONE);
            _acceptDataBaseImBtn.setVisibility(View.GONE);
            _deleteDataBaseImBtn.setVisibility(View.GONE);
            _menuDataBaseImBtn.setVisibility(View.VISIBLE);
            _counterMarkedDataBase = 0;
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
            _secondAcceptDataBaseImBtn.setVisibility(View.INVISIBLE);
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
                    if(_needDeleteTables)
                    {
                        DatabaseProvider provider = new DatabaseProvider(ViewPagerActivity.this);
                        provider.DeleteTable(_currentCompetition.GetDbParticipantPath());
                        provider.DeleteTable(_currentCompetition.GetSettingsPath());
                    }
                    Intent intent = new Intent(ViewPagerActivity.this, MainActivity.class);
                    ViewPagerActivity.this.finish();
                    startActivity(intent);
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


    public void OnClickColorParticipant(View view)
    {
        android.app.FragmentManager fm = this.getFragmentManager();
        _addColorToParticipantDialog.setSelectedColor(_colorParticipant);
        _addColorToParticipantDialog.show(fm, "colorpicker");
    }
}
