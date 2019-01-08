package com.pauli.justdanceproject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LaunchActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    public static final String USER_ID = "USER_ID";
    public static final String USERNAME = "username";
    private Translation translation = new Translation();
    private String language;

    private String userID;
    boolean notMember = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        //Intent intent = getIntent();

       // Log.d(TAG, "oncreate");
        //username = findViewById(R.id.txt_enter_name);
        //button = findViewById(R.id.bn_launch);
    }

    public void begin_game(View view) {  //if button is clicked

        final String username = ((EditText) findViewById(R.id.txt_enter_name)).getText().toString();
        if(username.isEmpty()) {
            // No connection to internet
            final Dialog dialog = new Dialog(LaunchActivity.this); // Context, this, etc.
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_ok, null);
            dialog.setContentView(layout);
            ((TextView) dialog.findViewById(R.id.dialog_info_ok)).setText(R.string.nameEmpty);
            Button dialogButton = dialog.findViewById(R.id.buttonOkDialog);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
        else if(isNetworkAvailable())
        {
            // Connection to network work properly
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
                        startActivity(intent);
                        finish();
                    } else {              // user exist, go to mainActivity to select a dance
                        translation.changeLanguage(getBaseContext(),language,userID);
                        Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                        intent.putExtra(USER_ID, userID);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else{
            final Dialog dialog = new Dialog(LaunchActivity.this); // Context, this, etc.
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_ok, null);
            dialog.setContentView(layout);
            ((TextView) dialog.findViewById(R.id.dialog_info_ok)).setText(R.string.internet_connection_message);
            dialog.setTitle(R.string.internet_connection_title);
            Button dialogButton = dialog.findViewById(R.id.buttonOkDialog);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }
    // Check if the internet connection is available
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
