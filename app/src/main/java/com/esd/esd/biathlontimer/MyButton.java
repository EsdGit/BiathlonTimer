package com.esd.esd.biathlontimer;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class MyButton
{
    private Context _context;

    public MyButton(Context context)
    {
        _context = context;
    }

    public void SetParticipantNumberAndBackground(View view, String numberParticipant, int backgroundColor)
    {
        TextView number = (TextView) view.findViewById(R.id.numberParticipantMyButton);
        number.setText(numberParticipant);
        FrameLayout background = (FrameLayout) view.findViewById(R.id.backgroundMyButton);
        background.setBackgroundColor(backgroundColor);
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

}
