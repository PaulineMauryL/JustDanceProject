package com.pauli.justdanceproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WatchMainActivity extends WearableActivity {

    public final String TAG = this.getClass().getSimpleName();

    private CommunicationBroadcastReceiver communicationBroadcastReceiver;
    private StartActivityBR startActivityBR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);

        /* Check the permission for the sensors */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission("android" + ""
                        + ".permission.BODY_SENSORS") == PackageManager
                        .PERMISSION_DENIED) {
            requestPermissions(new String[]{"android.permission" +
                    ".BODY_SENSORS"}, 0);
        }

        /* For the communication with the tablet*/
        // Get Message
        communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));

        startActivityBR = new StartActivityBR();
        startActivityBR.setCurrentContext(WatchMainActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));

        setAmbientEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(communicationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(startActivityBR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(communicationBroadcastReceiver == null) {
            communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
            startActivityBR.setCurrentContext(WatchMainActivity.this);
            LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));
        }

        if(startActivityBR == null) {
            startActivityBR = new StartActivityBR();
            LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));
        }
    }

  private class CommunicationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s_message = intent.getStringExtra(WearService.MESSAGE);
            Log.d(TAG, "Receive message: " + s_message);
            switch (s_message) {
                case BuildConfig.W_test_isPaired:
                    Communication.sendMessage(WatchMainActivity.this, BuildConfig.W_test_isPaired_true);
                    break;
            }
        }
    }
}
