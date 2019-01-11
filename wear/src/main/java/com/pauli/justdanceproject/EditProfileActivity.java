package com.pauli.justdanceproject;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;

public class EditProfileActivity extends WearableActivity {

    private final String TAG = this.getClass().getSimpleName();
    private StartActivityBR startActivityBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        startActivityBR = new StartActivityBR();
        startActivityBR.setCurrentContext(EditProfileActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));

        setAmbientEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startActivityBR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(startActivityBR == null) {
            startActivityBR = new StartActivityBR();
            startActivityBR.setCurrentContext(EditProfileActivity.this);
            LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));
        }
    }

}
