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
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class WatchMainActivity extends WearableActivity {

    public static final String ACTION_RECEIVE_MESSAGE = "RECEIVE_MESSAGE";
    public static final String MESSAGE = "MESSAGE";
    public static final String ACTION_RECEIVE_IMAGE = "ACTION_RECEIVE_IMAGE";
    public static final String IMAGE ="IMAGE";

    private TextView mTextView;
    private ConstraintLayout mLayout;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_main);
        mLayout = findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.textViewMain);
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
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s_message = intent.getStringExtra(MESSAGE);
                mTextView.setText("Message: " + s_message);
            }
        }, new IntentFilter(ACTION_RECEIVE_MESSAGE));
        // Get image
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                byte[] byteArray = intent.getByteArrayExtra(IMAGE);
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                mImageView.setImageBitmap(bmp);
            }
        }, new IntentFilter(ACTION_RECEIVE_IMAGE));

        // Enables Always-on
        setAmbientEnabled();
    }


/*@Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        updateDisplay();
    }

    private void updateDisplay() {
        if(isAmbient()){
            mLayout.setBackgroundColor(getResources().getColor(android.R.color.black,getTheme()));
        }else{
            mLayout.setBackgroundColor(getResources().getColor(android.R.color.darker_gray,getTheme()));
        }
    }*/

}
