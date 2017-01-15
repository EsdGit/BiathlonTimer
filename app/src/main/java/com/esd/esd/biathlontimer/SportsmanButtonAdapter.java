package com.esd.esd.biathlontimer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Oleg on 25.12.2016.
 */

public class SportsmanButtonAdapter extends RecyclerView.Adapter<SportsmanButtonAdapter.ViewHolder> implements IMyAdapter<MegaSportsman>
{
    private List<MegaSportsman> sportsmen;
    int layoutId;
    private Context _localContext;
    public SportsmanButtonAdapter(Context context, List<MegaSportsman> sportsmen, int layoutId)
    {
        _localContext = context;
        this.sportsmen = sportsmen;
        this.layoutId = layoutId;
    }
    @Override
    public void AddSportsmen(List<MegaSportsman> sportsmen) {

    }

    @Override
    public void RemoveSportsman(MegaSportsman sportsman) {

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
    public void SortList(List<MegaSportsman> sortedList) {

    }

    @Override
    public void AddSportsman(MegaSportsman sportsman)
    {
        sportsmen.add(sportsman);
    }


    @Override
    public SportsmanButtonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_competition_table, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SportsmanButtonAdapter.ViewHolder holder, int position)
    {
        MegaSportsman megaSportsman = sportsmen.get(position);
        holder.numberTextView.setText(megaSportsman.getNumber());


        holder.numberTextView.setTextColor(megaSportsman.getColor());
    }

    @Override
    public int getItemCount() {
        return sportsmen.size();
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
            nameTextView = (TextView) itemView.findViewById(R.id.nameCompetitionTable);
            numberTextView = (TextView) itemView.findViewById(R.id.numberCompetitionTable);
            positionTextView = (TextView) itemView.findViewById(R.id.positionCompetitionTable);
            timeTextView = (TextView) itemView.findViewById(R.id.timeCompetitionTable);
            lagTextView = (TextView) itemView.findViewById(R.id.lagCompetitionTable);
        }
    }

}
