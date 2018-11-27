package com.pauli.justdanceproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String RECORDING_ID = "recording_id";  //added by Pauline
    private String userID;
    private static final int PICK_MUSIC = 1;
    private int chosenMusic=0;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        if (intent.hasExtra(LaunchActivity.USER_ID)) {
            userID = intent.getStringExtra(LaunchActivity.USER_ID);
            //Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "error no id", Toast.LENGTH_SHORT).show();
        }

        Button b_testWatchActivity = findViewById(R.id.buttonTestWatchActivity);
        b_testWatchActivity.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Start on click activity
                Intent it = new Intent(MainActivity.this,TestWatchActivity.class);
                startActivity(it);
            }
        });
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
                intent_show.putExtra(LaunchActivity.USER_ID, userID);
                startActivity(intent_show);
                finish();
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(this, EditUser.class);
                intent_edit.putExtra(LaunchActivity.USER_ID, userID);
                startActivity(intent_edit);
                finish();
                break;
            case R.id.txt_change_user:                            // to copy in main activity and the three fragments
                // Check if user really wants to change
                builder = new AlertDialog.Builder(this);

                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Change user").setTitle("userChange");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you really want to change user ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                                Toast.makeText(getApplicationContext(),"Ok ! See you another time",
                                        Toast.LENGTH_SHORT).show();
                                // Leave
                                Intent intent_change = new Intent(getApplication(), LaunchActivity.class);
                                startActivity(intent_change);
                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),"Good choice",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Change of dancer");
                alert.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
