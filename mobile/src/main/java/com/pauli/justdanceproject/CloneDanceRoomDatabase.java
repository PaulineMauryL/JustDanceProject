package com.pauli.justdanceproject;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {DatabaseEntity.class}, version = 1)
public abstract class CloneDanceRoomDatabase extends RoomDatabase {

    //Dao to associate to the database and use the queries implemented
    public abstract DataDao dataDao();

}
