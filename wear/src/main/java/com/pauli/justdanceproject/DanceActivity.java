package com.pauli.justdanceproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

public class DanceActivity extends WearableActivity implements SensorEventListener {

    private float[] accRate = new float[3];
    private int error = 5;
    private int actualPosition;
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";
    private final String TAG = this.getClass().getSimpleName();
    private Handler mHandler;
    private int counter;

    private SensorManager sensorManager;
    private StopActivityBroadcastReceiver stopActivityBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);
        mHandler = new Handler();

        counter = 0;
        /*Sensor SetUp*/
        sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Enable registerListener
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);

        stopActivityBroadcastReceiver = new StopActivityBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(stopActivityBroadcastReceiver,new IntentFilter(STOP_ACTIVITY));

        setAmbientEnabled();
        startSendingData();
    }
    protected void startSendingData() {
        mHandler.postDelayed(sendDataToBroadcast,50);
    }

    final Runnable sendDataToBroadcast = new Runnable() {
        public void run() {

            TextView[] mText = {findViewById(R.id.textView1),findViewById(R.id.textView2),findViewById(R.id.textView3)};
            counter ++;
            
            if(Math.abs(accRate[0])<error) {
                actualPosition = 2;
            } else if(Math.abs(accRate[1])<error && Math.abs(accRate[2])<error && accRate[0]>0){
                actualPosition = 1;
            } else if(Math.abs(accRate[1])<error && Math.abs(accRate[2])<error && accRate[0]<0) {
                actualPosition = 3;
            } else{
                actualPosition += 0;
            }
            /* Send Values */
            Log.d(TAG,"Send values: 1");
            Intent intent = new Intent(DanceActivity.this, WearService.class);
            Log.d(TAG,"Send values: 2");
            intent.setAction(WearService.ACTION_SEND.COUNTER.name());
            Log.d(TAG,"Send values: 3");
            intent.putExtra(WearService.COUNTER, actualPosition);
            Log.d(TAG,"Send values: 4");
            startService(intent);

            mText[1].setText("Counter: "+ actualPosition);
            startSendingData();
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Enable registerListener
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);
        stopActivityBroadcastReceiver = new StopActivityBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(stopActivityBroadcastReceiver,new IntentFilter(STOP_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(stopActivityBroadcastReceiver);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (int i = 0; i < event.values.length; i++) {
            accRate[i] = event.values[i];
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class StopActivityBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            sensorManager.unregisterListener(DanceActivity.this);
            finish();
        }
    }
}
