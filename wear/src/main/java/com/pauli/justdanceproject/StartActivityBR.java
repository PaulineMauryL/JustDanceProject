package com.pauli.justdanceproject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartActivityBR extends BroadcastReceiver {
    private final String TAG = "StartActivityBR";
    private Context current_context;

    public void setCurrentContext(Context context){
        this.current_context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String s_message = intent.getStringExtra(WearService.PATH);
        Log.d(TAG,"Receive message : " + s_message);
        Activity activity = (Activity) current_context;
        Intent itStart = null;
        switch(s_message){
            case BuildConfig.W_dance_activity_start:
                itStart = new Intent(current_context, WatchDanceActivity.class);
                break;
            case BuildConfig.W_finish_activity_start:
                itStart = new Intent(current_context, WatchFinishActivity.class);
                break;
            case BuildConfig.W_hall_activity_start:
                itStart = new Intent(current_context, WatchHallOfFameActivity.class);
                break;
            case BuildConfig.W_watchmain_activity_start:
                itStart = new Intent(current_context, WatchMainActivity.class);
                break;
            case BuildConfig.W_edit_activity_start:
                itStart = new Intent(current_context, WatchEditProfileActivity.class);
                break;
        }
        if(itStart != null) {
            Log.v(TAG,"Start Activity : " + s_message);
            Log.v(TAG, "Stop Activity : " + activity.getClass());
            activity.startActivity(itStart);
            if(activity.getClass().equals(WatchDanceActivity.class)){
                ((WatchDanceActivity)activity).stopActivity();
            }else {
                activity.finish();
            }
        }
    }
}
