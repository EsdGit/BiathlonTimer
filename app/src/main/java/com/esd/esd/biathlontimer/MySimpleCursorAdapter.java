package com.esd.esd.biathlontimer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.icu.text.MessagePattern;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.DatabaseClasses.ParticipantSaver;

import java.util.ArrayList;

/**
 * Created by NIL_RIMS_2 on 15.12.2016.
 */

public class MySimpleCursorAdapter extends SimpleCursorAdapter
{
    public ArrayList<Participant> Participants;
    Context localContext;
    static String _localBdName;
    static ParticipantSaver ps;
    boolean _haveMarkedDataBase = false;
    private int _counterMarkedParticipant;
    public MySimpleCursorAdapter(Context context, int res, Cursor res2, String[] from, int[] to, int no)
    {
        super(context, res, res2, from, to, no);
        localContext = context;
        ps = new ParticipantSaver(context);
        Participants = new ArrayList<Participant>();
    }

    public static void SetLocalBdName(String name)
    {
        _localBdName = name;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {

        return super.newView(context, cursor, parent);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        Participants.add(new Participant(cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getString(5), Color.BLACK));

        super.bindView(view, context, cursor);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(parent.getId() == R.id.gridViewParticipantList)
        {
            if(convertView != null)
            {
                if (Participants.get(position).wasChecked)
                {
                    TableRow row;
                    for(int i = 0; i < parent.getChildCount(); i++)
                    {
                        row = (TableRow) parent.getChildAt(i);
                        if(((TextView)row.getChildAt(0)).getText().toString().equals(Participants.get(position).GetNumber()))
                        {
                            for (int j = 0; j < row.getChildCount(); j++)
                            {
                                row.getChildAt(i).setBackgroundColor(localContext.getResources().getColor(R.color.colorPrimary));
                            }
                        }

                    }


                }

            }

        }
        return super.getView(position, convertView, parent);
    }

    //    @Override
//    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        TableRow row = (TableRow) layoutInflater.inflate(R.layout.test_row, parent, false);
//        if (parent.getId() == R.id.gridViewParticipantList) {
//
//            TextView number = (TextView) row.getChildAt(0);
//            TextView name = (TextView) row.getChildAt(1);
//            TextView year = (TextView) row.getChildAt(2);
//            TextView group = (TextView) row.getChildAt(3);
//            TextView country = (TextView) row.getChildAt(4);
//            int num = Integer.valueOf(cursor.getString(1));
//            int color = ps.GetColor(_localBdName, num);
//            number.setTextColor(color);
//            name.setTextColor(color);
//            year.setTextColor(color);
//            group.setTextColor(color);
//            country.setTextColor(color);
//            row.setTag(num);
//            row.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    ArrayList<View> rowView = new ArrayList<View>();
//                    v.addChildrenForAccessibility(rowView);
//                    if (!_haveMarkedDataBase) {
//                        _haveMarkedDataBase = true;
//                        for (int i = 0; i < rowView.size(); i++) {
//                            rowView.get(i).setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
//                        }
//                    }
//                      Toast.makeText(localContext, String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();
//                    return false;
//                }
//            });
//        }
//            return row;
//
//        }

//    @Override
//    public void bindView(View view, Context context, final Cursor cursor) {
//        super.bindView(view, context, cursor);
//
//        view.setTag(cursor.getPosition());
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v)
//            {
//                cursor.move((int)v.getTag());
//                if(((TextView)((TableRow)v).getChildAt(0)).getText().toString().equals(cursor.getString(1)))
//                {
//                    ArrayList<View> rowView = new ArrayList<View>();
//                    v.addChildrenForAccessibility(rowView);
//                    if (!_haveMarkedDataBase) {
//                        _haveMarkedDataBase = true;
//                        for (int i = 0; i < rowView.size(); i++) {
//                            rowView.get(i).setBackgroundColor(localContext.getResources().getColor(R.color.colorPrimary));
//                        }
//                    }
//                }
//
//                return false;
//            }
//        });
//    }

    //    class ViewHolder
//    {
//        TableRow row;
//    }
//
//        @Override
//    public View getView(int position, View convertView, ViewGroup parent)
//    {
//        if(parent.getId() == R.id.gridViewParticipantList)
//        {
//            View row = convertView;
//            ViewHolder holder;
//
//            if(row == null)
//            {
//
//                LayoutInflater layoutInflater = (LayoutInflater) localContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                row = layoutInflater.inflate(R.layout.test_row, parent, false);
//                holder = new ViewHolder();
//                holder.row = (TableRow) row.findViewById(R.id.tblRow);
//                row.setTag(holder);
//            }
//            else
//            {
//                holder = (ViewHolder) row.getTag();
//            }
//
//
//            //int number = Integer.valueOf(numberTextView.getText().toString());
//            //int color = ps.GetColor(_localBdName, number);
////            int color = Color.BLACK;
////            nameTextView.setTextColor(color);
////            numberTextView.setTextColor(color);
////            yearTextView.setTextColor(color);
////            groupTextView.setTextColor(color);
////            countryTextView.setTextColor(color);
////
////            row.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    Toast.makeText(localContext, nameTextView.getText().toString(), Toast.LENGTH_SHORT).show();
////                }
////            });
//        }
////        else
////        {
////            View row = convertView;
////
////            if(row == null)
////            {
////                LayoutInflater layoutInflater = (LayoutInflater) localContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////                row = layoutInflater.inflate(R.layout.test_row, parent, false);
////            }
////
////            row.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    Toast.makeText(localContext, "база", Toast.LENGTH_SHORT).show();
////                }
////            });
////        }
//
//        return super.getView(position, convertView, parent);
//    }
    }

