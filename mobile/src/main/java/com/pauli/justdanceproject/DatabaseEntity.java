package com.pauli.justdanceproject;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class DatabaseEntity {
    //public final static int SCORE = 0;

    //Primary key to access the row of SQLite table for the entity SensorData
    @PrimaryKey
    public int row_id;

    //Different column for different attributes of the entity SensorData
    @ColumnInfo
    public String firebase_id;
    @ColumnInfo
    public String music;
    @ColumnInfo
    public int score;
}
