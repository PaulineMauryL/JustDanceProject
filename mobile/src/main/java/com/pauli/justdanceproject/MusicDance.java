package com.pauli.justdanceproject;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.MediaPlayer;
import java.io.Serializable;

public class MusicDance implements Serializable {
    private static final String TAG = "Music";

    protected String musicname;
    protected MediaPlayer musicsound;
    protected int[] musictiming;

    public MusicDance(String musicname, int[] music,Context context){
        this.musicname = musicname;
        this.musicsound = MediaPlayer.create(context,music[0]);
        Resources res = context.getResources();
        this.musictiming = res.getIntArray(music[1]);
    }

}
