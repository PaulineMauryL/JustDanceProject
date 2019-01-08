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
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

public class CounterActivity extends WearableActivity implements SensorEventListener {
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";
    private final String TAG = this.getClass().getSimpleName();
    private int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);
        counter = 0;
        /*Sensor SetUp*/
        final SensorManager sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Enable registerListener
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sensorManager.unregisterListener(CounterActivity.this);
                finish();
            }
        }, new IntentFilter(STOP_ACTIVITY));

        setAmbientEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final SensorManager sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Enable registerListener
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        final SensorManager sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accRate = new float[3];

        TextView[] mText = {findViewById(R.id.textView1),findViewById(R.id.textView2),findViewById(R.id.textView3)};

        for(int i = 0; i< event.values.length; i++)
        {
            accRate[i] = event.values[i];
            //mText[i].setText("Acc = " + accRate[i]);
        }
        counter ++;
        mText[1].setText("Counter: " + counter);
        /* Send Values */
        Log.d(TAG,"Send values: 1");
        Intent intent = new Intent(CounterActivity.this, WearService.class);
        Log.d(TAG,"Send values: 2");
        intent.setAction(WearService.ACTION_SEND.COUNTER.name());
        Log.d(TAG,"Send values: 3");
        intent.putExtra(WearService.COUNTER, counter);
        Log.d(TAG,"Send values: 4");
        startService(intent);
        if(counter>100){finishCounter();}
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void finishCounter(){
        finish();
    }

}
