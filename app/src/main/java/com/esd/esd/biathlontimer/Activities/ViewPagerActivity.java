package com.esd.esd.biathlontimer.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
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
import com.esd.esd.biathlontimer.PagerAdapterHelper;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.RecyclerViewAdapter;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity
{
    //private ParticipantSaver _dbSaver;
    private boolean _isFirstLoad = true;
    private boolean _haveMarkedParticipant = false;
    private boolean _haveMarkedDataBase = false;
    private int _counterMarkedParticipant;
    private int _counterMarkedDataBase;
    private TableRow _renameTableRow;
    private TextView _emptyDataBaseList;
    private TextView _emptyParticipantList;
    private LinearLayout _headDataBase;
    private LinearLayout _headParticipant;
    private AlertDialog.Builder _addDialogBuilder;
    private AlertDialog _addDialog;
    private PopupMenu _participantPopupMenu;
    private PopupMenu _dataBasePopupMenu;
    private RecyclerView _recyclerView;
    private ColorPickerDialog _addColorToParticipantDialog;
    private int _colorParticipant;
    private String[] _arrayGroup;

    // Элементы ParticipantList
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
    private Spinner _spinnerOfGroupRename;
    private AlertDialog.Builder _renameDialogBuilder;
    private AlertDialog _renameDialog;

    // Элементы DataBaseList
    private GridView _gridViewDatabase;
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
    private static String TitleDialog;
    private static String AddDialogBtn;
    private static String CancelDialogBtn;
    private static String DefaultGroup;

    private Competition _currentCompetition;

    private boolean _needDeleteTables = false;

    private Context _viewPagerContext;
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
//                Participant participant = new Participant(_numberDialog.getText().toString(),_nameDialog.getText().toString(),
//                        _countryDialog.getText().toString(), _birthdayDialog.getText().toString(),_spinnerOfGroup.getSelectedItem().toString(),_colorParticipant);
//
//                 _acceptParticipantImBtn.setVisibility(View.VISIBLE);
//                if(_dbSaver.SaveParticipantToDatabase(participant, DatabaseProvider.DbParticipant.TABLE_NAME))
//                {
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.participant_added),
//                            Toast.LENGTH_LONG).show();
//                }
//                else
//                {
//                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.participant_already_exists_in_database),
//                            Toast.LENGTH_LONG).show();
//                }
//                _dbSaver.SaveParticipantToDatabase(participant, _currentCompetition.GetDbParticipantPath());
//                Participant[] localArr = GetParticipantsFromTable(_gridView);
//                boolean needAdd = true;
//                for(int i = 0; i < localArr.length; i++)
//                {
//                    if(participant.equals(localArr[i]))
//                    {
//                        needAdd = false;
//                    }
//                }
//                if(needAdd) AddRowParticipantList(participant);


