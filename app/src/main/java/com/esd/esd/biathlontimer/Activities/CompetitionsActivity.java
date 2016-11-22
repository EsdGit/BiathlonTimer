package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esd.esd.biathlontimer.R;


public class CompetitionsActivity extends AppCompatActivity
{
    private GridLayout _participantGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competitions);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        _participantGridLayout = (GridLayout) findViewById(R.id.competitionGridLayout);
        for(int i = 0; i < 36; i++)
        {
            _participantGridLayout.addView(CreateButton(Integer.toString(i),Integer.toString(i+1)));
        }
    }


    private Button CreateButton(String numberParticipant, String numberCheckPoint)
    {
        final Button newButton = new Button(this);
        newButton.setText(numberParticipant + ", " + numberCheckPoint);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),newButton.getText(),Toast.LENGTH_LONG).show();
            }
        });
        return newButton;
    }


    public void imgBtnSettings_OnClick(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


}
