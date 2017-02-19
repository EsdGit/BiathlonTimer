package com.esd.esd.biathlontimer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.esd.esd.biathlontimer.MegaSportsman;
import com.esd.esd.biathlontimer.R;

import java.util.List;


public class GridViewAdapter extends BaseAdapter
{
    private Context _context;
    private List<MegaSportsman> _sportsmen;

    public GridViewAdapter(Context context, List<MegaSportsman> sportsmanList)
    {
        _context = context;
        _sportsmen = sportsmanList;
    }

    @Override
    public int getCount()
    {
        return _sportsmen.size();
    }

    @Override
    public Object getItem(int position)
    {
        return _sportsmen.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        MegaSportsman sportsman = _sportsmen.get(position);
        View gridElement;
        if(convertView == null)
        {
            gridElement = new View(_context);
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            gridElement = inflater.inflate(R.layout.my_btn, parent, false);
        }
        else
        {
            gridElement = (View)convertView;
        }
        TextView number = (TextView) gridElement.findViewById(R.id.numberParticipantMyButton);
        TextView lap = (TextView) gridElement.findViewById(R.id.lapParticipantMyButton);
        FrameLayout background = (FrameLayout) gridElement.findViewById(R.id.backgroundMyButton);
        number.setText(Integer.toString(sportsman.getNumber()));
        lap.setText(String.valueOf(sportsman.getCurrentLap()));
        background.setBackgroundColor(sportsman.getColor());
        FrameLayout finishedLayout = (FrameLayout) gridElement.findViewById(R.id.second_layout);
        if(sportsman.getFinished())
        {
            finishedLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            finishedLayout.setVisibility(View.GONE);
        }
        return gridElement;
    }

    public void AddSportsman(MegaSportsman sportsman)
    {
        _sportsmen.add(sportsman);
        notifyDataSetChanged();
    }

    public void ClearList()
    {
        for(int i = 0; i < _sportsmen.size(); i++)
        {
            _sportsmen.get(i).setCurrentLap(0);
        }
        _sportsmen.clear();
        notifyDataSetChanged();
    }

    public void setIsFinished(int number, boolean isFinished)
    {
        for(int i = 0; i < _sportsmen.size(); i++)
        {
            if(number == _sportsmen.get(i).getNumber())
            {
                _sportsmen.get(i).setFinished(isFinished);
                break;
            }
        }
    }

    public boolean getFinished(int number)
    {
        for(int i = 0; i < _sportsmen.size(); i++)
        {
            if(number == _sportsmen.get(i).getNumber())
            {
                return _sportsmen.get(i).getFinished();
            }
        }
        return false;
    }

    public void ChangeSportsmanLap(int number, int lap)
    {
        for(MegaSportsman sportsman:_sportsmen)
        {
            if(sportsman.getNumber() == number)
            {
                sportsman.setCurrentLap(lap);
                break;
            }
        }
        notifyDataSetChanged();
    }


    public void RemoveSportsman(int number)
    {
        for(MegaSportsman sportsman:_sportsmen)
        {
            if(sportsman.getNumber() == number)
            {
                sportsman.setCurrentLap(0);
                _sportsmen.remove(sportsman);
                notifyDataSetChanged();
                break;
            }
        }
    }
}
