package com.pauli.justdanceproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        userID = intent.getStringExtra(LaunchActivity.USER_ID);

        int nb_points = intent.getIntExtra(DanceActivity.NUMBER_POINTS, 0);
        String user_level = "to do";   //TODO: set user level depending on nb points (see Profile)

        TextView view_nb_points = findViewById(R.id.txt_nb_points);
        String points_number = "You have " + String.valueOf(nb_points) + "points";
        view_nb_points.setText(points_number);

        TextView view_level = findViewById(R.id.txt_level);
        String user_level_string = "You are a " + user_level;
        view_level.setText(user_level_string);
    }

    public void dance_again(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(LaunchActivity.USER_ID, userID);
        startActivity(intent);
        finish();
    }

    public void see_profile(View view) {
        Intent intent = new Intent(this, ShowProfile.class);
        intent.putExtra(LaunchActivity.USER_ID, userID);
        startActivity(intent);
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
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
