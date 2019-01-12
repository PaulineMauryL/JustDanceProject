package com.pauli.justdanceproject;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String key_userID = "key_userID";
    public static final String ACTIVITY_NAME = "main_activity";
    public static final String RECORDING_ID = "recording_id";  //added by Pauline
    private String userID;
    private static final int PICK_MUSIC = 1;
    private int[] chosenMusic = null;
    boolean musicSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicSelected = false;

        if (savedInstanceState != null) {
            userID = savedInstanceState.getString(key_userID);
        } else {
            Intent intent = getIntent();
            if (intent.hasExtra(LaunchActivity.USER_ID)) {
                userID = intent.getStringExtra(LaunchActivity.USER_ID);
            } else {
                Toast.makeText(this, "error no id", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(key_userID,userID);
    }

    public void StartDance(View view) {
        if(!musicSelected){
            // check if one music has been selected
            DialogOk dialogOk = new DialogOk(MainActivity.this,getString(R.string.error_message_music_selected));
            dialogOk.create();
        }
        else {
            // Start the dance Activity
            Intent intentStartDance = new Intent(MainActivity.this, DanceActivity.class);
            intentStartDance.putExtra("musicchosen", chosenMusic);
            intentStartDance.putExtra(LaunchActivity.USER_ID, userID);
            startActivity(intentStartDance);
            if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                // Change to dance activity
                Communication.changeWatchActivity(MainActivity.this,BuildConfig.W_dance_activity_start);
            }
            finish();
        }
    }


    public void ChooseCreuse(View view) {
        chosenMusic = new int[]{R.raw.musicalinette, R.array.shortMusic};
        musicSelected = true;
    }
    public void ChooseHercule(View view) {
        chosenMusic = new int[]{R.raw.zero,R.array.hercule};
        musicSelected = true;
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
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(MainActivity.this,BuildConfig.W_hall_activity_start);
                }
                finish();
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(this, EditUser.class);
                intent_edit.putExtra(LaunchActivity.USER_ID, userID);
                intent_edit.putExtra(EditUser.ACTIVITY_NAME,ACTIVITY_NAME);
                startActivity(intent_edit);
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(MainActivity.this,BuildConfig.W_edit_activity_start);
                }
                finish();
                break;
            case R.id.txt_change_user:// to copy in main activity and the three fragments
                DialogueYesNo dialogueYesNo = new DialogueYesNo(DialogueYesNo.EDIT_PROFILE,this, getString(R.string.QuestionChangeUser));
                dialogueYesNo.create(getString(R.string.YesChangeUserPopUp), getString(R.string.NoChangeUserPopUp));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void lockedClicked(View view) {
        Toast.makeText(getApplicationContext(),"You have not unlocked this song yet. Keep trying !", Toast.LENGTH_SHORT).show();
    }
}
