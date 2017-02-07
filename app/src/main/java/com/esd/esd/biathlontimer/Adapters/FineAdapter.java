package com.esd.esd.biathlontimer.Adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.R;

/**
 * Created by NIL_RIMS_4 on 07.02.2017.
 */

public class FineAdapter extends BaseAdapter
{
    private Context _context;
    private String[] _objects;
    private Drawable drawableTegOn;
    private Drawable drawableTegOff;
    private GridView _parentGridView;

    public FineAdapter(Context context, GridView gridView, String[] objects)
    {
        _context = context;
        _objects = objects;
        _parentGridView = gridView;
        drawableTegOn =  _context.getResources().getDrawable(R.drawable.background_fine_button_on);
        drawableTegOff =  _context.getResources().getDrawable(R.drawable.background_fine_button_off);
    }
    @Override
    public int getCount() {
        return _objects.length;
    }

    @Override
    public Object getItem(int i) {
        return _objects[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        String sportsman = _objects[i];
        View gridElement;
        if(view == null)
        {
            gridElement = new View(_context);
            LayoutInflater inflater = (LayoutInflater) _context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            gridElement = inflater.inflate(R.layout.fine_button, viewGroup, false);
        }
        else
        {
            gridElement = (View)view;
        }
        final TextView number = (TextView) gridElement.findViewById(R.id.number_fine);
        final Button button = (Button) gridElement.findViewById(R.id.button_fine);
        button.setTag(drawableTegOff);
        number.setText(sportsman);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(_context,number.getText().toString(),Toast.LENGTH_SHORT).show();
                if(view.findViewById(R.id.button_fine).getTag() == drawableTegOn)
                {
                    for(int i = 0; i < _parentGridView.getChildCount(); i++)
                    {
                        if(_parentGridView.getChildAt(i).findViewById(R.id.button_fine).getTag() == drawableTegOn)
                        {
                            if(Integer.valueOf(((TextView)_parentGridView.getChildAt(i).findViewById(R.id.number_fine)).getText().toString()) > Integer.valueOf(number.getText().toString()))
                            {
                                _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setBackground(_context.getResources().getDrawable(R.drawable.background_fine_button_off));
                                _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setTag(drawableTegOff);
                            }
                        }
                    }
//                    view.findViewById(R.id.button_fine).setBackground(_context.getResources().getDrawable(R.drawable.background_fine_button_off));
//                    view.findViewById(R.id.button_fine).setTag(drawableTegOff);
                }
                else
                {
                    for(int i = 0; i < _parentGridView.getChildCount(); i++)
                    {
                        if(_parentGridView.getChildAt(i).findViewById(R.id.button_fine).getTag() == drawableTegOff)
                        {
                            if(((TextView)_parentGridView.getChildAt(i).findViewById(R.id.number_fine)).getText().toString().equals(number.getText().toString()) ) break;
                            _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setBackground(_context.getResources().getDrawable(R.drawable.background_fine_button_on));
                            _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setTag(drawableTegOn);
                        }
                    }
                    view.findViewById(R.id.button_fine).setBackground(_context.getResources().getDrawable(R.drawable.background_fine_button_on));
                    view.findViewById(R.id.button_fine).setTag(drawableTegOn);
                }
            }
        });
        return gridElement;
    }

    public int getCurrentCountFine()
    {
        int count = 0;
        for (int i = 0; i < _parentGridView.getChildCount(); i ++)
        {
            if(_parentGridView.getChildAt(i).findViewById(R.id.button_fine).getTag() == drawableTegOn)
            {
                count++;
            }
        }
        return count;
    }

    public void setCountFine(int count)
    {
        for(int i = 0; i < _parentGridView.getChildCount(); i++)
        {
            if(i < count)
            {
                _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setBackground(_context.getResources().getDrawable(R.drawable.background_fine_button_on));
                _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setTag(drawableTegOn);
            }
            else
            {
                _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setBackground(_context.getResources().getDrawable(R.drawable.background_fine_button_off));
                _parentGridView.getChildAt(i).findViewById(R.id.button_fine).setTag(drawableTegOff);
            }
        }
    }


}
