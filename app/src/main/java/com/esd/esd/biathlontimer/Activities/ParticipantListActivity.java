package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.esd.esd.biathlontimer.R;

public class ParticipantListActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_list);
    }

    public void OnClick(View view)
    {
        Intent dataBaseIntent = new Intent(this, DataBaseActivity.class);
        startActivityForResult(dataBaseIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Код с приемом данных из активности с базой данных
    }
}
