package com.esd.esd.biathlontimer.Activities;

import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Oleg on 14.12.2016.
 */

public class SplashScreen extends AppCompatActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
=======
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.esd.esd.biathlontimer.R;


public class SplashScreen extends AppCompatActivity
{
    private static final int SLEEP_TIME = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // По истечении времени, запускаем главный активити, а Splash Screen закрываем
                Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SLEEP_TIME);
    }

>>>>>>> testDatabase
}
