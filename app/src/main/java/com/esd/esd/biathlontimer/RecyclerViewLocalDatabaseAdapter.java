package com.esd.esd.biathlontimer;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;
import com.esd.esd.biathlontimer.DatabaseClasses.RealmSportsmenSaver;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 18.12.2016.
 */

public class RecyclerViewLocalDatabaseAdapter extends RecyclerView.Adapter<RecyclerViewLocalDatabaseAdapter.ViewHolder> implements MyAdapterInteface
{
    private List<Sportsman> sportsmen;
    private boolean _haveMarkedParticipant = false;
    private int _countMarkedParticipant = 0;
    private Context _localContext;

    public RecyclerViewLocalDatabaseAdapter(Context context, List<Sportsman> sportsmen)
    {
        _localContext = context;
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
        if(sportsman.getNumber() != 0) holder.numberTextView.setText(String.valueOf(sportsman.getNumber()));
        else holder.numberTextView.setText("");
        holder.yearTextView.setText(String.valueOf(sportsman.getYear()));
        holder.countryTextView.setText(sportsman.getCountry());
        holder.groupTextView.setText(sportsman.getGroup());

        holder.nameTextView.setTextColor(sportsman.getColor());
        holder.yearTextView.setTextColor(sportsman.getColor());
        holder.numberTextView.setTextColor(sportsman.getColor());
        holder.countryTextView.setTextColor(sportsman.getColor());
        holder.groupTextView.setTextColor(sportsman.getColor());

        holder.longClickListener.setSportsman(sportsman);
        holder.clickListener.setSportsman(sportsman);
        if(sportsman.isChecked())
        {
            int color = _localContext.getResources().getColor(R.color.colorPrimary);
            holder.nameTextView.setBackgroundColor(color);
            holder.numberTextView.setBackgroundColor(color);
            holder.yearTextView.setBackgroundColor(color);
            holder.countryTextView.setBackgroundColor(color);
            holder.groupTextView.setBackgroundColor(color);
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


    @Override
    public void SortList(List<Sportsman> sortedList)
    {
        sportsmen.clear();
        sportsmen = sortedList;
        notifyDataSetChanged();
    }

    @Override
    public void AddSportsman(Sportsman sportsman)
    {
//        sportsmen.add(sportsmen.size(), sportsman);
//        notifyItemInserted(sportsmen.size());
        if(sportsmen.contains(sportsman)) return;
        sportsmen.add(0, sportsman);
        notifyItemInserted(0);
    }

    @Override
    public void ChangeSportsman(Sportsman newSportsman, Sportsman oldSportsman)
    {
        int pos = sportsmen.indexOf(oldSportsman);
        sportsmen.set(pos, newSportsman);
        notifyItemChanged(pos);
    }

    @Override
    public void RemoveSportsman(Sportsman sportsman)
    {
        int pos = sportsmen.indexOf(sportsman);
        sportsmen.remove(sportsman);
        notifyItemRemoved(pos);
    }

    @Override
    public void AddSportsmen(List<Sportsman> sportsmen)
    {
        for(int i = 0; i<sportsmen.size(); i++)
        {
            AddSportsman(sportsmen.get(i));
        }
    }

    @Override
    public void RemoveSportsmen(List<Sportsman> sportsmen)
    {
        int pos;
        for(int i = 0; i < sportsmen.size(); i++)
        {
            pos = this.sportsmen.indexOf(sportsmen.get(i));
            this.sportsmen.remove(sportsmen.get(i));
            notifyItemRemoved(pos);
        }
    }

    @Override
    public int getItemCount() {
        return sportsmen.size();
    }

    @Override
    public List<Sportsman> GetCheckedSportsmen() {
        List<Sportsman> checkedSportsmen = new ArrayList<Sportsman>();
        for(int i = 0; i < sportsmen.size(); i++)
        {
            if(sportsmen.get(i).isChecked())
            {
                sportsmen.get(i).setChecked(false);
                checkedSportsmen.add(sportsmen.get(i));
            }
        }
        return checkedSportsmen;
    }


    @Override
    public void ResetHaveMarkedFlag() {

        _haveMarkedParticipant = false;
        _countMarkedParticipant = 0;
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
                    ViewPagerActivity.SetStartPosition(1, sportsmen.size());
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
