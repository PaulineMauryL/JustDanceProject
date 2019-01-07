package com.pauli.justdanceproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DanceActivity extends AppCompatActivity {

    public static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity

    private MusicDance musical = null;
    private Handler mHandler;
    private int[] music = null;
    private Boolean resume = true;
    private ImageView imageButtonView = null;
    private int index = 0;
    private int askedposition = 0;
    private int actualposition = 0;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);

        Bundle bunble = getIntent().getExtras();
        if (bunble!=null){
            music = bunble.getIntArray("musicchosen");
            musical = new MusicDance("musicname", music,this);
            musical.musicsound.start();
            mHandler = new Handler();
            resume = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        index = 0;
        score = 0;
        mHandler.postDelayed(startMusic,1000);
    }

    public void movementsOnMusic(){
        int i=0;
        int delay=0;
        i=index*2;
        while(i<musical.musictiming.length){
            delay = musical.musictiming[i];
            mHandler.postDelayed(r1,delay-musical.musicsound.getCurrentPosition());
            mHandler.postDelayed(r2,delay-musical.musicsound.getCurrentPosition()+300);
            mHandler.postDelayed(r3,delay-musical.musicsound.getCurrentPosition()+600);
            i=i+2;
        }
    }

    public void ResumeMusic(View view) {
        Button pauseButton = findViewById(R.id.ResumeButton);
        if (musical.musicsound !=null){
            if (resume){
                musical.musicsound.start();
                resume = false;
                pauseButton.setText(R.string.Resume);
                movementsOnMusic();
            }else {
                musical.musicsound.pause();
                mHandler.removeCallbacks(r1);
                mHandler.removeCallbacks(r2);
                mHandler.removeCallbacks(r3);
                resume = true;
                pauseButton.setText(R.string.Restart);
            }
        }

    }

    public void UpButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.UpView);
            actualposition = 1;
        }
    }


    public void MiddleButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.MiddleView);
            actualposition = 2;
        }
    }

    public void DownButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.BottomView);
            actualposition = 3;
        }
    }
    final Runnable r1 = new Runnable() {
        public void run() {
            askedposition = musical.musictiming[index*2+1];
            switch (askedposition){
                case 1:
                    imageButtonView = findViewById(R.id.UpView);
                    break;
                case 2:
                    imageButtonView = findViewById(R.id.MiddleView);
                    break;
                case 3:
                    imageButtonView = findViewById(R.id.BottomView);
                    break;
            }
            imageButtonView.setActivated(true);
            imageButtonView.setEnabled(true);
        }
    };
    final Runnable r2 = new Runnable() {
        public void run() {

            imageButtonView.setActivated(true);
            imageButtonView.setEnabled(false);

            getPosition();
        }
    };
    private void getPosition() {
        if (actualposition==askedposition)
        {
            score = score+1;
        }
        Button ButtonView=findViewById(R.id.MiddleButton);
        ButtonView.setText(""+score);
    }
    final Runnable r3 = new Runnable() {
        public void run() {
            imageButtonView.setActivated(false);
            index = index+1;
        }
    };

    final Runnable startMusic = new Runnable() {
        public void run() {
            movementsOnMusic();
        }
    };
    public void ToggleFilter(){
        imageButtonView.setActivated(true);
        imageButtonView.setEnabled(false);
        imageButtonView.postDelayed(r1,600);
        imageButtonView.postDelayed(r2,1200);
    }
}
