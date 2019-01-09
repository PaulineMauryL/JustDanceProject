package com.pauli.justdanceproject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class DatabaseEntity {

    //Primary key to access the row of SQLite table for the entity SensorData
    @PrimaryKey(autoGenerate = true)
    public int row_id;

    //Different column for different attributes of the entity SensorData
    @ColumnInfo
    public String user_name;

    @ColumnInfo
    public String music;

    @ColumnInfo
    public int score;

    //Getters and Setters
    public int getRow_id() {
        return row_id;
    }

    public void setRow_id(int row_id) {
        this.row_id = row_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
