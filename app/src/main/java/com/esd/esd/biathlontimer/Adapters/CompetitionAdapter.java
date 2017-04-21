package com.esd.esd.biathlontimer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.Activities.FinalActivity;
import com.esd.esd.biathlontimer.Activities.MainActivity;
import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Oleg on 20.01.2017.
 */

public class CompetitionAdapter extends RecyclerView.Adapter<CompetitionAdapter.ViewHolder> {
    private List<Competition> competitions;
    private Context _localContext;

    private boolean _haveMarkedCompetition = false;
    private int _countMarkedCompetitions = 0;

    public CompetitionAdapter(Context context, List<Competition> competitions) {
        _localContext = context;
        this.competitions = competitions;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_table_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Competition competition = competitions.get(position);
        holder.nameTextView.setText(competition.getName());
        holder.dateTextView.setText(competition.getDate());
        holder.longClickListener.setCompetition(competition);
        holder.clickListener.setCompetition(competition);

        if (competition.isChecked()) {
            int color = _localContext.getResources().getColor(R.color.colorPrimary);
            holder.nameTextView.setBackgroundColor(color);
            holder.dateTextView.setBackgroundColor(color);
        } else {
            holder.nameTextView.setBackgroundColor(Color.WHITE);
            holder.dateTextView.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return competitions.size();
    }

    public List<Competition> GetCheckedCompetitions() {
        List<Competition> checkedCompetitions = new ArrayList<Competition>();
        for (int i = 0; i < competitions.size(); i++) {
            if (competitions.get(i).isChecked()) {
                competitions.get(i).setChecked(false);
                checkedCompetitions.add(competitions.get(i));
            }
        }
        return checkedCompetitions;
    }

    public void SortBy(List<Competition> sortedList)
    {
        competitions.clear();
        competitions.addAll(sortedList);
        notifyDataSetChanged();
    }
    public void RemoveCompetition(Competition competition)
    {
        int pos = competitions.indexOf(competition);
        competitions.remove(competition);
        notifyItemRemoved(pos);
    }

    public void SortByDate()
    {
        Collections.sort(competitions,new Comparator<Competition>() {
            @Override
            public int compare(Competition competition, Competition t1) {
                return competition.getRealDate().compareTo(t1.getRealDate());
            }
        });
        notifyDataSetChanged();
    }
    public void ResetHaveMarkedFlag() {

        _haveMarkedCompetition = false;
        _countMarkedCompetitions = 0;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTextView;
        private TextView dateTextView;
        private LongClickListener longClickListener;
        private ClickListener clickListener;

        public ViewHolder(final View itemView)
        {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_competition);
            dateTextView = (TextView) itemView.findViewById(R.id.data_competition);
            longClickListener = new LongClickListener();
            clickListener = new ClickListener();
            itemView.setOnLongClickListener(longClickListener);
            itemView.setOnClickListener(clickListener);
        }
    }

    private class LongClickListener implements View.OnLongClickListener
    {
        private Competition competition;
        @Override
        public boolean onLongClick(View v)
        {
            if(_haveMarkedCompetition) return false;
            _haveMarkedCompetition = true;
            _countMarkedCompetitions++;
            competition.setChecked(true);
            //ViewPagerActivity.SetEditPosition(1);
            MainActivity.SetEditPosition();
            notifyDataSetChanged();
            return false;
        }
        public void setCompetition(Competition competition)
        {
            this.competition = competition;
        }
    }

    private class ClickListener implements View.OnClickListener
    {
        private Competition competition;
        @Override
        public void onClick(View v)
        {
            if(!_haveMarkedCompetition)
            {
                if(competition.isFinished())
                {
                    Intent finalIntent = new Intent(_localContext.getApplicationContext(), FinalActivity.class);
                    finalIntent.putExtra("Name", competition.getName());
                    finalIntent.putExtra("Date", competition.getDate());
                    _localContext.startActivity(finalIntent);
                }
                else
                {
                    Intent myIntent = new Intent(_localContext.getApplicationContext(), ViewPagerActivity.class);
                    myIntent.putExtra("CompetitionName", competition.getName());
                    myIntent.putExtra("CompetitionDate", competition.getDate());
                    myIntent.putExtra("CompetitionStartType", "");
                    myIntent.putExtra("CompetitionInterval", "");
                    myIntent.putExtra("CompetitionCheckPointsCount", "");
                    myIntent.putExtra("NeedDelete", "false");
                    myIntent.putExtra("ArrayGroup", new String[]{});
                    _localContext.startActivity(myIntent);
                }
            }
            else
            {
                if (competition.isChecked())//Если спортсмен отмечен
                {
                    competition.setChecked(false);
                    _countMarkedCompetitions--;
                } else {
                    competition.setChecked(true);
                    _countMarkedCompetitions++;
                }
                switch (_countMarkedCompetitions) {
                    case 0:
                        _haveMarkedCompetition = false;
                        _countMarkedCompetitions = 0;
                        MainActivity.SetStartPosition();
                        break;
                    case 1:
                        MainActivity.SetEditPosition();
                        break;
                    default:
                        MainActivity.SetDelPosition();
                        break;
                }
                notifyDataSetChanged();
            }


        }
        public void setCompetition(Competition competition)
        {
            this.competition = competition;
        }
    }
}
