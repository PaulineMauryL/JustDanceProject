package com.pauli.justdanceproject;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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



    //Added by Pauline
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Added by Pauline
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.txt_show_profile:
                Intent intent_show = new Intent(this, ShowProfile.class);
                startActivity(intent_show);
                finish();
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(this, EditUser.class);
                startActivity(intent_edit);
                finish();
                break;
            case R.id.txt_change_user:
                Intent intent_change = new Intent(this, LaunchActivity.class);
                startActivity(intent_change);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