//                _currentCompetition.AddParticipant(participant);
//                SetStartPosition(_gridView);
//                _nameDialog.setText("");
//                _birthdayDialog.setText("");
//                _countryDialog.setText("");
//                _numberDialog.setText("");
//                _colorDialog.setBackgroundColor(Color.BLACK);
//                _addColorToParticipantDialog.setSelectedColor(Color.BLACK);
//                _spinnerOfGroup.setSelection(0);

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
                    ((TextView) _renameTableRow.getChildAt(0)).setText(_numberRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(1)).setText(_nameRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(2)).setText(_birthdayRenameDialog.getText());
                    ((TextView) _renameTableRow.getChildAt(3)).setText(group);
                    ((TextView) _renameTableRow.getChildAt(4)).setText(_countryRenameDialog.getText());

                    for(int j = 0; j < 5; j++)
                    {
                        ((TextView) _renameTableRow.getChildAt(j)).setTextColor(_colorParticipant);
                    }
                    cellsCount = 5;
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

                for(int j = 0; j < cellsCount; j++)
                {
                    ((TextView) _renameTableRow.getChildAt(j)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
                }

                _colorDialog.setBackgroundColor(Color.BLACK);
                SetStartPosition(currGridView);
            }
        });
        _renameDialogBuilder.setNegativeButton(CancelDialogBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name;
                String year;
                String country;

                int cellsCount = 5;
                if((GridView)_renameTableRow.getParent() == _gridViewDatabase)
                {
                    cellsCount = 3;
                    ((TextView) _renameTableRow.getChildAt(0)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
                }
                else
                {
                    name = ((TextView)_renameTableRow.getChildAt(1)).getText().toString();
                    year = ((TextView)_renameTableRow.getChildAt(2)).getText().toString();
                    country = ((TextView)_renameTableRow.getChildAt(4)).getText().toString();
                   // Participant currPart = new Participant("",name,country,year,DefaultGroup,-1);
                  //  Participant[] localArr = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
                    ((TextView) _renameTableRow.getChildAt(0)).setBackgroundColor(-1);

                }
                for(int j = 0; j < cellsCount; j++)
                {
                    ((TextView) _renameTableRow.getChildAt(j)).setBackground(new PaintDrawable(getResources().getColor(R.color.white)));
                }
                SetStartPosition((RecyclerView) _renameTableRow.getParent());
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
        List<Sportsman> sportsmen = new ArrayList<Sportsman>();

        if(_needDeleteTables)
        {
            GenerateStandartParticipants(_currentCompetition.GetStartNumber(), _currentCompetition.GetMaxParticipantCount(), sportsmen);
        }
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(sportsmen);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        _recyclerView.setAdapter(recyclerViewAdapter);
        _recyclerView.setLayoutManager(layoutManager);
        _recyclerView.setItemAnimator(itemAnimator);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        EmptyDataBaseCompetition();
        EmptyParticipantCompetition();
    }

    public void OnClick(View view) {
        _addDialog.show();
    }

    private void GenerateStandartParticipants(int firstNumber, int count, List<Sportsman> sportsmen)
    {
        int counter = 1;
        for(int i = firstNumber; i < count; i++)
        {
            Sportsman sportsman = new Sportsman();
            sportsman.setInfo(i, "Спортсмен "+ String.valueOf(counter), 1996, "Россия", "Без группы");
            sportsmen.add(sportsman);
            counter++;
        }
    }

    // Добавление участника в ParticipantList
//    private void AddRowParticipantList(Participant participant) {
//        int maxHeight = 0;
//        TableRow newRow = new TableRow(this);
//        final TextView newTextView0 = new TextView(this);
//        newTextView0.setSingleLine(false);
//        newTextView0.setText(participant.GetNumber());
//        newTextView0.setGravity(Gravity.CENTER);
//        newTextView0.setTextColor(participant.GetColor());
//        newTextView0.setBackgroundColor(Color.WHITE);
//        newTextView0.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView0.setLayoutParams(new TableRow.LayoutParams(_numberParticipantList.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT, 0.4f));
//        ((TableRow.LayoutParams) newTextView0.getLayoutParams()).setMargins(2, 0, 2, 2);
//
//        final TextView newTextView1 = new TextView(this);
//        newTextView1.setSingleLine(false);
//        newTextView1.setText(participant.GetFIO());
//        newTextView1.setGravity(Gravity.CENTER);
//        newTextView1.setTextColor(participant.GetColor());
//        newTextView1.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView1.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView1.setLayoutParams(new TableRow.LayoutParams(_nameParticipantList.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT/* _nameParticipantList.getMeasuredHeight()*/, 3.0f));
//        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        final TextView newTextView2 = new TextView(this);
//        newTextView2.setSingleLine(false);
//        newTextView2.setText(participant.GetBirthYear());
//        newTextView2.setGravity(Gravity.CENTER);
//        newTextView2.setTextColor(participant.GetColor());
//        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayParticipantList.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT/* _birthdayParticipantList.getMeasuredHeight()*/, 0.4f));
//        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        final TextView newTextView3 = new TextView(this);
//        newTextView3.setSingleLine(false);
//        newTextView3.setText(participant.GetGroup());
//        newTextView3.setGravity(Gravity.CENTER);
//        newTextView3.setTextColor(participant.GetColor());
//        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView3.setLayoutParams(new TableRow.LayoutParams(_groupParticipantList.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT/* _groupParticipantList.getMeasuredHeight()*/, 0.6f));
//        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        final TextView newTextView4 = new TextView(this);
//        newTextView4.setSingleLine(false);
//        newTextView4.setText(participant.GetCountry());
//        newTextView4.setGravity(Gravity.CENTER);
//        newTextView4.setTextColor(participant.GetColor());
//        newTextView4.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView4.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView4.setLayoutParams(new TableRow.LayoutParams(_countryParticipantList.getMeasuredWidth(),ViewGroup.LayoutParams.MATCH_PARENT/* _countryParticipantList.getMeasuredHeight()*/, 0.6f));
//        ((TableRow.LayoutParams) newTextView4.getLayoutParams()).setMargins(0, 0, 2, 2);
//
//        newRow.addView(newTextView0);
//        newRow.addView(newTextView1);
//        newRow.addView(newTextView2);
//        newRow.addView(newTextView3);
//        newRow.addView(newTextView4);
//        newRow.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View v)
//            {
//                ArrayList<View> rowView = new ArrayList<View>();
//                v.addChildrenForAccessibility(rowView);
//                if(!_haveMarkedParticipant)
//                {
//                    _haveMarkedParticipant = true;
//                    for (int i = 0; i < rowView.size(); i++)
//                    {
//                        rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
//                    }
//                }
//                return false;
//            }
//        });
//        newRow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                ArrayList<View> rowView = new ArrayList<View>();
//                v.addChildrenForAccessibility(rowView);
//                if(!_haveMarkedParticipant)return;
//                if (_counterMarkedParticipant == 0) {
//                    // Если отмечен первый
//                    _counterMarkedParticipant++;
//                    SetEditPosition(_gridView);
//                }
//                else
//                {
//                        PaintDrawable drawable = (PaintDrawable) rowView.get(1).getBackground();
//                        if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary))
//                        {
//                            _counterMarkedParticipant--;
//                            String name = ((TextView) rowView.get(1)).getText().toString();
//                            String year = ((TextView) rowView.get(2)).getText().toString();
//                            String country = ((TextView) rowView.get(4)).getText().toString();
//                            Participant currParticipant = new Participant("",name,country,year,DefaultGroup,1);
//                            Participant[] localArr = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
//                            for(int i = 0; i < rowView.size(); i++)
//                            {
//                                rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
//                            }
//
//
//                        }
//                        else
//                        {
//                            _counterMarkedParticipant++;
//                            for(int i = 0; i<5;i++)
//                            {
//                                rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
//                            }
//                        }
//                    }
//                    switch (_counterMarkedParticipant)
//                    {
//                        case 0:
//                            SetStartPosition(_gridView);
//                            _haveMarkedParticipant = false;
//                            break;
//                        case 1:
//                            SetEditPosition(_gridView);
//                            break;
//                        default:
//                            SetDelPosition(_gridView);
//                            break;
//                    }
//            }
//        });
//        _gridView.addView(newRow);
//        SetStartPosition(_gridView);
//    }
//
//    private void AddRowParticipantFromBase(Participant participant) {
//        TableRow newRow = new TableRow(this);
//        TextView newTextView1 = new TextView(this);
//        newTextView1.setText(participant.GetFIO());
//        newTextView1.setGravity(Gravity.CENTER);
//        newTextView1.setTextColor(Color.BLACK);
//        newTextView1.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView1.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView1.setLayoutParams(new TableRow.LayoutParams(_nameDataBaseList.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT, 3f));
//        ((TableRow.LayoutParams) newTextView1.getLayoutParams()).setMargins(2, 0, 2, 2);
//        TextView newTextView2 = new TextView(this);
//        newTextView2.setText(participant.GetBirthYear());
//        newTextView2.setGravity(Gravity.CENTER);
//        newTextView2.setTextColor(Color.BLACK);
//        newTextView2.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView2.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView2.setLayoutParams(new TableRow.LayoutParams(_birthdayDataBaseList.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
//        ((TableRow.LayoutParams) newTextView2.getLayoutParams()).setMargins(0, 0, 2, 2);
//        TextView newTextView3 = new TextView(this);
//        newTextView3.setText(participant.GetCountry());
//        newTextView3.setGravity(Gravity.CENTER);
//        newTextView3.setTextColor(Color.BLACK);
//        newTextView3.setBackground(new PaintDrawable(Color.WHITE));
//        newTextView3.setTextSize(getResources().getDimension(R.dimen.normal_text_size));
//        newTextView3.setLayoutParams(new TableRow.LayoutParams(_countryDataBaseList.getMeasuredWidth(), ViewGroup.LayoutParams.MATCH_PARENT, 0.5f));
//        ((TableRow.LayoutParams) newTextView3.getLayoutParams()).setMargins(0, 0, 2, 2);
//        newRow.addView(newTextView1);
//        newRow.addView(newTextView2);
//        newRow.addView(newTextView3);
//        newRow.setOnLongClickListener(new View.OnLongClickListener()
//        {
//            @Override
//            public boolean onLongClick(View v)
//            {
//                ArrayList<View> rowView = new ArrayList<View>();
//                v.addChildrenForAccessibility(rowView);
//                if(!_haveMarkedDataBase)
//                {
//                    _haveMarkedDataBase = true;
//                    for (int i = 0; i < rowView.size(); i++)
//                    {
//                        rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
//                    }
//                }
//                return false;
//            }
//        });
//        newRow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v)
//            {
//                ArrayList<View> rowView = new ArrayList<View>();
//                v.addChildrenForAccessibility(rowView);
//                if(!_haveMarkedDataBase) return;
//                if (_counterMarkedDataBase == 0)
//                {
//                    // Если отмечен первый
//                    _counterMarkedDataBase++;
//                    SetEditPosition(_gridViewDatabase);
//                }
//                else
//                {
//                    // Есди уже были отмечены
//                    for (int i = 0; i < rowView.size(); i++)
//                    {
//                        PaintDrawable drawable = (PaintDrawable) rowView.get(i).getBackground();
//                        if (drawable.getPaint().getColor() == getResources().getColor(R.color.colorPrimary))
//                        {
//                            if(i == 0)
//                            {
//                                _counterMarkedDataBase--;
//                            }
//                            rowView.get(i).setBackground(new PaintDrawable(Color.WHITE));
//                        }
//                        else
//                        {
//                            if(i == 0)
//                            {
//                                _counterMarkedDataBase++;
//                            }
//                            rowView.get(i).setBackground(new PaintDrawable(getResources().getColor(R.color.colorPrimary)));
//                        }
//                    }
//                    switch (_counterMarkedDataBase)
//                    {
//                        case 0:
//                            SetStartPosition(_gridViewDatabase);
//                            _haveMarkedDataBase = false;
//                            break;
//                        case 1:
//                            SetEditPosition(_gridViewDatabase);
//                            break;
//                        default:
//                            SetDelPosition(_gridViewDatabase);
//                            break;
//                    }
//                }
//            }
//        });
//        _gridViewDatabase.addView(newRow);
//    }

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

        _gridViewDatabase = (GridView) page2.findViewById(R.id.gridViewDataBaseLayout);
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

//    private void SortTableBy(GridView table, String orderBy, boolean sortState)
//    {
//        String localOrderString;
//        String localTableName = "";
//        Participant[] localArr;
//        if(sortState)
//        {
//            localOrderString = orderBy + " ASC";
//        }
//        else
//        {
//            localOrderString = orderBy + " DESC";
//        }
//        table.removeAllViews();
//
////        if(table == _tableLayoutDataBaseList)
////        {
////            localTableName = DatabaseProvider.DbParticipant.TABLE_NAME;
////            localArr = _dbSaver.GetAllParticipants(localTableName, localOrderString);
////            for(int i = 0; i < localArr.length; i++)
////            {
////                AddRowParticipantFromBase(localArr[i]);
////            }
////        }
////        else
////        {
////            localTableName = _currentCompetition.GetDbParticipantPath();
////            localArr = _dbSaver.GetAllParticipants(localTableName, localOrderString);
////            for(int i = 0; i < localArr.length; i++)
////            {
////                AddRowParticipantList(localArr[i]);
////            }
////        }
//        localTableName = _currentCompetition.GetDbParticipantPath();
//        localArr = _dbSaver.GetAllParticipants(localTableName, localOrderString);
//        for(int i = 0; i < localArr.length; i++)
//        {
//            AddRowParticipantList(localArr[i]);
//        }
//
//    }

//    private void SortByYear(GridView table,boolean sortState, boolean sortByYear)
//    {
//        Participant[] localArr;
//        if(table == _gridView)
//            localArr = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
//        else
//            localArr = _dbSaver.GetAllParticipants(DatabaseProvider.DbParticipant.TABLE_NAME, DatabaseProvider.DbParticipant.COLUMN_NAME);
//        int participantCount = localArr.length;
//        table.removeAllViews();
//        int k = 0;
//        Participant helpPart;
//        int firstParticipantData;
//        int secondParticipantData;
//        while(k != participantCount)
//        {
//            for(int i = 0; i < participantCount - k - 1; i++)
//            {
//                if(sortByYear)
//                {
//                    firstParticipantData = Integer.valueOf(localArr[i].GetBirthYear());
//                    secondParticipantData = Integer.valueOf(localArr[i + 1].GetBirthYear());
//                }
//                else
//                {
//                    try {
//                        firstParticipantData = Integer.valueOf(localArr[i].GetNumber());
//                    }catch (Exception e) {firstParticipantData = 0;}
//                    try {
//                        secondParticipantData = Integer.valueOf(localArr[i + 1].GetNumber());
//                    }catch (Exception e1) {secondParticipantData = 0;}
//                }
//                if(sortState)
//                {
//                    if (firstParticipantData > secondParticipantData) {
//                        helpPart = localArr[i];
//                        localArr[i] = localArr[i + 1];
//                        localArr[i + 1] = helpPart;
//                    }
//                }
//                else
//                {
//                    if (firstParticipantData < secondParticipantData) {
//                        helpPart = localArr[i];
//                        localArr[i] = localArr[i + 1];
//                        localArr[i + 1] = helpPart;
//                    }
//                }
//            }
//            k++;
//        }
//        if(table == _gridViewDatabase)
//        {
//            for (int j = 0; j < participantCount; j++)
//            {
//                AddRowParticipantFromBase(localArr[j]);
//            }
//        }
//        else
//        {
//            for (int j = 0; j < participantCount; j++)
//            {
//                AddRowParticipantList(localArr[j]);
//            }
//        }
//    }
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
                                    //SortTableBy(_gridView, DatabaseProvider.DbParticipant.COLUMN_GROUP, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по группам",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.nameSort:
                                    //SortTableBy(_gridView, DatabaseProvider.DbParticipant.COLUMN_NAME, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                    //SortByYear(_gridView, item.isChecked(), true);
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                    //SortTableBy(_gridView, DatabaseProvider.DbParticipant.COLUMN_COUNTRY, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка списка участников по региону",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.numberSort:
                                   // SortByYear(_gridView, item.isChecked(), false);
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
                                    //SortTableBy(_gridViewDatabase, DatabaseProvider.DbParticipant.COLUMN_NAME, item.isChecked());
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по имени",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.dataSort:
                                   // SortByYear(_gridViewDatabase, item.isChecked(),true);
                                    Toast.makeText(getApplicationContext(),"Сортировка базы данных по дате",Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.countrySort:
                                   // SortTableBy(_gridViewDatabase, DatabaseProvider.DbParticipant.COLUMN_COUNTRY, item.isChecked());
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

//    private Participant[] GetParticipantsFromTable(GridView table)
//    {
//        int participantCount = table.getChildCount();
//        Participant[] localArr = new Participant[participantCount];
//        String[] dataArr;
//        int cellsCount;
//        if(table == _gridViewDatabase)
//        {
//            cellsCount = 3;
//            dataArr = new String[cellsCount];
//        }
//        else
//        {
//            cellsCount = 5;
//            dataArr = new String[cellsCount];
//        }
//        for(int i = 0; i<participantCount; i++)
//        {
//            for(int j = 0; j < cellsCount; j++)
//            {
//                dataArr[j] = ((TextView)((TableRow) table.getChildAt(i)).getChildAt(j)).getText().toString();
//            }
//            if(table == _gridView) localArr[i] = new Participant(dataArr[0],dataArr[1], dataArr[4], dataArr[2],dataArr[3],1);
//            else localArr[i] = new Participant("",dataArr[0], dataArr[2], dataArr[1], DefaultGroup,1);
//        }
//
//        return localArr;
//    }

//    private Participant[] GetCheckedParticipants(GridView table, boolean needDelete)
//    {
//        // Переделать!!!
//        int participantCount = table.getChildCount();
//        TableRow row;
//        TextView textView;
//        int cellsCount = 3;
//        String[] dataArr = new String[cellsCount];
//        ArrayList<Participant> localArr = new ArrayList<Participant>();
//        boolean flag = false;
//        int k = 0;
//        int color = 1;
//        if(table == _gridView)
//        {
//            cellsCount = 5;
//            dataArr = new String[cellsCount];
//        }
//
//
//        for(int i = 0; i < participantCount - k; i++)
//        {
//            row = (TableRow) table.getChildAt(i);
//            textView = (TextView) row.getChildAt(1);
//            if(((PaintDrawable)(textView).getBackground()).getPaint().getColor() ==
//                    getResources().getColor(R.color.colorPrimary))
//            {
//                for(int j = 0; j < cellsCount; j++)
//                {
//                    dataArr[j] = ((TextView)row.getChildAt(j)).getText().toString();
//                }
//                flag = true;
//            }
//
//            if(flag)
//            {
//                flag = false;
//                if(table == _gridView)
//                {
//                    Participant[] arrFromBase = _dbSaver.GetAllParticipants(_currentCompetition.GetDbParticipantPath(), DatabaseProvider.DbParticipant.COLUMN_NAME);
//                    Participant localPart = new Participant("",dataArr[1], dataArr[4], dataArr[2],"",1);
//                    for(int j = 0; j<arrFromBase.length;j++)
//                    {
//                        if(localPart.equals(arrFromBase[j]))
//                        {
//                            color = arrFromBase[j].GetColor();
//                            localArr.add(arrFromBase[j]);
//                            break;
//                        }
//                    }
//
//                }
//                else
//                {
//                    localArr.add(new Participant("",dataArr[0], dataArr[2], dataArr[1], "",Color.BLACK));
//                }
//                if(needDelete)
//                {
//                    table.removeViewAt(i);
//                    k++;
//                    i--;
//                }
//                else
//                {
//                    _renameTableRow = row;
//                }
//
//            }
//        }
//        return localArr.toArray(new Participant[localArr.size()]);
//    }

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
//          Intent i = new Intent(this, TestBD.class);
//          i.putExtra("test", "participants"+_currentCompetition.GetNameDateString());
//          startActivity(i);
    }

//    public void OnClickEditParticipant(View view)
//    {
//        Participant[] currentParticipant = GetCheckedParticipants(_gridView, false);
//        _numberRenameDialog.setText(currentParticipant[0].GetNumber());
//        _nameRenameDialog.setText(currentParticipant[0].GetFIO());
//        _birthdayRenameDialog.setText(currentParticipant[0].GetBirthYear());
//        _countryRenameDialog.setText(currentParticipant[0].GetCountry());
//        _colorRenameDialog.setBackgroundColor(currentParticipant[0].GetColor());
//        for(int i = 0; i < _arrayGroup.length; i++)
//        {
//            if(_arrayGroup[i].equals(currentParticipant[0].GetGroup()))
//            {
//                _spinnerOfGroupRename.setSelection(i);
//                break;
//            }
//        }
//        _renameDialog.show();
//        Toast.makeText(getApplicationContext(),"Редактировать участника",Toast.LENGTH_SHORT).show();
//    }

//    public void OnClickDeleteParticipant(View view)
//    {
//        if(_gridView.getChildCount() == 0)
//        {
//            _acceptParticipantImBtn.setVisibility(View.GONE);
//        }
//        Participant[] myArr = GetCheckedParticipants(_gridView, true);
//        for(int i = 0; i<myArr.length; i++)
//        {
//            _currentCompetition.DeleteParticipantsFromCompetition(myArr[i]);
//            _dbSaver.DeleteParticipant(myArr[i], _currentCompetition.GetDbParticipantPath());
//        }
//        SetStartPosition(_gridView);
//        Toast.makeText(getApplicationContext(),"Удаление участника",Toast.LENGTH_SHORT).show();
//    }

//    public void OnClickDeleteDataBAse(View view)
//    {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
//        dialog.setTitle(getResources().getString(R.string.delete_dialog_title));
//        dialog.setMessage(getResources().getString(R.string.messege_del_dialog_from_database));
//        dialog.setCancelable(false);
//        dialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i)
//            {
//                Participant[] myArr = GetCheckedParticipants(_gridViewDatabase, true);
//                for(int j = 0; j<myArr.length;j++)
//                {
//                    _dbSaver.DeleteParticipant(myArr[j], DatabaseProvider.DbParticipant.TABLE_NAME);
//                }
//                SetStartPosition(_gridViewDatabase);
//                Toast.makeText(getApplicationContext(),"Участники удалены",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        dialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//        dialog.show();
//
//
//    }

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

//    public void OnClickAcceptDataBase(View view)
//    {
//        Participant[] localArr = GetCheckedParticipants(_gridViewDatabase, true);
//        Participant[] localCompParticipants = GetParticipantsFromTable(_gridView);
//        boolean localFlag;
//        for(int i = 0; i<localArr.length; i++)
//        {
//            localFlag = true;
//            for(int j = 0; j<localCompParticipants.length; j++)
//            {
//                if(localArr[i].equals(localCompParticipants[j]))
//                {
//                    localFlag = false;
//                    break;
//                }
//            }
//            if(localFlag)
//            {
//                AddRowParticipantList(localArr[i]);
//                _currentCompetition.AddParticipant(localArr[i]);
//            }
//        }
//        SetStartPosition(_gridViewDatabase);
//        Toast.makeText(getApplicationContext(),"Участники были добавлены в соревнование",Toast.LENGTH_SHORT).show();
//        EmptyDataBaseCompetition();
//        EmptyParticipantCompetition();
//    }

    private void SetStartPosition(RecyclerView gridView)
    {

        if(gridView == _recyclerView)
        {
            if(gridView.getChildCount()== 0)
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

    private void SetEditPosition(RecyclerView gridView)
    {
        if(gridView == _recyclerView)
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

    private void SetDelPosition(RecyclerView gridView)
    {
        if(gridView == _recyclerView)
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

    private void EmptyParticipantCompetition()
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

    private void EmptyDataBaseCompetition()
    {
        if(_gridViewDatabase.getChildCount() == 0)
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
