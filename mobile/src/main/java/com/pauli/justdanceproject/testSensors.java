package com.pauli.justdanceproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class testSensors extends AppCompatActivity {
    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private int counter;
    private CounterBroadcastReceiver counterBroadcastReceiver;
    
    public static String COUNTEUR = "COUNTEUR";
    public static String RECEIVE_COUNTER = "RECEIVE_COUNTER";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_test_sensors);
        mText1 = findViewById(R.id.textView1);
        mText2 = findViewById(R.id.textView2);
        mText3 = findViewById(R.id.textView3);
        counterBroadcastReceiver = new CounterBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(counterBroadcastReceiver,new
                IntentFilter(RECEIVE_COUNTER));
        // Change the dance activity
        Communication.changeWatchActivity(testSensors.this,BuildConfig.W_counter_activity);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Communication.stopRecordingOnWear(testSensors.this, BuildConfig.W_counter_activity);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(counterBroadcastReceiver);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(counterBroadcastReceiver,new
                IntentFilter(RECEIVE_COUNTER));
    }

    private class CounterBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get counter
            counter = intent.getIntExtra(COUNTEUR,0);
            mText2.setText("Counter: " + counter);
        }
    }
}
