package com.pauli.justdanceproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.tasks.OnSuccessListener;

public class testSensors extends AppCompatActivity {

    public static final String RECEIVE_ACC_RATE = "RECEIVE_ACC_RATE";
    public static final String ACC_RATE = "ACC_RATE";
    private AccRateBroadcastReceiver accRateBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_sensors);
        /* Start sensors */
        startWatchActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Get the accelerometer datas back from the watch
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_ACC_RATE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Get the accelerometer datas back from the watch
        accRateBroadcastReceiver = new AccRateBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(accRateBroadcastReceiver, new
                IntentFilter(RECEIVE_ACC_RATE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(accRateBroadcastReceiver);
    }

    private class AccRateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Show AR in a TextView
            float[] accRateWatch = intent.getFloatArrayExtra(ACC_RATE);
            TextView mTextView1 = findViewById(R.id.textView1);
            mTextView1.setText("Accx = " + accRateWatch[0]);
            TextView mTextView2 = findViewById(R.id.textView2);
            mTextView2.setText("Accy = " + accRateWatch[1]);
            TextView mTextView3 = findViewById(R.id.textView3);
            mTextView3.setText("Accz = " + accRateWatch[2]);
        }
    }

    private void startWatchActivity(){
        Intent intent = new Intent(this, WearService.class);
        intent.setAction(WearService.ACTION_SEND.STARTACTIVITY.name());
        intent.putExtra(WearService.ACTIVITY_TO_START, BuildConfig.W_dance_activity);
        startService(intent);
    }

    public void stopRecordingOnWear(View view) {
        /* Store here the datas is needed */
        Intent intentStopRec = new Intent(testSensors.this, WearService.class);
        intentStopRec.setAction(WearService.ACTION_SEND.STOPACTIVITY.name());
        intentStopRec.putExtra(WearService.ACTIVITY_TO_STOP, BuildConfig.W_dance_activity);
        startService(intentStopRec);
    }
}
