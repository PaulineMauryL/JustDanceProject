package com.pauli.justdanceproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static final String ACTIVITY_NAME = "main_activity";
    public static final String USER_ID = "USER_ID";
    public static final String USERNAME = "username";

    private String key_userID = "key_userID";
    private String key_music = "key_music";
    private String key_username = "key_username";

    public static final String RECORDING_ID = "recording_id";  //added by Pauline
    private String userID;
    private static final int PICK_MUSIC = 1;
    private int[] chosenMusic = null;
    boolean musicSelected = false;
    private String username = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        musicSelected = false;
        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(MainActivity.USER_ID)) {
                userID = intent.getStringExtra(MainActivity.USER_ID);
                username = intent.getStringExtra(MainActivity.USERNAME);
            } else {
                Toast.makeText(this, "error no id", Toast.LENGTH_SHORT).show();
            }
        }
        if(savedInstanceState != null) {
            userID = savedInstanceState.getString(key_userID);
            chosenMusic = savedInstanceState.getIntArray(key_music);
            username = savedInstanceState.getString(key_username);
            if (chosenMusic != null) {
                musicSelected = true;
            }
        }
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(key_userID,userID);
        outState.putIntArray(key_music,chosenMusic);
        outState.putString(key_username,username);
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
            intentStartDance.putExtra(MainActivity.USER_ID, userID);
            intentStartDance.putExtra(MainActivity.USERNAME,username);
            startActivity(intentStartDance);

            if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                // Change to dance activity
                Communication.changeWatchActivity(MainActivity.this,BuildConfig.W_dance_activity_start);
            }
            finish();
        }
    }


    public void ChooseCreuse(View view) {
        chosenMusic = new int[]{R.raw.creuse, R.array.creuse};
        musicSelected = true;
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_creuse_music);}
    }
    public void ChooseHercule(View view) {
        chosenMusic = new int[]{R.raw.zero,R.array.hercule};
        musicSelected = true;
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_hercule_music);
        }
    }
    public void ChooseMusicalinette(View view) {
        chosenMusic = new int[]{R.raw.musicalinette,R.array.musicalinette};
        musicSelected = true;
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_musicalinette_music);
        }
    }
    public void ChooseLalaland(View view) {
        chosenMusic = new int[]{R.raw.lalaland,R.array.lalaland};
        musicSelected = true;
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_shakira_music);
        }
    }
    public void ChooseShakira(View view) {
        chosenMusic = new int[]{R.raw.shakira,R.array.shakira};
        musicSelected = true;
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_lalaland_music);
        }
    }
    public void locked3Clicked(View view) {
        message_locked();
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_locked3_music);
        }
    }
    public void locked4Clicked(View view) {
        message_locked();
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_locked4_music);
        }
    }
    public void locked5Clicked(View view) {
        message_locked();
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_locked5_music);
        }
    }
    public void locked6Clicked(View view) {
        message_locked();
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
            // Change the image on Watch
            Communication.changeImage(MainActivity.this,BuildConfig.W_locked6_music);
        }
    }

    private void message_locked(){
        Toast.makeText(getApplicationContext(),"You have not unlocked this song yet. Keep trying !", Toast.LENGTH_SHORT).show();
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
                intent_show.putExtra(MainActivity.USER_ID, userID);
                intent_show.putExtra(MainActivity.USERNAME, username);
                startActivity(intent_show);
                if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                    // Change to dance activity
                    Communication.changeWatchActivity(MainActivity.this,BuildConfig.W_hall_activity_start);
                }
                finish();
                break;
            case R.id.txt_edit_profile:
                Intent intent_edit = new Intent(this, EditUser.class);
                intent_edit.putExtra(MainActivity.USER_ID, userID);
                intent_edit.putExtra(MainActivity.USERNAME,username);
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


}
