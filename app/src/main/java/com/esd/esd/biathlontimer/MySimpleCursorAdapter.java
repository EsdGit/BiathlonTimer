package com.esd.esd.biathlontimer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;

/**
 * Created by NIL_RIMS_2 on 15.12.2016.
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter
{
    Context localContext;
    static String _localBdName;
    static ParticipantSaver ps;
    public MySimpleCursorAdapter(Context context, int res, Cursor res2, String[] from, int[] to, int no)
    {
        super(context, res, res2, from, to, no);
        localContext = context;
        ps = new ParticipantSaver(context);
    }

    public static void SetLocalBdName(String name)
    {
        _localBdName = name;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        if(view.getId() == R.id.tblRow) {
            TextView number = (TextView) view.findViewById(R.id.number);
            TextView name = (TextView) view.findViewById(R.id.fio);
            int num = Integer.valueOf(number.getText().toString());
            int color = ps.GetColor(_localBdName, num);
        }
    }



//    @Override
//    public View getView(int position, View convertView, ViewGroup parent)
//    {
//        if(parent.getId() == R.id.gridViewParticipantList)
//        {
//            View row = convertView;
//
//            if(row == null)
//            {
//                LayoutInflater layoutInflater = (LayoutInflater) localContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                row = layoutInflater.inflate(R.layout.test_row, parent, false);
//            }
//
//            final TextView nameTextView = (TextView) ((TableRow) row).getChildAt(1);
//            final TextView numberTextView = (TextView) ((TableRow) row).getChildAt(0);
//            final TextView yearTextView = (TextView) ((TableRow) row).getChildAt(2);
//            final TextView groupTextView = (TextView) ((TableRow) row).getChildAt(3);
//            final TextView countryTextView = (TextView) ((TableRow) row).getChildAt(4);
//
//            //int number = Integer.valueOf(numberTextView.getText().toString());
//            //int color = ps.GetColor(_localBdName, number);
//            int color = Color.BLACK;
//            nameTextView.setTextColor(color);
//            numberTextView.setTextColor(color);
//            yearTextView.setTextColor(color);
//            groupTextView.setTextColor(color);
//            countryTextView.setTextColor(color);
//
//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(localContext, nameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//        else
//        {
//            View row = convertView;
//
//            if(row == null)
//            {
//                LayoutInflater layoutInflater = (LayoutInflater) localContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                row = layoutInflater.inflate(R.layout.test_row, parent, false);
//            }
//
//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(localContext, "база", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//
//        return super.getView(position, convertView, parent);
//    }

}
