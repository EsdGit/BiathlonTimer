package com.esd.esd.biathlontimer;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;

import java.util.List;

/**
 * Created by Oleg on 18.12.2016.
 */

public class RecyclerViewLocalDatabaseAdapter extends RecyclerView.Adapter<RecyclerViewLocalDatabaseAdapter.ViewHolder>
{
    private List<Sportsman> sportsmen;
    private boolean _haveMarkedParticipant = false;
    private int _countMarkedParticipant = 0;

    public RecyclerViewLocalDatabaseAdapter(List<Sportsman> sportsmen)
    {
        this.sportsmen = sportsmen;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sportsman sportsman = sportsmen.get(position);
        holder.nameTextView.setText(sportsman.getName());
        holder.numberTextView.setText(String.valueOf(sportsman.getNumber()));
        holder.yearTextView.setText(String.valueOf(sportsman.getYear()));
        holder.countryTextView.setText(sportsman.getCountry());
        holder.groupTextView.setText(sportsman.getGroup());
        holder.longClickListener.setSportsman(sportsman);
        holder.clickListener.setSportsman(sportsman);
        if(sportsman.isChecked())
        {
            holder.nameTextView.setBackgroundColor(Color.RED);
            holder.numberTextView.setBackgroundColor(Color.RED);
            holder.yearTextView.setBackgroundColor(Color.RED);
            holder.countryTextView.setBackgroundColor(Color.RED);
            holder.groupTextView.setBackgroundColor(Color.RED);
        }
        else
        {
            holder.nameTextView.setBackgroundColor(Color.WHITE);
            holder.numberTextView.setBackgroundColor(Color.WHITE);
            holder.yearTextView.setBackgroundColor(Color.WHITE);
            holder.countryTextView.setBackgroundColor(Color.WHITE);
            holder.groupTextView.setBackgroundColor(Color.WHITE);
        }
    }

    public void AddSportsmen(Sportsman sportsman)
    {
        sportsmen.add(sportsman);
        notifyItemInserted(sportsmen.size());
    }

    @Override
    public int getItemCount() {
        return sportsmen.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTextView;
        private TextView numberTextView;
        private TextView yearTextView;
        private TextView countryTextView;
        private TextView groupTextView;
        private LongClickListener longClickListener;
        private ClickListener clickListener;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.fio);
            numberTextView = (TextView) itemView.findViewById(R.id.number);
            yearTextView = (TextView) itemView.findViewById(R.id.year);
            countryTextView = (TextView) itemView.findViewById(R.id.country);
            groupTextView = (TextView) itemView.findViewById(R.id.group);
            longClickListener = new LongClickListener();
            clickListener = new ClickListener();
            itemView.setOnLongClickListener(longClickListener);
            itemView.setOnClickListener(clickListener);
        }
    }

    private class LongClickListener implements View.OnLongClickListener
    {
        private Sportsman sportsman;
        @Override
        public boolean onLongClick(View v)
        {
            if(_haveMarkedParticipant) return false;
            _haveMarkedParticipant = true;
            _countMarkedParticipant++;
            sportsman.setChecked(true);
            ViewPagerActivity.SetEditPosition(1);
            notifyDataSetChanged();
            return false;
        }

        public void setSportsman(Sportsman sportsman)
        {
            this.sportsman = sportsman;
        }
    }

    private class ClickListener implements View.OnClickListener
    {
        private Sportsman sportsman;
        @Override
        public void onClick(View v)
        {
            if(!_haveMarkedParticipant) return;
            if(sportsman.isChecked())//Если спортсмен отмечен
            {
                sportsman.setChecked(false);
                _countMarkedParticipant--;
            }
            else//Если не отмечен
            {
                sportsman.setChecked(true);
                _countMarkedParticipant++;
            }
            switch (_countMarkedParticipant)
            {
                case 0:
                    _haveMarkedParticipant = false;
                    _countMarkedParticipant = 0;
                    ViewPagerActivity.SetStartPosition(1, sportsmen.size(), _countMarkedParticipant);
                    break;
                case 1:
                    ViewPagerActivity.SetEditPosition(1);
                    break;
                default:
                    ViewPagerActivity.SetDelPosition(1);
                    break;
            }
            notifyDataSetChanged();
        }

        public void setSportsman(Sportsman sportsman)
        {
            this.sportsman = sportsman;
        }
    }
}
