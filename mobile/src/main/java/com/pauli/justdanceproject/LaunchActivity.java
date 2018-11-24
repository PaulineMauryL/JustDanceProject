package com.pauli.justdanceproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    private String userID;
    boolean notMember = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
       // Log.d(TAG, "oncreate");
        //username = findViewById(R.id.txt_enter_name);
        //button = findViewById(R.id.bn_launch);
    }

    public void begin_game(View view) {  //if button is clicked

        final String username = ((EditText) findViewById(R.id.txt_enter_name)).getText().toString();
        if(username.equals("")) {
            TextView empty_name_txt = findViewById(R.id.txt_name_empty);
            empty_name_txt.setText(R.string.nameEmpty);
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileRef = database.getReference("profiles");

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //if (snapshot.child(username).exists()) {
                for (final DataSnapshot user : dataSnapshot.getChildren()) {

                     String usernameDatabase = user.child("username").getValue(String.class);
                    if (username.equals(usernameDatabase)) {
                        userID = user.getKey();
                        notMember = false;
                        break;
                    }
                }
                if(notMember) {       // user exist, go to mainActivity to select a dance
                    Toast.makeText(LaunchActivity.this, getString(R.string.welcome) + username + getString(R.string.createProfile), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LaunchActivity.this, EditUser.class);
                    intent.putExtra(USERNAME, username);
                    startActivity(intent);
                    finish();
                } else {              // go to edit user to create a new profile
                    Toast.makeText(LaunchActivity.this, "Victory", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                    intent.putExtra(USER_ID, userID);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {  }
        });


    }
}
