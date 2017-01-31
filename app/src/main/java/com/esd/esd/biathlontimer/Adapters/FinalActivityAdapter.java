package com.esd.esd.biathlontimer.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;

import java.util.List;

/**
 * Created by Oleg on 03.01.2017.
 */

public class FinalActivityAdapter extends RecyclerView.Adapter<FinalActivityAdapter.ViewHolder> implements IMyAdapter<MegaSportsman>
{
    List<MegaSportsman> _megaSportsmen;
    public FinalActivityAdapter(List<MegaSportsman> sportsmen)
    {
        _megaSportsmen = sportsmen;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_final_table, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        MegaSportsman sportsman = _megaSportsmen.get(position);
        holder._placeTextView.setText(String.valueOf(sportsman.getPlace()));
        holder._numberTextView.setText(String.valueOf(sportsman.getNumber()));
        holder._nameTextView.setText(sportsman.getName());
        holder._timeTextView.setText(sportsman.getResult());
        holder._groupTextView.setText(sportsman.getGroup());

        holder._placeTextView.setTextColor(sportsman.getColor());
        holder._numberTextView.setTextColor(sportsman.getColor());
        holder._nameTextView.setTextColor(sportsman.getColor());
        holder._timeTextView.setTextColor(sportsman.getColor());
        holder._groupTextView.setTextColor(sportsman.getColor());
    }

    @Override
    public int getItemCount() {
        return _megaSportsmen.size();
    }

    public void ChangeAdapter(List<MegaSportsman> sportsmen)
    {
        _megaSportsmen = sportsmen;
    }
    @Override
    public void AddSportsman(MegaSportsman sportsman) {

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
    public void SortList(List<MegaSportsman> sortedList)
    {
        _megaSportsmen.clear();
        _megaSportsmen = sortedList;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView _placeTextView;
        private TextView _numberTextView;
        private TextView _nameTextView;
        private TextView _timeTextView;
        private TextView _groupTextView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            _placeTextView = (TextView) itemView.findViewById(R.id.place_final_table);
            _numberTextView = (TextView) itemView.findViewById(R.id.number_final_table);
            _nameTextView = (TextView) itemView.findViewById(R.id.fio_final_table);
            _timeTextView = (TextView) itemView.findViewById(R.id.time_final_table);
            _groupTextView = (TextView) itemView.findViewById(R.id.group_final_table);
        }
    }
}
