/*package com.pauli.justdanceproject;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DataDao {
    //Implementation of the queries to use to access the database

    @Query("SELECT * FROM DatabaseEntity WHERE music LIKE :music_name  ORDER BY " + "score DESC LIMIT 5")
    List<DatabaseEntity> getHallOfFame(String music_name);

    //@Query("SELECT * FROM DatabaseEntity WHERE music = :song_nb  AND " + "firebase_id = :user_id")
    //List<DatabaseEntity> getUserSong(int user_id, int song_nb);

    @Query("SELECT * FROM DatabaseEntity WHERE music LIKE :music_name  AND " + "firebase_id LIKE :userID ORDER BY " + "score DESC LIMIT 5")
    List<DatabaseEntity> getUserSongHallOfFame(String userID, String music_name);

    @Insert
    void insertEntity(DatabaseEntity... databaseEntity);

    @Query("DELETE FROM DatabaseEntity")
    void deleteAll();
}*/
