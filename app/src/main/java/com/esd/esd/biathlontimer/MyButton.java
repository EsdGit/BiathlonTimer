package com.esd.esd.biathlontimer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class MyButton
{
    private Context _context;
    private ArrayList<View> _buttonsArrayList;

    public MyButton(Context context)
    {
        _context = context;
        _buttonsArrayList = new ArrayList<View>();
    }

    public void SetParticipantNumberAndBackground(View view, String numberParticipant, int backgroundColor)
    {
        TextView number = (TextView) view.findViewById(R.id.numberParticipantMyButton);
        number.setText(numberParticipant);
        FrameLayout background = (FrameLayout) view.findViewById(R.id.backgroundMyButton);
        background.setBackgroundColor(backgroundColor);
        _buttonsArrayList.add(view);
    }

    public void SetParticipantLap(View view, String currentLap)
    {
        TextView lap = (TextView) view.findViewById(R.id.lapParticipantMyButton);
        lap.setText(currentLap);
    }

    public String GetParticipantNumber(View view)
    {
        TextView number = (TextView) view.findViewById(R.id.numberParticipantMyButton);
        return number.getText().toString();
    }

    public String GetLap(View view)
    {
        TextView lap = (TextView) view.findViewById(R.id.lapParticipantMyButton);
        return lap.getText().toString();
    }

    public void ChangeLap(String number, String lap)
    {
        for(int i = 0; i < _buttonsArrayList.size(); i++)
        {
            if(number.equals(GetParticipantNumber(_buttonsArrayList.get(i))))
            {
                SetParticipantLap(_buttonsArrayList.get(i),lap);
            }
        }
    }

}
