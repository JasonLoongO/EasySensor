package com.example.sensor;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    static boolean isPlay = true;
    MediaPlayer mediaPlayer;//音乐播放器对象
    Button music_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        music_btn = findViewById(R.id.music_btn);

        PlayMusic();
    }

    private void PlayMusic(){
        mediaPlayer=MediaPlayer.create(this,R.raw.bnsdmm);//加载音乐
        mediaPlayer.setLooping(true);//循环播放
        mediaPlayer.start();
    }

    public void OnBlueConnect(View v){
        startActivity(new Intent(MainActivity.this,ShowBlueConnectActivity.class));
    }
    public void OnShowTemper(View v){
        startActivity(new Intent(MainActivity.this,ShowTemperActivity.class));
    }
    public void OnShowHeartbt(View v){
        startActivity(new Intent(MainActivity.this,ShowHeartbtActivity.class));
    }
    public void OnShowBodyfat(View v){
        startActivity(new Intent(MainActivity.this,ShowBodyfatActivity.class));
    }
    public void OnShowAbout(View v){
        startActivity(new Intent(MainActivity.this,AboutActivity.class));
    }
    public void OnShowPhoneSensor(View v){
        startActivity(new Intent(MainActivity.this,PhoneSensorActivity.class));
    }

    public void OnMusic(View v){
        if(isPlay==true){
            if(mediaPlayer!=null){//判断播放器是否存在
                mediaPlayer.stop();
                music_btn.setBackgroundResource(R.drawable.music_btn2);
                isPlay=false;
            }
        }else {
            PlayMusic();
            music_btn.setBackgroundResource(R.drawable.music_btn1);
            isPlay=true;
        }
    }

}