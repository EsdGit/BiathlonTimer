package com.esd.esd.biathlontimer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.esd.esd.biathlontimer.Activities.CompetitionsActivity;
import com.esd.esd.biathlontimer.ChangeColorEvent;
import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.SportsmanDeleteEvent;

import org.apache.poi.ss.formula.functions.Even;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Oleg on 26.12.2016.
 */

public class CompetitionTableAdapter extends RecyclerView.Adapter<CompetitionTableAdapter.ViewHolder> implements IMyAdapter<MegaSportsman>
{
    private List<MegaSportsman> sportsmen;
    private int _currentLap;
    private Context _localContext;
    private android.app.FragmentManager _fragmentManager;
    private EventBus eventBus;

    public CompetitionTableAdapter(Context context, android.app.FragmentManager fragmentManager)
    {
        sportsmen = new ArrayList<MegaSportsman>();
        _localContext = context;
        _fragmentManager = fragmentManager;
        _currentLap = 0;
        eventBus = EventBus.getDefault();
    }

    @Override
    public CompetitionTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_competition_table, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        MegaSportsman megaSportsman = sportsmen.get(position);
        holder.numberTextView.setText(String.valueOf(megaSportsman.getNumber()));
        holder.nameTextView.setText(megaSportsman.getName());
        holder.positionTextView.setText(String.valueOf(megaSportsman.getPlace()));
        holder.timeTextView.setText(megaSportsman.getResultTime().format("%H:%M:%S"));
        holder.lagTextView.setText(megaSportsman.getLag());
        //holder.fineTextView.setText(String.valueOf(megaSportsman.getFineCount()));
        holder.fineTextView.setText(megaSportsman.getFineCountArrString());
        holder.lapNumber = megaSportsman.getCurrentLap();

        holder.numberTextView.setTextColor(megaSportsman.getColor());
        holder.nameTextView.setTextColor(megaSportsman.getColor());
        holder.positionTextView.setTextColor(megaSportsman.getColor());
        holder.timeTextView.setTextColor(megaSportsman.getColor());
        holder.lagTextView.setTextColor(megaSportsman.getColor());
        holder.fineTextView.setTextColor(megaSportsman.getColor());
    }

    @Override
    public int getItemCount() {
        return sportsmen.size();
    }

    @Override
    public void AddSportsman(MegaSportsman sportsman)
    {
        if(sportsmen.contains(sportsman)) return;
        sportsmen.add(sportsman);
        notifyDataSetChanged();
    }

    public void ChangePlacesAfterDelete()
    {
        for(int i = 0; i < sportsmen.size(); i++)
        {
            if(sportsmen.get(i).getPlace() != i+1)
            {
                sportsmen.get(i).setPlace(i+1);
            }
        }
        notifyDataSetChanged();
    }

    public void ClearList(){sportsmen.clear();}

    @Override
    public void AddSportsmen(List<MegaSportsman> sportsmen)
    {
        this.sportsmen.addAll(sportsmen);
    }

    @Override
    public void RemoveSportsman(MegaSportsman sportsman)
    {
        if(!sportsmen.contains(sportsman)) return;
        sportsmen.remove(sportsman);
        notifyDataSetChanged();
    }

    public void RemoveSportsmanByNumber(int number)
    {
        int p = 0;
        for(int i = 0; i < sportsmen.size(); i++)
        {
            if(sportsmen.get(i).getNumber() == number)
            {
                sportsmen.remove(i);
                p = i;
                break;
            }
        }
        for(int i = p; i < sportsmen.size(); i++)
        {
            sportsmen.get(i).setPlace(i+1);
        }
        notifyDataSetChanged();
    }

    @Override
    public void RemoveSportsmen(List<MegaSportsman> sportsmen) {

    }

    @Override
    public boolean ChangeSportsman(MegaSportsman newSportsman, MegaSportsman oldSportsman) {
        return true;
    }

    @Override
    public List<MegaSportsman> GetCheckedSportsmen() {
        return null;
    }

    @Override
    public void ResetHaveMarkedFlag() {

    }

    @Override
    public void SortList(List<MegaSportsman> sortedList)
    {

    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTextView;
        private TextView numberTextView;
        private TextView positionTextView;
        private TextView timeTextView;
        private TextView lagTextView;
        private TextView fineTextView;
        private int lapNumber;

        private AlertDialog.Builder _builderChooseDialog;
        private AlertDialog _chooseDialog;

        private ColorPickerDialog _chooseColorDialog;


        public ViewHolder(final View itemView)
        {
            super(itemView);
            numberTextView = (TextView) itemView.findViewById(R.id.numberCompetitionTable);
            nameTextView = (TextView) itemView.findViewById(R.id.nameCompetitionTable);
            positionTextView = (TextView) itemView.findViewById(R.id.positionCompetitionTable);
            timeTextView = (TextView) itemView.findViewById(R.id.timeCompetitionTable);
            lagTextView = (TextView) itemView.findViewById(R.id.lagCompetitionTable);
            fineTextView = (TextView) itemView.findViewById(R.id.countFineCompetitionTable);
            _builderChooseDialog = new AlertDialog.Builder(_localContext, R.style.test);
            //_builderChooseDialog.setTitle(_localContext.getResources().getString(R.string.dialog_choose_title));
            _builderChooseDialog.setNeutralButton(_localContext.getResources().getString(R.string.choose_color), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    _chooseColorDialog.show(_fragmentManager, "colorpicker");
                }
            });
            _builderChooseDialog.setPositiveButton(_localContext.getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    SportsmanDeleteEvent event = new SportsmanDeleteEvent(Integer.valueOf(numberTextView.getText().toString()),lapNumber);
                    eventBus.post(event);
                    //Toast.makeText(_localContext, "удалить", Toast.LENGTH_SHORT).show();
                }
            });
            _builderChooseDialog.setMessage(_localContext.getResources().getString(R.string.dialog_choose_title));
            _chooseDialog = _builderChooseDialog.create();

            _chooseColorDialog = new ColorPickerDialog();
            _chooseColorDialog.initialize(R.string.color_picker_default_title,
                    new int[] {
                            Color.RED,
                            Color.BLACK,
                            _localContext.getResources().getColor(R.color.darkBlue),
                            _localContext.getResources().getColor(R.color.orange),
                            _localContext.getResources().getColor(R.color.brown),
                            _localContext.getResources().getColor(R.color.violet),
                            _localContext.getResources().getColor(R.color.green),
                            _localContext.getResources().getColor(R.color.yellow)
                    }, Color.BLACK, 4, 30);

            _chooseColorDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                @Override
                public void onColorSelected(int color)
                {
                    ChangeColorEvent event = new ChangeColorEvent(color, Integer.valueOf(numberTextView.getText().toString()));
                    eventBus.post(event);
                    //Toast.makeText(_localContext, "Выбран цвет", Toast.LENGTH_SHORT).show();
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View view)
                {
                    _chooseDialog.show();
                    return false;
                }
            });
        }
    }
}
