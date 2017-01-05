package com.esd.esd.biathlontimer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 26.12.2016.
 */

public class CompetitionTableAdapter extends RecyclerView.Adapter<CompetitionTableAdapter.ViewHolder> implements IMyAdapter<MegaSportsman>
{
    private List<MegaSportsman> sportsmen;
    private int _currentLap;
    private Context _localContext;

    public CompetitionTableAdapter(Context context)
    {
        sportsmen = new ArrayList<MegaSportsman>();
        _localContext = context;
        _currentLap = 0;
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
        holder.lagTextView.setText(GetLag(megaSportsman));

        holder.numberTextView.setTextColor(megaSportsman.getColor());
        holder.nameTextView.setTextColor(megaSportsman.getColor());
        holder.positionTextView.setTextColor(megaSportsman.getColor());
        holder.timeTextView.setTextColor(megaSportsman.getColor());
        holder.lagTextView.setTextColor(megaSportsman.getColor());
    }

    public String GetLag(MegaSportsman sportsman)
    {
        Time localTime = new Time(sportsman.getResultTime());
        Time bestTime = new Time(sportsmen.get(0).getResultTime());
        localTime.hour -= bestTime.hour;
        localTime.minute -= bestTime.minute;
        localTime.second -= bestTime.second;
        localTime.normalize(false);
        return localTime.format("%H:%M:%S");
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
        //notifyDataSetChanged();
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

    }

    @Override
    public void RemoveSportsmen(List<MegaSportsman> sportsmen) {

    }

    @Override
    public void ChangeSportsman(MegaSportsman newSportsman, MegaSportsman oldSportsman) {

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

        public ViewHolder(final View itemView)
        {
            super(itemView);
            numberTextView = (TextView) itemView.findViewById(R.id.numberCompetitionTable);
            nameTextView = (TextView) itemView.findViewById(R.id.nameCompetitionTable);
            positionTextView = (TextView) itemView.findViewById(R.id.positionCompetitionTable);
            timeTextView = (TextView) itemView.findViewById(R.id.timeCompetitionTable);
            lagTextView = (TextView) itemView.findViewById(R.id.lagCompetitionTable);
        }
    }
}
