package com.pauli.justdanceproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_MUSIC = 1;
    private int chosenMusic=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void StartDance(View view) {
        if(chosenMusic != 0){
            Intent intentStartDance = new Intent(MainActivity.this,DanceActivity.class);
            intentStartDance.putExtra("musicchosen",chosenMusic);
            startActivity(intentStartDance);
        }else {
            TextView textView =  findViewById(R.id.StartDanceText);
            textView.setText("You need to choose a music first!");
        }
    }


    public void ChooseCreuse(View view) {
        chosenMusic = R.raw.creuser;
    }
    public void ChooseHercule(View view) {
        chosenMusic = R.raw.zero;
    }
}
