package com.pauli.justdanceproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

public class WatchDialogPause {
    private Context context_activity;
    private Activity activity;
    private Dialog dialog;
    private Boolean open;

    public WatchDialogPause(Context context_activity){
        this.context_activity = context_activity;
        this.activity = (Activity) context_activity;
        this.dialog = new Dialog(context_activity); // Context, this, etc.
        this.open = false;
    }
    public void create(){
        dialog.setContentView(R.layout.dialog_pause);
        dialog.setCancelable(false);
        ImageButton pauseButton = dialog.findViewById(R.id.imageButtonPause);
        // if button is clicked, close the custom dialog
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Communication.sendMessage(context_activity,BuildConfig.W_pause_activity_stop);
                ((WatchDanceActivity)activity).startSensor();
                dialog.dismiss();
            }
        });
        ((WatchDanceActivity)activity).stopSensor();
        dialog.show();
        open = true;
    }
    public void close(){
        dialog.dismiss();
    }
    public Boolean isOpen(){
        return(open);
    }
}
