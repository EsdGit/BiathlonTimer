package com.esd.esd.biathlontimer;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Oleg on 18.12.2016.
 */

public class RecyclerViewDatabaseAdapter extends RecyclerView.Adapter<RecyclerViewDatabaseAdapter.ViewHolder>
{
    private List<Sportsman> sportsmen;
    public RecyclerViewDatabaseAdapter(List<Sportsman> sportsmen)
    {
        this.sportsmen = sportsmen;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_db_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sportsman sportsman = sportsmen.get(position);
        holder.nameTextView.setText(sportsman.getName());
        holder.yearTextView.setText(String.valueOf(sportsman.getYear()));
        holder.countryTextView.setText(sportsman.getCountry());
        holder.longClickListener.setSportsman(sportsman);
        holder.clickListener.setSportsman(sportsman);
        if(sportsman.isChecked())
        {
            holder.nameTextView.setBackgroundColor(Color.RED);
            holder.yearTextView.setBackgroundColor(Color.RED);
            holder.countryTextView.setBackgroundColor(Color.RED);
        }
        else
        {
            holder.nameTextView.setBackgroundColor(Color.WHITE);
            holder.yearTextView.setBackgroundColor(Color.WHITE);
            holder.countryTextView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return sportsmen.size();
    }

    public void AddSportsmen(Sportsman sportsman)
    {
        sportsmen.add(sportsman);
        notifyItemInserted(sportsmen.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTextView;
        private TextView yearTextView;
        private TextView countryTextView;
        private LongClickListener longClickListener;
        private ClickListener clickListener;
        public ViewHolder(final View itemView)
        {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.fio1);
            yearTextView = (TextView) itemView.findViewById(R.id.year1);
            countryTextView = (TextView) itemView.findViewById(R.id.country1);
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
            sportsman.setChecked(true);
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
            sportsman.setChecked(false);
            notifyDataSetChanged();
        }

        public void setSportsman(Sportsman sportsman)
        {
            this.sportsman = sportsman;
        }
    }
}
