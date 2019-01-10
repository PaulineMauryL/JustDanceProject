package com.pauli.justdanceproject;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import java.io.Serializable;

public class MusicDance implements Serializable {

    private String musicName;
    private MediaPlayer musicSound;
    private int[] musicTiming;


    MusicDance(int[] music,final Context context){
        this.musicName = String.valueOf(music[0]);
        this.musicSound = MediaPlayer.create(context,music[0]);
        Resources res = context.getResources();
        this.musicTiming = res.getIntArray(music[1]);
    }

    protected String getName(){
        return this.musicName;
    }

    protected MediaPlayer getSound(){
        return this.musicSound;
    }

    protected int[] getTiming(){
        return this.musicTiming;
    }

}
