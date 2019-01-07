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

public class DanceActivity extends WearableActivity implements SensorEventListener {
    public static final String STOP_ACTIVITY = "STOP_ACTIVITY";
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dance);

        /*Sensor SetUp*/
        final SensorManager sensorManager = (SensorManager) getSystemService(WatchMainActivity.SENSOR_SERVICE);
        Sensor acc_sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // Enable registerListener
        sensorManager.registerListener(this, acc_sensor, SensorManager.SENSOR_DELAY_UI);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                sensorManager.unregisterListener(DanceActivity.this);
                finish();
            }
        }, new IntentFilter(STOP_ACTIVITY));

        setAmbientEnabled();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] accRate = new float[3];

        TextView[] mText = {findViewById(R.id.textView1),findViewById(R.id.textView2),findViewById(R.id.textView3)};

        for(int i = 0; i< event.values.length; i++)
        {
           accRate[i] = event.values[i];
           mText[i].setText("Acc = " + accRate[i]);
        }

        /* Send Values */
        Log.d(TAG,"Send values: 1");
        Intent intent = new Intent(DanceActivity.this, WearService.class);
        Log.d(TAG,"Send values: 2");
        intent.setAction(WearService.ACTION_SEND.ACC_RATE.name());
        Log.d(TAG,"Send values: 3");
        intent.putExtra(WearService.ACC_RATE, accRate);
        Log.d(TAG,"Send values: 4");
        startService(intent);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
