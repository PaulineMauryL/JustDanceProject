package com.pauli.justdanceproject;

import android.arch.persistence.room.Room;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LaunchActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    public static final String ACTIVITY_NAME = "launch_activity";
    public static final String USER_ID = "USER_ID";
    public static final String USERNAME = "username";
    private Translation translation = new Translation();
    private String language;
    private Boolean isWatchPaired= false;
    private String userID;
    boolean notMember = true;
    private CommunicationBroadcastReceiver communicationBroadcastReceiver;
    private final String key_userId = "key_userId";
    private String username = null;
    public static CloneDanceRoomDatabase cloneDanceRD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launch);

        if(savedInstanceState != null){
            username = savedInstanceState.getString(USERNAME);
        }

        Log.d(TAG, "test :" + Boolean.parseBoolean(BuildConfig.W_flag_watch_enable));
        // Get acknowledge from the watch
        if(communicationBroadcastReceiver == null){
            communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));
        }
        cloneDanceRD = Room.databaseBuilder(getApplicationContext(), CloneDanceRoomDatabase.class, "db").allowMainThreadQueries().build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USERNAME,username);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){testIfWatchPaired();}
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(communicationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(communicationBroadcastReceiver == null){
            communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
            LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));
        }
    }
    public void begin_game(View view) {  //if button is clicked

        username = ((EditText) findViewById(R.id.txt_enter_name)).getText().toString();
        if(username.isEmpty()) {
            // No connection to internet
            DialogOk dialogOk = new DialogOk(LaunchActivity.this,getString(R.string.nameEmpty));
            dialogOk.create();
        }
        else if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable) && !isBluetoothEnabled()){
            // check if the bluetooth is on
            DialogOk dialogOk = new DialogOk(LaunchActivity.this,getString(R.string.error_message_bluetooth));
            dialogOk.create();
        }
        else if (Boolean.parseBoolean(BuildConfig.W_flag_watch_enable) && !isWatchPaired){
            // Check if the watch is paired
            DialogOk dialogOk = new DialogOk(LaunchActivity.this,getString(R.string.error_message_pair_watch));
            dialogOk.create();
        }
        else if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable) && !isNetworkAvailable())
        {
            // Check if the internet connection works
            DialogOk dialogOk = new DialogOk(LaunchActivity.this,getString(R.string.internet_connection_message));
            dialogOk.create();
            // Test if the watch is paired
            if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){testIfWatchPaired();}
        } else{
            // Begin fire base transaction
            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference profileRef = database.getReference("profiles");

            profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //if (snapshot.child(username).exists()) {
                    for (final DataSnapshot user : dataSnapshot.getChildren()) {

                        String usernameDatabase = user.child("username").getValue(String.class);
                        language = user.child("language").getValue(String.class);

                        if (username.equals(usernameDatabase)) {
                            userID = user.getKey();
                            notMember = false;
                            break;
                        }
                    }
                    if (notMember) {       // go to edit user to create a new profile
                        Toast.makeText(LaunchActivity.this, getString(R.string.welcome) + username + getString(R.string.createProfile), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LaunchActivity.this, EditUser.class);
                        intent.putExtra(USERNAME, username);
                        intent.putExtra(EditUser.ACTIVITY_NAME,ACTIVITY_NAME);
                        startActivity(intent);
                        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                            // Change to dance activity
                            Communication.changeWatchActivity(LaunchActivity.this,BuildConfig.W_edit_activity_start);
                        }
                        finish();
                    } else {              // user exist, go to mainActivity to select a dance
                        translation.changeLanguage(getBaseContext(),language,userID);
                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        intent.putExtra(USER_ID, userID);
                        startActivity(intent);
                        if(Boolean.parseBoolean(BuildConfig.W_flag_watch_enable)){
                            // Change to dance activity
                            Communication.changeWatchActivity(LaunchActivity.this,BuildConfig.W_watchmain_activity_start);
                        }
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    // Check if the internet connection is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    // Check if the bluetooth is one
    public boolean isBluetoothEnabled()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();

    }
    // Check if the watch is paired
    private void testIfWatchPaired() {
        // need to be implemented
        Communication.sendMessage(LaunchActivity.this,BuildConfig.W_test_isPaired);
        Log.d(TAG, "Message send");
    }

    private class CommunicationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Message received");
            String s_message = intent.getStringExtra(WearService.MESSAGE);
            Log.d(TAG, "Message receive: " + s_message);
            if (s_message.equals(BuildConfig.W_test_isPaired_true)) {
                isWatchPaired = true;
            }
        }
    }


}
