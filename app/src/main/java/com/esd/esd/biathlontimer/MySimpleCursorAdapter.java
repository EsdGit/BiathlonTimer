package com.esd.esd.biathlontimer;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by NIL_RIMS_2 on 15.12.2016.
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter
{
    Context localContext;
    public MySimpleCursorAdapter(Context context, int res, Cursor res2, String[] from, int[] to, int no)
    {
        super(context, res, res2, from, to, no);
        localContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if(row == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) localContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.test_row, parent, false);
        }
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(localContext, "А у меня работает", Toast.LENGTH_SHORT).show();
            }
        });
        return super.getView(position, convertView, parent);
    }

}
