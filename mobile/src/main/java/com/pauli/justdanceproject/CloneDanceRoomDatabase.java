package com.pauli.justdanceproject;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DatabaseEntity.class}, version = 1)
public abstract class CloneDanceRoomDatabase extends RoomDatabase {

    //Dao to associate to the database and use the queries implemented
    public abstract DataDao dataDao();

    //Instance of the database that will be used later
    private static CloneDanceRoomDatabase INSTANCE;

    //Constructor of the class.
    public static synchronized CloneDanceRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context, CloneDanceRoomDatabase.class, "CloneDanceDB").build();
        }
        return INSTANCE;
    }

    //Method to destroy the instance of the database
    public static void destroyInstance() {
        INSTANCE = null;
    }
}
