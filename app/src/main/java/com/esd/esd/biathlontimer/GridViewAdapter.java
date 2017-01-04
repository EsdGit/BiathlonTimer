package com.esd.esd.biathlontimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Иван on 04.01.2017.
 */

public class GridViewAdapter extends BaseAdapter
{
    private Context _context;
    private List<Sportsman> _sportsmen;

    public GridViewAdapter(Context context, List<Sportsman> sportsmanList)
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
        number.setText(Integer.toString(_sportsmen.get(position).getNumber()));
        lap.setText("0");
        background.setBackgroundColor(_sportsmen.get(position).getColor());
        return gridElement;
    }
}
