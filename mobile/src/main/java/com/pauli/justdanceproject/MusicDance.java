package com.pauli.justdanceproject;

import android.media.MediaPlayer;

import java.io.Serializable;

public class MusicDance implements Serializable {
    private static final String TAG = "Music";

    protected String musicname;
    protected MediaPlayer musicsound;
    public MusicDance(String musicname,MediaPlayer musicsound){
        this.musicname = musicname;
        this.musicsound = musicsound;
    }

}
