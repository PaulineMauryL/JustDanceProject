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

    public static final String ACTION_RECEIVE_MESSAGE = "RECEIVE_MESSAGE";
    public static final String MESSAGE = "MESSAGE";
    public static final String ACTION_RECEIVE_IMAGE = "ACTION_RECEIVE_IMAGE";
    public static final String IMAGE ="IMAGE";
    public final String TAG = this.getClass().getSimpleName();
    private TextView mTextView;
    private ConstraintLayout mLayout;
    private ImageView mImageView;

    private CommunicationBroadcastReceiver communicationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        mLayout = findViewById(R.id.container);
        //mTextView = (TextView) findViewById(R.id.textViewMain);
        mImageView = findViewById(R.id.imageViewMain);

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
        LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(ACTION_RECEIVE_MESSAGE));
        setAmbientEnabled();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(communicationBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(ACTION_RECEIVE_MESSAGE));
    }

    private class CommunicationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String s_message = intent.getStringExtra(MESSAGE);
            Log.d(TAG,"Receive message: " + s_message);
            if(s_message.equals(BuildConfig.W_test_isPaired)){
                Communication.sendMessage(WatchMainActivity.this,BuildConfig.W_test_isPaired_true);
            }
        }
    }
}
