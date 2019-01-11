package com.pauli.justdanceproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class FinishActivity extends AppCompatActivity {

    private String userID;
    private String music_name;
    private int score;
    private TextView  TxtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        userID = intent.getStringExtra(LaunchActivity.USER_ID);
        music_name = intent.getStringExtra(DanceActivity.MUSIC_NAME);
        score = intent.getIntExtra(DanceActivity.NUMBER_POINTS, 0);

        TextView view_nb_points = findViewById(R.id.txt_nb_points);
        String points_number = "You have " + String.valueOf(score) + " points";
        view_nb_points.setText(points_number);

        TxtInfo = findViewById(R.id.txt_display_info);
        List<DatabaseEntity> entities = DanceActivity.cloneDanceRD.dataDao().getHallOfFame(music_name);

        String info = "";

        for(DatabaseEntity dbEnt : entities){
            String user_name = dbEnt.getUser_name();
            int score = dbEnt.getScore();
            info = info + "User :" + user_name + " Score :" + score + "\n";
        }

        TxtInfo.setText(info);
    }

    public void dance_again(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LaunchActivity.USER_ID, userID);
        startActivity(intent);
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(FinishActivity.this,BuildConfig.W_watchmain_activity_start);
        }
        finish();
    }

    public void see_profile(View view) {
        Intent intent = new Intent(this, ShowProfile.class);
        intent.putExtra(LaunchActivity.USER_ID, userID);
        startActivity(intent);
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change to dance activity
            Communication.changeWatchActivity(FinishActivity.this,BuildConfig.W_hall_activity_start);
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_finish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.txt_change_user:
                Intent intent_change = new Intent(this, LaunchActivity.class);
                startActivity(intent_change);
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(FinishActivity.this,BuildConfig.W_watchmain_activity_start);
                }
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
