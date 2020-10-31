package com.example.sensor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends Activity {

    @Override
    //启动界面入口
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //跳转界面，从StartActivity跳转到MainActivity
                startActivity(new Intent(StartActivity.this,MainActivity.class));
            }
        };timer.schedule(timerTask,2000);
    }
}
