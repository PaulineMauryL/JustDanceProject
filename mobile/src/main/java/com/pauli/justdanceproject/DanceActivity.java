package com.pauli.justdanceproject;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DanceActivity extends AppCompatActivity {

    public static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity

    private MediaPlayer mySound = null;
    private int Idmusictodisplay = 0;
    private Boolean resume = true;
    private ImageView imageButtonView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);

        Bundle bunble = getIntent().getExtras();
        if (bunble!=null){
            Idmusictodisplay = bunble.getInt("musicchosen");
            mySound = MediaPlayer.create(this, Idmusictodisplay);
            mySound.start();
            resume = false;
        }
    }

    public void ResumeMusic(View view) {
        Button pauseButton = findViewById(R.id.ResumeButton);
        if (mySound !=null){
            if (resume){
                mySound.start();
                resume = false;
                pauseButton.setText(R.string.Resume);
            }else {
                mySound.pause();
                resume = true;
                pauseButton.setText(R.string.Restart);
            }
        }

    }

    public void UpButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.UpView);
            ToggleFilter();
        }
    }


    public void MiddleButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.MiddleView);
            ToggleFilter();
        }
    }

    public void DownButton(View view) {
        if (!resume){
            imageButtonView = findViewById(R.id.BottomView);
            ToggleFilter();
        }
    }
    final Runnable r1 = new Runnable() {
        public void run() {
            imageButtonView.setActivated(true);
            imageButtonView.setEnabled(true);
        }
    };
    final Runnable r2 = new Runnable() {
        public void run() {
            imageButtonView.setActivated(false);
            imageButtonView.setEnabled(false);
        }
    };

    public void ToggleFilter(){
        imageButtonView.setActivated(true);
        imageButtonView.setEnabled(false);
        imageButtonView.postDelayed(r1,600);
        imageButtonView.postDelayed(r2,1200);
    }
}
