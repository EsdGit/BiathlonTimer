package com.esd.esd.biathlontimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by Иван on 04.01.2017.
 */

public class GridViewAdapter extends BaseAdapter
{
    private Context _context;
    public Integer[] _test = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};

    public GridViewAdapter(Context context)
    {
        _context = context;
    }

    @Override
    public int getCount()
    {
        return _test.length;
    }

    @Override
    public Object getItem(int position)
    {
        return _test[position];
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
        TextView textView = (TextView) gridElement.findViewById(R.id.numberParticipantMyButton);
        textView.setText(Integer.toString(_test[position]));
        return gridElement;
    }
}
