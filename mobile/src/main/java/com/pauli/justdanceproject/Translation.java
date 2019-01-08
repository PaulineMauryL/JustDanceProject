package com.pauli.justdanceproject;

import android.content.res.Configuration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class Translation {

    private String languageToLoad = "en";
    private static final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference profileGetRef = database.getReference("profiles");
    private static DatabaseReference profileRef = profileGetRef.push();
    private String language_db;

    public void changeLanguage(Context context,String languageFromUser,String userID){
        if(languageFromUser == null) {
            fetchDataFromFirebase(userID);
        } else {
            languageToLoad = languageFromUser;
        }
        if(languageToLoad!=null){
            Locale locale = new Locale(languageToLoad);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            context.getResources().updateConfiguration(config,context.getResources().getDisplayMetrics());
        }

    }

    private void fetchDataFromFirebase(String userID) {
        profileRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                language_db = dataSnapshot.child("Translation").getValue(String.class);
                if (language_db != null){
                    languageToLoad = language_db;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
