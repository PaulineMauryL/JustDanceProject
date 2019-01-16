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
import android.os.Vibrator;
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
    private ImageView mImageView;
    private Vibrator mVibrator;
    private final int indexInPatternToRepeat = -1;
    private final long[] vibrationPatternUnlocked = {0,100};
    private final long[] vibrationPatternLocked = {0, 100,0, 100,0};

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
        /* Check the permission for vibrate */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission("android" + ""
                        + ".permission.VIBRATE") == PackageManager
                        .PERMISSION_DENIED) {
            requestPermissions(new String[]{"android.permission" +
                    ".VIBRATE"}, 0);
        }

        /* For the communication with the tablet*/
        // Get Message
        communicationBroadcastReceiver = new CommunicationBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(communicationBroadcastReceiver, new IntentFilter(WearService.ACTION_RECEIVE_MESSAGE));

        startActivityBR = new StartActivityBR();
        startActivityBR.setCurrentContext(WatchMainActivity.this);
        LocalBroadcastManager.getInstance(this).registerReceiver(startActivityBR, new IntentFilter(WearService.ACTIVITY_TO_START));

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mImageView = findViewById(R.id.imageViewMain);

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
                case BuildConfig.W_musicalinette_music:
                    mImageView.setImageResource(R.mipmap.musicalinette);
                    mVibrator.vibrate(vibrationPatternUnlocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_hercule_music:
                    mImageView.setImageResource(R.mipmap.hercule);
                    mVibrator.vibrate(vibrationPatternUnlocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_creuse_music:
                    mImageView.setImageResource(R.mipmap.creuse);
                    mVibrator.vibrate(vibrationPatternUnlocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_lalaland_music:
                    mImageView.setImageResource(R.mipmap.locked1);
                    mVibrator.vibrate(vibrationPatternLocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_shakira_music:
                    mImageView.setImageResource(R.mipmap.locked2);
                    mVibrator.vibrate(vibrationPatternLocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_locked3_music:
                    mImageView.setImageResource(R.mipmap.locked3);
                    mVibrator.vibrate(vibrationPatternLocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_locked4_music:
                    mImageView.setImageResource(R.mipmap.locked4);
                    mVibrator.vibrate(vibrationPatternLocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_locked5_music:
                    mImageView.setImageResource(R.mipmap.locked5);
                    mVibrator.vibrate(vibrationPatternLocked, indexInPatternToRepeat);
                    break;
                case BuildConfig.W_locked6_music:
                    mImageView.setImageResource(R.mipmap.locked6);
                    mVibrator.vibrate(vibrationPatternLocked, indexInPatternToRepeat);
                    break;
            }
        }
    }
}
