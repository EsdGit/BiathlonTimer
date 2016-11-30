package com.esd.esd.biathlontimer;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


public class MyButton
{
    private TextView _number;
    private Context _context;

    public MyButton(Context context)
    {
        _context = context;
    }

    public void SetParticipantNumber(View view, String number)
    {
        _number = (TextView) view.findViewById(R.id.numberParticipant);
        _number.setText(number);
    }
}
