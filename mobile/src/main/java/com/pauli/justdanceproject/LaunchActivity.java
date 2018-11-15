package com.pauli.justdanceproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LaunchActivity extends AppCompatActivity {

    TextView name_entry;
    Button button;

    //private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        name_entry = findViewById(R.id.txt_enter_name);
        button = findViewById(R.id.bn_launch);


    }

    public void begin_game(View view) {
        /*
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference profileRef = database.getReference("profile");

        final String username = ((EditText) findViewById(R.id.txt_enter_name)).getText().toString();

        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean notMember = true;
                for (final DataSnapshot user : dataSnapshot.getChildren()) {
                    String usernameDatabase = user.child("username").getValue(String.class);
                    if (username.equals(usernameDatabase) ) {
                        userID = user.getKey();
                        notMember = false;
                        break;
                    }
                }
                if (notMember) {
                    // open edit user

                } else {
                    // open Main with settings of user
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(USER_ID, userID);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
        */


        // if yes, open Main to select song

        // otherwise, open edit user to create profile in database


    }
}
