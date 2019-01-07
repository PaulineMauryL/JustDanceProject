package com.pauli.justdanceproject;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer;
import java.io.Serializable;

public class MusicDance implements Serializable {
    private static final String TAG = "Music";

    protected String musicname;
    protected MediaPlayer musicsound;

    public MusicDance(String musicname, int IdofMusic,Context context){
        this.musicname = musicname;
        this.musicsound = MediaPlayer.create(context,IdofMusic);
    }

}
