package com.pauli.justdanceproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import java.io.Serializable;

public class MusicDance implements Serializable {
    static final String NUMBER_POINTS = "Number_points";  //Added by Pauline for finish activity

    private String musicName;
    private MediaPlayer musicSound;
    private int[] musicTiming;

    /**
     * @param musicName
     * @param music
     * @param context
     */
    MusicDance(String musicName, int[] music,final Context context){
        this.musicName = musicName;
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
