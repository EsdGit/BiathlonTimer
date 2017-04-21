package com.esd.esd.biathlontimer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.esd.esd.biathlontimer.Activities.ViewPagerActivity;
import com.esd.esd.biathlontimer.Competition;
import com.esd.esd.biathlontimer.R;
import com.esd.esd.biathlontimer.Sportsman;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 18.12.2016.
 */

public class RecyclerViewLocalDatabaseAdapter extends RecyclerView.Adapter<RecyclerViewLocalDatabaseAdapter.ViewHolder> implements IMyAdapter<Sportsman>
{
    private List<Sportsman> sportsmen;
    private boolean _haveMarkedParticipant = false;
    private int _countMarkedParticipant = 0;
    private Context _localContext;
    private String _competitionType;
    private String _interval;
    private String _secondInterval;
    private String _numberSecondInterval;
    private int _startNumber;

    public RecyclerViewLocalDatabaseAdapter(Context context, List<Sportsman> sportsmen, Competition competition)
    {
        _localContext = context;
        _competitionType = competition.getStartType();
        _interval = competition.getInterval();
        _secondInterval = competition.getSecondInterval();
        _numberSecondInterval = competition.getNumberSecondInterval();
        _startNumber = competition.getStartNumber();
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
        holder.startTimeTextView.setText(GetStartTime(sportsman));

        holder.nameTextView.setTextColor(sportsman.getColor());
        holder.yearTextView.setTextColor(sportsman.getColor());
        holder.numberTextView.setTextColor(sportsman.getColor());
        holder.countryTextView.setTextColor(sportsman.getColor());
        holder.groupTextView.setTextColor(sportsman.getColor());
        holder.startTimeTextView.setTextColor(sportsman.getColor());

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
            holder.startTimeTextView.setBackgroundColor(color);
        }
        else
        {
            holder.nameTextView.setBackgroundColor(Color.WHITE);
            holder.numberTextView.setBackgroundColor(Color.WHITE);
            holder.yearTextView.setBackgroundColor(Color.WHITE);
            holder.countryTextView.setBackgroundColor(Color.WHITE);
            holder.groupTextView.setBackgroundColor(Color.WHITE);
            holder.startTimeTextView.setBackgroundColor(Color.WHITE);
        }


    }

    private String GetStartTime(Sportsman sportsman)
    {
        Time startTime = new Time();
        boolean isFirstInterval = true;

        int number;
        if(_competitionType.equals(_localContext.getString(R.string.item_type_single_start)))
        {
            if(!_numberSecondInterval.equals(""))
            {
                if (sportsman.getNumber() < Integer.valueOf(_numberSecondInterval)) {
                    startTime.second = Integer.valueOf(_interval.split(":")[1]);
                    startTime.minute = Integer.valueOf(_interval.split(":")[0]);
                } else {
                    startTime.second = Integer.valueOf(_secondInterval.split(":")[1]);
                    startTime.minute = Integer.valueOf(_secondInterval.split(":")[0]);
                    isFirstInterval = false;
                }
            }
            else
            {
                startTime.second = Integer.valueOf(_interval.split(":")[1]);
                startTime.minute = Integer.valueOf(_interval.split(":")[0]);
            }
            if(isFirstInterval) {
                number = sportsman.getNumber() - _startNumber + 1;
                startTime.second = startTime.second * number;
                startTime.minute = startTime.minute * number;
                startTime.normalize(false);
                return startTime.format("%H:%M:%S");
            }else
            {
                number = sportsman.getNumber() - Integer.valueOf(_numberSecondInterval) + 1;
                int numberFirstInterval = Integer.valueOf(_numberSecondInterval) - _startNumber;
                Time startTimeSec = new Time();
                startTimeSec.second = Integer.valueOf(_interval.split(":")[1]);
                startTimeSec.minute = Integer.valueOf(_interval.split(":")[0]);
                startTimeSec.second = startTimeSec.second*numberFirstInterval;
                startTimeSec.minute = startTimeSec.minute*numberFirstInterval;
                startTimeSec.normalize(false);
                startTime.second = startTime.second * number;
                startTime.minute = startTime.minute * number;
                startTime.normalize(false);
                startTime.second += startTimeSec.second;
                startTime.minute += startTimeSec.minute;
                startTime.hour += startTimeSec.hour;
                startTime.normalize(false);
                return startTime.format("%H:%M:%S");
            }
        }
        else
        {
            if(_competitionType.equals(_localContext.getString(R.string.item_type_double_start)))
            {
                number = sportsman.getNumber() - _startNumber + 1;
                if((number % 2) == 0)
                {
                    number /= 2;
                }
                else
                {
                    number/=2;
                    number++;
                }
                if(!_numberSecondInterval.equals(""))
                {
                    if (number < Integer.valueOf(_numberSecondInterval))
                    {
                        startTime.second = Integer.valueOf(_interval.split(":")[1]);
                        startTime.minute = Integer.valueOf(_interval.split(":")[0]);
                    } else
                    {
                        startTime.second = Integer.valueOf(_secondInterval.split(":")[1]);
                        startTime.minute = Integer.valueOf(_secondInterval.split(":")[0]);
                        isFirstInterval = false;
                    }
                }
                else
                {
                    startTime.second = Integer.valueOf(_interval.split(":")[1]);
                    startTime.minute = Integer.valueOf(_interval.split(":")[0]);
                }
                if(isFirstInterval)
                {

                    startTime.second = startTime.second * number;
                    startTime.minute = startTime.minute * number;
                    startTime.normalize(false);
                    return startTime.format("%H:%M:%S");
                }
                else
                {
                    number = sportsman.getNumber() - _startNumber + 1;
                    int numberFirstInterval;
                    if(number % 2 == 0)
                    {
                        number /= 2;
                    }
                    else
                    {
                        number /= 2;
                        number++;
                    }

                    numberFirstInterval = Integer.valueOf(_numberSecondInterval) - 1;
                    number = number - Integer.valueOf(_numberSecondInterval) + 1;
                    Time startTimeSec = new Time();
                    startTimeSec.second = Integer.valueOf(_interval.split(":")[1]);
                    startTimeSec.minute = Integer.valueOf(_interval.split(":")[0]);
                    startTimeSec.second = startTimeSec.second*numberFirstInterval;
                    startTimeSec.minute = startTimeSec.minute*numberFirstInterval;
                    startTimeSec.normalize(false);
                    startTime.second = startTime.second * number;
                    startTime.minute = startTime.minute * number;
                    startTime.normalize(false);
                    startTime.second += startTimeSec.second;
                    startTime.minute += startTimeSec.minute;
                    startTime.hour += startTimeSec.hour;
                    startTime.normalize(false);
                    return startTime.format("%H:%M:%S");
                }
            }
        }
        return "00:00:00";
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
        sportsmen.add(0, new Sportsman(sportsman));
        notifyItemInserted(0);
    }

    @Override
    public boolean ChangeSportsman(Sportsman newSportsman, Sportsman oldSportsman)
    {
        //if(sportsmen.contains(newSportsman)) return false;
        int pos = sportsmen.indexOf(oldSportsman);
        sportsmen.set(pos, new Sportsman(newSportsman));
        notifyItemChanged(pos);
        return true;
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
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nameTextView;
        private TextView numberTextView;
        private TextView yearTextView;
        private TextView countryTextView;
        private TextView groupTextView;
        private TextView startTimeTextView;
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
            startTimeTextView = (TextView) itemView.findViewById(R.id.timeStart);
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
